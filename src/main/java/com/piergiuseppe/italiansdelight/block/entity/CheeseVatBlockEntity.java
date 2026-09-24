package com.piergiuseppe.italiansdelight.block.entity;

import java.util.Optional;

import com.piergiuseppe.italiansdelight.menu.CheeseVatMenu;
import com.piergiuseppe.italiansdelight.recipe.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.recipe.CheeseVatRecipeInput;
import com.piergiuseppe.italiansdelight.registry.ModBlockEntities;
import com.piergiuseppe.italiansdelight.registry.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;

public class CheeseVatBlockEntity
    extends BlockEntity
    implements Container, HeatableBlockEntity, MenuProvider {

    public static final int INPUT_SLOT_COUNT = 3;

    public static final int CONTAINER_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    public static final int CONTAINER_SIZE = 5;

    private final NonNullList<ItemStack> items =
        NonNullList.withSize(
            CONTAINER_SIZE,
            ItemStack.EMPTY
        );

    private int cookTime = 0;
    private int cookTimeTotal = 200;

    private final RecipeManager.CachedCheck<
        CheeseVatRecipeInput,
        CheeseVatRecipe
    > quickCheck;

    private final ContainerData cheeseVatData;

    public CheeseVatBlockEntity(
        BlockPos pos,
        BlockState state
    ) {
        super(
            ModBlockEntities.CHEESE_VAT,
            pos,
            state
        );

        this.quickCheck =
            RecipeManager.createCheck(
                ModRecipes.CHEESE_VAT_TYPE
            );

        this.cheeseVatData = createContainerData();
    }

    public static void serverTick(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CheeseVatBlockEntity cheeseVat
    ) {
        if (!cheeseVat.isHeated(level, pos)) {
            cheeseVat.decreaseCookingProgress();
            return;
        }

        CheeseVatRecipeInput input =
            cheeseVat.createRecipeInput();

        Optional<RecipeHolder<CheeseVatRecipe>> recipe =
            cheeseVat.quickCheck.getRecipeFor(
                input,
                level
            );

        if (recipe.isEmpty()) {
            cheeseVat.decreaseCookingProgress();
            return;
        }

        CheeseVatRecipe cheeseVatRecipe =
            recipe.get().value();

        if (!cheeseVat.canCook(cheeseVatRecipe)) {
            cheeseVat.decreaseCookingProgress();
            return;
        }

        cheeseVat.cookTimeTotal =
            Math.max(
                1,
                cheeseVatRecipe.getCookingTime()
            );

        cheeseVat.cookTime++;

        if (cheeseVat.cookTime >= cheeseVat.cookTimeTotal) {
            cheeseVat.finishCooking(
                cheeseVatRecipe
            );
        }

        cheeseVat.setChanged();
    }

    private void decreaseCookingProgress() {
        if (cookTime > 0) {
            cookTime = Math.max(
                0,
                cookTime - 2
            );

            setChanged();
        }
    }

    private CheeseVatRecipeInput createRecipeInput() {
        return new CheeseVatRecipeInput(
            items.get(0),
            items.get(1),
            items.get(2),
            items.get(CONTAINER_SLOT)
        );
    }

    private boolean canCook(
        CheeseVatRecipe recipe
    ) {
        ItemStack result =
            recipe.assemble(
                createRecipeInput()
            );

        if (result.isEmpty()) {
            return false;
        }

        ItemStack output =
            items.get(OUTPUT_SLOT);

        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(
                output,
                result
            )) {
                return false;
            }

            if (
                output.getCount() + result.getCount()
                    > output.getMaxStackSize()
            ) {
                return false;
            }
        }

        if (recipe.getContainerTemplate().isPresent()) {

            ItemStack required =
                recipe
                    .getContainerTemplate()
                    .get()
                    .create();

            ItemStack provided =
                items.get(CONTAINER_SLOT);

            if (provided.isEmpty()) {
                return false;
            }

            if (!ItemStack.isSameItemSameComponents(
                provided,
                required
            )) {
                return false;
            }

            if (
                provided.getCount()
                    < required.getCount()
            ) {
                return false;
            }
        }

        return true;
    }

    private void finishCooking(
        CheeseVatRecipe recipe
    ) {
        CheeseVatRecipeInput input =
            createRecipeInput();

        ItemStack result =
            recipe.assemble(input);

        for (
            int i = 0;
            i < recipe.getIngredientsList().size();
            i++
        ) {
            consumeIngredient(i);
        }

        if (recipe.getContainerTemplate().isPresent()) {

            ItemStack required =
                recipe
                    .getContainerTemplate()
                    .get()
                    .create();

            ItemStack container =
                items.get(CONTAINER_SLOT);

            container.shrink(
                required.getCount()
            );
        }

        ItemStack output =
            items.get(OUTPUT_SLOT);

        if (output.isEmpty()) {
            items.set(
                OUTPUT_SLOT,
                result.copy()
            );
        } else {
            output.grow(
                result.getCount()
            );
        }

        cookTime = 0;

        setChanged();
    }

    private void consumeIngredient(int slot) {

        ItemStack stack =
            items.get(slot);

        if (stack.isEmpty()) {
            return;
        }

        ItemStackTemplate remainder =
            stack.getCraftingRemainder();

        stack.shrink(1);

        if (
            stack.isEmpty()
            && remainder != null
        ) {
            items.set(
                slot,
                remainder.create()
            );
        }
    }

    public boolean isHeated() {
        return level != null
            && isHeated(
                level,
                worldPosition
            );
    }

    private ContainerData createContainerData() {

        return new ContainerData() {

            @Override
            public int get(int index) {
                return switch (index) {

                    case 0 ->
                        CheeseVatBlockEntity.this.cookTime;

                    case 1 ->
                        CheeseVatBlockEntity.this.cookTimeTotal;

                    case 2 ->
                        CheeseVatBlockEntity.this.isHeated()
                            ? 1
                            : 0;

                    default -> 0;
                };
            }

            @Override
            public void set(
                int index,
                int value
            ) {
                switch (index) {

                    case 0 ->
                        CheeseVatBlockEntity.this.cookTime =
                            value;

                    case 1 ->
                        CheeseVatBlockEntity.this.cookTimeTotal =
                            value;

                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public ContainerData getCheeseVatData() {
        return cheeseVatData;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(
        int slot,
        int amount
    ) {
        ItemStack stack =
            ContainerHelper.removeItem(
                items,
                slot,
                amount
            );

        if (!stack.isEmpty()) {
            setChanged();
        }

        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(
        int slot
    ) {
        return ContainerHelper.takeItem(
            items,
            slot
        );
    }

    @Override
    public void setItem(
        int slot,
        ItemStack stack
    ) {
        items.set(
            slot,
            stack
        );

        if (
            stack.getCount()
                > getMaxStackSize()
        ) {
            stack.setCount(
                getMaxStackSize()
            );
        }

        setChanged();
    }

    @Override
    public boolean stillValid(
        Player player
    ) {
        return Container.stillValidBlockEntity(
            this,
            player
        );
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public boolean canPlaceItem(
        int slot,
        ItemStack stack
    ) {
        return slot != OUTPUT_SLOT;
    }

    @Override
    protected void loadAdditional(
        ValueInput input
    ) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(
            input,
            items
        );

        cookTime =
            input.getIntOr(
                "CookTime",
                0
            );

        cookTimeTotal =
            input.getIntOr(
                "CookTimeTotal",
                200
            );
    }

    @Override
    protected void saveAdditional(
        ValueOutput output
    ) {
        ContainerHelper.saveAllItems(
            output,
            items
        );

        output.putInt(
            "CookTime",
            cookTime
        );

        output.putInt(
            "CookTimeTotal",
            cookTimeTotal
        );

        super.saveAdditional(output);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(
            "block.italiansdelight.cheese_vat"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
        int containerId,
        Inventory inventory,
        Player player
    ) {
        return new CheeseVatMenu(
            containerId,
            inventory,
            this,
            cheeseVatData
        );
    }
}