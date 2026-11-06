package dev.italiansdelight.common.orchard;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Deterministic, rotating fruit-bearing leaves: about 70% of naturally
 * eligible canopy surface leaves are productive in any one cycle.
 *
 * <p>There is no permanent "fruitful" flag or per-block save data.
 * A leaf's own four-Minecraft-day window is staggered by its position,
 * so the entire tree doesn't change its eligible leaves at once.
 * Every window gets a new pseudorandom selection. The result is stable
 * across world saves, between client/server, and during bonemeal use.
 *
 * <p>Do not apply this to hand-placed, enclosed or decaying foliage:
 * callers must check canDevelop and air exposure first.
 */
public final class OrchardFruitingCycle {
    public static final int ELIGIBILITY_PERCENT = 70;
    public static final long CYCLE_TICKS = 4L * 24_000L;

    private OrchardFruitingCycle() {}

    public static boolean isSelected(Level level, BlockPos pos, OrchardSeasonPolicy.Fruit fruit) {
        // A 64-bit avalanche hash spreads neighboring leaf positions apart.
        long base = mix(pos.asLong()
                ^ (0xD1B54A32D192ED03L * (fruit.ordinal() + 1L)));
        // Independent per-leaf offsets prevent a synchronized "whole-tree"
        // reset at dawn. Windows remain four Minecraft days long per leaf.
        long offset = Long.remainderUnsigned(base, CYCLE_TICKS);
        long window = Math.floorDiv(level.getGameTime() + offset, CYCLE_TICKS);
        long selection = mix(base ^ (window * 0x9E3779B97F4A7C15L));
        return Long.remainderUnsigned(selection, 100L) < ELIGIBILITY_PERCENT;
    }

    private static long mix(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
