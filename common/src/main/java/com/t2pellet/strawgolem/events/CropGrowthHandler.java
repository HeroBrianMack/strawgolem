package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Handles crop growth events, adds them to the crop handler
 */
public class CropGrowthHandler {

    private CropGrowthHandler() {
    }

    public static void onCropGrowth(ServerLevel world, BlockPos cropPos) {
        List<StrawGolem> nearbyGolems = getNearbyGolems(world, cropPos);
        Optional<StrawGolem> nearestGolem = nearbyGolems.stream()
                .filter(golem -> !golem.getHarvester().isHarvesting())
                .min(Comparator.comparingDouble(g -> cropPos.distSqr(g.blockPosition())));
        nearestGolem.ifPresent(g -> g.getHarvester().queueHarvest(cropPos));
    }

    private static List<StrawGolem> getNearbyGolems(ServerLevel level, BlockPos pos) {
        AABB searchRange = new AABB(pos).inflate(StrawgolemConfig.Harvesting.harvestRange.get());
        return level.getEntitiesOfClass(StrawGolem.class, searchRange);
    }

}
