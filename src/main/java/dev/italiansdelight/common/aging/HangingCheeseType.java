package dev.italiansdelight.common.aging;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

/**
 * Cheese states that can physically hang from a Cheese Hook.
 *
 * Fresh variants process while attached. Mature variants can also be attached
 * again as decoration, but do not run a timer.
 */
public enum HangingCheeseType implements StringRepresentable {
    EMPTY("empty"),
    FRESH_SCAMORZA("fresh_scamorza"),
    SCAMORZA("scamorza"),
    FRESH_PROVOLONE("fresh_provolone"),
    PROVOLONE("provolone");

    private final String serializedName;

    HangingCheeseType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isProcessing() {
        return this == FRESH_SCAMORZA
            || this == FRESH_PROVOLONE;
    }

    public Process process() {
        return switch (this) {
            case FRESH_SCAMORZA -> Process.DRYING;
            case FRESH_PROVOLONE -> Process.AGING;
            default -> Process.NONE;
        };
    }

    public HangingCheeseType matureVersion() {
        return switch (this) {
            case FRESH_SCAMORZA -> SCAMORZA;
            case FRESH_PROVOLONE -> PROVOLONE;
            default -> this;
        };
    }

    public ItemStack stack() {
        return switch (this) {
            case FRESH_SCAMORZA ->
                new ItemStack(ModItems.FRESH_SCAMORZA);
            case SCAMORZA ->
                new ItemStack(ModItems.SCAMORZA);
            case FRESH_PROVOLONE ->
                new ItemStack(ModItems.FRESH_PROVOLONE);
            case PROVOLONE ->
                new ItemStack(ModItems.PROVOLONE);
            case EMPTY ->
                ItemStack.EMPTY;
        };
    }

    public static HangingCheeseType fromStack(
        ItemStack stack
    ) {
        if (stack.is(ModItems.FRESH_SCAMORZA)) {
            return FRESH_SCAMORZA;
        }

        if (stack.is(ModItems.SCAMORZA)) {
            return SCAMORZA;
        }

        if (stack.is(ModItems.FRESH_PROVOLONE)) {
            return FRESH_PROVOLONE;
        }

        if (stack.is(ModItems.PROVOLONE)) {
            return PROVOLONE;
        }

        return EMPTY;
    }

    public enum Process {
        NONE,
        AGING,
        DRYING
    }
}
