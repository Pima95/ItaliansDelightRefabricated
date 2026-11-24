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
 * Honey uses the existing registered storage-only ModFluids.HONEY in JEI.
 * The tub retains its legacy minecraft:honey storage ID for save compatibility.
 */
public record CrushingTubJeiRecipe(ItemStack ingredient, Fluid fluid, long amount,
                                    ItemStack byproduct) {
    public CrushingTubJeiRecipe {
        if (ingredient.isEmpty() || amount <= 0 || fluid == Fluids.EMPTY)
            throw new IllegalArgumentException("Invalid Crushing Tub JEI recipe");
    }

    public static List<CrushingTubJeiRecipe> createExamples() {
        return List.of(
            new CrushingTubJeiRecipe(new ItemStack(ModItems.OLIVES),
                ModFluids.OLIVE_OIL_STILL, 250, new ItemStack(ModItems.OLIVE_SEED)),
            new CrushingTubJeiRecipe(new ItemStack(ModItems.GRAPES),
                ModFluids.GRAPE_JUICE, 250,
                new ItemStack(dev.italiansdelight.common.registry.ModBlocks.GRAPE_SEEDS)),
            new CrushingTubJeiRecipe(new ItemStack(ModItems.WHITE_GRAPES),
                ModFluids.WHITE_GRAPE_JUICE, 250,
                new ItemStack(dev.italiansdelight.common.registry.ModBlocks.WHITE_GRAPE_SEEDS)),
            new CrushingTubJeiRecipe(new ItemStack(ModItems.CHERRIES),
                ModFluids.CHERRY_JUICE, 250, ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.APPLE),
                ModFluids.APPLE_JUICE_STILL, 250, new ItemStack(ModItems.APPLE_SEEDS)),
            new CrushingTubJeiRecipe(new ItemStack(Items.SUGAR_CANE),
                Fluids.WATER, 250, new ItemStack(Items.SUGAR, 2)),
            new CrushingTubJeiRecipe(new ItemStack(Items.HONEYCOMB),
                ModFluids.HONEY, 250, ItemStack.EMPTY),
            new CrushingTubJeiRecipe(new ItemStack(Items.GOLDEN_APPLE),
                ModFluids.GOLDEN_APPLE_JUICE, 100, new ItemStack(ModItems.APPLE_SEEDS)),
            new CrushingTubJeiRecipe(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
                ModFluids.GOLDEN_APPLE_JUICE, 1000, new ItemStack(ModItems.APPLE_SEEDS))
        );
    }
}
