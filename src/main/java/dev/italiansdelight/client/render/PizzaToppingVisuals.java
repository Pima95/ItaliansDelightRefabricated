package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import dev.italiansdelight.common.pizza.PizzaToppingCatalog;
import dev.italiansdelight.common.pizza.PizzaToppingType;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Shared visual-only 2.5D treatment for raw and cooked pizza toppings.
 *
 * <p>SAUCE and CREAMY applications create shallow, pixelated spread patches
 * instead of showing the ingredient's bowl/item. SOLID applications use
 * smaller, flattened fragments of the REAL original item model, so optional
 * mod equivalences retain their appearance. The renderer draws only saved
 * remaining-mask quarters and never alters ingredients, refunds or nutrition.
 *
 * <p>All client/world data is sampled during extraction. The returned snapshot
 * contains render states and immutable geometry only. submit() never reads
 * BlockEntities/Level or randomizes positions from the frame clock.
 */
public final class PizzaToppingVisuals {
    private static final float[] SLOT_U = {0.26F, 0.18F, 0.36F, 0.18F, 0.36F, 0.28F};
    private static final float[] SLOT_V = {0.26F, 0.18F, 0.18F, 0.36F, 0.36F, 0.41F};

    private static final int[] SAUCE_COLORS = {
            0xFFA63829, 0xFFB5432C, 0xFF9D3024, 0xFFC04F33
    };
    private static final int[] CREAM_COLORS = {
            0xFFF2DFB9, 0xFFFCEDCE, 0xFFE8CFAE, 0xFFF7E2C6
    };
    private static final int[] RICOTTA_COLORS = {
            0xFFF1EACC, 0xFFFAF2D8, 0xFFE4E0C6, 0xFFF6F0DF
    };

    private PizzaToppingVisuals() {
    }

    /**
     * @param visibleMask bitmask of physical quarters still present. Raw
     *                    pizzas always pass 15; served pizzas use REMAINING.
     * @param legacyMozzarella saved pre-1.0 block/item flag, NOT a new topping
     * @param surfaceY the top of the dough (excluding its raised rim)
     */
    public static Visuals extract(
            BlockEntity entity, PizzaComposition composition,
            boolean legacyMozzarella, int visibleMask,
            ItemModelResolver models, float surfaceY
    ) {
        if (entity.getLevel() == null || visibleMask == 0) {
            return Visuals.EMPTY;
        }

        Direction forward = switch (composition.orientation()) {
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };
        int rightX = -forward.getStepZ(), rightZ = forward.getStepX();
        int frontX = forward.getStepX(), frontZ = forward.getStepZ();

        List<Piece> pieces = new ArrayList<>();
        List<Patch> patches = new ArrayList<>();
        int[] slots = new int[PizzaComposition.QUARTER_COUNT];
        int[] totalSolids = new int[PizzaComposition.QUARTER_COUNT];

        // Count only REAL visible solids for fragment density. Spreads use
        // their own floor layer and must not displace other toppings.
        for (PizzaToppingApplication application : composition.applications()) {
            if (isSpread(application.canonicalId())) {
                continue;
            }
            for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
                if ((visibleMask & (1 << q)) != 0 && application.covers(q)) {
                    totalSolids[q]++;
                }
            }
        }

        for (PizzaToppingApplication application : composition.applications()) {
            ItemStack original = application.originalStack();
            if (original.isEmpty()) {
                continue;
            }
            for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
                if ((visibleMask & (1 << q)) == 0 || !application.covers(q)) {
                    continue;
                }

                if (isSpread(application.canonicalId())) {
                    addSpread(patches, application.canonicalId(),
                            composition.renderSeed(), application.sequence(), q,
                            rightX, rightZ, frontX, frontZ, surfaceY);
                } else {
                    int slot = slots[q]++;
                    if (slot >= SLOT_U.length) {
                        continue;
                    }
                    int count = totalSolids[q] <= 3 ? 2 : 1;
                    addFragments(pieces, entity, models, original,
                            application.sequence(), composition.renderSeed(),
                            q, slot, count, rightX, rightZ, frontX, frontZ,
                            surfaceY, visualCategory(application.canonicalId()));
                }
            }
        }

        // The prototype's whole mozzarella remains refundable/visible in old
        // worlds. It must never become an approved NEW pizza ingredient.
        if (legacyMozzarella) {
            ItemStack oldCheese = new ItemStack(ModItems.MOZZARELLA);
            for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
                if ((visibleMask & (1 << q)) == 0) {
                    continue;
                }
                addFragments(pieces, entity, models, oldCheese,
                        1_000_000L, composition.renderSeed(), q,
                        Math.min(slots[q], SLOT_U.length - 1), 1,
                        rightX, rightZ, frontX, frontZ,
                        surfaceY, PizzaToppingType.NutritionCategory.DAIRY);
            }
        }

        return new Visuals(List.copyOf(patches), List.copyOf(pieces));
    }

    private static boolean isSpread(net.minecraft.resources.Identifier id) {
        return PizzaToppingCatalog.findType(id)
                .map(PizzaToppingType::occupiesSauceSlot)
                .orElse(false);
    }

    private static PizzaToppingType.NutritionCategory visualCategory(
            net.minecraft.resources.Identifier id
    ) {
        return PizzaToppingCatalog.findType(id)
                .map(PizzaToppingType::nutritionCategory)
                .orElse(PizzaToppingType.NutritionCategory.VEGETABLE);
    }

    private static void addFragments(
            List<Piece> output, BlockEntity entity,
            ItemModelResolver models, ItemStack original,
            long sequence, long renderSeed, int quarter, int slot, int count,
            int rightX, int rightZ, int frontX, int frontZ,
            float surfaceY, PizzaToppingType.NutritionCategory category
    ) {
        float size = switch (category) {
            case DAIRY -> 0.130F;
            case HERB -> 0.095F;
            case ANIMAL_PROTEIN -> 0.120F;
            default -> 0.115F;
        };
        float depth = category == PizzaToppingType.NutritionCategory.HERB
                ? 0.012F : 0.028F;

        // Use the actual source item (cooked/raw, mod equivalents, custom
        // item models), not a generic texture for each canonical kind.
        ItemStackRenderState item = new ItemStackRenderState();
        models.updateForTopItem(
                item, original, ItemDisplayContext.FIXED,
                entity.getLevel(), null, (int) (sequence ^ (quarter * 37L)));
        if (item.isEmpty()) {
            return;
        }

        int rSign = quarter >= 2 ? 1 : -1;
        int fSign = (quarter & 1) != 0 ? 1 : -1;
        long hash = mix(renderSeed ^ (sequence * 0x9E3779B97F4A7C15L)
                ^ ((long) quarter * 0xBF58476D1CE4E5B9L));
        // Each fragment stays within its own physical quarter, avoiding the
        // center seams and the raised edge (approx. 1px inside the pizza).
        for (int part = 0; part < count; part++) {
            long fragmentHash = mix(hash + part * 0x94D049BB133111EBL);
            float jitterU = signedUnit(fragmentHash) * 0.022F;
            float jitterV = signedUnit(fragmentHash >>> 16) * 0.022F;
            float spreadU = part == 0 ? -0.025F : 0.030F;
            float spreadV = part == 0 ? 0.025F : -0.030F;
            float u = rSign * clamp(SLOT_U[slot] + spreadU + jitterU, 0.15F, 0.425F);
            float v = fSign * clamp(SLOT_V[slot] + spreadV + jitterV, 0.15F, 0.425F);
            float x = 0.5F + rightX * u + frontX * v;
            float z = 0.5F + rightZ * u + frontZ * v;
            // Place solids visibly higher than the painted base, with only
            // one original icon's thickness (not a standing "item frame").
            float y = surfaceY + 0.014F + slot * 0.0016F + part * 0.001F;
            float rotation = (float) ((fragmentHash >>> 28) & 0xFFL) * (360F / 256F);
            float scale = size * (part == 0 ? 1F : 0.82F);
            output.add(new Piece(x, y, z, rotation, scale, depth, item));
        }
    }

    private static void addSpread(
            List<Patch> patches, net.minecraft.resources.Identifier id,
            long renderSeed, long sequence, int quarter,
            int rightX, int rightZ, int frontX, int frontZ, float surfaceY
    ) {
        int[] colors = id.getPath().endsWith("/ricotta") ? RICOTTA_COLORS
                : id.getPath().endsWith("/cream_bowl") ? CREAM_COLORS
                : SAUCE_COLORS;
        int rSign = quarter >= 2 ? 1 : -1;
        int fSign = (quarter & 1) != 0 ? 1 : -1;
        long seed = mix(renderSeed ^ sequence * 0x9E3779B97F4A7C15L
                ^ ((long) quarter * 0xBF58476D1CE4E5B9L));

        // Five shallow stepped patches suggest sauce spread across the pizza.
        // Coordinates are intentionally not allowed to touch the outer rim
        // or the other physical quarters. Preserve explicit pixelated edges.
        float[][] geometry = {
                {0.29F, 0.29F, 0.135F, 0.135F},
                {0.18F, 0.24F, 0.068F, 0.085F},
                {0.40F, 0.32F, 0.062F, 0.089F},
                {0.27F, 0.41F, 0.095F, 0.057F},
                {0.32F, 0.16F, 0.085F, 0.058F}
        };
        for (int i = 0; i < geometry.length; i++) {
            float[] rect = geometry[i];
            long salt = mix(seed + i * 0x94D049BB133111EBL);
            float du = signedUnit(salt) * 0.009F;
            float dv = signedUnit(salt >>> 14) * 0.009F;
            float u = rSign * clamp(rect[0] + du, 0.12F, 0.445F);
            float v = fSign * clamp(rect[1] + dv, 0.12F, 0.445F);
            float x = 0.5F + rightX * u + frontX * v;
            float z = 0.5F + rightZ * u + frontZ * v;
            float hw = rect[2], hd = rect[3];
            // Cardinal rotations swap width/depth, but never affect quarter
            // coverage; all orientations are exact 90 degree increments.
            float spanX = rightX != 0 ? hw : hd;
            float spanZ = rightZ != 0 ? hw : hd;
            patches.add(new Patch(x - spanX, surfaceY + 0.003F,
                    z - spanZ, x + spanX, surfaceY + 0.004F,
                    z + spanZ, colors[(int) (salt & 3L)]));
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float signedUnit(long hash) {
        return (((hash & 0xFFFFL) / 65535F) * 2F) - 1F;
    }

    private static long mix(long number) {
        number = (number ^ (number >>> 30)) * 0xBF58476D1CE4E5B9L;
        number = (number ^ (number >>> 27)) * 0x94D049BB133111EBL;
        return number ^ (number >>> 31);
    }

    public static void submit(
            Visuals visuals, int light,
            PoseStack poseStack, SubmitNodeCollector queue
    ) {
        if (!visuals.patches().isEmpty()) {
            // One geometry batch per pizza; no per-pixel draw calls.
            queue.submitCustomGeometry(
                    poseStack, RenderTypes.debugFilledBox(),
                    (pose, vertex) -> {
                        for (Patch patch : visuals.patches()) {
                            filledBox(pose, vertex, patch);
                        }
                    });
        }

        for (Piece piece : visuals.pieces()) {
            poseStack.pushPose();
            poseStack.translate(piece.x(), piece.y(), piece.z());
            // Vanilla item art is XY; lay it down on the pizza's XZ plane.
            poseStack.mulPose(Axis.XP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(piece.rotation()));
            // A small thickness instead of a free-standing inventory icon.
            poseStack.scale(piece.scale(), piece.scale(), piece.depth());
            piece.renderState().submit(
                    poseStack, queue, light, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    private static void filledBox(
            PoseStack.Pose pose, VertexConsumer out, Patch b
    ) {
        float x0 = b.x0(), x1 = b.x1();
        float y0 = b.y0(), y1 = b.y1();
        float z0 = b.z0(), z1 = b.z1();
        // Same POSITION_COLOR six-face box path used by PizzaDivisionOverlay.
        vertex(pose, out, x0,y0,z0,b.color()); vertex(pose, out, x1,y0,z0,b.color());
        vertex(pose, out, x1,y1,z0,b.color()); vertex(pose, out, x0,y1,z0,b.color());
        vertex(pose, out, x1,y0,z1,b.color()); vertex(pose, out, x0,y0,z1,b.color());
        vertex(pose, out, x0,y1,z1,b.color()); vertex(pose, out, x1,y1,z1,b.color());
        vertex(pose, out, x0,y0,z1,b.color()); vertex(pose, out, x0,y0,z0,b.color());
        vertex(pose, out, x0,y1,z0,b.color()); vertex(pose, out, x0,y1,z1,b.color());
        vertex(pose, out, x1,y0,z0,b.color()); vertex(pose, out, x1,y0,z1,b.color());
        vertex(pose, out, x1,y1,z1,b.color()); vertex(pose, out, x1,y1,z0,b.color());
        vertex(pose, out, x0,y1,z0,b.color()); vertex(pose, out, x1,y1,z0,b.color());
        vertex(pose, out, x1,y1,z1,b.color()); vertex(pose, out, x0,y1,z1,b.color());
        vertex(pose, out, x0,y0,z1,b.color()); vertex(pose, out, x1,y0,z1,b.color());
        vertex(pose, out, x1,y0,z0,b.color()); vertex(pose, out, x0,y0,z0,b.color());
    }

    private static void vertex(
            PoseStack.Pose pose, VertexConsumer out,
            float x, float y, float z, int color
    ) {
        out.addVertex(pose, x, y, z).setColor(color);
    }

    public record Visuals(List<Patch> patches, List<Piece> pieces) {
        public static final Visuals EMPTY = new Visuals(List.of(), List.of());
    }
    public record Patch(
            float x0, float y0, float z0,
            float x1, float y1, float z1, int color
    ) { }
    public record Piece(
            float x, float y, float z, float rotation,
            float scale, float depth, ItemStackRenderState renderState
    ) { }
}
