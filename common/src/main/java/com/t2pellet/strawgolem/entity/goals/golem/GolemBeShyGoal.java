package com.t2pellet.strawgolem.entity.goals.golem;

import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.world.entity.player.Player;

public class GolemBeShyGoal extends GolemFleeEntityGoal<Player> {

    public GolemBeShyGoal(StrawGolem golem) {
        super(golem, Player.class, 2.0F, 0.4D, 0.6D, false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !isTempting();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && !isTempting();
    }

    private boolean isTempting() {
        return toAvoid != null && toAvoid.isHolding(StrawGolem.REPAIR_ITEM);
    }
}
