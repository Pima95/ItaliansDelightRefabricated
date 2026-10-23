package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.block.entity.PizzaOvenBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * One-block pizza-only furnace. Temporarily uses the vanilla furnace model
 * (and vanilla FurnaceScreen). A custom block model can replace it later
 * without changing its inventory, direction, blockstate or save format.
 */
public final class PizzaOvenBlock extends BaseEntityBlock {
    private static final MapCodec<PizzaOvenBlock> CODEC =
            simpleCodec(PizzaOvenBlock::new);

    public static final EnumProperty<Direction> FACING =
            BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public PizzaOvenBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        // BaseEntityBlock defaults may be invisible: force the normal
        // furnace model to render until dedicated oven art is approved.
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaOvenBlockEntity(pos, state);
    }

    private static InteractionResult open(
            Level level, BlockPos pos, Player player
    ) {
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof PizzaOvenBlockEntity oven) {
            player.openMenu(oven);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit
    ) {
        return open(level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit
    ) {
        // Phase 4B: GUI works with or without the future pizza peel.
        // The peel's atomic direct-transfer interactions will be connected
        // after its own persisted item component has been implemented.
        return open(level, pos, player);
    }

    /**
     * Mirror vanilla furnace's client-side burning animation. Unlike a
     * texture animation, the vanilla furnace uses smoke and flame particles
     * at the front opening and occasional fire-crackle sounds.
     *
     * <p>Only the server-controlled LIT blockstate activates these effects.
     * In particular, the fire keeps crackling after the pizza is cooked
     * while the ignited fuel continues burning (vanilla furnace behavior).
     * Direction-relative positions work for all four horizontal facings.
     */
    @Override
    public void animateTick(
            BlockState state, Level level, BlockPos pos, RandomSource random
    ) {
        if (!state.getValue(LIT)) {
            return;
        }

        double centerX = pos.getX() + 0.5D;
        double baseY = pos.getY();
        double centerZ = pos.getZ() + 0.5D;

        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(
                    centerX, baseY, centerZ,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS, 1.0F, 1.0F, false
            );
        }

        Direction facing = state.getValue(FACING);
        Direction.Axis axis = facing.getAxis();
        double acrossOpening = random.nextDouble() * 0.6D - 0.3D;
        double x = centerX + (axis == Direction.Axis.X
                ? facing.getStepX() * 0.52D : acrossOpening);
        double y = baseY + random.nextDouble() * 6.0D / 16.0D;
        double z = centerZ + (axis == Direction.Axis.Z
                ? facing.getStepZ() * 0.52D : acrossOpening);

        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        if (level instanceof ServerLevel server) {
            return createTickerHelper(
                    type, ModBlockEntities.PIZZA_OVEN,
                    (world, pos, current, oven) ->
                            PizzaOvenBlockEntity.serverTick(
                                    server, pos, current, oven));
        }
        return null;
    }
}
