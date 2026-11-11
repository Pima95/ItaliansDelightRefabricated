# Italian's Delight Refabricated — Orchard Update 1.1.0

**Draft release notes — NOT YET PUBLISHED**

**Internal testing version: 1.1.0-rc.1.** Not a public GitHub or CurseForge release.
Minecraft 26.2 · Fabric · Farmer's Delight Refabricated add-on

## What's new
The Orchard Update adds three cultivable fruit trees with distinct
wood families and natural worldgen:

- **Olive trees**, including a rare monumental **Ancient Olive** with
  an irregular 2×2 trunk, grown from a 2×2 arrangement of Olive saplings;
- **Almond trees** with their own blossoms, almonds and wood;
- **Fruit Cherry trees** with green foliage and cherries, completely
  distinct from Minecraft's ornamental pink Cherry trees.

Trees develop fruit on eligible naturally generated leaves. Blossoms,
unripe fruit, right-click harvesting, regrowth and an approximately
65% rotating selection of productive exposed leaves make orchard
harvesting more gradual and varied.

### Building with three new wood families
Logs, planks and wooden derivatives, signs, hanging signs, doors,
trapdoors, boats and chest boats, cabinets, shelves and cheese-aging
racks add matching building options for each tree.

### Rustic-style processing and liquids
New Crushing Tubs (including matching wooden variants) process
olives, apples, sugar cane, honeycombs and golden apples.
Barrels and cauldrons work with the supported bottles and buckets;
olive oil, apple juice and the other existing compatible liquids
retain their established mechanics. Honey and Golden Apple Juice
buckets store 1,000 mB without allowing world placement.

### World generation
Following in-game feedback, normal Olives and Almonds are uncommon
in Plains and Savannas (each 1/40 attempts per suitable chunk);
Almonds are rarer in Meadows (1/100). Rare Ancient Olive (1/96)
and Fruit Cherry (1/7) retain their approved frequencies. Trees
generate in **new chunks**, not retroactively in existing areas.

### Compatibility
Minecraft **26.2**, Fabric Loader **0.20.1+**, Java **25**, Fabric API
**0.161.0+26.2**, Farmer's Delight Refabricated
**26.2-3.6.29+refabricated**. JEI is supported; Serene Seasons is
**optional** (with GlitchCore when installed).

## New olive foods and pizzas (development additions)

- Cutting Board yields 1 Pitted Olive and 1 Olive Pit from each Olive.
- Olive Pâté with olive oil, salt and a reusable bowl.
- Two open-faced bruschettas: Tomato & Basil, and Olive Pâté.
  The original sandwich-style top bread is deliberately removed.
- Two named pizzas: **Mediterranea** (tomato sauce, mozzarella,
  pitted olives, basil) and **Olive & Smoked Scamorza Bianca**
  (mozzarella, smoked scamorza, pitted olives).
- **Focaccia is explicitly deferred** to a later update.

## Almond preparations (development additions)

- Cutting Board converts each whole Almond into two portions of Granella.
- Pesto from chopped almonds, fresh basil, Parmesan and Olive Oil.
- Pasta with Almond & Basil Pesto cooked in the Farmer's Delight Cooking Pot.
- Almond Cake crafted from six almonds, two milk drinks and Farmer's Delight Pie Crust, cut into four edible slices; four slices can be crafted back into a whole cake.
- Recognized white pizza Mortadella, Ricotta and Almonds, without tomato sauce
  and with **chopped almonds**, not whole almonds.
- New 16×16 binary-alpha icons, plus a 12×12 granella topping atlas.
  The original pasta-with-pesto artwork and the approved pizza crust stay intact.
- Focaccia, grapes and grape juice remain excluded from 1.1.0.

## Cherry preserves and tart (development additions)

- Cherry jam remains served from the Farmer's Delight Cooking Pot with a glass bottle. Its item uses a squat **jam jar** sprite, but the existing stable `cherry_jam_bottle` registry ID is preserved to protect saved inventories.
- Cherry Tart: six cherries, two jam jars and Farmer's Delight Pie Crust, matching the cheesecake's two-row filling + central crust composition.
- Cutting Board divides a full Cherry Tart into four slices; a 2×2 grid of four slices reconstructs a whole placeable tart.
- The placed tart's top is a diagonal shortcrust lattice over dark cherry filling; outer side is solid baked pastry, without visible filling or layers.
- Tomato Slice icon was redrawn smaller, with a 10×10 maximum occupied pixel footprint.

## Pizzas, compatibility and candidate QA
Four cheeseless slices now have pointed triangular crusts with no mozzarella
drips. Collected slices can be placed on supports or into another pizza while
retaining their ingredients, and a served pizza never returns as a whole item.

The author approved old-world 1.0.0 compatibility and optimized gameplay.
On 11 November 2026 the author also reported that the current
`1.1.0-rc.1` JAR works in-game. The development CI for commit
`b3a8843cf401` succeeded, checking the packaged version, Java 25
bytecode and license notices. The headless Fabric dedicated-server check
reached readiness twice using the same saved test world after restart.
This does not verify real two-player multiplayer, block/entity persistence
or migration from a 1.0.0 server world. Runtime testing with optional
Serene Seasons and GlitchCore is still unperformed.

### License and attribution
Starting with this planned 1.1.0 release, the author's original contributions
use **Apache License 2.0** with an attribution NOTICE. Third-party material
retains its applicable copyright and MIT notices. Publicly available upstream
team credits have been preserved, but the authorship of some individual legacy
textures is not documented in the original projects; no per-file authorship
claim or blanket legal clearance is implied. Previously published version
1.0.0 remains under its original MIT terms.

## Not included
**Grapes, grape juice and wine** are planned for a later version.

## Before release
This draft is descriptive; follow
[RELEASE_READINESS.md](RELEASE_READINESS.md) for remaining
client, server, saved-world and packaging checks. Do not assign
a publication date, create a tag or upload a JAR until those checks
and final approval are complete.
