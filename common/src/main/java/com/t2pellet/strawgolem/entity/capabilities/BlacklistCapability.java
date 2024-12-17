package com.t2pellet.strawgolem.entity.capabilities;

import net.minecraft.core.BlockPos;

public interface BlacklistCapability {
    void addInvalidPos(BlockPos pos);
    // Possibly unncessary since this event will likely refresh.
    // Examine results in future.
    void clearInvalidPos();
}
