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

# Approved CURRENT numerical snapshot, not a proposal for future balance.
# Form: pizza ID -> (hunger points per slice, saturation points per slice).
EXPECTED = {
    "bianca": (1, 1.0),
    "rossa": (2, 1.5),
    "margherita": (3, 3.0),
    "caprese_rossa": (4, 3.5),
    "caprese_bianca": (3, 3.0),
    "quattro_formaggi_bianca": (2, 2.5),
    "quattro_formaggi_rossa": (3, 3.0),
    "prosciutto_crudo": (5, 5.5),
    "salame": (5, 5.5),
    "mortadella_ricotta": (4, 5.0),
    "guanciale_pecorino_rossa": (5, 5.5),
    "speck_gorgonzola": (4, 5.0),
    "salmone_ricotta": (4, 5.0),
    "salmone_scamorza": (4, 5.0),
    "carne_mozzarella": (5, 5.5),
    "zucca_speck": (5, 5.5),
    "patate_speck": (5, 5.5),
    "orto_disponibile": (3, 3.0),
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
        "EnumSet.noneOf(NutritionCategory.class)",
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
        "categories.add(NutritionCategory.DAIRY);",
    ]
    for snippet in snippets:
        require(snippet in source, f"Nutrition formula changed: {snippet!r}")


def food_values(categories: set[str]) -> tuple[int, float]:
    hunger, sat = 1, 1.0  # Crust
    for category in categories:
        bonus_hunger, bonus_sat = CATEGORY_BONUSES[category]
        hunger += bonus_hunger
        sat += bonus_sat
    return min(6, hunger), min(6.0, sat)


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
        categories = {catalogue[item] for item in items}
        actual = food_values(categories)
        require(actual == EXPECTED[name],
                f"{name}: current snapshot {EXPECTED[name]}, got {actual}")
    require(food_values(set(CATEGORY_BONUSES)) == (6, 6.0),
            "Six-point caps are no longer applied")
    require(food_values({"DAIRY"}) == (2, 2.5),
            "Multiple cheeses should count as one dairy category")
    audit_baking()
    print("OK: all 18 canonical pizza presets have consistent current nutrition")
    print("OK: categories counted once per slice; hunger/saturation capped at 6")
    print("OK: pizza oven 400 ticks, smoker 600 ticks, furnace 1200 ticks")
    print("OK: smoker/furnace JSON & serializers match and retain pizza data")
    print("NOTE: static source/recipe verification; client/runtime tests still required")


if __name__ == "__main__":
    main()
