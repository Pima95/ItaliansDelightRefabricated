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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


/**
 * Farmer's Delight wheat dough placed in the world.
 * Hand-stretching advances four persistent blockstate stages.
 * Hand-stretching and rolling-pin use are both handled by PizzaDoughPlacement
 * to ensure the same click path is used on client and server.
 */
public final class PizzaDoughBlock extends Block {
    private static final MapCodec<PizzaDoughBlock> CODEC = simpleCodec(PizzaDoughBlock::new);

    // Four interactions turn stage 0 (round dough) into stage 4 (flat base).
    // This is an initial test value; final hand-stretching balance is open.
    public static final int MAX_STAGE = 4;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, MAX_STAGE);
    // First topping proof of concept; arbitrary toppings will use a dedicated
    // data representation later, rather than adding one property per ingredient.
    public static final BooleanProperty MOZZARELLA = BooleanProperty.create("mozzarella");

    // Selection shapes mirror the exact recessed models, including the
    // raised edges. Unlike a single box, they expose the recessed center.
    private static final VoxelShape[] SHAPES = {
        box(4, 0, 4, 12, 4, 12),
        recessedShape(3, 13, 3, 4, 1),
        recessedShape(2, 14, 2, 4, 1),
        recessedShape(1, 15, 2, 3, 1),
        recessedShape(1, 15, 1, 3, 2)
    };

    private static VoxelShape recessedShape(
            int min, int max, int centerHeight, int edgeHeight, int edgeWidth
    ) {
        return net.minecraft.world.phys.shapes.Shapes.or(
                box(min, 0, min, max, centerHeight, max),
                box(min, centerHeight, min, max, edgeHeight, min + edgeWidth),
                box(min, centerHeight, max - edgeWidth, max, edgeHeight, max),
                box(min, centerHeight, min + edgeWidth,
                        min + edgeWidth, edgeHeight, max - edgeWidth),
                box(max - edgeWidth, centerHeight, min + edgeWidth,
                        max, edgeHeight, max - edgeWidth)
        ).optimize();
    }

    public PizzaDoughBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0).setValue(MOZZARELLA, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE, MOZZARELLA);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(STAGE)];
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
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get());
    }
}
