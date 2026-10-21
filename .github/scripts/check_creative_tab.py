#!/usr/bin/env python3
"""Verify that the Italian's Delight Creative tab exposes every public item.

No dependency on Minecraft or Gradle; run on each GitHub Actions build.
Technical block states without standalone item forms must stay hidden.
"""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path("src/main/java/dev/italiansdelight/common")
ITEMS = ROOT / "registry/ModItems.java"
BLOCKS = ROOT / "registry/ModBlocks.java"
TAB = ROOT / "item/group/ModItemGroups.java"

# These represent stateful/technical blocks, not independent Creative items.
INTERNAL_BLOCKS = {
    "POTTED_CARDOON",
    "PIZZA_DOUGH",
    "COOKED_PIZZA",
    "CHEESE_HOOK_OCCUPIED_SPACE",
    "AGING_CHEESE",
    "SALT_CAULDRON",
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
        r"output\.accept\(Mod(Items|Blocks)\.([A-Z][A-Z0-9_]*)\);",
        content,
    )
    entries = [(kind, name) for kind, name in matches]
    if len(entries) != len(set(entries)):
        raise ValueError("Creative tab includes one or more item/block duplicates")

    registered_items = declared(ITEMS, "Item")
    registered_blocks = declared(BLOCKS, "Block")
    expected = {
        ("Items", name) for name in registered_items
    } | {
        ("Blocks", name) for name in registered_blocks - INTERNAL_BLOCKS
    }
    exposed = set(entries)
    if exposed != expected:
        raise ValueError(
            "Creative tab is incomplete or has extra entries. "
            f"Missing: {sorted(expected - exposed)}; "
            f"unexpected: {sorted(exposed - expected)}"
        )

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


if __name__ == "__main__":
    main()
