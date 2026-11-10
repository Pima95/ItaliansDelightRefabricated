# Orchard 1.1.0 — Two pestos, two pastas and a placeable almond cake

## Recipes

| Result | Workstation | Ingredients |
| --- | --- | --- |
| 2 chopped almond portions | FD Cutting Board | 1 Almond + knife |
| Basil Pesto | Crafting | Bowl, 3 Basil, grated Parmigiano, Olive Oil Bottle, Salt |
| Almond & Basil Pesto | Crafting | Bowl, 2 Chopped Almonds, 2 Basil, grated Parmigiano, Olive Oil Bottle |
| Pasta with Basil Pesto | FD Cooking Pot | Pasta (c:foods/pasta), Basil Pesto; serve with Bowl |
| Pasta with Almond Pesto | FD Cooking Pot | Pasta (c:foods/pasta), Almond Pesto; serve with Bowl |
| Placeable Almond Cake | Crafting 3x3 | 6 Almonds, 2 Milk drinks (#c:drinks/milk), 1 Farmer's Delight Pie Crust |
| Whole cake from slices | Crafting 2x2 | 4 Almond Cake Slices, arranged in a square |
| Four cake slices | FD Cutting Board | 1 Almond Cake + knife |
| Mortadella, Ricotta & Almonds Pizza | Original dynamic pizza preparation | Ricotta, Mortadella Slices, Chopped Almonds |

## Corrected item identity
- The already present `italiansdelight:pasta_with_pesto` item and **original texture** represent **Basil Pesto** pasta; its recipe now consumes `italiansdelight:basil_pesto`.
- `italiansdelight:pasta_with_almond_pesto` is a brand-new separate item, cooking recipe, translation and 16×16 bowl sprite.
- Two separate 16×16 bowls `basil_pesto` and `almond_pesto` use the visual silhouette of Farmer's Delight's dark `tomato_sauce` bowl, but have visibly different bright-green basil and olive/cream almond fillings.
- `italiansdelight:almond_cake` remains the same ITEM identifier, but now is registered as a *placeable BlockItem*. ModItems must **not** separately register another Item with this identifier.

## Placed cake
The placeable cake uses the Farmer's Delight **Apple Pie** four-portion model family and has 4 saved `bites` states, rotation, and 12×12×4-height collision. Empty-hand right click while hungry consumes one edible serving (4 hunger, saturation modifier 0.5). Clicking with a knife (c:tools/knife) instead cuts and drops one Almond Cake Slice; the cake shows progressively fewer pieces. Removing the final piece removes the block; mining a whole cake returns the whole cake, while mining a partial cake returns only remaining slices. The existing FD Cutting Board recipe still converts a full cake into exactly four edible slices.

## Visual references and attribution
The bowl and pie art are adaptations of original **Farmer's Delight** textures by vectorwing, branch `1.21` (MIT license): `textures/item/tomato_sauce.png`, `apple_pie.png`, `apple_pie_slice.png` and `textures/block/apple_pie_top.png`, `apple_pie_inner.png`, `pie_side.png`, `pie_bottom.png`. Block models use bundled `italiansdelight:block/template_almond_cake*` four-piece geometry adapted from Farmer's Delight, so no runtime version-specific model parent is required. The in-mod original `pasta_with_pesto.png` is unchanged. All newly created PNG assets must have strictly binary alpha (0 or 255).

## Manual tests before release
- [ ] Both pesto bowl crafting recipes return the glass bottle from olive oil; basil pesto must not require almonds
- [ ] FD Cooking Pot: each pesto produces the appropriate separate Pasta bowl, no lost or duplicated bowls
- [ ] Craft/place Almond Cake, verify whole/partial 3D states and facing; eat four portions
- [ ] Cut four portions with knife, break partially consumed block, verify exact remaining count
- [ ] Cutting Board: whole Almond Cake -> 4 slices; no extra whole cakes or bowls
- [ ] Check JEI/Creative, translations, older saves and dedicated server
- [ ] Focaccia, grapes and grape juice remain out of scope

## Pie crust and rebuilding whole cakes (10 November 2026)

Follow the Farmer's Delight Refabricated Sweet Berry Cheesecake pattern: 6 Almonds in two rows; milk, `farmersdelight:pie_crust`, milk along the bottom. The milk input accepts `#c:drinks/milk`. Four slices in a 2×2 crafting pattern reconstruct one whole placeable Almond Cake. Cutting Board and in-world knife cutting still produce four edible slices. Test the milk container remainders in game.
