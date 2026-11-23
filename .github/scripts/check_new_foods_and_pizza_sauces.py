#!/usr/bin/env python3
"""Static regression for 1.2.0 finished foods and sauce chronology."""
import json
from pathlib import Path
root=Path("src/main/resources")
assets=root/"assets/italiansdelight"
recipes=root/"data/italiansdelight/recipe"
source=Path("src/main/java/dev/italiansdelight")
ids=("spicy_olive_pasta", "pasta_with_ricotta_and_pancetta",
     "risotto_with_scamorza_and_olives", "pasta_with_almond_pesto_and_speck",
     "grape_cherry_fruit_salad", "almond_biscuits")

def get(path):
    return json.loads(path.read_text(encoding="utf-8"))

for name in ids:
    image=(assets/"textures/item"/(name+".png")).read_bytes()
    assert image[:8]==bytes.fromhex("89504e470d0a1a0a"), name
    assert image[16:24]==(16).to_bytes(4,"big")*2, name
    assert get(assets/"items"/(name+".json"))["model"]["model"]=="italiansdelight:item/"+name
    assert get(assets/"models/item"/(name+".json"))["textures"]["layer0"]=="italiansdelight:item/"+name
    for lang in ("it_it","en_us"):
        assert "item.italiansdelight."+name in get(assets/"lang"/(lang+".json"))
    assert "italiansdelight:"+name in get(root/"data/c/tags/item/foods.json")["values"]

expected_cooking={
    "spicy_olive_pasta":["#c:foods/pasta","farmersdelight:tomato_sauce",
                          "italiansdelight:chili_pepper","italiansdelight:olive_pitted",
                          "italiansdelight:olive_pitted"],
    "pasta_with_ricotta_and_pancetta":["#c:foods/pasta","italiansdelight:ricotta",
                                        "italiansdelight:pancetta_slice",
                                        "italiansdelight:grated_parmigiano_reggiano"],
    "risotto_with_scamorza_and_olives":["#c:crops/rice","italiansdelight:scamorza_slice",
                                        "italiansdelight:olive_pitted","italiansdelight:olive_pitted",
                                        "italiansdelight:cream_bowl"],
    "pasta_with_almond_pesto_and_speck":["#c:foods/pasta","italiansdelight:almond_pesto",
                                          "italiansdelight:speck_slice",
                                          "italiansdelight:chopped_almonds"]
}
for name,ingredients in expected_cooking.items():
    r=get(recipes/"cooking"/(name+".json"))
    assert r["type"]=="farmersdelight:cooking"
    assert r["ingredients"]==ingredients,name
    assert r["container"]=={"id":"minecraft:bowl","count":1}
    assert r["result"]["id"]=="italiansdelight:"+name
fruit=get(recipes/"grape_cherry_fruit_salad.json")
assert fruit["type"]=="minecraft:crafting_shapeless"
assert fruit["ingredients"]==["minecraft:bowl","italiansdelight:grapes",
                              "italiansdelight:white_grapes","italiansdelight:cherries",
                              "italiansdelight:cherries"]
assert fruit["result"]["count"]==1
cookies=get(recipes/"almond_biscuits.json")
assert cookies["type"]=="minecraft:crafting_shapeless"
assert cookies["result"]=={"id":"italiansdelight:almond_biscuits","count":4}
assert cookies["ingredients"].count("italiansdelight:chopped_almonds")==2
assert not any("focaccia" in p.name for p in recipes.rglob("*.json"))

items=(source/"common/registry/ModItems.java").read_text(encoding="utf-8")
group=(source/"common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
for field in ("SPICY_OLIVE_PASTA","PASTA_WITH_RICOTTA_AND_PANCETTA",
              "RISOTTO_WITH_SCAMORZA_AND_OLIVES","PASTA_WITH_ALMOND_PESTO_AND_SPECK",
              "GRAPE_CHERRY_FRUIT_SALAD","ALMOND_BISCUITS"):
    assert "public static final Item "+field in items, field
    assert "output.accept(ModItems."+field+");" in group, field
assert 'registerBowlFoodItem("grape_cherry_fruit_salad", 6, 0.35f' in items

cat=(source/"common/pizza/PizzaToppingCatalog.java").read_text(encoding="utf-8")
assert 'add(types, nativeItems, "chili_pepper", VEGETABLE, SOLID, "italiansdelight", "chili_pepper");' in cat
assert "types.size() != 34 || nativeItems.size() != 38" in cat
names=(source/"common/pizza/PizzaDisplayNames.java").read_text(encoding="utf-8")
assert 'add(presets, "piccante_olive", "tomato_sauce", "mozzarella_slice",' in names
assert '"olive_pitted", "chili_pepper"' in names
assert 'if (presets.size() != 23)' in names
assert 'add(presets, "principessa", "tomato_sauce", "mozzarella_slice",' in names
assert '"prosciutto_crudo_slice", "cream_bowl"' in names
ops=(source/"common/pizza/PizzaCompositionOperations.java").read_text(encoding="utf-8")
assert "existing.occupiesSauceSlot()" not in ops
assert "uncovered &= ~overlapping;" in ops
assert "MAX_TOPPINGS_PER_QUARTER" in ops
comp=(source/"common/pizza/PizzaComposition.java").read_text(encoding="utf-8")
assert "sauceCount" not in comp
assert "distinctTypes.size() > MAX_TOPPINGS_PER_QUARTER" in comp
vis=(source/"client/render/PizzaToppingVisuals.java").read_text(encoding="utf-8")
for marker in ("baseMasks[i] = covered & ~claimedBase;", "claimedBase |= covered;",
               "int garnish = covered & ~base;", "SAUCE_PREFIX : SPRITE_PREFIX"):
    assert marker in vis,marker

for name in ("raw_pizza_piccante_olive","cooked_pizza_piccante_olive","pizza_slice_piccante_olive",
             "raw_pizza_principessa","cooked_pizza_principessa","pizza_slice_principessa"):
    for folder in ("items","models/item"):
        assert (assets/folder/(name+".json")).is_file(), name
    assert (assets/"textures/item"/(name+".png")).is_file(), name
for locale in ("it_it","en_us"):
    data=get(assets/"lang"/(locale+".json"))
    assert "pizza.italiansdelight.taste.piccante_olive" in data
    assert "pizza.italiansdelight.ingredient.chili_pepper" in data
assert (assets/"textures/block/pizza_topping_sprites/chili_pepper.png").is_file()
print("PASSED: six dishes + one recognized pizza, no focaccia, sauce reordering and legacy preservation")

# Only the Cooking Pot recipe may produce normal Pasta all'Amatriciana.
amatriciana=recipes/"cooking/pasta_all_amatriciana.json"
assert amatriciana.exists()
assert get(amatriciana)["type"]=="farmersdelight:cooking"
assert not (recipes/"fermented/red_wine_amatriciana.json").exists()
for recipe in recipes.rglob("*.json"):
    obj=get(recipe)
    if isinstance(obj,dict) and isinstance(obj.get("result"),dict) and obj["result"].get("id")=="italiansdelight:pasta_all_amatriciana":
        assert obj.get("type")=="farmersdelight:cooking", recipe
