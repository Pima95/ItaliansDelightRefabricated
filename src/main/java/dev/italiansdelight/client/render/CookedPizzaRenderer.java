package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import dev.italiansdelight.common.registry.ModItems;
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
 * Cooked pizza topping renderer. The current item models are only the same
 * PLACEHOLDER art already used on raw dough, not the final 2.5D topping art.
 *
 * <p>Draw only toppings of the STILL PRESENT physical quarters. The block's
 * REMAINING bitmask is synchronized by vanilla, while ingredient identity
 * and source item models come from the baked pizza's persisted BlockEntity.
 * No topping/empty bowl is actually returned from a served cooked slice.
 */
public final class CookedPizzaRenderer
        implements BlockEntityRenderer<CookedPizzaBlockEntity, CookedPizzaRenderer.State> {
    private static final float SURFACE_Y = 1.0F / 16.0F + 0.021F;
    private static final float ITEM_SCALE = 0.17F;
    private static final double CENTER_OFFSET = 0.21;
    private static final float[][] SLOTS = {
            {0.00F, 0.00F}, {-0.070F, -0.060F},
            {0.070F, -0.050F}, {-0.065F, 0.065F},
            {0.065F, 0.070F}, {0.00F, 0.095F}
    };
    private final ItemModelResolver models;

    public CookedPizzaRenderer(BlockEntityRendererProvider.Context context) {
        this.models = context.itemModelResolver();
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
        PizzaComposition pizza = entity.pizza().composition();
        int remaining = entity.getBlockState().getValue(CookedPizzaBlock.REMAINING);
        if (entity.getLevel() == null || remaining == 0) {
            state.pieces = List.of();
            return;
        }

        Direction forward = switch (pizza.orientation()) {
            case "north" -> Direction.NORTH;
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };
        int rightX = -forward.getStepZ(), rightZ = forward.getStepX();
        int frontX = forward.getStepX(), frontZ = forward.getStepZ();

        List<Piece> parts = new ArrayList<>();
        int[] occupied = new int[4];

        // Oldest at bottom, most recently placed closer to the top.
        for (PizzaToppingApplication application : pizza.applications()) {
            ItemStack source = application.originalStack();
            if (source.isEmpty()) {
                continue;
            }
            for (int q = 0; q < 4; q++) {
                if ((remaining & (1 << q)) == 0 || !application.covers(q)) {
                    continue;
                }
                addPiece(parts, entity, source, application.sequence(), q,
                        occupied[q]++, rightX, rightZ, frontX, frontZ);
            }
        }

        // Previously saved pizzas may still contain the legacy whole-
        // mozzarella flag. It was not included in the canonical applications.
        // Do not silently hide it when showing a cooked old-world pizza.
        if (entity.pizza().legacyMozzarella()) {
            for (int q = 0; q < 4; q++) {
                if ((remaining & (1 << q)) != 0) {
                    addPiece(parts, entity, new ItemStack(ModItems.MOZZARELLA),
                            1_000_000L, q, occupied[q]++,
                            rightX, rightZ, frontX, frontZ);
                }
            }
        }
        state.pieces = List.copyOf(parts);
    }

    private void addPiece(
            List<Piece> parts, CookedPizzaBlockEntity entity, ItemStack source,
            long sequence, int quarter, int slot,
            int rightX, int rightZ, int frontX, int frontZ
    ) {
        if (slot >= SLOTS.length) {
            return;
        }
        int rightSign = quarter >= 2 ? 1 : -1;
        int frontSign = (quarter & 1) != 0 ? 1 : -1;
        float u = (float) (rightSign * CENTER_OFFSET) + SLOTS[slot][0];
        float v = (float) (frontSign * CENTER_OFFSET) + SLOTS[slot][1];
        ItemStackRenderState item = new ItemStackRenderState();
        models.updateForTopItem(
                item, source, ItemDisplayContext.FIXED,
                entity.getLevel(), null, (int) (sequence ^ (quarter * 37L)));
        if (item.isEmpty()) {
            return;
        }
        parts.add(new Piece(
                0.5F + rightX * u + frontX * v,
                SURFACE_Y + slot * 0.001F,
                0.5F + rightZ * u + frontZ * v,
                (float) ((sequence * 41L + quarter * 47L) % 360L),
                item));
    }

    @Override
    public void submit(
            State state, PoseStack poseStack,
            SubmitNodeCollector queue, CameraRenderState camera
    ) {
        for (Piece piece : state.pieces) {
            poseStack.pushPose();
            poseStack.translate(piece.x(), piece.y(), piece.z());
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(piece.rotation()));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            piece.item().submit(
                    poseStack, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static final class State extends BlockEntityRenderState {
        private List<Piece> pieces = List.of();
    }

    private record Piece(float x, float y, float z, float rotation,
                         ItemStackRenderState item) {
    }
}
