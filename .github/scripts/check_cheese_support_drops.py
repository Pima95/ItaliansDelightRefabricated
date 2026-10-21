#!/usr/bin/env python3
"""Static regression: support removal must not erase cheese/hook drops."""
from pathlib import Path

BASE = Path("src/main/java/dev/italiansdelight/common/block")
for name, face in [("AgingCheeseBlock.java", "Direction.DOWN"),
                   ("CheeseHookBlock.java", "Direction.UP")]:
    code = (BASE / name).read_text(encoding="utf-8")
    for token in (
        "ticks.scheduleTick(pos, this, 1);",
        "protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)",
        "if (!canSurvive(state, level, pos))",
        "level.destroyBlock(pos, true);",
        "protected List<ItemStack> getDrops(",
        face,
    ):
        if token not in code:
            raise AssertionError(f"{name}: missing {token}")
    if "return Blocks.AIR.defaultBlockState()" in code or "return Blocks.AIR\n" in code:
        raise AssertionError(f"{name}: direct AIR bypasses loot")
    if name.startswith("CheeseHook"):
        for token in ("hook.getCheeseDrop(", "affectNeighborsAfterRemoval(",
                      "ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE"):
            if token not in code:
                raise AssertionError(f"{name}: broken hook inventory/space cleanup")
    else:
        if "blockEntity.getPickupStack(" not in code:
            raise AssertionError(f"{name}: cheese item fallback lost")
    print(f"OK {name}: drop-aware support removal")
print("NOTE: static audit only; manual Survival and server tests remain required")
