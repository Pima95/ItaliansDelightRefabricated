# Changelog

All notable changes to Italian's Delight Refabricated are documented here.

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
