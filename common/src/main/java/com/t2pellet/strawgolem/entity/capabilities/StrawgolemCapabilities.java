package com.t2pellet.strawgolem.entity.capabilities;

import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import com.t2pellet.strawgolem.entity.capabilities.hunger.Hunger;
import com.t2pellet.strawgolem.entity.capabilities.tether.Tether;
import com.t2pellet.haybalelib.entity.capability.api.registry.IModCapabilities;

public class StrawgolemCapabilities implements IModCapabilities {

    @ICapability(Decay.class)
    public static final HaybaleLibCapability<Decay> decay = new HaybaleLibCapability<>(Decay::getInstance);
    @ICapability(Hunger.class)
    public static final HaybaleLibCapability<Hunger> hunger = new HaybaleLibCapability<>(Hunger::getInstance);
    @ICapability(HeldItem.class)
    public static final HaybaleLibCapability<HeldItem> heldItem = new HaybaleLibCapability<>(HeldItem::getInstance);
    @ICapability(Harvester.class)
    public static final HaybaleLibCapability<Harvester> harvester = new HaybaleLibCapability<>(Harvester::getInstance);
    @ICapability(Deliverer.class)
    public static final HaybaleLibCapability<Deliverer> deliverer = new HaybaleLibCapability<>(Deliverer::getInstance);
    @ICapability(Tether.class)
    public static final HaybaleLibCapability<Tether> tether = new HaybaleLibCapability<>(Tether::getInstance);
}
