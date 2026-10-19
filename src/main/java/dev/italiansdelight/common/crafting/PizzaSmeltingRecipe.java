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
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * Vanilla furnace recipe with a dynamic pizza output.
 * The ordinary SmeltingRecipe output template would discard toppings,
 * quarter masks and the data-derived name.
 */
public final class PizzaSmeltingRecipe extends SmeltingRecipe {
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
        return PizzaBaking.finish(input.item(), PizzaBaking.MAX_PROGRESS)
                .map(PizzaBaking.BakingResult::cookedPizza)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<PizzaSmeltingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
