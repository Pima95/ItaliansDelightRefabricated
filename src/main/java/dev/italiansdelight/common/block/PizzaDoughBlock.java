package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaDoughSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModItems;

/**
 * Farmer's Delight wheat dough placed in the world.
 * Hand-stretching advances four persistent blockstate stages.
 * The rolling pin and toppings are reserved for later milestones.
 */
public final class PizzaDoughBlock extends Block {
    private static final MapCodec<PizzaDoughBlock> CODEC = simpleCodec(PizzaDoughBlock::new);
    public static final BooleanProperty LOWERED = BooleanProperty.create("lowered");

    // Four interactions turn stage 0 (round dough) into stage 4 (flat base).
    // This is an initial test value; final hand-stretching balance is open.
    public static final int MAX_STAGE = 4;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, MAX_STAGE);

    // Shapes match the simple test models and remain selectable after each click.
    private static final VoxelShape[] NORMAL_SHAPES = {
        box(4, 0, 4, 12, 6, 12),
        box(3, 0, 3, 13, 5, 13),
        box(2, 0, 2, 14, 4, 14),
        box(1, 0, 1, 15, 3, 15),
        box(1, 0, 1, 15, 2, 15)
    };
    private static final VoxelShape[] LOWERED_SHAPES = {
        box(4, -2, 4, 12, 4, 12),
        box(3, -2, 3, 13, 3, 13),
        box(2, -2, 2, 14, 2, 14),
        box(1, -2, 1, 15, 1, 15),
        box(1, -2, 1, 15, 0, 15)
    };

    public PizzaDoughBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(LOWERED, false)
                .setValue(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOWERED, STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int stage = state.getValue(STAGE);
        return state.getValue(LOWERED) ? LOWERED_SHAPES[stage] : NORMAL_SHAPES[stage];
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit
    ) {
        // Sneaking is reserved for future pickup/undo behavior.
        // The finished base does not accept additional stretching clicks.
        if (player.isShiftKeyDown() || state.getValue(STAGE) >= MAX_STAGE) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level instanceof ServerLevel server) {
            BlockState next = state.setValue(STAGE, state.getValue(STAGE) + 1);
            if (!server.setBlock(pos, next, Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            server.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.45F, 0.95F);
        }
        return InteractionResult.SUCCESS;
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
