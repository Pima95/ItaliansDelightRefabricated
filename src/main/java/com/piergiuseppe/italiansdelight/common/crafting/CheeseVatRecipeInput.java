package com.piergiuseppe.italiansdelight.common.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Rappresenta l'input immutabile passato al sistema ricette della Cheese Vat.
 */

public record CheeseVatRecipeInput(
    ItemStack first,
    ItemStack second,
    ItemStack third
) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.first;
            case 1 -> this.second;
            case 2 -> this.third;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
