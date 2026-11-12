package dev.italiansdelight.common.item;

import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Every bottle displays its own persistent wine-quality component. */
public final class WineBottleItem extends Item {
    public WineBottleItem(Properties properties) { super(properties); }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context,
            TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        int quality = stack.getOrDefault(ModDataComponents.WINE_QUALITY, 0);
        consumer.accept(Component.translatable("tooltip.italiansdelight.wine_quality", quality)
                .withStyle(ChatFormatting.GOLD));
    }
}
