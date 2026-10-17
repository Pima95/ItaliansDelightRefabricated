package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.pizza.PizzaToppingType.NutritionCategory;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.EnumSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/**
 * One edible slice for exactly one physical quarter of a baked pizza.
 *
 * <p>PizzaItemData never becomes edible. Slice hunger/saturation are computed
 * from DISTINCT approved categories on the chosen quarter, not from the
 * number of ingredients or from toppings on any other quarter.
 */
public final class PizzaSliceFactory {
    private PizzaSliceFactory() {
    }

    public static ItemStack create(PizzaItemData pizza, int quarter) {
        PizzaSliceData slice = PizzaSliceData.fromPizza(pizza, quarter);
        ItemStack stack = new ItemStack(ModItems.PIZZA_SLICE);
        stack.set(ModDataComponents.PIZZA_SLICE_CONTENTS, slice);
        stack.set(DataComponents.FOOD, foodFor(slice));
        return stack;
    }

    public static FoodProperties foodFor(PizzaSliceData slice) {
        EnumSet<NutritionCategory> categories = EnumSet.noneOf(NutritionCategory.class);
        for (PizzaSliceData.Topping topping : slice.toppings()) {
            PizzaToppingCatalog.findType(topping.canonicalId())
                    .ifPresent(type -> categories.add(type.nutritionCategory()));
        }
        // The old whole-mozzarella BlockState proof of concept covers
        // every quarter. It counts as dairy once, not as four mozzarellas.
        if (slice.legacyMozzarella()) {
            categories.add(NutritionCategory.DAIRY);
        }

        int hunger = 1;          // Crust: +1 hunger
        float saturation = 1.0f; // Crust: +1 saturation point
        for (NutritionCategory category : categories) {
            switch (category) {
                case DAIRY -> { hunger += 1; saturation += 1.5f; }
                case ANIMAL_PROTEIN -> { hunger += 2; saturation += 2.5f; }
                case VEGETABLE -> { hunger += 1; saturation += 0.5f; }
                case SAUCES -> { hunger += 1; saturation += 0.5f; }
                case HERB -> { /* herbs only add flavor */ }
                case OILS_FATS -> { saturation += 1.0f; }
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
