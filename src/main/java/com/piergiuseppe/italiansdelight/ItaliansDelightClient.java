package com.piergiuseppe.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

/**
 * Entrypoint solo-client: qui andranno le registrazioni di render layer,
 * particellari, tooltip custom, ecc. Per ora è vuoto: lo popoleremo quando
 * aggiungeremo i primi blocchi/oggetti con rendering non standard.
 */
public class ItaliansDelightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Nessun setup client-only ancora necessario.
    }
}
