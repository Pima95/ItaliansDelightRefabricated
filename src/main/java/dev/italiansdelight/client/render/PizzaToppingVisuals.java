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
 * Renders sauces as seamless 12x12 surfaces, and solid toppings as scattered
 * small sprites cut from the four independent 6x6 cells of each new atlas.
 *
 * <p>Old pizza_toppings/*.png assets are preserved verbatim. Sauce uses them;
 * all solid visual assets live under pizza_topping_sprites/*.png instead.
 * No inventory item models are submitted, and no extra block height is added.
 *
 * <p>The distribution is deterministic based on the original pizza renderSeed,
 * quarter and application identity. A packed 2x2 or 3x3 pixel seating grid
 * avoids sprite overlap even for six distinct solids per physical quarter.
 * Gameplay, saved data, quartile masks and refund order remain untouched.
 */
public final class PizzaToppingVisuals {
    private static final int ALL_QUARTERS = 0b1111;
    private static final float PIXEL = 1F / 16F;
    private static final float INTERIOR_HALF = 6F / 16F;
    private static final float SURFACE_Y = 1F / 16F;
    private static final float BASE_BIAS = 0.00016F;
    private static final float LAYER_BIAS = 0.000012F;
    private static final String SAUCE_PREFIX = "textures/block/pizza_toppings/";
    private static final String SPRITE_PREFIX = "textures/block/pizza_topping_sprites/";

    private PizzaToppingVisuals() {
    }

    public record Visuals(List<Layer> layers, List<Fragment> fragments,
                          int rightX, int rightZ, int frontX, int frontZ) {
        public static final Visuals EMPTY = new Visuals(
                List.of(), List.of(), 1, 0, 0, -1);
    }

    private record Layer(Identifier texture, int mask, int depth) {
    }

    /** UV coordinates locate a fragment inside one 6x6 atlas cell. */
    private record Fragment(Identifier texture, int quarter, int x, int z,
                            int size, int variant, int depth) {
    }

    private record Solid(Identifier texture, int mask, long sequence) {
    }

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

        List<Layer> sauces = new ArrayList<>();
        List<Solid> solids = new ArrayList<>();
        for (PizzaToppingApplication application : composition.applications()) {
            int covered = application.remainingMask() & mask;
            if (covered == 0) {
                continue;
            }
            PizzaToppingType type = PizzaToppingCatalog
                    .findType(application.canonicalId()).orElse(null);
            String name = type != null && !type.planned()
                    ? type.id().getPath().substring(
                            type.id().getPath().lastIndexOf('/') + 1)
                    : "unknown";
            if (type != null && type.occupiesSauceSlot()) {
                // The approved continuous 12x12 sauce artwork is unchanged.
                sauces.add(new Layer(texture(SAUCE_PREFIX, name),
                        covered, sauces.size()));
            } else {
                solids.add(new Solid(texture(SPRITE_PREFIX, name),
                        covered, application.sequence()));
            }
        }
        if (legacyMozzarella) {
            solids.add(new Solid(
                    texture(SPRITE_PREFIX, "legacy_mozzarella"),
                    mask, 1_000_000L));
        }

        List<Fragment> fragments = new ArrayList<>();
        for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
            if ((mask & (1 << q)) == 0) {
                continue;
            }
            List<Solid> entries = new ArrayList<>();
            for (Solid solid : solids) {
                if ((solid.mask() & (1 << q)) != 0) {
                    entries.add(solid);
                }
            }
            int count = entries.size();
            if (count == 0) {
                continue;
            }

            // Space is limited to 6x6 Minecraft pixels per physical quarter.
            // One topping: 2 independent 3x3 sprites.
            // Two toppings: 2 independent 2x2 sprites each.
            // Three toppings: 1 sprite each at 3x3.
            // Four or more: 1 smaller 2x2 sprite per topping (up to 7 with
            // the historical whole-mozzarella flag). Guaranteed disjoint.
            int size = (count == 1 || count == 3) ? 3 : 2;
            int repeats = count <= 2 ? 2 : 1;
            int seatsPerAxis = 6 / size;
            int[] seatOrder = shuffledSeats(seatsPerAxis * seatsPerAxis,
                    composition.renderSeed(), q);

            int cursor = 0;
            for (Solid solid : entries) {
                for (int copy = 0; copy < repeats; copy++) {
                    int seat = seatOrder[cursor];
                    int x = (seat % seatsPerAxis) * size;
                    int z = (seat / seatsPerAxis) * size;
                    long choice = mix(composition.renderSeed()
                            ^ (solid.sequence() * 0x9E3779B97F4A7C15L)
                            ^ ((long) q * 0xBF58476D1CE4E5B9L)
                            ^ ((long) copy * 0x94D049BB133111EBL));
                    int variant = (int) (choice & 3L);
                    fragments.add(new Fragment(
                            solid.texture(), q, x, z, size, variant,
                            sauces.size() + cursor));
                    cursor++;
                }
            }
        }
        return new Visuals(List.copyOf(sauces), List.copyOf(fragments),
                rightX, rightZ, frontX, frontZ);
    }

    private static int[] shuffledSeats(int size, long seed, int quarter) {
        int[] seats = new int[size];
        for (int i = 0; i < size; i++) {
            seats[i] = i;
        }
        long random = mix(seed ^ (quarter * 0x9E3779B97F4A7C15L));
        for (int i = size - 1; i > 0; i--) {
            random = mix(random + i);
            int j = (int) Long.remainderUnsigned(random, i + 1L);
            int swap = seats[i];
            seats[i] = seats[j];
            seats[j] = swap;
        }
        return seats;
    }

    private static long mix(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    private static Identifier texture(String prefix, String name) {
        return Identifier.fromNamespaceAndPath(
                "italiansdelight", prefix + name + ".png");
    }

    public static void submit(
            Visuals visuals, int packedLight,
            PoseStack poseStack, SubmitNodeCollector queue
    ) {
        for (Layer sauce : visuals.layers()) {
            queue.submitCustomGeometry(
                    poseStack, RenderTypes.entityCutout(sauce.texture()),
                    (pose, out) -> {
                        for (int q = 0; q < PizzaComposition.QUARTER_COUNT; q++) {
                            if ((sauce.mask() & (1 << q)) != 0) {
                                drawSauce(pose, out, visuals, q,
                                        sauce.depth(), packedLight);
                            }
                        }
                    });
        }

        for (Fragment fragment : visuals.fragments()) {
            queue.submitCustomGeometry(
                    poseStack, RenderTypes.entityCutout(fragment.texture()),
                    (pose, out) -> drawSprite(
                            pose, out, visuals, fragment, packedLight));
        }
    }

    private static void drawSauce(
            PoseStack.Pose pose, VertexConsumer out, Visuals visual,
            int quarter, int depth, int light
    ) {
        boolean right = quarter >= 2;
        boolean front = (quarter & 1) != 0;
        float u0 = right ? 0F : -INTERIOR_HALF;
        float u1 = right ? INTERIOR_HALF : 0F;
        float v0 = front ? 0F : -INTERIOR_HALF;
        float v1 = front ? INTERIOR_HALF : 0F;
        float tu0 = right ? 0.5F : 0F;
        float tu1 = right ? 1F : 0.5F;
        float tv0 = front ? 0.5F : 0F;
        float tv1 = front ? 1F : 0.5F;
        quad(pose, out, visual, u0, u1, v0, v1,
                tu0, tu1, tv0, tv1,
                SURFACE_Y + BASE_BIAS + depth * LAYER_BIAS, light);
    }

    private static void drawSprite(
            PoseStack.Pose pose, VertexConsumer out, Visuals visual,
            Fragment frag, int light
    ) {
        // Coordinates are exact integer model pixels. A fragment's own atlas
        // cell is 6x6, but only its central 2x2/3x3 drawn pixels are used.
        // Other original quarters remain safely preserved in old assets.
        float u0 = (frag.quarter() >= 2 ? 0 : -6) * PIXEL + frag.x() * PIXEL;
        float v0 = ((frag.quarter() & 1) != 0 ? 0 : -6) * PIXEL
                + frag.z() * PIXEL;
        float u1 = u0 + frag.size() * PIXEL;
        float v1 = v0 + frag.size() * PIXEL;

        int atlasX = (frag.variant() & 1) * 6 + 1;
        int atlasY = (frag.variant() >>> 1) * 6 + 1;
        float tu0 = atlasX / 12F;
        float tv0 = atlasY / 12F;
        float tu1 = (atlasX + frag.size()) / 12F;
        float tv1 = (atlasY + frag.size()) / 12F;
        quad(pose, out, visual, u0, u1, v0, v1,
                tu0, tu1, tv0, tv1,
                SURFACE_Y + BASE_BIAS + (frag.depth() + 1) * LAYER_BIAS,
                light);
    }

    private static void quad(
            PoseStack.Pose pose, VertexConsumer out, Visuals visual,
            float u0, float u1, float v0, float v1,
            float tu0, float tu1, float tv0, float tv1,
            float y, int light
    ) {
        vertex(pose, out, visual, u0, v0, y, tu0, tv0, light);
        vertex(pose, out, visual, u1, v0, y, tu1, tv0, light);
        vertex(pose, out, visual, u1, v1, y, tu1, tv1, light);
        vertex(pose, out, visual, u0, v1, y, tu0, tv1, light);
    }

    private static void vertex(
            PoseStack.Pose pose, VertexConsumer out, Visuals visual,
            float u, float v, float y, float texU, float texV, int light
    ) {
        float x = 0.5F + visual.rightX() * u + visual.frontX() * v;
        float z = 0.5F + visual.rightZ() * u + visual.frontZ() * v;
        out.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(texU, texV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0F, 1F, 0F);
    }
}
