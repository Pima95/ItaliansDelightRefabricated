#!/usr/bin/env python3
"""Almond starter assets: vanilla outline, rich shading and phase-one blocks."""
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
for n in ("almond_door","almond_trapdoor","almond_shelf","almond_sapling"):
    assert f'ModRegistries.id("{n}")' not in REG
for kind in ("item","block"):
    tag=load(D/"minecraft/tags"/kind/"logs_that_burn.json")["values"]
    assert all(f"italiansdelight:{n}" in tag for n in NAMES[:2])
    own=load(D/"italiansdelight/tags"/kind/"almond_logs.json")["values"]
    assert {"italiansdelight:almond_log","italiansdelight:stripped_almond_log"} <= set(own)
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
# The new wood palette has subtler extremes and transitional 1px shades;
# allow nearby tonal interpolation rather than six exact flat swatches.
WOOD_TONES=((149,109,90),(165,123,100),(186,146,118),
            (202,163,132),(217,180,148),(229,195,164))
def nearest_tone(rgb):
    return min(range(6), key=lambda i: sum((a-b)**2 for a,b in zip(rgb,WOOD_TONES[i])))
def tone_distance(rgb):
    return min(max(abs(a-b) for a,b in zip(rgb,base)) for base in WOOD_TONES)
log_end=decoded["almond_log_top"]
stripped_end=decoded["stripped_almond_log_top"]
inside=[y*16+x for y in range(1,15) for x in range(1,15)]
outside=[y*16+x for y in range(16) for x in range(16)
         if x==0 or x==15 or y==0 or y==15]
assert len(inside)==196 and len(outside)==60
assert all(log_end[i]==stripped_end[i] for i in inside), (
    "The inner cross sections must match pixel-for-pixel; only the rim changes"
)
assert all(tone_distance(log_end[i]) <= 9 for i in inside), (
    "The cut wood must stay near the softer Almond Planks tones"
)
assert len({log_end[i] for i in inside}) >= 9, (
    "Large flat areas between rings need subtle intermediate shading"
)
assert max(__import__("collections").Counter(log_end[i] for i in inside).values()) <= 79, (
    "Too much of the tree-ring interior is monochrome"
)
assert sum(log_end[i]!=stripped_end[i] for i in outside)==60, (
    "Every outer-edge pixel must distinguish natural bark from stripped sapwood"
)
# Preserve the exact original vanilla oak end-ring silhouette while allowing
# subtle additional pixel shading INSIDE the broad growth bands. Every pixel
# stays closest to the same base tonal class as the oak-derived recolor.
import hashlib
oak_ring_classes=bytes(nearest_tone(log_end[i]) for i in inside)
assert hashlib.sha256(oak_ring_classes).hexdigest() == (
    "517228f8b8d5991a86638217a82066d8b7082d35a37bbafb90e0f5efb4d0da46"
), "The almond end grain must preserve the vanilla oak ring geometry"
# Pixel-level 90-degree corners are inherited from the oak reference;
# prevent later accidental rounding or custom ring replacement.
for x,y in ((3,3),(4,4),(7,2),(13,13),(7,7)):
    assert log_end[y*16+x]==stripped_end[y*16+x]
def brightness(colors):
    return sum(sum(c)/3 for c in colors)/len(colors)
assert brightness([log_end[i] for i in outside])+40 < brightness(
    [stripped_end[i] for i in outside]), "Natural bark must be darker than stripped rim"
assert brightness([log_end[i] for i in inside]) == brightness(
    [stripped_end[i] for i in inside])
# User-requested refinements soften harsh plank pixels while preserving its
# original grain arrangement and clear horizontal board seams.
plank_data=decoded["almond_planks"]
assert hashlib.sha256((A/"textures/block/almond_planks.png").read_bytes()).hexdigest() == (
    "afef2d5ba69a3b978a58cfb60eb86f1a51a45165926560f58f13fa7a2c755322"
)
assert len(set(plank_data))>=10
assert all(tone_distance(rgb)<=9 for rgb in plank_data)
plank_classes=bytes(nearest_tone(rgb) for rgb in plank_data)
assert hashlib.sha256(plank_classes).hexdigest() == (
    "916a6d6b0e4393aecbcff25512d5f1998f63eb516676823bf246a6808cb5be5d"
), "The recognizable Minecraft plank seam/grain layout changed"
def lum(rgb):
    return sum(a*b for a,b in zip(rgb,(0.2126,0.7152,0.0722)))
plank_jump=max(abs(lum(plank_data[y*16+x])-lum(plank_data[(y+dy)*16+x+dx]))
               for y in range(16) for x in range(16)
               for dx,dy in ((1,0),(0,1))
               if x+dx<16 and y+dy<16)
assert plank_jump < 90, "Isolated high-contrast plank pixels returned"
for seam in (3,7,11,15):
    assert sum(lum(plank_data[seam*16+x]) for x in range(16)) < (
        sum(lum(plank_data[(seam-1)*16+x]) for x in range(16))
    ), f"Wooden plank seam row {seam} lost definition"
# Approved dark fissures and lit bark ridges now have 1px transition
# shadows. The exposed wood is more finely shaded and shares the softer
# plank colors, while remaining directionally coherent rather than noisy.
bark_pixels=decoded["almond_log"]
stripped_pixels=decoded["stripped_almond_log"]
assert len(set(bark_pixels))>=16, "Bark crevices/ridges lost tonal gradations"
assert len(set(stripped_pixels))>=12, "Stripped longitudinal fibers are too flat"
assert all(60<=rgb[0]<=185 and 45<=rgb[1]<=145
           and 43<=rgb[2]<=112 for rgb in bark_pixels)
assert all(tone_distance(rgb)<=9 for rgb in stripped_pixels)
stripped_classes=bytes(nearest_tone(rgb) for rgb in stripped_pixels)
assert hashlib.sha256(stripped_classes).hexdigest() == (
    "56dfc9ba38af3ef6b6f1ec9905e2eb4f13bb7a94781277cf6860011ba0d8d4d4"
), "Stripped fiber layout should remain aligned to the original approved drawing"
for name,im in (("almond_log",bark_pixels),("stripped_almond_log",stripped_pixels)):
    vertical=sum(sum(abs(a-b) for a,b in zip(im[y*16+x],im[(y+1)*16+x]))/3
                 for y in range(15) for x in range(16))/240
    horizontal=sum(sum(abs(a-b) for a,b in zip(im[y*16+x],im[y*16+x+1]))/3
                   for y in range(16) for x in range(15))/240
    assert 8 < vertical < horizontal*.8, f"{name}: longitudinal grain lost"
    assert horizontal > 20, f"{name}: texture lacks readable ridges"
    assert min(len(set(im[y*16:(y+1)*16])) for y in range(16))>=6
assert all(tone_distance(stripped_end[i])<=9 for i in outside)
assert {log_end[i] for i in inside} == {stripped_end[i] for i in inside}
assert all(data not in {(A/"textures/block/olive_log.png").read_bytes(),(A/"textures/block/olive_planks.png").read_bytes()} for data in art)
print("OK: 16x16 RGBA opaque almond sprites with structured bark and plank-colored stripped grain")
print("OK: vanilla oak ring SILHOUETTE preserved, with nuanced 14x14 shading and unchanged 1px rim")
print("OK: fine bark/stripped-fiber shadow gradients remain vertically coherent")
print("OK: softer plank palette, fewer harsh pixel jumps, preserved board seams and grain")
print("OK: item models, tags, recipes, drops, axe stripping, fire, localization")
