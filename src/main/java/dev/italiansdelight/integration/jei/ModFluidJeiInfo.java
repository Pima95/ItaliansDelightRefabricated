package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.registry.ModItems;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

/**
 * Information pages in the same order as our fluid items in the Creative/JEI list:
 * animal-derived liquids, Crushing Tub products, then fermented drinks and vinegars.
 * Containers are always bucket first, bottle second.
 */
public final class ModFluidJeiInfo {
    private ModFluidJeiInfo() {}

    private static void info(IRecipeRegistration registration, String name, Item bucket, Item bottle) {
        registration.addItemStackInfo(
            List.of(new ItemStack(bucket), new ItemStack(bottle)),
            Component.translatable("jei.italiansdelight.fluids." + name));
    }

    public static void register(IRecipeRegistration registration) {
        // 1. Animal-derived: milk, whey, rennet and honey (from bees).
        info(registration, "sheep_milk", ModItems.SHEEP_MILK_BUCKET, ModItems.SHEEP_MILK_BOTTLE);
        info(registration, "whey", ModItems.WHEY_BUCKET, ModItems.WHEY_BOTTLE);
        info(registration, "rennet", ModItems.RENNET_BUCKET, ModItems.RENNET);
        info(registration, "honey", ModItems.HONEY_BUCKET, Items.HONEY_BOTTLE);

        // 2. Crushing Tub and other non-fermented vegetable liquids.
        info(registration, "olive_oil", ModItems.OLIVE_OIL_BUCKET, ModItems.OLIVE_OIL_BOTTLE);
        info(registration, "apple_juice", ModItems.APPLE_JUICE_BUCKET, ModItems.APPLE_JUICE_BOTTLE);
        info(registration, "golden_apple_juice", ModItems.GOLDEN_APPLE_JUICE_BUCKET,
            ModItems.GOLDEN_APPLE_JUICE_BOTTLE);
        info(registration, "grape_juice", ModItems.GRAPE_JUICE_BUCKET, ModItems.GRAPE_JUICE_BOTTLE);
        info(registration, "white_grape_juice", ModItems.WHITE_GRAPE_JUICE_BUCKET,
            ModItems.WHITE_GRAPE_JUICE_BOTTLE);
        info(registration, "cherry_juice", ModItems.CHERRY_JUICE_BUCKET, ModItems.CHERRY_JUICE_BOTTLE);

        // 3. Fermented products: wine families first, followed by other drinks
        // and vinegars. There are no extra fake JEI fluids.
        info(registration, "wine", ModItems.WINE_BUCKET, ModItems.WINE_BOTTLE);
        info(registration, "white_wine", ModItems.WHITE_WINE_BUCKET, ModItems.WHITE_WINE_BOTTLE);
        info(registration, "cherry_wine", ModItems.CHERRY_WINE_BUCKET, ModItems.CHERRY_WINE_BOTTLE);
        info(registration, "mead", ModItems.MEAD_BUCKET, ModItems.MEAD_BOTTLE);
        info(registration, "cider", ModItems.CIDER_BUCKET, ModItems.CIDER_BOTTLE);
        info(registration, "apple_cider_vinegar", ModItems.APPLE_CIDER_VINEGAR_BUCKET,
            ModItems.APPLE_CIDER_VINEGAR);
        info(registration, "red_wine_vinegar", ModItems.RED_WINE_VINEGAR_BUCKET,
            ModItems.RED_WINE_VINEGAR_BOTTLE);
    }
}
