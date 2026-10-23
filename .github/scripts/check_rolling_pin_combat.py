#!/usr/bin/env python3
"""Guard rolling-pin combat stats against pre-bootstrap vanilla stack crashes.

This check intentionally runs BEFORE Gradle. Successful compilation alone
cannot detect the Components-not-bound-yet crash when launching runClient.
"""
from pathlib import Path

source = Path(
    "src/main/java/dev/italiansdelight/common/registry/ModItems.java"
).read_text(encoding="utf-8")

start = source.index("// Minecraft 26.2 binds vanilla item default components")
end = source.index("// Pasta with tomato sauce", start)
block = source[start:end]

assert "Items.WOODEN_SHOVEL.getDefaultInstance()" not in block.replace(
    "// mod registration. Reading Items.WOODEN_SHOVEL.getDefaultInstance()", ""
), "Do not construct any vanilla ItemStack inside static item registration!"
assert "new Item.Properties()" in block
assert ".durability(64)" in block, "Rolling pin still must have 64 uses"
assert "ItemAttributeModifiers.builder()" in block
assert "Item.BASE_ATTACK_DAMAGE_ID" in block
assert "1.5F + ToolMaterial.WOOD.attackDamageBonus()" in block
assert "Item.BASE_ATTACK_SPEED_ID" in block
assert "-3.0F" in block
assert block.count("EquipmentSlotGroup.MAINHAND") == 2
assert ".attributes(ROLLING_PIN_COMBAT_ATTRIBUTES)" in block
assert ".shovel(" not in block, "A rolling pin must not gain shovel mining properties!"
print("OK: rolling pin has wooden-shovel attack stats without early vanilla ItemStack access.")
