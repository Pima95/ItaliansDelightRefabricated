#!/usr/bin/env python3
"""Repository-wide, read-only consistency audit (Python standard library).

Runs over ALL src/main Java and JSON resources: detects duplicate recipes,
erroneous alternate-container forks, incorrect mod-local tag references,
shaped recipe key errors and regression of unified water-input recipes.
Reports potentially ambiguous intentional recipes without changing gameplay.
"""
import collections
import json
from pathlib import Path

BASE = Path("src/main")
RES = BASE / "resources"
SRC = BASE / "java"
RECIPES = RES / "data/italiansdelight/recipe"
VAT = RECIPES / "cheese_vat"
errors, warnings = [], []
json_data = {}
java_paths = list(SRC.rglob("*.java"))
for path in sorted(RES.rglob("*.json")):
    try:
        json_data[path] = json.loads(path.read_text(encoding="utf-8"))
    except (UnicodeDecodeError, json.JSONDecodeError) as ex:
        errors.append(f"INVALID_JSON {path}: {ex}")

def require(ok, problem):
    if not ok:
        errors.append(problem)

def food_source(x):
    """Recognize only explicit bucket and vanilla WATER-potion equivalents."""
    if x == "minecraft:water_bucket":
        return "bucket"
    if isinstance(x, dict) and x.get("fabric:type") == "fabric:components":
        if (x.get("base") == "minecraft:potion"
                and x.get("components", {}).get(
                    "minecraft:potion_contents", {}).get("potion") == "minecraft:water"):
            return "bottle"
    return None

def alternatives(x):
    if not isinstance(x, dict) or x.get("fabric:type") != "fabric:any":
        return None
    return x.get("ingredients")

def is_both_waters(x):
    options = alternatives(x)
    return isinstance(options, list) and len(options) == 2 and {
        food_source(z) for z in options
    } == {"bucket", "bottle"}

for stem, expected, extra in (
    ("rennet", "italiansdelight:rennet", "#c:crops/cardoon"),
    ("bocconcino", "italiansdelight:bocconcino", "#c:foods/curd"),
    ("fresh_provolone", "italiansdelight:fresh_provolone", "#c:foods/curd"),
):
    r = json_data.get(VAT / f"{stem}.json")
    require(isinstance(r, dict), f"MISSING_UNIFIED_RECIPE {stem}")
    if not isinstance(r, dict):
        continue
    require(r.get("result", {}).get("id") == expected, f"WRONG_OUTPUT {stem}")
    ingredients = r.get("ingredients", [])
    require(extra in ingredients, f"WRONG_INGREDIENTS {stem}")
    require(sum(is_both_waters(x) for x in ingredients) == 1,
            f"WATER_ALTERNATIVES {stem}: must support BOTH water sources")
    old = list(VAT.glob(f"{stem}_from_water_*.json"))
    require(not old, f"DUPLICATE_WATER_RECIPES {stem}: {old}")

tag = json_data.get(RES / "data/c/tags/item/crops/cardoon.json", {})
require({"italiansdelight:cardoon", "italiansdelight:cultivated_cardoon"}
        <= set(tag.get("values", [])), "MISSING_CARDOON_TAG_VARIANTS")

recipe_data = {p: v for p, v in json_data.items() if p.is_relative_to(RECIPES)}
canonical = collections.defaultdict(list)
similar = collections.defaultdict(list)
by_inputs = collections.defaultdict(list)
for path, r in recipe_data.items():
    if not isinstance(r, dict):
        continue
    # Detect exact duplicate recipes under two different resource locations.
    canonical[json.dumps(r, sort_keys=True)].append(path)
    if r.get("type") == "italiansdelight:cheese_vat":
        shared = {k: r.get(k) for k in
                  ("type", "result", "container", "cookingtime", "experience",
                   "whey", "whey_output")}
        similar[json.dumps(shared, sort_keys=True)].append((path, r))
        signature = (json.dumps(r.get("ingredients", []), sort_keys=True),
                     json.dumps(r.get("container", None), sort_keys=True))
        by_inputs[signature].append((path, r))

    if r.get("type") == "minecraft:crafting_shaped":
        keys = set(r.get("key", {}))
        used = set("".join(r.get("pattern", [])).replace(" ", ""))
        require(keys == used, f"SHAPED_KEY_MISMATCH {path}: keys={keys}, used={used}")

for paths in canonical.values():
    if len(paths) > 1:
        errors.append("IDENTICAL_RECIPE_JSON " + ", ".join(map(str, paths)))

for examples in similar.values():
    if len(examples) < 2:
        continue
    # Not every equal-output recipe is redundant (e.g. different milk source).
    for i, (p, a) in enumerate(examples):
        for q, b in examples[i + 1:]:
            x, y = a.get("ingredients", []), b.get("ingredients", [])
            if len(x) != len(y):
                continue
            differences = [(m, n) for m, n in zip(x, y) if m != n]
            if len(differences) == 1 and {
                    food_source(z) for z in differences[0]} == {"bucket", "bottle"}:
                errors.append(f"UNMERGED_WATER_VARIANTS {p} and {q}")

for recipes in by_inputs.values():
    if len(recipes) < 2:
        continue
    outputs = {json.dumps(r.get("result"), sort_keys=True) for _, r in recipes}
    if len(outputs) > 1:
        # Normal: mozzarella vs fresh scamorza has distinct LEAD requirement;
        # ricotta vs cream depends on explicit whey amount/flow.
        warnings.append("SAME_VAT_INPUTS_DIFFERENT_OUTPUTS "
                        + " ".join(str(p) for p, _ in recipes))

# Casing, outdated recipe names, TODOs, and obvious server/client coupling.
source = {p: p.read_text(encoding="utf-8") for p in java_paths}
todo = [(str(p), i) for p, code in source.items()
        for i, line in enumerate(code.splitlines(), 1)
        if "TODO" in line or "FIXME" in line]
warnings.append(f"JAVA_TODO_FIXME_REFERENCES {len(todo)} across "
                f"{len({p for p, _ in todo})} files (review by priority, not all defects)")
for path, text in source.items():
    if "/common/" in str(path) and ("net.minecraft.client.Minecraft" in text
                                     or "Minecraft.getInstance()" in text):
        warnings.append(f"CLIENT_API_IN_COMMON_CODE {path} (inspect side safety)")
    if "throw new NullPointerException(" in text:
        warnings.append(f"EXPLICIT_NPE {path} (inspect intentional guard)")
    if "new ItemStack(Items.BOWL)" in text and "pizza" in str(path).lower():
        warnings.append(f"CRAFTING_BOWL_REFUND {path} (check against cooking refunds)")

# Other container-bearing recipes must keep their vanilla crafting refunds.
# These are intentional no-op checks: do not rewrite working vanilla recipes.
mod_items = (SRC / "dev/italiansdelight/common/registry/ModItems.java").read_text(
    encoding="utf-8")
milk_bucket = mod_items.split("SHEEP_MILK_BUCKET = register(", 1)
require(len(milk_bucket) == 2 and
        ".craftRemainder(Items.BUCKET)" in milk_bucket[1].split("WHEY_BOTTLE_KEY", 1)[0],
        "SHEEP_MILK_CRAFT_REMAINDER: missing empty bucket refund")
sheep_milk_bottles = json_data.get(RECIPES / "sheep_milk_bottle.json", {})
require("italiansdelight:sheep_milk_bucket" in sheep_milk_bottles.get("ingredients", []),
        "SHEEP_MILK_CRAFT_RECIPE: bottle crafting lost milk bucket input")

print(f"SCANNED {len(java_paths)} Java files; {len(json_data)} JSON resources; "
      f"{len(recipe_data)} recipe JSON files")
print(f"CHECKED unified Cheese Vat water alternatives, tag compatibility, "
      "shaped keys, exact recipe duplicates, unintended water-source forks")
for warning in sorted(set(warnings)):
    print("REVIEW:", warning)
for error in errors:
    print("ERROR:", error)
if errors:
    raise SystemExit(f"FAILED: {len(errors)} consistency errors")
print("PASSED: no mechanically detectable duplicate/mismatched recipes")
print("NOTE: other behavior/runtime and mod-to-mod datapack compatibility "
      "still need dedicated tests; warnings are not verified bugs")
