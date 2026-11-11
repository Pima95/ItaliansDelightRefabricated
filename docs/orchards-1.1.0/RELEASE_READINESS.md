# Orchard Update 1.1.0 — Release-readiness checklist

**Branch:** `feature/orchards-1.1.0`
**Minecraft:** 26.2 · **Java:** 25 · **Fabric Loader:** 0.20.1+
**Fabric API:** 0.161.0+26.2 · **Farmer's Delight Refabricated:** 26.2-3.6.29+refabricated.

**Status, 9 November 2026: code and balancing ready for a release-candidate
review; no 1.1.0 tag or public publication yet.**
The public version remains 1.0.0, and `gradle.properties` intentionally
still says `mod_version=1.0.0`. Do not mistake an experimental 1.1.0
branch JAR named `italiansdelight-1.0.0.jar` for the published 1.0.0
artifact. Use its Git SHA to distinguish the build.

## Confirmed outcomes
- [x] The author tested natural Orchard worldgen and approved the final
  lower frequencies for Olive and Almond in Plains/Savanna/Meadow
  (9 November 2026).
- [x] Ancient Olive and Fruit Cherry worldgen frequencies were kept
  unchanged at the author's request.
- [x] The author previously confirmed the core Crushing Tub/Barrel/
  cauldron, tree and JEI implementation works in their in-game tests.
  This is not a blanket confirmation of every stress case below.
- [x] The mod-list icon points to the supplied resource
  `assets/italiansdelight/textures/icon.png`; the old empty copied
  resource was removed.
- [x] The branch's CI validates Java compilation and existing static
  orchard, fluid, recipe and visual-asset invariants.
- [x] The grapes/wine roadmap was explicitly deferred to a later version.

## Remaining release-gate tests (not assumed passed)
- [ ] **Client, final candidate JAR:** test from a clean Minecraft
  26.2 installation, ensure only one `italiansdelight` JAR is present,
  confirm game start, Fabric mod-list icon and JEI categories.
- [ ] **Fresh world:** confirm all four trees in new chunks; test rare
  Ancient Olive 2×2, correct separation from vanilla pink Cherry,
  terrain clearance and save/reload. The user has approved density,
  but systematic survival and persistence tests remain separate.
- [ ] **Existing world:** back up a 1.0.0 world before opening it,
  retain all Pizza Update blocks/inventories/data, migrate liquid
  containers and older cauldrons without losing or duplicating items.
  Existing chunks should not be rewritten by Orchard worldgen.
- [ ] **Liquids:** test 250 mB bottles and 1,000 mB buckets for
  Golden Apple Juice and Honey in all relevant Crushing Tubs,
  Barrel and cauldron, including near-capacity, reload, no mixing
  and no terrain placement of either non-world bucket.
- [ ] **Orchard harvest:** on all three natural species, verify the
  rotating ~65% leaf eligibility, exposed-leaf restriction, right-click
  drops, 1–3 fruits, bone meal and crop regeneration.
- [ ] **Optional dependency absent/present:** test with and without
  Serene Seasons + GlitchCore, including seasonal limiting of fruit
  maturation, autumn/winter foliage appearance and safe fallback.
- [ ] **Dedicated Fabric server:** start the same candidate JAR
  without a graphics client, generate new chunks, reload chunks,
  and watch for classloading exceptions.
- [ ] **Multiplayer and stress:** two players handling the same
  containers, inventories full, repetitive transfers, hopper
  interactions, chunk unload, no dupes.
- [ ] **Resource/license audit:** verify original Rustic artworks
  are correctly attributed and embedded notices are adequate for
  the material actually included in the public JAR.
- [ ] **Packaging and version:** after these tests and explicit
  approval, set `mod_version=1.1.0`, update version-specific
  regression expectations, rebuild and verify the exact release JAR.
- [ ] **Publication approval:** finalize release notes, merge only
  approved code into `main`, tag `v1.1.0` and publish artifacts.
  Do not tag or publish automatically from this checklist.

## Provenance and scope
Implemented Orchard features are documented in
[the roadmap](ROADMAP.md) and the
[natural-worldgen notes](NATURAL_WORLDGEN_26_2.md).
[Grapes, grape juice, wine and wine processing are excluded](ROADMAP.md)
from 1.1.0 even if unused texture resources are present.

CI can confirm compile-time and static invariants, **not** the
unperformed server/runtime/compatibility checks. Fill the boxes only
after recording the result, date, candidate JAR hash and test world.

## Analisi pre-release e impossibilità di test manuali (11 novembre 2026)

L'autore ha confermato in-game il funzionamento di **cibi, marmellata,
crostata, torta di mandorle, ricette, fette e riposizionamento degli
spicchi di pizza**. Questi test effettuati non devono essere confusi
con i test ancora mancanti per compatibilità tra versioni e server.

L'autore **non può effettuare** le prove di Serene Seasons né di server
dedicato/multiplayer. Le due caselle relative restano perciò aperte:
non vanno automaticamente contrassegnate come superate. Prima della
pubblicazione valutare uno **smoke test CI headless** con/senza
Serene Seasons e un test di persistenza/salvataggio. Anche in caso
di test automatici riusciti, annotare i limiti rispetto a due giocatori reali.

È stata completata una [revisione statica di 163 Java, 68 script Python e dei
controlli su 1.568 JSON](../CODE_OPTIMIZATION_AUDIT_2026-11-11.md).
Il documento contiene opportunità di ottimizzazione **non applicate**,
con priorità, misure da raccogliere, rischi e controlli di compatibilità.
La build automatica del [commit di sola pulizia docs](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/42927860323)
è riuscita. Nessuna proposta di ottimizzazione costituisce un problema
prestazionale già misurato né un obbligo di refactoring prima del rilascio.
