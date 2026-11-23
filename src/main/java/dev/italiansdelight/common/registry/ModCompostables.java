package dev.italiansdelight.common.registry;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/**
 * Minecraft 26.3 stores compostability in DataComponents.COMPOSTABLE.
 * Preserve old 30%, 65% and 85% chances via vanilla built-in providers.
 * The component is installed through Fabric's default item component event
 * because all referenced items are registered in other classes.
 */
public final class ModCompostables {
    private ModCompostables() {}

    public static void register() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(ModBlocks.OLIVE_SAPLING.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModBlocks.ALMOND_SAPLING.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModBlocks.FRUIT_CHERRY_SAPLING.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModBlocks.OLIVE_LEAVES.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModBlocks.ALMOND_LEAVES.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModBlocks.FRUIT_CHERRY_LEAVES.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModItems.APPLE_SEEDS, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModItems.OLIVES, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.OLIVE_SEED, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
            context.modify(ModItems.OLIVE_PITTED, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.OLIVE_PATE, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.BRUSCHETTA, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.OLIVE_BRUSCHETTA, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.ALMONDS, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.CHOPPED_ALMONDS, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.ALMOND_PESTO, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModItems.BASIL_PESTO, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.ALMOND_CAKE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModItems.ALMOND_CAKE_SLICE, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModItems.CHERRIES, builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.BASIL.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.WILD_BASIL.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.CARDOON.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.CULTIVATED_CARDOON.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM)));
            context.modify(ModBlocks.OLIVE_CRATE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModBlocks.ALMOND_CRATE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModBlocks.CHERRY_CRATE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModBlocks.BASIL_CRATE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
            context.modify(ModBlocks.CARDOON_CRATE.asItem(), builder -> builder.set(DataComponents.COMPOSTABLE,
                new Compostable(ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH)));
        });
    }
}
