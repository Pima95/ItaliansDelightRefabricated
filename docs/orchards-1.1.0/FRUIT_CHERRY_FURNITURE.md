# Fruit Cherry shelf and cabinet — vanilla-aligned pixel art

Branch: `feature/orchards-1.1.0`.
This revision supersedes previous art treatments. The cheese-aging rack,
Almond and Olive woods, all existing approved Fruit Cherry blocks/boats/signs,
recipes, models, Java and published v1.0.0 are untouched.

## Design principles

1. Original 16×16 cabinet tiles and 32×32 native shelf atlas, **no antialiasing**.
2. Finite 10-shade muted cherry/copper wood palette from the approved Cherry set;
   separate muted metallic hardware only where a door handle belongs.
3. Each dark pixel follows a physical object: bevel, wooden joint, depth,
   grain direction or hardware. No unstructured random per-pixel noise.
4. Uniform patches of wood can be intentional. Do **not** add random speckles
   solely to satisfy arbitrary "every 3×3 area must differ" testing.
5. Design must remain legible at actual 16×16 resolution, not just when zoomed.

## Shelf (`block/fruit_cherry_shelf.png`)

The reference is the previously approved Minecraft 26.2 Almond shelf and the
existing Olive shelf, **because these use the official vanilla model and UV
template**, rather than an imagined decorative item.

The shelf remains a 32×32 atlas with these exact regions:

| Pixel region | Purpose |
| --- | --- |
| x0–15, y4–11 | Unpowered face: exactly three item niches |
| x16–31, y0–15 | Approved stripped Fruit Cherry log pixels, exact pixel match |
| x0–15, y16–23 | Powered left |
| x16–31, y16–23 | Powered right |
| x0–15, y24–31 | Powered center |
| x16–31, y24–31 | Powered standalone |

All powered center panel pixels x1..14 are copied from the left panel,
and the edge/standalone transitions have the same approved vanilla side-chain
relationships as Olive. The front preserves the three distinct recesses,
correct jamb/shadow/floor hierarchy, and uses coherent, short horizontal timber
grain following the atlas's actual solid surfaces. There are no arbitrary
symbols, bolted decorations, diagonal supports or pseudo-vines.

The official native ShelfBlock, six template models, redstone and inventory
functions are unchanged.

## Cabinet (`block/fruit_cherry_cabinet_*.png`)

Researched the real Farmer's Delight Refabricated Cherry cabinet artwork in
`MehVahdJukaar/FarmersDelightRefabricated`, branch
`fabric/1.21.1-3.0.0`: the closed front, open front, sides and top.
Reference layout studied, but the new 16×16 pixels were drawn separately.

- **Closed front:** two symmetrical, fielded doors; consistent center stile,
  beveled door edges, two small metal handles and restrained natural Cherry
  grain with visible, coherent knots.
- **Open front:** dark one-piece upper cavity, physical shelf at mid-height,
  two lower recesses separated by a solid wooden divider, subtle background
  wood grain that follows the cabinet's depth.
- **Side:** large joined upright wood staves with continuous vertical seams
  and controlled beveling; no disconnected rectangles.
- **Top:** naturally joined horizontal timber boards, two clean recessed
  plank joints and subtle longitudinal streaks.

Keep the same outer timber frame and corner joinery on all four cabinet faces
so rotations and open/closed variants look like **one physical block**.
The Farmer's Delight 27-slot CabinetBlock, entities, model references,
blockstates, drops and recipes are unchanged.

## Regression checks

`.github/scripts/check_fruit_cherry_furniture.py` now validates material
identity and semantic construction: vanilla UV coordinates, back texture equal
to the real approved stripped Cherry log, pixel-exact side-chain transitions,
three unpowered recesses, cabinet handles, its upper/lower compartments, grain
direction, bounded wood palette, valid PNG CRCs and unchanged approved assets.
It no longer rewards arbitrary differences from Almond, nor requires every
3×3 area to have unrelated colors.

In-game review required: check all three shelf slots and the powered
left/center/right/standalone variants; rotate cabinet through all four facings,
open and close it and confirm storage persists across a reload.
