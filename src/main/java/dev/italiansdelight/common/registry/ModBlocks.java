package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.CheeseHookBlock;
import dev.italiansdelight.common.block.CheeseAgingRackBlock;
import dev.italiansdelight.common.block.AgingCheeseBlock;
import dev.italiansdelight.common.block.CardoonBlock;
import dev.italiansdelight.common.block.SaltCauldronBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;

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

    public static final ResourceKey<Block> POTTED_CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_cardoon")
    );

    public static final Block POTTED_CARDOON = registerBlockOnly(
        POTTED_CARDOON_KEY,
        new FlowerPotBlock(CARDOON,
            Block.Properties.ofFullCopy(Blocks.POTTED_ALLIUM).setId(POTTED_CARDOON_KEY))
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
                .noTerrainParticles()
                .strength(5.0F, 6.0F)
        )
    );



    // -------------------- Cheese Hook --------------------
    // Ceiling-mounted hook used by the hanging cheese system. Its first phase
    // registers only the physical block/model; Scamorza and Provolone
    // processing is connected in the next implementation step.
    public static final ResourceKey<Block> CHEESE_HOOK_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_hook")
        );

    public static final Block CHEESE_HOOK =
        register(
            CHEESE_HOOK_KEY,
            new CheeseHookBlock(
                Block.Properties
                    .ofFullCopy(Blocks.CHAIN)
                    .setId(CHEESE_HOOK_KEY)
                    .noOcclusion()
                    .noCollision()
                    .noTerrainParticles()
                    .strength(3.0F, 6.0F)
            )
        );

    // -------------------- Cheese Aging Racks --------------------
    // One block tall, two shelves, one cheese slot per shelf.
    // Crafting recipes are intentionally deferred; all wood variants are
    // registered now so models, interactions and persistence can be tested.
    public static final Block OAK_CHEESE_AGING_RACK =
        registerRack("oak", Blocks.OAK_PLANKS);

    public static final Block SPRUCE_CHEESE_AGING_RACK =
        registerRack("spruce", Blocks.SPRUCE_PLANKS);

    public static final Block BIRCH_CHEESE_AGING_RACK =
        registerRack("birch", Blocks.BIRCH_PLANKS);

    public static final Block JUNGLE_CHEESE_AGING_RACK =
        registerRack("jungle", Blocks.JUNGLE_PLANKS);

    public static final Block ACACIA_CHEESE_AGING_RACK =
        registerRack("acacia", Blocks.ACACIA_PLANKS);

    public static final Block DARK_OAK_CHEESE_AGING_RACK =
        registerRack("dark_oak", Blocks.DARK_OAK_PLANKS);

    public static final Block MANGROVE_CHEESE_AGING_RACK =
        registerRack("mangrove", Blocks.MANGROVE_PLANKS);

    public static final Block CHERRY_CHEESE_AGING_RACK =
        registerRack("cherry", Blocks.CHERRY_PLANKS);

    public static final Block PALE_OAK_CHEESE_AGING_RACK =
        registerRack("pale_oak", Blocks.PALE_OAK_PLANKS);

    public static final Block BAMBOO_CHEESE_AGING_RACK =
        registerRack("bamboo", Blocks.BAMBOO_PLANKS);

    public static final Block CRIMSON_CHEESE_AGING_RACK =
        registerRack("crimson", Blocks.CRIMSON_PLANKS);

    public static final Block WARPED_CHEESE_AGING_RACK =
        registerRack("warped", Blocks.WARPED_PLANKS);

    // Internal block used when a fresh cheese wheel is placed on a flat
    // surface. It intentionally has no BlockItem; placement happens through
    // AgingCheeseItem and any incomplete progress is world-only.
    public static final ResourceKey<Block> AGING_CHEESE_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("aging_cheese")
        );

    public static final Block AGING_CHEESE =
        registerBlockOnly(
            AGING_CHEESE_KEY,
            new AgingCheeseBlock(
                Block.Properties
                    .ofFullCopy(Blocks.CAKE)
                    .setId(AGING_CHEESE_KEY)
                    .noOcclusion()
                    .noTerrainParticles()
                    .strength(0.2F)
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


    private static Block registerRack(
        String woodName,
        Block plankTemplate
    ) {
        ResourceKey<Block> blockKey =
            ResourceKey.create(
                BuiltInRegistries.BLOCK.key(),
                ModRegistries.id(
                    woodName + "_cheese_aging_rack"
                )
            );

        return register(
            blockKey,
            new CheeseAgingRackBlock(
                Block.Properties
                    .ofFullCopy(plankTemplate)
                    .setId(blockKey)
                    .noOcclusion()
                    .strength(2.0F, 3.0F)
            )
        );
    }

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
