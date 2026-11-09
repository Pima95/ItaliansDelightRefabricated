# Changelog

All notable changes to Italian's Delight Refabricated are documented here.

## Unreleased — Orchard Update 1.1.0 (development branch)

**Development status (9 November 2026):** main functionality is implemented and
the author has confirmed the final **natural-generation density** in-game.
This is not a 1.1.0 release announcement. The branch still has
`mod_version=1.0.0` until release-candidate validation and an explicit
decision to change the shipped version and publish.

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
- Prepared [the 1.1.0 readiness checklist](docs/orchards-1.1.0/RELEASE_READINESS.md)
  and [draft release notes](docs/orchards-1.1.0/RELEASE_NOTES_DRAFT.md).
  A green CI build does not establish unperformed dedicated-server,
  multiplayer or saved-world compatibility tests.

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
- Classified remaining optimization ideas as measurement-dependent or optional, avoiding speculative caching of mutable renderer states and recipe/world data. See [the optimization decision log](docs/OPTIMIZATION_CLOSURE_2026-10-23.md).
- Prepared [the 1.0.0 release notes draft](docs/pizza-1.0.0/RELEASE_NOTES_DRAFT.md) and updated the release checklist.

**Historical test record:** compilation and static checks do not substitute for multiplayer testing, which was not fully completed before publication. The archival [1.0.0 readiness checklist](docs/pizza-1.0.0/RELEASE_READINESS.md) preserves the documented results. The author confirmed 1.0.0 was published and functioning.

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
