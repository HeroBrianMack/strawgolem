package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;


public class DeliverCropGoal extends GolemMoveGoal<Deliverer> {

//    private final StrawGolem golem;
//    private final ServerLevel level;
//    private Vec3 oldBlockPos = null;
//    private boolean scrambled = false;
    private Set<BlockPos> invalidLocations = new HashSet<>();

    public DeliverCropGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get(), golem, golem.getDeliverer());
//        this.golem = golem;
//        this.level = (ServerLevel) golem.level();
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return ContainerUtil.isContainer((LevelAccessor) levelReader, blockPos);
    }

    @Override
    public boolean canUse() {
        return !golem.isStarving() && golem.getHeldItem().has() && !golem.isPickingUpItem() && !golem.isPickingUpBlock() && findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getHeldItem().has() && isValidTarget(level, blockPos);
    }

    @Override
    public void tick() {
        tryTicks++;
        double recalcMod = 0.0D;
        BlockPos blockpos = this.getMoveToTarget();
        if (blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
            golem.getNavigation().stop();
            golem.getDeliverer().deliver(blockPos);
        } else if (shouldRecalculatePath()) {

    //                if (golem.getDeliverer().hasPriorityPos()) {
    //                blockPos = golem.getDeliverer().getDeliverPos();
    //                }
//            blockpos = getMoveToTarget();
            if (!golemCollision()) {
                this.mob.getNavigation().moveTo((double) ((float) blockpos.getX()) + recalcMod, (double) blockpos.getY() + recalcMod, (double) ((float) blockpos.getZ()) + recalcMod, this.speedModifier);
            }
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if (golem.isHoldingBlock()) golem.playSound(StrawgolemSounds.GOLEM_STRAINED.get());
        else golem.playSound(StrawgolemSounds.GOLEM_INTERESTED.get());
    }

    @Override
    public double acceptedDistance() {
        return 1.6D;
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockPos = golem.getDeliverer().getDeliverPos();
        if (isValidTarget(mob.level(), blockPos)) {
            this.blockPos = blockPos;
            return true;
        }
        if (blockPos == null) {
            System.out.println("no position!");
        }
        return false;
    }
}
