package com.piergiuseppe.italiansdelight.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import com.piergiuseppe.italiansdelight.registry.ModBlocks;

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
        CheeseVatJeiRecipes recipes = new CheeseVatJeiRecipes();

        registration.addRecipes(
            CheeseVatJeiRecipeTypes.CHEESE_VAT,
            recipes.getRecipes()
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
}