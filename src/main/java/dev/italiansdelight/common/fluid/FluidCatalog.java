package dev.italiansdelight.common.fluid;

import java.util.List;

/**
 * Loader-independent domain catalog. Keep this file free of Fabric, NeoForge
 * and Architectury imports; loader adapters bind these IDs to fluid APIs.
 *
 * "minecraft:milk" was a historical virtual tank identifier, not a vanilla
 * fluid registry ID. Never register new fluids under the Minecraft namespace.
 */
public final class FluidCatalog {
    private FluidCatalog() {}

    public record Definition(String id, boolean placeable, String previousId) {}

    public static final List<Definition> DEFINITIONS = List.of(
        new Definition("italiansdelight:olive_oil", true, ""),
        new Definition("italiansdelight:apple_juice", true, ""),
        new Definition("italiansdelight:cow_milk", false, "minecraft:milk"),
        new Definition("italiansdelight:sheep_milk", false, ""),
        new Definition("italiansdelight:whey", false, ""),
        new Definition("italiansdelight:rennet", false, ""),
        new Definition("italiansdelight:apple_cider_vinegar", false, "")
    );

    public static String canonicalId(String id) {
        if ("minecraft:milk".equals(id)) return "italiansdelight:cow_milk";
        return id;
    }

    public static boolean canBePlaced(String id) {
        return DEFINITIONS.stream()
            .anyMatch(d -> d.id().equals(id) && d.placeable());
    }
}
