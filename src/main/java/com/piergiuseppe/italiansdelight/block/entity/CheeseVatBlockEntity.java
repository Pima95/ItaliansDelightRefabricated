package com.piergiuseppe.italiansdelight.block.entity;

import com.piergiuseppe.italiansdelight.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CheeseVatBlockEntity extends BlockEntity {

    public CheeseVatBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEESE_VAT, pos, state);
    }
}