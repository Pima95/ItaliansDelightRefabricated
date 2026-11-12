package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * Standalone chili crop with Farmer's Delight tomato-style rope climbing.
 * Three blocks at most (the farmland root plus two vertical rope segments).
 * The legacy staked and rope_tied properties are read only for saved worlds.
 */
public class ChiliCropBlock extends CropBlock implements ReplaceOnRemoval {
    public static final BooleanProperty STAKED = BooleanProperty.create("staked");

    public ChiliCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CropStakeBlock.ROPE_TIED, false)
            .setValue(STAKED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CropStakeBlock.ROPE_TIED, STAKED);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(STAKED, false)
            .setValue(CropStakeBlock.ROPE_TIED, false);
    }

    @Override protected ItemLike getBaseSeedId() { return ModBlocks.CHILI_PEPPER_SEEDS; }
    protected boolean shouldRestoreRope() { return false; }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
            BlockPos pos, CollisionContext context) { return Shapes.empty(); }

    @Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (StakeSoilTilling.isFarmland(level.getBlockState(pos.below())))
            return level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
        return super.canSurvive(state, level, pos);
    }

    private boolean canClimb(LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get())
            && !above.getValue(net.minecraft.world.level.block.CrossCollisionBlock.WATERLOGGED);
    }

    private int vineHeight(LevelReader level, BlockPos pos) {
        int n = 1;
        while (n < 3) {
            BlockState below = level.getBlockState(pos.below(n));
            if (!below.is(ModBlocks.CHILI_CROP) && !below.is(ModBlocks.CHILI_ON_ROPE)) break;
            ++n;
        }
        return n;
    }

    public boolean tryClimb(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.is(this) || getAge(state) < getMaxAge()
                || !canClimb(level, pos) || vineHeight(level, pos) >= 3) return false;
        return level.setBlock(pos.above(), ModBlocks.CHILI_ON_ROPE.defaultBlockState()
            .setValue(AGE, 0).setValue(STAKED, false), Block.UPDATE_ALL);
    }

    @Override public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override public void randomTick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        // Old staked root states become ordinary plants, preserving their age.
        if (state.getValue(STAKED) || state.getValue(CropStakeBlock.ROPE_TIED)) {
            state = state.setValue(STAKED, false).setValue(CropStakeBlock.ROPE_TIED, false);
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
        super.randomTick(state, level, pos, random);
        BlockState current = level.getBlockState(pos);
        if (current.is(this) && random.nextInt(3) == 0)
            tryClimb(level, pos, current);
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state, BonemealSource source) {
        return !isMaxAge(state) || (canClimb(level, pos) && vineHeight(level, pos) < 3);
    }

    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        if (!isMaxAge(state)) {
            state = state.setValue(AGE, Math.min(getMaxAge(), getAge(state) + 1 + random.nextInt(2)))
                .setValue(STAKED, false).setValue(CropStakeBlock.ROPE_TIED, false);
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        BlockState current = level.getBlockState(pos);
        if (current.is(this)) tryClimb(level, pos, current);
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction changedSide,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            ticks.scheduleTick(pos, this, 1);
            return state;
        }
        return super.updateShape(state, level, ticks, pos, changedSide, neighborPos, neighbor, random);
    }

    @Override public void tick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (!isMaxAge(state) || player.isSpectator()) return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            BlockState current = server.getBlockState(pos);
            if (!current.is(this) || !isMaxAge(current)) return InteractionResult.PASS;
            server.setBlock(pos, current.setValue(AGE, 6), Block.UPDATE_CLIENTS);
            boolean rare = server.getRandom().nextInt(42) == 0;
            Block.popResource(server, pos, new ItemStack(rare
                ? ModItems.GHOST_PEPPER : ModItems.CHILI_PEPPER,
                rare ? 1 : 1 + server.getRandom().nextInt(2)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override public boolean onRemove(BlockState state, BlockState next,
            LevelAccessor level, BlockPos pos, int flags) {
        if (!shouldRestoreRope() || !next.isAir() || state.is(next.getBlock()))
            return false;
        BlockState rope = vectorwing.farmersdelight.common.block.RopeBlock.getStateWithConnections(
            vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get().defaultBlockState(),
            level, pos, Direction.UP);
        return level.setBlock(pos, rope, Block.UPDATE_ALL);
    }
}
