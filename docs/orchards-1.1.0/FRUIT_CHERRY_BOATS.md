# Fruit Cherry boats — Orchard Update 1.1.0

**Branch:** `feature/orchards-1.1.0`. **Status:** implemented as a development change, pending automated CI and in-game acceptance.

## Functional variants

| Vehicle | Registry ID | Art | Crafting |
| --- | --- | --- | --- |
| Fruit Cherry Boat | `italiansdelight:fruit_cherry_boat` | `textures/entity/boat/fruit_cherry.png` (128×64), `textures/item/fruit_cherry_boat.png` (16×16) | 5 Fruit Cherry planks in the standard boat shape |
| Fruit Cherry Chest Boat | `italiansdelight:fruit_cherry_chest_boat` | `textures/entity/chest_boat/fruit_cherry.png` (128×128), `textures/item/fruit_cherry_chest_boat.png` (16×16) | Fruit Cherry boat + vanilla chest |

- Both vehicles register a real vanilla-equivalent `Boat`/`ChestBoat` entity with a `BoatItem`, client model layer and renderer, and appropriate Creative inventory placement.
- They use existing Fabric/Minecraft 26.2 classes and the same working setup as Olive and Almond boats. They retain the standard vanilla hull and chest silhouette, passenger seating, physics, chest inventory and item drops.
- New texture colors follow the approved copper/reddish Fruit Cherry planks: dark bark/red-brown edges, warm cherry body and clear board highlights. The *boat geometry, vanilla texture UV layout, shadows and chest hardware* derive from the already implemented Almond boat sprites and are not redrawn; only the wood-color entries of the indexed palette were replaced. This is deliberate vanilla-style consistency, not an alteration of the original Almond textures.
- Item texture assets stay in the matching 16×16 pixel-art format, without scaling or antialiasing.
- Client and common code are separate, so boat renderer classes are never loaded from the common dedicated-server entry point.
- Both item and entity tags, recipes, Italian and English localization, and the project Creative tab include these vehicles.

## Regression tests

The workflow executes `.github/scripts/check_fruit_cherry_boats.py`. This checks PNG integrity and palette, dimensions, exact inherited vanilla-style item silhouettes and boat UVs, Java registration and renderers, item models, recipes, tags, translations, Creative group and the **unchanged SHA fingerprints of all eight already approved Fruit Cherry sign textures**.

Neither `main` nor the public release `v1.0.0` is altered. All existing Olive/Almond graphics, all Fruit Cherry foundational art, and previous door/trapdoor/sign art remain unmodified.

## In-game QA to complete

- [ ] Place Fruit Cherry Boat on water; verify controls, steering, collision, passengers, pick-up and breaking drops.
- [ ] Place Fruit Cherry Chest Boat; inspect openable 27-slot chest, hopper/container interactions where vanilla-supported, contents persistence, breaking drops, reloading world.
- [ ] Check vanilla side, front, back, underside and paddle UVs; item icons at the standard 16×16 scale.
- [ ] Compare the warm copper-pink cherry plank grain with approved doors/planks, in sunlight and shade. Submit palette adjustments only if requested after preview.
- [ ] Test recipes and JEI; confirm two Creative entries, not duplicated, with names in Italian and English.
- [ ] Run dedicated-server save/load and multiplayer passenger/inventory tests.

**Important:** compilation and static checks are not a substitute for in-game aesthetic or gameplay acceptance.
