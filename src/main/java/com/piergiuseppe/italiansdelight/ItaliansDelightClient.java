package com.piergiuseppe.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

import com.piergiuseppe.italiansdelight.client.gui.CheeseVatScreen;
import com.piergiuseppe.italiansdelight.common.registry.ModMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Client-only entrypoint for Italian's Delight.
 * Screens, renderers, and other client-side features are registered here and
 * must never be initialized on a dedicated server.
 */
public class ItaliansDelightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Links the server-synchronized MenuType to the client-side screen.
        MenuScreens.register(
            ModMenuTypes.CHEESE_VAT,
            CheeseVatScreen::new
        );
    }
}
