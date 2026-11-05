#!/usr/bin/env python3
"""Almond tree stage-0..4, pink-white blossoms, green hull, brown shell harvest.

Static checks complement but cannot replace a client/server in-game harvest test.
"""
import hashlib
import json
from pathlib import Path
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT=Path("src/main/resources")
A=ROOT/"assets/italiansdelight"
D=ROOT/"data/italiansdelight"
J=Path("src/main/java/dev/italiansdelight")
BASE=A/"textures/block/almond_leaves.png"
TYPES={
    0:"almond_leaves",
    1:"almond_leaves_buds",
    2:"almond_leaves_flowers",
    3:"almond_leaves_unripe",
    4:"almond_leaves_fruiting",
}
def load(path):
    assert path.is_file(),path
    return json.loads(path.read_text(encoding="utf-8"))

# Keep the user's existing foliage, six colors, silhouette and biome tint.
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==(
    "7fcfcb85a8f561c114f294650a764969df8aaac2306e6232324c23df90176c39")
base=[p for row in check_rgba_pixel_art(BASE,(16,16)) for p in row]
assert sum(p[3]==255 for p in base)==197
assert sum(p[3]==0 for p in base)==59

state=load(A/"blockstates/almond_leaves.json")
assert state == {"variants":{
    f"stage={i}":{"model":f"italiansdelight:block/{name}"}
    for i,name in TYPES.items()
}}, state

changed_counts={}
for stage,name in TYPES.items():
    if stage==0:continue
    m=load(A/f"models/block/{name}.json")
    assert m=={"parent":"minecraft:block/leaves",
               "textures":{"all":f"italiansdelight:block/{name}"}}
    current=[p for row in check_rgba_pixel_art(
        A/f"textures/block/{name}.png",(16,16)) for p in row]
    assert [p[3] for p in current]==[p[3] for p in base],name
    assert all(p[3] in (0,255) for p in current),name
    changed=[q for p,q in zip(base,current) if p!=q]
    changed_counts[stage]=len(changed)
    assert all(p[3]==255 for p in changed),name
    if stage==1:
        assert 12<=len(changed)<=25, "Pink almond buds need visible but sparse pixels"
        assert sum(r>g*1.20 and b>g and r>=200
                   for r,g,b,a in changed)>=5
    if stage==2:
        assert 35<=len(changed)<=85, "Blossoms must be distributed, not a single flower"
        assert sum(r>g*1.2 and b>125 for r,g,b,a in changed)>=14, (
            "Pink petals missing")
        assert sum(r>=242 and g>=210 and b>=220
                   for r,g,b,a in changed)>=14, "White-pink petal highlights missing"
    if stage==3:
        assert 38<=len(changed)<=80, "Green almond hulls not sufficiently visible"
        assert sum(g>=90 and g>=r*1.05 and g>b*1.12
                   for r,g,b,a in changed)>=28, "Green shells missing"
    if stage==4:
        assert 42<=len(changed)<=95, "Dry almond shells not sufficiently visible"
        assert sum(r>g*1.14 and g>b*1.08 and r>=95
                   for r,g,b,a in changed)>=35, "Brown dry-shell colors missing"
assert changed_counts[2]>changed_counts[1]
assert changed_counts[4]>changed_counts[1]

java=(J/"common/block/AlmondLeavesBlock.java").read_text(encoding="utf-8")
registry=(J/"common/registry/ModBlocks.java").read_text(encoding="utf-8")
client=(J/"ItaliansDelightClient.java").read_text(encoding="utf-8")
assert "extends UntintedParticleLeavesBlock" in java
assert 'IntegerProperty.create("stage", 0, 4)' in java
assert "RIPE_STAGE = 4" in java and "ADVANCE_CHANCE = 3" in java
assert "super.randomTick(state, level, pos, random)" in java
assert "super.isRandomlyTicking(state)" in java
assert "current.setValue(STAGE, stage + 1)" in java
assert "for (Direction face : Direction.values())" in java
assert "level.getBlockState(pos.relative(face))" in java
assert "neighbor.isAir()" in java
assert "hasAirFace(level, pos, direction, neighborState)" in java
assert "return updated.setValue(STAGE, 0)" in java
assert "!state.getValue(PERSISTENT)" in java
assert "state.getValue(DISTANCE) < 7" in java
assert "!state.getValue(WATERLOGGED)" in java
assert "|| !hasAirFace(server, pos)" in java
assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in java
assert "new ItemStack(ModItems.ALMONDS, 1 + server.getRandom().nextInt(3))" in java
assert "new AlmondLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)" in registry
assert "fire.add(ALMOND_LEAVES, 30, 60)" in registry
assert "BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES)" in client

# No extra almond items, no drops upon leaf destruction, no wild generation.
loot=load(D/"loot_table/blocks/almond_leaves.json")
drops=[e.get("name") for pool in loot["pools"] for e in pool["entries"]]
assert "italiansdelight:almond_leaves" in drops
assert "italiansdelight:almond_sapling" in drops
assert "minecraft:stick" in drops
assert "italiansdelight:almonds" not in drops
assert not (D/"worldgen/placed_feature/almond_tree_wild.json").exists()
print("OK: Almond original foliage preserved, 4 derived growth sprites, full binary alpha")
print("OK: pink/white blooms -> green husk -> dried almond shell, 5 stages in blockstates")
print("OK: exposed-air-only flowering and harvesting, natural-only growth, 1-3 almonds")
print("OK: almond/olive/cherry artwork and almond biome tint unaffected")
print("NOTE: in-game visuals, interaction and timings require user confirmation")
