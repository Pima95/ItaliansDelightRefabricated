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
planting = read(java / "common/block/StakeCropPlanting.java")
seeds = read(java / "common/item/ChiliSeedsItem.java")
require("TOMATO_SEEDS" in planting and "CHILI_PEPPER_SEEDS" in planting,
        "Crop stake planting must accept both original FDR tomato and chili seeds")
require("clicked.above()" in planting and "Blocks.FARMLAND" in planting,
        "Player must be able to plant through the farmland base of a thin stake")
require("StakeCropPlanting.tryPlant(" in stake and "StakeCropPlanting.tryPlant(" in seeds,
        "Both block and seed item must use the same planting implementation")
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

# Ensure stake planting intercepts both the original FDR tomato seeds and
# Italian's Delight chili seeds before BlockItem placement is attempted.
planting = read(java / "common/block/StakeCropPlanting.java")
entrypoint = read(java / "ItaliansDelight.java")
require("UseBlockCallback.EVENT.register" in planting
        and "ModBlocks.CHILI_PEPPER_SEEDS" in planting
        and "ModItems.TOMATO_SEEDS.get()" in planting
        and "Blocks.FARMLAND" in planting
        and "StakeCropPlanting.register();" in entrypoint,
        "Stake seeds must be intercepted before normal BlockItem placement")

# The stake, tomato crop and chili crop all retain a single loop after
# replacing a post with a crop, with no duplicate rings on each span.
for source in (stake, read(java / "common/block/ChiliCropBlock.java"),
               read(java / "common/block/StakedTomatoBlock.java")):
    require("CropStakeBlock.ROPE_TIED" in source or "BooleanProperty ROPE_TIED" in source,
            "A support must preserve its knot after planting")
require("CropStakeBlock.isPost(neighbor)" in horizontal,
        "Horizontal ropes must recognize planted supports as posts")
for block in ("crop_stake", "chili_crop", "staked_tomatoes"):
    state = j(assets / f"blockstates/{block}.json")
    require(any(part.get("when") == {"rope_tied": "true"} and
                part.get("apply", {}).get("model") == "italiansdelight:block/crop_stake_rope_loop"
                for part in state["multipart"]),
            f"Missing rope loop on {block}")

# Pixel-art geometry must be Minecraft-grid-aligned: 1 model unit = 1
# original 16x16 texture pixel. Face UV dimensions must match their model
# dimensions exactly. This prevents stretching, including on knot ends.
models = [
    assets / f"models/block/{name}.json" for name in (
        "grape_rope_horizontal", "grape_rope_tie_negative",
        "grape_rope_tie_positive", "crop_stake_rope_loop"
    )
] + [
    Path("src/main/resources") / f"assets/farmersdelight/models/block/{name}.json"
    for name in ("rope_side", "rope_side_alt")
]
for model_path in models:
    model = j(model_path)
    require(model["textures"]["rope"] == "italiansdelight:block/vineyard_rope",
            f"Wrong rope art source: {model_path}")
    for element in model["elements"]:
        corners = element["from"] + element["to"]
        require(all(type(coord) is int for coord in corners),
                f"Fractional model geometry in {model_path}: {corners}")
        dx = element["to"][0] - element["from"][0]
        dy = element["to"][1] - element["from"][1]
        dz = element["to"][2] - element["from"][2]
        require(dx > 0 and dy > 0 and dz > 0,
                f"Zero-thickness or inverted face in {model_path}")
        for face, details in element["faces"].items():
            if face in ("north", "south"):
                expected = (dx, dy)
            elif face in ("east", "west"):
                expected = (dz, dy)
            else:
                expected = (dx, dz)
            uv = details["uv"]
            require(all(type(coord) is int for coord in uv),
                    f"Fractional UV in {model_path}")
            require((abs(uv[2] - uv[0]), abs(uv[3] - uv[1])) == expected,
                    f"Stretched pixel UV in {model_path} {face}: {uv} vs {expected}")
            require(all(0 <= coord <= 16 for coord in uv),
                    f"UV outside 16x16 atlas in {model_path} {face}: {uv}")
loop = j(assets / "models/block/crop_stake_rope_loop.json")
require(len(loop["elements"]) == 5, "Leash-style loop needs 4 sides and a knot")
loop_min_x = min(el["from"][0] for el in loop["elements"])
loop_max_x = max(el["to"][0] for el in loop["elements"])
require(loop_min_x < 6 and loop_max_x > 10,
        "The knot must wrap around the 4px post, protruding at least 1px")
png = assets / "textures/block/vineyard_rope.png"
require(png.read_bytes()[:8] == bytes.fromhex("89504e470d0a1a0a"),
        "Vineyard rope pixel texture PNG is missing")
print("PASSED: exact 1px UV geometry, leash-style rope loop and two seed targets")
