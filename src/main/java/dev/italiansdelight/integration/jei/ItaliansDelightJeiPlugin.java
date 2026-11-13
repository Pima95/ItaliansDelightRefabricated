package dev.italiansdelight.integration.jei;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.client.gui.CheeseVatScreen;
import dev.italiansdelight.client.gui.PizzaOvenScreen;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
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

    /**
     * The real Minecraft items are shared between flavors, but JEI must treat
     * their saved quarter ingredients as distinct subtypes. Do not use the
     * complete component record as the UID: sequences, render seeds, and
     * original ingredient sources differ between otherwise identical pizzas.
     */
    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.RAW_PIZZA,
                (stack, context) -> PizzaJeiIngredientSamples.wholeSubtype(stack));
        registration.registerSubtypeInterpreter(ModItems.COOKED_PIZZA,
                (stack, context) -> PizzaJeiIngredientSamples.wholeSubtype(stack));
        registration.registerSubtypeInterpreter(ModItems.PIZZA_SLICE,
                (stack, context) -> PizzaJeiIngredientSamples.sliceSubtype(stack));
    }

    /**
     * Like the pasta, every named pizza AND each named slice gets its own
     * independently searchable slot in JEI's item sidebar, with the approved
     * texture and the same edible component data as the playable item.
     */
    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(PizzaJeiIngredientSamples.createAll());
    }

    @Override
    public void registerCategories(
        IRecipeCategoryRegistration registration
    ) {
        registration.addRecipeCategories(
            // Pizza Oven first, mirroring the Creative workstation section.
            new PizzaBakingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new CheeseVatRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new CheeseAgingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new HangingAgingDryingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new CrushingTubRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new SaltEvaporationRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new PizzaCraftingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new PizzaCuttingRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            ),
            new PizzaWorldRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
            )
        );
    }

    @Override
    public void registerRecipes(
        IRecipeRegistration registration
    ) {
        // Native JEI Information tabs: seven plant families, covering their linked
        // fruits/saplings/leaves or wild/cultivated forms (as in the video).
        OrchardPlantJeiInfo.register(registration);
        DecorativePoleJeiInfo.register(registration);
        ModFluidJeiInfo.register(registration);

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

        registration.addRecipes(
            SaltEvaporationJeiRecipeTypes.SALT_EVAPORATION,
            java.util.List.of(
                SaltEvaporationJeiRecipe.createDefault()
            )
        );

        registration.addRecipes(
            CrushingTubJeiRecipeTypes.CRUSHING_TUB,
            CrushingTubJeiRecipe.createExamples()
        );

        // Illustrative valid configurations for the dynamic crafting recipe.
        // These do NOT introduce five distinct gameplay recipes.
        registration.addRecipes(
            PizzaCraftingJeiRecipeTypes.PIZZA_CRAFTING_EXAMPLES,
            PizzaCraftingJeiRecipes.createExamples()
        );

        // All 18 named Pizza Oven products; vanilla smelting/smoking remain separate.
        registration.addRecipes(
            PizzaBakingJeiRecipeTypes.PIZZA_BAKING,
            PizzaBakingJeiRecipes.createExamples()
        );

        // Data-aware Cutting Board: each saved quarter becomes its own slice.
        registration.addRecipes(
            PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING,
            PizzaCuttingJeiRecipes.createExamples()
        );

        // Interactive in-world pizza handbook: placing/stretched dough,
        // rolling pin, quarters, toppings and collecting the formed pizza.
        // These are informational pages, not additional crafting recipes.
        registration.addRecipes(
            PizzaWorldJeiRecipeTypes.WORLD_PREPARATION,
            PizzaWorldJeiRecipes.createExamples()
        );
    }

    @Override
    public void registerRecipeCatalysts(
        IRecipeCatalystRegistration registration
    ) {
        // The Pizza Oven is the first workstation in Creative and JEI.
        registration.addCraftingStation(
            PizzaBakingJeiRecipeTypes.PIZZA_BAKING,
            new ItemStack(ModBlocks.PIZZA_OVEN)
        );

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

        // Every Crushing Tub variant (including Minecraft 26.3 Poplar) handles
        // the same recipes. They must all open the JEI category via Uses (U).
        for (net.minecraft.world.level.block.Block tub : new net.minecraft.world.level.block.Block[] {
            ModBlocks.CRUSHING_TUB,
            ModBlocks.SPRUCE_CRUSHING_TUB,
            ModBlocks.BIRCH_CRUSHING_TUB,
            ModBlocks.JUNGLE_CRUSHING_TUB,
            ModBlocks.ACACIA_CRUSHING_TUB,
            ModBlocks.DARK_OAK_CRUSHING_TUB,
            ModBlocks.MANGROVE_CRUSHING_TUB,
            ModBlocks.CHERRY_CRUSHING_TUB,
            ModBlocks.PALE_OAK_CRUSHING_TUB,
            ModBlocks.POPLAR_CRUSHING_TUB,
            ModBlocks.BAMBOO_CRUSHING_TUB,
            ModBlocks.CRIMSON_CRUSHING_TUB,
            ModBlocks.WARPED_CRUSHING_TUB,
            ModBlocks.OLIVE_CRUSHING_TUB,
            ModBlocks.ALMOND_CRUSHING_TUB,
            ModBlocks.FRUIT_CHERRY_CRUSHING_TUB
        }) {
            registration.addCraftingStation(
                CrushingTubJeiRecipeTypes.CRUSHING_TUB,
                new ItemStack(tub)
            );
        }

        registration.addCraftingStation(
            SaltEvaporationJeiRecipeTypes.SALT_EVAPORATION,
            new ItemStack(net.minecraft.world.level.block.Blocks.CAULDRON)
        );

        registration.addCraftingStation(
            PizzaCraftingJeiRecipeTypes.PIZZA_CRAFTING_EXAMPLES,
            new ItemStack(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE)
        );

        registration.addCraftingStation(
            PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING,
            new ItemStack(vectorwing.farmersdelight.common.registry.ModItems.CUTTING_BOARD.get())
        );

        registration.addCraftingStation(
            PizzaWorldJeiRecipeTypes.WORLD_PREPARATION,
            new ItemStack(ModItems.ROLLING_PIN)
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

        // Vanilla-layout arrow in our dedicated pizza furnace screen.
        registration.addRecipeClickArea(
            PizzaOvenScreen.class,
            79, 34, 24, 16,
            PizzaBakingJeiRecipeTypes.PIZZA_BAKING
        );
    }
}