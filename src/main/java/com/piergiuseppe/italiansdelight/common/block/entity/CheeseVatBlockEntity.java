package com.piergiuseppe.italiansdelight.common.block.entity;

import java.util.Optional;

import com.piergiuseppe.italiansdelight.common.block.entity.container.CheeseVatMenu;
import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipe;
import com.piergiuseppe.italiansdelight.common.crafting.CheeseVatRecipeInput;
import com.piergiuseppe.italiansdelight.common.registry.ModBlockEntities;
import com.piergiuseppe.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import vectorwing.farmersdelight.common.utility.ItemUtils;

public class CheeseVatBlockEntity
    extends BlockEntity
    implements Container, HeatableBlockEntity, MenuProvider {

    public static final int INPUT_SLOT_COUNT = 3;

    public static final int CONTAINER_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    // Internal slots keep cooked food safe while it waits for its container.
    private static final int PENDING_RESULT_SLOT = 5;
    private static final int PENDING_CONTAINER_SLOT = 6;

    public static final int CONTAINER_SIZE = 7;

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
        // Packaging a cooked serving does not need heat or recipe inputs.
        if (cheeseVat.hasPendingResult()) {
            cheeseVat.tryFillContainer();
            return;
        }

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

        if (cheeseVat.cookTime < cheeseVat.cookTimeTotal) {
            cheeseVat.cookTime++;
        }

        if (cheeseVat.cookTime >= cheeseVat.cookTimeTotal) {
            cheeseVat.finishCooking(cheeseVatRecipe);
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
            items.get(2)
        );
    }

    private boolean canCook(
        CheeseVatRecipe recipe
    ) {
        return canOutput(
            recipe.assemble(createRecipeInput())
        );
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }

        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(output, result)
            && output.getCount() + result.getCount()
                <= output.getMaxStackSize();
    }

    private boolean hasRequiredContainer(ItemStack required) {
        ItemStack provided = items.get(CONTAINER_SLOT);

        return !provided.isEmpty()
            && ItemStack.isSameItemSameComponents(provided, required)
            && provided.getCount() >= required.getCount();
    }

    public boolean hasPendingResult() {
        return !items.get(PENDING_RESULT_SLOT).isEmpty();
    }

    public boolean isPreviewVisible() {
        return hasPendingResult() && items.get(OUTPUT_SLOT).isEmpty();
    }

    public ItemStack getPendingResult() {
        return items.get(PENDING_RESULT_SLOT);
    }

    private void finishCooking(CheeseVatRecipe recipe) {
        CheeseVatRecipeInput input = createRecipeInput();
        ItemStack result = recipe.assemble(input);
        int[] matchingSlots =
            recipe.findMatchingIngredientSlots(input);

        if (matchingSlots == null || !canOutput(result)) {
            cookTime = 0;
            setChanged();
            return;
        }

        ItemStack required = recipe.getContainerTemplate()
            .map(ItemStackTemplate::create)
            .orElse(ItemStack.EMPTY);

        for (int slot : matchingSlots) {
            consumeIngredient(slot);
        }

        if (!required.isEmpty() && !hasRequiredContainer(required)) {
            items.set(PENDING_RESULT_SLOT, result.copy());
            items.set(PENDING_CONTAINER_SLOT, required.copy());
        } else {
            if (!required.isEmpty()) {
                consumeContainer(required);
            }
            addOutput(result);
        }

        cookTime = 0;
        setChanged();
    }

    private void tryFillContainer() {
        ItemStack pending = items.get(PENDING_RESULT_SLOT);
        ItemStack required = items.get(PENDING_CONTAINER_SLOT);

        if (!hasRequiredContainer(required) || !canOutput(pending)) {
            return;
        }

        consumeContainer(required);
        addOutput(pending);
        items.set(PENDING_RESULT_SLOT, ItemStack.EMPTY);
        items.set(PENDING_CONTAINER_SLOT, ItemStack.EMPTY);
        setChanged();
    }

    private void consumeContainer(ItemStack required) {
        ItemStack container = items.get(CONTAINER_SLOT);
        container.shrink(required.getCount());

        if (container.isEmpty()) {
            items.set(CONTAINER_SLOT, ItemStack.EMPTY);
        }
    }

    private void addOutput(ItemStack result) {
        ItemStack output = items.get(OUTPUT_SLOT);

        if (output.isEmpty()) {
            items.set(OUTPUT_SLOT, result.copy());
        } else {
            output.grow(result.getCount());
        }
    }

    private void consumeIngredient(int slot) {

        ItemStack stack =
            items.get(slot);

        if (stack.isEmpty()) {
            return;
        }

        ItemStackTemplate remainder =
            stack.getCraftingRemainder();

        // Farmer's Delight behavior:
        // the remainder is ejected from the machine instead of
        // replacing the ingredient stack in the input slot.
        if (remainder != null) {
            ItemStack remainderStack =
                remainder.create();

            if (!remainderStack.isEmpty()) {
                ejectIngredientRemainder(
                    remainderStack
                );
            }
        }

        stack.shrink(1);

        if (stack.isEmpty()) {
            items.set(
                slot,
                ItemStack.EMPTY
            );
        }
    }

    private void ejectIngredientRemainder(
        ItemStack remainderStack
    ) {
        if (
            level == null
            || remainderStack.isEmpty()
        ) {
            return;
        }

        // Cheese Vat does not currently have a FACING block state,
        // so remainders are consistently ejected from its north side.
        // This mirrors Farmer's Delight's physical ejection behavior
        // without changing the blockstate/model yet.
        Direction direction =
            Direction.NORTH;

        double x =
            worldPosition.getX()
                + 0.5D
                + direction.getStepX() * 0.35D;

        double y =
            worldPosition.getY()
                + 0.7D;

        double z =
            worldPosition.getZ()
                + 0.5D
                + direction.getStepZ() * 0.35D;

        ItemUtils.spawnItemEntity(
            level,
            remainderStack,
            x,
            y,
            z,
            direction.getStepX() * 0.08D,
            0.25D,
            direction.getStepZ() * 0.08D
        );
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

                    case 3 ->
                        CheeseVatBlockEntity.this.isPreviewVisible()
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
                return 4;
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
        if (slot >= PENDING_RESULT_SLOT) {
            return ItemStack.EMPTY;
        }

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
        if (slot >= PENDING_RESULT_SLOT) {
            return ItemStack.EMPTY;
        }

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
        if (slot >= PENDING_RESULT_SLOT) {
            return;
        }

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
        return slot >= 0
            && slot <= CONTAINER_SLOT;
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