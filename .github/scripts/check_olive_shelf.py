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
assert {pixel[:3] for pixel in atlas_pixels} <= allowed_rgb, "Only approved olive colors"
assert len({pixel[:3] for pixel in atlas_pixels}) >= 5, "Keep pixel-art shading"
# Preserve vanilla's three central recesses and the unornamented broad
# surfaces: the old attempt carved the middle of the shelf and was rejected.
# Hash the rows OUTSIDE the upper/lower trim UV strips, compared to the
# original, unmodified olive shelf atlas.
import hashlib
moulding_rows = {1, 2, 3, 12, 13, 14}
untouched = bytes(component
                  for y in range(32) if y not in moulding_rows
                  for x in range(32)
                  for component in atlas_pixels[y*32+x])
assert hashlib.sha256(untouched).hexdigest() == (
    "6319ff3a02dfb6ada1cecd85efaedb3047f37ce0a415ac87cab88872c09b2f2a"
), "The central panels or non-decorative UV regions must remain unmodified"

# Olive-leaf garland is engraved in BOTH 16px UV halves (front and back),
# mirrored in the upper and lower 3-pixel-high moulding. It repeats every
# eight pixels but must not extend onto the center panels.
for y in moulding_rows:
    for x in (1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14):
        assert atlas_pixels[y*32+x] == atlas_pixels[y*32+x+16], (
            "The olive-branch moulding must match on the front and back"
        )
for x in (1, 9, 17, 25):
    assert atlas_pixels[1*32+x][:3] == (224, 160, 100)
for x in (3, 11, 19, 27):
    assert atlas_pixels[2*32+x][:3] == (158, 87, 44)
assert len({atlas_pixels[y*32+x] for y in (1, 2, 3, 12, 13, 14)
            for x in range(1, 16)}) >= 5

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

print("OK: native shelf block entity, redstone chaining, 3-slot storage, vanilla 26.2 models")
print("OK: 32x32 olive-leaf frieze on upper/lower trims, front and back; central panels untouched")
print("OK: crafting x6, items, drops, translations and preserved cheese rack")
