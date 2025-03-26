package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.*;

public class StrawgolemAnimationController extends AnimationController<StrawGolem> {

    public StrawgolemAnimationController(StrawGolem animatable, String name, AnimationStateHandler<StrawGolem> animationPredicate) {
        super(animatable, name, 4, animationPredicate);
    }
}
