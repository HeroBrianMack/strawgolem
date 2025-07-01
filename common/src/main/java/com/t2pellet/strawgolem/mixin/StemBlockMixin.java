package com.t2pellet.strawgolem.mixin;

import com.t2pellet.strawgolem.mixin.methods.FruitGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StemBlock.class)
public class StemBlockMixin implements FruitGetter {
    @Shadow
    @Final
    private ResourceKey<Block> fruit;

    @Override
    public ResourceKey<Block> getFruit() {
        return fruit;
    }
}
