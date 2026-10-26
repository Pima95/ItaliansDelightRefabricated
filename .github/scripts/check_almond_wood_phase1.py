#!/usr/bin/env python3
"""Original almond phase-one blocks and pixel art: no premature variants."""
import json
import struct
import zlib
from pathlib import Path
R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
REG=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text()
TAB=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text()
NAMES=("almond_log","stripped_almond_log","almond_planks")
def load(path):
    assert path.is_file(), str(path)
    return json.loads(path.read_text(encoding="utf-8"))
for name in NAMES:
    assert f'ModRegistries.id("{name}")' in REG
    assert TAB.count(f"output.accept(ModBlocks.{name.upper()});")==1
    state=load(A/"blockstates"/f"{name}.json")["variants"]
    if name.endswith("planks"):
        assert set(state)=={""}
    else:
        assert set(state)=={"axis=x","axis=y","axis=z"}
        assert state["axis=x"]["x"]==90 and state["axis=z"]["x"]==90
    model=load(A/"models/block"/f"{name}.json")
    if name.endswith("planks"):
        assert model["parent"]=="minecraft:block/cube_all"
        assert model["textures"]["all"]==f"italiansdelight:block/{name}"
    else:
        assert model["parent"]=="minecraft:block/cube_column"
        assert model["textures"]=={"side":f"italiansdelight:block/{name}","end":f"italiansdelight:block/{name}_top"}
    assert load(A/"models/item"/f"{name}.json")["parent"]==f"italiansdelight:block/{name}"
    assert load(A/"items"/f"{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
    drop=load(D/"italiansdelight/loot_table/blocks"/f"{name}.json")
    assert drop["pools"][0]["entries"][0]["name"]==f"italiansdelight:{name}"
    assert f"italiansdelight:{name}" in load(D/"minecraft/tags/block/mineable/axe.json")["values"]
    assert f"block.italiansdelight.{name}" in load(A/"lang/it_it.json")
    assert f"block.italiansdelight.{name}" in load(A/"lang/en_us.json")
    assert f"fire.add({name.upper()}," in REG
assert "StrippableBlockRegistry.register(ALMOND_LOG, STRIPPED_ALMOND_LOG)" in REG
for n in ("almond_wood","stripped_almond_wood","almond_stairs","almond_sapling"):
    assert f'ModRegistries.id("{n}")' not in REG
for kind in ("item","block"):
    tag=load(D/"minecraft/tags"/kind/"logs_that_burn.json")["values"]
    assert all(f"italiansdelight:{n}" in tag for n in NAMES[:2])
    own=load(D/"italiansdelight/tags"/kind/"almond_logs.json")["values"]
    assert set(own)=={"italiansdelight:almond_log","italiansdelight:stripped_almond_log"}
assert "italiansdelight:almond_planks" in load(D/"minecraft/tags/item/planks.json")["values"]
recipe=load(D/"italiansdelight/recipe/almond_planks.json")
assert recipe=={"type":"minecraft:crafting_shapeless","category":"building","ingredients":["#italiansdelight:almond_logs"],"result":{"id":"italiansdelight:almond_planks","count":4}}
images=("almond_log","almond_log_top","stripped_almond_log","stripped_almond_log_top","almond_planks")
art=[]
decoded={}
for n in images:
    png=(A/"textures/block"/f"{n}.png").read_bytes()
    assert png[:8]==b"\x89PNG\r\n\x1a\n"
    assert struct.unpack_from(">II",png,16)==(16,16)
    assert tuple(png[24:27])==(8,6,0),n
    art.append(png)
    pos=8
    sections=[]
    while pos<len(png):
        length=struct.unpack_from(">I",png,pos)[0]
        kind=png[pos+4:pos+8]
        if kind==b"IDAT":sections.append(png[pos+8:pos+8+length])
        pos+=12+length
    packed=zlib.decompress(b"".join(sections))
    assert len(packed)==16*65
    previous=bytearray(64)
    rgb=[]
    for y in range(16):
        f=packed[y*65]
        encoded=packed[y*65+1:y*65+65]
        assert f in range(5)
        row=bytearray(64)
        for i,v in enumerate(encoded):
            left=row[i-4] if i>=4 else 0
            up=previous[i]
            upper_left=previous[i-4] if i>=4 else 0
            p=left+up-upper_left
            ds=(abs(p-left),abs(p-up),abs(p-upper_left))
            paeth=(left,up,upper_left)[ds.index(min(ds))]
            predictor=(0,left,up,(left+up)//2,paeth)[f]
            row[i]=(v+predictor)&255
        assert all(row[i]==255 for i in range(3,64,4)),n
        rgb.extend(tuple(row[i:i+3]) for i in range(0,64,4))
        previous=row
    decoded[n]=rgb
assert len(set(art))==5
# Vanilla-sized 16x16 end grain: 1px bark/sapwood rim (60 pixels) and
# identical 14x14 plank-colored wood inside (196 pixels).
# The same shared rings are exposed by both the normal and stripped log.
planks_palette=set(decoded["almond_planks"])
log_end=decoded["almond_log_top"]
stripped_end=decoded["stripped_almond_log_top"]
inside=[y*16+x for y in range(1,15) for x in range(1,15)]
outside=[y*16+x for y in range(16) for x in range(16)
         if x==0 or x==15 or y==0 or y==15]
assert len(inside)==196 and len(outside)==60
assert all(log_end[i]==stripped_end[i] for i in inside), (
    "The inner cross sections must match pixel-for-pixel; only the rim changes"
)
assert {log_end[i] for i in inside} <= planks_palette, (
    "The cut wood must use the exact color palette of almond_planks"
)
assert len({log_end[i] for i in inside})>=5, "Use nuanced vanilla-style plank shading on cut surfaces"
assert sum(log_end[i]!=stripped_end[i] for i in outside)==60, (
    "Every outer-edge pixel must distinguish natural bark from stripped sapwood"
)
# Vanilla-style growth rings use straight, axis-aligned 90-degree corners.
# Protect three complete, 1px-thick CONCENTRIC SQUARES (4x4, 8x8, 12x12),
# not round or diamond-shaped outlines. Their four corners and four sides
# must be connected at the exact same square radius.
for width,expected_rgb in (
    (4,(184,145,115)),
    (8,(146,107,87)),
    (12,(184,145,115)),
):
    lo=(16-width)//2
    hi=lo+width-1
    ring={(x,y) for x in range(lo,hi+1) for y in (lo,hi)}
    ring.update((x,y) for y in range(lo,hi+1) for x in (lo,hi))
    assert len(ring)==4*width-4
    for x,y in ring:
        assert log_end[y*16+x]==expected_rgb, (
            f"{width}x{width} growth ring must form an intact SQUARE at {x},{y}"
        )
# Explicitly prevent the old rounded ring profile (truncated corners).
assert log_end[2*16+2] == log_end[2*16+7]
assert log_end[4*16+4] == log_end[4*16+7]
def brightness(colors):
    return sum(sum(c)/3 for c in colors)/len(colors)
assert brightness([log_end[i] for i in outside])+40 < brightness(
    [stripped_end[i] for i in outside]), "Natural bark must be darker than stripped rim"
assert brightness([log_end[i] for i in inside]) == brightness(
    [stripped_end[i] for i in inside])
# The approved six-color plank artwork remains pixel-identical.
import hashlib
assert hashlib.sha256((A/"textures/block/almond_planks.png").read_bytes()).hexdigest() == (
    "cad111aed831068bc7a443a6963c21ddb7f306404c8f72c103176b2ddbaff323"
)
# Bark is visibly layered with four continuous, slightly bending fissures
# and plate ridges, not random colored noise or an oak side recolor.
# The stripped texture has coherent slender longitudinal wood fibers.
# Both use the existing approved almond bark/planks color sets.
bark_palette={(63,49,48),(79,57,49),(101,69,57),
              (126,87,69),(154,112,84),(181,141,108)}
assert set(decoded["almond_log"]) == bark_palette
assert set(decoded["stripped_almond_log"]) <= planks_palette, (
    "Stripped side must use the approved six plank colors; no new tint"
)
assert len(set(decoded["stripped_almond_log"])) >= 5
for name in ("almond_log","stripped_almond_log"):
    im=decoded[name]
    vertical_same=sum(im[y*16+x] == im[(y+1)*16+x]
                      for y in range(15) for x in range(16))
    horizontal_same=sum(im[y*16+x] == im[y*16+x+1]
                        for y in range(16) for x in range(15))
    # The eye should be able to trace fibers longitudinally. Tiny random
    # patches would have low directionality; straight uninterrupted bars
    # would have essentially no natural 1px bends.
    assert vertical_same >= 130, f"{name}: wood grain is noisy and incoherent"
    assert vertical_same >= 3*horizontal_same, f"{name}: grain lost its vertical direction"
    assert vertical_same <= 200, f"{name}: grain is unnaturally featureless"
    assert min(len(set(im[y*16:(y+1)*16])) for y in range(16)) >= 3, (
        f"{name}: some rows lack sufficiently small wood detail"
    )
assert {stripped_end[i] for i in outside} <= planks_palette
assert {log_end[i] for i in inside} == {stripped_end[i] for i in inside}
assert all(data not in {(A/"textures/block/olive_log.png").read_bytes(),(A/"textures/block/olive_planks.png").read_bytes()} for data in art)
print("OK: 16x16 RGBA opaque almond sprites with structured bark and plank-colored stripped grain")
print("OK: three complete nested SQUARE rings (4x4, 8x8, 12x12), 14x14 identical center and 1px rim")
print("OK: continuous bark fissures and directionally coherent stripped fibers")
print("OK: approved almond planks SHA-256 preserved; block models and recipes untouched")
print("OK: item models, tags, recipes, drops, axe stripping, fire, localization")
