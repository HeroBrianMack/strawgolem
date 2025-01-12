package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class StrawgolemMovementController extends StrawgolemAnimationController {

    public static final RawAnimation LEGS_RUN_ANIM =  RawAnimation.begin().thenPlay("legs_run");
    public static final RawAnimation LEGS_WALK_ANIM = RawAnimation.begin().thenPlay("legs_walk");
    public static final RawAnimation LEGS_IDLE_ANIM = RawAnimation.begin().thenPlay("legs_idle");

    private static final AnimationStateHandler<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();
        if (golem.isPickingUpBlock() || golem.isPickingUpItem()) return PlayState.STOP;

        AnimationController<StrawGolem> controller = event.getController();
        if (golem.isRunning()) controller.setAnimation(LEGS_RUN_ANIM);
        else if (golem.isMoving()) controller.setAnimation(LEGS_WALK_ANIM);
        else controller.setAnimation(LEGS_IDLE_ANIM);

        return PlayState.CONTINUE;
    };

    public StrawgolemMovementController(StrawGolem animatable) {
        super(animatable, "move_controller", PREDICATE);
    }

}
