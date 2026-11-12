package dev.italiansdelight.common.block;

import dev.italiansdelight.common.block.entity.BrewingBarrelBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Rustic brewing barrel interactions adapted for Fabric 26.3.
 * In this initial stage, fluid input/output uses bottles/buckets directly;
 * output wine quality is stored on each individual wine bottle.
 */
public final class BrewingBarrelBlock extends BaseEntityBlock {
    public BrewingBarrelBlock(Properties properties) { super(properties); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrewingBarrelBlockEntity(pos, state);
    }

    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(type, ModBlockEntities.BREWING_BARREL,
                    (world, pos, current, barrel) ->
                            BrewingBarrelBlockEntity.serverTick(server, pos, current, barrel));
        }
        return null;
    }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state,
            Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean juiceBottle = held.is(ModItems.GRAPE_JUICE_BOTTLE);
        boolean juiceBucket = held.is(ModItems.GRAPE_JUICE_BUCKET);
        boolean wineBottle = held.is(ModItems.WINE_BOTTLE);
        boolean emptyBottle = held.is(Items.GLASS_BOTTLE);
        if (!juiceBottle && !juiceBucket && !wineBottle && !emptyBottle)
            return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BrewingBarrelBlockEntity barrel))
            return InteractionResult.FAIL;

        if (juiceBottle || juiceBucket) {
            int amount = juiceBucket ? 1000 : 250;
            if (barrel.insertJuice(amount)) {
                ItemStack empty = new ItemStack(juiceBucket ? Items.BUCKET : Items.GLASS_BOTTLE);
                player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, empty));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }
        if (wineBottle) {
            // A single bottle primes the yeast culture; it is held separately
            // from the output tank, just like Rustic's auxiliary tank.
            int quality = held.getOrDefault(ModDataComponents.WINE_QUALITY, 0);
            if (barrel.insertCulture(quality)) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(held, player,
                        new ItemStack(Items.GLASS_BOTTLE)));
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.SUCCESS;
        }

        int quality = player.isShiftKeyDown()
                ? barrel.takeCulture() : barrel.extractWine(250);
        if (quality >= 0) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player,
                    BrewingBarrelBlockEntity.wineBottle(quality)));
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1, 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server &&
                server.getBlockEntity(pos) instanceof BrewingBarrelBlockEntity barrel) {
            player.displayClientMessage(Component.translatable(
                    "message.italiansdelight.brewing_barrel.status",
                    barrel.getJuiceAmount(), barrel.getWineAmount(),
                    barrel.getProgress(), BrewingBarrelBlockEntity.BREW_TIME,
                    barrel.getWineQuality()), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
                instanceof BrewingBarrelBlockEntity barrel)
            for (ItemStack stack : drops)
                if (stack.is(asItem()))
                    barrel.preserveContents(stack, params.getLevel().registryAccess());
        return drops;
    }
}
