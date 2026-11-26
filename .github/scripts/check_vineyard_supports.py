#!/usr/bin/env python3
"""Minecraft 26.3 vineyard and decorative pole regression audit (stdlib only)."""
import json
from pathlib import Path

src = Path("src/main/java/dev/italiansdelight")
resources = Path("src/main/resources")
assets = resources / "assets/italiansdelight"
data = resources / "data"
woods = ("oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
         "mangrove", "cherry", "bamboo", "crimson",
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

# Seedling -> mature vine is a two-block transition, matching upstream FDR.
# The FDR BuddingTomatoBlock transitions to vanilla tomatoes by hardcoded ID;
# our subclass redirects all three transition paths to the original chili ID.
budding = read(src/"common/block/BuddingChiliBlock.java")
assert "extends BuddingTomatoBlock" in budding
for method in ("growPastMaxAge", "updateShape", "performBonemeal"):
    assert method in budding
assert "ModBlocks.CHILI_CROP.defaultBlockState()" in budding
assert "BuddingBushBlock.AGE" in budding
assert "return ModBlocks.CHILI_PEPPER_SEEDS;" in budding
assert "new BlockItem(BUDDING_CHILI" in registry
assert 'ModRegistries.id("budding_chili")' in registry
assert "extends TomatoBlock" in chili
assert "extends ChiliCropBlock implements ReplaceOnRemoval" in hanging
assert "super(properties, true)" in hanging
for code in (chili, hanging):
    assert "getShape(" not in code, "Use FDR block geometry directly"
    assert "AGE_SHAPES" not in code and "HANGING_SHAPES" not in code
assert ".noOcclusion().noCollision().forceSolidOff()" in registry
assert ".setId(CHILI_ON_ROPE_KEY).noCollision().noOcclusion()" in registry
assert "BlockStateProperties.AGE_7" in chili
assert "getClimbingState" in chili and "ModBlocks.CHILI_ON_ROPE.defaultBlockState()" in chili
assert "climbRopeAbove" in chili and "height >= 3" in chili
assert "HangingTomatoBlock.placeRope" in hanging
assert "builder.add(CHILI_AGE);" in hanging
assert "builder.add(CHILI_AGE, ROPELOGGED);" in chili
assert "StakeSoilTilling.isFarmland" in chili
assert "private static final int FRUIT_REGROWTH_AGE = 0;" in chili
assert "current.setValue(CHILI_AGE, FRUIT_REGROWTH_AGE)" in chili
assert "return 7;" in chili
assert not (src/"common/item/ChiliSeedsItem.java").exists()
assert not (src/"mixin/TomatoOnStakeSupportMixin.java").exists()
assert "TOMATO_CROP.get()" in read(src/"common/block/StakedTomatoBlock.java")

# Each budding phase has a distinct original FDR growth sprite and the inherited
# age-dependent BuddingBushBlock selection box. The mature vine is foliage first,
# then green peppers, then red fruit, without resetting to seedlings mid-growth.
budstates=load(assets/"blockstates/budding_chili.json")["variants"]
assert len(budstates)==5
for age in range(5):
    assert budstates[f"age={age}"]["model"] == f"italiansdelight:block/budding_chili_stage{min(age,3)}"
for age in range(4):
    seedling=load(assets/f"models/block/budding_chili_stage{age}.json")
    assert seedling["parent"]=="farmersdelight:block/template_crop_cross"
    assert seedling["textures"]["cross"]==f"farmersdelight:block/budding_tomatoes_stage{age}"
assert load(data/"italiansdelight/loot_table/blocks/budding_chili.json")["pools"][0]["entries"][0]["name"] == "italiansdelight:chili_pepper_seeds"
ages=["foliage","foliage","stage_2","stage_2","stage_2","stage_2","stage_2","stage_3"]
rootstates=load(assets/"blockstates/chili_crop.json")
ropestates=load(assets/"blockstates/chili_on_rope.json")
assert len(rootstates["variants"])==16 and len(ropestates["variants"])==8
for age,stage in enumerate(ages):
    for flag in ("false","true"):
        assert rootstates["variants"][f"age={age},ropelogged={flag}"]["model"] == f"italiansdelight:block/chili_free_{stage}"
    assert ropestates["variants"][f"age={age}"]["model"] == f"italiansdelight:block/chili_rope_{stage}"
# The ripe-red sprite is reserved for the only harvestable adult stage:
# Tomato-derived ChiliCropBlock.getMaxAge() == 7; this also covers vines on rope.
for age in range(8):
    for rootflag in ("false", "true"):
        model=rootstates["variants"][f"age={age},ropelogged={rootflag}"]["model"]
        assert model.endswith("stage_3") == (age==7), ("root ripe before harvest", age, rootflag)
    model=ropestates["variants"][f"age={age}"]["model"]
    assert model.endswith("stage_3") == (age==7), ("rope ripe before harvest", age)
assert 'if (!isMaxAge(state) || player.isSpectator())' in chili
assert 'if (!current.is(this) || !isMaxAge(current))' in chili
for i in range(4):
    free=load(assets/f"models/block/chili_free_stage_{i}.json")
    raised=load(assets/f"models/block/chili_rope_stage_{i}.json")
    assert free["parent"]=="farmersdelight:block/template_crop_cross"
    assert raised["parent"]=="farmersdelight:block/template_crop_with_rope"
    assert free["textures"]["cross"]==raised["textures"]["crop"]==f"italiansdelight:block/chili_crop_{i}"
    assert raised["textures"]["rope_side"]=="farmersdelight:block/tomatoes_coiled_rope"
    assert raised["textures"]["rope_top"]=="farmersdelight:block/rope_top"
free_foliage=load(assets/"models/block/chili_free_foliage.json")
rope_foliage=load(assets/"models/block/chili_rope_foliage.json")
assert free_foliage["textures"]["cross"]=="italiansdelight:block/chili_crop_foliage"
assert rope_foliage["textures"]["crop"]=="italiansdelight:block/chili_crop_foliage"
for name in ("chili_crop","chili_on_rope"):
    loot=load(data/f"italiansdelight/loot_table/blocks/{name}.json")
    assert loot["pools"][-1]["condition"]["state"]["age"]=="7"
print("PASSED: chili seeds use FDR budding stage then adult tomato-derived vine; fruit matures without resets")

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


# The eight legacy mature states still load; new seedlings are separate.
# Age zero of the vine is already fully grown foliage, not a tiny sprout.
assert len(ages)==8 and ages[0]=="foliage" and ages[6]=="stage_2" and ages[7]=="stage_3"

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
# The cultivated sprites looked unusually dark against the wild shrub.
# Farmer's Delight template_crop_cross already disables AO and per-face shade;
# there is no custom tint registration. Lighten leaf pixels in the FIVE
# cultivated stages, leaving transparent pixels and red fruit unchanged.
# Pin their reviewed RGBA images to prevent accidental regressions.
import hashlib
crop_sprites = {
    "chili_crop_0": "ad343eb9589c6dc6965ebc2978c5d99c2d378e9a",
    "chili_crop_1": "777813ec3ea769ca06fa99c3fe15388a1519e904",
    "chili_crop_2": "842c37e3b610d8e24d2f1b0f79e264da80680c5c",
    "chili_crop_3": "4c31a0f709873faf61631e3df7a177081d38f6bf",
    "chili_crop_foliage": "f073db37d85fe62ad2047cbb4092d636772bd9a6",
}
for sprite_name, expected_sha in crop_sprites.items():
    png_data = (assets/"textures/block"/f"{sprite_name}.png").read_bytes()
    actual_sha = hashlib.sha1(f"blob {len(png_data)}\0".encode() + png_data).hexdigest()
    assert actual_sha == expected_sha, f"Unexpected texture regression: {sprite_name}"
    rgba = rgba_png_rows(png_data)
    green_pixels = [(r, g, b) for line in rgba for r, g, b, a in line
                    if a and g > r*1.12 and g > b*1.2]
    assert green_pixels
    mean_luminance = sum(.2126*r+.7152*g+.0722*b
                         for r,g,b in green_pixels) / len(green_pixels)
    assert mean_luminance >= 130, f"Cultivated chili still has dark foliage: {sprite_name}"
wild_png = (assets/"textures/block/wild_chili.png").read_bytes()
assert hashlib.sha1(f"blob {len(wild_png)}\0".encode() + wild_png).hexdigest() == \
       "2b817c690cdeb36f6e4a87c3b2c848da14d7fa9e", \
       "The wild pepper artwork was not meant to change"
print("PASSED: all cultivated chili shades brightened, red fruit and wild plant preserved")

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
assert wildloot["pools"][0]["entries"][0]["name"]=="italiansdelight:chili_pepper"
assert wildloot["pools"][0]["condition"]["terms"][0]["chance"]==0.2
assert wildloot["pools"][1]["entries"][0]["children"][0]["name"]=="italiansdelight:wild_chili"
assert wildloot["pools"][1]["entries"][0]["children"][1]["name"]=="italiansdelight:chili_pepper_seeds"
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
# Both cultivars use the same age-dependent break behavior. Ages 0-5 are
# soft seedlings; only the woody trunk model at ages 6-7 uses leaf hardness.
for stem in ("GRAPE_STEM", "WHITE_GRAPE_STEM"):
    declaration = registry.split(f"public static final Block {stem} = registerBlockOnly(", 1)[1].split(");", 1)[0]
    assert "new GrapeStemBlock(Block.Properties.ofFullCopy(Blocks.WHEAT)" in declaration
    assert ".strength(0.2F)" in declaration, f"{stem} must use leaf hardness for the trunk"
stem_variants=load(assets/"blockstates/grape_stem.json")["variants"]
assert all(stem_variants[f"age={age}"]["model"] == f"italiansdelight:block/grape_stem_{min(age//2, 3)}"
    for age in range(8)), "Update the woody age threshold if the visuals change"
assert "getDestroyProgress(BlockState state, Player player," in grape_stem
assert "state.getValue(AGE) < 6 ? 1.0F" in grape_stem, "Seedlings must still break instantly"
assert "super.getDestroyProgress(state, player, level, pos)" in grape_stem, "Trunks use registered hardness"
for id in ("WHITE_GRAPE_STEM","WHITE_GRAPE_SEEDS","WHITE_GRAPE_LEAVES"):
    assert f"public static final" in registry and id in registry,id
assert "WHITE_GRAPES = registerFoodItem(\"white_grapes\", 2, 0.2f)" in items_code
assert "whiteGrapes ? ModBlocks.WHITE_GRAPE_SEEDS : ModBlocks.GRAPE_SEEDS" in grape_stem
assert "ModBlocks.WHITE_GRAPE_LEAVES" in grape_stem
assert "ModBlocks.WHITE_GRAPE_STEM" in leaves_code
# White and purple grapes use the exact same GrapeStemBlock and
# GrapeLeavesBlock implementation. A rope changing into either canopy
# must keep the crop-stake's rope_tied flag and its rendered knot.
assert 'new GrapeStemBlock(Block.Properties.ofFullCopy(Blocks.WHEAT)' in registry
assert "new GrapeLeavesBlock(" in registry
assert "(neighbor.is(ModBlocks.GRAPE_LEAVES)" in stake
assert "|| neighbor.is(ModBlocks.WHITE_GRAPE_LEAVES))" in stake
assert "neighbor.getValue(GrapeLeavesBlock.AXIS) == direction.getAxis()" in stake
assert "neighbor.is(ModBlocks.HORIZONTAL_ROPE)" in stake

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
assert "italiansdelight:white_grapes" not in str(white_stem_loot)
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
# The shared JEI information helper builds real ItemStacks from the provided
# bucket and bottle, rather than allocating a separate literal per fluid.
fluid_jei_info = read(src/"integration/jei/ModFluidJeiInfo.java")
assert "new ItemStack(bucket)" in fluid_jei_info
assert "new ItemStack(bottle)" in fluid_jei_info
assert 'info(registration, "white_grape_juice", ModItems.WHITE_GRAPE_JUICE_BUCKET,' in fluid_jei_info
assert "ModItems.WHITE_GRAPE_JUICE_BOTTLE);" in fluid_jei_info
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

print("PASSED: 14 decorative wood stakes, 3 axes, rope integration unchanged, free chili + FDR climbing, grape and wine")


# Farmer's Delight tomato crate reference: a packed, overlapping fruit surface,
# not small disconnected motifs in an empty box. Every variant must be fully
# registered/craftable and ship both top and side textures in the block atlas.
import struct
import zlib
fruit_crates=(
    ("grape_crate","grapes","grape"),
    ("white_grape_crate","white_grapes","white_grape"),
    ("chili_pepper_crate","chili_pepper","chili_pepper"),
    ("ghost_pepper_crate","ghost_pepper","ghost_pepper"),
)
def packed_top_pixels(blob):
    assert blob.startswith(bytes.fromhex("89504e470d0a1a0a"))
    assert struct.unpack(">II",blob[16:24])==(16,16)
    assert blob[24:26]==bytes((8,2)), "16x16 RGB, opaque"
    stream=bytearray()
    off=8
    while off<len(blob):
        n=int.from_bytes(blob[off:off+4],"big")
        name=blob[off+4:off+8]
        if name==b"IDAT":stream.extend(blob[off+8:off+8+n])
        off+=n+12
        if name==b"IEND":break
    raw=zlib.decompress(stream)
    assert len(raw)==16*(1+16*3)
    prior=[0]*48
    rgb=[]
    at=0
    for _ in range(16):
        typ=raw[at];at+=1
        line=list(raw[at:at+48]);at+=48
        for j in range(48):
            a=line[j-3] if j>=3 else 0
            b=prior[j]
            c=prior[j-3] if j>=3 else 0
            if typ==0:pred=0
            elif typ==1:pred=a
            elif typ==2:pred=b
            elif typ==3:pred=(a+b)//2
            elif typ==4:
                p=a+b-c;dist=(abs(p-a),abs(p-b),abs(p-c))
                pred=(a,b,c)[dist.index(min(dist))]
            else:raise AssertionError("unsupported filter")
            line[j]=(line[j]+pred)&255
        rgb.append([tuple(line[i:i+3]) for i in range(0,48,3)])
        prior=line
    return rgb

top_sprites=[]
for crate,fruit,tag in fruit_crates:
    upper=crate.upper()
    assert f"public static final Block {upper}" in registry
    assert f"output.accept(ModBlocks.{upper})" in creative
    assert f"fire.add({upper}, 5, 20)" in registry
    state=load(assets/f"blockstates/{crate}.json")
    model=load(assets/f"models/block/{crate}.json")
    item=load(assets/f"items/{crate}.json")
    assert state["variants"][""]["model"]==f"italiansdelight:block/{crate}"
    assert model["parent"]=="minecraft:block/cube_bottom_top"
    assert model["textures"]=={
        "bottom":"farmersdelight:block/crate_bottom",
        "side":f"italiansdelight:block/{crate}_side",
        "top":f"italiansdelight:block/{crate}_top"
    }
    assert item["model"]["model"]==f"italiansdelight:block/{crate}"
    assert load(assets/f"models/item/{crate}.json")["parent"]==f"italiansdelight:block/{crate}"
    for face in ("top","side"):
        raw=(assets/f"textures/block/{crate}_{face}.png").read_bytes()
        assert raw[:8]==bytes.fromhex("89504e470d0a1a0a")
        assert struct.unpack(">II",raw[16:24])==(16,16)
    top=packed_top_pixels((assets/f"textures/block/{crate}_top.png").read_bytes())
    top_sprites.append(top)
    assert top[0][0]==(128,94,54), "Retain original FDR tomato crate wooden rim"
    # A tightly packed crate has colored fruits across all inner quadrants.
    assert all(len({p for row in top[y:y+5] for p in row[x:x+5]})>=5
               for x,y in ((2,2),(9,2),(2,9),(9,9))), "Crate must show detailed packed fruit"
    shaped=load(data/f"italiansdelight/recipe/{crate}.json")
    unpack=load(data/f"italiansdelight/recipe/{fruit}_from_crate.json")
    loot=load(data/f"italiansdelight/loot_table/blocks/{crate}.json")
    assert shaped["pattern"]==["###"]*3
    assert shaped["result"]["id"]==f"italiansdelight:{crate}"
    assert shaped["key"]["#"]==f"italiansdelight:{fruit}"
    assert unpack["result"]=={"id":f"italiansdelight:{fruit}","count":9}
    assert loot["pools"][0]["entries"][0]["name"]==f"italiansdelight:{crate}"
    for kind in ("block","item"):
        common=load(data/f"c/tags/{kind}/storage_blocks.json")
        specific=load(data/f"c/tags/{kind}/storage_blocks/{tag}.json")
        assert f"italiansdelight:{crate}" in common["values"]
        assert f"italiansdelight:{crate}" in specific["values"]
    assert f"italiansdelight:{crate}" in load(data/"minecraft/tags/block/mineable/axe.json")["values"]
    for locale in (en,it):
        assert locale[f"block.italiansdelight.{crate}"]
assert all(top_sprites[i]!=top_sprites[j] for i in range(4) for j in range(i+1,4))
print("PASSED: four packed FDR-reference crates, visible fruits, 9->1/1->9 recipes, loot, tags, models and labels")

# Double-height wild grapes follow FDR WildRiceBlock + WildRiceFeature.
wild=read(src/"common/block/WildGrapeBlock.java")
feat=read(src/"common/worldgen/WildGrapeFeature.java")
worldreg=read(src/"common/registry/ModWorldGeneration.java")
assert "extends DoublePlantBlock implements BonemealableBlock" in wild
assert "FDRefabricatedTags.Blocks.GROWS_WILD_CROPS" in wild
assert "DoubleBlockHalf.UPPER" in wild
assert "DoublePlantBlock.placeAt(level, lower, pos, 2)" in feat
assert "getBlockState(pos.above()).isAir()" in feat
assert "WildGrapeFeature.RED_CODEC" in worldreg
assert "WildGrapeFeature.WHITE_CODEC" in worldreg
for name,seed,fruit,reg in (
    ("wild_grape_vine","grape_seeds","grapes","WILD_GRAPE_VINE"),
    ("wild_white_grape_vine","white_grape_seeds","white_grapes","WILD_WHITE_GRAPE_VINE")
):
    assert f"public static final Block {reg}" in registry
    assert f"output.accept(ModBlocks.{reg})" in creative
    assert f"block.italiansdelight.{name}" in en and f"block.italiansdelight.{name}" in it
    assert load(assets/f"blockstates/{name}.json")["variants"]=={
        "half=lower":{"model":f"italiansdelight:block/{name}_bottom"},
        "half=upper":{"model":f"italiansdelight:block/{name}_top"}}
    assert not (assets/f"models/block/{name}.json").exists()
    assert not (assets/f"textures/block/{name}.png").exists()
    for half in ("lower","upper"):
        part="bottom" if half=="lower" else "top"
        m=load(assets/f"models/block/{name}_{part}.json")
        assert m["parent"]=="minecraft:block/cross"
        assert m["render_type"]=="minecraft:cutout"
        assert m["textures"]["cross"]==f"italiansdelight:block/{name}_{part}"
        verify_crop_png(assets/f"textures/block/{name}_{part}.png")
        assert not (assets/f"textures/block/{name}_rose_{part}.png").exists()
    assert load(assets/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
    assert load(assets/f"models/item/{name}.json")["textures"]["layer0"]==f"italiansdelight:block/{name}_top"
    pools=load(data/f"italiansdelight/loot_table/blocks/{name}.json")["pools"]
    assert len(pools)==4
    for half,i in (("lower",0),("upper",2)):
        fruitpool,harvestpool=pools[i:i+2]
        assert fruitpool["condition"]["terms"][0]["terms"][0]["state"]["half"]==half
        assert len(fruitpool["condition"]["terms"])==2
        assert fruitpool["condition"]["terms"][1]=={
            "type":"minecraft:inverted",
            "term":{"type":"farmersdelight:can_item_perform_ability","ability":"shears_harvest"}}
        fruits=fruitpool["entries"][0]
        assert fruits["name"]==f"italiansdelight:{fruit}"
        assert fruits["modifier"]==[
            {"type":"minecraft:set_count","count":{"type":"minecraft:uniform","min":0,"max":2}},
            {"type":"minecraft:explosion_decay"}]
        assert harvestpool["condition"]["terms"][0]["state"]["half"]==half
        children=harvestpool["entries"][0]["children"]
        assert children[0]["name"]==f"italiansdelight:{name}"
        assert children[0]["condition"]["type"]=="farmersdelight:can_item_perform_ability"
        assert children[1]["name"]==f"italiansdelight:{seed}"
        assert children[1]["modifier"]==[
            {"type":"minecraft:set_count","count":{"type":"minecraft:uniform","min":1,"max":4}},
            {"type":"minecraft:explosion_decay"}]
    assert load(data/f"italiansdelight/worldgen/feature/{name}.json")["type"]==f"italiansdelight:{name}"
    placed=load(data/f"italiansdelight/worldgen/placed_feature/{name}_patch.json")
    assert placed["feature"]==f"italiansdelight:{name}_cluster" and placed["placement"][0]["chance"]==100
    # FDR carrots + tomatoes: vegetation, coarse dirt and dead bushes in_order.
    cluster=load(data/f"italiansdelight/worldgen/feature/{name}_cluster.json")
    assert cluster["type"]=="farmersdelight:in_order"
    primary,bare,bush=cluster["features"]
    assert primary=={"feature":f"italiansdelight:{name}","placement":[]}
    assert bare["feature"]["type"]=="minecraft:simple_block"
    assert bare["feature"]["to_place"]["state"]["id"]=="minecraft:coarse_dirt"
    assert bush["feature"]["type"]=="minecraft:simple_block"
    assert bush["feature"]["to_place"]["state"]["id"]=="minecraft:dead_bush"
    assert bare["placement"][0]=={"type":"minecraft:rarity_filter","chance":3}
    assert bush["placement"][0]=={"type":"minecraft:rarity_filter","chance":4}
    assert bare["placement"][1]["x"]["min"]==-4 and bare["placement"][1]["x"]["max"]==4
    assert bush["placement"][1]["x"]["min"]==-5 and bush["placement"][1]["x"]["max"]==5
    assert bare["placement"][2]=={"type":"minecraft:offset","x":0,"y":-1,"z":0}
    # Coarse dirt can only replace open grass/dirt, never vines or trees.
    bare_pred=bare["placement"][3]["predicate"]["predicates"]
    assert bare_pred==[
      {"type":"minecraft:matching_blocks","blocks":["minecraft:grass_block","minecraft:dirt"]},
      {"type":"minecraft:matching_blocks","blocks":"minecraft:air","offset":[0,1,0]}]
    bush_pred=bush["placement"][2]["predicate"]["predicates"]
    assert bush_pred==[
      {"type":"minecraft:matching_blocks","blocks":"minecraft:air"},
      {"type":"minecraft:matching_blocks","blocks":[
       "minecraft:grass_block","minecraft:dirt","minecraft:coarse_dirt",
       "minecraft:podzol","minecraft:rooted_dirt"],"offset":[0,-1,0]}]
    steps=placed["placement"]
    assert steps[2]=={"type":"minecraft:count","count":24}
    assert steps[3]=={"type":"minecraft:heightmap","heightmap":"MOTION_BLOCKING_NO_LEAVES"}
    assert steps[5]["x"]["min"]==-4 and steps[5]["x"]["max"]==4
    assert steps[5]["z"]["min"]==-4 and steps[5]["z"]["max"]==4
    assert steps[5]["y"]["min"]==-2 and steps[5]["y"]["max"]==2
    predicates=steps[6]["predicate"]["predicates"]
    assert predicates[0]=={"type":"minecraft:matching_blocks","blocks":"minecraft:air"}
    assert predicates[1]=={"type":"minecraft:matching_blocks","blocks":"minecraft:air","offset":[0,1,0]}
    assert predicates[2]=={"type":"minecraft:matching_blocks","blocks":[
        "minecraft:grass_block","minecraft:dirt","minecraft:coarse_dirt",
        "minecraft:podzol","minecraft:rooted_dirt"],"offset":[0,-1,0]}
    assert f"{reg}_PATCH" in worldreg
# Cultivated root loot always gives ONE seed, while the leaves only
# give grapes when ripe. Unsupported ripe side branches drop on detachment.
for stem,canopy,seed,fruit in (
    ("grape_stem","grape_leaves","grape_seeds","grapes"),
    ("white_grape_stem","white_grape_leaves","white_grape_seeds","white_grapes")
):
    st=load(data/f"italiansdelight/loot_table/blocks/{stem}.json")
    lf=load(data/f"italiansdelight/loot_table/blocks/{canopy}.json")
    assert st["pools"]==[{"rolls":1,"entries":[{"type":"minecraft:item","name":f"italiansdelight:{seed}"}]}]
    assert f"italiansdelight:{fruit}" not in str(st)
    assert lf["pools"]==[{"rolls":1,
        "condition":{"type":"minecraft:match_block","blocks":f"italiansdelight:{canopy}","state":{"grapes":"true"}},
        "entries":[{"type":"minecraft:item","name":f"italiansdelight:{fruit}"}]}]
assert "removeUnsupportedCanopy(level, pos);" in leaves_code
assert "private void removeUnsupportedCanopy(ServerLevel level, BlockPos pos)" in leaves_code
assert "current.getValue(DISTANCE) == 1 && current.getValue(GRAPES)" in leaves_code
assert "level.removeBlock(pos, false);" in leaves_code
# Worldgen biomes are temperate; both cultivars have the same distribution.
grape_biomes=worldreg.split("VEGETAL_DECORATION, WILD_GRAPE_VINE_PATCH")[0].split("BiomeSelectors.includeByKey(")[-1]
white_grape_biomes=worldreg.split("VEGETAL_DECORATION, WILD_WHITE_GRAPE_VINE_PATCH")[0].split("BiomeSelectors.includeByKey(")[-1]
assert grape_biomes==white_grape_biomes
for biome in ("Biomes.PLAINS", "Biomes.SUNFLOWER_PLAINS", "Biomes.MEADOW",
              "Biomes.FOREST", "Biomes.FLOWER_FOREST",
              "Biomes.BIRCH_FOREST", "Biomes.CHERRY_GROVE"):
    assert biome in grape_biomes
assert "Biomes.SPARSE_JUNGLE" not in grape_biomes
# Approved fruits are active; the old backup copies were explicitly removed.
archived=Path("docs/vineyard-chili-1.2.0/saved-textures/wild-grapevines")
assert not archived.exists() or not any(archived.iterdir())
# No stale _rose_ sprite names remain; canonical 16x16 PNGs match the models.
print("PASSED: double-height wild grapevines, both halves placed and looted as FDR wild rice")

assert not (assets/"textures/block/wild_grapes.png").exists()
leafcode=read(src/"common/block/GrapeLeavesBlock.java")
assert 'IntegerProperty FRUIT_STAGE = IntegerProperty.create("fruit_stage", 0, 3)' in leafcode
assert "b.add(DISTANCE, AXIS, GRAPES, FRUIT_STAGE" in leafcode
assert "advanceFruit(state)" in leafcode and "current.setValue(GRAPES, false)" in leafcode
for canopy,fruit in (("grape_leaves","grapes"),("white_grape_leaves","white_grapes")):
    entries=load(assets/f"blockstates/{canopy}.json")["multipart"]
    ripe=[x for x in entries if x.get("when",{}).get("grapes")=="true"]
    assert len(ripe)==1 and ripe[0]["apply"]["model"]==f"italiansdelight:block/{fruit}"
    for stage in (1,2,3):
        found=[x for x in entries if x.get("when",{}).get("fruit_stage")==str(stage)]
        assert len(found)==1 and found[0]["when"]["grapes"]=="false"
        assert found[0]["apply"]["model"]==f"italiansdelight:block/{fruit}_unripe_{stage}"
        assert load(assets/f"models/block/{fruit}_unripe_{stage}.json")["textures"]["0"]==f"italiansdelight:block/{fruit}_unripe_{stage}"
        verify_crop_png(assets/f"textures/block/{fruit}_unripe_{stage}.png")
print("PASSED: FDR shears on all wild crops, two new wild grapevines, three unripe grape stages, existing ripe states preserved")
