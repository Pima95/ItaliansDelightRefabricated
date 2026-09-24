package com.piergiuseppe.italiansdelight.item.group;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import com.piergiuseppe.italiansdelight.registry.ModItems;
import com.piergiuseppe.italiansdelight.registry.ModBlocks;
import com.piergiuseppe.italiansdelight.registry.ModRegistries;

/** Tab creativa dedicata a Italian's Delight, separata da quella di Farmer's Delight. */
public final class ModItemGroups {

    private ModItemGroups() {}

    public static final ResourceKey<CreativeModeTab> ITALIANS_DELIGHT_GROUP_KEY =
            ResourceKey.create(
                    BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    ModRegistries.id("italiansdelight_group")
            );

    public static final CreativeModeTab ITALIANS_DELIGHT_GROUP = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.PASTA_WITH_TOMATO_SAUCE)) //Modifica icona del tab creative
            .title(Component.translatable("itemGroup.italiansdelight.main"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.CHEESE_VAT);
                output.accept(ModItems.PASTA_WITH_TOMATO_SAUCE);
                output.accept(ModItems.RISOTTO_WITH_TOMATO_SAUCE);
                output.accept(ModItems.MOZZARELLA);
                output.accept(ModItems.MOZZARELLA_SLICE);
                output.accept(ModItems.TOMATO_SLICE);
                output.accept(ModItems.RENNET);
                output.accept(ModItems.CURD);
                output.accept(ModItems.SALT);
            })
            .build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ITALIANS_DELIGHT_GROUP_KEY,
                ITALIANS_DELIGHT_GROUP
        );
    }
}
