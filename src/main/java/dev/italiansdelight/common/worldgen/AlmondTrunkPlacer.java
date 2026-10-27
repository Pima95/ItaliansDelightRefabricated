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
 * Almond trees are taller than olive trees: five to seven wood layers,
 * a gently curved crown-bearing stem, occasionally a short ascending branch.
 * All logs touch along block faces and the entire trunk is checked before
 * replacing any sapling. This feature is only for planted saplings.
 */
public final class AlmondTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<AlmondTrunkPlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, AlmondTrunkPlacer::new));

    public AlmondTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreePlacers.ALMOND_TRUNK;
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
        Direction lean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        Direction branchDirection = random.nextBoolean()
            ? lean.getClockWise() : lean.getCounterClockWise();
        boolean crooked = random.nextFloat() < 0.7F;
        boolean forked = treeHeight >= 6 && random.nextFloat() < 0.35F;
        List<BlockPos> planned = new ArrayList<>();
        BlockPos stem = origin;
        BlockPos forkStart = null;
        for (int y = 1; y < treeHeight; y++) {
            stem = stem.above();
            planned.add(stem);
            if (y == treeHeight - 3) {
                forkStart = stem;
            }
            // The bend stays connected by a visible, one-block lateral log.
            if (crooked && y == 3) {
                stem = stem.relative(lean);
                planned.add(stem);
            }
        }
        BlockPos tip = stem;
        BlockPos forkTip = null;
        if (forked && forkStart != null) {
            BlockPos branch = forkStart.relative(branchDirection);
            forkTip = branch.above();
            planned.add(branch);
            planned.add(forkTip);
        }

        Set<BlockPos> seen = new HashSet<>();
        seen.add(origin);
        for (BlockPos pos : planned) {
            if (!seen.add(pos)) {
                return List.of(); // Reject intersecting limbs, do not half-build.
            }
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.canBeReplaced()) {
                return List.of(); // Blocked: leave the original sapling intact.
            }
        }
        placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), config);
        if (!placeLog(level, trunkSetter, random, origin, config)) {
            return List.of();
        }
        for (BlockPos pos : planned) {
            if (!placeLog(level, trunkSetter, random, pos, config)) {
                return List.of();
            }
        }

        List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>(2);
        crowns.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));
        if (forkTip != null) {
            crowns.add(new FoliagePlacer.FoliageAttachment(forkTip.above(), 0, false));
        }
        return crowns;
    }
}
