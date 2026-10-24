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
assert "placeLog(level, trunkSetter" in trunk
# Low two/three-block stem, with forks connected on the 2nd/3rd block.
assert "int stemHeight = 2 + random.nextInt(2);" in trunk
assert "int firstForkY = Math.min(stemHeight - 1, 1 + random.nextInt(2));" in trunk
assert "int secondForkY = Math.max(1, firstForkY - (random.nextBoolean() ? 1 : 0));" in trunk
assert "for (int i = 0; i < 2; i++)" in trunk
# Opposing or angled main limbs, not high horizontal branches from a
# continuous main trunk like a vanilla cherry.
assert "first.getOpposite()" in trunk
assert "first.getClockWise()" in trunk and "first.getCounterClockWise()" in trunk
assert "placeAscendingBranch(" in trunk
assert "int length = 3 + random.nextInt(3);" in trunk
assert "int firstRise = 1 + random.nextInt(2);" in trunk
assert "BlockPos horizontal = tip.relative(outward);" in trunk
assert "BlockPos sideways = tip.relative(lateral);" in trunk
assert "BlockPos above = tip.above();" in trunk
assert "tip = horizontal;" in trunk and "tip = above;" in trunk
assert "state -> state.setValue(RotatedPillarBlock.AXIS, outward.getAxis())" in trunk
assert "state -> state.setValue(RotatedPillarBlock.AXIS, lateral.getAxis())" in trunk
assert "new FoliagePlacer.FoliageAttachment(tip.above()" in trunk
assert "new FoliagePlacer.FoliageAttachment(stemTop.above()" in trunk  # obstacle-only fallback
assert "outwardSteps >= 2 ? tip : null" in trunk
assert "foliagePlacerParts(instance)" in foliage
assert "shouldSkipLocation" in foliage
assert "tryPlaceLeaf" in foliage
assert "int radius = dy == 0 ? leafRadius : Math.max(1, leafRadius - 1);" in foliage
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
print("OK: low fork at block 2/3, exactly two principal rising face-connected limbs and separate crowns")
print("OK: tapered foliage lobes and both custom placer codecs registered")
print("OK: old 2x2 prototype retained; natural olive world generation stays DISABLED")
