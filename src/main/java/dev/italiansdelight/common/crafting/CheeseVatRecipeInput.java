package dev.italiansdelight.common.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Represents the immutable input passed to the Cheese Vat recipe system.
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
