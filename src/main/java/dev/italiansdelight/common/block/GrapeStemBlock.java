package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;

/**
 * Rustic grape trunk: a normal planted crop that grows a canopy only when
 * horizontal Farmer's Delight rope is directly overhead. Grapes never appear
 * on the vertical trunk itself. Original 0..7 states remain save-compatible.
 */
public final class GrapeStemBlock extends CropBlock {
    public GrapeStemBlock(Properties properties) { super(properties); }

    @Override protected ItemLike getBaseSeedId() { return ModBlocks.GRAPE_SEEDS; }

    /** Vineyard leaves use ONLY the dedicated horizontal rope, never FDR's vertical rope. */
    public static Direction.Axis ropeAxis(BlockState rope) {
        return rope.is(ModBlocks.HORIZONTAL_ROPE)
            ? rope.getValue(HorizontalRopeBlock.AXIS) : null;
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
        if (level.getBlockState(above).is(ModBlocks.HORIZONTAL_ROPE)) {
            canopy = canopy.setValue(GrapeLeavesBlock.TIED_NEGATIVE,
                        level.getBlockState(above).getValue(HorizontalRopeBlock.TIED_NEGATIVE))
                    .setValue(GrapeLeavesBlock.TIED_POSITIVE,
                        level.getBlockState(above).getValue(HorizontalRopeBlock.TIED_POSITIVE));
        }
        return level.setBlock(above, canopy, Block.UPDATE_ALL);
    }

    // A ripe grapevine must react when a player adds the dedicated horizontal
    // rope afterwards, not only when the random-tick scheduler selects it.
    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (direction == Direction.UP && state.getValue(AGE) == MAX_AGE
                && ropeAxis(neighbor) != null) {
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, level, ticks, pos, direction,
            neighborPos, neighbor, random);
    }

    @Override public void tick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        tryGrowCanopy(level, pos, state);
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
