# Fruit Cherry signs — phase B (Minecraft 26.2)

**Branch:** `feature/orchards-1.1.0`
**Status:** implemented; artwork requires author approval and in-game QA.

## Blocks, items and recipes
- `fruit_cherry_sign` + `fruit_cherry_wall_sign`: one ordinary sign item,
  3 outputs from 6 `fruit_cherry_planks` and 1 stick.
- `fruit_cherry_hanging_sign` + `fruit_cherry_wall_hanging_sign`:
  one hanging sign item, 6 outputs from stripped Fruit Cherry logs
  and two vanilla iron chains.
- New registration in ModBlocks and ModSigns; vanilla sign and hanging-sign
  block entities support text editing, back text, wax and data persistence.
- Complete vanilla-compatible standing, wall, hanging, attached, and wall-hanging
  blockstates/models, item model definitions, loot and vanilla block/item tags.
- Italian and English names, two Creative items.

## Artwork — original cherry grain
The **8 new PNGs** are separately authored textures for block atlas (32×32),
inventory (16×16), GUI (24×26 and 16×16), and entity rendering (64×32).
They preserve only the working Minecraft 26.2 UV transparency layout and the
physical steel-chain design, not any almond wood colors, pixel motif, or glyph.
### Revised vanilla-friendly drawing (31 October 2026)
The original horizontal plank joins and the prominent repeated decorative
knot were removed from **both signs**. The standing sign is now a single
smooth wooden face, with short staggered horizontal cherry-grain fibers,
muted edge light and a matching vertical post. The hanging sign has its
**own separate** more compact grain rhythm, a subtly highlighted center
and unobtrusive bevel; neither sign copies the other's decorative pattern.
Both remain clear enough for the in-game text.

The item thumbnails, text-entry GUIs and 64×32 sign entity atlases were
redrawn consistently. All 8 alpha masks, both sign silhouettes and the
metallic chain pixels retain their previous positions and transparency.
The artwork is original, while the coordinate layouts and block models
remain the approved Minecraft 26.2 vanilla-compatible ones.

New regression checks assert the eight unchanged transparency masks and
limit cross-row brightness jumps to prevent a return of strong, full-width
board separations. This is a visual redesign only: no item IDs, models,
recipes, translations or gameplay logic were changed.

## Test and artistic review
The new script `.github/scripts/check_fruit_cherry_signs.py` checks 4 block
forms, 2 items, 16 rotations and 16 attached hanging states, data recipes,
language, valid vanilla sign block entities, 8 PNG dimensions, alpha, inventory
silhouette, and original artwork preservation via Git blob SHA fingerprints.
The workflow executes this validation before the Java 25 build.

- [ ] Check ordinary sign ground/wall geometry, orientation and legibility.
- [ ] Check hanging sign ceiling, attached and side-mount orientation and chain.
- [ ] Check both item icons against vanilla inventory scale and alignment.
- [ ] Check both text editing UIs, text, dye, glow ink, wax and double-sided text.
- [ ] Reload a world and dedicated server, verify persistence and recipes.
- [ ] Approve pixel art before boats, shelf, or cabinet.

**Frozen:** all five approved Fruit Cherry foundational textures, door/botola,
plus every existing Almond, Olive, other mod asset. No changes to `main`.
