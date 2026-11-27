# Changelog

This branch records the native NeoForge port. The Fabric sections below are historical development notes, not the current publication status of the Fabric project.

## 1.2.0-mc1.21.1-neoforge — Native NeoForge port (unpublished candidate)

**Minecraft 1.21.1 · NeoForge · Java 21 · Farmer's Delight 1.3.4**

### Porting and compatibility
- Native NeoForge registrations, lifecycle, menus, client rendering, item components, persistence, fluid capabilities, world generation and events. No Fabric runtime dependency.
- Retained all supported source-snapshot recipes: 270 of 283. Removed 10 wood-variant recipes and 3 orchard shelf recipes because their base blocks do not exist in this Minecraft version.
- Converted modern model definitions, data codecs and datapack formats to 1.21.1; preserved source recipe data and retained source textures.
- Added native orchard boats and signs, fixed missing block render shapes, liquid sprites, transparent plants/doors/trapdoors and the Margherita mod icon. Restored constant potion overlay colors from the Fabric source (new automated tint check pending).
- JEI verified by the author; automated Serene Seasons calendar, greenhouse and foliage tests passed. Optional thirst integration has preliminary tests. Create support is deferred, despite successful endpoint/tank transfer tests.

### Validation and release limitations
- Prior CI [46052322543](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/46052322543): 11/11 jobs passed, including 14 required GameTests with Serene Seasons installed.
- Added vanilla packet/component roundtrip and menu checks, and final source parity/metadata checks; new CI results must be recorded before packaging the release.
- Real multi-player session not tested; no claim of full multiplayer compatibility. No downgrade support for Minecraft 26.x worlds.
- Version/name and distribution require author approval. No final JAR or tag is published in this preparation stage.
- Original code/artwork Apache-2.0; third-party credits and licenses retained; unresolved legacy asset provenance documented.

See [release readiness](docs/NEOFORGE_1.21.1_RELEASE.md) and [porting log](docs/NEOFORGE_1.21.1_PORTING.md).

## 1.2.0 — Vineyard & Chili Update (release candidate, NOT YET PUBLISHED)

**Minecraft 26.3 · Fabric · Java 25**. The full-version `italiansdelight-1.2.0-mc26.3.jar` is prepared for client testing on the **existing** `feature/vineyard-chili-1.2.0` branch; it is not yet a published release. No new branch or tag is created during preparation.

### Vineyard farming and wild crops
- Added **red and white grapes**, distinct seeds and juices, trellis-trained vines with Farmer's Delight rope, seasonal/biome-tinted foliage, ripening stages and repeat harvesting.
- Added two-block wild red/white grapevines, generating in suitable temperate biomes with occasional coarse dirt and dead bushes in newly generated chunks.
- Added chilies with growth, regrowth and rope/stake support, plus a rare Ghost Pepper; retained existing FDR tomato behavior.

### Fermentation, drinks and JEI
- Introduced wood-variant **Brewing Barrels** with a working GUI, individual drink quality, persistent tanks/culture and recipe viewing in JEI.
- Added **red and white wines, cherry wine, mead, cider, red wine vinegar**, and cherry juice, alongside existing apple cider vinegar and other compatible liquids.
- Added drinkable juice and fermented drink bottles with appropriate quality effects and optional Thirst Was Taken 2 integration; olive oil remains drinkable but does not restore thirst.
- Refined fluid bottles/buckets, language strings, color palettes, storage transfers and world/client rendering.

### Foods, pizzas and compatibility
- Added Spicy Olive Pasta, Pasta with Ricotta & Pancetta, Risotto with Scamorza & Olives, Pasta with Almond Pesto & Speck, Grape & Cherry Fruit Salad and Almond Biscuits.
- Added two recognized pizzas: **Spicy Olive Pizza** and **Pizza Principessa**; total recognized flavors: **23**. Different sauces can coexist per quarter, with the latest applied sauce as the base and previous sauces as garnishes.
- Corrected food pixel art, creative inventory order, individual pizza topping/slice art and removed the unintended crafting-table recipe for normal Pasta all'Amatriciana (Cooking Pot only).
- Deferred focaccia and beer; do not label them as 1.2.0 features. Existing registry IDs and saved composition rules are preserved.

### Requirements and validation
- Minecraft **26.3**, **Fabric Loader 0.20.1+**, **Fabric API 0.166.0+26.3**, **Farmer's Delight Refabricated 26.3-3.6.30+refabricated**, **Java 25**.
- JEI integration is included; Serene Seasons / GlitchCore and Thirst Was Taken 2 remain optional.
- **Pending for the candidate:** verify the exact `1.2.0-mc26.3` JAR via GitHub Actions, inspect the SHA-256 checksum and packaged legal notices, then complete [client, save/reload and optional-mod tests](docs/vineyard-chili-1.2.0/TESTING.md). Prior development builds passed CI and dedicated-server smoke, but these are not equivalent to final client or multiplayer verification.
- Back up worlds before trying the candidate; compatibility across Minecraft **26.2 to 26.3** must not be assumed without separately testing conversion and backups.
- Original contributions are Apache-2.0; third-party MIT artwork and notices remain separately attributed in [CREDIT](CREDIT.md), [THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES.md) and [FDR crate license](docs/FDR_CRATES_LICENSE.md). Attribution of some legacy assets still requires specific verification.

**Distribution:** After author approval of the tested binary, publish the exact verified JAR and its `SHA256SUMS` from the same CI run. Do not distribute the `sources` or `dev` JAR as the playable download; do not create a release tag before approval.

## 1.1.0 — Orchard Update (released 11 November 2026)

Published as [v1.1.0](https://github.com/Pima95/ItaliansDelightRefabricated/releases/tag/v1.1.0) on 11 November 2026, with a tested full-version 1.1.0 JAR and preserved existing-world compatibility. The original release notes document the validations and limitations; two-player multiplayer and live Serene Seasons/GlitchCore tests were not confirmed.

### Additional content and RC hardening
- Added Olive Pâté, Olive/Tomato-Basil bruschettas, two Olive pizzas,
  Almond & Basil Pesto, pesto pasta, Almond Cake and four slices,
  the Mortadella-Ricotta-Almond pizza, Cherry jam and Cherry Tart.
- Four non-mozzarella pizza slices now have a straight triangular crust
  without cheese drips. Reinserted slices preserve their toppings and
  cannot turn served pizzas into whole item drops.
- Optimized Cheese Vat recipe lookups, salt/Crushing Tub allocations,
  and pizza/fluid rendering without changing the approved gameplay.
- Headless dedicated-server startup and Gradle/static checks succeeded;
  the author confirmed the optimized features work in-game.
- Packaged upstream license notices for source artwork and crates;
  remaining individual asset attribution is documented.

### Orchard trees, fruiting and wood
- Added separate, original **Olive, Almond and Fruit Cherry** tree and wood
  families: logs and derivatives, saplings, doors, signs, boats and chest
  boats, cabinets, vanilla-style shelves and cheese-aging racks.
- Added a monumental **Ancient Olive** with a true irregular 2×2 trunk;
  four Olive saplings in a 2×2 can grow one, and it occurs rarely in nature.
- Added species-specific flowering/unripe/ripe foliage, seasonal appearance,
  exposed-natural-leaf right-click harvesting (1–3 fruit), bone-meal rules,
  regrowth and rotating **65% fruiting eligibility**.
- Added optional Serene Seasons integration; it is not a required mod.

### Natural generation — final approved balancing
- Normal Olive: **1/40** attempts/chunk in Plains, Sunflower Plains and
  Savanna variants, including Windswept Savanna.
- Almond: **1/40** in Plains/Sunflower Plains/Savanna/Savanna Plateau;
  **1/100** in Meadow using a distinct placed feature.
- Ancient Olive: **1/96** in its original biomes (**unchanged**).
- Fruit Cherry: **1/7** in Forest/Flower Forest/Cherry Grove (**unchanged**).
- These are *placement attempts*, not guaranteed trees, and only apply to
  **newly generated chunks**. The user approved the reduced density in-game.

### Orchard processing and compatibility
- Added five reversible fruit/herb crates and JEI recipes/information.
- Added 15 wooden **Crushing Tub** variants, including the original
  honeycomb, apple, olive, sugar cane and golden-apple processing paths.
- Added Barrel and cauldron liquid transfer with 250 mB bottles and
  1,000 mB buckets; new Golden Apple Juice and Honey buckets remain
  **non-placeable** and preserve their existing liquid identities.
- Reused provided Rustic fluid sprites and their animation metadata.
- Added compatibility checks for fluid storage and neighboring mods;
  kept the existing 1.0.0 Pizza Update behavior intact.
- **Not part of 1.1.0:** grapes, grape juice and wine; any stored artwork
  for these is unused and does not imply implemented gameplay.

### Release preparation
- Corrected Fabric mod-list icon metadata to point to the actual icon
  under `assets/italiansdelight/textures/icon.png`.
- Historical work records are available in the [1.1.0 changelog](docs/orchards-1.1.0/CHANGELOG.md) and [Git history](https://github.com/Pima95/ItaliansDelightRefabricated/blob/d351e1660eb4bea9327070edaa3d8c9c5fa5ff13/docs/orchards-1.1.0/RELEASE_READINESS.md). Build and server checks passed; real multiplayer and optional-mod tests were not confirmed.

## 1.0.0 — Pizza Update (released 23 October 2026)

The Pizza Update is published as [v1.0.0](https://github.com/Pima95/ItaliansDelightRefabricated/releases/tag/v1.0.0). This section records the features released after 0.4.0.

### Interactive pizza system

- Added placeable Farmer's Delight wheat dough, hand stretching and rolling-pin shaping, physical halves/quarters, ingredient placement and last-in-first-out removal.
- Added a data-bearing pizza peel that transfers one raw or cooked pizza between the world and the Pizza Oven without discarding its saved toppings.
- Added the Pizza Oven with a furnace-style GUI, vanilla fuel and hopper automation, and component-preserving baking; raw pizzas can also cook in vanilla furnaces and smokers.
- Added dynamic shapeless crafting of uniform raw pizzas alongside freeform preparation in the world.
- Added 18 recognized pizza flavors and Mixed Pizza with independent names, toppings and sprites for each of the four served quarters.
- Added custom slice nutrition derived from the actual canonical topping categories; at most two distinct ingredients per category contribute, capped at six hunger and six saturation points.
- Every recognized pizza now appears in JEI's Pizza Oven catalogue and Cutting Board slicing guide. Dough-only Bianca remains a world-prepared pizza rather than a false shapeless crafting recipe.
- A cooked whole pizza can also be cut on Farmer's Delight Cutting Board with any tagged knife, producing four individually data-bearing slices, including mixed pizzas.
- Added short Farmer's Delight Nourishment for slices meeting the nutritional threshold; slices of the same flavor stack up to 16.
- Added Pizza Oven, crafting and in-world preparation guides in JEI and organized Creative samples without duplicate empty Pizza Bianca entries.

### Compatibility and pre-release fixes

- Preserved pizza data through block breaking, loss of support, placement, ovens, slice serving and peel transfers.
- Improved hopper bowl handling for custom-component items and limited bowl returns to original sauce/cream applications.
- Updated old Mixed Pizza item icons and legacy saved slice food/effect components without changing their toppings.
- Fixed hook-only Scamorza and Provolone placement and guarded hanging-cheese/wheel drops when their supports are removed.
- Reused the original Margherita sprite as the Creative tab icon and Mixed Pizza whole-item illustration; approved textures are unchanged.

### Release review and tooling

- Bumped the JAR version to **1.0.0**, updated **Fabric Loader to 0.20.1** and **Farmer's Delight Refabricated 26.2 to 3.6.29**; **Fabric API 0.161.0+26.2** remains current for Minecraft 26.2.
- Completed the final reported Cheese Vat and code-audit work (author confirmation, 23 October 2026).
- Applied a conservative naming-path micro-optimization to avoid rebuilding the same quarter data and sorting the same topping list twice; previous baking, network update, bowl, peel and recipe-generation optimizations remain intact.
- Classified remaining optimization ideas as measurement-dependent or optional, avoiding speculative caching of mutable renderer states and recipe/world data. See the [historical optimization decision log](https://github.com/Pima95/ItaliansDelightRefabricated/blob/3801dd9762312c30706137bf06da88023f0ad6a3/docs/OPTIMIZATION_CLOSURE_2026-10-23.md).
- The published 1.0.0 changes are summarized in the [1.0.0 changelog](docs/pizza-1.0.0/CHANGELOG.md).

**Historical test record:** compilation and static checks do not substitute for multiplayer testing, which was not fully completed before publication. The archival [archived 1.0.0 readiness checklist](https://github.com/Pima95/ItaliansDelightRefabricated/blob/d351e1660eb4bea9327070edaa3d8c9c5fa5ff13/docs/pizza-1.0.0/RELEASE_READINESS.md) preserves the documented results. The author confirmed 1.0.0 was published and functioning.

## 0.4.0

### Basil

- Added **Basil** as a new ingredient and directly replantable crop.
- Added four visual growth stages mapped across the vanilla 0–7 crop ages.
- Added **Wild Basil** generation in Plains, Sunflower Plains, and Meadow biomes.
- Added Wild Basil bone-meal spreading and 1–2 Basil harvest drops.
- Reused the existing basil/herb textures without modifying the original assets.
- Added the common `#c:crops/basil` ingredient tag for cross-mod recipe compatibility.

### Recipes and compatibility

- Updated **Mozzarella Salad** to require Basil.
- Replaced the fixed Farmer's Delight cabbage leaf ingredient with `#c:foods/leafy_green`, allowing compatible leafy greens from other mods.
- Added Basil and Wild Basil to the Italian's Delight creative tab.
- Added English and Italian translations.

### Dependencies and tooling

- Updated **Farmer's Delight Refabricated** from 3.6.26 to 3.6.28 for Minecraft 26.2.
- Updated **JEI Fabric** to 30.39.0.233 (CurseForge file ID 9068092).
- Pinned **Fabric Loom** to the stable 1.18.3 release instead of a floating snapshot.
- Updated the **Gradle wrapper distribution** to 9.8.1.

## 0.3.0

### Cured meats

- Added **Prosciutto Crudo, Salame, Mortadella, Pancetta, Guanciale, Bresaola, Coppa, and Speck**.
- Added preparation-stage items and sliced variants for the cured-meat chains.
- Extended the existing Hanging Hook to support cheese and cured-meat aging/drying while preserving its historical registry ID.
- Added dedicated hanging models, hitboxes, textures, and persistent processing progress.
- Added Furnace and Smoker processing for Mortadella and Speck.
- Added Cutting Board recipes for all final cured meats.
- Rebalanced ingredient costs, slice yields, stack sizes, aging times, and food values.
- Split JEI presentation into **Cheese Aging** and **Hanging Aging & Drying** categories.

### Dishes

- Added **Pasta alla Carbonara**.
- Added **Pasta alla Gricia**.
- Added **Pasta all'Amatriciana**.
- Added **Pasta with Speck and Gorgonzola**.
- Added **Risotto with Speck and Gorgonzola**.
- Added **Risotto with Parmigiano Reggiano**.
- Added gameplay logic for the existing **Mozzarella Salad** texture.
- Added gameplay logic for the existing **Mushroom Risotto** texture.
- Added Farmer's Delight **Nourishment** to complete meals.
- Rebalanced nutrition, saturation, and Nourishment duration against Farmer's Delight Refabricated 26.2.
- Reorganized the Italian's Delight creative inventory into clearer production and food groups.

### Salt

- Added a dedicated **Salt Evaporation** JEI category.
- JEI now displays a full cauldron's **1000 mB of water** as a fluid input and Salt as the output.
- Added the vanilla Cauldron as the JEI catalyst/icon.
- Documented the real **3–7 Salt** yield, 10 minutes of useful daylight, and environmental requirements.

### Compatibility and fixes

- Added Furnace compatibility alongside Smoker processing for food recipes that use vanilla cooking.
- Preserved compatibility of the existing cheese aging and Hanging Hook systems.
- Updated documentation, test checklists, balance notes, and recipe references.
- Fixed stale documentation links and outdated cured-meat yield references.

## 0.2.0

### Cheese update

- Added the Cheese Vat and its GUI, automation, whey storage, and JEI integration.
- Added cheese-making ingredients and the first complete dairy production chains.
- Added cheese aging racks and the Hanging Hook.
- Added salt evaporation in vanilla water cauldrons.
- Added Cardoon generation and cultivation.
- Added cutting/grating support for cheeses.
