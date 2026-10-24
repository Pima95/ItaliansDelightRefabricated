package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Low-forking Mediterranean olive: a two/three-block central stem splits
 * into TWO opposed or angled rising limbs. Branch paths are constructed
 * one face-connected log at a time (horizontal, then vertical and sometimes
 * sideways), with distinct foliage anchors only at their elevated tips.
 *
 * This replaces the earlier cherry-like high branches and central canopy.
 * Ancient 2x2 olives use a separate, still provisional configuration.
 */
public final class OliveTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<OliveTrunkPlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, OliveTrunkPlacer::new));

    public OliveTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreePlacers.OLIVE_TRUNK;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        int treeHeight,
        BlockPos origin,
        TreeConfiguration config
    ) {
        placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), config);

        List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>(2);
        // 2 or 3 blocks of trunk, including the one planted at y=0.
        // The fork connects to the 2nd or 3rd block (y=1 or y=2).
        int stemHeight = 2 + random.nextInt(2);
        BlockPos stemTop = null;
        for (int y = 0; y < stemHeight; y++) {
            BlockPos spot = origin.above(y);
            if (!placeLog(level, trunkSetter, random, spot, config)) {
                break;
            }
            stemTop = spot;
        }
        if (stemTop == null) {
            return crowns;
        }

        Direction first = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        // Usually two opposite arms, sometimes approximately a right angle.
        // Both always spring from the LOWER stem, never the top of a tall trunk.
        Direction second = random.nextInt(4) == 0
            ? (random.nextBoolean() ? first.getClockWise() : first.getCounterClockWise())
            : first.getOpposite();

        int firstForkY = Math.min(stemHeight - 1, 1 + random.nextInt(2));
        int secondForkY = Math.max(1, firstForkY - (random.nextBoolean() ? 1 : 0));
        Direction[] directions = {first, second};
        int[] forkHeights = {firstForkY, secondForkY};

        for (int i = 0; i < 2; i++) {
            BlockPos branchBase = origin.above(forkHeights[i]);
            BlockPos tip = placeAscendingBranch(
                level, trunkSetter, random, config, branchBase, directions[i]
            );
            if (tip != null) {
                // No central crown: each oblique primary limb supports its
                // own compact, silver-green leaf cluster.
                crowns.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));
            }
        }

        // If obstacles blocked BOTH branches, avoid an undecorated stump.
        // This is an emergency fallback, not a third crown in normal growth.
        if (crowns.isEmpty()) {
            crowns.add(new FoliagePlacer.FoliageAttachment(stemTop.above(), 0, false));
        }
        return crowns;
    }

    /**
     * Gently climbing, mangrove-inspired stepping:
     * 3-5 outward steps, at least two upward steps, sometimes a lateral kink.
     * Every log shares a FULL FACE with the preceding log. Never place a
     * diagonal-only pair or leap over an obstructed block.
     *
     * @return the connected tip when a branch extends at least two horizontal
     *         blocks; otherwise null (no disconnected foliage cluster).
     */
    private BlockPos placeAscendingBranch(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        TreeConfiguration config,
        BlockPos start,
        Direction outward
    ) {
        BlockPos tip = start;
        int outwardSteps = 0;
        int length = 3 + random.nextInt(3);
        int firstRise = 1 + random.nextInt(2);
        Direction lateral = random.nextBoolean()
            ? outward.getClockWise()
            : outward.getCounterClockWise();
        boolean sideKink = random.nextFloat() < 0.55F;

        for (int step = 1; step <= length; step++) {
            // Horizontal segment first; then vertical segment on the SAME
            // column. This produces a connected oblique line of trunk logs.
            BlockPos horizontal = tip.relative(outward);
            if (!placeLog(level, trunkSetter, random, horizontal, config,
                state -> state.setValue(RotatedPillarBlock.AXIS, outward.getAxis()))) {
                break;
            }
            tip = horizontal;
            outwardSteps++;

            if (step == 2 && sideKink) {
                BlockPos sideways = tip.relative(lateral);
                if (placeLog(level, trunkSetter, random, sideways, config,
                    state -> state.setValue(RotatedPillarBlock.AXIS, lateral.getAxis()))) {
                    tip = sideways;
                }
            }

            // At least two rises, with slight variation in their location.
            // Endpoints stay high enough that their foliage will not touch
            // the soil even when the main fork occurs at block two.
            boolean rise = step == firstRise || step == length
                || (length == 5 && step == 3);
            if (rise) {
                BlockPos above = tip.above();
                if (!placeLog(level, trunkSetter, random, above, config)) {
                    break;
                }
                tip = above;
            }
        }
        return outwardSteps >= 2 ? tip : null;
    }
}
