package com.t2pellet.strawgolem.entity.capabilities;

import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import com.t2pellet.strawgolem.entity.capabilities.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capabilities.tether.Tether;
import com.t2pellet.tlib.entity.capability.api.registry.IModCapabilities;

public class StrawgolemCapabilities implements IModCapabilities {

    @ICapability(Decay.class)
    public static final TLibCapability<Decay> decay = new TLibCapability<>(Decay::getInstance);
    @ICapability(Hunger.class)
    public static final TLibCapability<Hunger> hunger = new TLibCapability<>(Hunger::getInstance);
    @ICapability(HeldItem.class)
    public static final TLibCapability<HeldItem> heldItem = new TLibCapability<>(HeldItem::getInstance);
    @ICapability(Harvester.class)
    public static final TLibCapability<Harvester> harvester = new TLibCapability<>(Harvester::getInstance);
    @ICapability(Deliverer.class)
    public static final TLibCapability<Deliverer> deliverer = new TLibCapability<>(Deliverer::getInstance);
    @ICapability(Tether.class)
    public static final TLibCapability<Tether> tether = new TLibCapability<>(Tether::getInstance);
}
