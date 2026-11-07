package dev.italiansdelight.common.block;

import java.util.List;
import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import net.minecraft.core.BlockPos;
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
public final class BarrelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(3, 1, 3, 13, 2, 13),
        Block.box(3, 0, 2, 13, 16, 3),
        Block.box(3, 0, 13, 13, 16, 14),
        Block.box(2, 0, 3, 3, 16, 13),
        Block.box(13, 0, 3, 14, 16, 13)
    ).optimize();

    public BarrelBlock(Properties props) { super(props); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(BarrelBlock::new); }
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

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BarrelFluidContainers.Portion fluid = BarrelFluidContainers.filledContainer(held);
        boolean emptyBucket = held.is(Items.BUCKET), emptyBottle = held.is(Items.GLASS_BOTTLE);
        // Rustic barrels reject hot lava. Do not pass to BucketItem: that
        // would spill an otherwise rejected lava bucket beside the wooden tank.
        if (held.is(Items.LAVA_BUCKET)) return InteractionResult.SUCCESS;
        if (fluid == null && !emptyBucket && !emptyBottle) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
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
        if (level.isRainingAt(pos.above()) && level.getBlockEntity(pos) instanceof BarrelBlockEntity tank)
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
