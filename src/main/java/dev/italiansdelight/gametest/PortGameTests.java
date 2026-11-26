package dev.italiansdelight.gametest;

import dev.italiansdelight.common.block.entity.BarrelBlockEntity;
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
    public static void barrelRegistration(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.BARREL);
        helper.assertTrue(helper.getLevel().getBlockEntity(helper.absolutePos(pos))
            instanceof BarrelBlockEntity, "Registered barrel must create its block entity");
        helper.succeed();
    }
}
