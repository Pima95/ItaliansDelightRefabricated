# Shared fluids — first multi-loader-friendly step (Minecraft 26.2)

## Scope and implementation
- The loader-neutral `FluidCatalog` contains the seven stable fluid identifiers and distinguishes placeable olive oil / apple juice from non-world milk, sheep milk, whey, rennet and apple cider vinegar.
- All seven are registered in the vanilla **Fluid registry** (Fluid is a Minecraft class, not Fabric-specific); the last five use `ContainedFluid`, which cannot generate world blocks or flow.
- `italiansdelight:cow_milk` is a **namespaced adapter** for vanilla milk, which Minecraft itself does not register as a fluid. Older tanks holding virtual `minecraft:milk` are canonicalized when read. Do not claim that other mods' milk fluids are automatically identical.
- Existing Barrel and cauldron `FluidVariant` storage migrates legacy `FluidId` + `Amount` data without changing item or block IDs. The old virtual tank path remains as a fallback for missing fluid mods.
- Existing item buckets/bottles are kept as they are, not replaced with world-placeable BucketItems. This allows recipes, inventories and cheese-vat logic to remain stable.
- JEI info tabs and locale names now also cover cow milk, rennet and vinegar.

## Loader portability
- Fabric backend: `CompatibleFluidTank`, `FluidTransferBridge`, and `FluidStorage.ITEM` providers, with 81,000 droplets per 1,000 mB.
- NeoForge backend (future): expose `IFluidHandler` on `Capabilities.FluidHandler.BLOCK` and `ITEM`; native bucket uses 1,000 units. Translate `FluidCatalog` IDs and preserve data components, do not copy Fabric's numeric storage units.
- Future shared modules: `common` (FluidCatalog + vanilla Fluid registration + recipes), `fabric` (Fabric Transfer API), `neoforge` (fluid capabilities), `client` (renderer adaptation), plus FDR integration per loader.
- **Architectury API 21.1.x supports 26.2**, including multiplatform registry abstraction and FluidStack; consider introducing it when the common/loader split begins, not as an extra runtime requirement during this safe incremental release.
- Reference: https://docs.architectury.dev/ and https://docs.fabricmc.net/develop/fluids/first-fluid
- Never treat non-placeable registered liquids as flowing/world fluids. No new blockstates or liquid world blocks are registered for dairy/condiments.

## In-game migration tests
1. Open an existing world with 1,000 mB `minecraft:milk` in a Barrel: it must become `italiansdelight:cow_milk` without loss, with the correct white animated surface.
2. Repeat for existing sheep milk, whey, rennet and vinegar, with buckets and bottles.
3. Use Fabric pipes to fill and empty these registered fluids in Barrel and filled cauldron, confirm variant ID and exact amount after reload.
4. In JEI ensure all seven fluid entries and the associated container information appear.
5. Confirm cow/sheep milk, whey, rennet and cider vinegar cannot be placed into the world using their original items.
