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


# Prevent mere palette recolors of Almond. Compare spatial rank patterns,
# not RGB, and preserve key details of the original Cherry design.
from check_orchard_signs_and_cutting import check_rgba_pixel_art

def ordered_tone_signature(pixels):
    tones = sorted({px[:3] for row in pixels for px in row if px[3]}, key=lambda col: (sum(col), col))
    groups = {col:min(4, 5*i//len(tones)) for i, col in enumerate(tones)}
    return [groups[px[:3]] if px[3] else -1 for row in pixels for px in row]

for name, size in new_assets.items():
    new_pixels = check_rgba_pixel_art(A/"textures"/name, size)
    almond_pixels = check_rgba_pixel_art(A/"textures"/name.replace("fruit_cherry","almond"), size)
    new_sig, almond_sig = ordered_tone_signature(new_pixels), ordered_tone_signature(almond_pixels)
    mismatches = sum(a != b for a, b in zip(new_sig, almond_sig))
    assert mismatches >= (250 if size == (32, 32) else 94), (
        "Cherry panel design too similar to Almond; recoloring is insufficient", name, mismatches
    )
front_closed = check_rgba_pixel_art(A/"textures/block/fruit_cherry_cabinet_front.png", (16,16))
assert front_closed[7][6][:3] == (84,82,78) and front_closed[7][9][:3] == (84,82,78), (
    "Cabinet must retain two readable iron handles"
)
assert len({front_closed[y][4][:3] for y in (4,5,6,7,8,9,10,11)}) >= 4, (
    "Cabinet doors must retain short horizontal slats, not flat Almond recesses"
)

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
print("OK: newly carved Cherry furniture motifs distinct from Almond even without colors")
print("OK: approved cherry planks and sign artwork unchanged")
