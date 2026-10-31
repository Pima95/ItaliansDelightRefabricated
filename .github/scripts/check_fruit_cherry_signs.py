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
    assert len({p[:3] for row in pixels for p in row if p[3]==255})>=5,path
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
print("OK: vanilla item silhouettes, GUI/editor and entity UVs; original copper artwork")
print("OK: all previously approved cherry base wood, door and trapdoor textures unchanged")
