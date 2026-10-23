package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaCreativeSamples;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * One truthful Pizza Oven 20s preview for EACH recognized flavor, not just
 * the original Margherita. They are actual component-bearing stacks, and
 * baking preserves their original four quarters.
 */
public final class PizzaBakingJeiRecipes {
    private PizzaBakingJeiRecipes() {
    }

    public static List<PizzaBakingJeiRecipe> createExamples() {
        int ticks = (PizzaBaking.MAX_PROGRESS
                + PizzaBaking.Oven.PIZZA_OVEN.unitsPerTick() - 1)
                / PizzaBaking.Oven.PIZZA_OVEN.unitsPerTick();
        List<PizzaBakingJeiRecipe> recipes = new ArrayList<>();
        for (var preset : PizzaDisplayNames.allPresets().entrySet()) {
            ItemStack raw = PizzaCreativeSamples.create(preset.getKey(), false);
            ItemStack cooked = PizzaBaking.previewCookedPizza(raw);
            if (cooked.isEmpty()) {
                throw new IllegalStateException("Cannot preview Pizza Oven flavor " + preset.getValue());
            }
            recipes.add(new PizzaBakingJeiRecipe(raw, cooked, ticks));
        }
        return List.copyOf(recipes);
    }
}
