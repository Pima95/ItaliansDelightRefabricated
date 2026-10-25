package dev.italiansdelight.common.worldgen;

import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/** Registry-only glue; actual tree geometry belongs to its placer classes. */
public final class ModTreePlacers {
    private ModTreePlacers() {}

    public static final TrunkPlacerType<OliveTrunkPlacer> OLIVE_TRUNK = Registry.register(
        BuiltInRegistries.TRUNK_PLACER_TYPE,
        ModRegistries.id("olive_trunk_placer"),
        new TrunkPlacerType<>(OliveTrunkPlacer.CODEC)
    );

    public static final FoliagePlacerType<OliveFoliagePlacer> OLIVE_FOLIAGE = Registry.register(
        BuiltInRegistries.FOLIAGE_PLACER_TYPE,
        ModRegistries.id("olive_foliage_placer"),
        new FoliagePlacerType<>(OliveFoliagePlacer.CODEC)
    );

    public static final TrunkPlacerType<AncientOliveTrunkPlacer> ANCIENT_OLIVE_TRUNK = Registry.register(
        BuiltInRegistries.TRUNK_PLACER_TYPE,
        ModRegistries.id("ancient_olive_trunk_placer"),
        new TrunkPlacerType<>(AncientOliveTrunkPlacer.CODEC)
    );

    public static final FoliagePlacerType<AncientOliveFoliagePlacer> ANCIENT_OLIVE_FOLIAGE = Registry.register(
        BuiltInRegistries.FOLIAGE_PLACER_TYPE,
        ModRegistries.id("ancient_olive_foliage_placer"),
        new FoliagePlacerType<>(AncientOliveFoliagePlacer.CODEC)
    );

    public static void register() {
        // Trigger the static registrations before the datapack loads.
    }
}
