package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
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
 * Crooked Mediterranean olives with an explicitly selected trunk height.
 *
 * 90% are single-crowned; 10% of the singles have 6-block-high trunks.
 * The remaining singles and every forked tree have 4- or 5-block-high
 * trunks. One tree in ten has two genuinely separate ascending limbs.
 *
 * All wood paths are face-connected. The low bend and forks use all-bark
 * olive wood to hide exposed log rings. The ancient 2x2 tree is separate.
 */
public final class OliveTrunkPlacer extends TrunkPlacer {
    private static final int FORK_VARIANT_CHANCE = 10;
    private static final int TALL_SINGLE_CHANCE = 10;

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
        List<FoliagePlacer.FoliageAttachment> crowns = new ArrayList<>(2);
        // Select the shape BEFORE checking placement, rather than silently
        // changing an obstructed bifurcated tree into a single tree.
        boolean forked = random.nextInt(FORK_VARIANT_CHANCE) == 0;
        int trunkHeight = !forked && random.nextInt(TALL_SINGLE_CHANCE) == 0
            ? 6 : 4 + random.nextInt(2);

        Direction firstLean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        boolean clockwise = random.nextBoolean();
        boolean upperKink = random.nextBoolean();

        // Try four rotations of the complete plan. In a clear area the first
        // orientation works, ensuring the planned 10% fork rate is preserved.
        for (int attempt = 0; attempt < 4; attempt++) {
            Direction lean = firstLean;
            for (int rotation = 0; rotation < attempt; rotation++) {
                lean = lean.getClockWise();
            }
            Direction turn = clockwise ? lean.getClockWise() : lean.getCounterClockWise();
            OlivePlan plan = planTree(origin, trunkHeight, forked, lean, turn, upperKink);
            if (!canPlacePlan(level, origin, plan.barkPositions())) {
                continue;
            }

            placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), config);
            if (!placeLog(level, trunkSetter, random, origin, config)) {
                return crowns;
            }

            for (BlockPos position : plan.barkPositions()) {
                if (!placeBark(level, trunkSetter, random, position, config)) {
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
     * Assemble the entire connected tree before placing any wood.
     * The main trunk leans at y=1 and bends again at y=3.
     * y=3, 4 and 5 are the uppermost possible wood levels respectively
     * for 4-, 5- and 6-block trunks (counting the planted block as level 1).
     */
    private OlivePlan planTree(
        BlockPos origin,
        int height,
        boolean forked,
        Direction lean,
        Direction turn,
        boolean upperKink
    ) {
        List<BlockPos> bark = new ArrayList<>(16);
        BlockPos forkBase = origin.above();
        bark.add(forkBase);
        BlockPos lowSide = forkBase.relative(lean);
        bark.add(lowSide);
        BlockPos raisedStem = lowSide.above();
        bark.add(raisedStem);

        // The first main limb rises and THEN turns; no two-block-long
        // horizontal beam. The y=3 bend also hides its end grain.
        BlockPos mainTip = raisedStem.above();
        bark.add(mainTip);
        mainTip = mainTip.relative(turn);
        bark.add(mainTip);

        if (height >= 5) {
            mainTip = mainTip.above();
            bark.add(mainTip);
        }
        if (height == 6) {
            // An optional third gentle bend, always remaining below y=6.
            if (upperKink) {
                mainTip = mainTip.relative(lean);
                bark.add(mainTip);
            }
            mainTip = mainTip.above();
            bark.add(mainTip);
        }

        BlockPos secondaryTip = null;
        if (forked) {
            // The secondary limb starts at the SECOND trunk block, not at
            // the canopy. Its path curves outward and rises at each stage.
            // This separates the two visible limbs by roughly 3-4 blocks.
            Direction secondary = turn.getOpposite();
            BlockPos lowerFork = forkBase.relative(secondary);
            bark.add(lowerFork);
            BlockPos raisedFork = lowerFork.above();
            bark.add(raisedFork);
            BlockPos outerFork = raisedFork.relative(secondary);
            bark.add(outerFork);
            secondaryTip = outerFork.above();
            bark.add(secondaryTip);
        }

        return new OlivePlan(bark, mainTip, secondaryTip);
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

    private boolean placeBark(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        BlockPos position,
        TreeConfiguration config
    ) {
        return placeLog(level, trunkSetter, random, position, config,
            state -> ModBlocks.OLIVE_WOOD.defaultBlockState());
    }

    private record OlivePlan(
        List<BlockPos> barkPositions,
        BlockPos mainTip,
        BlockPos secondaryTip
    ) {}
}
