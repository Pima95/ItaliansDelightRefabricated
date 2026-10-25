package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Natural Mediterranean olive crown generator.
 *
 * Real olive trees possess a light, airy umbrella- or bowl-like canopy
 * formed by soft cushions (cuscini) of silvery foliage. The canopy is centered
 * on the limb axis without lopsided drift, with smoothly rounded layers that avoid
 * cubic boxiness. It maintains at least 2 clear air blocks above ground level.
 * On tall trees (6 blocks high), foliage envelops down to the 3rd-last trunk block;
 * on shorter trees (4-5 blocks high), it envelops down at most to the 2nd-last block.
 */
public final class OliveFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<OliveFoliagePlacer> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, OliveFoliagePlacer::new));

    public OliveFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModTreePlacers.OLIVE_FOLIAGE;
    }

    @Override
    public int foliageHeight(RandomSource random, int treeHeight, TreeConfiguration config) {
        return 4;
    }

    @Override
    protected boolean shouldSkipLocation(
        RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk
    ) {
        int distance = dx * dx + dz * dz;
        if (distance > currentRadius * currentRadius + 1) {
            return true;
        }
        return distance >= currentRadius * currentRadius && random.nextFloat() < 0.55F;
    }

    @Override
    protected void createFoliage(
        WorldGenLevel level,
        FoliageSetter foliageSetter,
        RandomSource random,
        TreeConfiguration config,
        int treeHeight,
        FoliageAttachment attachment,
        int foliageHeight,
        int leafRadius,
        int offset
    ) {
        // Anchor directly on the topmost trunk log (mainTip)
        BlockPos anchor = attachment.pos().above(offset).below();
        double radiusScale = Math.max(0.8D, leafRadius / 2.0D);

        // Determine ground elevation under the tree to check trunk height and air clearance
        int groundY = anchor.getY() - 1;
        while (groundY > level.getMinY()) {
            BlockPos checkPos = new BlockPos(anchor.getX(), groundY, anchor.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.OLIVE_LEAVES)
                && !state.is(ModBlocks.OLIVE_LOG)
                && !state.is(ModBlocks.OLIVE_WOOD)) {
                break;
            }
            groundY--;
        }
        int trunkHeightAboveGround = anchor.getY() - groundY;
        boolean isTallTrunk = trunkHeightAboveGround >= 5;

        // If trunk is 6 blocks tall, envelop down to terzultimo blocco (dy = -2).
        // Otherwise (4-5 blocks tall), envelop maximum down to secondo blocco (dy = -1).
        int minDy = isTallTrunk ? -2 : -1;

        // Base radii per vertical layer to form a convex rounded dome without flat boxy walls
        // dy = -2: tight collar wrapping lower log (radius ~1.65)
        // dy = -1: lower canopy wrapping branch (radius ~2.15)
        // dy =  0: main equator, widest (radius ~2.55)
        // dy =  1: upper dome (radius ~2.05)
        // dy =  2: crown peak (radius ~1.20, soft plus-shape summit)
        double rMinus2 = 2.05D * radiusScale;
        double rMinus1 = 2.55D * radiusScale;
        double rZero   = 2.95D * radiusScale;
        double rPlus1  = 2.45D * radiusScale;
        double rPlus2  = 1.55D * radiusScale;

        // Radial harmonic modulation for organic cushions, kept symmetric/balanced
        // so the center of mass stays directly over the trunk axis (never lopsided).
        int waveFreq = random.nextBoolean() ? 3 : 4;
        double wavePhase = random.nextDouble() * 2.0D * Math.PI;
        double waveAmp = 0.12D + random.nextDouble() * 0.06D;

        // Micro-jitter strictly limited to +/- 0.15 blocks
        double jitterX = random.nextDouble() * 0.30D - 0.15D;
        double jitterZ = random.nextDouble() * 0.30D - 0.15D;

        Set<BlockPos> planned = new HashSet<>();

        for (int dy = minDy; dy <= 2; dy++) {
            // Strictly enforce at least 2 clear air blocks between ground and leaves
            if (anchor.getY() + dy - groundY < 3) {
                continue;
            }

            double baseR;
            if (dy == -2) baseR = rMinus2;
            else if (dy == -1) baseR = rMinus1;
            else if (dy == 0) baseR = rZero;
            else if (dy == 1) baseR = rPlus1;
            else baseR = rPlus2;

            baseR += (random.nextDouble() * 0.16D - 0.08D);

            for (int dx = -4; dx <= 4; dx++) {
                for (int dz = -4; dz <= 4; dz++) {
                    double ox = dx - jitterX;
                    double oz = dz - jitterZ;
                    double distH = Math.sqrt(ox * ox + oz * oz);

                    if (distH > baseR + 0.50D) {
                        continue;
                    }

                    // Cut sharp diagonal corners (never generate a cubic square box)
                    if (Math.abs(dx) >= 2 && Math.abs(dz) >= 2 && distH > 2.45D) {
                        continue;
                    }

                    double theta = distH > 0.0D ? Math.atan2(oz, ox) : 0.0D;
                    double localR = baseR * (1.0D + waveAmp * Math.cos(waveFreq * theta + wavePhase));

                    BlockPos leafPos = anchor.offset(dx, dy, dz);
                    if (!hasGroundClearance(level, leafPos)) {
                        continue;
                    }

                    if (distH <= localR) {
                        // Light skip chance on outer boundary for airy olive foliage
                        if (distH > localR - 0.40D && random.nextFloat() < 0.20F) {
                            continue;
                        }
                        planned.add(leafPos);
                    } else if (distH <= localR + 0.45D) {
                        // Soft outer feathering on equator and lower layer
                        if ((dy == 0 || dy == -1) && random.nextFloat() < 0.22F) {
                            planned.add(leafPos);
                        }
                    }
                }
            }
        }

        // Place leaves, ensuring no disconnected floating singletons.
        // A leaf is connected if it touches the upper trunk logs or another placed leaf.
        Direction[] directions = Direction.values();
        BlockPos trunk1 = anchor;
        BlockPos trunk2 = anchor.below(1);
        BlockPos trunk3 = anchor.below(2);

        for (BlockPos pos : planned) {
            boolean connected = false;
            for (Direction dir : directions) {
                BlockPos neighbor = pos.relative(dir);
                if (neighbor.equals(trunk1) || neighbor.equals(trunk2)
                    || (isTallTrunk && neighbor.equals(trunk3))
                    || planned.contains(neighbor)) {
                    connected = true;
                    break;
                }
            }
            if (connected) {
                tryPlaceLeaf(level, foliageSetter, random, config, pos);
            }
        }
    }

    /**
     * Enforce at least 2 blocks of clear vertical clearance between the ground and leaves.
     * Both pos.below(1) and pos.below(2) must not be solid terrain/ground.
     */
    private static boolean hasGroundClearance(WorldGenLevel level, BlockPos pos) {
        for (int down = 1; down <= 2; down++) {
            BlockPos check = pos.below(down);
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.canBeReplaced()
                && !state.is(ModBlocks.OLIVE_LEAVES)
                && !state.is(ModBlocks.OLIVE_LOG)
                && !state.is(ModBlocks.OLIVE_WOOD)) {
                return false;
            }
        }
        return true;
    }
}
