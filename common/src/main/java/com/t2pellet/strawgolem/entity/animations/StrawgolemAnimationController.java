package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.*;

public class StrawgolemAnimationController extends AnimationController<StrawGolem> {

    public StrawgolemAnimationController(StrawGolem animatable, String name, AnimationStateHandler<StrawGolem> animationPredicate) {
        super(animatable, name, 4, animationPredicate);
    }

    protected void setAnimation(@NotNull String animation) {
        setAnimation(animation, Animation.LoopType.LOOP);
    }

    protected void setAnimation(@NotNull String animation, Animation.LoopType loopType) {
        AnimationProcessor.QueuedAnimation current = getCurrentAnimation();
        boolean isNewAnimation = current == null || !current.animation().name().equals(animation);
        if (!animation.isEmpty() && isNewAnimation) {
            RawAnimation.begin().then(animation, loopType);
        }
    }
}
