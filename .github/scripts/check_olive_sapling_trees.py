#!/usr/bin/env python3
"""Verify data-driven 26.2 olive sapling and normal/2x2 ancient growth.

This does not replace an in-game bone-meal, sapling or worldgen check.
"""
from pathlib import Path
import json,struct
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
SRC=Path("src/main/java/dev/italiansdelight")
def j(p):
    if not p.exists(): raise ValueError(f"Missing: {p}")
    return json.loads(p.read_text())
def main():
    png=A/"textures/block/olive_sapling.png"
    raw=png.read_bytes()
    assert raw[:8]==b"\\x89PNG\\r\\n\\x1a\\n"
    assert struct.unpack(">II",raw[16:24])==(16,16)
    assert not (A/"textures/block/sapling_olive.png").exists(), "Rustic sapling is prohibited"
    block=j(A/"models/block/olive_sapling.json")
    assert block["parent"]=="minecraft:block/cross"
    assert block["textures"]["cross"]=="italiansdelight:block/olive_sapling"
    assert j(A/"blockstates/olive_sapling.json")["variants"][""]["model"]=="italiansdelight:block/olive_sapling"
    assert j(A/"items/olive_sapling.json")["model"]["model"]=="italiansdelight:item/olive_sapling"
    assert j(A/"models/item/olive_sapling.json")["textures"]["layer0"]=="italiansdelight:block/olive_sapling"
    loot=j(D/"italiansdelight/loot_table/blocks/olive_sapling.json")
    assert loot["pools"][0]["entries"][0]["name"]=="italiansdelight:olive_sapling"
    for kind in ("block","item"):
        assert "italiansdelight:olive_sapling" in j(D/f"minecraft/tags/{kind}/saplings.json")["values"]
    for name,placer,base_height in (("olive_tree","minecraft:fancy_trunk_placer",4),("ancient_olive_tree","minecraft:dark_oak_trunk_placer",7)):
        feat=j(D/f"italiansdelight/worldgen/configured_feature/{name}.json")
        assert feat["type"]=="minecraft:tree"
        cfg=feat["config"]
        assert cfg["trunk_provider"]["state"]["Name"]=="italiansdelight:olive_log"
        assert cfg["foliage_provider"]["state"]["Name"]=="italiansdelight:olive_leaves"
        assert cfg["foliage_provider"]["state"]["Properties"]["persistent"]=="false"
        assert cfg["trunk_placer"]["type"]==placer
        assert cfg["trunk_placer"]["base_height"]==base_height
        assert cfg["below_trunk_provider"]["type"]=="minecraft:rule_based_state_provider"
    for name,chance in (("olive_tree",55),("ancient_olive_tree",450)):
        p=j(D/f"italiansdelight/worldgen/placed_feature/{name}_wild.json")
        assert p["feature"]==f"italiansdelight:{name}"
        assert any(x.get("type")=="minecraft:rarity_filter" and x.get("chance")==chance for x in p["placement"])
        assert any(x.get("type")=="minecraft:biome" for x in p["placement"])
    code=(SRC/"common/registry/ModBlocks.java").read_text()
    assert "new SaplingBlock(OLIVE_TREE_GROWER" in code
    assert "Optional.of(ANCIENT_OLIVE_TREE_FEATURE)" in code
    assert "Optional.of(OLIVE_TREE_FEATURE)" in code
    wg=(SRC/"common/registry/ModWorldGeneration.java").read_text()
    assert "OLIVE_TREE_WILD" in wg and "ANCIENT_OLIVE_TREE_WILD" in wg
    tab=(SRC/"common/item/group/ModItemGroups.java").read_text()
    assert tab.count("output.accept(ModBlocks.OLIVE_SAPLING);")==1
    print("OK: sapling in Creative, loot and sapling tags; 26.2 vanilla TreeGrower recognizes 1x1 and 2x2")
    print("OK: normal and ancient data-driven trees use exclusively olive log and silvery leaves")
    print("OK: natural tree placements uncommon and ancient placement very rare")
if __name__=="__main__":
    main()
