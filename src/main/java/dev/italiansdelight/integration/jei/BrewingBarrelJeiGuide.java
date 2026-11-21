package dev.italiansdelight.integration.jei;

/**
 * Actual brewing-barrel production paths. White grapes deliberately remain
 * unregistered until a separate white-wine fluid and vessels exist.
 */
public record BrewingBarrelJeiGuide(String id) {
    public boolean isVinegar() { return "apple_cider_vinegar".equals(id); }
}
