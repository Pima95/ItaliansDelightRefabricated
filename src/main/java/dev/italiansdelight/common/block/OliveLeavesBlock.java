package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Harvestable olive foliage (Orchard 1.1.0, first fruit).
 *
 * <p>The registered block ID stays italiansdelight:olive_leaves. A boolean
 * BlockState property persists ripeness through world saves/chunk reloads.
 * Natural 1x1 and 2x2 sapling-grown trees can fruit; manually placed leaves
 * cannot. Vanilla leaf decay, waterlogging, Silk Touch/shears, sapling drops
 * and silver-grey foliage remain unchanged.
 */
public final class OliveLeavesBlock extends UntintedParticleLeavesBlock {
    public static final BooleanProperty FRUITING = BooleanProperty.create("fruiting");

    // A naturally supported, immature leaf gets one independent attempt per
    // random tick. This is separate from vanilla's decay logic.
    private static final int RIPEN_CHANCE = 12;

    public OliveLeavesBlock(Properties properties) {
        super(0.0F, ParticleTypes.CHERRY_LEAVES, properties);
        registerDefaultState(defaultBlockState().setValue(FRUITING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FRUITING);
    }

    /** No fruit on placed leaves, unsupported/decaying leaves or underwater leaves. */
    private static boolean canFruit(BlockState state) {
        return !state.getValue(PERSISTENT)
                && state.getValue(DISTANCE) < 7
                && !state.getValue(WATERLOGGED);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // UntintedParticleLeavesBlock normally random-ticks only for decay.
        // Immature, natural foliage additionally ticks to ripen.
        return super.isRandomlyTicking(state)
                || (canFruit(state) && !state.getValue(FRUITING));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // ALWAYS delegate to vanilla before new growth: keep leaf decay.
        super.randomTick(state, level, pos, random);
        if (!state.getValue(FRUITING)
                && canFruit(state)
                && level.getBlockState(pos).is(this)
                && random.nextInt(RIPEN_CHANCE) == 0) {
            level.setBlock(pos, state.setValue(FRUITING, true), 2);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (!state.getValue(FRUITING) || !canFruit(state)) {
            return InteractionResult.PASS;
        }
        // Server authority guarantees one harvest per ripe state even when
        // multiple players right-click in the same tick. No item is consumed.
        if (level instanceof ServerLevel server) {
            if (!server.getBlockState(pos).getValue(FRUITING)) {
                return InteractionResult.PASS;
            }
            server.setBlock(pos, state.setValue(FRUITING, false), 2);
            Block.popResource(server, pos, new ItemStack(ModItems.OLIVES, 1 + server.random.nextInt(3)));
            server.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS, 0.75F, 0.9F + server.random.nextFloat() * 0.2F);
        }
        return InteractionResult.SUCCESS;
    }
}
