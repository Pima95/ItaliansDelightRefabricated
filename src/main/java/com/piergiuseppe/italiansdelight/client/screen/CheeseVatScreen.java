package com.piergiuseppe.italiansdelight.client.screen;

import com.piergiuseppe.italiansdelight.menu.CheeseVatMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CheeseVatScreen extends AbstractContainerScreen<CheeseVatMenu> {

    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath(
            "italiansdelight",
            "textures/gui/cheese_vat.png"
        );

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

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

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            this.leftPos,
            this.topPos,
            0,
            0,
            GUI_WIDTH,
            GUI_HEIGHT,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT
        );
    }
}