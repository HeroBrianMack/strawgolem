package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.hunger.HungerState;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class GolemFoodGoal extends TemptGoal {

    StrawGolem golem;
    public GolemFoodGoal(StrawGolem golem) {
        super(golem, StrawgolemConfig.Behaviour.golemWalkSpeed.get(), Ingredient.of(StrawGolem.FEED_ITEM), false);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        return !golem.getHeldItem().has() && golem.getHunger().getState() != HungerState.FULL && super.canUse();
    }
}
