# Italian's Delight — NeoForge 1.21.1

<p align="center">
  <img src="src/main/resources/assets/italiansdelight/textures/item/pizza_margherita.png" alt="Margherita pizza" width="96" style="image-rendering: pixelated">
</p>

Italian's Delight is an independent add-on for [Farmer's Delight](https://github.com/vectorwing/FarmersDelight), bringing Italian cuisine, agriculture and food processing to Minecraft. This branch is the **native NeoForge port for Minecraft 1.21.1** of Italian's Delight Refabricated. No Fabric API, Fabric Loader or compatibility layer is required.

**Status:** release preparation; not yet published. Proposed version: `1.2.0-mc1.21.1-neoforge`. The final version identifier and publication require author approval. See [release readiness](docs/NEOFORGE_1.21.1_RELEASE.md), [changelog](CHANGELOG.md) and [technical porting log](docs/NEOFORGE_1.21.1_PORTING.md).

## Versioni e installazione

| Requirement | Tested configuration |
| --- | --- |
| Minecraft | 1.21.1 |
| Java | 21 |
| NeoForge | 21.1.267 (metadata range 21.1.219 to before 21.2) |
| Farmer's Delight | Original NeoForge 1.3.4 (required; metadata range 1.3.4 to before 1.4) |
| JEI | 19.51.0.418 (optional) |
| Serene Seasons / GlitchCore | 10.1.0.3 / 2.1.0.2 (optional) |

1. Create a Minecraft 1.21.1 NeoForge installation with Java 21.
2. Install the original [Farmer's Delight](https://www.curseforge.com/minecraft/mc-mods/farmers-delight) for NeoForge and the matching Italian's Delight JAR in `mods/`.
3. Install optional mods separately if desired. On a dedicated server, use matching mod/loader versions on both sides, except client-only additions as documented by their authors.

Use the playable JAR, not the sources JAR. Existing Fabric downloads are separate builds: consult [the Fabric branch](https://github.com/Pima95/ItaliansDelightRefabricated/tree/main) and the [published releases](https://github.com/Pima95/ItaliansDelightRefabricated/releases).

**World compatibility:** make backups. Downgrading worlds made on Minecraft 26.x to 1.21.1 is not supported. Start a new 1.21.1 world.

## Features

- Interactive pizzas: independent quarters, toppings, pizza peel, oven, slicing and recombination.
- Cheese Vat, dairy products, cheese aging racks, hooks, cured meats and Italian dishes.
- Olive, almond and fruit cherry trees, wood families, fruiting cycles and juice extraction.
- Basil, cardoon, wild and cultivated grapevines, white/red grapes, stakes, ropes and chili peppers.
- Crushing tubs, barrels, cauldrons, liquid containers, brewing barrels, wines, mead, cider, juices and vinegars.
- Integration with Farmer's Delight Cooking Pot and Cutting Board; recipe viewing with optional JEI.

Minecraft 1.21.1 lacks pale oak, poplar and wooden shelves from the source snapshot. Only the corresponding wood variants and shelves have been omitted. This is not the separate 2.0.0 development update.

## Optional integrations and validation

JEI and the corrected plant rendering were checked in-game by the author. Automated tests with Serene Seasons cover the orchard calendar, greenhouse behavior, crop tags and foliage states. Thirst Was Taken 2 and Tough As Nails have API/tag tests; original Thirst Was Taken has startup/presence coverage only. Actual drinking behavior for all optional thirst mods has not yet been comprehensively tested.

Create is deferred by author decision. Prior endpoint and tank transfer tests passed, but pump networks and full automation are not certified. Dedicated GameTest server startup has passed; a real multi-player session has not been tested. See the [release readiness report](docs/NEOFORGE_1.21.1_RELEASE.md) for precise limitations.

## Development

Use Java 21 and the included Gradle wrapper. Linux/macOS: `./gradlew build`; Windows: `.\gradlew.bat build`. Output is in `build/libs/` on Linux/macOS or `build-neoforge/libs/` on Windows. The separate `run-neoforge/` directory keeps test worlds away from Fabric installations.

Optional dev profiles: `-Pwith_jei=true`, `-Pwith_serene_seasons=true`, `-PportThirst=twt|twt2|tan` (one at a time). See [CI](https://github.com/Pima95/ItaliansDelightRefabricated/actions/workflows/neoforge-1.21.1.yml) and the [documentation index](docs/README.md).

## Credits and licensing

Original contributions are licensed under [Apache-2.0](LICENSE); existing third-party material retains its applicable terms. See [NOTICE](NOTICE), [CREDIT](CREDIT.md), [third-party notices](THIRD_PARTY_NOTICES.md) and [Farmer's Delight crate license](docs/FDR_CRATES_LICENSE.md). The original Italian's Delight project and Rustic/Alcocraft are separately credited. This port is not an official continuation of those projects. Some legacy artwork attribution remains unresolved; a successful JAR packaging check is not an asset-by-asset rights clearance.

[Issues and feedback](https://github.com/Pima95/ItaliansDelightRefabricated/issues).
