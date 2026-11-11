# Orchard Update 1.1.0 — Release-readiness checklist

**Branch:** `feature/orchards-1.1.0`
**Minecraft:** 26.2 · **Java:** 25 · **Fabric Loader:** 0.20.1+
**Fabric API:** 0.161.0+26.2 · **Farmer's Delight Refabricated:** 26.2-3.6.29+refabricated.

**Status, 11 November 2026:** internal candidate
`1.1.0-rc.1` prepared after the author's functional approval and
confirmation of compatibility with old 1.0.0 saves. Public remains
version 1.0.0; **no 1.1.0 tag or public publication yet**.
The RC stays on the development branch until separately approved.

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
- [x] **Existing world:** the author explicitly approved compatibility
  with previous 1.0.0 saves on 11 November 2026. This records the author's
  in-game validation; no separate automated migration fixture or log is
  available. Continue recommending a backup before all future updates.
  Worldgen still applies only to new chunks.
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
Le proposte conservative elencate nel rapporto di audit sono ora **in parte implementate**, con CI verde e smoke server riuscito. Il report distingue i cambiamenti effettuati dai refactoring differiti e dai rischi ancora aperti.
La build automatica del [commit di sola pulizia docs](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/42927860323)
è riuscita. Nessuna proposta di ottimizzazione costituisce un problema
prestazionale già misurato né un obbligo di refactoring prima del rilascio.

## Verifiche dopo ottimizzazione (11 novembre 2026)

- [x] **Development CI:** compilazione e controlli statici superati
  ([run 43005250616](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250616)).
- [x] **Smoke server dedicato:** Fabric 0.20.1, Minecraft 26.2, Java 25;
  avvio headless riuscito fino a `Done`, senza giocatori connessi
  ([run 43005250714](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250714)).
- [x] **JAR di prova identificato:** [artifact QA SHA d9d71395](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250616/artifacts/13848360820).
  Era il precedente JAR di test, con versione interna ancora 1.0.0.
  La nuova candidate `1.1.0-rc.1` è distinta e **non è una release**.
- [x] **Regressioni funzionali in-game delle ottimizzazioni:** l'autore ha confermato
  «Funziona tutto» l'11 novembre 2026 dopo la checklist su Cheese Vat, sale,
  Crushing Tub, pizze/spicchi, fluidi, alberi, JEI e controlli del mondo.
  La conferma è complessiva: non sono allegati log, risultati per singola prova
  o prova documentale di un aggiornamento su salvataggio 1.0.0; la specifica
  compatibilità retroattiva è stata successivamente **approvata dall'autore**
  l'11 novembre 2026; non esiste però un report indipendente di migrazione.
- [ ] **Compatibilità Serene Seasons + GlitchCore in runtime** e
  **multiplayer reale**: non coperte dallo smoke headless.
- [ ] **Provenienza/licenze asset** e autorizzazione finale alla pubblicazione.

Le [azioni di collaudo richieste all'autore](RELEASE_QA_AFTER_OPTIMIZATIONS.md)
sono circoscritte alle funzionalità verificabili senza server multiplayer e
senza installazione di Serene Seasons. **Non spuntare le ultime due verifiche
in assenza di evidenze reali.**

## Conferma dell'autore — 11 novembre 2026

Dopo l'installazione e la checklist della build ottimizzata, l'autore ha
confermato in chat: **«Funziona tutto»**. Questa evidenza chiude il giro di
regressione funzionale segnalato all'autore. Non equivale a un benchmark né
ad attestare prove non descritte (server con due utenti,
Serene Seasons/GlitchCore). La compatibilità 1.0.0 è stata in seguito
esplicitamente approvata dall'autore e registrata nella checklist. Sono ancora aperte l'analisi licenze,
la scelta sui rischi residui e l'autorizzazione esplicita a promuovere la
versione 1.1.0 o a pubblicarla.

## Packaging della release candidate — 11 novembre 2026

- [x] **Salvataggi 1.0.0:** compatibilità approvata dall'autore;
  backup del mondo originale sempre raccomandato.
- [x] **Metadata candidato:** versione `1.1.0-rc.1` solo nel branch
  Orchard; versione pubblicata e `main` restano a 1.0.0.
- [x] **Inventario Rustic:** 39 asset/metadata ancora identici e
  due eliminati precedentemente; dettagli in
  [audit asset e licenze](LICENSE_AND_ASSET_RC_REVIEW.md).
- [ ] **Packaging definitivo della RC:** GitHub Actions deve controllare
  il `fabric.mod.json` compilato, notices nel JAR e artifact scaricabile.
- [ ] **Provenienza/crediti di ogni asset legacy e artisti individuali:**
  da esaminare prima della pubblicazione finale.
- [ ] **Serene Seasons+GlitchCore e multiplayer reale:** non verificati;
  avvio server dedicato senza utenti riuscito.
- [ ] **Autorizzazione release definitiva:** serve consenso separato
  prima di cambiare `1.1.0-rc.1` in `1.1.0`, creare tag, unire in
  `main` o pubblicare su GitHub/CurseForge.
