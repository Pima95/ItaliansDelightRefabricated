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

## The ONLY new named pizza

**Pizza piccante alle olive**: tomato sauce, mozzarella slice,
pitted olives, existing `italiansdelight:chili_pepper`. Chili becomes
a verified solid topping. Its full-size pizza, raw pizza and cooked
quarter all retain the original item IDs and data-backed composition;
the 22nd named preset has dedicated 16×16 art.

No other named pizza preset is added: **Principessa** (tomato sauce,
mozzarella, prosciutto crudo and cream) can now be made as an ordinary
dynamic-name pizza but is **not a recognized taste yet**.

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
