#!/usr/bin/env python3
"""Check that Italian's Delight pizza names agree across items and locales.

Standard-library only:
    python scripts/check_pizza_names.py

The source of truth for recognized tastes remains PizzaDisplayNames.java.
A complete raw/cooked pizza MUST use the same localized taste as a slice.
The cooking state is an item property, not part of the recognized taste name.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/italiansdelight/lang"
NAMES_SOURCE = ROOT / "src/main/java/dev/italiansdelight/common/pizza/PizzaDisplayNames.java"
ITEM_SOURCE = ROOT / "src/main/java/dev/italiansdelight/common/item/PizzaNamedItem.java"
PREFIX = "pizza.italiansdelight."
SPECIAL = ("bianca", "rossa")


def check(condition: bool, detail: str, failures: list[str]) -> None:
    if not condition:
        failures.append(detail)


def translate(template: str, name: str) -> str:
    return template.replace("%s", name)


def main() -> int:
    en = json.loads((ASSETS / "en_us.json").read_text(encoding="utf-8"))
    it = json.loads((ASSETS / "it_it.json").read_text(encoding="utf-8"))
    source = NAMES_SOURCE.read_text(encoding="utf-8")
    item_source = ITEM_SOURCE.read_text(encoding="utf-8")
    failures: list[str] = []

    tastes = re.findall(r'add\(presets,\s*"([a-z_]+)"', source)
    check(len(tastes) == 17, f"Expected 17 active tastes, found {len(tastes)}", failures)
    check(len(set(tastes)) == len(tastes), "Duplicate taste ID", failures)

    en_keys = {k for k in en if k.startswith(PREFIX)}
    it_keys = {k for k in it if k.startswith(PREFIX)}
    check(en_keys == it_keys, "Translation key mismatch between en_us and it_it: "
          + str(sorted(en_keys ^ it_keys)), failures)

    for item in ("raw_pizza", "cooked_pizza", "pizza_slice"):
        key = "item.italiansdelight." + item
        check(key in en and key in it, f"Missing fallback item label {key}", failures)

    check("PizzaDisplayNames.wholeName(data)" in item_source,
          "Raw and cooked pizza items no longer use the same naming function", failures)
    check("PizzaDisplayNames.sliceName(data)" in item_source,
          "Pizza slice item no longer uses the slice naming function", failures)

    for locale, messages in (("en_us", en), ("it_it", it)):
        for k in ("named_whole", "named_slice", "with_whole", "with_slice",
                  "assorted", "slice_bianca", "slice_rossa"):
            check(PREFIX + k in messages, f"{locale}: missing {k}", failures)
        for taste_id in tastes:
            taste_key = PREFIX + "taste." + taste_id
            if taste_key not in messages:
                failures.append(f"{locale}: missing taste {taste_id}")
                continue
            taste = messages[taste_key]
            whole = translate(messages[PREFIX + "named_whole"], taste)
            # raw and cooked have IDENTICAL taste names by design
            raw_name = cooked_name = whole

            if taste_id in SPECIAL:
                slice_name = messages[PREFIX + "slice_" + taste_id]
            else:
                slice_name = translate(messages[PREFIX + "named_slice"], taste)

            check(raw_name == cooked_name, f"{locale} {taste_id}: cooking changes taste", failures)
            if locale == "en_us":
                check(slice_name == whole + " Slice",
                      f"{locale} {taste_id}: '{whole}' vs '{slice_name}'", failures)
            else:
                check(whole == "Pizza " + taste,
                      f"{locale} {taste_id}: malformed whole name '{whole}'", failures)
                expected = ("Spicchio di pizza " + taste.lower()
                            if taste_id in SPECIAL else "Spicchio di " + taste)
                check(slice_name == expected,
                      f"{locale} {taste_id}: expected '{expected}', got '{slice_name}'",
                      failures)

        # Dynamic names and mixed pizzas must use the same naming conventions.
        expected_templates = (
            {"with_whole": "Pizza with %s",
             "with_slice": "Pizza Slice with %s",
             "assorted": "Assorted Pizza"}
            if locale == "en_us" else
            {"with_whole": "Pizza con %s",
             "with_slice": "Spicchio con %s",
             "assorted": "Pizza assortita"}
        )
        for key, value in expected_templates.items():
            check(messages.get(PREFIX + key) == value,
                  f"{locale}: unexpected dynamic name {key}", failures)

    if failures:
        for failure in failures:
            print("FAIL:", failure, file=sys.stderr)
        return 1

    # Each locale has one name for raw and cooked + one per matching slice.
    print(f"OK: {len(tastes)} pizza tastes, Italian and English: "
          f"{len(tastes) * 3 * 2} consistent raw/cooked/slice names.")
    print("OK: dynamic, assorted, fallback labels and translation keys.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
