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
import net.minecraft.util.FormattedCharSequence;
import java.util.List;

/**
 * Rustic 176px-wide art, split into a 198px-tall screen with a real header.
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

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 198;
    private static final int MACHINE_SHIFT = BrewingBarrelMenu.MACHINE_Y_OFFSET;
    private static final int INVENTORY_SHIFT = BrewingBarrelMenu.INVENTORY_Y_OFFSET;
    // The original texture remains 256x256; only sections of its 176x166 GUI
    // are drawn at new positions. No slot outlines are stretched.
    private static final int ORIGINAL_MACHINE_START = 4;
    private static final int ORIGINAL_INVENTORY_START = 81;

    public BrewingBarrelScreen(BrewingBarrelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GUI_WIDTH, GUI_HEIGHT);
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = 104;
    }

    /**
     * Each wood variant can have a long localized name. Wrap to two lines
     * if needed, while keeping the full translated title visible.
     */
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<FormattedCharSequence> lines = font.split(title, GUI_WIDTH - 16);
        for (int line = 0; line < Math.min(lines.size(), 2); line++) {
            graphics.text(font, lines.get(line), titleLabelX, titleLabelY + line * 9,
                0xFF404040, false);
        }
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY,
            0xFF404040, false);
    }

    /**
     * Add a 20px title band and a further 12px of clearance above Inventory
     * without changing any pixel of the original Rustic graphic.
     * Source 0..4: original upper bevel. Source 4..81: machine artwork.
     * Source 81..166: inventory and lower bevel.
     */
    private void drawExtendedBackground(GuiGraphicsExtractor graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
            leftPos, topPos, 0, 0, GUI_WIDTH, ORIGINAL_MACHINE_START, 256, 256);
        for (int y = ORIGINAL_MACHINE_START; y < MACHINE_SHIFT + ORIGINAL_MACHINE_START; y++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos, topPos + y, 0, ORIGINAL_MACHINE_START, GUI_WIDTH, 1, 256, 256);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
            leftPos, topPos + MACHINE_SHIFT + ORIGINAL_MACHINE_START,
            0, ORIGINAL_MACHINE_START,
            GUI_WIDTH, ORIGINAL_INVENTORY_START - ORIGINAL_MACHINE_START, 256, 256);
        for (int y = MACHINE_SHIFT + ORIGINAL_INVENTORY_START;
                y < INVENTORY_SHIFT + ORIGINAL_INVENTORY_START; y++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos, topPos + y, 0, ORIGINAL_INVENTORY_START, GUI_WIDTH, 1, 256, 256);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
            leftPos, topPos + INVENTORY_SHIFT + ORIGINAL_INVENTORY_START,
            0, ORIGINAL_INVENTORY_START, GUI_WIDTH,
            166 - ORIGINAL_INVENTORY_START, 256, 256);
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
        drawExtendedBackground(graphics);

        tank(graphics, menu.hasWhiteGrapeJuice() ? WHITE : PURPLE,
            62, 27 + MACHINE_SHIFT, 32, menu.getInputAmount(), BrewingBarrelBlockEntity.CAPACITY);
        tank(graphics, WINE, 116, 27 + MACHINE_SHIFT, 32, menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tank(graphics, WINE, 26, 35 + MACHINE_SHIFT, 16, menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY);

        // Original Rustic icons for empty container slots, from the existing PNG.
        if (!menu.getSlot(BrewingBarrelBlockEntity.INPUT_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 62, topPos + 7 + MACHINE_SHIFT, 176, 54, 16, 16, 256, 256);
        if (!menu.getSlot(BrewingBarrelBlockEntity.OUTPUT_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 116, topPos + 7 + MACHINE_SHIFT, 176, 70, 16, 16, 256, 256);
        if (!menu.getSlot(BrewingBarrelBlockEntity.CULTURE_IN).hasItem())
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 26, topPos + 15 + MACHINE_SHIFT, 176, 70, 16, 16, 256, 256);
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
        tankTooltip(graphics, mouseX, mouseY, 62, 27 + MACHINE_SHIFT, 16, 32,
            "gui.italiansdelight.brewing_barrel.input", menu.getInputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tankTooltip(graphics, mouseX, mouseY, 116, 27 + MACHINE_SHIFT, 16, 32,
            "gui.italiansdelight.brewing_barrel.output", menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY);
        tankTooltip(graphics, mouseX, mouseY, 26, 35 + MACHINE_SHIFT, 16, 16,
            "gui.italiansdelight.brewing_barrel.culture", menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY);
    }
}
