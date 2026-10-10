package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaDoughSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModItems;

/** World-only dough placeholder. Stretching and toppings come in later steps. */
public final class PizzaDoughBlock extends Block {
    private static final MapCodec<PizzaDoughBlock> CODEC = simpleCodec(PizzaDoughBlock::new);
    public static final BooleanProperty LOWERED = BooleanProperty.create("lowered");

    private static final VoxelShape NORMAL_SHAPE = box(4, 0, 4, 12, 6, 12);
    private static final VoxelShape LOWERED_SHAPE = box(4, -2, 4, 12, 4, 12);

    public PizzaDoughBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LOWERED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOWERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(LOWERED) ? LOWERED_SHAPE : NORMAL_SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos supportPos = pos.below();
        return PizzaDoughSupport.canSupport(level.getBlockState(supportPos), level, supportPos);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN) {
            if (!canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
            // When the support changes between soul sand and an ordinary block,
            // keep the visual height synchronized without dropping the dough.
            return state.setValue(LOWERED, PizzaDoughSupport.needsLowerModel(neighborState));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ModItems.WHEAT_DOUGH.get());
    }
}
