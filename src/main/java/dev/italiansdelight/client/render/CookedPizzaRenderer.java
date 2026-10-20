package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Baked pizza renderer. Shares its topping appearance and deterministic
 * placement algorithm with PizzaToppingRenderer. The existing REMAINING mask
 * hides ingredients on eaten/collected slices, without changing their data.
 */
public final class CookedPizzaRenderer
        implements BlockEntityRenderer<CookedPizzaBlockEntity, CookedPizzaRenderer.State> {
    public CookedPizzaRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            CookedPizzaBlockEntity entity, State state, float partialTicks,
            Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                entity, state, partialTicks, cameraPosition, breakProgress);

        int remaining = entity.getBlockState().getValue(CookedPizzaBlock.REMAINING);
        state.visuals = PizzaToppingVisuals.extract(
                entity.pizza().composition(),
                entity.pizza().legacyMozzarella(), remaining);
    }

    @Override
    public void submit(
            State state, PoseStack poseStack,
            SubmitNodeCollector queue, CameraRenderState camera
    ) {
        PizzaToppingVisuals.submit(state.visuals, state.lightCoords, poseStack, queue);
    }

    public static final class State extends BlockEntityRenderState {
        private PizzaToppingVisuals.Visuals visuals = PizzaToppingVisuals.Visuals.EMPTY;
    }
}
