package com.piergiuseppe.italiansdelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CheeseVatRecipeInput(
    ItemStack first,
    ItemStack second,
    ItemStack third,
    ItemStack container
) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.first;
            case 1 -> this.second;
            case 2 -> this.third;
            case 3 -> this.container;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }
}