package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class SecondiJeiInfo {
    private SecondiJeiInfo() {}

    public static void register(IRecipeRegistration registration) {
        add(registration, new ItemStack(ModBlocks.POLLO_ALLA_CACCIATORA), new ItemStack(ModItems.POLLO_ALLA_CACCIATORA_SERVING), true);
        add(registration, new ItemStack(ModBlocks.POLLO_ALLA_DIAVOLA), new ItemStack(ModItems.POLLO_ALLA_DIAVOLA_SERVING), true);
        add(registration, new ItemStack(ModBlocks.FRITTATA_DI_PATATE), new ItemStack(ModItems.FRITTATA_DI_PATATE_SLICE), false);
        add(registration, new ItemStack(ModBlocks.FRITTATA_AL_FORMAGGIO), new ItemStack(ModItems.FRITTATA_AL_FORMAGGIO_SLICE), false);
        add(registration, new ItemStack(ModBlocks.FRITTATA_CON_PANCETTA), new ItemStack(ModItems.FRITTATA_CON_PANCETTA_SLICE), false);
        addBoard(registration, new ItemStack(ModBlocks.COLD_CUTS_BOARD), new ItemStack(ModItems.COLD_CUTS_BOARD_SERVING));
        addBoard(registration, new ItemStack(ModBlocks.CHEESE_BOARD), new ItemStack(ModItems.CHEESE_BOARD_SERVING));
        addBoard(registration, new ItemStack(ModBlocks.MIXED_BOARD), new ItemStack(ModItems.MIXED_BOARD_SERVING));
    }

    private static void addBoard(IRecipeRegistration registration, ItemStack full, ItemStack portion) {
        registration.addItemStackInfo(List.of(full, portion),
            Component.translatable("jei.italiansdelight.antipasti.board", full.getHoverName(), portion.getHoverName()));
    }

    private static void add(IRecipeRegistration registration, ItemStack full, ItemStack portion, boolean bowl) {
        registration.addItemStackInfo(List.of(full, portion),
            Component.translatable("jei.italiansdelight.secondi." + (bowl ? "feast" : "frittata"),
                full.getHoverName(), portion.getHoverName()));
    }
}
