package com.piergiuseppe.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import com.piergiuseppe.italiansdelight.common.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Internal ready state of a cauldron after all water has evaporated.
 * Right-clicking collects four salt and restores the vanilla empty cauldron.
 */
public final class SaltCauldronBlock extends Block {

    public static final MapCodec<SaltCauldronBlock> CODEC =
        simpleCodec(SaltCauldronBlock::new);

    private static final VoxelShape SHAPE =
        Blocks.CAULDRON
            .defaultBlockState()
            .getShape(
                EmptyBlockGetter.INSTANCE,
                BlockPos.ZERO
            );

    public SaltCauldronBlock(
        Properties properties
    ) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        return collectSalt(
            level,
            pos,
            player
        );
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack itemStack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        return collectSalt(
            level,
            pos,
            player
        );
    }

    private InteractionResult collectSalt(
        Level level,
        BlockPos pos,
        Player player
    ) {
        if (!level.isClientSide()) {
            ItemStack salt =
                new ItemStack(
                    ModItems.SALT,
                    4
                );

            if (!player.addItem(salt)) {
                player.drop(
                    salt,
                    false
                );
            }

            level.setBlockAndUpdate(
                pos,
                Blocks.CAULDRON
                    .defaultBlockState()
            );
        }

        return InteractionResult.SUCCESS;
    }
}
