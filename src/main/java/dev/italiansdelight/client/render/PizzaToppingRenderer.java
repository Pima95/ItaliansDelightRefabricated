package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Raw pizza world renderer: the shared 2.5D treatment is implemented once in
 * PizzaToppingVisuals for both raw and baked pizzas.
 *
 * <p>The saved composition and old-world mozzarella flag are sampled during
 * render-state extraction only. Submitting a frame never touches world data.
 */
public final class PizzaToppingRenderer
        implements BlockEntityRenderer<PizzaDoughBlockEntity, PizzaToppingRenderer.State> {
    private static final float TOP_Y = 1F / 16F + 0.021F;
    private final ItemModelResolver models;

    public PizzaToppingRenderer(BlockEntityRendererProvider.Context context) {
        models = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            PizzaDoughBlockEntity entity, State state, float partialTicks,
            Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                entity, state, partialTicks, cameraPosition, breakProgress);

        boolean legacy = entity.getBlockState().getValue(PizzaDoughBlock.MOZZARELLA);
        state.visuals = PizzaToppingVisuals.extract(
                entity, entity.composition(), legacy, 0b1111, models, TOP_Y);
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
