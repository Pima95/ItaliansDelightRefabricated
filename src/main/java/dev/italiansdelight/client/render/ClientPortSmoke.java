package dev.italiansdelight.client.render;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Opt-in development smoke test: initial client resources and representative baked models. */
public final class ClientPortSmoke {
    private static int readyTicks;
    private static boolean finished;

    private ClientPortSmoke() {}

    public static void tick(ClientTickEvent.Post event) {
        if (finished) return;
        var mc = Minecraft.getInstance();
        // Fresh installations can show accessibility onboarding instead of the title.
        if (mc.getOverlay() != null || mc.screen == null
                || (!(mc.screen instanceof TitleScreen)
                    && !mc.screen.getClass().getSimpleName().equals("AccessibilityOnboardingScreen"))) {
            readyTicks = 0;
            return;
        }
        if (++readyTicks < 20) return;
        finished = true;
        var manager = mc.getModelManager();
        var missing = manager.getMissingModel();
        int checked = 0;
        for (Block block : new Block[]{
                ModBlocks.PIZZA_OVEN, ModBlocks.CHEESE_VAT, ModBlocks.CHEESE_HOOK,
                ModBlocks.OAK_CHEESE_AGING_RACK, ModBlocks.BARREL,
                ModBlocks.BREWING_BARREL, ModBlocks.CRUSHING_TUB,
                ModBlocks.OLIVE_LEAVES, ModBlocks.ALMOND_LEAVES,
                ModBlocks.FRUIT_CHERRY_LEAVES, ModBlocks.GRAPE_LEAVES,
                ModBlocks.OLIVE_SIGN, ModBlocks.OLIVE_WALL_SIGN, ModBlocks.OLIVE_HANGING_SIGN, ModBlocks.OLIVE_WALL_HANGING_SIGN,
                ModBlocks.ALMOND_SIGN, ModBlocks.ALMOND_WALL_SIGN, ModBlocks.ALMOND_HANGING_SIGN, ModBlocks.ALMOND_WALL_HANGING_SIGN,
                ModBlocks.FRUIT_CHERRY_SIGN, ModBlocks.FRUIT_CHERRY_WALL_SIGN, ModBlocks.FRUIT_CHERRY_HANGING_SIGN, ModBlocks.FRUIT_CHERRY_WALL_HANGING_SIGN}) {
            for (var state : block.getStateDefinition().getPossibleStates()) {
                require(mc.getBlockRenderer().getBlockModel(state) != missing,
                    "Missing baked block model: " + state);
                checked++;
            }
        }
        require(manager.getModel(PizzaPeelItemModel.BASE) instanceof PizzaPeelItemModel,
            "Pizza peel custom model not installed");
        require(manager.getModel(PizzaPeelItemModel.LOADED) != missing,
            "Loaded pizza peel model missing");
        for (var item : new net.minecraft.world.item.Item[]{
                ModItems.RAW_PIZZA, ModItems.COOKED_PIZZA, ModItems.PIZZA_SLICE,
                ModItems.GRAPE_JUICE_BOTTLE, ModItems.WINE_BOTTLE}) {
            require(mc.getItemRenderer().getModel(new ItemStack(item), null, null, 0) != missing,
                "Missing baked item model: " + item);
        }
        var atlas = manager.getAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
        for (String sprite : new String[]{"block/liquid_surface/whey", "block/fluids/whey_still",
                "block/fluids/olive_oil_still", "block/fluids/olive_oil_flow",
                "block/fluids/apple_juice_still", "block/fluids/apple_juice_flow"}) {
            var id = dev.italiansdelight.common.registry.ModRegistries.id(sprite);
            require(atlas.getSprite(id).contents().name().equals(id), "Missing fluid sprite: " + id);
        }
        for (String wood : new String[]{"olive", "almond", "fruit_cherry"}) {
            for (String prefix : new String[]{"entity/signs/", "entity/signs/hanging/"}) {
                var id = dev.italiansdelight.common.registry.ModRegistries.id(prefix + wood);
                var signAtlas = manager.getAtlas(net.minecraft.client.renderer.Sheets.SIGN_SHEET);
                require(signAtlas.getSprite(id).contents().name().equals(id), "Missing sign sprite: " + id);
            }
        }
        boolean expectedJei = Boolean.getBoolean("italiansdelight.portClientSmokeJei");
        require(ModList.get().isLoaded("jei") == expectedJei, "Unexpected JEI runtime presence");
        ItaliansDelight.LOGGER.info("IDR_CLIENT_SMOKE_PASS states={} jei={}", checked, expectedJei);
        mc.stop();
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
