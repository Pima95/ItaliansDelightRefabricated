# Changelog

All notable changes to Italian's Delight Refabricated are documented here.

## Unreleased — Pizza Update 1.0.0 (development branch; NOT published)

This section describes changes currently implemented on `feature/pizza-1.0.0`, **not an available 1.0.0 download**. Version metadata and published downloads remain at 0.4.0 until the author signs off on release testing.

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

### Pre-release review and release gate

- Completed the final reported Cheese Vat and code-audit work (author confirmation, 23 October 2026).
- Applied a conservative naming-path micro-optimization to avoid rebuilding the same quarter data and sorting the same topping list twice; previous baking, network update, bowl, peel and recipe-generation optimizations remain intact.
- Classified remaining optimization ideas as measurement-dependent or optional, avoiding speculative caching of mutable renderer states and recipe/world data. See [the optimization decision log](docs/OPTIMIZATION_CLOSURE_2026-10-23.md).
- Prepared [the 1.0.0 release notes draft](docs/pizza-1.0.0/RELEASE_NOTES_DRAFT.md) and updated the release checklist.

The Java 25 CI build and resource/static checks run on each branch push. **Dedicated-server/multiplayer testing cannot currently be completed by the author**, and detailed runtime corner cases remain unconfirmed; they are not implicitly passed by compilation. Follow [the 1.0.0 readiness checklist](docs/pizza-1.0.0/RELEASE_READINESS.md). **Do not change `mod_version`, tag a release, upload to Modrinth/CurseForge, or merge to protected `main` until the author approves these checks.**

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
