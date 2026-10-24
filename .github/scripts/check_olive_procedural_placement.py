#!/usr/bin/env python3
"""Ensure first olive procedural generator is wired, and never world-generated."""
from pathlib import Path
import json
SRC=Path("src/main/java/dev/italiansdelight")
W=SRC/"common/worldgen"
DATA=Path("src/main/resources/data/italiansdelight/worldgen")
for filename in ("OliveTrunkPlacer.java","OliveFoliagePlacer.java","ModTreePlacers.java"):
    assert (W/filename).is_file(), filename
trunk=(W/"OliveTrunkPlacer.java").read_text()
foliage=(W/"OliveFoliagePlacer.java").read_text()
registry=(W/"ModTreePlacers.java").read_text()
init=(SRC/"ItaliansDelight.java").read_text()
assert "trunkPlacerParts(instance)" in trunk
assert "placeBelowTrunkBlock" in trunk
assert "new FoliagePlacer.FoliageAttachment(tip.above()" in trunk
assert "new FoliagePlacer.FoliageAttachment(topLog.above()" in trunk
assert "placeLog(level, trunkSetter" in trunk
# Regression: a branch below the bend must originate at the lower axis.
# A raised branch end must be stacked over its horizontal segment instead
# of starting diagonally, which would leave unsupported leaf clumps.
assert "branchY < bendLevel ? origin : center" in trunk
assert "BlockPos raisedTip = tip.above();" in trunk
assert "tip = raisedTip;" in trunk
assert "foliagePlacerParts(instance)" in foliage
assert "shouldSkipLocation" in foliage
assert "tryPlaceLeaf" in foliage
assert "BuiltInRegistries.TRUNK_PLACER_TYPE" in registry
assert "BuiltInRegistries.FOLIAGE_PLACER_TYPE" in registry
assert "ModTreePlacers.register()" in init
normal=json.loads((DATA/"configured_feature/olive_tree.json").read_text())
ancient=json.loads((DATA/"configured_feature/ancient_olive_tree.json").read_text())
assert normal["config"]["trunk_placer"]["type"]=="italiansdelight:olive_trunk_placer"
assert normal["config"]["foliage_placer"]["type"]=="italiansdelight:olive_foliage_placer"
assert ancient["config"]["trunk_placer"]["type"]=="minecraft:dark_oak_trunk_placer"
assert ancient["config"]["foliage_placer"]["type"]=="minecraft:dark_oak_foliage_placer"
assert not (DATA/"placed_feature/olive_tree_wild.json").exists()
assert not (DATA/"placed_feature/ancient_olive_tree_wild.json").exists()
wg=(SRC/"common/registry/ModWorldGeneration.java").read_text()
assert "OLIVE_TREE_WILD" not in wg and "ANCIENT_OLIVE_TREE_WILD" not in wg
print("OK: procedural 1x1 olive trunk, elevated branch foliage and both placer codecs registered")
print("OK: old 2x2 prototype retained; natural olive world generation stays DISABLED")
