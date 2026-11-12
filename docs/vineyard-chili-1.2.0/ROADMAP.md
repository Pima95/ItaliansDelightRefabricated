# Italian's Delight 1.2.0 — Vineyard & Chili Update

Branch: feature/vineyard-chili-1.2.0
Base: Minecraft 26.3, Java 25, development version 1.2.0-mc26.3-dev.
Status: planning; crops and recipes are not implemented yet.

## Phases
1. Separate branch, documentation, development version and baseline CI.
2. Grapevines on supports, red/white grapes, harvesting without destroying plants.
3. Chili pepper seeds and crops; green immature fruit, red mature fruit, repeated harvesting.
4. Crushing Tub: red and white grape must, fluid transfer without accidental mixing.
5. Dedicated fermentation barrel, persistent recipes, red/white wine and bottles.
6. Cooking and JEI: arrabbiata, garlic-olive oil-chili pasta, grape schiacciata and cooked must.
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