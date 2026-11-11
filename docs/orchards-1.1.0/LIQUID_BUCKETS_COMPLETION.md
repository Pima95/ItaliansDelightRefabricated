# Liquid buckets — Minecraft 26.2 (9 November 2026)

All mod-added liquid families now have an appropriate 1000 mB bucket. The new variants are:

| Item | Fluid identity | World placeable? | Transfer route |
|---|---|---|---|
| `italiansdelight:golden_apple_juice_bucket` | `italiansdelight:golden_apple_juice` | **No** | Fabric FluidStorage.ITEM and legacy tub adapter |
| `italiansdelight:honey_bucket` | Historic virtual `minecraft:honey` | **No** | Legacy ID-based container adapter (not Fabric fluid registry) |

Previously present bucket items (olive oil, apple juice, sheep milk, whey, rennet and apple cider vinegar) remain unchanged. Vanilla water and cow milk buckets remain vanilla, honey bottles remain `minecraft:honey_bottle`.

## Safety and compatibility
- No new placeable block, no new `minecraft:water` fluid tag, no source regeneration. Both buckets are **plain Items**, not `BucketItem`s.
- The existing `italiansdelight:golden_apple_juice` stays a registered **non-world** `ContainedFluid`; its `getBucket()` now returns the registered Golden Apple Juice Bucket. Its Fabric item adapter supports 1000mB bucket and 250mB bottle.
- Honey stays the **virtual** `minecraft:honey` tank ID. Neither the ID nor existing world data changes; new Honey Buckets transfer through `BarrelFluidContainers`. They are not falsely exposed as a registered Fabric FluidVariant.
- Both can be retrieved from Crushing Tub (1000mB), emptied into and filled from Barrel or custom cauldron, and appear in JEI/Creative. No crafting-time fluid generation recipes were added.
- Vanilla iron silhouette and gray pixels were copied unchanged from the already-approved Apple Juice bucket. Only five liquid palette colors are recolored; textures are exact 16×16 with binary alpha 0 or 255.

## Manual tests
1. Make 1000mB honey with four honeycombs (4 landings). Empty bucket gets exactly one new Honey Bucket. Return it to Barrel and check 1000mB, then take four vanilla honey bottles; no duplication.
2. Make 1000mB golden apple juice with one enchanted golden apple. Empty bucket gets one new Golden Apple Juice Bucket. Verify it contains 1000mB by re-inserting to Barrel, then filling four 250mB bottles.
3. Transfer each new bucket into an empty custom cauldron (four 250mB levels) and re-extract with empty bucket. Check that tank empties and empty cauldron reappears.
4. Right-click terrain with both filled buckets: no fluid block should appear and no bucket should be consumed.
5. Save/reload each partially filled container and tank. Ensure `minecraft:honey` and `italiansdelight:golden_apple_juice` IDs remain stable.
6. Verify existing JEI Crushing Tub UI and established olive oil/apple juice world-fluid behavior are unchanged.

CI: `.github/scripts/check_new_liquid_buckets_26_2.py` plus full Gradle build.
