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
assert wg.count("GenerationStep.Decoration.VEGETAL_DECORATION") >= 7

# Probabilities are per eligible chunk BEFORE terrain-clearance checks.
# Vanilla 26.2 trees_plains: weighted 0*19 + 1*1 -> 1/20 attempt/chunk.
# Two independent 1/40 species sum to the same EXPECTED attempts, not
# precisely the same per-chunk distribution (a rare chunk can roll both).
# Vanilla 26.2 trees_meadow: rarity_filter chance=100.
specs={
    "olive_tree":("OLIVE_TREE_WILD","olive_sapling",40,{"PLAINS","SUNFLOWER_PLAINS","SAVANNA","SAVANNA_PLATEAU","WINDSWEPT_SAVANNA"}),
    "ancient_olive_tree":("ANCIENT_OLIVE_TREE_WILD","olive_sapling",96,{"PLAINS","SUNFLOWER_PLAINS","SAVANNA","SAVANNA_PLATEAU","WINDSWEPT_SAVANNA"}),
    "almond_tree":("ALMOND_TREE_WILD","almond_sapling",40,{"PLAINS","SUNFLOWER_PLAINS","SAVANNA","SAVANNA_PLATEAU"}),
    "almond_tree_meadow":("ALMOND_TREE_MEADOW_WILD","almond_sapling",100,{"MEADOW"}),
    "fruit_cherry_tree":("FRUIT_CHERRY_TREE_WILD","fruit_cherry_sapling",7,{"FOREST","FLOWER_FOREST","CHERRY_GROVE"})
}
for name,(const,sapling,chance,biomes) in specs.items():
    # The Meadow variant is only a different *placement* for the SAME
    # unchanged almond_tree configured feature (no sapling/canopy changes).
    config_name="almond_tree" if name=="almond_tree_meadow" else name
    placed_name="almond_tree_meadow_wild" if name=="almond_tree_meadow" else f"{name}_wild"
    configured=json.loads((DATA/f"configured_feature/{config_name}.json").read_text())
    placed=json.loads((DATA/f"placed_feature/{placed_name}.json").read_text())
    assert configured["type"]=="minecraft:tree"
    assert placed["feature"]==f"italiansdelight:{config_name}"
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
    assert constBlock!=-1 and f'ModRegistries.id("{placed_name}")' in wg[constBlock:constBlock+220]
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
# Ancient olives are explicitly exempt from the balancing change.
assert specs["ancient_olive_tree"][2]==96
assert specs["fruit_cherry_tree"][2]==7
assert 1/specs["olive_tree"][2]+1/specs["almond_tree"][2]==1/20, (
    "Combined plains/savanna custom trees should match vanilla Plains oak expectation")
assert specs["almond_tree_meadow"][2]==100
# The same biome cannot inject BOTH almond placements, which would
# defeat the separate Meadow rarity.
assert "MEADOW" not in specs["almond_tree"][3]
assert specs["almond_tree_meadow"][3]=={"MEADOW"}
assert "OLIVE_TREE_WILD" in wg and "ANCIENT_OLIVE_TREE_WILD" in wg
# Never generate ornamental vanilla pink cherry via the fruit-cherry entry.
assert json.loads((DATA/"configured_feature/fruit_cherry_tree.json").read_text())[
    "config"]["trunk_provider"]["state"]["Name"]=="italiansdelight:fruit_cherry_log"
print("OK: five placed features for four orchard trees; plains/savanna 1/40+1/40 and meadow 1/100")
print("OK: ancient olive 1/96 and fruit cherry 1/7 unchanged")
print("NOTE: existing chunks never retroactively acquire natural trees")
