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
