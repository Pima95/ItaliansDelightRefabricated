package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * One animated fluid-surface path shared by barrels and custom cauldrons.
 *
 * Vanilla fluid blocks and this renderer now sample THE SAME Minecraft blocks
 * atlas; SpriteContents animates that atlas without manual frame calculations.
 * The old direct-PNG renderer always sampled frame zero and applied blending,
 * making water pale and oil unnaturally dark and motionless.
 */
public final class LiquidSurfaceVisuals {
    private LiquidSurfaceVisuals() {}

    private record Surface(Identifier sprite, int tint) {}

    private static final Surface WATER = new Surface(
        Identifier.withDefaultNamespace("block/water_still"), 0xFFFFFFFF);
    private static final Map<String, Surface> SURFACES = new ConcurrentHashMap<>(Map.of(
        "minecraft:water", WATER,
        "italiansdelight:apple_juice",
            new Surface(ModRegistries.id("block/fluids/apple_juice_still"), 0xFFFFFFFF),
        "italiansdelight:olive_oil",
            new Surface(ModRegistries.id("block/fluids/olive_oil_still"), 0xFFFFFFFF),
        "minecraft:milk", staticSurface("milk"),
        "italiansdelight:sheep_milk", staticSurface("sheep_milk"),
        "italiansdelight:whey", staticSurface("whey")
    ));

    private static Surface staticSurface(String key) {
        return new Surface(ModRegistries.id("block/liquid_surface/" + key), 0xFFFFFFFF);
    }

    /** Other modded liquids only need a registered block-atlas still sprite. */
    public static void registerSurface(String fluidId, Identifier stillSprite, int tint) {
        if (fluidId == null || fluidId.isBlank() || stillSprite == null)
            throw new IllegalArgumentException("Invalid liquid surface");
        SURFACES.put(fluidId, new Surface(stillSprite, tint));
    }

    /**
     * Animated fluid surface from the vanilla blocks atlas.
     * Water in Minecraft 26.2 has a native sprite opacity of 180/255;
     * use a translucent pass so this alpha is preserved (as in vanilla
     * water cauldrons). Other liquids keep their prior cutout pass.
     */
    public static void submit(
        String fluidId, int amount, int capacity, float x0, float x1,
        float z0, float z1, float minY, float maxY, int waterTint,
        int packedLight, PoseStack stack, SubmitNodeCollector queue
    ) {
        if (amount <= 0 || capacity <= 0) return;
        Surface surface = SURFACES.getOrDefault(fluidId, WATER);
        // Fresh atlas lookup per submit keeps resource-pack reloads and animations working.
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager()
            .getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(surface.sprite());
        float y = minY + (maxY - minY) * Math.min(1F, (float) amount / capacity);
        boolean water = "minecraft:water".equals(fluidId);
        // Preserve native water-sprite alpha (180/255) instead of forcing
        // opaque pixels. Its biome tint only adjusts RGB, not opacity.
        int color = water
            ? 0xFF000000 | (waterTint & 0xFFFFFF)
            : surface.tint();

        // entityCutout discards/blunts the fractional alpha of water_still.
        // The translucent atlas pass retains it, revealing the wooden bottom
        // of all Barrel variants exactly as the vanilla cauldron reveals iron.
        // Keep all non-water fluids on the previous rendering path.
        queue.submitCustomGeometry(stack,
            water ? RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS)
                  : RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
            (pose, out) -> {
                // Counter-clockwise looking from above (upward winding).
                vertex(pose, out, x0, y, z1, sprite.getU0(), sprite.getV1(), color, packedLight);
                vertex(pose, out, x1, y, z1, sprite.getU1(), sprite.getV1(), color, packedLight);
                vertex(pose, out, x1, y, z0, sprite.getU1(), sprite.getV0(), color, packedLight);
                vertex(pose, out, x0, y, z0, sprite.getU0(), sprite.getV0(), color, packedLight);
            });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer out,
        float x, float y, float z, float u, float v, int tint, int light) {
        out.addVertex(pose, x, y, z)
            .setColor(tint)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0F, 1F, 0F);
    }
}
