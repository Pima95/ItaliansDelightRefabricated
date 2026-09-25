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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Final cauldron state after all water has evaporated.
 * The block visually keeps the salt residue; right-clicking lets the player
 * collect a random amount of salt and restores an empty cauldron.
 */
public final class SaltCauldronBlock extends Block {

    public static final MapCodec<SaltCauldronBlock> CODEC =
        simpleCodec(SaltCauldronBlock::new);

    private static final int MIN_SALT_DROP = 3;
    private static final int MAX_SALT_DROP = 7;

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

    /**
     * Pick Block returns the hidden BlockItem for this ready state.
     * No BlockEntity with NBT is required because the presence of salt is
     * already represented by the italiansdelight:salt_cauldron block id.
     */
    @Override
    protected ItemStack getCloneItemStack(
        LevelReader level,
        BlockPos pos,
        BlockState state,
        boolean includeData
    ) {
        return new ItemStack(this);
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
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Change the block first. If another mod prevents the transition, do
        // not hand out salt and therefore do not risk duplicating the result.
        if (
            !level.setBlockAndUpdate(
                pos,
                Blocks.CAULDRON
                    .defaultBlockState()
            )
        ) {
            return InteractionResult.FAIL;
        }

        // Bounds are inclusive: each collection yields between 3 and 7 units.
        int saltCount =
            level.getRandom()
                .nextIntBetweenInclusive(
                    MIN_SALT_DROP,
                    MAX_SALT_DROP
                );

        ItemStack salt =
            new ItemStack(
                ModItems.SALT,
                saltCount
            );

        if (!player.addItem(salt)) {
            player.drop(
                salt,
                false
            );
        }

        return InteractionResult.SUCCESS;
    }
}
