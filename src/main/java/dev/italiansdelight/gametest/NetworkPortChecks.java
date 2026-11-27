package dev.italiansdelight.gametest;

import dev.italiansdelight.ItaliansDelight;
import dev.italiansdelight.common.block.entity.*;
import dev.italiansdelight.common.block.entity.container.*;
import dev.italiansdelight.common.pizza.*;
import dev.italiansdelight.common.registry.*;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Exercise the real vanilla network codecs, without claiming a connected multiplayer session. */
final class NetworkPortChecks {
    private NetworkPortChecks() {}

    static void run(GameTestHelper helper) {
        var level = helper.getLevel();
        var topping = new ItemStack(Items.BEETROOT_SOUP);
        topping.set(DataComponents.CUSTOM_NAME, Component.literal("Network topping"));
        var application = new PizzaToppingApplication(1L,
            ModRegistries.id("pizza_toppings/tomato_sauce"), topping, 15, 15);
        var composition = new PizzaComposition(1, 0, "north", 2L, 42L, List.of(application));
        var payload = new PizzaItemData(composition, false);
        var raw = new ItemStack(ModItems.RAW_PIZZA);
        raw.set(ModDataComponents.PIZZA_CONTENTS, payload);
        var cooked = PizzaBaking.finish(raw, 1200).orElseThrow().cookedPizza();
        var slice = PizzaSliceFactory.create(payload, 0);
        var peel = new ItemStack(ModItems.PIZZA_PEEL);
        peel.set(ModDataComponents.PEEL_CARGO, new PizzaPeelCargo(cooked));
        var wine = new ItemStack(ModItems.WINE_BOTTLE);
        wine.set(ModDataComponents.WINE_QUALITY, 87);
        for (var stack : List.of(raw, cooked, slice, peel, wine)) {
            var received = roundTrip(helper, ItemStack.STREAM_CODEC, stack);
            helper.assertTrue(ItemStack.matches(stack, received),
                "Item network roundtrip lost data: " + stack);
        }

        // Initial chunk tag and incremental update packet must carry equivalent
        // NBT. Decode it into a fresh receiving BE, not the original object.
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.PIZZA_DOUGH);
        var dough = (PizzaDoughBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        dough.setComposition(composition);
        var doughMirror = (PizzaDoughBlockEntity) receive(helper, dough);
        helper.assertTrue(composition.equals(doughMirror.composition()), "Dough packet lost composition");

        helper.setBlock(pos, ModBlocks.COOKED_PIZZA);
        var pizza = (CookedPizzaBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        pizza.setPizza(payload);
        pizza.markServed(0);
        pizza.insertSlice(0, slice.get(ModDataComponents.PIZZA_SLICE_CONTENTS));
        var pizzaMirror = (CookedPizzaBlockEntity) receive(helper, pizza);
        helper.assertTrue(payload.equals(pizzaMirror.pizza()) && pizzaMirror.servedEver()
            && pizza.sliceAt(0).equals(pizzaMirror.sliceAt(0)), "Cooked pizza packet lost served/slice state");

        helper.setBlock(pos, ModBlocks.BARREL);
        var barrel = (BarrelBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        helper.assertTrue(barrel.insert("italiansdelight:grape_juice", 750), "Seed barrel");
        var barrelMirror = (BarrelBlockEntity) receive(helper, barrel);
        helper.assertTrue(barrelMirror.getAmount() == 750
            && barrelMirror.getFluidId().equals(barrel.getFluidId()), "Barrel packet lost fluid");

        helper.setBlock(pos, ModBlocks.FLUID_CAULDRON);
        var cauldron = (FluidCauldronBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        helper.assertTrue(cauldron.insert("italiansdelight:grape_juice", 500), "Seed cauldron");
        var cauldronMirror = (FluidCauldronBlockEntity) receive(helper, cauldron);
        helper.assertTrue(cauldronMirror.getAmount() == 500
            && cauldronMirror.getFluidId().equals(cauldron.getFluidId()), "Cauldron packet lost fluid");

        helper.setBlock(pos, ModBlocks.CRUSHING_TUB);
        var tub = (CrushingTubBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        helper.assertTrue(tub.insert(new ItemStack(Items.APPLE, 3)) == 3, "Seed crushing tub");
        tub.stomp();
        var tubMirror = (CrushingTubBlockEntity) receive(helper, tub);
        helper.assertTrue(ItemStack.matches(tub.getInput(), tubMirror.getInput())
            && tub.getAmount() == tubMirror.getAmount()
            && tub.getFluidId().equals(tubMirror.getFluidId()), "Crushing tub packet lost contents");

        helper.setBlock(pos, ModBlocks.BREWING_BARREL);
        var brew = (BrewingBarrelBlockEntity) level.getBlockEntity(helper.absolutePos(pos));
        helper.assertTrue(brew.insertJuice(250) && brew.insertCulture(75), "Seed brewer");
        var brewMirror = (BrewingBarrelBlockEntity) receive(helper, brew);
        helper.assertTrue(brewMirror.getJuiceAmount() == 250 && brewMirror.getCultureAmount() == 250
            && brewMirror.getCultureQuality() == 75, "Brewer packet lost liquid/culture quality");

        // Apply the native data packets to the same placeholder menus used by
        // the receiving client; check indices, ranges and read-only outputs.
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var inventory = player.getInventory();
        var bm = new BrewingBarrelMenu(1, inventory);
        applyData(helper, bm, new int[]{8000, 250, 1000, 11999, 1, 87, -1, 2, 1});
        helper.assertTrue(bm.slots.size() == 42 && bm.getInputAmount() == 8000
            && bm.getProgress() == 11999 && bm.getOutputQuality() == 87
            && bm.getCultureQuality() == -1 && bm.getCultureWineKind() == 1
            && !bm.slots.get(3).mayPlace(new ItemStack(Items.APPLE)), "Brewer menu synchronization");

        var vm = new CheeseVatMenu(2, inventory);
        applyData(helper, vm, new int[]{100, 200, 1, 1, 4000});
        helper.assertTrue(vm.slots.size() == 43 && vm.getCookProgressionScaled() == 12
            && vm.isHeated() && vm.isWheyFlowEnabled() && vm.getWheyAmount() == 4000,
            "Vat menu synchronization");
        helper.assertTrue(!vm.slots.get(6).mayPickup(player)
            && !vm.slots.get(6).mayPlace(new ItemStack(Items.APPLE))
            && vm.slots.get(6).remove(1).isEmpty()
            && vm.quickMoveStack(player, -1).isEmpty()
            && vm.quickMoveStack(player, vm.slots.size()).isEmpty()
            && !vm.clickMenuButton(player, 0), "Vat preview/button must not grant client-side items or mutations");

        var om = new PizzaOvenMenu(3, inventory);
        applyData(helper, om, new int[]{100, 200, 600, 1200});
        helper.assertTrue(om.slots.size() == 39 && om.getCookProgressionScaled() == 12
            && om.getBurnProgressionScaled() == 6
            && !om.slots.get(2).mayPlace(new ItemStack(Items.APPLE)), "Oven menu synchronization");
        ItaliansDelight.LOGGER.info("IDR_NETWORK_PORT_PASS items=5 block_entities=6 menus=3");
    }

    private static BlockEntity receive(GameTestHelper helper, BlockEntity sender) {
        var packet = (ClientboundBlockEntityDataPacket) sender.getUpdatePacket();
        helper.assertTrue(packet != null, "Missing BE packet " + sender.getType());
        var received = roundTrip(helper, ClientboundBlockEntityDataPacket.STREAM_CODEC, packet);
        var regs = helper.getLevel().registryAccess();
        helper.assertTrue(received.getType() == sender.getType()
            && received.getPos().equals(sender.getBlockPos())
            && received.getTag().equals(sender.getUpdateTag(regs)), "Packet/tag mismatch " + sender.getType());
        var mirror = received.getType().create(received.getPos(), sender.getBlockState());
        helper.assertTrue(mirror != null, "Unable to construct receiving block entity");
        mirror.handleUpdateTag(received.getTag(), regs);
        return mirror;
    }

    private static void applyData(GameTestHelper helper, AbstractContainerMenu menu, int[] values) {
        for (int i = 0; i < values.length; i++) {
            var sent = new ClientboundContainerSetDataPacket(menu.containerId, i, values[i]);
            var decoded = roundTrip(helper, ClientboundContainerSetDataPacket.STREAM_CODEC, sent);
            helper.assertTrue(decoded.getContainerId() == menu.containerId
                && decoded.getId() == i && decoded.getValue() == values[i],
                "Menu data packet changed index/value");
            menu.setData(decoded.getId(), decoded.getValue());
        }
    }

    private static <T> T roundTrip(GameTestHelper helper, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T value) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            codec.encode(buf, value);
            T decoded = codec.decode(buf);
            helper.assertTrue(!buf.isReadable(), "Network codec left unread bytes");
            return decoded;
        } finally {
            buf.release();
        }
    }
}
