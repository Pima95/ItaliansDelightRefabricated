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
 * Rustic's brewing-time bubbles and arrow overlays are redrawn over the
 * approved texture, while all 16 wood-title variants share the same screen.
 */
public final class BrewingBarrelScreen extends AbstractContainerScreen<BrewingBarrelMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "textures/gui/brewing_barrel.png");
    private static final Identifier PURPLE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/grape_juice_still");
    private static final Identifier WHITE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/white_grape_juice_still");
    private static final Identifier APPLE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/apple_juice_still");
    private static final Identifier VINEGAR = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/apple_cider_vinegar_still");
    private static final Identifier WINE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/wine_still");
    private static final Identifier CHERRY_JUICE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/cherry_juice_still");
    private static final Identifier WHITE_WINE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/white_wine_still");
    private static final Identifier CHERRY_WINE = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/cherry_wine_still");
    private static final Identifier MEAD = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/mead_still");
    private static final Identifier CIDER = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/cider_still");
    private static final Identifier WINE_VINEGAR = Identifier.fromNamespaceAndPath(
        "italiansdelight", "fluids/red_wine_vinegar_still");

    // Both modern 16x16 silhouettes are drawn from the spare sprite cells in
    // the SAME 256x256 brewing_barrel.png GUI texture, at u=176, v=54/70.
    // The bucket includes a curved inner rim to show its depth.
    private static final int VESSEL_U = 176;
    private static final int BUCKET_V = 54;
    private static final int BOTTLE_V = 70;
    private static final int TANK_WIDTH = 16;
    private static final int FLUID_FRAME_SIZE = 16;

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

    /**
     * Each grape/wine fluid has a 16x16 animated atlas frame (16x512 sprite
     * sheet with 32 frames). Never stretch the live 16px frame to 16x32:
     * repeat native 16x16 tiles instead, then scissor to the liquid level.
     * This keeps the artwork crisp and synchronized in both tank heights.
     */
    private void tank(GuiGraphicsExtractor graphics, Identifier sprite,
                      int x, int y, int height, int amount, int capacity,
                      int surfaceColor, int backingColor) {
        if (amount <= 0) return;
        int level = Math.clamp((int) Math.ceil(amount * height / (double) capacity), 1, height);
        int absoluteX = leftPos + x;
        int absoluteY = topPos + y;
        int surfaceY = absoluteY + height - level;
        graphics.enableScissor(absoluteX, surfaceY, absoluteX + TANK_WIDTH, absoluteY + height);
        try {
            // The red/black grape textures contain translucent animated
            // pixels. Put an opaque wine-colored backing ONLY beneath the
            // visible liquid so the dark tank cannot wash out its movement.
            // The original PNG frames and their animation metadata stay intact.
            if (backingColor != 0) {
                graphics.fill(absoluteX, surfaceY,
                    absoluteX + TANK_WIDTH, absoluteY + height, backingColor);
            }
            for (int row = 0; row < height; row += FLUID_FRAME_SIZE) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite,
                    absoluteX, absoluteY + row, FLUID_FRAME_SIZE, FLUID_FRAME_SIZE);
            }
        } finally {
            graphics.disableScissor();
        }
        // Single-pixel meniscus distinguishes the surface from the dark tank
        // background even at a low fill level; it is not a second animation.
        graphics.fill(absoluteX, surfaceY, absoluteX + TANK_WIDTH, surfaceY + 1, surfaceColor);
    }

    /**
     * Render the real, unscaled 16x16 silhouette at exactly the same
     * coordinates as the 16x16 ItemStack that can occupy this slot. The source
     * built-in GUI texture preserves the original 16x16 scale: bottle
     * ink spans x=4..12 and y=2..14; modern bucket outline spans
     * x=2..13 and y=1..14 with an internal curved rim at rows 6-7.
     *
     * No separate sprite file, resizing or dark wash over the bucket.
     */
    private void emptyVesselIcon(GuiGraphicsExtractor graphics, int slotIndex, boolean bucket) {
        var slot = menu.getSlot(slotIndex);
        if (slot.hasItem()) return;
        int iconX = leftPos + slot.x;
        int iconY = topPos + slot.y;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
            iconX, iconY, VESSEL_U, bucket ? BUCKET_V : BOTTLE_V,
            16, 16, 256, 256);
    }

    @Override public void extractBackground(
            GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        drawExtendedBackground(graphics);

        Identifier inputSprite = switch (menu.getInputKind()) {
            case BrewingBarrelBlockEntity.JUICE_WHITE -> WHITE;
            case BrewingBarrelBlockEntity.JUICE_APPLE -> APPLE;
            case BrewingBarrelBlockEntity.JUICE_CHERRY -> CHERRY_JUICE;
            case BrewingBarrelBlockEntity.JUICE_HONEY -> MEAD;
            case BrewingBarrelBlockEntity.JUICE_RED_WINE -> WINE;
            case BrewingBarrelBlockEntity.JUICE_CIDER -> CIDER;
            default -> PURPLE;
        };
        Identifier outputSprite = switch (menu.getOutputKind()) {
            case BrewingBarrelBlockEntity.OUTPUT_VINEGAR -> VINEGAR;
            case BrewingBarrelBlockEntity.OUTPUT_WHITE_WINE -> WHITE_WINE;
            case BrewingBarrelBlockEntity.OUTPUT_CHERRY_WINE -> CHERRY_WINE;
            case BrewingBarrelBlockEntity.OUTPUT_MEAD -> MEAD;
            case BrewingBarrelBlockEntity.OUTPUT_CIDER -> CIDER;
            case BrewingBarrelBlockEntity.OUTPUT_WINE_VINEGAR -> WINE_VINEGAR;
            default -> WINE;
        };
        boolean paleInput = menu.getInputKind() == BrewingBarrelBlockEntity.JUICE_WHITE
            || menu.getInputKind() == BrewingBarrelBlockEntity.JUICE_APPLE
            || menu.getInputKind() == BrewingBarrelBlockEntity.JUICE_HONEY
            || menu.getInputKind() == BrewingBarrelBlockEntity.JUICE_CIDER;
        boolean paleOutput = menu.getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_WHITE_WINE
            || menu.getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_MEAD
            || menu.getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_CIDER
            || menu.hasVinegarOutput();
        tank(graphics, inputSprite, 62, 27 + MACHINE_SHIFT, 32, menu.getInputAmount(),
            BrewingBarrelBlockEntity.CAPACITY,
            paleInput ? 0xFFDFC284 : 0xFFC78AB9, paleInput ? 0 : 0xFF87395F);
        tank(graphics, outputSprite, 116, 27 + MACHINE_SHIFT, 32, menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY,
            paleOutput ? 0xFFE5CB8D : 0xFFC789A8, paleOutput ? 0 : 0xFF7B304D);
        tank(graphics, WINE, 26, 35 + MACHINE_SHIFT, 16, menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY, 0xBFCA8FA6, 0xFF7B304D);

        emptyVesselIcon(graphics, BrewingBarrelBlockEntity.INPUT_IN, true);
        emptyVesselIcon(graphics, BrewingBarrelBlockEntity.OUTPUT_IN, false);
        emptyVesselIcon(graphics, BrewingBarrelBlockEntity.CULTURE_IN, false);
        // Same overlays as Rustic GuiBrewingBarrel: the 24x16 progress
        // arrow, 10x10 culture arrow and 11x28 bubble animation.
        // The art lives in the existing brewing_barrel.png and needs the
        // same 20px machine offset used by the six inventory slots.
        int progress = menu.getProgress();
        int duration = menu.getMaxBrewTime();
        if (progress > 0 && duration > 0) {
            int arrow = Math.min(24, progress * 24 / duration + 1);
            int smallArrow = Math.min(10, progress * 10 / duration + 1);
            int bubbles = (int) ((progress / (float) duration) * 5600) % 28;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 85, topPos + 35 + MACHINE_SHIFT,
                176, 28, arrow, 16, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                leftPos + 45, topPos + 38 + MACHINE_SHIFT,
                176, 44, smallArrow, 10, 256, 256);
            if (bubbles > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE,
                    leftPos + 139, topPos + 29 + MACHINE_SHIFT + 28 - bubbles,
                    176, 28 - bubbles, 11, bubbles, 256, 256);
            }
        }
    }

    private void tankTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                             int x, int y, int w, int h, String key,
                             int amount, int capacity, int quality) {
        if (mouseX < leftPos+x || mouseX >= leftPos+x+w
                || mouseY < topPos+y || mouseY >= topPos+y+h) return;
        List<Component> lines = new java.util.ArrayList<>();
        lines.add(Component.translatable(key).withStyle(ChatFormatting.WHITE));
        lines.add(Component.literal(amount + " / " + capacity + " mB")
            .withStyle(ChatFormatting.GRAY));
        if (amount > 0 && quality >= 0) {
            lines.add(Component.translatable("tooltip.italiansdelight.wine_quality",
                quality).withStyle(ChatFormatting.GOLD));
        }
        graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
    }

    @Override protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        tankTooltip(graphics, mouseX, mouseY, 62, 27 + MACHINE_SHIFT, 16, 32,
            "gui.italiansdelight.brewing_barrel.input", menu.getInputAmount(),
            BrewingBarrelBlockEntity.CAPACITY, -1);
        tankTooltip(graphics, mouseX, mouseY, 116, 27 + MACHINE_SHIFT, 16, 32,
            "gui.italiansdelight.brewing_barrel.output", menu.getOutputAmount(),
            BrewingBarrelBlockEntity.CAPACITY,
            menu.hasVinegarOutput() ? -1 : menu.getOutputQuality());
        tankTooltip(graphics, mouseX, mouseY, 26, 35 + MACHINE_SHIFT, 16, 16,
            "gui.italiansdelight.brewing_barrel.culture", menu.getCultureAmount(),
            BrewingBarrelBlockEntity.CULTURE_CAPACITY, menu.getCultureQuality());
    }
}
