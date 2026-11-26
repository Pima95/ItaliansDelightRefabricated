package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import dev.italiansdelight.common.block.entity.BrewingBarrelBlockEntity;
import dev.italiansdelight.common.block.entity.CrushingTubBlockEntity;
import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.block.entity.CheeseAgingRackBlockEntity;
import dev.italiansdelight.common.block.entity.CheeseHookBlockEntity;
import dev.italiansdelight.common.block.entity.AgingCheeseBlockEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Central registry for Italian's Delight BlockEntities.
 */

public final class ModBlockEntities {

    private ModBlockEntities() {
    }

    // Separate persistent data for the four physical quarters of a dough pizza.
    public static BlockEntityType<PizzaDoughBlockEntity> PIZZA_DOUGH;

    public static BlockEntityType<CookedPizzaBlockEntity> COOKED_PIZZA;

    public static BlockEntityType<PizzaOvenBlockEntity> PIZZA_OVEN;

    public static BlockEntityType<CrushingTubBlockEntity> CRUSHING_TUB;

    public static BlockEntityType<BrewingBarrelBlockEntity> BREWING_BARREL;

    public static BlockEntityType<BarrelBlockEntity> BARREL;
    public static BlockEntityType<FluidCauldronBlockEntity> FLUID_CAULDRON;

    public static BlockEntityType<CheeseVatBlockEntity> CHEESE_VAT;

    public static BlockEntityType<AgingCheeseBlockEntity> AGING_CHEESE;

    public static BlockEntityType<CheeseHookBlockEntity> CHEESE_HOOK;

    public static BlockEntityType<CheeseAgingRackBlockEntity> CHEESE_AGING_RACK;

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
        String name,
        BlockEntityType.BlockEntitySupplier<? extends T> entityFactory,
        net.minecraft.world.level.block.Block... blocks
    ) {
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ModRegistries.id(name),
            BlockEntityType.Builder.<T>of(entityFactory, blocks).build(null)
        );
    }

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) return;
        PIZZA_DOUGH = register("pizza_dough", PizzaDoughBlockEntity::new, ModBlocks.PIZZA_DOUGH);
        COOKED_PIZZA = register("cooked_pizza", CookedPizzaBlockEntity::new, ModBlocks.COOKED_PIZZA);
        PIZZA_OVEN = register("pizza_oven", PizzaOvenBlockEntity::new, ModBlocks.PIZZA_OVEN);
        CRUSHING_TUB = register("crushing_tub", CrushingTubBlockEntity::new, ModBlocks.CRUSHING_TUB, ModBlocks.SPRUCE_CRUSHING_TUB, ModBlocks.BIRCH_CRUSHING_TUB, ModBlocks.JUNGLE_CRUSHING_TUB, ModBlocks.ACACIA_CRUSHING_TUB, ModBlocks.DARK_OAK_CRUSHING_TUB, ModBlocks.MANGROVE_CRUSHING_TUB, ModBlocks.CHERRY_CRUSHING_TUB, ModBlocks.BAMBOO_CRUSHING_TUB, ModBlocks.CRIMSON_CRUSHING_TUB, ModBlocks.WARPED_CRUSHING_TUB, ModBlocks.OLIVE_CRUSHING_TUB, ModBlocks.ALMOND_CRUSHING_TUB, ModBlocks.FRUIT_CHERRY_CRUSHING_TUB);
        BREWING_BARREL = register("brewing_barrel", BrewingBarrelBlockEntity::new, ModBlocks.BREWING_BARREL,
            ModBlocks.SPRUCE_BREWING_BARREL,
            ModBlocks.BIRCH_BREWING_BARREL,
            ModBlocks.JUNGLE_BREWING_BARREL,
            ModBlocks.ACACIA_BREWING_BARREL,
            ModBlocks.DARK_OAK_BREWING_BARREL,
            ModBlocks.MANGROVE_BREWING_BARREL,
            ModBlocks.CHERRY_BREWING_BARREL,
            ModBlocks.BAMBOO_BREWING_BARREL,
            ModBlocks.CRIMSON_BREWING_BARREL,
            ModBlocks.WARPED_BREWING_BARREL,
            ModBlocks.OLIVE_BREWING_BARREL,
            ModBlocks.ALMOND_BREWING_BARREL,
            ModBlocks.FRUIT_CHERRY_BREWING_BARREL);
        BARREL = register("barrel", BarrelBlockEntity::new, ModBlocks.BARREL, ModBlocks.SPRUCE_BARREL, ModBlocks.BIRCH_BARREL, ModBlocks.JUNGLE_BARREL, ModBlocks.ACACIA_BARREL, ModBlocks.DARK_OAK_BARREL, ModBlocks.MANGROVE_BARREL, ModBlocks.CHERRY_BARREL, ModBlocks.BAMBOO_BARREL, ModBlocks.CRIMSON_BARREL, ModBlocks.WARPED_BARREL, ModBlocks.OLIVE_BARREL, ModBlocks.ALMOND_BARREL, ModBlocks.FRUIT_CHERRY_BARREL);
        FLUID_CAULDRON = register("fluid_cauldron", FluidCauldronBlockEntity::new, ModBlocks.FLUID_CAULDRON);
        CHEESE_VAT = register(
            "cheese_vat",
            CheeseVatBlockEntity::new,
            ModBlocks.CHEESE_VAT
        );
        AGING_CHEESE = register(
            "aging_cheese",
            AgingCheeseBlockEntity::new,
            ModBlocks.AGING_CHEESE
        );
        CHEESE_HOOK = register(
            "cheese_hook",
            CheeseHookBlockEntity::new,
            ModBlocks.CHEESE_HOOK
        );
        CHEESE_AGING_RACK = register(
            "cheese_aging_rack",
            CheeseAgingRackBlockEntity::new,
            ModBlocks.OAK_CHEESE_AGING_RACK,
            ModBlocks.SPRUCE_CHEESE_AGING_RACK,
            ModBlocks.BIRCH_CHEESE_AGING_RACK,
            ModBlocks.JUNGLE_CHEESE_AGING_RACK,
            ModBlocks.ACACIA_CHEESE_AGING_RACK,
            ModBlocks.DARK_OAK_CHEESE_AGING_RACK,
            ModBlocks.MANGROVE_CHEESE_AGING_RACK,
            ModBlocks.CHERRY_CHEESE_AGING_RACK,
            ModBlocks.BAMBOO_CHEESE_AGING_RACK,
            ModBlocks.CRIMSON_CHEESE_AGING_RACK,
            ModBlocks.WARPED_CHEESE_AGING_RACK,
            ModBlocks.OLIVE_CHEESE_AGING_RACK,
            ModBlocks.ALMOND_CHEESE_AGING_RACK,
            ModBlocks.FRUIT_CHERRY_CHEESE_AGING_RACK
        );
    }

    public static void addValidBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SIGN, ModBlocks.OLIVE_SIGN, ModBlocks.OLIVE_WALL_SIGN,
            ModBlocks.ALMOND_SIGN, ModBlocks.ALMOND_WALL_SIGN,
            ModBlocks.FRUIT_CHERRY_SIGN, ModBlocks.FRUIT_CHERRY_WALL_SIGN);
        event.modify(BlockEntityType.HANGING_SIGN, ModBlocks.OLIVE_HANGING_SIGN, ModBlocks.OLIVE_WALL_HANGING_SIGN,
            ModBlocks.ALMOND_HANGING_SIGN, ModBlocks.ALMOND_WALL_HANGING_SIGN,
            ModBlocks.FRUIT_CHERRY_HANGING_SIGN, ModBlocks.FRUIT_CHERRY_WALL_HANGING_SIGN);
        event.modify(vectorwing.farmersdelight.common.registry.ModBlockEntityTypes.CABINET.get(),
            ModBlocks.OLIVE_CABINET, ModBlocks.ALMOND_CABINET, ModBlocks.FRUIT_CHERRY_CABINET);
    }
}
