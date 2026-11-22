#!/usr/bin/env python3
"""Guard native drinkability, compatibility tags and TWT2 values for the 1.2.0 branch."""
import json
from pathlib import Path

root = Path("src/main/resources/data")
java = Path("src/main/java/dev/italiansdelight/common")
items = (java / "registry/ModItems.java").read_text(encoding="utf-8")
bottle = (java / "item/WineBottleItem.java").read_text(encoding="utf-8")
effects = (java / "item/WineDrinkEffects.java").read_text(encoding="utf-8")
drink_ids = (
    "sheep_milk_bottle", "sheep_milk_bucket",
    "apple_juice_bottle", "golden_apple_juice_bottle",
    "grape_juice_bottle", "white_grape_juice_bottle",
    "cherry_juice_bottle", "olive_oil_bottle",
    "wine_bottle", "white_wine_bottle", "cherry_wine_bottle",
    "mead_bottle", "cider_bottle",
)

def values(path):
    return json.loads((root / path).read_text(encoding="utf-8"))["values"]

tag_drinks = set(values("c/tags/item/drinks.json"))
tag_bottle = set(values("c/tags/item/drink_containing/bottle.json"))
tag_bucket = set(values("c/tags/item/drink_containing/bucket.json"))
thirst = values("italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json")

for name in drink_ids:
    key = "italiansdelight:" + name
    assert key in thirst, "Missing Thirst Was Taken 2 entry: " + name
    assert key in (tag_bottle if name.endswith("_bottle") else tag_bucket), name
    assert (key in tag_drinks) == (name != "olive_oil_bottle"), name
    record = thirst[key]
    assert isinstance(record["thirst"], int) and 0 <= record["thirst"] <= 20
    assert isinstance(record.get("quenched", 0), int) and 0 <= record.get("quenched", 0) <= 20

# An oil bottle may be drunk, but cannot hydrate: the explicit TWT2
# override prevents default fallback, and no broad c:drinks tag is given.
assert thirst["italiansdelight:olive_oil_bottle"]["thirst"] == 0
for name in ("whey_bottle", "rennet", "apple_cider_vinegar",
             "red_wine_vinegar_bottle"):
    assert "italiansdelight:" + name not in tag_drinks
    assert "italiansdelight:" + name not in thirst

for name in ("WINE_BOTTLE", "WHITE_WINE_BOTTLE",
             "CHERRY_WINE_BOTTLE", "MEAD_BOTTLE", "CIDER_BOTTLE"):
    start = items.index("public static final Item " + name + " = register(")
    end = items.index(";", start)
    definition = items[start:end]
    assert "DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK" in definition, name
    assert "usingConvertsTo(Items.GLASS_BOTTLE)" in definition, name
    assert "ModDataComponents.WINE_QUALITY, 0" in definition, name

assert "finishUsingItem(ItemStack stack, Level level, LivingEntity user)" in bottle
assert "WineDrinkEffects.apply(stack, user)" in bottle
for token in ("change", "durations(", "potency(", "absorption(",
              "getOrDefault(ModDataComponents.WINE_QUALITY, 0)",
              "MAX_DURATION = 12000", "MAX_AMPLIFIER = 2",
              "MAX_ABSORPTION = 20.0F", "isBeneficial()"):
    assert token in effects, token

print("PASSED: all 13 consumable drinks mapped; TWT2 data and wine-quality effect hooks checked")
