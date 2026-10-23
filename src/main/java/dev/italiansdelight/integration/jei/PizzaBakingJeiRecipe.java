package dev.italiansdelight.integration.jei;

import net.minecraft.world.item.ItemStack;

/**
 * One Pizza Oven JEI display example, not a new gameplay recipe.
 * The vanilla smelting/smoking categories already display their own recipes.
 */
public record PizzaBakingJeiRecipe(
        ItemStack rawPizza,
        ItemStack cookedPizza,
        int cookingTicks
) {
    public PizzaBakingJeiRecipe {
        rawPizza = rawPizza.copy();
        cookedPizza = cookedPizza.copy();
        if (cookingTicks <= 0) {
            throw new IllegalArgumentException("JEI Pizza Oven time must be positive");
        }
    }

    @Override
    public ItemStack rawPizza() {
        return rawPizza.copy();
    }

    @Override
    public ItemStack cookedPizza() {
        return cookedPizza.copy();
    }
}
