package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.random.RandomGenerator;

public class GolemPickupItemGoal extends MoveToBlockGoal {
    private StrawGolem golem;
    private RandomGenerator random = () -> 0;
    double range = StrawgolemConfig.Harvesting.harvestRange.get();

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
            System.out.println(nearbyItems.get(0).getOnPos());
            golem.getNavigation().moveTo(nearbyItems.get(0), getSpeed());
        }

    }

    @Override
    public void tick() {
        List<ItemEntity> nearbyItems = golem.level().getEntitiesOfClass(ItemEntity.class, golem.getBoundingBox().inflate(range, range, range), golem.validGolemItems);
        BlockPos targetPos;
        ItemEntity oldest = getOldestTarget(nearbyItems);
        if (!nearbyItems.isEmpty() && !shouldRecalculatePath()) {
            golem.getNavigation().moveTo(oldest, getSpeed());
        } else if (!nearbyItems.isEmpty()) {
            targetPos = nearbyItems.get(0).blockPosition();
            if (this.shouldRecalculatePath()) {
//                this.mob.getNavigation().stop();
//                this.mob.getNavigation().moveTo((double)((float)targetPos.getX()) + random.nextDouble(-.5f, 0.5f), (double)targetPos.getY() + random.nextDouble(-.5f, 0.5f), (double)((float)targetPos.getZ()) + random.nextDouble(-.5f, 0.5f), this.speedModifier);
                golem.getNavigation().moveTo(oldest, getSpeed());
            }
            if (!golem.getLookControl().isLookingAtTarget()) {
                golem.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
            }
        }

    }

    public ItemEntity getOldestTarget(List<ItemEntity> entities) {
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

    public Path directionalIncrease(BlockPos blockPos, Vec3 realPos) {
//        Node endPos = mob.getNavigation().getPath().getEndNode();
        double x = realPos.x();
        double y = realPos.y();
        double z = realPos.z();

        int bx = blockPos.getX();
        int by = blockPos.getY();
        int bz = blockPos.getZ();
        int increment = 1;
        if (x - (double) bx > 0.0D) {
            System.out.println("Decrease X");
            x -= increment;
        } else {
            System.out.println("Increase X");
            x += increment;
        }

/*        if (y - (double) by > 0.0D) {
            System.out.println("Decrease Y");
            y -= increment;
        } else {
            System.out.println("Increase Y");
            y += increment;
        }*/
        if (z - (double) bz > 0.0D) {
            System.out.println("Decrease Z");

            z -= increment;
        } else {
            System.out.println("Increase Z");

            z += increment;
        }
        System.out.printf("X: %f Y: %f Z: %f\n + Block: %s", x, y, z, BlockPos.containing(x, y, z).toShortString());

        return mob.getNavigation().createPath(new BlockPos((int) x, (int) y, (int) z), 1);

    }


    private double getSpeed() {
        return this.speedModifier;
    }
}

