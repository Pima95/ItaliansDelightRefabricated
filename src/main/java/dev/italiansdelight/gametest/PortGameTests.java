package dev.italiansdelight.gametest;

import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
import dev.italiansdelight.common.block.entity.BrewingBarrelBlockEntity;
import dev.italiansdelight.common.pizza.*;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModFluids;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidUtil;
import java.util.List;
import dev.italiansdelight.common.fluid.CompatibleFluidTank;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Development-only smoke checks for the 1.21.1 port. */
@GameTestHolder("italiansdelight")
@PrefixGameTestTemplate(false)
public final class PortGameTests {
    private PortGameTests() {}

    @GameTest(template = "port_empty")
    public static void recipeReload(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        for (String id : new String[] {
                "almond_cake", "olive_boat", "pizza_smelting", "pizza_smoking",
                "pizza_crafting", "cheese_vat/curd", "cooking/pasta_alla_carbonara",
                "cutting/almond_cake_slice" }) {
            helper.assertTrue(recipes.byKey(ModRegistries.id(id)).isPresent(),
                "Missing or invalid recipe: " + id);
        }
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void allPackagedRecipes(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        var resources = helper.getLevel().getServer().getResourceManager()
            .listResources("recipe", id -> id.getNamespace().equals("italiansdelight")
                && id.getPath().endsWith(".json"));
        helper.assertTrue(resources.size() >= 270, "Missing packaged recipe resources");
        for (var file : resources.keySet()) {
            String path = file.getPath();
            var id = ModRegistries.id(path.substring("recipe/".length(), path.length() - ".json".length()));
            helper.assertTrue(recipes.byKey(id).isPresent(), "Recipe skipped by reload: " + id);
        }
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void cheeseVatWaterIngredient(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        var water = net.minecraft.world.item.alchemy.PotionContents.createItemStack(
            net.minecraft.world.item.Items.POTION, net.minecraft.world.item.alchemy.Potions.WATER);
        var healing = net.minecraft.world.item.alchemy.PotionContents.createItemStack(
            net.minecraft.world.item.Items.POTION, net.minecraft.world.item.alchemy.Potions.HEALING);
        for (String id : new String[]{"cheese_vat/bocconcino", "cheese_vat/fresh_provolone", "cheese_vat/rennet"}) {
            var recipe = (dev.italiansdelight.common.crafting.CheeseVatRecipe)
                recipes.byKey(ModRegistries.id(id)).orElseThrow().value();
            var ingredient = recipe.getIngredientsList().getLast();
            helper.assertTrue(ingredient.test(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WATER_BUCKET)),
                id + " must accept water bucket");
            helper.assertTrue(ingredient.test(water), id + " must accept water bottle");
            helper.assertTrue(!ingredient.test(healing), id + " must reject healing potion");
            helper.assertTrue(!ingredient.test(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE)),
                id + " must reject empty bottle");
        }
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void quarterTank(GameTestHelper helper) {
        var tank = new CompatibleFluidTank(1000, true, () -> true, () -> {});
        var water = new FluidStack(Fluids.WATER, 300);
        helper.assertTrue(tank.fill(water, FluidAction.SIMULATE) == 250, "Quarter rounding");
        helper.assertTrue(tank.amountMb() == 0, "Simulation must not mutate");
        helper.assertTrue(tank.fill(water, FluidAction.EXECUTE) == 250, "Water fill");
        helper.assertTrue(tank.fill(new FluidStack(Fluids.LAVA, 250), FluidAction.EXECUTE) == 0,
            "Lava must not enter this tank");
        helper.assertTrue(tank.drain(249, FluidAction.EXECUTE).isEmpty(), "Sub-quarter drain");
        helper.assertTrue(tank.drain(250, FluidAction.EXECUTE).getAmount() == 250, "Quarter drain");
        helper.assertTrue(tank.isEmptyTank(), "Tank should be empty");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void pizzaComponentsAndBaking(GameTestHelper helper) {
        var regs = helper.getLevel().registryAccess();
        var sauce = new ItemStack(Items.BEETROOT_SOUP);
        sauce.set(DataComponents.CUSTOM_NAME, Component.literal("Test topping"));
        var application = new PizzaToppingApplication(1L,
            ModRegistries.id("pizza_toppings/tomato_sauce"), sauce, 15, 15);
        var independent = new PizzaToppingApplication(1L,
            application.canonicalId(), sauce.copy(), 15, 15);
        helper.assertTrue(application.equals(independent)
            && application.hashCode() == independent.hashCode(), "Topping value equality");
        sauce.setCount(2);
        helper.assertTrue(application.originalStack().getCount() == 1, "Topping must be defensive");
        var composition = new PizzaComposition(1, 0, "north", 2L, 42L, List.of(application));
        var payload = new PizzaItemData(composition, false);
        var raw = new ItemStack(ModItems.RAW_PIZZA);
        raw.set(ModDataComponents.PIZZA_CONTENTS, payload);
        raw.set(DataComponents.CUSTOM_NAME, Component.literal("Test pizza"));
        var restored = ItemStack.parse(regs, raw.save(regs)).orElseThrow();
        helper.assertTrue(ItemStack.matches(raw, restored), "Pizza save/reload must retain identical components");
        helper.assertTrue(PizzaBaking.finish(restored, 1199).isEmpty(), "No premature baking");
        var result = PizzaBaking.finish(restored, 1200).orElseThrow();
        helper.assertTrue(result.cookedPizza().is(ModItems.COOKED_PIZZA), "Cooked output");
        helper.assertTrue(payload.equals(result.cookedPizza().get(ModDataComponents.PIZZA_CONTENTS)), "Toppings survive baking");
        helper.assertTrue(Component.literal("Test pizza").equals(result.cookedPizza().get(DataComponents.CUSTOM_NAME)), "Custom name survives baking");
        helper.assertTrue(result.containers().size() == 1 && result.containers().getFirst().is(Items.BOWL)
            && result.containers().getFirst().getCount() == 1, "One deposited sauce returns one bowl");
        helper.assertTrue(restored.getCount() == 1 && restored.is(ModItems.RAW_PIZZA), "Preview does not consume input");
        var peel = new ItemStack(ModItems.PIZZA_PEEL);
        peel.set(ModDataComponents.PEEL_CARGO, new PizzaPeelCargo(result.cookedPizza()));
        var reloadedPeel = ItemStack.parse(regs, peel.save(regs)).orElseThrow();
        helper.assertTrue(ItemStack.matches(peel, reloadedPeel), "Peel cargo equality after save/reload");
        helper.assertTrue(peel.get(ModDataComponents.PEEL_CARGO).hashCode()
            == reloadedPeel.get(ModDataComponents.PEEL_CARGO).hashCode(), "Peel cargo hash");
        var first = PizzaSliceFactory.create(payload, 0);
        var second = PizzaSliceFactory.create(payload, 3);
        helper.assertTrue(first.get(ModDataComponents.PIZZA_SLICE_CONTENTS).equals(
            second.get(ModDataComponents.PIZZA_SLICE_CONTENTS)), "Same flavor across different quarters");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void nativeFluidContainersAndPersistence(GameTestHelper helper) {
        var fluid = new FluidStack(ModFluids.GRAPE_JUICE, 1000);
        var bottle = FluidUtil.getFluidHandler(new ItemStack(Items.GLASS_BOTTLE)).orElseThrow();
        helper.assertTrue(bottle.fill(fluid, FluidAction.SIMULATE) == 250
            && bottle.getContainer().is(Items.GLASS_BOTTLE), "Simulated bottle fill does not mutate");
        helper.assertTrue(bottle.fill(fluid.copyWithAmount(249), FluidAction.EXECUTE) == 0, "Reject underfill");
        helper.assertTrue(bottle.fill(fluid, FluidAction.EXECUTE) == 250
            && bottle.getContainer().is(ModItems.GRAPE_JUICE_BOTTLE), "Bottle fill");
        helper.assertTrue(bottle.drain(249, FluidAction.EXECUTE).isEmpty(), "Reject partial bottle drain");
        helper.assertTrue(bottle.drain(250, FluidAction.EXECUTE).getAmount() == 250
            && bottle.getContainer().is(Items.GLASS_BOTTLE), "Bottle drain returns container");
        var bucket = FluidUtil.getFluidHandler(new ItemStack(ModItems.GRAPE_JUICE_BUCKET)).orElseThrow();
        helper.assertTrue(bucket.drain(999, FluidAction.SIMULATE).isEmpty(), "Reject partial bucket drain");
        helper.assertTrue(bucket.drain(1000, FluidAction.EXECUTE).getAmount() == 1000
            && bucket.getContainer().is(Items.BUCKET), "Storage bucket drain returns container");
        var tank = new CompatibleFluidTank(1000, true, () -> true, () -> {});
        helper.assertTrue(tank.fill(fluid.copyWithAmount(750), FluidAction.EXECUTE) == 750, "Grape juice tank");
        helper.assertTrue(tank.fill(new FluidStack(Fluids.WATER, 250), FluidAction.EXECUTE) == 0, "Do not mix fluids");
        var data = new CompoundTag();
        tank.save(data, helper.getLevel().registryAccess());
        var restored = new CompatibleFluidTank(1000, true, () -> true, () -> {});
        restored.load(data, helper.getLevel().registryAccess(), false);
        helper.assertTrue(restored.amountMb() == 750
            && restored.currentVariant().is(ModFluids.GRAPE_JUICE), "Fluid ID and quantity survive reload");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void brewingBarrelTransaction(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(1, 1, 1));
        var state = ModBlocks.BREWING_BARREL.defaultBlockState();
        // Detached instance: exercise the exact tick handler without depending on world timing.
        var barrel = new BrewingBarrelBlockEntity(pos, state);
        barrel.setItem(BrewingBarrelBlockEntity.INPUT_IN, new ItemStack(ModItems.GRAPE_JUICE_BOTTLE));
        BrewingBarrelBlockEntity.serverTick(level, pos, state, barrel);
        helper.assertTrue(barrel.getJuiceAmount() == 250 && barrel.getItem(BrewingBarrelBlockEntity.INPUT_IN).isEmpty()
            && barrel.getItem(BrewingBarrelBlockEntity.INPUT_OUT).is(Items.GLASS_BOTTLE), "Juice vessel transaction");
        helper.assertTrue(barrel.insertCulture(60), "Insert culture");
        var data = barrel.saveWithoutMetadata(level.registryAccess());
        data.putInt("BrewTime", BrewingBarrelBlockEntity.BREW_TIME - 1);
        var restored = new BrewingBarrelBlockEntity(pos, state);
        restored.loadWithComponents(data, level.registryAccess());
        helper.assertTrue(restored.getJuiceAmount() == 250 && restored.getCultureAmount() == 250
            && restored.getCultureQuality() == 60, "Brewer NBT roundtrip");
        BrewingBarrelBlockEntity.serverTick(level, pos, state, restored);
        helper.assertTrue(restored.getJuiceAmount() == 0 && restored.getWineAmount() == 250
            && restored.getOutputKind() == BrewingBarrelBlockEntity.OUTPUT_WINE, "Fermentation conserves amount");
        int quality = restored.getWineQuality();
        helper.assertTrue(quality >= 59 && quality <= 64 && restored.getCultureAmount() == 250, "Culture quality and reuse");
        restored.setItem(BrewingBarrelBlockEntity.OUTPUT_IN, new ItemStack(Items.GLASS_BOTTLE));
        BrewingBarrelBlockEntity.serverTick(level, pos, state, restored);
        var wine = restored.getItem(BrewingBarrelBlockEntity.OUTPUT_OUT);
        helper.assertTrue(restored.getWineAmount() == 0 && wine.is(ModItems.WINE_BOTTLE)
            && wine.getOrDefault(ModDataComponents.WINE_QUALITY, -1) == quality, "Bottled wine retains quality");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void orchardBoatWood(GameTestHelper helper) {
        helper.assertTrue(net.minecraft.world.entity.vehicle.Boat.Type.byName("italiansdelight:olive")
                .getPlanks() == ModBlocks.OLIVE_PLANKS, "Olive boats must drop olive planks");
        helper.assertTrue(net.minecraft.world.entity.vehicle.Boat.Type.byName("italiansdelight:almond")
                .getPlanks() == ModBlocks.ALMOND_PLANKS, "Almond boats must drop almond planks");
        helper.assertTrue(net.minecraft.world.entity.vehicle.Boat.Type.byName("italiansdelight:fruit_cherry")
                .getPlanks() == ModBlocks.FRUIT_CHERRY_PLANKS, "Fruit cherry boats must drop fruit cherry planks");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void customBlockModels(GameTestHelper helper) {
        int checked = 0;
        for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
            var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals("italiansdelight")) continue;
            if (!(block instanceof dev.italiansdelight.common.block.AgingCheeseBlock
                    || block instanceof dev.italiansdelight.common.block.BarrelBlock
                    || block instanceof dev.italiansdelight.common.block.BrewingBarrelBlock
                    || block instanceof dev.italiansdelight.common.block.CheeseAgingRackBlock
                    || block instanceof dev.italiansdelight.common.block.CheeseHookBlock
                    || block instanceof dev.italiansdelight.common.block.CheeseVatBlock
                    || block instanceof dev.italiansdelight.common.block.CrushingTubBlock
                    || block instanceof dev.italiansdelight.common.block.PizzaOvenBlock)) continue;
            for (var state : block.getStateDefinition().getPossibleStates()) {
                helper.assertTrue(state.getRenderShape() == net.minecraft.world.level.block.RenderShape.MODEL,
                    "Missing world model render shape: " + id + " " + state);
            }
            checked++;
        }
        helper.assertTrue(checked >= 59, "Expected all custom block families and their wood variants");
        helper.assertTrue(ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE.defaultBlockState().getRenderShape()
            == net.minecraft.world.level.block.RenderShape.INVISIBLE,
            "Reserved hanging cheese space must remain invisible");
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void optionalThirstIntegration(GameTestHelper helper) {
        String profile = System.getProperty("italiansdelight.portThirst", "none");
        var mods = net.neoforged.fml.ModList.get();
        if (profile.equals("none")) {
            helper.succeed();
            return;
        }
        String modId = switch (profile) {
            case "twt" -> "thirst";
            case "twt2" -> "thirstwastaken2";
            case "tan" -> "toughasnails";
            default -> throw new IllegalStateException("Unknown thirst profile: " + profile);
        };
        helper.assertTrue(mods.isLoaded(modId), "Expected optional mod missing: " + modId);
        ItemStack[] beverages = {
            new ItemStack(ModItems.APPLE_JUICE_BOTTLE),
            new ItemStack(ModItems.GRAPE_JUICE_BOTTLE),
            new ItemStack(ModItems.WHITE_GRAPE_JUICE_BOTTLE),
            new ItemStack(ModItems.SHEEP_MILK_BOTTLE),
            new ItemStack(ModItems.WINE_BOTTLE)
        };
        int[] thirst = {6, 6, 6, 4, 2};
        int[] quenched = {4, 4, 4, 0, 0};
        try {
            if (profile.equals("twt2")) {
                var api = Class.forName("com.thirstwastaken2.api.ThirstApi");
                var values = api.getMethod("thirstValues", ItemStack.class);
                for (int i = 0; i < beverages.length; i++) {
                    int[] result = (int[]) values.invoke(null, beverages[i]);
                    helper.assertTrue(result != null && result.length >= 2
                        && result[0] == thirst[i] && result[1] == quenched[i],
                        "Unexpected TW2 hydration: " + beverages[i]);
                }
                int[] oil = (int[]) values.invoke(null, new ItemStack(ModItems.OLIVE_OIL_BOTTLE));
                helper.assertTrue(oil == null || oil[0] == 0, "Oil must not hydrate");
            } else if (profile.equals("tan")) {
                var tags = Class.forName("toughasnails.init.ModTags$Items");
                var values = tags.getMethod("getThirstRestored", ItemStack.class);
                for (int i = 0; i < beverages.length; i++) {
                    int result = (Integer) values.invoke(null, beverages[i]);
                    helper.assertTrue(result == thirst[i], "Unexpected TAN thirst: " + beverages[i]);
                    var hydration = net.minecraft.tags.TagKey.create(
                        net.minecraft.core.registries.Registries.ITEM,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("toughasnails",
                            "hydration/" + thirst[i] * 10 + "_hydration_drinks"));
                    helper.assertTrue(beverages[i].is(hydration), "Missing TAN hydration tag: " + beverages[i]);
                }
            }
            // TWT 2.1.5 has no matching public upstream 1.21.1 source on the
            // original repository; do not pretend its drink resolution is tested.
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Optional thirst API incompatible: " + modId, e);
        }
        dev.italiansdelight.ItaliansDelight.LOGGER.info("IDR_THIRST_PROFILE_PASS profile={}", profile);
        helper.succeed();
    }

    @GameTest(template = "port_empty")
    public static void barrelRegistration(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.BARREL);
        helper.assertTrue(helper.getLevel().getBlockEntity(helper.absolutePos(pos))
            instanceof BarrelBlockEntity, "Registered barrel must create its block entity");
        helper.succeed();
    }
}
