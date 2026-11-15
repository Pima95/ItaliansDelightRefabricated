# Fermentation barrel textures — palette correction

Revision: 15 November 2026. Source model remains the original Alcocraft `spruce_keg` (22 cuboids, 64×64 atlas); registered block names remain `italiansdelight:brewing_barrel` and `<wood>_brewing_barrel`.

## Problem and fix

The previous artwork recolor applied arbitrary RGB values with a brightness multiplier. It ignored the actual block palette, especially for **poplar, pale oak, cherry, warped, crimson, olive, almond and fruit cherry**, resulting in misleading colors.

The corrected 14 textures now use the **pixel color palette of each real wood plank** and preserve Alcocraft's steel rims, fasteners, original texture geometry and transparency.

- Vanilla planks: Minecraft Java **26.3** assets, archived at [minecraft-assets 26.3](https://github.com/InventivetalentDev/minecraft-assets/tree/aaf3b3dbdf72b5723861324984813f3fd87db179/assets/minecraft/textures/block). Only their color palettes are sampled; the plank image pattern is **not** copied into the keg textures.
- Original mod wood planks: `olive_planks.png`, `almond_planks.png`, `fruit_cherry_planks.png`, already included in Italian's Delight Refabricated. Almond and fruit cherry ramps use representative sampled pixel brightness levels.
- Exact numeric reference ramps are stored in `.github/scripts/brewing_barrel_wood_palettes.json`.
- For each brown wood pixel in Alcocraft's original spruce atlas, classify a wood pixel by `R>G+7` and `G>B+7`, measure source luminance and interpolate between sorted plank palette colors using a consistent source brightness range 32–143. Grey iron/steel and transparency are unchanged.
- **Do not change** `spruce_brewing_barrel.png` (identical to original Alcocraft image) or `mangrove_brewing_barrel.png` (already approved).
- Tests in `check_brewing_barrel_woods.py` verify actual RGBA pixels, source color ramps, metal, alpha and the two accepted sprite SHA checks.

This modifies **textures only**. No registration IDs, recipes, model UVs or fermentation logic changed. Visual testing remains required in Minecraft, including how oak/cherry/pale oak and the two Nether woods read under in-game lighting.

## Attribution

Source keg model / texture: **Alcocraft: Beer & Stuff**, by **hadrus91**. See the revised `CREDIT.md` and `THIRD_PARTY_NOTICES.md` for the MIT license and provenance. Neither source attribution nor this palette update claims any ownership of the underlying Minecraft textures.
