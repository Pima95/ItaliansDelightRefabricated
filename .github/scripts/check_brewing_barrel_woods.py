#!/usr/bin/env python3
"""Static reproducible checks: Alcocraft-derived geometry and 16 wood fermenters."""
from pathlib import Path
import hashlib
import json
import struct

root = Path("src/main/resources")
assets = root / "assets/italiansdelight"
data = root / "data/italiansdelight"
java = Path("src/main/java/dev/italiansdelight")
woods = (
    "oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
    "mangrove", "cherry", "pale_oak", "poplar", "bamboo",
    "crimson", "warped", "olive", "almond", "fruit_cherry"
)
custom = {"olive", "almond", "fruit_cherry"}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

model = load(assets / "models/block/brewing_barrel.json")
assert model["texture_size"] == [64, 64]
assert model["textures"]["0"] == "italiansdelight:block/brewing_barrel"
assert len(model["elements"]) == 22, "Must preserve imported keg geometry"
assert any(e.get("name") == "top_plug" for e in model["elements"])
assert any(e.get("name") == "front_plug" for e in model["elements"])
assert model["credit"] == "Made with Blockbench"

lang_en = load(assets / "lang/en_us.json")
lang_it = load(assets / "lang/it_it.json")
registration = (java / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
entities = (java / "common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
creative = (java / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
block_code = (java / "common/block/BrewingBarrelBlock.java").read_text(encoding="utf-8")
assert "EnumProperty<Direction> FACING" in block_code
assert 'getHorizontalDirection().getOpposite()' in block_code
assert "builder.add(FACING)" in block_code
assert 'register("brewing_barrel", BrewingBarrelBlockEntity::new' in entities
assert "getTicker(" in block_code
assert "preserveContents(stack" in block_code, "Keep item contents on breaking"

original = (Path(".github/scripts/fixtures/alcocraft_spruce_keg.png").read_bytes())
assert hashlib.sha1(f"blob {len(original)}\0".encode("ascii") + original).hexdigest() == \
       "15bc4dcee27b3cb34a82d9bbf9ac50a43bdbee26", "Original Alcocraft fixture changed"
for wood in woods:
    id_ = "brewing_barrel" if wood == "oak" else wood + "_brewing_barrel"
    name = "italiansdelight:" + id_
    blockstate = load(assets / "blockstates" / (id_ + ".json"))["variants"]
    assert set(blockstate) == {f"facing={d}" for d in ("north", "east", "south", "west")}
    for facing, rotation in (("north", 0), ("east", 90),
                             ("south", 180), ("west", 270)):
        entry = blockstate[f"facing={facing}"]
        assert entry["model"] == f"italiansdelight:block/{id_}"
        assert entry.get("y", 0) == rotation
    if wood != "oak":
        expected_ns = "italiansdelight" if wood in custom else "minecraft"
        sub = load(assets / "models/block" / (id_ + ".json"))
        assert sub["parent"] == "italiansdelight:block/brewing_barrel"
        assert sub["textures"]["0"] == f"italiansdelight:block/{id_}"
        assert sub["textures"]["particle"] == f"{expected_ns}:block/{wood}_planks"
        assert load(assets / "models/item" / (id_ + ".json"))["parent"] == f"italiansdelight:block/{id_}"
        assert load(assets / "items" / (id_ + ".json"))["model"]["model"] == f"italiansdelight:item/{id_}"
        recipe = load(data / "recipe" / (id_ + ".json"))
        assert recipe["result"]["id"] == name and recipe["result"]["count"] == 1
        assert recipe["key"]["P"] == f"{expected_ns}:{wood}_planks"
        assert recipe["key"]["S"] == f"{expected_ns}:{wood}_slab"
        loot = load(data / "loot_table/blocks" / (id_ + ".json"))
        assert loot["pools"][0]["entries"][0]["name"] == name
        assert f'{wood.upper()}_BREWING_BARREL' in registration
        assert f'ModBlocks.{wood.upper()}_BREWING_BARREL' in entities
        assert f'ModBlocks.{wood.upper()}_BREWING_BARREL' in creative
    assert lang_en[f"block.italiansdelight.{id_}"]
    assert lang_it[f"block.italiansdelight.{id_}"]
    png = (assets / "textures/block" / (id_ + ".png")).read_bytes()
    assert png[:8] == bytes.fromhex("89504e470d0a1a0a")
    assert struct.unpack(">II", png[16:24]) == (64, 64)
    assert png[24:26] == bytes((8, 6)), "PNG must be 64x64 RGBA8"
    assert png != original, f"{wood}: texture still flat/unmodified original"
assert "block.italiansdelight.brewing_barrel" in lang_it
assert 'BREWING_BARREL = register(' in registration
for notice in ("CREDIT.md", "THIRD_PARTY_NOTICES.md", "NOTICE", "LICENSE"):
    assert Path(notice).is_file(), f"Missing legal notice {notice}"
credits = Path("CREDIT.md").read_text(encoding="utf-8")
assert all(name in credits for name in ("hadrus91", "Alcocraft", "Pima95"))
third_party = Path("THIRD_PARTY_NOTICES.md").read_text(encoding="utf-8")
assert "Alcocraft: Beer & Stuff" in third_party
assert "MIT" in third_party
assert 'from("CREDIT.md")' in Path("build.gradle").read_text(encoding="utf-8")
print("PASSED: Alcocraft keg geometry and 64x64 wood palettes are packaged")
print("PASSED: 16 brewer IDs, blockstates, inventory models, recipes, loot, languages and attributions")

assert "Morogorian" not in credits
assert "Morogorian" not in third_party

# Depth regression checks, comparing every recolor with original Alcocraft
# pixels instead of enforcing the previously flat single-axis ramp.
import zlib
import math

def decode_rgba(path):
    data = path.read_bytes()
    assert data[:8] == bytes.fromhex("89504e470d0a1a0a")
    pos = 8
    compressed = bytearray()
    width = height = None
    while pos < len(data):
        n = int.from_bytes(data[pos:pos + 4], "big")
        kind = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + n]
        pos += n + 12
        if kind == b"IHDR":
            width = int.from_bytes(chunk[0:4], "big")
            height = int.from_bytes(chunk[4:8], "big")
            assert chunk[8:10] == bytes((8, 6))
        elif kind == b"IDAT":
            compressed.extend(chunk)
        elif kind == b"IEND":
            break
    assert (width, height) == (64, 64)
    raw = zlib.decompress(compressed)
    assert len(raw) == 64 * 257
    result = []
    above = [0] * 256
    pos = 0
    for _ in range(64):
        filtering = raw[pos]
        row = list(raw[pos + 1:pos + 257])
        pos += 257
        for x in range(256):
            left = row[x - 4] if x >= 4 else 0
            up = above[x]
            upper_left = above[x - 4] if x >= 4 else 0
            if filtering == 0:
                prediction = 0
            elif filtering == 1:
                prediction = left
            elif filtering == 2:
                prediction = up
            elif filtering == 3:
                prediction = (left + up) // 2
            elif filtering == 4:
                p = left + up - upper_left
                errors = (abs(p - left), abs(p - up), abs(p - upper_left))
                prediction = (left, up, upper_left)[errors.index(min(errors))]
            else:
                raise AssertionError(f"Unsupported PNG filter {filtering}")
            row[x] = (row[x] + prediction) & 255
        result.extend(tuple(row[i:i + 4]) for i in range(0, 256, 4))
        above = row
    return result


def luminance(rgb):
    return rgb[0] * .2126 + rgb[1] * .7152 + rgb[2] * .0722

metadata = load(Path(".github/scripts/brewing_barrel_wood_palettes.json"))
assert not metadata["unmodified"], "All 16 textures now use distinct grading"
assert len(metadata["spriteGitBlobSHA"]) == 16
base_sprite = decode_rgba(Path(".github/scripts/fixtures/alcocraft_spruce_keg.png"))
assert len(base_sprite) == 4096
for wood in woods:
    id_ = "brewing_barrel" if wood == "oak" else wood + "_brewing_barrel"
    png_path = assets / "textures/block" / (id_ + ".png")
    data = png_path.read_bytes()
    sha = hashlib.sha1(f"blob {len(data)}\0".encode("ascii") + data).hexdigest()
    assert sha == metadata["spriteGitBlobSHA"][wood], \
           f"Unreviewed texture modification for {wood}"
    recolored = decode_rgba(png_path)
    assert len(recolored) == 4096
    wood_luminances = []
    metal_pixels = 0
    for source, result in zip(base_sprite, recolored):
        r, g, b, a = source
        assert a == result[3], f"Pixel transparency changed in {wood}"
        is_wood = a > 0 and r > g + 7 and g > b + 7
        if not is_wood:
            assert source == result, f"Metal/transparent details altered in {wood}"
            if a:
                metal_pixels += 1
        else:
            wood_luminances.append(luminance(result[:3]))
    assert len(wood_luminances) == 2492 and metal_pixels == 196
    wood_luminances.sort()
    lo = wood_luminances[int(len(wood_luminances) * .05)]
    hi = wood_luminances[int(len(wood_luminances) * .95)]
    contrast = hi - lo
    if wood != "mangrove":
        assert contrast >= metadata["expectedWoodContrastP05P95"][wood] - 3, \
               f"{wood} has lost its grain/wood depth: {contrast:.2f}"
    if wood in metadata["moderatedContrastP05P95Upper"]:
        assert contrast <= metadata["moderatedContrastP05P95Upper"][wood], \
               f"{wood}: contrast too harsh ({contrast:.2f})"
    if wood == "mangrove":
        assert sha == "26a66116e14b1d32dc57d44f67544ac0e2d8e794", \
               "Mangrove must be exactly reverted to the earlier approved atlas"
    if wood == "pale_oak":
        assert not any(all(channel >= 248 for channel in px[:3])
                       for src, px in zip(base_sprite, recolored)
                       if src[3] and src[0] > src[1] + 7 and src[1] > src[2] + 7), \
               "Pale oak must not have fully white blown-out wood pixels"
    if wood == "bamboo":
        # Bamboo preserves real vanilla stalk bands/joints, not only yellow RGB.
        assert len({tuple(px[:3]) for orig, px in zip(base_sprite, recolored)
                    if orig[3] and orig[0] > orig[1] + 7
                    and orig[1] > orig[2] + 7}) > 150, \
               "Bamboo texture lost stalk and knot variations"
print("PASSED: depth, knots, RGBA, metal and distinct actual wood palettes")
print("PASSED: all 16 image SHA references match the new reviewed sprites")
