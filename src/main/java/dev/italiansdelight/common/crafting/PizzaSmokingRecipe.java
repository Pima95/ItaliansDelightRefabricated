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
import net.minecraft.world.item.crafting.SmokingRecipe;

/** Smoker counterpart of PizzaSmeltingRecipe, with the same lossless result. */
public final class PizzaSmokingRecipe extends SmokingRecipe {
    public static final MapCodec<PizzaSmokingRecipe> CODEC =
            AbstractCookingRecipe.cookingMapCodec(PizzaSmokingRecipe::new, 600);
    public static final StreamCodec<RegistryFriendlyByteBuf, PizzaSmokingRecipe> STREAM_CODEC =
            AbstractCookingRecipe.cookingStreamCodec(PizzaSmokingRecipe::new);
    public static final RecipeSerializer<PizzaSmokingRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public PizzaSmokingRecipe(Recipe.CommonInfo info,
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
    public RecipeSerializer<PizzaSmokingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
