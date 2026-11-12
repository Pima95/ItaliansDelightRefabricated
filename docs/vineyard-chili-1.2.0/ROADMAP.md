# Italian's Delight 1.2.0 — Vineyard & Chili Update

Branch: feature/vineyard-chili-1.2.0
Base: Minecraft 26.3, Java 25, development version 1.2.0-mc26.3-dev.
Status: first playable agriculture and grape-juice prototype implemented, CI and dedicated-server smoke passed. Grapevine canopy/rope, wine fermentation and ghost-pepper gameplay are NOT implemented yet.

## Phases
1. DONE: separate branch, documentation, development version and baseline CI.
2. PARTIAL: grape stems/seeds, one purple grape bunch, eight logical growth ages, repeat harvest; trellis canopy and red/white varietal split still pending.
3. PARTIAL: chili seeds, crop growth, original Rustic chili stages/stake model, red chili harvest and regrowth; ghost pepper mechanic pending.
4. PARTIAL: one registered grape juice fluid, 250 mB per bunch, barrel/cauldron, bucket/bottle and JEI; two colored grape musts pending.
5. NOT STARTED: dedicated fermentation barrel, quality/culture mechanics, wine bottles, fluid persistence.
6. PARTIAL: crushing tub and grape juice JEI done; new dishes pending.
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
