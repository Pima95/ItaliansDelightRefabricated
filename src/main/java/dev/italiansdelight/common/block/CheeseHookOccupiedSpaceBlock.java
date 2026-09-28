package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Invisible technical block reserving the block volume occupied by a cheese
 * hanging below a Cheese Hook.
 *
 * It has no collision or rendering, but it is deliberately non-replaceable so
 * another block or fluid cannot be placed through the hanging cheese.
 */
public final class CheeseHookOccupiedSpaceBlock extends Block {

    public static final MapCodec<CheeseHookOccupiedSpaceBlock> CODEC =
        simpleCodec(
            CheeseHookOccupiedSpaceBlock::new
        );

    public CheeseHookOccupiedSpaceBlock(
        Properties properties
    ) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(
        BlockState state
    ) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    protected boolean canBeReplaced(
        BlockState state,
        BlockPlaceContext context
    ) {
        return false;
    }

    @Override
    protected boolean canBeReplaced(
        BlockState state,
        Fluid fluid
    ) {
        return false;
    }

    @Override
    protected boolean canSurvive(
        BlockState state,
        LevelReader level,
        BlockPos pos
    ) {
        BlockState hookState =
            level.getBlockState(
                pos.above()
            );

        return hookState.getBlock()
                == ModBlocks.CHEESE_HOOK
            && !hookState.getValue(
                    CheeseHookBlock.CHEESE
                )
                .isEmpty();
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random
    ) {
        if (
            directionToNeighbour == Direction.UP
            && !canSurvive(
                state,
                level,
                pos
            )
        ) {
            return Blocks.AIR
                .defaultBlockState();
        }

        return super.updateShape(
            state,
            level,
            ticks,
            pos,
            directionToNeighbour,
            neighbourPos,
            neighbourState,
            random
        );
    }
}
