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
        for token in ("state.getValue(CHEESE).stack()", "affectNeighborsAfterRemoval(",
                      "ModBlocks.CHEESE_HOOK_OCCUPIED_SPACE"):
            if token not in code:
                raise AssertionError(f"{name}: broken hook product/space cleanup")
        assert "params.getOptionalParameter(" not in code, (
            "Product drops must depend on saved BlockState, not an optional BlockEntity"
        )
    else:
        if "blockEntity.getPickupStack(" not in code:
            raise AssertionError(f"{name}: cheese item fallback lost")
    print(f"OK {name}: drop-aware support removal")

entities = Path("src/main/java/dev/italiansdelight/common/block/entity")
for name in ("CheeseHookBlockEntity.java", "AgingCheeseBlockEntity.java"):
    source = (entities / name).read_text(encoding="utf-8")
    entry = source.split("public static void serverTick(", 1)[1]
    checks = entry.index("if (!state.canSurvive(level, pos))")
    recipes = entry.index("level.destroyBlock(pos, true);")
    assert checks < recipes, f"{name}: missing orphan repair before processing"
    assert entry.index("return;", recipes) > recipes, f"{name}: do not tick after orphan drop"
    print(f"OK {name}: orphan from loaded chunks is removed through drop-aware destruction")

hook = (entities / "CheeseHookBlockEntity.java").read_text(encoding="utf-8")
hook_insert = hook.split("public boolean insert(", 1)[1].split("public ItemStack remove(", 1)[0]
hook_remove = hook.split("public ItemStack remove(", 1)[1].split("public ItemStack getCheeseDrop(", 1)[0]
assert "if (!level.setBlock(" in hook_insert and "return false;" in hook_insert
assert "if (!level.setBlock(" in hook_remove and "return ItemStack.EMPTY;" in hook_remove
assert hook_remove.index("if (!level.setBlock(") < hook_remove.index("return cheeseType.stack();")
assert hook_remove.index("if (!level.setBlock(") < hook_remove.index("clearReservedHangingSpace(level);")
print("OK hook: failed insert/remove never grants or consumes a product")

wheel = (BASE / "AgingCheeseBlock.java").read_text(encoding="utf-8")
pickup = wheel.split("protected InteractionResult useWithoutItem(", 1)[1].split("protected List<ItemStack> getDrops(", 1)[0]
assert "if (level.removeBlock(pos, false)" in pickup
assert pickup.index("level.removeBlock(pos, false)") < pickup.index("player.addItem(pickup)")
print("OK wheel: manual pickup requires successful block removal")
print("NOTE: manual Survival, Creative and dedicated-server tests are still required")
