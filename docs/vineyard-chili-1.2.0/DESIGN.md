# 1.2.0 — Design: grapes and chili peppers

Status: proposed gameplay. Do not treat these rules as implemented.

## Grapes and vines
- Grow red and white grapes on reusable vines trained to a trellis. Harvest ripe bunches without destroying the plant.
- Keep server-side growth and a seasonal/greenhouse bridge that is optional when Serene Seasons is absent.
- Existing texture at src/main/resources/assets/italiansdelight/textures/item/grape.png must not be overwritten without inspection.
- Reuse CrushingTubRecipes for two grape must IDs. Initial yield proposal: 250 mB per harvested bunch; balance later.
- Fermentation uses a NEW dedicated block entity, not the current BarrelBlockEntity (to preserve existing 16-bucket save behavior).
- Start with non-placeable grape must and wine fluids, bottled outputs, persistent fermentation time and compatible fluid transfer.
- Proposed food: grape schiacciata and cooked grape must.

## Chili peppers
- Farmland crop with seeds and eight logical growth ages, four visual phases, green unripe fruit and red ripe fruit.
- Right-click harvest resets growth; harvest on breaking and bonemeal must not duplicate drops.
- Dried chili and crushed chili; recipes: pasta all'arrabbiata and pasta aglio, olio e peperoncino.
- No automatic damage or new spicy effects without further design testing.

## Architecture and testing
- Reuse ModItems, ModBlocks, ModFluids, FluidCatalog, ModWorldGeneration, CrushingTubRecipes, existing JEI patterns.
- New registry IDs are fixed only when gameplay items are first implemented, never rename released IDs.
- Test texture/recipe JSON, saved states, chunk unload/reload, dedicated-server startup, fluid quantity, JEI, and recipes.
- Develop and test on Minecraft 26.3 first. Port to 26.1/26.1.1/26.1.2 only after a stable 26.3 implementation.

Open choices: trellis versus pergola, worldgen rarity, grape yield, fermentation duration/capacity, wine effects and balancing.