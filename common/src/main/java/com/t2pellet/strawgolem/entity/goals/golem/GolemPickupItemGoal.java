package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import java.util.List;


public class GolemPickupItemGoal extends MoveToBlockGoal {
    private StrawGolem golem;
    private double range = StrawgolemConfig.Harvesting.harvestRange.get() / 2.0D;

    public GolemPickupItemGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get());
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        return !nearbyItems.isEmpty() && golem.getHeldItem().get().isEmpty();


    }
    @Override
    public void start() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        if (!nearbyItems.isEmpty()) {
            golem.getNavigation().moveTo(nearbyItems.get(0), getSpeed());
        }

    }

    @Override
    public void tick() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        ItemEntity oldest = getOldestTarget(nearbyItems);
        if (!nearbyItems.isEmpty()) {
            golem.getNavigation().moveTo(oldest, getSpeed());
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
        }
    }

    public static ItemEntity getOldestTarget(List<ItemEntity> entities) {
        if (entities.isEmpty()) {
            return null;
        }
        ItemEntity oldest = entities.get(0);
        for (ItemEntity entity : entities) {
            if (entity.getAge() > oldest.getAge()) {
                oldest = entity;
            }
        }
        return oldest;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return canUse();
    }

    private double getSpeed() {
        return this.speedModifier;
    }
}

