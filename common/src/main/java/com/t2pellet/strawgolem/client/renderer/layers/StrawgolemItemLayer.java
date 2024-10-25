package com.t2pellet.strawgolem.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;



public class StrawgolemItemLayer extends GeoRenderLayer<StrawGolem> {

    private static final ItemDisplayContext context = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    private final ItemInHandRenderer itemInHandRenderer;
    private final StrawgolemGeoModel model;

    public StrawgolemItemLayer(GeoRenderer<StrawGolem> entityRendererIn, StrawgolemGeoModel model, ItemInHandRenderer itemInHandRenderer) {
        super(entityRendererIn);
        this.model = model;
        this.itemInHandRenderer = itemInHandRenderer;
    }


    // Switching to bone rendering
//    @Override
//    public void render(PoseStack matrixStackIn, StrawGolem golem, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferIn,
//                       VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
//        HeldItem heldItem = golem.getHeldItem();
//        if (heldItem.has()) {
//            matrixStackIn.pushPose();
//            this.model.translateToHand(matrixStackIn);
//            if (golem.isHoldingBlock() && golem.shouldHoldAboveHead()) {
//                this.renderItem(matrixStackIn, bufferIn, packedLightIn, golem);
//            } else {
//                renderForBone(matrixStackIn, golem, bakedModel, renderType, bufferIn,
//                        buffer, partialTicks, packedLightIn, packedOverlay);
//            }
//            matrixStackIn.popPose();
//        }
//    }

    private ItemStack getItemFromBone(GeoBone bone, StrawGolem golem) {
        if (bone.getName().equals("arms")) {
            return golem.getHeldItem().get();
        }
        return null;
    }
    //TODO: Fix wonky arms
    @Override
    public void renderForBone(PoseStack matrixStackIn, StrawGolem golem, GeoBone bone, RenderType renderType, MultiBufferSource bufferIn, VertexConsumer buffer, float partialTick, int packedLightIn, int packedOverlay) {

        if (golem.isHoldingBlock() && golem.shouldHoldAboveHead()) {
            this.renderItem(matrixStackIn, bufferIn, packedLightIn, golem);
        } else {
            ItemStack itemStack = getItemFromBone(bone, golem);
            if (itemStack == null) {
                return;
            }
            renderStackForBone(matrixStackIn, bone, itemStack, golem, bufferIn, partialTick, packedLightIn, packedOverlay);
        }
    }

    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, StrawGolem golem, MultiBufferSource bufferSource, float partialTick, int packedLightIn, int packedOverlay) {
        if (golem instanceof LivingEntity) {
            // A very useless cast due to not being Java 21
            LivingEntity livingEntity = golem;
            Minecraft.getInstance().getItemRenderer().renderStatic(livingEntity, stack, context, false, poseStack, bufferSource, livingEntity.level(), packedLightIn, packedOverlay, livingEntity.getId());

       } else {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, context, packedLightIn, packedOverlay, poseStack, bufferSource, Minecraft.getInstance().level, (int) this.renderer.getInstanceId(golem));
        }

    }

    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem) {
        HeldItem heldItem = golem.getHeldItem();
        boolean holdAboveHead = golem.shouldHoldAboveHead();
        boolean isBlock = golem.isHoldingBlock();
        matrixStackIn.pushPose();
        // I hate ternary when debugging this...
        // For the sake of readability this is now not ternary
        float rotateDeg;
        if (isBlock) {
            rotateDeg = -180.0f;
        } else {
            rotateDeg = -90.0f;
        }
        float yTranslate;
        float zTranslate;
        if (holdAboveHead && isBlock) {
            yTranslate = -0.3f;
            zTranslate = 0.0f;
        } else if (holdAboveHead && !isBlock) {
            yTranslate = 0.0f;
            zTranslate = 0.1f;
        } else {
            yTranslate = -0.45f;
            zTranslate = -0.15f;
        }
        //zTranslate = 1.0f;
        //System.out.println(golem.getXRot() + " " + golem.getYRot());
        //zTranslate = (float)(-1.0f + Math.random());
        //matrixStackIn.rotateAround(Axis.XP.rotationDegrees(rotateDeg), golem.getXRot() * 0.017453292F, golem.getYRot(), 1.0f);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(isBlock ? -180.0F : -90.0F));
        matrixStackIn.translate(0, holdAboveHead ? isBlock ? -0.3F : 0.0F : -0.45F, holdAboveHead ? isBlock ? 0.0F : 0.1F : -0.15F);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        //matrixStackIn.mulPose(Axis.XP.rotationDegrees(golem.getXRot()));
//        matrixStackIn.translate(1.0f, yTranslate, zTranslate);
//        matrixStackIn.translate(0.0F, 0.0625F, 0.1875F);
//        matrixStackIn.mulPose(Axis.XP.rotation(golem.getXRot()));
//        //matrixStackIn.translate(0.0f, 0, 0);
//        matrixStackIn.translate(0.0625F, 0.0F, 0.0F);
        // matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        //golem.get
        this.itemInHandRenderer.renderItem(golem, heldItem.get(), ItemDisplayContext.NONE, false, matrixStackIn, bufferIn, packedLightIn);

        matrixStackIn.popPose();
    }
}
