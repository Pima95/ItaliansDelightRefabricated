# Rustic Crushing Tub — first interactive port (Minecraft 26.2)

## Verified source
Original Rustic 1.12.2 repository `src/main/java/rustic/common/crafting/Recipes.java`,
`addCrushingTubRecipes()`:

| Ingredient | Fluid | mB per stomp | Byproduct |
| - | - | -: | - |
| Olives | Olive oil | 250 | — |
| Apples | Apple juice | 250 | Apple seeds |
| Sugar cane | Water | 250 | 2 sugar |
| Grapes | Grape juice | 250 | — |
| Wildberries | Wildberry juice | 250 | — |
| Ironberries | Ironberry juice | 250 | — |
| Honeycomb | Honey | 250 | — |
| Golden apple | Golden apple juice | 100 | Apple seeds |
| Enchanted golden apple | Golden apple juice | 1000 | Apple seeds |
| Vanta lily | Vanta oil | 250 | — |

Source: https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/Recipes.java

## Engine in this commit
- **Already operable:** olive -> 250mB olive oil; sugar cane -> 250mB water and 2 sugar.
- One ingredient per landing; only server processes an actual living entity fall.
- Up to 64 same-type ingredients, and up to 8,000mB one fluid in the same tub.
- Right-click with ingredients to deposit; right-click empty hand to retrieve unprocessed items;
  sneak-right-click empty hand to discard fluid; right-click with empty glass bottle/bucket to extract
  only if a compatible container exists.
- No mixing fluids, no item consumption when a tank is full, no consumption in creative inventory
  on insert; input stack and fluid are persisted and synchronized by the block entity.
- Original hollow collision and all 15 wood assets unchanged.
- Dropped block returns unprocessed input; consumed ingredients remain consumed.
- Apple juice and other missing fluids are **not** approximated as water/oil.
  Their recipes are deferred until their real fluid IDs and assets are added.
- The old static `check_crushing_tub_26_2.py` needs updating: the tub is no longer an empty
  decorative `Block`, but a `BaseEntityBlock` with persistent state.

## In-game tests
1. Put 4 olives into tub, jump 4 times, get 1000mB and scoop one oil bucket.
2. 32 olives -> 8000mB (maximum); 33rd stomp must not consume an olive.
3. Insert another fluid input into a nonempty tub: no fluid mixing and no consumption.
4. Put sugar cane into an empty tub; every jump adds 250mB water and drops 2 sugar.
5. Use bottles, empty hand and shift-empty-hand with 15 wood tubs.
6. Reload the world while the tub holds items and fluid; verify exact quantities.
