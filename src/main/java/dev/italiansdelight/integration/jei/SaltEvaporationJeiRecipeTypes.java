package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;

import mezz.jei.api.recipe.types.IRecipeType;

/** JEI recipe type for salt evaporation in a vanilla cauldron. */
public final class SaltEvaporationJeiRecipeTypes {

    private SaltEvaporationJeiRecipeTypes() {
    }

    public static final IRecipeType<SaltEvaporationJeiRecipe> SALT_EVAPORATION =
        IRecipeType.create(
            ItaliansDelight.MOD_ID,
            "salt_evaporation",
            SaltEvaporationJeiRecipe.class
        );
}
