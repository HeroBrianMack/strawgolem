package com.t2pellet.strawgolem.util;

import com.t2pellet.strawgolem.StrawgolemConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class VisibilityUtil {

    private VisibilityUtil() {}

    @Deprecated
    public static boolean canSee(LivingEntity e, BlockPos query) {
//        Level level = e.level();
//        Vec3 entityPos = e.getEyePosition();
//        Vec3 queryPos = Vec3.atCenterOf(query);
//        ClipContext ctx = new ClipContext(entityPos, queryPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e);
//        BlockHitResult result = level.clip(ctx);
//        ClipContext ctx2 = new ClipContext(entityPos, queryPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, e);
//        BlockHitResult result2 = level.clip(ctx2);

//        return result.getBlockPos().equals(query) || result.getBlockPos().equals(result2);
        return isNearby(e, query);
    }


    public static boolean isNearby(LivingEntity e, BlockPos block) {
        int distanceX = Math.abs(e.getBlockX() - block.getX());
        int distanceY = Math.abs(e.getBlockY() - block.getY());
        int distanceZ = Math.abs(e.getBlockZ() - block.getZ());
        // If block is within harvest range front/back/left/right
        // and within 2 above or below, the golem should see it.
        if (distanceX > StrawgolemConfig.Harvesting.harvestRange.get() || distanceY > 3 || distanceZ > StrawgolemConfig.Harvesting.harvestRange.get()) {
            return false;
        }
        return true;

    }

}
