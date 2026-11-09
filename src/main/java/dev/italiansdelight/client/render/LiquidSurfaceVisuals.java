package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
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
        // Honey is a virtual liquid in this port (bottle-only, not a world fluid).
        // Render its tank surface from vanilla honey art, never blue water.
        "minecraft:honey", new Surface(
            Identifier.withDefaultNamespace("block/honey_block_top"), 0xFFFFFFFF),
        "italiansdelight:golden_apple_juice", new Surface(
            Identifier.withDefaultNamespace("block/honey_block_top"), 0xFFFFD471),
        "italiansdelight:apple_juice",
            new Surface(ModRegistries.id("block/fluids/apple_juice_still"), 0xFFFFFFFF),
        "italiansdelight:olive_oil",
            new Surface(ModRegistries.id("block/fluids/olive_oil_still"), 0xFFFFFFFF),
        "minecraft:milk", new Surface(ModRegistries.id("block/fluids/milk_still"), 0xFFFFFFFF),
        "italiansdelight:sheep_milk", new Surface(ModRegistries.id("block/fluids/sheep_milk_still"), 0xFFFFFFFF),
        "italiansdelight:whey", new Surface(ModRegistries.id("block/fluids/whey_still"), 0xFFFFFFFF),
        "italiansdelight:rennet", new Surface(
            ModRegistries.id("block/fluids/rennet_still"), 0xFFFFFFFF),
        "italiansdelight:apple_cider_vinegar", new Surface(
            ModRegistries.id("block/fluids/booze/cider_still"), 0xFFFFFFFF)
    ));

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
    // Unregistered or old virtual fluids use the original ID-only rendering.
    public static void submit(
        String fluidId, int amount, int capacity, float x0, float x1,
        float z0, float z1, float minY, float maxY, int waterTint,
        int packedLight, PoseStack stack, SubmitNodeCollector queue
    ) {
        submit(fluidId, FluidVariant.blank(), amount, capacity, x0, x1,
            z0, z1, minY, maxY, waterTint, packedLight, stack, queue);
    }

    public static void submit(
        String fluidId, FluidVariant variant, int amount, int capacity,
        float x0, float x1, float z0, float z1, float minY, float maxY,
        int waterTint, int packedLight, PoseStack stack, SubmitNodeCollector queue
    ) {
        if (amount <= 0 || capacity <= 0) return;
        var atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        Surface known = SURFACES.get(fluidId);
        boolean custom = known == null;
        TextureAtlasSprite sprite = custom
            ? lookUpFluidSprite(fluidId, atlas)
            : atlas.getSprite(known.sprite());
        float y = minY + (maxY - minY) * Math.min(1F, (float) amount / capacity);
        boolean water = "minecraft:water".equals(fluidId);
        int color = water ? (0xFF000000 | (waterTint & 0xFFFFFF))
            : known != null ? known.tint()
            : (!variant.isBlank()
                ? FluidVariantRendering.getColor(variant, Minecraft.getInstance().level, null)
                : 0xFFFFFFFF);
        // Preserve alpha supplied by other mods, plus vanilla water's alpha.
        if ((color >>> 24) == 0) color |= 0xFF000000;
        boolean translucent = water || custom || (color >>> 24) < 255;
        final int vertexColor = color;
        queue.submitCustomGeometry(stack,
            translucent ? RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS)
                        : RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
            (pose, out) -> {
                // Counter-clockwise looking from above (upward winding).
                vertex(pose, out, x0, y, z1, sprite.getU0(), sprite.getV1(), vertexColor, packedLight);
                vertex(pose, out, x1, y, z1, sprite.getU1(), sprite.getV1(), vertexColor, packedLight);
                vertex(pose, out, x1, y, z0, sprite.getU1(), sprite.getV0(), vertexColor, packedLight);
                vertex(pose, out, x0, y, z0, sprite.getU0(), sprite.getV0(), vertexColor, packedLight);
            });
    }

    /**
     * Look for the conventional still-fluid sprite names shipped by Fabric
     * fluid mods. Never render the missing-texture checkerboard: a fluid with
     * an unconventional sprite path falls back to vanilla water's surface,
     * colored using that fluid's own variant render handler.
     */
    private static TextureAtlasSprite lookUpFluidSprite(String fluidId,
            net.minecraft.client.renderer.texture.TextureAtlas atlas) {
        Identifier key = Identifier.tryParse(fluidId);
        if (key != null) {
            String name = key.getPath();
            for (String path : List.of(
                    "block/" + name + "_still", "block/fluids/" + name + "_still",
                    "block/fluid/" + name + "_still", "block/" + name,
                    "block/" + name + "/still")) {
                Identifier candidate = Identifier.fromNamespaceAndPath(key.getNamespace(), path);
                TextureAtlasSprite sprite = atlas.getSprite(candidate);
                if (sprite.contents().name().equals(candidate)) return sprite;
            }
        }
        return atlas.getSprite(WATER.sprite());
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
