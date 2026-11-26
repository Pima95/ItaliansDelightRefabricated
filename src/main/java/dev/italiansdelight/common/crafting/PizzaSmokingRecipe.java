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
public final class PizzaSmokingRecipe extends AbstractCookingRecipe {
    public static final RecipeSerializer<PizzaSmokingRecipe> SERIALIZER =
        new SimpleCookingSerializer<>(PizzaSmokingRecipe::new, 100);

    public PizzaSmokingRecipe(String group, CookingBookCategory category,
            Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(RecipeType.SMOKING, group, category, ingredient, result, experience, cookingTime);
    }

    @Override public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return PizzaBaking.previewCookedPizza(input.item());
    }

    @Override public ItemStack getToastSymbol() { return new ItemStack(Items.SMOKER); }

    @Override public RecipeSerializer<PizzaSmokingRecipe> getSerializer() { return SERIALIZER; }
}
