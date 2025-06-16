package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.client.StrawgolemClient;
import com.t2pellet.strawgolem.events.ContainerClickHandler;
import com.t2pellet.strawgolem.events.CropGrowthEvent;
import com.t2pellet.strawgolem.events.CropGrowthHandler;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import com.t2pellet.haybalelib.HaybaleLibNeoMod;
import com.t2pellet.haybalelib.HaybaleLibMod;
import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.Mod;

import java.util.function.Consumer;


@Mod(Constants.MOD_ID)
@HaybaleLibMod.IMod(Constants.MOD_ID)
public class StrawgolemForge extends HaybaleLibNeoMod {

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
        NeoForge.EVENT_BUS.addListener((Consumer<CropGrowthEvent>) event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                CropGrowthHandler.onCropGrowth(serverLevel, event.getPos());
            }
        });
        NeoForge.EVENT_BUS.addListener((Consumer<PlayerInteractEvent.RightClickBlock>) event -> {
            if (ContainerUtil.isContainer(event.getLevel(), event.getPos())) {
                ContainerClickHandler.onContainerClicked(event.getEntity(), event.getPos());
            }
        });
    }
}