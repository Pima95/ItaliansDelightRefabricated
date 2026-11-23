package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import dev.italiansdelight.common.block.entity.CrushingTubBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** One shared animated surface renderer for every Crushing Tub wood variant. */
public final class CrushingTubFluidRenderer
    implements BlockEntityRenderer<CrushingTubBlockEntity, CrushingTubFluidRenderer.State> {
    public CrushingTubFluidRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(CrushingTubBlockEntity entity, State state,
            float partialTicks, Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breaking);
        ItemStack input = entity.getInput();
        state.inputCount = entity.getBlockState().getValue(BlockStateProperties.WATERLOGGED)
            ? 0 : input.getCount();
        // Resolve the actual item model during extraction (never in submit).
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
            state.inputModel, input, ItemDisplayContext.NONE, entity.getLevel(), null,
            entity.getBlockPos().hashCode());
        state.amount = entity.getBlockState().getValue(BlockStateProperties.WATERLOGGED)
            ? 0 : entity.getAmount();
        state.fluidId = entity.getFluidId();
        state.waterTint = Minecraft.getInstance().level == null ? 0xFF3F76E4 :
            BlockTintSources.water().colorInWorld(Blocks.WATER_CAULDRON.defaultBlockState(),
                Minecraft.getInstance().level, entity.getBlockPos());
    }
    @Override public void submit(State state, PoseStack stack, SubmitNodeCollector collector,
            CameraRenderState camera) {
        // Rustic shows the pending solid ingredients in a short stack on the
        // visible wooden bottom (one representative model per eight items).
        // The collision plane is intentionally higher than these item sprites:
        // it is NOT geometry, so it cannot occlude the rendered contents.
        if (state.inputCount > 0 && !state.inputModel.isEmpty()) {
            int layers = Math.min(8, (state.inputCount + 7) / 8);
            for (int i = 0; i < layers; ++i) {
                stack.pushPose();
                // Model space is centered on the origin; lay the item face
                // UP, just above the modeled 1/16-high wooden bottom.
                // NONE skips the Item Frame FIXED transformation, which was
                // tipping/displacing the sprite below the visible floor.
                stack.translate(0.5, 0.09 + i * 0.0625, 0.5);
                stack.rotate(Axis.YP.rotationDegrees(i * 37.0f));
                stack.rotate(Axis.XP.rotationDegrees(-90.0f));
                stack.scale(0.50f, 0.50f, 0.50f);
                state.inputModel.submit(stack, collector,
                    state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                stack.popPose();
            }
        }
        LiquidSurfaceVisuals.submit(state.fluidId, state.amount, CrushingTubBlockEntity.CAPACITY,
            1.01f/16f, 14.99f/16f, 1.01f/16f, 14.99f/16f,
            1.10f/16f, 8.50f/16f, state.waterTint, state.lightCoords, stack, collector);
    }
    public static final class State extends BlockEntityRenderState {
        private String fluidId = "";
        private final ItemStackRenderState inputModel = new ItemStackRenderState();
        private int inputCount;
        private int amount;
        private int waterTint = 0xFF3F76E4;
    }
}
