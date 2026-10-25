#!/usr/bin/env python3
"""Check that olive_shelf is a native 26.2 shelf, not a decorative imitation."""
import json
import struct
import zlib
from pathlib import Path

R = Path("src/main/resources")
A = R / "assets/italiansdelight"
D = R / "data/italiansdelight"
J = Path("src/main/java/dev/italiansdelight")

def load(path):
    assert path.is_file(), f"Missing: {path}"
    return json.loads(path.read_text(encoding="utf-8"))

registry = (J / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
tab = (J / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")

# The vanilla block must be the implementation itself: it provides storage,
# persisted items, native renderer, three slots, redstone and side chaining.
assert "new ShelfBlock(" in registry
assert "Block.Properties.ofFullCopy(Blocks.OAK_SHELF)" in registry
be_registry = (J / "common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
assert "BlockEntityTypes.SHELF.addValidBlock(ModBlocks.OLIVE_SHELF)" in be_registry
assert 'ModRegistries.id("olive_shelf")' in registry
assert not (J / "common/block/OliveShelfBlock.java").exists()
assert tab.count("output.accept(ModBlocks.OLIVE_SHELF);") == 1
assert "fire.add(OLIVE_SHELF, 5, 20)" in registry

# 6 variants: 1 body plus unpowered / unconnected / left / center / right.
for suffix, parent in (("", "body"), ("_unpowered", "unpowered"),
                       ("_unconnected", "unconnected"), ("_left", "left"),
                       ("_center", "center"), ("_right", "right"),
                       ("_inventory", "inventory")):
    model = load(A / f"models/block/olive_shelf{suffix}.json")
    assert model["parent"] == f"minecraft:block/template_shelf_{parent}"
    assert model["textures"] == {
        "all": "italiansdelight:block/olive_shelf",
        "particle": "italiansdelight:block/stripped_olive_log"
    }
assert not (A / "models/item/olive_shelf.json").exists(), "Obsolete item model still present"
assert load(A / "items/olive_shelf.json")["model"] == {
    "type": "minecraft:model", "model":"italiansdelight:block/olive_shelf_inventory"
}

# Vanilla shelf uses 4 facing x (unpowered + 4 powered chain positions) = 24.
states = load(A / "blockstates/olive_shelf.json")["multipart"]
assert len(states) == 24
seen = set()
for part in states:
    condition = part["when"]
    if "AND" in condition:
        condition = {key: val for sub in condition["AND"] for key, val in sub.items()}
    facing = condition["facing"]
    powered = condition.get("powered")
    chain = condition.get("side_chain")
    assert facing in ("north","east","south","west")
    assert part["apply"]["y"] == {"north":0,"east":90,"south":180,"west":270}[facing] if "y" in part["apply"] else facing=="north"
    assert part["apply"]["model"].startswith("italiansdelight:block/olive_shelf")
    seen.add((facing,powered,chain))
assert len(seen) == 24
for facing in ("north","east","south","west"):
    assert (facing,None,None) in seen
    assert (facing,"false",None) in seen
    for chain in ("unconnected","left","center","right"):
        assert (facing,"true",chain) in seen

# Vanilla UV atlas is 32x32, not a custom 16x16 inventory icon. Alpha is
# opaque and only the non-Rustic approved olive wood family supplies colors.
tex = A/"textures/block/olive_shelf.png"
raw = tex.read_bytes()
assert raw[:8] == b"\x89PNG\r\n\x1a\n"
assert struct.unpack(">II",raw[16:24]) == (32,32)

# Pixel-level checks for the revised, trim-only olive-leaf garland.
# This PNG uses vanilla's 32x32 UV atlas; use only colors from the previously
# approved olive_planks palette and forbid semitransparent pixels.
def png_chunks(png):
    assert png.startswith(b"\x89PNG\r\n\x1a\n")
    pos = 8
    while pos < len(png):
        size = struct.unpack_from(">I", png, pos)[0]
        kind, data = png[pos+4:pos+8], png[pos+8:pos+8+size]
        yield kind, data
        pos += size + 12
        if kind == b"IEND":
            break

def png_rgba_pixels(chunks):
    ihdr = next(data for kind, data in chunks if kind == b"IHDR")
    assert struct.unpack(">IIBBBBB", ihdr) == (32, 32, 8, 6, 0, 0, 0)
    pixel_data = zlib.decompress(b"".join(data for kind, data in chunks if kind == b"IDAT"))
    stride = 32 * 4
    assert len(pixel_data) == 32 * (stride + 1)
    previous = bytearray(stride)
    result = []
    for y in range(32):
        start = y * (stride + 1)
        filter_kind = pixel_data[start]
        scan = pixel_data[start+1:start+1+stride]
        assert filter_kind in (0, 1, 2, 3, 4)
        row = bytearray(stride)
        for i, encoded in enumerate(scan):
            left = row[i-4] if i >= 4 else 0
            up = previous[i]
            corner = previous[i-4] if i >= 4 else 0
            p = left + up - corner
            distance = (abs(p-left), abs(p-up), abs(p-corner))
            paeth = (left, up, corner)[distance.index(min(distance))]
            predictor = (0, left, up, (left+up)//2, paeth)[filter_kind]
            row[i] = (encoded + predictor) & 255
        result.extend(tuple(row[i:i+4]) for i in range(0, stride, 4))
        previous = row
    return result

planks_png = (A/"textures/block/olive_planks.png").read_bytes()
plte = next(data for kind, data in png_chunks(planks_png) if kind == b"PLTE")
allowed_rgb = {tuple(plte[i:i+3]) for i in range(0, len(plte), 3)}
atlas_pixels = png_rgba_pixels(list(png_chunks(raw)))
assert len(atlas_pixels) == 32 * 32
assert all(pixel[3] == 255 for pixel in atlas_pixels), "No translucent texture pixels"
# Six new shadow pixels are restricted to the front-facing slot overlays;
# the rear, sides, body and existing approved olive-leaf frieze are unchanged.
accent_rgb = {(118,67,39), (130,73,41), (142,80,45),
              (153,87,47), (164,92,48), (176,100,51)}
assert {pixel[:3] for pixel in atlas_pixels} <= (allowed_rgb | accent_rgb)
assert len({pixel[:3] for pixel in atlas_pixels}) >= 5, "Keep pixel-art shading"
# The vanilla UV layout has FIVE FRONT overlays, not two:
# normal triple slots (0..15,4..11), and the four powered chain states
# left/center/right/unconnected in the BOTTOM HALF of the 32px atlas.
# The old code incorrectly shaded the REAR (x16..31, y4..10), which is
# restored byte-for-byte to its approved version.
import hashlib
atlas_rgb = bytes(component for px in atlas_pixels for component in px[:3])
def coords(x0,y0,x1,y1):
    return ((x,y) for y in range(y0,y1) for x in range(x0,x1))
five_front_uvs = {
    "triple": (0,4,16,12),
    "unconnected": (16,24,32,32),
    "left": (0,16,16,24),
    "center": (0,24,16,32),
    "right": (16,16,32,24)
}
mask = set()
for name,(x0,y0,x1,y1) in five_front_uvs.items():
    patch = [atlas_pixels[y*32+x] for x,y in coords(x0,y0,x1,y1)]
    mask.update(coords(x0,y0,x1,y1))
    assert len({p[:3] for p in patch}) >= 5, f"{name} must have vanilla depth shading"
    assert any(p[:3] in accent_rgb for p in patch), f"{name}: no dark recesses"
assert len(mask)==5*16*8

rear = bytes(c for x,y in coords(16,0,32,16)
             for c in atlas_pixels[y*32+x][:3])
assert hashlib.sha256(rear).hexdigest()==(
    "02f549753d34d0dbd5c771cf27d3ee5a2ec822dc6746a47437fbb0d94d332cbd"
), "The back panel must remain exactly the approved original"

outside = bytes(c for y in range(32) for x in range(32)
                if (x,y) not in mask
                for c in atlas_pixels[y*32+x][:3])
assert hashlib.sha256(outside).hexdigest()==(
    "6f3d0bad6eed465da844ecbc7059bcab5bb3c1b89b0c837686d6d74020180cc4"
), "Only the five frontal overlay UV rectangles may change"

# All wooden shelves (even of mixed wood species) may side-chain when
# powered AND facing the same direction, up to 3 shelves total.
for kind in ("block","item"):
    tag = load(R/f"data/minecraft/tags/{kind}/wooden_shelves.json")
    assert tag["replace"] is False
    assert tag["values"] == ["italiansdelight:olive_shelf"]

assert not (A/"textures/item/olive_shelf.png").exists()

recipe = load(D/"recipe/olive_shelf.json")
assert recipe == {
    "type":"minecraft:crafting_shaped", "group":"shelf",
    "pattern":["###","   ","###"],
    "key":{"#":"italiansdelight:stripped_olive_log"},
    "result":{"id":"italiansdelight:olive_shelf","count":6}
}
loot = load(D/"loot_table/blocks/olive_shelf.json")
assert loot["pools"][0]["entries"][0]["name"] == "italiansdelight:olive_shelf"
assert "italiansdelight:olive_shelf" in load(R/"data/minecraft/tags/block/mineable/axe.json")["values"]
assert load(A/"lang/en_us.json")["block.italiansdelight.olive_shelf"] == "Olive Shelf"
assert load(A/"lang/it_it.json")["block.italiansdelight.olive_shelf"] == "Mensola d'Ulivo"

# The existing functional cheese aging rack must remain untouched.
assert 'registerRack("olive", OLIVE_PLANKS)' in registry
assert load(A/"models/block/olive_cheese_aging_rack.json")["textures"]["wood"] == "italiansdelight:block/olive_planks"
assert load(D/"recipe/crafting/olive_cheese_aging_rack.json")["result"]["id"] == "italiansdelight:olive_cheese_aging_rack"

print("OK: vanilla shelf block entity and #minecraft:wooden_shelves connection tag (both registries)")
print("OK: all 5 front overlay UVs shaded; rear, ornament and other pixels preserved")
print("OK: crafting x6, items, drops, translations and preserved cheese rack")
