package dev.italiansdelight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.fluid.FluidTransferBridge;
import dev.italiansdelight.common.fluid.NeoForgeFluidItemContainers;
import dev.italiansdelight.common.registry.ModCompostables;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.block.FluidCauldronInteractions;
import dev.italiansdelight.common.block.HorizontalRopePlacement;
import dev.italiansdelight.common.block.StakeSoilTilling;
import dev.italiansdelight.common.block.LegacyOilCauldronMigration;
import dev.italiansdelight.common.registry.ModSigns;
import dev.italiansdelight.common.registry.ModBoats;
import dev.italiansdelight.common.item.group.ModItemGroups;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModMenuTypes;
import dev.italiansdelight.common.registry.ModParticles;
import dev.italiansdelight.common.registry.ModRecipes;
import dev.italiansdelight.common.registry.ModWorldGeneration;
import dev.italiansdelight.common.worldgen.ModTreePlacers;
import dev.italiansdelight.common.salt.SaltCauldronManager;
import dev.italiansdelight.common.pizza.PizzaDoughPlacement;
import dev.italiansdelight.common.pizza.PizzaDivisionSync;
import dev.italiansdelight.common.pizza.PizzaToppingCatalog;
import dev.italiansdelight.common.pizza.PizzaToppingMappings;

/**
 * Common entrypoint (server + client) for Italian's Delight.
 * <p>
 * This mod is an add-on for Farmer's Delight (mod id: "farmersdelight"):
 * it reuses its systems (Cutting Board, Cooking Pot, Stove, food quality) instead of
 * reinventing them, and extends them with Italian ingredients and dishes.
 */
@Mod(ItaliansDelight.MOD_ID)
public class ItaliansDelight {

    public static final String MOD_ID = "italiansdelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ItaliansDelight(IEventBus modBus) {
        modBus.addListener(ModDataComponents::onRegister);
        modBus.addListener(ModMenuTypes::onRegister);
        modBus.addListener(ModParticles::onRegister);
        modBus.addListener(ModRecipes::onRegister);
        modBus.addListener(ModTreePlacers::onRegister);
        modBus.addListener(ModSigns::onRegister);
        modBus.addListener(ModFluids::onRegister);
        modBus.addListener(ModItemGroups::onRegister);
        modBus.addListener(ModWorldGeneration::onRegister);
        modBus.addListener(ModBlocks::onRegister);
        modBus.addListener(ModBlockEntities::onRegister);
        modBus.addListener(ModBlockEntities::addValidBlocks);
        modBus.addListener(FluidTransferBridge::register);
        modBus.addListener(NeoForgeFluidItemContainers::register);
        // Registry lifecycle is being migrated in separate commits; do not
        // treat this constructor as a runnable port until that is complete.
        modBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(this::initializeCommon);
    }

    private void initializeCommon() {
        LOGGER.info("[Italian's Delight] Inizializzazione in corso...");

        // Registries are initialized before the systems that depend on them.
        // This ensures BlockEntities, menus, recipes, and events can already find
        // all registered items and blocks when they are configured.
        ModItems.register();
        ModBlocks.register();
        ModBoats.register();
        // Pure, read-only registry. The toppings will be wired to pizza interactions later.
        PizzaToppingCatalog.register();
        // Datapack equivalences are loaded on server data reload, never per tick.
        PizzaToppingMappings.register();
        PizzaDoughPlacement.register();
        PizzaDivisionSync.register();
        ModCompostables.register();

        SaltCauldronManager.register();
        FluidCauldronInteractions.register();
        StakeSoilTilling.register();
        HorizontalRopePlacement.register();
        LegacyOilCauldronMigration.register();

        LOGGER.info("[Italian's Delight] Pronta! Buon appetito.");
    }
}
