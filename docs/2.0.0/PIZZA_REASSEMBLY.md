# Pizza reassembly

Four cooked pizza slices in four occupied crafting slots (2×2 or 3×3, shapeless) make one cooked pizza. Any flavor combination is accepted. Reading occupied slots from top to bottom, left to right assigns the slices to the four north-facing quarters in visual row order. Each slice retains its own toppings and application order, including legacy mozzarella coverage. No dough, cooking, bowls or other remainders. Vanilla crafting consumes one slice per occupied slot.

JEI shows homogeneous examples for each recognized flavor and one mixed example via the same reassembly code used in gameplay.

## Manual regression checks (pending in-game)

- Four identical Margherita slices: cooked Margherita; after placing or cutting, four equivalent slices.
- Four different flavors: Pizza mista; place and inspect all four quarters, then cut and compare slice names/ingredients.
- Two plus two, four Bianca slices, and four copies from a stack split across slots.
- Four slices in noncontiguous crafting-table slots, and the 2×2 inventory grid.
- Three/five slices, four slices plus another item, or raw pizza: no result.
- Repeat reassemble/cut; no extra bowls, no ingredient or food duplication.
- Save/reload a mixed cooked pizza and its placed block; verify legacy mozzarella on its original quarter if legacy slices are available.
- Verify recipe discovery in JEI for named cooked pizzas and the example mixed pizza.
