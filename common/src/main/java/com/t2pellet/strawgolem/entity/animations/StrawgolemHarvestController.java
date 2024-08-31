package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class StrawgolemHarvestController extends StrawgolemAnimationController {

    public static final RawAnimation HARVEST_BLOCK_ANIM = RawAnimation.begin().thenPlay("harvest_block");
    public static final RawAnimation HARVEST_ITEM_ANIM = RawAnimation.begin().thenPlay("harvest_item");

    private static PlayState predicate(AnimationState<StrawGolem> event) {
        // Appropriate animation for regular crop or gourd crop
        if (event.getAnimatable().isPickingUpBlock()) {
            if (StrawgolemConfig.Visual.showHarvestBlockAnimation.get()) {
                event.getController().setAnimation(HARVEST_BLOCK_ANIM);
                return PlayState.CONTINUE;
            }
        } else if (event.getAnimatable().isPickingUpItem()) {
            if (StrawgolemConfig.Visual.showHarvestItemAnimation.get()) {
                event.getController().setAnimation(event.getAnimatable().hasBarrel() ? HARVEST_BLOCK_ANIM : HARVEST_ITEM_ANIM);
                return PlayState.CONTINUE;
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
