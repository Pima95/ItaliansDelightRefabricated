package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
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
 * Short Mediterranean olive with a crooked low stem, never a tall staircase.
 * The main canopy is supported at y=2 relative to the sapling; leaves reach
 * at most y=4 with the current foliage placer. One tree in ten can split
 * into a second short limb growing from the low fork.
 *
 * Each bend uses the all-bark olive_wood block: no visible log end grain
 * on sideways joints. Every placed block is connected through a full face.
 * Ancient 2x2 olives are configured separately and are unaffected.
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

        Stem stem = placeLowBentStem(level, trunkSetter, random, origin, config);
        if (stem == null) {
            return crowns;
        }

        // A short, sideways-curving continuation at the crown's height:
        // no extra upward steps and no repeated straight horizontal beams.
        boolean forked = random.nextInt(FORK_VARIANT_CHANCE) == 0;
        Direction turn = random.nextBoolean()
            ? stem.lean().getClockWise() : stem.lean().getCounterClockWise();
        BlockPos mainTip = stem.tip();
        if (forked || random.nextFloat() < 0.70F) {
            BlockPos crookedTip = mainTip.relative(turn);
            if (canGrowInto(level, crookedTip)
                && placeBark(level, trunkSetter, random, crookedTip, config)) {
                mainTip = crookedTip;
            }
        }
        crowns.add(new FoliagePlacer.FoliageAttachment(mainTip.above(), 0, false));

        // A genuinely rare second branch, shorter than the first one. Both
        // tips stay at y=2: the fork broadens the crown instead of making a
        // tall or doubled-up trunk.
        if (forked) {
            Direction secondary = turn.getOpposite();
            BlockPos lowSide = stem.forkBase().relative(secondary);
            BlockPos secondTip = lowSide.above();
            // Plan both steps before placing either: never leave the lone
            // horizontal stump visible in the screenshot of the old fork.
            if (canGrowInto(level, lowSide) && canGrowInto(level, secondTip)
                && placeBark(level, trunkSetter, random, lowSide, config)
                && placeBark(level, trunkSetter, random, secondTip, config)) {
                crowns.add(new FoliagePlacer.FoliageAttachment(secondTip.above(), 0, false));
            }
        }

        return crowns;
    }

    /**
     * A low, connected kink: origin (y=0), fork base (y=1), sideways at y=1,
     * and one final rise at y=2. The stem never reaches y=3 or beyond.
     * The horizontal segment has bark on every face, not exposed end grain.
     */
    private Stem placeLowBentStem(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        BlockPos origin,
        TreeConfiguration config
    ) {
        if (!placeLog(level, trunkSetter, random, origin, config)) {
            return null;
        }
        BlockPos forkBase = origin.above();
        if (!placeBark(level, trunkSetter, random, forkBase, config)) {
            return null;
        }

        Direction candidate = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        for (int attempt = 0; attempt < 4; attempt++) {
            BlockPos lowSide = forkBase.relative(candidate);
            BlockPos raisedTip = lowSide.above();
            // Precheck the entire bend so a blocked vertical step does not
            // result in an isolated piece jutting out at waist height.
            if (canGrowInto(level, lowSide) && canGrowInto(level, raisedTip)
                && placeBark(level, trunkSetter, random, lowSide, config)
                && placeBark(level, trunkSetter, random, raisedTip, config)) {
                return new Stem(raisedTip, forkBase, candidate);
            }
            candidate = candidate.getClockWise();
        }
        // A blocked tree has no normal crown instead of a malformed
        // straight trunk or loose horizontal branch.
        return null;
    }

    private static boolean canGrowInto(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    private boolean placeBark(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        BlockPos pos,
        TreeConfiguration config
    ) {
        return placeLog(level, trunkSetter, random, pos, config,
            state -> ModBlocks.OLIVE_WOOD.defaultBlockState());
    }

    private record Stem(BlockPos tip, BlockPos forkBase, Direction lean) {}
}
