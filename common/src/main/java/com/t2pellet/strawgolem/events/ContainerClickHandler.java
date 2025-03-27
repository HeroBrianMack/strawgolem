package com.t2pellet.strawgolem.events;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.StrawGolemOrderer;
import com.t2pellet.haybalelib.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class ContainerClickHandler {

    private static final Component ORDERED_MESSAGE = Component.translatable("strawgolem.ordering.complete");

    private ContainerClickHandler() {}

    public static InteractionResult onContainerClicked(Player player, BlockPos pos) {
        if (!player.isCrouching() || !player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        StrawGolemOrderer orderer = (StrawGolemOrderer) (Object) player;
        Optional<StrawGolem> golemOptional = orderer.getOrderedGolem();
        if (golemOptional.isPresent()) {
            StrawGolem strawGolem = golemOptional.get();
            strawGolem.getDeliverer().setPriorityPos(pos);
            player.displayClientMessage(ORDERED_MESSAGE, true);
            orderer.setOrderedGolem(null);
            return InteractionResult.SUCCESS;
        }
        orderer.setOrderedGolem(null);
        // Handling Carryon for both forge and fabric (has possible improvement)
        return Services.PLATFORM.isModLoaded("carryon") ? InteractionResult.PASS : InteractionResult.FAIL;
    }
}
