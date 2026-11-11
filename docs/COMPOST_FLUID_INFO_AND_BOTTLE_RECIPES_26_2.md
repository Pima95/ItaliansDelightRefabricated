# Composting, native JEI Info, bottle crafting — Minecraft 26.2

## Vanilla Composter
Uses Fabric's CompostableRegistry.INSTANCE.add, not a mixin or any vanilla block overwrite. 19 natural items are now compostable:
- 30%: olive/almond/fruit-cherry saplings and leaves, apple seeds.
- 65%: olives, almonds, cherries, wild/cultivated basil and wild/cultivated cardoon.
- 85%: five crates packed with olives/almonds/cherries/basil/cardoon.
- Not included: logs, processed boards, metal, meat, cheese, flour, bottles and buckets. Vanilla similarly does not compost logs or manufactured containers.
- Register after ModItems and ModBlocks initializers.

## JEI
Uses `IRecipeRegistration.addItemStackInfo`, identical to the existing OrchardPlantJeiInfo helper, for four fluids: olive oil (Crushing Tub), apple juice (Crushing Tub), sheep milk (milking adult sheep), and whey (Cheese Vat byproduct). Both available containers show the same info. Messages exist in Italian and English.

## Recipes
- `olive_oil_bottle`: oil bucket + 4 empty glass bottles -> 4 oil bottles.
- `apple_juice_bottle`: apple juice bucket + 4 empty glass bottles -> 4 apple juice bottles.
- `whey_bottle`: whey bucket + 4 empty glass bottles -> 4 whey bottles.
- `sheep_milk_bottle`: pre-existing recipe, unchanged.
- Each bucket's existing craftRemainder returns the empty bucket; no item duplication by handcrafting.
- Farmer's Delight's cow milk bottle crafting remains owned by Farmer's Delight.
