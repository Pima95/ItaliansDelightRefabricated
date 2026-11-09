# New Olive Foods and Pizzas — Orchard 1.1.0

The author approved olive pitting, olive pâté, two **open** bruschettas and
two named pizzas. Focaccia is **excluded** until a future version.

| Recipe | Station | Ingredients | Output |
|---|---|---|---|
| Pit an Olive | FD Cutting Board + knife | 1 whole Olive | 1 Pitted Olive + 1 Olive Pit |
| Olive Pâté | Crafting, shapeless | 3 Pitted Olives, 1 Olive Oil Bottle, 1 Salt, 1 Bowl | 1 bowl of Olive Pâté; oil bottle returns a glass bottle |
| Tomato Bruschetta | Crafting, shapeless | 1 Bread, 1 Tomato Slice, 1 Basil | 1 Tomato Bruschetta |
| Olive Bruschetta | Crafting, shapeless | 1 Bread, 1 Olive Pâté | 1 Olive Bruschetta; empty Pâté bowl returned |
| Pizza Mediterranea | Original dynamic pizza recipes / assembly | Wheat Dough, Tomato Sauce, Mozzarella Slice, Pitted Olives, Basil | 1 recognized raw pizza, baking then serving yields 4 slices |
| Olive & Scamorza Bianca | Same original dynamic pizza system | Wheat Dough, Mozzarella Slice, Smoked Scamorza Slice, Pitted Olives | 1 recognized raw pizza, baking then serving yields 4 slices |

## Art and compatibility
- `bruschetta.png` was a sandwich-like **unused texture** (no item ID or crafting recipe). It has been redesigned as a single open toast with tomato and basil. `olive_bruschetta.png` shares that one-bread silhouette with an olive-green spread.
- Original `olive_seed.png` retained byte for byte and registered as `italiansdelight:olive_seed`. This is **not** an extra plantable tree species and does not generate olives.
- Newly drawn items are 16×16 with only **binary transparency** 0 or 255; new pizza toppings have a 12×12 four-variant sprite atlas.
- Canonical pizza toppings now total **32** types/36 native items (the new vegetable topping is prepared `olive_pitted`). Whole Olives, Olive Pit and Pâté are explicitly excluded.
- Pizza presets now total **20**. Both new pizzas use the existing component-backed crafting, baking, JEI, 3D rendering and single-slice functionality. Older 1.0.0 IDs, snapshots and original graphics are preserved.

## Tests to do in-game
- [ ] Cutting Board: one olive → one pitted olive + one pit; knife loses durability once, no duplication.
- [ ] Pâté recipe returns one glass bottle, and using Pâté for bruschetta returns its bowl exactly once.
- [ ] Both separate Bruschettas have no upper bread in the inventory artwork and are edible.
- [ ] Raw/cooked/slice pizza visual distinction, in-world olive sprite, dynamic crafting and recipe JEI.
- [ ] Reject unpitted olives, pits and pâté on pizza. Pitted olive removal refunds exactly the proper item.
- [ ] Slice portion sizes/effects, survival crafting, save/reload, multiplayer and dedicated server.
