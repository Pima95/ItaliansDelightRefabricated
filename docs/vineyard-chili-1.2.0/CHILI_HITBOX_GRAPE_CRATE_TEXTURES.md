# Chili lifecycle and grape crate top artwork — 1.2.0 revision

## Root cause: only the adult tomato block had been extended

Farmer's Delight has **two** tomato blocks: `BuddingTomatoBlock` for seedling ages 0–3 and `TomatoBlock` for the mature plant. Our chili seeds incorrectly placed the adult vine directly, so the original progressive growth phase never occurred. The old mature chili reused identical textures for consecutive ages and even jumped back to a sprout mid-growth.

**Fix:** a new `BuddingChiliBlock extends BuddingTomatoBlock` handles the true FDR seedling phase, including inherited age-dependent shape, random ticks and bonemeal behavior. The three FDR conversion methods which hardcode the normal tomato are redirected to the existing chili adult ID. The same chili seed item now places this budding phase. Seedling stages reuse the **four original FDR vegetative sprites** (different every age). At maturity the plant turns into the existing `ChiliCropBlock extends TomatoBlock`, with leaf-only mature foliage, green fruit and finally red fruit. Both normal and hanging adult plants retain the upstream tomato selection shape and collision-free behavior.

Saved chili crops `chili_crop` and `chili_on_rope`, including age 0–7 states, remain registered and load. Existing saved seedlings weren't a separate ID before. Adult fruit regrowth now resets to adult foliage age 0 (not a tiny seedling). No changes to stakes, horizontal rope, grape cultivation or the red/white juices.

## Crate textures

Replaced **both grape crate top sprites from scratch**. The wooden frame is sampled directly from the FDR `tomato_crate_top`, not from Italian's Delight cherries. Four separate small triangular clusters of grapes use different shades for berries and stems on the dark packing surface. Purple and yellow-green bunches have their own palette, with clear gaps between clusters. Output: 16×16 RGB PNG, fully opaque, hard-edged pixels. Existing side textures are left untouched pending in-game review.

## Tests

`/give @p italiansdelight:chili_pepper_seeds`

`/give @p italiansdelight:grape_crate`

`/give @p italiansdelight:white_grape_crate`

Plant a chili seed on farmland and verify the four growing-seedling states, conversion to adult vine, foliage -> unripe -> ripe peppers, harvesting and regrowth. Apply bonemeal to seedling and adult, repeat on ropes, and load an existing saved chili. Place both grape crates and inspect top/sides in daylight. Build and dedicated-server smoke test are CI checks, not replacements for visual gameplay verification.
