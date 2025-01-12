package com.t2pellet.strawgolem.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.t2pellet.strawgolem.client.model.StrawgolemGeoModel;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@Deprecated
public class StrawgolemItemLayer extends GeoRenderLayer<StrawGolem> {

    private final ItemInHandRenderer itemInHandRenderer;
    private final StrawgolemGeoModel model;

    public StrawgolemItemLayer(GeoRenderer<StrawGolem> entityRendererIn, StrawgolemGeoModel model, ItemInHandRenderer itemInHandRenderer) {
        super(entityRendererIn);
        this.model = model;
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack matrixStackIn, StrawGolem golem, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferIn,
                       VertexConsumer buffer, float partialTicks, int packedLightIn, int packedOverlay) {
        HeldItem heldItem = golem.getHeldItem();
        if (heldItem.has()) {
            matrixStackIn.pushPose();
//            this.model.translateToHidden(matrixStackIn);
            this.renderItem(matrixStackIn, bufferIn, packedLightIn, golem);
            matrixStackIn.popPose();
        }
    }

    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, StrawGolem golem) {
        HeldItem heldItem = golem.getHeldItem();
        if (heldItem.has()) {
            matrixStackIn.pushPose();
//            this.model.translateToHidden(matrixStackIn);
            this.renderItem(matrixStackIn, bufferIn, packedLightIn, golem);
            matrixStackIn.popPose();
        }
        //        if (model.getBone("hidden").isPresent()) {
//            System.out.println("hidden found");
//        }
        boolean holdAboveHead = golem.shouldHoldAboveHead();
        boolean isBlock = golem.isHoldingBlock();
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(isBlock ? -180.0F : -90.0F));
        matrixStackIn.translate(0, holdAboveHead ? isBlock ? -0.3F : 0.0F : -0.45F, holdAboveHead ? isBlock ? 0.0F : 0.1F : -0.15F);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        this.itemInHandRenderer.renderItem(golem, heldItem.get(), ItemDisplayContext.NONE, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

}
