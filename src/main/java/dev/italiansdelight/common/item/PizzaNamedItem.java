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

/**
 * A single item ID per pizza state (raw, baked, or slice), with data-driven
 * names and tooltips. Gameplay/food behavior remains owned by the old items.
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
        } else {
            PizzaItemData data = stack.get(ModDataComponents.PIZZA_CONTENTS);
            if (data != null) {
                PizzaDisplayNames.appendWholeTooltip(data, consumer);
            }
        }
    }
}
