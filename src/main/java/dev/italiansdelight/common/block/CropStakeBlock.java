package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Rustic-style crop support. The STAKE is always the actual world block and
 * never gets replaced by a crop. Crop kind and crop age are independent,
 * persisted properties; the crop geometry is a separate multipart model.
 *
 * This keeps the wooden support solid, even after picking, mining or
 * losing the crop, without destroy-and-replace flicker or item duplication.
 */
public final class CropStakeBlock extends IronBarsBlock implements BonemealableBlock {
    public static final BooleanProperty ROPE_TIED = BooleanProperty.create("rope_tied");
    public static final IntegerProperty CROP = IntegerProperty.create("crop", 0, 2);
    public static final IntegerProperty CROP_AGE = IntegerProperty.create("crop_age", 0, 7);
    public static final int EMPTY = 0, CHILI = 1, TOMATO = 2;

    public static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape PLANT = Shapes.or(
        Block.box(2, 0, 7, 14, 12, 9), Block.box(7, 0, 2, 9, 12, 14));

    public CropStakeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(ROPE_TIED, false).setValue(CROP, EMPTY).setValue(CROP_AGE, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROPE_TIED, CROP, CROP_AGE);
    }

    public static boolean hasCrop(BlockState state, int kind) {
        return state.is(ModBlocks.CROP_STAKE) && state.getValue(CROP) == kind;
    }

    public static boolean isPost(BlockState state) {
        return state.is(ModBlocks.CROP_STAKE)
            || state.is(ModBlocks.CHILI_CROP) && state.getValue(ChiliCropBlock.STAKED)
            || state.is(ModBlocks.STAKED_TOMATOES);
    }

    public static boolean isRopeTied(LevelReader level, BlockPos pos) {
        for (Direction direction : new Direction[] {
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (neighbor.is(ModBlocks.HORIZONTAL_ROPE)
                    && neighbor.getValue(HorizontalRopeBlock.AXIS) == direction.getAxis())
                return true;
            if (neighbor.is(ModBlocks.GRAPE_LEAVES)
                    && neighbor.getValue(GrapeLeavesBlock.AXIS) == direction.getAxis())
                return true;
        }
        return false;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return state == null ? null : state
            .setValue(ROPE_TIED, isRopeTied(ctx.getLevel(), ctx.getClickedPos()));
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        if (side == Direction.DOWN && state.getValue(CROP) != EMPTY)
            ticks.scheduleTick(pos, this, 1);
        return super.updateShape(state, level, ticks, pos, side, neighborPos, neighbor, random)
            .setValue(ROPE_TIED, isRopeTied(level, pos));
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return state.getValue(CROP) == EMPTY ? POST : Shapes.or(POST, PLANT);
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
            BlockPos pos, CollisionContext context) {
        return POST;
    }

    @Override protected InteractionResult useItemOn(ItemStack held, BlockState state,
            Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return StakeCropPlanting.tryPlant(player, level, hand, pos);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level,
            BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isSpectator() || state.getValue(CROP) == EMPTY) return InteractionResult.PASS;
        int kind = state.getValue(CROP);
        int age = state.getValue(CROP_AGE);
        if (age < maxAge(kind)) return InteractionResult.PASS;
        if (level instanceof ServerLevel server) {
            if (kind == CHILI) {
                boolean ghost = server.getRandom().nextInt(42) == 0;
                Block.popResource(server, pos, new ItemStack(ghost
                    ? dev.italiansdelight.common.registry.ModItems.GHOST_PEPPER
                    : dev.italiansdelight.common.registry.ModItems.CHILI_PEPPER,
                    ghost ? 1 : 1 + server.getRandom().nextInt(2)));
            } else {
                Block.popResource(server, pos, new ItemStack(
                    vectorwing.farmersdelight.common.registry.ModItems.TOMATO.get(),
                    1 + server.getRandom().nextInt(2)));
                if (server.getRandom().nextFloat() < 0.05F)
                    Block.popResource(server, pos, new ItemStack(
                        vectorwing.farmersdelight.common.registry.ModItems.ROTTEN_TOMATO.get()));
            }
            server.setBlock(pos, state.setValue(CROP_AGE,
                kind == TOMATO ? 0 : 6), Block.UPDATE_CLIENTS);
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 0.8F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    public static int maxAge(int kind) {
        return kind == TOMATO ? 3 : 7;
    }

    public static BlockState clearCrop(BlockState state) {
        return state.setValue(CROP, EMPTY).setValue(CROP_AGE, 0);
    }

    /** Break the crop, not the support, when attacking a planted stake. */
    public static boolean removeCrop(ServerLevel level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.CROP_STAKE) || state.getValue(CROP) == EMPTY) return false;
        if (player != null && !level.mayInteract(player, pos)) return false;
        if (player == null || !player.getAbilities().instabuild) {
            if (state.getValue(CROP) == TOMATO) {
                Block.popResource(level, pos, new ItemStack(
                    vectorwing.farmersdelight.common.registry.ModItems.TOMATO_SEEDS.get()));
            } else {
                Block.popResource(level, pos, new ItemStack(ModBlocks.CHILI_PEPPER_SEEDS));
            }
        }
        return level.setBlock(pos, clearCrop(state), Block.UPDATE_ALL);
    }

    /** Reusable support is independent of its cultivated soil. */
    @Override public void tick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        if (state.getValue(CROP) != EMPTY && !canCropLive(level, pos, state.getValue(CROP)))
            removeCrop(level, pos, null);
    }

    private static boolean canCropLive(LevelReader level, BlockPos pos, int kind) {
        BlockState below = level.getBlockState(pos.below());
        if (StakeSoilTilling.isFarmland(below)) return true;
        if (hasCrop(below, kind)) return true;
        if (kind == CHILI && (below.is(ModBlocks.CHILI_CROP) || below.is(ModBlocks.CHILI_ON_ROPE)))
            return true;
        if (kind == TOMATO && below.is(ModBlocks.STAKED_TOMATOES)) return true;
        return false;
    }

    private static int vineHeight(LevelReader level, BlockPos pos, int kind) {
        int height = 1;
        while (height < 3) {
            BlockState below = level.getBlockState(pos.below(height));
            boolean matching = hasCrop(below, kind)
                || (kind == CHILI && (below.is(ModBlocks.CHILI_CROP)
                    || below.is(ModBlocks.CHILI_ON_ROPE)))
                || (kind == TOMATO && below.is(ModBlocks.STAKED_TOMATOES));
            if (!matching) break;
            height++;
        }
        return height;
    }

    private static boolean tryClimb(ServerLevel level, BlockPos pos, BlockState state) {
        int kind = state.getValue(CROP);
        if (kind == EMPTY || state.getValue(CROP_AGE) < maxAge(kind)
                || vineHeight(level, pos, kind) >= 3) return false;
        BlockPos abovePos = pos.above();
        BlockState above = level.getBlockState(abovePos);
        if (above.is(ModBlocks.CROP_STAKE) && above.getValue(CROP) == EMPTY) {
            return level.setBlock(abovePos, above.setValue(CROP, kind).setValue(CROP_AGE, 0),
                Block.UPDATE_ALL);
        }
        if (above.is(vectorwing.farmersdelight.common.registry.ModBlocks.ROPE.get())
                && !above.getValue(net.minecraft.world.level.block.CrossCollisionBlock.WATERLOGGED)) {
            BlockState upper = kind == CHILI
                ? ModBlocks.CHILI_ON_ROPE.defaultBlockState().setValue(ChiliCropBlock.STAKED, false)
                : vectorwing.farmersdelight.common.registry.ModBlocks.TOMATO_CROP_ON_ROPE
                    .get().defaultBlockState();
            return level.setBlock(abovePos, upper, Block.UPDATE_ALL);
        }
        return false;
    }

    @Override public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(CROP) != EMPTY;
    }

    @Override public void randomTick(BlockState state, ServerLevel level,
            BlockPos pos, RandomSource random) {
        int kind = state.getValue(CROP);
        if (kind == EMPTY) return;
        if (!canCropLive(level, pos, kind)) {
            removeCrop(level, pos, null);
            return;
        }
        if (level.getRawBrightness(pos, 0) < 9) return;
        int age = state.getValue(CROP_AGE);
        if (age < maxAge(kind) && random.nextInt(5) == 0) {
            level.setBlock(pos, state.setValue(CROP_AGE, age + 1), Block.UPDATE_CLIENTS);
        } else if (age == maxAge(kind) && random.nextInt(3) == 0) {
            tryClimb(level, pos, state);
        }
    }

    @Override public boolean isValidBonemealTarget(LevelReader level,
            BlockPos pos, BlockState state, BonemealSource source) {
        return state.getValue(CROP) != EMPTY &&
            (state.getValue(CROP_AGE) < maxAge(state.getValue(CROP))
                || vineHeight(level, pos, state.getValue(CROP)) < 3);
    }

    @Override public boolean isBonemealSuccess(Level level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    @Override public void performBonemeal(ServerLevel level, RandomSource random,
            BlockPos pos, BlockState state, BonemealSource source) {
        int kind = state.getValue(CROP);
        if (kind == EMPTY) return;
        if (state.getValue(CROP_AGE) < maxAge(kind)) {
            BlockState grown = state.setValue(CROP_AGE,
                Math.min(maxAge(kind), state.getValue(CROP_AGE) + 1 + random.nextInt(2)));
            level.setBlock(pos, grown, Block.UPDATE_ALL);
            state = grown;
        }
        tryClimb(level, pos, state);
    }
}
