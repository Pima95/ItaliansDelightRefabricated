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
    assert count == (114 if name.endswith("sapling") else 157), (name, count)
    assert read(A / f"blockstates/{name}.json")["variants"][""]["model"] == f"italiansdelight:block/{name}"
    assert read(A / f"items/{name}.json")["model"]["model"] == f"italiansdelight:item/{name}"
    assert read(D / f"italiansdelight/loot_table/blocks/{name}.json")["type"] == "minecraft:block"
    for cat in ("block", "item"):
        collection = "saplings" if name.endswith("sapling") else "leaves"
        assert f"italiansdelight:{name}" in read(D / f"minecraft/tags/{cat}/{collection}.json")["values"]
    for locale in ("en_us", "it_it"):
        assert read(A / f"lang/{locale}.json")[f"block.italiansdelight.{name}"]

assert hashlib.sha256((A / "textures/block/fruit_cherry_leaves.png").read_bytes()).hexdigest() == "1d4b76bbd78358e48203f89551f3ff56c4d658827fd0b013e59aebc0bc108514"
assert hashlib.sha256((A / "textures/block/fruit_cherry_sapling.png").read_bytes()).hexdigest() == "7b2228956073169b8a27c50b4c616e337cb22618898e272ef2edf24c1eca5337"

# Distinct, deliberately grouped Fruit Cherry leaflets -- NOT a palette
# swap of Olive, Almond or the earlier Fruit Cherry foliage.
olive = check_rgba_pixel_art(A / "textures/block/olive_leaves.png", (16, 16))
almond = check_rgba_pixel_art(A / "textures/block/almond_leaves.png", (16, 16))
cherry = check_rgba_pixel_art(A / "textures/block/fruit_cherry_leaves.png", (16, 16))
alpha = lambda pixels: tuple(p[3] for row in pixels for p in row)
assert alpha(cherry) != alpha(olive) and alpha(cherry) != alpha(almond), (
    "Fruit Cherry leaves must have their own cutout silhouette"
)
assert sum(p[3] == 0 for row in cherry for p in row) == 99
assert 140 <= sum(p[3] == 255 for row in cherry for p in row) <= 180
def mask_difference(a, b):
    return sum(x != y for x, y in zip(alpha(a), alpha(b)))
assert mask_difference(cherry, olive) >= 20
assert mask_difference(cherry, almond) >= 20
cherry_colors = {px[:3] for row in cherry for px in row if px[3]}
assert cherry_colors == {
    (61,81,58),(78,102,72),(99,124,86),
    (121,146,100),(144,167,114),(169,190,135)
}
assert len(cherry_colors) == 6

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
# The block class is unchanged: exactly like Almond, the biome tint is
# applied through Fabric's client BlockColorRegistry, not by replacing the
# foliage particle block with a new server-facing renderer.
assert "new UntintedParticleLeavesBlock(0.0F, ParticleTypes.CHERRY_LEAVES" in blocks
client = (JAVA / "ItaliansDelightClient.java").read_text(encoding="utf-8")
assert 'BlockColorRegistry.register(List.of(ALMOND_BIOME_FOLIAGE), ModBlocks.ALMOND_LEAVES);' in client
assert 'BlockColorRegistry.register(List.of(FRUIT_CHERRY_BIOME_FOLIAGE), ModBlocks.FRUIT_CHERRY_LEAVES);' in client
assert "BlockTintSources.foliage().colorInWorld(state, level, pos)" in client
assert "return softenFruitCherryTint(biome);" in client
for part in ("red", "green", "blue"):
    assert f"int {part} = (255 * 5 +" in client
assert "return 0xFFFFFFFF; // Inventory has its consistent untinted base palette." in client
assert "FRUIT_CHERRY_SAPLING" not in client
assert "BlockTintSources.foliage()" in client
assert "fire.add(FRUIT_CHERRY_LEAVES, 30, 60);" in blocks
for name in ("fruit_cherry_trunk_placer", "fruit_cherry_foliage_placer"):
    assert f'ModRegistries.id("{name}")' in placers

# Exact same APPROVED trunk topology as Almond and Olive, with cherry's
# own log texture supplied by the configured tree feature. Cloning only the
# geometry preserves all connected bends, forks, placement preflight, and
# single/forked/tall proportions. An unrelated vertical pole must never return.
almond_trunk = (JAVA / "common/worldgen/AlmondTrunkPlacer.java").read_text(encoding="utf-8")
normalized = almond_trunk.replace("Almond", "FruitCherry").replace("ALMOND", "FRUIT_CHERRY").replace("almond", "fruit cherry")
marker = "public final class FruitCherryTrunkPlacer"
assert trunk[trunk.index(marker):] == normalized[normalized.index(marker):], (
    "Fruit Cherry trunks must have the same tested face-connected geometry as Almond/Olive"
)
assert "FORK_VARIANT_CHANCE = 5" in trunk
assert "TALL_SINGLE_CHANCE = 4" in trunk
assert "4 + random.nextInt(2)" in trunk
assert "canPlacePlan(level, origin, plan.barkPositions())" in trunk
assert "BlockPos lowSide = forkBase.relative(lean)" in trunk
assert "BlockPos outerFork = raisedFork.relative(secondary)" in trunk
assert "private FruitCherryPlan planTree(" in trunk
assert "double[] radii = {2.15D, 1.95D, 2.72D, 3.00D, 2.38D, 1.72D};" in crown
# The broader crown should be a modest, proportional increase over Almond:
# radii at ALL six levels expand, peaking at 3.00 rather than Almond's 2.45.
def radii(source):
    line = next((row for row in source.splitlines() if "double[] radii = {" in row), None)
    assert line is not None, "Missing foliage radii profile"
    values = line.split("{", 1)[1].split("}", 1)[0]
    return [float(x.strip().removesuffix("D")) for x in values.split(",")]
almond_crown = (JAVA / "common/worldgen/AlmondFoliagePlacer.java").read_text(encoding="utf-8")
cherry_widths, almond_widths = radii(crown), radii(almond_crown)
assert len(cherry_widths) == len(almond_widths) == 6
assert all(0.15 <= cherry - almond <= 0.60 for cherry, almond in zip(cherry_widths, almond_widths))
assert 1.20 <= max(cherry_widths) / max(almond_widths) <= 1.25
# Preserve the low 2-block walking clearance, support under real wooden bends
# and six-step leaf connectivity rather than placing floating canopy clusters.
assert "int minDy = trunkHeightAboveGround >= 5 ? -2 : -1;" in crown
assert "for (int dy = minDy; dy <= 3; dy++)" in crown
assert "anchor.getY() + dy - groundY < 3" in crown
assert "for (int arm = 0; arm < 3; arm++)" not in crown
assert "distanceFromWood" in crown and "nextDistance > 6" in crown
assert "tryPlaceLeaf(level, setter, random, config, pos)" in crown
assert "isFruitCherryWood" in crown and "isFruitCherryWood" not in trunk
assert not any(f"fruit_cherry_tree" in x for x in world.splitlines()), "Do not activate wild generation yet"
assert not (D / "italiansdelight/worldgen/placed_feature/fruit_cherry_tree_wild.json").exists()

print("OK: unique 157-pixel cherry leaflet silhouette, six muted green shades, not Olive/Almond recolor")
print("OK: client-side biome foliage colors registered for Fruit Cherry; Almond tint preserved")
print("OK: sapling retains trunk geometry and uses six visibly varied leaf shades")
print("OK: cherry uses proven Almond/Olive trunk topology and a 20–25% wider rounded canopy")
print("OK: planted sapling growth remains enabled; branch/foliage clearance maintained")
print("OK: no fruit harvest, fruit drops, or wild worldgen before review")
