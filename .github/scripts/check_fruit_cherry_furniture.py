#!/usr/bin/env python3
"""Validate Fruit Cherry shelf, FD cabinet, cheese-aging rack and approved texture preservation."""
import hashlib
import json
import struct
import zlib
from pathlib import Path

A = Path("src/main/resources/assets/italiansdelight")
D = Path("src/main/resources/data")
J = Path("src/main/java/dev/italiansdelight")
WOOD = "fruit_cherry"
BLOCKS = [f"{WOOD}_shelf", f"{WOOD}_cabinet", f"{WOOD}_cheese_aging_rack"]

def read(path):
    assert path.is_file(), f"Missing {path}"
    return json.loads(path.read_text(encoding="utf-8"))

def png(path, dim):
    data=path.read_bytes()
    assert data[:8]==b"\x89PNG\r\n\x1a\n", path
    assert struct.unpack_from(">II", data, 16)==dim,(path,dim)
    assert data[24:27]==bytes([8,6,0]), (path, "pixel-art RGBA8 expected")
    p=8
    while p<len(data):
        n=struct.unpack_from(">I",data,p)[0]
        assert p+n+12<=len(data),(path,"truncated")
        tag=data[p+4:p+8]; raw=data[p+8:p+8+n]
        assert zlib.crc32(tag+raw)==struct.unpack_from(">I",data,p+8+n)[0],(path,"CRC mismatch",tag)
        p+=n+12
    assert p==len(data)
    return data

blocks = (J/"common/registry/ModBlocks.java").read_text()
entities = (J/"common/registry/ModBlockEntities.java").read_text()
creative = (J/"common/item/group/ModItemGroups.java").read_text()
generator = (Path(".github/scripts/generate_aging_rack_recipes.py")).read_text()
for name in BLOCKS:
    const=name.upper()
    assert creative.count(f"output.accept(ModBlocks.{const});")==1,name
    assert "italiansdelight:"+name in read(D/"minecraft/tags/block/mineable/axe.json")["values"],name
    assert read(D/f"italiansdelight/loot_table/blocks/{name}.json")["pools"][0]["entries"][0]["name"]=="italiansdelight:"+name,name
    assert read(A/f"items/{name}.json")["model"]["model"]==("italiansdelight:item/fruit_cherry_cabinet" if name=="fruit_cherry_cabinet" else f"italiansdelight:block/{name+'_inventory' if name=='fruit_cherry_shelf' else name}"),name
    for lang in ("en_us","it_it"):
        assert read(A/f"lang/{lang}.json")[f"block.italiansdelight.{name}"],(lang,name)

assert 'ModRegistries.id("fruit_cherry_shelf")' in blocks
assert 'new ShelfBlock(Block.Properties.ofFullCopy(Blocks.OAK_SHELF).setId(FRUIT_CHERRY_SHELF_KEY))' in blocks
assert 'BlockEntityTypes.SHELF.addValidBlock(ModBlocks.FRUIT_CHERRY_SHELF)' in entities
assert 'new CabinetBlock(Block.Properties.ofFullCopy(Blocks.BARREL).setId(FRUIT_CHERRY_CABINET_KEY))' in blocks
assert '.addValidBlock(ModBlocks.FRUIT_CHERRY_CABINET)' in entities
assert 'registerRack("fruit_cherry", FRUIT_CHERRY_PLANKS)' in blocks
assert 'ModBlocks.FRUIT_CHERRY_CHEESE_AGING_RACK' in entities
assert '"fruit_cherry"' in generator

for tag in ("block","item"):
    assert "italiansdelight:fruit_cherry_shelf" in read(D/f"minecraft/tags/{tag}/wooden_shelves.json")["values"]
assert len(read(A/"blockstates/fruit_cherry_shelf.json")["multipart"])==24
assert len(read(A/"blockstates/fruit_cherry_cabinet.json")["variants"])==8
assert len(read(A/"blockstates/fruit_cherry_cheese_aging_rack.json")["multipart"])>=10

expected_parents={
    "fruit_cherry_shelf":"minecraft:block/template_shelf_body",
    "fruit_cherry_shelf_inventory":"minecraft:block/template_shelf_inventory",
    "fruit_cherry_shelf_unpowered":"minecraft:block/template_shelf_unpowered",
    "fruit_cherry_shelf_unconnected":"minecraft:block/template_shelf_unconnected",
    "fruit_cherry_shelf_center":"minecraft:block/template_shelf_center",
    "fruit_cherry_shelf_left":"minecraft:block/template_shelf_left",
    "fruit_cherry_shelf_right":"minecraft:block/template_shelf_right",
    "fruit_cherry_cabinet":"minecraft:block/orientable",
}
for name,parent in expected_parents.items():
    model=read(A/f"models/block/{name}.json")
    assert model["parent"]==parent,(name,model["parent"])
    if "shelf" in name:
        assert model["textures"]["all"]=="italiansdelight:block/fruit_cherry_shelf"
assert read(A/"models/block/fruit_cherry_cheese_aging_rack.json")["textures"]["wood"]=="italiansdelight:block/fruit_cherry_planks"

shelf_recipe=read(D/"italiansdelight/recipe/fruit_cherry_shelf.json")
assert shelf_recipe["pattern"]==["###","   ","###"]
assert shelf_recipe["key"]=={"#":"italiansdelight:stripped_fruit_cherry_log"}
assert shelf_recipe["result"]=={"id":"italiansdelight:fruit_cherry_shelf","count":6}
cabinet_recipe=read(D/"italiansdelight/recipe/fruit_cherry_cabinet.json")
assert cabinet_recipe["key"]=={"_":"italiansdelight:fruit_cherry_slab","D":"italiansdelight:fruit_cherry_trapdoor"}
assert cabinet_recipe["pattern"]==["___","D D","___"]
assert cabinet_recipe["result"]=={"id":"italiansdelight:fruit_cherry_cabinet","count":1}
rack_recipe=read(D/"italiansdelight/recipe/crafting/fruit_cherry_cheese_aging_rack.json")
assert rack_recipe["key"]=={"S":"italiansdelight:fruit_cherry_fence","B":"italiansdelight:fruit_cherry_planks"}
assert rack_recipe["pattern"]==["S S"," B ","S S"]
assert rack_recipe["result"]=={"id":"italiansdelight:fruit_cherry_cheese_aging_rack","count":1}

new_assets={
    "block/fruit_cherry_shelf.png":(32,32),
    "block/fruit_cherry_cabinet_front.png":(16,16),
    "block/fruit_cherry_cabinet_front_open.png":(16,16),
    "block/fruit_cherry_cabinet_side.png":(16,16),
    "block/fruit_cherry_cabinet_top.png":(16,16),
}
for name,size in new_assets.items():
    new=png(A/"textures"/name,size)
    original=png(A/"textures"/name.replace("fruit_cherry","almond"),size)
    assert new!=original,("Furniture not recolored",name)


# Semantic vanilla-art checks: do NOT enforce arbitrary per-pixel variation.
# Requiring every 3x3 wood region to differ was introducing fake confetti;
# intentional flat surfaces are part of Minecraft's readable pixel art.
from check_orchard_signs_and_cutting import check_rgba_pixel_art

shelf=check_rgba_pixel_art(A/"textures/block/fruit_cherry_shelf.png",(32,32))
wood=check_rgba_pixel_art(A/"textures/block/stripped_fruit_cherry_log.png",(16,16))
assert all(shelf[y][x]==wood[y][x-16] for y in range(16) for x in range(16,32)), (
    "Shelf rear UV must be sampled exactly from approved stripped Fruit Cherry wood"
)
# Same UV regions as the accepted vanilla 26.2 Almond/Olive shelves.
uv={
  "three_slot_face":(0,4,16,12),
  "powered_left":(0,16,16,24),
  "powered_right":(16,16,32,24),
  "powered_center":(0,24,16,32),
  "powered_standalone":(16,24,32,32),
}
for name,(x0,y0,x1,y1) in uv.items():
    assert len({shelf[y][x][:3] for y in range(y0,y1) for x in range(x0,x1)})>=5, (
        "Shelf overlay lacks vanilla wood bevels",name
    )
# Consecutive shelf faces must join without suddenly changing the timber
# grain across the powered center/standalone segments.
for y in range(8):
    # No left-side divider in the center shelf's bottom-left atlas tile.
    # x=1 must blend with x=0 and x=2 instead of preserving the left jamb.
    assert shelf[24+y][1]==shelf[24+y][2], (
        "Center shelf has a stray separator at atlas x=1", y
    )
    assert all(shelf[24+y][x]==shelf[16+y][x] for x in range(2,15))
    assert shelf[24+y][0]==shelf[16+y][2]
    assert shelf[24+y][15]==shelf[16+y][12]
    assert all(shelf[24+y][16+x]==shelf[16+y][x] for x in range(15))
    assert shelf[24+y][31]==shelf[16+y][31]
# Three physical recesses: depth at the jamb, inset at the center.
for y in (7,9,10):
    for x in (1,6,11):
        assert sum(shelf[y][x][:3])<sum(shelf[y][x+2][:3]), (
            "Unpowered shelf must have three correctly placed slots",x,y
        )

# Regressions requested for the Fruit Cherry native shelf:
# 1. The wood behind the actual 3 empty-item niches must be a soft,
#    continuous 2-shade gradient, not a checkerboard of unrelated contrasts.
cherry_tones={(143,91,66),(154,99,72),(166,109,79),(180,119,86),
              (191,129,94),(202,138,102),(213,149,112),(225,162,122)}
assert {pixel[:3] for row in shelf for pixel in row}<=cherry_tones
for i in (0,5,10):
    niche=[shelf[y][x][:3] for y in range(6,10) for x in range(i+1,i+5)]
    assert set(niche)=={(166,109,79),(180,119,86)},(
        "Interior pixels must use only adjacent soft wood shades",i
    )
    assert max(sum(c) for c in niche)-min(sum(c) for c in niche)==31
# 2. Top/bottom fascia each carry a matching low-contrast 3×3 carved
#    cherry-blossom motif, but never in the item openings or the rear UV.
for cx in (4,11):
    for cy in (1,13):
        assert shelf[cy][cx][:3]==(166,109,79),("Cherry blossom engraving missing",cx,cy)
        assert shelf[cy][cx-1][:3]==(180,119,86)
        assert shelf[cy][cx+1][:3]==(180,119,86)
# 3. No model, recipe or block entity change needed: all 5 native
#    connected/unconnected atlas locations are still stitched pixel-perfectly.

front=check_rgba_pixel_art(A/"textures/block/fruit_cherry_cabinet_front.png",(16,16))
opened=check_rgba_pixel_art(A/"textures/block/fruit_cherry_cabinet_front_open.png",(16,16))
side=check_rgba_pixel_art(A/"textures/block/fruit_cherry_cabinet_side.png",(16,16))
top=check_rgba_pixel_art(A/"textures/block/fruit_cherry_cabinet_top.png",(16,16))
# Two raised wood doors with a functional central join and deliberately
# paired silver handles, as in Farmer's Delight's cabinet design grammar.
for x in (7,8):
    assert front[7][x][:3]==(175,167,144)
    assert front[8][x][:3]==(113,111,102)
for x in (3,10):
    assert sum(front[6][x][:3])>sum(front[6][x-1][:3]), (
        "Two cabinet doors must have a correctly chamfered fielded panel",x
    )
# Open cabinet: a single broad top recess plus an uninterrupted physical
# middle shelf and two distinct lower compartments separated by a stile.
assert sum(opened[8][6][:3])>sum(opened[7][6][:3])
assert sum(opened[10][7][:3])>sum(opened[10][6][:3])
# Joinery has a consistent direction: vertical slats at the side,
# horizontal solid boards in the top.
assert all(side[y][5][:3]!=side[y][6][:3] for y in (4,6,8,10))
assert all(top[y][5][:3]!=top[y+1][5][:3] for y in (6,10))
approved_tones={
    (109,63,50),(126,76,59),(143,91,66),(154,99,72),
    (166,109,79),(180,119,86),(191,129,94),
    (202,138,102),(213,149,112),(225,162,122),
}
hardware={(113,111,102),(175,167,144)}
for name,texture in (
    ("front",front),("open",opened),("side",side),("top",top)
):
    colors={px[:3] for row in texture for px in row}
    assert colors<=approved_tones|hardware, (
        "Cabinet shading must reuse approved cherry wood colors",name
    )
    assert 7<=len(colors)<=12,("Maintain restrained wood shading",name)

# No previously approved assets should change during this furniture-only phase.
approved={
    "block/fruit_cherry_planks.png":"d45def1e1a00bb93f2372f11b4dc79185ab343ff",
    "block/fruit_cherry_sign.png":"bf1026643591f8637aa9ca0f2cd44337e8c92114",
    "item/fruit_cherry_sign.png":"ad0ecf5c5f05345780a2244a172d5505fc1cfaf0",
    "gui/signs/fruit_cherry.png":"d4f8deea07eb990e87ef83d85fcf54c4bbc318b3",
    "block/fruit_cherry_hanging_sign.png":"91a63387c6db8ef89bdfd34f18a87501ec099717",
    "item/fruit_cherry_hanging_sign.png":"fcd834967fc5a2325ff8f452d5f024f53fe92ae3",
    "gui/hanging_signs/fruit_cherry.png":"51c01ef1270629e315f41f429a636ea7fa76ed19",
    "entity/sign/hanging/fruit_cherry.png":"8314478a68d52d0b16a501494b45c9fb9aaa9e9f",
}
for name,sha in approved.items():
    raw=(A/"textures"/name).read_bytes()
    assert hashlib.sha1(b"blob "+str(len(raw)).encode()+b"\0"+raw).hexdigest()==sha,("Approved texture altered",name)

print("OK: functional vanilla shelf, FD cabinet, cheese-aging rack and associated block entity types")
print("OK: valid 24-part shelf, 8-state cabinet, rack models, 5 new PNGs, recipes, tags and names")
print("OK: native 3-slot shelf, wood-grain UV layout, FD-style cabinet frame and storage recesses")
print("OK: intentional wood grain and restrained cabinet shading without artificial noise tests")
print("OK: approved cherry planks and sign artwork unchanged")
