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
 * Compact Mediterranean olive. About one in ten saplings grows two irregular
 * main limbs; the other trees keep a provisional single-stem silhouette.
 * Each limb is face-connected and has a separate foliage attachment.
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
        boolean forked = random.nextInt(FORK_VARIANT_CHANCE) == 0;
        int stemHeight = forked ? 2 + random.nextInt(2) : 3 + random.nextInt(2);
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

        if (!forked) {
            // Deliberately provisional: the single-stem variant will be designed
            // after the asymmetric fork has been visually approved.
            crowns.add(new FoliagePlacer.FoliageAttachment(stemTop.above(), 0, false));
            return crowns;
        }

        Direction first = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        // Perpendicular limbs are more common than opposing ones, preventing
        // the straight, mirror-symmetric Y in the previous implementation.
        Direction second = random.nextInt(5) == 0
            ? first.getOpposite()
            : (random.nextBoolean() ? first.getClockWise() : first.getCounterClockWise());
        int firstY = stemHeight - 1;
        int secondY = 1;

        BlockPos firstTip = placeAscendingBranch(
            level, trunkSetter, random, config, origin.above(firstY), first,
            2, 1 + random.nextInt(2), random.nextFloat() < 0.25F
        );
        if (firstTip != null) {
            crowns.add(new FoliagePlacer.FoliageAttachment(firstTip.above(), 0, false));
        }

        // Shorter secondary limb with a different ascent: no repeated staircase,
        // and no stack of logs running parallel at the fork.
        BlockPos secondTip = placeAscendingBranch(
            level, trunkSetter, random, config, origin.above(secondY), second,
            1 + random.nextInt(2), 1, false
        );
        if (secondTip != null) {
            crowns.add(new FoliagePlacer.FoliageAttachment(secondTip.above(), 0, false));
        }

        if (crowns.isEmpty()) {
            crowns.add(new FoliagePlacer.FoliageAttachment(stemTop.above(), 0, false));
        }
        return crowns;
    }

    /**
     * Face-connected angled limb. Ascend before or after moving outward to
     * avoid the repeated horizontal-vertical-horizontal-vertical pattern.
     * A lateral bend can replace the final outward step.
     */
    private BlockPos placeAscendingBranch(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        TreeConfiguration config,
        BlockPos start,
        Direction outward,
        int horizontalLength,
        int rise,
        boolean bend
    ) {
        BlockPos tip = start;
        int placedHorizontal = 0;
        boolean riseFirst = random.nextBoolean();
        for (int step = 0; step < horizontalLength; step++) {
            if (riseFirst && step < rise) {
                BlockPos up = tip.above();
                if (!placeLog(level, trunkSetter, random, up, config)) {
                    break;
                }
                tip = up;
            }
            Direction movement = bend && step == horizontalLength - 1
                ? (random.nextBoolean() ? outward.getClockWise() : outward.getCounterClockWise())
                : outward;
            BlockPos next = tip.relative(movement);
            if (!placeLog(level, trunkSetter, random, next, config,
                state -> state.setValue(RotatedPillarBlock.AXIS, movement.getAxis()))) {
                break;
            }
            tip = next;
            placedHorizontal++;
            if (!riseFirst && step < rise) {
                BlockPos up = tip.above();
                if (!placeLog(level, trunkSetter, random, up, config)) {
                    break;
                }
                tip = up;
            }
        }
        return placedHorizontal > 0 ? tip : null;
    }
}
