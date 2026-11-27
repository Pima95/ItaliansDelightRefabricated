package dev.italiansdelight.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.block.AlmondLeavesBlock;
import dev.italiansdelight.common.block.FruitCherryLeavesBlock;
import dev.italiansdelight.common.orchard.OrchardSeasonPolicy;
import dev.italiansdelight.common.orchard.OrchardSeasonPolicy.Fruit;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonHandler;

/** Development-only optional integration checks; loaded only with Serene Seasons installed. */
final class SeasonPortChecks {
    private SeasonPortChecks() {}

    // Independent fixtures for the approved Fabric calendar, spring through winter.
    private static final String[] PERIODS = {
        "EARLY_SPRING", "MID_SPRING", "LATE_SPRING",
        "EARLY_SUMMER", "MID_SUMMER", "LATE_SUMMER",
        "EARLY_AUTUMN", "MID_AUTUMN", "LATE_AUTUMN",
        "EARLY_WINTER", "MID_WINTER", "LATE_WINTER"
    };
    private static final int[] OLIVE = {0,0,1,2,3,3,4,4,4,0,0,0};
    private static final int[] ALMOND = {2,3,3,3,3,4,4,4,0,0,0,1};
    private static final int[] CHERRY = {1,2,3,4,5,5,0,0,0,0,0,0};
    private static final int[] ALMOND_VISUAL = {0,0,0,0,0,0,0,0,1,2,2,2};
    private static final int[] CHERRY_VISUAL = {0,0,0,0,0,0,1,1,1,2,2,2};

    static void run(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        var biome = level.getBiome(pos);
        helper.assertTrue(ModConfig.seasons.isDimensionWhitelisted(level.dimension())
            && !biome.is(ModTags.Biomes.BLACKLISTED_BIOMES)
            && !SeasonHelper.usesTropicalSeasons(biome),
            "Season test requires the normal temperate GameTest overworld");
        helper.assertTrue(Blocks.GLASS.defaultBlockState().is(ModTags.Blocks.GREENHOUSE_GLASS),
            "Vanilla glass must resolve through the native greenhouse tag");

        var saved = SeasonHandler.getSeasonSavedData(level);
        int originalTicks = saved.seasonCycleTicks;
        var originalState = level.getBlockState(pos);
        var originalDimensions = ModConfig.seasons.whitelistedDimensions;
        // The entire cycle is synchronous in one test invocation; restore
        // the exact original world season/config/block even on assertion failure.
        try {
            for (int i = 0; i < PERIODS.length; i++) {
                setSeason(helper, PERIODS[i]);
                assertStage(helper, pos, Fruit.OLIVE, 4, OLIVE[i], 0);
                assertStage(helper, pos, Fruit.ALMOND, 4, ALMOND[i], ALMOND_VISUAL[i]);
                assertStage(helper, pos, Fruit.CHERRY, 5, CHERRY[i], CHERRY_VISUAL[i]);
            }
            // In winter an actual leaf random tick must change the stored
            // seasonal model even on hand-placed foliage.
            setSeason(helper, "MID_WINTER");
            verifyLeafVisuals(helper, pos, 2, 2);
            setSeason(helper, "LATE_AUTUMN");
            verifyLeafVisuals(helper, pos, 1, 1);
            setSeason(helper, "MID_SUMMER");
            verifyLeafVisuals(helper, pos, 0, 0);
            setSeason(helper, "MID_WINTER");

            // Check the precise approved greenhouse scan boundaries.
            for (int height : new int[]{1, 16, 17}) {
                BlockPos roof = pos.above(height);
                var previous = level.getBlockState(roof);
                try {
                    level.setBlock(roof, Blocks.GLASS.defaultBlockState(), Block.UPDATE_CLIENTS);
                    boolean protectedByGlass = height <= 16;
                    assertStage(helper, pos, Fruit.OLIVE, 4, protectedByGlass ? 4 : 0, 0);
                    assertStage(helper, pos, Fruit.ALMOND, 4, protectedByGlass ? 4 : 0,
                        protectedByGlass ? 0 : 2);
                    assertStage(helper, pos, Fruit.CHERRY, 5, protectedByGlass ? 5 : 0,
                        protectedByGlass ? 0 : 2);
                    if (protectedByGlass) verifyLeafVisuals(helper, pos, 0, 0);
                } finally {
                    level.setBlock(roof, previous, Block.UPDATE_CLIENTS);
                }
            }

            // Removing the dimension from the whitelist must restore the
            // original year-round behavior, without editing any config file.
            ModConfig.seasons.whitelistedDimensions = List.of();
            assertStage(helper, pos, Fruit.OLIVE, 4, 4, 0);
            assertStage(helper, pos, Fruit.ALMOND, 4, 4, 0);
            assertStage(helper, pos, Fruit.CHERRY, 5, 5, 0);
            ModConfig.seasons.whitelistedDimensions = originalDimensions;

            // Check the real datapack tags read by Serene Seasons.
            assertTag(helper, ModBlocks.BASIL, "spring_crops", true);
            assertTag(helper, ModBlocks.BASIL, "summer_crops", true);
            assertTag(helper, ModBlocks.BASIL, "autumn_crops", false);
            assertTag(helper, ModBlocks.CULTIVATED_CARDOON, "spring_crops", true);
            assertTag(helper, ModBlocks.CULTIVATED_CARDOON, "summer_crops", true);
            assertTag(helper, ModBlocks.CULTIVATED_CARDOON, "autumn_crops", true);
            assertTag(helper, ModBlocks.ALMOND_SAPLING, "spring_crops", true);
            assertTag(helper, ModBlocks.ALMOND_SAPLING, "autumn_crops", true);
            assertTag(helper, ModBlocks.FRUIT_CHERRY_SAPLING, "spring_crops", true);
            assertTag(helper, ModBlocks.OLIVE_SAPLING, "year_round_crops", true);
            for (Block block : new Block[]{ModBlocks.BASIL, ModBlocks.CULTIVATED_CARDOON,
                    ModBlocks.OLIVE_SAPLING, ModBlocks.ALMOND_SAPLING, ModBlocks.FRUIT_CHERRY_SAPLING})
                assertTag(helper, block, "unbreakable_infertile_crops", true);
        } finally {
            ModConfig.seasons.whitelistedDimensions = originalDimensions;
            saved.seasonCycleTicks = originalTicks;
            saved.setDirty();
            SeasonHandler.sendSeasonUpdate(level);
            level.setBlock(pos, originalState, Block.UPDATE_CLIENTS);
        }
        ItaliansDelight.LOGGER.info("IDR_SEASON_PROFILE_PASS installed=true periods=12 greenhouse=1,16,17");
    }

    private static void setSeason(GameTestHelper helper, String name) {
        var level = helper.getLevel();
        try {
            int result = level.getServer().getCommands().getDispatcher().execute(
                "season set " + name.toLowerCase(Locale.ROOT),
                level.getServer().createCommandSourceStack().withLevel(level).withPermission(4));
            helper.assertTrue(result == 1, "Serene Seasons command failed for " + name);
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException("Native season command unavailable", e);
        }
        helper.assertTrue(SeasonHelper.getSeasonState(level).getSubSeason() == Season.SubSeason.valueOf(name),
            "Serene Seasons did not enter " + name);
    }

    private static void assertStage(GameTestHelper helper, BlockPos pos, Fruit fruit,
                                    int ripe, int expected, int visual) {
        var level = helper.getLevel();
        helper.assertTrue(OrchardSeasonPolicy.maxStage(level, pos, fruit, ripe) == expected,
            "Incorrect seasonal fruit cap for " + fruit + " in " + SeasonHelper.getSeasonState(level).getSubSeason());
        helper.assertTrue(OrchardSeasonPolicy.foliagePhase(level, pos, fruit) == visual,
            "Incorrect foliage phase for " + fruit);
    }

    private static void verifyLeafVisuals(GameTestHelper helper, BlockPos pos, int almond, int cherry) {
        var level = helper.getLevel();
        var a = ModBlocks.ALMOND_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        level.setBlock(pos, a, Block.UPDATE_CLIENTS);
        a.randomTick(level, pos, level.getRandom());
        helper.assertTrue(level.getBlockState(pos).getValue(AlmondLeavesBlock.SEASONAL_VISUAL) == almond,
            "Almond leaf tick failed to apply seasonal visual");
        var c = ModBlocks.FRUIT_CHERRY_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        level.setBlock(pos, c, Block.UPDATE_CLIENTS);
        c.randomTick(level, pos, level.getRandom());
        helper.assertTrue(level.getBlockState(pos).getValue(FruitCherryLeavesBlock.SEASONAL_VISUAL) == cherry,
            "Cherry leaf tick failed to apply seasonal visual");
    }

    private static void assertTag(GameTestHelper helper, Block block, String name, boolean expected) {
        var tag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("sereneseasons", name));
        helper.assertTrue(block.defaultBlockState().is(tag) == expected,
            "Unexpected Serene Seasons fertility tag " + name + " for " + block);
    }
}
