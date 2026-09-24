package com.piergiuseppe.italiansdelight.block;

import com.piergiuseppe.italiansdelight.block.entity.CheeseVatBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
}