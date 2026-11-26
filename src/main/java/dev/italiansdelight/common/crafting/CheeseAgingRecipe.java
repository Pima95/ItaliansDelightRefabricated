package dev.italiansdelight.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Data-driven transformation used by cheese wheels aging on a surface or rack.
 */
public final class CheeseAgingRecipe
    implements Recipe<SingleRecipeInput> {

    public static final MapCodec<CheeseAgingRecipe> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Ingredient.CODEC
                    .fieldOf("ingredient")
                    .forGetter(CheeseAgingRecipe::getIngredient),

                ItemStack.STRICT_CODEC
                    .fieldOf("result")
                    .forGetter(CheeseAgingRecipe::getResultTemplate),

                Codec.INT
                    .fieldOf("agingtime")
                    .forGetter(CheeseAgingRecipe::getAgingTime)
            ).apply(
                instance,
                CheeseAgingRecipe::new
            )
        );

    public static final StreamCodec<
        RegistryFriendlyByteBuf,
        CheeseAgingRecipe
    > STREAM_CODEC =
        StreamCodec.of(
            CheeseAgingRecipe::toNetwork,
            CheeseAgingRecipe::fromNetwork
        );

    public static final RecipeSerializer<CheeseAgingRecipe>
        SERIALIZER =
            new RecipeSerializer<CheeseAgingRecipe>() {
                @Override public MapCodec<CheeseAgingRecipe> codec() { return CODEC; }
                @Override public StreamCodec<RegistryFriendlyByteBuf, CheeseAgingRecipe> streamCodec() { return STREAM_CODEC; }
            };

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int agingTime;

    public CheeseAgingRecipe(
        Ingredient ingredient,
        ItemStack result,
        int agingTime
    ) {
        this.ingredient = ingredient;
        this.result = result.copy();
        this.agingTime =
            Math.max(
                1,
                agingTime
            );
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getResultTemplate() {
        return result.copy();
    }

    public int getAgingTime() {
        return agingTime;
    }

    @Override
    public boolean matches(
        SingleRecipeInput input,
        Level level
    ) {
        return ingredient.test(
            input.item()
        );
    }

    public ItemStack assemble(
        SingleRecipeInput input
    ) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>>
    getSerializer() {
        return ModRecipes.CHEESE_AGING_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>>
    getType() {
        return ModRecipes.CHEESE_AGING_TYPE;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return assemble(input);
    }


    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String getGroup() {
        return "cheese_aging";
    }

    private static CheeseAgingRecipe fromNetwork(
        RegistryFriendlyByteBuf buffer
    ) {
        Ingredient ingredient =
            Ingredient.CONTENTS_STREAM_CODEC
                .decode(buffer);

        ItemStack result =
            ItemStack.STREAM_CODEC
                .decode(buffer);

        int agingTime =
            buffer.readVarInt();

        return new CheeseAgingRecipe(
            ingredient,
            result,
            agingTime
        );
    }

    private static void toNetwork(
        RegistryFriendlyByteBuf buffer,
        CheeseAgingRecipe recipe
    ) {
        Ingredient.CONTENTS_STREAM_CODEC
            .encode(
                buffer,
                recipe.ingredient
            );

        ItemStack.STREAM_CODEC
            .encode(
                buffer,
                recipe.result
            );

        buffer.writeVarInt(
            recipe.agingTime
        );
    }
}
