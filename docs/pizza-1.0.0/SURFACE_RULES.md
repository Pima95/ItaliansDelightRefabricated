# Pizza placement supports — same rule as Farmer's Delight Refabricated

**Updated 22 November 2026 for 1.2.0 / Fabric 26.3.**
This replaces the custom vanilla-only 10 October 2026 surface allowlist.
The author has requested the same supports accepted by placeable food in
Farmer's Delight Refabricated.

## Canonical rule

The upstream source
`vectorwing.farmersdelight.common.block.FeastBlock.canSurvive()`
on branch `fabric/latest/26.3` returns
`canSupportRigidBlock(level, pos.below())`.

Our equivalent lives in
`dev.italiansdelight.common.pizza.PizzaDoughSupport.canSupport()`
and delegates directly to **`Block.canSupportRigidBlock(level, supportPos)`**.

This is a world-aware, collision-face-based rule, not a fixed set of vanilla
block IDs. It therefore accepts modded furniture/cabinets when Minecraft
considers their upper face rigid enough, exactly as FDR does.

## Which pizza variants use the rule

* **Dough balls and all four stretching stages:** `PizzaDoughBlock.canSurvive`
* **Already prepared raw pizza (including mixed toppings):** the same dough block
* **Whole baked pizza:** `CookedPizzaBlock.canSurvive`
* **Standalone cooked quarter (pizza slice):** the same cooked pizza block
* **Reinserted cooked quarter:** keeps using an existing cooked pizza block

Both item placement callbacks (`PizzaDoughPlacement` and
`PizzaSlicePlacement`) use the block's `canSurvive`. Thus client/server
placement and support loss use the same validation.

No changes to entity NBT, saved composition, block IDs, textures, recipes,
stretching, baking, cutting, or drop rules.

## Testing (not yet performed by author)

Use the same surfaces to compare a Farmer's Delight placeable Feast against
Italian's Delight wheat dough, raw pizza, baked pizza and a loose cooked slice:

- [ ] Full solid blocks (stone, planks, etc.).
- [ ] Full-top supports such as **upper slabs** and supported top-half stair
      shapes, where FDR actually permits placement.
- [ ] Farmer's Delight cabinets and optional mod furniture/table blocks:
      only when the same block state supports an FDR feast.
- [ ] Non-supporting surfaces (bottom slabs, carpets, open trapdoors,
      unsupported furniture): should not work in either mod.
- [ ] Replace/remove the support under a raw pizza, an intact baked pizza
      and a partly served pizza; verify exactly one correctly composed raw
      pizza, one intact baked pizza, or only the remaining slices.
- [ ] Reload a chunk/world containing pizzas on a modded support, and check
      that the pizza survives whenever the support still passes the rule.
- [ ] Verify standalone slices can be reinserted into a partially served pizza.

The exact behavior of unusual blocks (anvils, soul sand, chests, custom
furniture) is dictated by FDR's **rigid support** predicate, not by the old
custom allowlist. If any such block differs from previous releases, this is
an intentional consequence of matching FDR and should be verified in-game.

Automated static regression:
`.github/scripts/check_pizza_support_drops.py`.
This checks delegation, all placement pathways, and preservation of
BlockEntity-backed drops. It does not simulate client clicking.

Upstream source:
https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.3/src/main/java/vectorwing/farmersdelight/common/block/FeastBlock.java
