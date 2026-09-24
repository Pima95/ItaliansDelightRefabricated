package com.piergiuseppe.italiansdelight.common.registry;

import com.piergiuseppe.italiansdelight.common.block.CheeseVatBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModBlocks {

    private ModBlocks() {
    }

    // Cheese Vat Block
    public static final ResourceKey<Block> CHEESE_VAT_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(),
        ModRegistries.id("cheese_vat")
    );

    public static final Block CHEESE_VAT = register(
        CHEESE_VAT_KEY,
        new CheeseVatBlock(
            Block.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_BLOCK)
                .setId(CHEESE_VAT_KEY)
                .strength(2.0F, 6.0F)
        )
    );

    private static Block register(ResourceKey<Block> blockKey, Block block) {
        Block registeredBlock = Registry.register(
            BuiltInRegistries.BLOCK,
            blockKey,
            block
        );

        ResourceKey<Item> itemKey = ResourceKey.create(
            BuiltInRegistries.ITEM.key(),
            ModRegistries.id("cheese_vat")
        );

        BlockItem blockItem = new BlockItem(
            registeredBlock,
            new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(itemKey)
        );

        Registry.register(
            BuiltInRegistries.ITEM,
            itemKey,
            blockItem
        );

        return registeredBlock;
    }

    public static void register() {

    }
}