package com.t2pellet.strawgolem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.client.renderer.layers.StrawgolemItemLayer;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StrawGolemRenderer extends GeoEntityRenderer<StrawGolem> {

    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
        this.addRenderLayer(new StrawgolemItemLayer(this, (StrawgolemGeoModel) model, renderManager.getItemInHandRenderer()));
    }

    @Override
    public void renderFinal(PoseStack poseStack, StrawGolem animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, StrawGolem animatable, BakedGeoModel model, RenderType type, @Nullable MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Set whether to render hat

        getGeoModel().getAnimationProcessor().getBone("hat").setHidden(!animatable.hasHat());
        getGeoModel().getAnimationProcessor().getBone("barrel").setHidden(!animatable.hasBarrel());
        // Shivering animation
        if (StrawgolemConfig.Visual.golemShiversWhenDecayingFast.get() && animatable.isInWaterOrRain()) {
            if (animatable.isInWater() || !animatable.hasHat()) {
                shiver(animatable, poseStack);
            }
        } else if (StrawgolemConfig.Visual.golemShiversWhenCold.get() && animatable.isInCold()) {
            shiver(animatable, poseStack);
        }
        super.actuallyRender(poseStack, animatable, model, type, bufferSource,
                buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


    private void shiver(StrawGolem animatable, PoseStack poseStack) {
        double offX = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
        double offZ = animatable.getRandom().nextDouble() / 32 - 1 / 64F;
        poseStack.translate(offX, 0, offZ);
    }

}
