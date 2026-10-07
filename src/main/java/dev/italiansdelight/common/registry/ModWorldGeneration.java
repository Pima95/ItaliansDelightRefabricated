package dev.italiansdelight.common.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Adds the data-defined cardoon patches to newly generated grassy biomes. */
public final class ModWorldGeneration {
    private static final ResourceKey<PlacedFeature> CARDOON_PATCH = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("cardoon_patch")
    );

    private static final ResourceKey<PlacedFeature> WILD_BASIL_PATCH = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("wild_basil_patch")
    );

    private ModWorldGeneration() {
    }

    public static void register() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            CARDOON_PATCH
        );

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            WILD_BASIL_PATCH
        );
    }
}
