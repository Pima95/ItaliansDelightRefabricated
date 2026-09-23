package com.piergiuseppe.italiansdelight.registry;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Registro centrale degli item di Italian's Delight.
 * <p>
 * Convenzione: un campo statico per item, inizializzato con {@link #register}.
 * I FoodComponent seguono lo stile di Farmer's Delight (nutrimento contenuto,
 * saturazione più alta della media vanilla) invece di quello vanilla puro.
 * <p>
 * Questo file contiene solo 2 item di esempio (Pasta secca cruda, Cacio e Pepe
 * pronto) per fissare il pattern: gli item veri verranno aggiunti nel prossimo
 * step insieme alle relative recipe di Cutting Board / Cooking Pot.
 */
public final class ModItems {

    private ModItems() {}

    // --- Ingredienti crudi ---
    public static final Item RAW_PASTA_DOUGH = register(
            "raw_pasta_dough",
            new Item.Settings()
    );

    // --- Piatti pronti (esempio di food component in stile Farmer's Delight) ---
    public static final Item CACIO_E_PEPE = register(
            "cacio_e_pepe",
            new Item.Settings().food(
                    new FoodComponent.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build()
            )
    );

    private static Item register(String path, Item.Settings settings) {
        return Registry.register(Registries.ITEM, ModRegistries.id(path), new Item(settings));
    }

    public static void register() {
        // Il caricamento della classe (tramite i riferimenti statici sopra)
        // è sufficiente a far scattare le Registry.register(...).
    }
}
