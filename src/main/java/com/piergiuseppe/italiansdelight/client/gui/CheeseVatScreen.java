package com.piergiuseppe.italiansdelight.client.gui;

import com.piergiuseppe.italiansdelight.common.block.entity.container.CheeseVatMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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

    private static final int HEAT_X = 47;
    private static final int HEAT_Y = 55;

    private static final int HEAT_WIDTH = 17;
    private static final int HEAT_HEIGHT = 15;

    private static final int PROGRESS_X = 89;
    private static final int PROGRESS_Y = 25;

    private static final int PROGRESS_HEIGHT = 17;

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
    }
}