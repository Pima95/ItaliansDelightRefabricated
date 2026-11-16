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

## Spruce shading reference — top and bottom corrections (16 November 2026)

The previous update correctly darkened mangrove but made the upper and lower wooden atlas sections visually flat. The bright area of pale oak remained crowded near the same near-white values. We compared both with the **existing spruce brewing barrel** (not the raw Alcocraft reference image) pixel by pixel. The same 2492 wood pixels in each 64×64 RGBA atlas were remapped using the spruce sprite's local luminance. All steel, fasteners and transparent pixels are untouched.

**Mangrove:** continuous color interpolation with seven shaded anchors matching the dark red-brown hue of vanilla `mangrove_planks` (red never exceeds 139). In the model's main upper UV region, distinct wood RGB shades increased from **9 to 22**; in the lower region, from **10 to 16**. Their p05–p95 brightness intervals are now **24** and **18**, respectively (previously 14 and 8). The region's grain comes from the spruce shading reference, not a uniform flat surface.

**Pale oak:** replaces the concentrated near-white highlights with warm-neutral grays, based on the subtle tone of `stripped_pale_oak_log` but preserving the detailed spruce grain transitions. The highest wood red-channel value is **218**; the main upper UV region ranges from brightness **185** (p05) to **210** (p95), rather than **209–222**. No white or near-white wooden pixels remain.

Static checks now protect the upper and lower UV panels independently, with minima for the number of distinct wood shades and their luminance spread. The 14 other wood variants and the block model remain unchanged. This supersedes the previous rendering method for these two wooden barrels.

## Pale oak hue only — warm ivory without moving shades (16 November 2026)

The previous spruce-based pixel arrangement was accepted, but looked too gray. We have **not** moved any pixel or recomputed its luminance ramp. The wood area is recolored by a constant `RGB (+7, 0, -14)` translation: it shifts neutral gray to subtly warm ivory/cream, retains **all 69 original distinct wood shades**, preserves the same 5–95 percentile luminance spread (approx. 50.43 units), and keeps wood red <= 225 (no white hotspots).

The 2492 wood pixels are changed in hue only. All 1604 other atlas pixels (including steel, rivets and transparent areas), alpha, geometry, block IDs and fermentation behavior remain byte-identical to the prior version. Tests pin the new image blob and assert its warmer hue and highlight ceiling. No changes to vines or server-test behavior were made.

## Full pale-oak repaint from spruce + vanilla stripped log (16 November 2026)

The pale oak needed a **new sprite**, not another correction of the gray previous version. We rebuilt all **2492 wooden pixels from scratch** using the approved `spruce_brewing_barrel.png` sprite as the source of *pixel placement, stave seams, highlights and surface depth*. We did **not use the previous pale-oak RGB pixels**. The other **1604 pixels** (metal, bolts, transparent pixels) and all alpha values match the spruce model exactly.

The original vanilla Java 26.3 side texture `stripped_pale_oak_log.png` is a 16×16 indexed, 4-bit PNG with palette:
`#FFFBF8`, `#F8F0F0`, `#F0E7E5`, `#EBE0E0`, `#E6DBDB`, occurring **65, 94, 61, 25, 11** times respectively. The darkest shades are intentionally much rarer. We reuse this *distribution and its actual spatial grain pattern* to introduce subtle light/dark shifts, rather than copying the nearly-white RGB values unchanged.

For the keg's deeper 3D shape we interpolate a custom **nine-shade warm ivory/beige palette**, from RGB `(145,127,113)` for the recesses to `(222,206,190)` for highlights. Spruce's existing pixel luminance determines the shade, and a small additional deviation from the original 16×16 stripped-log pixel pattern adds texture. This combines the game's restrained pale-oak hue with readable stave depth and avoids gray or nearly-white clipping.

Results: **126 distinct wooden RGB shades**, wood luminance 5th–95th percentile roughly **149–199**, upper front surface **174–199**, lower front surface **159–183**. No white highlights. JSON models, registration IDs, recipes, saved worlds and growth code remain unchanged by this texture edit.

Source: Minecraft Java 26.3 `stripped_pale_oak_log.png`, Git blob `ddfff87b1967af2b4eaeb3bbfc4c268b4086d756` in the vanilla [26.3 asset archive](https://github.com/InventivetalentDev/minecraft-assets/tree/26.3/assets/minecraft/textures/block).

## Pale oak corrected to the actual white vanilla log palette (16 November 2026)

An in-game screenshot showed the `stripped_pale_oak_log` at RGB roughly `(248,240,240)`, alongside our erroneously brown keg at roughly `(182,163,145)`. The previous nine-color warm-beige palette was based on incorrect color assumptions; it is now **completely replaced**.

This sprite is built from the **spruce brewing barrel's** original wooden pixel mask and its existing distribution of stave lines, shadows and lid shading. Of 2492 wooden pixels, **2318 use precisely one of the five original Minecraft 26.3 stripped-pale-oak-log colors**: `#FFFBF8`, `#F8F0F0`, `#F0E7E5`, `#EBE0E0`, `#E6DBDB`. The remaining 174 use two darker, neutral rose-grey derivatives `#DBD1D1` and `#CFC5C5` exclusively for deeper wood recesses. We sampled the original 16x16 vanilla log pixel indices and combined their local 1-pixel pattern with the SPRUCE barrel's luminance-ranked geometry, instead of recoloring the previous pale-oak PNG. The seven exact wood colors preserve pixel edges and match the vanilla color family, including the very light highlights that Minecraft itself uses.

All 1604 original non-wood atlas pixels, the exact alpha mask, iron hoops, fasteners and Minecraft block models are preserved. No branch, registry ID or save compatibility changes.
