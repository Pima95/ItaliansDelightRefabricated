#!/usr/bin/env python3
"""JEI 26.2 native Information tabs for every added plant family.

Video reference: Farmer's Delight Tomato/Tomato Shrub single "Information"
page with a rotating group icon and scrollable text. We use JEI's built-in
IRecipeRegistration.addItemStackInfo rather than custom category graphics.
"""
from pathlib import Path
import json
import re

R=Path("src/main/resources")
plugin=Path("src/main/java/dev/italiansdelight/integration/jei/ItaliansDelightJeiPlugin.java").read_text(encoding="utf-8")
source=Path("src/main/java/dev/italiansdelight/integration/jei/OrchardPlantJeiInfo.java").read_text(encoding="utf-8")
blocks=Path("src/main/java/dev/italiansdelight/common/registry/ModBlocks.java").read_text(encoding="utf-8")
items=Path("src/main/java/dev/italiansdelight/common/registry/ModItems.java").read_text(encoding="utf-8")

assert 'OrchardPlantJeiInfo.register(registration);' in plugin
assert plugin.index('OrchardPlantJeiInfo.register(registration);') > plugin.index("public void registerRecipes(")
assert 'mezz.jei.api.registration.IRecipeRegistration' in source
assert 'mezz.jei.api.registration.IRecipeRegistration' in plugin
assert 'net.minecraft.network.chat.Component' in source
assert "registration.addItemStackInfo(" in source
assert "new ItemStack(" in source
assert "registerCategories(" not in source, "Do not create redundant custom JEI category"
assert "registerExtraIngredients(" not in source, "Normal registered plants already appear in JEI"
assert "CrushingTub" not in source and "crushing_tub" not in source
assert "olive_oil" not in source and "olive_oil" not in plugin

pattern=re.compile(
    r'registration\.addItemStackInfo\(\s*List\.of\((.*?)\)\s*,\s*'
    r'Component\.translatable\("([^"]+)"\)\s*\);', re.S
)
entries=pattern.findall(source)
assert len(entries)==5,(len(entries),[key for _,key in entries])

expected={
    "olive":{("Items","OLIVES"),("Blocks","OLIVE_SAPLING"),("Blocks","OLIVE_LEAVES")},
    "almond":{("Items","ALMONDS"),("Blocks","ALMOND_SAPLING"),("Blocks","ALMOND_LEAVES")},
    "fruit_cherry":{("Items","CHERRIES"),("Blocks","FRUIT_CHERRY_SAPLING"),("Blocks","FRUIT_CHERRY_LEAVES")},
    "basil":{("Blocks","BASIL"),("Blocks","WILD_BASIL")},
    "cardoon":{("Blocks","CULTIVATED_CARDOON"),("Blocks","CARDOON")}
}
seen=set()
for content,key in entries:
    assert key.startswith("jei.italiansdelight.plants."),key
    plant=key.removeprefix("jei.italiansdelight.plants.")
    assert plant in expected and plant not in seen, key
    seen.add(plant)
    symbols=re.findall(r'new ItemStack\(Mod(Items|Blocks)\.([A-Z_]+)\)',content)
    assert len(symbols)==len(expected[plant]) and set(symbols)==expected[plant], (plant,symbols)
    for kind,symbol in symbols:
        body=items if kind=="Items" else blocks
        assert re.search(r'public static final (Item|Block) '+symbol+r'\s*=',body),(kind,symbol)

    for locale in ("en_us","it_it"):
        lang=json.loads((R/f"assets/italiansdelight/lang/{locale}.json").read_text(encoding="utf-8"))
        txt=lang[key]
        assert isinstance(txt,str) and len(txt)>150,(locale,plant)
        assert "\n\n" in txt,(locale,plant,"must have paragraphs and natural JEI scroll")
        if plant in {"olive", "almond", "fruit_cherry"}:
            assert "65%" in txt, (locale, plant)
        elif locale=="it_it":
            assert "farina d'ossa" in txt.lower(), (locale, plant)
        else:
            assert "bone meal" in txt.lower(), (locale, plant)
        assert "Serene Seasons" in txt,(locale,plant)
        assert "Crushing Tub" not in txt and "crushing tub" not in txt.lower(),(locale,plant)

assert seen==set(expected)
assert source.count("registration.addItemStackInfo(")==5
assert len(set(expected))==5

# No new fake recipes or worldgen mechanics. One entry links the natural/
# cultivated forms, another links fruit / sapling / leaf stacks per tree.
assert not (R/"data/italiansdelight/recipe/olive_oil.json").exists()
print("OK: five native JEI Information pages, matching Farmer's Delight video behavior")
print("OK: 13 existing registered plant items covered, with no duplicate groups")
print("OK: localized English/Italian text, orchard and Serene Seasons mechanics grounded")
print("OK: JEI vanilla navigation and scroll; no custom GUI or crushing recipes")
print("NOTE: visually verify the blue info tab and rotating icon in Minecraft")
