#!/usr/bin/env python3
"""Static Pizza 1.0.0 balance snapshot and data-consistency checks.

Reads the authoritative Java recipe/category tables, food algorithm, baking
constants and actual furnace/smoker JSON files. Does NOT simulate client or
server runtime, or decide future balance on the author's behalf.
Update the expectations deliberately if an approved rebalance changes values.
"""
from __future__ import annotations

import json
import re
from pathlib import Path

JAVA = Path("src/main/java/dev/italiansdelight/common/pizza")
DATA = Path("src/main/resources/data/italiansdelight/recipe")

# Approved maximum 2 distinct ingredients per category (21 October 2026).
# Form: pizza ID -> (hunger points per slice, saturation points per slice).
EXPECTED = {
    "bianca": (1, 1.0),
    "rossa": (2, 1.5),
    "margherita": (3, 3.0),
    "caprese_rossa": (4, 3.5),
    "caprese_bianca": (3, 3.0),
    "quattro_formaggi_bianca": (3, 4.0),
    "quattro_formaggi_rossa": (4, 4.5),
    "prosciutto_crudo": (5, 5.5),
    "salame": (5, 5.5),
    "mortadella_ricotta": (5, 6.0),
    "guanciale_pecorino_rossa": (5, 5.5),
    "speck_gorgonzola": (5, 6.0),
    "salmone_ricotta": (5, 6.0),
    "salmone_scamorza": (5, 6.0),
    "carne_mozzarella": (5, 5.5),
    "zucca_speck": (5, 5.5),
    "patate_speck": (5, 5.5),
    "orto_disponibile": (4, 3.5),
}

CATEGORY_BONUSES = {
    "DAIRY": (1, 1.5),
    "ANIMAL_PROTEIN": (2, 2.5),
    "VEGETABLE": (1, 0.5),
    "SAUCES": (1, 0.5),
    "HERB": (0, 0.0),
    "OILS_FATS": (0, 1.0),
}


def require(condition: bool, message: str) -> None:
    if not condition:
        raise ValueError(message)


def recipes_from_java() -> dict[str, list[str]]:
    source = (JAVA / "PizzaDisplayNames.java").read_text(encoding="utf-8")
    recipes: dict[str, list[str]] = {}
    # Multiline add(presets, ...) in the single authoritative method.
    for match in re.finditer(r'add\(presets,\s*"([^"]+)"(.*?)\);', source, re.S):
        taste = match.group(1)
        require(taste not in recipes, f"Duplicate preset {taste}")
        recipes[taste] = re.findall(r'"([^"]+)"', match.group(2))
    require(len(recipes) == 18 and "principessa" not in recipes,
            f"Expected 18 active presets excluding Principessa, got {len(recipes)}")
    require(set(recipes) == set(EXPECTED),
            "Changed preset identities: review and update the balance snapshot")
    return recipes


def catalogue_from_java() -> dict[str, str]:
    source = (JAVA / "PizzaToppingCatalog.java").read_text(encoding="utf-8")
    matches = re.findall(
        r'add\(types,\s*nativeItems,\s*"([^"]+)",\s*'
        r'(DAIRY|ANIMAL_PROTEIN|VEGETABLE|SAUCES|HERB|OILS_FATS),',
        source,
    )
    result = dict(matches)
    require(len(matches) == 31 and len(result) == 31,
            f"Expected 31 distinct canonical topping types, got {len(result)}")
    for must_not in ("mozzarella", "scamorza", "mascarpone"):
        require(must_not not in result,
                f"Whole/unprepared {must_not} wrongly permitted as topping")
    return result


def audit_food_algorithm() -> None:
    source = (JAVA / "PizzaSliceFactory.java").read_text(encoding="utf-8")
    # Guard the ACTUAL nutritional terms before deriving snapshot from category data.
    snippets = [
        "EnumMap<NutritionCategory, Integer> counts = new EnumMap<>(NutritionCategory.class);",
        "Set<Identifier> seenTypes = new HashSet<>();",
        "if (!seenTypes.add(topping.canonicalId()))",
        "counts.merge(type.nutritionCategory(), 1, Integer::sum)",
        "int contributions = Math.min(2, counts.getOrDefault(category, 0));",
        '"italiansdelight:pizza_toppings/mozzarella_slice"',
        "int hunger = 1;",
        "float saturation = 1.0f;",
        "case DAIRY -> { hunger += 1; saturation += 1.5f; }",
        "case ANIMAL_PROTEIN -> { hunger += 2; saturation += 2.5f; }",
        "case VEGETABLE -> { hunger += 1; saturation += 0.5f; }",
        "case SAUCES -> { hunger += 1; saturation += 0.5f; }",
        "case HERB -> { /* herbs only add flavor */ }",
        "case OILS_FATS -> { saturation += 1.0f; }",
        "hunger = Math.min(6, hunger);",
        "saturation = Math.min(6.0f, saturation);",
        ".saturationModifier(saturation / (2.0f * hunger))",
        "counts.merge(NutritionCategory.DAIRY, 1, Integer::sum)",
    ]
    for snippet in snippets:
        require(snippet in source, f"Nutrition formula changed: {snippet!r}")


def food_values(
    canonical_items: list[str], catalogue: dict[str, str], legacy_mozzarella: bool = False
) -> tuple[int, float]:
    """Model Java semantics: distinct canonical item types, max two per category."""
    counts: dict[str, int] = {}
    seen_types = set(canonical_items)
    for item in seen_types:
        category = catalogue.get(item)
        if category:
            counts[category] = counts.get(category, 0) + 1

    # Whole legacy mozzarella and modern mozzarella slice are the same cheese.
    if legacy_mozzarella and "mozzarella_slice" not in seen_types:
        counts["DAIRY"] = counts.get("DAIRY", 0) + 1

    hunger, sat = 1, 1.0  # Crust
    for category, number in counts.items():
        bonus_hunger, bonus_sat = CATEGORY_BONUSES[category]
        hunger += min(2, number) * bonus_hunger
        sat += min(2, number) * bonus_sat
    return min(6, hunger), min(6.0, sat)


def audit_nourishment(recipes: dict[str, list[str]], catalogue: dict[str, str]) -> None:
    """Dynamic short Nourishment belongs to substantial slices, not named recipes."""
    source = (JAVA / "PizzaSliceFactory.java").read_text(encoding="utf-8")
    requirements = [
        "import vectorwing.farmersdelight.common.FoodValues;",
        "private static final int NOURISHMENT_MIN_HUNGER = 3;",
        "FoodProperties food = foodFor(slice);",
        "stack.set(DataComponents.FOOD, food);",
        "if (food.nutrition() >= NOURISHMENT_MIN_HUNGER)",
        "stack.set(DataComponents.CONSUMABLE,",
        "FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION",
    ]
    for expression in requirements:
        require(expression in source, f"Pizza slice Nourishment changed: {expression}")
    # Vanilla food component remains for generic bare pizza_slice stacks.
    items = Path("src/main/java/dev/italiansdelight/common/registry/ModItems.java").read_text(encoding="utf-8")
    require("PIZZA_SLICE = register(" in items and ".nutrition(1).saturationModifier(0.5f).build()" in items,
            "Default pizza_slice must remain a plain edible item")
    no_effect = {name for name, toppings in recipes.items()
                 if food_values(toppings, catalogue)[0] < 3}
    require(no_effect == {"bianca", "rossa"},
            f"Incorrect Nourishment eligibility among 18 presets: {no_effect}")
    for toppings, expected in [
        ([], False),
        (["tomato_sauce"], False),
        (["mozzarella_slice"], False),
        (["mozzarella_slice", "gorgonzola_wedge"], True),
        (["mozzarella_slice", "gorgonzola_wedge", "provolone_slice"], True),
        (["salame_slice"], True),
        (["tomato_sauce", "mozzarella_slice", "basil"], True),
    ]:
        require((food_values(toppings, catalogue)[0] >= 3) is expected,
                f"Wrong Nourishment threshold for {toppings}")


def audit_saved_slice_compatibility() -> None:
    """Old stack components must converge to the new dynamic rules without repeated writes."""
    factory = (JAVA / "PizzaSliceFactory.java").read_text(encoding="utf-8")
    named_item = Path("src/main/java/dev/italiansdelight/common/item/PizzaNamedItem.java").read_text(encoding="utf-8")
    for expression in (
        "public static boolean refreshSavedSlice(ItemStack stack)",
        "if (!stack.is(ModItems.PIZZA_SLICE))",
        "PizzaSliceData data = stack.get(ModDataComponents.PIZZA_SLICE_CONTENTS);",
        "FoodProperties expectedFood = foodFor(data);",
        "if (!expectedFood.equals(stack.get(DataComponents.FOOD)))",
        "stack.set(DataComponents.FOOD, expectedFood);",
        "if (expectedFood.nutrition() >= NOURISHMENT_MIN_HUNGER)",
        "if (!nourishment.equals(currentConsumable))",
        "stack.set(DataComponents.CONSUMABLE, nourishment);",
        "else if (nourishment.equals(currentConsumable))",
        "stack.remove(DataComponents.CONSUMABLE);",
    ):
        require(expression in factory, f"Legacy slice component migration changed: {expression}")
    require("if (slice) {" in named_item
            and "PizzaSliceFactory.refreshSavedSlice(stack);" in named_item
            and "PizzaDisplayNames.refreshMixedItemModel(stack);" in named_item,
            "Legacy slices and mixed whole pizzas must both be upgraded on inventory tick")


def audit_nourishment_tooltip() -> None:
    """The pizza item uses the dependency's tooltip formatter, not hard-coded time."""
    item = Path("src/main/java/dev/italiansdelight/common/item/PizzaNamedItem.java").read_text(encoding="utf-8")
    required = [
        "import vectorwing.farmersdelight.common.Configuration;",
        "import vectorwing.farmersdelight.common.utility.TextUtils;",
        "if (slice) {",
        "super.appendHoverText(stack, context, display, consumer, type);",
        "PizzaDisplayNames.appendSliceTooltip(data, consumer);",
        "if (Configuration.ENABLE_FOOD_EFFECT_TOOLTIP.get())",
        "TextUtils.addFoodEffectTooltip(stack, consumer, 1.0F, context.tickRate());",
    ]
    for expression in required:
        require(expression in item, f"Pizza slice tooltip no longer matches Farmer's Delight: {expression}")
    # The formatter must be in the slice branch (not on raw/cooked whole pizza).
    slice_branch = item.split("public void appendHoverText(", 1)[1]
    require(slice_branch.index("TextUtils.addFoodEffectTooltip(")
            < slice_branch.index("} else {"),
            "Food effect tooltip must be scoped to edible pizza slices")
    require("Component.literal(\"Nourishment" not in item
            and "60 seconds" not in item,
            "Effect names and durations must be read from actual Consumable")


def audit_baking() -> None:
    source = (JAVA / "PizzaBaking.java").read_text(encoding="utf-8")
    require("MAX_PROGRESS = 1_200" in source, "Pizza baking maximum changed")
    for item in ("PIZZA_OVEN(3)", "SMOKER(2)", "FURNACE(1)"):
        require(item in source, f"Unexpected pizza baking speed: {item}")
    require("if (application.remainingMask() != 0" in source
            and "isBowlBearing(application.canonicalId())" in source,
            "Bowl remainders must count active applications")
    require("new ItemStack(Items.BOWL, bowls)" in source,
            "Bowl count is not preserved")

    for filename, recipe_type, ticks, java_file, codec_ticks in [
        ("pizza_smelting.json", "italiansdelight:pizza_smelting",
         1200, "src/main/java/dev/italiansdelight/common/crafting/PizzaSmeltingRecipe.java", 1200),
        ("pizza_smoking.json", "italiansdelight:pizza_smoking",
         600, "src/main/java/dev/italiansdelight/common/crafting/PizzaSmokingRecipe.java", 600),
    ]:
        recipe = json.loads((DATA / filename).read_text(encoding="utf-8"))
        require(recipe["type"] == recipe_type, f"Wrong recipe type in {filename}")
        require(recipe["cookingtime"] == ticks, f"Cookingtime mismatch: {filename}")
        require(recipe["ingredient"] == "italiansdelight:raw_pizza",
                f"Unexpected input in {filename}")
        require(recipe["result"]["id"] == "italiansdelight:cooked_pizza",
                f"Unexpected output in {filename}")
        serializer = Path(java_file).read_text(encoding="utf-8")
        require(f"cookingMapCodec(" in serializer
                and re.search(r"cookingMapCodec\([^)]*,\s*"
                              + str(codec_ticks) + r"\)", serializer) is not None,
                f"Serializer cooking time differs from datapack: {java_file}")
        require("PizzaBaking.finish(input.item(), PizzaBaking.MAX_PROGRESS)"
                in serializer, f"Component-preserving baking missing in {java_file}")

    oven = Path("src/main/java/dev/italiansdelight/common/block/entity/"
                "PizzaOvenBlockEntity.java").read_text(encoding="utf-8")
    require("PizzaBaking.Oven.PIZZA_OVEN" in oven,
            "Pizza oven no longer uses authoritative cooking speed")
    require('input.getIntOr("CookTime", 0)' in oven,
            "Oven progress must be saved in the block entity")
    require("PizzaBaking.finish(input, oven.cookingProgress)" in oven,
            "Oven must use the component-preserving baking result")


def main() -> None:
    audit_food_algorithm()
    recipes = recipes_from_java()
    catalogue = catalogue_from_java()
    for name, items in recipes.items():
        missing = [item for item in items if item not in catalogue]
        require(not missing, f"{name} uses unknown topping types: {missing}")
        actual = food_values(items, catalogue)
        require(actual == EXPECTED[name],
                f"{name}: current snapshot {EXPECTED[name]}, got {actual}")

    # Identical or raw/cooked-alias canonical types never multiply the bonus.
    mozzarella = ["mozzarella_slice"]
    two_cheeses = ["mozzarella_slice", "gorgonzola_wedge"]
    four_cheeses = ["mozzarella_slice", "gorgonzola_wedge",
                    "provolone_slice", "grated_parmigiano_reggiano"]
    require(food_values(mozzarella, catalogue) == (2, 2.5), "First dairy")
    require(food_values(mozzarella + mozzarella, catalogue) == (2, 2.5),
            "Duplicate canonical type must count once")
    require(food_values(two_cheeses, catalogue) == (3, 4.0),
            "Two distinct dairy types must stack")
    require(food_values(four_cheeses, catalogue) == (3, 4.0),
            "Third/fourth dairy must NOT stack")
    require(food_values(["salame_slice", "speck_slice"], catalogue) == (5, 6.0),
            "Two animal proteins must stack (with saturation cap)")
    require(food_values(["mozzarella_slice"], catalogue, True) == (2, 2.5),
            "Legacy plus same modern mozzarella must count once")
    require(food_values(["gorgonzola_wedge"], catalogue, True) == (3, 4.0),
            "Legacy whole mozzarella remains a distinct dairy contribution")
    require(food_values(["mozzarella_slice", "salame_slice", "speck_slice",
                         "tomato_sauce", "tomato_slice"], catalogue) == (6, 6.0),
            "Six-point caps must apply to mixed-category custom pizza")
    audit_nourishment(recipes, catalogue)
    audit_nourishment_tooltip()
    audit_saved_slice_compatibility()
    audit_baking()
    print("OK: all 18 canonical pizza presets match updated per-slice nutrition")
    print("OK: max 2 DISTINCT canonical types per category, max 6 hunger/saturation")
    print("OK: duplicate types, old mozzarella and custom pizza categories checked")
    print("OK: short Nourishment on newly created slices >=3 hunger (16 of 18 named presets)")
    print("OK: tooltip uses Farmer\'s Delight effect name/duration and respects its config")
    print("OK: saved pizza slices refresh legacy food/consumable components idempotently")
    print("OK: pizza oven 400 ticks, smoker 600 ticks, furnace 1200 ticks")
    print("OK: smoker/furnace JSON & serializers match and retain pizza data")
    print("NOTE: static source/recipe verification; client/runtime tests still required")


if __name__ == "__main__":
    main()
