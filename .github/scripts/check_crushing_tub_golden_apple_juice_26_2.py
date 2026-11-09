"""Regression for the two original Rustic golden-apple Crushing Tub recipes."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[2]
java = root / "src/main/java/dev/italiansdelight"
res = root / "src/main/resources"
recipe = (java / "common/block/CrushingTubRecipes.java").read_text()
jei = (java / "integration/jei/CrushingTubJeiRecipe.java").read_text()
category = (java / "integration/jei/CrushingTubRecipeCategory.java").read_text()
items = (java / "common/registry/ModItems.java").read_text()
fluids = (java / "common/registry/ModFluids.java").read_text()
bridge = (java / "common/block/BarrelFluidContainers.java").read_text()
adapter = (java / "common/fluid/FabricFluidItemContainers.java").read_text()
client = (java / "ItaliansDelightClient.java").read_text()
render = (java / "client/render/LiquidSurfaceVisuals.java").read_text()
group = (java / "common/item/group/ModItemGroups.java").read_text()
info = (java / "integration/jei/ModFluidJeiInfo.java").read_text()
assert 'stack.is(Items.GOLDEN_APPLE)' in recipe
assert 'stack.is(Items.ENCHANTED_GOLDEN_APPLE)' in recipe
assert 'new Recipe("italiansdelight:golden_apple_juice", 100,' in recipe
assert 'new Recipe("italiansdelight:golden_apple_juice", 1000,' in recipe
assert recipe.count('new ItemStack(ModItems.APPLE_SEEDS)') >= 3
assert 'ModFluids.GOLDEN_APPLE_JUICE, 100,' in jei
assert 'ModFluids.GOLDEN_APPLE_JUICE, 1000,' in jei
assert 'public static final Item GOLDEN_APPLE_JUICE_BOTTLE' in items
assert 'new ContainedFluid(() -> ModItems.GOLDEN_APPLE_JUICE_BUCKET)' in fluids
assert 'storageOnly(ModFluids.GOLDEN_APPLE_JUICE,' in adapter
assert '"italiansdelight:golden_apple_juice", 250, Items.GLASS_BOTTLE' in bridge
assert 'if ("italiansdelight:golden_apple_juice".equals(id))' in bridge
assert 'registerContainedFluid(ModFluids.GOLDEN_APPLE_JUICE,' in client
assert 'ModRegistries.id("block/fluids/golden_apple_juice_still")' in client
assert 'Identifier.withDefaultNamespace("block/honey_block_top")' not in client
assert '"italiansdelight:golden_apple_juice", new Surface(' in render
assert 'ModRegistries.id("block/fluids/golden_apple_juice_still"), 0xFFFFFFFF' in render
atlas = json.loads((res / "assets/minecraft/atlases/blocks.json").read_text())
assert any(s.get("resource") == "italiansdelight:block/fluids/golden_apple_juice_still"
           for s in atlas["sources"])
for suffix in ("still", "flow"):
    texture = res / f"assets/italiansdelight/textures/block/fluids/golden_apple_juice_{suffix}.png"
    assert texture.is_file()
    meta = texture.with_suffix(".png.mcmeta")
    assert json.loads(meta.read_text()).get("animation") is not None
assert (res / "assets/italiansdelight/textures/block/fluids/golden_apple_juice_overlay.png").is_file()
assert 'output.accept(ModItems.GOLDEN_APPLE_JUICE_BOTTLE);' in group
assert 'new ItemStack(ModItems.GOLDEN_APPLE_JUICE_BOTTLE)' in info
assert 'private static final int WIDTH = 151;' in category
assert 'builder.addOutputSlot(93, 26)' in category
assert 'builder.addOutputSlot(126, 26)' in category
assert 'public static final Item GOLDEN_APPLE_JUICE_BUCKET' in items
assert 'storageOnly(ModFluids.GOLDEN_APPLE_JUICE,' in adapter
assert 'ModItems.GOLDEN_APPLE_JUICE_BUCKET, ModItems.GOLDEN_APPLE_JUICE_BOTTLE)' in adapter
assert '"italiansdelight:golden_apple_juice"' not in (
    res / "data/minecraft/tags/fluid/water.json").read_text()
tag=json.loads((res / "data/italiansdelight/tags/fluid/golden_apple_juice.json").read_text())
assert tag["values"] == ["italiansdelight:golden_apple_juice"]
definition = json.loads((res / "assets/italiansdelight/items/golden_apple_juice_bottle.json").read_text())
assert definition["model"]["model"] == "minecraft:item/potion"
assert definition["model"]["tints"][0]["type"] == "minecraft:constant"
for lang,name in [("it_it","Succo di mela d'oro"),("en_us","Golden Apple Juice")]:
    j=json.loads((res / f"assets/italiansdelight/lang/{lang}.json").read_text())
    assert j["block.italiansdelight.golden_apple_juice"] == name
    assert j["fluid.italiansdelight.golden_apple_juice"] == name
    assert j["item.italiansdelight.golden_apple_juice_bottle"]
    assert j["jei.italiansdelight.fluids.golden_apple_juice"]
print("OK: distinct golden apple juice fluid, two Rustic recipes, single bottle, JEI, all 15 tub variants")
