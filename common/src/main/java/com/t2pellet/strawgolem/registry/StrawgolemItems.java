package com.t2pellet.strawgolem.registry;

import com.t2pellet.haybalelib.registry.api.ItemEntryType;
import com.t2pellet.haybalelib.registry.api.RegistryClass;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

@RegistryClass.IRegistryClass(Item.class)
public class StrawgolemItems implements RegistryClass {

    // Creative Tabs were reworked D:
    private static final Item.Properties strawHatProperties = new Item.Properties()
            .stacksTo(1);

    @IRegistryEntry
    public static final ItemEntryType strawHat = new ItemEntryType("straw_hat", strawHatProperties);

}
