package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import com.t2pellet.tlib.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;


public class HarvestCropGoal extends GolemMoveGoal<Harvester> {

    public HarvestCropGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get(), golem, golem.getHarvester());
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        Optional<BlockPos> harvestPos = golem.getHarvester().getHarvesting();
        return harvestPos.isPresent() && harvestPos.get().equals(blockPos) && CropUtil.isGrownCrop((LevelAccessor) levelReader, blockPos);
    }

    @Override
    public boolean canUse() {
        if (golem.isStarving() || golem.getHeldItem().has()) return false;
        Optional<BlockPos> harvestPos = golem.getHarvester().startHarvest();
        if (harvestPos.isPresent()) {
            blockPos = harvestPos.get();
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !golem.getHeldItem().has() && this.isValidTarget(this.mob.level(), this.blockPos);
    }

    @Override
    protected int nextStartTick(PathfinderMob $$0) {
        return reducedTickDelay(100 + $$0.getRandom().nextInt(100));
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos targetPos = this.getMoveToTarget();
        tryTicks++;
        // Default below being at actual targetPos.
        BlockPos below = targetPos.below();
        if (StrawgolemConfig.Harvesting.enableVineHarvest.get()) {
            below = targetPos.below().below();
            Block belowBlock = golem.level().getBlockState(below).getBlock();
            while (!blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance()) &&
                    belowBlock.equals(golem.level().getBlockState(targetPos.below()).getBlock()) && CropUtil.isGrownCrop(golem.level(), targetPos.below())) {
                below = below.below();
                belowBlock = golem.level().getBlockState(below).getBlock();
            }
        }

        if (withinDistance(targetPos) || withinDistance(below)) {
            Harvester harvester = golem.getHarvester();
            golem.getNavigation().stop();
            if (harvester.isHarvestingBlock()) {
                golem.setPickingUpBlock(true);
            } else {
                golem.setPickingUpItem(true);
            }
            harvester.completeHarvest();
            Services.SIDE.scheduleServer(40, () -> {
                harvester.findHarvestables();
                golem.setPickingUpBlock(false);
                golem.setPickingUpItem(false);
            });
        } else {
            if (this.shouldRecalculatePath()) {
                if(!golemCollision()) {
                    if (!fail && !this.mob.getNavigation().moveTo((double) ((float) targetPos.getX()), (double) targetPos.getY() - 1.0, (double) ((float) targetPos.getZ()), this.speedModifier)) {
                        fail = true;
                    }
                    if (fail) {
                        failToReachGoal();
//                        this.mob.getNavigation().moveTo((double) ((float) blockPos.getX()), (double) blockPos.getY() - 2.0, (double) ((float) blockPos.getZ()), this.speedModifier);
                    }


//                    this.mob.getNavigation().moveTo((double) ((float) targetPos.getX()), (double) targetPos.getY() + 1.0, (double) ((float) targetPos.getZ()), this.speedModifier);
                }
            }
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
        }
    }

    @Override
    public void start() {
        super.start();
        fail = false;
        golem.playSound(StrawgolemSounds.GOLEM_INTERESTED.get());
        // Update the tether to the crop we're harvesting
        golem.getTether().update(blockPos);
    }

    @Override
    public double acceptedDistance() {
        return 1.3D;
    }


    @Override
    protected boolean findNearestBlock() {
        if (!golem.getHarvester().isHarvesting()) {
            golem.getHarvester().findHarvestables();
        }
       if (golem.getHarvester().getHarvesting().isPresent()) {
           BlockPos blockPos = golem.getHarvester().getHarvesting().get();
           if (isValidTarget(mob.level(), blockPos)) {
               this.blockPos = blockPos;
               return true;
           }
       }
        return false;
    }

    @Override
    protected void updateBlackList() {
        blackList = golem.getHarvester();
    }
}
