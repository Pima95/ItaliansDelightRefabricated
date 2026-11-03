# Fruit Cherry tree — planted growth, Orchard 1.1.0

**Branch:** `feature/orchards-1.1.0` · **Status:** implementation awaiting GitHub CI and in-game visual acceptance.

## Approved separation of species

This is **not** the pink-leaved vanilla ornamental Cherry, and it does not
modify `minecraft:cherry_*`. It uses original Fruit Cherry wood textures and
green leaves. It is also visually different from the existing Olive and
Almond species.

### New resources

- `fruit_cherry_leaves`: original 16×16 green-toned opaque/cutout pixel art,
  157 solid pixels and 99 fully transparent, with its own overlapping pointed leaflet clusters, natural holes and six muted greens,
  no semi-transparency or missing texture. Native leaves distance, decay,
  waterlogging and silk touch/shears loot behavior remain vanilla.
- `fruit_cherry_sapling`: original 16×16 sapling with asymmetric branching,
  bent copper-colored trunk and green lateral leaf clusters now detailed with six deliberately placed leaf-shading tones; vanilla cross
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

### Art revision — own silhouette and biome coloring (3 November 2026)

The previous `fruit_cherry_leaves.png` reused Olive's alpha silhouette, with
only different green pixels. **That is no longer the design.** The new
16×16 texture has 157 opaque / 99 transparent pixels and six subdued,
related green-grey shades. It is an original arrangement of staggered
small pointed leaflets, grouped into angled sprays with purposeful light
and shadow along each leaf. Olive and Almond remain untouched. The new
texture is visually distinct in both its alpha cutout mask and interior
shading, without high-contrast random speckling.

**Biome color:** the existing vanilla `minecraft:block/leaves` parent
supplies the tint index. Client-only `ItaliansDelightClient` now registers
`FRUIT_CHERRY_BIOME_FOLIAGE` via Fabric's `BlockColorRegistry`, exactly
as for Almond, using `BlockTintSources.foliage()` at the world location.
Fruit Cherry blends **50% local biome foliage RGB / 50% white**, retaining
the 16×16 texture's details while visibly responding to warm, temperate
and cold biome foliage colors. Almond's separate 30% biome influence
and Olive's fixed colors are unchanged. The base texture is displayed
without biome tint in the inventory; the approved sapling is also
unchanged. The existing `UntintedParticleLeavesBlock` *block class* is
still appropriate: biome tint is client-side block rendering, not a
property of that particle-handling class.

CI checks distinct alpha masks from Olive and Almond, pixel-art palette
and PNG fingerprint, the actual foliage tint registration and blend,
and preservation of the sapling checksum and tree growth configuration.
A build cannot replace an in-game appearance check.

For review, test in at least a temperate forest, savanna, and cold biome;
compare the world-placed leaves and their untinted inventory item. The
branch remains development-only, with natural generation and cherry
fruit harvesting intentionally unimplemented.
