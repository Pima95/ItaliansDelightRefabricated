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
 * Stato finale del calderone dopo l'evaporazione completa dell'acqua.
 * Il blocco mantiene visivamente il residuo di sale; con il tasto destro il
 * giocatore raccoglie una quantità casuale di sale e torna al calderone vuoto.
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
     * Pick Block restituisce il BlockItem nascosto di questo stato già pronto.
     * Non serve una BlockEntity con NBT perché la presenza del sale è già
     * rappresentata dall'id del blocco italiansdelight:salt_cauldron.
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

        // Gli estremi sono inclusivi: ogni raccolta produce da 3 a 7 unità.
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
