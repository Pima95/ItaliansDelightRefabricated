# Fermentation barrels — high-contrast wood depth, 16/16 variants

The previous 26.3 recolor used a linear color ramp on the Alcocraft `spruce_keg` sprite. This left **oak, jungle, spruce, acacia, cherry, fruit cherry, almond, olive, crimson, pale oak and poplar** visually flat. Mangrove also needed a more accurate color. Bamboo had become a bright yellow keg with no recognizable bamboo canes.

## Revised design

- All 16 barrels use the original **Alcocraft: Beer & Stuff** 22-element keg model, with `brewing_barrel` kept as the historic oak registry ID. New sprite size remains **64×64 RGBA**.
- Spruce has been darkened in the stave seams and lightened at the wood highlights; it is now more pronounced than the source rather than an unmodified original.
- All other wood variants (including mangrove) use **Minecraft Java 26.3 / Italian's Delight** plank color ramps. Their original Alcocraft wood pixels have substantially more contrast: 5th–95th-percentile pixel luminosity varies from 53 (dark oak) to 157 (almond), depending on natural wood brightness.
- Grain now gets an irregular vertical streak and a slight dark rim at the boundaries against metal/transparent atlas pixels. **Steel bands, nails, alpha and model UVs remain byte-identical** to the original source.
- Bamboo is a dedicated reconstruction: the actual Minecraft 26.3 `bamboo_planks` texture is tiled into the barrel's wood regions, with intentionally stronger vertical stems and dark transverse joints, then shaded. Bamboo is visibly less flat and less bright than the previous uniform gold recolor.
- The exact source Alcocraft sprite is retained **only as a test fixture** in `.github/scripts/fixtures/alcocraft_spruce_keg.png`. This fixture keeps its original Git blob SHA `15bc4dcee27b3cb34a82d9bbf9ac50a43bdbee26`.
- Precise sprite SHAs, wood ramps and expected luminance contrast are stored in `.github/scripts/brewing_barrel_wood_palettes.json`. Automated tests verify wood-vs-steel separation, alpha, 16 sprite hashes and bamboo tonal detail.

## Provenance and credits

Original model + sprite: **Alcocraft: Beer & Stuff**, **hadrus91**, MIT. See `CREDIT.md` and `THIRD_PARTY_NOTICES.md`. No assets from a later third-party port were used. The bamboo pixel pattern and default wood palettes are drawn from **Minecraft Java 26.3**, not from Alcocraft; Italian's Delight's proprietary fruit-tree wood palettes are from the existing project resources.

## Compatibility

There are **no ID, blockstate, BE/NBT, item, recipe or fermentation changes**. Only texture atlases, tests and this documentation changed. Visually test in Minecraft in natural and artificial light before considering the artwork approved.

## Selective moderation and bamboo facade (15 November 2026)

After in-game review, the strongest grading was **too contrasty** for birch, acacia, vanilla cherry, almond and pale oak. The updated atlas colors are interpolations between the low-contrast pre-grading version (commit `1f11821`) and the high-contrast grading (commit `2632854`), retaining the wood palettes but avoiding harsh streaks. White highlights on pale oak are capped to a maximum RGB component of **237**, removing isolated white pixels.

- Birch: blend 54% high + 46% low; 5–95% luminance contrast roughly **98**.
- Acacia: 52% high + 48% low; contrast **61**.
- Vanilla cherry: 50% / 50%; contrast **91**.
- Almond: 52% high + 48% low; contrast **107**.
- Pale oak: 43% high + 57% low; contrast **71**, and no blown-out white.
- **Mangrove:** revert to the exact previously approved Git blob `26a66116e14b1d32dc57d44f67544ac0e2d8e794`, from commit `1f11821`, without interpolation.
- **Bamboo:** completely redesigned from a *single actual vanilla bamboo_planks facade* (Minecraft 26.3, 16×16). The original horizontal bamboo strips and their natural seams are tiled over the atlas's wood polygons. Only a slight brightness modulation and edge shade retain barrel volume. No synthetic vertical stalks, checkerboards or extra periodic nodes.

The original metal, nails, transparency, block IDs, geometry, blockstates, game mechanics and source attribution are unchanged. Pixel tests check the committed SHA of every barrel, the new contrast ceilings, restored mangrove and the absence of white hotspots in pale oak.

This revision supersedes the stronger contrast guidance in the **Revised design** section above for these seven wood varieties.
## Matching vanilla mangrove planks and stripped pale oak log (16 November 2026)

Two remaining cosmetic issues were observed in Minecraft after the previous revision.

**Mangrove:** the earlier restored sprite had wood-pixel red up to **207** and median wood luminance **67.4**, whereas the actual Minecraft 26.3 `mangrove_planks.png` has only seven reddish-brown colors with RGB maximum **(139, 77, 58)**. The wood region has now been remapped to those seven real plank tones, preserving grain and original steel/nails: the new median luminance is **58.8**, upper 95th-percentile **77.4**, red capped at **139**. The previous mangrove rollback is now superseded.

**Pale oak:** the side of Minecraft 26.3 `stripped_pale_oak_log.png` contains **five tightly grouped warm-grey/ivory shades**, not strong isolated white flashes. The barrel previously contained **757 nearly white wood pixels**. Instead of repainting the whole atlas, only wood highlights above luminance 203 have been gently compressed (0.62 gradient factor); the new maximum red channel is **227**, the upper 95th-percentile wood luminance **222.1**, and there are **zero nearly-white wood pixels**. Lower shadows and texture detail are preserved.

The source sprites used for visual/color analysis are Minecraft's original `mangrove_planks.png` and `stripped_pale_oak_log.png` from the [26.3 asset archive](https://github.com/InventivetalentDev/minecraft-assets/tree/26.3/assets/minecraft/textures/block). The source colors and their provenance are recorded in `.github/scripts/brewing_barrel_wood_palettes.json`. Automated tests guard the wood maximum RGB, lack of bright pale-oak hotspots, transparency, original metal pixels and all committed hashes.

Only `mangrove_brewing_barrel.png` and `pale_oak_brewing_barrel.png` are changed by this revision. All models, NBT, registry IDs, game code, other 14 barrel textures, and credits remain untouched.
