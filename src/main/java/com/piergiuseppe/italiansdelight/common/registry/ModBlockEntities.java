package com.piergiuseppe.italiansdelight.common.registry;

import com.piergiuseppe.italiansdelight.common.block.entity.CheeseVatBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {

    private ModBlockEntities() {
    }

    public static final BlockEntityType<CheeseVatBlockEntity> CHEESE_VAT =
        register(
            "cheese_vat",
            CheeseVatBlockEntity::new,
            ModBlocks.CHEESE_VAT
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