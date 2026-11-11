"""Static Poplar-26.3 check: Python standard library only."""
from pathlib import Path
import json
import struct
import zlib

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / "src/main/resources"
ASSETS = RES / "assets/italiansdelight"
DATA = RES / "data/italiansdelight"
JAVA = ROOT / "src/main/java/dev/italiansdelight"
KINDS = ("cheese_aging_rack", "crushing_tub", "barrel")


def get(path):
    assert path.exists(), f"Missing file: {path}"
    return json.loads(path.read_text(encoding="utf-8"))


def check_png(path):
    data = path.read_bytes()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"Not a PNG: {path}"
    assert struct.unpack(">IIBB", data[16:26]) == (16, 16, 8, 6), path
    offset, idat = 8, bytearray()
    while offset < len(data):
        size = struct.unpack(">I", data[offset:offset + 4])[0]
        tag = data[offset + 4:offset + 8]
        payload = data[offset + 8:offset + 8 + size]
        checksum = struct.unpack(">I", data[offset + 8 + size:offset + 12 + size])[0]
        assert zlib.crc32(tag + payload) == checksum, f"CRC mismatch: {path}"
        if tag == b"IDAT":
            idat.extend(payload)
        offset += size + 12
    decoded = zlib.decompress(idat)
    assert len(decoded) == 1040, f"Unexpected scanlines: {path}"
    assert all(decoded[row * 65] == 0 for row in range(16)), path
    alphas = {decoded[row * 65 + 4 * column + 4]
              for row in range(16) for column in range(16)}
    assert alphas <= {0, 255}, f"Semitransparent pixels: {path}"


for kind in KINDS:
    name = f"poplar_{kind}"
    block = f"italiansdelight:{name}"
    state = get(ASSETS / "blockstates" / f"{name}.json")
    model = get(ASSETS / "models/block" / f"{name}.json")
    item = get(ASSETS / "items" / f"{name}.json")
    loot = get(DATA / "loot_table/blocks" / f"{name}.json")
    assert block in json.dumps(loot)
    recipe_dir = "recipe/crafting" if kind == "cheese_aging_rack" else "recipe"
    recipe = get(DATA / recipe_dir / f"{name}.json")
    assert recipe["result"]["id"] == block
    assert "minecraft:poplar_planks" in json.dumps(recipe)
    assert name in json.dumps(item)
    assert name in json.dumps(state)

    if kind == "cheese_aging_rack":
        assert "minecraft:poplar_fence" in json.dumps(recipe)
        assert model["textures"]["wood"] == "minecraft:block/poplar_planks"
    else:
        assert model["textures"]["0"] == "minecraft:block/poplar_planks"
        assert name in json.dumps(get(ASSETS / "models/item" / f"{name}.json"))
        texture_name = "barrel_poplar" if kind == "barrel" else "crushing_tub_poplar"
        assert model["textures"]["1"] == f"italiansdelight:block/{texture_name}"
        check_png(ASSETS / "textures/block" / f"{texture_name}.png")

    for locale in ("en_us", "it_it"):
        lang = get(ASSETS / "lang" / f"{locale}.json")
        assert f"block.italiansdelight.{name}" in lang

registry = (JAVA / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
entities = (JAVA / "common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
creative = (JAVA / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
jei = (JAVA / "integration/jei/ItaliansDelightJeiPlugin.java").read_text(encoding="utf-8")
axe = get(RES / "data/minecraft/tags/block/mineable/axe.json")["values"]
for kind in KINDS:
    name = f"POPLAR_{kind.upper()}"
    assert name in registry and name in entities and name in creative, name
    assert f"italiansdelight:poplar_{kind}" in axe
assert "POPLAR_CRUSHING_TUB" in jei
for scope in ("block", "item"):
    tag = get(RES / f"data/c/tags/{scope}/barrels/wooden.json")["values"]
    assert "italiansdelight:poplar_barrel" in tag

print("OK: 3 Poplar variants — PNG, alpha, block/item models, recipes, loot, registries, Creative, JEI and tags.")
