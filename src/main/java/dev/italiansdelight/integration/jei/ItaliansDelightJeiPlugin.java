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
            ),
            new HangingAgingDryingRecipeCategory(
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

        var rackAgingRecipes =
            agingRecipes.getRackAgingRecipes();

        var hangingRecipes =
            agingRecipes.getHangingRecipes();

        ItaliansDelight.LOGGER.info(
            "[Italian's Delight / JEI] Ricette Cheese Aging sincronizzate: {}",
            rackAgingRecipes.size()
        );

        ItaliansDelight.LOGGER.info(
            "[Italian's Delight / JEI] Ricette Hanging Aging/Drying sincronizzate: {}",
            hangingRecipes.size()
        );

        registration.addRecipes(
            CheeseAgingJeiRecipeTypes.CHEESE_AGING,
            rackAgingRecipes
        );

        registration.addRecipes(
            CheeseAgingJeiRecipeTypes.HANGING_AGING_DRYING,
            hangingRecipes
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

        registration.addCraftingStation(
            CheeseAgingJeiRecipeTypes.CHEESE_AGING,
            new ItemStack(
                ModBlocks.OAK_CHEESE_AGING_RACK
            )
        );

        registration.addCraftingStation(
            CheeseAgingJeiRecipeTypes.HANGING_AGING_DRYING,
            new ItemStack(ModBlocks.CHEESE_HOOK)
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