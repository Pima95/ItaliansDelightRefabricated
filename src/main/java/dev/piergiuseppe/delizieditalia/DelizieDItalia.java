package dev.piergiuseppe.delizieditalia;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point principale di "Delizie d'Italia", add-on per Farmer's Delight
 * Refabricated che aggiunge cibo, piatti e blocchi della cucina italiana.
 */
public class DelizieDItalia implements ModInitializer {

	public static final String MOD_ID = "delizieditalia";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Delizie d'Italia: avvio registrazione contenuti...");

		// TODO: registrare item, blocchi, cibi e recipe type qui,
		// es. ModItems.register(); ModBlocks.register(); ModFoods.register();
	}
}
