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

**Pizza Update 1.0.0 is in active development** on the [`feature/pizza-1.0.0` branch](https://github.com/Pima95/ItaliansDelightRefabricated/tree/feature/pizza-1.0.0). It is **not part of the published 0.4.0 release**. The in-development update adds interactive pizza preparation on placed dough, a reusable pizza peel, the Pizza Oven, vanilla furnace/smoker support, data-bearing topping combinations, independent pizza slices, JEI guides, and Creative examples. Its release is gated on the remaining multiplayer, automation, persistence and regression tests. See the [Pizza 1.0.0 release readiness checklist](docs/pizza-1.0.0/RELEASE_READINESS.md) for details.

Additional pasta dishes, olive trees, grapes/wine and seasonal content remain future plans.

## Requirements

The latest documented **published version is 0.4.0**, targeting **Minecraft 26.2**, and requires:

- **Java 25**
- **Fabric Loader 0.19.5 or newer**
- **Fabric API 0.161.0+26.2**
- **Farmer's Delight Refabricated 26.2-3.6.28+refabricated**

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

## License

See the [LICENSE](LICENSE) file for license information.
