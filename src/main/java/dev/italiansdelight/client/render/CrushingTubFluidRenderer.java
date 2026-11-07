package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
        state.inputCount = input.getCount();
        // Resolve the actual item model during extraction (never in submit).
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(
            state.inputModel, input, ItemDisplayContext.FIXED, entity.getLevel(), null,
            entity.getBlockPos().hashCode());
        state.amount = entity.getAmount();
        state.fluidId = entity.getFluidId();
        state.waterTint = Minecraft.getInstance().level == null ? 0xFF3F76E4 :
            BlockTintSources.water().colorInWorld(Blocks.WATER_CAULDRON.defaultBlockState(),
                Minecraft.getInstance().level, entity.getBlockPos());
    }
    @Override public void submit(State state, PoseStack stack, SubmitNodeCollector collector,
            CameraRenderState camera) {
        // Rustic shows the pending solid ingredients in a short stack on the
        // bottom of the tub (one representative model for every eight items).
        // The content is a render snapshot and is never a dropped world entity.
        if (state.inputCount > 0 && !state.inputModel.isEmpty()) {
            int layers = Math.min(8, (state.inputCount + 7) / 8);
            for (int i = 0; i < layers; ++i) {
                stack.pushPose();
                stack.translate(0.5, 0.105 + i * 0.045, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(i * 37.0f));
                stack.mulPose(Axis.XP.rotationDegrees(90.0f));
                stack.scale(0.45f, 0.45f, 0.45f);
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
