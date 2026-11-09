"""Worldgen 26.2 regression: configured trees, placed features and biome injection.
Runs as a fast static check before Gradle. Minecraft in-game chunk generation
still needs manual validation of density/clearance and final appearance.
"""
from pathlib import Path
import json, re

ROOT=Path(__file__).resolve().parents[2]
JAVA=ROOT/"src/main/java/dev/italiansdelight"
DATA=ROOT/"src/main/resources/data/italiansdelight/worldgen"
wg=(JAVA/"common/registry/ModWorldGeneration.java").read_text()
init=(JAVA/"ItaliansDelight.java").read_text()
reg=(JAVA/"common/worldgen/ModTreePlacers.java").read_text()
assert "ModTreePlacers.register();" in init
assert "ModWorldGeneration.register();" in init
assert wg.count("GenerationStep.Decoration.VEGETAL_DECORATION") >= 6

# Initial rarity values are per eligible chunk BEFORE clearance tests.
specs={
    "olive_tree":("OLIVE_TREE_WILD","olive_sapling",6,{"PLAINS","SUNFLOWER_PLAINS","SAVANNA","SAVANNA_PLATEAU","WINDSWEPT_SAVANNA"}),
    "ancient_olive_tree":("ANCIENT_OLIVE_TREE_WILD","olive_sapling",96,{"PLAINS","SUNFLOWER_PLAINS","SAVANNA","SAVANNA_PLATEAU","WINDSWEPT_SAVANNA"}),
    "almond_tree":("ALMOND_TREE_WILD","almond_sapling",8,{"PLAINS","SUNFLOWER_PLAINS","MEADOW","SAVANNA","SAVANNA_PLATEAU"}),
    "fruit_cherry_tree":("FRUIT_CHERRY_TREE_WILD","fruit_cherry_sapling",7,{"FOREST","FLOWER_FOREST","CHERRY_GROVE"})
}
for name,(const,sapling,chance,biomes) in specs.items():
    configured=json.loads((DATA/f"configured_feature/{name}.json").read_text())
    placed=json.loads((DATA/f"placed_feature/{name}_wild.json").read_text())
    assert configured["type"]=="minecraft:tree"
    assert placed["feature"]==f"italiansdelight:{name}"
    mods=placed["placement"]
    assert [m["type"] for m in mods]==[
        "minecraft:rarity_filter","minecraft:in_square",
        "minecraft:surface_water_depth_filter","minecraft:heightmap",
        "minecraft:block_predicate_filter","minecraft:biome"
    ],name
    assert mods[0]["chance"]==chance
    assert mods[2]["max_water_depth"]==0
    assert mods[3]["heightmap"]=="OCEAN_FLOOR"
    assert mods[4]["predicate"]=={
        "type":"minecraft:would_survive",
        "state":{"Name":f"italiansdelight:{sapling}","Properties":{"stage":"0"}}
    }
    constBlock=wg.index(f"private static final ResourceKey<PlacedFeature> {const} =")
    assert constBlock!=-1 and f'ModRegistries.id("{name}_wild")' in wg[constBlock:constBlock+180]
    match=re.search(
        r"BiomeModifications\.addFeature\(\s*BiomeSelectors\.includeByKey\(([^()]*)\),"
        r"\s*GenerationStep\.Decoration\.VEGETAL_DECORATION,\s*"+const+r"\s*\)",
        wg,re.S
    )
    assert match is not None,f"{name} not injected into biome vegetation"
    found=set(re.findall(r"Biomes\.([A-Z_]+)",match.group(1)))
    assert found==biomes,(name,found,biomes)
    leaf=configured["config"]["foliage_provider"]["state"]
    assert leaf["Properties"]["persistent"]=="false",name
    for key in ("trunk_placer","foliage_placer"):
        typ=configured["config"][key]["type"].split(":",1)[1]
        assert f'ModRegistries.id("{typ}")' in reg,(name,typ)
assert specs["ancient_olive_tree"][2]>=10*specs["olive_tree"][2]
assert "OLIVE_TREE_WILD" in wg and "ANCIENT_OLIVE_TREE_WILD" in wg
# Never generate ornamental vanilla pink cherry via the fruit-cherry entry.
assert json.loads((DATA/"configured_feature/fruit_cherry_tree.json").read_text())[
    "config"]["trunk_provider"]["state"]["Name"]=="italiansdelight:fruit_cherry_log"
print("OK: four orchard wild placements, eligible biomes, soil/water filters and rare ancient 2x2")
print("NOTE: existing chunks never retroactively acquire natural trees")
