package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.entity.CrushingTubBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Native immediate-buffer renderer for Minecraft 1.21.1. */
public final class CrushingTubFluidRenderer implements BlockEntityRenderer<CrushingTubBlockEntity> {
    public CrushingTubFluidRenderer(BlockEntityRendererProvider.Context context) {}

    @Override public void render(CrushingTubBlockEntity entity, float partialTick, PoseStack stack,
            MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (entity.getBlockState().getValue(BlockStateProperties.WATERLOGGED)) return;
        ItemStack input = entity.getInput();
        if (!input.isEmpty()) {
            int layers = Math.min(8, (input.getCount() + 7) / 8);
            for (int i = 0; i < layers; ++i) {
                stack.pushPose();
                stack.translate(0.5, 0.09 + i * 0.0625, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(i * 37.0f));
                stack.mulPose(Axis.XP.rotationDegrees(-90.0f));
                stack.scale(0.50f, 0.50f, 0.50f);
                Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemDisplayContext.NONE,
                    packedLight, OverlayTexture.NO_OVERLAY, stack, buffers, entity.getLevel(),
                    entity.getBlockPos().hashCode());
                stack.popPose();
            }
        }
        int tint = entity.getLevel() == null ? 0xFF3F76E4 :
            BiomeColors.getAverageWaterColor(entity.getLevel(), entity.getBlockPos());
        LiquidSurfaceVisuals.submit(entity.getFluidId(), entity.getAmount(), CrushingTubBlockEntity.CAPACITY,
            1.01f/16f, 14.99f/16f, 1.01f/16f, 14.99f/16f, 1.10f/16f, 8.50f/16f,
            tint, packedLight, stack, buffers);
    }
}
