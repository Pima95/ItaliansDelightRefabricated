# Water-like in-world liquid physics without infinite sources (Minecraft 26.2)

## Scope

Only the two **placeable** liquid families are affected:

- `italiansdelight:olive_oil` and `italiansdelight:flowing_olive_oil`
- `italiansdelight:apple_juice` and `italiansdelight:flowing_apple_juice`

Four storage-only fluids (sheep milk, whey, rennet, apple cider vinegar) stay unplaceable.

## Why the player previously fell through the liquid

The two fluids already extend `FlowingFluid`, but were not members of `minecraft:water`.
In Minecraft 26.2, entity fluid movement (swimming, buoyancy, flowing current)
relies on fluid tags. A **non-replacing** resource
`data/minecraft/tags/fluid/water.json` now references the existing mod-specific
fluid tags (which already include the still and flowing forms).

This makes entities interact with the two liquids as water: pushing by currents,
swimming and floating. It also intentionally activates other vanilla checks that
use the water tag (e.g. underwater effects, sponges and plant interactions).

## Infinite source generation is explicitly forbidden

Both fluid classes override
`canConvertToSource(ServerLevel world) { return false; }`.
This remains false even when the Minecraft `waterSourceConversion` gamerule
is true. Adjacent source blocks must **not** generate new source blocks.
A placed bucket is a source; flowing levels are not sources.

## Spread behavior

To match vanilla water's flow, both fluids now use:
- slope search distance: 4
- level drop-off: 1
- update delay: 5 ticks

These are separate from entity movement and separate from source creation.

## Verification

Static regression: `python3 .github/scripts/check_world_fluid_physics_26_2.py`.
Compilation: GitHub Actions Gradle build.

**Real in-game checks are still required:**
1. Pour each liquid in a >=3-block-deep basin; in survival, the player should
   float, swim with sprint, and move up with jump.
2. Pour on a slope: player and dropped items should move with the stream.
3. Place two source blocks with a gap and solid block underneath, with
   `/gamerule waterSourceConversion true`; gap may fill with a **flowing**
   state, but must never become a third **source**.
4. Remove the original buckets/sources: no independently generated infinite pool
   should remain.
5. Reload the world and repeat to confirm persistence.
6. Ensure milk, whey, rennet and vinegar buckets remain storage-only.

### References
- [Fabric 26.2: Creating Your First Fluid](https://docs.fabricmc.net/develop/fluids/first-fluid)
- [Minecraft water fluid tag](https://minecraft.wiki/w/Fluid_tag_%28Java_Edition%29)
