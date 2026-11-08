package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Blocks;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Uses the shared top surface, with 16,000 mB mapped to the inner height. */
public final class BarrelFluidRenderer
    implements BlockEntityRenderer<BarrelBlockEntity, BarrelFluidRenderer.State> {
    public BarrelFluidRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(
        BarrelBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(
            entity, state, partialTicks, cameraPosition, breaking);
        state.amount = entity.getBlockState().getValue(BlockStateProperties.WATERLOGGED)
            ? 0 : entity.getAmount();
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
        LiquidSurfaceVisuals.submit(state.fluidId, state.fluid, state.amount, BarrelBlockEntity.CAPACITY,
            3.01F/16F, 12.99F/16F, 3.01F/16F, 12.99F/16F,
            2.00F/16F, 15.00F/16F, state.waterTint, state.lightCoords, stack, queue);
    }
    public static final class State extends BlockEntityRenderState {
        private String fluidId = "";
        private FluidVariant fluid = FluidVariant.blank();
        private int amount;
        private int waterTint = 0xFF3F76E4;
    }
}
