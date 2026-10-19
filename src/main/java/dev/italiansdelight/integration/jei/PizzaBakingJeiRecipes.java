package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * Display one identical, data-carrying Margherita across three baking methods.
 * Using PizzaBaking.finish preserves the real topping component/name in JEI.
 * No cooking recipes or balances are registered by this class.
 */
public final class PizzaBakingJeiRecipes {
    private PizzaBakingJeiRecipes() {
    }

    public static List<PizzaBakingJeiRecipe> createExamples() {
        List<PizzaCraftingJeiRecipe> crafting = PizzaCraftingJeiRecipes.createExamples();
        // The second crafting example is the already validated Margherita.
        if (crafting.size() < 2) {
            ItaliansDelight.LOGGER.warn(
                    "[Pizza / JEI] Cannot create baking guide: Margherita sample missing");
            return List.of();
        }

        ItemStack raw = crafting.get(1).result();
        var result = PizzaBaking.finish(raw, PizzaBaking.MAX_PROGRESS);
        if (result.isEmpty()) {
            ItaliansDelight.LOGGER.warn(
                    "[Pizza / JEI] Cannot create baking guide: sample could not be cooked");
            return List.of();
        }
        ItemStack cooked = result.get().cookedPizza();

        return List.of(
                example(raw, cooked, new ItemStack(ModBlocks.PIZZA_OVEN),
                        PizzaBaking.Oven.PIZZA_OVEN,
                        "gui.italiansdelight.jei.pizza_baking.method.oven"),
                example(raw, cooked, new ItemStack(Blocks.SMOKER),
                        PizzaBaking.Oven.SMOKER,
                        "gui.italiansdelight.jei.pizza_baking.method.smoker"),
                example(raw, cooked, new ItemStack(Blocks.FURNACE),
                        PizzaBaking.Oven.FURNACE,
                        "gui.italiansdelight.jei.pizza_baking.method.furnace")
        );
    }

    private static PizzaBakingJeiRecipe example(
            ItemStack raw, ItemStack cooked, ItemStack station,
            PizzaBaking.Oven oven, String methodKey
    ) {
        int time = (PizzaBaking.MAX_PROGRESS + oven.unitsPerTick() - 1)
                / oven.unitsPerTick();
        return new PizzaBakingJeiRecipe(raw, cooked, station, time, methodKey);
    }
}
