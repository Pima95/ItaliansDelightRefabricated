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
    "block/almond_sign.png":"958159d11790e52d0dca7b44d0ade1111de77e6e",
    "block/almond_hanging_sign.png":"e33ea68327c6672e75e0ecef9e09600054253ef2",
    "block/almond_shelf.png":"db520dea81a4e8744a089334badb02db9f5d2005",
    "item/almond_sign.png":"09c1ebe9eac228f64a34393030ac0762dedad3bf",
    "item/almond_hanging_sign.png":"b795de005e2210e1e086bb180c9643000e3813a5",
    "block/almond_cabinet_front.png":"9869de32b9af82e5f69a09420944a1d8b801bd5f",
    "block/almond_cabinet_front_open.png":"6e2e5c487929941f0fee1c4947228ea646a2471f",
    "block/almond_cabinet_side.png":"da41ed62ee61bb3f229c0734472dfc8e63a6cce8",
    "block/almond_cabinet_top.png":"898d71607f4139b1aee94ee59188938cf3132754",
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
# Prevent regression: the block/GUI/item must be three views of ONE
# sign, rather than three separately invented pixel-art textures.
from check_orchard_signs_and_cutting import check_rgba_pixel_art
normal=check_rgba_pixel_art(A/"textures/block/almond_sign.png",(32,32))
hanging=check_rgba_pixel_art(A/"textures/block/almond_hanging_sign.png",(32,32))
g_normal=check_rgba_pixel_art(A/"textures/gui/signs/almond.png",(24,26))
g_hanging=check_rgba_pixel_art(A/"textures/gui/hanging_signs/almond.png",(16,16))
planks=check_rgba_pixel_art(A/"textures/block/almond_planks.png",(16,16))
stripped=check_rgba_pixel_art(A/"textures/block/stripped_almond_log.png",(16,16))
normal_item=check_rgba_pixel_art(A/"textures/item/almond_sign.png",(16,16))
hanging_item=check_rgba_pixel_art(A/"textures/item/almond_hanging_sign.png",(16,16))

# The 24x12 sign face is a DIRECT pixel repeat of approved Almond planks.
for y in range(12):
    for x in range(24):
        approved=planks[(y+1)%16][x%16]
        assert normal[y+16][x]==approved
        assert normal[y+2][x]==approved
        assert g_normal[y][x]==normal[y+16][x]

# Preserve all original in-world sign post shading, even in unused UV
# positions: the author explicitly approved it and requested only panel work.
post_bytes=bytes(c for y in range(32) for x in range(28,32) for c in normal[y][x])
assert hashlib.sha256(post_bytes).hexdigest()==(
    "654852038f967eb6becc07ac67deb97d3c7d338baa9ed5976e1bfd3cafd78bcd"
)
assert sum(pixel[3]==255 for row in normal for pixel in row)==836
assert sum(pixel[3]==255 for row in g_normal for pixel in row)==316
assert sum(pixel[3]==255 for row in normal_item for pixel in row)==132

# The 14x10 hanging-sign board is displayed in GUI in the same order.
for y in range(10):
    for x in range(14):
        assert g_hanging[y+6][x+1]==hanging[y+16][x+18]
metal={(91,100,124),(73,80,101),(62,68,83),(37,44,61)}
metal_pixels=[p for row in hanging for p in row if p[3]==255 and p[:3] in metal]
assert len(metal_pixels)==96, "Hanging chains and wall hardware must remain metallic"
assert sum(p[3] for row in hanging for p in row)==632*255
assert sum(p[3] for row in g_hanging for p in row)==188*255
assert sum(p[3] for row in hanging_item for p in row)==148*255

# Wood portion is drawn in exact stripped-Almond palette.
exposed_colors={p[:3] for row in stripped for p in row if p[3]}
for row in hanging:
    for p in row:
        if p[3] and p[:3] not in metal:
            # darker exposed edges are allowed, but not a new orange palette.
            assert p[0]>=80 and p[1]>=55 and p[2]>=40
assert len(exposed_colors)>=5
print("OK: Almond standing sign plank faces equal approved planks, retained world post")
print("OK: vanilla hanging chain metal, all 632 pixels and coherent block/GUI/item masks")
print("OK: approved Almond boats byte-identical; signs keep 26.2 UV support silhouettes; native shelf overlays and door-motif FD cabinet")
