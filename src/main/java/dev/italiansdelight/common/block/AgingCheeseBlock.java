package dev.italiansdelight.common.block;

import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.block.entity.AgingCheeseBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Internal world representation of one cheese wheel aging on a flat surface.
 *
 * It has no BlockItem: players place it by using a compatible fresh cheese
 * item on a sturdy top face.
 */
public final class AgingCheeseBlock
    extends BaseEntityBlock {

    public static final EnumProperty<AgingCheeseType>
        CHEESE =
            EnumProperty.create(
                "cheese",
                AgingCheeseType.class
            );

    public static final BooleanProperty MATURE =
        BooleanProperty.create(
            "mature"
        );

    private static final VoxelShape SHAPE =
        Block.box(
            2,
            0,
            2,
            14,
            4,
            14
        );

    public AgingCheeseBlock(
        Properties properties
    ) {
        super(properties);

        registerDefaultState(
            stateDefinition.any()
                .setValue(
                    CHEESE,
                    AgingCheeseType.PARMIGIANO_REGGIANO
                )
                .setValue(
                    MATURE,
                    false
                )
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(
            AgingCheeseBlock::new
        );
    }

    @Override
    public BlockEntity newBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        return new AgingCheeseBlockEntity(
            pos,
            state
        );
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<
            Block,
            BlockState
        > builder
    ) {
        super.createBlockStateDefinition(
            builder
        );

        builder.add(
            CHEESE,
            MATURE
        );
    }

    @Override
    protected VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(
        BlockState state,
        LevelReader level,
        BlockPos pos
    ) {
        BlockPos supportPos =
            pos.below();

        return level.getBlockState(
                supportPos
            )
            .isFaceSturdy(
                level,
                supportPos,
                Direction.UP
            );
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
            directionToNeighbour
                == Direction.DOWN
            && !canSurvive(
                state,
                level,
                pos
            )
        ) {
            // Vanilla's normal block replacement/drop path handles the actual
            // removal. getDrops() below intentionally returns an item with no
            // aging progress.
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

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level instanceof ServerLevel serverLevel
            && level.getBlockEntity(pos)
                instanceof AgingCheeseBlockEntity blockEntity
        ) {
            ItemStack pickup =
                blockEntity.getPickupStack(
                    serverLevel,
                    state
                );

            if (!player.addItem(pickup)) {
                player.drop(
                    pickup,
                    false
                );
            }

            // No drop from block removal: the correct fresh/final item was
            // handed out above. Any incomplete progress disappears here.
            level.removeBlock(
                pos,
                false
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(
        BlockState state,
        LootParams.Builder params
    ) {
        ServerLevel serverLevel =
            params.getLevel();

        if (
            params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
            ) instanceof AgingCheeseBlockEntity blockEntity
        ) {
            return List.of(
                blockEntity.getPickupStack(
                    serverLevel,
                    state
                )
            );
        }

        AgingCheeseType type =
            state.getValue(
                CHEESE
            );

        return List.of(
            state.getValue(MATURE)
                ? type.matureStack()
                : type.freshStack()
        );
    }

    @Override
    protected ItemStack getCloneItemStack(
        LevelReader level,
        BlockPos pos,
        BlockState state,
        boolean includeData
    ) {
        AgingCheeseType type =
            state.getValue(
                CHEESE
            );

        return state.getValue(MATURE)
            ? type.matureStack()
            : type.freshStack();
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
                ModBlockEntities.AGING_CHEESE,
                (
                    commonLevel,
                    pos,
                    blockState,
                    blockEntity
                ) ->
                    AgingCheeseBlockEntity.serverTick(
                        serverLevel,
                        pos,
                        blockState,
                        blockEntity
                    )
            );
        }

        return null;
    }
}
