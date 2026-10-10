package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaDoughSupport;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModItems;

/**
 * Farmer's Delight wheat dough placed in the world.
 * Hand-stretching advances four persistent blockstate stages.
 * The rolling pin stretches to the final stage in one interaction;
 * toppings are reserved for a later milestone.
 */
public final class PizzaDoughBlock extends Block {
    private static final MapCodec<PizzaDoughBlock> CODEC = simpleCodec(PizzaDoughBlock::new);

    // Four interactions turn stage 0 (round dough) into stage 4 (flat base).
    // This is an initial test value; final hand-stretching balance is open.
    public static final int MAX_STAGE = 4;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, MAX_STAGE);

    // Shapes match the simple test models and remain selectable after each click.
    private static final VoxelShape[] SHAPES = {
        box(4, 0, 4, 12, 6, 12),
        box(3, 0, 3, 13, 5, 13),
        box(2, 0, 2, 14, 4, 14),
        box(1, 0, 1, 15, 3, 15),
        box(1, 0, 1, 15, 2, 15)
    };

    public PizzaDoughBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(STAGE)];
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
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!stack.is(ModItems.ROLLING_PIN) || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        // The rolling pin does not lose durability and is not consumed.
        // A fully flattened dough cannot be stretched again.
        if (state.getValue(STAGE) == MAX_STAGE) {
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (level instanceof ServerLevel server) {
            BlockState flattened = state.setValue(STAGE, MAX_STAGE);
            if (!server.setBlock(pos, flattened, Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            server.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.6F, 0.9F);
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
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ModItems.WHEAT_DOUGH.get());
    }
}
