# Rustic liquid containers — shared fluid engine (26.2)

- Empty cauldron remains vanilla `minecraft:cauldron`, including vanilla water/lava fill behavior. We do NOT overwrite vanilla blocks or interaction maps.
- When another compatible liquid is inserted, the placed cauldron transitions to ONE internal `italiansdelight:fluid_cauldron` block, keeping the vanilla metal model and collision.
- The technical block carries a BlockEntity with fluid registry ID + volume (0–1,000 mB), persisted and sent to clients using vanilla block-entity data packets.
- Four 250 mB steps match the new olive-oil bottle. The new rendering uses the same shared textured horizontal fluid plane as the Barrel.
- Olive oil renders the first exact 16x16 frame from the uploaded Rustic `olive_oil_still.png`; water/milk/whey get original pixel surfaces with binary alpha.
- The fluid adapter `BarrelFluidContainers` resolves all registered source-fluid BucketItem containers. Special bottles are explicitly registered. Additional liquids reuse the same engine.
- Vanilla water/lava cauldrons are NOT replaced or intercepted. Vanilla behavior takes priority.
- Older `italiansdelight:olive_oil_cauldron` remains registered for loading existing worlds; it is legacy-only and no longer placed by the new interaction.
- No wood variants are added in this revision.

## Regression tests in a real Minecraft client
1. Empty vanilla cauldron remains iron and vanilla water/lava still work as usual.
2. Add one olive-oil bottle to an empty vanilla cauldron, inspect correct olive surface at 250 mB.
3. Add 3 more oil bottles, then retrieve 4 bottles; the original empty vanilla cauldron must return.
4. Repeat using a 1,000 mB oil bucket, reload between interactions, and try milk/whey bottles and buckets.
5. Check Barrel oil surface at 250/1000/8000/16000 mB and after world reload, break and replacement.
6. Confirm no transfer into a tank already holding a different liquid.
7. Confirm the oil source fluid remains placeable in the world like Rustic.
8. Inspect exact metal colors and pixel geometry of the recolored vanilla bucket.
