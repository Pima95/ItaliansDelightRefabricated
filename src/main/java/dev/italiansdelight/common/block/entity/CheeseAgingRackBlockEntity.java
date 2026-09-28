package dev.italiansdelight.common.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.block.CheeseAgingRackBlock;
import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Stores the two independent aging processes of a Cheese Aging Rack.
 *
 * Aging progress belongs only to this BlockEntity. Removing a cheese creates a
 * clean ItemStack and therefore intentionally loses any incomplete progress.
 */
public final class CheeseAgingRackBlockEntity extends BlockEntity {

    public static final int LOWER_SLOT = 0;
    public static final int UPPER_SLOT = 1;
    public static final int SLOT_COUNT = 2;

    private final AgingCheeseType[] cheeseTypes =
        new AgingCheeseType[SLOT_COUNT];

    private final int[] elapsedTicks =
        new int[SLOT_COUNT];

    private final boolean[] mature =
        new boolean[SLOT_COUNT];

    private final RecipeManager.CachedCheck<
        SingleRecipeInput,
        CheeseAgingRecipe
    > quickCheck;

    public CheeseAgingRackBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        super(
            ModBlockEntities.CHEESE_AGING_RACK,
            pos,
            state
        );

        this.quickCheck =
            RecipeManager.createCheck(
                ModRecipes.CHEESE_AGING_TYPE
            );
    }

    public boolean insert(
        ServerLevel level,
        int slot,
        AgingCheeseType cheeseType
    ) {
        if (
            !isValidSlot(slot)
            || isOccupied(slot)
            || findRecipe(level, cheeseType).isEmpty()
        ) {
            return false;
        }

        cheeseTypes[slot] =
            cheeseType;

        elapsedTicks[slot] = 0;
        mature[slot] = false;

        updateOccupiedState(level);
        setChanged();

        return true;
    }

    public ItemStack remove(
        ServerLevel level,
        int slot
    ) {
        if (
            !isValidSlot(slot)
            || !isOccupied(slot)
        ) {
            return ItemStack.EMPTY;
        }

        ItemStack stack =
            createPickupStack(
                level,
                slot
            );

        clearSlot(slot);
        updateOccupiedState(level);
        setChanged();

        return stack;
    }

    public List<ItemStack> getCheeseDrops(
        ServerLevel level
    ) {
        List<ItemStack> drops =
            new ArrayList<>();

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (isOccupied(slot)) {
                drops.add(
                    createPickupStack(
                        level,
                        slot
                    )
                );
            }
        }

        return drops;
    }

    public boolean isOccupied(int slot) {
        return isValidSlot(slot)
            && cheeseTypes[slot] != null;
    }

    public boolean matchesCheeseType(
        int slot,
        AgingCheeseType cheeseType
    ) {
        return isValidSlot(slot)
            && cheeseTypes[slot] == cheeseType;
    }

    public static void serverTick(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CheeseAgingRackBlockEntity rack
    ) {
        boolean changed =
            false;

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (
                !rack.isOccupied(slot)
                || rack.mature[slot]
            ) {
                continue;
            }

            AgingCheeseType cheeseType =
                rack.cheeseTypes[slot];

            Optional<RecipeHolder<CheeseAgingRecipe>>
                recipeHolder =
                    rack.findRecipe(
                        level,
                        cheeseType
                    );

            if (recipeHolder.isEmpty()) {
                continue;
            }

            CheeseAgingRecipe recipe =
                recipeHolder.get()
                    .value();

            rack.elapsedTicks[slot]++;

            if (
                rack.elapsedTicks[slot]
                    >= recipe.getAgingTime()
            ) {
                rack.elapsedTicks[slot] =
                    recipe.getAgingTime();

                rack.mature[slot] =
                    true;
            }

            changed =
                true;
        }

        if (changed) {
            rack.setChanged();
        }
    }

    private Optional<RecipeHolder<CheeseAgingRecipe>>
    findRecipe(
        ServerLevel level,
        AgingCheeseType cheeseType
    ) {
        return quickCheck.getRecipeFor(
            new SingleRecipeInput(
                cheeseType.freshStack()
            ),
            level
        );
    }

    private ItemStack createPickupStack(
        ServerLevel level,
        int slot
    ) {
        AgingCheeseType cheeseType =
            cheeseTypes[slot];

        if (
            cheeseType == null
            || !mature[slot]
        ) {
            return cheeseType == null
                ? ItemStack.EMPTY
                : cheeseType.freshStack();
        }

        return findRecipe(
            level,
            cheeseType
        ).map(holder ->
            holder.value()
                .getResultTemplate()
                .create()
        ).orElseGet(
            cheeseType::matureStack
        );
    }

    private void clearSlot(int slot) {
        cheeseTypes[slot] = null;
        elapsedTicks[slot] = 0;
        mature[slot] = false;
    }

    private void updateOccupiedState(
        ServerLevel level
    ) {
        BlockState state =
            getBlockState();

        BlockState updated =
            state
                .setValue(
                    CheeseAgingRackBlock.LOWER_OCCUPIED,
                    isOccupied(LOWER_SLOT)
                )
                .setValue(
                    CheeseAgingRackBlock.UPPER_OCCUPIED,
                    isOccupied(UPPER_SLOT)
                );

        if (!updated.equals(state)) {
            level.setBlock(
                worldPosition,
                updated,
                Block.UPDATE_CLIENTS
            );
        }
    }

    private static boolean isValidSlot(int slot) {
        return slot >= 0
            && slot < SLOT_COUNT;
    }

    private static AgingCheeseType typeFromOrdinal(
        int ordinal
    ) {
        AgingCheeseType[] values =
            AgingCheeseType.values();

        if (
            ordinal < 0
            || ordinal >= values.length
        ) {
            return null;
        }

        return values[ordinal];
    }

    @Override
    protected void loadAdditional(
        ValueInput input
    ) {
        super.loadAdditional(input);

        cheeseTypes[LOWER_SLOT] =
            typeFromOrdinal(
                input.getIntOr(
                    "LowerType",
                    -1
                )
            );

        elapsedTicks[LOWER_SLOT] =
            Math.max(
                0,
                input.getIntOr(
                    "LowerElapsedTicks",
                    0
                )
            );

        mature[LOWER_SLOT] =
            input.getBooleanOr(
                "LowerMature",
                false
            );

        cheeseTypes[UPPER_SLOT] =
            typeFromOrdinal(
                input.getIntOr(
                    "UpperType",
                    -1
                )
            );

        elapsedTicks[UPPER_SLOT] =
            Math.max(
                0,
                input.getIntOr(
                    "UpperElapsedTicks",
                    0
                )
            );

        mature[UPPER_SLOT] =
            input.getBooleanOr(
                "UpperMature",
                false
            );
    }

    @Override
    protected void saveAdditional(
        ValueOutput output
    ) {
        output.putInt(
            "LowerType",
            cheeseTypes[LOWER_SLOT] == null
                ? -1
                : cheeseTypes[LOWER_SLOT].ordinal()
        );

        output.putInt(
            "LowerElapsedTicks",
            elapsedTicks[LOWER_SLOT]
        );

        output.putBoolean(
            "LowerMature",
            mature[LOWER_SLOT]
        );

        output.putInt(
            "UpperType",
            cheeseTypes[UPPER_SLOT] == null
                ? -1
                : cheeseTypes[UPPER_SLOT].ordinal()
        );

        output.putInt(
            "UpperElapsedTicks",
            elapsedTicks[UPPER_SLOT]
        );

        output.putBoolean(
            "UpperMature",
            mature[UPPER_SLOT]
        );

        super.saveAdditional(output);
    }
}
