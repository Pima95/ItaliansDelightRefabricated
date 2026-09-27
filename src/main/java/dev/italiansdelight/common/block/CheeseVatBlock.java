package dev.italiansdelight.common.block;

import java.util.List;

import com.mojang.serialization.MapCodec;

import dev.italiansdelight.common.block.entity.CheeseVatBlockEntity;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModParticles;
import dev.italiansdelight.common.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cheese Vat block. Handles block state, facing, direct serving collection,
 * heat visuals, Farmer's Delight tray support, and the linked BlockEntity.
 */
public class CheeseVatBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING =
        BlockStateProperties.HORIZONTAL_FACING;

    /**
     * Server-maintained visual state. It does not control cooking by itself:
     * the BlockEntity still performs the authoritative heat check.
     */
    public static final BooleanProperty HEATED =
        BooleanProperty.create("heated");

    /**
     * Whether Farmer's Delight's heating tray should be rendered below the vat.
     */
    public static final BooleanProperty SUPPORT =
        BooleanProperty.create("support");

    // Local reference recommended by Farmer's Delight for add-ons.
    private static final TagKey<Block> TRAY_HEAT_SOURCES =
        TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(
                "farmersdelight",
                "tray_heat_sources"
            )
        );

    // Keep the vat hollow like a vanilla cauldron. The previous filled inner
    // volume made block-breaking effects much denser than expected.
    private static final VoxelShape SHAPE = Shapes.or(
        Blocks.CAULDRON.defaultBlockState().getShape(
            EmptyBlockGetter.INSTANCE,
            BlockPos.ZERO
        ),
        Block.box(1, 8, -1, 15, 12, 0),
        Block.box(1, 8, 16, 15, 12, 17),
        Block.box(-1, 8, 1, 0, 12, 15),
        Block.box(16, 8, 1, 17, 12, 15)
    );

    private static final VoxelShape SHAPE_WITH_TRAY = Shapes.or(
        SHAPE,
        Block.box(0, -1, 0, 16, 0, 16)
    );

    public CheeseVatBlock(
        Properties properties
    ) {
        super(properties);

        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HEATED, false)
                .setValue(SUPPORT, false)
        );
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return state.getValue(SUPPORT)
            ? SHAPE_WITH_TRAY
            : SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(
        BlockPlaceContext context
    ) {
        BlockPos pos = context.getClickedPos();

        return defaultBlockState()
            .setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
            )
            .setValue(
                SUPPORT,
                hasTraySupport(context.getLevel(), pos)
            );
    }

    @Override
    public BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess scheduledTickAccess,
        BlockPos currentPos,
        Direction direction,
        BlockPos neighborPos,
        BlockState neighborState,
        RandomSource random
    ) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return state.setValue(
                SUPPORT,
                hasTraySupport(level, currentPos)
            );
        }

        return state;
    }

    private static boolean hasTraySupport(
        LevelReader level,
        BlockPos pos
    ) {
        return level.getBlockState(pos.below())
            .is(TRAY_HEAT_SOURCES);
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HEATED, SUPPORT);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(
            CheeseVatBlock::new
        );
    }

    @Override
    public BlockEntity newBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        return new CheeseVatBlockEntity(
            pos,
            state
        );
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (
            !level.isClientSide()
            && level.getBlockEntity(pos)
                instanceof CheeseVatBlockEntity cheeseVat
        ) {
            player.openMenu(
                cheeseVat
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack itemStack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (
            level.getBlockEntity(pos)
                instanceof CheeseVatBlockEntity cheeseVat
        ) {
            ItemStack serving =
                cheeseVat.takeServing(
                    itemStack,
                    player
                );

            if (!serving.isEmpty()) {
                if (!player.addItem(serving)) {
                    player.drop(
                        serving,
                        false
                    );
                }

                return InteractionResult.SUCCESS;
            }

            // Empty containers extract whey from the internal tank.
            // Filled whey containers are intentionally not handled here:
            // the tank is output-only and cannot be manually refilled.
            if (
                itemStack.is(Items.GLASS_BOTTLE)
                && cheeseVat.extractWhey(250)
            ) {
                player.setItemInHand(
                    hand,
                    ItemUtils.createFilledResult(
                        itemStack,
                        player,
                        new ItemStack(ModItems.WHEY_BOTTLE)
                    )
                );

                level.playSound(
                    null,
                    pos,
                    SoundEvents.BOTTLE_FILL,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
                );

                level.gameEvent(
                    null,
                    GameEvent.FLUID_PICKUP,
                    pos
                );

                return InteractionResult.SUCCESS;
            }

            if (
                itemStack.is(Items.BUCKET)
                && cheeseVat.extractWhey(1000)
            ) {
                player.setItemInHand(
                    hand,
                    ItemUtils.createFilledResult(
                        itemStack,
                        player,
                        new ItemStack(ModItems.WHEY_BUCKET)
                    )
                );

                level.playSound(
                    null,
                    pos,
                    SoundEvents.BUCKET_FILL,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
                );

                level.gameEvent(
                    null,
                    GameEvent.FLUID_PICKUP,
                    pos
                );

                return InteractionResult.SUCCESS;
            }

            player.openMenu(
                cheeseVat
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected List<ItemStack> getDrops(
        BlockState state,
        LootParams.Builder params
    ) {
        List<ItemStack> drops =
            super.getDrops(
                state,
                params
            );

        if (
            params.getOptionalParameter(
                LootContextParams.BLOCK_ENTITY
            ) instanceof CheeseVatBlockEntity vat
        ) {
            for (ItemStack stack : drops) {
                if (stack.is(asItem())) {
                    vat.preservePendingResult(
                        stack,
                        params.getLevel().registryAccess()
                    );
                }
            }
        }

        return drops;
    }

    @Override
    public void animateTick(
        BlockState state,
        Level level,
        BlockPos pos,
        RandomSource random
    ) {
        if (!state.getValue(HEATED) || random.nextFloat() >= 0.20F) {
            return;
        }

        // Mirrors Farmer's Delight's Cooking Pot bubble-pop cadence, but uses
        // a dedicated white sprite animation to match the heated cheese surface.
        double x =
            pos.getX()
                + 0.5D
                + (random.nextDouble() * 0.6D - 0.3D);
        double y = pos.getY() + 0.96D;
        double z =
            pos.getZ()
                + 0.5D
                + (random.nextDouble() * 0.6D - 0.3D);

        level.addParticle(
            ModParticles.WHITE_BUBBLE_POP,
            x,
            y,
            z,
            0.0D,
            0.0D,
            0.0D
        );
    }

    @Override
    public <T extends BlockEntity>
    BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (
            level instanceof ServerLevel serverLevel
        ) {
            return createTickerHelper(
                type,
                ModBlockEntities.CHEESE_VAT,
                (
                    commonLevel,
                    pos,
                    blockState,
                    cheeseVat
                ) ->
                    CheeseVatBlockEntity.serverTick(
                        serverLevel,
                        pos,
                        blockState,
                        cheeseVat
                    )
            );
        }

        return null;
    }
}
