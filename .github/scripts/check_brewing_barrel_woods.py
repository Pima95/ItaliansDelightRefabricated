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

original = None
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
    if wood == "spruce":
        sha = hashlib.sha1(f"blob {len(png)}\0".encode("ascii") + png).hexdigest()
        assert sha == "15bc4dcee27b3cb34a82d9bbf9ac50a43bdbee26", \
               "Original MIT spruce asset changed without documented update"
        original = png
    elif wood != "oak" and original is not None:
        assert png != original, f"{wood}: missing wood-specific recolor"
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
