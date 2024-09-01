package com.t2pellet.strawgolem.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.Constants;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.DecayState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.util.RenderUtils;

public class StrawgolemGeoModel extends GeoModel<StrawGolem> {

    private static final ResourceLocation modelResource = new ResourceLocation(Constants.MOD_ID, "geo/strawgolem.geo.json");
    private static final ResourceLocation animationResource = new ResourceLocation(Constants.MOD_ID, "animations/strawgolem.animation.json");

    // Textures
    private static final ResourceLocation newTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem.png");
    private static final ResourceLocation oldTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem_old.png");
    private static final ResourceLocation dyingTextureResource = new ResourceLocation(Constants.MOD_ID, "textures/straw_golem_dying.png");


    @Override
    public ResourceLocation getModelResource(StrawGolem golem) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureResource(StrawGolem golem) {
        if (!StrawgolemConfig.Visual.golemDecayingTexture.get()) return newTextureResource;

        DecayState state = golem.getDecay().getState();
        switch (state) {
            case NEW -> {
                return newTextureResource;
            }
            case OLD -> {
                return oldTextureResource;
            }
            default -> {
                return dyingTextureResource;
            }
        }
    }



    @Override
    public ResourceLocation getAnimationResource(StrawGolem golem) {
        return animationResource;
    }



    @Override
    public void setCustomAnimations(StrawGolem animatable, long instanceId, AnimationState animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraData().get(0);
        if (head != null && extraData != null) {
            head.setRotX(extraData.headPitch() * (float) Math.PI / 180);
            head.setRotY(extraData.netHeadYaw() * (float) Math.PI / 180);
        }
    }

    public void translateToHand(PoseStack poseStack) {
        if (getBone("arms").isPresent() && getBone("upper").isPresent()) {
            GeoBone arms = getBone("arms").get();
            GeoBone upper = getBone("upper").get();
            RenderUtils.prepMatrixForBone(poseStack, upper);
            RenderUtils.translateAndRotateMatrixForBone(poseStack, upper);
            RenderUtils.prepMatrixForBone(poseStack, arms);
            RenderUtils.translateAndRotateMatrixForBone(poseStack, arms);
        }
    }
}
