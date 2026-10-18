package dev.italiansdelight;

import net.fabricmc.api.ClientModInitializer;

import dev.italiansdelight.client.gui.CheeseVatScreen;
import dev.italiansdelight.client.gui.PizzaOvenScreen;
import dev.italiansdelight.client.particle.WhiteBubblePopParticle;
import dev.italiansdelight.client.render.PizzaDivisionOverlay;
import dev.italiansdelight.client.render.PizzaToppingRenderer;
import dev.italiansdelight.client.render.CookedPizzaRenderer;
import dev.italiansdelight.client.render.PizzaPeelItemModel;
import dev.italiansdelight.common.registry.ModRegistries;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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
        // Wrap ONLY the already-baked peel item model; keep its 3D geometry
        // and all approved transforms. On loaded peels the wrapper appends
        // the exact ItemStack model from the serialized cargo component.
        ModelLoadingPlugin.register(context ->
                context.modifyItemModelAfterBake().register((model, details) ->
                        details.itemId().equals(ModRegistries.id("pizza_peel"))
                                ? new PizzaPeelItemModel(model) : model));

        PizzaDivisionOverlay.register();
        BlockEntityRenderers.register(ModBlockEntities.PIZZA_DOUGH, PizzaToppingRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COOKED_PIZZA, CookedPizzaRenderer::new);
        // Links the server-synchronized MenuType to the client-side screen.
        MenuScreens.register(
            ModMenuTypes.CHEESE_VAT,
            CheeseVatScreen::new
        );

        // Dedicated pizza-only screen, no vanilla furnace recipe book.
        MenuScreens.register(
            ModMenuTypes.PIZZA_OVEN,
            PizzaOvenScreen::new
        );

        ParticleProviderRegistry.getInstance().register(
            ModParticles.WHITE_BUBBLE_POP,
            WhiteBubblePopParticle.Provider::new
        );
    }
}
