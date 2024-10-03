package com.t2pellet.strawgolem.entity.capabilities.hunger;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;

public enum HungerState {
    // TO DO, CHANGE FROM HEALTH TO MOVEMENT

    FULL("strawgolem.health.new", 0),
    PECKISH("strawgolem.health.old", 1),
    HUNGRY("strawgolem.health.withered", 2),
    STARVING("strawgolem.health.dying", 3);

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

    // TO DO
    // Convert to movement with formula:
    // Base Movement / (state.value() + 1)
    // Ex: Hungry = Base / 3
    // May use unique formula
    public float getSpeed(boolean isRunning) {
        int hungerStates = HungerState.values().length;
        float speedRatio = (float) (hungerStates - value) / hungerStates;
        float speedValue = speedRatio;
        if (isRunning) {
            speedValue *= StrawgolemConfig.Behaviour.golemRunSpeed.get();
        } else {
            speedValue *= StrawgolemConfig.Behaviour.golemWalkSpeed.get();
        }
        //System.out.println(speedValue);
        return speedValue;
    }
}
