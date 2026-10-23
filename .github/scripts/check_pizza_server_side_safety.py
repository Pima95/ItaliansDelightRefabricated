#!/usr/bin/env python3
"""Check separation between server/common and client-only code.

Not a server boot or multiplayer test: a fast guard against accidental imports
of render/client classes in common Fabric bootstrap and shared mixin targets.
"""
import json
from pathlib import Path

root = Path("src/main/java/dev/italiansdelight")
mod = json.loads(Path("src/main/resources/fabric.mod.json").read_text(encoding="utf-8"))
mixins = json.loads(Path("src/main/resources/italiansdelight.mixins.json").read_text(encoding="utf-8"))
assert mod["environment"] == "*"
assert mod["entrypoints"]["main"] == ["dev.italiansdelight.ItaliansDelight"]
assert mod["entrypoints"]["client"] == ["dev.italiansdelight.ItaliansDelightClient"]
assert "client.CheeseHookParticlesMixin" in mixins["client"]
assert "client.CheeseHookParticlesMixin" not in mixins["mixins"]
for mixin in ("PizzaCuttingBoardMixin", "PizzaVanillaFurnaceMixin",
              "LevelMixin", "SheepMixin"):
    assert mixin in mixins["mixins"], f"{mixin}: server-side mixin was removed"
    f = root / "mixin" / f"{mixin}.java"
    src = f.read_text(encoding="utf-8")
    assert "net.minecraft.client." not in src, f"{mixin}: client classes in server mixin"
    assert "dev.italiansdelight.client." not in src, f"{mixin}: client renderer in server mixin"
    assert "net.fabricmc.fabric.api.client." not in src, f"{mixin}: Fabric client API in shared mixin"

common = (root / "ItaliansDelight.java").read_text(encoding="utf-8")
assert "implements ModInitializer" in common
for forbidden in ("net.minecraft.client.", "net.fabricmc.fabric.api.client.",
                  "ItaliansDelightClient", "client.render", "client.gui"):
    assert forbidden not in common, f"Common entrypoint has client reference: {forbidden}"

cut = (root / "mixin/PizzaCuttingBoardMixin.java").read_text(encoding="utf-8")
assert "if (!(board.getLevel() instanceof ServerLevel serverLevel))" in cut
assert "if (!(player instanceof ServerPlayer serverPlayer))" in cut
assert cut.index("board.getInventory().extractItem(0, 1, false)") < cut.index(
    "ItemUtils.spawnItemEntity("), "Cutting result spawned before server inventory commit"
furnace = (root / "mixin/PizzaVanillaFurnaceMixin.java").read_text(encoding="utf-8")
assert "@Mixin(AbstractFurnaceBlockEntity.class)" in furnace
assert "original.call(items, input, pizza.cookedPizza())" in furnace
assert furnace.index("original.call(items, input, pizza.cookedPizza())") < furnace.index(
    "PizzaFurnaceRemainders.deliver(level, pos, containers)"), (
        "Furnace returned bowls before completing the pizza burn transaction"
    )
oven = (root / "common/block/PizzaOvenBlock.java").read_text(encoding="utf-8")
assert "if (level instanceof ServerLevel server)" in oven
assert "PizzaOvenBlockEntity.serverTick(" in oven

print("OK: Fabric common/client entrypoints separated; shared mixins contain no client imports")
print("OK: cutting board and furnace produce output only after server-side item commit")
print("OK: Pizza Oven ticks only on the server")
print("NOTE: start a REAL dedicated server and test two connected clients before release")
