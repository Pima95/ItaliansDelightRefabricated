package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import mezz.jei.api.recipe.RecipeType;

/** JEI recipe type for jumping on ingredients in a Crushing Tub. */
public final class CrushingTubJeiRecipeTypes {
    private CrushingTubJeiRecipeTypes() {}

    public static final RecipeType<CrushingTubJeiRecipe> CRUSHING_TUB =
        RecipeType.create(ItaliansDelight.MOD_ID, "crushing_tub", CrushingTubJeiRecipe.class);
}
