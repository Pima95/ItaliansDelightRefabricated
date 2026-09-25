package com.piergiuseppe.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

import com.piergiuseppe.italiansdelight.client.gui.CheeseVatScreen;
import com.piergiuseppe.italiansdelight.common.registry.ModMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Entrypoint esclusivamente client di Italian's Delight.
 * Qui vengono registrate schermate, renderer e altre funzionalità che non
 * devono essere inizializzate su un server dedicato.
 */
public class ItaliansDelightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Collega il MenuType sincronizzato dal server alla schermata grafica client.
        MenuScreens.register(
            ModMenuTypes.CHEESE_VAT,
            CheeseVatScreen::new
        );
    }
}
