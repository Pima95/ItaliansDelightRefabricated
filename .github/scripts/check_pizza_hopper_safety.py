#!/usr/bin/env python3
"""Static pizza hopper and remainder regression checks (not a game integration test)."""
from pathlib import Path

OVEN = Path("src/main/java/dev/italiansdelight/common/block/entity/PizzaOvenBlockEntity.java").read_text(encoding="utf-8")
VANILLA = Path("src/main/java/dev/italiansdelight/common/pizza/PizzaFurnaceRemainders.java").read_text(encoding="utf-8")
MIXIN = Path("src/main/java/dev/italiansdelight/mixin/PizzaVanillaFurnaceMixin.java").read_text(encoding="utf-8")


def require(test: bool, message: str) -> None:
    if not test:
        raise AssertionError(message)


for path, source in [("PizzaOvenBlockEntity", OVEN), ("PizzaFurnaceRemainders", VANILLA)]:
    require("ItemStack.isSameItemSameComponents(existing, left)" in source,
            path + ": cannot merge bowls by item type alone")
    require("!existing.isEmpty()" in source and "hopper.setItem(" in source,
            path + ": expected slot insertion with occupied-stack checks")
    require("existing.grow(moved);" in source and "left.shrink(moved);" in source,
            path + ": expected bounded remainder transfer")
    require("if (!left.isEmpty())" in source and "Containers.dropItemStack(" in source,
            path + ": overflow must be dropped rather than lost")

require("private static final int[] FROM_TOP = {INPUT};" in OVEN, "Top must be pizza input")
require("private static final int[] FROM_SIDES = {FUEL};" in OVEN, "Sides must be fuel only")
require("private static final int[] FROM_BOTTOM = {RESULT, FUEL};" in OVEN,
        "Bottom must expose finished output plus empty lava bucket")
require("stack.is(ModItems.COOKED_PIZZA)" in OVEN and "stack.is(Items.BUCKET)" in OVEN,
        "Bottom extraction may take cooked pizza or empty lava bucket")
require("side == Direction.DOWN" in OVEN and "side == Direction.UP" in OVEN,
        "Sided inventory checks are required")
require("items.get(RESULT).isEmpty()" in OVEN,
        "Only one pizza total may occupy the Pizza Oven")
require("ContainerHelper.loadAllItems(input, items)" in OVEN and
        "ContainerHelper.saveAllItems(output, items)" in OVEN,
        "Oven must persist complete ItemStack data")
require('input.getIntOr("CookTime", 0)' in OVEN and 'output.putInt("CookTime", cookingProgress)' in OVEN,
        "Cooking progress must persist in oven, not pizza")
require("PizzaFurnaceRemainders.deliver(level, pos, containers);" in MIXIN,
        "Vanilla furnace/smoker must use same remainders policy")

print("OK: Pizza Oven sided inventory, single-pizza limit and persistence invariants")
print("OK: bowl returns preserve all stack components across all three ovens")
print("NOTE: static source audit; runtime automation and persistence need Survival testing")
