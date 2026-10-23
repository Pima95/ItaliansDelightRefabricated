#!/usr/bin/env python3
"""Generate/check exactly twelve vanilla-style wooden cheese aging rack recipes.

Run with --check in CI (read-only) or --write when adding/updating wood sets.
No runtime recipe schema or output IDs are changed.
"""
import argparse
import json
from pathlib import Path

WOODS = ("oak", "spruce", "birch", "jungle", "acacia", "dark_oak",
         "mangrove", "cherry", "bamboo", "crimson", "warped", "pale_oak", "olive")
DIRECTORY = Path("src/main/resources/data/italiansdelight/recipe/crafting")

def recipe(wood):
    return {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "group": "italiansdelight_cheese_aging_racks",
        "key": {
            "S": f"{'italiansdelight' if wood == 'olive' else 'minecraft'}:{wood}_fence",
            "B": f"{'italiansdelight' if wood == 'olive' else 'minecraft'}:{wood}_planks",
        },
        "pattern": ["S S", " B ", "S S"],
        "result": {"id": f"italiansdelight:{wood}_cheese_aging_rack", "count": 1},
    }

def main():
    ap = argparse.ArgumentParser()
    mx = ap.add_mutually_exclusive_group(required=True)
    mx.add_argument("--check", action="store_true", help="Verify unchanged existing recipes")
    mx.add_argument("--write", action="store_true", help="Regenerate all known recipes")
    args = ap.parse_args()
    found = {p.stem for p in DIRECTORY.glob("*_cheese_aging_rack.json")}
    expected = {f"{wood}_cheese_aging_rack" for wood in WOODS}
    if args.check and found != expected:
        raise SystemExit(f"Rack recipe variants differ: missing={sorted(expected-found)} extra={sorted(found-expected)}")
    for wood in WOODS:
        target = DIRECTORY / f"{wood}_cheese_aging_rack.json"
        value = recipe(wood)
        if args.write:
            target.write_text(json.dumps(value, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        elif not target.exists() or json.loads(target.read_text(encoding="utf-8")) != value:
            raise SystemExit(f"Rack recipe mismatch: {target}")
    print(f"OK: {len(WOODS)} rack recipes match the original wood-specific recipe schema")
    print("No distributed recipe was modified" if args.check else "Regenerated recipes for review")

if __name__ == "__main__":
    main()
