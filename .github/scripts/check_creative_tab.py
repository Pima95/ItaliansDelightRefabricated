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

    # Only a single tab is intended; splitting it should be an intentional,
    # separately reviewed design decision rather than a silent regression.
    if content.count(".displayItems(") != 1:
        raise ValueError("Expected exactly one Creative tab display callback")
    print(
        f"OK: {len(entries)} unique Creative entries, "
        f"{len(registered_items)} registered items and "
        f"{len(registered_blocks) - len(INTERNAL_BLOCKS)} public blocks"
    )


if __name__ == "__main__":
    main()
