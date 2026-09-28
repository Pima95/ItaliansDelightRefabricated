package dev.italiansdelight.common.block.entity;

import java.util.Optional;
import java.util.OptionalInt;

import dev.italiansdelight.common.aging.HangingCheeseType;
import dev.italiansdelight.common.block.CheeseHookBlock;
import dev.italiansdelight.common.crafting.CheeseAgingRecipe;
import dev.italiansdelight.common.crafting.CheeseDryingRecipe;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModBlocks;
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
 * World-side process state for one Cheese Hook.
 *
 * Only elapsed time is persisted in the BlockEntity. The attached cheese type
 * is represented by the block state. No process data is ever written to the
 * returned ItemStack.
 */
public final class CheeseHookBlockEntity extends BlockEntity {

    private int elapsedTicks;

    private final RecipeManager.CachedCheck<
        SingleRecipeInput,
        CheeseAgingRecipe
    > agingCheck;

    private final RecipeManager.CachedCheck<
        SingleRecipeInput,
        CheeseDryingRecipe
    > dryingCheck;

    public CheeseHookBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        super(
            ModBlockEntities.CHEESE_HOOK,
            pos,
            state
        );

        this.agingCheck =
            RecipeManager.createCheck(
                ModRecipes.CHEESE_AGING_TYPE
            );

        this.dryingCheck =
            RecipeManager.createCheck(
                ModRecipes.CHEESE_DRYING_TYPE
            );
    }

    public boolean insert(
        ServerLevel level,
        HangingCheeseType cheeseType
    ) {
        if (
            cheeseType.isEmpty()
            || !getBlockState()
                .getValue(CheeseHookBlock.CHEESE)
                .isEmpty()
        ) {
            return false;
        }

        if (
            cheeseType.isProcessing()
            && requiredTicks(
                level,
                cheeseType
            ).isEmpty()
        ) {
            return false;
        }

        if (!canReserveHangingSpace(level)) {
            return false;
        }

        elapsedTicks = 0;

        BlockState previousState =
            getBlockState();

        level.setBlock(
            worldPosition,
            previousState.setValue(
                CheeseHookBlock.CHEESE,
                cheeseType
            ),
            Block.UPDATE_CLIENTS
        );

        if (!reserveHangingSpace(level)) {
            level.setBlock(
                worldPosition,
                previousState,
                Block.UPDATE_CLIENTS
            );
            return false;
        }

        setChanged();
        return true;
    }

    public ItemStack remove(
        ServerLevel level
    ) {
        HangingCheeseType cheeseType =
            getBlockState()
                .getValue(
                    CheeseHookBlock.CHEESE
                );

        if (cheeseType.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result =
            cheeseType.stack();

        elapsedTicks = 0;

        clearReservedHangingSpace(
            level
        );

        level.setBlock(
            worldPosition,
            getBlockState()
                .setValue(
                    CheeseHookBlock.CHEESE,
                    HangingCheeseType.EMPTY
                ),
            Block.UPDATE_CLIENTS
        );

        setChanged();
        return result;
    }

    public ItemStack getCheeseDrop(
        BlockState state
    ) {
        return state.getValue(
                CheeseHookBlock.CHEESE
            )
            .stack();
    }

    public static void serverTick(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CheeseHookBlockEntity blockEntity
    ) {
        HangingCheeseType cheeseType =
            state.getValue(
                CheeseHookBlock.CHEESE
            );

        if (cheeseType.isEmpty()) {
            blockEntity.clearReservedHangingSpace(
                level
            );
            return;
        }

        // Also repairs old worlds created before the occupied-space helper
        // existed, as long as the block below is still empty.
        blockEntity.reserveHangingSpace(
            level
        );

        if (!cheeseType.isProcessing()) {
            return;
        }

        OptionalInt required =
            blockEntity.requiredTicks(
                level,
                cheeseType
            );

        if (required.isEmpty()) {
            return;
        }

        blockEntity.elapsedTicks++;

        if (
            blockEntity.elapsedTicks
                >= required.getAsInt()
        ) {
            blockEntity.elapsedTicks = 0;

            level.setBlock(
                pos,
                state.setValue(
                    CheeseHookBlock.CHEESE,
                    cheeseType.matureVersion()
                ),
                Block.UPDATE_CLIENTS
            );
        }

        blockEntity.setChanged();
    }

    private boolean canReserveHangingSpace(
        ServerLevel level
    ) {
        BlockState below =
            level.getBlockState(
                worldPosition.below()
            );

        return below.isAir()
            || below.getBlock()
                == ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE;
    }

    private boolean reserveHangingSpace(
        ServerLevel level
    ) {
        BlockPos reservedPos =
            worldPosition.below();

        BlockState below =
            level.getBlockState(
                reservedPos
            );

        if (
            below.getBlock()
                == ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE
        ) {
            return true;
        }

        if (!below.isAir()) {
            return false;
        }

        return level.setBlock(
            reservedPos,
            ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE
                .defaultBlockState(),
            Block.UPDATE_ALL
        );
    }

    private void clearReservedHangingSpace(
        ServerLevel level
    ) {
        BlockPos reservedPos =
            worldPosition.below();

        if (
            level.getBlockState(
                    reservedPos
                )
                .getBlock()
                == ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE
        ) {
            level.removeBlock(
                reservedPos,
                false
            );
        }
    }

    private OptionalInt requiredTicks(
        ServerLevel level,
        HangingCheeseType cheeseType
    ) {
        SingleRecipeInput input =
            new SingleRecipeInput(
                cheeseType.stack()
            );

        if (
            cheeseType.process()
                == HangingCheeseType.Process.AGING
        ) {
            Optional<RecipeHolder<CheeseAgingRecipe>>
                recipe =
                    agingCheck.getRecipeFor(
                        input,
                        level
                    );

            return recipe.isPresent()
                ? OptionalInt.of(
                    recipe.get()
                        .value()
                        .getAgingTime()
                )
                : OptionalInt.empty();
        }

        if (
            cheeseType.process()
                == HangingCheeseType.Process.DRYING
        ) {
            Optional<RecipeHolder<CheeseDryingRecipe>>
                recipe =
                    dryingCheck.getRecipeFor(
                        input,
                        level
                    );

            return recipe.isPresent()
                ? OptionalInt.of(
                    recipe.get()
                        .value()
                        .getDryingTime()
                )
                : OptionalInt.empty();
        }

        return OptionalInt.empty();
    }

    @Override
    protected void loadAdditional(
        ValueInput input
    ) {
        super.loadAdditional(input);

        elapsedTicks =
            Math.max(
                0,
                input.getIntOr(
                    "ElapsedTicks",
                    0
                )
            );
    }

    @Override
    protected void saveAdditional(
        ValueOutput output
    ) {
        output.putInt(
            "ElapsedTicks",
            elapsedTicks
        );

        super.saveAdditional(output);
    }
}
