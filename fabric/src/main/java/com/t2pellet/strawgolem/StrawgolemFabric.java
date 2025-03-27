package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.client.StrawgolemClient;
import com.t2pellet.strawgolem.events.ContainerClickHandler;
import com.t2pellet.strawgolem.events.CropGrowthCallback;
import com.t2pellet.strawgolem.events.CropGrowthHandler;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import com.t2pellet.haybalelib.Services;
import com.t2pellet.haybalelib.HaybaleLibFabricMod;
import com.t2pellet.haybalelib.HaybaleLibMod;
import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

@HaybaleLibMod.IMod(Constants.MOD_ID)
public class StrawgolemFabric extends HaybaleLibFabricMod {

    @Override
    protected HaybaleLibMod getCommonMod() {
        return StrawgolemCommon.INSTANCE;
    }

    @Override
    protected HaybaleLibModClient getClientMod() {
        return StrawgolemClient.INSTANCE;
    }

    @Override
    protected void registerEvents() {
        CropGrowthCallback.EVENT.register(CropGrowthHandler::onCropGrowth);
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (ContainerUtil.isContainer(world, hitResult.getBlockPos())) {
                return ContainerClickHandler.onContainerClicked(player, hitResult.getBlockPos());
            }
            return InteractionResult.PASS;
        });
    }
}
