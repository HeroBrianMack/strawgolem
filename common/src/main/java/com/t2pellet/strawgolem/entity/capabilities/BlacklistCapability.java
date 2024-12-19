package com.t2pellet.strawgolem.entity.capabilities;

import net.minecraft.core.BlockPos;

public interface BlacklistCapability {
    void addInvalidPos(BlockPos pos);
    void clearInvalidPos();
}
