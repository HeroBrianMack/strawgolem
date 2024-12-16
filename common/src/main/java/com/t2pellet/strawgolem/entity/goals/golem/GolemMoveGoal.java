package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;


public abstract class GolemMoveGoal extends MoveToBlockGoal {
    protected final StrawGolem golem;
    protected final ServerLevel level;
    protected Vec3 oldBlockPos = null;
    protected boolean scrambled = false;
    public Predicate<StrawGolem> validGolem = this::validGolem;

    public GolemMoveGoal(PathfinderMob mob, double speed, int range, StrawGolem golem) {
        super(mob, speed, range);
        this.golem = golem;
        this.level = (ServerLevel) golem.level();
    }

    protected boolean golemCollision() {
        List<StrawGolem> nearbyGolem = golem.level().getEntitiesOfClass(StrawGolem.class, golem.getBoundingBox().inflate(0.5), validGolem);
        return !nearbyGolem.isEmpty();
    }

    public boolean validGolem(StrawGolem golem) {
        return !golem.position().equals(this.golem.position());
    }

}
