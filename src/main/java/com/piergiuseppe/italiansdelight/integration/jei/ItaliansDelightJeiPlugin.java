package com.piergiuseppe.italiansdelight.integration.jei;

import com.piergiuseppe.italiansdelight.ItaliansDelight;
import com.piergiuseppe.italiansdelight.client.gui.CheeseVatScreen;
import com.piergiuseppe.italiansdelight.common.registry.ModBlocks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Plugin JEI della mod. Registra categorie, ricette, catalyst e integrazione con la GUI della Cheese Vat.
 */

@JeiPlugin
public class ItaliansDelightJeiPlugin implements IModPlugin {

    private static final Identifier UID =
        Identifier.fromNamespaceAndPath(
            "italiansdelight",
            "jei_plugin"
        );

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(
        IRecipeCategoryRegistration registration
    ) {
        registration.addRecipeCategories(
            new CheeseVatRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            )
        );
    }

    @Override
    public void registerRecipes(
        IRecipeRegistration registration
    ) {
        CheeseVatJeiRecipes recipes =
            new CheeseVatJeiRecipes();

        var cheeseVatRecipes =
            recipes.getRecipes();

        ItaliansDelight.LOGGER.info(
            "[Italian's Delight / JEI] Ricette Cheese Vat sincronizzate: {}",
            cheeseVatRecipes.size()
        );

        registration.addRecipes(
            CheeseVatJeiRecipeTypes.CHEESE_VAT,
            cheeseVatRecipes
        );
    }

    @Override
    public void registerRecipeCatalysts(
        IRecipeCatalystRegistration registration
    ) {
        registration.addCraftingStation(
            CheeseVatJeiRecipeTypes.CHEESE_VAT,
            new ItemStack(ModBlocks.CHEESE_VAT)
        );
    }

    @Override
    public void registerGuiHandlers(
        IGuiHandlerRegistration registration
    ) {
        registration.addRecipeClickArea(
            CheeseVatScreen.class,

            // Freccia di avanzamento della GUI
            89,
            25,
            24,
            17,

            CheeseVatJeiRecipeTypes.CHEESE_VAT
        );
    }
}