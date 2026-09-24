package com.piergiuseppe.italiansdelight.client.screen;

import com.piergiuseppe.italiansdelight.menu.CheeseVatMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CheeseVatScreen extends AbstractContainerScreen<CheeseVatMenu> {

    public CheeseVatScreen(
        CheeseVatMenu menu,
        Inventory inventory,
        Component title
    ) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float delta
    ) { {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        graphics.fill(
            this.leftPos,
            this.topPos,
            this.leftPos + 176,
            this.topPos + 166,
            0xFFC6C6C6
        );
    }
}
}