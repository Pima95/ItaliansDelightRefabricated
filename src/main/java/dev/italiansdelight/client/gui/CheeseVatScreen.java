package dev.italiansdelight.client.gui;

import java.util.List;

import dev.italiansdelight.common.block.entity.container.CheeseVatMenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client screen for the Cheese Vat. Draws the GUI and displays heat state and processing progress.
 */

public class CheeseVatScreen
    extends AbstractContainerScreen<CheeseVatMenu> {

    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(
            "italiansdelight",
            "textures/gui/cheese_vat.png"
        );

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final int HEAT_X = 34;
    private static final int HEAT_Y = 55;

    private static final int HEAT_WIDTH = 17;
    private static final int HEAT_HEIGHT = 15;

    private static final int PROGRESS_X = 76;
    private static final int PROGRESS_Y = 25;

    private static final int PROGRESS_HEIGHT = 17;

    // Clickable whey-flow arrow between the vat and the tank.
    private static final int WHEY_FLOW_X = 133;
    private static final int WHEY_FLOW_Y = 29;
    private static final int WHEY_FLOW_WIDTH = 9;
    private static final int WHEY_FLOW_HEIGHT = 9;

    // Red blocked-flow sprite already present in cheese_vat.png.
    private static final int WHEY_FLOW_BLOCKED_U = 176;
    private static final int WHEY_FLOW_BLOCKED_V = 55;

    // 16x32 interior of the tank drawn in the GUI texture.
    private static final int WHEY_TANK_X = 144;
    private static final int WHEY_TANK_Y = 19;
    private static final int WHEY_TANK_WIDTH = 16;
    private static final int WHEY_TANK_HEIGHT = 32;

    // Hover also includes the one-pixel frame around the tank.
    private static final int WHEY_TANK_HOVER_X = WHEY_TANK_X - 1;
    private static final int WHEY_TANK_HOVER_Y = WHEY_TANK_Y - 1;
    private static final int WHEY_TANK_HOVER_WIDTH = WHEY_TANK_WIDTH + 2;
    private static final int WHEY_TANK_HOVER_HEIGHT = WHEY_TANK_HEIGHT + 2;

    // Pale yellow whey, with a lighter one-pixel liquid surface.
    private static final int WHEY_COLOR = 0xFFE1D27A;
    private static final int WHEY_SURFACE_COLOR = 0xFFF1E5A5;

    public CheeseVatScreen(
        CheeseVatMenu menu,
        Inventory inventory,
        Component title
    ) {
        super(
            menu,
            inventory,
            title,
            GUI_WIDTH,
            GUI_HEIGHT
        );
    }

    @Override
    public void extractBackground(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float delta
    ) {
        super.extractBackground(
            graphics,
            mouseX,
            mouseY,
            delta
        );

        // Main GUI
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            leftPos,
            topPos,
            0,
            0,
            GUI_WIDTH,
            GUI_HEIGHT,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT
        );

        // Whey tank: 4000 mB fill 32 pixels, so each 250 mB batch
        // produced by curd is represented by exactly two pixels.
        int wheyLevel =
            menu.getWheyLevelScaled(
                WHEY_TANK_HEIGHT
            );

        if (wheyLevel > 0) {
            int wheyTop =
                topPos
                    + WHEY_TANK_Y
                    + WHEY_TANK_HEIGHT
                    - wheyLevel;

            graphics.fill(
                leftPos + WHEY_TANK_X,
                wheyTop,
                leftPos + WHEY_TANK_X + WHEY_TANK_WIDTH,
                topPos + WHEY_TANK_Y + WHEY_TANK_HEIGHT,
                WHEY_COLOR
            );

            graphics.fill(
                leftPos + WHEY_TANK_X,
                wheyTop,
                leftPos + WHEY_TANK_X + WHEY_TANK_WIDTH,
                wheyTop + 1,
                WHEY_SURFACE_COLOR
            );
        }

        // Active flame indicator
        if (menu.isHeated()) {

            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos + HEAT_X,
                topPos + HEAT_Y,
                176,
                0,
                HEAT_WIDTH,
                HEAT_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
            );
        }

        // Recipe progress
        int progress =
            menu.getCookProgressionScaled();

        if (progress > 0) {

            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos + PROGRESS_X,
                topPos + PROGRESS_Y,
                176,
                15,
                progress,
                PROGRESS_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
            );
        }
        // The base GUI already contains the normal flow arrow. Cover it with
        // the red X only while automatic whey flow from the tank is disabled.
        if (!menu.isWheyFlowEnabled()) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos + WHEY_FLOW_X,
                topPos + WHEY_FLOW_Y,
                WHEY_FLOW_BLOCKED_U,
                WHEY_FLOW_BLOCKED_V,
                WHEY_FLOW_WIDTH,
                WHEY_FLOW_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
            );
        }
    }

    @Override
    protected void extractTooltip(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY
    ) {
        super.extractTooltip(
            graphics,
            mouseX,
            mouseY
        );

        if (
            mouseX >= leftPos + WHEY_TANK_HOVER_X
            && mouseX < leftPos + WHEY_TANK_HOVER_X + WHEY_TANK_HOVER_WIDTH
            && mouseY >= topPos + WHEY_TANK_HOVER_Y
            && mouseY < topPos + WHEY_TANK_HOVER_Y + WHEY_TANK_HOVER_HEIGHT
        ) {
            graphics.setComponentTooltipForNextFrame(
                font,
                List.of(
                    Component.translatable(
                        "gui.italiansdelight.cheese_vat.whey"
                    ).withStyle(ChatFormatting.WHITE),
                    Component.translatable(
                        "gui.italiansdelight.cheese_vat.whey_amount",
                        menu.getWheyAmount(),
                        menu.getWheyCapacity()
                    ).withStyle(ChatFormatting.GRAY)
                ),
                mouseX,
                mouseY
            );
        }
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClick
    ) {
        double mouseX = event.x();
        double mouseY = event.y();

        if (
            event.button() == 0
            && mouseX >= leftPos + WHEY_FLOW_X
            && mouseX < leftPos + WHEY_FLOW_X + WHEY_FLOW_WIDTH
            && mouseY >= topPos + WHEY_FLOW_Y
            && mouseY < topPos + WHEY_FLOW_Y + WHEY_FLOW_HEIGHT
        ) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(
                    menu.containerId,
                    CheeseVatMenu.TOGGLE_WHEY_FLOW_BUTTON
                );
            }

            return true;
        }

        return super.mouseClicked(
            event,
            doubleClick
        );
    }
}