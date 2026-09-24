package com.piergiuseppe.italiansdelight;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.piergiuseppe.italiansdelight.registry.ModItems;
import com.piergiuseppe.italiansdelight.registry.ModBlocks;
import com.piergiuseppe.italiansdelight.item.group.ModItemGroups;
import com.piergiuseppe.italiansdelight.registry.ModBlockEntities;
import com.piergiuseppe.italiansdelight.registry.ModMenuTypes;

/**
 * Entrypoint comune (server + client) di Italian's Delight.
 * <p>
 * Questa mod è un add-on di Farmer's Delight Refabricated (mod id: "farmersdelight"):
 * riusa i suoi sistemi (Cutting Board, Cooking Pot, Stove, qualità del cibo) invece
 * di reinventarli, e li estende con ingredienti e piatti della cucina italiana.
 */
public class ItaliansDelight implements ModInitializer {

    public static final String MOD_ID = "italiansdelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Italian's Delight] Inizializzazione in corso...");

        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModMenuTypes.register();
        ModItemGroups.register();

        LOGGER.info("[Italian's Delight] Pronta! Buon appetito.");
    }
}
