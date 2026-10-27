package dev.italiansdelight;

import net.fabricmc.api.ClientModInitializer;
import dev.italiansdelight.common.registry.ModBlocks;
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

    @Override
    public void onInitializeClient() {
        // Affect only Almond leaves. Olive foliage, all other trees, and the
        // Almond sapling retain their existing untinted appearances.
        BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES);

        ModelLayerRegistry.registerModelLayer(
            OLIVE_BOAT_LAYER, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(
            OLIVE_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
        EntityRenderers.register(ModBoats.OLIVE_BOAT_ENTITY,
            context -> new BoatRenderer(context, OLIVE_BOAT_LAYER));
        EntityRenderers.register(ModBoats.OLIVE_CHEST_BOAT_ENTITY,
            context -> new BoatRenderer(context, OLIVE_CHEST_BOAT_LAYER));

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
