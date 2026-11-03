package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Cultivated fruit-cherry: mostly upright crown-bearing trunk with an
 * occasional short lateral fruiting bough. The trunk has genuinely different
 * geometry from Almond's low two-bend form. Growth is sapling-only;
 * wild/worldgen placement remains disabled until approved.
 *
 * We preflight all log positions and rotate the complete structure around
 * obstacles; avoid leaving partially placed branches on blocked growth.
 */
public final class FruitCherryTrunkPlacer extends TrunkPlacer {
    private static final int FORK_VARIANT_CHANCE = 3;
    private static final int TALL_SINGLE_CHANCE = 3;

    public static final MapCodec<FruitCherryTrunkPlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, FruitCherryTrunkPlacer::new));

    public FruitCherryTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreePlacers.FRUIT_CHERRY_TRUNK;
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
        List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>(2);
        // Select the shape before checking placement; do not silently change
        // a blocked two-bough tree into a single-stem variant.
        boolean forked = random.nextInt(FORK_VARIANT_CHANCE) == 0;
        int trunkHeight = !forked && random.nextInt(TALL_SINGLE_CHANCE) == 0
            ? 6 : 5 + random.nextInt(2);

        Direction firstLean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        boolean clockwise = random.nextBoolean();
        boolean upperKink = random.nextBoolean();

        // Try four rotations of the connected, preflighted tree plan.
        for (int attempt = 0; attempt < 4; attempt++) {
            Direction lean = firstLean;
            for (int rotation = 0; rotation < attempt; rotation++) {
                lean = lean.getClockWise();
            }
            Direction turn = clockwise ? lean.getClockWise() : lean.getCounterClockWise();
            FruitCherryPlan plan = planTree(origin, trunkHeight, forked, lean, turn, upperKink);
            if (!canPlacePlan(level, origin, plan.barkPositions())) {
                continue;
            }

            placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), config);
            if (!placeLog(level, trunkSetter, random, origin, config)) {
                return crowns;
            }

            for (BlockPos position : plan.barkPositions()) {
                if (!placeLog(level, trunkSetter, random, position, config)) {
                    // A placement failure should never create an additional
                    // foliage attachment or claim a completed second limb.
                    return crowns;
                }
            }

            crowns.add(new FoliagePlacer.FoliageAttachment(plan.mainTip().above(), 0, false));
            if (plan.secondaryTip() != null) {
                crowns.add(new FoliagePlacer.FoliageAttachment(
                    plan.secondaryTip().above(), 0, false));
            }
            return crowns;
        }

        // There was not enough clear space for this shape. Do not leave
        // a partially grown fork or create a straight fallback pole.
        return crowns;
    }

    /**
     * Central upright stem, optional single one-block lean at mid-height,
     * and a short upward secondary branch (never a detached horizontal beam).
     * This gives the fruit cherry a rounded, orchard-style crown rather than
     * Almond's low zig-zag and upright tufts.
     */
    private FruitCherryPlan planTree(
        BlockPos origin,
        int height,
        boolean forked,
        Direction lean,
        Direction turn,
        boolean upperKink
    ) {
        List<BlockPos> bark = new ArrayList<>(12);
        BlockPos stem = origin.above();
        bark.add(stem);
        stem = stem.above();
        bark.add(stem);
        stem = stem.above();
        bark.add(stem);
        if (upperKink) {
            stem = stem.relative(lean);
            bark.add(stem);
        }
        stem = stem.above();
        bark.add(stem);
        if (height >= 6) {
            stem = stem.above();
            bark.add(stem);
        }

        BlockPos secondaryTip = null;
        if (forked) {
            // Branch originates from y=3 and curves gradually away from the
            // main crown, staying face-connected to the central stem.
            BlockPos side = origin.above(3).relative(turn);
            bark.add(side);
            side = side.above();
            bark.add(side);
            side = side.relative(turn);
            bark.add(side);
            secondaryTip = side.above();
            bark.add(secondaryTip);
        }
        return new FruitCherryPlan(bark, stem, secondaryTip);
    }

    private static boolean canPlacePlan(
        WorldGenLevel level,
        BlockPos origin,
        List<BlockPos> positions
    ) {
        Set<BlockPos> seen = new HashSet<>();
        seen.add(origin);
        for (BlockPos position : positions) {
            if (!seen.add(position)) {
                return false; // Never overwrite a log from another limb.
            }
            BlockState state = level.getBlockState(position);
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    private record FruitCherryPlan(
        List<BlockPos> barkPositions,
        BlockPos mainTip,
        BlockPos secondaryTip
    ) {}
}
