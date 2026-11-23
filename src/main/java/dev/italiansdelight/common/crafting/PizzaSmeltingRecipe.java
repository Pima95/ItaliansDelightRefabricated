package dev.italiansdelight.common.crafting;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaBaking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;

/**
 * Vanilla furnace recipe with a dynamic pizza output.
 * The ordinary SmeltingRecipe output template would discard toppings,
 * quarter masks and the data-derived name.
 */
public final class PizzaSmeltingRecipe extends AbstractCookingRecipe {
    public static final MapCodec<PizzaSmeltingRecipe> CODEC =
            AbstractCookingRecipe.cookingMapCodec(PizzaSmeltingRecipe::new, 1200);
    public static final StreamCodec<RegistryFriendlyByteBuf, PizzaSmeltingRecipe> STREAM_CODEC =
            AbstractCookingRecipe.cookingStreamCodec(PizzaSmeltingRecipe::new);
    public static final RecipeSerializer<PizzaSmeltingRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public PizzaSmeltingRecipe(Recipe.CommonInfo info,
                               AbstractCookingRecipe.CookingBookInfo book,
                               Ingredient ingredient, ItemStackTemplate result,
                               float experience, int cookingTime) {
        super(info, book, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return PizzaBaking.previewCookedPizza(input.item());
    }

    @Override
    protected Item furnaceIcon() {
        return Items.FURNACE;
    }

    @Override
    public RecipeType<? extends AbstractCookingRecipe> getType() {
        return RecipeType.SMELTING;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return switch (category()) {
            case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
            case FOOD -> RecipeBookCategories.FURNACE_FOOD;
            case MISC -> RecipeBookCategories.FURNACE_MISC;
        };
    }

    @Override
    public RecipeSerializer<PizzaSmeltingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
