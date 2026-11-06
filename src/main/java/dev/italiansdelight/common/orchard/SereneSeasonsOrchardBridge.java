package dev.italiansdelight.common.orchard;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModTags;

/**
 * This is the ONLY class that imports Serene Seasons APIs.
 *
 * <p>Never access it unless FabricLoader reports sereneseasons installed.
 * Serene Seasons is a compile-only / optional runtime dependency.
 */
final class SereneSeasonsOrchardBridge {
    private SereneSeasonsOrchardBridge() {}

    /**
     * Match Serene Seasons 26.2 SeasonalCropGrowthHandler.isGlassAboveBlock:
     * any block in the native greenhouse_glass tag 1..16 blocks above
     * the target is enough. The same test applies to leaf canopy and crops.
     */
    private static boolean hasGreenhouseGlass(Level level, BlockPos pos) {
        for (int height = 1; height <= 16; ++height) {
            if (level.getBlockState(pos.above(height)).is(ModTags.Blocks.GREENHOUSE_GLASS)) {
                return true;
            }
        }
        return false;
    }

    /** Keep autumn/winter foliage visual phases decoupled from fruit stages. */
    static int foliagePhase(Level level, BlockPos pos, OrchardSeasonPolicy.Fruit fruit) {
        // Greenhouse trees remain green: otherwise autumn/winter block models
        // would visually hide ripe fruit that is allowed to grow under glass.
        if (hasGreenhouseGlass(level, pos)) return 0;
        if (!SeasonHelper.usesStandardSeasons(level, pos)) return 0;
        ISeasonState season = SeasonHelper.getSeasonState(level);
        if (season == null || season.getSubSeason() == null) return 0;
        return OrchardSeasonPolicy.deciduousFoliagePhase(fruit, season.getSubSeason().name());
    }

    static int maxStage(Level level, BlockPos pos, OrchardSeasonPolicy.Fruit fruit,
                        int fallbackMax) {
        // A greenhouse is the ONLY exception to the fruiting calendar:
        // outside, ripe stages remain unavailable outside harvest season.
        if (hasGreenhouseGlass(level, pos)) {
            return fallbackMax;
        }
        // Dimensions with disabled seasons, and tropical biomes with wet/dry
        // seasons, keep vanilla orchard behavior until a dedicated tropical
        // calendar is designed. No false "winter" dormancy in the tropics.
        if (!SeasonHelper.usesStandardSeasons(level, pos)) {
            return fallbackMax;
        }

        ISeasonState season = SeasonHelper.getSeasonState(level);
        if (season == null || season.getSubSeason() == null) {
            return fallbackMax;
        }
        return OrchardSeasonPolicy.stageCap(fruit, season.getSubSeason().name());
    }
}
