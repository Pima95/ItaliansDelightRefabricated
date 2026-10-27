#!/usr/bin/env python3
"""Almond orchard stage 1: original leaves and plantable 1x1 tree (not wild)."""
from pathlib import Path
import json
import hashlib
from check_orchard_signs_and_cutting import check_rgba_pixel_art

R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data"
SRC=Path("src/main/java/dev/italiansdelight")
def j(path):
    assert path.exists(), path
    return json.loads(path.read_text(encoding="utf-8"))
def png(name):
    path=A/"textures/block"/f"{name}.png"
    data=path.read_bytes()
    assert data[:8]==b"\x89PNG\r\n\x1a\n",name
    import struct
    assert struct.unpack(">II",data[16:24])==(16,16),name
    pix=[p for row in check_rgba_pixel_art(path,(16,16)) for p in row]
    assert len(pix)==256 and all(p[3] in (0,255) for p in pix)
    return pix

def main():
    leaf=png("almond_leaves")
    sapling=png("almond_sapling")
    colors={(40,75,40),(58,99,49),(78,121,57),
            (103,148,71),(136,174,95),(166,192,119)}
    leaf_visible=[p for p in leaf if p[3]==255]
    assert len(leaf_visible)==202 and {p[:3] for p in leaf_visible}==colors
    assert 65<=sum(p[3]==255 for p in sapling)<=110
    stem={(87,58,48),(120,79,58),(156,110,78)}
    assert {p[:3] for p in sapling if p[3]==255}<=colors|stem
    assert sum(p[:3] in stem for p in sapling if p[3])>=8
    assert hashlib.sha256((A/"textures/block/almond_leaves.png").read_bytes()).hexdigest()==(
        "bbe13d99cde46a0b0eff5c6c23eead04d0c2335d05f7fa5eb6f509619f121cd7")
    assert hashlib.sha256((A/"textures/block/almond_sapling.png").read_bytes()).hexdigest()==(
        "13511593773754a693aff73a64faba0e78c1cbef7b220e38662583a515628e3e")

    for name in ("almond_sapling","almond_leaves"):
        block=j(A/f"blockstates/{name}.json")
        assert block["variants"][""]["model"]==f"italiansdelight:block/{name}"
        assert j(A/f"items/{name}.json")["model"]["model"]==f"italiansdelight:item/{name}"
        assert j(D/f"italiansdelight/loot_table/blocks/{name}.json")["type"]=="minecraft:block"
        for kind in ("block","item"):
            cat="saplings" if name.endswith("sapling") else "leaves"
            assert "italiansdelight:"+name in j(D/f"minecraft/tags/{kind}/{cat}.json")["values"]
        for lang in ("it_it","en_us"):
            assert "block.italiansdelight."+name in j(A/f"lang/{lang}.json")
    assert j(A/"models/block/almond_leaves.json")=={
        "parent":"minecraft:block/leaves",
        "textures":{"all":"italiansdelight:block/almond_leaves"}}
    assert j(A/"models/block/almond_sapling.json")=={
        "parent":"minecraft:block/cross",
        "textures":{"cross":"italiansdelight:block/almond_sapling"}}
    assert j(A/"models/item/almond_sapling.json")["textures"]["layer0"]==(
        "italiansdelight:block/almond_sapling")
    assert j(A/"models/item/almond_leaves.json")["parent"]==(
        "italiansdelight:block/almond_leaves")
    assert j(A/"models/block/potted_almond_sapling.json")["parent"]==(
        "minecraft:block/flower_pot_cross")
    assert j(A/"blockstates/potted_almond_sapling.json")["variants"][""]["model"]==(
        "italiansdelight:block/potted_almond_sapling")
    assert "italiansdelight:potted_almond_sapling" in j(
        D/"minecraft/tags/block/flower_pots.json")["values"]
    loot=j(D/"italiansdelight/loot_table/blocks/almond_leaves.json")
    names=[e.get("name") for pool in loot["pools"] for e in pool["entries"]]
    assert "italiansdelight:almond_leaves" in names
    assert "italiansdelight:almond_sapling" in names
    assert "minecraft:stick" in names
    assert "italiansdelight:almonds" not in names
    assert j(D/"italiansdelight/loot_table/blocks/almond_sapling.json")["pools"][0]["entries"][0]["name"]==(
        "italiansdelight:almond_sapling")
    for lang in ("it_it","en_us"):
        assert "block.italiansdelight.potted_almond_sapling" in j(A/f"lang/{lang}.json")

    tree=j(D/"italiansdelight/worldgen/configured_feature/almond_tree.json")
    assert tree["type"]=="minecraft:tree"
    cfg=tree["config"]
    assert cfg["trunk_provider"]["state"]["Name"]=="italiansdelight:almond_log"
    assert cfg["foliage_provider"]["state"]["Name"]=="italiansdelight:almond_leaves"
    assert cfg["foliage_provider"]["state"]["Properties"]["persistent"]=="false"
    assert cfg["trunk_placer"]=={
        "type":"italiansdelight:almond_trunk_placer",
        "base_height":5, "height_rand_a":2, "height_rand_b":0}
    assert cfg["foliage_placer"]["type"]=="italiansdelight:almond_foliage_placer"
    assert cfg["foliage_placer"]["radius"]==2
    blocks=(SRC/"common/registry/ModBlocks.java").read_text()
    placers=(SRC/"common/worldgen/ModTreePlacers.java").read_text()
    assert "new SaplingBlock(ALMOND_TREE_GROWER" in blocks
    assert "Optional.of(ALMOND_TREE_FEATURE)" in blocks
    assert "new UntintedParticleLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES" in blocks
    assert "fire.add(ALMOND_LEAVES, 30, 60);" in blocks
    for name in ("ALMOND_LEAVES","ALMOND_SAPLING"):
        assert (SRC/"common/item/group/ModItemGroups.java").read_text().count(
            f"output.accept(ModBlocks.{name});")==1
    for kind in ("almond_trunk_placer","almond_foliage_placer"):
        assert f'ModRegistries.id("{kind}")' in placers
    for class_name in ("AlmondTrunkPlacer","AlmondFoliagePlacer"):
        assert (SRC/f"common/worldgen/{class_name}.java").exists()
    assert "ALMOND_TREE" not in (SRC/"common/registry/ModWorldGeneration.java").read_text()
    assert not (D/"italiansdelight/worldgen/placed_feature/almond_tree_wild.json").exists()
    print("OK: Almond original leaves/sapling art, correct vanilla models, pot, drops and tags")
    print("OK: Almond 1x1 sapling growth with bespoke trunk and foliage placers")
    print("OK: No fruit drops or wild Almond worldgen until later phases")

if __name__=="__main__":
    main()
