package dev.italiansdelight.client.gui;

import dev.italiansdelight.common.block.entity.BrewingBarrelBlockEntity;
import dev.italiansdelight.common.block.entity.container.BrewingBarrelMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

/**
 * Rustic's original 176x166 brewing-barrel GUI and tank/slot coordinates.
 * Only vessels and existing tank contents work during this phase. Progress
 * is deliberately not drawn: brewing recipes and timers remain disabled.
 */
public final class BrewingBarrelScreen extends AbstractContainerScreen<BrewingBarrelMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "textures/gui/brewing_barrel.png");
    private static final Identifier PURPLE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/grape_juice_still");
    private static final Identifier WHITE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/white_grape_juice_still");
    private static final Identifier WINE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/wine_still");

    public BrewingBarrelScreen(BrewingBarrelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        // Rustic's original top row is occupied by vessel slots, not a title.
        this.titleLabelY = -1000;
    }

    private void tank(GuiGraphicsExtractor graphics, Identifier sprite,
                      int x, int y, int height, int amount, int capacity) {
        if (amount <= 0) return;
        int level = Math.clamp((int) Math.ceil(amount * height / (double) capacity), 1, height);
        int absoluteX = leftPos + x, absoluteY = topPos + y;
        graphics.enableScissor(absoluteX, absoluteY + height - level,
                               absoluteX + 16, absoluteY + height);
        try {
            // Fluid GUI atlas sprites retain the existing animated Rustic art.
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite,
                                absoluteX, absoluteY, 16, height);
        } finally {
            graphics.disableScissor();
        }
    }

    @Override public void extractBackground(
            GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
            leftPos, topPos, 0, 0, 176, 166, 256, 256);

        tank(graphics, menu.hasWhiteGrapeJuice() ? WHITE : PURPLE,
            62, 27, 32, menu.getInputAmount(), BrewingBarrelBlockEntity.CAPACITY);
        tank(graphics, WINE, 116, 27, 32, menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tank(graphics, WINE, 26, 35, 16, menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY);

        // Original Rustic icons for empty container slots, from the existing PNG.
        if (!menu.getSlot(BrewingBarrelBlockEntity.INPUT_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 62, topPos + 7, 176, 54, 16, 16, 256, 256);
        if (!menu.getSlot(BrewingBarrelBlockEntity.OUTPUT_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 116, topPos + 7, 176, 70, 16, 16, 256, 256);
        if (!menu.getSlot(BrewingBarrelBlockEntity.CULTURE_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 26, topPos + 15, 176, 70, 16, 16, 256, 256);
    }

    private void tankTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                             int x, int y, int w, int h, String key,
                             int amount, int capacity) {
        if (mouseX < leftPos+x || mouseX >= leftPos+x+w
                || mouseY < topPos+y || mouseY >= topPos+y+h) return;
        graphics.setComponentTooltipForNextFrame(font, List.of(
            Component.translatable(key).withStyle(ChatFormatting.WHITE),
            Component.literal(amount + " / " + capacity + " mB").withStyle(ChatFormatting.GRAY)
        ), mouseX, mouseY);
    }

    @Override protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        tankTooltip(graphics, mouseX, mouseY, 62, 27, 16, 32,
            "gui.italiansdelight.brewing_barrel.input", menu.getInputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tankTooltip(graphics, mouseX, mouseY, 116, 27, 16, 32,
            "gui.italiansdelight.brewing_barrel.output", menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tankTooltip(graphics, mouseX, mouseY, 26, 35, 16, 16,
            "gui.italiansdelight.brewing_barrel.culture", menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY);
    }
}
