package dev.italiansdelight.common.crafting;

import dev.italiansdelight.common.pizza.PizzaBaking;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/** Dynamic cooking result retains the pizza's original composition. */
public final class PizzaSmeltingRecipe extends AbstractCookingRecipe {
    public static final RecipeSerializer<PizzaSmeltingRecipe> SERIALIZER =
        new SimpleCookingSerializer<>(PizzaSmeltingRecipe::new, 200);

    public PizzaSmeltingRecipe(String group, CookingBookCategory category,
            Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeType.SMELTING, group, category, ingredient, result, experience, cookingTime);
    }

    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return PizzaBaking.previewCookedPizza(input.item());
    }

    @Override public ItemStack getToastSymbol() { return new ItemStack(Items.FURNACE); }

    @Override public RecipeSerializer<PizzaSmeltingRecipe> getSerializer() { return SERIALIZER; }
}
