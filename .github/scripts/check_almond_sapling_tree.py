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
    assert len(leaf_visible)==209 and {p[:3] for p in leaf_visible}==colors
    assert 65<=sum(p[3]==255 for p in sapling)<=110
    stem={(87,58,48),(120,79,58),(156,110,78)}
    assert {p[:3] for p in sapling if p[3]==255}<=colors|stem
    assert sum(p[:3] in stem for p in sapling if p[3])>=16
    # Real saplings have a crooked S-shaped stem and secondary branches,
    # not a straight trunk with a symmetric pixel cloud above it.
    for x,y in ((8,15),(7,13),(6,11),(9,6)):
        assert sapling[y*16+x][:3] in stem, ("missing crooked wooden stem",x,y)
    assert len({x for y in range(6,16) for x in range(16)
                if sapling[y*16+x][:3] in stem})>=5
    assert {p[:3] for p in sapling if p[3]>0}==colors|stem
    assert hashlib.sha256((A/"textures/block/almond_leaves.png").read_bytes()).hexdigest()==(
        "0d18e28fb2be34731eb41b2d53c01514c0339ce8f216de5c6f08d64f6c31d726")
    assert hashlib.sha256((A/"textures/block/almond_sapling.png").read_bytes()).hexdigest()==(
        "f033c642298211c138e08538587e4eac4d9a9caa9553c7406bd9ba0c37196606")

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
        "base_height":6, "height_rand_a":0, "height_rand_b":0}
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
    # The ALMOND trunk reuses Olive's proven lower bend + rising double fork.
    # The crown MUST NOT reuse the olive flattened dome: its vertical tuft
    # structure extends from dy=-1 through +5, with 3 upward shoots.
    trunk=(SRC/"common/worldgen/AlmondTrunkPlacer.java").read_text()
    foliage=(SRC/"common/worldgen/AlmondFoliagePlacer.java").read_text()
    assert "BlockPos lowSide = forkBase.relative(lean)" in trunk
    assert "BlockPos outerFork = raisedFork.relative(secondary)" in trunk
    assert "canPlacePlan(level, origin, plan.barkPositions())" in trunk
    assert "FORK_VARIANT_CHANCE = 5" in trunk
    assert "TALL_SINGLE_CHANCE = 4" in trunk
    assert "new double" not in foliage
    assert "anchor.above(5)" in foliage
    assert "for (int dy = 1; dy <= 4; dy++)" in foliage
    assert "distanceFromWood" in foliage and "nextDistance > 6" in foliage
    assert "radii = {1.70D, 2.35D, 2.45D, 2.00D, 1.45D, 0.85D}" in foliage
    assert "OLIVE_LEAVES" not in foliage and "OLIVE_TRUNK" not in trunk
    for class_name in ("AlmondTrunkPlacer","AlmondFoliagePlacer"):
        assert (SRC/f"common/worldgen/{class_name}.java").exists()
    assert "ALMOND_TREE" not in (SRC/"common/registry/ModWorldGeneration.java").read_text()
    assert not (D/"italiansdelight/worldgen/placed_feature/almond_tree_wild.json").exists()
    print("OK: Almond original leaves/sapling art, correct vanilla models, pot, drops and tags")
    print("OK: Almond sapling has crooked hand-drawn stem; trunk reuses Olive bends/forks and crown has tall upward foliage")
    print("OK: No fruit drops or wild Almond worldgen until later phases")

if __name__=="__main__":
    main()
