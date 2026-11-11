# Rustic barrel / olive oil — Minecraft 26.2
- Open Barrel: bottom + four walls; 16,000 mB; single fluid type, no mixing.
- All standard BucketItem fluids (water, oil and future registered compatible source fluids), plus vanilla water bottles, milk, sheep milk and whey.
- 250 mB bottles; 1,000 mB buckets. Save, reload, and dropped Barrel item retain type and amount; old OliveOilAmount is migrated.
- Rainwater in empty/water barrels. No mixing rainwater into other fluids.
- Bucket uses a vanilla empty bucket metal sprite with a yellow olive liquid overlay; bottle uses vanilla potion sprite with constant golden olive tint.
- Cauldron: 4 x 250 mB levels; oil bucket fills empty cauldron completely; oil bottle adds one level; empty bottle draws one; empty bucket draws four; empties back to vanilla.
- Oil can already be poured onto the ground as a real world fluid. Client renderer now uses author's Rustic still, flow, and overlay textures.

## In-game regression checks
1. Look into the empty Barrel; bottom should be visible and no upper lid; collision hollow.
2. Insert water, olive oil, milk, whey and lava on separate barrels; mixed inputs must be rejected.
3. Break/re-place and reload a partially filled Barrel; check fluid type and volume.
4. Rain fills only water/empty barrels.
5. Fill empty vanilla cauldron with oil bottle/bucket; empty with bottle/bucket, verify quantities and no duplication.
6. Place olive oil on ground with bucket; verify the liquid actually renders and can be scooped up.
7. Creative inventory and JEI remain intact. Check no crashes with the previous world save.

Known limitation: custom modded bottle item types require an explicit container adapter; most fluid buckets work automatically.

- Rustic safety behavior: vanilla lava is not accepted by the wooden liquid barrel; future hot/gaseous modded fluids need explicit classification before they are enabled.
