#!/usr/bin/env python3
"""Guard searchable, real JEI pizza item variations (not just recipe examples)."""
from pathlib import Path

root=Path("src/main/java/dev/italiansdelight")
jei=root/"integration/jei"
plugin=(jei/"ItaliansDelightJeiPlugin.java").read_text(encoding="utf-8")
samples=(jei/"PizzaJeiIngredientSamples.java").read_text(encoding="utf-8")
presets=(root/"common/pizza/PizzaDisplayNames.java").read_text(encoding="utf-8")
creative=(root/"common/pizza/PizzaCreativeSamples.java").read_text(encoding="utf-8")
assert "presets.size() != 18" in presets, "One 18-recipe canonical name catalogue"
assert "void registerExtraIngredients(IExtraIngredientRegistration registration)" in plugin
assert "registration.addExtraItemStacks(PizzaJeiIngredientSamples.createAll())" in plugin
assert "void registerItemSubtypes(ISubtypeRegistration registration)" in plugin
for item in ("RAW_PIZZA","COOKED_PIZZA","PIZZA_SLICE"):
    assert f"registration.registerSubtypeInterpreter(ModItems.{item}," in plugin, (
        f"JEI must distinguish saved {item} flavor components")
assert "for (Set<Identifier> recipe : PizzaDisplayNames.allPresets().keySet())" in samples
for line in ("stacks.add(PizzaCreativeSamples.create(recipe, false))",
             "stacks.add(PizzaCreativeSamples.create(recipe, true))",
             "stacks.add(PizzaCreativeSamples.createSlice(recipe))",
             "stacks.add(PizzaCreativeSamples.createMixed(false))",
             "stacks.add(PizzaCreativeSamples.createMixed(true))"):
    assert line in samples, f"Missing a raw/cooked/slice/mixed JEI item: {line}"
assert "new ArrayList<>(3 * 18 + 2)" in samples
assert "PizzaDisplayNames.wholePresetId(data)" in samples
assert "PizzaDisplayNames.slicePresetId(data)" in samples
assert "data.composition().toppingsForQuarter(quarter)" in samples
assert ".map(PizzaSliceData.Topping::canonicalId)" in samples
assert ".sorted(Comparator.naturalOrder())" in samples
assert "application.sequence()" not in samples and "renderSeed()" not in samples, (
    "JEI item subtypes must ignore source application and random placement metadata")
assert "PizzaSliceFactory.create(pizza, 0)" in creative, (
    "JEI slices must have genuine per-quarter food and model metadata")
assert "PizzaDisplayNames.nameWholeStack(stack, data)" in creative
print("OK: JEI sidebar includes 18 raw + 18 cooked + 18 slices + 2 mixed entries")
print("OK: subtype lookup treats equal recipes as equal despite saved placement details")
print("OK: whole and slice items use their approved genuine names/models/nutrition")
print("NOTE: player-facing search and duplicate-filtering must be tested in client JEI")
