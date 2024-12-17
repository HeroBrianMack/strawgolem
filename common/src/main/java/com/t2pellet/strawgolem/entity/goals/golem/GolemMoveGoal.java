package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.BlacklistCapability;
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
    public Predicate<StrawGolem> validGolem = this::validGolem;

    public GolemMoveGoal(PathfinderMob mob, double speed, int range, StrawGolem golem, BlacklistCapability obj) {
        super(mob, speed, range);
        this.golem = golem;
        updateBlackList();
        this.level = (ServerLevel) golem.level();
    }

    protected boolean golemCollision() {
        List<StrawGolem> nearbyGolem = golem.level().getEntitiesOfClass(StrawGolem.class, golem.getBoundingBox().inflate(0.3), validGolem);
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
        if (oldBlockPos.distanceTo(golem.position()) == 0.0 && !golem.isFallFlying()) {

            oldBlockPos = golem.position();
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                scramblePath(golem);
            } else { // Golem cannot path to target, find new target.
                blackList.addInvalidPos(blockPos);
                if (!findNearestBlock()) {
                    // Give up on new pathing something has gone seriously wrong with code
                    // or player didn't make the target accessible
                    blackList.clearInvalidPos();
                    if (findNearestBlock()) {
                        return true;
                    }
//                    System.out.println(blockPos.closerToCenterThan(this.mob.position(), this.acceptedDistance()));
                    return false;
                }
            }

            return true;
        } else if ((oldBlockPos.distanceTo(golem.position()) < 0.01)) {
            oldBlockPos = golem.position();
            // If golem is stuck on a golem, make them shift slightly.
            if (golemCollision()) {
                scramblePath(golem);
            }
            return true;
        }
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
            x += modifier;
        } else if (golemDirection == Direction.SOUTH) {
            x -= modifier;
        } else if (golemDirection == Direction.EAST) {
            z += modifier;
        } else if (golemDirection == Direction.WEST) {
            z -= modifier;
        }
        scrambled = true;
        System.out.println(x + " " + y + " " + z);
        golem.getNavigation().moveTo(x, y, z, speedModifier);
    }

    @Override
    protected abstract boolean findNearestBlock();

    @Override
    public void start() {
        super.start();
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

    protected abstract void updateBlackList();

}
