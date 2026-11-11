# Rustic wooden vessels, Minecraft 26.2
- IDs `italiansdelight:barrel` and `italiansdelight:crushing_tub` remain, now as **Oak** variants, preserving worlds.
- 14 variants each for spruce, birch, jungle, acacia, dark_oak, mangrove, cherry, pale_oak, bamboo, crimson, warped, olive, almond, fruit_cherry, all sharing the original hollow geometry and interactions.
- All 30 new 16×16 RGBA block sprites are authored from a current vanilla plank pattern and palette (current 26.2 palettes for vanilla woods). Both Barrel corners use iron at **UV x=2,3,12,13**, so the original 1-pixel side edge can no longer expose wood.
- Crushing Tub corners also have continuous steel bands. All texture alpha is exactly 255.
- Recipes are now wood-specific; no generic planks recipe unexpectedly yields Oak. Every wood has matching planks and slabs, registered blocks, BlockItems, item models, blockstates, loot, mining tags, localized display names and Creative tab entries.
- Barrel block entity accepts each variant: the same 16,000 mB multi-fluid system, drop item NBT, animated fluid surface, rain behavior and old-world persistence.
- Remove the legacy debug action-bar status on an empty-hand click or rejected fill. Cauldron did not emit its own debug message in common source.
- Crushing Tub processing mechanics are deferred; this pass adds wood/texture variants only.
- In-game tests: verify every variant and corner from all rotations, visual match with plank blocks, old world loading, bottle/bucket, break/replacement NBT and creative tab.
