"""Orchard almond recipes, assets and new canonical pizza regression."""
from pathlib import Path
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
R=ROOT/"data/italiansdelight/recipe"
JAVA=Path("src/main/java/dev/italiansdelight")
read=lambda p:json.loads(p.read_text(encoding="utf-8"))

for name in ("chopped_almonds","almond_pesto","almond_cake","almond_cake_slice","pasta_with_pesto"):
    assert read(A/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
    assert read(A/f"models/item/{name}.json")["textures"]["layer0"]==f"italiansdelight:item/{name}"
    pixels=check_rgba_pixel_art(A/f"textures/item/{name}.png",(16,16))
    assert all(px[3] in (0,255) for row in pixels for px in row)
for prefix in ("raw_pizza","cooked_pizza","pizza_slice"):
    id=f"{prefix}_mortadella_ricotta_mandorle"
    assert read(A/f"items/{id}.json")["model"]["model"]==f"italiansdelight:item/{id}"
    pixels=check_rgba_pixel_art(A/f"textures/item/{id}.png",(16,16))
    assert any(p[3]==255 for row in pixels for p in row)
sprites=check_rgba_pixel_art(A/"textures/block/pizza_topping_sprites/chopped_almonds.png",(12,12))
assert any(p[3] for row in sprites for p in row)
cut=read(R/"cutting/chopped_almonds.json")
assert cut["type"]=="farmersdelight:cutting"
assert cut["ingredients"]==["italiansdelight:almonds"]
assert cut["tool"]=="#c:tools/knife"
assert cut["result"]==[{"item":{"id":"italiansdelight:chopped_almonds","count":2}}]
pesto=read(R/"almond_pesto.json")
assert pesto["type"]=="minecraft:crafting_shapeless" and pesto["category"]=="misc"
assert pesto["ingredients"].count("italiansdelight:chopped_almonds")==2
assert pesto["ingredients"].count("italiansdelight:basil")==2
assert {"minecraft:bowl","italiansdelight:olive_oil_bottle",
        "italiansdelight:grated_parmigiano_reggiano"}<=set(pesto["ingredients"])
assert pesto["result"]["id"]=="italiansdelight:almond_pesto"
cooking=read(R/"cooking/pasta_with_pesto.json")
assert cooking["type"]=="farmersdelight:cooking"
assert cooking["ingredients"]==["#c:foods/pasta","italiansdelight:almond_pesto"]
assert cooking["container"]=={"id":"minecraft:bowl","count":1}
assert cooking["result"]=={"id":"italiansdelight:pasta_with_pesto"}
cake=read(R/"almond_cake.json")
assert cake["pattern"]==["AAA","SMS","WEW"]
assert cake["key"]=={"A":"italiansdelight:almonds","S":"minecraft:sugar",
                     "M":"minecraft:milk_bucket","W":"minecraft:wheat","E":"minecraft:egg"}
slices=read(R/"cutting/almond_cake_slice.json")
assert slices["type"]=="farmersdelight:cutting"
assert slices["ingredients"]==["italiansdelight:almond_cake"]
assert slices["tool"]=="#c:tools/knife"
assert slices["result"]==[{"item":{"id":"italiansdelight:almond_cake_slice","count":4}}]
items=(JAVA/"common/registry/ModItems.java").read_text()
group=(JAVA/"common/item/group/ModItemGroups.java").read_text()
for symbol in ("CHOPPED_ALMONDS","ALMOND_PESTO","ALMOND_CAKE","ALMOND_CAKE_SLICE","PASTA_WITH_PESTO"):
    assert f"public static final Item {symbol}" in items
    assert group.count(f"output.accept(ModItems.{symbol});")==1
assert 'new Item.Properties().craftRemainder(Items.BOWL).stacksTo(16)' in items
assert 'registerBowlFoodItem("pasta_with_pesto", 10, 0.7f,' in items
catalog=(JAVA/"common/pizza/PizzaToppingCatalog.java").read_text()
assert '"chopped_almonds", OILS_FATS, SOLID, "italiansdelight", "chopped_almonds"' in catalog
assert "types.size() != 33 || nativeItems.size() != 37" in catalog
for id in ("almonds","almond_pesto","almond_cake"):
    assert f'Identifier.parse("italiansdelight:{id}")' in catalog
pizza=(JAVA/"common/pizza/PizzaDisplayNames.java").read_text()
assert 'add(presets, "mortadella_ricotta_mandorle",' in pizza
assert '"ricotta", "mortadella_slice", "chopped_almonds"' in pizza
assert "presets.size() != 21" in pizza
for locale in ("it_it","en_us"):
    loc=read(A/f"lang/{locale}.json")
    for name in ("chopped_almonds","almond_pesto","almond_cake",
                 "almond_cake_slice","pasta_with_pesto"):
        assert loc[f"item.italiansdelight.{name}"]
    assert loc["pizza.italiansdelight.taste.mortadella_ricotta_mandorle"]
    assert loc["pizza.italiansdelight.ingredient.chopped_almonds"]
assert not (R/"focaccia.json").exists()
print("OK: 5 almond food models and original pasta sprite, binary alpha")
print("OK: Cutting Board, crafting, FD Cooking Pot and 4 edible cake slices")
print("OK: 21 named pizza presets with chopped almond sprite and no uncut nut topping")
