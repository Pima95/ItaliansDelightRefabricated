#!/usr/bin/env python3
"""Cultivable Fruit Cherry tree (no natural generation or fruit harvest yet)."""
from pathlib import Path
import hashlib
import json
from check_orchard_signs_and_cutting import check_rgba_pixel_art

ROOT = Path("src/main/resources")
A = ROOT / "assets/italiansdelight"
D = ROOT / "data"
JAVA = Path("src/main/java/dev/italiansdelight")

def read(path):
    assert path.is_file(), f"Missing: {path}"
    return json.loads(path.read_text(encoding="utf-8"))

names = ("fruit_cherry_sapling", "fruit_cherry_leaves")
for name in names:
    pixels = check_rgba_pixel_art(A / f"textures/block/{name}.png", (16, 16))
    assert all(p[3] in (0, 255) for row in pixels for p in row), name
    count = sum(p[3] == 255 for row in pixels for p in row)
    assert count == (114 if name.endswith("sapling") else 168), (name, count)
    assert read(A / f"blockstates/{name}.json")["variants"][""]["model"] == f"italiansdelight:block/{name}"
    assert read(A / f"items/{name}.json")["model"]["model"] == f"italiansdelight:item/{name}"
    assert read(D / f"italiansdelight/loot_table/blocks/{name}.json")["type"] == "minecraft:block"
    for cat in ("block", "item"):
        collection = "saplings" if name.endswith("sapling") else "leaves"
        assert f"italiansdelight:{name}" in read(D / f"minecraft/tags/{cat}/{collection}.json")["values"]
    for locale in ("en_us", "it_it"):
        assert read(A / f"lang/{locale}.json")[f"block.italiansdelight.{name}"]

assert hashlib.sha256((A / "textures/block/fruit_cherry_leaves.png").read_bytes()).hexdigest() == "363f220a1cefb96c5399f8ad746365de62fbc1a40965530facad84df3b4db0fd"
assert hashlib.sha256((A / "textures/block/fruit_cherry_sapling.png").read_bytes()).hexdigest() == "7b2228956073169b8a27c50b4c616e337cb22618898e272ef2edf24c1eca5337"

# The approved Olive *shape* is the reference: identical cutout opacity,
# but the Fruit Cherry is green, not an olive/silver recolor.
olive = check_rgba_pixel_art(A / "textures/block/olive_leaves.png", (16, 16))
cherry = check_rgba_pixel_art(A / "textures/block/fruit_cherry_leaves.png", (16, 16))
assert all(olive[y][x][3] == cherry[y][x][3] for y in range(16) for x in range(16)), (
    "Fruit Cherry must have the same airy cutout shape as approved Olive"
)
leaf_green = {px[:3] for row in cherry for px in row if px[3]}
assert leaf_green == {(56,98,47),(96,141,65),(117,162,79),(165,195,117)}
assert leaf_green.isdisjoint({px[:3] for row in olive for px in row if px[3]})
assert sum(px[3]==0 for row in cherry for px in row) == 88

# Preserve the approved sapling shape and all twenty exact woody pixels,
# introducing a six-tone leaf pattern instead of plain green panels.
sapling = check_rgba_pixel_art(A / "textures/block/fruit_cherry_sapling.png", (16, 16))
stem = {
    (9,5):(125,75,57), (9,6):(125,75,57), (8,7):(125,75,57),
    (7,8):(91,57,46), (8,8):(125,75,57), (9,8):(125,75,57),
    (6,9):(125,75,57), (7,9):(91,57,46),
    (6,10):(91,57,46), (7,10):(167,103,75),
    (6,11):(91,57,46), (7,11):(125,75,57),
    (6,12):(91,57,46), (7,12):(167,103,75),
    (7,13):(91,57,46), (8,13):(125,75,57),
    (7,14):(91,57,46), (8,14):(167,103,75),
    (7,15):(91,57,46), (8,15):(125,75,57),
}
for (x,y),rgb in stem.items():
    assert sapling[y][x] == (*rgb,255), ("Approved stem changed",x,y)
sapling_green = [px[:3] for row in sapling for px in row
                 if px[3] and px[:3] not in set(stem.values())]
assert len(sapling_green) == 94
assert set(sapling_green) == {
    (56,98,47),(76,119,56),(96,141,65),
    (117,162,79),(139,178,96),(165,195,117)}
assert sapling_green.count((165,195,117)) >= 16
assert sapling_green.count((56,98,47)) >= 8

assert read(A / "models/block/fruit_cherry_leaves.json") == {
    "parent": "minecraft:block/leaves", "textures": {"all": "italiansdelight:block/fruit_cherry_leaves"}
}
assert read(A / "models/block/fruit_cherry_sapling.json") == {
    "parent": "minecraft:block/cross", "textures": {"cross": "italiansdelight:block/fruit_cherry_sapling"}
}
assert read(A / "models/block/potted_fruit_cherry_sapling.json") == {
    "parent": "minecraft:block/flower_pot_cross",
    "textures": {"plant": "italiansdelight:block/fruit_cherry_sapling"},
}
assert "italiansdelight:potted_fruit_cherry_sapling" in read(D / "minecraft/tags/block/flower_pots.json")["values"]
potted = read(D / "italiansdelight/loot_table/blocks/potted_fruit_cherry_sapling.json")
potted_drops = [e["name"] for pool in potted["pools"] for e in pool["entries"]]
assert set(potted_drops) == {"minecraft:flower_pot", "italiansdelight:fruit_cherry_sapling"}

leaf_loot = read(D / "italiansdelight/loot_table/blocks/fruit_cherry_leaves.json")
leaf_drops = [e.get("name") for pool in leaf_loot["pools"] for e in pool["entries"]]
assert {"minecraft:stick", "italiansdelight:fruit_cherry_leaves", "italiansdelight:fruit_cherry_sapling"} <= set(leaf_drops)
assert not any("cherries" in (x or "") for x in leaf_drops), "Fruit drops belong to the later shared harvest system"

feature = read(D / "italiansdelight/worldgen/configured_feature/fruit_cherry_tree.json")
assert feature["type"] == "minecraft:tree"
c = feature["config"]
assert c["trunk_provider"]["state"]["Name"] == "italiansdelight:fruit_cherry_log"
assert c["foliage_provider"]["state"]["Name"] == "italiansdelight:fruit_cherry_leaves"
assert c["foliage_provider"]["state"]["Properties"]["persistent"] == "false"
assert c["foliage_placer"]["type"] == "italiansdelight:fruit_cherry_foliage_placer"
assert c["trunk_placer"]["type"] == "italiansdelight:fruit_cherry_trunk_placer"
assert c["decorators"] == []

blocks = (JAVA / "common/registry/ModBlocks.java").read_text(encoding="utf-8")
placers = (JAVA / "common/worldgen/ModTreePlacers.java").read_text(encoding="utf-8")
tab = (JAVA / "common/item/group/ModItemGroups.java").read_text(encoding="utf-8")
world = (JAVA / "common/registry/ModWorldGeneration.java").read_text(encoding="utf-8")
trunk = (JAVA / "common/worldgen/FruitCherryTrunkPlacer.java").read_text(encoding="utf-8")
crown = (JAVA / "common/worldgen/FruitCherryFoliagePlacer.java").read_text(encoding="utf-8")
for symbol in ("FRUIT_CHERRY_LEAVES", "FRUIT_CHERRY_SAPLING"):
    assert tab.count(f"output.accept(ModBlocks.{symbol});") == 1
assert "new SaplingBlock(FRUIT_CHERRY_TREE_GROWER" in blocks
assert "Optional.of(FRUIT_CHERRY_TREE_FEATURE)" in blocks
assert "Optional.empty()" in blocks
assert "new UntintedParticleLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES" in blocks
assert "fire.add(FRUIT_CHERRY_LEAVES, 30, 60);" in blocks
for name in ("fruit_cherry_trunk_placer", "fruit_cherry_foliage_placer"):
    assert f'ModRegistries.id("{name}")' in placers

# Unique orchard silhouette rather than Almond's standing sprays.
assert "FORK_VARIANT_CHANCE = 3" in trunk
assert "for (int y = 1" not in trunk
assert "canPlacePlan(level, origin, plan.barkPositions())" in trunk
assert "private FruitCherryPlan planTree(" in trunk
assert "double[] radii = {1.70D, 2.35D, 2.95D, 2.75D, 2.20D, 1.35D};" in crown
assert "for (int arm = 0; arm < 3; arm++)" not in crown
assert "distanceFromWood" in crown and "nextDistance > 6" in crown
assert "tryPlaceLeaf(level, setter, random, config, pos)" in crown
assert "isFruitCherryWood" in crown and "isFruitCherryWood" not in trunk
assert not any(f"fruit_cherry_tree" in x for x in world.splitlines()), "Do not activate wild generation yet"
assert not (D / "italiansdelight/worldgen/placed_feature/fruit_cherry_tree_wild.json").exists()

print("OK: Fruit Cherry leafy silhouette matches Olive alpha (168 opaque) with distinct bright greens")
print("OK: sapling retains trunk geometry and uses six visibly varied leaf shades")
print("OK: planted tree grows with bone meal using custom trunk and rounded canopy")
print("OK: no fruit harvest, fruit drops, or wild worldgen before review")
