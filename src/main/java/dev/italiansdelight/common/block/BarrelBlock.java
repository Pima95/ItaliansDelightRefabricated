package dev.italiansdelight.common.block;

import java.util.List;
import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

/** Rustic-style six-element Barrel: olive-oil buckets and bottles only. */
public final class BarrelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(3, 1, 3, 13, 2, 13),
        Block.box(3, 0, 2, 13, 16, 3),
        Block.box(3, 0, 13, 13, 16, 14),
        Block.box(3, 14, 3, 13, 15, 13),
        Block.box(2, 0, 3, 3, 16, 13),
        Block.box(13, 0, 3, 14, 16, 13)
    ).optimize();

    public BarrelBlock(Properties properties) { super(properties); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(BarrelBlock::new); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BarrelBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) { return SHAPE; }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) { return SHAPE; }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel) {
            reportAmount(player, barrel);
        }
        return InteractionResult.SUCCESS;
    }

    private static void reportAmount(Player player, BarrelBlockEntity barrel) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                Component.translatable("message.italiansdelight.barrel.amount",
                    barrel.getOliveOilAmount(), BarrelBlockEntity.CAPACITY)));
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        boolean insertBucket = held.is(ModItems.OLIVE_OIL_BUCKET);
        boolean insertBottle = held.is(ModItems.OLIVE_OIL_BOTTLE);
        boolean takeBucket = held.is(Items.BUCKET);
        boolean takeBottle = held.is(Items.GLASS_BOTTLE);
        if (!insertBucket && !insertBottle && !takeBucket && !takeBottle)
            return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel))
            return InteractionResult.PASS;

        boolean deposit = insertBucket || insertBottle;
        int amount = (insertBucket || takeBucket) ? BarrelBlockEntity.BUCKET_AMOUNT
                                                  : BarrelBlockEntity.BOTTLE_AMOUNT;
        if (!(deposit ? barrel.insertOliveOil(amount) : barrel.extractOliveOil(amount))) {
            reportAmount(player, barrel);
            return InteractionResult.SUCCESS;
        }

        if (deposit) {
            int countBefore = held.getCount();
            held.consume(1, player);
            // Never duplicate empty containers in Creative.
            if (held.getCount() < countBefore) {
                ItemStack empty = new ItemStack(insertBucket ? Items.BUCKET : Items.GLASS_BOTTLE);
                if (held.isEmpty()) player.setItemInHand(hand, empty);
                else if (!player.addItem(empty)) player.drop(empty, false);
            }
        } else {
            ItemStack filled = new ItemStack(takeBucket ? ModItems.OLIVE_OIL_BUCKET
                                                       : ModItems.OLIVE_OIL_BOTTLE);
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, filled));
        }
        level.playSound(null, pos, deposit
            ? (insertBucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_EMPTY)
            : (takeBucket ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_FILL),
            SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, deposit ? GameEvent.FLUID_PLACE : GameEvent.FLUID_PICKUP, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BarrelBlockEntity barrel) {
            for (ItemStack stack : drops)
                if (stack.is(asItem())) barrel.preserveContents(stack, params.getLevel().registryAccess());
        }
        return drops;
    }
}
