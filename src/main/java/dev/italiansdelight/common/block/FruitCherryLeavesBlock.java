package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.orchard.OrchardSeasonPolicy;
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
 * Six visual states for the *fruiting* sweet cherry: leaves (0), buds (1),
 * first WHITE flowers (2), abundant WHITE blossoms (3), small green drupes (4),
 * and harvestable red cherries (5). This is not an ornamental pink sakura.
 *
 * <p>As with approved olive and almond harvesting, flowers and cherries
 * develop only on natural, supported leaves with one of six faces facing air.
 * Fully enclosed leaves immediately revert to stage zero; vanilla decay,
 * manual leaf placement, shears, Silk Touch, and sapling drops stay intact.
 */
public final class FruitCherryLeavesBlock extends UntintedParticleLeavesBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);
    public static final int RIPE_STAGE = 5;
    private static final int ADVANCE_CHANCE = 3; // one advance in three random ticks

    public FruitCherryLeavesBlock(Properties properties) {
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
     * Fruit Cherry flowers and nuts may only exist on exposed canopy leaves.
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
        int seasonalMax = OrchardSeasonPolicy.maxStage(level, pos,
                OrchardSeasonPolicy.Fruit.CHERRY, RIPE_STAGE);
        // Stages above the seasonal window must not carry ripe fruit into
        // winter (or back into spring when /season set changes the calendar).
        if (stage > seasonalMax) {
            level.setBlock(pos, current.setValue(STAGE, 0), 2);
            return;
        }
        if (stage < seasonalMax && random.nextInt(ADVANCE_CHANCE) == 0) {
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
            // Always ask the *server's* season before allowing a harvest.
            // Without Serene Seasons this check is a no-op.
            if (OrchardSeasonPolicy.maxStage(server, pos,
                    OrchardSeasonPolicy.Fruit.CHERRY, RIPE_STAGE) < RIPE_STAGE) {
                return InteractionResult.PASS;
            }
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
                    new ItemStack(ModItems.CHERRIES, 1 + server.getRandom().nextInt(3)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS, 0.75F, 0.9F + server.getRandom().nextFloat() * 0.2F);
        }
        return InteractionResult.SUCCESS;
    }
}
