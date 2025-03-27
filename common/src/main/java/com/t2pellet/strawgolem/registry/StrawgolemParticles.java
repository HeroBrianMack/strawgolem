package com.t2pellet.strawgolem.registry;

import com.t2pellet.haybalelib.registry.api.ParticleEntryType;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import net.minecraft.core.particles.SimpleParticleType;

@RegistryClass.IRegistryClass(SimpleParticleType.class)
public class StrawgolemParticles implements RegistryClass {

    @IRegistryEntry
    public static final ParticleEntryType FLY_PARTICLE = new ParticleEntryType("fly");
    @IRegistryEntry
    public static final ParticleEntryType FOOD_PARTICLE = new ParticleEntryType("food");



}
