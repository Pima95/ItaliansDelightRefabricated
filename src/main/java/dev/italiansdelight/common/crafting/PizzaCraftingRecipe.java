package dev.italiansdelight.common.crafting;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaCompositionOperations;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaToppingMappings;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaQuarterSelection;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.Optional;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Dynamic shapeless pizza crafting: exactly one FDR wheat dough plus one to
 * six distinct approved toppings, in any grid slots.
 *
 * <p>Each original topping application covers ALL FOUR physical quarters.
 * Using the same operations as preparation in the world preserves the
 * canonical equivalences, allowed items, topping limit and sauce exclusion.
 *
 * <p>Crafting returns empty bowls immediately, while world-applied toppings
 * continue to return them at baking. A persistent flag prevents double refunds.
 */
public final class PizzaCraftingRecipe extends CustomRecipe {
    private static final PizzaCraftingRecipe INSTANCE = new PizzaCraftingRecipe();

    public static final MapCodec<PizzaCraftingRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PizzaCraftingRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<PizzaCraftingRecipe> SERIALIZER =
            new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private PizzaCraftingRecipe() {
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return compose(input).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        Optional<PizzaComposition> composition = compose(input);
        if (composition.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Preserve which sauce bowls the crafting grid already returned.
        PizzaComposition base = composition.get();
        var marked = base.applications().stream()
                .map(a -> PizzaBaking.isBowlBearing(a.canonicalId())
                        ? a.withCraftingBowlReturned() : a)
                .toList();
        PizzaItemData data = new PizzaItemData(
                base.withApplications(marked, base.nextApplicationId()), false);
        ItemStack pizza = new ItemStack(ModItems.RAW_PIZZA);
        pizza.set(ModDataComponents.PIZZA_CONTENTS, data);
        PizzaDisplayNames.nameWholeStack(pizza, data);
        return pizza;
    }

    /**
     * Return one empty bowl per sauce, cream or ricotta ingredient here.
     * Vanilla leaves it in the emptied grid slot (or moves it to inventory
     * when the input slot had multiple items). Invalid recipes refund nothing.
     */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remains = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        if (compose(input).isEmpty()) {
            return remains;
        }
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack ingredient = input.getItem(slot);
            if (PizzaToppingMappings.find(ingredient)
                    .filter(type -> PizzaBaking.isBowlBearing(type.id())).isPresent()) {
                remains.set(slot, new ItemStack(Items.BOWL));
            }
        }
        return remains;
    }

    @Override
    public RecipeSerializer<PizzaCraftingRecipe> getSerializer() {
        return SERIALIZER;
    }

    /**
     * Pure composition build: vanilla consumes one unit from each occupied
     * slot only when the crafting output is taken. Inputs remain untouched.
     */
    private static Optional<PizzaComposition> compose(CraftingInput input) {
        int occupied = input.ingredientCount();
        if (occupied < 2 || occupied > 7) {
            return Optional.empty(); // 1 dough + 1..6 toppings.
        }

        int doughCount = 0;
        int toppingCount = 0;
        PizzaComposition composition = PizzaComposition.empty();

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack ingredient = input.getItem(slot);
            if (ingredient.isEmpty()) {
                continue;
            }

            if (ingredient.is(
                    vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get())) {
                if (++doughCount != 1) {
                    return Optional.empty();
                }
                continue;
            }

            // This also rejects forbidden/whole/planned items, duplicate
            // canonical IDs (including raw/cooked), and overlapping sauces.
            Optional<PizzaComposition> updated = PizzaCompositionOperations.add(
                    composition, ingredient, PizzaQuarterSelection.ALL_QUARTERS);
            if (updated.isEmpty()) {
                return Optional.empty();
            }
            composition = updated.get();
            toppingCount++;
        }

        return doughCount == 1 && toppingCount >= 1 && toppingCount <= 6
                ? Optional.of(composition)
                : Optional.empty();
    }
}
