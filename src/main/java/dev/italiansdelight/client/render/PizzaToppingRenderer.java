package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.italiansdelight.common.block.PizzaDoughBlock;

/** Native immediate-buffer renderer for Minecraft 1.21.1. */
public final class PizzaToppingRenderer implements BlockEntityRenderer<PizzaDoughBlockEntity> {
    public PizzaToppingRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(PizzaDoughBlockEntity entity, float partialTick, PoseStack stack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        boolean legacy = entity.getBlockState().getValue(PizzaDoughBlock.MOZZARELLA);
        PizzaToppingVisuals.submit(PizzaToppingVisuals.extract(entity.composition(), legacy, 0b1111),
                packedLight, stack, buffers);
    }
}
