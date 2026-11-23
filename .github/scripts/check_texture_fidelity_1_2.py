#!/usr/bin/env python3
"""Validate existing-silhouette texture edits and absence of obsolete amatriciana crafting."""
from pathlib import Path
from hashlib import sha256
from check_named_pizza_slices import rgba_png
import json

ASSETS=Path("src/main/resources/assets/italiansdelight")
RECIPES=Path("src/main/resources/data/italiansdelight/recipe")
# Each SHA256 is for the 16x16 binary alpha plane, not for the mutable RGB art.
shapes={
    "almond_biscuits": "91ef81b4d57ba66a04665dc08ac339ca0f62009dbc358eb587f7177888ced703",
    "grape_cherry_fruit_salad": "94a6f60d060dcdc0395cc44ed7a2fcb958a97008552dc4b451ae3f04439e6825",
    "risotto_with_scamorza_and_olives": "5487e5f0756c33862bc124930544a1dbc9b4d55bf40ffc6284e77a726d0fe9fd",
    "pasta_with_ricotta_and_pancetta": "eb9bbbc0e582eb073cf1cac6b468aa71400109fd2fcbf4350e90b708feacde99",
}
for name, expected in shapes.items():
    rgba=rgba_png(ASSETS / "textures/item" / (name + ".png"))
    alpha=bytes(rgba[3::4])
    assert sha256(alpha).hexdigest()==expected,(name,"outline unexpectedly changed")
# At the same time compare directly against unmodified textures in this mod.
for revised,reference in (
    ("grape_cherry_fruit_salad","mozzarella_salad"),
    ("risotto_with_scamorza_and_olives","risotto_with_mushroom"),
):
    a=rgba_png(ASSETS/"textures/item"/(revised+".png"))
    b=rgba_png(ASSETS/"textures/item"/(reference+".png"))
    assert a[3::4]==b[3::4], (revised, reference, "silhouette mismatch")
# The ricotta pasta's central area should contain no old pesto-green pixels.
import colorsys
pasta=rgba_png(ASSETS/"textures/item/pasta_with_ricotta_and_pancetta.png")
for y in range(4,10):
    for x in range(4,13):
        pos=4*(y*16+x)
        r,g,b,a=pasta[pos:pos+4]
        if not a:
            continue
        hue=colorsys.rgb_to_hsv(r/255,g/255,b/255)[0]
        assert not (.14<hue<.45 and g>r*.8 and g>b), ("green pasta pixel",x,y)
# Only the Cooking Pot recipe may yield the usual amatriciana.
assert not (RECIPES/"fermented/red_wine_amatriciana.json").exists()
normal=json.loads((RECIPES/"cooking/pasta_all_amatriciana.json").read_text())
assert normal["type"]=="farmersdelight:cooking"
assert normal["result"]["id"]=="italiansdelight:pasta_all_amatriciana"
for path in RECIPES.rglob("*.json"):
    recipe=json.loads(path.read_text())
    if isinstance(recipe,dict) and isinstance(recipe.get("result"),dict) and recipe["result"].get("id")=="italiansdelight:pasta_all_amatriciana":
        assert recipe["type"]=="farmersdelight:cooking", path
for locale in ("it_it","en_us"):
    labels=json.loads((ASSETS/"lang"/(locale+".json")).read_text())
    assert labels["pizza.italiansdelight.taste.principessa"]=="Principessa"
print("PASSED: cookie/salad/risotto silhouettes, golden pasta, Principessa name, Cooking Pot-only Amatriciana")
