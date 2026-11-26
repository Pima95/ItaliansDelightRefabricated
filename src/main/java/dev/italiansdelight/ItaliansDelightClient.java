package dev.italiansdelight;

import dev.italiansdelight.client.gui.*;
import dev.italiansdelight.client.render.*;
import dev.italiansdelight.client.particle.WhiteBubblePopParticle;
import dev.italiansdelight.common.registry.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid = ItaliansDelight.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ItaliansDelightClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModBoats.OLIVE_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, false));
        event.registerEntityRenderer(ModBoats.OLIVE_CHEST_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, true));
        event.registerEntityRenderer(ModBoats.ALMOND_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, false));
        event.registerEntityRenderer(ModBoats.ALMOND_CHEST_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, true));
        event.registerEntityRenderer(ModBoats.FRUIT_CHERRY_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, false));
        event.registerEntityRenderer(ModBoats.FRUIT_CHERRY_CHEST_BOAT_ENTITY, ctx -> new BoatRenderer(ctx, true));
        event.registerBlockEntityRenderer(ModBlockEntities.PIZZA_DOUGH, PizzaToppingRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COOKED_PIZZA, CookedPizzaRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BARREL, BarrelFluidRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRUSHING_TUB, CrushingTubFluidRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_CAULDRON, FluidCauldronRenderer::new);
    }

    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CHEESE_VAT, CheeseVatScreen::new);
        event.register(ModMenuTypes.BREWING_BARREL, BrewingBarrelScreen::new);
        event.register(ModMenuTypes.PIZZA_OVEN, PizzaOvenScreen::new);
    }

    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.WHITE_BUBBLE_POP, WhiteBubblePopParticle.Provider::new);
    }

    private static int soften(int color, int weight) {
        int red = (255 * (10-weight) + ((color >>> 16) & 255)*weight)/10;
        int green = (255 * (10-weight) + ((color >>> 8) & 255)*weight)/10;
        int blue = (255 * (10-weight) + (color & 255)*weight)/10;
        return red << 16 | green << 8 | blue;
    }

    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> level == null || pos == null ? 0xFFFFFF :
            soften(BiomeColors.getAverageFoliageColor(level,pos), 3), ModBlocks.ALMOND_LEAVES);
        event.register((state, level, pos, tint) -> level == null || pos == null ? 0xFFFFFF :
            soften(BiomeColors.getAverageFoliageColor(level,pos), 5), ModBlocks.FRUIT_CHERRY_LEAVES);
        event.register((state, level, pos, tint) -> level == null || pos == null ? 0xFFFFFF :
            BiomeColors.getAverageFoliageColor(level,pos), ModBlocks.GRAPE_LEAVES, ModBlocks.WHITE_GRAPE_LEAVES);
    }

    private static void fluid(RegisterClientExtensionsEvent event, Fluid fluid, String still, String flow, String overlay) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return ModRegistries.id(still); }
            @Override public ResourceLocation getFlowingTexture() { return ModRegistries.id(flow); }
            @Override public ResourceLocation getOverlayTexture() { return overlay == null ? null : ModRegistries.id(overlay); }
        }, fluid.getFluidType());
    }

    @SubscribeEvent public static void fluids(RegisterClientExtensionsEvent event) {
        fluid(event, ModFluids.OLIVE_OIL_STILL, "block/fluids/olive_oil_still", "block/fluids/olive_oil_flow", "block/fluids/olive_oil_overlay");
        fluid(event, ModFluids.APPLE_JUICE_STILL, "block/fluids/apple_juice_still", "block/fluids/apple_juice_flow", "block/fluids/apple_juice_overlay");
        fluid(event, ModFluids.SHEEP_MILK, "block/fluids/sheep_milk_still", "block/fluids/sheep_milk_still", null);
        fluid(event, ModFluids.WHEY, "block/fluids/whey_still", "block/fluids/whey_still", null);
        fluid(event, ModFluids.RENNET, "block/fluids/rennet_still", "block/fluids/rennet_still", null);
        fluid(event, ModFluids.APPLE_CIDER_VINEGAR, "block/fluids/booze/cider_still", "block/fluids/booze/cider_still", null);
        fluid(event, ModFluids.GOLDEN_APPLE_JUICE, "block/fluids/golden_apple_juice_still", "block/fluids/golden_apple_juice_still", null);
        fluid(event, ModFluids.GRAPE_JUICE, "block/fluids/grape_juice_still", "block/fluids/grape_juice_still", null);
        fluid(event, ModFluids.WHITE_GRAPE_JUICE, "block/fluids/white_grape_juice_still", "block/fluids/white_grape_juice_still", null);
        fluid(event, ModFluids.WINE, "block/fluids/booze/wine_still", "block/fluids/booze/wine_still", null);
        fluid(event, ModFluids.CHERRY_JUICE, "block/fluids/booze/cherry_juice_still", "block/fluids/booze/cherry_juice_still", null);
        fluid(event, ModFluids.WHITE_WINE, "block/fluids/booze/white_wine_still", "block/fluids/booze/white_wine_still", null);
        fluid(event, ModFluids.CHERRY_WINE, "block/fluids/booze/cherry_wine_still", "block/fluids/booze/cherry_wine_still", null);
        fluid(event, ModFluids.HONEY, "block/fluids/booze/mead_still", "block/fluids/booze/mead_still", null);
        fluid(event, ModFluids.MEAD, "block/fluids/booze/mead_still", "block/fluids/booze/mead_still", null);
        fluid(event, ModFluids.CIDER, "block/fluids/booze/cider_still", "block/fluids/booze/cider_still", null);
        fluid(event, ModFluids.RED_WINE_VINEGAR, "block/fluids/booze/red_wine_vinegar_still", "block/fluids/booze/red_wine_vinegar_still", null);
    }

    @SubscribeEvent public static void additionalModels(ModelEvent.RegisterAdditional event) {
        event.register(PizzaPeelItemModel.LOADED);
    }

    @SubscribeEvent public static void bakedModels(ModelEvent.ModifyBakingResult event) {
        event.getModels().computeIfPresent(PizzaPeelItemModel.BASE, (id, model) -> new PizzaPeelItemModel(model));
    }

    @SubscribeEvent public static void itemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private PizzaPeelItemModel.Renderer renderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new PizzaPeelItemModel.Renderer();
                return renderer;
            }
        }, ModItems.PIZZA_PEEL);
    }

    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.OLIVE_OIL_STILL, RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.OLIVE_OIL_FLOWING, RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.APPLE_JUICE_STILL, RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.APPLE_JUICE_FLOWING, RenderType.translucent());
            PizzaDivisionOverlay.register();
        });
    }
}
