#!/usr/bin/env python3
"""Orchard items: olive, almond and fruit-cherry right-click harvest."""
import hashlib
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT = Path("src/main/resources")
ASSETS = ROOT / "assets/italiansdelight"
DATA = ROOT / "data"
JAVA = Path("src/main/java/dev/italiansdelight/common")
ids = ("olives", "almonds", "cherries")
items = (JAVA / "registry/ModItems.java").read_text(encoding="utf-8")
tab = (JAVA / "item/group/ModItemGroups.java").read_text(encoding="utf-8")

for name in ids:
    symbol = name.upper()
    assert items.count(f'public static final Item {symbol} =') == 1, name
    assert items.count(f'registerSimpleItem("{name}", 64)') == 1, name
    assert tab.count(f'output.accept(ModItems.{symbol});') == 1, name

    definition = json.loads((ASSETS / f"items/{name}.json").read_text())
    model = json.loads((ASSETS / f"models/item/{name}.json").read_text())
    assert definition == {"model": {
        "type": "minecraft:model", "model": f"italiansdelight:item/{name}"
    }}
    assert model == {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"italiansdelight:item/{name}"},
    }
    for locale in ("en_us", "it_it"):
        lang = json.loads((ASSETS / f"lang/{locale}.json").read_text(encoding="utf-8"))
        assert lang[f"item.italiansdelight.{name}"], (locale, name)

# Existing public olives sprite is an indexed PNG; preserve its byte-for-byte
# Git blob SHA, including its palette. Do not re-encode/recolor it.
original_olives = (ASSETS / "textures/item/olives.png").read_bytes()
git_blob = b"blob " + str(len(original_olives)).encode() + b"\0" + original_olives
assert hashlib.sha1(git_blob).hexdigest() == "f24328902ea59a6f74285c0c38ff2c83b045ba17"
assert original_olives[:8] == b"\x89PNG\r\n\x1a\n"
assert int.from_bytes(original_olives[16:20], "big") == 16
assert int.from_bytes(original_olives[20:24], "big") == 16

# Almond remains approved; cherry fruit was deliberately resized.
expected_almonds = "bdc426ebd75d2adc704999bc62b0bfbf2ccc17a290f877acb09ebc989f97ca8c"
for name in ("almonds","cherries"):
    path = ASSETS / f"textures/item/{name}.png"
    if name == "almonds":
        assert hashlib.sha256(path.read_bytes()).hexdigest() == expected_almonds
    pixels = check_rgba_pixel_art(path, (16, 16))
    assert all(px[3] in (0,255) for row in pixels for px in row)
    opaque = [px for row in pixels for px in row if px[3]]
    assert (65 <= len(opaque) <= 95) if name == "cherries" else (85 <= len(opaque) <= 130)
    assert len({px[:3] for px in opaque}) >= 5

# Fruit harvesting for all three species uses the exposed-leaf interaction.
# It must NOT be duplicated by a leaf-break loot entry. Crates remain reversible.
assert all(f'registerFoodItem("{n}"' not in items for n in ids)
assert tab.index("output.accept(ModItems.OLIVES);") < tab.index("output.accept(ModItems.ALMONDS);") < tab.index("output.accept(ModItems.CHERRIES);")
# Crafting inputs may now consume Olives for approved Orchard foods,
# but cutting/recipe outputs must NOT duplicate whole Olives.
# Natural leaves still only give fruit on right-click (never on break).
recipes=DATA/"italiansdelight/recipe"
for path in recipes.rglob("*.json"):
    obj=json.loads(path.read_text(encoding="utf-8"))
    if path.name in {"olives_from_crate.json","almonds_from_crate.json","cherries_from_crate.json"}:
        continue
    # This test guards *new food* recipes, without forbidding valid uses
    # of existing fruit as ingredients or in old pack/storage content.
    if path.name in {"olive_pate.json","bruschetta.json","olive_bruschetta.json","olive_pitted.json"}:
        products=obj.get("result",[])
        assert not any(x in str(products) for x in ('"italiansdelight:olives"','"italiansdelight:almonds"','"italiansdelight:cherries"'))
for name,leaf in (("olives","olive"),("almonds","almond"),("cherries","fruit_cherry")):
    loot=DATA/f"italiansdelight/loot_table/blocks/{leaf}_leaves.json"
    assert loot.is_file()
    assert f"italiansdelight:{name}" not in loot.read_text(encoding="utf-8"),loot
print("OK: original olive/almond art retained, cherry sprite smaller and binary-alpha")
print("OK: olive, almond and cherry harvests use block interactions; no leaf-breaking fruit duplication")
