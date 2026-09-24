package com.piergiuseppe.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

import com.piergiuseppe.italiansdelight.client.screen.CheeseVatScreen;
import com.piergiuseppe.italiansdelight.registry.ModMenuTypes;

import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Entrypoint solo-client: qui andranno le registrazioni di render layer,
 * particellari, tooltip custom, ecc. Per ora è vuoto: lo popoleremo quando
 * aggiungeremo i primi blocchi/oggetti con rendering non standard.
 */
public class ItaliansDelightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(
            ModMenuTypes.CHEESE_VAT,
            CheeseVatScreen::new
        );
    }
}
