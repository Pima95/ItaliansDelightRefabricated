# Italian's Delight — Requirements and Roadmap

Add-on for **Farmer's Delight Refabricated** on Fabric, focused on Italian
cuisine. This document describes both the current technical requirements and
the actual status of implemented features and the future roadmap.

## 1. Technical target

| Component | Current version | Notes |
|---|---|---|
| Minecraft | 26.2 | project target version |
| Fabric Loader | 0.19.5 | configured in `gradle.properties` |
| Fabric API | 0.161.0+26.2 | configured in `gradle.properties` |
| Farmer's Delight Refabricated | 26.2-3.6.26+refabricated | required dependency via Cassian's Maven |
| Java | 25 | Gradle release target |
| Fabric Loom | 1.17-SNAPSHOT | plugin configured in `build.gradle` |
| JEI | Curse Maven file 8937443 | Cheese Vat integration is implemented |

Before updating any dependency, verify that the new release is compatible with
Minecraft 26.2 and with the other dependencies used by the mod.

## 2. Design principles

1. **Real Farmer's Delight integration.** Whenever possible, food should use
   existing systems such as the Cutting Board, Cooking Pot, Stove, shared tags,
   and containers instead of being simple crafting-table recipes.

2. **Coherent production chains.** Intermediate ingredients should have a real
   purpose in the preparation of final products. The Cheese Vat is the first
   concrete example of this approach.

3. **Original functional blocks.** The mod may introduce custom machines when
   Farmer's Delight does not already provide an equivalent mechanic. The Cheese
   Vat is currently the first custom functional block; the wood-fired oven
   remains planned for a later phase.

4. **Clean namespace and data structure.** All proprietary content uses the
   `italiansdelight` namespace. Shared tags such as `c:foods`, `c:crops`,
   `c:dusts`, and `c:tools` are preferred for interoperability.

5. **Compatibility through tags.** Ingredients compatible with other mods
   should prefer shared tags over direct item references whenever that does not
   change the recipe's intended meaning.

## 3. Currently implemented features

### 3.1 Cheese Vat

The **Cheese Vat** is a complete functional block with:

- a BlockEntity and persistent inventory;
- three ingredient slots;
- a container slot;
- a real output slot separated from the cooked-product preview;
- support for custom shapeless recipes;
- a Farmer's Delight-compatible heat source requirement;
- client/server synchronized processing progress;
- a dedicated GUI;
- shift-click support;
- crafting remainder handling;
- recipe serialization through codecs;
- JEI integration.

Cheese Vat recipes currently available:

- milk + rennet → **curd**, requires a bowl;
- allium → **rennet**, requires a glass bottle;
- curd + salt → **mozzarella**.

### 3.2 Food items and Farmer's Delight processing

Currently registered items:

- pasta with tomato sauce;
- risotto with tomato sauce;
- mozzarella;
- mozzarella slice;
- tomato slice;
- rennet;
- curd;
- salt.

Farmer's Delight recipes already present:

- pasta + tomato sauce → pasta with tomato sauce through the Cooking Pot;
- rice + tomato sauce → risotto with tomato sauce through the Cooking Pot;
- mozzarella → 4 slices through the Cutting Board;
- tomato → 4 slices through the Cutting Board.

### 3.3 Salt production

A normal vanilla cauldron filled with water can produce salt through natural
evaporation.

Current rules:

- **12,000 useful ticks** are required;
- progress advances only during daytime;
- rain and thunderstorms pause the process;
- any non-air block above the cauldron temporarily blocks evaporation,
  including glass, leaves, and open trapdoors;
- unloaded chunks do not progress and are never force-loaded;
- removing or lowering the water level, breaking the cauldron, or replacing it
  resets progress;
- progress is persisted per dimension through `SavedData`;
- when evaporation completes, the water disappears and a 2-pixel-high salt
  layer is displayed at the bottom;
- the residue uses a dedicated block texture;
- right-clicking the residue yields a random **3 to 7 salt** and leaves an
  empty cauldron;
- Creative Pick Block can copy and directly re-place the cauldron with salt
  residue;
- breaking the final block returns the normal cauldron, not salt.

## 4. Project structure

Shared gameplay logic is stored in `common`, client-only code in `client`,
external integrations in `integration`, and mixins in the `mixin` package.

```text
src/main/
├── java/dev/italiansdelight/
│   ├── ItaliansDelight.java
│   ├── ItaliansDelightClient.java
│   ├── client/
│   │   └── gui/CheeseVatScreen.java
│   ├── common/
│   │   ├── block/
│   │   │   ├── CheeseVatBlock.java
│   │   │   ├── SaltCauldronBlock.java
│   │   │   └── entity/
│   │   │       ├── CheeseVatBlockEntity.java
│   │   │       └── container/CheeseVatMenu.java
│   │   ├── crafting/
│   │   │   ├── CheeseVatRecipe.java
│   │   │   └── CheeseVatRecipeInput.java
│   │   ├── item/group/ModItemGroups.java
│   │   ├── registry/Mod*.java
│   │   └── salt/
│   │       ├── SaltCauldronManager.java
│   │       └── SaltCauldronProgressData.java
│   ├── integration/
│   │   └── jei/
│   │       ├── ItaliansDelightJeiPlugin.java
│   │       └── CheeseVat*.java
│   └── mixin/
│       └── LevelMixin.java
└── resources/
    ├── fabric.mod.json
    ├── italiansdelight.mixins.json
    ├── assets/italiansdelight/
    │   ├── blockstates/
    │   ├── items/
    │   ├── lang/
    │   ├── models/
    │   └── textures/
    └── data/
        ├── c/tags/item/
        └── italiansdelight/
            ├── advancement/
            ├── loot_table/
            └── recipe/
```

Cheese Vat recipes are stored in
`data/italiansdelight/recipe/cheese_vat/`.

Recipes that use Farmer's Delight systems are organized under
`recipe/cooking/` and `recipe/cutting/`.

## 5. Roadmap status

- [x] **Step 0 — Project foundation:** repository, Gradle, Fabric, entrypoints,
      main registries, and resource structure.
- [x] **Step 1 — First functional machine:** Cheese Vat with GUI, custom
      recipes, persistence, and JEI integration.
- [x] **Step 2 — First dairy production chain:** rennet → curd → mozzarella,
      including mozzarella processing on the Cutting Board.
- [x] **Step 3 — Salt production system:** cauldron evaporation, persistent
      progress, visible residue, and collection.
- [ ] **Step 4 — Complete tomato production chain:** tomato slices and recipes
      reusing Farmer's Delight tomato sauce already exist; a full custom
      passata/preserve/sauce chain is still missing if this design direction is
      retained.
- [ ] **Step 5 — Complete pasta production chain:** define dough production,
      fresh/dried pasta, and new dishes such as cacio e pepe, carbonara,
      amatriciana, and pesto.
- [ ] **Step 6 — Advanced cheeses:** parmesan, pecorino, and other products,
      with a possible aging mechanic.
- [ ] **Step 7 — Wood-fired oven and pizza:** dedicated functional block,
      toppings, and cooking system.
- [ ] **Step 8 — Additional crops and ingredients:** decide which prepared
      textures will become actual content and which crops need implementation.
- [ ] **Step 9 — Progression:** dedicated advancement tree, additional recipes,
      missing loot/tags, and possible data generation.
- [ ] **Step 10 — Release:** full testing, balancing, packaging, and publishing
      for Modrinth/CurseForge.

## 6. Rules for future implementations

Before adding new content:

1. verify whether Farmer's Delight already provides the required mechanic;
2. prefer shared tags over hard dependencies on individual items;
3. add the required registration, model, texture, translations, and recipes;
4. verify behavior on both server and client;
5. add comments to code sections that are not immediately obvious;
6. update this document whenever a feature changes roadmap status;
7. run at least `gradlew build` and an in-game test before merging into
   `main`.

## 7. Open decisions

The following still need to be defined during development:

1. which new Italian crops should actually be implemented compared with the
   textures already prepared;
2. whether to introduce a full tomato production chain distinct from Farmer's
   Delight tomato sauce;
3. which pasta types and dishes will receive a complete production chain;
4. the exact behavior of cheese aging;
5. the design and gameplay of the wood-fired oven;
6. a possible beverage system including wine and coffee.
