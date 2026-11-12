#!/usr/bin/env python3
"""1.2.0 wine/vineyard integration audit; standard library only, CI safe."""
import json
from pathlib import Path

root = Path("src/main/resources")
java = Path("src/main/java/dev/italiansdelight")
assets = root / "assets/italiansdelight"
data = root / "data"

def read(path):
    assert path.is_file(), f"Missing: {path}"
    return path.read_text(encoding="utf-8")
def j(path): return json.loads(read(path))
def require(cond,msg): assert cond,msg

recipe = j(data / "italiansdelight/recipe/crop_stake.json")
require(recipe["pattern"] == ["L","L"],"Two logs must craft one set of stakes")
require(recipe["key"]["L"] == "#italiansdelight:stake_logs","Recipe must use shared log tags")
tag = j(data / "italiansdelight/tags/item/stake_logs.json")
refs = {x.get("id") if isinstance(x,dict) else x for x in tag["values"]}
require({"#minecraft:logs","#c:logs"} <= refs,"Missing common tags")
require(not (data / "farmersdelight/tags/block/ropes.json").exists(),
        "Reusing a stake as FDR consumable rope would destroy it on tomato climbs")

for path in ("blockstates/grape_leaves.json","blockstates/grape_stem.json",
             "blockstates/chili_crop.json","blockstates/chili_on_rope.json",
             "blockstates/crop_stake.json","blockstates/staked_tomatoes.json",
             "models/item/wine_bucket.json","items/wine_bucket.json"):
    j(assets / path)
for name in ("wine_bucket","grape_juice_bucket"):
    path = assets / f"textures/item/{name}.png"
    require(path.is_file() and path.read_bytes()[:8] == b"\x89PNG\r\n\x1a\n",
            f"Missing PNG {name}")
for path in ("textures/block/fluids/grape_juice_still.png",
             "textures/block/fluids/booze/wine_still.png",
             "textures/block/grapes.png"):
    require((assets/path).is_file(),f"Missing fluid or grape texture: {path}")
en = j(assets / "lang/en_us.json")
it = j(assets / "lang/it_it.json")
for key in ("block.italiansdelight.grape_juice","block.italiansdelight.wine",
            "item.italiansdelight.wine_bucket","block.italiansdelight.crop_stake"):
    require(key in en and key in it,f"Missing JEI or crop translation {key}")
client = read(Path("src/main/java/dev/italiansdelight/ItaliansDelightClient.java"))
require('registerContainedFluid(ModFluids.WINE,' in client,"Wine fluid JEI rendering not registered")
require('ModRegistries.id("block/fluids/booze/wine_still")' in client,"Wrong wine sprite")
stake = read(java / "common/block/CropStakeBlock.java")
require("TOMATO_SEEDS" in stake and "CHILI_PEPPER_SEEDS" in stake,
        "Crop stake must accept both original FDR tomato and chili seeds")
for c in ("GrapeStemBlock","GrapeLeavesBlock","ChiliCropBlock","HangingChiliBlock",
          "StakedTomatoBlock"):
    read(java / f"common/block/{c}.java")
print("PASSED: tagged stake recipe, rope reuse, crops, wine/juice JEI names and sprites")

# 1.2.0 placement regression checks
stake_source = read(java / "common/block/CropStakeBlock.java")
block_registry = read(java / "common/registry/ModBlocks.java")
place = read(java / "common/block/HorizontalRopePlacement.java")
horizontal = read(java / "common/block/HorizontalRopeBlock.java")
root = read(java / "common/block/GrapeStemBlock.java")
canopy = read(java / "common/block/GrapeLeavesBlock.java")
require(".setId(CROP_STAKE_KEY).noCollision()" in block_registry,
        "Crop stake must not compact farmland")
require("return Shapes.empty();" in stake_source,
        "Crop stake collision must be empty above farmland")
require("UseBlockCallback.EVENT.register" in place and
        "isSecondaryUseActive()" in place and
        "ModItems.ROPE.get()" in place and
        "rope.shrink(1)" in place,
        "Shift-place must consume a single original FDR rope item")
require("EnumProperty.create(\"axis\"" in horizontal,
        "Horizontal rope must preserve horizontal axis")
require("ModBlocks.HORIZONTAL_ROPE" in root and "ModBlocks.HORIZONTAL_ROPE" in canopy,
        "Grapes must grow on and restore horizontal spans")
require(j(assets/"items/grape_juice_bottle.json")["model"]["model"] ==
        "minecraft:item/potion",
        "Grape juice must use vanilla bottle model")
require(j(data/"italiansdelight/loot_table/blocks/horizontal_rope.json")
        ["pools"][0]["entries"][0]["name"] == "farmersdelight:rope",
        "Horizontal rope must refund original FDR rope")
