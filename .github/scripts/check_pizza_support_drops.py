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
    "if (mask == PizzaQuarterSelection.ALL_QUARTERS)",
    "drops.add(PizzaSliceFactory.create(pizza, q));",
    "return remainingDrops(state,",
))
print("NOTE: verify scheduled tick and item drops in Survival")
