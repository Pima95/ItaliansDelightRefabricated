package dev.italiansdelight.common.orchard;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

/**
 * This is the ONLY class that imports Serene Seasons APIs.
 *
 * <p>Never access it unless FabricLoader reports sereneseasons installed.
 * Serene Seasons is a compile-only / optional runtime dependency.
 */
final class SereneSeasonsOrchardBridge {
    private SereneSeasonsOrchardBridge() {}

    /** Keep autumn/winter foliage visual phases decoupled from fruit stages. */
    static int foliagePhase(Level level, BlockPos pos, OrchardSeasonPolicy.Fruit fruit) {
        if (!SeasonHelper.usesStandardSeasons(level, pos)) return 0;
        ISeasonState season = SeasonHelper.getSeasonState(level);
        if (season == null || season.getSubSeason() == null) return 0;
        return OrchardSeasonPolicy.deciduousFoliagePhase(fruit, season.getSubSeason().name());
    }

    static int maxStage(Level level, BlockPos pos, OrchardSeasonPolicy.Fruit fruit,
                        int fallbackMax) {
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
