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
 * An olive grows a short upright base, bends near its crown and sends out
 * two or three raised branches. Unlike FancyTrunkPlacer, all foliage anchors
 * belong to real log endpoints ABOVE the trunk base: no canopy at ground.
 *
 * The same vanilla TreeFeature contract handles water/obstacle checks, block
 * scheduling and leaf distance afterwards. No natural worldgen is registered.
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

        List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>();
        // Keep the first 3 blocks upright and connected to the planted root.
        int bendLevel = Math.max(3, treeHeight - 2);
        Direction bend = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos center = origin;
        BlockPos topLog = null;

        for (int y = 0; y < treeHeight; y++) {
            BlockPos previous = center;
            if (y == bendLevel) {
                // Add a horizontal bridge BEFORE moving the trunk axis, so
                // it remains connected even when the next section bends.
                BlockPos bridge = previous.above(y);
                placeLog(level, trunkSetter, random, bridge, config);
                center = center.relative(bend);
            }
            BlockPos spot = center.above(y);
            if (placeLog(level, trunkSetter, random, spot, config)) {
                topLog = spot;
            }
        }

        if (topLog != null) {
            crowns.add(new FoliagePlacer.FoliageAttachment(topLog.above(), 0, false));
        }

        // Branch tips spread outward and rise as they leave the trunk,
        // carrying separate olive foliage clusters instead of an oak sphere.
        int count = 2 + random.nextInt(2);
        int firstDirection = random.nextInt(4);
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = 0; i < count; i++) {
            Direction outward = directions[(firstDirection + i * 4 / count) % 4];
            int branchY = Math.max(3, treeHeight - 2 - random.nextInt(2));
            // A branch below the bend must start on the ORIGINAL trunk,
            // not on its higher displaced axis, otherwise it would float.
            BlockPos base = (branchY < bendLevel ? origin : center).above(branchY);
            BlockPos tip = null;
            int length = 2 + random.nextInt(2);
            for (int step = 1; step <= length; step++) {
                BlockPos branchPos = base.relative(outward, step);
                if (!placeLog(level, trunkSetter, random, branchPos, config,
                        state -> state.setValue(RotatedPillarBlock.AXIS, outward.getAxis()))) {
                    break;
                }
                tip = branchPos;
            }
            if (tip != null) {
                // Raise the crown with a VERTICAL log on the horizontal
                // branch tip. The upward segment has a shared face with the
                // last branch log, unlike a detached diagonal step.
                BlockPos raisedTip = tip.above();
                if (placeLog(level, trunkSetter, random, raisedTip, config)) {
                    tip = raisedTip;
                }
                crowns.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));
            }
        }

        return crowns;
    }
}
