package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class StrawgolemHarvestController extends StrawgolemAnimationController {

    public static final RawAnimation HARVEST_BLOCK_ANIM = RawAnimation.begin().thenPlay("harvest_block");
    public static final RawAnimation HARVEST_ITEM_ANIM = RawAnimation.begin().thenPlay("harvest_item");

    private static PlayState predicate(AnimationState<StrawGolem> event) {
        // Appropriate animation for regular crop or gourd crop
        AnimationController<StrawGolem> controller = event.getController();
        if (event.getAnimatable().isPickingUpBlock()) {
            // Should never be stopped if not picking up things...
            refresh(controller);
            if (StrawgolemConfig.Visual.showHarvestBlockAnimation.get()) {
                return event.setAndContinue(HARVEST_BLOCK_ANIM);
            }
        } else if (event.getAnimatable().isPickingUpItem()) {
            refresh(controller);
            if (StrawgolemConfig.Visual.showHarvestItemAnimation.get()) {
                return event.setAndContinue(event.getAnimatable().hasBarrel() ? HARVEST_BLOCK_ANIM : HARVEST_ITEM_ANIM);
            }
        }
        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    public StrawgolemHarvestController(StrawGolem animatable) {
        super(animatable, "harvest", StrawgolemHarvestController::predicate);
        setCustomInstructionKeyframeHandler(event -> {
            if (event.getKeyframeData().getInstructions().equals("completeHarvest")) {
                animatable.setPickingUpBlock(false);
                animatable.setPickingUpItem(false);
            }
        });
    }
}
