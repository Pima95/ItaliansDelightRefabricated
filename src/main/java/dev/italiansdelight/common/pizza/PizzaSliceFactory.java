package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.pizza.PizzaToppingType.NutritionCategory;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.FoodValues;

/**
 * One edible slice for exactly one physical quarter of a baked pizza.
 *
 * <p>PizzaItemData never becomes edible. Slice hunger/saturation are computed
 * from canonical ingredient types present on that same quarter. Each type
 * contributes once; each category may contribute at most twice (two different
 * ingredients). Nothing on the other three quarters affects this slice.
 * Slices with at least 3 hunger grant the short Farmer's Delight Nourishment
 * effect, independently of their recognized or dynamic pizza name.
 */
public final class PizzaSliceFactory {
    // A slice restores at most six hunger points, below a full Farmer's Delight meal.
    // Bianca, Rossa and other minimally topped quarters must not grant Nourishment.
    private static final int NOURISHMENT_MIN_HUNGER = 3;

    private PizzaSliceFactory() {
    }

    public static ItemStack create(PizzaItemData pizza, int quarter) {
        PizzaSliceData slice = PizzaSliceData.fromPizza(pizza, quarter);
        ItemStack stack = new ItemStack(ModItems.PIZZA_SLICE);
        stack.set(ModDataComponents.PIZZA_SLICE_CONTENTS, slice);
        PizzaDisplayNames.nameSliceStack(stack, slice);
        FoodProperties food = foodFor(slice);
        stack.set(DataComponents.FOOD, food);
        // Same official Farmer's Delight consumable as our prepared pasta dishes.
        // A separate effect is added only to substantial, freshly created slices.
        if (food.nutrition() >= NOURISHMENT_MIN_HUNGER) {
            stack.set(DataComponents.CONSUMABLE,
                    FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION);
        }
        return stack;
    }

    /**
     * Repair slices saved before the approved nutrition rebalance.
     *
     * The saved quarter data is authoritative, but old ItemStacks may still
     * have older FOOD/CONSUMABLE components. Refresh them only when different;
     * never replace the stack, its original toppings, name, count or model.
     * Calling this on every player inventory tick is safe and idempotent.
     *
     * @return true only when an obsolete component was updated.
     */
    public static boolean refreshSavedSlice(ItemStack stack) {
        if (!stack.is(ModItems.PIZZA_SLICE)) {
            return false;
        }
        PizzaSliceData data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
        if (data == null) {
            return false;
        }

        boolean changed = false;
        FoodProperties expectedFood = foodFor(data);
        if (!expectedFood.equals(stack.get(DataComponents.FOOD))) {
            stack.set(DataComponents.FOOD, expectedFood);
            changed = true;
        }

        var nourishment = FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION;
        var currentConsumable = stack.get(DataComponents.CONSUMABLE);
        if (expectedFood.nutrition() >= NOURISHMENT_MIN_HUNGER) {
            if (!nourishment.equals(currentConsumable)) {
                stack.set(DataComponents.CONSUMABLE, nourishment);
                changed = true;
            }
        } else if (nourishment.equals(currentConsumable)) {
            // An old slice may still carry Nourishment despite no longer
            // reaching the hunger threshold. Restore the default vanilla
            // edible-item consumable, rather than deleting eating behavior.
            stack.remove(DataComponents.CONSUMABLE);
            changed = true;
        }
        return changed;
    }

    public static FoodProperties foodFor(PizzaSliceData slice) {
        // The user-approved balance (21 October 2026): use the existing
        // bonuses per category, but count up to TWO DIFFERENT canonical
        // ingredients of the same category on THIS physical quarter.
        // Raw/cooked equivalents share an ID, and repeated applications
        // must never create a second food bonus for one ingredient.
        EnumMap<NutritionCategory, Integer> counts = new EnumMap<>(NutritionCategory.class);
        Set<Identifier> seenTypes = new HashSet<>();
        for (PizzaSliceData.Topping topping : slice.toppings()) {
            if (!seenTypes.add(topping.canonicalId())) {
                continue;
            }
            PizzaToppingCatalog.findType(topping.canonicalId()).ifPresent(type ->
                    counts.merge(type.nutritionCategory(), 1, Integer::sum));
        }
        // Older pizzas may carry the pre-catalogue whole mozzarella flag.
        // Count it as dairy once, unless a modern mozzarella slice already
        // represents that same cheese. The category cap still applies.
        if (slice.legacyMozzarella()
                && !seenTypes.contains(Identifier.parse(
                        "italiansdelight:pizza_toppings/mozzarella_slice"))) {
            counts.merge(NutritionCategory.DAIRY, 1, Integer::sum);
        }

        int hunger = 1;          // Crust: +1 hunger
        float saturation = 1.0f; // Crust: +1 saturation point
        for (NutritionCategory category : NutritionCategory.values()) {
            int contributions = Math.min(2, counts.getOrDefault(category, 0));
            for (int i = 0; i < contributions; i++) {
                switch (category) {
                    case DAIRY -> { hunger += 1; saturation += 1.5f; }
                    case ANIMAL_PROTEIN -> { hunger += 2; saturation += 2.5f; }
                    case VEGETABLE -> { hunger += 1; saturation += 0.5f; }
                    case SAUCES -> { hunger += 1; saturation += 0.5f; }
                    case HERB -> { /* herbs only add flavor */ }
                    case OILS_FATS -> { saturation += 1.0f; }
                }
            }
        }
        hunger = Math.min(6, hunger);
        saturation = Math.min(6.0f, saturation);

        // In vanilla Minecraft, FoodProperties uses a saturation MODIFIER,
        // not saturation points: saturationAdded = nutrition * modifier * 2.
        return new FoodProperties.Builder()
                .nutrition(hunger)
                .saturationModifier(saturation / (2.0f * hunger))
                .build();
    }
}
