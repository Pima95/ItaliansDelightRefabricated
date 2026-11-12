# Italian's Delight 1.2.0 — Vineyard & Chili Update

Branch: feature/vineyard-chili-1.2.0
Base: Minecraft 26.3, Java 25, development version 1.2.0-mc26.3-dev.
Status: first playable agriculture and grape-juice prototype implemented, CI and dedicated-server smoke passed. Red/white varietals, a brewing GUI and ghost pepper's original special effects are not yet implemented; vertical/horizontal crop growth now uses FDR rope.

## Phases
1. DONE: separate branch, documentation, development version and baseline CI.
2. PARTIAL: grapes on horizontal Farmer's Delight rope, one-unit side spread and independent fruit harvesting implemented; red/white varietals and polishing pending.
3. PARTIAL: craftable generic log-tag crop stake, chili planting on stake, vine climbing up original Farmer's Delight rope, FDR tomato-on-stake support, and ghost pepper 1:42; special food effects pending.
4. PARTIAL: one registered grape juice fluid, 250 mB per bunch, barrel/cauldron, bucket/bottle and JEI; two colored grape musts pending.
5. PARTIAL: dedicated brewing barrel (8 L input/output), 12,000-tick fermentation, optional culture, quality on individual bottles and persistence; no GUI or generic wine-fluid transfers.
6. PARTIAL: crushing tub, grape juice and wine instruction pages in JEI; new dishes and graphical brewery category pending.
7. World generation, optional Serene Seasons, balance and QA.
8. After 26.3 gameplay tests, separately port to Minecraft 26.1.x.

## Release requirements
- Preserve all existing Minecraft 1.1.0 item/block/fluid IDs and saved worlds.
- Preserve existing Barrel and Crushing Tub behavior.
- Avoid fluid and item duplication with bonemeal, extraction or inventory automation.
- Ship blockstates, loot, crafting, language translations, JEI and textures together.
- Existing item texture grape.png remains untouched pending review.
- No commits to 1.1.0 branches or main. Test in disposable worlds and on a server.
- No public 1.2.0 release until build, world reload, client/server and gameplay tests pass.

For design decisions see DESIGN.md.
## Completed test runs (12 November 2026)
- Java 25 Gradle build + JSON audit: [success](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43200831498).
- Dedicated Fabric server boot and world reload: [success](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43200831629).
- These do not constitute manual client/gameplay verification; run the steps in TESTING.md.

## Corrections after user playtest — 12 November 2026
- Registered `crop_stake`, one recipe with shared tags for logs; tomatoes reuse the actual FDR seed and growth block subclass; FD ropes connect physically to posts without tagging posts as consumable ropes.
- Grapevines now climb horizontal FDR rope and fruit only on side canopy segments.
- Chilis plant only on a stake and grow upward along FD rope; they keep the rare 1:42 ghost pepper behavior.
- Added real wine fluid atlas for JEI, missing translations for grape juice and wine, wine bucket item (quality component) and filled wine bucket sprite.
- Source parity checked against Rustic original and FDR Fabric 26.3; browser could not stream the YouTube recording.
