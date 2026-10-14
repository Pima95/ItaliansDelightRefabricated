package dev.italiansdelight.common.block;

import com.mojang.serialization.MapCodec;
import dev.italiansdelight.common.pizza.PizzaDoughSupport;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.pizza.PizzaComposition;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
public final class PizzaDoughBlock extends Block implements EntityBlock {
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
        recessedShape(2, 14, 2, 3, 1),
        recessedShape(1, 15, 1, 2, 1)
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaDoughBlockEntity(pos, state);
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

    /**
     * Formed dough yields ONE complete pizza item, not the wheat dough and
     * separate toppings. Earlier dough stages retain their normal loot table.
     * Loot context supplies the original BlockEntity before it is removed.
     */
    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> normalDrops = super.getDrops(state, params);
        if (state.getValue(STAGE) < MAX_STAGE) {
            return normalDrops;
        }
        // Respect the existing vanilla loot conditions (including explosion
        // survival). If the base dough did not survive, do not create a pizza.
        boolean baseSurvived = normalDrops.stream().anyMatch(
                stack -> stack.is(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get()));
        if (!baseSurvived) {
            return List.of();
        }
        // Convert the whole surviving base into ONE data-carrying item.
        // Suppress the old loot table's separate legacy-mozzarella drop:
        // that ingredient is preserved in the component instead.
        PizzaDoughBlockEntity pizzaEntity =
                params.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
                        instanceof PizzaDoughBlockEntity entity ? entity : null;
        return List.of(createPizzaItem(state, pizzaEntity));
    }

    private static ItemStack createPizzaItem(BlockState state, PizzaDoughBlockEntity entity) {
        PizzaComposition composition =
                entity == null ? PizzaComposition.empty() : entity.composition();
        ItemStack stack = new ItemStack(ModItems.RAW_PIZZA);
        stack.set(ModDataComponents.PIZZA_CONTENTS,
                new PizzaItemData(composition, state.getValue(MOZZARELLA)));
        return stack;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (state.getValue(STAGE) == MAX_STAGE) {
            // Pick Block on a formed pizza also preserves its composition.
            PizzaDoughBlockEntity entity = level.getBlockEntity(pos)
                    instanceof PizzaDoughBlockEntity pizza ? pizza : null;
            return createPizzaItem(state, entity);
        }
        return new ItemStack(vectorwing.farmersdelight.common.registry.ModItems.WHEAT_DOUGH.get());
    }
}
