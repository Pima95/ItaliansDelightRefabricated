package dev.italiansdelight.integration.jei;

/**
 * Two real wine-brewing recipes: purple and white grape must both yield the
 * mod's existing wine fluid. No distinct white-wine item is registered yet.
 */
public record BrewingBarrelJeiGuide(String id) {
    public boolean isWhite() { return "white".equals(id); }
}
