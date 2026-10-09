# Italian's Delight Refabricated — Basil update 0.4.0

Mini-update dedicato al basilico e alla sua integrazione con le ricette italiane.

## Obiettivi confermati

- introdurre il **Basil / Basilico** come ingrediente;
- riutilizzare senza modifiche le texture già presenti:
  - `textures/item/herb.png`;
  - `textures/block/herbs_stage0.png` ... `herbs_stage3.png`;
  - `textures/block/wild_herbs.png`;
- rendere il basilico direttamente ripiantabile, senza un item semi separato;
- aggiungere **Wild Basil / Basilico Selvatico** alla generazione naturale;
- integrare il basilico nelle ricette che ne hanno già bisogno;
- mantenere questa release piccola: olive, olio, uva e vino restano fuori dallo scope.

## Meccanica iniziale

### Coltivazione

Il basilico coltivato usa il normale sistema `CropBlock` di Minecraft:

- si pianta su farmland usando direttamente il Basilico;
- mantiene gli 8 age vanilla (0–7), mappati sulle 4 texture disponibili;
- la velocità di crescita resta quindi comparabile a quella del grano;
- a maturazione produce il Basilico necessario sia per cucinare sia per ripiantare;
- la Fortune aumenta la resa come per le colture di Farmer's Delight.

### Basilico selvatico

Il Basilico Selvatico:

- genera in Plains, Sunflower Plains e Meadow;
- usa `wild_herbs.png`;
- fornisce 1–2 Basilico quando raccolto;
- può diffondersi localmente con la bone meal;
- serve come fonte naturale iniziale della coltura.

## Integrazione ricette

Prima integrazione:

- **Mozzarella Salad / Insalata di Mozzarella** richiede ora `#c:crops/basil`;
- la parte "foglia verde" usa `#c:foods/leafy_green` invece di un item fisso, mantenendo compatibilità con Farmer's Delight e altre mod che usano i common tags.

Il tag comune `#c:crops/basil` permette compatibilità futura con altri addon.

## Fuori scope per la 0.4.0

Per ora non vengono implementati:

- Pesto;
- Pasta al Pesto;
- olivi;
- olive;
- olio d'oliva;
- uva e vino.

Pesto e Pasta al Pesto verranno valutati quando sarà disponibile una filiera
coerente dell'olio d'oliva, evitando ricette temporanee.

## Checklist

- [x] bump versione a 0.4.0;
- [x] registrazione Basilico;
- [x] coltura con 4 texture e 8 age vanilla;
- [x] loot della coltura;
- [x] Basilico Selvatico;
- [x] worldgen iniziale;
- [x] tag `#c:crops/basil`;
- [x] aggiornamento Insalata di Mozzarella;
- [x] traduzioni EN/IT;
- [x] creative tab;
- [x] clean build;
- [x] test crescita naturale;
- [x] test bone meal;
- [x] test raccolta e resa;
- [x] test worldgen in nuovi chunk;
- [x] test ricetta Insalata di Mozzarella in JEI.

## Dependency refresh before 0.4.0 merge

Updated on `feature/basil`:

- Farmer's Delight Refabricated: `26.2-3.6.26+refabricated` -> `26.2-3.6.28+refabricated`;
- JEI Fabric: CurseForge file `8937443` -> `9068092` (30.39.0.233);
- Fabric Loom: `1.18-SNAPSHOT` -> `1.18.3` (pinned stable version);
- Gradle wrapper distribution: `9.7.1` -> `9.8.1`.

These changes still need a new verification run because the previous gameplay
tests were completed with the older dependency set.

- [ ] `gradlew.bat clean build` succeeds with the updated dependency versions;
- [ ] `gradlew.bat runClient` starts successfully;
- [ ] JEI categories and recipes still work;
- [ ] Cheese Vat, Hanging Hook and Basil continue to work.
