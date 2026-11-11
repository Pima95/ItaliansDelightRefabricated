#!/usr/bin/env python3
"""Static safeguards for re-placeable cooked slices (not a runtime game test)."""
from pathlib import Path

BASE = Path("src/main/java/dev/italiansdelight")
def source(relative):
    return (BASE / relative).read_text(encoding="utf-8")

place = source("common/pizza/PizzaSlicePlacement.java")
route = source("common/pizza/PizzaDoughPlacement.java")
cooked = source("common/block/CookedPizzaBlock.java")
entity = source("common/block/entity/CookedPizzaBlockEntity.java")
factory = source("common/pizza/PizzaSliceFactory.java")
peel = source("common/pizza/PizzaPeelInteractions.java")
renderer = source("client/render/CookedPizzaRenderer.java")
visuals = source("client/render/PizzaToppingVisuals.java")
data = source("common/pizza/PizzaSliceData.java")

for snippet in (
    "if (held.is(dev.italiansdelight.common.registry.ModItems.PIZZA_SLICE))",
    "return PizzaSlicePlacement.use(player, level, hit, held);",
):
    assert snippet in route, "Slice placement not connected to block interactions"

# Both entry paths are strictly server authoritative and do not consume
# items when a position is full, a chunk is obstructed or a server operation fails.
for snippet in (
    "boolean hitPizza = clickedState.is(ModBlocks.COOKED_PIZZA);",
    "BlockPos target = hitPizza ? clicked : clicked.above();",
    "current.is(ModBlocks.COOKED_PIZZA)",
    "remaining == PizzaQuarterSelection.ALL_QUARTERS",
    "findOpenQuarter(preferred, remaining)",
    "player.mayUseItemAt(target, Direction.UP, held)",
    "if (level.isClientSide())",
    "server.setBlock(target,",
    "entity.insertSlice(quarter, slice);",
    "if (!entity.canInsertSlice(quarter))",
    "level.isUnobstructed(placed, target, CollisionContext.empty())",
    "placed.canSurvive(level, target)",
    "held.shrink(1);",
):
    assert snippet in place, f"Unsafe or incomplete pizza slice placement: {snippet}"
assert place.count("entity.insertSlice(quarter, slice);") == 2
assert place.count("consumeAndSound(player, server, target, held);") == 2

# A served pizza is never whole again, even when all four slots are full.
for snippet in (
    "private boolean servedEver;",
    'input.getBooleanOr("ServedEver", false)',
    'output.putBoolean("ServedEver", servedEver);',
    'input.read("InsertedSlices", PizzaSliceData.CODEC.listOf())',
    'output.store("InsertedSlices", PizzaSliceData.CODEC.listOf(), insertedSlices);',
    "new PizzaSliceData(quarter, source.toppings(), source.legacyMozzarella())",
    "public PizzaSliceData sliceAt(int quarter)",
    "public void insertSlice(int quarter, PizzaSliceData source)",
    "public boolean canInsertSlice(int quarter)",
    "public void markServed(int quarter)",
    "if (inserted.quarter() != quarter)",
):
    assert snippet in entity, f"Missing persistent source-slice provenance: {snippet}"
assert "if (mask == PizzaQuarterSelection.ALL_QUARTERS && !entity.servedEver())" in cooked
assert "PizzaSliceFactory.create(entity.sliceAt(q))" in cooked
assert "PizzaSliceFactory.create(entity.sliceAt(q));" in cooked
assert "entity.markServed(q);" in cooked
assert "!served.servedEver()" in peel
assert "public static final Codec<PizzaSliceData> CODEC" in data
assert "return create(PizzaSliceData.fromPizza(pizza, quarter));" in factory
assert "public static ItemStack create(PizzaSliceData slice)" in factory

# The renderer must show the newly inserted slice's TOPPINGS, not the
# previous occupant's original canonical ingredients.
assert "var composition = entity.renderComposition();" in renderer
assert "int legacyMask = entity.renderLegacyMask();" in renderer
assert "PizzaToppingVisuals.extract(composition, legacyMask, remaining)" in renderer
assert "cachedRenderComposition = null;" in entity
assert "cachedRemaining == remaining" in entity
assert "facing.equals(cachedFacing)" in entity
assert "public PizzaComposition renderComposition()" in entity
assert "sliceAt(quarter).toppings()" in entity
assert "topping.originalStack()" in entity
assert "legacyMozzarellaMask & mask" in visuals

print("OK: placement on empty support or pizza, exact saved per-slot topping data")
print("OK: server-only placement, single-stack consumption and empty-slot guard")
print("OK: permanent whole-pizza recovery ban after first serving")
print("OK: moved slices retain flavor/nutrition/legacy and correct world rendering")
print("NOTE: test multiplayer, chunk reload and full inventories in actual Minecraft")
