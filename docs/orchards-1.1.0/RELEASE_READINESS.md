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
- [x] **Packaging della RC verificato:** [CI 43010592706](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43010592706)
  ha compilato `italiansdelight-1.1.0-rc.1.jar`, verificato
  `fabric.mod.json` e tutti i notice nel JAR e pubblicato
  l'[artifact di test](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43010592706/artifacts/13849559373).
  Lo [smoke server dedicato 43010592911](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43010592911)
  è riuscito senza giocatori connessi.
- [ ] **Provenienza/crediti di ogni asset legacy e artisti individuali:**
  da esaminare prima della pubblicazione finale.
- [ ] **Serene Seasons+GlitchCore e multiplayer reale:** non verificati;
  avvio server dedicato senza utenti riuscito.
- [ ] **Autorizzazione release definitiva:** serve consenso separato
  prima di cambiare `1.1.0-rc.1` in `1.1.0`, creare tag, unire in
  `main` o pubblicare su GitHub/CurseForge.

## Scelta licenza di progetto (11 novembre 2026)

- [x] L'autore ha scelto **Apache-2.0** per il progetto a partire dalla
  futura release Orchard, applicabile ai suoi contributi originali.
- [x] Il branch di sviluppo include `LICENSE`, `NOTICE`, aggiornamento
  di `fabric.mod.json` e packaging dei notice in `build.gradle`.
- [ ] Ricompilare la candidate e verificare la presenza di
  `LICENSE_italiansdelight` e `META-INF/NOTICE` nel JAR effettivo.
- [ ] Verificare i crediti di tutti gli asset legacy prima del rilascio;
  le condizioni MIT dei materiali di terzi restano in vigore.
- [ ] Aggiornare la licenza selezionata su CurseForge/Modrinth al momento
  della pubblicazione autorizzata; versione pubblica 1.0.0 resta MIT.

## Java 25 e controlli Apache-2.0 della candidate (11 novembre 2026)

- [x] **Decisione tecnica:** mantenere Java 25 e Minecraft 26.2 nella
  release 1.1.0; Java 21 richiederebbe un porting separato.
- [x] **Verifica statica predisposta:** lo script
  `.github/scripts/check_orchard_release_readiness.py` controlla la
  coerenza fra MC 26.2, target Java 25, metadati Apache-2.0, file
  `NOTICE` e packaging delle attribuzioni di terzi.
- [x] **Verifica JAR predisposta:** dopo la build, il workflow
  `.github/workflows/pizza-build.yml` verifica il bytecode Java 25
  (class-file major 69) della classe principale, la versione RC,
  la licenza Apache-2.0, `LICENSE_italiansdelight`,
  `META-INF/NOTICE` e gli avvisi MIT delle risorse di terzi.
- [ ] **CI del nuovo commit:** verificare esito effettivo e artifact della
  candidate; modifiche ai test non equivalgono a una build riuscita.
- [ ] **Pubblicazione:** mantenere `main`, versioni pubbliche e descrizioni
  CurseForge/Modrinth invariati fino all'autorizzazione finale.

## Pulizia dei file ignorati e prevenzione regressioni (11 novembre 2026)

- [x] **Pulizia del repository:** commit `a39cb50` rimuove dal tracciamento
  quattro documenti sotto `docs/dishes/` e `docs/herbs/` già esclusi da
  `.gitignore`; il codice e le texture della mod non sono coinvolti.
- [x] **Controllo automatico introdotto:**
  `.github/scripts/check_ignored_tracked_files.py` esegue in sola lettura
  `git ls-files --cached --ignored --exclude-standard -z` e fallisce se
  trova file contemporaneamente tracciati e ignorati.
- [x] **CI aggiornata:** lo script viene eseguito dalla build GitHub Actions
  per il branch Orchard prima dei test e della compilazione Java 25.
- [ ] **Esito CI:** verificare la riuscita del workflow dopo l'ultimo commit;
  la sola presenza del nuovo controllo non certifica una build riuscita.
- [ ] **Licenze degli asset legacy e test runtime opzionali:** rimangono
  separati e aperti; nessuna release, tag o merge in `main` è autorizzata.

## Riscontri del client e attribuzioni (11 novembre 2026)

- [x] **Avvio del gioco (utente):** l'autore ha confermato
  «Il gioco si avvia». Si registra il test di apertura del client
  come positivo, **non** come collaudo completo di ogni funzione.
  La versione esatta del JAR e il log non sono allegati a tale conferma;
  la checklist `Client, final candidate JAR` resta dunque aperta per
  installazione pulita, icona, JEI e verifica dell'artifact finale.
- [x] **Credits upstream documentati:**
  `THIRD_PARTY_NOTICES.md` ora riporta gli artisti elencati pubblicamente
  dal team Italian's Delight originale e i riconoscimenti Rustic, con
  esplicito limite sull'attribuzione per singolo PNG.
- [x] **Protezione CI predisposta:** nel JAR della RC sono verificati i
  crediti upstream, oltre ad Apache-2.0, NOTICE e licenza MIT delle casse.
- [ ] **CI dopo i nuovi commit:** acquisire un esito effettivo dei job;
  l'esito non si deduce dal solo avvio del client o dalla presenza del test.
- [ ] **Audit dei singoli PNG legacy:** resta aperto, così come l'eventuale
  recupero di avvisi copyright specifici delle distribuzioni originali.
- [ ] **Test opzionali e multiplayer:** non dichiarati superati.

## Chiusura ricerca attribuzione non documentata e test server (11 novembre 2026)

- [x] **Decisione dell'autore:** interrompere la ricerca dei nomi degli
  artisti delle singole texture quando tali informazioni non sono
  documentate nel progetto originale; mantenere tutti i crediti
  disponibili e gli avvisi di licenza già raccolti.
- [ ] **Provenienza per-singolo-asset:** non verificabile per parte delle
  texture storiche: rimane una limitazione dichiarata, non una
  attestazione di titolarità o di conformità legale completa.
  Riaprire la verifica soltanto in presenza di nuove fonti pertinenti.
- [x] **Smoke automatico ampliato:** `.github/workflows/orchard-server-smoke.yml`
  ora esegue un primo avvio dedicato e prova a riavviare **lo stesso mondo**
  dopo aver controllato l'esistenza di `run/world/level.dat`.
  Il test non crea un mondo migrato dalla 1.0.0 e non verifica dati
  di inventari, contenitori, blocchi o due giocatori simultanei.
- [ ] **Nuovo esito Actions:** la ripetizione di avvio e caricamento
  dello stesso mondo sarà considerata positiva solo dopo un job CI
  completato correttamente e log scaricabile.
- [ ] **Compatibilità Serene Seasons + GlitchCore e multiplayer reale:**
  ancora privi di test runtime; non dichiararli superati.
- [ ] **Rilascio definitivo:** richiede sempre autorizzazione
  esplicita separata per aggiornare `main`, tag e pagine pubbliche.


## Ripresa dello sviluppo dopo il ripristino di main (11 novembre 2026)

- [x] La PR #11 ha ripristinato `main` ai file della release pubblica
  1.0.0 (licenza MIT); la PR #10 resta in cronologia, ma il suo
  contenuto è stato annullato con un nuovo commit.
- [x] Il branch autorizzato per proseguire è esclusivamente
  `feature/orchards-1.1.0`, con `mod_version=1.1.0-rc.1`;
  non creare ulteriori branch né effettuare merge senza richiesta
  esplicita dell'autore.
- [x] L'autore conferma l'avvio del client e l'apertura del mondo.
- [ ] Generare un artifact QA aggiornato dal commit corrente del branch
  di sviluppo con i notice/licenza appena sistemati; la precedente
  build QA era antecedente ad alcune modifiche di packaging.
- [ ] Installare **un solo** JAR di prova `italiansdelight-1.1.0-rc.1.jar`
  e completare le ultime verifiche giocabili prima di proporre
  l'integrazione finale.
- [ ] I test non realizzati (Serene Seasons/GlitchCore e multiplayer
  reale) devono restare indicati come limiti, non come test superati.
- [ ] La futura pubblicazione richiederà una nuova strategia esplicita
  di integrazione, perché il vecchio merge è stato annullato nella
  cronologia di `main`.
