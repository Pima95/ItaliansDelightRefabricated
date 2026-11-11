package dev.italiansdelight.common.fluid;

import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

/**
 * FABRIC-ONLY adapter: register our supported fluid families with FluidStorage.ITEM.
 * Milk buckets and Farmer's Delight milk bottles are deliberately left untouched.
 *
 * Uses combined providers on vanilla bucket/bottle items to avoid clobbering
 * Fabric's water/lava/potion handlers or another mod's registrations.
 * Platform-specific equivalents for NeoForge should live in its loader module.
 */
public final class FabricFluidItemContainers {
    private FabricFluidItemContainers() {}

    public static void register() {
        // Registered (non-world) fluids: buckets and bottles are first-class
        // fluid containers; no hardcoded hand logic is needed in other mods.
        storageOnly(ModFluids.SHEEP_MILK, ModItems.SHEEP_MILK_BUCKET, ModItems.SHEEP_MILK_BOTTLE);
        storageOnly(ModFluids.WHEY, ModItems.WHEY_BUCKET, ModItems.WHEY_BOTTLE);
        storageOnly(ModFluids.RENNET, ModItems.RENNET_BUCKET, ModItems.RENNET);
        storageOnly(ModFluids.APPLE_CIDER_VINEGAR,
            ModItems.APPLE_CIDER_VINEGAR_BUCKET, ModItems.APPLE_CIDER_VINEGAR);

        // Their normal BucketItems are already exposed by Fabric, but their
        // additional 250mB bottle items are not automatically recognized.
        bottle(ModFluids.OLIVE_OIL_STILL, ModItems.OLIVE_OIL_BOTTLE);
        bottle(ModFluids.APPLE_JUICE_STILL, ModItems.APPLE_JUICE_BOTTLE);
        // Golden apple juice uses a non-placeable storage-only Item bucket,
        // like whey; never register it as a vanilla placeable BucketItem.
        storageOnly(ModFluids.GOLDEN_APPLE_JUICE,
            ModItems.GOLDEN_APPLE_JUICE_BUCKET, ModItems.GOLDEN_APPLE_JUICE_BOTTLE);
        // Vanilla honey has NO registered Fluid, so HONEY_BUCKET deliberately
        // remains an ID-only container handled by BarrelFluidContainers.
    }

    private static void storageOnly(Fluid fluid, Item fullBucket, Item fullBottle) {
        full(fullBucket, Items.BUCKET, fluid, FluidConstants.BUCKET);
        FluidStorage.combinedItemApiProvider(Items.BUCKET).register(
            context -> new EmptyItemFluidStorage(
                context, fullBucket, fluid, FluidConstants.BUCKET));
        bottle(fluid, fullBottle);
    }

    private static void bottle(Fluid fluid, Item fullBottle) {
        full(fullBottle, Items.GLASS_BOTTLE, fluid, FluidConstants.BOTTLE);
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(
            context -> new EmptyItemFluidStorage(
                context, fullBottle, fluid, FluidConstants.BOTTLE));
    }

    private static void full(Item full, Item empty, Fluid fluid, long amount) {
        FluidStorage.combinedItemApiProvider(full).register(
            context -> new FullItemFluidStorage(
                context, empty, FluidVariant.of(fluid), amount));
    }
}
