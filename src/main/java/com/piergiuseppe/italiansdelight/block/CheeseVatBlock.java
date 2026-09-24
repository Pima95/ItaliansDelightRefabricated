package com.piergiuseppe.italiansdelight.block;

import com.piergiuseppe.italiansdelight.block.entity.CheeseVatBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;

public class CheeseVatBlock extends BaseEntityBlock {

    public CheeseVatBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CheeseVatBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheeseVatBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (!level.isClientSide() &&
            level.getBlockEntity(pos) instanceof CheeseVatBlockEntity cheeseVat) {
            player.openMenu(cheeseVat);
        }

        return InteractionResult.SUCCESS;
    }
}