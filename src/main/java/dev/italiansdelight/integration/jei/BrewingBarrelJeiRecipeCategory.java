package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * JEI documentation of the Rustic-layout barrel GUI.
 * This page contains NO output recipe; juice-to-wine fermentation stays off.
 */
public final class BrewingBarrelJeiRecipeCategory
        extends AbstractRecipeCategory<BrewingBarrelJeiGuide> {
    private static final int WIDTH = 170;
    private static final int HEIGHT = 112;

    public BrewingBarrelJeiRecipeCategory(IGuiHelper helper) {
        super(BrewingBarrelJeiRecipeTypes.BARREL_GUI_GUIDE,
            Component.translatable("gui.italiansdelight.jei.brewing_barrel.title"),
            helper.createDrawableItemLike(ModBlocks.BREWING_BARREL),
            WIDTH, HEIGHT);
    }

    @Override
    public Identifier getIdentifier(BrewingBarrelJeiGuide guide) {
        return Identifier.fromNamespaceAndPath("italiansdelight",
            "brewing_barrel/guide/" + guide.id());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
            BrewingBarrelJeiGuide guide, IFocusGroup focuses) {
        // Illustrative containers only. In particular there is NO wine output
        // slot, because the barrel does not currently make any new wine.
        builder.addInputSlot(9, 9).setStandardSlotBackground()
            .add(new ItemStack(ModItems.GRAPE_JUICE_BOTTLE));
        builder.addInputSlot(31, 9).setStandardSlotBackground()
            .add(new ItemStack(ModItems.WHITE_GRAPE_JUICE_BOTTLE));
        builder.addInputSlot(53, 9).setStandardSlotBackground()
            .add(new ItemStack(Items.GLASS_BOTTLE));
        builder.addInputSlot(75, 9).setStandardSlotBackground()
            .add(new ItemStack(ModItems.WINE_BOTTLE));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
            BrewingBarrelJeiGuide guide, IFocusGroup focuses) {
        builder.addText(Component.translatable(
                "gui.italiansdelight.jei.brewing_barrel.slots"),
                WIDTH - 12, 48)
            .setPosition(6, 36);
        builder.addText(Component.translatable(
                "gui.italiansdelight.jei.brewing_barrel.disabled")
                    .withStyle(ChatFormatting.DARK_GRAY),
                WIDTH - 12, 30)
            .setPosition(6, 85);
    }
}
