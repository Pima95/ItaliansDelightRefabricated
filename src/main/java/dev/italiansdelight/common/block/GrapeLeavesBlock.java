package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * Rustic vine canopy: center replaces a rope above a mature grape stem;
 * it extends at most ONE rope block on each side along the horizontal axis.
 * Fruit develops only on outer segments with air underneath. Breaking a
 * canopy segment restores the existing FDR rope, never duplicates a rope item.
 */
public final class GrapeLeavesBlock extends Block implements BonemealableBlock, ReplaceOnRemoval {
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 1);
    public static final EnumProperty<Direction.Axis> AXIS =
        EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);
    public static final BooleanProperty GRAPES = BooleanProperty.create("grapes");
    public static final BooleanProperty TIED_NEGATIVE = BooleanProperty.create("tied_negative");
    public static final BooleanProperty TIED_POSITIVE = BooleanProperty.create("tied_positive");

    public GrapeLeavesBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DISTANCE, 0)
            .setValue(AXIS, Direction.Axis.X).setValue(GRAPES, false)
            .setValue(TIED_NEGATIVE, false).setValue(TIED_POSITIVE, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(DISTANCE, AXIS, GRAPES, TIED_NEGATIVE, TIED_POSITIVE);
    }

    @Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(DISTANCE) == 0) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(ModBlocks.GRAPE_STEM) &&
                below.getValue(net.minecraft.world.level.block.CropBlock.AGE) == 7;
        }
        if (state.getValue(AXIS) == Direction.Axis.X) {
            return isCenter(level, pos.east(), Direction.Axis.X)
                || isCenter(level, pos.west(), Direction.Axis.X);
        }
        return isCenter(level, pos.north(), Direction.Axis.Z)
            || isCenter(level, pos.south(), Direction.Axis.Z);
    }

    private static boolean isCenter(LevelReader level, BlockPos pos, Direction.Axis axis) {
        BlockState neighbor = level.getBlockState(pos);
        return neighbor.is(ModBlocks.GRAPE_LEAVES)
            && neighbor.getValue(DISTANCE) == 0 && neighbor.getValue(AXIS) == axis;
    }

    private void spread(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(DISTANCE) != 0) return;
        Direction[] directions = state.getValue(AXIS) == Direction.Axis.X
                ? new Direction[] {Direction.EAST, Direction.WEST}
                : new Direction[] {Direction.NORTH, Direction.SOUTH};
        for (Direction direction : directions) {
            BlockPos target = pos.relative(direction);
            if (!level.isLoaded(target) || !level.getBlockState(target.below()).isAir())
                continue;
            BlockState old = level.getBlockState(target);
            // The new horizontal support has its own saved axis. Older FDR
            // rope blocks are still accepted when their connections align.
            Direction.Axis axis = GrapeStemBlock.ropeAxis(old);
            if (axis != state.getValue(AXIS)) continue;
            // Preserve the rope axis via the GrapeLeaves state until broken.
            BlockState grown = defaultBlockState().setValue(AXIS,
                state.getValue(AXIS)).setValue(DISTANCE, 1);
            if (old.is(ModBlocks.HORIZONTAL_ROPE)) {
                grown = grown.setValue(TIED_NEGATIVE,
                        old.getValue(HorizontalRopeBlock.TIED_NEGATIVE))
                    .setValue(TIED_POSITIVE,
                        old.getValue(HorizontalRopeBlock.TIED_POSITIVE));
            }
            level.setBlock(target, grown, 3);
        }
    }

    @Override public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
            return;
        }
        if (state.getValue(DISTANCE) == 0) {
            if (random.nextInt(5) == 0) spread(level, pos, state);
        } else if (!state.getValue(GRAPES) && level.getBlockState(pos.below()).isAir()
                && random.nextInt(6) == 0) {
            level.setBlock(pos, state.setValue(GRAPES, true), Block.UPDATE_CLIENTS);
        } else if (state.getValue(GRAPES) && !level.getBlockState(pos.below()).isAir()) {
            level.setBlock(pos, state.setValue(GRAPES, false), Block.UPDATE_CLIENTS);
        }
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side, BlockPos neighborPos,
            BlockState neighbor, RandomSource random) {
        if (!state.canSurvive(level, pos)) ticks.scheduleTick(pos, this, 1);
        Direction.Axis axis = state.getValue(AXIS);
        if ((axis == Direction.Axis.X && side == Direction.WEST)
                || (axis == Direction.Axis.Z && side == Direction.NORTH))
            return state.setValue(TIED_NEGATIVE, neighbor.is(ModBlocks.CROP_STAKE));
        if ((axis == Direction.Axis.X && side == Direction.EAST)
                || (axis == Direction.Axis.Z && side == Direction.SOUTH))
            return state.setValue(TIED_POSITIVE, neighbor.is(ModBlocks.CROP_STAKE));
        return state;
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.removeBlock(pos, false);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isSpectator() || !state.getValue(GRAPES)
                || state.getValue(DISTANCE) == 0) return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            BlockState current = server.getBlockState(pos);
            if (!current.is(this) || !current.getValue(GRAPES)
                    || !server.getBlockState(pos.below()).isAir()) return InteractionResult.PASS;
            server.setBlock(pos, current.setValue(GRAPES, false), Block.UPDATE_CLIENTS);
            Block.popResource(server, pos.below(), new ItemStack(
                dev.italiansdelight.common.registry.ModItems.GRAPES,
                1 + server.getRandom().nextInt(2)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state, BonemealSource source) {
        return state.getValue(DISTANCE) == 0
            || (!state.getValue(GRAPES) && level.getBlockState(pos.below()).isAir());
    }
    @Override public boolean isBonemealSuccess(Level level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) { return true; }
    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        if (!state.canSurvive(level, pos)) return;
        if (state.getValue(DISTANCE) == 0) spread(level, pos, state);
        else if (level.getBlockState(pos.below()).isAir())
            level.setBlock(pos, state.setValue(GRAPES, true), Block.UPDATE_CLIENTS);
    }

    @Override public boolean onRemove(BlockState state, BlockState newState,
            LevelAccessor level, BlockPos pos, int flags) {
        if (newState.isAir() && !state.is(newState.getBlock())) {
            BlockState rope = HorizontalRopeBlock.withConnections(
                ModBlocks.HORIZONTAL_ROPE.defaultBlockState()
                    .setValue(HorizontalRopeBlock.AXIS, state.getValue(AXIS)),
                level, pos);
            return level.setBlock(pos, rope, 3);
        }
        return false;
    }
}
