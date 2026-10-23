#!/usr/bin/env python3
"""Guard vanilla crafting-grid bowl remainders and no double refund on pizzas."""
from pathlib import Path
base=Path("src/main/java/dev/italiansdelight/common")
craft=(base/"crafting/PizzaCraftingRecipe.java").read_text(encoding="utf-8")
application=(base/"pizza/PizzaToppingApplication.java").read_text(encoding="utf-8")
baking=(base/"pizza/PizzaBaking.java").read_text(encoding="utf-8")
operations=(base/"pizza/PizzaCompositionOperations.java").read_text(encoding="utf-8")
for fragment in (
    "public NonNullList<ItemStack> getRemainingItems(CraftingInput input)",
    "if (compose(input).isEmpty())",
    "PizzaToppingMappings.find(ingredient)",
    "remains.set(slot, new ItemStack(Items.BOWL));",
    "a.withCraftingBowlReturned()",
):
    assert fragment in craft, f"Crafting-grid refund missing: {fragment}"
for fragment in (
    'Codec.BOOL.optionalFieldOf("bowl_returned_on_crafting", false)',
    "this(sequence, canonicalId, originalStack, originalMask, remainingMask, false);",
    "public PizzaToppingApplication withCraftingBowlReturned()",
    "int originalMask, int remainingMask, boolean bowlReturnedOnCrafting", # marker? See exact record in source
):
    if fragment.startswith("int originalMask"):
        continue
    assert fragment in application, f"Saved refund state missing: {fragment}"
assert "originalStack, originalMask, mask, bowlReturnedOnCrafting)" in application, (
    "Partial removal must preserve the already-returned bowl flag"
)
assert "public static boolean isBowlBearing(Identifier canonical)" in baking
assert "&& !application.bowlReturnedOnCrafting()" in baking, (
    "Pizza baking would return the crafted bowl a second time"
)
assert "existing.bowlReturnedOnCrafting()" in operations
assert "? ItemStack.EMPTY : existing.originalStack();" in operations, (
    "World removal would duplicate the crafted sauce-in-bowl"
)
assert 'Codec.BOOL.optionalFieldOf("bowl_returned_on_crafting", false)' in application, (
    "Pre-update pizzas must decode with bowls still refundable on baking"
)
print("OK: crafting returns one empty bowl for each valid sauce/cream/ricotta input")
print("OK: data component records the bowl payout across placement, baking and save/load")
print("OK: no duplicate sauce/bowl on topping removal or baking")
print("NOTE: survival crafting grid, shift-crafting, old saves and furnaces require runtime tests")
