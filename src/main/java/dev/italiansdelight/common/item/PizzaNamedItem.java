package dev.italiansdelight.common.item;

import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceData;
import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.utility.TextUtils;

/**
 * A single item ID per pizza state (raw, baked, or slice), with data-driven
 * names and tooltips. Gameplay/food behavior remains owned by the old items.
 * The optional slice effect tooltip is delegated to Farmer's Delight so
 * it displays the actual saved Consumable's effect and localized duration.
 */
public final class PizzaNamedItem extends Item {
    private final boolean slice;

    public PizzaNamedItem(Properties properties, boolean slice) {
        super(properties);
        this.slice = slice;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (slice) {
            PizzaSliceData data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
            return data == null ? super.getName(stack) : PizzaDisplayNames.sliceName(data);
        }
        PizzaItemData data = stack.get(ModDataComponents.PIZZA_CONTENTS);
        return data == null ? super.getName(stack) : PizzaDisplayNames.wholeName(data);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> consumer, TooltipFlag type
    ) {
        super.appendHoverText(stack, context, display, consumer, type);
        if (slice) {
            PizzaSliceData data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
            if (data != null) {
                PizzaDisplayNames.appendSliceTooltip(data, consumer);
            }
            // Match Farmer's Delight ConsumableItem: use the actual saved
            // Consumable for localized effect name, color and duration.
            // In particular, no tooltip is added for Bianca/Rossa (no effect).
            if (Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
                TextUtils.addFoodEffectTooltip(stack, consumer, 1.0F, context.tickRate());
            }
        } else {
            PizzaItemData data = stack.get(ModDataComponents.PIZZA_CONTENTS);
            if (data != null) {
                PizzaDisplayNames.appendWholeTooltip(data, consumer);
            }
        }
    }
}
