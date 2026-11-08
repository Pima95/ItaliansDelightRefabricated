package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

/** JEI examples match the three live CrushingTubRecipes (one stomp each). */
public record CrushingTubJeiRecipe(ItemStack ingredient, Fluid fluid, long amount, ItemStack byproduct) {
    public static List<CrushingTubJeiRecipe> createExamples() {
        return List.of(
            new CrushingTubJeiRecipe(new ItemStack(ModItems.OLIVES),
                ModFluids.OLIVE_OIL_STILL, 250, ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.APPLE),
                ModFluids.APPLE_JUICE_STILL, 250, new ItemStack(ModItems.APPLE_SEEDS)),
            new CrushingTubJeiRecipe(new ItemStack(Items.SUGAR_CANE),
                Fluids.WATER, 250, new ItemStack(Items.SUGAR, 2))
        );
    }
}
