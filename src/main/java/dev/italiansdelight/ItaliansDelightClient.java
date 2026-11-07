package dev.italiansdelight;

import net.fabricmc.api.ClientModInitializer;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModFluids;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.ARGB;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import dev.italiansdelight.client.gui.CheeseVatScreen;
import dev.italiansdelight.client.gui.PizzaOvenScreen;
import dev.italiansdelight.client.particle.WhiteBubblePopParticle;
import dev.italiansdelight.client.render.PizzaDivisionOverlay;
import dev.italiansdelight.client.render.PizzaToppingRenderer;
import dev.italiansdelight.client.render.CookedPizzaRenderer;
import dev.italiansdelight.client.render.BarrelFluidRenderer;
import dev.italiansdelight.client.render.CrushingTubFluidRenderer;
import dev.italiansdelight.client.render.FluidCauldronRenderer;
import dev.italiansdelight.client.render.PizzaPeelItemModel;
import dev.italiansdelight.common.registry.ModRegistries;
import dev.italiansdelight.common.registry.ModBoats;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import dev.italiansdelight.common.registry.ModBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import dev.italiansdelight.common.registry.ModMenuTypes;
import dev.italiansdelight.common.registry.ModParticles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

/**
 * Client-only entrypoint for Italian's Delight.
 * Screens, renderers, and other client-side features are registered here and
 * must never be initialized on a dedicated server.
 */
public class ItaliansDelightClient implements ClientModInitializer {

    // The layer IDs are also the relative paths of the existing entity art:
    // textures/entity/boat/olive.png and textures/entity/chest_boat/olive.png.
    private static final ModelLayerLocation OLIVE_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("boat/olive"), "main");
    private static final ModelLayerLocation OLIVE_CHEST_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("chest_boat/olive"), "main");
    private static final ModelLayerLocation ALMOND_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("boat/almond"), "main");
    private static final ModelLayerLocation ALMOND_CHEST_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("chest_boat/almond"), "main");
    private static final ModelLayerLocation FRUIT_CHERRY_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("boat/fruit_cherry"), "main");
    private static final ModelLayerLocation FRUIT_CHERRY_CHEST_BOAT_LAYER =
        new ModelLayerLocation(ModRegistries.id("chest_boat/fruit_cherry"), "main");


    /**
     * Soft biome variation only for Almond leaves. Keep the original approved
     * green texture; a 30% vanilla foliage influence gives restrained warm
     * savanna foliage and cooler greens in cold biomes rather than turning
     * the six-color art into a dark, fully multiplied oak-style texture.
     */
    private static final BlockTintSource ALMOND_BIOME_FOLIAGE = new BlockTintSource() {
        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            int biome = BlockTintSources.foliage().colorInWorld(state, level, pos);
            return softenAlmondTint(biome);
        }

        @Override
        public int color(BlockState state) {
            return 0xFFFFFFFF; // Inventory keeps the approved leaf color.
        }
    };

    private static int softenAlmondTint(int biome) {
        int red = (255 * 7 + ((biome >>> 16) & 255) * 3) / 10;
        int green = (255 * 7 + ((biome >>> 8) & 255) * 3) / 10;
        int blue = (255 * 7 + (biome & 255) * 3) / 10;
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    /**
     * Fruit Cherry: vanilla foliage-biome colors blended into the original
     * subdued leaf sprites. Keep the approved sapling completely untinted.
     * A 50% biome contribution is clearer than Almond's 30% soft variation.
     * This is a client-only BlockTintSource: no server-side color code.
     */
    private static final BlockTintSource FRUIT_CHERRY_BIOME_FOLIAGE = new BlockTintSource() {
        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            // Every growth stage uses the SAME canopy biome tint.
            // Stage models render petals and fruit as separate untinted quads.
            int biome = BlockTintSources.foliage().colorInWorld(state, level, pos);
            return softenFruitCherryTint(biome);
        }

        @Override
        public int color(BlockState state) {
            return 0xFFFFFFFF; // Inventory has its consistent untinted base palette.
        }
    };

    private static int softenFruitCherryTint(int biome) {
        int red = (255 * 5 + ((biome >>> 16) & 255) * 5) / 10;
        int green = (255 * 5 + ((biome >>> 8) & 255) * 5) / 10;
        int blue = (255 * 5 + (biome & 255) * 5) / 10;
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    @Override
    public void onInitializeClient() {
        // The authored Rustic oil frames are already under textures/block/fluids.
        FluidRenderingRegistry.register(
            ModFluids.OLIVE_OIL_STILL,
            ModFluids.OLIVE_OIL_FLOWING,
            new FluidModel.Unbaked(
                new Material(ModRegistries.id("block/fluids/olive_oil_still")),
                new Material(ModRegistries.id("block/fluids/olive_oil_flow")),
                new Material(ModRegistries.id("block/fluids/olive_oil_overlay")),
                BlockTintSources.constant(ARGB.opaque(0xFFFFFF))
            )
        );
        // Almond and Fruit Cherry react to foliage biome colors independently.
        // Olive, both saplings, and the rest of the orchard art remain untinted.
        BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES);
        BlockColorRegistry.register(List.of(FRUIT_CHERRY_BIOME_FOLIAGE), ModBlocks.FRUIT_CHERRY_LEAVES);

        ModelLayerRegistry.registerModelLayer(
            OLIVE_BOAT_LAYER, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(
            OLIVE_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
        EntityRenderers.register(ModBoats.OLIVE_BOAT_ENTITY,
            context -> new BoatRenderer(context, OLIVE_BOAT_LAYER));
        EntityRenderers.register(ModBoats.OLIVE_CHEST_BOAT_ENTITY,
            context -> new BoatRenderer(context, OLIVE_CHEST_BOAT_LAYER));
        ModelLayerRegistry.registerModelLayer(ALMOND_BOAT_LAYER, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(ALMOND_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
        EntityRenderers.register(ModBoats.ALMOND_BOAT_ENTITY,
            context -> new BoatRenderer(context, ALMOND_BOAT_LAYER));
        EntityRenderers.register(ModBoats.ALMOND_CHEST_BOAT_ENTITY,
            context -> new BoatRenderer(context, ALMOND_CHEST_BOAT_LAYER));
        ModelLayerRegistry.registerModelLayer(FRUIT_CHERRY_BOAT_LAYER, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(FRUIT_CHERRY_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
        EntityRenderers.register(ModBoats.FRUIT_CHERRY_BOAT_ENTITY,
            context -> new BoatRenderer(context, FRUIT_CHERRY_BOAT_LAYER));
        EntityRenderers.register(ModBoats.FRUIT_CHERRY_CHEST_BOAT_ENTITY,
            context -> new BoatRenderer(context, FRUIT_CHERRY_CHEST_BOAT_LAYER));

        // Wrap ONLY the already-baked peel item model; keep its 3D geometry
        // and all approved transforms. On loaded peels the wrapper appends
        // the exact ItemStack model from the serialized cargo component.
        ModelLoadingPlugin.register(context ->
                context.modifyItemModelAfterBake().register((model, details) ->
                        details.itemId().equals(ModRegistries.id("pizza_peel"))
                                ? new PizzaPeelItemModel(model) : model));

        PizzaDivisionOverlay.register();
        BlockEntityRenderers.register(ModBlockEntities.PIZZA_DOUGH, PizzaToppingRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COOKED_PIZZA, CookedPizzaRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.BARREL, BarrelFluidRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CRUSHING_TUB, CrushingTubFluidRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FLUID_CAULDRON, FluidCauldronRenderer::new);
        // Links the server-synchronized MenuType to the client-side screen.
        MenuScreens.register(
            ModMenuTypes.CHEESE_VAT,
            CheeseVatScreen::new
        );

        // Dedicated pizza-only screen, no vanilla furnace recipe book.
        MenuScreens.register(
            ModMenuTypes.PIZZA_OVEN,
            PizzaOvenScreen::new
        );

        ParticleProviderRegistry.getInstance().register(
            ModParticles.WHITE_BUBBLE_POP,
            WhiteBubblePopParticle.Provider::new
        );
    }
}
