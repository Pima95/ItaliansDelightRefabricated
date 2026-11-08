package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.block.entity.CrushingTubBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Rustic 1.12.2 Crushing Tub interaction adapted to Minecraft 26.2.
 * Hollow appearance is unchanged across all fifteen wood variants.
 * Collision follows the solid, 9/16-high landing plane used by original Rustic.
 */
public final class CrushingTubBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(1, 0, 1, 15, 1, 15),
        Block.box(1, 0, 0, 15, 9, 1),
        Block.box(1, 0, 15, 15, 9, 16),
        Block.box(0, 0, 1, 1, 9, 15),
        Block.box(15, 0, 1, 16, 9, 15)
    ).optimize();

    /**
     * Rustic 1.12.2 BlockCrushingTub returns TUB_AABB (0,0,0 -> 1,9/16,1)
     * for collision, despite the hollow five-part appearance. Its single flat
     * landing surface causes fallOn to fire in the CENTER as well as on the rim.
     *
     * Keep SHAPE for selection and the approved hollow model/texture. Do not
     * use SHAPE for actual collision: the 1/16-high center floor could make
     * Minecraft resolve fallOn against the ground underneath the tub instead.
     */
    private static final VoxelShape RUSTIC_LANDING_COLLISION =
        Block.box(0, 0, 0, 16, 9, 16);

    public CrushingTubBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED,
            context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
    }

    @Override public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED))
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, ticks, pos, direction,
            neighborPos, neighborState, random);
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrushingTubBlock::new);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrushingTubBlockEntity(pos, state);
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                         CollisionContext context) { return SHAPE; }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                                  CollisionContext context) {
        return RUSTIC_LANDING_COLLISION;
    }

    @Override protected InteractionResult useItemOn(
        ItemStack held, BlockState state, Level level, BlockPos pos, Player player,
        InteractionHand hand, BlockHitResult hit
    ) {
        boolean bottle = held.is(Items.GLASS_BOTTLE);
        boolean bucket = held.is(Items.BUCKET);
        boolean ingredient = CrushingTubRecipes.find(held) != null;
        if (state.getValue(WATERLOGGED)) return InteractionResult.SUCCESS;
        if (player.isSpectator()) return InteractionResult.PASS;
        // Right click with an unrelated item retrieves the existing inputs.
        if (!bottle && !bucket && !ingredient) {
            if (level.getBlockEntity(pos) instanceof CrushingTubBlockEntity contents
                    && !contents.getInput().isEmpty()) {
                if (!level.isClientSide()) returnInputs(level, pos, player, contents);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof CrushingTubBlockEntity tub))
            return InteractionResult.FAIL;

        if (bottle || bucket) {
            int volume = bottle ? 250 : 1000;
            ItemStack filled = BarrelFluidContainers.makeFilled(tub.getFluidId(), volume);
            if (!filled.isEmpty() && tub.extract(volume)) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, filled));
                level.playSound(null, pos, bottle ? SoundEvents.BOTTLE_FILL : SoundEvents.BUCKET_FILL,
                    SoundSource.BLOCKS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        }

        int inserted = tub.insert(held);
        if (inserted > 0 && !player.getAbilities().instabuild) held.shrink(inserted);
        else if (inserted == 0 && !tub.getInput().isEmpty()
                && !ItemStack.isSameItemSameComponents(tub.getInput(), held))
            returnInputs(level, pos, player, tub);
        return InteractionResult.SUCCESS;
    }

    @Override protected InteractionResult useWithoutItem(
        BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit
    ) {
        if (state.getValue(WATERLOGGED)) return InteractionResult.SUCCESS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof CrushingTubBlockEntity tub))
            return InteractionResult.FAIL;
        // Always return the unprocessed input on right-click. Only when
        // empty, sneak-right-click may drain the remaining processed liquid.
        if (!tub.getInput().isEmpty()) returnInputs(level, pos, player, tub);
        if (player.isShiftKeyDown()) tub.discardFluid();
        return InteractionResult.SUCCESS;
    }

    private static void returnInputs(Level level, BlockPos pos, Player player,
                                     CrushingTubBlockEntity tub) {
        ItemStack back = tub.takeInputs();
        if (!back.isEmpty() && !player.addItem(back)) popResource(level, pos, back);
    }

    @Override public void fallOn(Level level, BlockState state, BlockPos pos,
                                 Entity entity, double fallDistance) {
        if (!state.getValue(WATERLOGGED) && !level.isClientSide()
                && entity instanceof LivingEntity &&
            level.getBlockEntity(pos) instanceof CrushingTubBlockEntity tub) {
            ItemStack byproduct = tub.stomp();
            if (byproduct != null) {
                if (!byproduct.isEmpty()) popResource(level, pos.above(), byproduct);
                level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL,
                    SoundSource.BLOCKS, 0.5f, 1.0f);
            }
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
            instanceof CrushingTubBlockEntity tub && !tub.getInput().isEmpty())
            drops.add(tub.getInput());
        return drops;
    }
}
