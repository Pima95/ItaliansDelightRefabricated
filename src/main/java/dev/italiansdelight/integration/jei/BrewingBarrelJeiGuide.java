package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModFluids;
import net.minecraft.world.level.material.Fluid;

/** Exact fermenter recipes, never a pretend recipe for an unregistered drink. */
public record BrewingBarrelJeiGuide(String id) {
    public boolean isHoney() { return id.equals("mead"); }
    public boolean requiresCulture() { return id.equals("cider"); }
    public boolean isVinegar() {
        return id.equals("apple_cider_vinegar") || id.equals("red_wine_vinegar")
            || id.equals("cider_vinegar");
    }
    public Fluid input() {
        return switch (id) {
            case "white_wine" -> ModFluids.WHITE_GRAPE_JUICE;
            case "cherry_wine" -> ModFluids.CHERRY_JUICE;
            case "mead" -> ModFluids.MEAD; // virtual honey: display a honey bottle instead
            case "cider", "apple_cider_vinegar" -> ModFluids.APPLE_JUICE_STILL;
            case "cider_vinegar" -> ModFluids.CIDER;
            case "red_wine_vinegar" -> ModFluids.WINE;
            default -> ModFluids.GRAPE_JUICE;
        };
    }
    public Fluid output() {
        return switch (id) {
            case "white_wine" -> ModFluids.WHITE_WINE;
            case "cherry_wine" -> ModFluids.CHERRY_WINE;
            case "mead" -> ModFluids.MEAD;
            case "cider" -> ModFluids.CIDER;
            case "apple_cider_vinegar", "cider_vinegar" -> ModFluids.APPLE_CIDER_VINEGAR;
            case "red_wine_vinegar" -> ModFluids.RED_WINE_VINEGAR;
            default -> ModFluids.WINE;
        };
    }
}
