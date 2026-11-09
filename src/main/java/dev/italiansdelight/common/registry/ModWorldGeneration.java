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
    // Data-driven natural placements reuse the original, already-tested
    // sapling tree features and their custom trunk/foliage placers.
    // The numbers are initial chances per eligible chunk, not guaranteed
    // trees: terrain, canopy clearance and nearby vegetation can reject them.
    private static final ResourceKey<PlacedFeature> OLIVE_TREE_WILD = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("olive_tree_wild"));
    private static final ResourceKey<PlacedFeature> ANCIENT_OLIVE_TREE_WILD = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("ancient_olive_tree_wild"));
    private static final ResourceKey<PlacedFeature> ALMOND_TREE_WILD = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("almond_tree_wild"));
    private static final ResourceKey<PlacedFeature> FRUIT_CHERRY_TREE_WILD = ResourceKey.create(
        Registries.PLACED_FEATURE, ModRegistries.id("fruit_cherry_tree_wild"));
    private ModWorldGeneration() {}
    public static void register() {
        // Orchard spawning occurs only during NEW chunk generation.
        // 2x2 ancient olive uses the same configured feature as 2x2 saplings,
        // but its attempt frequency is much lower than a regular olive.
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA),
            GenerationStep.Decoration.VEGETAL_DECORATION, OLIVE_TREE_WILD
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA),
            GenerationStep.Decoration.VEGETAL_DECORATION, ANCIENT_OLIVE_TREE_WILD
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU),
            GenerationStep.Decoration.VEGETAL_DECORATION, ALMOND_TREE_WILD
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.CHERRY_GROVE),
            GenerationStep.Decoration.VEGETAL_DECORATION, FRUIT_CHERRY_TREE_WILD
        );
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
