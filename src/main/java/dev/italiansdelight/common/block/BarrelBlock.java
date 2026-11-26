package dev.italiansdelight.common.block;

import net.minecraft.world.level.LevelAccessor;

import java.util.List;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import dev.italiansdelight.common.fluid.FluidTransferBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Real open Rustic liquid barrel: no lid, one fluid at a time. */
public final class BarrelBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final com.mojang.serialization.MapCodec<BarrelBlock> CODEC = simpleCodec(BarrelBlock::new);
    @Override protected com.mojang.serialization.MapCodec<BarrelBlock> codec() { return CODEC; }

    // In 1.21.1 BaseEntityBlock defaults to INVISIBLE. Keep the JSON model
    // visible alongside any dynamic block-entity renderer.
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(3, 1, 3, 13, 2, 13),
        Block.box(3, 0, 2, 13, 16, 3),
        Block.box(3, 0, 13, 13, 16, 14),
        Block.box(2, 0, 3, 3, 16, 13),
        Block.box(13, 0, 3, 14, 16, 13)
    ).optimize();

    public BarrelBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED,
            context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
    }

    @Override public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BarrelBlockEntity(pos, state);
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                         CollisionContext ctx) { return SHAPE; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                                  CollisionContext ctx) { return SHAPE; }
    // Full/mixed barrels silently reject insertion; no debug action-bar messages.
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.SUCCESS;
    }

    @Override protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = interactWithItem(held, state, level, pos, player, hand, hit);
        if (result == InteractionResult.PASS) return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (result == InteractionResult.FAIL) return net.minecraft.world.ItemInteractionResult.FAIL;
        return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult interactWithItem(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(WATERLOGGED)) return InteractionResult.SUCCESS;
        // Prefer Fabric transactions for registered fluids and component-aware
        // containers, but DO NOT shadow old virtual milk/whey/rennet liquids
        // when the player uses an empty bucket or bottle.
        if (FluidTransferBridge.itemStorage(player, hand) != null) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (!(level.getBlockEntity(pos) instanceof BarrelBlockEntity storage))
                return InteractionResult.FAIL;
            if (FluidTransferBridge.transfer(player, hand, storage.getFluidStorage()))
                return InteractionResult.SUCCESS;
            // A registered fluid-bearing container must not fall through into
            // the legacy ID-only path, which would lose its component patch.
            if (!FluidTransferBridge.heldSource(player, hand).isEmpty()
                || !storage.getFluidVariant().isComponentsPatchEmpty())
                return InteractionResult.SUCCESS;
            // Empty bucket/bottle + legacy virtual fluid: let the original
            // 250/1000 mB item-specific logic fill that container.
        }
        BarrelFluidContainers.Portion fluid = BarrelFluidContainers.filledContainer(held);
        boolean emptyBucket = held.is(Items.BUCKET), emptyBottle = held.is(Items.GLASS_BOTTLE);
        // Rustic barrels reject hot lava. Do not pass to BucketItem: that
        // would spill an otherwise rejected lava bucket beside the wooden tank.
        if (held.is(Items.LAVA_BUCKET)) return InteractionResult.SUCCESS;
        if (fluid == null && !emptyBucket && !emptyBottle) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BarrelBlockEntity tank)) return InteractionResult.PASS;
        boolean deposit = fluid != null;
        int volume = deposit ? fluid.amount() :
            (emptyBucket ? BarrelBlockEntity.BUCKET_AMOUNT : BarrelBlockEntity.BOTTLE_AMOUNT);
        ItemStack result = deposit ? new ItemStack(fluid.empty()) :
            BarrelFluidContainers.makeFilled(tank.getFluidId(), volume);
        if (result.isEmpty() || !(deposit ? tank.insert(fluid.id(), volume) : tank.extract(volume))) {
            return InteractionResult.SUCCESS;
        }
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, result));
        level.playSound(null, pos, deposit
            ? (volume == 1000 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY)
            : (volume == 1000 ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_FILL),
            SoundSource.BLOCKS, 1f, 1f);
        level.gameEvent(null, deposit ? GameEvent.FLUID_PLACE : GameEvent.FLUID_PICKUP, pos);
        return InteractionResult.SUCCESS;
    }

    // Rain never mixes into barrels holding oil, milk, lava or any other fluid.
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        if (!state.getValue(WATERLOGGED) && level.isRainingAt(pos.above()) && level.getBlockEntity(pos) instanceof BarrelBlockEntity tank)
            tank.insert("minecraft:water", BarrelBlockEntity.BOTTLE_AMOUNT);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BarrelBlockEntity tank)
            for (ItemStack stack : drops)
                if (stack.is(asItem())) tank.preserveContents(stack, params.getLevel().registryAccess());
        return drops;
    }
}
