package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.hunger.HungerState;
import com.t2pellet.strawgolem.registry.StrawgolemSounds;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DeliverCropGoal extends GolemMoveGoal {

//    private final StrawGolem golem;
//    private final ServerLevel level;
//    private Vec3 oldBlockPos = null;
//    private boolean scrambled = false;
    private Set<BlockPos> invalidLocations = new HashSet<>();

    public DeliverCropGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get(), golem);
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
        BlockPos blockpos = this.getMoveToTarget();
        if (blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
            golem.getNavigation().stop();
            golem.getDeliverer().deliver(blockPos);
        } else {

    //                if (golem.getDeliverer().hasPriorityPos()) {
    //                blockPos = golem.getDeliverer().getDeliverPos();
    //                }
            blockpos = getMoveToTarget();

            this.mob.getNavigation().moveTo((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY() + 0.5D, (double)((float)blockpos.getZ()) + 0.5D, this.speedModifier);

            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
        }
    }

    // Self Note: Worked on golem collision and ensuring golems don't just decide to stop moving forever
    // Did some edits, just do extra tests to ensure nothing else broke.
    @Override
    public boolean shouldRecalculatePath() {
        if (tryTicks % 40 != 0) {
            return false;
        }
        System.out.println(oldBlockPos.distanceTo(golem.position()));
        if (oldBlockPos.distanceTo(golem.position()) == 0.0 && !golem.isFallFlying()) {

            oldBlockPos = golem.position();
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                System.out.println("Scrambled!");
                scramblePath();
            } else { // Golem cannot path to target, find new target.
                System.out.println("invalid path");
                golem.getDeliverer().addInvalidDeliverPos(blockPos);
                if (!findNearestBlock()) {
                    // Give up on new pathing something has gone seriously wrong with code
                    // or player didn't make the target accessible
                    System.out.println("This shouldn't occur!");
                    golem.getDeliverer().clearInvalidPos();
                    if (findNearestBlock()) {
                        System.out.println("retry!");
                        return true;
                    }
//                    System.out.println(blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance()));
                    return false;
                }
            }
            System.out.println("valid path?");

            return true;
        } else if ((oldBlockPos.distanceTo(golem.position()) < 0.01)) {
            oldBlockPos = golem.position();
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                System.out.println("Scrambled!");
                scramblePath();
            }
            return true;
        }
        scrambled = false;
        oldBlockPos = golem.position();
        return false;
    }

    private void scramblePath() {
        double x = golem.position().x;
        double y = golem.position().y;
        double z = golem.position().z;
        double modifier = 0.05;
        Direction golemDirection = golem.getDirection();
        if (scrambled) {
            golemDirection = Direction.getRandom(RandomSource.create());
        }
        if (golemDirection == Direction.NORTH) {
            x += modifier;
        } else if (golemDirection == Direction.SOUTH) {
            x -= modifier;
        } else if (golemDirection == Direction.EAST) {
            z += modifier;
        } else if (golemDirection == Direction.WEST) {
            z -= modifier;
        }
        scrambled = true;
        if (golem != null) {
            golem.setPos(x, y ,z);
        }
//        golem.getNavigation().moveTo(x, y, z, speedModifier);
    }

    @Override
    public void start() {
        super.start();
        oldBlockPos = golem.position();
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
