package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Blocks;
import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Native vanilla iron cauldron model with an actual fluid level, no baked slab. */
public final class FluidCauldronRenderer
    implements BlockEntityRenderer<FluidCauldronBlockEntity, FluidCauldronRenderer.State> {
    public FluidCauldronRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(
        FluidCauldronBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(
            entity, state, partialTicks, cameraPosition, breaking);
        state.amount = entity.getAmount();
        state.fluidId = entity.getFluidId();
        state.fluid = entity.getFluidVariant();
        // The same biome tint vanilla uses on a water cauldron.
        // ClientLevel implements BlockAndTintGetter; generic Level does not.
        state.waterTint = Minecraft.getInstance().level == null ? 0xFF3F76E4 :
            BlockTintSources.water().colorInWorld(
                Blocks.WATER_CAULDRON.defaultBlockState(),
                Minecraft.getInstance().level, entity.getBlockPos());
    }
    @Override public void submit(State state, PoseStack stack, SubmitNodeCollector queue,
                                 CameraRenderState camera) {
        LiquidSurfaceVisuals.submit(state.fluidId, state.fluid, state.amount, FluidCauldronBlockEntity.CAPACITY,
            2.02F/16F, 13.98F/16F, 2.02F/16F, 13.98F/16F,
            7.00F/16F, 15.00F/16F, state.waterTint, state.lightCoords, stack, queue);
    }
    public static final class State extends BlockEntityRenderState {
        private String fluidId = "";
        private FluidVariant fluid = FluidVariant.blank();
        private int amount;
        private int waterTint = 0xFF3F76E4;
    }
}
