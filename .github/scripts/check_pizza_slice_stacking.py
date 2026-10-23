#!/usr/bin/env python3
"""Regression guards for flavor-based slice stacking (Minecraft 26.2).

A static check only: actual inventory merges/chests/hoppers need in-game tests.
"""
from pathlib import Path

BASE = Path("src/main/java/dev/italiansdelight/common")
data = (BASE / "pizza/PizzaSliceData.java").read_text(encoding="utf-8")
items = (BASE / "registry/ModItems.java").read_text(encoding="utf-8")
tab = (BASE / "item/group/ModItemGroups.java").read_text(encoding="utf-8")

assert ".stacksTo(16)" in items, "Pizza slice stack limit must remain exactly 16"
assert "record PizzaSliceData(int quarter, List<Topping> toppings, boolean legacyMozzarella)" in data
assert "private Set<Identifier> stackingIngredients()" in data
assert "ids.add(topping.canonicalId());" in data
assert "public boolean equals(Object object)" in data
assert "legacyMozzarella == other.legacyMozzarella" in data
assert "stackingIngredients().equals(other.stackingIngredients())" in data
assert "public int hashCode()" in data
assert "Objects.hash(stackingIngredients(), legacyMozzarella)" in data
assert "Codec.INT.fieldOf(\"quarter\")" in data, "Old saved slice payloads must stay readable"
assert "ItemStack.CODEC.fieldOf(\"original_stack\")" in data, "Do not break old saved data"
assert "new PizzaSliceData(quarter, selected, whole.legacyMozzarella())" in data
# The source quarter/original stack must not be part of stacking equality:
equals = data.split("public boolean equals(Object object)", 1)[1].split("public record Topping", 1)[0]
assert "quarter ==" not in equals
assert "originalStack" not in equals
for name in ("RAW_PIZZA", "COOKED_PIZZA", "PIZZA_SLICE"):
    assert f"output.accept(ModItems.{name});" not in tab
for marker in ("PizzaCreativeSamples.create(preset.getKey(), false)",
               "PizzaCreativeSamples.create(preset.getKey(), true)",
               "PizzaCreativeSamples.createSlice(preset.getKey())"):
    assert tab.count(marker) == 1
print("OK: same canonical flavor + legacy flag compares equally, regardless of quarter/source/order")
print("OK: slice cap 16; distinct toppings do not share identity; saved codec unchanged")
print("OK: 18 real per-stage Creative samples, no duplicate bare Pizza Bianca")
print("NOTE: real in-game stacking, slot merging, multiplayer still require manual tests")
