package dev.italiansdelight.common.worldgen;

import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/** Registry-only glue; actual tree geometry belongs to its placer classes. */
public final class ModTreePlacers {
    private ModTreePlacers() {}

    public static TrunkPlacerType<OliveTrunkPlacer> OLIVE_TRUNK;

    public static FoliagePlacerType<OliveFoliagePlacer> OLIVE_FOLIAGE;

    public static TrunkPlacerType<AncientOliveTrunkPlacer> ANCIENT_OLIVE_TRUNK;

    public static FoliagePlacerType<AncientOliveFoliagePlacer> ANCIENT_OLIVE_FOLIAGE;

    public static TrunkPlacerType<AlmondTrunkPlacer> ALMOND_TRUNK;

    public static FoliagePlacerType<AlmondFoliagePlacer> ALMOND_FOLIAGE;

    public static TrunkPlacerType<FruitCherryTrunkPlacer> FRUIT_CHERRY_TRUNK;

    public static FoliagePlacerType<FruitCherryFoliagePlacer> FRUIT_CHERRY_FOLIAGE;

    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.TRUNK_PLACER_TYPE)) {
            OLIVE_TRUNK = Registry.register(
                    BuiltInRegistries.TRUNK_PLACER_TYPE,
                    ModRegistries.id("olive_trunk_placer"),
                    new TrunkPlacerType<>(OliveTrunkPlacer.CODEC)
                );
            ANCIENT_OLIVE_TRUNK = Registry.register(
                    BuiltInRegistries.TRUNK_PLACER_TYPE,
                    ModRegistries.id("ancient_olive_trunk_placer"),
                    new TrunkPlacerType<>(AncientOliveTrunkPlacer.CODEC)
                );
            ALMOND_TRUNK = Registry.register(
                    BuiltInRegistries.TRUNK_PLACER_TYPE,
                    ModRegistries.id("almond_trunk_placer"),
                    new TrunkPlacerType<>(AlmondTrunkPlacer.CODEC)
                );
            FRUIT_CHERRY_TRUNK = Registry.register(
                    BuiltInRegistries.TRUNK_PLACER_TYPE,
                    ModRegistries.id("fruit_cherry_trunk_placer"),
                    new TrunkPlacerType<>(FruitCherryTrunkPlacer.CODEC)
                );
        }
        if (event.getRegistryKey().equals(Registries.FOLIAGE_PLACER_TYPE)) {
            OLIVE_FOLIAGE = Registry.register(
                    BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                    ModRegistries.id("olive_foliage_placer"),
                    new FoliagePlacerType<>(OliveFoliagePlacer.CODEC)
                );
            ANCIENT_OLIVE_FOLIAGE = Registry.register(
                    BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                    ModRegistries.id("ancient_olive_foliage_placer"),
                    new FoliagePlacerType<>(AncientOliveFoliagePlacer.CODEC)
                );
            ALMOND_FOLIAGE = Registry.register(
                    BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                    ModRegistries.id("almond_foliage_placer"),
                    new FoliagePlacerType<>(AlmondFoliagePlacer.CODEC)
                );
            FRUIT_CHERRY_FOLIAGE = Registry.register(
                    BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                    ModRegistries.id("fruit_cherry_foliage_placer"),
                    new FoliagePlacerType<>(FruitCherryFoliagePlacer.CODEC)
                );
        }
    }
}
