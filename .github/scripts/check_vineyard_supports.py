#!/usr/bin/env python3
"""Minecraft 26.3 vineyard and decorative pole regression audit (stdlib only)."""
import json
from pathlib import Path

src = Path("src/main/java/dev/italiansdelight")
resources = Path("src/main/resources")
assets = resources / "assets/italiansdelight"
data = resources / "data"
woods = ("oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
         "mangrove", "cherry", "pale_oak", "poplar", "bamboo", "crimson",
         "warped", "olive", "almond", "fruit_cherry")

def load(path):
    assert path.is_file(), f"Missing {path}"
    return json.loads(path.read_text(encoding="utf-8"))

def read(path):
    assert path.is_file(), f"Missing {path}"
    return path.read_text(encoding="utf-8")

stake = read(src/"common/block/CropStakeBlock.java")
chili = read(src/"common/block/ChiliCropBlock.java")
hanging = read(src/"common/block/HangingChiliBlock.java")
registry = read(src/"common/registry/ModBlocks.java")
creative = read(src/"common/item/group/ModItemGroups.java")
placement = read(src/"common/block/HorizontalRopePlacement.java")
rope = read(src/"common/block/HorizontalRopeBlock.java")
grape = read(src/"common/block/GrapeStemBlock.java")
init = read(src/"ItaliansDelight.java")
seed = read(src/"common/item/ChiliSeedsItem.java")

# No crops can be planted onto decorative stakes.
assert "extends RotatedPillarBlock" in stake
assert "IntegerProperty CROP" not in stake
assert "StakeCropPlanting" not in init and "StakeCropBreaking" not in init
assert not (src/"common/block/StakeCropPlanting.java").exists()
assert not (src/"common/block/StakeCropBreaking.java").exists()
assert "StakeCropPlanting" not in seed
assert "getStateForPlacement" in stake and "super.getStateForPlacement(ctx)" in stake
assert "case X -> X_SHAPE" in stake and "case Z -> Z_SHAPE" in stake
assert "case Y -> POST" in stake
assert "getCollisionShape" in stake and "return shape(state)" in stake
assert "state.getBlock() instanceof CropStakeBlock" in stake

# Rope interactions stay the same, with only variant-aware post identification.
assert "CropStakeBlock.isPost" in rope
assert "CropStakeBlock.isPost(level.getBlockState(anchor))" in placement
assert "ModItems.ROPE.get()" in placement
assert "rope.shrink(1)" in placement
assert "isSecondaryUseActive()" in placement
assert "return rope.is(ModBlocks.HORIZONTAL_ROPE)" in grape
assert not any((resources/"assets/farmersdelight"/p).exists() for p in
  ("models/block/rope_side.json", "models/block/rope_side_alt.json",
   "textures/block/rope.png"))

# Chili root must be an ordinary crop; only FDR vertical ropes are consumed.
assert "super.useOn" not in seed or "StakeCropPlanting" not in seed
assert ".setValue(STAKED, false)" in chili
assert "support.is(ModBlocks.CROP_STAKE)" not in chili
assert "CropStakeBlock.CROP" not in chili
assert "canClimb(" in chili and "ModBlocks.ROPE.get()" in chili
assert "ModBlocks.CHILI_ON_ROPE.defaultBlockState()" in chili
assert "vineHeight(level, pos) >= 3" in chili
assert "CropStakeBlock.hasCrop" not in hanging
assert "shouldRestoreRope()" in chili and "RopeBlock.getStateWithConnections" in chili
assert "ModBlocks.CROP_STAKE.defaultBlockState()" not in chili
assert not (src/"mixin/TomatoOnStakeSupportMixin.java").exists()
assert "TomatoOnStakeSupportMixin" not in load(resources/"italiansdelight.mixins.json")["mixins"]
assert "TOMATO_CROP.get()" in read(src/"common/block/StakedTomatoBlock.java")

root = load(assets/"blockstates/chili_crop.json")
for age in range(8):
    assert any(part.get("when") == {"age":str(age)}
        and part["apply"]["model"] ==
        f"italiansdelight:block/chili_free_stage_{0 if age<2 else 1 if age<4 else 2 if age<7 else 3}"
        for part in root["multipart"])
assert all("staked" not in part.get("when",{}) for part in root["multipart"])
for i in range(4):
    assert load(assets/f"models/block/chili_free_stage_{i}.json")["textures"]["cross"] == f"italiansdelight:block/chili_crop_{i}"
    assert not (assets/f"models/block/chili_stage_{i}.json").exists()

# All vanilla + 26.3 + addon wood types, same model geometry, own recipe/drop.
en=load(assets/"lang/en_us.json")
it=load(assets/"lang/it_it.json")
farmtag=load(data/"minecraft/tags/block/maintains_farmland.json")
for wood in woods:
    ident="crop_stake" if wood=="oak" else f"{wood}_crop_stake"
    bid=f"italiansdelight:{ident}"
    assert f"block.italiansdelight.{ident}" in en and f"block.italiansdelight.{ident}" in it
    if wood!="oak":
        assert f'{wood.upper()}_CROP_STAKE' in registry
        assert f'output.accept(ModBlocks.{wood.upper()}_CROP_STAKE)' in creative
    assert bid in farmtag["values"]
    states=load(assets/f"blockstates/{ident}.json")["multipart"]
    for axis in ("x","y","z"):
        assert any(part.get("when")=={"axis":axis} and
            part["apply"]["model"] == f"italiansdelight:block/{ident}"
            for part in states)
        assert any(part.get("when")=={"axis":axis,"rope_tied":"true"} and
            part["apply"]["model"] == "italiansdelight:block/crop_stake_rope_loop"
            for part in states)
    assert not any("crop" in part.get("when",{}) for part in states)
    if wood!="oak":
        model=load(assets/f"models/block/{ident}.json")
        assert model["parent"]=="italiansdelight:block/crop_stake"
        assert load(assets/f"models/item/{ident}.json")["parent"]==f"italiansdelight:block/{ident}"
        assert load(assets/f"items/{ident}.json")["model"]["model"]==f"italiansdelight:item/{ident}"
        assert load(data/f"italiansdelight/loot_table/blocks/{ident}.json")["pools"][0]["entries"][0]["name"]==bid
    recipe=load(data/f"italiansdelight/recipe/{ident}.json")
    assert recipe["pattern"]==["L","L"] and recipe["result"]=={"id":bid,"count":2}

# Preserve the grape canopy model and the wine fluids.
assert load(assets/"models/block/grape_leaves_1.json")["elements"][0]["to"]==[13,13,16]
assert load(assets/"items/grape_juice_bottle.json")["model"]["model"]=="minecraft:item/potion"
assert "registerContainedFluid(ModFluids.WINE" in read(src/"ItaliansDelightClient.java")
for name in ("wine_bucket","grape_juice_bucket"):
    assert (assets/f"textures/item/{name}.png").read_bytes()[:8] == bytes.fromhex("89504e470d0a1a0a")

# Chili art must keep the 16x16 pixel grid and reuse one crop texture for
# both freestanding and rope-borne crop model stages.
import struct
for stage in range(4):
    sprite=(assets/f"textures/block/chili_crop_{stage}.png").read_bytes()
    assert sprite[:8]==bytes.fromhex("89504e470d0a1a0a")
    assert struct.unpack(">II",sprite[16:24])==(16,16),f"Chili stage {stage} must stay 16x16"
    assert sprite[25]==6,f"Chili stage {stage} needs RGBA transparency"
    assert load(assets/f"models/block/chili_rope_stage_{stage}.json")["textures"]["crop"]==f"italiansdelight:block/chili_crop_{stage}"

# Wild pepper uses its own vegetation block and worldgen, but drops the
# existing crop seeds so no incompatible duplicate seed item is introduced.
assert "WildChiliBlock" in registry and 'ModRegistries.id("wild_chili")' in registry
assert "output.accept(ModBlocks.WILD_CHILI)" in creative
assert "extends WildBasilBlock" in read(src/"common/block/WildChiliBlock.java")
assert "wild_chili_patch" in read(src/"common/registry/ModWorldGeneration.java")
assert "Biomes.SAVANNA" in read(src/"common/registry/ModWorldGeneration.java")
assert "block.italiansdelight.wild_chili" in en and "block.italiansdelight.wild_chili" in it
assert load(assets/"blockstates/wild_chili.json")["variants"][""]["model"]=="italiansdelight:block/wild_chili"
assert load(assets/"models/block/wild_chili.json")["textures"]["cross"]=="italiansdelight:block/wild_chili"
assert load(assets/"items/wild_chili.json")["model"]["model"]=="italiansdelight:item/wild_chili"
assert load(assets/"models/item/wild_chili.json")["parent"]=="italiansdelight:block/wild_chili"
wild=(assets/"textures/block/wild_chili.png").read_bytes()
assert wild[:8]==bytes.fromhex("89504e470d0a1a0a")
assert struct.unpack(">II",wild[16:24])==(16,16)
wildloot=load(data/"italiansdelight/loot_table/blocks/wild_chili.json")
assert wildloot["pools"][0]["entries"][0]["name"]=="italiansdelight:chili_pepper_seeds"
wildfeature=load(data/"italiansdelight/worldgen/feature/wild_chili.json")
assert wildfeature["to_place"]["state"]["id"]=="italiansdelight:wild_chili"
wildplaced=load(data/"italiansdelight/worldgen/placed_feature/wild_chili_patch.json")
assert wildplaced["feature"]=="italiansdelight:wild_chili"
assert wildplaced["placement"][0]=={"type":"minecraft:rarity_filter","chance":120}
# Bamboo end grain now matches the warm plank color, not the green block cap.
bamboo=load(assets/"models/block/bamboo_crop_stake.json")["textures"]
assert bamboo["2"]==bamboo["0"]=="minecraft:block/bamboo_planks"
print("PASSED: revised 16x16 chili/rope art, naturally-spawning wild chili, seed drops and warm bamboo caps")

print("PASSED: 16 decorative wood stakes, 3 axes, rope integration unchanged, free chili + FDR climbing, grape and wine")
