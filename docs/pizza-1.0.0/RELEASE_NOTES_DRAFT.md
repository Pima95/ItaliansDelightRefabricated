# Italian's Delight Refabricated — Pizza Update 1.0.0

> **DRAFT — NOT PUBLISHED.** Prepared from the `feature/pizza-1.0.0` branch on 23 October 2026. This is release-note copy for the eventual Modrinth/CurseForge/GitHub publication, **not** an announcement that version 1.0.0 is available. The shipped version remains 0.4.0 until the release is explicitly approved.

## Highlights

The Pizza Update introduces a complete, interactive pizza-making system for **Italian's Delight Refabricated**, an add-on for **Farmer's Delight Refabricated**.

### Prepare your pizza in the world

- Place Farmer's Delight dough, stretch it by hand or with a rolling pin, and divide the base into halves and quarters.
- Add toppings independently to each physical quarter. Remove toppings in reverse order and reclaim eligible ingredients according to their original application.
- Prepare **18 recognized pizza flavors** or combine different toppings into a **Mixed Pizza** with independently identified slices.
- Make uniform pizzas with shapeless crafting or prepare freeform recipes directly in the world.

### Bake and serve

- Bake in the new **Pizza Oven** (20 seconds at 20 TPS), a **Smoker** (30 seconds), or a vanilla **Furnace** (60 seconds).
- Use the reusable **Pizza Peel** to carry and transfer a single pizza while preserving its toppings.
- Use vanilla-style fuel, GUI and hopper integration in the Pizza Oven.
- Take four quarter-specific slices from a cooked pizza, or cut the whole pizza with a valid knife on Farmer's Delight Cutting Board.
- Slices of the same canonical flavor stack up to **16**. Nutrition is calculated from the ingredients in each slice, with caps to keep combinations balanced.

### Recipes, crops and polish

- Browse recognized pizzas, preparation guides, oven recipes and cutting instructions in **JEI**, including searchable pizza and slice variants.
- Explore expanded Cardoon cultivation and revised natural distribution of Wild Cardoon and Wild Basil.
- Enjoy refinements to hanging cheese/meat hooks, ingredient/container handling and compatibility with the existing cheese and cured-meat production chains.
- Includes bug fixes for legacy whole-mozzarella pizza behavior, dropped items when supports disappear, and selected inventory/rendering cases.

## Requirements

| Dependency | Requirement |
|---|---|
| Minecraft | **26.2** |
| Mod loader | **Fabric Loader 0.19.5+** |
| Java runtime | **Java 25** |
| Fabric API | **0.161.0+26.2** |
| Farmer's Delight Refabricated | **26.2-3.6.28+refabricated** |
| JEI | Supported for recipe viewing |

Use a backup when opening worlds created with earlier development builds. Previously started Cheese Vat recipes whose internal recipe IDs changed during consolidation can lose partial **progress**; produced items are not migrated or intentionally changed.

## Test status before publication

The author has confirmed the basic Cheese Vat checks, final code audit, the most recent JEI presentation fixes, core pizza slice stacking, hopper input protection and preservation of Mixed Pizza flavors through save/reload.

**Outstanding:** dedicated-server and two-player concurrency tests, plus unconfirmed corner cases from [the full release checklist](RELEASE_READINESS.md). These are **not** marked passed. Further profiling of optional performance opportunities is tracked in [the optimization decision log](../OPTIMIZATION_CLOSURE_2026-10-23.md).

## Publisher checklist — remove this section from public notes

1. Confirm the final runtime release checks and obtain explicit author approval.
2. Verify the latest GitHub Actions Java 25 build for the intended release commit.
3. Merge the prepared branch into protected `main` through the approved PR process.
4. Only then set `mod_version=1.0.0` and adapt/remove the development-only release metadata check in the appropriate commit, rebuild and verify the actual 1.0.0 JAR.
5. Create the GitHub tag/release and upload that same verified artifact to CurseForge and Modrinth. Cross-check dependencies and supported Minecraft version.
6. Change this note's DRAFT banner to the verified publication state.

Do **not** publish or tag from this draft alone.
