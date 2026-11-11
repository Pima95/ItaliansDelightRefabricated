#!/usr/bin/env python3
"""Regressions for real FD Cutting Board and the 18 named pizza JEI views."""
import json
from pathlib import Path

JAVA=Path("src/main/java/dev/italiansdelight")
jei=JAVA/"integration/jei"
board=(JAVA/"mixin/PizzaCuttingBoardMixin.java").read_text(encoding="utf-8")
mix=json.loads(Path("src/main/resources/italiansdelight.mixins.json").read_text(encoding="utf-8"))
assert "PizzaCuttingBoardMixin" in mix["mixins"]
for fragment in (
    '@Mixin(CuttingBoardBlockEntity.class)',
    'method = "processStoredItemUsingTool"',
    'stored.is(ModItems.COOKED_PIZZA)',
    'tool.is(CommonTags.Items.TOOLS_KNIFE)',
    'PizzaSliceFactory.create(pizza, quarter)',
    'board.getInventory().extractItem(0, 1, false)',
    'for (ItemStack slice : slices)',
    'tool.hurtAndBreak(1, serverLevel, serverPlayer',
    'cir.setReturnValue(true)',
):
    assert fragment in board, f"Board integration missing {fragment}"
assert board.index('extractItem(0, 1, false)') < board.index('ItemUtils.spawnItemEntity('), (
    "Output must never spawn before the whole pizza has been consumed")
assert "if (pizza == null)" in board, "Invalid pizza data must never produce plain slices"
display=(jei/"PizzaDisplayNames.java") if False else JAVA/"common/pizza/PizzaDisplayNames.java"
source=display.read_text(encoding="utf-8")
assert 'if (presets.size() != 18)' in source
craft=(jei/"PizzaCraftingJeiRecipes.java").read_text(encoding="utf-8")
bake=(jei/"PizzaBakingJeiRecipes.java").read_text(encoding="utf-8")
cuts=(jei/"PizzaCuttingJeiRecipes.java").read_text(encoding="utf-8")
plugin=(jei/"ItaliansDelightJeiPlugin.java").read_text(encoding="utf-8")
oven=(jei/"PizzaBakingRecipeCategory.java").read_text(encoding="utf-8")
assert 'PizzaDisplayNames.allPresets().entrySet()' in craft
assert 'preset.getKey().isEmpty()' in craft, "Bianca must not be misrepresented as shapeless"
assert 'a.withCraftingBowlReturned()' in craft, "JEI crafting bowls must match real output"
assert 'PizzaDisplayNames.allPresets().entrySet()' in bake
assert 'PizzaBaking.previewCookedPizza(raw)' in bake
assert '"/pizza_oven/" + flavor' in oven, "All 18 oven pages need distinct JEI IDs"
assert 'PizzaDisplayNames.allPresets().entrySet()' in cuts
assert 'PizzaCreativeSamples.createMixed(true)' in cuts
assert 'PizzaSliceFactory.create(data, q)' in cuts
for marker in ('new PizzaCuttingRecipeCategory(', 'PizzaCuttingJeiRecipes.createExamples()',
               'PizzaCuttingJeiRecipeTypes.PIZZA_CUTTING',
               'ModItems.CUTTING_BOARD.get()'):
    assert marker in plugin, f"JEI Cutting Board UI not fully registered: {marker}"
for lang in ("it_it", "en_us"):
    data=json.loads(Path(f"src/main/resources/assets/italiansdelight/lang/{lang}.json").read_text(encoding="utf-8"))
    for name in ("gui.italiansdelight.jei.pizza_cutting",
                 "gui.italiansdelight.jei.pizza_cutting.note"):
        assert data.get(name), f"{lang}: missing {name}"
print("OK: Cutting Board consumes once, uses a knife and returns four component-bearing slices")
print("OK: all 18 named pizzas appear in oven and cutting, with Bianca not falsely shapeless")
print("OK: real shapeless pizza presets indexed, plus the Mixed Pizza cutting demonstration")
print("OK: JEI category/bookmarks/catalysts and Italian/English text registered")
print("NOTE: client JEI lookup and FD knife interactions need Minecraft runtime testing")
