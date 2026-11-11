# Rustic Barrel and Olive Oil — 1.1.0

The four supplied Rustic 1.12.2 JSON assets were adapted for Fabric Minecraft 26.2. The model retains six elements, UV coordinates, and item transforms. The shaped recipe retains the original PSP/I I/PSP pattern and yields two barrels.

## Current scope
- The barrel stores **olive oil only** (16,000 mB; 1 bucket = 1,000 mB; 1 glass bottle = 250 mB).
- Right-click with full oil containers to deposit, empty containers to withdraw; empty hand reports stored amount.
- Contents survive world reload and are transferred into the barrel's dropped BlockItem data.
- The oil bucket places and retrieves a real oil source fluid; flowing and overlay textures reuse the user's Rustic assets.
- The oil bottle is an ingredient/container, not a beverage.
- No olive-processing recipe or Crushing Tub integration in this change.

## Texture dependency
The user-created `assets/italiansdelight/textures/block/barrel.png` has not yet been committed to this branch. The model therefore temporarily references the existing `block/brewing_barrel_side` texture. When the author's texture is committed, change both texture references in `models/block/barrel.json` to `italiansdelight:block/barrel`.

## Manual gameplay verification
1. Check two barrels output from crafting, placement and item rendering.
2. Fill to 16,000 mB and verify overflow rejection; drain in 1,000/250 mB increments and verify underflow rejection.
3. Test stacked bottles and empty/full container exchange in Survival and Creative.
4. Save/reopen the world, then break and re-place a barrel with oil stored.
5. Place a source using oil bucket, observe flow/still textures, and scoop it again.
6. Confirm translated Creative tab items and binary-alpha texture assets.
