package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.client.gui.CheeseVatScreen;
import dev.italiansdelight.common.registry.ModBlocks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * JEI plugin for the mod. Registers categories, recipes, catalysts, and Cheese Vat GUI integration.
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
            ),
            new CheeseAgingRecipeCategory(
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

        CheeseAgingJeiRecipes agingRecipes =
            new CheeseAgingJeiRecipes();

        var combinedAgingRecipes =
            agingRecipes.getRecipes();

        ItaliansDelight.LOGGER.info(
            "[Italian's Delight / JEI] Ricette Aging/Drying sincronizzate: {}",
            combinedAgingRecipes.size()
        );

        registration.addRecipes(
            CheeseAgingJeiRecipeTypes.CHEESE_AGING,
            combinedAgingRecipes
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

            // GUI progress arrow
            76,
            25,
            24,
            17,

            CheeseVatJeiRecipeTypes.CHEESE_VAT
        );
    }
}