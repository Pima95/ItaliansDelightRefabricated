# Italian's Delight Refabricated

<p align="center">
  <img src="src/main/resources/assets/italiansdelight/textures/italians_delight_refabricated.png" alt="Italian's Delight Refabricated" width="50%">
</p>

## Overview

**Italian's Delight Refabricated** is a **Farmer's Delight Refabricated** add-on for Fabric that expands Minecraft cooking with ingredients, dishes, and production systems inspired by Italian cuisine.

Rather than adding isolated food items, the mod is designed around Farmer's Delight mechanics such as the **Cooking Pot**, **Cutting Board**, heat sources, containers, and shared ingredient tags.

Current features include:

- the **Cheese Vat**, with heat-based processing, container handling, whey storage, automation, and JEI support;
- an expanded dairy chain with **rennet, curd, mozzarella, ricotta, mascarpone, burrata, bocconcini, Parmigiano Reggiano, Pecorino Romano, Gorgonzola, Provolone, and Scamorza**;
- **cheese aging and drying** through dedicated racks, hanging hooks, and placeable aging cheese;
- **sheep milk**, cream, whey, blue mold culture, apple cider vinegar, and other dairy ingredients;
- a **cheese grater** and Farmer's Delight Cutting Board integration for sliced and grated cheeses;
- **cardoon** generation and cultivation as a source used in the dairy chain;
- **basil** cultivation and naturally generated Wild Basil, with common-tag compatibility for recipes;
- **salt production** by naturally evaporating water in vanilla cauldrons, with a dedicated JEI evaporation recipe;
- a complete **cured-meat chain** with Prosciutto Crudo, Salame, Mortadella, Pancetta, Guanciale, Bresaola, Coppa, and Speck;
- hanging aging/drying for cheeses and cured meats through the shared **Hanging Hook**;
- Italian dishes including **Carbonara, Gricia, Amatriciana, Speck and Gorgonzola pasta/risotto, Parmigiano risotto, Mushroom risotto, Mozzarella Salad**, and the existing tomato-sauce dishes;
- **Nourishment** support for complete meals, balanced against Farmer's Delight Refabricated;
- integration with **JEI** and Farmer's Delight systems, including Cheese Vat, aging/drying, and salt evaporation.

**Pizza Update 1.0.0** is now published. It introduces interactive dough stretching and topping placement, 18 recognized pizza flavors, Mixed Pizza, a reusable Pizza Peel, the Pizza Oven, vanilla furnace/smoker support, individual ingredient-bearing slices and searchable JEI guides. See the [v1.0.0 release](https://github.com/Pima95/ItaliansDelightRefabricated/releases/tag/v1.0.0).

**In development: Orchard Update 1.1.0** — on the
[`feature/orchards-1.1.0` branch](https://github.com/Pima95/ItaliansDelightRefabricated/tree/feature/orchards-1.1.0).
It adds three complete orchard species with their own wood families:
**Olive** (including the rare monumental 2×2 Ancient Olive), **Almond**,
and a green-leaved **Fruit Cherry** distinct from vanilla pink Cherry.
Each has its own sapling, original tree shapes, foliage stages, fruit
harvesting and wood-derived furniture (signs, doors, shelves, cabinets,
boats and aging racks). Natural generation in the chosen biomes is enabled
and its lower density has been accepted in-game: normal Olives and Almonds
each attempt 1/40 eligible Plains/Savanna chunks, Almonds attempt 1/100
Meadow chunks; rare Ancient Olive 1/96 and Fruit Cherry 1/7 are unchanged.

The update also provides Orchard fruit crates; Rustic-style
**Crushing Tubs in all 15 wood variants**; **Barrels and cauldrons** with
liquid handling, bottles and buckets; recipes for olives, apples, sugar
cane, honeycombs and golden apples; JEI guidance; and optional
**Serene Seasons** compatibility. Natural leaves use a rotating
approximately **65% fruiting eligibility** rather than always ripening.
**Grapes, grape juice and wine are reserved for a later version**, even
though some unused texture resources are already present in the tree.

The public release is still **1.0.0**. The Orchard development branch
now compiles an internal **1.1.0-rc.1** release candidate; it has not
been tagged, merged to `main` or published. Do not mistake the candidate
for a public release. See the
[Orchard readiness checklist](docs/orchards-1.1.0/RELEASE_READINESS.md),
[draft release notes](docs/orchards-1.1.0/RELEASE_NOTES_DRAFT.md)
and [technical roadmap](docs/orchards-1.1.0/ROADMAP.md).
These development features are **not part of the published 1.0.0 JAR**.

## Requirements

The current **published version is 1.0.0**, targeting **Minecraft 26.2**. It requires:

- **Java 25**
- **Fabric Loader 0.20.1 or newer**
- **Fabric API 0.161.0+26.2**
- **Farmer's Delight Refabricated 26.2-3.6.29+refabricated**

The development toolchain uses **Fabric Loom 1.18.3** and **Gradle 9.8.1**.

JEI is supported for recipe viewing, including Cheese Vat and cheese-aging recipes.

## Development

Clone the repository and build the mod with:

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew build
```

The generated JAR can be found in:

```text
build/libs/
```

For detailed technical requirements, design decisions, and the development roadmap:

- [Current 1.0.0 implementation, tests and remaining audits](docs/pizza-1.0.0/CURRENT_STATUS.md)
- [Requirements and Roadmap](docs/REQUIREMENTS.md)
- [Requisiti e Roadmap — Italian version](docs/REQUISITI.md)

## Contributing

Feedback, bug reports, and pull requests are welcome.

If you find an issue or have an idea for improving the mod, feel free to open an issue or submit a pull request.

## Credits

Italian's Delight Refabricated is built as an add-on for [Farmer's Delight Refabricated](https://github.com/MehVahdJukaar/FarmersDelightRefabricated).

## License and asset attribution

See the [LICENSE](LICENSE) file for the license of this project and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for attributions of assets and inspirations from **PuurpZ's Italian's Delight** and **cadaverous_queen's Rustic**, including links to the original MIT notices.
