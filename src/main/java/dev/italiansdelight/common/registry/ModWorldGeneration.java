package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.worldgen.WildGrapeFeature;
import dev.italiansdelight.common.worldgen.InOrderFeature;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ModWorldGeneration {
    private ModWorldGeneration() {}
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.FEATURE, ModRegistries.id("wild_grape_vine"), () -> new WildGrapeFeature(false));
        event.register(Registries.FEATURE, ModRegistries.id("wild_white_grape_vine"), () -> new WildGrapeFeature(true));
        event.register(Registries.FEATURE, ModRegistries.id("in_order"), InOrderFeature::new);
    }
}
