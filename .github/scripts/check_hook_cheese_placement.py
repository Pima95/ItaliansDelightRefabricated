#!/usr/bin/env python3
"""Static regression for BUG-001: whole Scamorza and Provolone are hook-only.

Minecraft interaction order, saving and drying/aging are tested in game.
"""
from pathlib import Path

BASE = Path("src/main/java/dev/italiansdelight/common")
enum = (BASE / "aging/AgingCheeseType.java").read_text(encoding="utf-8")
item = (BASE / "item/AgingCheeseItem.java").read_text(encoding="utf-8")
hook = (BASE / "aging/HangingCheeseType.java").read_text(encoding="utf-8")
block = (BASE / "block/CheeseHookBlock.java").read_text(encoding="utf-8")

# Check the actual complete eligibility expression, not just a comment.
assert "return this != PROVOLONE && this != SCAMORZA && this != SMOKED_SCAMORZA;" in enum
assert "if (!cheeseType.agesOnSurface()) {" in item
guard_pos = item.index("if (!cheeseType.agesOnSurface()) {")
placement_pos = item.index("serverLevel.setBlock(")
assert guard_pos < placement_pos
assert "return InteractionResult.PASS;" in item[guard_pos:guard_pos + 135]

for field in ("PARMIGIANO_REGGIANO", "PECORINO_ROMANO", "GORGONZOLA"):
    assert field in enum, f"Surface wheel cheese {field} missing"

for field in ("FRESH_SCAMORZA", "SCAMORZA", "SMOKED_SCAMORZA",
              "FRESH_PROVOLONE", "PROVOLONE"):
    assert f"if (stack.getItem() == ModItems.{field})" in hook, (
        f"Hook pickup mapping for {field} must be preserved"
    )

assert "HangingCheeseType.fromStack(" in block
assert "hook.insert(" in block
print("OK: only eligible wheel cheeses can enter surface aging placement")
print("OK: both Provolone and all Scamorza stages retain hook compatibility")
print("NOTE: must verify placement, hook pickup and aging in a real Survival world")
