package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GolemPickupItemGoal extends GolemMoveGoal {
    private double range = StrawgolemConfig.Harvesting.harvestRange.get() / 2.0D;
    private Set<BlockPos> blacklist = new HashSet<>();

    public GolemPickupItemGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), StrawgolemConfig.Harvesting.harvestRange.get(), golem);
    }

    @Override
    public boolean canUse() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        return getOldestTarget(nearbyItems) != null && golem.getHeldItem().get().isEmpty();


    }

    @Override
    protected boolean findNearestBlock() {
        return !golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems).isEmpty();
    }

    @Override
    public void start() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        super.start();
        fail = false;
        if (!nearbyItems.isEmpty()) {
            golem.getNavigation().moveTo(nearbyItems.get(0), getSpeed());
        }

    }

    @Override
    public void tick() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        ItemEntity oldest = getOldestTarget(nearbyItems);
        tryTicks++;
        if (shouldRecalculatePath()) {
            System.out.println("recalc");
            System.out.println(blacklist);
            if (!fail && oldest != null) {
                blockPos = oldest.blockPosition();
                System.out.println(oldest.blockPosition());
                fail = !golem.getNavigation().moveTo(oldest, getSpeed());
                System.out.println(fail);
                if (!golem.getLookControl().isLookingAtTarget()) {
                    golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
                }
            }
            if (fail && oldest != null && closeEnough(oldest.getOnPos())) {
                System.out.println("fail");
                failToReachGoal();
            }
        }
    }

    @Override
    public double acceptedDistance() {
        return 1.0;
    }

    public ItemEntity getOldestTarget(List<ItemEntity> entities) {
        if (entities.isEmpty()) {
            return null;
        }
        ItemEntity oldest = null;
        for (ItemEntity entity : entities) {
            if ((oldest == null || entity.getAge() > oldest.getAge()) && !blacklist.contains(entity.blockPosition())) {
                oldest = entity;
            }
        }
        return oldest;
    }

    private double getSpeed() {
        return this.speedModifier;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        return canUse();
    }

    @Override
    protected void blackListAdd(BlockPos blockPos) {
        blacklist.add(blockPos);
    }

    @Override
    protected void blackListClear() {
        blacklist.clear();
    }
}

