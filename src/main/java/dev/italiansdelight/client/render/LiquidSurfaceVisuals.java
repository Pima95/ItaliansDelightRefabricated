package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Shared horizontal liquid surfaces for tank-like blocks. No overlay cuboids
 * inside block JSON: all fluid heights and kinds are rendered from block data.
 * The supported map is intentionally central for future Rustic liquids.
 */
public final class LiquidSurfaceVisuals {
    private LiquidSurfaceVisuals() {}

    private record Surface(Identifier texture, float lastFrameV, int argb) {}

    private static final Surface DEFAULT = new Surface(
        Identifier.fromNamespaceAndPath("minecraft", "textures/block/water_still.png"),
        1F / 32F, 0xDC568AEF
    );
    private static final Map<String, Surface> SURFACES = new ConcurrentHashMap<>(Map.of(
        "minecraft:water", DEFAULT,
        "italiansdelight:olive_oil",
            new Surface(ModRegistries.id("textures/block/fluids/olive_oil_still.png"),
                1F / 32F, 0xFFFFFFFF),
        "minecraft:milk", staticSurface("milk", 0xFFFFFFFF),
        "italiansdelight:sheep_milk", staticSurface("sheep_milk", 0xFFFFFFFF),
        "italiansdelight:whey", staticSurface("whey", 0xFFFFFFFF)
    ));

    private static Surface staticSurface(String key, int color) {
        return new Surface(
            ModRegistries.id("textures/block/liquid_surface/" + key + ".png"),
            1F, color
        );
    }

    /**
     * Future fluids register their still texture ONCE. Vertical spritesheets
     * use frames>1, so the top face samples a single 16x16 frame.
     * Client registration only; no modification to vessel renderer needed.
     */
    public static void registerSurface(String fluidId, Identifier stillTexture,
                                       int frames, int argb) {
        if (fluidId == null || fluidId.isBlank() || stillTexture == null || frames < 1)
            throw new IllegalArgumentException("Invalid fluid surface registration");
        SURFACES.put(fluidId, new Surface(stillTexture, 1F / frames, argb));
    }

    /** Render a surface strictly INSIDE the original vessel walls. */
    public static void submit(
        String fluidId, int amount, int capacity, float x0, float x1,
        float z0, float z1, float minY, float maxY,
        int packedLight, PoseStack stack, SubmitNodeCollector queue
    ) {
        if (amount <= 0 || capacity <= 0) return;
        Surface surface = SURFACES.getOrDefault(fluidId, DEFAULT);
        float y = minY + (maxY - minY) *
            Math.min(1F, (float) amount / capacity);
        queue.submitCustomGeometry(stack,
            RenderTypes.entityTranslucent(surface.texture()),
            (pose, out) -> {
                vertex(pose, out, x0, y, z0, 0, 0, surface, packedLight);
                vertex(pose, out, x1, y, z0, 1, 0, surface, packedLight);
                vertex(pose, out, x1, y, z1, 1, surface.lastFrameV(), surface, packedLight);
                vertex(pose, out, x0, y, z1, 0, surface.lastFrameV(), surface, packedLight);
            });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer out,
        float x, float y, float z, float u, float v, Surface surface, int light) {
        out.addVertex(pose, x, y, z)
            .setColor(surface.argb())
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0F, 1F, 0F);
    }
}
