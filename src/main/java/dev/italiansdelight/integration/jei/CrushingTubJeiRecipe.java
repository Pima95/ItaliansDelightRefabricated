package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

/**
 * One stomp in the Crushing Tub.
 * Vanilla honey has no registered Fluid, only a honey bottle; for that
 * recipe fluid is Fluids.EMPTY and bottlePreview is the EXTRACTED result.
 * No bottle materializes on landing: the 250 mB stays in the tub.
 */
public record CrushingTubJeiRecipe(ItemStack ingredient, Fluid fluid, long amount,
                                    ItemStack byproduct, ItemStack bottlePreview) {
    public CrushingTubJeiRecipe {
        if (ingredient.isEmpty() || amount <= 0 || (fluid == Fluids.EMPTY) == bottlePreview.isEmpty())
            throw new IllegalArgumentException("Invalid Crushing Tub JEI recipe");
    }

    public static List<CrushingTubJeiRecipe> createExamples() {
        return List.of(
            new CrushingTubJeiRecipe(new ItemStack(ModItems.OLIVES),
                ModFluids.OLIVE_OIL_STILL, 250, new ItemStack(ModItems.OLIVE_SEED), ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.APPLE),
                ModFluids.APPLE_JUICE_STILL, 250, new ItemStack(ModItems.APPLE_SEEDS), ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.SUGAR_CANE),
                Fluids.WATER, 250, new ItemStack(Items.SUGAR, 2), ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.HONEYCOMB),
                Fluids.EMPTY, 250, ItemStack.EMPTY, new ItemStack(Items.HONEY_BOTTLE)),
            new CrushingTubJeiRecipe(new ItemStack(Items.GOLDEN_APPLE),
                ModFluids.GOLDEN_APPLE_JUICE, 100, new ItemStack(ModItems.APPLE_SEEDS), ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
                ModFluids.GOLDEN_APPLE_JUICE, 1000, new ItemStack(ModItems.APPLE_SEEDS), ItemStack.EMPTY)
        );
    }
}
