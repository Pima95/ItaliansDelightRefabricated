#!/usr/bin/env python3
"""Check the CURRENT olive tree layout and prevent premature world generation.

This is a static regression check, not a substitute for compiling Java or
inspecting the canopy and sapling growth in Minecraft.
"""
from pathlib import Path
import json

SRC = Path("src/main/java/dev/italiansdelight")
W = SRC / "common/worldgen"
DATA = Path("src/main/resources/data/italiansdelight/worldgen")

for filename in ("OliveTrunkPlacer.java", "OliveFoliagePlacer.java", "AncientOliveTrunkPlacer.java", "AncientOliveFoliagePlacer.java", "ModTreePlacers.java"):
    assert (W / filename).is_file(), filename

trunk = (W / "OliveTrunkPlacer.java").read_text()
foliage = (W / "OliveFoliagePlacer.java").read_text()
ancient_trunk = (W / "AncientOliveTrunkPlacer.java").read_text()
ancient_foliage = (W / "AncientOliveFoliagePlacer.java").read_text()
registry = (W / "ModTreePlacers.java").read_text()
init = (SRC / "ItaliansDelight.java").read_text()

# Keep the explicitly planned tree shape, connected limbs and obstruction
# checks; do not demand obsolete variable names from the previous generator.
for token in (
    "trunkPlacerParts(instance)",
    "placeBelowTrunkBlock",
    "placeLog(level, trunkSetter",
    "FORK_VARIANT_CHANCE = 10",
    "TALL_SINGLE_CHANCE = 10",
    "int trunkHeight = !forked",
    "4 + random.nextInt(2)",
    "planTree(origin, trunkHeight, forked, lean, turn, upperKink)",
    "canPlacePlan(level, origin, plan.barkPositions())",
    "placeBark(level, trunkSetter",
    "lowerFork = forkBase.relative(secondary)",
    "secondaryTip = outerFork.above()",
    "new FoliagePlacer.FoliageAttachment(plan.mainTip().above()",
    "new FoliagePlacer.FoliageAttachment(",
):
    assert token in trunk, f"OliveTrunkPlacer missing expected behavior: {token}"
assert "plan.secondaryTip() != null" in trunk

# A softly tapered, irregular canopy must compile structurally and stay
# attached to its actual olive trunk; no isolated leaves or floating pairs.
for token in (
    "foliagePlacerParts(instance)",
    "shouldSkipLocation",
    "tryPlaceLeaf",
    "double rZero   = 2.95D * radiusScale;",
    "for (int dy = minDy; dy <= 2; dy++)",
    "if (Math.abs(dx) >= 2 && Math.abs(dz) >= 2",
    "waveAmp * Math.cos(waveFreq * theta + wavePhase)",
    "hasGroundClearance(level, leafPos)",
    "Deque<BlockPos> frontier",
    "while (!frontier.isEmpty())",
    "planned.contains(next) && connected.add(next)",
    "connected.contains(pos)",
):
    assert token in foliage, f"OliveFoliagePlacer missing expected behavior: {token}"

# Catch unmatched braces that previously stopped compilation before the CI
# build step. Java compilation remains the final authority on syntax.
def assert_braces_balanced(content, filename):
    depth = 0
    for ch in content:
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            assert depth >= 0, f"{filename}: extra closing brace"
    assert depth == 0, f"{filename}: {depth} unclosed braces"

assert_braces_balanced(trunk, "OliveTrunkPlacer.java")
assert_braces_balanced(foliage, "OliveFoliagePlacer.java")
assert_braces_balanced(ancient_trunk, "AncientOliveTrunkPlacer.java")
assert_braces_balanced(ancient_foliage, "AncientOliveFoliagePlacer.java")
for token in (
    "trunkPlacerParts(instance)", "BOLE_HEIGHT = 3", "MAIN_LIMBS = 4",
    "y == BOLE_HEIGHT - 1 ? upper : origin",
    "for (int dx = 0; dx < 2; dx++)", "for (int dz = 0; dz < 2; dz++)",
    "hasClearSpace(level, origin, plan)", "origin.relative(lean)",
    "logs.put(root, true)", "knotA", "knotB",
    "int rootY = attachmentHeight[i]",
    "tip = tip.relative(outward)", "tip = tip.relative(second)",
    "placeBelowTrunkBlock", "new FoliagePlacer.FoliageAttachment(tip.above()",
    "return List.of();",
):
    assert token in ancient_trunk, f"Ancient trunk missing {token}"
for token in (
    "foliagePlacerParts(instance)", "for (int dy = -2; dy <= 2; dy++)",
    "hasGroundClearance", "while (!frontier.isEmpty())",
    "connected.contains(leaf)", "tryPlaceLeaf",
):
    assert token in ancient_foliage, f"Ancient foliage missing {token}"
# The old giant, straight 5-9-block bole caused the baobab silhouette.
assert "stemHeight = Math.max(5, treeHeight - 4)" not in ancient_trunk
assert "for (int y = 0; y < stemHeight; y++)" not in ancient_trunk
assert "BOLE_HEIGHT = 3" in ancient_trunk
assert "attachmentHeight[i] = 1 + ((i + lowFirst) % 2)" in ancient_trunk
assert "if (i == 3)" in ancient_trunk and "logs.put(root, true)" in ancient_trunk
# Regression: ground-level buttresses made branches appear from block one.
# The only y=0 wood must be the original 2x2 base; all lateral wood starts
# at y>=1 (second level), including the asymmetric buttress knots.
assert "BlockPos root = edge.relative(side).above();" in ancient_trunk
assert "BlockPos root = edge.relative(side);" not in ancient_trunk
assert "logs.put(layer.offset(dx, y, dz), y != 0);" in ancient_trunk
assert "attachmentHeight[i] = 1 + ((i + lowFirst) % 2)" in ancient_trunk
assert "tip = tip.relative(outward);" in ancient_trunk
assert "logs.put(root.above(), true);" in ancient_trunk
assert "ANCIENT_OLIVE_TRUNK" in registry
assert "ANCIENT_OLIVE_FOLIAGE" in registry

assert "BuiltInRegistries.TRUNK_PLACER_TYPE" in registry
assert "BuiltInRegistries.FOLIAGE_PLACER_TYPE" in registry
assert "ModTreePlacers.register()" in init

normal = json.loads((DATA / "configured_feature/olive_tree.json").read_text())
ancient = json.loads((DATA / "configured_feature/ancient_olive_tree.json").read_text())
assert normal["config"]["trunk_placer"]["type"] == "italiansdelight:olive_trunk_placer"
assert normal["config"]["foliage_placer"]["type"] == "italiansdelight:olive_foliage_placer"
assert ancient["config"]["trunk_placer"]["type"] == "italiansdelight:ancient_olive_trunk_placer"
assert ancient["config"]["foliage_placer"]["type"] == "italiansdelight:ancient_olive_foliage_placer"
assert not (DATA / "placed_feature/olive_tree_wild.json").exists()
assert not (DATA / "placed_feature/ancient_olive_tree_wild.json").exists()
wg = (SRC / "common/registry/ModWorldGeneration.java").read_text()
assert "OLIVE_TREE_WILD" not in wg and "ANCIENT_OLIVE_TREE_WILD" not in wg

print("OK: planned 4-6-block olive stems with connected, occasional low forks")
print("OK: rounded tapering canopy, terrain clearance and connected leaves")
print("OK: custom normal and monumental 2x2 placers registered; sapling-only")
print("OK: natural olive world generation remains DISABLED")
