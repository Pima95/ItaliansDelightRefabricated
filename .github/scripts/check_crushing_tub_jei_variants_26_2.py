"""Validate every registered Crushing Tub variant appears as a JEI catalyst.

This is a static API/integration check, not a replacement for in-game JEI testing.
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
java = ROOT / "src/main/java/dev/italiansdelight"
plugin = (java / "integration/jei/ItaliansDelightJeiPlugin.java").read_text()
category = (java / "integration/jei/CrushingTubRecipeCategory.java").read_text()
modblocks = (java / "common/registry/ModBlocks.java").read_text()
variants = ["CRUSHING_TUB","SPRUCE_CRUSHING_TUB","BIRCH_CRUSHING_TUB","JUNGLE_CRUSHING_TUB","ACACIA_CRUSHING_TUB","DARK_OAK_CRUSHING_TUB","MANGROVE_CRUSHING_TUB","CHERRY_CRUSHING_TUB","PALE_OAK_CRUSHING_TUB","BAMBOO_CRUSHING_TUB","CRIMSON_CRUSHING_TUB","WARPED_CRUSHING_TUB","OLIVE_CRUSHING_TUB","ALMOND_CRUSHING_TUB","FRUIT_CHERRY_CRUSHING_TUB"]
assert len(variants) == len(set(variants)) == 15
for name in variants:
    assert f"ModBlocks.{name}" in plugin, f"Missing JEI catalyst: {name}"
    assert f"Block {name} =" in modblocks, f"Missing world block: {name}"
assert "for (net.minecraft.world.level.block.Block tub : new net.minecraft.world.level.block.Block[]" in plugin
assert "registration.addCraftingStation(" in plugin
assert "CrushingTubJeiRecipeTypes.CRUSHING_TUB," in plugin
assert "new ItemStack(tub)" in plugin
assert 'Component.translatable("gui.italiansdelight.jei.crushing_tub")' in category
for language, expected in [("it_it", "Tino di pigiatura"), ("en_us", "Crushing Tub")]:
    translations = json.loads(
        (ROOT / f"src/main/resources/assets/italiansdelight/lang/{language}.json").read_text()
    )
    assert translations["gui.italiansdelight.jei.crushing_tub"] == expected
assert "private static final int WIDTH = 151;" in category  # preserve accepted GUI
assert "builder.addInputSlot(9, 26)" in category
assert "builder.addOutputSlot(93, 26)" in category
assert "builder.addOutputSlot(126, 26)" in category
print("OK: all 15 Crushing Tub variants are JEI catalysts, localized, accepted GUI untouched")
