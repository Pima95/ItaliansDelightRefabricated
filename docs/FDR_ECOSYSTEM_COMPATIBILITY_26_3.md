# Compatibility with Farmer's Delight ecosystem — Italian's Delight 1.2.0 / Fabric 26.3

Scope: **existing development branch only**. This integration does not alter world generation, recipes, registry IDs, saved NBT, fermentation, wine effects or Serene Seasons seasonal rules. No extra hard dependencies have been introduced.

## Compatibility approach

* `farmersdelight:drinks`, `farmersdelight:meals`, `farmersdelight:snacks`, `farmersdelight:wild_crops`, `farmersdelight:flat_on_cutting_board` use the same category paths as Refabricated 26.3. They **advertise categories**; they do not add custom machines or recipes for other mods.
* `c:foods`, `c:foods/fruit`, `c:foods/vegetable`, `c:crops`, `c:seeds` and other existing conventional tags let cross-mod ingredients/recipe filters see Italian's Delight items.
* `createaddition:plant_foods` is the same optional tag path used by Farmer's Delight Refabricated; adding the tag alone does **not** promise Create or Create Addition machine integration.
* **Thirst Was Taken 2:** existing optional `data/italiansdelight/thirstwastaken2/drinks/italiansdelight_beverages.json` gives 6/4 thirst/quenched to juices, 4/0 to sheep milk, 2/0 to wine/mead/cider and 0 to olive oil.
* **Tough As Nails 26.3:** optional tags under `data/toughasnails/tags/item/thirst/` and `hydration/` grant wine 2 thirst + 20%-class hydration, milk 4 + 40%, and juices 6 + 60%. Uses official 26.3 group naming. No dependency.
* **Dehydration 26.3:** `data/italiansdelight/hydration_items/italiansdelight_drinks.json` and category tags grant 2/4/6 thirst. The mod's loader enumerates `hydration_items` files across namespaces and merges them. No dependency.
* **Farmer's Delight itself:** required upstream mod ID is `farmersdelight`; standard Cutting Board, food item components and food effects continue unchanged.
* **Serene Seasons:** seasonal tags and optional integration already existed; deliberately not changed in this pass.
* **AppleSkin:** the base Minecraft food/consumable components remain visible to standard nutrition viewers; this does not assert custom tooltip integration.
* **JEI:** custom recipes/categories are already integrated through the project's JEI entrypoint.

## Outside the scope of a *verified* support claim

The following require dedicated 26.3 code/API adapters or builds and **cannot be claimed as working** just by adding category tags:

* **REI, EMI / EMI Port Forward, RRV:** crafting recipes should remain visible through Minecraft's normal recipe data, but Italian's Delight custom machine categories are JEI-specific. Adding FDR's plugin IDs would be incorrect. A separate viewer plugin must be built and tested for each.
* **Jade / WTHIT:** generic block inspection may work; bespoke Brewing Barrel/Cheese Vat tank and quality overlays need a separate client plugin.
* **Create (full kinetic automation), Supplementaries and Quark:** shared crops and other existing conventional resources may be used, but machine automation, ropes with different behavior and interactions require individual adapters.
* **CraftTweaker, Mod Menu, YACL/Cloth Config:** available upstream does not imply Italian's Delight adds its own custom scripting/config UI. No new registration claimed.
* **Original Thirst Was Taken**, **Yet Another Thirst**, **Droplets of Thirst**, **Legendary Survival Overhaul:** no *verified Fabric 26.3* port/API contract; not declared compatible.
* Other historical Farmer's Delight addons are unbounded and version-specific. Support depends on them accepting shared ingredient tags and implementing their own compatibility with our mod.

### Validation

`python3 .github/scripts/check_fdr_compatibility.py` checks all supported datapack paths, hydration group consistency, ingredient tags and excluded non-drinks. CI also compiles Java and packages resources. Runtime testing with the exact external mods remains required before listing compatibility as confirmed on Modrinth/CurseForge.

### Upstream references

* Refabricated 26.3 resources: https://github.com/MehVahdJukaar/FarmersDelightRefabricated/tree/fabric/latest/26.3/src/generated/resources/data
* Tough As Nails 26.3: https://github.com/Glitchfiend/ToughAsNails/tree/26.3/common/src/generated/resources/data/toughasnails/tags/item
* Dehydration 26.3: https://github.com/Globox1997/Dehydration/tree/26.3/common/src/main/resources/data/dehydration
* Thirst Was Taken 2: https://whoisnighter.github.io/ThirstWasTaken2/docs/developers/data-packs
