package dev.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

import dev.italiansdelight.client.gui.CheeseVatScreen;
import dev.italiansdelight.client.particle.WhiteBubblePopParticle;
import dev.italiansdelight.common.registry.ModMenuTypes;
import dev.italiansdelight.common.registry.ModParticles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

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

        ParticleProviderRegistry.getInstance().register(
            ModParticles.WHITE_BUBBLE_POP,
            WhiteBubblePopParticle.Provider::new
        );
    }
}
