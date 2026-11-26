package dev.italiansdelight.common.registry;

import dev.italiansdelight.common.block.CrushingTubBlock;
import dev.italiansdelight.common.block.OliveOilCauldronBlock;
import dev.italiansdelight.common.block.FluidCauldronBlock;
import dev.italiansdelight.common.block.BarrelBlock;
import dev.italiansdelight.common.block.BrewingBarrelBlock;
import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.CheeseHookBlock;
import dev.italiansdelight.common.block.CheeseHookOccupiedSpaceBlock;
import dev.italiansdelight.common.block.CheeseAgingRackBlock;
import dev.italiansdelight.common.block.AgingCheeseBlock;
import dev.italiansdelight.common.block.CardoonBlock;
import dev.italiansdelight.common.block.CultivatedCardoonCropBlock;
import dev.italiansdelight.common.block.OliveLeavesBlock;
import dev.italiansdelight.common.block.AlmondLeavesBlock;
import dev.italiansdelight.common.block.FruitCherryLeavesBlock;
import dev.italiansdelight.common.block.BasilCropBlock;
import dev.italiansdelight.common.block.RepeatHarvestCropBlock;
import dev.italiansdelight.common.block.CropStakeBlock;
import dev.italiansdelight.common.block.HorizontalRopeBlock;
import dev.italiansdelight.common.block.StakedTomatoBlock;
import dev.italiansdelight.common.block.GrapeStemBlock;
import dev.italiansdelight.common.block.GrapeLeavesBlock;
import dev.italiansdelight.common.block.ChiliCropBlock;
import dev.italiansdelight.common.block.HangingChiliBlock;
import dev.italiansdelight.common.block.BuddingChiliBlock;
import dev.italiansdelight.common.block.WildBasilBlock;
import dev.italiansdelight.common.block.WildChiliBlock;
import dev.italiansdelight.common.block.WildGrapeBlock;
import dev.italiansdelight.common.block.SaltCauldronBlock;
import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.PizzaOvenBlock;
import dev.italiansdelight.common.block.CookedPizzaBlock;
import dev.italiansdelight.common.block.AlmondCakeBlock;
import dev.italiansdelight.common.block.CherryTartBlock;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.core.registries.Registries;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.SoundType;

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
    public static Block OLIVE_LOG;

    public static final ResourceKey<Block> OLIVE_WOOD_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_wood")
    );
    public static Block OLIVE_WOOD;

    public static final ResourceKey<Block> STRIPPED_OLIVE_LOG_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_olive_log")
    );
    public static Block STRIPPED_OLIVE_LOG;

    public static final ResourceKey<Block> STRIPPED_OLIVE_WOOD_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_olive_wood")
    );
    public static Block STRIPPED_OLIVE_WOOD;

    public static final ResourceKey<Block> OLIVE_PLANKS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_planks")
    );
    public static Block OLIVE_PLANKS;




    // Original, non-vanilla fruit cherry wood family (Orchard 1.1.0).
    // Fruit leaves and sapling are registered below; fruiting and natural generation remain deferred.
    public static final ResourceKey<Block> FRUIT_CHERRY_LOG_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_log"));
    public static Block FRUIT_CHERRY_LOG;
    public static final ResourceKey<Block> FRUIT_CHERRY_WOOD_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_wood"));
    public static Block FRUIT_CHERRY_WOOD;
    public static final ResourceKey<Block> STRIPPED_FRUIT_CHERRY_LOG_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_fruit_cherry_log"));
    public static Block STRIPPED_FRUIT_CHERRY_LOG;
    public static final ResourceKey<Block> STRIPPED_FRUIT_CHERRY_WOOD_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_fruit_cherry_wood"));
    public static Block STRIPPED_FRUIT_CHERRY_WOOD;
    public static final ResourceKey<Block> FRUIT_CHERRY_PLANKS_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_planks"));
    public static Block FRUIT_CHERRY_PLANKS;

    // Fruit cherry vanilla-style derivatives: reuse only approved fruit_cherry_planks art.
    public static final BlockSetType FRUIT_CHERRY_BLOCK_SET_TYPE =
        BlockSetType.register(new BlockSetType("italiansdelight:fruit_cherry"));
    public static final WoodType FRUIT_CHERRY_WOOD_TYPE =
        WoodType.register(new WoodType("italiansdelight:fruit_cherry", FRUIT_CHERRY_BLOCK_SET_TYPE));

    public static final ResourceKey<Block> FRUIT_CHERRY_STAIRS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_stairs")
    );
    public static Block FRUIT_CHERRY_STAIRS;

    public static final ResourceKey<Block> FRUIT_CHERRY_SLAB_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_slab")
    );
    public static Block FRUIT_CHERRY_SLAB;

    public static final ResourceKey<Block> FRUIT_CHERRY_FENCE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_fence")
    );
    public static Block FRUIT_CHERRY_FENCE;

    public static final ResourceKey<Block> FRUIT_CHERRY_FENCE_GATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_fence_gate")
    );
    public static Block FRUIT_CHERRY_FENCE_GATE;

    public static final ResourceKey<Block> FRUIT_CHERRY_BUTTON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_button")
    );
    public static Block FRUIT_CHERRY_BUTTON;

    public static final ResourceKey<Block> FRUIT_CHERRY_PRESSURE_PLATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_pressure_plate")
    );
    public static Block FRUIT_CHERRY_PRESSURE_PLATE;



    
    // Fruit cherry door and trapdoor: standard wooden behavior and approved WoodType.
    // Thin door models control the visible hinge edge, preventing mirrored hinges.
    public static final ResourceKey<Block> FRUIT_CHERRY_DOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_door"));
    public static Block FRUIT_CHERRY_DOOR;

    public static final ResourceKey<Block> FRUIT_CHERRY_TRAPDOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_trapdoor"));
    public static Block FRUIT_CHERRY_TRAPDOOR;

    // Almond originals: log, stripped log and planks (art approved).
    public static final ResourceKey<Block> ALMOND_LOG_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_log"));
    public static Block ALMOND_LOG;
    public static final ResourceKey<Block> STRIPPED_ALMOND_LOG_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_almond_log"));
    public static Block STRIPPED_ALMOND_LOG;
    public static final ResourceKey<Block> ALMOND_PLANKS_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_planks"));
    public static Block ALMOND_PLANKS;


    // Approved almond_log texture (six faces), ordinary and stripped wood.
    public static final ResourceKey<Block> ALMOND_WOOD_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_wood"));
    public static Block ALMOND_WOOD;
    public static final ResourceKey<Block> STRIPPED_ALMOND_WOOD_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("stripped_almond_wood"));
    public static Block STRIPPED_ALMOND_WOOD;

    // Standard wooden building derivatives: only approved almond_planks art.
    public static final BlockSetType ALMOND_BLOCK_SET_TYPE =
        BlockSetType.register(new BlockSetType("italiansdelight:almond"));
    public static final WoodType ALMOND_WOOD_TYPE =
        WoodType.register(new WoodType("italiansdelight:almond", ALMOND_BLOCK_SET_TYPE));

    public static final ResourceKey<Block> ALMOND_STAIRS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_stairs")
    );
    public static Block ALMOND_STAIRS;

    public static final ResourceKey<Block> ALMOND_SLAB_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_slab")
    );
    public static Block ALMOND_SLAB;

    public static final ResourceKey<Block> ALMOND_FENCE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_fence")
    );
    public static Block ALMOND_FENCE;

    public static final ResourceKey<Block> ALMOND_FENCE_GATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_fence_gate")
    );
    public static Block ALMOND_FENCE_GATE;

    public static final ResourceKey<Block> ALMOND_BUTTON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_button")
    );
    public static Block ALMOND_BUTTON;

    public static final ResourceKey<Block> ALMOND_PRESSURE_PLATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_pressure_plate")
    );
    public static Block ALMOND_PRESSURE_PLATE;



    // Original matching almond-blossom ornament, vanilla redstone and hinges.
    public static final ResourceKey<Block> ALMOND_DOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_door"));
    public static Block ALMOND_DOOR;

    public static final ResourceKey<Block> ALMOND_TRAPDOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_trapdoor"));
    public static Block ALMOND_TRAPDOOR;

    // Fruit Cherry: green, non-ornamental foliage and cultivated 1x1
    // sapling. Reuses the approved Fruit Cherry log (never vanilla cherry).
    // Fruiting leaves: three WHITE blossom phases, green cherry and red harvest.
    // Natural world generation remains deferred.
    public static final ResourceKey<Block> FRUIT_CHERRY_LEAVES_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_leaves"));
    public static Block FRUIT_CHERRY_LEAVES;

    public static final ResourceKey<ConfiguredFeature<?, ?>> FRUIT_CHERRY_TREE_FEATURE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, ModRegistries.id("fruit_cherry_tree"));
    public static final TreeGrower FRUIT_CHERRY_TREE_GROWER = new TreeGrower(
        "italiansdelight:fruit_cherry", Optional.empty(), Optional.of(FRUIT_CHERRY_TREE_FEATURE), Optional.empty()
    );
    public static final ResourceKey<Block> FRUIT_CHERRY_SAPLING_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_sapling"));
    public static Block FRUIT_CHERRY_SAPLING;
    public static final ResourceKey<Block> POTTED_FRUIT_CHERRY_SAPLING_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_fruit_cherry_sapling"));
    public static Block POTTED_FRUIT_CHERRY_SAPLING;

    // Almond (Orchard 1.1.0): approved natural foliage with a five-stage
    // pink-white blossom -> green husk -> dry almond harvest cycle.
    // Conventional decay, planted saplings, and client-only biome tint persist.
    // Natural biome generation is still deferred.
    public static final ResourceKey<Block> ALMOND_LEAVES_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_leaves"));
    public static Block ALMOND_LEAVES;

    public static final ResourceKey<ConfiguredFeature<?, ?>> ALMOND_TREE_FEATURE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, ModRegistries.id("almond_tree"));
    public static final TreeGrower ALMOND_TREE_GROWER = new TreeGrower(
        "italiansdelight:almond", Optional.empty(), Optional.of(ALMOND_TREE_FEATURE), Optional.empty()
    );
    public static final ResourceKey<Block> ALMOND_SAPLING_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_sapling"));
    public static Block ALMOND_SAPLING;
    public static final ResourceKey<Block> POTTED_ALMOND_SAPLING_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_almond_sapling"));
    public static Block POTTED_ALMOND_SAPLING;

    // First Orchard foliage block: use the existing non-Rustic 16x16
    // olive_leaves.png. OliveLeavesBlock extends the vanilla leaf implementation
    // and preserves its fixed silver-green palette while adding fruit ripening.
    // Vanilla leaves continue to handle decay,
    // waterlogging, persistent placement and log distance. Particle chance
    // 0 avoids inappropriate vanilla cherry/pale-leaf falling particles.
    // Olive logs are already in #minecraft:logs via logs_that_burn.
    public static final ResourceKey<Block> OLIVE_LEAVES_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_leaves"));
    public static Block OLIVE_LEAVES;


    // One seedling: regular olive. Four identical seedlings in a 2x2:
    // ancient (secular) olive, via vanilla TreeGrower mega-tree detection.
    // Features are data-driven, using our non-Rustic log and silver leaves.
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVE_TREE_FEATURE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, ModRegistries.id("olive_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_OLIVE_TREE_FEATURE =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, ModRegistries.id("ancient_olive_tree"));
    public static final TreeGrower OLIVE_TREE_GROWER = new TreeGrower(
        "italiansdelight:olive", Optional.of(ANCIENT_OLIVE_TREE_FEATURE),
        Optional.of(OLIVE_TREE_FEATURE), Optional.empty()
    );
    public static final ResourceKey<Block> OLIVE_SAPLING_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_sapling"));
    public static Block OLIVE_SAPLING;

    public static final ResourceKey<Block> POTTED_OLIVE_SAPLING_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_olive_sapling")
    );
    public static Block POTTED_OLIVE_SAPLING;

    // Phase A of the Orchard Update: six vanilla-equivalent building blocks.
    // All their block and inventory models reuse the existing olive_planks.png.
    // BlockSetType/WoodType are registered once for future doors and signs.
    public static final BlockSetType OLIVE_BLOCK_SET_TYPE =
        BlockSetType.register(new BlockSetType("italiansdelight:olive"));
    public static final WoodType OLIVE_WOOD_TYPE =
        WoodType.register(new WoodType("italiansdelight:olive", OLIVE_BLOCK_SET_TYPE));

    public static final ResourceKey<Block> OLIVE_STAIRS_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_stairs")
    );
    public static Block OLIVE_STAIRS;

    public static final ResourceKey<Block> OLIVE_SLAB_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_slab")
    );
    public static Block OLIVE_SLAB;

    public static final ResourceKey<Block> OLIVE_FENCE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_fence")
    );
    public static Block OLIVE_FENCE;

    public static final ResourceKey<Block> OLIVE_FENCE_GATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_fence_gate")
    );
    public static Block OLIVE_FENCE_GATE;

    public static final ResourceKey<Block> OLIVE_BUTTON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_button")
    );
    public static Block OLIVE_BUTTON;

    public static final ResourceKey<Block> OLIVE_PRESSURE_PLATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_pressure_plate")
    );
    public static Block OLIVE_PRESSURE_PLATE;


    // Phase B: two pre-existing Italian's Delight olive textures and
    // standard vanilla door/trapdoor redstone, collision and interaction.
    public static final ResourceKey<Block> OLIVE_DOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_door"));
    public static Block OLIVE_DOOR;

    public static final ResourceKey<Block> OLIVE_TRAPDOOR_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_trapdoor"));
    public static Block OLIVE_TRAPDOOR;

    // All cheese aging racks are behaviorally identical; only the wooden
    // construction material and crafting ingredients vary.
    public static Block OLIVE_CHEESE_AGING_RACK;
    public static Block ALMOND_CHEESE_AGING_RACK;
    public static Block FRUIT_CHERRY_CHEESE_AGING_RACK;

    // Olive signs (Minecraft 26.2 ordinary block models) and Farmer's Delight
    // cabinet. Sign block-items are registered as two SignItem variants,
    // not four BlockItems, so that wall/ceiling placement uses the same item.
    public static final ResourceKey<Block> OLIVE_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_sign"));
    public static Block OLIVE_SIGN;

    public static final ResourceKey<Block> OLIVE_WALL_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_wall_sign"));
    public static Block OLIVE_WALL_SIGN;

    public static final ResourceKey<Block> OLIVE_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_hanging_sign"));
    public static Block OLIVE_HANGING_SIGN;

    public static final ResourceKey<Block> OLIVE_WALL_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_wall_hanging_sign"));
    public static Block OLIVE_WALL_HANGING_SIGN;


    public static final ResourceKey<Block> OLIVE_CABINET_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_cabinet"));
    public static Block OLIVE_CABINET;

    // Almond signs, hanging signs and functional FD cabinet.
    // Almond signs (Minecraft 26.2 ordinary block models) and Farmer's Delight
    // cabinet. Sign block-items are registered as two SignItem variants,
    // not four BlockItems, so that wall/ceiling placement uses the same item.
    public static final ResourceKey<Block> ALMOND_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_sign"));
    public static Block ALMOND_SIGN;

    public static final ResourceKey<Block> ALMOND_WALL_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_wall_sign"));
    public static Block ALMOND_WALL_SIGN;

    public static final ResourceKey<Block> ALMOND_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_hanging_sign"));
    public static Block ALMOND_HANGING_SIGN;

    public static final ResourceKey<Block> ALMOND_WALL_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_wall_hanging_sign"));
    public static Block ALMOND_WALL_HANGING_SIGN;

    // Original Fruit Cherry signs, normal + wall and hanging + wall.
    // No autogenerated BlockItems: ModSigns registers exactly two SignItem IDs.
    public static final ResourceKey<Block> FRUIT_CHERRY_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_sign"));
    public static Block FRUIT_CHERRY_SIGN;

    public static final ResourceKey<Block> FRUIT_CHERRY_WALL_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_wall_sign"));
    public static Block FRUIT_CHERRY_WALL_SIGN;

    public static final ResourceKey<Block> FRUIT_CHERRY_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_hanging_sign"));
    public static Block FRUIT_CHERRY_HANGING_SIGN;

    public static final ResourceKey<Block> FRUIT_CHERRY_WALL_HANGING_SIGN_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_wall_hanging_sign"));
    public static Block FRUIT_CHERRY_WALL_HANGING_SIGN;


    public static final ResourceKey<Block> ALMOND_CABINET_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_cabinet"));
    public static Block ALMOND_CABINET;

    // Fruit Cherry: Farmer's Delight 27-slot cabinet.

    public static final ResourceKey<Block> FRUIT_CHERRY_CABINET_KEY =
        ResourceKey.create(BuiltInRegistries.BLOCK.key(), ModRegistries.id("fruit_cherry_cabinet"));
    public static Block FRUIT_CHERRY_CABINET;

    public static final ResourceKey<Block> CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cardoon")
    );

    // Historical ID stays wild to preserve old worlds and flower pots.
    public static Block CARDOON;

    // New crop + BlockItem; harvested cardoon replants directly as with basil.
    public static final ResourceKey<Block> CULTIVATED_CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cultivated_cardoon")
    );
    public static Block CULTIVATED_CARDOON;

    public static final ResourceKey<Block> POTTED_CARDOON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("potted_cardoon")
    );

    public static Block POTTED_CARDOON;

    // Farmer's Delight-style placeable four-portion Almond Cake. The
    // corresponding BlockItem has the same "italiansdelight:almond_cake" ID
    // previously used by the unplaceable intermediate, preserving saves.
    public static final ResourceKey<Block> ALMOND_CAKE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_cake"));
    public static Block ALMOND_CAKE;

    // Placeable cherry tart with four edible or knife-cut servings.
    public static final ResourceKey<Block> CHERRY_TART_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cherry_tart"));
    public static Block CHERRY_TART;

    // Reversible nine-crop Farmer's Delight-style storage crates.
    public static final ResourceKey<Block> OLIVE_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_crate"));
    public static Block OLIVE_CRATE;

    public static final ResourceKey<Block> ALMOND_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("almond_crate"));
    public static Block ALMOND_CRATE;

    public static final ResourceKey<Block> CHERRY_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cherry_crate"));
    public static Block CHERRY_CRATE;

    public static final ResourceKey<Block> BASIL_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("basil_crate"));
    public static Block BASIL_CRATE;

    public static final ResourceKey<Block> CARDOON_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cardoon_crate"));
    public static Block CARDOON_CRATE;

    public static final ResourceKey<Block> GRAPE_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("grape_crate"));
    public static Block GRAPE_CRATE;

    public static final ResourceKey<Block> WHITE_GRAPE_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("white_grape_crate"));
    public static Block WHITE_GRAPE_CRATE;

    // Farmer's Delight-style packed pepper crates (nine items each).

    public static final ResourceKey<Block> CHILI_PEPPER_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("chili_pepper_crate"));
    public static Block CHILI_PEPPER_CRATE;

    public static final ResourceKey<Block> GHOST_PEPPER_CRATE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("ghost_pepper_crate"));
    public static Block GHOST_PEPPER_CRATE;

    // -------------------- Basil --------------------
    // The cultivated crop uses the same registry id for its BlockItem, so the
    // harvested basil itself can be replanted directly on farmland.
    public static final ResourceKey<Block> BASIL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("basil")
    );

    public static Block BASIL;

    // Wild basil is a separate decorative/worldgen form. Breaking it yields
    // cultivated basil through its loot table.
    public static final ResourceKey<Block> WILD_BASIL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("wild_basil")
    );

    public static Block WILD_BASIL;

    // Wild peppers are a separate overworld plant; breaking them yields
    // the existing chili seeds, while cultivated chili remains unchanged.
    public static final ResourceKey<Block> WILD_CHILI_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("wild_chili")
    );
    public static Block WILD_CHILI;

    // FDR wild crop: break for seeds / occasional fruit; shears collect whole plant.
    public static final ResourceKey<Block> WILD_GRAPE_VINE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("wild_grape_vine"));
    public static Block WILD_GRAPE_VINE;

    public static final ResourceKey<Block> WILD_WHITE_GRAPE_VINE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("wild_white_grape_vine"));
    public static Block WILD_WHITE_GRAPE_VINE;

    // Decorative posts only: the original crop_stake is the oak variant.
    public static final ResourceKey<Block> CROP_STAKE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("crop_stake"));
    public static Block CROP_STAKE;

    // All 13 other wood types (vanilla 1.21.1 and custom orchard woods).
    public static Block SPRUCE_CROP_STAKE;
    public static Block BIRCH_CROP_STAKE;
    public static Block JUNGLE_CROP_STAKE;
    public static Block ACACIA_CROP_STAKE;
    public static Block DARK_OAK_CROP_STAKE;
    public static Block MANGROVE_CROP_STAKE;
    public static Block CHERRY_CROP_STAKE;
    public static Block BAMBOO_CROP_STAKE;
    public static Block CRIMSON_CROP_STAKE;
    public static Block WARPED_CROP_STAKE;
    public static Block OLIVE_CROP_STAKE;
    public static Block ALMOND_CROP_STAKE;
    public static Block FRUIT_CHERRY_CROP_STAKE;

    // Reuses Farmer's Delight rope *items* and texture; no second rope item.
    // The axis is saved in the block state to allow horizontal placement.
    public static final ResourceKey<Block> HORIZONTAL_ROPE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("horizontal_rope"));
    public static Block HORIZONTAL_ROPE;

    public static final ResourceKey<Block> STAKED_TOMATOES_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("staked_tomatoes"));
    public static Block STAKED_TOMATOES;

    // Rustic-inspired grapevine and chili plants; seeds are BlockItems with their own IDs.
    // Both crops retain their roots on a successful empty-hand harvest.
    // Cultivated grape stems break as slowly as vanilla leaves (0.2F), not instantly like wheat.
    public static final ResourceKey<Block> GRAPE_STEM_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("grape_stem"));
    public static Block GRAPE_STEM;
    public static final ResourceKey<Item> GRAPE_SEEDS_KEY = ResourceKey.create(
        BuiltInRegistries.ITEM.key(), ModRegistries.id("grape_seeds"));
    public static Item GRAPE_SEEDS;

    // Grapes are harvested from horizontal canopy branches, not the stem.
    public static final ResourceKey<Block> GRAPE_LEAVES_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("grape_leaves"));
    public static Block GRAPE_LEAVES;

    // A second grape cultivar: distinct seeds, root and canopy IDs preserve
    // old-world purple vineyards and avoid accidental cross-variety changes.
    public static final ResourceKey<Block> WHITE_GRAPE_STEM_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("white_grape_stem"));
    public static Block WHITE_GRAPE_STEM;
    public static final ResourceKey<Item> WHITE_GRAPE_SEEDS_KEY = ResourceKey.create(
        BuiltInRegistries.ITEM.key(), ModRegistries.id("white_grape_seeds"));
    public static Item WHITE_GRAPE_SEEDS;
    public static final ResourceKey<Block> WHITE_GRAPE_LEAVES_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("white_grape_leaves"));
    public static Block WHITE_GRAPE_LEAVES;

    // FDR tomato-style seedling block; existing mature chili ID remains untouched.
    public static final ResourceKey<Block> BUDDING_CHILI_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("budding_chili"));
    public static Block BUDDING_CHILI;

    public static final ResourceKey<Block> CHILI_CROP_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("chili_crop"));
    public static Block CHILI_CROP;
    public static final ResourceKey<Item> CHILI_PEPPER_SEEDS_KEY = ResourceKey.create(
        BuiltInRegistries.ITEM.key(), ModRegistries.id("chili_pepper_seeds"));
    public static Item CHILI_PEPPER_SEEDS;

    public static final ResourceKey<Block> CHILI_ON_ROPE_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("chili_on_rope"));
    public static Block CHILI_ON_ROPE;

    // World representation of Farmer's Delight wheat dough; no additional dough item.
    public static final ResourceKey<Block> PIZZA_DOUGH_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("pizza_dough")
    );
    public static Block PIZZA_DOUGH;

    // One baked pizza served as four physical quarters. No BlockItem:
    // players place the data-carrying cooked_pizza directly via the same
    // interaction handler used for raw pizza.
    public static final ResourceKey<Block> COOKED_PIZZA_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("cooked_pizza"));
    public static Block COOKED_PIZZA;

    // One-block, pizza-only furnace. BlockItem is registered automatically.
    public static final ResourceKey<Block> PIZZA_OVEN_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("pizza_oven"));
    public static Block PIZZA_OVEN;

    // Rustic-inspired Crushing Tub — geometry and crafting only.
    // Registry helper automatically creates the matching placeable BlockItem.
    public static final ResourceKey<Block> CRUSHING_TUB_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("crushing_tub")
    );
    public static Block CRUSHING_TUB;

    // Rustic Barrel: persistent storage and a placeable BlockItem.
    public static final ResourceKey<Block> BARREL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("barrel"));
    public static Block BARREL;

    // Separate, closed Rustic-style fermentation machine: does not modify the
    // original liquid Barrel ID, inventory, tank, or save format.
    public static final ResourceKey<Block> BREWING_BARREL_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("brewing_barrel"));
    public static Block BREWING_BARREL;

    // All wood variants share the old fermentation machine's logic and save format.
    public static Block SPRUCE_BREWING_BARREL;
    public static Block BIRCH_BREWING_BARREL;
    public static Block JUNGLE_BREWING_BARREL;
    public static Block ACACIA_BREWING_BARREL;
    public static Block DARK_OAK_BREWING_BARREL;
    public static Block MANGROVE_BREWING_BARREL;
    public static Block CHERRY_BREWING_BARREL;
    public static Block BAMBOO_BREWING_BARREL;
    public static Block CRIMSON_BREWING_BARREL;
    public static Block WARPED_BREWING_BARREL;
    public static Block OLIVE_BREWING_BARREL;
    public static Block ALMOND_BREWING_BARREL;
    public static Block FRUIT_CHERRY_BREWING_BARREL;

    // Apple juice created by the Rustic Crushing Tub; real source/flowing liquid.
    public static final ResourceKey<Block> APPLE_JUICE_FLUID_BLOCK_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("apple_juice_fluid"));
    public static Block APPLE_JUICE_FLUID_BLOCK;

    // Keep the old IDs as Oak; register thirteen additional Rustic vessel wood types.
    public static Block SPRUCE_CRUSHING_TUB;
    public static Block SPRUCE_BARREL;
    public static Block BIRCH_CRUSHING_TUB;
    public static Block BIRCH_BARREL;
    public static Block JUNGLE_CRUSHING_TUB;
    public static Block JUNGLE_BARREL;
    public static Block ACACIA_CRUSHING_TUB;
    public static Block ACACIA_BARREL;
    public static Block DARK_OAK_CRUSHING_TUB;
    public static Block DARK_OAK_BARREL;
    public static Block MANGROVE_CRUSHING_TUB;
    public static Block MANGROVE_BARREL;
    public static Block CHERRY_CRUSHING_TUB;
    public static Block CHERRY_BARREL;
    public static Block BAMBOO_CRUSHING_TUB;
    public static Block BAMBOO_BARREL;
    public static Block CRIMSON_CRUSHING_TUB;
    public static Block CRIMSON_BARREL;
    public static Block WARPED_CRUSHING_TUB;
    public static Block WARPED_BARREL;
    public static Block OLIVE_CRUSHING_TUB;
    public static Block OLIVE_BARREL;
    public static Block ALMOND_CRUSHING_TUB;
    public static Block ALMOND_BARREL;
    public static Block FRUIT_CHERRY_CRUSHING_TUB;
    public static Block FRUIT_CHERRY_BARREL;

    // The fluid block itself is technical; only the bucket is visible in Creative.
    public static final ResourceKey<Block> OLIVE_OIL_FLUID_BLOCK_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_oil_fluid"));
    public static Block OLIVE_OIL_FLUID_BLOCK;

    // Filled olive-oil cauldron is a technical block, not a separately crafted item.
    public static final ResourceKey<Block> OLIVE_OIL_CAULDRON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("olive_oil_cauldron"));
    public static Block OLIVE_OIL_CAULDRON;

    // One generic technical filled state, preserving the original vanilla empty cauldron.
    public static final ResourceKey<Block> FLUID_CAULDRON_KEY = ResourceKey.create(
        BuiltInRegistries.BLOCK.key(), ModRegistries.id("fluid_cauldron"));
    public static Block FLUID_CAULDRON;

    // Cheese Vat Block
    public static final ResourceKey<Block> CHEESE_VAT_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_vat")
        );

    public static Block CHEESE_VAT;



    // -------------------- Cheese Hook --------------------
    // Ceiling-mounted hook used by the hanging cheese system. Its first phase
    // registers only the physical block/model; Scamorza and Provolone
    // processing is connected in the next implementation step.
    public static final ResourceKey<Block> CHEESE_HOOK_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_hook")
        );

    public static Block CHEESE_HOOK;

    // Invisible helper occupying the block volume used by a hanging cheese.
    // It has no item and exists only while a Cheese Hook above is occupied.
    public static final ResourceKey<Block> CHEESE_HOOK_OCCUPIED_SPACE_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("cheese_hook_occupied_space")
        );

    public static Block CHEESE_HOOK_OCCUPIED_SPACE;

    // -------------------- Cheese Aging Racks --------------------
    // One block tall, two shelves, one cheese slot per shelf.
    // Each wood variant has its own matching fence + planks crafting recipe.
    public static Block OAK_CHEESE_AGING_RACK;

    public static Block SPRUCE_CHEESE_AGING_RACK;

    public static Block BIRCH_CHEESE_AGING_RACK;

    public static Block JUNGLE_CHEESE_AGING_RACK;

    public static Block ACACIA_CHEESE_AGING_RACK;

    public static Block DARK_OAK_CHEESE_AGING_RACK;

    public static Block MANGROVE_CHEESE_AGING_RACK;

    public static Block CHERRY_CHEESE_AGING_RACK;



    public static Block BAMBOO_CHEESE_AGING_RACK;

    public static Block CRIMSON_CHEESE_AGING_RACK;

    public static Block WARPED_CHEESE_AGING_RACK;

    // Internal block used when a fresh cheese wheel is placed on a flat
    // surface. It intentionally has no BlockItem; placement happens through
    // AgingCheeseItem and any incomplete progress is world-only.
    public static final ResourceKey<Block> AGING_CHEESE_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("aging_cheese")
        );

    public static Block AGING_CHEESE;

    // Internal state used when a full water cauldron has finished evaporating.
    // A BlockItem is registered so Creative Pick Block can copy and re-place
    // the exact salt-filled state. It is not added to the mod creative tab.
    public static final ResourceKey<Block> SALT_CAULDRON_KEY =
        ResourceKey.create(
            BuiltInRegistries.BLOCK.key(),
            ModRegistries.id("salt_cauldron")
        );

    public static Block SALT_CAULDRON;


    private record PendingItem(ResourceKey<Item> key, Block block) {}
    private static final List<PendingItem> BLOCK_ITEMS = new ArrayList<>();

    /** Register the real block instances in the BLOCK registry event.
     * Raw fields preserve the existing mod-wide references, but are only
     * assigned while their registry is writable.
     */
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            registerBlocks();
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            for (PendingItem pending : BLOCK_ITEMS) {
                Registry.register(BuiltInRegistries.ITEM, pending.key().location(),
                    new BlockItem(pending.block(), new Item.Properties()));
            }
            BLOCK_ITEMS.clear();
            GRAPE_SEEDS = Registry.register(BuiltInRegistries.ITEM,
        GRAPE_SEEDS_KEY.location(), new BlockItem(GRAPE_STEM, new Item.Properties()));
            WHITE_GRAPE_SEEDS = Registry.register(BuiltInRegistries.ITEM,
        WHITE_GRAPE_SEEDS_KEY.location(), new BlockItem(WHITE_GRAPE_STEM,
            new Item.Properties()));
            CHILI_PEPPER_SEEDS = Registry.register(BuiltInRegistries.ITEM,
        CHILI_PEPPER_SEEDS_KEY.location(), new BlockItem(BUDDING_CHILI,
            new Item.Properties()));
        }
    }

    private static void registerBlocks() {
        OLIVE_LOG = register(
        OLIVE_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG))
    );
        OLIVE_WOOD = register(
        OLIVE_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD))
    );
        STRIPPED_OLIVE_LOG = register(
        STRIPPED_OLIVE_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    );
        STRIPPED_OLIVE_WOOD = register(
        STRIPPED_OLIVE_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    );
        OLIVE_PLANKS = register(
        OLIVE_PLANKS_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    );
        FRUIT_CHERRY_LOG = register(
        FRUIT_CHERRY_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG))
    );
        FRUIT_CHERRY_WOOD = register(
        FRUIT_CHERRY_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD))
    );
        STRIPPED_FRUIT_CHERRY_LOG = register(
        STRIPPED_FRUIT_CHERRY_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    );
        STRIPPED_FRUIT_CHERRY_WOOD = register(
        STRIPPED_FRUIT_CHERRY_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    );
        FRUIT_CHERRY_PLANKS = register(
        FRUIT_CHERRY_PLANKS_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    );
        FRUIT_CHERRY_STAIRS = register(
        FRUIT_CHERRY_STAIRS_KEY,
        new StairBlock(FRUIT_CHERRY_PLANKS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.OAK_STAIRS))
    );
        FRUIT_CHERRY_SLAB = register(
        FRUIT_CHERRY_SLAB_KEY,
        new SlabBlock(Block.Properties.ofFullCopy(Blocks.OAK_SLAB))
    );
        FRUIT_CHERRY_FENCE = register(
        FRUIT_CHERRY_FENCE_KEY,
        new FenceBlock(Block.Properties.ofFullCopy(Blocks.OAK_FENCE))
    );
        FRUIT_CHERRY_FENCE_GATE = register(
        FRUIT_CHERRY_FENCE_GATE_KEY,
        new FenceGateBlock(FRUIT_CHERRY_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    );
        FRUIT_CHERRY_BUTTON = register(
        FRUIT_CHERRY_BUTTON_KEY,
        new ButtonBlock(FRUIT_CHERRY_BLOCK_SET_TYPE, 30, Block.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    );
        FRUIT_CHERRY_PRESSURE_PLATE = register(
        FRUIT_CHERRY_PRESSURE_PLATE_KEY,
        new PressurePlateBlock(FRUIT_CHERRY_BLOCK_SET_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    );
        FRUIT_CHERRY_DOOR = register(
        FRUIT_CHERRY_DOOR_KEY,
        new DoorBlock(FRUIT_CHERRY_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_DOOR))
    );
        FRUIT_CHERRY_TRAPDOOR = register(
        FRUIT_CHERRY_TRAPDOOR_KEY,
        new TrapDoorBlock(FRUIT_CHERRY_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    );
        ALMOND_LOG = register(
        ALMOND_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG))
    );
        STRIPPED_ALMOND_LOG = register(
        STRIPPED_ALMOND_LOG_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG))
    );
        ALMOND_PLANKS = register(
        ALMOND_PLANKS_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    );
        ALMOND_WOOD = register(
        ALMOND_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD))
    );
        STRIPPED_ALMOND_WOOD = register(
        STRIPPED_ALMOND_WOOD_KEY,
        new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD))
    );
        ALMOND_STAIRS = register(
        ALMOND_STAIRS_KEY,
        new StairBlock(ALMOND_PLANKS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.OAK_STAIRS))
    );
        ALMOND_SLAB = register(
        ALMOND_SLAB_KEY,
        new SlabBlock(Block.Properties.ofFullCopy(Blocks.OAK_SLAB))
    );
        ALMOND_FENCE = register(
        ALMOND_FENCE_KEY,
        new FenceBlock(Block.Properties.ofFullCopy(Blocks.OAK_FENCE))
    );
        ALMOND_FENCE_GATE = register(
        ALMOND_FENCE_GATE_KEY,
        new FenceGateBlock(ALMOND_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    );
        ALMOND_BUTTON = register(
        ALMOND_BUTTON_KEY,
        new ButtonBlock(ALMOND_BLOCK_SET_TYPE, 30, Block.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    );
        ALMOND_PRESSURE_PLATE = register(
        ALMOND_PRESSURE_PLATE_KEY,
        new PressurePlateBlock(ALMOND_BLOCK_SET_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    );
        ALMOND_DOOR = register(
        ALMOND_DOOR_KEY,
        new DoorBlock(ALMOND_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_DOOR))
    );
        ALMOND_TRAPDOOR = register(
        ALMOND_TRAPDOOR_KEY,
        new TrapDoorBlock(ALMOND_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    );
        FRUIT_CHERRY_LEAVES = register(
        FRUIT_CHERRY_LEAVES_KEY,
        new FruitCherryLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES))
    );
        FRUIT_CHERRY_SAPLING = register(
        FRUIT_CHERRY_SAPLING_KEY,
        new SaplingBlock(FRUIT_CHERRY_TREE_GROWER,
            Block.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    );
        POTTED_FRUIT_CHERRY_SAPLING = registerBlockOnly(
        POTTED_FRUIT_CHERRY_SAPLING_KEY,
        new FlowerPotBlock(FRUIT_CHERRY_SAPLING,
            Block.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    );
        ALMOND_LEAVES = register(
        ALMOND_LEAVES_KEY,
        new AlmondLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES))
    );
        ALMOND_SAPLING = register(
        ALMOND_SAPLING_KEY,
        new SaplingBlock(ALMOND_TREE_GROWER,
            Block.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    );
        POTTED_ALMOND_SAPLING = registerBlockOnly(
        POTTED_ALMOND_SAPLING_KEY,
        new FlowerPotBlock(ALMOND_SAPLING,
            Block.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    );
        OLIVE_LEAVES = register(
        OLIVE_LEAVES_KEY,
        new OliveLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES))
    );
        OLIVE_SAPLING = register(
        OLIVE_SAPLING_KEY,
        new SaplingBlock(OLIVE_TREE_GROWER,
            Block.Properties.ofFullCopy(Blocks.OAK_SAPLING))
    );
        POTTED_OLIVE_SAPLING = registerBlockOnly(
        POTTED_OLIVE_SAPLING_KEY,
        new FlowerPotBlock(OLIVE_SAPLING,
            Block.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING))
    );
        OLIVE_STAIRS = register(
        OLIVE_STAIRS_KEY,
        new StairBlock(OLIVE_PLANKS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.OAK_STAIRS))
    );
        OLIVE_SLAB = register(
        OLIVE_SLAB_KEY,
        new SlabBlock(Block.Properties.ofFullCopy(Blocks.OAK_SLAB))
    );
        OLIVE_FENCE = register(
        OLIVE_FENCE_KEY,
        new FenceBlock(Block.Properties.ofFullCopy(Blocks.OAK_FENCE))
    );
        OLIVE_FENCE_GATE = register(
        OLIVE_FENCE_GATE_KEY,
        new FenceGateBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))
    );
        OLIVE_BUTTON = register(
        OLIVE_BUTTON_KEY,
        new ButtonBlock(OLIVE_BLOCK_SET_TYPE, 30, Block.Properties.ofFullCopy(Blocks.OAK_BUTTON))
    );
        OLIVE_PRESSURE_PLATE = register(
        OLIVE_PRESSURE_PLATE_KEY,
        new PressurePlateBlock(OLIVE_BLOCK_SET_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))
    );
        OLIVE_DOOR = register(
        OLIVE_DOOR_KEY,
        new DoorBlock(OLIVE_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_DOOR))
    );
        OLIVE_TRAPDOOR = register(
        OLIVE_TRAPDOOR_KEY,
        new TrapDoorBlock(OLIVE_BLOCK_SET_TYPE,
            Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR))
    );
        OLIVE_CHEESE_AGING_RACK = registerRack("olive", OLIVE_PLANKS);
        ALMOND_CHEESE_AGING_RACK = registerRack("almond", ALMOND_PLANKS);
        FRUIT_CHERRY_CHEESE_AGING_RACK = registerRack("fruit_cherry", FRUIT_CHERRY_PLANKS);
        OLIVE_SIGN = registerBlockOnly(
        OLIVE_SIGN_KEY,
        new StandingSignBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_SIGN)
            )
    );
        OLIVE_WALL_SIGN = registerBlockOnly(
        OLIVE_WALL_SIGN_KEY,
        new WallSignBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)
            )
    );
        OLIVE_HANGING_SIGN = registerBlockOnly(
        OLIVE_HANGING_SIGN_KEY,
        new CeilingHangingSignBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)
            )
    );
        OLIVE_WALL_HANGING_SIGN = registerBlockOnly(
        OLIVE_WALL_HANGING_SIGN_KEY,
        new WallHangingSignBlock(OLIVE_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)
            )
    );
        OLIVE_CABINET = register(
        OLIVE_CABINET_KEY,
        new CabinetBlock(Block.Properties.ofFullCopy(Blocks.BARREL))
    );
        ALMOND_SIGN = registerBlockOnly(
        ALMOND_SIGN_KEY,
        new StandingSignBlock(ALMOND_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_SIGN)
            )
    );
        ALMOND_WALL_SIGN = registerBlockOnly(
        ALMOND_WALL_SIGN_KEY,
        new WallSignBlock(ALMOND_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)
            )
    );
        ALMOND_HANGING_SIGN = registerBlockOnly(
        ALMOND_HANGING_SIGN_KEY,
        new CeilingHangingSignBlock(ALMOND_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)
            )
    );
        ALMOND_WALL_HANGING_SIGN = registerBlockOnly(
        ALMOND_WALL_HANGING_SIGN_KEY,
        new WallHangingSignBlock(ALMOND_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)
            )
    );
        FRUIT_CHERRY_SIGN = registerBlockOnly(
        FRUIT_CHERRY_SIGN_KEY,
        new StandingSignBlock(FRUIT_CHERRY_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_SIGN)
            )
    );
        FRUIT_CHERRY_WALL_SIGN = registerBlockOnly(
        FRUIT_CHERRY_WALL_SIGN_KEY,
        new WallSignBlock(FRUIT_CHERRY_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)
            )
    );
        FRUIT_CHERRY_HANGING_SIGN = registerBlockOnly(
        FRUIT_CHERRY_HANGING_SIGN_KEY,
        new CeilingHangingSignBlock(FRUIT_CHERRY_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)
            )
    );
        FRUIT_CHERRY_WALL_HANGING_SIGN = registerBlockOnly(
        FRUIT_CHERRY_WALL_HANGING_SIGN_KEY,
        new WallHangingSignBlock(FRUIT_CHERRY_WOOD_TYPE, Block.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)
            )
    );
        ALMOND_CABINET = register(
        ALMOND_CABINET_KEY,
        new CabinetBlock(Block.Properties.ofFullCopy(Blocks.BARREL))
    );
        FRUIT_CHERRY_CABINET = register(
        FRUIT_CHERRY_CABINET_KEY,
        new CabinetBlock(Block.Properties.ofFullCopy(Blocks.BARREL))
    );
        CARDOON = register(
        CARDOON_KEY,
        new CardoonBlock(Block.Properties.ofFullCopy(Blocks.TALL_GRASS))
    );
        CULTIVATED_CARDOON = register(
        CULTIVATED_CARDOON_KEY,
        new CultivatedCardoonCropBlock(
            Block.Properties.ofFullCopy(Blocks.WHEAT)
        )
    );
        POTTED_CARDOON = registerBlockOnly(
        POTTED_CARDOON_KEY,
        new FlowerPotBlock(CARDOON,
            Block.Properties.ofFullCopy(Blocks.POTTED_ALLIUM))
    );
        ALMOND_CAKE = register(
        ALMOND_CAKE_KEY,
        new AlmondCakeBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .noOcclusion().strength(0.5F))
    );
        CHERRY_TART = register(
        CHERRY_TART_KEY,
        new CherryTartBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .noOcclusion().strength(0.5F))
    );
        OLIVE_CRATE = register(
        OLIVE_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        ALMOND_CRATE = register(
        ALMOND_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        CHERRY_CRATE = register(
        CHERRY_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        BASIL_CRATE = register(
        BASIL_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        CARDOON_CRATE = register(
        CARDOON_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        GRAPE_CRATE = register(
        GRAPE_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        WHITE_GRAPE_CRATE = register(
        WHITE_GRAPE_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        CHILI_PEPPER_CRATE = register(
        CHILI_PEPPER_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        GHOST_PEPPER_CRATE = register(
        GHOST_PEPPER_CRATE_KEY,
        new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .strength(2.0F, 3.0F).sound(SoundType.WOOD))
    );
        BASIL = register(
        BASIL_KEY,
        new BasilCropBlock(
            Block.Properties.ofFullCopy(Blocks.WHEAT)
        )
    );
        WILD_BASIL = register(
        WILD_BASIL_KEY,
        new WildBasilBlock(
            Block.Properties.ofFullCopy(Blocks.TALL_GRASS)
        )
    );
        WILD_CHILI = register(WILD_CHILI_KEY,
        new WildChiliBlock(Block.Properties.ofFullCopy(Blocks.TALL_GRASS))
    );
        WILD_GRAPE_VINE = register(WILD_GRAPE_VINE_KEY,
        new WildGrapeBlock(Block.Properties.ofFullCopy(Blocks.TALL_GRASS)
            ));
        WILD_WHITE_GRAPE_VINE = register(WILD_WHITE_GRAPE_VINE_KEY,
        new WildGrapeBlock(Block.Properties.ofFullCopy(Blocks.TALL_GRASS)
            ));
        CROP_STAKE = register(CROP_STAKE_KEY,
        new CropStakeBlock(Block.Properties.of()
            .noOcclusion()
            .forceSolidOff().strength(0.7F).sound(SoundType.WOOD)));
        SPRUCE_CROP_STAKE = registerCropStakeVariant("spruce");
        BIRCH_CROP_STAKE = registerCropStakeVariant("birch");
        JUNGLE_CROP_STAKE = registerCropStakeVariant("jungle");
        ACACIA_CROP_STAKE = registerCropStakeVariant("acacia");
        DARK_OAK_CROP_STAKE = registerCropStakeVariant("dark_oak");
        MANGROVE_CROP_STAKE = registerCropStakeVariant("mangrove");
        CHERRY_CROP_STAKE = registerCropStakeVariant("cherry");
        BAMBOO_CROP_STAKE = registerCropStakeVariant("bamboo");
        CRIMSON_CROP_STAKE = registerCropStakeVariant("crimson");
        WARPED_CROP_STAKE = registerCropStakeVariant("warped");
        OLIVE_CROP_STAKE = registerCropStakeVariant("olive");
        ALMOND_CROP_STAKE = registerCropStakeVariant("almond");
        FRUIT_CHERRY_CROP_STAKE = registerCropStakeVariant("fruit_cherry");
        HORIZONTAL_ROPE = registerBlockOnly(HORIZONTAL_ROPE_KEY,
        new HorizontalRopeBlock(Block.Properties.of()
            .noCollission().noOcclusion()
            .instabreak().sound(SoundType.WOOL)));
        STAKED_TOMATOES = registerBlockOnly(STAKED_TOMATOES_KEY,
        new StakedTomatoBlock(Block.Properties.of()
            .noOcclusion().forceSolidOff()
            .randomTicks().instabreak().sound(SoundType.CROP)));
        GRAPE_STEM = registerBlockOnly(GRAPE_STEM_KEY,
        new GrapeStemBlock(Block.Properties.ofFullCopy(Blocks.WHEAT)
            .randomTicks().strength(0.2F)));
        GRAPE_LEAVES = registerBlockOnly(GRAPE_LEAVES_KEY,
        new GrapeLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)
            .noOcclusion().randomTicks()));
        WHITE_GRAPE_STEM = registerBlockOnly(WHITE_GRAPE_STEM_KEY,
        new GrapeStemBlock(Block.Properties.ofFullCopy(Blocks.WHEAT)
            .randomTicks().strength(0.2F), true));
        WHITE_GRAPE_LEAVES = registerBlockOnly(WHITE_GRAPE_LEAVES_KEY,
        new GrapeLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)
            .noOcclusion().randomTicks(), true));
        BUDDING_CHILI = registerBlockOnly(BUDDING_CHILI_KEY,
        new BuddingChiliBlock(Block.Properties.of()
            .noCollission().noOcclusion().forceSolidOff()
            .randomTicks().instabreak().sound(SoundType.CROP)));
        CHILI_CROP = registerBlockOnly(CHILI_CROP_KEY,
        new ChiliCropBlock(Block.Properties.of()
            .noOcclusion().noCollission().forceSolidOff()
            .randomTicks().instabreak().sound(SoundType.CROP)));
        CHILI_ON_ROPE = registerBlockOnly(CHILI_ON_ROPE_KEY,
        new HangingChiliBlock(Block.Properties.of()
            .noCollission().noOcclusion()
            .randomTicks().instabreak().sound(SoundType.CROP)));
        PIZZA_DOUGH = registerBlockOnly(
        PIZZA_DOUGH_KEY,
        new PizzaDoughBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .noCollission().noOcclusion().strength(0.2F))
    );
        COOKED_PIZZA = registerBlockOnly(
        COOKED_PIZZA_KEY,
        new CookedPizzaBlock(Block.Properties.ofFullCopy(Blocks.CAKE)
            .noCollission().noOcclusion().strength(0.2F)));
        PIZZA_OVEN = register(
        PIZZA_OVEN_KEY,
        new PizzaOvenBlock(Block.Properties.ofFullCopy(Blocks.FURNACE)
            .strength(3.5F)));
        CRUSHING_TUB = register(
        CRUSHING_TUB_KEY,
        new CrushingTubBlock(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            
            .noOcclusion()
            .strength(2.0F, 3.0F))
    );
        BARREL = register(
        BARREL_KEY,
        new BarrelBlock(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .noOcclusion().randomTicks().strength(2.0F, 3.0F))
    );
        BREWING_BARREL = register(
        BREWING_BARREL_KEY,
        new BrewingBarrelBlock(Block.Properties.ofFullCopy(Blocks.BARREL)
            .noOcclusion().strength(2.5F, 3.0F)));
        SPRUCE_BREWING_BARREL = registerBrewingBarrelVariant("spruce");
        BIRCH_BREWING_BARREL = registerBrewingBarrelVariant("birch");
        JUNGLE_BREWING_BARREL = registerBrewingBarrelVariant("jungle");
        ACACIA_BREWING_BARREL = registerBrewingBarrelVariant("acacia");
        DARK_OAK_BREWING_BARREL = registerBrewingBarrelVariant("dark_oak");
        MANGROVE_BREWING_BARREL = registerBrewingBarrelVariant("mangrove");
        CHERRY_BREWING_BARREL = registerBrewingBarrelVariant("cherry");
        BAMBOO_BREWING_BARREL = registerBrewingBarrelVariant("bamboo");
        CRIMSON_BREWING_BARREL = registerBrewingBarrelVariant("crimson");
        WARPED_BREWING_BARREL = registerBrewingBarrelVariant("warped");
        OLIVE_BREWING_BARREL = registerBrewingBarrelVariant("olive");
        ALMOND_BREWING_BARREL = registerBrewingBarrelVariant("almond");
        FRUIT_CHERRY_BREWING_BARREL = registerBrewingBarrelVariant("fruit_cherry");
        APPLE_JUICE_FLUID_BLOCK = registerBlockOnly(
        APPLE_JUICE_FLUID_BLOCK_KEY,
        new LiquidBlock(ModFluids.APPLE_JUICE_STILL,
            Block.Properties.ofFullCopy(Blocks.WATER))
    );
        SPRUCE_CRUSHING_TUB = registerCrushingTubVariant("spruce");
        SPRUCE_BARREL = registerBarrelVariant("spruce");
        BIRCH_CRUSHING_TUB = registerCrushingTubVariant("birch");
        BIRCH_BARREL = registerBarrelVariant("birch");
        JUNGLE_CRUSHING_TUB = registerCrushingTubVariant("jungle");
        JUNGLE_BARREL = registerBarrelVariant("jungle");
        ACACIA_CRUSHING_TUB = registerCrushingTubVariant("acacia");
        ACACIA_BARREL = registerBarrelVariant("acacia");
        DARK_OAK_CRUSHING_TUB = registerCrushingTubVariant("dark_oak");
        DARK_OAK_BARREL = registerBarrelVariant("dark_oak");
        MANGROVE_CRUSHING_TUB = registerCrushingTubVariant("mangrove");
        MANGROVE_BARREL = registerBarrelVariant("mangrove");
        CHERRY_CRUSHING_TUB = registerCrushingTubVariant("cherry");
        CHERRY_BARREL = registerBarrelVariant("cherry");
        BAMBOO_CRUSHING_TUB = registerCrushingTubVariant("bamboo");
        BAMBOO_BARREL = registerBarrelVariant("bamboo");
        CRIMSON_CRUSHING_TUB = registerCrushingTubVariant("crimson");
        CRIMSON_BARREL = registerBarrelVariant("crimson");
        WARPED_CRUSHING_TUB = registerCrushingTubVariant("warped");
        WARPED_BARREL = registerBarrelVariant("warped");
        OLIVE_CRUSHING_TUB = registerCrushingTubVariant("olive");
        OLIVE_BARREL = registerBarrelVariant("olive");
        ALMOND_CRUSHING_TUB = registerCrushingTubVariant("almond");
        ALMOND_BARREL = registerBarrelVariant("almond");
        FRUIT_CHERRY_CRUSHING_TUB = registerCrushingTubVariant("fruit_cherry");
        FRUIT_CHERRY_BARREL = registerBarrelVariant("fruit_cherry");
        OLIVE_OIL_FLUID_BLOCK = registerBlockOnly(
        OLIVE_OIL_FLUID_BLOCK_KEY,
        new LiquidBlock(ModFluids.OLIVE_OIL_STILL,
            Block.Properties.ofFullCopy(Blocks.WATER))
    );
        OLIVE_OIL_CAULDRON = registerBlockOnly(
        OLIVE_OIL_CAULDRON_KEY,
        new OliveOilCauldronBlock(Block.Properties.ofFullCopy(Blocks.CAULDRON)
            .noOcclusion())
    );
        FLUID_CAULDRON = registerBlockOnly(
        FLUID_CAULDRON_KEY,
        new FluidCauldronBlock(Block.Properties.ofFullCopy(Blocks.CAULDRON)
            .noOcclusion())
    );
        CHEESE_VAT = register(
        CHEESE_VAT_KEY,
        new CheeseVatBlock(
            Block.Properties
                .ofFullCopy(Blocks.IRON_BLOCK)
                
                .noOcclusion()
                .noTerrainParticles()
                .strength(5.0F, 6.0F)
        )
    );
        CHEESE_HOOK = register(
            CHEESE_HOOK_KEY,
            new CheeseHookBlock(
                Block.Properties
                    .ofFullCopy(Blocks.IRON_BLOCK)
                    
                    .noOcclusion()
                    .noCollission()
                    .sound(SoundType.LANTERN)
                    .strength(3.0F, 6.0F)
            )
        );
        CHEESE_HOOK_OCCUPIED_SPACE = registerBlockOnly(
            CHEESE_HOOK_OCCUPIED_SPACE_KEY,
            new CheeseHookOccupiedSpaceBlock(
                Block.Properties
                    .ofFullCopy(Blocks.IRON_BLOCK)
                    
                    .noCollission()
                    .noOcclusion()
                    .noTerrainParticles()
                    .noLootTable()
                    .strength(-1.0F, 3600000.0F)
            )
        );
        OAK_CHEESE_AGING_RACK = registerRack("oak", Blocks.OAK_PLANKS);
        SPRUCE_CHEESE_AGING_RACK = registerRack("spruce", Blocks.SPRUCE_PLANKS);
        BIRCH_CHEESE_AGING_RACK = registerRack("birch", Blocks.BIRCH_PLANKS);
        JUNGLE_CHEESE_AGING_RACK = registerRack("jungle", Blocks.JUNGLE_PLANKS);
        ACACIA_CHEESE_AGING_RACK = registerRack("acacia", Blocks.ACACIA_PLANKS);
        DARK_OAK_CHEESE_AGING_RACK = registerRack("dark_oak", Blocks.DARK_OAK_PLANKS);
        MANGROVE_CHEESE_AGING_RACK = registerRack("mangrove", Blocks.MANGROVE_PLANKS);
        CHERRY_CHEESE_AGING_RACK = registerRack("cherry", Blocks.CHERRY_PLANKS);
        BAMBOO_CHEESE_AGING_RACK = registerRack("bamboo", Blocks.BAMBOO_PLANKS);
        CRIMSON_CHEESE_AGING_RACK = registerRack("crimson", Blocks.CRIMSON_PLANKS);
        WARPED_CHEESE_AGING_RACK = registerRack("warped", Blocks.WARPED_PLANKS);
        AGING_CHEESE = registerBlockOnly(
            AGING_CHEESE_KEY,
            new AgingCheeseBlock(
                Block.Properties
                    .ofFullCopy(Blocks.CAKE)
                    
                    .noOcclusion()
                    .noTerrainParticles()
                    .strength(0.2F)
            )
        );
        SALT_CAULDRON = register(
            SALT_CAULDRON_KEY,
            new SaltCauldronBlock(
                Block.Properties
                    .ofFullCopy(Blocks.CAULDRON)
                    
                    .noOcclusion()
            )
        );
    }

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
                blockKey.location()
            );

        BLOCK_ITEMS.add(new PendingItem(itemKey, registeredBlock));
        return registeredBlock;
    }

    // Registers only the block, without automatically creating an item.
    private static Block registerBlockOnly(
        ResourceKey<Block> blockKey,
        Block block
    ) {
        return Registry.register(
            BuiltInRegistries.BLOCK,
            blockKey.location(),
            block
        );
    }

    private static Block registerCropStakeVariant(String wood) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(),
            ModRegistries.id(wood + "_crop_stake"));
        return register(key, new CropStakeBlock(Block.Properties.of()
            .noOcclusion().forceSolidOff()
            .strength(0.7F).sound(SoundType.WOOD)));
    }

    private static Block registerBrewingBarrelVariant(String wood) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(),
            ModRegistries.id(wood + "_brewing_barrel"));
        return register(key, new BrewingBarrelBlock(Block.Properties.ofFullCopy(Blocks.BARREL)
            .noOcclusion().strength(2.5F, 3.0F)));
    }

    private static Block registerCrushingTubVariant(String wood) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(),
            ModRegistries.id(wood + "_crushing_tub"));
        return register(key, new CrushingTubBlock(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .noOcclusion().strength(2.0F, 3.0F)));
    }

    private static Block registerBarrelVariant(String wood) {
        ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(),
            ModRegistries.id(wood + "_barrel"));
        return register(key, new BarrelBlock(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            .noOcclusion().randomTicks().strength(2.0F, 3.0F)));
    }

    private static void stripLog(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.AXE_STRIP) return;
        BlockStateStrip.apply(event);
    }

    private static final class BlockStateStrip {
        private static void apply(BlockEvent.BlockToolModificationEvent event) {
            Block block = event.getState().getBlock();
            Block stripped = block == OLIVE_LOG ? STRIPPED_OLIVE_LOG :
                block == OLIVE_WOOD ? STRIPPED_OLIVE_WOOD :
                block == ALMOND_LOG ? STRIPPED_ALMOND_LOG :
                block == ALMOND_WOOD ? STRIPPED_ALMOND_WOOD :
                block == FRUIT_CHERRY_LOG ? STRIPPED_FRUIT_CHERRY_LOG :
                block == FRUIT_CHERRY_WOOD ? STRIPPED_FRUIT_CHERRY_WOOD : null;
            if (stripped != null) {
                event.setFinalState(stripped.defaultBlockState()
                    .setValue(BlockStateProperties.AXIS, event.getState().getValue(BlockStateProperties.AXIS)));
            }
        }
    }

    public static void register() {
        // Vanilla-style log stripping and fire spread: performed only after
        // the registered block fields are initialized.
        NeoForge.EVENT_BUS.addListener(ModBlocks::stripLog);
        FireBlock fire = (FireBlock) Blocks.FIRE;
        fire.setFlammable(OLIVE_LOG, 5, 5);
        fire.setFlammable(OLIVE_WOOD, 5, 5);
        fire.setFlammable(STRIPPED_OLIVE_LOG, 5, 5);
        fire.setFlammable(STRIPPED_OLIVE_WOOD, 5, 5);
        fire.setFlammable(OLIVE_PLANKS, 5, 20);
        fire.setFlammable(ALMOND_LOG, 5, 5);
        fire.setFlammable(STRIPPED_ALMOND_LOG, 5, 5);
        fire.setFlammable(ALMOND_PLANKS, 5, 20);
        fire.setFlammable(ALMOND_WOOD, 5, 5);
        fire.setFlammable(STRIPPED_ALMOND_WOOD, 5, 5);
        fire.setFlammable(FRUIT_CHERRY_LOG, 5, 5);
        fire.setFlammable(FRUIT_CHERRY_WOOD, 5, 5);
        fire.setFlammable(STRIPPED_FRUIT_CHERRY_LOG, 5, 5);
        fire.setFlammable(STRIPPED_FRUIT_CHERRY_WOOD, 5, 5);
        fire.setFlammable(FRUIT_CHERRY_PLANKS, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_STAIRS, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_SLAB, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_FENCE, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_FENCE_GATE, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_BUTTON, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_PRESSURE_PLATE, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_DOOR, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_TRAPDOOR, 5, 20);
        fire.setFlammable(ALMOND_STAIRS, 5, 20);
        fire.setFlammable(ALMOND_SLAB, 5, 20);
        fire.setFlammable(ALMOND_FENCE, 5, 20);
        fire.setFlammable(ALMOND_FENCE_GATE, 5, 20);
        fire.setFlammable(ALMOND_BUTTON, 5, 20);
        fire.setFlammable(ALMOND_PRESSURE_PLATE, 5, 20);
        fire.setFlammable(ALMOND_DOOR, 5, 20);
        fire.setFlammable(ALMOND_TRAPDOOR, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_LEAVES, 30, 60);
        fire.setFlammable(ALMOND_LEAVES, 30, 60);
        fire.setFlammable(OLIVE_LEAVES, 30, 60);
        fire.setFlammable(OLIVE_STAIRS, 5, 20);
        fire.setFlammable(OLIVE_SLAB, 5, 20);
        fire.setFlammable(OLIVE_FENCE, 5, 20);
        fire.setFlammable(OLIVE_FENCE_GATE, 5, 20);
        fire.setFlammable(OLIVE_BUTTON, 5, 20);
        fire.setFlammable(OLIVE_PRESSURE_PLATE, 5, 20);
        fire.setFlammable(OLIVE_DOOR, 5, 20);
        fire.setFlammable(OLIVE_TRAPDOOR, 5, 20);
        fire.setFlammable(OLIVE_CABINET, 5, 20);
        fire.setFlammable(ALMOND_CABINET, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_CABINET, 5, 20);
        fire.setFlammable(OLIVE_CRATE, 5, 20);
        fire.setFlammable(ALMOND_CRATE, 5, 20);
        fire.setFlammable(CHERRY_CRATE, 5, 20);
        fire.setFlammable(BASIL_CRATE, 5, 20);
        fire.setFlammable(CARDOON_CRATE, 5, 20);
        fire.setFlammable(GRAPE_CRATE, 5, 20);
        fire.setFlammable(WHITE_GRAPE_CRATE, 5, 20);
        fire.setFlammable(CHILI_PEPPER_CRATE, 5, 20);
        fire.setFlammable(GHOST_PEPPER_CRATE, 5, 20);
        fire.setFlammable(CROP_STAKE, 5, 20);
        fire.setFlammable(SPRUCE_CROP_STAKE, 5, 20);
        fire.setFlammable(BIRCH_CROP_STAKE, 5, 20);
        fire.setFlammable(JUNGLE_CROP_STAKE, 5, 20);
        fire.setFlammable(ACACIA_CROP_STAKE, 5, 20);
        fire.setFlammable(DARK_OAK_CROP_STAKE, 5, 20);
        fire.setFlammable(MANGROVE_CROP_STAKE, 5, 20);
        fire.setFlammable(CHERRY_CROP_STAKE, 5, 20);
        fire.setFlammable(BAMBOO_CROP_STAKE, 5, 20);
        fire.setFlammable(CRIMSON_CROP_STAKE, 5, 20);
        fire.setFlammable(WARPED_CROP_STAKE, 5, 20);
        fire.setFlammable(OLIVE_CROP_STAKE, 5, 20);
        fire.setFlammable(ALMOND_CROP_STAKE, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_CROP_STAKE, 5, 20);
        fire.setFlammable(BREWING_BARREL, 5, 20);
        fire.setFlammable(SPRUCE_BREWING_BARREL, 5, 20);
        fire.setFlammable(BIRCH_BREWING_BARREL, 5, 20);
        fire.setFlammable(JUNGLE_BREWING_BARREL, 5, 20);
        fire.setFlammable(ACACIA_BREWING_BARREL, 5, 20);
        fire.setFlammable(DARK_OAK_BREWING_BARREL, 5, 20);
        fire.setFlammable(MANGROVE_BREWING_BARREL, 5, 20);
        fire.setFlammable(CHERRY_BREWING_BARREL, 5, 20);
        fire.setFlammable(BAMBOO_BREWING_BARREL, 5, 20);
        fire.setFlammable(CRIMSON_BREWING_BARREL, 5, 20);
        fire.setFlammable(WARPED_BREWING_BARREL, 5, 20);
        fire.setFlammable(OLIVE_BREWING_BARREL, 5, 20);
        fire.setFlammable(ALMOND_BREWING_BARREL, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_BREWING_BARREL, 5, 20);
        fire.setFlammable(SPRUCE_BARREL, 5, 20);
        fire.setFlammable(SPRUCE_CRUSHING_TUB, 5, 20);
        fire.setFlammable(BIRCH_BARREL, 5, 20);
        fire.setFlammable(BIRCH_CRUSHING_TUB, 5, 20);
        fire.setFlammable(JUNGLE_BARREL, 5, 20);
        fire.setFlammable(JUNGLE_CRUSHING_TUB, 5, 20);
        fire.setFlammable(ACACIA_BARREL, 5, 20);
        fire.setFlammable(ACACIA_CRUSHING_TUB, 5, 20);
        fire.setFlammable(DARK_OAK_BARREL, 5, 20);
        fire.setFlammable(DARK_OAK_CRUSHING_TUB, 5, 20);
        fire.setFlammable(MANGROVE_BARREL, 5, 20);
        fire.setFlammable(MANGROVE_CRUSHING_TUB, 5, 20);
        fire.setFlammable(CHERRY_BARREL, 5, 20);
        fire.setFlammable(CHERRY_CRUSHING_TUB, 5, 20);
        fire.setFlammable(BAMBOO_BARREL, 5, 20);
        fire.setFlammable(BAMBOO_CRUSHING_TUB, 5, 20);
        fire.setFlammable(CRIMSON_BARREL, 5, 20);
        fire.setFlammable(CRIMSON_CRUSHING_TUB, 5, 20);
        fire.setFlammable(WARPED_BARREL, 5, 20);
        fire.setFlammable(WARPED_CRUSHING_TUB, 5, 20);
        fire.setFlammable(OLIVE_BARREL, 5, 20);
        fire.setFlammable(OLIVE_CRUSHING_TUB, 5, 20);
        fire.setFlammable(ALMOND_BARREL, 5, 20);
        fire.setFlammable(ALMOND_CRUSHING_TUB, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_BARREL, 5, 20);
        fire.setFlammable(FRUIT_CHERRY_CRUSHING_TUB, 5, 20);
    }
}
