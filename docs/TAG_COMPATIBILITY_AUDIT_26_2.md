# Audit tag JEI / Minecraft / Fabric — 26.2

This inventory is based on the *existing repository tags* and Fabric Conventional Tags v2. JEI's tag-browser is a visualization of Minecraft's loaded data-pack tags; it is not a separate JEI tag registry. Tags merge across mods/datapacks when `replace: false`.

## Existing tags already in the repository

- Vanilla Minecraft wood families: planks, leaves, saplings, logs_that_burn, stairs, slabs, doors, trapdoors, wooden_fences, fence_gates, buttons, pressure_plates, signs, hanging_signs and boats; block and item tags as applicable.
- Mining and burning: `minecraft:mineable/axe`, `minecraft:mineable/pickaxe`, `minecraft:logs_that_burn`.
- Conventional food and ingredients: `c:foods/cheese`, `c:foods/curd`, `c:crops/basil`, `c:crops/cardoon`, `c:drinks/milk`, `c:dusts/salt`, `c:rennet`, `c:storage_blocks`.
- Mod-owned categories: logs, meat, milk and fluid families; seasonal crop tags.

## New, verified semantically appropriate tag mappings

| Tag | Type | Added mod content | Why |
| --- | --- | --- | --- |
| `c:fences/wooden` | block + item | olive, almond, fruit_cherry fences | Compatible with the JEI tag-grouping screenshot |
| `c:fence_gates/wooden` | block + item | olive, almond, fruit_cherry fence gates | Match the established Fabric V2 wooden fence-gate family |
| `c:barrels/wooden` | block + item | 15 Rustic-style wooden Barrel variants | Wooden barrel category; fluid handling remains specific to this mod |
| `c:crops` | item | existing basil/cardoon crop tags plus harvested olives, almonds, cherries | Harvested crops/products (tag merges with Fabric) |
| `c:fruits/olive` | item | olives | Used by cross-mod fruit tag conventions |
| `c:fruits/cherry` | item | cherries | Used by cross-mod fruit tag conventions |

## Intentionally not assigned

- `c:foods/fruits` for olives/almonds/cherries: these items currently use ordinary non-edible Item properties. Marking them as edible food would be misleading.
- `c:drinks/juice` for apple juice: the current bottle is not configured as a drinkable consumable.
- `c:seeds` for apple seeds: planting apple trees is still a future feature.
- `c:foods/cooked_meats` for cured meats: curing is not equivalent to cooking.
- Unofficial guessed generic wood tags (`c:planks`, `c:stairs/wooden`, etc.) are not introduced when their expected semantics are unverified; Minecraft vanilla wood tags already cover these elements.
- No tags are added to mechanical Crushing Tubs or cheese machines as generic storage barrels/chests; those are not equivalent inventories.

## Validation

1. In JEI's tag browser, `c:fences/wooden` should now include our three fences, and `c:fence_gates/wooden` the gates.
2. `c:barrels/wooden` should contain the Rustic barrel variants.
3. Confirm crops/fruits grouped without changing gameplay mechanics or recipes.
4. Check other datapacks continue merging values; JSON uses `replace: false`.
5. The dedicated Crushing Tub GUI is 176x60 RGBA, uses alpha only 0 or 255, and places inputs at (16,21), fluid outputs at (114,21), byproducts at (145,21). No clock or overlapping decorative pseudo-slots.

References: https://wiki.fabricmc.net/tutorial:tags and https://wiki.fabricmc.net/community:common_tags
