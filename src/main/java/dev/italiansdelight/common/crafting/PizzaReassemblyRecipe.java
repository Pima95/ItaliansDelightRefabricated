package dev.italiansdelight.common.crafting;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.pizza.PizzaToppingApplication;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** Four already baked slices become one fully cooked pizza, without refunds. */
public final class PizzaReassemblyRecipe extends CustomRecipe {
    private static final PizzaReassemblyRecipe INSTANCE = new PizzaReassemblyRecipe();
    public static final MapCodec<PizzaReassemblyRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PizzaReassemblyRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<PizzaReassemblyRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    // Grid reading order (top-left, top-right, bottom-left, bottom-right)
    // mapped to the existing north-facing physical quarter bit order.
    private static final int[] QUARTERS = {1, 3, 0, 2};

    private PizzaReassemblyRecipe() {}

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        if (input.ingredientCount() != 4) return ItemStack.EMPTY;
        List<ItemStack> slices = new ArrayList<>(4);
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty()) slices.add(stack);
        }
        return reassemble(slices);
    }

    /** Shared pure implementation for actual crafting and JEI examples. */
    public static ItemStack reassemble(List<ItemStack> slices) {
        if (slices.size() != 4) return ItemStack.EMPTY;
        List<PizzaToppingApplication> applications = new ArrayList<>();
        int legacyMask = 0;
        long sequence = 1L;
        for (int i = 0; i < slices.size(); i++) {
            ItemStack stack = slices.get(i);
            if (!stack.is(ModItems.PIZZA_SLICE)) return ItemStack.EMPTY;
            PizzaSliceData slice = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
            if (slice == null || slice.toppings().size() > PizzaComposition.MAX_TOPPINGS_PER_QUARTER) {
                return ItemStack.EMPTY;
            }
            int bit = 1 << QUARTERS[i];
            if (slice.legacyMozzarella()) legacyMask |= bit;
            Set<Identifier> seen = new HashSet<>();
            for (PizzaSliceData.Topping topping : slice.toppings()) {
                if (!seen.add(topping.canonicalId())) return ItemStack.EMPTY;
                // Preserve the source stack and its order inside this slice.
                // Baked ingredients are historical provenance only. A sauce
                // must never yield another bowl as a result of reassembly.
                applications.add(new PizzaToppingApplication(sequence++,
                        topping.canonicalId(), topping.originalStack(), bit, bit,
                        PizzaBaking.isBowlBearing(topping.canonicalId())));
            }
        }
        PizzaComposition composition = new PizzaComposition(
                PizzaComposition.FORMAT_VERSION, 0, "north", sequence, 0L, applications);
        PizzaItemData data = new PizzaItemData(composition, legacyMask == 15, legacyMask);
        ItemStack result = new ItemStack(ModItems.COOKED_PIZZA);
        result.set(ModDataComponents.PIZZA_CONTENTS, data);
        PizzaDisplayNames.nameWholeStack(result, data);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<PizzaReassemblyRecipe> getSerializer() {
        return SERIALIZER;
    }
}
