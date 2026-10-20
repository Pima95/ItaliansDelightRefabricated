package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.registry.ModItems;
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
 * Phase 3F/3G: dynamic approved toppings using ORIGINAL item models.
 *
 * <p>Four physical quarters are always defined, even when the logical cuts
 * have been joined. Applications are drawn only on their remainingMask bits,
 * preserving their real source ItemStack (including cooked/raw/external
 * equivalents). Creams and sauces temporarily use the same item-model
 * placeholder: their final low-profile surface rendering remains a separate
 * visual-art task. The render state is extracted before the drawing phase:
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
        boolean legacyMozzarella = entity.getBlockState().getValue(PizzaDoughBlock.MOZZARELLA);
        if (entity.getLevel() == null
                || (composition.applications().isEmpty() && !legacyMozzarella)) {
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

        // Keep each canonical ingredient in its original quarter and order.
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
                    continue; // Valid compositions contain at most six toppings per quarter.
                }
                addPiece(pieces, entity, original, application.sequence(), quarter,
                        slot, slot, rightX, rightZ, frontX, frontZ);
            }
        }

        // Historical pizzas may have the old whole-mozzarella flag instead of
        // an application in their composition. New clicks cannot set this
        // flag anymore, but existing worlds and old pizza items still carry
        // it through the saved BlockState / PizzaItemData. Show it instead of
        // silently hiding a previously consumed item. The final index is
        // clamped to a valid visual position if a legacy pizza already has
        // six modern toppings; elevation still keeps this old piece visible.
        if (legacyMozzarella) {
            ItemStack oldCheese = new ItemStack(ModItems.MOZZARELLA);
            for (int quarter = 0; quarter < PizzaComposition.QUARTER_COUNT; quarter++) {
                int slot = occupiedSlots[quarter];
                addPiece(pieces, entity, oldCheese, 1_000_000L, quarter,
                        Math.min(slot, SLOTS.length - 1), slot,
                        rightX, rightZ, frontX, frontZ);
            }
        }

        state.pieces = List.copyOf(pieces);
    }

    /**
     * Single visual item placement path for both modern ingredients and old
     * mozzarella, keeping all transformations isolated for the later 2.5D
     * rendering work. Never changes the pizza's contents or BlockState.
     *
     * @param layoutSlot bounded [0..5] slot for horizontal placement
     * @param depthSlot original layering count, including old-world extras
     */
    private void addPiece(
            List<Piece> pieces, PizzaDoughBlockEntity entity,
            ItemStack source, long sequence, int quarter,
            int layoutSlot, int depthSlot,
            int rightX, int rightZ, int frontX, int frontZ
    ) {
        int rightSign = quarter >= 2 ? 1 : -1;
        int frontSign = (quarter & 1) != 0 ? 1 : -1;
        float u = (float) (rightSign * CENTER_OFFSET) + SLOTS[layoutSlot][0];
        float v = (float) (frontSign * CENTER_OFFSET) + SLOTS[layoutSlot][1];
        float x = 0.5F + rightX * u + frontX * v;
        float z = 0.5F + rightZ * u + frontZ * v;

        ItemStackRenderState item = new ItemStackRenderState();
        this.models.updateForTopItem(
                item, source, ItemDisplayContext.FIXED,
                entity.getLevel(), null,
                (int) (sequence ^ (quarter * 37L)));
        if (item.isEmpty()) {
            return;
        }

        float rotation = (float) ((sequence * 41L + quarter * 47L) % 360L);
        pieces.add(new Piece(
                x, SURFACE_Y + depthSlot * 0.001F, z, rotation, item));
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
