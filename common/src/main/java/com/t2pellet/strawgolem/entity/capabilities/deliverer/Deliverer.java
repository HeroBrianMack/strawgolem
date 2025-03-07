package com.t2pellet.strawgolem.entity.capabilities.deliverer;

import com.t2pellet.strawgolem.entity.capabilities.BlacklistCapability;
import com.t2pellet.tlib.entity.capability.api.Capability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface Deliverer extends Capability, BlacklistCapability {

    static <E extends Entity & ICapabilityHaver> Deliverer getInstance(E entity) {
        return new DelivererImpl<>((LivingEntity & ICapabilityHaver) entity);
    }

    BlockPos getDeliverPos();
    void setPriorityPos(BlockPos pos);
    void deliver(BlockPos pos);
    boolean hasPriorityPos();


}
