# Orchard 1.1.0 — Almond foods and pizza

New almond preparations use existing Farmer's Delight and Italian's Delight systems. No new machines.

| Result | Method | Ingredients |
| --- | --- | --- |
| 2 Chopped Almonds | FD Cutting Board, knife | 1 Almond |
| 1 Almond & Basil Pesto | Shapeless crafting | Bowl, 2 Chopped Almonds, 2 Basil, Grated Parmigiano, Olive Oil Bottle |
| 1 Pasta with Almond Pesto | FD Cooking Pot | Pasta (tag `c:foods/pasta`), Almond Pesto, serving Bowl |
| 1 Almond Cake | 3×3 crafting | 3 Almonds, 2 Sugar, Milk Bucket, 2 Wheat, Egg |
| 4 Almond Cake Slices | FD Cutting Board, knife | 1 whole Almond Cake |
| Mortadella Ricotta & Almond Pizza | Existing dynamic pizza preparation | Ricotta, Mortadella Slices, Chopped Almonds, Wheat Dough; no tomato sauce |

The full cake is a *cuttable intermediate*; only the four slices are edible. Vanilla crafting should return a bucket from Milk Bucket and a glass bottle from Olive Oil Bottle. Pesto is a non-edible bowl-based intermediate; it carries an empty Bowl remainder as in other bowl-based recipes in the project. Because the Cooking Pot is a separate handler, test bowl recovery in-game.

**Artwork:** reuses original `pasta_with_pesto.png`, and gives the four new items their own 16×16 art. The named pizza uses original Mortadella/Ricotta shapes/crust with added almond fragments and a new 12×12 (4-variation) topping sprite atlas. Binary alpha 0 or 255 everywhere.

**Scope and compatibility:** 21 recognizable pizza flavors (previously 20); 33 canonical pizza topping types and 37 native mappings. Whole Almonds, Pesto and Cake are *not* pizza toppings, only chopped Almonds. Existing food IDs, worldgen, Rustic Crushing Tub behavior and grapes/focaccia deferral remain unchanged.

## In-game checks (not yet marked as passed)

- [ ] Cutting Board: 1 Almond → 2 granella; cake → exactly 4 slices, with one knife action each
- [ ] Pesto crafting returns 1 glass bottle; Cooking Pot uses pasta/pesto and serves in a bowl without duplicating containers
- [ ] Cake craft returns 1 empty bucket; cake slices are edible and do not duplicate the whole cake
- [ ] New pizza uses only chopped almonds; shape and placement render on all four quarters and survive save/reload
- [ ] JEI, Italian/English names, Creative, dedicated server and original 20 pizza flavors remain functional
