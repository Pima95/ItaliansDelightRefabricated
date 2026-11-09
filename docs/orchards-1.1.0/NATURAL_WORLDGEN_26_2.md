# Orchard 1.1.0 — natural world generation for Minecraft 26.2

Activated on branch `feature/orchards-1.1.0`, 9 November 2026.

Previously all four tree configurations were available **only** to sapling growth. This change adds four `data/italiansdelight/worldgen/placed_feature/*_wild.json` files and attaches them through `BiomeModifications.addFeature` at `VEGETAL_DECORATION`. It uses the **exact same approved custom trunk and foliage placers**, preserving fruiting mechanics, leaf art, growth and wood.

| Tree | Biomes | Initial probability of one attempt per eligible chunk |
| --- | --- | --- |
| Normal Olive | Plains, Sunflower Plains, Savanna, Savanna Plateau, Windswept Savanna | 1/6 |
| **Ancient Olive 2×2** | Same Olive biomes | **1/96** |
| Almond | Plains, Sunflower Plains, Meadow, Savanna, Savanna Plateau | 1/8 |
| Fruit Cherry | Forest, Flower Forest, Cherry Grove | 1/7 |

The 2×2 ancient tree uses `configured_feature/ancient_olive_tree.json`, exactly as for four olive saplings in a 2×2. It is **16 times less likely to attempt** placement than a normal olive, and often requires more open terrain. It does not introduce a separate fruit or wood type.

Each natural placed feature applies, in order: rarity filter, random in-chunk X/Z, no standing water, `OCEAN_FLOOR` heightmap, `minecraft:would_survive` for the corresponding **stage-0 sapling**, biome filter. Survival excludes invalid substrates; tree placers and foliage placers retain their established terrain clearance checks. Attempts can fail; the fractions above are NOT a guaranteed tree density.

The three tree families remain separate from vanilla pink ornamental cherry trees. No new fruit, grape, juice, item ID, block state or spawning in unrelated biomes has been added.

## In-game validation (important)
1. **Create a new world**, or travel far enough to generate **unexplored chunks**. Existing chunks remain unchanged.
2. Plains/Savanna: look for normal Olive and Almond, including their fruiting leaf mechanics; a very rare Ancient Olive has a true 2×2 trunk.
3. Forest/Flower Forest/Cherry Grove: look for distinct green **Fruit Cherry** trees. Vanilla pink Cherry Grove trees must stay.
4. Inspect whether trees have floating leaves, detached branches, partially generated trunks, water placement or collisions with structures.
5. Test natural fruiting on newly generated leaves across seasons, right-click harvest, and bone meal; compare to planted trees.
6. Save and reload, then generate more chunks. Validate on both a client and a dedicated server.
7. Balance the initial probabilities **after observing trees in a real world**; do not treat the static checks or Java build as proof of in-game spawn rates.

## Technical checks
- CI: `python3 .github/scripts/check_orchard_natural_worldgen_26_2.py`
- Existing olive, almond, cherry sapling/tree/foliage tests have been updated to expect actual natural generation rather than its former absence.
- Full Java 25 Gradle build also runs in CI.

References:
- https://docs.fabricmc.net/develop/data-generation/features
- https://mcasset.cloud/26.2/data/minecraft/worldgen/placed_feature/trees_plains.json
