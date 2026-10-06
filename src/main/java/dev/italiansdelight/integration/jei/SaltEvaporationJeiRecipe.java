package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.salt.SaltCauldronManager;

/**
 * JEI-only description of passive salt evaporation.
 *
 * Gameplay is handled by SaltCauldronManager rather than Minecraft's recipe
 * system, so this adapter exists only to expose the process in JEI.
 */
public record SaltEvaporationJeiRecipe(
    long waterAmount,
    int processingTime,
    int minSalt,
    int maxSalt
) {

    public static SaltEvaporationJeiRecipe createDefault() {
        return new SaltEvaporationJeiRecipe(
            1000L,
            SaltCauldronManager.REQUIRED_USEFUL_TICKS,
            3,
            7
        );
    }
}
