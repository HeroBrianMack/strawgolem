package com.t2pellet.strawgolem.entity.capabilities.hunger;


import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface Hunger extends Capability {

    static <E extends Entity & ICapabilityHaver> Hunger getInstance(E entity) {
        return new HungerImpl<>((LivingEntity & ICapabilityHaver) entity);
    }

    void hunger(StrawGolem golem);
    void setFromHealth();

    boolean feed(StrawGolem golem);

    HungerState getState();

}
