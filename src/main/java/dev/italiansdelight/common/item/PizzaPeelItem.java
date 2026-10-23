package dev.italiansdelight.common.item;

import dev.italiansdelight.common.pizza.PizzaPeelCargo;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.registry.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;
import vectorwing.farmersdelight.common.item.SkilletItem;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Reusable, nonstackable pizza transport tool.
 *
 * <p>When empty, the peel uses the EXACT combat attribute definition of
 * Farmer's Delight Refabricated 26.2's Skillet (iron material, 5 attack
 * damage before the material bonus, -3.1 attack speed, +1 knockback).
 * When carrying a pizza it has NONE of those modifiers. This item deliberately
 * has no durability or skillet cooking/flip abilities.
 */
public final class PizzaPeelItem extends Item {
    public static final ItemAttributeModifiers EMPTY_PEEL_WEAPON_ATTRIBUTES =
            SkilletItem.createAttributes(SkilletItem.SKILLET_MATERIAL, 5.0F, -3.1F);

    public PizzaPeelItem(Properties properties) {
        super(properties);
    }

    /**
     * Call immediately after every cargo change, including failed transactions.
     * Attribute components, just like peel_cargo, are synced/persisted on the
     * real ItemStack; the main-hand stats change without replacing the item.
     *
     * <p>Do NOT remove ATTRIBUTE_MODIFIERS on unload: removing a prototype
     * component can leave no modifiers at all. Explicitly restore the same
     * attributes as the registered empty peel.
     */
    public static void setCargo(ItemStack peel, @Nullable PizzaPeelCargo cargo) {
        if (cargo == null) {
            peel.remove(ModDataComponents.PEEL_CARGO);
        } else {
            peel.set(ModDataComponents.PEEL_CARGO, cargo);
        }
        syncCombatAttributes(peel);
    }

    /** Also repairs older, pre-weapon pizza peels loaded from saved inventories. */
    public static void syncCombatAttributes(ItemStack peel) {
        ItemAttributeModifiers expected = peel.has(ModDataComponents.PEEL_CARGO)
                ? ItemAttributeModifiers.EMPTY : EMPTY_PEEL_WEAPON_ATTRIBUTES;
        if (!expected.equals(peel.get(DataComponents.ATTRIBUTE_MODIFIERS))) {
            peel.set(DataComponents.ATTRIBUTE_MODIFIERS, expected);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner,
                              @Nullable EquipmentSlot slot) {
        // Handles legacy saves and stacks moved from chests, without changing
        // components every tick when the state is already correct.
        syncCombatAttributes(stack);
        // Peels can carry a pre-update mixed pizza whose ITEM_MODEL is stored
        // inside PEEL_CARGO. Re-save that one nested stack at most once.
        PizzaPeelCargo oldCargo = stack.get(ModDataComponents.PEEL_CARGO);
        if (oldCargo != null) {
            ItemStack pizza = oldCargo.pizza();
            if (PizzaDisplayNames.refreshMixedItemModel(pizza)) {
                setCargo(stack, new PizzaPeelCargo(pizza));
            }
        }
        super.inventoryTick(stack, level, owner, slot);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                TooltipDisplay display, Consumer<Component> consumer,
                                TooltipFlag type) {
        super.appendHoverText(stack, context, display, consumer, type);
        PizzaPeelCargo cargo = stack.get(ModDataComponents.PEEL_CARGO);
        if (cargo == null) {
            consumer.accept(Component.translatable("item.italiansdelight.pizza_peel.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        consumer.accept(Component.translatable("item.italiansdelight.pizza_peel.loaded",
                cargo.pizza().getHoverName()).withStyle(ChatFormatting.GRAY));
    }
}
