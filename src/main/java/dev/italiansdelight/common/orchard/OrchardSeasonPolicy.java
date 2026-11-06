package dev.italiansdelight.common.orchard;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Optional Serene Seasons bridge for the three orchard trees.
 *
 * <p>With no Serene Seasons installed, the existing growth and harvest
 * mechanics remain unchanged. There are no direct optional-mod references
 * in this class's signatures or static initializers.
 *
 * <p>The subseason windows are initial agronomic defaults, NOT final
 * random-tick speeds or drop yields (those remain unchanged).
 */
public final class OrchardSeasonPolicy {
    public enum Fruit { OLIVE, ALMOND, CHERRY }

    private OrchardSeasonPolicy() {}

    public static int maxStage(Level level, BlockPos pos, Fruit fruit, int vanillaMax) {
        if (!FabricLoader.getInstance().isModLoaded("sereneseasons")) {
            return vanillaMax;
        }
        // The adapter is not loaded/linked without the optional mod.
        return SereneSeasonsOrchardBridge.maxStage(level, pos, fruit, vanillaMax);
    }

    /**
     * Pure calendar: stage caps for each sub-season (12 periods).
     * Only the stage cap is affected; advancement still uses the vanilla
     * random ticks and the user's provisional 1/3 chance.
     */
    public static int stageCap(Fruit fruit, String subSeason) {
        return switch (fruit) {
            case ALMOND -> switch (subSeason) {
                case "LATE_WINTER" -> 1;                     // Pink buds
                case "EARLY_SPRING" -> 2;                   // Pink/white flowers
                case "MID_SPRING", "LATE_SPRING",
                     "EARLY_SUMMER", "MID_SUMMER" -> 3;     // Green hull
                case "LATE_SUMMER", "EARLY_AUTUMN",
                     "MID_AUTUMN" -> 4;                     // Dry-shell almonds
                default -> 0;                               // Winter dormancy
            };
            case CHERRY -> switch (subSeason) {
                case "EARLY_SPRING" -> 1;                   // White buds
                case "MID_SPRING" -> 2;                     // First white petals
                case "LATE_SPRING" -> 3;                    // Full white bloom
                case "EARLY_SUMMER" -> 4;                   // Small green cherries
                case "MID_SUMMER", "LATE_SUMMER" -> 5;      // Ripe red cherries
                default -> 0;
            };
            case OLIVE -> switch (subSeason) {
                case "LATE_SPRING" -> 1;                    // White buds
                case "EARLY_SUMMER" -> 2;                   // White flowers
                case "MID_SUMMER", "LATE_SUMMER" -> 3;      // Green drupes
                case "EARLY_AUTUMN", "MID_AUTUMN",
                     "LATE_AUTUMN" -> 4;                    // Harvest season
                default -> 0;
            };
        };
    }
}
