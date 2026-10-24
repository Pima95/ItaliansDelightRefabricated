#!/usr/bin/env python3
"""Static data-pack audit for *all* Italian's Delight vanilla crafting recipes.

Runs on GitHub Actions before Gradle. This is NOT a substitute for actually
crafting the recipes in a Minecraft 26.2 client, nor does it simulate modded
custom serialisers. It verifies JSON structure, identifiers, categories and
the complete Olive Wood crafting family against confirmed vanilla/FD forms.
"""
from pathlib import Path
import json

BASE=Path("src/main/resources/data/italiansdelight/recipe")
CATEGORY={None,"building","equipment","misc","redstone"}

# Exact output counts and ingredients/patterns documented for Orchard 1.1.0.
# 17 grid-crafting recipes; stripping on Cutting Board is checked separately.
EXPECTED={
"olive_planks": (4, ["#italiansdelight:olive_logs"]),
"olive_wood": (3, ["LL","LL"], {"L":"italiansdelight:olive_log"}),
"stripped_olive_wood": (3, ["LL","LL"], {"L":"italiansdelight:stripped_olive_log"}),
"olive_stairs": (4, ["P  ","PP ","PPP"], {"P":"italiansdelight:olive_planks"}),
"olive_slab": (6, ["PPP"], {"P":"italiansdelight:olive_planks"}),
"olive_fence": (3, ["PSP","PSP"], {"P":"italiansdelight:olive_planks","S":"minecraft:stick"}),
"olive_fence_gate": (1, ["SPS","SPS"], {"P":"italiansdelight:olive_planks","S":"minecraft:stick"}),
"olive_button": (1, ["italiansdelight:olive_planks"]),
"olive_pressure_plate": (1, ["PP"], {"P":"italiansdelight:olive_planks"}),
"olive_door": (3, ["PP","PP","PP"], {"P":"italiansdelight:olive_planks"}),
"olive_trapdoor": (2, ["PPP","PPP"], {"P":"italiansdelight:olive_planks"}),
"olive_sign": (3, ["PPP","PPP"," S "], {"P":"italiansdelight:olive_planks","S":"minecraft:stick"}),
"olive_hanging_sign": (6, ["C C","PPP","PPP"], {"P":"italiansdelight:stripped_olive_log","C":"minecraft:iron_chain"}),
"olive_cabinet": (1, ["___","D D","___"], {"_":"italiansdelight:olive_slab","D":"italiansdelight:olive_trapdoor"}),
"olive_boat": (1, ["P P","PPP"], {"P":"italiansdelight:olive_planks"}),
"olive_chest_boat": (1, ["italiansdelight:olive_boat","minecraft:chest"]),
"crafting/olive_cheese_aging_rack": (1, ["S S"," B ","S S"], {"S":"italiansdelight:olive_fence","B":"italiansdelight:olive_planks"}),
}

def fail(path,reason):
    raise ValueError(f"{path}: {reason}")

def check(path,obj):
    kind=obj.get("type")
    if kind not in ("minecraft:crafting_shaped","minecraft:crafting_shapeless"):
        return False
    if obj.get("category") not in CATEGORY:
        fail(path, f"unrecognized Minecraft 26.2 crafting category: {obj.get('category')!r}")
    result=obj.get("result",{})
    if not isinstance(result,dict) or not isinstance(result.get("id"),str) or not result["id"]:
        fail(path, "crafting recipe must specify result.id")
    count=result.get("count",1)
    if not isinstance(count,int) or not 1<=count<=99:
        fail(path, "invalid result count")
    if kind=="minecraft:crafting_shaped":
        pattern=obj.get("pattern",[])
        key=obj.get("key",{})
        if not isinstance(pattern,list) or not 1<=len(pattern)<=3:
            fail(path,"pattern must have 1..3 rows")
        if not all(isinstance(row,str) and len(row)==len(pattern[0]) for row in pattern):
            fail(path,"pattern rows must have equal width")
        if not 1<=len(pattern[0])<=3:
            fail(path,"pattern width must be 1..3")
        if not isinstance(key,dict) or not key:
            fail(path,"shaped recipe must provide ingredient key")
        used={char for row in pattern for char in row if char!=" "}
        if used!=set(key):
            fail(path,f"pattern and key mismatch: {used} != {set(key)}")
        if "ingredients" in obj:
            fail(path,"shaped recipe must use key, not ingredients")
    else:
        ingredients=obj.get("ingredients")
        if not isinstance(ingredients,list) or not 1<=len(ingredients)<=9:
            fail(path,"shapeless ingredients must be a list of 1..9 values")
        if "pattern" in obj:
            fail(path,"shapeless recipe cannot have pattern")
    return True

def main():
    count=0
    files=list(BASE.rglob("*.json"))
    if len(files)<75:
        raise ValueError(f"Unexpectedly few recipe files: {len(files)}")
    for path in files:
        obj=json.loads(path.read_text(encoding="utf-8"))
        if check(path,obj):
            count+=1
    for rel,expected in EXPECTED.items():
        path=BASE/(rel+".json")
        data=json.loads(path.read_text(encoding="utf-8"))
        if not check(path,data):
            fail(path,"expected vanilla grid crafting recipe")
        exp_count=expected[0]
        if data["result"]["id"]!="italiansdelight:"+Path(rel).name:
            fail(path,"wrong Olive Wood result item")
        if data["result"].get("count",1)!=exp_count:
            fail(path,f"expected {exp_count} outputs")
        if len(expected)==2:
            if data["type"]!="minecraft:crafting_shapeless" or data["ingredients"]!=expected[1]:
                fail(path,"wrong shapeless ingredients")
        else:
            if data["type"]!="minecraft:crafting_shaped" or data["pattern"]!=expected[1] or data["key"]!=expected[2]:
                fail(path,"wrong 26.2 vanilla/FD pattern or item ID")

    # Native Cutting Board handling remains separate from grid-crafting.
    for wood in ("olive_log","olive_wood"):
        data=json.loads((BASE/"cutting"/f"{wood}.json").read_text(encoding="utf-8"))
        assert data["type"]=="farmersdelight:cutting"
        assert data["ingredients"]==[f"italiansdelight:{wood}"]
        assert data["tool"]=={"fabric:type":"farmersdelight:item_ability","action":"axe_strip"}
        assert any(it.get("item",{}).get("id")=="farmersdelight:tree_bark" for it in data["result"])

    print(f"OK: all {count} standard Italian's Delight grid-crafting JSON recipes parsed and structurally validated")
    print("OK: complete 17-recipe Olive Wood grid-crafting family matches Minecraft 26.2/FD")
    print("OK: two FD Cutting Board stripping recipes return tree bark")
    print("NOTE: runtime crafting and JEI require manual in-game tests")

if __name__=="__main__":
    main()
