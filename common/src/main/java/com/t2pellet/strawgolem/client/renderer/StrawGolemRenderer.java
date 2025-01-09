package com.t2pellet.strawgolem.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;

public class StrawGolemRenderer extends DynamicGeoEntityRenderer<StrawGolem> {

    ItemStack heldItem;
    ItemInHandRenderer renderer;
    public StrawGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrawgolemGeoModel());
//        this.addRenderLayer(new StrawgolemItemLayer(this, (StrawgolemGeoModel) model, renderManager.getItemInHandRenderer()));
        renderer = renderManager.getItemInHandRenderer();
        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            // Return the equipment slot relevant to the bone we're using
            @Nonnull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, StrawGolem animatable) {
                return switch (bone.getName()) {
                    case "rightArm" -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    case "leftArm" -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }
        });
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @javax.annotation.Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, StrawGolem golem) {
                // Retrieve the items in the entity's hands for the relevant bone
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
                    case "leftArm", "rightArm" -> ItemDisplayContext.NONE;
                    default -> ItemDisplayContext.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, StrawGolem golem,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (golem.shouldHoldAboveHead()) {
                    // Not a huge fan of this, but rendering above head was already working fairly correctly
                    // Possibly switch this to bone rendered, but low-priority
                    renderBlock2(poseStack, bufferSource, packedLight, golem);

//                    poseStack.translate(0.0f, 1.0f, 0.0f);
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

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem) {
        HeldItem heldItem = golem.getHeldItem();
        if (heldItem.has()) {
            matrixStackIn.pushPose();
//            this.model.translateToHidden(matrixStackIn);
            boolean holdAboveHead = golem.shouldHoldAboveHead();
            boolean isBlock = golem.isHoldingBlock();
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(isBlock ? -180.0F : -90.0F));
            matrixStackIn.translate(0, holdAboveHead ? isBlock ? -0.3F : 0.0F : -0.45F, holdAboveHead ? isBlock ? 0.0F : 0.1F : -0.15F);
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            this.renderer.renderItem(golem, heldItem.get(), ItemDisplayContext.NONE, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
    }

    private void renderBlock2(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, StrawGolem golem) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(golem.isHoldingBlock() ? -180.0F : -90.0F));
        poseStack.translate(0, golem.isHoldingBlock() ? 0.0F : 0.0F, golem.isHoldingBlock() ? 0.0F : 0.1F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        this.renderer.renderItem(golem, heldItem, ItemDisplayContext.NONE, false, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }


}
