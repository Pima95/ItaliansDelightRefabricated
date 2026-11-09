# Golden Apple Juice — Rustic Crushing Tub follow-up (1.1.0)

## Recipe fidelity
Adapted from Rustic 1.12.2 `Recipes.addCrushingTubRecipes()` (both golden apple types):
- `minecraft:golden_apple` -> **100 mB** `italiansdelight:golden_apple_juice` + **one apple seed** per stomp.
- `minecraft:enchanted_golden_apple` -> **1,000 mB** of the SAME golden apple juice + **one apple seed** per stomp.
- Existing four recipes (olives, apples, sugar cane and honeycomb) are untouched.

## Gameplay and compatibility
- This juice has its **own registered Fluid ID** (`italiansdelight:golden_apple_juice`), not regular apple juice, water or honey.
- It is **storage-only**, via ContainedFluid: no world liquid block, movement physics or infinite-source generation.
- The 250 mB `golden_apple_juice_bottle` is an empty-glass-bottle extraction container; no new bucket, no status effect, no direct drinking mechanic.
- One ordinary golden apple yields only 100 mB; the player needs **at least 3 normal apples** for a first 250 mB bottle (50 mB remains), or **one enchanted apple** yields four bottles.
- All Crushing Tubs: 8,000 mB capacity, no mixing, no loss when full; one input consumed per landing. The apple seed byproduct is dropped by the existing CrushingTubBlock.
- Barrel and custom cauldron use the existing Fabric storage API for the registered fluid (same bottle, no special-case mix); reloading saves the original fluid ID and amount.
- JEI shows **two distinct recipes** in the original approved Crushing Tub GUI, as fluid ingredients plus apple seed byproducts. JEI item information explains extraction.
- Bottle model reuses the vanilla potion sprite with a unique amber tint. The contained-fluid rendering temporarily reuses vanilla honey block art with golden tint until bespoke fluid art is approved; no unrequested original texture asset is overwritten.
- No changes to existing fluid behavior, texture assets or GUI; the honey recipe remains virtual `minecraft:honey` and is not converted.

## In-game validation
1. Put 3 normal Golden Apples in one tub, stomp three times: amount **300 mB**. Fill **one bottle** (50 mB remains).
2. Put 1 Enchanted Golden Apple into an empty tub; stomp once: **1,000 mB**, yielding four 250 mB bottles and one apple seed.
3. Confirm no golden-apple-juice bucket exists and no fluid can be placed in the world.
4. Try mixing with regular apple juice: refuse mixing, do not consume ingredients. Test full 8,000 mB tank and save/reload.
5. Test bottle transfer into Barrel and cauldron, including exact 250 mB levels.
6. In JEI check both recipe amounts and the seeds; GUI must look identical to the already-approved background.

Source: https://github.com/cadaverous-eris/Rustic/blob/master/src/main/java/rustic/common/crafting/Recipes.java

CI: `.github/scripts/check_crushing_tub_golden_apple_juice_26_2.py` and full Java 25 Gradle build.
