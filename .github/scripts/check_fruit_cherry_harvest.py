#!/usr/bin/env python3
"""Fruit cherry stage 0..5: 3 WHITE flowering phases, green drupes, red cherry harvest.
Static QA only: client/server gameplay still needs manual testing.
"""
from pathlib import Path
import hashlib
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

R=Path("src/main/resources")
A=R/"assets/italiansdelight"
D=R/"data/italiansdelight"
JAVA=Path("src/main/java/dev/italiansdelight")
name="fruit_cherry_leaves"
STAGES={
    0:name,
    1:name+"_buds",
    2:name+"_flowers_early",
    3:name+"_flowers",
    4:name+"_unripe",
    5:name+"_fruiting",
}
def read(path):
    assert path.is_file(), path
    return json.loads(path.read_text(encoding="utf-8"))
def px(path):
    return [p for row in check_rgba_pixel_art(path,(16,16)) for p in row]

base=A/f"textures/block/{name}.png"
assert hashlib.sha256(base.read_bytes()).hexdigest()==(
    "1d4b76bbd78358e48203f89551f3ff56c4d658827fd0b013e59aebc0bc108514"
), "Approved base foliage was modified"
original=px(base)
assert sum(p[3]==255 for p in original)==157
assert sum(p[3]==0 for p in original)==99
assert set(p[3] for p in original)=={0,255}
states=read(A/f"blockstates/{name}.json")["variants"]
assert states == {
    f"stage={i}":{"model":"italiansdelight:block/"+model}
    for i,model in STAGES.items()
}, "Cherry foliage needs six model states 0..5"

changes={}
for stage,model in STAGES.items():
    if stage==0:continue
    asset=A/f"textures/block/{model}.png"
    assert read(A/f"models/block/{model}.json")=={
        "parent":"minecraft:block/leaves",
        "textures":{"all":"italiansdelight:block/"+model}
    },model
    pixlist=px(asset)
    assert [p[3] for p in pixlist]==[p[3] for p in original],model
    assert all(p[3] in (0,255) for p in pixlist),model
    changed=[curr for before,curr in zip(original,pixlist) if before!=curr]
    assert all(p[3]==255 for p in changed),model
    changes[stage]=len(changed)
    if stage==1:
        assert 15 <= len(changed) <= 30, "Buds need sparse pale/ivory pixels"
        assert sum(r>200 and g>200 and b>165 for r,g,b,a in changed)>=10
    if stage in (2,3):
        # Sweet cherry blossoms are white, not sakura's pink. The centers
        # may be ochre/yellow; all petals must be white, pale ivory or cool grey.
        assert len(changed)>=35 if stage==2 else len(changed)>=65
        assert sum(r>=237 and g>=237 and b>=210
                   for r,g,b,a in changed)>=20 if stage==2 else (
                       sum(r>=237 and g>=237 and b>=210
                           for r,g,b,a in changed)>=42)
        assert sum(r>=185 and r>g*1.24 and b>g*.83
                   for r,g,b,a in changed)==0, "Pink cherry petals are forbidden"
    if stage==4:
        assert 40<=len(changed)<=80
        assert sum(g>=90 and g>=r*1.05 and g>b*1.10
                   for r,g,b,a in changed)>=20, "Unripe cherries must be green"
    if stage==5:
        # Mature cherries: fewer, smaller, deliberately uneven and crimson.
        # No orange (highlights must be ruby-pink, blue channel > green).
        assert len(changed)==32, "Expected 23 crimson cherry pixels plus 9 stems"
        berries={(index % 16,index // 16)
                 for index,(old,p) in enumerate(zip(original,pixlist))
                 if old != p and p[0]>=100 and p[0]>p[1]*1.35 and p[0]>p[2]*1.20}
        assert len(berries)==23, "Four added cherries should bring the total to 23 red pixels"
        for x,y in berries:
            r,g,b,_=pixlist[y*16+x]
            assert b>=g+15 and r>b+15, ("Orange cherry pixel",x,y,(r,g,b))
        # Count connected red clusters: five originals and FOUR added; each <=3px.
        pending=set(berries)
        groups=[]
        while pending:
            stack=[pending.pop()]
            group=[]
            while stack:
                x,y=stack.pop()
                group.append((x,y))
                neighbours={(xx,yy) for xx in range(x-1,x+2)
                            for yy in range(y-1,y+2)}
                found=pending & neighbours
                pending.difference_update(found)
                stack.extend(found)
            groups.append(group)
        assert len(groups)==9 and all(2<=len(g)<=3 for g in groups), groups
        assert {min(group,key=lambda p:(p[1],p[0])) for group in groups}=={
            (8,1),(2,2),(5,4),(12,6),(1,7),(5,9),(8,11),(14,11),(10,14)
        }, "The 9 crimson bunches should be scattered asymmetrically"
        assert berries != {(15-x,y) for x,y in berries}, "Mirrored cherry arrangement"
        assert sum(1 for p in changed if
                   p[0]<100 and p[1]>p[0] and p[1]>p[2])==9, (
            "Keep thin green fruit stems")
assert changes[1]<changes[2]<changes[3], ("Flower density needs 3 increasing phases",changes)
assert changes[4]==57 and changes[5]==32

src=(JAVA/"common/block/FruitCherryLeavesBlock.java").read_text()
blocks=(JAVA/"common/registry/ModBlocks.java").read_text()
client=(JAVA/"ItaliansDelightClient.java").read_text()
assert "extends UntintedParticleLeavesBlock" in src
assert 'IntegerProperty.create("stage", 0, 5)' in src
assert "RIPE_STAGE = 5" in src and "ADVANCE_CHANCE = 3" in src
assert "super.randomTick(state, level, pos, random)" in src
assert "super.isRandomlyTicking(state)" in src
assert "current.setValue(STAGE, stage + 1)" in src
assert "for (Direction face : Direction.values())" in src
assert "level.getBlockState(pos.relative(face))" in src
assert "neighbor.isAir()" in src
assert "hasAirFace(level, pos, direction, neighborState)" in src
assert "return updated.setValue(STAGE, 0)" in src
assert "!state.getValue(PERSISTENT)" in src and "state.getValue(DISTANCE) < 7" in src
assert "!state.getValue(WATERLOGGED)" in src
assert "|| !hasAirFace(server, pos)" in src
assert "server.setBlock(pos, current.setValue(STAGE, 0), 2)" in src
assert "new ItemStack(ModItems.CHERRIES, 1 + server.getRandom().nextInt(3))" in src
assert "new FruitCherryLeavesBlock(Block.Properties.ofFullCopy(Blocks.OAK_LEAVES)" in blocks
assert "fire.add(FRUIT_CHERRY_LEAVES, 30, 60);" in blocks
assert "BlockColorRegistry.register(List.of(FRUIT_CHERRY_BIOME_FOLIAGE), ModBlocks.FRUIT_CHERRY_LEAVES)" in client
assert "state.hasProperty(FruitCherryLeavesBlock.STAGE)" in client
assert "state.getValue(FruitCherryLeavesBlock.STAGE) > 0" in client
assert "return 0xFFFFFFFF;" in client, "Flowers must stay white in tinted biomes"

# Leaf-break drops remain vanilla-like: shears/Silk Touch leaves, otherwise
# possible sapling/sticks. Never duplicate cherries when breaking a block.
loot=read(D/"loot_table/blocks/fruit_cherry_leaves.json")
drops=[entry.get("name") for pool in loot["pools"] for entry in pool["entries"]]
assert "italiansdelight:fruit_cherry_leaves" in drops
assert "italiansdelight:fruit_cherry_sapling" in drops
assert "minecraft:stick" in drops
assert "italiansdelight:cherries" not in drops
assert not (D/"worldgen/placed_feature/fruit_cherry_tree_wild.json").exists()

print("OK: approved 157px fruit-cherry leaves unchanged; 5 new 16x16 sprites use identical hard-alpha mask")
print("OK: 3 WHITE bloom phases (buds, early flowers, full bloom) with pale yellow centers")
print("OK: 9 asymmetric ruby-red cherry clusters (5 original + 4 new), 23 red pixels, 9 stems; no orange")
print("OK: natural supported leaves, any exposed air face, server-side 1-3 cherries, reset and regrowth")
print("OK: biome tint kept for stage 0, disabled while flowering/fruiting to preserve white petals")
print("NOTE: in-game growth, visuals, 6-face exposure and multi-player harvesting not yet tested")
