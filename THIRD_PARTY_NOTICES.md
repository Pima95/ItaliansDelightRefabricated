# Third-party notices and inspirations

Italian's Delight Refabricated is an independent mod inspired by the two projects below. It is **not** an official continuation of either project.

## Italian's Delight (original)

- Original project: [Italian's Delight on CurseForge](https://www.curseforge.com/minecraft/mc-mods/italians-delight)
- Original project owner: **PuurpZ** and the original Italian's Delight team/artists.
- The [original CurseForge project page](https://www.curseforge.com/minecraft/mc-mods/italians-delight) publicly lists **nickhanic2021**, **JAMMSON**, and **duynotsimp** as team **Artists** (as checked 11 November 2026). These are *project-level credits*, not evidence assigning a particular texture to one of them. Their exact per-asset rights and original copyright notices require further checking.
- License listed by CurseForge: [MIT License](https://www.curseforge.com/minecraft/mc-mods/italians-delight/license).
- This repository includes some legacy artwork associated with the original project; per-file provenance remains under review. Do not attribute unmatched PNGs solely to PuurpZ without verification.

## Rustic

- Original project: [Rustic on CurseForge](https://www.curseforge.com/minecraft/mc-mods/rustic)
- Original project author: **cadaverous_queen**; original source: [cadaverous-eris/Rustic](https://github.com/cadaverous-eris/Rustic). Other contributors may hold rights in individual original assets.
- License listed by CurseForge: [MIT License](https://www.curseforge.com/minecraft/mc-mods/rustic/license).
- The [Rustic CurseForge description](https://www.curseforge.com/minecraft/mc-mods/rustic) also thanks **Nazenn** for making/editing textures and **Copygirl** for permitting the use of locker models/textures. These upstream credits are preserved for transparency; *no identified binary match in this repository establishes that the specific reused artwork was drawn by either contributor*.
- A historical reproducible comparison identified **41 binary-identical assets/metadata files** before two resources were deleted. The current Orchard branch retains **39 unchanged binary matches** (11 November 2026): original matching `leaves_olive.png` and `sapling_olive.png` were already removed. These include olive bark, olive seed, fluids and some currently unused legacy resources. See [the audit with each source path and Git blob SHA](docs/orchards-1.1.0/RUSTIC_ASSET_MATCHES.md).

## License notice for upstream MIT-licensed material

The MIT license, as stated on the upstream CurseForge project pages, permits use, modification, distribution, and sublicensing of covered material provided the original copyright and permission notices are retained. For reference, the MIT permission and warranty language is reproduced below. The public license pages use a **generic copyright placeholder**, and the available Rustic source tree lacks a standalone LICENSE file with an exact holder/year statement. **This generic reproduction is not a verified substitute for every upstream copyright notice.** If an actual source-distribution or asset-specific copyright notice is found, it must be retained verbatim. Attribution here does **not** imply that an asset from a separately licensed contributor is automatically MIT-licensed.

```text
MIT License

Original copyright remains with the respective upstream copyright holders.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

These third-party notices supplement the project's Apache-2.0 `LICENSE` and its separate `NOTICE` attribution file; they do not transfer authorship of third-party art, and it does not claim that PuurpZ or cadaverous_queen endorse this project. If a more specific upstream copyright notice is identified in a source distribution, retain that notice as well.

## Distribution packaging and attribution limits — 11 November 2026

The development JAR includes
`LICENSE_italiansdelight` (Apache-2.0), `META-INF/NOTICE`,
`META-INF/THIRD_PARTY_NOTICES.md`, and `META-INF/FDR_CRATES_LICENSE.md`
(original source's MIT notice with **Copyright (c) 2020 vectorwing**).
This packaging was checked by the successful
[development CI run 43050050711](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43050050711)
for the `1.1.0-rc.1` candidate. Any subsequent changed release JAR needs
its own packaging verification; the automated checks do not grant legal
clearance for individually unattributed artwork.
The five Orchard crates retain adapted Farmer's Delight wooden crate
texture elements; see [the per-feature license note](docs/orchards-1.1.0/FDR_CRATES_LICENSE.md).

**Current audit, not a blanket clearance:** the Rustic matches demonstrate
binary identity and the upstream CurseForge pages list MIT, but neither proves
the provenance of every other original/ported PNG. Italian's Delight names
multiple artists, and artwork originating from individually licensed
contributors remains subject to those license terms. Do not treat
the project's blanket MIT label or these notices as proof that every
unmatched artwork has been individually reviewed.

Full packaging and open questions are recorded in
[the 1.1.0 RC asset/license review](docs/orchards-1.1.0/LICENSE_AND_ASSET_RC_REVIEW.md).
