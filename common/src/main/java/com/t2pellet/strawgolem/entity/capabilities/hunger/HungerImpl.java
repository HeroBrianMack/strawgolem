package com.t2pellet.strawgolem.entity.capabilities.hunger;

import com.t2pellet.tlib.entity.capability.api.AbstractCapability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HungerImpl <E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Hunger{
    private HungerState state = HungerState.FULL;
    int hungerTime;
    protected HungerImpl(E e) {
        super(e);
        this.hungerTime = 100;
    }

    @Override
    public void hunger(boolean isRunning) {
        updateSpeedFromState(true, isRunning);
    }

    @Override
    public void setFromHealth() {

    }

    @Override
    public boolean repair(boolean isRunning) {
        if (state == HungerState.FULL) return false;
            state = HungerState.fromValue(state.getValue() - 1);
            updateSpeedFromState(true, isRunning);
        synchronize();
        return true;
    }

    private void updateSpeedFromState(boolean shouldUpdate, boolean isRunning) {
//        float speed = state.getSpeed(isRunning);
//
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
