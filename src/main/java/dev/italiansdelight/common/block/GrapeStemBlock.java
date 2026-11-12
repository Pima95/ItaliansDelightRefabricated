package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CrossCollisionBlock;

/**
 * Rustic grape trunk: a normal planted crop that grows a canopy only when
 * horizontal Farmer's Delight rope is directly overhead. Grapes never appear
 * on the vertical trunk itself. Original 0..7 states remain save-compatible.
 */
public final class GrapeStemBlock extends CropBlock {
    public GrapeStemBlock(Properties properties) { super(properties); }

    @Override protected ItemLike getBaseSeedId() { return ModBlocks.GRAPE_SEEDS; }

    /** Only a horizontal, non-waterlogged rope can hold the primary vine. */
    public static Direction.Axis ropeAxis(BlockState rope) {
        if (rope.is(ModBlocks.HORIZONTAL_ROPE)) return rope.getValue(HorizontalRopeBlock.AXIS);
        if (!rope.is(vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get())
                || rope.getValue(CrossCollisionBlock.WATERLOGGED)) return null;
        boolean x = rope.getValue(CrossCollisionBlock.EAST)
                || rope.getValue(CrossCollisionBlock.WEST);
        boolean z = rope.getValue(CrossCollisionBlock.NORTH)
                || rope.getValue(CrossCollisionBlock.SOUTH);
        // Cross junctions are ambiguous: use the stronger pairs to choose.
        if (x && !z) return Direction.Axis.X;
        if (z && !x) return Direction.Axis.Z;
        if (x && z) {
            int xCount = (rope.getValue(CrossCollisionBlock.EAST) ? 1 : 0)
                + (rope.getValue(CrossCollisionBlock.WEST) ? 1 : 0);
            int zCount = (rope.getValue(CrossCollisionBlock.NORTH) ? 1 : 0)
                + (rope.getValue(CrossCollisionBlock.SOUTH) ? 1 : 0);
            return xCount >= zCount ? Direction.Axis.X : Direction.Axis.Z;
        }
        return null;
    }

    public static boolean tryGrowCanopy(ServerLevel level, BlockPos pos, BlockState current) {
        if (!current.is(ModBlocks.GRAPE_STEM)
                || current.getValue(AGE) < MAX_AGE) return false;
        BlockPos above = pos.above();
        if (!level.isLoaded(above)) return false;
        Direction.Axis axis = ropeAxis(level.getBlockState(above));
        if (axis == null) return false;
        BlockState canopy = ModBlocks.GRAPE_LEAVES.defaultBlockState()
                .setValue(GrapeLeavesBlock.AXIS, axis);
        level.setBlock(above, canopy, 3);
        return true;
    }

    // Vanilla CropBlock stops ticking at maturity; rope-driven climbing must
    // remain active if the player adds the support AFTER the plant ripens.
    @Override public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        tryGrowCanopy(level, pos, level.getBlockState(pos));
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state, BonemealSource source) {
        if (!isMaxAge(state)) return true;
        return ropeAxis(level.getBlockState(pos.above())) != null;
    }

    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        if (!isMaxAge(state)) super.performBonemeal(level, random, pos, state, source);
        tryGrowCanopy(level, pos, level.getBlockState(pos));
    }
}
