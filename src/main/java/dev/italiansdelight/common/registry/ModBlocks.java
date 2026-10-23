package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.CheeseHookBlock;
import dev.italiansdelight.common.block.CheeseHookOccupiedSpaceBlock;
import dev.italiansdelight.common.block.CheeseAgingRackBlock;
import dev.italiansdelight.common.block.AgingCheeseBlock;
import dev.italiansdelight.common.block.CardoonBlock;
import dev.italiansdelight.common.block.CultivatedCardoonCropBlock;
import dev.italiansdelight.common.block.BasilCropBlock;
import dev.italiansdelight.common.block.WildBasilBlock;
import dev.italiansdelight.common.block.SaltCauldronBlock;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.PizzaOvenBlock;
import dev.italiansdelight.common.block.CookedPizzaBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.minecraft.world.level.block.SoundType;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

/**
 * Central registry for mod blocks and their BlockItems when they need to be placeable from the inventory.
 */

public final class ModBlocks {

    private ModBlocks() {
    }

    // -------------------- Registered blocks --------------------
    // -------------------- Olive wood (Orchard Update 1.1.0) --------------------
    // These five baseline building blocks only reference the newer olive_*
    // artwork. Rustic's legacy log_olive/leaves_olive/sapling_olive are excluded.
    public static final ResourceKey<Block> OLIVE_LOG_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_log")
    );
    public static final Block OLIVE_LOG = register(
        OLIVE_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG).setId(OLIVE_LOG_KEY))
    );

    public static final ResourceKey<Block> OLIVE_WOOD_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_wood")
    );
    public static final Block OLIVE_WOOD = register(
        OLIVE_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).setId(OLIVE_WOOD_KEY))
    );

    public static final ResourceKey<Block> STRIPPED_OLIVE_LOG_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_olive_log")
    );
    public static final Block STRIPPED_OLIVE_LOG = register(
        STRIPPED_OLIVE_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).setId(STRIPPED_OLIVE_LOG_KEY))
    );

    public static final ResourceKey<Block> STRIPPED_OLIVE_WOOD_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_olive_wood")
    );
    public static final Block STRIPPED_OLIVE_WOOD = register(
        STRIPPED_OLIVE_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).setId(STRIPPED_OLIVE_WOOD_KEY))
    );

    public static final ResourceKey<Block> OLIVE_PLANKS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_planks")
    );
    public static final Block OLIVE_PLANKS = register(
        OLIVE_PLANKS_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS).setId(OLIVE_PLANKS_KEY))
    );



    // Phase A of the Orchard Update: six vanilla-equivalent building blocks.
    // All their block and inventory models reuse the existing olive_planks.png.
    // BlockSetType/WoodType are registered once for future doors and signs.
    public static final BlockSetType OLIVE_BLOCK_SET_TYPE =
        BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(ModRegistries.id("olive"));
    public static final WoodType OLIVE_WOOD_TYPE =
        new WoodTypeBuilder().register(ModRegistries.id("olive"), OLIVE_BLOCK_SET_TYPE);

    public static final ResourceKey<Block> OLIVE_STAIRS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_stairs")
    );
    public static final Block OLIVE_STAIRS = register(
        OLIVE_STAIRS_KEY,
        new StairBlock(OLIVE_PLANKS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.OAK_STAIRS).setId(OLIVE_STAIRS_KEY))
    );

    public static final ResourceKey<Block> OLIVE_SLAB_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_slab")
    );
    public static final Block OLIVE_SLAB = register(
        OLIVE_SLAB_KEY,
        new SlabBlock(Block.Properties.ofFullCopy(Blocks.OAK_SLAB).setId(OLIVE_SLAB_KEY))
    );

    public static final ResourceKey<Block> OLIVE_FENCE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_fence")
    );
    public static final Block OLIVE_FENCE = register(
        OLIVE_FENCE_KEY,
        new FenceBlock(Block.Properties.ofFullCopy(Blocks.OAK_FENCE).setId(OLIVE_FENCE_KEY))
    );

    public static final ResourceKey<Block> OLIVE_FENCE_GATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_fence_gate")
    );
    public static final Block OLIVE_FENCE_GATE = register(
        OLIVE_FENCE_GATE_KEY,
        new FenceGateBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).setId(OLIVE_FENCE_GATE_KEY))
    );

    public static final ResourceKey<Block> OLIVE_BUTTON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_button")
    );
    public static final Block OLIVE_BUTTON = register(
        OLIVE_BUTTON_KEY,
        new ButtonBlock(OLIVE_BLOCK_SET_TYPE, 30, Block.Properties.ofFullCopy(Blocks.OAK_BUTTON).setId(OLIVE_BUTTON_KEY))
    );

    public static final ResourceKey<Block> OLIVE_PRESSURE_PLATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_pressure_plate")
    );
    public static final Block OLIVE_PRESSURE_PLATE = register(
        OLIVE_PRESSURE_PLATE_KEY,
        new PressurePlateBlock(OLIVE_BLOCK_SET_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).setId(OLIVE_PRESSURE_PLATE_KEY))
    );


    // Phase B: two pre-existing Italian's Delight olive textures and
    // standard vanilla door/trapdoor redstone, collision and interaction.
    public static final ResourceKey<Block> OLIVE_DOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_door"));
    public static final Block OLIVE_DOOR = register(
        OLIVE_DOOR_KEY,
        new DoorBlock(OLIVE_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_DOOR).setId(OLIVE_DOOR_KEY))
    );

    public static final ResourceKey<Block> OLIVE_TRAPDOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_trapdoor"));
    public static final Block OLIVE_TRAPDOOR = register(
        OLIVE_TRAPDOOR_KEY,
        new TrapDoorBlock(OLIVE_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).setId(OLIVE_TRAPDOOR_KEY))
    );

    // All cheese aging racks are behaviorally identical; only the wooden
    // construction material and crafting ingredients vary.
    public static final Block OLIVE_CHEESE_AGING_RACK =
        registerRack("olive", OLIVE_PLANKS);

    public static final ResourceKey<Block> CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cardoon")
    );

    // Historical ID stays wild to preserve old worlds and flower pots.
    public static final Block CARDOON = register(
        CARDOON_KEY,
        new CardoonBlock(Block.Properties.ofFullCopy(Blocks.ALLIUM).setId(CARDOON_KEY))
    );

    // New crop + BlockItem; harvested cardoon replants directly as with basil.
    public static final ResourceKey<Block> CULTIVATED_CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cultivated_cardoon")
    );
    public static final Block CULTIVATED_CARDOON = register(
        CULTIVATED_CARDOON_KEY,
        new CultivatedCardoonCropBlock(
            Block.Properties.ofFullCopy(Blocks.WHEAT).setId(CULTIVATED_CARDOON_KEY)
        )
    );

    public static final ResourceKey<Block> POTTED_CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_cardoon")
    );

    public static final Block POTTED_CARDOON = registerBlockOnly(
        POTTED_CARDOON_KEY,
        new FlowerPotBlock(CARDOON,
            Block.Properties.ofFullCopy(Blocks.POTTED_ALLIUM).setId(POTTED_CARDOON_KEY))
    );

    // -------------------- Basil --------------------
    // The cultivated crop uses the same registry id for its BlockItem, so the
    // harvested basil itself can be replanted directly on farmland.
    public static final ResourceKey<Block> BASIL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("basil")
    );

    public static final Block BASIL = register(
        BASIL_KEY,
        new BasilCropBlock(
            Block.Properties.ofFullCopy(Blocks.WHEAT).setId(BASIL_KEY)
        )
    );

    // Wild basil is a separate decorative/worldgen form. Breaking it yields
    // cultivated basil through its loot table.
    public static final ResourceKey<Block> WILD_BASIL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("wild_basil")
    );

    public static final Block WILD_BASIL = register(
        WILD_BASIL_KEY,
        new WildBasilBlock(
            Block.Properties.ofFullCopy(Blocks.ALLIUM).setId(WILD_BASIL_KEY)
        )
    );

    // World representation of Farmer's Delight wheat dough; no additional dough item.
    public static final ResourceKey<Block> PIZZA_DOUGH_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("pizza_dough")
    );
    public static final Block PIZZA_DOUGH = registerBlockOnly(
        PIZZA_DOUGH_KEY,
        new PizzaDoughBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .setId(PIZZA_DOUGH_KEY).noCollision().noOcclusion().strength(0.2F))
    );

    // One baked pizza served as four physical quarters. No BlockItem:
    // players place the data-carrying cooked_pizza directly via the same
    // interaction handler used for raw pizza.
    public static final ResourceKey<Block> COOKED_PIZZA_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cooked_pizza"));
    public static final Block COOKED_PIZZA = registerBlockOnly(
        COOKED_PIZZA_KEY,
        new CookedPizzaBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .setId(COOKED_PIZZA_KEY).noCollision().noOcclusion().strength(0.2F)));

    // One-block, pizza-only furnace. BlockItem is registered automatically.
    public static final ResourceKey<Block> PIZZA_OVEN_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("pizza_oven"));
    public static final Block PIZZA_OVEN = register(
        PIZZA_OVEN_KEY,
        new PizzaOvenBlock(Block.Properties.ofFullCopy(Blocks.FURNACE)
            .setId(PIZZA_OVEN_KEY).strength(3.5F)));

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
                    .ofFullCopy(Blocks.IRON_BLOCK)
                    .setId(CHEESE_HOOK_KEY)
                    .noOcclusion()
                    .noCollision()
                    .sound(SoundType.LANTERN)
                    .strength(3.0F, 6.0F)
            )
        );

    // Invisible helper occupying the block volume used by a hanging cheese.
    // It has no item and exists only while a Cheese Hook above is occupied.
    public static final ResourceKey<Block> CHEESE_HOOK_OCCUPIED_SPACE_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_hook_occupied_space")
        );

    public static final Block CHEESE_HOOK_OCCUPIED_SPACE =
        registerBlockOnly(
            CHEESE_HOOK_OCCUPIED_SPACE_KEY,
            new CheeseHookOccupiedSpaceBlock(
                Block.Properties
                    .ofFullCopy(Blocks.IRON_BLOCK)
                    .setId(CHEESE_HOOK_OCCUPIED_SPACE_KEY)
                    .noCollision()
                    .noOcclusion()
                    .noTerrainParticles()
                    .noLootTable()
                    .strength(-1.0F, 3600000.0F)
            )
        );

    // -------------------- Cheese Aging Racks --------------------
    // One block tall, two shelves, one cheese slot per shelf.
    // Each wood variant has its own matching fence + planks crafting recipe.
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
        // Vanilla-style log stripping and fire spread: performed only after
        // the registered block fields are initialized.
        StrippableBlockRegistry.register(OLIVE_LOG, STRIPPED_OLIVE_LOG);
        StrippableBlockRegistry.register(OLIVE_WOOD, STRIPPED_OLIVE_WOOD);
        FlammableBlockRegistry fire = FlammableBlockRegistry.getDefaultInstance();
        fire.add(OLIVE_LOG, 5, 5);
        fire.add(OLIVE_WOOD, 5, 5);
        fire.add(STRIPPED_OLIVE_LOG, 5, 5);
        fire.add(STRIPPED_OLIVE_WOOD, 5, 5);
        fire.add(OLIVE_PLANKS, 5, 20);
        fire.add(OLIVE_STAIRS, 5, 20);
        fire.add(OLIVE_SLAB, 5, 20);
        fire.add(OLIVE_FENCE, 5, 20);
        fire.add(OLIVE_FENCE_GATE, 5, 20);
        fire.add(OLIVE_BUTTON, 5, 20);
        fire.add(OLIVE_PRESSURE_PLATE, 5, 20);
        fire.add(OLIVE_DOOR, 5, 20);
        fire.add(OLIVE_TRAPDOOR, 5, 20);
    }
}
