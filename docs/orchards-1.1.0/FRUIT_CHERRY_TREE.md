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
The Fruit Cherry now uses the **exact same face-connected Mediterranean
trunk shape as Almond and Olive**: a low sideways bend, another upward turn,
and occasional ascending side fork, with a 4–6 block trunk. The Fruit Cherry
variant uses the same Almond fork frequency (one in five) and tall-single
probability (one in four of eligible single trunks). The underlying wood
remains exclusively `fruit_cherry_log`, never Olive, Almond or vanilla
ornamental Cherry. The complete planned log structure is checked before
placement to prevent a detached fork or a partially grown blocked tree.

The foliage keeps a **rounded, gently broader crown** rather than Almond's
upright sprays. Its six ring radii now use a broader, rounded profile peaking at
**4.10** versus Almond's **2.45** (approximately 67% larger maximum radius).
The canopy placement scan was expanded to 11×11 instead of the old 7×7
area, which previously clipped the largest radii.
It retains the same overall vertical range and two-block clearance below
the canopy. Planned leaves are limited to six face steps from actual
fruit-cherry wood, preserving natural Minecraft decay.

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
without biome tint in the inventory; the sapling retains its original
shape but now has eight subtle red pixels arranged in four tiny pairs. The existing `UntintedParticleLeavesBlock` *block class* is
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


## Tree geometry alignment — 3 November 2026

After approving the **unique biome-tinted leaf texture** and the sapling,
the tree growth itself was aligned with Olive and Almond. Only
`FruitCherryTrunkPlacer.java` and `FruitCherryFoliagePlacer.java` are
changed in gameplay: the old almost-vertical stem is replaced with the
already accepted Almond/Olive low-bend and second-turn branching topology.
The Fruit Cherry configured feature continues placing ONLY the approved
Fruit Cherry logs and leaves. Both short and tall versions use the same
face-connected fork and all-or-nothing clearance logic.

Compared with Almond's crown profile
`{1.95,1.70,2.35,2.45,2.00,1.45}`, Fruit Cherry uses
`{2.55,2.70,3.65,4.10,3.50,2.55}`. The rounded silhouette is
approximately **67% wider in maximum radius** than Almond, with no taller
vertical leaf columns. Olive and Almond trunk/foliage code and all
wood/material textures are untouched. The fruit cherry's leaf artwork,
biome tint, original green foliage, recipes, drops and existing blocks remain
unchanged. Only eight green pixels in the sapling artwork were replaced
with small red cherry accents; its silhouette and trunk are untouched.

This remains planted-tree generation from saplings (including bone meal).
**Natural biome spawning and fruit harvest are still disabled.**
A Minecraft playtest should grow several saplings on clear ground and
near obstacles, verify both trunk fork variants and inspect the canopy
from above/side, including checking leaf persistence after reload.

## Visual expansion and Creative-only orchard fruits — 4 November 2026

The prior radius 3.00 crown still appeared too compact because the
`FruitCherryFoliagePlacer` scanned only x/z -3 to +3. The expanded
six-layer profile is `{2.55, 2.70, 3.65, 4.10, 3.50, 2.55}`; its main
scan now covers x/z -5 to +5. The highest/middle rings flare out into
a visibly wider, rounded crown while retaining the **existing crooked
Olive/Almond trunk**, tree height, leaf-distance checks and understory
clearance. The planted sapling still controls tree growth.

The sapling's 16×16 green silhouette (114 opaque pixels), six approved
leaf greens and twenty copper trunk pixels are preserved. Exactly **eight
former green pixels**, in four small pairs near the outer leaves, are
now dark-to-bright cherry red. These are only decorative pixels on the
sapling. They do not represent any harvestable crop state.

Three plain, stackable **Creative-only items** are registered:
`italiansdelight:olives`, `italiansdelight:almonds` and
`italiansdelight:cherries`. The olives item reuses the existing
`textures/item/olives.png` unchanged. Almonds and cherries each have
new hand-crafted 16×16 RGBA pixel artwork and ordinary generated
inventory models. All three use simple Item properties (stack size 64),
without food components, recipes, loot/drop tables, harvesting logic,
right-click interactions, or worldgen. They appear together in the
Italian's Delight Creative tab under crops.

In-game review: grow several Cherry saplings (with bone meal) and inspect
the radius of the middle canopy, plus the small red sapling details and
all three 16×16 fruit inventory icons at their actual size.
