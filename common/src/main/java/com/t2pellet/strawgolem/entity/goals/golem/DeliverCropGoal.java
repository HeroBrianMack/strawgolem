package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;



public class DeliverCropGoal extends GolemMoveGoal<Deliverer> {

    public DeliverCropGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get(), golem);

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
        super.tick();
        tryTicks++;
        BlockPos blockpos = this.getMoveToTarget();
        if (blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
            golem.getNavigation().stop();
            golem.getDeliverer().deliver(blockPos);
        } else {
            if (this.shouldRecalculatePath()) {
                if(!golemCollision()) {
                    if (!fail && !this.mob.getNavigation().moveTo((double) ((float) blockpos.getX()), (double) blockpos.getY(), (double) ((float) blockpos.getZ()), this.speedModifier)) {
                        fail = true;
                    }
                    if (fail && still() && (closeEnough(blockpos))) {
                        fail = failToReachGoal();
                    }
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
        return false;
    }

    @Override
    protected void blackListAdd(BlockPos blockPos) {
        golem.getDeliverer().addInvalidPos(blockPos);
    }

    @Override
    protected void blackListClear() {
        golem.getDeliverer().clearInvalidPos();
    }

}
