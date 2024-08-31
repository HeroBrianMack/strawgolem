package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class StrawgolemArmsController extends StrawgolemAnimationController {

    public static final RawAnimation SCARED_ANIM = RawAnimation.begin().thenPlay("arms_scared");
    public static final RawAnimation HOLDING_BLOCK_ANIM = RawAnimation.begin().thenPlay("arms_hold_block");
    public static final RawAnimation HOLDING_ITEM_ANIM = RawAnimation.begin().thenPlay("arms_hold_item");
    public static final RawAnimation RUN_ARMS_ANIM = RawAnimation.begin().thenPlay("arms_run");
    public static final RawAnimation WALK_ARMS_ANIM = RawAnimation.begin().thenPlay("arms_walk");
    public static final RawAnimation IDLE_ANIM =  RawAnimation.begin().thenPlay("arms_idle");

    private static final AnimationStateHandler<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();
        if (golem.isPickingUpItem() || golem.isPickingUpBlock()) {
            return PlayState.STOP;
        }

        AnimationController<StrawGolem> controller = event.getController();
        if (golem.isScared()) controller.setAnimation(SCARED_ANIM);
        else if (golem.shouldHoldAboveHead()) controller.setAnimation(HOLDING_BLOCK_ANIM);
        else if (golem.getHeldItem().has()) controller.setAnimation(HOLDING_ITEM_ANIM);
        else if (golem.isRunning()) controller.setAnimation(RUN_ARMS_ANIM);
        else if (golem.isMoving()) controller.setAnimation(WALK_ARMS_ANIM);
        else controller.setAnimation(IDLE_ANIM);

        return PlayState.CONTINUE;
    };

    public StrawgolemArmsController(StrawGolem animatable) {
        super(animatable, "arms_controller", PREDICATE);
    }
}
