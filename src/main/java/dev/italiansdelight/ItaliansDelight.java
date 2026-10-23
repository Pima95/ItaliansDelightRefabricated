package dev.italiansdelight;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModBoats;
import dev.italiansdelight.common.item.group.ModItemGroups;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModMenuTypes;
import dev.italiansdelight.common.registry.ModParticles;
import dev.italiansdelight.common.registry.ModRecipes;
import dev.italiansdelight.common.registry.ModWorldGeneration;
import dev.italiansdelight.common.salt.SaltCauldronManager;
import dev.italiansdelight.common.pizza.PizzaDoughPlacement;
import dev.italiansdelight.common.pizza.PizzaDivisionSync;
import dev.italiansdelight.common.pizza.PizzaToppingCatalog;
import dev.italiansdelight.common.pizza.PizzaToppingMappings;

/**
 * Common entrypoint (server + client) for Italian's Delight.
 * <p>
 * This mod is an add-on for Farmer's Delight Refabricated (mod id: "farmersdelight"):
 * it reuses its systems (Cutting Board, Cooking Pot, Stove, food quality) instead of
 * reinventing them, and extends them with Italian ingredients and dishes.
 */
public class ItaliansDelight implements ModInitializer {

    public static final String MOD_ID = "italiansdelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Italian's Delight] Inizializzazione in corso...");

        // Registries are initialized before the systems that depend on them.
        // This ensures BlockEntities, menus, recipes, and events can already find
        // all registered items and blocks when they are configured.
        ModDataComponents.register();
        ModItems.register();
        ModBlocks.register();
        ModBoats.register();
        // Pure, read-only registry. The toppings will be wired to pizza interactions later.
        PizzaToppingCatalog.register();
        // Datapack equivalences are loaded on server data reload, never per tick.
        PizzaToppingMappings.register();
        PizzaDoughPlacement.register();
        PizzaDivisionSync.register();
        ModBlockEntities.register();
        ModMenuTypes.register();
        ModRecipes.register();
        ModParticles.register();
        ModItemGroups.register();
        ModWorldGeneration.register();

        SaltCauldronManager.register();

        LOGGER.info("[Italian's Delight] Pronta! Buon appetito.");
    }
}
