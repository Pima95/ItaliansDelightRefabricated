package dev.italiansdelight.common.block.entity;

import java.util.Optional;

import dev.italiansdelight.common.aging.AgingCheeseType;
import dev.italiansdelight.common.block.AgingCheeseBlock;
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
 * World-side aging progress for one cheese wheel placed on a flat surface.
 *
 * Only elapsed ticks are persisted. Progress never moves back into an
 * ItemStack, so removing/breaking an incomplete cheese resets it by design.
 */
public final class AgingCheeseBlockEntity
    extends BlockEntity {

    private int elapsedTicks;

    private final RecipeManager.CachedCheck<
        SingleRecipeInput,
        CheeseAgingRecipe
    > quickCheck;

    public AgingCheeseBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        super(
            ModBlockEntities.AGING_CHEESE,
            pos,
            state
        );

        this.quickCheck =
            RecipeManager.createCheck(
                ModRecipes.CHEESE_AGING_TYPE
            );
    }

    public static void serverTick(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        AgingCheeseBlockEntity blockEntity
    ) {
        if (
            state.getValue(
                AgingCheeseBlock.MATURE
            )
        ) {
            return;
        }

        AgingCheeseType cheeseType =
            state.getValue(
                AgingCheeseBlock.CHEESE
            );

        if (!cheeseType.agesOnSurface()) {
            return;
        }

        Optional<RecipeHolder<CheeseAgingRecipe>>
            recipeHolder =
                blockEntity.quickCheck
                    .getRecipeFor(
                        new SingleRecipeInput(
                            cheeseType.freshStack()
                        ),
                        level
                    );

        if (recipeHolder.isEmpty()) {
            return;
        }

        CheeseAgingRecipe recipe =
            recipeHolder.get()
                .value();

        blockEntity.elapsedTicks++;

        if (
            blockEntity.elapsedTicks
                >= recipe.getAgingTime()
        ) {
            blockEntity.elapsedTicks =
                recipe.getAgingTime();

            level.setBlock(
                pos,
                state.setValue(
                    AgingCheeseBlock.MATURE,
                    true
                ),
                Block.UPDATE_CLIENTS
            );
        }

        blockEntity.setChanged();
    }

    /**
     * Returns exactly what the player receives when the placed cheese leaves
     * the world. Incomplete wheels always return their clean fresh ItemStack.
     */
    public ItemStack getPickupStack(
        ServerLevel level,
        BlockState state
    ) {
        AgingCheeseType cheeseType =
            state.getValue(
                AgingCheeseBlock.CHEESE
            );

        if (
            !state.getValue(
                AgingCheeseBlock.MATURE
            )
        ) {
            return cheeseType.freshStack();
        }

        // A finished cheese placed by the player must remain that exact item,
        // even if a datapack changes the aging recipe output afterwards.
        if (elapsedTicks == 0 || !cheeseType.agesOnSurface()) {
            return cheeseType.matureStack();
        }

        Optional<RecipeHolder<CheeseAgingRecipe>>
            recipeHolder =
                quickCheck.getRecipeFor(
                    new SingleRecipeInput(
                        cheeseType.freshStack()
                    ),
                    level
                );

        return recipeHolder
            .map(holder ->
                holder.value()
                    .getResultTemplate()
                    .create()
            )
            .orElseGet(
                cheeseType::matureStack
            );
    }

    public int getElapsedTicks() {
        return elapsedTicks;
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
