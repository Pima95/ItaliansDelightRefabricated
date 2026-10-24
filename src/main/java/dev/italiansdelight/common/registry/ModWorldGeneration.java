package dev.italiansdelight.common.registry;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Same 1/120 rarity and 64 attempts as FDR 26.2 wild onions/carrots. */
public final class ModWorldGeneration {
    private static final ResourceKey<PlacedFeature> CARDOON_PATCH = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("cardoon_patch"));
    private static final ResourceKey<PlacedFeature> WILD_BASIL_PATCH = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("wild_basil_patch"));
    // Olive world generation intentionally DISABLED until we have custom
    // authentic olive canopies and twisted trunks. Sapling-only tree growth
    // uses separate configured features and remains available for tests.
    private ModWorldGeneration() {}
    public static void register() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS,
                Biomes.MEADOW, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_SAVANNA),
            GenerationStep.Decoration.VEGETAL_DECORATION, CARDOON_PATCH
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS,
                Biomes.MEADOW, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST,
                Biomes.CHERRY_GROVE, Biomes.SPARSE_JUNGLE),
            GenerationStep.Decoration.VEGETAL_DECORATION, WILD_BASIL_PATCH
        );
    }
}
