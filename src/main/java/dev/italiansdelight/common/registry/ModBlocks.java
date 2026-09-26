package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.CardoonBlock;
import dev.italiansdelight.common.block.SaltCauldronBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Central registry for mod blocks and their BlockItems when they need to be placeable from the inventory.
 */

public final class ModBlocks {

    private ModBlocks() {
    }

    // -------------------- Registered blocks --------------------
    public static final ResourceKey<Block> CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cardoon")
    );

    public static final Block CARDOON = register(
        CARDOON_KEY,
        new CardoonBlock(Block.Properties.ofFullCopy(Blocks.ALLIUM).setId(CARDOON_KEY))
    );

    // Cheese Vat Block
    public static final ResourceKey<Block> CHEESE_VAT_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_vat")
        );

    public static final Block CHEESE_VAT = register(
        CHEESE_VAT_KEY,
        new CheeseVatBlock(
            Block.Properties
                .ofFullCopy(Blocks.IRON_BLOCK)
                .setId(CHEESE_VAT_KEY)
                .noOcclusion()
                .strength(2.0F, 6.0F)
        )
    );

    // Internal state used when a full water cauldron has finished evaporating.
    // A BlockItem is registered so Creative Pick Block can copy and re-place
    // the exact salt-filled state. It is not added to the mod creative tab.
    public static final ResourceKey<Block> SALT_CAULDRON_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("salt_cauldron")
        );

    public static final Block SALT_CAULDRON =
        register(
            SALT_CAULDRON_KEY,
            new SaltCauldronBlock(
                Block.Properties
                    .ofFullCopy(Blocks.CAULDRON)
                    .setId(SALT_CAULDRON_KEY)
                    .noOcclusion()
            )
        );

    /**
     * Registers both the block and its BlockItem with the same identifier.
     * Use this helper for blocks that need to be placeable from the inventory.
     */
    private static Block register(
        ResourceKey<Block> blockKey,
        Block block
    ) {
        Block registeredBlock =
            registerBlockOnly(
                blockKey,
                block
            );

        ResourceKey<Item> itemKey =
            ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                blockKey.identifier()
            );

        BlockItem blockItem =
            new BlockItem(
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

    // Registers only the block, without automatically creating an item.
    private static Block registerBlockOnly(
        ResourceKey<Block> blockKey,
        Block block
    ) {
        return Registry.register(
            BuiltInRegistries.BLOCK,
            blockKey,
            block
        );
    }

    public static void register() {
    }
}
