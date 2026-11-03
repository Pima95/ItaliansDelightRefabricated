# Fruit Cherry tree — planted growth, Orchard 1.1.0

**Branch:** `feature/orchards-1.1.0` · **Status:** implementation awaiting GitHub CI and in-game visual acceptance.

## Approved separation of species

This is **not** the pink-leaved vanilla ornamental Cherry, and it does not
modify `minecraft:cherry_*`. It uses original Fruit Cherry wood textures and
green leaves. It is also visually different from the existing Olive and
Almond species.

### New resources

- `fruit_cherry_leaves`: original 16×16 green-toned opaque/cutout pixel art,
  219 solid pixels and 37 fully transparent; five restrained foliage colors,
  no semi-transparency or missing texture. Native leaves distance, decay,
  waterlogging and silk touch/shears loot behavior remain vanilla.
- `fruit_cherry_sapling`: original 16×16 sapling with asymmetric branching,
  bent copper-colored trunk and green lateral leaf clusters; vanilla cross
  block/item and matching potted version. TreeGrower enables random growth
  and bone meal through normal Minecraft logic.
- `fruit_cherry_tree`: registered configured feature with a custom
  `FruitCherryTrunkPlacer` and `FruitCherryFoliagePlacer`. **Sapling
  growth only**, not world generation.
- Regular fruit cherry is a **1×1 sapling** (no forced 2×2 mechanics).
  Planting four adjacent saplings does not create a mega-tree variant.
- Leaves have the approved cherry family tags, standard stick and sapling
  drops; fruit drops are intentionally deferred to the shared harvesting
  mechanic.

### Tree geometry

The wood is `italiansdelight:fruit_cherry_log` (not `minecraft:cherry_log`).
A relatively upright 5–6 block trunk sometimes leans a single block at
mid-height. One in three trees has a short, face-connected lateral bough.
Trunk positions are planned and checked *before* placement. A blocked
position causes the placement attempt to rotate/retry, rather than
constructing an obviously detached branch.

The foliage is **rounded with layered rings** instead of Olive's low dome
or Almond's upright sprays. Planned leaves are filtered to <=6 face steps
from real fruit-cherry wood and keep vanilla distance/decay semantics.
Two blocks of clear space are retained near the lower canopy.

### Intentionally not included

- **Fruit-bearing leaves and picking cherries:** to be implemented as one
  shared, reviewed mechanic for Olive, Almond and Fruit Cherry after the
  planted tree variants are accepted.
- **World generation:** no biome modifications, placed feature or spawn
  probabilities; it remains deliberately disabled for all orchard species.
- **Existing art and gameplay:** all previously approved signs, shelves,
  cabinets, boats, doors, trapdoors and wood/log/plank PNGs are unchanged.
- Neither `main` nor the released `v1.0.0` is modified.

### In-game QA

- [ ] Sapling appears in Creative and can be planted on natural soil.
- [ ] Bone meal triggers growth; blocked space does not leave half a tree.
- [ ] Check several orientations and both single and two-bough trunks.
- [ ] Inspect canopy at eye level and from above; green unlike vanilla pink.
- [ ] Verify leaf decay, silk-touch/shears, sapling drops, stick drops.
- [ ] Verify potted sapling and its two drops after breaking.
- [ ] Load/reload and test on dedicated server with multiplayer.
- [ ] Confirm no fruit is produced early and no natural generation is enabled.

CI test: `.github/scripts/check_fruit_cherry_tree.py`.
Compilation and static checks are not a substitute for a Minecraft playtest.
