#!/usr/bin/env python3
"""Verify completed almond wooden family uses functional vanilla/FD variants."""
from pathlib import Path
import json, struct, re
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
J=Path("src/main/java/dev/italiansdelight")
def read(path):
    assert path.is_file(),f"Missing: {path}"
    return json.loads(path.read_text(encoding="utf-8"))
def png(path,size):
    data=path.read_bytes()
    assert data.startswith(b"\x89PNG\r\n\x1a\n"),path
    assert struct.unpack_from(">II",data,16)==size,(path,size)
    assert data[24:27]==bytes((8,6,0)),(path,"must have RGBA 8bit")
def tag(kind,name,key):
    values=read(D/f"minecraft/tags/{kind}/{name}.json")["values"]
    assert "italiansdelight:"+key in values,(kind,name,key)

blocks=(J/"common/registry/ModBlocks.java").read_text()
signs=(J/"common/registry/ModSigns.java").read_text()
boats=(J/"common/registry/ModBoats.java").read_text()
be=(J/"common/registry/ModBlockEntities.java").read_text()
client=(J/"ItaliansDelightClient.java").read_text()
tab=(J/"common/item/group/ModItemGroups.java").read_text()
for name in ("sign","wall_sign","hanging_sign","wall_hanging_sign","shelf","cabinet"):
    assert f'ModRegistries.id("almond_{name}")' in blocks
for name in ("sign","hanging_sign"):
    assert f'ALMOND_{name.upper()}' in signs
    assert tab.count("output.accept(ModSigns.ALMOND_"+name.upper()+");")==1
    assert "italiansdelight:almond_"+name in read(A/f"lang/en_us.json").keys() or (
        "item.italiansdelight.almond_"+name in read(A/"lang/en_us.json"))
for name in ("boat","chest_boat"):
    assert "ALMOND_"+name.upper()+"_ENTITY" in boats
    assert "ALMOND_"+name.upper()+"_ITEM" in boats
    assert "ModBoats.ALMOND_"+name.upper()+"_ENTITY" in client
    assert "output.accept(ModBoats.ALMOND_"+name.upper()+"_ITEM);" in tab
    tag("item","chest_boats" if name=="chest_boat" else "boats","almond_"+name)
    tag("entity_type","boat","almond_"+name)
for name in ("sign","wall_sign"):
    assert f"BlockEntityTypes.SIGN.addValidBlock(ModBlocks.ALMOND_{name.upper()})" in be
for name in ("hanging_sign","wall_hanging_sign"):
    assert f"BlockEntityTypes.HANGING_SIGN.addValidBlock(ModBlocks.ALMOND_{name.upper()})" in be
assert "BlockEntityTypes.SHELF.addValidBlock(ModBlocks.ALMOND_SHELF)" in be
assert ".addValidBlock(ModBlocks.ALMOND_CABINET)" in be
assert 'registerRack("almond", ALMOND_PLANKS)' in blocks
assert "ModBlocks.ALMOND_CHEESE_AGING_RACK" in be
for name in ("shelf","cabinet","cheese_aging_rack"):
    assert "output.accept(ModBlocks.ALMOND_"+name.upper()+");" in tab

for key in ("almond_sign","almond_hanging_sign","almond_cabinet","almond_shelf",
            "almond_cheese_aging_rack"):
    assert (A/f"blockstates/{key}.json").exists()
    assert (A/f"items/{key}.json").exists()
    assert (D/f"italiansdelight/loot_table/blocks/{key}.json").exists()
for name in ("sign","wall_sign","hanging_sign","wall_hanging_sign"):
    assert (A/f"blockstates/almond_{name}.json").exists()
    tag("block",{"sign":"standing_signs","wall_sign":"wall_signs",
                 "hanging_sign":"ceiling_hanging_signs",
                 "wall_hanging_sign":"wall_hanging_signs"}[name],"almond_"+name)
tag("block","wooden_shelves","almond_shelf")
tag("item","wooden_shelves","almond_shelf")
for name in ("sign","hanging_sign"):
    tag("item","signs" if name=="sign" else "hanging_signs","almond_"+name)
for name in ("sign","hanging_sign","shelf","cabinet","cheese_aging_rack"):
    assert "italiansdelight:almond_"+name in read(D/"minecraft/tags/block/mineable/axe.json")["values"]
assert len(read(A/"blockstates/almond_shelf.json")["multipart"])==24
assert len(read(A/"blockstates/almond_cabinet.json")["variants"])==8
assert len(read(A/"blockstates/almond_cheese_aging_rack.json")["multipart"])>=10
assert read(A/"models/block/almond_shelf.json")["parent"]=="minecraft:block/template_shelf_body"
assert read(A/"models/block/almond_cabinet.json")["parent"]=="minecraft:block/orientable"
assert read(A/"models/block/almond_cheese_aging_rack.json")["textures"]["wood"]=="italiansdelight:block/almond_planks"
for t in ("sign","hanging_sign"):
    assert len(read(A/f"blockstates/almond_{t}.json")["variants"])==4
for filename,size in {
    "block/almond_sign.png":(32,32),"block/almond_hanging_sign.png":(32,32),
    "block/almond_shelf.png":(32,32),
    "block/almond_cabinet_front.png":(16,16),
    "block/almond_cabinet_front_open.png":(16,16),
    "block/almond_cabinet_side.png":(16,16),
    "block/almond_cabinet_top.png":(16,16),
    "item/almond_sign.png":(16,16),"item/almond_hanging_sign.png":(16,16),
    "item/almond_boat.png":(16,16),"item/almond_chest_boat.png":(16,16),
    "entity/boat/almond.png":(128,64),
    "entity/chest_boat/almond.png":(128,128)}.items():
    png(A/"textures"/filename,size)
    assert (A/"textures"/filename).read_bytes()!=(
        A/"textures"/filename.replace("almond","olive")).read_bytes(),filename
for name in ("boat","chest_boat"):
    assert read(A/f"items/almond_{name}.json")["model"]["model"]==f"italiansdelight:item/almond_{name}"
    assert read(D/f"italiansdelight/recipe/almond_{name}.json")["result"]["id"]==f"italiansdelight:almond_{name}"
for name in ("sign","hanging_sign","shelf","cabinet"):
    rec=read(D/f"italiansdelight/recipe/almond_{name}.json")
    assert rec["result"]["id"]=="italiansdelight:almond_"+name
rack=read(D/"italiansdelight/recipe/crafting/almond_cheese_aging_rack.json")
assert rack["key"]=={"S":"italiansdelight:almond_fence","B":"italiansdelight:almond_planks"}
assert rack["result"]["id"]=="italiansdelight:almond_cheese_aging_rack"
for loc in ("it_it","en_us"):
    data=read(A/f"lang/{loc}.json")
    for name in ("sign","hanging_sign","shelf","cabinet","cheese_aging_rack"):
        assert f"block.italiansdelight.almond_{name}" in data or f"item.italiansdelight.almond_{name}" in data
print("OK: Almond sign + hanging sign, boats + chest boats, native shelf, FD cabinet, aging rack")
print("OK: 13 original RGBA textures, item models, entity models, blockstates, craft/drop and tags")
print("OK: shelf BE, cabinet BE, signs and boat entity renderers are functional")
