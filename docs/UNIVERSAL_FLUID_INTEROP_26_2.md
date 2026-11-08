# Fabric Transfer API — Universal fluid interoperability

- `FluidStorage.SIDED` registered for all Barrel (15 wood variants) and generic filled cauldrons.
- `FluidStorage.ITEM` handles buckets, bottles and foreign fluid-bearing item stacks from other mods via `ContainerItemContext.ofPlayerHand`.
- Transfers are atomic: held-item storage and tank storage participate in ONE Fabric transaction; failed transfers roll back, not duplicating fluid or items.
- FluidVariant (including component patch) stored with the fluid, not just a string ID. Old `FluidId` + `Amount` NBT is read and migrated; `AmountDroplets` preserves sub-mB amounts.
- Fabric units: 81000 droplets/bucket; 20250 droplets/bottle. Cauldron accepts increments of 250 mB, while Barrel supports arbitrary droplets.
- Milk, whey, rennet and vinegar are virtual liquids from the mod's older code; until they are registered as actual Fabric Fluid variants, they remain usable via dedicated 250/1000mB item interactions. They are not exposed via Fabric pipes because this would misidentify them as vanilla water.
- Vanilla empty/water/lava cauldron still remains a vanilla block; custom external fluids use existing `italiansdelight:fluid_cauldron` only while occupied.
- Exotic mods that do not implement the Fabric Transfer API and do not register a normal fluid bucket need their own adapters; it is not possible to discover arbitrary fluid contents from an opaque ItemStack.
- Barrels retain Rustic's original refusal of burning-hot lava.
- In-game tests: milk/whey saved legacy; third-party mod fluid bucket and bottle; direct pipe insertion/extraction, variant component identity, partial volume storage, 15 wooden variants, creative interactions, unload/reload, state transitions.
