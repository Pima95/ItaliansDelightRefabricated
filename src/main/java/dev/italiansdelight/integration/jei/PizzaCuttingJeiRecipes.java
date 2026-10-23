package dev.italiansdelight.integration.jei;

import dev.italiansdelight.common.pizza.PizzaCreativeSamples;
import dev.italiansdelight.common.pizza.PizzaDisplayNames;
import dev.italiansdelight.common.pizza.PizzaItemData;
import dev.italiansdelight.common.pizza.PizzaSliceFactory;
import dev.italiansdelight.common.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/** All 18 recognized flavors and one mixed pizza with four different slices. */
public final class PizzaCuttingJeiRecipes {
    private PizzaCuttingJeiRecipes() {}

    public static List<PizzaCuttingJeiRecipe> createExamples() {
        List<PizzaCuttingJeiRecipe> all = new ArrayList<>();
        for (var preset : PizzaDisplayNames.allPresets().entrySet()) {
            all.add(create(PizzaCreativeSamples.create(preset.getKey(), true)));
        }
        all.add(create(PizzaCreativeSamples.createMixed(true)));
        return List.copyOf(all);
    }

    private static PizzaCuttingJeiRecipe create(ItemStack pizza) {
        PizzaItemData data = pizza.get(ModDataComponents.PIZZA_CONTENTS);
        if (data == null) throw new IllegalStateException("JEI pizza missing quarter data");
        List<ItemStack> slices = new ArrayList<>(4);
        for (int q = 0; q < 4; q++) {
            slices.add(PizzaSliceFactory.create(data, q));
        }
        return new PizzaCuttingJeiRecipe(pizza, slices);
    }
}
