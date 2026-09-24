package com.piergiuseppe.italiansdelight.registry;

import com.piergiuseppe.italiansdelight.block.entity.CheeseVatBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {

    private ModBlockEntities() {
    }

    public static final BlockEntityType<CheeseVatBlockEntity> CHEESE_VAT =
        Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ModRegistries.id("cheese_vat"),
            FabricBlockEntityTypeBuilder.create(
                CheeseVatBlockEntity::new,
                ModBlocks.CHEESE_VAT
            ).build()
        );

    public static void register() {
    }
}