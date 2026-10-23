#!/usr/bin/env python3
"""Audit: Orchard Update's initial wood models never reference Rustic olive art."""
from pathlib import Path
import json

ROOT = Path("src/main/resources")
MODELS = ROOT / "assets/italiansdelight/models/block"
BLOCKS = ROOT / "assets/italiansdelight/blockstates"
DEFINITIONS = ROOT / "assets/italiansdelight/items"
REGISTRY = Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text(encoding="utf-8")
NAMES = ("olive_log", "olive_wood", "stripped_olive_log", "stripped_olive_wood", "olive_planks", "olive_stairs", "olive_slab", "olive_fence", "olive_fence_gate", "olive_button", "olive_pressure_plate")
ALLOWED = {"olive_log", "olive_log_top", "stripped_olive_log", "stripped_olive_log_top", "olive_planks"}
LEGACY_RUSTIC = {"log_olive", "leaves_olive", "sapling_olive", "olive_seed"}

def main():
    for name in NAMES:
        if 'ModRegistries.id("' + name + '")' not in REGISTRY:
            raise ValueError("Missing olive registration " + name)
        for base in (MODELS, BLOCKS, DEFINITIONS):
            # Vanilla fences use a multipart blockstate with post/side models;
            # there intentionally is no single 'olive_fence' block model.
            if base == MODELS and name == "olive_fence":
                continue
            path = base / (name + ".json")
            if not path.exists():
                raise ValueError("Missing wood model/definition " + str(path))
            data = json.loads(path.read_text(encoding="utf-8"))
            if base == MODELS:
                for texture in data.get("textures", {}).values():
                    namespace, part = texture.split(":", 1)
                    basename = part.rsplit("/", 1)[-1]
                    if namespace != "italiansdelight" or basename not in ALLOWED or basename in LEGACY_RUSTIC:
                        raise ValueError("Unapproved/legacy olive art reference: " + texture)
                    if not (ROOT / "assets/italiansdelight/textures/block" / (basename + ".png")).is_file():
                        raise ValueError("Texture missing: " + basename)
        for base in ("loot_table/blocks",):
            f = ROOT / "data/italiansdelight" / base / (name + ".json")
            if not f.is_file():
                raise ValueError("Missing olive loot: " + str(f))
            json.loads(f.read_text(encoding="utf-8"))
    for name in ("olive_planks", "olive_wood", "stripped_olive_wood", "olive_stairs", "olive_slab", "olive_fence", "olive_fence_gate", "olive_button", "olive_pressure_plate"):
        json.loads((ROOT / "data/italiansdelight/recipe" / (name + ".json")).read_text(encoding="utf-8"))
    slab_loot = json.loads((ROOT / "data/italiansdelight/loot_table/blocks/olive_slab.json").read_text(encoding="utf-8"))
    drop = slab_loot["pools"][0]["entries"][0]
    double = [f for f in drop.get("functions", []) if f.get("function") == "minecraft:set_count"]
    if len(double) != 1 or double[0].get("count") != 2 or not any(
            c.get("condition") == "minecraft:block_state_property"
            and c.get("block") == "italiansdelight:olive_slab"
            and c.get("properties", {}).get("type") == "double"
            for c in double[0].get("conditions", [])):
        raise ValueError("Double olive slab must drop two slabs, matching vanilla")
    for path in (
        ROOT / "data/italiansdelight/tags/item/olive_logs.json",
        ROOT / "data/italiansdelight/tags/block/olive_logs.json",
        ROOT / "data/minecraft/tags/item/logs_that_burn.json",
        ROOT / "data/minecraft/tags/block/logs_that_burn.json",
        ROOT / "data/minecraft/tags/item/planks.json",
    ):
        json.loads(path.read_text(encoding="utf-8"))
    # Additional model variants are also pinned to the one approved plank
    # texture and standard Minecraft shapes.
    for pattern in ("olive_stairs_*.json", "olive_slab_top.json", "olive_fence_*.json",
                    "olive_button_*.json", "olive_pressure_plate_down.json"):
        for path in MODELS.glob(pattern):
            model = json.loads(path.read_text(encoding="utf-8"))
            for texture in model.get("textures", {}).values():
                if texture != "italiansdelight:block/olive_planks":
                    raise ValueError("Non-approved texture for " + str(path) + ": " + texture)
    expected_variants = {"olive_stairs": 40, "olive_slab": 3,
                         "olive_fence_gate": 16, "olive_button": 24,
                         "olive_pressure_plate": 2}
    for name, count in expected_variants.items():
        actual = json.loads((BLOCKS / (name + ".json")).read_text(encoding="utf-8"))
        if len(actual.get("variants", {})) != count:
            raise ValueError(f"{name}: expected {count} vanilla variants")
    # Match Minecraft's vanilla model rotations (not just JSON existence).
    gate = json.loads((BLOCKS / "olive_fence_gate.json").read_text(encoding="utf-8"))["variants"]
    for direction, expected_y in (("north", 180), ("east", 270), ("south", 0), ("west", 90)):
        v = gate[f"facing={direction},in_wall=false,open=false"]
        if v.get("y", 0) != expected_y or not v.get("uvlock"):
            raise ValueError(f"Fence gate rotation mismatch: {direction}")
    button = json.loads((BLOCKS / "olive_button.json").read_text(encoding="utf-8"))["variants"]
    for direction in ("north", "east", "south", "west"):
        if not button[f"face=wall,facing={direction},powered=false"].get("uvlock"):
            raise ValueError(f"Wall button must lock UVs: {direction}")
    print("OK: eleven olive wood building blocks; models use only non-Rustic olive textures")

if __name__ == "__main__":
    main()
