package com.t2pellet.strawgolem.compat.jade;

import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class StrawgolemJadePlugin implements IWailaPlugin, IEntityComponentProvider {

    private static final ResourceLocation UUID = new ResourceLocation(Constants.MOD_ID, "strawgolem");
    private static final ResourceLocation DECAY = new ResourceLocation(Constants.MOD_ID, "strawgolem_decay");
    private static final ResourceLocation HUNGER = new ResourceLocation(Constants.MOD_ID, "strawgolem_hunger");
    private static final ResourceLocation BARREL = new ResourceLocation(Constants.MOD_ID, "strawgolem_barrel");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(this, StrawGolem.class);
        registration.addConfig(DECAY, true);
        registration.addConfig(HUNGER, true);
        registration.addConfig(BARREL, true);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (iPluginConfig.get(DECAY)) {
            StrawGolem golem = (StrawGolem) entityAccessor.getEntity();
            String decay = golem.getDecay().getState().getDescription();
            iTooltip.add(Component.translatable(decay));
        }
        if (iPluginConfig.get(HUNGER)) {
            StrawGolem golem = (StrawGolem) entityAccessor.getEntity();
            String hunger = golem.getHunger().getState().getDescription();
            iTooltip.add(Component.translatable(hunger));
        }
        if (iPluginConfig.get(BARREL)) {
            StrawGolem golem = (StrawGolem) entityAccessor.getEntity();
            if (golem.hasBarrel()) {
                Component component = Component.translatable("strawgolem.barrel.health", golem.getBarrelHealth());
                iTooltip.add(component);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UUID;
    }
}
