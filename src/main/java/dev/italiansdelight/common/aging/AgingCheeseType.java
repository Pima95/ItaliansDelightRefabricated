package dev.italiansdelight.common.aging;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Whole cheeses supported on surfaces and shelves.
 *
 * The actual transformation and duration remain data-driven through
 * italiansdelight:cheese_aging recipes.
 */
public enum AgingCheeseType implements StringRepresentable {
    PARMIGIANO_REGGIANO("parmigiano_reggiano"),
    PECORINO_ROMANO("pecorino_romano"),
    GORGONZOLA("gorgonzola"),
    PROVOLONE("provolone"),
    // Append new types: the existing rack saves the first four as ordinals.
    SCAMORZA("scamorza"),
    SMOKED_SCAMORZA("smoked_scamorza");

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
            case SCAMORZA -> ModItems.FRESH_SCAMORZA;
            case SMOKED_SCAMORZA -> ModItems.SMOKED_SCAMORZA;
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
            case SCAMORZA -> ModItems.SCAMORZA;
            case SMOKED_SCAMORZA -> ModItems.SMOKED_SCAMORZA;
        };
    }

    public ItemStack freshStack() {
        return new ItemStack(freshItem());
    }

    /** Scamorza dries on the hook; these supports only display it. */
    public boolean agesOnSurface() {
        return this != SCAMORZA && this != SMOKED_SCAMORZA;
    }

    public boolean fitsInRack() {
        return this == PARMIGIANO_REGGIANO
            || this == PECORINO_ROMANO
            || this == GORGONZOLA;
    }

    public ItemStack matureStack() {
        return new ItemStack(matureItem());
    }

    public boolean matches(ItemStack stack) {
        return stack.is(freshItem())
            || stack.is(matureItem());
    }

    public static AgingCheeseType fromStack(ItemStack stack) {
        for (AgingCheeseType type : values()) {
            if (type.matches(stack)) {
                return type;
            }
        }

        return null;
    }
}
