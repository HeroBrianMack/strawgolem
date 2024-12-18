package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.BlacklistCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.Predicate;


public abstract class GolemMoveGoal<E extends BlacklistCapability> extends MoveToBlockGoal {
    protected final StrawGolem golem;
    protected final ServerLevel level;
    protected Vec3 oldBlockPos = null;
    protected boolean scrambled = false;
    protected E blackList;
    protected boolean fail = false;
    public Predicate<StrawGolem> validGolem = this::validGolem;

    public GolemMoveGoal(PathfinderMob mob, double speed, int range, StrawGolem golem, BlacklistCapability obj) {
        super(mob, speed, range);
        this.golem = golem;
        updateBlackList();
        this.level = (ServerLevel) golem.level();
    }

    protected boolean golemCollision() {
        List<StrawGolem> nearbyGolem = golem.level().getEntitiesOfClass(StrawGolem.class, golem.getBoundingBox().inflate(0.4), validGolem);
        return !nearbyGolem.isEmpty();
    }

    public boolean validGolem(StrawGolem golem) {
        return !golem.position().equals(this.golem.position());
    }

    // Self Note: Worked on golem collision and ensuring golems don't just decide to stop moving forever
    // Did some edits, just do extra tests to ensure nothing else broke.
    @Override
    public boolean shouldRecalculatePath() {
        if (tryTicks % 40 != 0) {
            return false;
        }
        System.out.println(this.blockPos.below().distToCenterSqr(golem.position()) - (acceptedDistance() * acceptedDistance()));
        if (delta() == 0.0 && !golem.isFallFlying()) {

            oldBlockPos = golem.position();
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                fail = false;
                scramblePath(golem);
            } /*else { // Golem cannot path to target, find new target.
                fail = false;
                blackList.addInvalidPos(blockPos);
                if (!findNearestBlock()) {
                    // Give up on new pathing something has gone seriously wrong with code
                    // or player didn't make the target accessible
                    blackList.clearInvalidPos();
                    if (findNearestBlock()) {
                        return true;
                    }
                    return false;
                }*/
            }

/*            return true;
        } else if (delta() < 0.01) {
            oldBlockPos = golem.position();
            fail = false;
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                scramblePath(golem);
            }
            return true;
        }*/
        scrambled = false;
        oldBlockPos = golem.position();
        return true;
    }

    public void scramblePath(StrawGolem golem) {
        double x = golem.position().x;
        double y = golem.position().y;
        double z = golem.position().z;
        double modifier = 2.0;
        Direction golemDirection = golem.getDirection();
        if (scrambled) {
            golemDirection = Direction.getRandom(RandomSource.create());
        }
        if (golemDirection == Direction.NORTH) {
            z -= modifier;
        } else if (golemDirection == Direction.SOUTH) {
            z += modifier;
        } else if (golemDirection == Direction.EAST) {
            x += modifier;
        } else if (golemDirection == Direction.WEST) {
            x -= modifier;
        }
        scrambled = true;
        golem.getNavigation().moveTo(x, y, z, speedModifier);
    }

    private double delta() {
        return oldBlockPos.distanceTo(golem.position());
    }

    public boolean failToReachGoal() {
        if (true) {
            fail = true;
            System.out.println("fail");
            double x = blockPos.getX();
            double y = blockPos.getY();
            double z = blockPos.getZ();
            double modifier = 2.0;
            Direction golemDirection = golem.getDirection();
            if (golemDirection == Direction.NORTH) {
                z -= modifier;
            } else if (golemDirection == Direction.SOUTH) {
                z += modifier;
            } else if (golemDirection == Direction.EAST) {
                x += modifier;
            } else if (golemDirection == Direction.WEST) {
                x -= modifier;
            }
            golem.getNavigation().moveTo(x, y, z, speedModifier);
            return true;
        }
        return false;
    }

    @Override
    protected abstract boolean findNearestBlock();

    @Override
    public void start() {
        super.start();
        System.out.println(getMoveToTarget());
        oldBlockPos = golem.position();
        if (!StrawgolemConfig.Harvesting.permanentIgnore.get()) {
            updateBlackList();
            blackList.clearInvalidPos();
        }
    }

    @Override
    public void tick() {
        updateBlackList();
    }

    protected boolean withinDistance(BlockPos targetPos, double distance) {
        return targetPos.closerToCenterThan(mob.position(), distance);
    }

    protected boolean withinDistance(BlockPos targetPos) {
        return withinDistance(targetPos, acceptedDistance());
    }

    public abstract double acceptedDistance();
    protected abstract void updateBlackList();

}
