package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Small JEI-only adapter that lets the current Aging screen display both
 * cheese_aging and cheese_drying recipes without changing their gameplay
 * recipe types.
 */
public record CheeseAgingJeiRecipe(
    Ingredient ingredient,
    ItemStackTemplate result,
    int processingTime,
    Process process
) {

    public enum Process {
        AGING,
        DRYING
    }

    public static CheeseAgingJeiRecipe fromAging(
        CheeseAgingRecipe recipe
    ) {
        return new CheeseAgingJeiRecipe(
            recipe.getIngredient(),
            recipe.getResultTemplate(),
            recipe.getAgingTime(),
            Process.AGING
        );
    }

    public static CheeseAgingJeiRecipe fromDrying(
        CheeseDryingRecipe recipe
    ) {
        return new CheeseAgingJeiRecipe(
            recipe.getIngredient(),
            recipe.getResultTemplate(),
            recipe.getDryingTime(),
            Process.DRYING
        );
    }
}
