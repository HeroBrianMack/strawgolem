package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class StrawgolemArmsController extends StrawgolemAnimationController {

    public static final RawAnimation SCARED_ANIM = RawAnimation.begin().thenLoop("arms_scared");
    public static final RawAnimation HOLDING_BLOCK_ANIM = RawAnimation.begin().thenLoop("arms_hold_block");
    public static final RawAnimation HOLDING_ITEM_ANIM = RawAnimation.begin().thenLoop("arms_hold_item");
    public static final RawAnimation RUN_ARMS_ANIM = RawAnimation.begin().thenLoop("arms_run");
    public static final RawAnimation WALK_ARMS_ANIM = RawAnimation.begin().thenLoop("arms_walk");
    public static final RawAnimation IDLE_ANIM =  RawAnimation.begin().thenLoop("arms_idle");

    private static final AnimationStateHandler<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();
        if (event.getController().getAnimationState().equals(State.STOPPED)) {
            event.getController().forceAnimationReset();
        }
        if ((golem.isPickingUpItem() || golem.isPickingUpBlock())) {

//            event.getController().forceAnimationReset();
            return PlayState.CONTINUE;

        } else {

            AnimationController<StrawGolem> controller = event.getController();
            if (golem.isScared()) {
                System.out.println("SCARED");

                controller.setAnimation(SCARED_ANIM);
            } else if (golem.shouldHoldAboveHead()) {
                System.out.println("BLOCK");

                controller.setAnimation(HOLDING_BLOCK_ANIM);
            } else if (golem.getHeldItem().has()) {
                System.out.println("ITEM");

                controller.setAnimation(HOLDING_ITEM_ANIM);
            } else if (golem.isRunning()) {
                System.out.println("RUN");

                controller.setAnimation(RUN_ARMS_ANIM);
            } else if (golem.isMoving()) {
                System.out.println("WALK");

                controller.setAnimation(WALK_ARMS_ANIM);
            } else {
                System.out.println("IDLE");
                controller.setAnimation(IDLE_ANIM);
            }

            System.out.println(event.getController().getAnimationState());
            return PlayState.CONTINUE;
        }
    };

    public StrawgolemArmsController(StrawGolem animatable) {
        super(animatable, "arms_controller", PREDICATE);
    }
}
