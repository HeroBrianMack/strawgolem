package com.t2pellet.strawgolem.client;

import com.t2pellet.strawgolem.client.registry.StrawgolemEntityRenderers;
import com.t2pellet.strawgolem.client.registry.StrawgolemParticleFactories;
import com.t2pellet.haybalelib.client.HaybaleLibModClient;
import com.t2pellet.haybalelib.registry.api.RegistryClass;

public class StrawgolemClient extends HaybaleLibModClient {
    public static final StrawgolemClient INSTANCE = new StrawgolemClient();

    @Override
    public Class<? extends RegistryClass> entityRenderers() {
        return StrawgolemEntityRenderers.class;
    }

    @Override
    public Class<? extends RegistryClass> particleFactories() {
        return StrawgolemParticleFactories.class;
    }
}
