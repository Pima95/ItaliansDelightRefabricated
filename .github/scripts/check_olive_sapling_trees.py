#!/usr/bin/env python3
"""Verify data-driven 26.2 olive sapling and normal/2x2 ancient growth.

This does not replace an in-game bone-meal, sapling or worldgen check.
"""
from pathlib import Path
import json,struct,hashlib
from check_orchard_signs_and_cutting import check_rgba_pixel_art
A=Path("src/main/resources/assets/italiansdelight")
D=Path("src/main/resources/data")
SRC=Path("src/main/java/dev/italiansdelight")
def j(p):
    if not p.exists(): raise ValueError(f"Missing: {p}")
    return json.loads(p.read_text())
def main():
    png=A/"textures/block/olive_sapling.png"
    raw=png.read_bytes()
    assert raw[:8]==b"\x89PNG\r\n\x1a\n"
    assert struct.unpack(">II",raw[16:24])==(16,16)
    # Silhouette, wood stem and original alpha are preserved; ONLY green
    # foliage is recolored to the exact palette of olive_leaves.png.
    sapling=[px for row in check_rgba_pixel_art(png,(16,16)) for px in row]
    assert sum(px[3] == 0 for px in sapling) == 171
    foliage_colors = {
        (68,91,78), (104,127,110), (146,163,143), (198,209,194)
    }
    stem_colors = {
        (84,74,54), (89,83,63), (99,92,70), (73,61,45),
        (73,65,47), (86,73,52)
    }
    assert {px[:3] for px in sapling if px[3] == 255} == foliage_colors | stem_colors
    assert sum(px[:3] in foliage_colors for px in sapling if px[3] == 255) == 37
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
    for name,placer,base_height in (("olive_tree","italiansdelight:olive_trunk_placer",6),("ancient_olive_tree","italiansdelight:ancient_olive_trunk_placer",7)):
        feat=j(D/f"italiansdelight/worldgen/configured_feature/{name}.json")
        assert feat["type"]=="minecraft:tree"
        cfg=feat["config"]
        assert cfg["trunk_provider"]["state"]["Name"]=="italiansdelight:olive_log"
        assert cfg["foliage_provider"]["state"]["Name"]=="italiansdelight:olive_leaves"
        assert cfg["foliage_provider"]["state"]["Properties"]["persistent"]=="false"
        assert cfg["trunk_placer"]["type"]==placer
        assert cfg["trunk_placer"]["base_height"]==base_height
        if name=="olive_tree":
            assert cfg["foliage_placer"]["type"]=="italiansdelight:olive_foliage_placer"
            assert cfg["foliage_placer"]["radius"]==2
        else:
            assert cfg["foliage_placer"]["type"]=="italiansdelight:ancient_olive_foliage_placer"
            assert cfg["foliage_placer"]["radius"]==2
        assert cfg["below_trunk_provider"]["type"]=="minecraft:rule_based_state_provider"
    # Provisional oak/dark-oak shapes must NEVER appear by natural worldgen.
    # Keep only configured features for sapling growth via bone meal or ticks.
    for name in ("olive_tree", "ancient_olive_tree"):
        assert not (D/f"italiansdelight/worldgen/placed_feature/{name}_wild.json").exists(), (
            f"Provisional {name} placement must not be present in datapacks"
        )
    code=(SRC/"common/registry/ModBlocks.java").read_text()
    assert "new SaplingBlock(OLIVE_TREE_GROWER" in code
    assert "Optional.of(ANCIENT_OLIVE_TREE_FEATURE)" in code
    assert "Optional.of(OLIVE_TREE_FEATURE)" in code
    wg=(SRC/"common/registry/ModWorldGeneration.java").read_text()
    assert "OLIVE_TREE_WILD" not in wg and "ANCIENT_OLIVE_TREE_WILD" not in wg
    assert "olive_tree_wild" not in wg and "ancient_olive_tree_wild" not in wg
    tab=(SRC/"common/item/group/ModItemGroups.java").read_text()
    assert tab.count("output.accept(ModBlocks.OLIVE_SAPLING);")==1
    print("OK: sapling in Creative, loot and sapling tags; 26.2 vanilla TreeGrower recognizes 1x1 and 2x2")
    print("OK: normal and ancient olive trees use distinct original procedural placers")
    print("OK: NO natural olive worldgen, no provisional olive placed features nor biome injection")
    print("OK: silver-green sapling art preserves original 16x16 alpha and stem")
if __name__=="__main__":
    main()
