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
for n in ("almond_shelf",):
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
assert all(tone_distance(log_end[i]) <= 12 for i in inside), (
    "The cut wood must stay near the softer Almond Planks tones"
)
from collections import Counter
assert len({log_end[i] for i in inside}) >= 13, (
    "The cut wood needs several visible intermediate tones inside the square rings"
)
assert max(Counter(log_end[i] for i in inside).values()) <= 60, (
    "Too many pixels between growth rings have the same flat color"
)
assert sum(log_end[i]!=stripped_end[i] for i in outside)==60, (
    "Every outer-edge pixel must distinguish natural bark from stripped sapwood"
)
# Preserve the oak-derived square ring silhouette instead of freezing
# RGB classes, which prevented stronger but still natural shading.
# The original dark-ring footprint is 43 pixels, in exactly the
# original positions; the remaining bands may use brighter/darker shades.
import hashlib
oak_dark_ring_mask=bytes(int(max(log_end[i])<189) for i in inside)
assert sum(oak_dark_ring_mask)==43
assert hashlib.sha256(oak_dark_ring_mask).hexdigest() == (
    "4fbab6026fcc71deb174e63508fa2dc01d04557136e5058cafc79fae816f82a9"
), "The oak-inspired square growth ring structure was damaged"
# Both natural and stripped cuts retain the approved, 1px external rims.
for name,top,expected in (
    ("almond_log_top",log_end,
     "4b014d766ecd0f573cb2d2b73837d2024d74dc3952f2235e889604038818a49c"),
    ("stripped_almond_log_top",stripped_end,
     "028ef29df1414e94e5339648ac6d9c0d94504508645f05f01cdf325ed83c6935")
):
    rim=bytes(v for i in outside for v in top[i])
    assert hashlib.sha256(rim).hexdigest()==expected, (
        f"{name}: the approved bark/sapwood rim must remain unchanged"
    )
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
# Deliberately fewer shades/isolated pixels than the last 'soil' version:
# visible plates and long fissures take priority over noisy gradients.
assert 7 <= len(set(bark_pixels)) <= 12, (
    "Bark needs restrained but recognizable shades, not noise or flat plates"
)
bark_dark=sum(sum(rgb)/3<96 for rgb in bark_pixels)
assert bark_dark <= 145, "Bark still contains too many dark scattered holes"
bark_sha=hashlib.sha256((A/"textures/block/almond_log.png").read_bytes()).hexdigest()
assert bark_sha == "68b1a9076049c3e3466c5aed66154279c936e52bee51eb912e60b9fd5c8f1e49", (
    "The original-color Almond bark redesign changed unexpectedly"
)
assert len(set(stripped_pixels))>=8, "Stripped longitudinal fibers need several soft tonal levels"
# Approved stripped-wood COLOR PALETTE retained, but its original unrelated
# groove map is replaced with a lighter version of the approved natural
# bark's now-irregular lengthwise grain. Only this SIDE PNG is changed;
# approved dark bark, cut-end wood rings and plank artwork stay untouched.
assert hashlib.sha256((A/"textures/block/stripped_almond_log.png").read_bytes()).hexdigest() == (
    "5c51b381a599593a36de5d25b6b2dd9f8383c3c47cdda38f07516afd0e9bec84"
), "Adapted stripped Almond wood artwork must not drift unnoticed"
assert hashlib.sha256((A/"textures/block/almond_log_top.png").read_bytes()).hexdigest() == (
    "3022f84cd1039849ecee528ed572a66f057927b00b3c1de0593832e879712540"
)
assert hashlib.sha256((A/"textures/block/stripped_almond_log_top.png").read_bytes()).hexdigest() == (
    "de0aa52080d28f22d4a65950a1dbceed6ffcaacd7392af87ccaa6567b66b5284"
)
# Every original brown color is retained exactly; no pale stripped-wood tint
# was introduced. The artist changed silhouette and arrangement, not palette.
ORIGINAL_BARK_PALETTE={
    (65,49,47),(77,56,50),(92,65,55),(108,76,61),(125,88,70),
    (142,103,81),(156,116,90),(171,132,102),(184,143,112)
}
assert set(bark_pixels)==ORIGINAL_BARK_PALETTE, (
    "Natural Almond bark must preserve the nine approved ORIGINAL colors"
)
mean_bark=tuple(sum(rgb[i] for rgb in bark_pixels)/256 for i in range(3))
assert all(abs(actual-reference)<3 for actual,reference in
           zip(mean_bark,(129.9,96.2,76.9))), (
    "Rebalanced bark has drifted from the original average chestnut tone"
)
# Both seamless edges now share the surrounding wood grain. Repainting
# just column 15 produced a detached vertical ribbon; the approved center
# stays byte-for-byte unchanged while edge pixels on BOTH sides are blended.
for name,central_sha,max_seam in (
    ("almond_log",
     "19987d8dbb8b9cf9b70ddcd5f92c30160a7a0a2277c14d1024afb12a5e9201bf",17),
    ("stripped_almond_log",
     "986b91a5987694e3bcd2f885cd0fb183ca9f09e8dabfbc5264629b33c9cc4398",13),
):
    pix=decoded[name]
    center=bytes(v for y in range(16) for x in range(2,13)
                 for v in pix[y*16+x])
    assert hashlib.sha256(center).hexdigest()==central_sha, (
        name+": approved middle 11 columns were unexpectedly modified"
    )
    for x in (0,15):
        col=[pix[y*16+x] for y in range(16)]
        assert len(set(col))>=3, (name,x,"flat monochrome border")
        runs=[1]
        for y in range(1,16):
            runs.append(runs[-1]+1 if col[y]==col[y-1] else 1)
        assert max(runs)<=4, (name,x,"vertical strip returned")
    assert max(abs(a-b) for y in range(16)
               for a,b in zip(pix[y*16+15],pix[y*16]))<=max_seam, (
        name+": two adjacent logs have an obvious high-contrast seam"
    )
    # The right-hand grain must also flow into columns x=13,14 rather
    # than standing as a detached single-column stripe.
    edge_jump=sum(sum(abs(a-b) for a,b in
                      zip(pix[y*16+x],pix[y*16+(x+1)%16]))/3
                  for y in range(16) for x in (13,14,15,0))/64
    assert edge_jump < (20 if name=="almond_log" else 10), (
        name+": painted edge is not integrated with interior texture"
    )
assert all(tone_distance(rgb)<=9 for rgb in stripped_pixels)
stripped_classes=bytes(nearest_tone(rgb) for rgb in stripped_pixels)
# The author's original dark palette is restored but rearranged into
# contiguous bark plates and interrupted vertical fissures, not the old
# randomized checkerboard and not the pale ploughed-soil design.
# The reworked stripped surface keeps lighter timber grain while following
# the new natural log's dark-groove distribution rather than the old map.
def luminance(rgb):
    return sum(a*b for a,b in zip(rgb,(.2126,.7152,.0722)))
def column_profile(im):
    return [sum(luminance(im[y*16+x]) for y in range(16))/16 for x in range(16)]
normal_profile=column_profile(bark_pixels)
exposed_profile=column_profile(stripped_pixels)
def profile_correlation(a,b):
    av=sum(a)/len(a)
    bv=sum(b)/len(b)
    numerator=sum((x-av)*(y-bv) for x,y in zip(a,b))
    denominator=(sum((x-av)**2 for x in a)*sum((y-bv)**2 for y in b))**.5
    return numerator/denominator
assert max(normal_profile)-min(normal_profile)>35, (
    "Natural bark must have readable shadowed fissures between raised plates"
)
assert sum(v<115 for v in normal_profile)>=2, (
    "Fine dark fissures of the new Almond bark disappeared"
)
assert sum(normal_profile)/16+25 < sum(exposed_profile)/16, (
    "Natural bark must remain darker than exposed stripped wood"
)
# Visually coherent means the two trunks share the same major
# vertical ridge and groove positions, not the same RGB/roughness.
assert profile_correlation(normal_profile,exposed_profile)>.90, (
    "Stripped wood must align with the user-approved natural Almond bark"
)
# Do not recolor the exposed wood: colors originate from its prior
# nine-swatch texture, with no new shades, blur or antialiasing.
APPROVED_EXPOSED_PALETTE={
    (186,146,118),(190,150,122),(198,159,128),
    (202,163,132),(206,167,136),(217,180,148),
    (220,183,152),(226,191,160),(229,195,164)
}
assert set(stripped_pixels)<=APPROVED_EXPOSED_PALETTE
assert len(set(stripped_pixels))>=8
assert all(abs(sum(stripped_pixels[i][c] for i in range(256))/256-old) < 2
           for c,old in enumerate((209.97,172.44,141.75))), (
    "The exposed timber average should preserve its previous light warmth"
)
for name,im in (("almond_log",bark_pixels),("stripped_almond_log",stripped_pixels)):
    vertical=sum(sum(abs(a-b) for a,b in zip(im[y*16+x],im[(y+1)*16+x]))/3
                 for y in range(15) for x in range(16))/240
    horizontal=sum(sum(abs(a-b) for a,b in zip(im[y*16+x],im[y*16+x+1]))/3
                   for y in range(16) for x in range(15))/240
    assert (8 if name=="almond_log" else 5) < vertical < horizontal*.8, (
        f"{name}: longitudinal grain lost"
    )
    assert horizontal > (20 if name=="almond_log" else 10), (
        f"{name}: texture lacks readable ridges"
    )
    assert min(len(set(im[y*16:(y+1)*16])) for y in range(16)) >= (4 if name=="almond_log" else 6)
assert all(tone_distance(stripped_end[i])<=9 for i in outside)
assert {log_end[i] for i in inside} == {stripped_end[i] for i in inside}
assert all(data not in {(A/"textures/block/olive_log.png").read_bytes(),(A/"textures/block/olive_planks.png").read_bytes()} for data in art)
print("OK: 16x16 RGBA opaque almond sprites with structured bark and plank-colored stripped grain")
print("OK: square oak ring footprint with stronger 14x14 tonal shading and both approved 1px rims")
print("OK: both Almond log tiles preserve their central 11 columns, blend both edges, and tile seamlessly")
print("OK: unchanged Almond planks SHA and board seams, no changes to approved variants")
print("OK: item models, tags, recipes, drops, axe stripping, fire, localization")
