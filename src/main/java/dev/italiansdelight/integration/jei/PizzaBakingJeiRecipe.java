package dev.italiansdelight.integration.jei;

import net.minecraft.world.item.ItemStack;

/**
 * JEI-only example of baking an already-formed pizza.
 * This is NOT a new datapack recipe: PizzaOvenBlockEntity and the two
 * vanilla-furnace recipe serializers remain the gameplay authorities.
 */
public record PizzaBakingJeiRecipe(
        ItemStack rawPizza,
        ItemStack cookedPizza,
        ItemStack station,
        int cookingTicks,
        String methodKey
) {
    public PizzaBakingJeiRecipe {
        rawPizza = rawPizza.copy();
        cookedPizza = cookedPizza.copy();
        station = station.copy();
        if (cookingTicks <= 0) {
            throw new IllegalArgumentException("JEI pizza baking time must be positive");
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

    @Override
    public ItemStack station() {
        return station.copy();
    }
}
