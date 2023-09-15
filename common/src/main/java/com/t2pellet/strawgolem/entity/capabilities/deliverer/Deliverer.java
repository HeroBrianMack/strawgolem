package com.t2pellet.strawgolem.entity.capabilities.deliverer;

import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

public interface Deliverer extends Capability {

    static <E extends LivingEntity & ICapabilityHaver> Deliverer getInstance(E entity) {
        return new DelivererImpl<>(entity);
    }

    BlockPos getDeliverPos();

    void deliver(BlockPos pos);

}
