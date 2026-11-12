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
## Rustic wine fermentation — verified design reference, NOT yet implemented
- User archive supplies animated grape juice art and the `winemaxxing` advancement JSON, **not** Java code for wine brewing or its GUI.
- Original Rustic uses a brewing barrel with input and output fluid tanks of **8,000 mB** and an auxiliary culture tank of **1,000 mB**.
- A batch takes **12,000 ticks** (half a Minecraft day). Fresh wine starts with random quality **0.05–0.75**.
- A wine culture of the same type is not consumed. The next batch has quality roughly **culture−0.01 to culture+0.04** (clamped 0–1).
- The wine quality must persist per bottle and fluid stack, not as one global value, and cannot mix without a defined policy.
- Original Rustic requires a dedicated brewing barrel with proper input/output and culture behavior. Do **not** claim that an ordinary fluid Barrel ferments automatically.
- The uploaded `winemaxxing.json` uses removed Forge/1.12 inventory predicates and old NBT; it needs a separate 26.3 advancement implementation after the wine-quality data component is implemented.
- For safety, implement the brewing barrel and wine as an isolated phase with dedicated client/server and save/load tests rather than adding a fake crafting recipe.

## Botte di fermentazione: primo porting 26.3
La macchina `brewing_barrel` converte `grape_juice` in `wine` dopo 12.000 tick, con capacità di 8.000 mB per ingresso e uscita. La qualità iniziale è 5–75%; una coltura opzionale conservata separatamente influenza il lotto successivo di -1..+4 punti. La qualità è persistente nel BlockEntity e nel componente `wine_quality` di ogni bottiglia, visualizzato nel tooltip. Il vino non è ancora bevibile e non si trasferisce a serbatoi generici: il passaggio tramite Fabric FluidVariant dovrà preservarne prima il componente qualità. Testare sul client prima di dichiarare la meccanica completata.

## Trellis + pepper support design (Nov 12)
Rustic's original crops require reusable wooden stakes and horizontal rope for grape vines. We reuse FDR rope directly; no local rope item is added. `crop_stake` extends IronBarsBlock to make FDR's RopeBlock connect to it on horizontal faces. A crop stake can be turned into a special `staked_tomatoes` subclass using the *original* Farmer's Delight tomato seed. The stake returns after the tomato crop is removed. Staking chili uses `chili_pepper_seeds`; each mature pepper stem can grow two upper segments by replacing existing Farmer's Delight rope, which is restored when plants are broken. Grape stems use vanilla crop age 0..7 for save compatibility, but never yield grapes themselves; overhead horizontal rope becomes `grape_leaves` canopy, spreading at most one rope segment in each direction and fruiting only when air is beneath.

**Tag compatibility:** the sole stake recipe uses one local ingredient tag pointing to optional `#minecraft:logs` and `#c:logs`. An earlier experiment had registered the stake as a Farmer's Delight rope, but this was removed: FDR converts rope to an upper tomato plant and then restores rope, which would lose a reusable stake. Our integration instead plants FDR tomato seeds *directly on* the stake.
