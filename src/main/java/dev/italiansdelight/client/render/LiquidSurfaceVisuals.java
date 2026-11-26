package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

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

    private record Surface(ResourceLocation sprite, int tint) {}

    private static final Surface WATER = new Surface(
        ResourceLocation.withDefaultNamespace("block/water_still"), 0xFFFFFFFF);
    private static final Map<String, Surface> SURFACES = new ConcurrentHashMap<>(Map.of(
        "minecraft:water", WATER,
        // Honey is a virtual liquid in this port (bottle-only, not a world fluid).
        // Render its tank surface from vanilla honey art, never blue water.
        "minecraft:honey", new Surface(
            ResourceLocation.withDefaultNamespace("block/honey_block_top"), 0xFFFFFFFF),
        "italiansdelight:golden_apple_juice", new Surface(
            ModRegistries.id("block/fluids/golden_apple_juice_still"), 0xFFFFFFFF),
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

    static {
        // Map.of permits only ten entries; extend the mutable map explicitly.
        SURFACES.put("italiansdelight:white_grape_juice",
            new Surface(ModRegistries.id("block/fluids/white_grape_juice_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:grape_juice",
            new Surface(ModRegistries.id("block/fluids/grape_juice_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:wine",
            new Surface(ModRegistries.id("block/fluids/booze/wine_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:cherry_juice",
            new Surface(ModRegistries.id("block/fluids/booze/cherry_juice_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:white_wine",
            new Surface(ModRegistries.id("block/fluids/booze/white_wine_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:cherry_wine",
            new Surface(ModRegistries.id("block/fluids/booze/cherry_wine_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:mead",
            new Surface(ModRegistries.id("block/fluids/booze/mead_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:cider",
            new Surface(ModRegistries.id("block/fluids/booze/cider_still"), 0xFFFFFFFF));
        SURFACES.put("italiansdelight:red_wine_vinegar",
            new Surface(ModRegistries.id("block/fluids/booze/red_wine_vinegar_still"), 0xFFFFFFFF));
    }

    // Only external/unregistered fluids need the conventional five-path atlas
    // search. Remember resolved IDs for the active atlas, never the sprite
    // object itself; rendering still samples current animated atlas frames.
    private static final Map<String, ResourceLocation> EXTERNAL_STILL_IDS = new HashMap<>();
    private static TextureAtlas externalAtlas;
    private static TextureAtlasSprite externalWaterMarker;

    /** Other modded liquids only need a registered block-atlas still sprite. */
    public static void registerSurface(String fluidId, ResourceLocation stillSprite, int tint) {
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
        int packedLight, PoseStack stack, MultiBufferSource buffers
    ) {
        submit(fluidId, FluidStack.EMPTY, amount, capacity, x0, x1,
            z0, z1, minY, maxY, waterTint, packedLight, stack, buffers);
    }

    public static void submit(
        String fluidId, FluidStack variant, int amount, int capacity,
        float x0, float x1, float z0, float z1, float minY, float maxY,
        int waterTint, int packedLight, PoseStack stack, MultiBufferSource buffers
    ) {
        if (amount <= 0 || capacity <= 0) return;
        var atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        Surface known = SURFACES.get(fluidId);
        boolean custom = known == null;
        TextureAtlasSprite sprite = custom
            ? lookUpFluidSprite(fluidId, variant, atlas)
            : atlas.getSprite(known.sprite());
        float y = minY + (maxY - minY) * Math.min(1F, (float) amount / capacity);
        boolean water = "minecraft:water".equals(fluidId);
        int color = water ? (0xFF000000 | (waterTint & 0xFFFFFF))
            : known != null ? known.tint()
            : (!variant.isEmpty()
                ? IClientFluidTypeExtensions.of(variant.getFluid()).getTintColor(variant)
                : 0xFFFFFFFF);
        // Preserve alpha supplied by other mods, plus vanilla water's alpha.
        if ((color >>> 24) == 0) color |= 0xFF000000;
        // Both grape juices have semi-transparent animated RGBA sprites.
        // A known fluid usually uses cutout, which discards alpha blending:
        // explicitly choose translucent for grape juices so their texture's
        // alpha channel produces real in-world transparency in barrels/tanks.
        boolean translucent = water || custom || (color >>> 24) < 255
            || "italiansdelight:grape_juice".equals(fluidId)
            || "italiansdelight:white_grape_juice".equals(fluidId);
        final int vertexColor = color;
        PoseStack.Pose pose = stack.last();
        VertexConsumer out = buffers.getBuffer(translucent
            ? RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS)
            : RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        {
                // Counter-clockwise looking from above (upward winding).
                vertex(pose, out, x0, y, z1, sprite.getU0(), sprite.getV1(), vertexColor, packedLight);
                vertex(pose, out, x1, y, z1, sprite.getU1(), sprite.getV1(), vertexColor, packedLight);
                vertex(pose, out, x1, y, z0, sprite.getU1(), sprite.getV0(), vertexColor, packedLight);
                vertex(pose, out, x0, y, z0, sprite.getU0(), sprite.getV0(), vertexColor, packedLight);
        }
    }

    /**
     * Look for the conventional still-fluid sprite names shipped by Fabric
     * fluid mods. Never render the missing-texture checkerboard: a fluid with
     * an unconventional sprite path falls back to vanilla water's surface,
     * colored using that fluid's own variant render handler.
     */
    private static TextureAtlasSprite lookUpFluidSprite(String fluidId, FluidStack variant,
            TextureAtlas atlas) {
        if (!variant.isEmpty()) {
            ResourceLocation still = IClientFluidTypeExtensions.of(variant.getFluid()).getStillTexture(variant);
            if (still != null) return atlas.getSprite(still);
        }
        // A texture-pack reload can recreate sprites without changing fluid IDs.
        // Checking the water sprite identity also handles atlases reused in place.
        TextureAtlasSprite waterSprite = atlas.getSprite(WATER.sprite());
        if (atlas != externalAtlas || waterSprite != externalWaterMarker) {
            EXTERNAL_STILL_IDS.clear();
            externalAtlas = atlas;
            externalWaterMarker = waterSprite;
        }
        ResourceLocation still = EXTERNAL_STILL_IDS.computeIfAbsent(fluidId,
                id -> resolveExternalStillId(id, atlas));
        return atlas.getSprite(still);
    }

    private static ResourceLocation resolveExternalStillId(String fluidId, TextureAtlas atlas) {
        ResourceLocation key = ResourceLocation.tryParse(fluidId);
        if (key != null) {
            String name = key.getPath();
            for (String path : List.of(
                    "block/" + name + "_still", "block/fluids/" + name + "_still",
                    "block/fluid/" + name + "_still", "block/" + name,
                    "block/" + name + "/still")) {
                ResourceLocation candidate = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path);
                TextureAtlasSprite sprite = atlas.getSprite(candidate);
                if (sprite.contents().name().equals(candidate)) return candidate;
            }
        }
        return WATER.sprite();
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
