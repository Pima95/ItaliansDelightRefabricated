package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.pizza.PizzaBaking;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * Displays an actual data-carrying Margherita for the Pizza Oven only.
 * JEI already registers vanilla Smelting and Smoking with their usual
 * category layout, fuel cycling and timings, so do not duplicate them here.
 */
public final class PizzaBakingJeiRecipes {
    private PizzaBakingJeiRecipes() {
    }

    public static List<PizzaBakingJeiRecipe> createExamples() {
        List<PizzaCraftingJeiRecipe> crafting = PizzaCraftingJeiRecipes.createExamples();
        // The second validated crafting example is a topped Margherita.
        if (crafting.size() < 2) {
            ItaliansDelight.LOGGER.warn(
                    "[Pizza / JEI] Cannot create Pizza Oven example: Margherita sample missing");
            return List.of();
        }

        ItemStack raw = crafting.get(1).result();
        var cooked = PizzaBaking.finish(raw, PizzaBaking.MAX_PROGRESS);
        if (cooked.isEmpty()) {
            ItaliansDelight.LOGGER.warn(
                    "[Pizza / JEI] Cannot create Pizza Oven example: sample could not be cooked");
            return List.of();
        }

        int time = (PizzaBaking.MAX_PROGRESS
                + PizzaBaking.Oven.PIZZA_OVEN.unitsPerTick() - 1)
                / PizzaBaking.Oven.PIZZA_OVEN.unitsPerTick();

        return List.of(new PizzaBakingJeiRecipe(
                raw, cooked.get().cookedPizza(), time));
    }
}
