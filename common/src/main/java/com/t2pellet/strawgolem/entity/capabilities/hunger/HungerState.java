package com.t2pellet.strawgolem.entity.capabilities.hunger;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.ai.attributes.Attributes;

public enum HungerState {

    FULL("strawgolem.hunger.full", 0),
    PECKISH("strawgolem.hunger.peckish", 1),
    HUNGRY("strawgolem.hunger.hungry", 2),
    STARVING("strawgolem.hunger.starving", 3);

    private final String description;
    private final int value;
    HungerState(String description, int value) {
        this.description = description;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static HungerState fromValue(int value) {
        HungerState[] states = HungerState.values();
        for (HungerState state : states) {
            if (state.value == value) return state;
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void updateSpeed(StrawGolem golem) {
        golem.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(getSpeedValue());
    }

    public double getSpeedValue() {
        int hungerStates = HungerState.values().length;
        float speedRatio = (float) (hungerStates - value) / hungerStates;
        return (float) (StrawGolem.defaultMovement * speedRatio);
    }
}
