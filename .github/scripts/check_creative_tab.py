#!/usr/bin/env python3
"""Verify that the Italian's Delight Creative tab exposes every public item.

No dependency on Minecraft or Gradle; run on each GitHub Actions build.
Technical block states without standalone item forms must stay hidden.
"""
from __future__ import annotations

import re
import json
from pathlib import Path

ROOT = Path("src/main/java/dev/italiansdelight/common")
ITEMS = ROOT / "registry/ModItems.java"
BLOCKS = ROOT / "registry/ModBlocks.java"
SIGNS = ROOT / "registry/ModSigns.java"
TAB = ROOT / "item/group/ModItemGroups.java"

# These three item IDs are represented by the data-bearing samples below.
# Bare default stacks have empty toppings, producing duplicate Pizza Bianca
# entries (and wrong generic art). They MUST remain registered for gameplay.
PIZZA_DATA_ITEMS = {"RAW_PIZZA", "COOKED_PIZZA", "PIZZA_SLICE"}

# These represent stateful/technical blocks, not independent Creative items.
INTERNAL_BLOCKS = {
    "POTTED_CARDOON",
    "POTTED_OLIVE_SAPLING",
    "POTTED_ALMOND_SAPLING",
    "PIZZA_DOUGH",
    "COOKED_PIZZA",
    "CHEESE_HOOK_OCCUPIED_SPACE",
    "AGING_CHEESE",
    "SALT_CAULDRON",
    # These 4 sign blocks are placed by two SignItem instances, not BlockItems.
    "OLIVE_SIGN", "OLIVE_WALL_SIGN", "OLIVE_HANGING_SIGN", "OLIVE_WALL_HANGING_SIGN",
}


def declared(path: Path, java_type: str) -> set[str]:
    names = re.findall(
        rf"public\s+static\s+final\s+{java_type}\s+([A-Z][A-Z0-9_]*)\s*=",
        path.read_text(encoding="utf-8"),
    )
    if len(names) != len(set(names)):
        raise ValueError(f"Duplicate {java_type} field declaration in {path}")
    return set(names)


def main() -> None:
    content = TAB.read_text(encoding="utf-8")
    matches = re.findall(
        r"output\.accept\(Mod(Items|Blocks|Signs)\.([A-Z][A-Z0-9_]*)\);",
        content,
    )
    entries = [(kind, name) for kind, name in matches]
    if len(entries) != len(set(entries)):
        raise ValueError("Creative tab includes one or more item/block duplicates")

    registered_items = declared(ITEMS, "Item")
    registered_blocks = declared(BLOCKS, "Block")
    registered_sign_items = declared(SIGNS, "Item")
    if not PIZZA_DATA_ITEMS.issubset(registered_items):
        raise ValueError("Pizza item IDs must remain registered even when bare stacks are hidden")
    expected = {
        ("Items", name) for name in registered_items - PIZZA_DATA_ITEMS
    } | {
        ("Blocks", name) for name in registered_blocks - INTERNAL_BLOCKS
    } | {
        ("Signs", name) for name in registered_sign_items
    }
    exposed = set(entries)
    if exposed != expected:
        raise ValueError(
            "Creative tab is incomplete or has extra entries. "
            f"Missing: {sorted(expected - exposed)}; "
            f"unexpected: {sorted(exposed - expected)}"
        )

    for name in PIZZA_DATA_ITEMS:
        if f"output.accept(ModItems.{name});" in content:
            raise ValueError(f"Bare pizza item {name} duplicates the genuine Bianca sample")
    # All three IDs are exposed via registered, data-bearing preset stacks,
    # not via bare Item defaults with an empty pizza composition.
    if content.count("PizzaCreativeSamples.create(preset.getKey(),") != 2:
        raise ValueError("Raw and cooked pizza item IDs must be exposed via preset stacks")
    if content.count("PizzaCreativeSamples.createSlice(preset.getKey())") != 1:
        raise ValueError("Slice item ID must be exposed via preset stacks")

    # Tab icon MUST reuse the existing approved cooked Margherita sprite.
    if ".icon(() -> new ItemStack(ModItems.COOKED_PIZZA))" not in content:
        raise ValueError("Creative tab icon must use cooked Margherita pizza")
    model_path = Path("src/main/resources/assets/italiansdelight/models/item/cooked_pizza.json")
    item_def_path = Path("src/main/resources/assets/italiansdelight/items/cooked_pizza.json")
    model = json.loads(model_path.read_text(encoding="utf-8"))
    definition = json.loads(item_def_path.read_text(encoding="utf-8"))
    if model.get("textures", {}).get("layer0") != "italiansdelight:item/pizza_margherita":
        raise ValueError("Creative icon no longer resolves to the approved Margherita texture")
    if definition.get("model", {}).get("model") != "italiansdelight:item/cooked_pizza":
        raise ValueError("Cooked pizza model binding changed unexpectedly")

    # Enforce stable browsing categories and three contiguous pizza families.
    # They intentionally reuse the same THREE registered Item IDs.
    markers = [
        "// WORKSTATIONS:", "// TOOLS:", "// CROPS AND PREPARED VEGETABLES.",
        "// MILK, CULTURES AND CHEESE-PRODUCTION INGREDIENTS.",
        "// CHEESES:", "// CURED MEATS:", "// RAW PIZZAS:",
        "// COOKED PIZZAS:", "// EDIBLE SLICES:", "// PREPARED DISHES",
    ]
    offsets = [content.find(marker) for marker in markers]
    if min(offsets) < 0 or offsets != sorted(offsets) or len(set(offsets)) != len(offsets):
        raise ValueError("Creative tab categories must stay contiguous and ordered")

    for expected in (
        "output.accept(PizzaCreativeSamples.createMixed(false));",
        "output.accept(PizzaCreativeSamples.createMixed(true));",
        "output.accept(PizzaCreativeSamples.create(preset.getKey(), false));",
        "output.accept(PizzaCreativeSamples.create(preset.getKey(), true));",
        "output.accept(PizzaCreativeSamples.createSlice(preset.getKey()));",
    ):
        if content.count(expected) != 1:
            raise ValueError(f"Missing/duplicate Creative pizza family: {expected}")
    if content.count("PizzaDisplayNames.allPresets().entrySet()") != 3:
        raise ValueError("Creative must enumerate the same 18 presets for all 3 pizza stages")
    # Only a single tab is intended; splitting it should be an intentional,
    # separately reviewed design decision rather than a silent regression.
    if content.count(".displayItems(") != 1:
        raise ValueError("Expected exactly one Creative tab display callback")
    print(
        f"OK: {len(entries)} unique Creative entries, "
        f"{len(registered_items)} registered items and "
        f"{len(registered_blocks) - len(INTERNAL_BLOCKS)} public blocks"
    )
    print("OK: production categories and raw/cooked/slice pizza groups ordered")
    print("OK: raw + cooked Pizza mista examples use original data-bearing stacks")
    print("OK: no duplicate bare Pizza Bianca; 3 pizza item IDs remain registered")


if __name__ == "__main__":
    main()
