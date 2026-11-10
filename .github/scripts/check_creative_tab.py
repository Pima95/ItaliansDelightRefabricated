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
    "POTTED_FRUIT_CHERRY_SAPLING",
    "PIZZA_DOUGH",
    "COOKED_PIZZA",
    "CHEESE_HOOK_OCCUPIED_SPACE",
    "AGING_CHEESE",
    "SALT_CAULDRON",
    "OLIVE_OIL_FLUID_BLOCK",  # Technical liquid state: it has no BlockItem.
    "APPLE_JUICE_FLUID_BLOCK", # Technical fluid state; its bucket/bottle are the inventory items.
    "OLIVE_OIL_CAULDRON",  # Legacy filled oil cauldron, no standalone BlockItem.
    "FLUID_CAULDRON",  # Generic custom-fluid cauldron, visually vanilla.
    # These 4 sign blocks are placed by two SignItem instances, not BlockItems.
    "OLIVE_SIGN", "OLIVE_WALL_SIGN", "OLIVE_HANGING_SIGN", "OLIVE_WALL_HANGING_SIGN",
    "ALMOND_SIGN", "ALMOND_WALL_SIGN", "ALMOND_HANGING_SIGN", "ALMOND_WALL_HANGING_SIGN",
    "FRUIT_CHERRY_SIGN", "FRUIT_CHERRY_WALL_SIGN", "FRUIT_CHERRY_HANGING_SIGN", "FRUIT_CHERRY_WALL_HANGING_SIGN",
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
        "// WORKSTATIONS AND STORAGE:",
        "// CRATES AND CROP STORAGE.",
        "// OLIVE WOOD:", "// ALMOND WOOD:", "// FRUIT CHERRY WOOD:",
        "// TOOLS:", "// CROPS AND WILD PLANTS.",
        "// ORCHARD FRUITS AND RAW INGREDIENTS.",
        "// SAUCES, SEASONINGS AND PREPARED VEGETABLES.",
        "// OILS, JUICES AND OTHER STORED LIQUIDS.",
        "// MILK, CULTURES AND CHEESE-PRODUCTION INGREDIENTS.",
        "// CHEESES:", "// CURED MEATS:", "// RAW PIZZAS:",
        "// COOKED PIZZAS:", "// EDIBLE SLICES:", "// PREPARED DISHES",
    ]
    offsets = [content.find(marker) for marker in markers]
    if min(offsets) < 0 or offsets != sorted(offsets) or len(set(offsets)) != len(offsets):
        raise ValueError("Creative tab categories must stay contiguous and ordered")

    # Verify the Creative ordering without changing the available item set.
    ordered = [m.group(1) for m in re.finditer(
        r"output\.accept\((Mod(?:Items|Blocks)\.[A-Z][A-Z0-9_]*)\);", content)]
    def before(left, right):
        if ordered.index(left) >= ordered.index(right):
            raise ValueError(f"Creative order regression: {left} must precede {right}")
    for left, right in (
        ("ModItems.OLIVES", "ModItems.ALMONDS"),
        ("ModItems.ALMONDS", "ModItems.CHERRIES"),
        ("ModItems.CHOPPED_ALMONDS", "ModItems.ALMOND_PESTO"),
        ("ModItems.BASIL_PESTO", "ModItems.PASTA_WITH_PESTO"),
        ("ModItems.ALMOND_PESTO", "ModItems.PASTA_WITH_ALMOND_PESTO"),
        ("ModItems.PASTA_WITH_ALMOND_PESTO", "ModBlocks.ALMOND_CAKE"),
        ("ModItems.BRUSCHETTA", "ModBlocks.ALMOND_CAKE"),
    ):
        before(left, right)

    # Complete Creative order, including sign/boat instances not included in the
    # basic registry-presence check above.
    full_order = [m.group(1) for m in re.finditer(
        r"output\.accept\((Mod(?:Items|Blocks|Signs|Boats)\.[A-Z0-9_]+)\);", content
    )]
    if full_order[0] != "ModBlocks.PIZZA_OVEN":
        raise ValueError("Pizza oven must be the first Creative entry")
    if not (
        full_order.index("ModBlocks.FRUIT_CHERRY_CHEESE_AGING_RACK")
        < full_order.index("ModBlocks.OLIVE_CRATE")
        < full_order.index("ModBlocks.CARDOON")
    ):
        raise ValueError("Storage crates belong with the blocks, before crops/ingredients")
    crates = ("OLIVE", "ALMOND", "CHERRY", "BASIL", "CARDOON")
    crate_indexes = [full_order.index(f"ModBlocks.{c}_CRATE") for c in crates]
    if crate_indexes != list(range(crate_indexes[0], crate_indexes[0]+len(crates))):
        raise ValueError("The five crate blocks must remain contiguous")
    derivatives = (
        "LOG", "WOOD", "STRIPPED_LOG", "STRIPPED_WOOD", "PLANKS",
        "STAIRS", "SLAB", "FENCE", "FENCE_GATE", "DOOR", "TRAPDOOR",
        "PRESSURE_PLATE", "BUTTON", "SIGN", "HANGING_SIGN",
        "CABINET", "SHELF", "BOAT_ITEM", "CHEST_BOAT_ITEM",
        "LEAVES", "SAPLING",
    )
    for species in ("OLIVE", "ALMOND", "FRUIT_CHERRY"):
        wood_order = []
        for derivative in derivatives:
            symbol = f"STRIPPED_{species}_{derivative[9:]}" if derivative.startswith("STRIPPED_") else f"{species}_{derivative}"
            prefix = ("ModBoats" if derivative.endswith("BOAT_ITEM")
                      else "ModSigns" if derivative in ("SIGN", "HANGING_SIGN")
                      else "ModBlocks")
            wood_order.append(f"{prefix}.{symbol}")
        positions = [full_order.index(item) for item in wood_order]
        if positions != list(range(positions[0], positions[0]+len(positions))):
            raise ValueError(f"{species} wood derivatives are non-contiguous/out of order")
    fluid_pairs = (
        ("OLIVE_OIL_BUCKET", "OLIVE_OIL_BOTTLE"),
        ("APPLE_JUICE_BUCKET", "APPLE_JUICE_BOTTLE"),
        ("GOLDEN_APPLE_JUICE_BUCKET", "GOLDEN_APPLE_JUICE_BOTTLE"),
        ("SHEEP_MILK_BUCKET", "SHEEP_MILK_BOTTLE"),
        ("WHEY_BUCKET", "WHEY_BOTTLE"),
        ("RENNET_BUCKET", "RENNET"),
        ("APPLE_CIDER_VINEGAR_BUCKET", "APPLE_CIDER_VINEGAR"),
    )
    for bucket, bottle in fluid_pairs:
        if full_order.index(f"ModItems.{bucket}") + 1 != full_order.index(f"ModItems.{bottle}"):
            raise ValueError(f"{bucket} must directly precede {bottle}")
    # Pizza examples are added as extra JEI ingredients in the same three
    # Creative families; JEI's user-configured sorting determines final display.
    jei_samples = Path("src/main/java/dev/italiansdelight/integration/jei/PizzaJeiIngredientSamples.java").read_text(encoding="utf-8")
    for snippet in (
        "stacks.add(PizzaCreativeSamples.create(recipe, false));",
        "stacks.add(PizzaCreativeSamples.createMixed(false));",
        "stacks.add(PizzaCreativeSamples.create(recipe, true));",
        "stacks.add(PizzaCreativeSamples.createMixed(true));",
        "stacks.add(PizzaCreativeSamples.createSlice(recipe));",
    ):
        if snippet not in jei_samples:
            raise ValueError(f"Missing JEI pizza sample: {snippet}")
    jei_positions = [jei_samples.index(x) for x in (
        "stacks.add(PizzaCreativeSamples.create(recipe, false));",
        "stacks.add(PizzaCreativeSamples.createMixed(false));",
        "stacks.add(PizzaCreativeSamples.create(recipe, true));",
        "stacks.add(PizzaCreativeSamples.createMixed(true));",
        "stacks.add(PizzaCreativeSamples.createSlice(recipe));",
    )]
    if jei_positions != sorted(jei_positions):
        raise ValueError("JEI pizza extras must follow Creative raw/cooked/slice order")

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
