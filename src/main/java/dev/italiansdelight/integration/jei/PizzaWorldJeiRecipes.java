package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import java.util.Arrays;
import java.util.List;

/**
 * Reuses the already validated Margherita crafting example only as a visual
 * reference. Every page describes existing right-click gameplay mechanics.
 */
public final class PizzaWorldJeiRecipes {
    private PizzaWorldJeiRecipes() {
    }

    public static List<PizzaWorldJeiRecipe> createExamples() {
        List<PizzaCraftingJeiRecipe> examples =
                PizzaCraftingJeiRecipes.createExamples();
        if (examples.size() < 2 || examples.get(1).ingredients().size() != 4) {
            ItaliansDelight.LOGGER.warn(
                    "[Pizza / JEI] World guide unavailable: Margherita example missing");
            return List.of();
        }

        PizzaCraftingJeiRecipe margherita = examples.get(1);
        return Arrays.stream(PizzaWorldJeiRecipe.Step.values())
                .map(step -> new PizzaWorldJeiRecipe(
                        step, margherita.ingredients(), margherita.result()))
                .toList();
    }
}
