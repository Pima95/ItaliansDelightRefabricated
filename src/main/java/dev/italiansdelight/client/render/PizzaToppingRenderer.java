package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Phase 3F: dynamic SOLID toppings using the ORIGINAL item models.
 *
 * <p>Four physical quarters are always defined, even when the logical cuts
 * have been joined. Applications are drawn only on their remainingMask bits,
 * preserving their real source ItemStack (including cooked/raw/external
 * equivalents). The render state is extracted before the drawing phase:
 * no world/BE/item data is accessed by submit().
 *
 * <p>All geometry is visual only. No new hitbox, texture, block model,
 * gameplay or loot state is introduced.
 */
public final class PizzaToppingRenderer
        implements BlockEntityRenderer<PizzaDoughBlockEntity, PizzaToppingRenderer.State> {

    // Dough stage 4: top surface is y = 1px; the raised rim is 1px higher.
    // Icons sit on the dough but entirely inside that rim.
    private static final float SURFACE_Y = 1.0F / 16.0F + 0.021F;
    private static final float ITEM_SCALE = 0.17F;
    private static final double CENTER_OFFSET = 0.21;

    // The six positions remain fixed per physical quarter. An ingredient
    // refund/removal cannot make a different quarter suddenly rotate or
    // replace its textures. Small offsets keep toppings from stacking exactly.
    private static final float[][] SLOTS = {
            {0.00F, 0.00F},
            {-0.070F, -0.060F},
            {0.070F, -0.050F},
            {-0.065F, 0.065F},
            {0.065F, 0.070F},
            {0.00F, 0.095F}
    };

    private final ItemModelResolver models;

    public PizzaToppingRenderer(BlockEntityRendererProvider.Context context) {
        this.models = context.itemModelResolver();
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

        PizzaComposition composition = entity.composition();
        if (entity.getLevel() == null || composition.applications().isEmpty()) {
            state.pieces = List.of();
            return;
        }

        Direction forward = switch (composition.orientation()) {
            case "north" -> Direction.NORTH;
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };

        int rightX = -forward.getStepZ();
        int rightZ = forward.getStepX();
        int frontX = forward.getStepX();
        int frontZ = forward.getStepZ();

        List<Piece> pieces = new ArrayList<>();
        int[] occupiedSlots = new int[PizzaComposition.QUARTER_COUNT];

        // The order is always original application order (oldest at bottom).
        for (PizzaToppingApplication application : composition.applications()) {
            ItemStack original = application.originalStack();
            if (original.isEmpty()) {
                continue;
            }
            for (int quarter = 0; quarter < PizzaComposition.QUARTER_COUNT; quarter++) {
                if (!application.covers(quarter)) {
                    continue;
                }
                int slot = occupiedSlots[quarter]++;
                if (slot >= SLOTS.length) {
                    continue; // Defensive upper bound: saved codec validates 6.
                }

                int rightSign = quarter >= 2 ? 1 : -1;
                int frontSign = (quarter & 1) != 0 ? 1 : -1;
                float u = (float) (rightSign * CENTER_OFFSET) + SLOTS[slot][0];
                float v = (float) (frontSign * CENTER_OFFSET) + SLOTS[slot][1];
                float x = 0.5F + rightX * u + frontX * v;
                float z = 0.5F + rightZ * u + frontZ * v;

                ItemStackRenderState itemState = new ItemStackRenderState();
                this.models.updateForTopItem(
                        itemState, original, ItemDisplayContext.FIXED,
                        entity.getLevel(), null,
                        (int) (application.sequence() ^ (quarter * 37L)));
                if (itemState.isEmpty()) {
                    continue;
                }

                // No ticking RNG: same pizza, same placement on every client.
                float rotation = (float) ((application.sequence() * 41L
                        + quarter * 47L) % 360L);
                float y = SURFACE_Y + slot * 0.001F;
                pieces.add(new Piece(x, y, z, rotation, itemState));
            }
        }

        state.pieces = List.copyOf(pieces);
    }

    @Override
    public void submit(
            State state, PoseStack poseStack,
            SubmitNodeCollector queue, CameraRenderState camera
    ) {
        for (Piece piece : state.pieces) {
            poseStack.pushPose();
            poseStack.translate(piece.x(), piece.y(), piece.z());
            // FIXED model is upright in XY; rotate onto the XZ pizza surface.
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(piece.rotation()));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            piece.renderState().submit(
                    poseStack, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static final class State extends BlockEntityRenderState {
        private List<Piece> pieces = List.of();
    }

    private record Piece(
            float x, float y, float z, float rotation,
            ItemStackRenderState renderState
    ) {
    }
}
