package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Native immediate-buffer renderer for Minecraft 1.21.1. */
public final class BarrelFluidRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    public BarrelFluidRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(BarrelBlockEntity entity, float partialTick, PoseStack stack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        int amount = entity.getBlockState().getValue(BlockStateProperties.WATERLOGGED) ? 0 : entity.getAmount();
        int tint = entity.getLevel() == null ? 0xFF3F76E4 :
            BiomeColors.getAverageWaterColor(entity.getLevel(), entity.getBlockPos());
        LiquidSurfaceVisuals.submit(entity.getFluidId(), entity.getFluidVariant(), amount, BarrelBlockEntity.CAPACITY,
            3.01F/16F, 12.99F/16F, 3.01F/16F, 12.99F/16F, 2.00F/16F, 15.00F/16F,
            tint, packedLight, stack, buffers);
    }
}
