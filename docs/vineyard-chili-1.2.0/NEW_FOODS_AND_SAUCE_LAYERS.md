# New prepared foods, spicy olive pizza and stacked sauce behavior — 1.2.0

## Scope
Use **existing ingredients only**. No new farms, food intermediates, cheese types or
machines. Focaccia is explicitly **deferred** until it can be baked and served
in-world. The six new ready-to-eat items listed below each have original
16×16 inventory art and retain the required vanilla bowl remainders.

| Item | Station | Recipe | Output / proposed nutrition |
|---|---|---|---|
| Spicy Olive Pasta | Farmer's Delight Cooking Pot | pasta, tomato sauce, chili pepper, 2 pitted olives | bowl, 12 hunger, 0.75 saturation, Medium Nourishment |
| Pasta with Ricotta and Pancetta | Cooking Pot | pasta, ricotta, pancetta slice, grated Parmigiano Reggiano | bowl, 14 hunger, 0.75 saturation, Long Nourishment |
| Risotto with Scamorza and Olives | Cooking Pot | rice, scamorza slice, 2 pitted olives, cream bowl | bowl, 12 hunger, 0.8 saturation, Medium Nourishment |
| Pasta with Almond Pesto and Speck | Cooking Pot | pasta, almond pesto, speck slice, chopped almonds | bowl, 14 hunger, 0.75 saturation, Long Nourishment |
| Grape and Cherry Fruit Salad | Crafting, shapeless | bowl, purple grapes, white grapes, 2 cherries | bowl, 6 hunger, 0.35 saturation |
| Almond Biscuits | Crafting, shapeless | 2 wheat, 2 chopped almonds, 1 egg, 1 sugar | 4 biscuits, 3 hunger each, 0.3 saturation |

Cooking Pot recipes consume a separate vanilla serving bowl. Ingredient
crafting remainders (ricotta/cream/pesto) must be verified in-game to avoid
duplicate or missing empty bowls.

## Recognized pizzas added in 1.2.0

**Pizza piccante alle olive**: tomato sauce, mozzarella slice,
pitted olives and existing `italiansdelight:chili_pepper`. The 22nd
named taste is backed by real 16×16 raw, cooked and slice models.

**Pizza Principessa**: tomato sauce, mozzarella slice, prosciutto crudo
slice and cream bowl. The 23rd recognized taste has its own real
raw, cooked and edible-slice 16×16 textures and remains backed by
the existing pizza item IDs, canonical topping components and oven.
Both spreads persist independently with the existing per-quarter
last-sauce-as-base / earlier-sauce-as-garnish rule.

## New sauce rule

Distinct creamy spreads and sauces may coexist (tomato, ricotta, cream).
For each physical quarter, the **last applied sauce is the full base**.
Previously applied sauces are displayed as small garnish fragments. This
is a *render-time* role change — no original item is lost or rewritten,
and the canonical ID, source stack, order, bowls, nutrition, names and
baking components remain intact. Identical toppings are still non-
duplicable, with a limit of six different types per physical quarter.

When Shift + empty hand removes the newest sauce, the preceding sauce
becomes the base again. All four quarters decide their sauce order
independently, including previously saved and mixed pizzas.

## Tests before release

* Client: tomato then cream, cream then tomato; use ricotta third. Removing
  the last input restores the previous full base with exact refund; no
  duplicate bowl after baking.
* Mixed pizza: different sauces on adjacent quarters, sliced and reunited;
  check local base, scattered garnish and physical quarter identities.
* Creative: inspect spicy olive pizza raw/cooked/slice and confirm native
  chili texture appears on the world block.
* Cooking Pot/JEI/recipe book: cook four meals and craft two snacks. Test
  empty bowl handling with partially full inventory.
* Save/reopen: old pizzas, new pizza with multiple sauces, removed support
  and three remaining slices. No missing toppings or extra drops.

## November 2026 art and recipe corrections

- `almond_biscuits.png` follows the exact single round cookie silhouette
  of Farmer's Delight Honey Cookie / vanilla-style cookie, in toasted almond
  colors. No stacked biscuits.
- `grape_cherry_fruit_salad.png` reuses the actual Mozzarella Salad shape,
  retaining its bowl/rim while recoloring the ingredients to white/purple
  grapes and cherries.
- `risotto_with_scamorza_and_olives.png` reuses the Mushroom Risotto shape
  and bowl (also consistent with Tomato Risotto), with golden grains, cream
  and pitted-olive speckles.
- `pasta_with_ricotta_and_pancetta.png` changes the residual pesto-green
  pasta to warm golden-yellow, preserving ricotta and pancetta.
- The shapeless `fermented/red_wine_amatriciana.json` crafting-grid recipe
  was removed. `cooking/pasta_all_amatriciana.json` is still the sole normal
  cooking recipe for Pasta all'Amatriciana in the Cooking Pot.

The above changes preserve original item registry IDs and existing saves.
