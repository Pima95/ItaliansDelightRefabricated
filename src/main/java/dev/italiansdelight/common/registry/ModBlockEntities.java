package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.block.entity.CookedPizzaBlockEntity;
import dev.italiansdelight.common.block.entity.CheeseAgingRackBlockEntity;
import dev.italiansdelight.common.block.entity.CheeseHookBlockEntity;
import dev.italiansdelight.common.block.entity.AgingCheeseBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

/**
 * Central registry for Italian's Delight BlockEntities.
 */

public final class ModBlockEntities {

    private ModBlockEntities() {
    }

    // Separate persistent data for the four physical quarters of a dough pizza.
    public static final BlockEntityType<PizzaDoughBlockEntity> PIZZA_DOUGH =
        register("pizza_dough", PizzaDoughBlockEntity::new, ModBlocks.PIZZA_DOUGH);

    public static final BlockEntityType<CookedPizzaBlockEntity> COOKED_PIZZA =
        register("cooked_pizza", CookedPizzaBlockEntity::new, ModBlocks.COOKED_PIZZA);

    public static final BlockEntityType<PizzaOvenBlockEntity> PIZZA_OVEN =
        register("pizza_oven", PizzaOvenBlockEntity::new, ModBlocks.PIZZA_OVEN);

    public static final BlockEntityType<CheeseVatBlockEntity> CHEESE_VAT =
        register(
            "cheese_vat",
            CheeseVatBlockEntity::new,
            ModBlocks.CHEESE_VAT
        );

    public static final BlockEntityType<AgingCheeseBlockEntity> AGING_CHEESE =
        register(
            "aging_cheese",
            AgingCheeseBlockEntity::new,
            ModBlocks.AGING_CHEESE
        );

    public static final BlockEntityType<CheeseHookBlockEntity> CHEESE_HOOK =
        register(
            "cheese_hook",
            CheeseHookBlockEntity::new,
            ModBlocks.CHEESE_HOOK
        );

    public static final BlockEntityType<CheeseAgingRackBlockEntity> CHEESE_AGING_RACK =
        register(
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
            ModBlocks.PALE_OAK_CHEESE_AGING_RACK,
            ModBlocks.BAMBOO_CHEESE_AGING_RACK,
            ModBlocks.CRIMSON_CHEESE_AGING_RACK,
            ModBlocks.WARPED_CHEESE_AGING_RACK,
            ModBlocks.OLIVE_CHEESE_AGING_RACK,
            ModBlocks.ALMOND_CHEESE_AGING_RACK
        );

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(
        String name,
        FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
        net.minecraft.world.level.block.Block... blocks
    ) {
        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ModRegistries.id(name),
            FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    public static void register() {
        // Native 26.2 shelf: register our wood variant with the same type as
        // the vanilla shelves, so three-item storage, persistence and the
        // existing shelf item renderer work on modded olive blocks.
        BlockEntityTypes.SHELF.addValidBlock(ModBlocks.OLIVE_SHELF);
        BlockEntityTypes.SHELF.addValidBlock(ModBlocks.ALMOND_SHELF);

        // Vanilla sign block entities keep writable text, both sides and dye.
        // Keep their four modded block variants in the vanilla BE type.
        BlockEntityTypes.SIGN.addValidBlock(ModBlocks.OLIVE_SIGN);
        BlockEntityTypes.SIGN.addValidBlock(ModBlocks.OLIVE_WALL_SIGN);
        BlockEntityTypes.SIGN.addValidBlock(ModBlocks.ALMOND_SIGN);
        BlockEntityTypes.SIGN.addValidBlock(ModBlocks.ALMOND_WALL_SIGN);
        BlockEntityTypes.HANGING_SIGN.addValidBlock(ModBlocks.OLIVE_HANGING_SIGN);
        BlockEntityTypes.HANGING_SIGN.addValidBlock(ModBlocks.OLIVE_WALL_HANGING_SIGN);
        BlockEntityTypes.HANGING_SIGN.addValidBlock(ModBlocks.ALMOND_HANGING_SIGN);
        BlockEntityTypes.HANGING_SIGN.addValidBlock(ModBlocks.ALMOND_WALL_HANGING_SIGN);

        // Reuse Farmer's Delight 27-slot cabinet BE: it already implements
        // screen, automation, sounds, comparator, save and open/close state.
        vectorwing.farmersdelight.common.registry.ModBlockEntityTypes.CABINET.get()
            .addValidBlock(ModBlocks.OLIVE_CABINET);
        vectorwing.farmersdelight.common.registry.ModBlockEntityTypes.CABINET.get()
            .addValidBlock(ModBlocks.ALMOND_CABINET);
    }
}
