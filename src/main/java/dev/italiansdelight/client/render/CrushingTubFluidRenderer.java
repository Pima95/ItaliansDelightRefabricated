package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
        state.amount = entity.getAmount();
        state.fluidId = entity.getFluidId();
        state.waterTint = Minecraft.getInstance().level == null ? 0xFF3F76E4 :
            BlockTintSources.water().colorInWorld(Blocks.WATER_CAULDRON.defaultBlockState(),
                Minecraft.getInstance().level, entity.getBlockPos());
    }
    @Override public void submit(State state, PoseStack stack, SubmitNodeCollector collector,
            CameraRenderState camera) {
        LiquidSurfaceVisuals.submit(state.fluidId, state.amount, CrushingTubBlockEntity.CAPACITY,
            1.01f/16f, 14.99f/16f, 1.01f/16f, 14.99f/16f,
            1.10f/16f, 8.50f/16f, state.waterTint, state.lightCoords, stack, collector);
    }
    public static final class State extends BlockEntityRenderState {
        private String fluidId = "";
        private int amount;
        private int waterTint = 0xFF3F76E4;
    }
}
