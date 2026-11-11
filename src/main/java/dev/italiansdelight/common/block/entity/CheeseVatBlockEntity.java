package dev.italiansdelight.common.block.entity;

import java.util.Optional;

import dev.italiansdelight.common.block.CheeseVatBlock;
import dev.italiansdelight.common.block.entity.container.CheeseVatMenu;
import dev.italiansdelight.common.crafting.CheeseVatRecipe;
import dev.italiansdelight.common.crafting.CheeseVatRecipeInput;
import dev.italiansdelight.common.registry.ModBlockEntities;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.utility.ItemUtils;

/**
 * Server-side Cheese Vat logic: inventory, recipe lookup, cooking, containers, output, and persistence.
 */

public class CheeseVatBlockEntity
    extends BlockEntity
    implements WorldlyContainer, HeatableBlockEntity, MenuProvider {

    // -------------------- Inventory layout --------------------
    // Slots 0-2 hold ingredients; 3 is the recipe container; 4 is the
    // finished food output; 5 is the dedicated whey-container slot.
    public static final int INPUT_SLOT_COUNT = 3;

    public static final int CONTAINER_SLOT = 3;
    public static final int OUTPUT_SLOT = 4;
    public static final int WHEY_CONTAINER_SLOT = 5;

    // Vanilla sided-inventory contract used by hoppers.
    // Top: ingredients. Horizontal faces: recipe container. Bottom: outputs.
    private static final int[] TOP_HOPPER_SLOTS = {
        0,
        1,
        2
    };

    private static final int[] SIDE_HOPPER_SLOTS = {
        CONTAINER_SLOT
    };

    private static final int[] BOTTOM_HOPPER_SLOTS = {
        OUTPUT_SLOT,
        WHEY_CONTAINER_SLOT
    };

    // Slots 0-5 are real inventory slots exposed to the player.
    public static final int CONTAINER_SIZE = WHEY_CONTAINER_SLOT + 1;

    // Internal whey tank. It can only be filled by recipes that produce whey.
    public static final int WHEY_TANK_CAPACITY = 4000;

    // These slots are persisted internally. On break they travel inside the
    // dropped vat, never as a free serving or an unconsumed container.
    public static final int PREVIEW_SLOT = 6;
    private static final int PENDING_CONTAINER_SLOT = 7;
    private static final int INTERNAL_SLOT_COUNT = 8;

    // Save-layout marker used to migrate worlds created before the dedicated
    // whey-container slot was introduced.
    private static final int WHEY_SLOT_LAYOUT_VERSION = 1;

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

    // Controls only automatic whey consumption from the internal tank.
    private boolean wheyFlowEnabled = true;

    // Stored independently from inventory slots and lost when the vat is broken.
    private int wheyAmount = 0;

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

        // Package existing servings and fill the dedicated whey-container
        // slot before recipe lookup. These operations also work while the vat
        // has no active cooking recipe.
        cheeseVat.tryFillContainer();
        cheeseVat.tryFillWheyContainer(level);

        CheeseVatRecipeInput input =
            cheeseVat.createRecipeInput();

        Optional<RecipeHolder<CheeseVatRecipe>> recipe =
            cheeseVat.findRecipe(
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

        if (!heated || !cheeseVat.canCook(cheeseVatRecipe, input)) {
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

    private static final int NO_WHEY_ITEM = -1;
    private static final int AMBIGUOUS_WHEY_ITEMS = -2;

    /**
     * Whey-aware recipes get priority over normal recipes. This is what makes
     * Milk + Bowl select Ricotta while whey is available and fall back to
     * Cream when tank flow is disabled or the resource is unavailable.
     */
    private Optional<RecipeHolder<CheeseVatRecipe>> findRecipe(
        CheeseVatRecipeInput input,
        ServerLevel level
    ) {
        // Cheese Vat recipes always contain at least one ingredient. Avoid
        // scanning the global recipe collection while an idle vat is empty.
        if (
            input.first().isEmpty()
            && input.second().isEmpty()
            && input.third().isEmpty()
        ) {
            return Optional.empty();
        }

        RecipeManager recipeManager =
            level.getServer()
                .getRecipeManager();

        Optional<RecipeHolder<CheeseVatRecipe>> wheyRecipe =
            findWheyRecipe(
                input,
                recipeManager
            );

        if (wheyRecipe.isPresent()) {
            return wheyRecipe;
        }

        return findNormalRecipe(
            input,
            recipeManager
        );
    }

    /**
     * Resolves normal (non-whey) recipes deterministically.
     *
     * Exact container matches have the highest priority, which allows recipes
     * with identical ingredients to use the container slot as their selector.
     * If no container selects a recipe, a container-less recipe wins. Finally,
     * a recipe that merely needs a container can still start cooking without
     * it and keep its finished serving pending, preserving the existing vat
     * behavior for bowls and bottles.
     */
    private Optional<RecipeHolder<CheeseVatRecipe>>
    findNormalRecipe(
        CheeseVatRecipeInput input,
        RecipeManager recipeManager
    ) {
        ItemStack provided =
            items.get(CONTAINER_SLOT);

        Optional<RecipeHolder<CheeseVatRecipe>>
            currentRecipe =
                findCurrentNormalRecipe(
                    input,
                    recipeManager,
                    provided
                );

        if (currentRecipe.isPresent()) {
            return currentRecipe;
        }

        Optional<RecipeHolder<CheeseVatRecipe>>
            withoutContainer =
                Optional.empty();

        Optional<RecipeHolder<CheeseVatRecipe>>
            withContainer =
                Optional.empty();

        for (
            RecipeHolder<?> holder :
            recipeManager.getRecipes()
        ) {
            if (
                !(holder.value() instanceof CheeseVatRecipe recipe)
                || recipe.getType() != ModRecipes.CHEESE_VAT_TYPE
                || recipe.getWheyAmount() > 0
                || recipe.findMatchingIngredientSlots(input) == null
            ) {
                continue;
            }

            if (recipe.getContainerTemplate().isEmpty()) {
                if (withoutContainer.isEmpty()) {
                    withoutContainer =
                        Optional.of(
                            castCheeseVatRecipeHolder(
                                holder
                            )
                        );
                }

                continue;
            }

            ItemStack required =
                recipe.getContainerTemplate()
                    .orElseThrow()
                    .create();

            if (
                !provided.isEmpty()
                && ItemStack.isSameItemSameComponents(
                    provided,
                    required
                )
                && provided.getCount() >= required.getCount()
            ) {
                return Optional.of(
                    castCheeseVatRecipeHolder(
                        holder
                    )
                );
            }

            if (withContainer.isEmpty()) {
                withContainer =
                    Optional.of(
                        castCheeseVatRecipeHolder(
                            holder
                        )
                    );
            }
        }

        return withoutContainer.isPresent()
            ? withoutContainer
            : withContainer;
    }

    /**
     * Fast path for the recipe already being cooked.
     *
     * During steady-state cooking the input normally does not change, so there
     * is no reason to scan every loaded recipe on every server tick. We only
     * reuse the hint when doing so cannot bypass container-selection priority.
     */
    private Optional<RecipeHolder<CheeseVatRecipe>>
    findCurrentNormalRecipe(
        CheeseVatRecipeInput input,
        RecipeManager recipeManager,
        ItemStack provided
    ) {
        if (cookingRecipe == null) {
            return Optional.empty();
        }

        Optional<RecipeHolder<?>> hinted =
            recipeManager.byKey(
                cookingRecipe
            );

        if (
            hinted.isEmpty()
            || !(hinted.get().value()
                instanceof CheeseVatRecipe recipe)
            || recipe.getType()
                != ModRecipes.CHEESE_VAT_TYPE
            || recipe.getWheyAmount() > 0
            || recipe.findMatchingIngredientSlots(input)
                == null
        ) {
            return Optional.empty();
        }

        if (recipe.getContainerTemplate().isEmpty()) {
            // A newly inserted container may intentionally select another
            // otherwise-identical recipe, so re-run the full selection then.
            return provided.isEmpty()
                ? Optional.of(
                    castCheeseVatRecipeHolder(
                        hinted.get()
                    )
                )
                : Optional.empty();
        }

        // Recipes that require a container are allowed to keep cooking while
        // the container slot is empty; the finished serving simply waits.
        if (provided.isEmpty()) {
            return Optional.of(
                castCheeseVatRecipeHolder(
                    hinted.get()
                )
            );
        }

        ItemStack required =
            recipe.getContainerTemplate()
                .orElseThrow()
                .create();

        if (
            ItemStack.isSameItemSameComponents(
                provided,
                required
            )
            && provided.getCount()
                >= required.getCount()
        ) {
            return Optional.of(
                castCheeseVatRecipeHolder(
                    hinted.get()
                )
            );
        }

        return Optional.empty();
    }

    private Optional<RecipeHolder<CheeseVatRecipe>> findWheyRecipe(
        CheeseVatRecipeInput input,
        RecipeManager recipeManager
    ) {
        int wheyItemSlot =
            findWheyItemSlot(
                input
            );

        if (wheyItemSlot == AMBIGUOUS_WHEY_ITEMS) {
            return Optional.empty();
        }

        // Without an explicit whey item, a disabled/empty tank cannot satisfy
        // any whey-aware recipe, so avoid a global recipe scan entirely.
        if (
            wheyItemSlot == NO_WHEY_ITEM
            && (
                !wheyFlowEnabled
                || wheyAmount <= 0
            )
        ) {
            return Optional.empty();
        }

        // Preserve the recipe already in progress when it is still valid.
        if (cookingRecipe != null) {
            Optional<RecipeHolder<?>> hinted =
                recipeManager.byKey(
                    cookingRecipe
                );

            if (
                hinted.isPresent()
                && hinted.get().value() instanceof CheeseVatRecipe recipe
                && recipe.getType() == ModRecipes.CHEESE_VAT_TYPE
                && recipe.getWheyAmount() > 0
                && canSupplyWhey(
                    recipe,
                    input,
                    wheyItemSlot
                )
            ) {
                return Optional.of(
                    castCheeseVatRecipeHolder(
                        hinted.get()
                    )
                );
            }
        }

        for (
            RecipeHolder<?> holder :
            recipeManager.getRecipes()
        ) {
            if (
                holder.value() instanceof CheeseVatRecipe recipe
                && recipe.getType() == ModRecipes.CHEESE_VAT_TYPE
                && recipe.getWheyAmount() > 0
                && canSupplyWhey(
                    recipe,
                    input,
                    wheyItemSlot
                )
            ) {
                return Optional.of(
                    castCheeseVatRecipeHolder(
                        holder
                    )
                );
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<CheeseVatRecipe>
    castCheeseVatRecipeHolder(
        RecipeHolder<?> holder
    ) {
        return (RecipeHolder<CheeseVatRecipe>)
            (RecipeHolder<?>) holder;
    }

    /**
     * Returns the single explicit whey source slot, NO_WHEY_ITEM when the
     * machine should try the tank, or AMBIGUOUS_WHEY_ITEMS when bottle and
     * bucket sources occupy different ingredient slots simultaneously.
     */
    private int findWheyItemSlot(
        CheeseVatRecipeInput input
    ) {
        int foundSlot =
            NO_WHEY_ITEM;

        for (
            int slot = 0;
            slot < INPUT_SLOT_COUNT;
            slot++
        ) {
            ItemStack stack =
                input.getItem(
                    slot
                );

            if (
                !stack.is(ModItems.WHEY_BOTTLE)
                && !stack.is(ModItems.WHEY_BUCKET)
            ) {
                continue;
            }

            if (foundSlot != NO_WHEY_ITEM) {
                return AMBIGUOUS_WHEY_ITEMS;
            }

            foundSlot = slot;
        }

        return foundSlot;
    }

    private boolean canSupplyWhey(
        CheeseVatRecipe recipe,
        CheeseVatRecipeInput input
    ) {
        return canSupplyWhey(
            recipe,
            input,
            findWheyItemSlot(input)
        );
    }

    private boolean canSupplyWhey(
        CheeseVatRecipe recipe,
        CheeseVatRecipeInput input,
        int wheyItemSlot
    ) {
        if (recipe.getWheyAmount() <= 0) {
            return recipe.findMatchingIngredientSlots(
                input
            ) != null;
        }

        if (wheyItemSlot == AMBIGUOUS_WHEY_ITEMS) {
            return false;
        }

        if (wheyItemSlot >= 0) {
            return recipe.findMatchingIngredientSlots(
                input,
                wheyItemSlot
            ) != null;
        }

        return wheyFlowEnabled
            && wheyAmount >= recipe.getWheyAmount()
            && recipe.findMatchingIngredientSlots(
                input
            ) != null;
    }

    private boolean canCook(
        CheeseVatRecipe recipe,
        CheeseVatRecipeInput input
    ) {

        if (
            recipe.getWheyAmount() > 0
            && !canSupplyWhey(
                recipe,
                input
            )
        ) {
            return false;
        }

        ItemStack result = recipe.assemble(input);
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
        output.putInt("WheySlotLayout", WHEY_SLOT_LAYOUT_VERSION);
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

        int wheyItemSlot =
            recipe.getWheyAmount() > 0
                ? findWheyItemSlot(input)
                : NO_WHEY_ITEM;

        if (wheyItemSlot == AMBIGUOUS_WHEY_ITEMS) {
            cookTime = 0;
            setChanged();
            return;
        }

        int[] matchingSlots =
            wheyItemSlot >= 0
                ? recipe.findMatchingIngredientSlots(
                    input,
                    wheyItemSlot
                )
                : recipe.findMatchingIngredientSlots(
                    input
                );

        if (
            matchingSlots == null
            || !canCook(recipe, input)
        ) {
            cookTime = 0;
            setChanged();
            return;
        }

        // A whey-producing recipe may finish only when the whole by-product
        // fits. Keep the completed progress waiting instead of consuming inputs.
        if (!canStoreProducedWhey(recipe.getWheyOutput())) {
            cookTime = cookTimeTotal;
            setChanged();
            return;
        }

        ItemStack required = recipe.getContainerTemplate()
            .map(ItemStackTemplate::create)
            .orElse(ItemStack.EMPTY);

        for (int slot : matchingSlots) {
            consumeIngredient(slot);
        }

        if (recipe.getWheyAmount() > 0) {
            if (wheyItemSlot >= 0) {
                // Explicit item source always wins and returns its normal
                // crafting remainder through consumeIngredient().
                consumeIngredient(
                    wheyItemSlot
                );
            } else {
                // Tank consumption is exact and happens only when the batch
                // actually completes.
                wheyAmount -=
                    recipe.getWheyAmount();
            }
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

        addProducedWhey(recipe.getWheyOutput());

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
            if (serverLevel.getRandom().nextFloat() < storedExperience - amount) {
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
        if (level instanceof ServerLevel serverLevel) {
            // Vanilla terrain particles are disabled for the vat itself so we
            // can emit a smaller burst. Use vanilla cauldron particles to keep
            // the normal dark metal color instead of the heated inner color.
            serverLevel.sendParticles(
                new BlockParticleOption(
                    ParticleTypes.BLOCK,
                    Blocks.CAULDRON.defaultBlockState()
                ),
                pos.getX() + 0.5D,
                pos.getY() + 0.55D,
                pos.getZ() + 0.5D,
                10,
                0.35D,
                0.30D,
                0.35D,
                0.08D
            );
        }

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
                + direction.getStepX() * 0.85D;

        double y =
            worldPosition.getY()
                + 0.7D;

        double z =
            worldPosition.getZ()
                + 0.5D
                + direction.getStepZ() * 0.85D;

        ItemUtils.spawnItemEntity(
            level,
            remainderStack,
            x,
            y,
            z,
            direction.getStepX() * 0.16D,
            0.18D,
            direction.getStepZ() * 0.16D
        );
    }

    public boolean isHeated() {
        return level != null
            && isHeated(
                level,
                worldPosition
            );
    }

    public boolean isWheyFlowEnabled() {
        return wheyFlowEnabled;
    }

    public int getWheyAmount() {
        return wheyAmount;
    }

    public int getWheyCapacity() {
        return WHEY_TANK_CAPACITY;
    }

    private boolean canStoreProducedWhey(int amount) {
        if (amount <= 0) {
            return true;
        }

        return amount <= WHEY_TANK_CAPACITY - wheyAmount;
    }

    private void addProducedWhey(int amount) {
        if (amount <= 0) {
            return;
        }

        wheyAmount = Math.min(
            WHEY_TANK_CAPACITY,
            wheyAmount + amount
        );
    }

    /**
     * Removes an exact amount from the tank. External containers may only
     * extract whey; they can never insert it back into the machine.
     */
    public boolean extractWhey(int amount) {
        if (amount <= 0 || wheyAmount < amount) {
            return false;
        }

        wheyAmount -= amount;
        setChanged();
        return true;
    }

    /**
     * Automatically fills the single empty container placed in the slot below
     * the tank. Filled containers remain there until the player removes them.
     */
    private void tryFillWheyContainer(ServerLevel level) {
        ItemStack container = items.get(WHEY_CONTAINER_SLOT);

        if (
            container.is(Items.GLASS_BOTTLE)
            && wheyAmount >= 250
        ) {
            wheyAmount -= 250;
            items.set(
                WHEY_CONTAINER_SLOT,
                new ItemStack(ModItems.WHEY_BOTTLE)
            );

            level.playSound(
                null,
                worldPosition,
                SoundEvents.BOTTLE_FILL,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
            );

            level.gameEvent(
                null,
                GameEvent.FLUID_PICKUP,
                worldPosition
            );

            setChanged();
            return;
        }

        if (
            container.is(Items.BUCKET)
            && wheyAmount >= 1000
        ) {
            wheyAmount -= 1000;
            items.set(
                WHEY_CONTAINER_SLOT,
                new ItemStack(ModItems.WHEY_BUCKET)
            );

            level.playSound(
                null,
                worldPosition,
                SoundEvents.BUCKET_FILL,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
            );

            level.gameEvent(
                null,
                GameEvent.FLUID_PICKUP,
                worldPosition
            );

            setChanged();
        }
    }

    public void toggleWheyFlow() {
        wheyFlowEnabled = !wheyFlowEnabled;
        setChanged();
    }

    // -------------------- Menu/client synchronization --------------------
    // Values: progress, total duration, heat state, whey-flow toggle, and whey amount.
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
                        CheeseVatBlockEntity.this.getBlockState()
                            .getValue(CheeseVatBlock.HEATED)
                            ? 1
                            : 0;

                    case 3 ->
                        CheeseVatBlockEntity.this.wheyFlowEnabled
                            ? 1
                            : 0;

                    case 4 ->
                        CheeseVatBlockEntity.this.wheyAmount;

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

                    case 3 ->
                        CheeseVatBlockEntity.this.wheyFlowEnabled =
                            value != 0;

                    case 4 ->
                        CheeseVatBlockEntity.this.wheyAmount =
                            Math.max(
                                0,
                                Math.min(
                                    WHEY_TANK_CAPACITY,
                                    value
                                )
                            );

                    default -> {
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
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

        int maxStackSize =
            slot == WHEY_CONTAINER_SLOT
                ? 1
                : getMaxStackSize();

        if (stack.getCount() > maxStackSize) {
            stack.setCount(maxStackSize);
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
        if (slot >= 0 && slot <= CONTAINER_SLOT) {
            return true;
        }

        return slot == WHEY_CONTAINER_SLOT
            && (
                stack.is(Items.GLASS_BOTTLE)
                || stack.is(Items.BUCKET)
            );
    }

    @Override
    public int[] getSlotsForFace(
        Direction side
    ) {
        if (side == Direction.UP) {
            return TOP_HOPPER_SLOTS;
        }

        if (side == Direction.DOWN) {
            return BOTTOM_HOPPER_SLOTS;
        }

        return SIDE_HOPPER_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(
        int slot,
        ItemStack stack,
        Direction side
    ) {
        if (side == null) {
            return canPlaceItem(
                slot,
                stack
            );
        }

        if (side == Direction.UP) {
            return slot >= 0
                && slot < INPUT_SLOT_COUNT
                && canPlaceItem(
                    slot,
                    stack
                );
        }

        if (side.getAxis().isHorizontal()) {
            return slot == CONTAINER_SLOT
                && isRecipeContainerForAutomation(
                    stack
                );
        }

        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(
        int slot,
        ItemStack stack,
        Direction side
    ) {
        if (side != Direction.DOWN) {
            return false;
        }

        if (slot == OUTPUT_SLOT) {
            return true;
        }

        return slot == WHEY_CONTAINER_SLOT
            && (
                stack.is(ModItems.WHEY_BOTTLE)
                || stack.is(ModItems.WHEY_BUCKET)
            );
    }

    private static boolean isRecipeContainerForAutomation(
        ItemStack stack
    ) {
        // These are the container-slot items currently used by Cheese Vat
        // recipes. Keep the side hopper restricted so unrelated items cannot
        // jam the recipe-container slot.
        return stack.is(Items.BOWL)
            || stack.is(Items.GLASS_BOTTLE)
            || stack.is(Items.LEAD);
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

        // Before the whey-container slot existed, the hidden preview and its
        // pending container occupied slots 5 and 6. Move those legacy values
        // to 6 and 7 exactly once so old vats keep their pending servings.
        if (
            input.getIntOr(
                "WheySlotLayout",
                0
            ) < WHEY_SLOT_LAYOUT_VERSION
        ) {
            ItemStack legacyPreview =
                items.get(5);

            ItemStack legacyPendingContainer =
                items.get(6);

            items.set(
                PENDING_CONTAINER_SLOT,
                legacyPendingContainer
            );

            items.set(
                PREVIEW_SLOT,
                legacyPreview
            );

            items.set(
                WHEY_CONTAINER_SLOT,
                ItemStack.EMPTY
            );
        }

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
        wheyFlowEnabled = input.getBooleanOr("WheyFlowEnabled", true);
        wheyAmount = Math.max(
            0,
            Math.min(
                WHEY_TANK_CAPACITY,
                input.getIntOr("WheyAmount", 0)
            )
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

        output.storeNullable("CookingRecipe", Recipe.KEY_CODEC, cookingRecipe);
        output.putInt("PendingBatchSize", pendingBatchSize);
        output.putFloat("StoredExperience", storedExperience);
        output.putBoolean("WheyFlowEnabled", wheyFlowEnabled);
        output.putInt("WheyAmount", wheyAmount);
        output.putInt("WheySlotLayout", WHEY_SLOT_LAYOUT_VERSION);

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
