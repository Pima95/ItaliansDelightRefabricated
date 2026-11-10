"""Orchard almond recipes, assets and new canonical pizza regression."""
from pathlib import Path
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
R=ROOT/"data/italiansdelight/recipe"
JAVA=Path("src/main/java/dev/italiansdelight")
read=lambda p:json.loads(p.read_text(encoding="utf-8"))

for name in ("chopped_almonds","basil_pesto","almond_pesto","almond_cake",
             "almond_cake_slice","pasta_with_pesto","pasta_with_almond_pesto"):
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
basil=read(R/"basil_pesto.json")
assert basil["type"]=="minecraft:crafting_shapeless"
assert basil["ingredients"].count("italiansdelight:basil")==3
assert "italiansdelight:chopped_almonds" not in basil["ingredients"]
assert {"minecraft:bowl","italiansdelight:salt","italiansdelight:olive_oil_bottle",
        "italiansdelight:grated_parmigiano_reggiano"}<=set(basil["ingredients"])
assert basil["result"]["id"]=="italiansdelight:basil_pesto"
for name,pesto in (("pasta_with_pesto","basil_pesto"),
                   ("pasta_with_almond_pesto","almond_pesto")):
    cooking=read(R/f"cooking/{name}.json")
    assert cooking["type"]=="farmersdelight:cooking"
    assert cooking["ingredients"]==["#c:foods/pasta",f"italiansdelight:{pesto}"]
    assert cooking["container"]=={"id":"minecraft:bowl","count":1}
    assert cooking["result"]=={"id":f"italiansdelight:{name}"}
cake=read(R/"almond_cake.json")
assert cake["type"] == "minecraft:crafting_shaped"
assert cake["pattern"] == ["AAA","AAA","MPM"]
assert cake["key"] == {"A":"italiansdelight:almonds",
                       "M":"#c:drinks/milk",
                       "P":"farmersdelight:pie_crust"}
almond_from_slices=read(R/"almond_cake_from_slices.json")
assert almond_from_slices["type"] == "minecraft:crafting_shaped"
assert almond_from_slices["pattern"] == ["SS","SS"]
assert almond_from_slices["key"] == {"S":"italiansdelight:almond_cake_slice"}
assert almond_from_slices["result"] == {"id":"italiansdelight:almond_cake","count":1}
slices=read(R/"cutting/almond_cake_slice.json")
assert slices["type"]=="farmersdelight:cutting"
assert slices["ingredients"]==["italiansdelight:almond_cake"]
assert slices["tool"]=="#c:tools/knife"
assert slices["result"]==[{"item":{"id":"italiansdelight:almond_cake_slice","count":4}}]
items=(JAVA/"common/registry/ModItems.java").read_text()
group=(JAVA/"common/item/group/ModItemGroups.java").read_text()
for symbol in ("CHOPPED_ALMONDS","BASIL_PESTO","ALMOND_PESTO",
               "ALMOND_CAKE_SLICE","PASTA_WITH_PESTO","PASTA_WITH_ALMOND_PESTO"):
    assert f"public static final Item {symbol}" in items
    assert group.count(f"output.accept(ModItems.{symbol});")==1
blocks=(JAVA/"common/registry/ModBlocks.java").read_text()
cake_class=(JAVA/"common/block/AlmondCakeBlock.java").read_text()
assert 'public static final Block ALMOND_CAKE = register(' in blocks
assert 'new AlmondCakeBlock(' in blocks
assert 'registerSimpleItem("almond_cake"' not in items
assert group.count("output.accept(ModBlocks.ALMOND_CAKE);")==1
assert 'IntegerProperty.create("bites", 0, 3)' in cake_class
for token in ("useWithoutItem(", "useItemOn(", "held.is(KNIVES)",
              "player.getFoodData().eat(4, 0.5f)", "Block.popResource(",
              "return List.of(new ItemStack(ModItems.ALMOND_CAKE_SLICE, remaining));"):
    assert token in cake_class, token
state=read(A/"blockstates/almond_cake.json")
assert len(state["variants"])==16
for n in ("almond_cake","almond_cake_slice1","almond_cake_slice2","almond_cake_slice3"):
    model=read(A/f"models/block/{n}.json")
    assert model["parent"].startswith("italiansdelight:block/template_almond_cake")
    assert set(model["textures"])=={"bottom","top","side","inner"}
    template=read(A/f"models/block/template_{n}.json")
    assert template["elements"] and template["textures"]["particle"]=="#top"
for n in ("almond_cake_top","almond_cake_inner","almond_cake_side","almond_cake_bottom"):
    check_rgba_pixel_art(A/f"textures/block/{n}.png",(16,16))
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
    for name in ("chopped_almonds","basil_pesto","almond_pesto","almond_cake",
                 "almond_cake_slice","pasta_with_pesto","pasta_with_almond_pesto"):
        assert loc[f"item.italiansdelight.{name}"]
    assert loc["pizza.italiansdelight.taste.mortadella_ricotta_mandorle"]
    assert loc["pizza.italiansdelight.ingredient.chopped_almonds"]
assert not (R/"focaccia.json").exists()
print("OK: 7 pesto and almond food models, two distinct pastas, binary alpha")
print("OK: Cutting Board, two FD Cooking Pot dishes and 4 edible cake slices")
print("OK: placeable cake has four portions, knife interaction and remaining-slice drops")
print("OK: 21 named pizza presets with chopped almond sprite and no uncut nut topping")


# Cherry jam, placeable tart, and toast: resource/recipe regression checks.
for name in ("cherry_jam_bottle", "cherry_jam_toast", "cherry_tart", "cherry_tart_slice"):
    assert read(A / f"items/{name}.json")["model"]["model"] == f"italiansdelight:item/{name}"
    assert read(A / f"models/item/{name}.json")["textures"]["layer0"] == f"italiansdelight:item/{name}"
    check_rgba_pixel_art(A / f"textures/item/{name}.png", (16, 16))
for suffix in ("top", "bottom", "side", "inner"):
    check_rgba_pixel_art(A / f"textures/block/cherry_tart_{suffix}.png", (16, 16))
jam = read(R / "cooking/cherry_jam_bottle.json")
assert jam["type"] == "farmersdelight:cooking"
assert jam["ingredients"].count("italiansdelight:cherries") == 3
assert jam["ingredients"].count("minecraft:sugar") == 1
assert jam["container"] == {"id": "minecraft:glass_bottle", "count": 1}
assert jam["result"] == {"id": "italiansdelight:cherry_jam_bottle"}
toast = read(R / "cherry_jam_toast.json")
assert toast["type"] == "minecraft:crafting_shapeless"
assert toast["ingredients"] == ["minecraft:bread", "italiansdelight:cherry_jam_bottle"]
tart = read(R / "cherry_tart.json")
assert tart["type"] == "minecraft:crafting_shaped"
assert tart["pattern"] == ["CCC","CCC","JPJ"]
assert tart["key"] == {"C":"italiansdelight:cherries",
                       "J":"italiansdelight:cherry_jam_bottle",
                       "P":"farmersdelight:pie_crust"}
cherry_from_slices=read(R/"cherry_tart_from_slices.json")
assert cherry_from_slices["type"] == "minecraft:crafting_shaped"
assert cherry_from_slices["pattern"] == ["SS","SS"]
assert cherry_from_slices["key"] == {"S":"italiansdelight:cherry_tart_slice"}
assert cherry_from_slices["result"] == {"id":"italiansdelight:cherry_tart","count":1}
slices = read(R / "cutting/cherry_tart_slice.json")
assert slices["tool"] == "#c:tools/knife"
assert slices["result"] == [{"item": {"id": "italiansdelight:cherry_tart_slice", "count": 4}}]
assert len(read(A / "blockstates/cherry_tart.json")["variants"]) == 16
for suf in ("", "_slice1", "_slice2", "_slice3"):
    model = read(A / f"models/block/cherry_tart{suf}.json")
    assert model["parent"] == f"italiansdelight:block/template_almond_cake{suf}"
    assert set(model["textures"]) == {"top", "bottom", "side", "inner"}
for n in ("CHERRY_JAM_BOTTLE", "CHERRY_JAM_TOAST", "CHERRY_TART_SLICE"):
    assert f"public static final Item {n}" in items
    assert group.count(f"output.accept(ModItems.{n});") == 1
assert group.count("output.accept(ModBlocks.CHERRY_TART);") == 1
cherry_class = (JAVA / "common/block/CherryTartBlock.java").read_text()
assert "IntegerProperty.create(\"bites\", 0, 3)" in cherry_class
assert "ModItems.CHERRY_TART_SLICE" in cherry_class
assert "ModBlocks.CHERRY_TART" in cherry_class
assert "new CherryTartBlock(" in blocks
assert "registerSimpleItem(\"cherry_tart\"" not in items
for loc in ("it_it", "en_us"):
    l = read(A / f"lang/{loc}.json")
    assert all(l[k] for k in (
        "item.italiansdelight.cherry_jam_bottle",
        "item.italiansdelight.cherry_jam_toast",
        "block.italiansdelight.cherry_tart",
        "item.italiansdelight.cherry_tart_slice"))
assert read(A / "lang/it_it.json")["item.italiansdelight.cherry_jam_bottle"] == "Marmellata di Ciliegie"
print("OK: cherry jam bottle, tart with four portions, jam toast, all binary-alpha PNGs")

# Art invariants: a smaller tomato sprite and a squat cherry jam jar.
# Use the standard-library PNG decoder shared with the rest of the CI
# (GitHub's minimal Python environment intentionally has no Pillow).
tomato_pixels=check_rgba_pixel_art(A / "textures/item/tomato_slice.png", (16,16))
tomato_coords=[(x,y) for y,row in enumerate(tomato_pixels)
               for x,pixel in enumerate(row) if pixel[3] == 255]
assert tomato_coords
assert max(x for x,y in tomato_coords)-min(x for x,y in tomato_coords)+1 <= 10
assert max(y for x,y in tomato_coords)-min(y for x,y in tomato_coords)+1 <= 10
jar_pixels=check_rgba_pixel_art(A / "textures/item/cherry_jam_bottle.png", (16,16))
jar_coords=[(x,y) for y,row in enumerate(jar_pixels)
            for x,pixel in enumerate(row) if pixel[3] == 255]
assert jar_coords and max(x for x,y in jar_coords)-min(x for x,y in jar_coords)+1 >= 8
assert all(px[3] in (0,255) for row in tomato_pixels for px in row)
assert all(px[3] in (0,255) for row in jar_pixels for px in row)
print("OK: tomato slice is reduced; jar/tart textures remain 16x16 binary alpha")

# The placed tart uses 12x12 UV faces: avoid transparent holes and keep
# its exterior single-layered (jam is visible only on top and cut faces).
tart_top=check_rgba_pixel_art(A / "textures/block/cherry_tart_top.png", (16,16))
tart_bottom=check_rgba_pixel_art(A / "textures/block/cherry_tart_bottom.png", (16,16))
tart_side=check_rgba_pixel_art(A / "textures/block/cherry_tart_side.png", (16,16))
assert all(tart_top[y][x][3] == 255 and tart_bottom[y][x][3] == 255
           for y in range(2,14) for x in range(2,14))
assert all(tart_side[y][x][3] == 255 for y in range(12,16) for x in range(2,14))
assert all(tart_side[y][x][1] > tart_side[y][x][2]
           for y in range(12,16) for x in range(2,14)), "Outer crust should not show red jam layers"
print("OK: the tart fills its 12x12 block faces and its outside is pastry only")

# The user-approved square geometry is non-negotiable for the placed tart:
# top and bottom cover exactly the unrounded 12x12 world-model UV footprint.
for name,pixels in (("top",tart_top),("bottom",tart_bottom)):
    occupied=[(x,y) for y,row in enumerate(pixels) for x,px in enumerate(row) if px[3]==255]
    assert occupied, f"Cherry tart {name} texture is empty"
    assert (min(x for x,y in occupied),min(y for x,y in occupied),
            max(x for x,y in occupied)+1,max(y for x,y in occupied)+1)==(2,2,14,14), (
            f"Cherry tart {name} must have square corners and no rounded silhouette")
# The jar label must not obscure the filling: its entire center panel is red jam.
for y in range(7,12):
    for x in range(5,11):
        r,g,b,a=jar_pixels[y][x]
        assert a==255 and r>g*1.6 and r>b*1.1, (
            "Cherry jam must be visible through the jar without a paper label")
print("OK: squared tart faces and unobstructed red jam in the label-free jar")

# Use crossed diagonal strips on the placed tart, not a right-angle grid.
def is_pastry(px):
    return px[3] == 255 and px[0] >= 145 and px[1] >= 100 and px[2] < 135
for dx in (1, -1):
    runs = 0
    for y in range(4, 12):
        for x in range(4, 12):
            segment = [(x + i*dx, y + i) for i in range(4)]
            if all(4 <= xx < 12 and 4 <= yy < 12 and is_pastry(tart_top[yy][xx])
                   for xx, yy in segment):
                runs += 1
    assert runs >= 2, "Cherry tart must feature diagonal pastry in both directions"
# White, wide plastic cap like a classic hazelnut spread jar; blue clear glass.
assert all(all(component >= 185 for component in jar_pixels[2][x][:3])
           for x in range(3, 13)), "Jam jar must have a full-width white cap"
assert all(jar_pixels[7][x][2] > jar_pixels[7][x][0]
           for x in (3, 4, 11, 12)), "Jam jar glass border must be blue"
print("OK: diagonal cherry lattice and reference-style, wide white jar cap")
