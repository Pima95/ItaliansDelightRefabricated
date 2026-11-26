package dev.italiansdelight.common.item;

import java.util.List;

import dev.italiansdelight.common.registry.ModDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import vectorwing.farmersdelight.common.item.DrinkableItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Every bottle displays its own persistent wine-quality component. */
public final class WineBottleItem extends DrinkableItem {
    public WineBottleItem(Properties properties) { super(properties); }

    @Override public void affectConsumer(ItemStack stack, Level level, LivingEntity user) {
        WineDrinkEffects.apply(stack, user);
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.getCraftingRemainingItem().is(Items.GLASS_BOTTLE))
            return InteractionResultHolder.pass(held);
        return super.use(level, player, hand);
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context,
            List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int quality = stack.getOrDefault(ModDataComponents.WINE_QUALITY, 0);
        tooltip.add(Component.translatable("tooltip.italiansdelight.wine_quality", quality)
                .withStyle(ChatFormatting.GOLD));
    }
}
