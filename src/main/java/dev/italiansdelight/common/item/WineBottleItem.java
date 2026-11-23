package dev.italiansdelight.common.item;

import dev.italiansdelight.common.registry.ModDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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

    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        // Capture the quality-bearing stack before vanilla consumption swaps the bottle.
        if (level instanceof ServerLevel) WineDrinkEffects.apply(stack, user);
        return super.finishUsingItem(stack, level, user);
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context,
            TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, consumer, flag);
        int quality = stack.getOrDefault(ModDataComponents.WINE_QUALITY, 0);
        consumer.accept(Component.translatable("tooltip.italiansdelight.wine_quality", quality)
                .withStyle(ChatFormatting.GOLD));
    }
}
