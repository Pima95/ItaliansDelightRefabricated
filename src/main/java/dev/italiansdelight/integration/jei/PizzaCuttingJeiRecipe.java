package dev.italiansdelight.integration.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** One complete pizza produces exactly four physical-quarter slices. */
public record PizzaCuttingJeiRecipe(ItemStack whole, List<ItemStack> slices) {
    public PizzaCuttingJeiRecipe {
        whole = whole.copy();
        if (slices.size() != 4) {
            throw new IllegalArgumentException("Pizza needs four physical slices");
        }
        slices = slices.stream().map(ItemStack::copy).toList();
    }

    @Override public ItemStack whole() { return whole.copy(); }
    @Override public List<ItemStack> slices() {
        return slices.stream().map(ItemStack::copy).toList();
    }
}
