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
- **Already operable:** olive -> 250mB olive oil; apple -> 250mB apple juice and one apple seed; sugar cane -> 250mB water and 2 sugar.
- One ingredient per landing; only server processes an actual living entity fall.
- Up to 64 same-type ingredients, and up to 8,000mB one fluid in the same tub.
- Right-click with ingredients to deposit; right-click empty hand to retrieve unprocessed items;
  sneak-right-click empty hand to discard fluid; right-click with empty glass bottle/bucket to extract
  only if a compatible container exists.
- No mixing fluids, no item consumption when a tank is full, no consumption in creative inventory
  on insert; input stack and fluid are persisted and synchronized by the block entity.
- Original Rustic **full-width 9/16-high landing collision** restored so a jump at the middle invokes fallOn. The hollow model, five physical-looking parts, all fifteen wood textures and visual selection shape are retained unchanged; this is the invisible interaction plane Rustic actually uses.
- Dropped block returns unprocessed input; consumed ingredients remain consumed.
- Apple juice is a distinct registered still/flowing fluid with Rustic's animated sprites, own vanilla-shaped recolored bucket and potion-style bottle. The seed byproduct is currently an item; planting apple trees is a separate future feature. Other missing fruits remain deferred, never approximated as water.
- The old static `check_crushing_tub_26_2.py` needs updating: the tub is no longer an empty
  decorative `Block`, but a `BaseEntityBlock` with persistent state.

## In-game tests
1. Put 4 olives into tub, jump 4 times, get 1000mB and scoop one oil bucket.
2. 32 olives -> 8000mB (maximum); 33rd stomp must not consume an olive.
3. Insert another fluid input into a nonempty tub: no fluid mixing and no consumption.
4. Put sugar cane into an empty tub; every jump adds 250mB water and drops 2 sugar.
5. Use bottles, empty hand and shift-empty-hand with 15 wood tubs.
6. Reload the world while the tub holds items and fluid; verify exact quantities.

- Fluid PNG sprites imported from Rustic 1.12.2 (source: https://github.com/cadaverous-eris/Rustic) consistent with the existing olive-oil art. Vanilla water bucket recolored ONLY in the liquid palette for apple juice; metal kept identical. All new bucket/item pixels alpha 0/255.

## Follow-up: original collision and item visibility fixes
The original Rustic source `BlockCrushingTub#getBoundingBox` returns `TUB_AABB =
(0,0,0)-(1,0.5625,1)`: a continuous collision box over the hollow-looking
tub. The first port used five hollow collision sub-shapes, making center landings
resolve against the terrain underneath while the higher rim still activated
`fallOn`. `getCollisionShape` now returns Rustic's continuous 9/16-high
collision, while `getShape` preserves the visible five-piece outline.

The ingredient renderer now resolves in `ItemDisplayContext.NONE` and tilts
flat item art upwards by -90° in the same model-space convention already used
for the pizza peel item overlay. The previous FIXED item-frame transform and
+90° tilt could hide sprites beneath the wooden bottom. A representative item
is drawn for every eight pending ingredients, up to eight levels; the underlying
inventory remains stored/synchronized in the block entity.

In-game checks: jump in the **center** and on the **rim** of Oak and a second
wood type. Every landing must consume exactly one item, with neither idle
standing nor a single landing consuming multiple items. Verify at least one
olive, apple and sugar cane ingredient visible before stomping; verify the stack
shrinks after each successful landing and persists after reload.
