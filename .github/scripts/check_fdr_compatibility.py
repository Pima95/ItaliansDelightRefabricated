#!/usr/bin/env python3
"""Check opt-in 26.3 cross-mod datapacks without loading any optional mod."""
import json
from pathlib import Path

resources = Path("src/main/resources/data")

def read(path):
    return json.loads((resources / path).read_text(encoding="utf-8"))

def entries(path):
    return set(read(path)["values"])

def ids(names):
    return {"italiansdelight:" + name for name in names}

juice = ids(("apple_juice_bottle", "golden_apple_juice_bottle",
             "grape_juice_bottle", "white_grape_juice_bottle", "cherry_juice_bottle"))
milk = ids(("sheep_milk_bottle", "sheep_milk_bucket"))
wine = ids(("wine_bottle", "white_wine_bottle", "cherry_wine_bottle",
            "mead_bottle", "cider_bottle"))
all_drinks = juice | milk | wine

# Farmer's Delight's canonical category tags are the actual shared
# datapack namespaces used by the 26.3 Refabricated distribution.
assert all_drinks <= entries("farmersdelight/tags/item/drinks.json")
assert all_drinks <= entries("c/tags/item/drinks.json")
assert {
    "italiansdelight:pasta_alla_carbonara",
    "italiansdelight:risotto_with_parmigiano",
    "italiansdelight:mozzarella_salad",
} <= entries("farmersdelight/tags/item/meals.json")
assert {"italiansdelight:wild_grape_vine", "italiansdelight:wild_white_grape_vine",
        "italiansdelight:wild_chili", "italiansdelight:wild_basil"} <= entries(
            "farmersdelight/tags/block/wild_crops.json")

for value, group, hydration_percent in ((2, wine, 20), (4, milk, 40), (6, juice, 60)):
    assert entries(f"toughasnails/tags/item/thirst/{value}_thirst_drinks.json") == group
    assert entries(f"toughasnails/tags/item/hydration/{hydration_percent}_hydration_drinks.json") == group

dehydration = read("italiansdelight/hydration_items/italiansdelight_drinks.json")
for value, group in (("2", wine), ("4", milk), ("6", juice)):
    assert dehydration[value]["replace"] is False
    assert set(dehydration[value]["items"]) == group
assert entries("dehydration/tags/item/hydrating_drinks.json") == milk | wine
assert entries("dehydration/tags/item/stronger_hydrating_drinks.json") == juice

twt2 = read("italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json")["values"]
for value, group in ((2, wine), (4, milk), (6, juice)):
    for item in group:
        assert twt2[item]["thirst"] == value, (item, value)

not_drinks = ids(("whey_bottle", "whey_bucket", "rennet", "rennet_bucket",
                  "apple_cider_vinegar", "red_wine_vinegar_bottle", "olive_oil_bottle"))
for path in ("c/tags/item/drinks.json", "farmersdelight/tags/item/drinks.json",
             "dehydration/tags/item/hydrating_drinks.json",
             "dehydration/tags/item/stronger_hydrating_drinks.json"):
    assert not (entries(path) & not_drinks), path
assert twt2["italiansdelight:olive_oil_bottle"]["thirst"] == 0

assert {"italiansdelight:grapes", "italiansdelight:white_grapes",
        "italiansdelight:cherries"} <= entries("c/tags/item/foods/fruit.json")
assert {"italiansdelight:grape_seeds", "italiansdelight:white_grape_seeds",
        "italiansdelight:chili_pepper_seeds"} <= entries("c/tags/item/seeds.json")
assert {"#c:crops/grape", "#c:crops/chili_pepper"} <= entries("c/tags/item/crops.json")

# JSON loading and the explicit classifications above are checked here;
# dedicated client interaction in each external mod still needs runtime tests.
print("PASSED: FDR tags, common crops/food, TWT2/TAN/Dehydration mappings and exclusions")
