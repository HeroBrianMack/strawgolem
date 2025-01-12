package com.t2pellet.strawgolem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;

public class StrawGolemRenderer extends DynamicGeoEntityRenderer<StrawGolem> {

    ItemStack heldItem;
    ItemInHandRenderer renderer;
    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
        renderer = renderManager.getItemInHandRenderer();
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @javax.annotation.Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, StrawGolem golem) {
                // Retrieve the items in the golem's hands for the relevant bone
                if (bone.getName().equals("hidden") && !golem.shouldHoldAboveHead()) {
                    return heldItem;
                } else if (bone.getName().equals("head") && golem.shouldHoldAboveHead()) {
                    return heldItem;
                } else {
                    return null;
                }
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, StrawGolem animatable) {
                // Apply the camera transform for the given hand
                return switch (bone.getName()) {
                    default -> ItemDisplayContext.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, StrawGolem golem,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (golem.shouldHoldAboveHead()) {
                    StrawGolemRenderer.this.renderBlockForBone(poseStack, bufferSource, packedLight, golem);
                } else {
                    if (stack == heldItem) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(0f));
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                    }
                    super.renderStackForBone(poseStack, bone, stack, golem, bufferSource, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }

    // Only here because the IDE marked it as an error (will still run if removed)
    @Override
    public ResourceLocation getTextureLocation(StrawGolem golem) {
        return this.getGeoModel().getTextureResource(golem);
    }

    @Override
    public void preRender(PoseStack poseStack, StrawGolem animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        heldItem = animatable.getHeldItem().get();
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

    // Note: This is not the renderBlockForBone method of geckolib
    private void renderBlockForBone(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, StrawGolem golem) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(golem.isHoldingBlock() ? -180.0F : -90.0F));
        poseStack.translate(0, golem.isHoldingBlock() ? -0.492F : -0.01F, golem.isHoldingBlock() ? 0.0F : 0.3F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        this.renderer.renderItem(golem, heldItem, ItemDisplayContext.NONE, false, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }


}
