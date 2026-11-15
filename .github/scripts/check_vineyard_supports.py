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

# No crops can be planted onto decorative stakes.
assert "extends RotatedPillarBlock" in stake
assert "IntegerProperty CROP" not in stake
assert "StakeCropPlanting" not in init and "StakeCropBreaking" not in init
assert not (src/"common/block/StakeCropPlanting.java").exists()
assert not (src/"common/block/StakeCropBreaking.java").exists()
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
assert "private static final int FRUIT_REGROWTH_AGE = 4;" in chili
assert "current.setValue(CHILI_AGE, FRUIT_REGROWTH_AGE)" in chili
assert "current.setValue(CHILI_AGE, 0)" not in chili
assert "getMaxAge()" in chili and "return 7;" in chili
assert not (src/"common/item/ChiliSeedsItem.java").exists()
assert "new BlockItem(CHILI_CROP" in registry
assert "ChiliSeedsItem" not in registry
assert not (src/"mixin/TomatoOnStakeSupportMixin.java").exists()
assert "TomatoOnStakeSupportMixin" not in load(resources/"italiansdelight.mixins.json")["mixins"]
assert "TOMATO_CROP.get()" in read(src/"common/block/StakedTomatoBlock.java")

# FDR TomatoBlock has one boolean ROPELOGGED for the root and only AGE
# for the separate hanging block. Every state must resolve to a real model.
# Age 4 represents adult foliage after harvesting, not the seedling at age 0.
# 5-6 regenerate green peppers and 7 produces ripe red ones.
ages=[0,0,1,1,"foliage",2,2,3]
rootstates=load(assets/"blockstates/chili_crop.json")
ropestates=load(assets/"blockstates/chili_on_rope.json")
assert set(rootstates)==set(ropestates)=={"variants"}
assert len(rootstates["variants"])==16 and len(ropestates["variants"])==8
for age,stage in enumerate(ages):
    suffix="foliage" if stage=="foliage" else f"stage_{stage}"
    for value in ("false","true"):
        key=f"age={age},ropelogged={value}"
        assert rootstates["variants"][key]["model"]==f"italiansdelight:block/chili_free_{suffix}",key
    assert ropestates["variants"][f"age={age}"]["model"]==f"italiansdelight:block/chili_rope_{suffix}"
for i in range(4):
    free=load(assets/f"models/block/chili_free_stage_{i}.json")
    raised=load(assets/f"models/block/chili_rope_stage_{i}.json")
    assert free["parent"]=="farmersdelight:block/template_crop_cross"
    assert raised["parent"]=="farmersdelight:block/template_crop_with_rope"
    assert free["textures"]["cross"]==raised["textures"]["crop"]==f"italiansdelight:block/chili_crop_{i}"
    assert raised["textures"]["rope_side"]=="farmersdelight:block/tomatoes_coiled_rope"
    assert raised["textures"]["rope_top"]=="farmersdelight:block/rope_top"
# An established pepper has its own mature, fruitless foliage sprite.
# Both free and rope models use the identical texture; no juvenile seedling.
free_foliage=load(assets/"models/block/chili_free_foliage.json")
rope_foliage=load(assets/"models/block/chili_rope_foliage.json")
assert free_foliage["parent"]=="farmersdelight:block/template_crop_cross"
assert rope_foliage["parent"]=="farmersdelight:block/template_crop_with_rope"
assert free_foliage["textures"]["cross"]=="italiansdelight:block/chili_crop_foliage"
assert rope_foliage["textures"]["crop"]=="italiansdelight:block/chili_crop_foliage"
assert rope_foliage["textures"]["rope_side"]=="farmersdelight:block/tomatoes_coiled_rope"
assert rope_foliage["textures"]["rope_top"]=="farmersdelight:block/rope_top"
assert rootstates["variants"]["age=0,ropelogged=false"]["model"]!="italiansdelight:block/chili_free_foliage"

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
# A valid filename/size is NOT enough: damaged PNG IDAT checksums used to
# pass CI and Minecraft rendered the missing-texture checkerboard. Verify
# every PNG chunk CRC, the complete IEND trailer and zlib scanline stream.
import binascii
import zlib

def verify_crop_png(path, expected_height=16):
    blob = path.read_bytes()
    assert blob.startswith(b"\x89PNG\r\n\x1a\n"), f"{path}: PNG signature"
    pos = 8
    seen_ihdr = False
    seen_iend = False
    compressed = bytearray()
    while pos < len(blob):
        assert pos + 12 <= len(blob), f"{path}: truncated PNG chunk header"
        length = int.from_bytes(blob[pos:pos + 4], "big")
        kind = blob[pos + 4:pos + 8]
        end = pos + 12 + length
        assert end <= len(blob), f"{path}: truncated {kind!r} chunk"
        payload = blob[pos + 8:pos + 8 + length]
        expected_crc = int.from_bytes(blob[end - 4:end], "big")
        actual_crc = binascii.crc32(kind + payload) & 0xffffffff
        assert actual_crc == expected_crc, f"{path}: broken {kind!r} CRC"
        if kind == b"IHDR":
            assert not seen_ihdr and pos == 8 and length == 13
            assert payload[:8] == (16).to_bytes(4, "big") + expected_height.to_bytes(4, "big")
            assert payload[8:10] == bytes([8, 6]), f"{path}: RGBA8 required"
            seen_ihdr = True
        elif kind == b"IDAT":
            compressed.extend(payload)
        elif kind == b"IEND":
            assert length == 0 and end == len(blob), f"{path}: incomplete IEND"
            seen_iend = True
        pos = end
    assert seen_ihdr and seen_iend and compressed, f"{path}: incomplete PNG"
    decoded = zlib.decompress(bytes(compressed))
    assert len(decoded) == expected_height * (1 + 16 * 4), f"{path}: truncated RGBA scanlines"
    for y in range(expected_height):
        assert decoded[y * 65] <= 4, f"{path}: invalid filter at row {y}"

for stage in range(4):
    verify_crop_png(assets/f"textures/block/chili_crop_{stage}.png")
verify_crop_png(assets/"textures/block/chili_crop_foliage.png")
print("PASSED: all five chili PNGs have valid CRC, IDAT data and IEND")

for stage in range(4):
    sprite=(assets/f"textures/block/chili_crop_{stage}.png").read_bytes()
    assert sprite[:8]==bytes.fromhex("89504e470d0a1a0a")
    assert struct.unpack(">II",sprite[16:24])==(16,16),f"Chili stage {stage} must stay 16x16"
    assert sprite[25]==6,f"Chili stage {stage} needs RGBA transparency"
    assert load(assets/f"models/block/chili_rope_stage_{stage}.json")["textures"]["crop"]==f"italiansdelight:block/chili_crop_{stage}"


# The eight preserved age states map to four FDR-style stages, but all
# crop models and animations are inherited directly from FDR's templates.
assert len(ages)==8 and ages[0]==0 and ages[4]=="foliage" and ages[7]==3

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
foliage=rgba_png_rows((assets/"textures/block/chili_crop_foliage.png").read_bytes())
wildsprite=rgba_png_rows((assets/"textures/block/wild_chili.png").read_bytes())
assert any(p[3]==255 for p in mature[0]),"Mature plant stem must reach block top"
assert any(p[3]==255 for p in mature[15]),"Mature plant must reach ground"
assert any(p[3]==255 for p in green[0]) and any(p[3]==255 for p in green[15]),"Green-fruit growth must connect vertically"
assert any(p[3]==255 for p in foliage[0]) and any(p[3]==255 for p in foliage[15]),"Adult bush must connect across rope segments"
assert not any(a and r>110 and r>g*1.3 and r>b*1.3 for row in foliage for r,g,b,a in row), "Harvested foliage must not retain ripe red peppers"
assert foliage!=rgba_png_rows((assets/"textures/block/chili_crop_0.png").read_bytes()),"Post-harvest adult plant must not look like a sprout"
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

# Rustic grape leaves have a tintindex on every leaf face, but the custom
# grape_leaves registry ID needs its OWN client BlockColorRegistry source.
# Vanilla's foliage resolver supplies biome colors and Serene Seasons patches
# that same resolver when installed, so no custom seasonal calendar is needed.
client=read(src/"ItaliansDelightClient.java")
assert "ModBlocks.GRAPE_LEAVES, ModBlocks.WHITE_GRAPE_LEAVES);" in client
assert "BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES);" in client
assert "BlockColorRegistry.register(List.of(FRUIT_CHERRY_BIOME_FOLIAGE), ModBlocks.FRUIT_CHERRY_LEAVES);" in client
for model_name in ("grape_leaves_0", "grape_leaves_1"):
    leaf=load(assets/f"models/block/{model_name}.json")
    assert leaf["textures"]["0"]=="minecraft:block/oak_leaves"
    assert leaf["elements"],f"{model_name}: missing foliage geometry"
    for element in leaf["elements"]:
        for side,face in element["faces"].items():
            assert face.get("tintindex")==0,(model_name,side,face)
assert "tintindex" not in str(load(assets/"models/block/grapes.json")), "Fruit bunches must not be tinted"
assert "tintindex" not in str(load(assets/"models/block/grape_vine.json")), "Woody vines must not be tinted"
assert "sereneseasons." not in client, "Client foliage must not require optional season mod classes"
print("PASSED: grape center/side leaves use vanilla biome & Serene Seasons foliage tint; grapes and wood unchanged")

# White grapes share the approved vineyard mechanics but NOT their
# registries, fruit sprites, harvest items or seeds. Keep existing purple IDs.
leaves_code=read(src/"common/block/GrapeLeavesBlock.java")
grape_stem=read(src/"common/block/GrapeStemBlock.java")
items_code=read(src/"common/registry/ModItems.java")
crush=read(src/"common/block/CrushingTubRecipes.java")
jei_grapes=read(src/"integration/jei/OrchardPlantJeiInfo.java")
jei_crush=read(src/"integration/jei/CrushingTubJeiRecipe.java")
assert "new GrapeStemBlock(Block.Properties.ofFullCopy(Blocks.WHEAT)" in registry
assert "new GrapeLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)" in registry
for id in ("WHITE_GRAPE_STEM","WHITE_GRAPE_SEEDS","WHITE_GRAPE_LEAVES"):
    assert f"public static final" in registry and id in registry,id
assert "WHITE_GRAPES = registerFoodItem(\"white_grapes\", 2, 0.2f)" in items_code
assert "whiteGrapes ? ModBlocks.WHITE_GRAPE_SEEDS : ModBlocks.GRAPE_SEEDS" in grape_stem
assert "ModBlocks.WHITE_GRAPE_LEAVES" in grape_stem
assert "ModBlocks.WHITE_GRAPE_STEM" in leaves_code
assert "neighbor.is(this)" in leaves_code, "Canopy must not connect across varieties"
assert "ModItems.WHITE_GRAPES" in leaves_code
assert "ModItems.WHITE_GRAPES" in crush and "ModBlocks.WHITE_GRAPE_SEEDS" in crush
assert "new ItemStack(ModItems.WHITE_GRAPES)" in jei_crush
assert "new ItemStack(dev.italiansdelight.common.registry.ModBlocks.WHITE_GRAPE_SEEDS)" in jei_crush
assert "new ItemStack(ModBlocks.WHITE_GRAPE_SEEDS)" in jei_grapes
assert "new ItemStack(ModItems.WHITE_GRAPES)" in jei_grapes

# Fully resolve each of the eight crop age states for both cultivars.
purple_root=load(assets/"blockstates/grape_stem.json")
white_root=load(assets/"blockstates/white_grape_stem.json")
assert purple_root==white_root, "White grape crop must use identical age stages"
assert len(white_root["variants"])==8
for age in range(8):
    model=white_root["variants"][f"age={age}"]["model"]
    assert (assets/f"models/{model.split(':')[1]}.json").is_file()

purple_canopy=load(assets/"blockstates/grape_leaves.json")
white_canopy=load(assets/"blockstates/white_grape_leaves.json")
assert len(white_canopy["multipart"])==len(purple_canopy["multipart"])
assert sum(p["apply"]["model"]=="italiansdelight:block/white_grapes"
           for p in white_canopy["multipart"])==1
assert sum(p["apply"]["model"]=="italiansdelight:block/grapes"
           for p in white_canopy["multipart"])==0
assert "tintindex" not in str(load(assets/"models/block/white_grapes.json"))
assert load(assets/"models/block/white_grapes.json")["textures"]["0"]=="italiansdelight:block/white_grapes"
for name in ("white_grapes","white_grape_seeds"):
    assert load(assets/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
    expected_texture="white_grape" if name=="white_grapes" else name
    assert load(assets/f"models/item/{name}.json")["textures"]["layer0"]==f"italiansdelight:item/{expected_texture}"
for path in (assets/"textures/block/white_grapes.png",
             assets/"textures/item/white_grape.png",
             assets/"textures/item/white_grape_seeds.png"):
    verify_crop_png(path)
assert (assets/"textures/item/grapes.png").read_bytes() != (assets/"textures/item/white_grapes.png").read_bytes()
assert (assets/"textures/block/grapes.png").read_bytes() != (assets/"textures/block/white_grapes.png").read_bytes()
assert load(data/"italiansdelight/recipe/white_grape_seeds.json")["result"]["id"]=="italiansdelight:white_grape_seeds"
white_stem_loot=load(data/"italiansdelight/loot_table/blocks/white_grape_stem.json")
white_leaf_loot=load(data/"italiansdelight/loot_table/blocks/white_grape_leaves.json")
assert "italiansdelight:white_grape_seeds" in str(white_stem_loot)
assert "italiansdelight:white_grapes" in str(white_stem_loot)
assert "italiansdelight:white_grapes" in str(white_leaf_loot)
assert "italiansdelight:grapes" not in str(white_stem_loot)
assert "italiansdelight:grapes" not in str(white_leaf_loot)
assert "output.accept(ModBlocks.GRAPE_SEEDS);" in creative
assert "output.accept(ModBlocks.WHITE_GRAPE_SEEDS);" in creative
assert creative.index("output.accept(ModBlocks.GRAPE_SEEDS)") < creative.index("output.accept(ModItems.GRAPES)") < creative.index("output.accept(ModBlocks.WHITE_GRAPE_SEEDS)") < creative.index("output.accept(ModItems.WHITE_GRAPES)")
for locale in (en,it):
    for id in ("block.italiansdelight.white_grape_stem","block.italiansdelight.white_grape_leaves",
               "item.italiansdelight.white_grapes","item.italiansdelight.white_grape_seeds"):
        assert locale[id],id
    assert len(locale["jei.italiansdelight.plants.grape"])>250
assert "WHITE_GRAPE_LEAVES" in client
# Confirm the approved singular 16x16 grape item sprites are actually used.
assert load(assets/"models/item/grapes.json")["textures"]["layer0"]=="italiansdelight:item/grape"
assert load(assets/"models/item/white_grapes.json")["textures"]["layer0"]=="italiansdelight:item/white_grape"
verify_crop_png(assets/"textures/item/grape.png")
verify_crop_png(assets/"textures/item/white_grape.png")
for name in ("grape_juice","white_grape_juice"):
    assert f'new Definition("italiansdelight:{name}", false, "")' in read(src/"common/fluid/FluidCatalog.java")
    assert f'new Recipe("italiansdelight:{name}", STANDARD_AMOUNT' in crush
assert "ModFluids.WHITE_GRAPE_JUICE" in read(src/"common/fluid/FabricFluidItemContainers.java")
assert "ModItems.WHITE_GRAPE_JUICE_BUCKET" in read(src/"common/fluid/FabricFluidItemContainers.java")
assert "ModItems.WHITE_GRAPE_JUICE_BOTTLE" in read(src/"common/fluid/FabricFluidItemContainers.java")
assert "new ContainedFluid(() -> ModItems.WHITE_GRAPE_JUICE_BUCKET)" in read(src/"common/registry/ModFluids.java")
assert "new ItemStack(ModItems.WHITE_GRAPE_JUICE_BUCKET)" in read(src/"integration/jei/ModFluidJeiInfo.java")
assert "new ItemStack(ModItems.WHITE_GRAPE_JUICE_BOTTLE)" in read(src/"integration/jei/ModFluidJeiInfo.java")
assert "ModFluids.WHITE_GRAPE_JUICE, 250" in jei_crush
assert 'new Surface(ModRegistries.id("block/fluids/white_grape_juice_still"), 0xFFFFFFFF)' in read(src/"client/render/LiquidSurfaceVisuals.java")
renderer=read(src/"client/render/LiquidSurfaceVisuals.java")
translucency_selector=renderer.split("boolean translucent =",1)[1].split(";",1)[0]
for fluid_name in ("italiansdelight:grape_juice","italiansdelight:white_grape_juice"):
    assert fluid_name in translucency_selector, "Both grape juices must blend RGBA alpha"
assert "translucent ? RenderTypes.entityTranslucent" in renderer

assert 'registerContainedFluid(ModFluids.WHITE_GRAPE_JUICE,' in client
assert "italiansdelight:white_grape_juice" in read(src/"common/block/BarrelFluidContainers.java")
assert "WHITE_GRAPE_JUICE_BOTTLE" in creative and "WHITE_GRAPE_JUICE_BUCKET" in creative
assert creative.index("output.accept(ModItems.WHITE_GRAPE_JUICE_BUCKET)") < creative.index("output.accept(ModItems.WHITE_GRAPE_JUICE_BOTTLE)")
assert 'CHERRIES = registerFoodItem("cherries", 2, 0.2f)' in items_code
assert 'GRAPES = registerFoodItem("grapes", 2, 0.2f)' in items_code
assert ".noOcclusion().noCollision().forceSolidOff()" in registry, "Chili crop must be non-colliding at ground level"
assert 'WHITE_GRAPE_JUICE_BOTTLE' in items_code
# All five bottle items are genuinely drinkable, and the normal golden apple
# effects match vanilla: Regeneration II 5s and Absorption I 2min.
for item_id in ("GRAPE_JUICE_BOTTLE","WHITE_GRAPE_JUICE_BOTTLE",
                "APPLE_JUICE_BOTTLE","GOLDEN_APPLE_JUICE_BOTTLE",
                "OLIVE_OIL_BOTTLE"):
    declaration=items_code[items_code.index(f"public static final Item {item_id} = register("):]
    assert "ConsumableItem::new" in declaration.split(");",1)[0], item_id
assert "MobEffects.REGENERATION, 100, 1" in items_code
assert "MobEffects.ABSORPTION, 2400, 0" in items_code
assert "DataComponents.CONSUMABLE, consumable" in items_code
for name, texture in (("grapes","grape"),("white_grapes","white_grape")):
    assert load(assets/f"models/block/{name}.json")["textures"]["0"]==f"italiansdelight:block/{name}"
    assert (assets/f"textures/block/{name}.png").read_bytes()==(
        assets/f"textures/item/{texture}.png").read_bytes(), "Block atlas must contain the same updated art"
drinks=load(data/"c/tags/item/drinks.json")["values"]
for name in ("apple_juice","golden_apple_juice","grape_juice","white_grape_juice"):
    assert f"italiansdelight:{name}_bottle" in drinks
for locale in (en,it):
    expected_name = "Succo d'uva bianca" if locale is it else "White Grape Juice"
    for prefix in ("block","fluid","item"):
        assert locale[f"{prefix}.italiansdelight.white_grape_juice"] == expected_name
assert "italiansdelight:olive_oil_bottle" not in drinks
thirst=load(data/"italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json")["values"]
assert thirst["italiansdelight:olive_oil_bottle"]["thirst"]==0
for name in ("apple_juice","golden_apple_juice","grape_juice","white_grape_juice"):
    assert thirst[f"italiansdelight:{name}_bottle"]=={"thirst":6,"quenched":4}
white_frame=assets/"textures/block/fluids/white_grape_juice_still.png"
red_frame=assets/"textures/block/fluids/grape_juice_still.png"
assert white_frame.read_bytes()!=red_frame.read_bytes()
assert load(assets/"textures/block/fluids/white_grape_juice_still.png.mcmeta") == load(
       assets/"textures/block/fluids/grape_juice_still.png.mcmeta")

assert load(assets/"items/white_grape_juice_bottle.json")["model"]["model"]=="minecraft:item/potion"
assert load(assets/"items/white_grape_juice_bucket.json")["model"]["model"]=="italiansdelight:item/white_grape_juice_bucket"
assert load(assets/"models/item/white_grape_juice_bucket.json")["textures"]["layer0"]=="italiansdelight:item/white_grape_juice_bucket"
assert load(assets/"models/item/white_grape_juice_bottle.json")["parent"]=="minecraft:item/potion"
verify_crop_png(assets/"textures/item/white_grape_juice_bucket.png")
verify_crop_png(assets/"textures/block/fluids/white_grape_juice_still.png", expected_height=512)

# Two harvestable seed icons must remain equally usable at 16x16 but
# distinguish cultivars by a clearly separated plum vs golden palette.
red_seed=rgba_png_rows((assets/"textures/item/grape_seeds.png").read_bytes())
white_seed=rgba_png_rows((assets/"textures/item/white_grape_seeds.png").read_bytes())
red_mask={(x,y) for y,row in enumerate(red_seed) for x,p in enumerate(row) if p[3]>0}
white_mask={(x,y) for y,row in enumerate(white_seed) for x,p in enumerate(row) if p[3]>0}
assert red_mask==white_mask and len(red_mask)==47, "Only seed palette may change, not silhouette"
assert all(red_seed[y][x][0]>red_seed[y][x][1]+45 and
           red_seed[y][x][2]>red_seed[y][x][1]+20
           for x,y in red_mask), "Red-grape seeds must read as plum-purple"
assert all(white_seed[y][x][0]>=white_seed[y][x][1] and
           white_seed[y][x][1]>white_seed[y][x][2]+50
           for x,y in white_mask), "White-grape seeds must read as golden yellow"
assert load(assets/"models/item/grape_seeds.json")["textures"]["layer0"]=="italiansdelight:item/grape_seeds"
assert load(assets/"models/item/white_grape_seeds.json")["textures"]["layer0"]=="italiansdelight:item/white_grape_seeds"

def fluid_png_rgba(path):
    """Unfilter any RGBA8 sprite frame stack, no third-party dependencies."""
    blob=path.read_bytes()
    off=8; compressed=bytearray(); width=height=None
    while off<len(blob):
        n=int.from_bytes(blob[off:off+4],"big")
        kind=blob[off+4:off+8]; chunk=blob[off+8:off+8+n]
        off+=n+12
        if kind==b"IHDR":
            width=int.from_bytes(chunk[:4],"big")
            height=int.from_bytes(chunk[4:8],"big")
            assert chunk[8:10]==bytes([8,6])
        if kind==b"IDAT": compressed.extend(chunk)
        if kind==b"IEND": break
    assert width==16 and height==512, "Expected 32 complete animated frames"
    raw=zlib.decompress(bytes(compressed)); stride=width*4
    assert len(raw)==height*(stride+1)
    rgba=[]; above=[0]*stride; ptr=0
    for _ in range(height):
        f=raw[ptr]; ptr+=1
        row=list(raw[ptr:ptr+stride]); ptr+=stride
        for i in range(stride):
            left=row[i-4] if i>=4 else 0
            up=above[i]
            upper=above[i-4] if i>=4 else 0
            if f==0: prediction=0
            elif f==1: prediction=left
            elif f==2: prediction=up
            elif f==3: prediction=(left+up)//2
            elif f==4:
                candidate=left+up-upper
                errors=[abs(candidate-v) for v in (left,up,upper)]
                prediction=(left,up,upper)[errors.index(min(errors))]
            else: raise AssertionError("Unsupported PNG filter")
            row[i]=(row[i]+prediction)&255
        rgba.extend(tuple(row[i:i+4]) for i in range(0,stride,4))
        above=row
    return rgba

red_liquid=fluid_png_rgba(red_frame)
white_liquid=fluid_png_rgba(white_frame)
assert 160<=min(px[3] for px in white_liquid)<190
assert 195<=max(px[3] for px in white_liquid)<=210
assert all(w[3]<r[3] for w,r in zip(white_liquid,red_liquid)), "White juice needs stronger transparency"
assert max(px[0] for px in white_liquid)-min(px[0] for px in white_liquid)>=65, \
       "Golden juice must retain highlighted ripples, not a uniform flat color"
assert len({bytes(sum((list(p) for p in white_liquid[i*256:(i+1)*256]), []))
            for i in range(32)})>1, "Juice must be animated, not 32 identical frames"

# Cross-checked with the previously working block paths: item/ sprites
# cannot be used directly on the block texture atlas (missing checkerboard).
for name in ("grapes","white_grapes"):
    verify_crop_png(assets/f"textures/block/{name}.png")
for language in (en,it):
    for key in ("item.italiansdelight.white_grape_juice_bucket",
        "item.italiansdelight.white_grape_juice_bottle",
        "fluid.italiansdelight.white_grape_juice",
        "jei.italiansdelight.fluids.white_grape_juice"):
        assert language[key], key
    assert "white" in language["item.italiansdelight.white_grapes"].lower() or "bianca" in language["item.italiansdelight.white_grapes"].lower()
print("PASSED: cherries edible, ground chili non-colliding, singular grapes textures, independent white grape juice & containers")

print("PASSED: white grapes have independent seeds/harvest, full age stages, tinted canopy, and valid 16x16 sprites")

print("PASSED: 16 decorative wood stakes, 3 axes, rope integration unchanged, free chili + FDR climbing, grape and wine")
