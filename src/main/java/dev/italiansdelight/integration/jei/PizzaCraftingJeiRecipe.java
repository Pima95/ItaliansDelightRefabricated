package dev.italiansdelight.integration.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * One representative recipe for JEI presentation, NOT a new fixed gameplay
 * recipe. The single dynamic PizzaCraftingRecipe still accepts any valid
 * one-to-six-topping combination in any grid arrangement.
 */
public record PizzaCraftingJeiRecipe(List<ItemStack> ingredients, ItemStack result) {
    public PizzaCraftingJeiRecipe {
        ingredients = ingredients.stream().map(ItemStack::copy).toList();
        result = result.copy();
    }

    @Override
    public List<ItemStack> ingredients() {
        return ingredients.stream().map(ItemStack::copy).toList();
    }

    @Override
    public ItemStack result() {
        return result.copy();
    }
}
