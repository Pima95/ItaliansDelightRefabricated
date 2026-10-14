package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.pizza.PizzaToppingCatalog;
import dev.italiansdelight.common.pizza.PizzaToppingType;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Temporary visual cut guides for fully stretched pizza dough (Minecraft 26.2).
 *
 * <p>Client-only, zero block-model/texture/hitbox changes. The server saves
 * the true division and facing in PizzaComposition; the matching BlockState
 * properties are synchronized automatically to clients. Geometry is prepared
 * in Fabric's extraction phase and submitted in its drawing phase, without
 * retaining references to any mutable ClientLevel / BlockEntity / Player.
 */
public final class PizzaDivisionOverlay {
    private static final RenderStateDataKey<List<Guide>> GUIDES =
            RenderStateDataKey.create(() -> "italiansdelight pizza division guides");

    // Interior of stage-4 pizza: raised crust occupies the outer 1px rim.
    private static final double MIN = 2.25 / 16.0;
    private static final double MAX = 13.75 / 16.0;
    private static final double MID = 0.5;
    private static final double LINE_HALF_WIDTH = 1.0 / 150.0;
    private static final double Y_MIN = 1.0 / 16.0 + 0.004;
    private static final double Y_MAX = Y_MIN + 0.002;
    private static final int COLOR = 0xDD000000;

    // Restrict the lookup to nearby loaded blocks while the player holds
    // a relevant item. One reusable BlockPos eliminates per-cell allocations.
    private static final int HORIZONTAL_RADIUS = 8;
    private static final int VERTICAL_RADIUS = 4;

    private PizzaDivisionOverlay() {
    }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(PizzaDivisionOverlay::extract);
        LevelRenderEvents.COLLECT_SUBMITS.register(PizzaDivisionOverlay::submit);
    }

    private static boolean isRelevantHeldItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(ModItems.ROLLING_PIN)) {
            return true;
        }
        // Only the approved toppings currently enabled by the server-side
        // interaction handler: bowls/creamy/sauces are not interactive yet.
        return PizzaToppingCatalog.findNativeItem(
                        BuiltInRegistries.ITEM.getKey(stack.getItem()))
                .filter(type -> type.role() == PizzaToppingType.Role.SOLID)
                .isPresent();
    }

    private static void extract(LevelExtractionContext context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !isRelevantHeldItem(player.getMainHandItem())) {
            context.levelState().setData(GUIDES, List.of());
            return;
        }

        Vec3 camera = context.levelState().cameraRenderState.pos;
        int centerX = (int) Math.floor(camera.x);
        int centerY = (int) Math.floor(camera.y);
        int centerZ = (int) Math.floor(camera.z);

        List<Guide> result = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = centerY - VERTICAL_RADIUS; y <= centerY + VERTICAL_RADIUS; y++) {
            for (int x = centerX - HORIZONTAL_RADIUS; x <= centerX + HORIZONTAL_RADIUS; x++) {
                for (int z = centerZ - HORIZONTAL_RADIUS; z <= centerZ + HORIZONTAL_RADIUS; z++) {
                    cursor.set(x, y, z);
                    BlockState state = context.level().getBlockState(cursor);
                    if (!state.is(ModBlocks.PIZZA_DOUGH)
                            || state.getValue(PizzaDoughBlock.STAGE) != PizzaDoughBlock.MAX_STAGE) {
                        continue;
                    }
                    int division = state.getValue(PizzaDoughBlock.DIVISION);
                    if (division == 0) {
                        continue;
                    }
                    // Immutable render snapshot; never pass Level or BE into
                    // the rendering phase (it may execute on another thread).
                    result.add(new Guide(new BlockPos(x, y, z), division,
                            state.getValue(PizzaDoughBlock.DIVISION_FACING)));
                }
            }
        }
        context.levelState().setData(GUIDES, List.copyOf(result));
    }

    private static void submit(LevelRenderContext context) {
        List<Guide> guides = context.levelState().getData(GUIDES);
        if (guides == null || guides.isEmpty()) {
            return;
        }
        Vec3 camera = context.levelState().cameraRenderState.pos;
        PoseStack poseStack = context.poseStack();

        for (Guide guide : guides) {
            poseStack.pushPose();
            poseStack.translate(
                    guide.pos().getX() - camera.x,
                    guide.pos().getY() - camera.y,
                    guide.pos().getZ() - camera.z);

            context.submitNodeCollector().submitCustomGeometry(
                    poseStack, RenderTypes.debugFilledBox(),
                    (pose, consumer) -> submitGuide(pose, consumer, guide));
            poseStack.popPose();
        }
    }

    private static void submitGuide(
            PoseStack.Pose pose, VertexConsumer consumer, Guide guide) {
        Direction facing = guide.facing();
        boolean northSouth = facing == Direction.NORTH || facing == Direction.SOUTH;

        // First cut: full line parallel to the first player's look direction.
        if (northSouth) {
            filledBox(pose, consumer, MID - LINE_HALF_WIDTH, Y_MIN, MIN,
                    MID + LINE_HALF_WIDTH, Y_MAX, MAX);
        } else {
            filledBox(pose, consumer, MIN, Y_MIN, MID - LINE_HALF_WIDTH,
                    MAX, Y_MAX, MID + LINE_HALF_WIDTH);
        }

        // Second/third cuts: only on the physical halves that are currently
        // divided. Left = bit 2, right = bit 4. Which block-world side is
        // "left" reverses when the pizza originally faced south or west.
        if ((guide.division() & 2) != 0) {
            submitHalfCut(pose, consumer, facing, false);
        }
        if ((guide.division() & 4) != 0) {
            submitHalfCut(pose, consumer, facing, true);
        }
    }

    private static void submitHalfCut(
            PoseStack.Pose pose, VertexConsumer consumer,
            Direction facing, boolean rightHalf
    ) {
        // Right vector of the saved facing is (-facing.z, facing.x).
        int rightX = -facing.getStepZ();
        int rightZ = facing.getStepX();
        int sign = rightHalf ? 1 : -1;

        if (rightX != 0) {
            double endX = sign * rightX > 0 ? MAX : MIN;
            filledBox(pose, consumer,
                    Math.min(MID, endX), Y_MIN, MID - LINE_HALF_WIDTH,
                    Math.max(MID, endX), Y_MAX, MID + LINE_HALF_WIDTH);
        } else if (rightZ != 0) {
            double endZ = sign * rightZ > 0 ? MAX : MIN;
            filledBox(pose, consumer,
                    MID - LINE_HALF_WIDTH, Y_MIN, Math.min(MID, endZ),
                    MID + LINE_HALF_WIDTH, Y_MAX, Math.max(MID, endZ));
        }
    }

    /**
     * Same POSITION_COLOR 6-face box structure used in the Fabric 26.2
     * LevelRenderEventsTests example. Thin raised quads avoid z-fighting.
     */
    private static void filledBox(
            PoseStack.Pose pose, VertexConsumer out,
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ) {
        float x0 = (float) minX, x1 = (float) maxX;
        float y0 = (float) minY, y1 = (float) maxY;
        float z0 = (float) minZ, z1 = (float) maxZ;
        // North
        point(pose, out, x0, y0, z0);
        point(pose, out, x1, y0, z0);
        point(pose, out, x1, y1, z0);
        point(pose, out, x0, y1, z0);
        // South
        point(pose, out, x1, y0, z1);
        point(pose, out, x0, y0, z1);
        point(pose, out, x0, y1, z1);
        point(pose, out, x1, y1, z1);
        // West
        point(pose, out, x0, y0, z1);
        point(pose, out, x0, y0, z0);
        point(pose, out, x0, y1, z0);
        point(pose, out, x0, y1, z1);
        // East
        point(pose, out, x1, y0, z0);
        point(pose, out, x1, y0, z1);
        point(pose, out, x1, y1, z1);
        point(pose, out, x1, y1, z0);
        // Top
        point(pose, out, x0, y1, z0);
        point(pose, out, x1, y1, z0);
        point(pose, out, x1, y1, z1);
        point(pose, out, x0, y1, z1);
        // Bottom
        point(pose, out, x0, y0, z1);
        point(pose, out, x1, y0, z1);
        point(pose, out, x1, y0, z0);
        point(pose, out, x0, y0, z0);
    }

    private static void point(
            PoseStack.Pose pose, VertexConsumer out, float x, float y, float z) {
        out.addVertex(pose, x, y, z).setColor(COLOR);
    }

    private record Guide(BlockPos pos, int division, Direction facing) {
    }
}
