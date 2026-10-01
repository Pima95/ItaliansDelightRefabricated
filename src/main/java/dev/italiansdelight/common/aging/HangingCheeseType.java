package dev.italiansdelight.common.aging;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

/**
 * Products that can physically hang from the existing Cheese Hook block.
 *
 * The class name and serialized block-state property are intentionally kept for
 * world compatibility with 0.2.0. The hook itself is now presented to players
 * as a generic Hanging Hook and can process both cheeses and cured meats.
 */
public enum HangingCheeseType implements StringRepresentable {
    EMPTY("empty"),

    FRESH_SCAMORZA("fresh_scamorza"),
    SCAMORZA("scamorza"),
    SMOKED_SCAMORZA("smoked_scamorza"),
    FRESH_PROVOLONE("fresh_provolone"),
    PROVOLONE("provolone"),

    SALTED_HAM("salted_ham"),
    PROSCIUTTO_CRUDO("prosciutto_crudo"),
    RAW_SALAME("raw_salame"),
    SALAME("salame"),
    PREPARED_PANCETTA("prepared_pancetta"),
    PANCETTA("pancetta"),
    PREPARED_GUANCIALE("prepared_guanciale"),
    GUANCIALE("guanciale"),
    PREPARED_BRESAOLA("prepared_bresaola"),
    BRESAOLA("bresaola"),
    PREPARED_COPPA("prepared_coppa"),
    COPPA("coppa"),
    SMOKED_PREPARED_SPECK("smoked_prepared_speck"),
    SPECK("speck");

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
        return switch (this) {
            case FRESH_SCAMORZA,
                 FRESH_PROVOLONE,
                 SALTED_HAM,
                 RAW_SALAME,
                 PREPARED_PANCETTA,
                 PREPARED_GUANCIALE,
                 PREPARED_BRESAOLA,
                 PREPARED_COPPA,
                 SMOKED_PREPARED_SPECK -> true;
            default -> false;
        };
    }

    public Process process() {
        return switch (this) {
            case FRESH_PROVOLONE -> Process.AGING;
            case FRESH_SCAMORZA,
                 SALTED_HAM,
                 RAW_SALAME,
                 PREPARED_PANCETTA,
                 PREPARED_GUANCIALE,
                 PREPARED_BRESAOLA,
                 PREPARED_COPPA,
                 SMOKED_PREPARED_SPECK -> Process.DRYING;
            default -> Process.NONE;
        };
    }

    public HangingCheeseType matureVersion() {
        return switch (this) {
            case FRESH_SCAMORZA -> SCAMORZA;
            case FRESH_PROVOLONE -> PROVOLONE;
            case SALTED_HAM -> PROSCIUTTO_CRUDO;
            case RAW_SALAME -> SALAME;
            case PREPARED_PANCETTA -> PANCETTA;
            case PREPARED_GUANCIALE -> GUANCIALE;
            case PREPARED_BRESAOLA -> BRESAOLA;
            case PREPARED_COPPA -> COPPA;
            case SMOKED_PREPARED_SPECK -> SPECK;
            default -> this;
        };
    }

    public ItemStack stack() {
        return switch (this) {
            case FRESH_SCAMORZA -> new ItemStack(ModItems.FRESH_SCAMORZA);
            case SCAMORZA -> new ItemStack(ModItems.SCAMORZA);
            case SMOKED_SCAMORZA -> new ItemStack(ModItems.SMOKED_SCAMORZA);
            case FRESH_PROVOLONE -> new ItemStack(ModItems.FRESH_PROVOLONE);
            case PROVOLONE -> new ItemStack(ModItems.PROVOLONE);
            case SALTED_HAM -> new ItemStack(ModItems.SALTED_HAM);
            case PROSCIUTTO_CRUDO -> new ItemStack(ModItems.PROSCIUTTO_CRUDO);
            case RAW_SALAME -> new ItemStack(ModItems.RAW_SALAME);
            case SALAME -> new ItemStack(ModItems.SALAME);
            case PREPARED_PANCETTA -> new ItemStack(ModItems.PREPARED_PANCETTA);
            case PANCETTA -> new ItemStack(ModItems.PANCETTA);
            case PREPARED_GUANCIALE -> new ItemStack(ModItems.PREPARED_GUANCIALE);
            case GUANCIALE -> new ItemStack(ModItems.GUANCIALE);
            case PREPARED_BRESAOLA -> new ItemStack(ModItems.PREPARED_BRESAOLA);
            case BRESAOLA -> new ItemStack(ModItems.BRESAOLA);
            case PREPARED_COPPA -> new ItemStack(ModItems.PREPARED_COPPA);
            case COPPA -> new ItemStack(ModItems.COPPA);
            case SMOKED_PREPARED_SPECK -> new ItemStack(ModItems.SMOKED_PREPARED_SPECK);
            case SPECK -> new ItemStack(ModItems.SPECK);
            case EMPTY -> ItemStack.EMPTY;
        };
    }

    public static HangingCheeseType fromStack(ItemStack stack) {
        if (stack.getItem() == ModItems.FRESH_SCAMORZA) return FRESH_SCAMORZA;
        if (stack.getItem() == ModItems.SCAMORZA) return SCAMORZA;
        if (stack.getItem() == ModItems.SMOKED_SCAMORZA) return SMOKED_SCAMORZA;
        if (stack.getItem() == ModItems.FRESH_PROVOLONE) return FRESH_PROVOLONE;
        if (stack.getItem() == ModItems.PROVOLONE) return PROVOLONE;

        if (stack.getItem() == ModItems.SALTED_HAM) return SALTED_HAM;
        if (stack.getItem() == ModItems.PROSCIUTTO_CRUDO) return PROSCIUTTO_CRUDO;
        if (stack.getItem() == ModItems.RAW_SALAME) return RAW_SALAME;
        if (stack.getItem() == ModItems.SALAME) return SALAME;
        if (stack.getItem() == ModItems.PREPARED_PANCETTA) return PREPARED_PANCETTA;
        if (stack.getItem() == ModItems.PANCETTA) return PANCETTA;
        if (stack.getItem() == ModItems.PREPARED_GUANCIALE) return PREPARED_GUANCIALE;
        if (stack.getItem() == ModItems.GUANCIALE) return GUANCIALE;
        if (stack.getItem() == ModItems.PREPARED_BRESAOLA) return PREPARED_BRESAOLA;
        if (stack.getItem() == ModItems.BRESAOLA) return BRESAOLA;
        if (stack.getItem() == ModItems.PREPARED_COPPA) return PREPARED_COPPA;
        if (stack.getItem() == ModItems.COPPA) return COPPA;
        if (stack.getItem() == ModItems.SMOKED_PREPARED_SPECK) return SMOKED_PREPARED_SPECK;
        if (stack.getItem() == ModItems.SPECK) return SPECK;

        return EMPTY;
    }

    public enum Process {
        NONE,
        AGING,
        DRYING
    }
}
