package dev.italiansdelight.common.block.entity;

import java.util.Optional;

import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.entity.container.CheeseVatMenu;
import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.crafting.CheeseVatRecipeInput;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;

/**
 * Server-side Cheese Vat logic: inventory, recipe lookup, cooking, containers, output, and persistence.
 */

public class CheeseVatBlockEntity
    extends BlockEntity
    implements Container, HeatableBlockEntity, MenuProvider {

    // -------------------- Inventory layout --------------------
    // Slots 0-2 hold ingredients; 3 is the container slot; 4 is the output slot.
    public static final int INPUT_SLOT_COUNT = 3;

    public static final int CONTAINER_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;

    // Only slots 0-4 contain real inventory items.
    public static final int CONTAINER_SIZE = OUTPUT_SLOT + 1;

    // These slots are persisted internally. On break they travel inside the
    // dropped vat, never as a free serving or an unconsumed container.
    public static final int PREVIEW_SLOT = 5;
    private static final int PENDING_CONTAINER_SLOT = 6;
    private static final int INTERNAL_SLOT_COUNT = 7;

    // Complete internal list. Technical slots beyond CONTAINER_SIZE are not
    // exposed to the player; they are used to keep pending results.
    private final NonNullList<ItemStack> items =
        NonNullList.withSize(
            INTERNAL_SLOT_COUNT,
            ItemStack.EMPTY
        );

    // -------------------- Processing state --------------------
    // cookTime advances only when a valid recipe, heat, and output space are available.
    private int cookTime = 0;
    private int cookTimeTotal = 200;
    private ResourceKey<Recipe<?>> cookingRecipe;
    private int pendingBatchSize = 1;
    private float storedExperience;

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
     * Main machine tick. The order of checks is intentional:
     * first try to package an already cooked result, then verify heat,
     * recipe validity, and whether the output can be produced.
     */
    public static void serverTick(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CheeseVatBlockEntity cheeseVat
    ) {
        boolean heated = cheeseVat.isHeated(level, pos);
        if (state.getValue(CheeseVatBlock.HEATED) != heated) {
            level.setBlock(pos, state.setValue(CheeseVatBlock.HEATED, heated), Block.UPDATE_CLIENTS);
        }

        // Package existing servings first, then keep cooking while the buffer
        // has room. Output space is independent of unbottled/unbowled food.
        cheeseVat.tryFillContainer();

        CheeseVatRecipeInput input =
            cheeseVat.createRecipeInput();

        Optional<RecipeHolder<CheeseVatRecipe>> recipe =
            cheeseVat.quickCheck.getRecipeFor(
                input,
                level
            );

        if (recipe.isEmpty()) {
            cheeseVat.resetCookingProgress();
            return;
        }

        // Progress belongs to a recipe, including across a world restart.
        // Changing recipes must not reuse time spent on the previous product.
        if (!recipe.get().id().equals(cheeseVat.cookingRecipe)) {
            cheeseVat.cookingRecipe = recipe.get().id();
            cheeseVat.cookTime = 0;
            cheeseVat.setChanged();
        }

        CheeseVatRecipe cheeseVatRecipe =
            recipe.get().value();

        if (!heated || !cheeseVat.canCook(cheeseVatRecipe)) {
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

    private void resetCookingProgress() {
        if (cookTime != 0 || cookingRecipe != null) {
            cookTime = 0;
            cookingRecipe = null;
            setChanged();
        }
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

    // Creates a view containing only the three ingredient slots for the RecipeManager.
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
        ItemStack result = recipe.assemble(createRecipeInput());
        if (result.isEmpty() || result.getCount() > result.getMaxStackSize()) {
            return false;
        }

        ItemStack required = recipe.getContainerTemplate()
            .map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
        if (required.isEmpty()) {
            return !hasPendingResult() && canOutput(result);
        }
        if (!hasPendingResult()) {
            return true;
        }

        ItemStack pending = items.get(PREVIEW_SLOT);
        ItemStack pendingContainer = items.get(PENDING_CONTAINER_SLOT);
        return ItemStack.isSameItemSameComponents(pending, result)
            && ItemStack.isSameItemSameComponents(pendingContainer, required)
            && pendingContainer.getCount() == required.getCount()
            && pendingBatchSize == result.getCount()
            && pending.getCount() + result.getCount() <= pending.getMaxStackSize();
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty() || result.getCount() > result.getMaxStackSize()) {
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
     * Keep only the unpackaged serving inside the dropped vat. Vanilla drops
     * the five real Container slots separately in preRemoveSideEffects.
     * Replacing the vat restores these hidden slots through BlockItem's NBT
     * handling, so the player still has to supply the missing container.
     */
    public void preservePendingResult(ItemStack vatStack, HolderLookup.Provider registries) {
        if (!hasPendingResult()) {
            return;
        }

        NonNullList<ItemStack> pendingItems =
            NonNullList.withSize(INTERNAL_SLOT_COUNT, ItemStack.EMPTY);
        pendingItems.set(PREVIEW_SLOT, items.get(PREVIEW_SLOT).copy());
        pendingItems.set(PENDING_CONTAINER_SLOT, items.get(PENDING_CONTAINER_SLOT).copy());

        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        ContainerHelper.saveAllItems(output, pendingItems);
        output.putInt("PendingBatchSize", pendingBatchSize);
        BlockItem.setBlockEntityData(vatStack, getType(), output);
    }

    /**
     * Consumes the ingredients and completes the recipe. If the required
     * container is missing, the cooked result stays in the internal preview
     * slot until the player inserts the correct container.
     */
    private void finishCooking(CheeseVatRecipe recipe) {
        CheeseVatRecipeInput input = createRecipeInput();
        ItemStack result = recipe.assemble(input);
        int[] matchingSlots =
            recipe.findMatchingIngredientSlots(input);

        if (matchingSlots == null || !canCook(recipe)) {
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

        if (required.isEmpty()) {
            addOutput(result);
        } else {
            if (hasPendingResult()) {
                items.get(PREVIEW_SLOT).grow(result.getCount());
            } else {
                items.set(PREVIEW_SLOT, result.copy());
                items.set(PENDING_CONTAINER_SLOT, required.copy());
                pendingBatchSize = result.getCount();
            }
            tryFillContainer();
        }

        storedExperience += recipe.getExperience();
        cookTime = 0;
        setChanged();
    }

    // Completes a pending result as soon as the required container becomes available.
    private void tryFillContainer() {
        while (hasPendingResult()) {
            ItemStack pending = items.get(PREVIEW_SLOT);
            ItemStack required = items.get(PENDING_CONTAINER_SLOT);
            ItemStack serving = pending.copyWithCount(pendingBatchSize);
            if (pending.getCount() < pendingBatchSize || !hasRequiredContainer(required)
                || !canOutput(serving)) {
                return;
            }
            consumeContainer(required);
            addOutput(removePendingServing());
            setChanged();
        }
    }

    private ItemStack removePendingServing() {
        ItemStack serving = items.get(PREVIEW_SLOT).split(pendingBatchSize);
        if (!hasPendingResult()) {
            items.set(PREVIEW_SLOT, ItemStack.EMPTY);
            items.set(PENDING_CONTAINER_SLOT, ItemStack.EMPTY);
            pendingBatchSize = 1;
        }
        return serving;
    }

    /** Collect a single recipe batch with the container held in either hand. */
    public ItemStack takeServing(ItemStack held, Player player) {
        if (!hasPendingResult() || items.get(PREVIEW_SLOT).getCount() < pendingBatchSize) {
            return ItemStack.EMPTY;
        }
        ItemStack required = items.get(PENDING_CONTAINER_SLOT);
        if (required.isEmpty() || !ItemStack.isSameItemSameComponents(held, required)
            || held.getCount() < required.getCount()) {
            return ItemStack.EMPTY;
        }
        held.consume(required.getCount(), player);
        ItemStack serving = removePendingServing();
        awardExperience();
        setChanged();
        return serving;
    }

    /** Like a furnace, accumulate recipe XP until collection or block removal. */
    public void awardExperience() {
        if (level instanceof ServerLevel serverLevel && storedExperience > 0.0F) {
            int amount = (int) storedExperience;
            if (serverLevel.random.nextFloat() < storedExperience - amount) {
                amount++;
            }
            storedExperience = 0.0F;
            if (amount > 0) {
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(worldPosition), amount);
            }
            setChanged();
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        awardExperience();
        super.preRemoveSideEffects(pos, state);
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
     * Consumes a single ingredient unit. Any crafting remainder is ejected
     * sideways, matching Farmer's Delight machine behavior.
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
        } else if (stack.is(Items.POTION)) {
            // Vanilla potions have a drinking remainder, not a crafting one.
            ejectIngredientRemainder(new ItemStack(Items.GLASS_BOTTLE));
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

    // -------------------- Menu/client synchronization --------------------
    // The three synchronized integers are: progress, total duration, and heat state.
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

    // -------------------- Container implementation --------------------
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
        pendingBatchSize = 1;
        storedExperience = 0.0F;
        resetCookingProgress();
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

    // -------------------- World data persistence --------------------
    @Override
    protected void loadAdditional(
        ValueInput input
    ) {
        super.loadAdditional(input);

        items.clear();
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

        cookingRecipe = input.read("CookingRecipe", Recipe.KEY_CODEC).orElse(null);
        // Legacy saves held one whole batch in the preview.
        pendingBatchSize = Math.max(1, input.getIntOr("PendingBatchSize",
            items.get(PREVIEW_SLOT).getCount()));
        storedExperience = Math.max(0.0F, input.getFloatOr("StoredExperience", 0.0F));
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

        output.storeNullable("CookingRecipe", Recipe.KEY_CODEC, cookingRecipe);
        output.putInt("PendingBatchSize", pendingBatchSize);
        output.putFloat("StoredExperience", storedExperience);

        super.saveAdditional(output);
    }

    // -------------------- Menu opening --------------------
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
