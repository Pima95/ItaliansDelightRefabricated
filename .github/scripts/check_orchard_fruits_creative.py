#!/usr/bin/env python3
"""Orchard items: olive and almond right-click harvest; cherry still Creative-only."""
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

# Cherry harvesting is still pending. Olive and almond right-click harvests exist in Java;
# it must NOT be duplicated by an olive leaf-break loot entry. Crate recipes remain intentional.
assert all(f'registerFoodItem("{n}"' not in items for n in ids)
assert tab.index("output.accept(ModItems.OLIVES);") < tab.index("output.accept(ModItems.ALMONDS);") < tab.index("output.accept(ModItems.CHERRIES);")
allowed={f"{n}_crate.json" for n in ("olive","almond","cherry")}
allowed|={f"{n}_from_crate.json" for n in ids}
for folder in (DATA / "italiansdelight/recipe", DATA / "italiansdelight/loot_table"):
    if folder.is_dir():
        for path in folder.rglob("*.json"):
            content=path.read_text(encoding="utf-8")
            if path.parent==DATA / "italiansdelight/recipe" and path.name in allowed:
                continue
            assert not any(f"italiansdelight:{n}" in content for n in ids), (
                "Premature fruit crafting/harvesting",str(path))
print("OK: original olive/almond art retained, cherry sprite smaller and binary-alpha")
print("OK: olive and almond harvests use block interactions, no fruit-breaking loot duplication; cherry pending")
