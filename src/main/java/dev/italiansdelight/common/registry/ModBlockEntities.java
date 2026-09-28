package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.block.entity.AgingCheeseBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Central registry for Italian's Delight BlockEntities.
 */

public final class ModBlockEntities {

    private ModBlockEntities() {
    }

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
        
    }
}