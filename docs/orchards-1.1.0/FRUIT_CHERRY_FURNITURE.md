# Fruit Cherry — shelf, cabinet and cheese-aging rack

**Branch:** `feature/orchards-1.1.0` · **Status:** implemented and queued for CI and in-game validation.

## Three separate functional blocks

| Block | Registry ID | Gameplay |
| --- | --- | --- |
| Native Shelf | `italiansdelight:fruit_cherry_shelf` | Native Minecraft 26.2 `ShelfBlock`, 3 display slots, linked shelf/redstone behavior, vanilla block entity and model variants |
| Farmer's Delight Cabinet | `italiansdelight:fruit_cherry_cabinet` | Native Farmer's Delight `CabinetBlock` and shared block entity, 27 slots, container GUI, open/closed state, comparator, persistence |
| Cheese Aging Rack | `italiansdelight:fruit_cherry_cheese_aging_rack` | Existing Italian's Delight `CheeseAgingRackBlock` with two usable racks and persistent aging slots; uses authentic `fruit_cherry_planks` |

## Art and vanilla integration

The new 32×32 `fruit_cherry_shelf.png` atlas preserves the existing 26.2 native shelf UV layout and its power/connection overlays, with a new muted, copper-red Cherry wood palette and original subtle grain marks. The four 16×16 cabinet PNGs (closed front, open front, side and top) preserve functional door and cavity geometry while introducing Cherry-specific highlights and varied pixel-grain details.

The cheese aging rack requires **no new PNG**: its block model references the approved `textures/block/fruit_cherry_planks.png`, and retains the exact functional rack geometry and two-level aging behavior.

- Shelf recipe: 6 **stripped fruit-cherry logs** in two horizontal rows → 6 shelves.
- Cabinet recipe: 6 **fruit-cherry slabs** and 2 matching **fruit-cherry trapdoors** → 1 cabinet.
- Cheese rack recipe: 4 **fruit-cherry fences** and 1 **fruit-cherry planks** → 1 aging rack.
- Each block has an item, native blockstate/model, axe mining, drops, Italian/English name and Creative entry.
- Shelf participates in `#minecraft:wooden_shelves` (both block and item), enabling the same redstone adjacency logic as other wooden shelves.
- Cabinet reuses Farmer's Delight block entity; shelf reuses the vanilla `SHELF` block entity; rack joins the mod's existing `CHEESE_AGING_RACK` type.
- Rack recipe generator updated from 14 to 15 wood types and includes the new Cherry variant.
- Existing Cherry planks, signs, boats, door and trapdoor, and all Almond and Olive art **remain unchanged**. No changes to `main` or published `v1.0.0`.

## QA to complete in Minecraft

- [ ] Compare all five new textures with existing Fruit Cherry wood in natural and indoor light; inspect individual pixels/UVs for flat color regions and unnatural repeats.
- [ ] Shelf: display/remove each of three items, test multiple linked shelves, redstone-powered swaps, removal/drop and world reload.
- [ ] Cabinet: open/close, 27-slot inventory, breaking and drops, chest-like automation, comparator response, retained contents after reload.
- [ ] Cheese rack: position different fresh cheeses on both rows, advance aging, remove/restore cheeses and verify world persistence after restart.
- [ ] Verify both model orientations, inventory thumbnails and crafting recipes/JEI, with no missing textures.
- [ ] Verify dedicated server and multiplayer operation.

CI: `.github/scripts/check_fruit_cherry_furniture.py` verifies registrations, entity types, 5 PNGs and correct texture formats, recipes, all shelf model variants, tags, and SHA protection of approved artwork. Builds and these checks do **not** substitute for in-game testing.

## Original Cherry furniture redesign (November 1, 2026)

The earlier Fruit Cherry shelf and cabinet were predominantly recolors of
Almond. All **five decorative PNGs** are now independently redrawn, while
retaining Minecraft's exact models and functional UV layout:

- **Shelf 32×32:** the three original vanilla storage niches remain, with new
  triangular inner supports, short routed strokes, a subtle branching detail,
  staggered knots and distinctive lower wooden panels, avoiding Almond's
  uniform grain and continuous repeated motifs.
- **Cabinet front 16×16:** twin doors with three *horizontal* wooden slats
  each, restrained Cherry grain, and two readable muted-metal handles;
  no Almond-style vertically nested inset rectangles.
- **Cabinet open front 16×16:** matching frame and open storage compartments
  with two wood shelf dividers. The actual 27-slot cabinet behavior is unchanged.
- **Cabinet side 16×16:** joined horizontal boards with small wooden pegs and
  staggered grain rather than a single centered square.
- **Cabinet top 16×16:** four lengthwise planks with uneven end-grain joints,
  replacing the old Almond-like centered square.

All textures remain at authentic 16×16/32×32 pixel resolutions with opaque
alpha, pixel-level highlights/shadows and the approved muted copper-red
Fruit Cherry palette, without image interpolation.

**Frozen:** the cheese-aging rack's model/texture/behavior, every Almond and
Olive asset, all existing Cherry planks, signs, doors, boats, and the public
`main` and released v1.0.0. Static CI now validates that the *arrangement*
of tones (not merely the RGB palette) differs substantially from Almond,
and checks the cabinet's metal handles and short wooden slats.
