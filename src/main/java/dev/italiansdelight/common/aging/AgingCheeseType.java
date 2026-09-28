package dev.italiansdelight.common.aging;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Visual/state variants supported by the first surface-aging implementation.
 *
 * The actual transformation and duration remain data-driven through
 * italiansdelight:cheese_aging recipes.
 */
public enum AgingCheeseType implements StringRepresentable {
    PARMIGIANO_REGGIANO("parmigiano_reggiano"),
    PECORINO_ROMANO("pecorino_romano"),
    GORGONZOLA("gorgonzola"),
    PROVOLONE("provolone");

    private final String serializedName;

    AgingCheeseType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public Item freshItem() {
        return switch (this) {
            case PARMIGIANO_REGGIANO -> ModItems.FRESH_PARMIGIANO_REGGIANO;
            case PECORINO_ROMANO -> ModItems.FRESH_PECORINO_ROMANO;
            case GORGONZOLA -> ModItems.FRESH_GORGONZOLA;
            case PROVOLONE -> ModItems.FRESH_PROVOLONE;
        };
    }

    /**
     * Fallback result used only if the corresponding datapack recipe is
     * temporarily unavailable. Normal completion/drop uses the recipe result.
     */
    public Item matureItem() {
        return switch (this) {
            case PARMIGIANO_REGGIANO -> ModItems.PARMIGIANO_REGGIANO;
            case PECORINO_ROMANO -> ModItems.PECORINO_ROMANO;
            case GORGONZOLA -> ModItems.GORGONZOLA;
            case PROVOLONE -> ModItems.PROVOLONE;
        };
    }

    public ItemStack freshStack() {
        return new ItemStack(freshItem());
    }

    public ItemStack matureStack() {
        return new ItemStack(matureItem());
    }
}
