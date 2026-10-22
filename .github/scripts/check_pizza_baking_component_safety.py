#!/usr/bin/env python3
"""Guard lossless three-oven baking, minimal previews and single BE sync."""
from pathlib import Path
pizza = Path("src/main/java/dev/italiansdelight/common/pizza/PizzaBaking.java").read_text(encoding="utf-8")
raw = Path("src/main/java/dev/italiansdelight/common/crafting")
for x in ("PizzaSmeltingRecipe.java","PizzaSmokingRecipe.java"):
    source=(raw / x).read_text(encoding="utf-8")
    assert "return PizzaBaking.previewCookedPizza(input.item());" in source, x
    assert "PizzaBaking.finish(" not in source, x
assert "public static ItemStack previewCookedPizza(ItemStack rawPizza)" in pizza
a=pizza.index("public static ItemStack previewCookedPizza")
b=pizza.index("public static Optional<BakingResult> finish")
preview=pizza[a:b]
assert "Items.BOWL" not in preview
assert "PizzaDisplayNames.nameWholeStack(cooked, data);" in preview
for key in ("CUSTOM_NAME","LORE","CUSTOM_DATA"):
    assert f"rawPizza.has(DataComponents.{key})" in preview
    assert f"cooked.set(DataComponents.{key}, rawPizza.get(DataComponents.{key}));" in preview
assert "ItemStack cooked = previewCookedPizza(rawPizza);" in pizza[b:]
assert "if (application.remainingMask() != 0" in pizza[b:]
assert "new ItemStack(Items.BOWL, bowls)" in pizza[b:]
entity=Path("src/main/java/dev/italiansdelight/common/block/entity/PizzaDoughBlockEntity.java").read_text(encoding="utf-8")
topping=Path("src/main/java/dev/italiansdelight/common/pizza/PizzaToppingInteractions.java").read_text(encoding="utf-8")
assert "level.sendBlockUpdated(worldPosition," in entity
assert "server.sendBlockUpdated(" not in topping
print("OK: 3 ovens preserve name/lore/custom data and count bowls only on commit")
print("OK: topping updates are broadcast by one BlockEntity path")
print("NOTE: runtime, in particular anvil names and mod components, need client tests")
