package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Fixed starting set of Rustic 1.12.2 stomping recipes.
 *
 * Rustic Recipes.addCrushingTubRecipes() produces 250 mB per OLIVE, APPLE,
 * SUGAR_CANE and other standard fruit. A normal golden apple yields 100 mB
 * and an enchanted golden apple yields 1000 mB (planned separately).
 *
 * Dedicated fruit fluids can be added here once registered. Never silently
 * substitute one liquid for another.
 */
public final class CrushingTubRecipes {
    public static final int STANDARD_AMOUNT = 250;
    private CrushingTubRecipes() {}

    public record Recipe(String fluidId, int amount, ItemStack byproduct) {
        public Recipe {
            if (fluidId == null || fluidId.isBlank() || amount <= 0)
                throw new IllegalArgumentException("Invalid crushing recipe");
        }
    }

    public static Recipe find(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.is(ModItems.OLIVES))
            return new Recipe("italiansdelight:olive_oil", STANDARD_AMOUNT, ItemStack.EMPTY);
        if (stack.is(Items.SUGAR_CANE))
            return new Recipe("minecraft:water", STANDARD_AMOUNT, new ItemStack(Items.SUGAR, 2));
        return null;
    }
}
