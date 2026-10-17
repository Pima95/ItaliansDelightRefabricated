package dev.italiansdelight.common.item;

import dev.italiansdelight.common.pizza.PizzaPeelCargo;
import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Reusable, nonstackable transport tool. No durability or weapon bonuses. */
public final class PizzaPeelItem extends Item {
    public PizzaPeelItem(Properties properties) {
        super(properties);
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
