package com.t2pellet.strawgolem.mixin.methods;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface FruitGetter {
    public ResourceKey<Block> golemGetFruit();
}
