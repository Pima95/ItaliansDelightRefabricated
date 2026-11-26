package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.italiansdelight.common.block.CookedPizzaBlock;

/** Native immediate-buffer renderer for Minecraft 1.21.1. */
public final class CookedPizzaRenderer implements BlockEntityRenderer<CookedPizzaBlockEntity> {
    public CookedPizzaRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(CookedPizzaBlockEntity entity, float partialTick, PoseStack stack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        int remaining = entity.getBlockState().getValue(CookedPizzaBlock.REMAINING);
        PizzaToppingVisuals.submit(PizzaToppingVisuals.extract(entity.renderComposition(),
            entity.renderLegacyMask(), remaining), packedLight, stack, buffers);
    }
}
