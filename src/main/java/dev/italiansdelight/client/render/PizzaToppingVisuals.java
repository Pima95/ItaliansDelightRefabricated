package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import dev.italiansdelight.common.pizza.PizzaToppingCatalog;
import dev.italiansdelight.common.pizza.PizzaToppingType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/**
 * Pixel-perfect 12x12 pizza surface renderer shared by raw and cooked blocks.
 *
 * Each approved canonical topping owns an independent 12x12 RGBA PNG; no
 * inventory item sprites, blurred scaling, color patches or 3D fragments.
 * For each saved application, the matching full texture is drawn only over
 * its 6x6 UV quarters. Consequently whole-pizza sauces are seamless, but a
 * different sauce on another quarter replaces its portion exactly at the
 * dividing line. Other toppings use the same physical-quarter clipping.
 *
 * Plane is exactly inside the pizza's 14x14 footprint: model pixels 2..14
 * on X/Z, at the 1px-high dough surface. The tiny sub-pixel depth bias is
 * necessary for depth precision; it is far below a Minecraft texture pixel.
 * This class only reads composition snapshots; it never changes gameplay.
 */
public final class PizzaToppingVisuals {
    private static final float INTERIOR_HALF = 6.0F / 16.0F;
    private static final float SURFACE_Y = 1.0F / 16.0F;
    private static final float BASE_BIAS = 0.00016F;
    private static final float LAYER_BIAS = 0.00009F;
    private static final int ALL_QUARTERS = 0b1111;
    private static final String RESOURCE_PREFIX = "textures/block/pizza_toppings/";

    private PizzaToppingVisuals() {
    }

    /** Immutable snapshot of the actual ingredient UV layers. */
    public record Visuals(List<Layer> layers,
                          int rightX, int rightZ, int frontX, int frontZ) {
        public static final Visuals EMPTY = new Visuals(List.of(), 1, 0, 0, -1);
    }

    private record Layer(Identifier texture, int quarters, int depth) {
    }

    /**
     * Extract exactly the source composition and physical-quarter masks.
     * Unknown canonical IDs use a dedicated checked fallback texture, rather
     * than crashing or drawing an arbitrary item icon.
     */
    public static Visuals extract(
            PizzaComposition composition, boolean legacyMozzarella,
            int visibleQuarters
    ) {
        int mask = visibleQuarters & ALL_QUARTERS;
        if (mask == 0 || (composition.applications().isEmpty() && !legacyMozzarella)) {
            return Visuals.EMPTY;
        }

        Direction facing = switch (composition.orientation()) {
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };
        int rightX = -facing.getStepZ();
        int rightZ = facing.getStepX();
        int frontX = facing.getStepX();
        int frontZ = facing.getStepZ();

        List<Layer> layers = new ArrayList<>();
        // Always paint sauce/cream first. The player may legitimately add a
        // solid topping before applying a sauce; chronological draw order
        // would then hide that solid under an opaque sauce texture.
        // Retain original order WITHIN each group for old-world compatibility.
        for (boolean spreadPass : new boolean[] {true, false}) {
            for (PizzaToppingApplication application : composition.applications()) {
                int covered = application.remainingMask() & mask;
                if (covered == 0) {
                    continue;
                }
                PizzaToppingType type = PizzaToppingCatalog
                        .findType(application.canonicalId()).orElse(null);
                boolean spread = type != null && type.occupiesSauceSlot();
                if (spread != spreadPass) {
                    continue;
                }
                String name = type != null && !type.planned()
                        ? type.id().getPath().substring(
                                type.id().getPath().lastIndexOf('/') + 1)
                        : "unknown";
                layers.add(new Layer(texture(name), covered, layers.size()));
            }
        }

        // A previously saved whole-mozzarella flag is still visible and
        // refundable. New pizzas cannot acquire that old flag.
        if (legacyMozzarella) {
            layers.add(new Layer(texture("legacy_mozzarella"), mask, layers.size()));
        }
        return new Visuals(List.copyOf(layers), rightX, rightZ, frontX, frontZ);
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(
                "italiansdelight", RESOURCE_PREFIX + name + ".png");
    }

    /**
     * One cutout batch per canonical application, with up to four UV-clipped
     * quads. Adjacent quarters use matching UV ranges of the SAME 12x12 image
     * (6x6 pixels each) and meet at the center with no gap or frame.
     */
    public static void submit(
            Visuals visuals, int packedLight,
            PoseStack poseStack, SubmitNodeCollector queue
    ) {
        for (Layer layer : visuals.layers()) {
            queue.submitCustomGeometry(
                    poseStack, RenderTypes.entityCutout(layer.texture()),
                    (pose, out) -> {
                        for (int quarter = 0; quarter < 4; quarter++) {
                            if ((layer.quarters() & (1 << quarter)) != 0) {
                                quad(pose, out, visuals, layer.depth(),
                                        quarter, packedLight);
                            }
                        }
                    });
        }
    }

    private static void quad(
            PoseStack.Pose pose, VertexConsumer consumer, Visuals visual,
            int depth, int quarter, int packedLight
    ) {
        // Quarter indices are the same stable left/back, left/front,
        // right/back, right/front bits used in PizzaComposition.
        boolean right = quarter >= 2;
        boolean front = (quarter & 1) != 0;
        float u0 = right ? 0 : -INTERIOR_HALF;
        float u1 = right ? INTERIOR_HALF : 0;
        float v0 = front ? 0 : -INTERIOR_HALF;
        float v1 = front ? INTERIOR_HALF : 0;

        float tu0 = right ? 0.5F : 0F;
        float tu1 = right ? 1F : 0.5F;
        float tv0 = front ? 0.5F : 0F;
        float tv1 = front ? 1F : 0.5F;

        // The pizza body top is at y=1/16. 0.00016 blocks = 0.00256
        // model pixels: eliminates z-fighting without any visible lift.
        float y = SURFACE_Y + BASE_BIAS + depth * LAYER_BIAS;

        // Orientation follows the first cut saved on the pizza, not the
        // current player's view, and preserves full-texture continuity.
        vertex(pose, consumer, visual, u0, v0, y, tu0, tv0, packedLight);
        vertex(pose, consumer, visual, u1, v0, y, tu1, tv0, packedLight);
        vertex(pose, consumer, visual, u1, v1, y, tu1, tv1, packedLight);
        vertex(pose, consumer, visual, u0, v1, y, tu0, tv1, packedLight);
    }

    private static void vertex(
            PoseStack.Pose pose, VertexConsumer out, Visuals visual,
            float u, float v, float y, float texU, float texV, int packedLight
    ) {
        float x = 0.5F + visual.rightX() * u + visual.frontX() * v;
        float z = 0.5F + visual.rightZ() * u + visual.frontZ() * v;
        out.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(texU, texV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0F, 1F, 0F);
    }
}
