#!/usr/bin/env python3
"""Fruit Cherry boats: real vanilla-style vehicles, original cherry palette, frozen approved artwork."""
import hashlib
import json
import struct
import zlib
from pathlib import Path

ROOT = Path("src/main/resources")
ASSETS = ROOT / "assets/italiansdelight"
DATA = ROOT / "data"
JAVA = Path("src/main/java/dev/italiansdelight")
WOOD_COLORS = {
    (215, 156, 116), (204, 144, 107), (194, 132, 99),
    (184, 122, 91), (174, 112, 83), (162, 98, 71),
    (143, 83, 61), (225, 162, 122), (178, 113, 83),
    (124, 70, 52), (104, 59, 45),
}

def read(path):
    assert path.is_file(), f"Missing {path}"
    return json.loads(path.read_text(encoding="utf-8"))

def png_parts(path):
    raw = path.read_bytes()
    assert raw.startswith(b"\x89PNG\r\n\x1a\n"), path
    pos = 8
    size = None
    palette = set()
    image_data = bytearray()
    saw_end = False
    while pos < len(raw):
        size_bytes, kind = raw[pos:pos + 4], raw[pos + 4:pos + 8]
        length = struct.unpack(">I", size_bytes)[0]
        data = raw[pos + 8:pos + 8 + length]
        stored_crc = struct.unpack(">I", raw[pos + 8 + length:pos + 12 + length])[0]
        assert zlib.crc32(kind + data) & 0xffffffff == stored_crc, (path, kind)
        if kind == b"IHDR":
            width, height, depth, colortype, _, _, interlace = struct.unpack(">IIBBBBB", data)
            assert depth == 8 and colortype == 3 and interlace == 0, (path, "must be indexed pixel art")
            size = (width, height)
        elif kind == b"PLTE":
            assert len(data) % 3 == 0, path
            palette = {tuple(data[i:i+3]) for i in range(0, len(data), 3)}
        elif kind == b"IDAT":
            image_data.extend(data)
        elif kind == b"IEND":
            saw_end = True
            break
        pos += 12 + length
    assert saw_end and image_data and palette, path
    return size, palette, bytes(image_data)

for name, dimensions in (
    ("boat", (128, 64)),
    ("chest_boat", (128, 128)),
):
    prefix = "entity/boat" if name == "boat" else "entity/chest_boat"
    path = ASSETS / f"textures/{prefix}/fruit_cherry.png"
    old = ASSETS / f"textures/{prefix}/almond.png"
    size, palette, data = png_parts(path)
    old_size, old_palette, old_data = png_parts(old)
    assert size == old_size == dimensions, path
    assert data == old_data, (path, "vanilla boat layout, hull and chest UVs must remain intact")
    assert len(palette & WOOD_COLORS) >= 6, (path, "authentic cherry-wood colors missing")

for name in ("boat", "chest_boat"):
    path = ASSETS / f"textures/item/fruit_cherry_{name}.png"
    old = ASSETS / f"textures/item/almond_{name}.png"
    size, palette, data = png_parts(path)
    old_size, old_palette, old_data = png_parts(old)
    assert size == old_size == (16, 16)
    assert data == old_data, (path, "preserve vanilla boat item silhouette and shading")
    assert len(palette & WOOD_COLORS) >= 5, (path, "item must match real cherry planks")

boats = (JAVA / "common/registry/ModBoats.java").read_text(encoding="utf-8")
client = (JAVA / "ItaliansDelightClient.java").read_text(encoding="utf-8")
creative = (JAVA / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
init = (JAVA / "ItaliansDelight.java").read_text(encoding="utf-8")
assert "ModBoats.register()" in init
for name, kind in (("fruit_cherry_boat", "Boat"), ("fruit_cherry_chest_boat", "ChestBoat")):
    const = name.upper()
    method = "FruitCherryBoat" if name == "fruit_cherry_boat" else "FruitCherryChestBoat"
    assert f'"{name}"' in boats and f"{const}_ENTITY" in boats, name
    assert f"new {kind}(type, level, ModBoats::get{method}Item)" in boats, name
    assert f"new BoatItem({const}_ENTITY" in boats, name
    assert creative.count(f"output.accept(ModBoats.{const}_ITEM);") == 1
    assert f"EntityRenderers.register(ModBoats.{const}_ENTITY," in client
    assert f'new ModelLayerLocation(ModRegistries.id("{"boat" if kind == "Boat" else "chest_boat"}/fruit_cherry"), "main")' in client
    assert read(ASSETS / f"items/{name}.json")["model"]["model"] == f"italiansdelight:item/{name}"
    assert read(ASSETS / f"models/item/{name}.json")["textures"]["layer0"] == f"italiansdelight:item/{name}"
    recipe = read(DATA / f"italiansdelight/recipe/{name}.json")
    assert recipe["result"] == {"id": f"italiansdelight:{name}", "count": 1}
    if kind == "Boat":
        assert recipe["type"] == "minecraft:crafting_shaped"
        assert recipe["key"] == {"P": "italiansdelight:fruit_cherry_planks"}
        assert recipe["pattern"] == ["P P", "PPP"]
    else:
        assert recipe["type"] == "minecraft:crafting_shapeless"
        assert set(recipe["ingredients"]) == {"italiansdelight:fruit_cherry_boat", "minecraft:chest"}
    for language in ("en_us", "it_it"):
        words = read(ASSETS / f"lang/{language}.json")
        assert words[f"item.italiansdelight.{name}"]
        assert words[f"entity.italiansdelight.{name}"]

for tag in ("entity_type/boat", "item/boats"):
    entries = read(DATA / f"minecraft/tags/{tag}.json")["values"]
    assert "italiansdelight:fruit_cherry_boat" in entries
    assert "italiansdelight:fruit_cherry_chest_boat" in entries
assert "italiansdelight:fruit_cherry_chest_boat" in read(
    DATA / "minecraft/tags/item/chest_boats.json")["values"]

# These eight sign texture git blobs were approved previously; boats must not change them.
signs = {
    "block/fruit_cherry_sign.png": "bf1026643591f8637aa9ca0f2cd44337e8c92114",
    "item/fruit_cherry_sign.png": "ad0ecf5c5f05345780a2244a172d5505fc1cfaf0",
    "gui/signs/fruit_cherry.png": "d4f8deea07eb990e87ef83d85fcf54c4bbc318b3",
    "entity/sign/fruit_cherry.png": "868d10c359f7b780b23dd2be997793a1b5a5ce31",
    "block/fruit_cherry_hanging_sign.png": "91a63387c6db8ef89bdfd34f18a87501ec099717",
    "item/fruit_cherry_hanging_sign.png": "fcd834967fc5a2325ff8f452d5f024f53fe92ae3",
    "gui/hanging_signs/fruit_cherry.png": "51c01ef1270629e315f41f429a636ea7fa76ed19",
    "entity/sign/hanging/fruit_cherry.png": "8314478a68d52d0b16a501494b45c9fb9aaa9e9f",
}
for name, sha in signs.items():
    raw = (ASSETS / "textures" / name).read_bytes()
    actual = hashlib.sha1(b"blob " + str(len(raw)).encode() + b"\0" + raw).hexdigest()
    assert actual == sha, ("Approved cherry sign texture changed", name)

print("OK: 2 working Fruit Cherry boats, correct registrations, item models, crafting, translations, tags")
print("OK: 4 boat textures, palette/PNG CRCs, vanilla silhouettes and UVs")
print("OK: all eight previously approved cherry sign PNGs unchanged")
