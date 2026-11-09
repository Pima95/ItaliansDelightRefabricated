package dev.italiansdelight.common.fluid;

import java.util.List;

/**
 * Loader-independent domain catalog. Keep this file free of Fabric, NeoForge
 * and Architectury imports; loader adapters bind these IDs to fluid APIs.
 *
 * Milk from vanilla Minecraft is deliberately NOT registered here. Its historical
 * tank ID, "minecraft:milk", must remain unchanged for existing player worlds.
 */
public final class FluidCatalog {
    private FluidCatalog() {}

    public record Definition(String id, boolean placeable, String previousId) {}

    public static final List<Definition> DEFINITIONS = List.of(
        new Definition("italiansdelight:olive_oil", true, ""),
        new Definition("italiansdelight:apple_juice", true, ""),
        new Definition("italiansdelight:sheep_milk", false, ""),
        new Definition("italiansdelight:whey", false, ""),
        new Definition("italiansdelight:rennet", false, ""),
        new Definition("italiansdelight:apple_cider_vinegar", false, ""),
        new Definition("italiansdelight:golden_apple_juice", false, "")
    );

    public static boolean canBePlaced(String id) {
        return DEFINITIONS.stream()
            .anyMatch(d -> d.id().equals(id) && d.placeable());
    }
}
