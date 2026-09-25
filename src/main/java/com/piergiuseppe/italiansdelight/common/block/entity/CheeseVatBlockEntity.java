package com.piergiuseppe.italiansdelight.common.block.entity;

import java.util.Optional;

import com.piergiuseppe.italiansdelight.common.block.CheeseVatBlock;
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

/**
 * Logica server-side della Cheese Vat: inventario, ricerca ricette, cottura, contenitori, output e persistenza.
 */

public class CheeseVatBlockEntity
    extends BlockEntity
    implements Container, HeatableBlockEntity, MenuProvider {

    // -------------------- Layout dell'inventario --------------------
    // Gli slot 0-2 sono ingredienti; 3 è il contenitore; 4 è l'output.
    public static final int INPUT_SLOT_COUNT = 3;

    public static final int CONTAINER_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    // Only slots 0-4 contain real inventory items.
    public static final int CONTAINER_SIZE = OUTPUT_SLOT + 1;

    // These slots are persisted internally but must never be dropped as items.
    public static final int PREVIEW_SLOT = 5;
    private static final int PENDING_CONTAINER_SLOT = 6;
    private static final int INTERNAL_SLOT_COUNT = 7;

    // Lista interna completa. Gli slot tecnici oltre CONTAINER_SIZE non
    // vengono esposti al giocatore, ma servono a conservare risultati pendenti.
    private final NonNullList<ItemStack> items =
        NonNullList.withSize(
            INTERNAL_SLOT_COUNT,
            ItemStack.EMPTY
        );

    // -------------------- Stato della lavorazione --------------------
    // cookTime avanza soltanto quando esistono ricetta valida, calore e spazio.
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

    /**
     * Tick principale della macchina. L'ordine dei controlli è intenzionale:
     * prima si prova a confezionare un risultato già cotto, poi si verificano
     * calore, ricetta e possibilità di produrre l'output.
     */
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

    // Crea una vista dei soli tre slot ingredienti per il RecipeManager.
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
        return !items.get(PREVIEW_SLOT).isEmpty();
    }

    public ItemStack getPendingPreview() {
        return items.get(PREVIEW_SLOT).copy();
    }

    /**
     * Consuma gli ingredienti e conclude la ricetta. Se manca il contenitore
     * richiesto, il prodotto cotto resta nello slot preview interno finché
     * il giocatore non inserisce il contenitore corretto.
     */
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
            items.set(PREVIEW_SLOT, result.copy());
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

    // Completa un risultato pendente non appena compare il contenitore richiesto.
    private void tryFillContainer() {
        ItemStack pending = items.get(PREVIEW_SLOT);
        ItemStack required = items.get(PENDING_CONTAINER_SLOT);

        if (!hasRequiredContainer(required) || !canOutput(pending)) {
            return;
        }

        consumeContainer(required);
        addOutput(pending);
        items.set(PREVIEW_SLOT, ItemStack.EMPTY);
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

    /**
     * Consuma una singola unità di ingrediente. Eventuali crafting remainder
     * vengono espulsi lateralmente come nelle macchine di Farmer's Delight.
     */
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

        // Eject to the right of the vat, just like the Cooking Pot.
        Direction direction =
            getBlockState()
                .getValue(CheeseVatBlock.FACING)
                .getCounterClockWise();

        double x =
            worldPosition.getX()
                + 0.5D
                + direction.getStepX() * 0.25D;

        double y =
            worldPosition.getY()
                + 0.7D;

        double z =
            worldPosition.getZ()
                + 0.5D
                + direction.getStepZ() * 0.25D;

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

    // -------------------- Sincronizzazione menu/client --------------------
    // I tre interi sincronizzati sono: progresso, durata totale e stato calore.
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

    // -------------------- Implementazione Container --------------------
    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {

        for (int slot = 0; slot < CONTAINER_SIZE; slot++) {
            if (!items.get(slot).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < CONTAINER_SIZE
            ? items.get(slot)
            : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(
        int slot,
        int amount
    ) {
        if (slot >= PREVIEW_SLOT) {
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
        if (slot >= PREVIEW_SLOT) {
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
        if (slot >= PREVIEW_SLOT) {
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

    // -------------------- Persistenza dati del mondo --------------------
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

    // -------------------- Apertura del menu --------------------
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