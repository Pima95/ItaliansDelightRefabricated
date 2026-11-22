#!/usr/bin/env python3
"""Static pizza support-loss regression checks.

This is not an integration test: manual Survival tests are still required.
"""
from pathlib import Path

BASE = Path("src/main/java/dev/italiansdelight/common/block")


def check(filename: str, loot_terms: tuple[str, ...]) -> None:
    path = BASE / filename
    src = path.read_text(encoding="utf-8")
    terms = (
        "direction == Direction.DOWN && !canSurvive(state, level, pos)",
        "ticks.scheduleTick(pos, this, 1);",
        "protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)",
        "if (!canSurvive(state, level, pos))",
        "level.destroyBlock(pos, true);",
        *loot_terms,
    )
    for term in terms:
        if term not in src:
            raise AssertionError(f"{filename}: missing {term}")
    if "return Blocks.AIR.defaultBlockState();" in src:
        raise AssertionError(f"{filename}: direct AIR removes the pizza without loot")
    shape_handler = src.split("protected BlockState updateShape(", 1)[1].split(
        "protected void tick(", 1)[0]
    if "destroyBlock(" in shape_handler:
        raise AssertionError(f"{filename}: do not destroy blocks during neighbor shape updates")
    print(f"OK: {filename} preserves BlockEntity until vanilla drops")


check("PizzaDoughBlock.java", (
    "LootContextParams.BLOCK_ENTITY",
    "hasPersistentPizzaContents(state, pizzaEntity)",
    "return List.of(createPizzaItem(state, pizzaEntity));",
    "return normalDrops;",
))
check("CookedPizzaBlock.java", (
    "LootContextParams.BLOCK_ENTITY",
    "if (mask == PizzaQuarterSelection.ALL_QUARTERS && !entity.servedEver())",
    "drops.add(PizzaSliceFactory.create(entity.sliceAt(q)));",
    "return remainingDrops(state,",
))
print("NOTE: verify scheduled tick and item drops in Survival")

// The pizza support rule must be IDENTICAL to Farmer's Delight FeastBlock
// canSurvive(), not an ad hoc vanilla block allowlist. All pizza placement
// and survival paths must flow through the same helper.
ROOT = Path("src/main/java/dev/italiansdelight")
support = (ROOT / "common/pizza/PizzaDoughSupport.java").read_text(encoding="utf-8")
assert "Block.canSupportRigidBlock(level, supportPos)" in support
assert "isCollisionShapeFullBlock" not in support
assert "instanceof SlabBlock" not in support
assert "instanceof StairBlock" not in support
assert "instanceof TrapDoorBlock" not in support
for block_name in ("PizzaDoughBlock.java", "CookedPizzaBlock.java"):
    code = (BASE / block_name).read_text(encoding="utf-8")
    assert "PizzaDoughSupport.canSupport(level," in code, block_name
    assert code.count("PizzaDoughSupport.canSupport(level,") == 1, block_name

placement = (ROOT / "common/pizza/PizzaDoughPlacement.java").read_text(encoding="utf-8")
for item in ("COOKED_PIZZA", "RAW_PIZZA", "WHEAT_DOUGH"):
    assert f"ModItems.{item}" in placement, item
assert placement.count(".canSurvive(level, target)") >= 2
assert ".canSurvive(level, pos)" in placement

slices = (ROOT / "common/pizza/PizzaSlicePlacement.java").read_text(encoding="utf-8")
assert "held.is(ModItems.PIZZA_SLICE)" in slices
assert "if (!placed.canSurvive(level, target))" in slices
assert "entity.insertSlice(quarter, slice);" in slices
print("OK: raw/formed/baked pizza and individual slices use FDR rigid supports")
