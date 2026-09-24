package com.piergiuseppe.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;

import com.piergiuseppe.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import com.piergiuseppe.italiansdelight.common.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CheeseVatBlock extends BaseEntityBlock {

    public CheeseVatBlock(
        Properties properties
    ) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(
            CheeseVatBlock::new
        );
    }

    @Override
    public BlockEntity newBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        return new CheeseVatBlockEntity(
            pos,
            state
        );
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (
            !level.isClientSide()
            && level.getBlockEntity(pos)
                instanceof CheeseVatBlockEntity cheeseVat
        ) {
            player.openMenu(
                cheeseVat
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity>
    BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (
            level instanceof ServerLevel serverLevel
        ) {
            return createTickerHelper(
                type,
                ModBlockEntities.CHEESE_VAT,
                (
                    commonLevel,
                    pos,
                    blockState,
                    cheeseVat
                ) ->
                    CheeseVatBlockEntity.serverTick(
                        serverLevel,
                        pos,
                        blockState,
                        cheeseVat
                    )
            );
        }

        return null;
    }
}