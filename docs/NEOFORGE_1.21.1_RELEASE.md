# NeoForge 1.21.1 — Release preparation

**Scope:** first native port, candidate `1.2.0-mc1.21.1-neoforge`. Name in game: Italian's Delight. Final publication/version approval pending. No new release or tag is authorized by this document.

## 1. Network and multiplayer review

Minecraft's native mechanisms carry the mod state, rather than a bespoke payload protocol:
- Item components use persistent codecs with automatic network codecs in 1.21.1, including pizza composition, slice, peel cargo and wine quality.
- The six visible/dynamic block entities (raw/cooked pizza, barrel, cauldron, crushing tub, brewing barrel) implement update tags and packets; server mutations request block updates. Pizza division uses a blockstate shadow and deferred post-tick reconciliation. Rack/hook/aging cheese visual state is stored in blockstates, and machine interfaces use vanilla menus.
- The three menu types synchronize slots and fields through vanilla AbstractContainerMenu/ContainerData. Cheese Vat button now checks server side and stillValid; quickMoveStack rejects out-of-range slot indices. Preview remains read-only.
- New GameTest exercises real item stream codecs, six block-entity update packet codecs and three receiving menus. New results pending. It is not a two-player session, does not certify actual remote packet delivery, and does not cover every gameplay path.

## 2. Source parity

Pinned Fabric source: [dd0d7ce](https://github.com/Pima95/ItaliansDelightRefabricated/tree/dd0d7cef35a6d74a3db9448c07172ea57767f64a). Git tree comparison at [794e02b](https://github.com/Pima95/ItaliansDelightRefabricated/tree/794e02b10491d40784d0e3a6f0bb8e13580ddeaa):

| Source resource | Fabric | NeoForge | Explanation |
| --- | ---: | ---: | --- |
| Recipe JSON | 283 | 270 | 13 explicit exclusions, all retained source blobs identical |
| Modern item definitions | 380 | 367 | Same 13 exclusions; every retained definition has a 1.21.1 inventory model |
| Blockstates | 184 | 173 | 13 exclusions, 2 new liquid blockstates |
| Loot table JSON | 182 | 169 | Same 13 exclusions, retained source blobs identical |
| Texture-directory files (PNG + metadata) | 638 | 633 | 9 removed, 4 copied sign sprites, all retained source blobs identical |

Exclusions: pale oak and poplar variants of barrel, brewing barrel, crushing tub, crop stake and cheese aging rack; olive, almond and fruit cherry shelves. Configured features migrated from worldgen/feature to worldgen/configured_feature; runtime resource normalization and backported random providers are intentional. The final source-tree parity check now runs in CI. It is not proof of complete gameplay equivalence.

Modern constant potion tints were missing on 10 registered items; exact RGB colors from source definitions are now registered in the native client. Four older aliases (apple_juice, olive_oil, sheep_milk, whey) are not registered items; their source assets remain. Pizza peel uses its native wrapper; pizzas use legacy model overrides. Client tint verification pending.

The generated JAR deliberately repairs the unreadable legacy whey sprite from the animated asset and adapts the missing legacy almond sign UV textures; those are output-only exceptions to texture preservation.

## 3. Requirements, metadata and notices

Required: Minecraft 1.21.1, Java 21, NeoForge (tested 21.1.267; declared [21.1.219,21.2)), original Farmer's Delight 1.3.4 (declared [1.3.4,1.4)). JEI and Serene Seasons optional. Other integrations are optional data/runtime test profiles, not mandatory dependencies. Exact tested versions are listed in README/gradle.properties; declared ranges are not exhaustive test matrices.

The candidate uses native META-INF/neoforge.mods.toml, mod ID italiansdelight, Margherita logo, mixin config and boat enum extension. No Fabric manifest or required Fabric runtime. The five original legal files are byte-identical to the Fabric snapshot and packaged in the JAR. Preserve the Apache-2.0 LICENSE, NOTICE, CREDIT, third-party notices and explicit MIT crate license.

Legal limitation: original Italian's Delight artist-level asset provenance and some Rustic/Alcocraft copyright notices remain unresolved. The upstream Rustic and Alcocraft default source trees checked during this review contain no standalone license/notice file; CurseForge lists MIT as previously recorded. Do not invent a copyright holder/year or represent these checks as comprehensive clearance. Obtain/retain any actual upstream asset-specific notices before distributing if required. The source project is independent, not an official continuation.

## 4. Validation snapshot

Last completed prior CI [46052322543](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/46052322543) at 794e02b: 11/11 jobs passed, including 14 required GameTests with Serene Seasons, all twelve periods and greenhouse boundary checks. Author verified the client rendering fixes and JEI. Create endpoint transfer passed in the prior CI, but further Create work is deferred. Original Thirst Was Taken is startup-only; TW2/TAN API/tag checks passed, consumption not fully certified. New network, tint, parity and metadata tests must pass before final packaging.

## 5. Draft distribution description (English)

**Italian's Delight — Native NeoForge 1.21.1 port**

Bring the flavors of Italy to Minecraft with this independent Farmer's Delight add-on. Create customizable pizzas, make and age cheeses and cured meats, harvest olives, almonds and cherries, grow vineyards and chili peppers, crush fruits and ferment juices into wines and other beverages. Includes Cooking Pot and Cutting Board recipes, and optional JEI and Serene Seasons integration.

Requires Minecraft 1.21.1, Java 21, NeoForge and the original Farmer's Delight. No Fabric API required. Optional thirst compatibility is preliminary; full Create automation and live multiplayer testing are not yet certified. Do not downgrade Minecraft 26.x worlds; use a new world and backups.

## 6. Final release gate (not performed here)

- Confirm latest CI after these changes and review network/tint markers.
- Resolve or explicitly assess remaining asset attribution limitations.
- Author decides final version and release channel (beta suggested because multiplayer is untested).
- Build the final playable JAR and calculate SHA-256 from the same verified artifact. Do not distribute the sources JAR. Do not tag/publish before approval.
