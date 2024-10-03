package com.t2pellet.strawgolem.entity.capabilities.hunger;


import com.t2pellet.strawgolem.entity.capabilities.hunger.HungerImpl;
import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface Hunger extends Capability {

    static <E extends Entity & ICapabilityHaver> Hunger getInstance(E entity) {
        return new HungerImpl<>((LivingEntity & ICapabilityHaver) entity);
    }

    void hunger(boolean isRunning);
    void setFromHealth();

    boolean repair(boolean isRunning);

    HungerState getState();

}
