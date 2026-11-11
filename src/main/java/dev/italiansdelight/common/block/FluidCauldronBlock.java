package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.FluidCauldronBlockEntity;
import dev.italiansdelight.common.fluid.FluidTransferBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Generic filled CAULDRON state, like vanilla water_cauldron / lava_cauldron.
 * Empty cauldrons are still Blocks.CAULDRON; no overwrite/mixin of vanilla.
 * All fluids share this ONE technical block and its vanilla iron model.
 */
public final class FluidCauldronBlock extends BaseEntityBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 1, 4);
    private static final VoxelShape SHAPE =
        Blocks.CAULDRON.defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

    public FluidCauldronBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 4));
    }
@Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidCauldronBlockEntity(pos, state);
    }
    @Override public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(LEVEL);
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) { return SHAPE; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                                 CollisionContext context) { return SHAPE; }

    // Middle-click on the filled state must return the NORMAL vanilla cauldron
    // item; this technical block has no BlockItem and must never return air.
    @Override protected ItemStack getCloneItemStack(
            LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state, Level world,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) return InteractionResult.PASS;
        if (FluidTransferBridge.itemStorage(player, hand) != null) {
            if (world.isClientSide()) return InteractionResult.SUCCESS;
            if (!(world.getBlockEntity(pos) instanceof FluidCauldronBlockEntity storage))
                return InteractionResult.FAIL;
            if (FluidTransferBridge.transfer(player, hand, storage.getFluidStorage()))
                return InteractionResult.SUCCESS;
            if (!FluidTransferBridge.heldSource(player, hand).isBlank()
                || storage.getFluidVariant().hasComponents())
                return InteractionResult.SUCCESS;
            // Empty vanilla container + virtual fluid uses the legacy adapter.
        }
        BarrelFluidContainers.Portion fluid = BarrelFluidContainers.filledContainer(held);
        boolean emptyBucket = held.is(Items.BUCKET);
        boolean emptyBottle = held.is(Items.GLASS_BOTTLE);
        if (fluid == null && !emptyBucket && !emptyBottle)
            return InteractionResult.PASS;
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof FluidCauldronBlockEntity tank))
            return InteractionResult.FAIL;
        boolean deposit = fluid != null;
        int delta = deposit ? fluid.amount() :
            (emptyBucket ? 1000 : 250);
        ItemStack result = deposit ? new ItemStack(fluid.empty()) :
            BarrelFluidContainers.makeFilled(tank.getFluidId(), delta);
        if (result.isEmpty()) return InteractionResult.SUCCESS;
        if (deposit ? !tank.insert(fluid.id(), delta) : !tank.extract(delta))
            return InteractionResult.SUCCESS;
        // Maintain the native cauldron shell with four precisely measured levels.
        int volume = tank.getAmount();
        int levels = volume / 250;
        if (levels == 0) {
            world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
        } else if (levels != state.getValue(LEVEL)) {
            world.setBlockAndUpdate(pos, state.setValue(LEVEL, levels));
        }
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, result));
        world.playSound(null, pos, deposit
            ? (delta == 1000 ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY)
            : (delta == 1000 ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_FILL),
            SoundSource.BLOCKS, 1F, 1F);
        return InteractionResult.SUCCESS;
    }
}
