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
 * Low and crooked Mediterranean olive.
 * All stems make at least one sideways turn instead of growing as a pillar.
 * One in ten trees produces a second, shorter ascending limb.
 *
 * Every log in a bend is placed face-to-face with the previous log: there
 * are no isolated diagonal logs or long horizontal cherry-tree branches.
 * The ancient 2x2 olive uses a separate vanilla placer.
 */
public final class OliveTrunkPlacer extends TrunkPlacer {
    private static final int FORK_VARIANT_CHANCE = 10;

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
        Stem stem = placeBentStem(level, trunkSetter, random, config, origin);
        if (stem == null) {
            return crowns;
        }

        boolean forked = random.nextInt(FORK_VARIANT_CHANCE) == 0;
        // The main limb curves away from the already leaning stem. In most
        // cases the second horizontal step changes direction rather than
        // extending into a straight beam.
        Direction primaryDirection = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos primaryTip = placeAscendingBranch(
            level, trunkSetter, random, config, stem.tip(),
            primaryDirection, 2, random.nextFloat() < 0.75F
        );
        BlockPos crownTip = primaryTip != null ? primaryTip : stem.tip();
        crowns.add(new FoliagePlacer.FoliageAttachment(crownTip.above(), 0, false));

        if (forked) {
            // Start the secondary limb lower down. Choose a direction across
            // the stem's lean and away from the primary direction, so the
            // two arms do not form a mirrored Y or collide with the stem.
            Direction left = stem.lean().getClockWise();
            Direction right = stem.lean().getCounterClockWise();
            Direction secondaryDirection = left == primaryDirection ? right
                : right == primaryDirection ? left
                : (random.nextBoolean() ? left : right);

            BlockPos secondaryTip = placeAscendingBranch(
                level, trunkSetter, random, config, stem.lowFork(),
                secondaryDirection, 1, false
            );
            if (secondaryTip != null) {
                crowns.add(new FoliagePlacer.FoliageAttachment(secondaryTip.above(), 0, false));
            }
        }

        return crowns;
    }

    /**
     * Creates the same short, crooked base for both variants.
     *
     * Two vertical blocks anchor the tree; a low horizontal log makes its
     * first compulsory lean; the stem rises again away from the base.
     * Some trees then turn once more at a right angle, never piling parallel
     * columns of logs at the base. All moves stay face-connected.
     *
     * If an obstacle prevents the compulsory lean, the placement stops
     * rather than silently producing another straight pole.
     */
    private Stem placeBentStem(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        TreeConfiguration config,
        BlockPos origin
    ) {
        if (!placeLog(level, trunkSetter, random, origin, config)) {
            return null;
        }
        BlockPos lowFork = origin.above();
        if (!placeLog(level, trunkSetter, random, lowFork, config)) {
            return null;
        }

        Direction candidate = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction lean = null;
        BlockPos tip = lowFork;
        // Try each side exactly once if terrain blocks the first choice.
        for (int attempt = 0; attempt < 4; attempt++) {
            Direction attemptedDirection = candidate;
            BlockPos sideways = lowFork.relative(attemptedDirection);
            if (placeLog(level, trunkSetter, random, sideways, config,
                state -> state.setValue(RotatedPillarBlock.AXIS, attemptedDirection.getAxis()))) {
                tip = sideways;
                lean = candidate;
                break;
            }
            candidate = candidate.getClockWise();
        }
        if (lean == null) {
            return null;
        }

        BlockPos raised = tip.above();
        if (placeLog(level, trunkSetter, random, raised, config)) {
            tip = raised;
        }

        // Optional second kink: turn to the side, never reverse into
        // already-placed stem logs. Only attempt it after the initial rise.
        if (tip.getY() > lowFork.getY() && random.nextFloat() < 0.35F) {
            Direction side = random.nextBoolean()
                ? lean.getClockWise() : lean.getCounterClockWise();
            BlockPos next = tip.relative(side);
            if (placeLog(level, trunkSetter, random, next, config,
                state -> state.setValue(RotatedPillarBlock.AXIS, side.getAxis()))) {
                tip = next;
                BlockPos up = tip.above();
                if (placeLog(level, trunkSetter, random, up, config)) {
                    tip = up;
                }
            }
        }
        return new Stem(tip, lowFork, lean);
    }

    /**
     * Builds an ascending, face-connected limb with at most two horizontal
     * moves. It climbs before/after moves in varying order and usually
     * turns sideways on the second move. The short secondary limb has only
     * one outward step, so the two forks do not form parallel beams.
     */
    private BlockPos placeAscendingBranch(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        TreeConfiguration config,
        BlockPos start,
        Direction outward,
        int horizontalSteps,
        boolean curve
    ) {
        BlockPos tip = start;
        boolean climbBeforeFirstMove = random.nextBoolean();
        int placedSteps = 0;
        Direction turn = random.nextBoolean()
            ? outward.getClockWise() : outward.getCounterClockWise();

        for (int step = 0; step < horizontalSteps; step++) {
            boolean climbBefore = step == 0
                ? climbBeforeFirstMove : random.nextBoolean();
            if (climbBefore) {
                BlockPos up = tip.above();
                if (!placeLog(level, trunkSetter, random, up, config)) {
                    break;
                }
                tip = up;
            }

            Direction travel = curve && step == horizontalSteps - 1
                ? turn : outward;
            BlockPos next = tip.relative(travel);
            if (!placeLog(level, trunkSetter, random, next, config,
                state -> state.setValue(RotatedPillarBlock.AXIS, travel.getAxis()))) {
                break;
            }
            tip = next;
            placedSteps++;

            // An upward segment follows each move that did not already
            // climb, and always caps the last move for an ascending tip.
            if (!climbBefore || step == horizontalSteps - 1) {
                BlockPos up = tip.above();
                if (!placeLog(level, trunkSetter, random, up, config)) {
                    break;
                }
                tip = up;
            }
        }
        return placedSteps == 0 ? null : tip;
    }

    private record Stem(BlockPos tip, BlockPos lowFork, Direction lean) {}
}
