package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.RiceRollMedleyBlock;

/** Eight individual ingredients, in the same order as the staged models. */
public final class AntipastiBoardBlock extends RiceRollMedleyBlock {
    private final String[] ingredients;

    public AntipastiBoardBlock(Properties properties, String... ingredients) {
        super(properties);
        if (ingredients.length != 4) throw new IllegalArgumentException("Expected four ingredient types");
        this.ingredients = ingredients.clone();
    }

    @Override
    public ItemStack getServingItem(BlockState state) {
        int remaining = state.getValue(getServingsProperty());
        if (remaining == 0) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.getValue(ModRegistries.id(ingredients[(remaining - 1) / 2])));
    }
}
