package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModFluids;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Actual native JEI fluid recipes for the Rustic brewing-barrel cycle.
 * A culture can optionally modify quality; it is never consumed.
 */
public final class BrewingBarrelJeiRecipeCategory
        extends AbstractRecipeCategory<BrewingBarrelJeiGuide> {
    private static final int WIDTH = 170;
    private static final int HEIGHT = 112;
    private static final long MAX_BATCH = FluidConstants.BUCKET * 8;

    public BrewingBarrelJeiRecipeCategory(IGuiHelper helper) {
        super(BrewingBarrelJeiRecipeTypes.BARREL_GUI_GUIDE,
            Component.translatable("gui.italiansdelight.jei.brewing_barrel.title"),
            helper.createDrawableItemLike(ModBlocks.BREWING_BARREL),
            WIDTH, HEIGHT);
    }

    @Override public Identifier getIdentifier(BrewingBarrelJeiGuide guide) {
        return Identifier.fromNamespaceAndPath("italiansdelight",
            "brewing_barrel/" + guide.id());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
            BrewingBarrelJeiGuide recipe, IFocusGroup focuses) {
        // The actual game handles fluid amounts in 250/1000 mB vessels;
        // the JEI category shows the full possible 8,000 mB cycle.
        builder.addInputSlot(29, 9)
            .setFluidRenderer(MAX_BATCH, false, 16, 32)
            .add(recipe.isWhite() ? ModFluids.WHITE_GRAPE_JUICE
                                  : ModFluids.GRAPE_JUICE, MAX_BATCH);
        builder.addOutputSlot(125, 9)
            .setFluidRenderer(MAX_BATCH, false, 16, 32)
            .add(ModFluids.WINE, MAX_BATCH);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
            BrewingBarrelJeiGuide recipe, IFocusGroup focuses) {
        builder.addText(Component.translatable(
                "gui.italiansdelight.jei.brewing_barrel.slots"),
                WIDTH - 12, 45).setPosition(6, 49);
        builder.addText(Component.translatable(
                "gui.italiansdelight.jei.brewing_barrel.details")
                    .withStyle(ChatFormatting.DARK_GRAY),
                WIDTH - 12, 28).setPosition(6, 85);
    }
}
