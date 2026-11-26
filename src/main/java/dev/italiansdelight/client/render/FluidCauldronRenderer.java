package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Native immediate-buffer renderer for Minecraft 1.21.1. */
public final class FluidCauldronRenderer implements BlockEntityRenderer<FluidCauldronBlockEntity> {
    public FluidCauldronRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(FluidCauldronBlockEntity entity, float partialTick, PoseStack stack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        int amount = entity.getAmount();
        int tint = entity.getLevel() == null ? 0xFF3F76E4 :
            BiomeColors.getAverageWaterColor(entity.getLevel(), entity.getBlockPos());
        LiquidSurfaceVisuals.submit(entity.getFluidId(), entity.getFluidVariant(), amount, FluidCauldronBlockEntity.CAPACITY,
            2.02F/16F, 13.98F/16F, 2.02F/16F, 13.98F/16F, 7.00F/16F, 15.00F/16F,
            tint, packedLight, stack, buffers);
    }
}
