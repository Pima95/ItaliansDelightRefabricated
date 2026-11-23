package dev.italiansdelight.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.italiansdelight.common.registry.ModBlocks;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * Broad, rounded Mediterranean olive crowns, not square Minecraft canopies.
 *
 * The five tapered layers have soft, irregular boundaries and small peripheral
 * tufts. They stay centered above each limb rather than drifting sideways.
 * Every leaf must be connected through other foliage to the actual olive wood.
 * Leaves are kept at least two air blocks above solid ground.
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
        // Trunk placer attaches above its tip; the crown is centered on that
        // tip. The second fork, when present, gets its own overlapping crown.
        BlockPos anchor = attachment.pos().above(offset).below();
        double radiusScale = Math.max(0.8D, leafRadius / 2.0D);

        // Find the actual terrain under this crown, ignoring existing olive
        // wood/leaves so that the clearance does not count a branch as ground.
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
        // A tall trunk can carry foliage one layer lower than short trunks.
        int minDy = trunkHeightAboveGround >= 5 ? -2 : -1;

        // Rounded olive dome: wide middle, compact crest, soft lower skirt.
        // Keep these approved proportions; no texture or trunk changes here.
        double rMinus2 = 2.05D * radiusScale;
        double rMinus1 = 2.55D * radiusScale;
        double rZero   = 2.95D * radiusScale;
        double rPlus1  = 2.45D * radiusScale;
        double rPlus2  = 1.55D * radiusScale;

        int waveFreq = random.nextBoolean() ? 3 : 4;
        double wavePhase = random.nextDouble() * 2.0D * Math.PI;
        double waveAmp = 0.12D + random.nextDouble() * 0.06D;

        // Preserve insertion order for deterministic foliage placement.
        Set<BlockPos> planned = new LinkedHashSet<>();

        for (int dy = minDy; dy <= 2; dy++) {
            // Never lower the foliage into the player's two-block clearance.
            if (anchor.getY() + dy - groundY < 3) {
                continue;
            }

            double baseR;
            if (dy == -2) {
                baseR = rMinus2;
            } else if (dy == -1) {
                baseR = rMinus1;
            } else if (dy == 0) {
                baseR = rZero;
            } else if (dy == 1) {
                baseR = rPlus1;
            } else {
                baseR = rPlus2;
            }
            baseR += random.nextDouble() * 0.16D - 0.08D;

            // Tiny variation per layer breaks straight vertical walls without
            // moving the center of the whole crown off its branch.
            double jitterX = random.nextDouble() * 0.40D - 0.20D;
            double jitterZ = random.nextDouble() * 0.40D - 0.20D;

            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    double ox = dx - jitterX;
                    double oz = dz - jitterZ;
                    double distH = Math.sqrt(ox * ox + oz * oz);

                    if (distH > baseR + 0.45D) {
                        continue;
                    }
                    // Reject the far diagonals: no cuboid canopy corners.
                    if (Math.abs(dx) >= 2 && Math.abs(dz) >= 2
                        && distH > baseR * 0.85D) {
                        continue;
                    }

                    double theta = distH > 0.0D ? Math.atan2(oz, ox) : 0.0D;
                    double localR = baseR
                        * (1.0D + waveAmp * Math.cos(waveFreq * theta + wavePhase));

                    BlockPos leafPos = anchor.offset(dx, dy, dz);
                    if (!hasGroundClearance(level, leafPos)) {
                        continue;
                    }

                    if (distH <= localR) {
                        // Sparse holes at the rim, never an empty solid center.
                        if (distH > localR - 0.40D && random.nextFloat() < 0.20F) {
                            continue;
                        }
                        planned.add(leafPos);
                    } else if (distH <= localR + 0.45D) {
                        // Occasional tufts near the equator make the silhouette
                        // irregular without creating floating distant clusters.
                        if ((dy == 0 || dy == -1) && random.nextFloat() < 0.22F) {
                            planned.add(leafPos);
                        }
                    }
                }
            }
        }

        // Keep only components that are reachable from real olive wood via
        // face-connected planned leaves. Checking just one planned neighbor
        // would accidentally allow floating pairs and isolated clusters.
        Deque<BlockPos> frontier = new ArrayDeque<>();
        Set<BlockPos> connected = new HashSet<>();
        for (BlockPos pos : planned) {
            for (Direction dir : Direction.values()) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                if (neighbor.is(ModBlocks.OLIVE_LOG) || neighbor.is(ModBlocks.OLIVE_WOOD)) {
                    if (connected.add(pos)) {
                        frontier.addLast(pos);
                    }
                    break;
                }
            }
        }

        while (!frontier.isEmpty()) {
            BlockPos pos = frontier.removeFirst();
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (planned.contains(next) && connected.add(next)) {
                    frontier.addLast(next);
                }
            }
        }

        for (BlockPos pos : planned) {
            if (connected.contains(pos)) {
                tryPlaceLeaf(level, foliageSetter, random, config, pos);
            }
        }
    }

    /** Keep two blocks of clearance below leaves, without treating tree wood as terrain. */
    private static boolean hasGroundClearance(WorldGenLevel level, BlockPos pos) {
        for (int down = 1; down <= 2; down++) {
            BlockState state = level.getBlockState(pos.below(down));
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
