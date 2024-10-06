package com.t2pellet.strawgolem.entity.capabilities.hunger;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.tlib.entity.capability.api.AbstractCapability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HungerImpl <E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Hunger{
    private HungerState state = HungerState.FULL;
    int hungerTime;
    private final int hungerTicks = 100;
    protected HungerImpl(E e) {
        super(e);
        this.hungerTime = hungerTicks;
    }

    @Override
    public void hunger(StrawGolem golem) {
        if (state != HungerState.STARVING) {
            hungerTime--;
            if (hungerTime <= 0) {
                hungerTime = hungerTicks;
                state = HungerState.fromValue(state.getValue() + 1);
            }
        }
        updateSpeedFromState(true, golem);
        if (golem.isAlive()) {
            synchronize();
        }
    }

    @Override
    public void setFromHealth() {

    }

    @Override
    public boolean feed(StrawGolem golem) {
        if (state == HungerState.FULL) return false;
            state = HungerState.fromValue(state.getValue() - 1);
            updateSpeedFromState(true, golem);
        synchronize();
        return true;
    }

    private void updateSpeedFromState(boolean shouldUpdate, StrawGolem golem) {
        state.getSpeed(golem);

//        entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
//        if (shouldUpdate /*|| entity.getSpeed() > speed*/) entity.setSpeed(speed);
    }

    @Override
    public HungerState getState() {
        return state;
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("hungerTicks", hungerTime);
        if (state != null) {
            tag.putInt("hunger", state.getValue());
        }
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        hungerTime = compoundTag.getInt("hungerTicks");
        state = HungerState.fromValue(compoundTag.getInt("hunger"));
    }
}
