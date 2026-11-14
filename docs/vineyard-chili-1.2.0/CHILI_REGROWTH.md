# Chili harvest and regrowth (1.2.0, Minecraft 26.3)

## Why tomato-style harvesting needed an adaptation

Farmer's Delight `TomatoBlock.useWithoutItem` sends a picked tomato crop back to age 0. Its `tomatoes_stage0` model is already a well-developed leafy plant. That is visually appropriate for a picked tomato, but **not** for this mod's chili: `chili_crop_0.png` is a newly planted sprout. Copying the reset to age 0 made an established pepper vine appear to be a seedling, on farmland and on ropes.

The code still extends Farmer's Delight `TomatoBlock` for random ticks, growth, bonemeal, and rope climbing. Only the post-harvest age and the visuals of established foliage are different.

## Shared crop / rope age progression

| Age | Phase | Visual model |
| --- | --- | --- |
| 0–1 | Newly planted sprout | `chili_*_stage_0` |
| 2–3 | Young leafy plant | `chili_*_stage_1` |
| 4 | Established, full-height bush with no fruit | `chili_*_foliage` |
| 5–6 | Established bush with green peppers | `chili_*_stage_2` |
| 7 | Ripe red peppers, harvestable | `chili_*_stage_3` |

Harvesting at 7 gives the same pepper drops as before and resets **only the clicked segment** to 4. Growth then advances 4 → 5 → 6 → 7 normally, without replanting or returning to the seedling. This is applied to both the farmland root (`chili_crop`) and hanging segments (`chili_on_rope`) through the shared `ChiliCropBlock` implementation.

No new blockstate properties or IDs are introduced: the original age 0–7 values remain valid in saved worlds, as does the `ropelogged` root property. Rope placement and the three-block growth limit are unchanged.

## Verification

The vineyard audit checks the age-to-model mapping, actual full-height leaf-only sprite, no red ripe fruit at age 4, and valid PNG CRC / IEND. The packaged-JAR audit checks each model and sprite for root and hanging states. The dedicated-server smoke test places established root and rope segments; client visual verification is still recommended for harvesting with right click.

## Client acceptance check

1. Plant chili seeds on normal farmland: the initial crop must show the small seedling at age 0.
2. Grow to age 7: the two long red peppers should appear. Right-click once to harvest.
3. The selected plant must immediately display the **full-height, fruitless leafy bush** at age 4, not a seedling or a broken model.
4. After ordinary random ticks, age 5-6 must display green peppers and age 7 ripe red peppers; the bush must never be replanted.
5. Repeat steps 2-4 for a hanging chili segment supported by Farmer's Delight rope. Harvesting that segment must not reset neighboring segments or remove the rope.
6. Repeat a second harvest on the same plant to ensure fruit regrowth works more than once.

Server checks can validate the registered age-4 states, but **only the client can verify actual right-click behavior and rendered models**.
