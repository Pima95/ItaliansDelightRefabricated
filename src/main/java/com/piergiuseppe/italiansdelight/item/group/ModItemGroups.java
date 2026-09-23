package com.piergiuseppe.italiansdelight.item.group;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import com.piergiuseppe.italiansdelight.registry.ModItems;
import com.piergiuseppe.italiansdelight.registry.ModRegistries;

/** Tab creativa dedicata a Italian's Delight, separata da quella di Farmer's Delight. */
public final class ModItemGroups {

    private ModItemGroups() {}

    public static final ItemGroup ITALIANS_DELIGHT_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            ModRegistries.id("italiansdelight_group"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.CACIO_E_PEPE))
                    .displayName(Text.translatable("itemGroup.italiansdelight.main"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.RAW_PASTA_DOUGH);
                        entries.add(ModItems.CACIO_E_PEPE);
                    })
                    .build()
    );

    public static void register() {
        // Il caricamento della classe è sufficiente.
    }
}
