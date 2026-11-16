# Chili selection hitboxes and grape crate texture drafts — 1.2.0

## 1. Chili: actual inheritance and age-specific outlines

`ChiliCropBlock` is derived from `vectorwing.farmersdelight.common.block.TomatoBlock`, which is itself a vanilla `CropBlock`. The important distinction is that Farmer's Delight `TomatoBlock.getShape()` overrides the usual vanilla crop geometry and always returns a **12×16×12** shape, even for the youngest age. Therefore the same outline at each stage was genuinely inherited, not a rendering problem.

We now override `getShape` in `ChiliCropBlock` with **eight cached outline shapes**. Their top Y heights in Minecraft model pixels are **3, 5, 7, 9, 11, 13, 15, 16** respectively for ages 0–7, with widths that expand from 4 to 12 pixels. This only changes the target-selection outline, not collision: both root and rope crops remain passable. `HangingChiliBlock` adds a thin full-height rope outline to its own age-specific crop outline, so even a very young hanging pepper's rope can be targeted.

`GrapeStemBlock` extends Minecraft's `CropBlock` directly and already uses its vanilla age-based outlines: no grape hitbox changes are needed. No block-state IDs, growth speed, seeds, harvest logic, bonemeal or saved-world compatibility are changed.

## 2. Preliminary crate textures for both grapes

Four **16×16, fully opaque RGBA** textures have been added for visual review:

- `grape_crate_top.png`: dense purple grapes, with highlights based on the approved grape item palette.
- `grape_crate_side.png`: purple grapes visible between existing wooden slats.
- `white_grape_crate_top.png`: golden/yellow-green grapes following the approved white-grape item palette.
- `white_grape_crate_side.png`: white grapes visible between identical wooden slats.

To remain consistent with all five existing Italian's Delight crates, these sprites use the approved `cherry_crate_top` and `cherry_crate_side` pixel arrangement as the structural reference; **all seven existing wooden RGB colors and all green leaf/stem accent pixels are exactly unchanged**. Only cherry-red fruit pixels were reassigned to the eight purple or yellow-gold grape shades. No antialiasing or semi-transparent pixels were introduced.

**Scope intentionally limited to textures:** the crate blocks, models, recipes (9 grapes ↔ 1 crate), storage tags, loot tables, creative tab and translations have **not** yet been introduced. These steps should follow visual approval of both crates. No new Git branch was created.
