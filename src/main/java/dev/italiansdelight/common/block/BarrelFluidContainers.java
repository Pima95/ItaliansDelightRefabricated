package dev.italiansdelight.common.block;

import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/** Rustic-style adapters: all registered fluid buckets, plus named special containers. */
public final class BarrelFluidContainers {
    private BarrelFluidContainers() {}
    public record Portion(String id, int amount, Item empty) {}

    public static Portion filledContainer(ItemStack stack) {
        if (stack.is(ModItems.RENNET))
            return new Portion("italiansdelight:rennet", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.APPLE_CIDER_VINEGAR))
            return new Portion("italiansdelight:apple_cider_vinegar", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.RENNET_BUCKET))
            return new Portion("italiansdelight:rennet", 1000, Items.BUCKET);
        if (stack.is(ModItems.APPLE_CIDER_VINEGAR_BUCKET))
            return new Portion("italiansdelight:apple_cider_vinegar", 1000, Items.BUCKET);
        // Farmer's Delight cow milk bottles must behave like our sheep milk.
        if (stack.is(vectorwing.farmersdelight.common.registry.ModItems.MILK_BOTTLE.get()))
            return new Portion("minecraft:milk", 250, Items.GLASS_BOTTLE);
        // Honey is a real vanilla bottled liquid, though not placeable as fluid.
        if (stack.is(Items.HONEY_BOTTLE))
            return new Portion("minecraft:honey", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.SHEEP_MILK_BOTTLE))
            return new Portion("italiansdelight:sheep_milk", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.APPLE_JUICE_BOTTLE))
            return new Portion("italiansdelight:apple_juice", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.OLIVE_OIL_BOTTLE))
            return new Portion("italiansdelight:olive_oil", 250, Items.GLASS_BOTTLE);
        if (stack.is(ModItems.WHEY_BOTTLE))
            return new Portion("italiansdelight:whey", 250, Items.GLASS_BOTTLE);
        if (stack.is(Items.POTION) &&
            stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER))
            return new Portion("minecraft:water", 250, Items.GLASS_BOTTLE);
        if (stack.is(Items.MILK_BUCKET))
            return new Portion("minecraft:milk", 1000, Items.BUCKET);
        if (stack.is(ModItems.SHEEP_MILK_BUCKET))
            return new Portion("italiansdelight:sheep_milk", 1000, Items.BUCKET);
        if (stack.is(ModItems.WHEY_BUCKET))
            return new Portion("italiansdelight:whey", 1000, Items.BUCKET);
        if (stack.getItem() instanceof BucketItem) {
            // Minecraft 26.2 has no public BucketItem.getFluid(): resolve by the
            // registered SOURCE fluid's actual bucket item, supporting future fluids.
            for (Fluid fluid : BuiltInRegistries.FLUID) {
                if (fluid != Fluids.LAVA && fluid.isSource(fluid.defaultFluidState()) &&
                    fluid.getBucket() == stack.getItem()) {
                    Identifier id = BuiltInRegistries.FLUID.getKey(fluid);
                    if (id != null) return new Portion(id.toString(), 1000, Items.BUCKET);
                }
            }
        }
        return null;
    }

    public static ItemStack makeFilled(String id, int size) {
        if (size == 250) {
            if ("italiansdelight:rennet".equals(id)) return new ItemStack(ModItems.RENNET);
            if ("italiansdelight:apple_cider_vinegar".equals(id)) return new ItemStack(ModItems.APPLE_CIDER_VINEGAR);
            if ("italiansdelight:sheep_milk".equals(id)) return new ItemStack(ModItems.SHEEP_MILK_BOTTLE);
            if ("minecraft:milk".equals(id)) return new ItemStack(
                vectorwing.farmersdelight.common.registry.ModItems.MILK_BOTTLE.get());
            if ("minecraft:honey".equals(id)) return new ItemStack(Items.HONEY_BOTTLE);
            if ("italiansdelight:apple_juice".equals(id))
                return new ItemStack(ModItems.APPLE_JUICE_BOTTLE);
            if ("italiansdelight:olive_oil".equals(id)) return new ItemStack(ModItems.OLIVE_OIL_BOTTLE);
            if ("italiansdelight:whey".equals(id)) return new ItemStack(ModItems.WHEY_BOTTLE);
            if ("minecraft:water".equals(id)) return PotionContents.createItemStack(Items.POTION, Potions.WATER);
            return ItemStack.EMPTY;
        }
        if (size != 1000) return ItemStack.EMPTY;
        if ("italiansdelight:rennet".equals(id)) return new ItemStack(ModItems.RENNET_BUCKET);
        if ("italiansdelight:apple_cider_vinegar".equals(id)) return new ItemStack(ModItems.APPLE_CIDER_VINEGAR_BUCKET);
        if ("minecraft:milk".equals(id)) return new ItemStack(Items.MILK_BUCKET);
        if ("italiansdelight:sheep_milk".equals(id)) return new ItemStack(ModItems.SHEEP_MILK_BUCKET);
        if ("italiansdelight:whey".equals(id)) return new ItemStack(ModItems.WHEY_BUCKET);
        Identifier key = Identifier.tryParse(id);
        if (key == null) return ItemStack.EMPTY;
        Fluid fluid = BuiltInRegistries.FLUID.getValue(key);
        if (fluid == null || fluid == Fluids.EMPTY || fluid.getBucket() == Items.AIR)
            return ItemStack.EMPTY;
        return new ItemStack(fluid.getBucket());
    }
}
