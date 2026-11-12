package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.refabricated.block.ReplaceOnRemoval;

/**
 * Rustic chili stake crop. Reuses Farmer's Delight rope to grow at most two
 * upper hanging segments, each separately harvestable (1:42 ghost pepper).
 */
public class ChiliCropBlock extends CropBlock implements ReplaceOnRemoval {
    public ChiliCropBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CropStakeBlock.ROPE_TIED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CropStakeBlock.ROPE_TIED);
    }

    @Override protected ItemLike getBaseSeedId() { return ModBlocks.CHILI_PEPPER_SEEDS; }

    protected boolean shouldRestoreRope() { return false; }

    // The wooden stake remains physically solid even after the crop is planted.
    // A rope-borne chili segment still behaves like Farmer's Delight rope.
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
            BlockPos pos, CollisionContext context) {
        return shouldRestoreRope() ? net.minecraft.world.phys.shapes.Shapes.empty() : CropStakeBlock.POST;
    }

    // The root needs farmland; upper chili segments can instead root in a
    // cultivated stake immediately below, just like climbing FDR tomatoes.
    @Override public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.is(ModBlocks.CHILI_CROP) || below.is(ModBlocks.CHILI_ON_ROPE)) {
            return level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
        }
        return super.canSurvive(state, level, pos);
    }

    private boolean canClimb(LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(ModBlocks.CROP_STAKE)
            || (above.is(vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get())
                && !above.getValue(net.minecraft.world.level.block.CrossCollisionBlock.WATERLOGGED));
    }

    private int vineHeight(LevelReader level, BlockPos pos) {
        int n=1;
        while (n < 3) {
            BlockState below=level.getBlockState(pos.below(n));
            if (!below.is(ModBlocks.CHILI_CROP) && !below.is(ModBlocks.CHILI_ON_ROPE)) break;
            ++n;
        }
        return n;
    }

    public boolean tryClimb(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.is(this) || getAge(state) < getMaxAge()
                || !canClimb(level,pos) || vineHeight(level,pos)>=3) return false;
        BlockState support = level.getBlockState(pos.above());
        BlockState next = support.is(ModBlocks.CROP_STAKE)
            ? ModBlocks.CHILI_CROP.defaultBlockState()
                .setValue(CropStakeBlock.ROPE_TIED, support.getValue(CropStakeBlock.ROPE_TIED))
            : ModBlocks.CHILI_ON_ROPE.defaultBlockState();
        return level.setBlock(pos.above(), next, Block.UPDATE_ALL);
    }

    // Vanilla CropBlock stops ticking at maturity; rope-driven climbing must
    // remain active if the player adds the support AFTER the plant ripens.
    @Override public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state,level,pos,random);
        BlockState current=level.getBlockState(pos);
        if (current.is(this) && random.nextInt(3)==0) tryClimb(level,pos,current);
    }

    @Override public boolean isValidBonemealTarget(LevelReader level, BlockPos pos,
            BlockState state, BonemealSource source) {
        return !isMaxAge(state) || (canClimb(level,pos) && vineHeight(level,pos)<3);
    }

    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        if (!isMaxAge(state)) super.performBonemeal(level,random,pos,state,source);
        BlockState current=level.getBlockState(pos);
        if (current.is(this)) tryClimb(level,pos,current);
    }

    // Vanilla CropBlock can remove an unsupported upper segment during a
    // neighbor update before the rope-restoration hook can run. Schedule a
    // server removal instead, keeping the reusable support in place.
    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction changedSide,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (!state.canSurvive(level,pos)) {
            ticks.scheduleTick(pos,this,1);
            return state;
        }
        return super.updateShape(state,level,ticks,pos,changedSide,neighborPos,neighbor,random)
            .setValue(CropStakeBlock.ROPE_TIED, CropStakeBlock.isRopeTied(level, pos));
    }

    @Override public void tick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            // Dropping just the crop and restoring the reusable stake (or
            // original vertical rope) avoids destroyBlock's unwanted loss of
            // the support when farmland is trampled or reverted to dirt.
            Block.dropResources(state, level, pos);
            if (shouldRestoreRope()) {
                level.setBlock(pos,
                    vectorwing.farmersdelight.common.block.RopeBlock.getStateWithConnections(
                        vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get().defaultBlockState(),
                        level, pos, Direction.UP), Block.UPDATE_ALL);
            } else {
                level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState()
                    .setValue(CropStakeBlock.ROPE_TIED,
                        CropStakeBlock.isRopeTied(level, pos)), Block.UPDATE_ALL);
            }
        }
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (!isMaxAge(state) || player.isSpectator()) return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            BlockState current=server.getBlockState(pos);
            if (!current.is(this) || !isMaxAge(current)) return InteractionResult.PASS;
            server.setBlock(pos,current.setValue(AGE,6),Block.UPDATE_CLIENTS);
            boolean rare=server.getRandom().nextInt(42)==0;
            Block.popResource(server,pos,new ItemStack(rare
                ? ModItems.GHOST_PEPPER : ModItems.CHILI_PEPPER,
                rare ? 1 : 1 + server.getRandom().nextInt(2)));
            server.playSound(null,pos,SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS,0.8F,1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override public boolean onRemove(BlockState state, BlockState next,
            LevelAccessor level, BlockPos pos, int flags) {
        if (!next.isAir() || state.is(next.getBlock())) return false;
        if (shouldRestoreRope()) {
            BlockState rope=vectorwing.farmersdelight.common.block.RopeBlock
                .getStateWithConnections(
                    vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get().defaultBlockState(),
                    level,pos,net.minecraft.core.Direction.UP);
            return level.setBlock(pos,rope,3);
        }
        return level.setBlock(pos, ModBlocks.CROP_STAKE.defaultBlockState()
            .setValue(CropStakeBlock.ROPE_TIED, CropStakeBlock.isRopeTied(level,pos)), 3);
    }
}
