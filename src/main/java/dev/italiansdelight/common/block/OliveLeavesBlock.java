package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The olive's five visual states: bare foliage (0), buds (1), white blossoms
 * (2), small green drupes (3), and harvestable green olives (4).
 *
 * <p>Flowers and olives develop ONLY on naturally grown foliage with
 * at least one orthogonally adjacent air block (any of six faces).
 * Occlusion/leaf decay removes the fruit stage, never the vanilla foliage.
 */
public final class OliveLeavesBlock extends UntintedParticleLeavesBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);
    public static final int RIPE_STAGE = 4;
    private static final int ADVANCE_CHANCE = 3; // one advance in three random ticks

    public OliveLeavesBlock(Properties properties) {
        super(0.0F, ParticleTypes.CHERRY_LEAVES, properties);
        registerDefaultState(defaultBlockState().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    /** Foliage placed by players, decaying leaves, and underwater leaves do not flower. */
    private static boolean canDevelop(BlockState state) {
        return !state.getValue(PERSISTENT)
                && state.getValue(DISTANCE) < 7
                && !state.getValue(WATERLOGGED);
    }

    /**
     * A flower or olive may only exist on canopy surface blocks.
     * For updateShape, consider the new neighboring blockstate, which can
     * precede the world's neighbor lookup on some update paths.
     */
    private static boolean hasAirFace(BlockGetter level, BlockPos pos,
                                      Direction changedSide, BlockState newNeighbor) {
        for (Direction face : Direction.values()) {
            BlockState neighbor = face == changedSide
                    ? newNeighbor : level.getBlockState(pos.relative(face));
            if (neighbor.isAir()) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAirFace(BlockGetter level, BlockPos pos) {
        return hasAirFace(level, pos, null, null);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // Vanilla decay remains active. Mature exposed foliage also ticks to
        // correct stale fruit if a neighboring block changes while unloaded.
        return super.isRandomlyTicking(state) || canDevelop(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos,
                              RandomSource random) {
        super.randomTick(state, level, pos, random);
        BlockState current = level.getBlockState(pos);
        if (!current.is(this)) {
            return; // vanilla decay removed this block
        }

        if (!canDevelop(current) || !hasAirFace(level, pos)) {
            if (current.getValue(STAGE) != 0) {
                level.setBlock(pos, current.setValue(STAGE, 0), 2);
            }
            return;
        }

        int stage = current.getValue(STAGE);
        if (stage < RIPE_STAGE && random.nextInt(ADVANCE_CHANCE) == 0) {
            level.setBlock(pos, current.setValue(STAGE, stage + 1), 2);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level,
                                     ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        BlockState updated = super.updateShape(state, level, ticks, pos,
                direction, neighborPos, neighborState, random);
        // A canopy face becoming enclosed immediately hides its flowers/fruit.
        if (updated.is(this) && updated.getValue(STAGE) != 0
                && (!canDevelop(updated)
                    || !hasAirFace(level, pos, direction, neighborState))) {
            return updated.setValue(STAGE, 0);
        }
        return updated;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (state.getValue(STAGE) != RIPE_STAGE
                || !canDevelop(state)
                || !hasAirFace(level, pos)) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel server) {
            BlockState current = server.getBlockState(pos);
            if (!current.is(this)
                    || current.getValue(STAGE) != RIPE_STAGE
                    || !canDevelop(current)
                    || !hasAirFace(server, pos)) {
                return InteractionResult.PASS;
            }
            // Reset FIRST to avoid double-harvest by two players on the same tick.
            server.setBlock(pos, current.setValue(STAGE, 0), 2);
            Block.popResource(server, pos,
                    new ItemStack(ModItems.OLIVES, 1 + server.getRandom().nextInt(3)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS, 0.75F, 0.9F + server.getRandom().nextFloat() * 0.2F);
        }
        return InteractionResult.SUCCESS;
    }
}
