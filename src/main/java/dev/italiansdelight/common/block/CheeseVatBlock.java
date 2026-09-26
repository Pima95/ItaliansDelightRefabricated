package dev.italiansdelight.common.block;

import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cheese Vat block. Handles block state, facing, player interactions, and the link to its BlockEntity.
 */

public class CheeseVatBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING =
        BlockStateProperties.HORIZONTAL_FACING;

    // Vanilla cauldron, filled to one pixel below the rim, with wooden side braces.
    private static final VoxelShape SHAPE = Shapes.or(
        Blocks.CAULDRON.defaultBlockState().getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO),
        Block.box(2, 4, 2, 14, 15, 14),
        Block.box(1, 8, -1, 15, 12, 0),
        Block.box(1, 8, 16, 15, 12, 17),
        Block.box(-1, 8, 1, 0, 12, 15),
        Block.box(16, 8, 1, 17, 12, 15)
    );

    public CheeseVatBlock(
        Properties properties
    ) {
        super(properties);
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
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
    public BlockState getStateForPlacement(
        BlockPlaceContext context
    ) {
        return defaultBlockState().setValue(
            FACING,
            context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
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
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CheeseVatBlockEntity vat) {
            for (ItemStack stack : drops) {
                if (stack.is(asItem())) {
                    vat.preservePendingResult(stack, params.getLevel().registryAccess());
                }
            }
        }
        return drops;
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
