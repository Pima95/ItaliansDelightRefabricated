#!/usr/bin/env python3
"""Orchard 1.1.0 Phase B: verify olive doors, cheese rack and real boat resources."""
from pathlib import Path
import json
import struct

RES = Path("src/main/resources")
ASSETS = RES / "assets/italiansdelight"
DATA = RES / "data/italiansdelight"
JAVA = Path("src/main/java/dev/italiansdelight")
TEX = ASSETS / "textures"
def read_json(path):
    if not path.is_file():
        raise AssertionError(f"Missing: {path}")
    return json.loads(path.read_text(encoding="utf-8"))
def image_size(path):
    value = path.read_bytes()
    if value[:8] != b"\x89PNG\r\n\x1a\n":
        raise AssertionError(f"Not PNG: {path}")
    return struct.unpack(">II", value[16:24])

def main():
    # Assets are reused intact and no historical Rustic 1.12-era art is
    # referenced by any new construction model.
    expected_sizes = {
        "block/olive_door_bottom.png": (16, 16),
        "block/olive_door_top.png": (16, 16),
        "block/olive_trapdoor.png": (16, 16),
        "block/olive_planks.png": (16, 16),
        "item/olive_door.png": (16, 16),
        "item/olive_boat.png": (16, 16),
        "item/olive_chest_boat.png": (16, 16),
        "entity/boat/olive.png": (128, 64),
        "entity/chest_boat/olive.png": (128, 128),
    }
    for path, dims in expected_sizes.items():
        png = TEX / path
        assert png.is_file(), f"Missing PNG: {path}"
        assert image_size(png) == dims, f"Unexpected dimensions: {path}"

    door = read_json(ASSETS / "blockstates/olive_door.json")
    trapdoor = read_json(ASSETS / "blockstates/olive_trapdoor.json")
    rack = read_json(ASSETS / "blockstates/olive_cheese_aging_rack.json")
    assert len(door["variants"]) == 32
    assert len(trapdoor["variants"]) == 16
    assert len(rack["multipart"]) >= 10
    for half in ("bottom", "top"):
        for side in ("left", "right"):
            for suffix in ("", "_open"):
                model = read_json(ASSETS / f"models/block/olive_door_{half}_{side}{suffix}.json")
                assert model["textures"]["bottom"] == "italiansdelight:block/olive_door_bottom"
                assert model["textures"]["top"] == "italiansdelight:block/olive_door_top"
    for pos in ("bottom", "top", "open"):
        model = read_json(ASSETS / f"models/block/olive_trapdoor_{pos}.json")
        assert model["textures"]["texture"] == "italiansdelight:block/olive_trapdoor"
    assert read_json(ASSETS / "models/block/olive_cheese_aging_rack.json")["textures"]["wood"] == "italiansdelight:block/olive_planks"
    rack_recipe = read_json(DATA / "recipe/crafting/olive_cheese_aging_rack.json")
    assert rack_recipe["key"] == {"S":"italiansdelight:olive_fence","B":"italiansdelight:olive_planks"}
    assert rack_recipe["result"]["id"] == "italiansdelight:olive_cheese_aging_rack"
    for block in ("olive_door","olive_trapdoor","olive_cheese_aging_rack"):
        read_json(DATA / f"loot_table/blocks/{block}.json")
        read_json(ASSETS / f"items/{block}.json")

    door_loot = read_json(DATA / "loot_table/blocks/olive_door.json")
    assert any(c.get("properties",{}).get("half") == "lower"
        for pool in door_loot["pools"] for c in pool.get("conditions",[])), "Door must drop once"
    registry=(JAVA/"common/registry/ModBlocks.java").read_text(encoding="utf-8")
    assert "OLIVE_CHEESE_AGING_RACK" in registry and "registerRack(\"olive\", OLIVE_PLANKS)" in registry
    rack_be=(JAVA/"common/registry/ModBlockEntities.java").read_text(encoding="utf-8")
    assert "ModBlocks.OLIVE_CHEESE_AGING_RACK" in rack_be

    # Boat entities must be real registered vanilla-style vehicles. An item
    # image by itself is not accepted as implementation.
    entities=(JAVA/"common/registry/ModBoats.java").read_text(encoding="utf-8")
    client=(JAVA/"ItaliansDelightClient.java").read_text(encoding="utf-8")
    init=(JAVA/"ItaliansDelight.java").read_text(encoding="utf-8")
    for item,cls,tex_path in (
        ("olive_boat","Boat","entity/boat/olive.png"),
        ("olive_chest_boat","ChestBoat","entity/chest_boat/olive.png"),
    ):
        assert f"new {cls}(" in entities and f"new BoatItem(" in entities
        assert f'"{item}"' in entities
        assert tex_path in client, f"Missing boat renderer asset declaration: {tex_path}"
        read_json(ASSETS/f"items/{item}.json")
        read_json(ASSETS/f"models/item/{item}.json")
        read_json(DATA/f"recipe/{item}.json")
    assert "ModBoats.register()" in init
    assert "EntityRenderers.register(ModBoats.OLIVE_BOAT_ENTITY" in client
    assert "EntityRenderers.register(ModBoats.OLIVE_CHEST_BOAT_ENTITY" in client
    print("OK: Phase B models and 13th rack, functional boat entities, pre-existing non-Rustic PNGs")

if __name__ == "__main__":
    main()
