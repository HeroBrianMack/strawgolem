package com.t2pellet.strawgolem.entity.capabilities.hunger;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public enum HungerState {
    // TO DO IMPLEMENT SPEED, NEED TO MODIFY UPPER CLASSES
    // For others reading this: I'm just writing that I need to
    // modify classes calling these methods now.

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
    // TO DO
    // Convert to movement with formula:
    // Base Movement / (state.value())
    // Ex: Hungry = Base / 3
    // May use unique formula
    public void getSpeed(StrawGolem golem) {
        int hungerStates = HungerState.values().length;
        float speedRatio = (float) (hungerStates - value) / hungerStates;
        float speedValue = (float) (StrawGolem.defaultMovement * speedRatio);
        if (value == 3) {

        }
      //golem.golemWalkSpeed = StrawgolemConfig.Behaviour.golemWalkSpeed.get() * speedRatio;
        golem.setSpeed(speedValue);
//      // Not Starving
//        if (value != 3) {
//            golem.golemRunSpeed = StrawgolemConfig.Behaviour.golemRunSpeed.get() * speedRatio;
//            System.out.println("Not Starving");
//            return;
//
//        }
//
//        // Desperation Running Mechanic
//        //System.out.println(golem.isScared());
//        golem.golemWalkSpeed = StrawgolemConfig.Behaviour.golemRunSpeed.get();
//        golem.golemRunSpeed = StrawgolemConfig.Behaviour.golemRunSpeed.get();
        golem.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(speedValue);
    }
}
