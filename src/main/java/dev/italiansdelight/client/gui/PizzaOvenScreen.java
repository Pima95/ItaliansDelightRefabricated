package dev.italiansdelight.client.gui;

import dev.italiansdelight.common.block.entity.container.PizzaOvenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Vanilla furnace GUI art and coordinates, WITHOUT its recipe book.
 *
 * <p>Unlike FurnaceScreen, this class extends AbstractContainerScreen
 * directly: it creates no RecipeBookComponent, recipe-book button, ghost
 * recipe overlays, recipe filtering, or recipe hotkeys. All three slot
 * positions, inventory layout, fuel flame and cook arrow stay vanilla.
 *
 * <p>No custom PNG is needed: the original Minecraft 26.2 furnace texture
 * and the furnace's original GUI atlas sprites are drawn as-is.
 */
public final class PizzaOvenScreen extends AbstractContainerScreen<PizzaOvenMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft", "textures/gui/container/furnace.png");
    private static final ResourceLocation FLAME =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft", "container/furnace/lit_progress");
    private static final ResourceLocation ARROW =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft", "container/furnace/burn_progress");

    public PizzaOvenScreen(PizzaOvenMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        // Exact 176x166 vanilla furnace background; inventory and slots
        // are provided by the common container superclass.
        graphics.blit(
                BACKGROUND,
                leftPos, topPos, 0, 0,
                176, 166, 256, 256);

        // Vanilla furnace flame sprite is 14x14, filled upwards.
        int flameHeight = menu.getBurnProgressionScaled();
        if (flameHeight > 0) {
            graphics.blitSprite(
                    FLAME,
                    14, 14, 0, 14 - flameHeight,
                    leftPos + 56, topPos + 36 + 14 - flameHeight,
                    14, flameHeight);
        }

        // Vanilla furnace progress arrow is 24x16, filled left-to-right.
        int progress = menu.getCookProgressionScaled();
        if (progress > 0) {
            graphics.blitSprite(
                    ARROW,
                    24, 16, 0, 0,
                    leftPos + 79, topPos + 34,
                    progress, 16);
        }
    }
}
