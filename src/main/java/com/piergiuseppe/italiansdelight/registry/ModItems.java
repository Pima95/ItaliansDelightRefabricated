package com.piergiuseppe.italiansdelight.registry;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Registro centrale degli item di Italian's Delight.
 *
 * Gli item vengono registrati nel namespace della mod tramite ModRegistries.
 * Per ora contiene solamente gli item utilizzati per verificare la struttura
 * tecnica del progetto; gli ingredienti e i piatti definitivi verranno aggiunti
 * nella fase dedicata al contenuto.
 */
public final class ModItems {

    private ModItems() {
    }

    // --- Ingredienti di base ---
    public static final Item RAW_PASTA_DOUGH = register(
            "raw_pasta_dough",
            new Item.Settings()
    );

    // --- Piatti pronti ---
    public static final Item CACIO_E_PEPE = register(
            "cacio_e_pepe",
            new Item.Settings().food(
                    new FoodComponent.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build()
            )
    );

    /**
     * Registra un item nel registry di Minecraft.
     */
    private static Item register(String path, Item.Settings settings) {
        return Registry.register(
                Registries.ITEM,
                ModRegistries.id(path),
                new Item(settings)
        );
    }

    /**
     * Metodo chiamato dall'entrypoint della mod.
     *
     * Il caricamento della classe inizializza i campi statici e quindi
     * esegue automaticamente le registrazioni degli item.
     */
    public static void register() {
        // Le registrazioni avvengono durante l'inizializzazione dei campi statici.
    }
}