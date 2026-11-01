#!/usr/bin/env python3
"""Minecraft 26.2: new Fruit Cherry signs and unchanged approved sprites."""
import hashlib
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art

R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
JAVA=Path("src/main/java/dev/italiansdelight/common/registry")
BLOCKS=(JAVA/"ModBlocks.java").read_text(encoding="utf-8")
ITEMS=(JAVA/"ModSigns.java").read_text(encoding="utf-8")
BE=(JAVA/"ModBlockEntities.java").read_text(encoding="utf-8")
CREATIVE=Path("src/main/java/dev/italiansdelight/common/item/group/ModItemGroups.java").read_text(encoding="utf-8")

def read(p):
    assert p.is_file(),p
    return json.loads(p.read_text(encoding="utf-8"))

# Names and vanilla placement: only 2 real inventory items, 4 block forms.
for n in ("sign","wall_sign","hanging_sign","wall_hanging_sign"):
    key="fruit_cherry_"+n
    assert 'ModRegistries.id("'+key+'")' in BLOCKS,key
    assert "new "+{"sign":"StandingSignBlock","wall_sign":"WallSignBlock",
           "hanging_sign":"CeilingHangingSignBlock",
           "wall_hanging_sign":"WallHangingSignBlock"}[n]+"(FRUIT_CHERRY_WOOD_TYPE" in BLOCKS,key
    blockstate=read(A/f"blockstates/{key}.json")["variants"]
    assert len(blockstate)==(32 if n=="hanging_sign" else 16 if n=="sign" else 4),key
    loot=read(D/f"italiansdelight/loot_table/blocks/{key}.json")
    item="fruit_cherry_hanging_sign" if "hanging" in n else "fruit_cherry_sign"
    assert loot["pools"][0]["entries"][0]["name"]=="italiansdelight:"+item,key
    assert f"BlockEntityTypes.{'HANGING_SIGN' if 'hanging' in n else 'SIGN'}.addValidBlock(ModBlocks.{key.upper()});" in BE
    tag={"sign":"standing_signs","wall_sign":"wall_signs","hanging_sign":"ceiling_hanging_signs",
         "wall_hanging_sign":"wall_hanging_signs"}[n]
    assert "italiansdelight:"+key in read(D/f"minecraft/tags/block/{tag}.json")["values"]
for n in ("sign","hanging_sign"):
    key="fruit_cherry_"+n
    assert f'new {"HangingSignItem" if n=="hanging_sign" else "SignItem"}(ModBlocks.{key.upper()}' in ITEMS
    assert CREATIVE.count(f"output.accept(ModSigns.{key.upper()});")==1
    assert "italiansdelight:"+key in read(D/f"minecraft/tags/item/{n+'s' if n=='sign' else 'hanging_signs'}.json")["values"]
    assert read(A/f"items/{key}.json")["model"]["model"]=="italiansdelight:item/"+key
    assert read(A/f"models/item/{key}.json")["textures"]["layer0"]=="italiansdelight:item/"+key
    recipe=read(D/f"italiansdelight/recipe/{key}.json")
    assert recipe["result"]=={"id":"italiansdelight:"+key,"count":6 if n=="hanging_sign" else 3}
    if n=="sign":
        assert recipe["key"]=={"P":"italiansdelight:fruit_cherry_planks","S":"minecraft:stick"}
        assert recipe["pattern"]==["PPP","PPP"," S "]
    else:
        assert recipe["key"]=={"C":"minecraft:iron_chain","P":"italiansdelight:stripped_fruit_cherry_log"}
        assert recipe["pattern"]==["C C","PPP","PPP"]
    for locale in ("en_us","it_it"):
        lang=read(A/f"lang/{locale}.json")
        assert lang["item.italiansdelight."+key]
        assert lang["block.italiansdelight."+key]

# Match vanilla 26.2 state rotation, attached hanging variants and side orientation.
for key,kind in (("fruit_cherry_sign","sign"),("fruit_cherry_hanging_sign","hanging_sign")):
    states=read(A/f"blockstates/{key}.json")["variants"]
    for rotation in range(16):
        for attached in (["false","true"] if kind=="hanging_sign" else [None]):
            k=f"rotation={rotation}" if attached is None else f"attached={attached},rotation={rotation}"
            o=states[k]
            prefix=key+("_attached" if attached=="true" else "")
            stem=f"{prefix}_rot_{rotation%4}"
            assert o["model"]=="italiansdelight:block/"+stem
            assert o.get("y",0)==(rotation//4)*90
            m=read(A/f"models/block/{stem}.json")
            stem_parent=("template_sign" if kind=="sign" else
                         "template_attached_hanging_sign" if attached=="true" else "template_hanging_sign")
            assert m["parent"]==f"minecraft:block/{stem_parent}_rot_{rotation%4}"
            assert m["textures"]["all"]=="italiansdelight:block/"+key
for n in ("fruit_cherry_wall_sign","fruit_cherry_wall_hanging_sign"):
    m=read(A/f"models/block/{n}.json")
    assert m["textures"]["all"]=="italiansdelight:block/"+("fruit_cherry_hanging_sign" if "hanging" in n else "fruit_cherry_sign")

# 8 unique, handmade RGBA assets. Vanilla alpha silhouettes preserved,
# including the exact GUI/editor resolutions and separate 64x32 entity atlases.
assets={
    "block/fruit_cherry_sign.png":((32,32),836),
    "block/fruit_cherry_hanging_sign.png":((32,32),632),
    "item/fruit_cherry_sign.png":((16,16),132),
    "item/fruit_cherry_hanging_sign.png":((16,16),148),
    "gui/signs/fruit_cherry.png":((24,26),316),
    "gui/hanging_signs/fruit_cherry.png":((16,16),188),
    "entity/sign/fruit_cherry.png":((64,32),840),
    "entity/sign/hanging/fruit_cherry.png":((64,32),632)
}
for path,(dims,filled) in assets.items():
    pixels=check_rgba_pixel_art(A/"textures"/path,dims)
    assert sum(p[3]==255 for row in pixels for p in row)==filled,path
    # A 16px vanilla item / editor thumbnail intentionally uses fewer shades.
    min_tones=4 if path.startswith(("item/","gui/")) else 5
    assert len({p[:3] for row in pixels for p in row if p[3]==255})>=min_tones,path
# Freeze the UV/alpha masks of all eight vanilla-compatible atlases.
# Only grain and wood shading were redrawn, not sign geometry or metal chains.
alpha_checks={
    "block/fruit_cherry_sign.png":"14a809f59cb0066b3c92c2853be5940afdb338858dfa043ed6179e8cc4346fba",
    "block/fruit_cherry_hanging_sign.png":"8891e87cfca5c59a4f1c8dcec4bae192e93a3c29ed52c48e30c35475b59a67db",
    "item/fruit_cherry_sign.png":"0680e022b903c50de3e543253779bc225c14e802fe12c5d174581189acb9aa5b",
    "item/fruit_cherry_hanging_sign.png":"f3acbeb2c1574628720d5e4a09ea772afb28f32a25b2072bca8f48c5a1cbe9d1",
    "gui/signs/fruit_cherry.png":"40f2bbec67865c5b8ad356fe729c8208b37fae198f0896acbbeb6cf8642dc17c",
    "gui/hanging_signs/fruit_cherry.png":"78a626d250553172f1ef7125d0818634ea17779cc7ba0ba70562bd2066ab1634",
    "entity/sign/fruit_cherry.png":"ae740014329219e3d434f465fb2e023136ca141c1d01d14f07d1649168325949",
    "entity/sign/hanging/fruit_cherry.png":"b78da22b2c336545626dac22fb36e9faa6f1a6764a8978a402100c4b1c661469",
}
for path,expect in alpha_checks.items():
    dims=assets[path][0]
    pixels=check_rgba_pixel_art(A/"textures"/path,dims)
    silhouette=bytes(px[3] for row in pixels for px in row)
    assert hashlib.sha256(silhouette).hexdigest()==expect,(
        path,"sign or chain silhouette shifted"
    )
# Compare with previously approved Almond sign: normal sign is made from
# real cherry PLANKS, exactly as the user established for the Almond stage.
# The dark separator strokes y=1,5,9,13 and immediately highlighted
# next row must be present rather than a flat invented smooth surface.
front=check_rgba_pixel_art(A/"textures/block/fruit_cherry_sign.png",(32,32))
planks=check_rgba_pixel_art(A/"textures/block/fruit_cherry_planks.png",(16,16))
stripped=check_rgba_pixel_art(A/"textures/block/stripped_fruit_cherry_log.png",(16,16))
plank_colors={p[:3] for row in planks for p in row}
stripped_colors={p[:3] for row in stripped for p in row}
assert {front[y][x][:3] for y in range(14) for x in range(24)} <= plank_colors, (
    "Normal sign face must be constructed from authentic approved Cherry planks"
)
def mean_luma(seq):
    return sum(sum(p[:3])/3 for p in seq)/len(seq)
for seam in (1,5,9):
    dark=mean_luma([front[seam][x] for x in range(2,22)])
    lighter=mean_luma([front[seam+1][x] for x in range(2,22)])
    assert 17 < lighter-dark < 42, (
        "Approved vanilla wood-board alignments (y1,5,9) missing", seam,lighter-dark
    )
assert {front[y][x][:3] for y in range(16,30) for x in range(24)} <= plank_colors
hf=check_rgba_pixel_art(A/"textures/block/fruit_cherry_hanging_sign.png",(32,32))
assert {hf[y][x][:3] for y in range(16,26) for x in range(16)} <= stripped_colors, (
    "Hanging sign must use actual stripped Fruit Cherry log tones"
)
# Hanging sign is intentionally DIFFERENT from four-board standing sign.
assert len({hf[y][x][:3] for y in range(16,26) for x in range(16)})>=7
assert len({front[y][x][:3] for y in range(14) for x in range(24)})==5

normal=check_rgba_pixel_art(A/"textures/item/fruit_cherry_sign.png",(16,16))
hanging=check_rgba_pixel_art(A/"textures/item/fruit_cherry_hanging_sign.png",(16,16))
assert all(p[3]==0 for p in normal[0]+normal[1])
assert all(normal[y][x][3]==255 for y in range(2,11) for x in range(2,15))
assert all(normal[y][x][3]==255 for y in range(11,16) for x in range(7,10))
assert all(p[3]==0 for p in hanging[0])
# Distinct woodworking, no copied Almond sign glyphs or single-color slab.
sign=check_rgba_pixel_art(A/"textures/block/fruit_cherry_sign.png",(32,32))
assert len({sign[y][x] for y in range(3,12) for x in range(2,22)})>=4
hang=check_rgba_pixel_art(A/"textures/block/fruit_cherry_hanging_sign.png",(32,32))
metal={(37,44,61),(62,68,83),(73,80,101),(91,100,124)}
assert sum(p[:3] in metal and p[3] for row in hang for p in row)>=90


# Material identity is intentional, not an independent recoloring.
# World, inventory, editor and entity must agree with original approved woods.
bark=check_rgba_pixel_art(A/"textures/block/fruit_cherry_log.png",(16,16))
bark_colors={p[:3] for row in bark for p in row}
for y in list(range(14))+list(range(16,32)):
    for x in range(28,32):
        px=front[y][x]
        if px[3]:
            assert px[:3] in bark_colors, ("Standing sign UV pole must use normal log bark",x,y)
gui=check_rgba_pixel_art(A/"textures/gui/signs/fruit_cherry.png",(24,26))
assert all(gui[y][x]==front[y+16][x] for y in range(12) for x in range(24)), (
    "Sign editor must display the back-face board after its two-pixel upward shift"
)
assert all(gui[y][x][:3] in bark_colors for y in range(12,26) for x in (11,12))
# Vanilla icons have a stylized 25-pixel inscription, not a blank plank board.
# The approved olive sprite encodes the precise 26.2 positions and two shades.
olive_item=check_rgba_pixel_art(A/"textures/item/olive_sign.png",(16,16))
ink_colors={(86,49,25),(126,68,34)}
ink_positions=[(y,x) for y in range(2,10) for x in range(2,15)
               if olive_item[y][x][:3] in ink_colors]
assert len(ink_positions)==25
for y,x in ink_positions:
    expected=(104,59,45) if olive_item[y][x][:3]==(86,49,25) else (124,70,52)
    assert normal[y][x][:3]==expected, ("Vanilla item sign inscription lost",x,y)
assert all(normal[y][x][:3] in plank_colors | bark_colors
           for y in range(2,11) for x in range(2,15)), (
    "The inventory sprite must use approved cherry plank and bark tones"
)
almond_hanging=check_rgba_pixel_art(A/"textures/item/almond_hanging_sign.png",(16,16))
assert all(hanging[y][x][3]==almond_hanging[y][x][3]
           for y in range(16) for x in range(16)),(
    "Hanging inventory icon must preserve vanilla item geometry"
)
assert sum(hanging[y][x][:3]==(143,91,66) for y in range(6,14) for x in range(2,14))>0,(
    "Hanging item must reproduce the engraved vanilla sign design"
)
assert all(normal[y][x][:3] in bark_colors for y in range(11,16) for x in range(7,10))
entity=check_rgba_pixel_art(A/"textures/entity/sign/fruit_cherry.png",(64,32))
assert all(entity[y][x][:3] in bark_colors for y in range(16,30) for x in range(8))
# Steel chains are neutral-grey; all visually wooden pixels on *every*
# hanging-sign asset must originate from the approved stripped Cherry log.
for asset,dimensions in (
    ("block/fruit_cherry_hanging_sign.png",(32,32)),
    ("item/fruit_cherry_hanging_sign.png",(16,16)),
    ("gui/hanging_signs/fruit_cherry.png",(16,16)),
    ("entity/sign/hanging/fruit_cherry.png",(64,32)),
):
    pixels=check_rgba_pixel_art(A/"textures"/asset,dimensions)
    for row in pixels:
        for rgba in row:
            r,g,b,a=rgba
            if a and r>g+14 and g>b+9 and r>110:
                assert (r,g,b) in stripped_colors,(asset,"Not genuine stripped-log wood",rgba)

# Only the standing sign reverse-face plank UV rows move upward by 2px.
# The front board preserves the approved grain and board join positions.
for seam in (3,7,11):
    dark=mean_luma([front[16+seam][x] for x in range(2,22)])
    lighter=mean_luma([front[17+seam][x] for x in range(2,22)])
    assert 17 < lighter-dark < 42,("Reverse face needs 2px upward offset",seam)
# Regression lock: approved hanging sign block, editing GUI and legacy atlas.
hanging_frozen={
    "block/fruit_cherry_hanging_sign.png":"91a63387c6db8ef89bdfd34f18a87501ec099717",
    "gui/hanging_signs/fruit_cherry.png":"51c01ef1270629e315f41f429a636ea7fa76ed19",
    "entity/sign/hanging/fruit_cherry.png":"8314478a68d52d0b16a501494b45c9fb9aaa9e9f",
}
for path,sha in hanging_frozen.items():
    b=(A/"textures"/path).read_bytes()
    assert hashlib.sha1(b"blob "+str(len(b)).encode()+b"\0"+b).hexdigest()==sha,(
        "Approved hanging sign asset must not be modified",path
    )

# Keep every previously approved fruit cherry base, door and trapdoor unchanged.
approved={
"fruit_cherry_log.png":"630180b174460c2c53e2a75d3b29efe0fa403da8",
"fruit_cherry_log_top.png":"7a44276cce4f6d3d30ebee10383c2649c45136f2",
"fruit_cherry_planks.png":"d45def1e1a00bb93f2372f11b4dc79185ab343ff",
"stripped_fruit_cherry_log.png":"3ebad8726bf7a0fe64cd6ffde7546ef7e6dd2184",
"stripped_fruit_cherry_log_top.png":"a95a0fade44f58854bbfd5560f41d2e4e7daef21",
"fruit_cherry_door_bottom.png":"40311b22fb74b57718a5e7e9a2c0ee61e798293a",
"fruit_cherry_door_bottom_edge.png":"8c5b8c679ba26e945779451d33d261b545a61476",
"fruit_cherry_door_top.png":"dcfa13880c52dfc63b65028d1146cced31f41bb0",
"fruit_cherry_door_top_edge.png":"4207c711703932dab2b36a2bbcc3ae0b035695e3",
"fruit_cherry_trapdoor.png":"06c6caf36483078d33fd7eec35264dd57712fb5e",
}
for filename,sha in approved.items():
    b=(A/"textures/block"/filename).read_bytes()
    current=hashlib.sha1(b"blob "+str(len(b)).encode()+b"\0"+b).hexdigest()
    assert current==sha,(filename,"Approved asset modified unexpectedly")

print("OK: cherry normal/wall/hanging/ceiling signs, 26.2 rotations and loot")
print("OK: approved hanging sign block/GUI unchanged; item vanilla glyphs restored")
print("OK: authentic cherry planks on normal sign, stripped log on hanging sign")
print("OK: normal sign reverse UV and text-editor board shifted 2px up")
print("OK: all previously approved cherry base wood, door and trapdoor textures unchanged")
