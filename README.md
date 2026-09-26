# Italian's Delight

**Italian's Delight** is an add-on for **Farmer's Delight Refabricated** on
**Fabric / Minecraft 26.2**, focused on Italian cuisine.

The goal of the project is not to add isolated food items, but to build
production chains that integrate with Farmer's Delight systems whenever
possible, reusing the Cooking Pot, Cutting Board, heat sources, containers, and
shared tags.

## Project status

The mod is currently in development, but several gameplay features are already
implemented and usable in-game.

### Cheese Vat

The **Cheese Vat** is a custom machine dedicated to producing cheeses and dairy
ingredients.

It currently supports:

- persistent inventory;
- 3 ingredient slots;
- a container slot;
- a real output separated from the cooked-product preview;
- custom shapeless recipes;
- a heat-source requirement;
- a dedicated GUI with a progress indicator;
- shift-click;
- crafting remainders;
- client/server synchronization;
- JEI integration.

The first available processes are:

- milk + rennet → **curd**;
- allium → **rennet**;
- curd + salt → **mozzarella**.

### Farmer's Delight integration

Several recipes already use Farmer's Delight systems directly, including:

- pasta + tomato sauce → **pasta with tomato sauce** through the Cooking Pot;
- rice + tomato sauce → **risotto with tomato sauce** through the Cooking Pot;
- mozzarella → **4 mozzarella slices** through the Cutting Board;
- tomato → **4 tomato slices** through the Cutting Board.

### Salt production

Salt can be produced by evaporating water in a normal vanilla cauldron.

A completely full cauldron requires **12,000 useful ticks** of evaporation.

The process:

- advances only during daytime;
- pauses during rain or thunderstorms;
- pauses if any block exists above the cauldron;
- does not advance while the chunk is unloaded;
- resets if the water is removed or lowered, or if the cauldron is broken or
  replaced;
- keeps its progress across world restarts.

When evaporation finishes, a visible salt layer appears at the bottom of the
cauldron. Interacting with it yields a random **3 to 7 units of salt** and
restores an empty cauldron.

In Creative mode, Pick Block can also copy the cauldron with salt residue and
place it again while preserving that state.

## Development requirements

| Component | Version |
|---|---|
| Minecraft | 26.2 |
| Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.161.0+26.2 |
| Fabric Loom | 1.17-SNAPSHOT |
| Farmer's Delight Refabricated | 26.2-3.6.26+refabricated |
| JEI | Curse Maven file 8937443 |

Farmer's Delight Refabricated is downloaded automatically through
**Cassian's Maven** during Gradle configuration.

## Build

On Windows:

```powershell
.\gradlew build
```

On Linux/macOS:

```bash
./gradlew build
```

The resulting JAR is generated in:

```text
build/libs/
```

To start the development client:

### Windows

```powershell
.\gradlew runClient
```

### Linux/macOS

```bash
./gradlew runClient
```

## Code structure

The project separates responsibilities across dedicated packages:

- `common/` — blocks, BlockEntities, recipes, registries, and shared logic;
- `client/` — GUI and client-only code;
- `integration/jei/` — JEI integration;
- `mixin/` — hooks into vanilla mechanics;
- `resources/assets/` — models, textures, blockstates, and translations;
- `resources/data/` — recipes, tags, loot tables, and advancements.

The source code includes comments especially around non-obvious control flow, so
future maintenance and feature development are easier to follow.

## Roadmap and documentation

The full roadmap, design decisions, detailed project structure, and feature
status are documented here:

- [**Requirements and Roadmap — English**](docs/REQUIREMENTS.md)
- [**Requirements and Roadmap — Italian version**](docs/REQUISITI.md)
- [**Cheese Roadmap — Italian**](docs/ROADMAP_FORMAGGI.md) — scope, implementation
  phases, and completion criteria for `feature/cheeses`.

Planned features include:

- a complete pasta production chain;
- a complete dairy production chain with ricotta, parmesan, pecorino, and aging;
- new crops and ingredients;
- a wood-fired oven;
- pizza;
- dedicated advancements;
- broader compatibility with the Farmer's Delight ecosystem.

## License

This project includes a [LICENSE](LICENSE) file in the repository root.
