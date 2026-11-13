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

# Cultivated chili now follows Farmer's Delight tomato classes literally:
# the same CropBlock growth/bonemeal tick, no detached custom grow algorithm.
# Eight old ages and both registry IDs are retained for save compatibility.
assert "extends TomatoBlock" in chili and "super(properties)" in chili
assert "BlockStateProperties.AGE_7" in chili and "getMaxAge()" in chili
assert "getClimbingState" in chili and "ModBlocks.CHILI_ON_ROPE.defaultBlockState()" in chili
assert "climbRopeAbove" in chili and "height >= 3" in chili
assert "extends ChiliCropBlock implements ReplaceOnRemoval" in hanging
assert "super(properties, true)" in hanging
assert "HangingTomatoBlock.placeRope" in hanging
assert "builder.add(CHILI_AGE);" in hanging
assert "builder.add(CHILI_AGE, ROPELOGGED);" in chili
assert "StakeSoilTilling.isFarmland" in chili
assert "CropStakeBlock.CROP" not in chili
assert "crop_stake" not in chili.lower()
assert "current.setValue(CHILI_AGE, 0)" in chili
assert not (src/"common/item/ChiliSeedsItem.java").exists()
assert "new BlockItem(CHILI_CROP" in registry
assert "ChiliSeedsItem" not in registry
assert not (src/"mixin/TomatoOnStakeSupportMixin.java").exists()
assert "TomatoOnStakeSupportMixin" not in load(resources/"italiansdelight.mixins.json")["mixins"]
assert "TOMATO_CROP.get()" in read(src/"common/block/StakedTomatoBlock.java")

# FDR TomatoBlock has one boolean ROPELOGGED for the root and only AGE
# for the separate hanging block. Every state must resolve to a real model.
ages=[0,0,1,1,2,2,2,3]
rootstates=load(assets/"blockstates/chili_crop.json")
ropestates=load(assets/"blockstates/chili_on_rope.json")
assert set(rootstates)==set(ropestates)=={"variants"}
assert len(rootstates["variants"])==16 and len(ropestates["variants"])==8
for age,stage in enumerate(ages):
    for value in ("false","true"):
        key=f"age={age},ropelogged={value}"
        assert rootstates["variants"][key]["model"]==f"italiansdelight:block/chili_free_stage_{stage}",key
    assert ropestates["variants"][f"age={age}"]["model"]==f"italiansdelight:block/chili_rope_stage_{stage}"
for i in range(4):
    free=load(assets/f"models/block/chili_free_stage_{i}.json")
    raised=load(assets/f"models/block/chili_rope_stage_{i}.json")
    assert free["parent"]=="farmersdelight:block/template_crop_cross"
    assert raised["parent"]=="farmersdelight:block/template_crop_with_rope"
    assert free["textures"]["cross"]==raised["textures"]["crop"]==f"italiansdelight:block/chili_crop_{i}"
    assert raised["textures"]["rope_side"]=="farmersdelight:block/tomatoes_coiled_rope"
    assert raised["textures"]["rope_top"]=="farmersdelight:block/rope_top"
for old in ("template_chili_crop_cross", "template_chili_crop_with_rope"):
    assert not (assets/f"models/block/{old}.json").exists(), old
for name in ("chili_crop","chili_on_rope"):
    loot=load(data/f"italiansdelight/loot_table/blocks/{name}.json")
    assert loot["pools"][-1]["condition"]["state"]["age"]=="7"

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
        if wood=="bamboo":
            # Custom vanilla-aligned UVs require standalone elements rather than
            # inheriting the generic pole's 6..10 top crop.
            assert model.get("elements") and model["elements"][0]["from"]==[6,0,6]
            assert model["elements"][0]["to"]==[10,16,10]
        else:
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


# The eight preserved age states map to four FDR-style stages, but all
# crop models and animations are inherited directly from FDR's templates.
assert len(ages)==8 and ages[0]==0 and ages[7]==3

# Check the actual alpha pixels rather than only checking a PNG's dimensions.
# This catches a plant that visually floats above the dirt or whose ripe
# stem cannot reach the next rope segment. Uses only Python stdlib (no Pillow).
import zlib
def rgba_png_rows(blob):
    assert blob[:8]==bytes.fromhex("89504e470d0a1a0a")
    offset=8
    compressed=b""
    while offset<len(blob):
        n=int.from_bytes(blob[offset:offset+4],"big")
        kind=blob[offset+4:offset+8]
        payload=blob[offset+8:offset+8+n]
        offset += 12+n
        if kind==b"IHDR":
            assert payload[:8]==(16).to_bytes(4,"big")*2
            assert payload[8:10]==bytes([8,6]),"RGBA8 required: no palette tint"
        if kind==b"IDAT": compressed+=payload
        if kind==b"IEND":break
    raw=zlib.decompress(compressed)
    rows=[]
    prior=[0]*64
    pos=0
    for _ in range(16):
        filt=raw[pos]; pos+=1
        curr=list(raw[pos:pos+64]);pos+=64
        for j in range(64):
            left=curr[j-4] if j>=4 else 0
            above=prior[j]
            upper_left=prior[j-4] if j>=4 else 0
            if filt==1: pred=left
            elif filt==2: pred=above
            elif filt==3: pred=(left+above)//2
            elif filt==4:
                p=left+above-upper_left
                distances=[abs(p-left),abs(p-above),abs(p-upper_left)]
                pred=(left,above,upper_left)[distances.index(min(distances))]
            else:
                assert filt==0,"Unexpected PNG scanline filter"
                pred=0
            curr[j]=(curr[j]+pred)&255
        rows.append([tuple(curr[k:k+4]) for k in range(0,64,4)])
        prior=curr
    return rows
mature=rgba_png_rows((assets/"textures/block/chili_crop_3.png").read_bytes())
green=rgba_png_rows((assets/"textures/block/chili_crop_2.png").read_bytes())
wildsprite=rgba_png_rows((assets/"textures/block/wild_chili.png").read_bytes())
assert any(p[3]==255 for p in mature[0]),"Mature plant stem must reach block top"
assert any(p[3]==255 for p in mature[15]),"Mature plant must reach ground"
assert any(p[3]==255 for p in green[0]) and any(p[3]==255 for p in green[15]),"Green-fruit growth must connect vertically"
assert any(p[3]==255 for p in wildsprite[15]),"Wild chili must touch ground, not hover"
for name,rows in (("ripe",mature),("wild",wildsprite)):
    red_y={y for y,line in enumerate(rows) for (r,g,b,a) in line
        if a==255 and r>110 and r>g*1.3 and r>b*1.3}
    assert len(red_y)>=6,f"{name} chili needs elongated pepper fruit, not tomato-like red dots"
assert not any(r>110 and r>g*1.3 and r>b*1.3 and a==255 for line in green for (r,g,b,a) in line), \
    "Green-fruit stage must not already show ripe red peppers"

# Wild pepper uses its own vegetation block and worldgen, but drops the
# existing crop seeds so no incompatible duplicate seed item is introduced.
assert "WildChiliBlock" in registry and 'ModRegistries.id("wild_chili")' in registry
assert "output.accept(ModBlocks.WILD_CHILI)" in creative
# Use the exact base class/placement properties as Farmer's Delight wild
# tomatoes: visual offset from TALL_GRASS while collision shape stays centered.
wild_java=read(src/"common/block/WildChiliBlock.java")
assert "extends WildCropBlock" in wild_java and "super(MobEffects.POISON, 10, properties)" in wild_java
assert "extends WildBasilBlock" not in wild_java
assert "extends WildCropBlock" in read(src/"common/block/WildBasilBlock.java")
assert "extends WildCropBlock" in read(src/"common/block/CardoonBlock.java")
assert "Block.Properties.ofFullCopy(Blocks.TALL_GRASS).setId(WILD_BASIL_KEY)" in registry
assert "Block.Properties.ofFullCopy(Blocks.TALL_GRASS).setId(CARDOON_KEY)" in registry
assert "Block.Properties.ofFullCopy(Blocks.TALL_GRASS).setId(WILD_CHILI_KEY)" in registry
assert "wild_chili_patch" in read(src/"common/registry/ModWorldGeneration.java")
assert "Biomes.SAVANNA" in read(src/"common/registry/ModWorldGeneration.java")
assert "block.italiansdelight.wild_chili" in en and "block.italiansdelight.wild_chili" in it
assert load(assets/"blockstates/wild_chili.json")["variants"][""]["model"]=="italiansdelight:block/wild_chili"
assert load(assets/"models/block/wild_chili.json")["textures"]["cross"]=="italiansdelight:block/wild_chili"
assert load(assets/"items/wild_chili.json")["model"]["model"]=="italiansdelight:item/wild_chili"
assert load(assets/"models/item/wild_chili.json") == {"parent": "minecraft:item/generated", "textures": {"layer0": "italiansdelight:block/wild_chili"}}
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
# Vanilla bamboo_fence_post inherits custom_fence_post. Its up/down
# 4x4 section samples UV [4,0,8,4] of minecraft:block/bamboo_fence.
# Do not recolor planks or invent a replacement cap: reuse the ORIGINAL atlas.
bamboo_model=load(assets/"models/block/bamboo_crop_stake.json")
bamboo=bamboo_model["textures"]
assert bamboo["0"]=="minecraft:block/bamboo_planks"
assert bamboo["2"]=="minecraft:block/bamboo_fence"
assert len(bamboo_model["elements"])==1
for face in ("up","down"):
    assert bamboo_model["elements"][0]["faces"][face]["texture"]=="#2"
    assert bamboo_model["elements"][0]["faces"][face]["uv"]==[4,0,8,4]
assert not (assets/"textures/block/bamboo_stake_end.png").exists(), "Old custom cap must be removed"
print("PASSED: FDR-based wild crop offset with centered hitbox, age-only chili models, vanilla bamboo fence cap")

print("PASSED: 16 decorative wood stakes, 3 axes, rope integration unchanged, free chili + FDR climbing, grape and wine")
