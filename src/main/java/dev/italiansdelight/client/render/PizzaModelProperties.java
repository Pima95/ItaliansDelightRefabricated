package dev.italiansdelight.client.render;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import dev.italiansdelight.common.registry.ModRegistries;
import java.util.List;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** Native 1.21.1 item overrides; values match the three inventory model JSONs. */
@EventBusSubscriber(modid = ItaliansDelight.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PizzaModelProperties {
    private static final ResourceLocation PRESET = ModRegistries.id("pizza_preset");
    private static final List<String> PRESETS = List.of(
        "bianca",
        "caprese_bianca",
        "caprese_rossa",
        "carne_mozzarella",
        "custom",
        "guanciale_pecorino_rossa",
        "mediterranea",
        "mortadella_ricotta",
        "mortadella_ricotta_mandorle",
        "olive_scamorza_bianca",
        "orto_disponibile",
        "patate_speck",
        "piccante_olive",
        "principessa",
        "prosciutto_crudo",
        "quattro_formaggi_bianca",
        "quattro_formaggi_rossa",
        "rossa",
        "salame",
        "salmone_ricotta",
        "salmone_scamorza",
        "speck_gorgonzola",
        "zucca_speck"
    );

    private static float value(ResourceLocation model, String base) {
        if (model.getPath().equals(base)) return 0.0F;
        String suffix = model.getPath().substring(base.length() + 1);
        int index = PRESETS.indexOf(suffix);
        return index < 0 ? 0.0F : (index + 1) / 100.0F;
    }

    private static float whole(ItemStack stack, boolean cooked) {
        var data = stack.get(ModDataComponents.PIZZA_CONTENTS);
        return data == null ? 0.0F : value(PizzaDisplayNames.wholeItemModel(data, cooked),
                cooked ? "cooked_pizza" : "raw_pizza");
    }

    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.RAW_PIZZA, PRESET,
                (stack, level, entity, seed) -> whole(stack, false));
            ItemProperties.register(ModItems.COOKED_PIZZA, PRESET,
                (stack, level, entity, seed) -> whole(stack, true));
            ItemProperties.register(ModItems.PIZZA_SLICE, PRESET, (stack, level, entity, seed) -> {
                var data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);
                return data == null ? 0.0F : value(PizzaDisplayNames.sliceItemModel(data), "pizza_slice");
            });
        });
    }
}
