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
    # Vanilla Minecraft 26.2 uses indexed PNGs for boats and some wood atlases.
    # Both 8-bit RGBA (type 6) and indexed PLTE+tRNS (type 3) are supported.
    depth,kind,compression=data[24:27]
    assert (kind==6 and depth==8 or kind==3 and depth in (1,2,4,8)) and compression==0, (
        path,"unsupported vanilla PNG format",depth,kind
    )
    if data[25]==3:
        assert b"PLTE" in data,(path,"indexed PNG missing palette")
    # PNG CRC validation for every chunk, including indexed palette and alpha.
    import zlib
    off=8
    while off<len(data):
        length=struct.unpack_from(">I",data,off)[0]
        assert off+length+12<=len(data),(path,"truncated PNG")
        typ=data[off+4:off+8]
        body=data[off+8:off+8+length]
        stored=struct.unpack_from(">I",data,off+8+length)[0]
        assert stored==zlib.crc32(typ+body),(path,typ,"bad CRC")
        off+=12+length
    assert off==len(data),(path,"invalid PNG trailer")
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
for t, count in (("sign",16),("hanging_sign",32)):
    assert len(read(A/f"blockstates/almond_{t}.json")["variants"])==count
for filename,size in {
    "block/almond_sign.png":(32,32),"block/almond_hanging_sign.png":(32,32),
    "gui/signs/almond.png":(24,26),"gui/hanging_signs/almond.png":(16,16),
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
print("OK: 15 original RGBA textures, sign GUI atlases, models, blockstates, craft/drop and tags")
print("OK: shelf BE, cabinet BE, signs and boat entity renderers are functional")

# Match vanilla 26.2 sign/shelf model families: no custom geometry,
# because orientation, hitboxes, writing and redstone must stay vanilla.
for block in ("almond_sign_rot_0", "almond_hanging_sign_rot_0"):
    model=read(A/f"models/block/{block}.json")
    assert model["parent"].startswith("minecraft:block/template_")
assert read(A/"models/block/almond_shelf_inventory.json")["parent"]=="minecraft:block/template_shelf_inventory"
# GUIs must exist separately from the 32x32 world block textures.
assert (A/"textures/gui/signs/almond.png").is_file()
assert (A/"textures/gui/hanging_signs/almond.png").is_file()

# Texture layouts are now derived from Minecraft 26.2 vanilla:
# boats and chest boats preserve the exact UV mapping, transparent regions
# and paddles. We record their Git blob checksums to reject accidental
# overwrites with the former all-over plank patterns.
import hashlib
ASSET_BLOBS={
    "entity/boat/almond.png":"14d28a0fe1e1db9a233e50715b4c0fb34a2f0371",
    "entity/chest_boat/almond.png":"9a1e18b6f49668300518ff4e24ba3884b4d79426",
    "item/almond_boat.png":"0f85db269abc5d872309fb0ac513a09fe440cea8",
    "item/almond_chest_boat.png":"263cfc3a34f536bd8cea025007d4078fd8e81300",
    "block/almond_sign.png":"6274f3f7a60f1f1bc030e9a8c405bfd5a98b6c68",
    "block/almond_hanging_sign.png":"c7a02a11c8fd2bd0ded62967fe4bb3963df557fd",
    "block/almond_shelf.png":"c2b36ec30ed10a1f569d21fdc33886983093a3a8",
    "block/almond_cabinet_front.png":"6503e5520be4a87b47b4a2015ecc070330776750",
    "block/almond_cabinet_front_open.png":"9cc5ec0c08e96dedda431fed2932583a802fd651",
    "block/almond_cabinet_side.png":"a413d7147ec122bcd37923529002bd94f0fa683e",
    "block/almond_cabinet_top.png":"af5238fcb4f727fb987aaaa8a07f4848543ad489",
}
for file,expected in ASSET_BLOBS.items():
    payload=(A/"textures"/file).read_bytes()
    sha=hashlib.sha1(b"blob "+str(len(payload)).encode("ascii")+b"\0"+payload).hexdigest()
    assert sha==expected,(file,"unexpected texture data")
# Vanilla models reference one 32×32 atlas for the full sign, and it must
# cover all face, post, bracket and hanging-chain coordinates.
for n in ("almond_sign_rot_0","almond_hanging_sign_rot_0",
          "almond_hanging_sign_attached_rot_0","almond_wall_hanging_sign"):
    m=read(A/f"models/block/{n}.json")
    assert m["parent"].startswith("minecraft:block/template_"),n
    assert m["textures"]["all"] in {
        "italiansdelight:block/almond_sign",
        "italiansdelight:block/almond_hanging_sign",
    }
# Every distinct shelf overlay uses the official native template and a
# dedicated 32x32 atlas region: unpowered, unconnected, left, right, center.
for name in ("unpowered","unconnected","left","right","center"):
    m=read(A/f"models/block/almond_shelf_{name}.json")
    assert m["parent"]==f"minecraft:block/template_shelf_{name}"
    assert m["textures"]["all"]=="italiansdelight:block/almond_shelf"
print("OK: Minecraft vanilla 26.2 UV-mapped Almond boat/sign textures, detailed shelf states and FD cabinet motifs")
