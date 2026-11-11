# Audit statico del codice e piano di ottimizzazione — 11 novembre 2026

**Progetto:** Italian's Delight Refabricated — preparazione Orchard 1.1.0  
**Branch:** feature/orchards-1.1.0  
**Snapshot esaminato:** [baceedcf637a6d72affc94be84c63b891319a2f1](https://github.com/Pima95/ItaliansDelightRefabricated/commit/baceedcf637a6d72affc94be84c63b891319a2f1) (dopo la pulizia di docs)  
**Metodo:** il rilievo originario era in sola lettura. In seguito alla richiesta esplicita dell'autore, sono state implementate le ottimizzazioni conservative riepilogate sotto. **Nessuna pubblicazione o merge su main.** Le migliorie non sono ancora quantificate con benchmark.

## Attuazione autorizzata — 11 novembre 2026

La revisione iniziale identificava possibilità, non guasti. L'autore ha
successivamente autorizzato la realizzazione. Lo stato aggiornato è:

| ID | Stato finale in questo ciclo | Commit/evidenza |
|---|---|---|
| OPT-1101 | **Applicata** — lookup Cheese Vat con `RecipeManager.getAllOfType`, sempre aggiornato ai datapack, e fast path vuoto | [b8e4f68](https://github.com/Pima95/ItaliansDelightRefabricated/commit/b8e4f68d61aa8d1a733974d04e5e8d53013fee32), [32925f3](https://github.com/Pima95/ItaliansDelightRefabricated/commit/32925f35871f1e1558c3b3cde48cc538dcc8812d) |
| OPT-1102 | **Parziale sicura** — singolo MutableBlockPos per tick, uscita per mappa vuota. Il lavoro per tick sui calderoni attivi resta O(N); una nuova pianificazione richiederebbe prove sui 12.000 tick utili | [254fdcc](https://github.com/Pima95/ItaliansDelightRefabricated/commit/254fdcc9c297ffca3d359693057888e904f1c570), [32925f3](https://github.com/Pima95/ItaliansDelightRefabricated/commit/32925f35871f1e1558c3b3cde48cc538dcc8812d) |
| OPT-1103 | **Applicata** — snapshot di composizione cotta, cache negli stati renderer quando riutilizzati, identificatori texture e niente liste temporanee per ogni quarto | [4f60740](https://github.com/Pima95/ItaliansDelightRefabricated/commit/4f607405b669b8cafee3e5f12a1a4f5d49ace3d9) |
| OPT-1104 | **Applicata** — AABB invariabile per Crushing Tub | [254fdcc](https://github.com/Pima95/ItaliansDelightRefabricated/commit/254fdcc9c297ffca3d359693057888e904f1c570) |
| OPT-1105 | **Parziale sicura** — presenza Serene Seasons risolta una volta; letture del mondo/serra non cache-ate per non rendere stantie le foglie | [254fdcc](https://github.com/Pima95/ItaliansDelightRefabricated/commit/254fdcc9c297ffca3d359693057888e904f1c570) |
| OPT-1106 | **Differita** — i `RecipeManager.CachedCheck` esistono già; ulteriori cache di durata rischiano di non rispettare `/reload` | Profiling necessario |
| OPT-1107 | **Applicata** — memorizzazione ID della still texture per fluidi esterni, reset al cambio atlante o sprite acqua | [5915da9](https://github.com/Pima95/ItaliansDelightRefabricated/commit/5915da90808979aaa81edefdfb0f9d43fdbcc399) |
| OPT-1108 | **Applicata** — preflight sullo spicchio già presente nei dati prima di cambiare lo stato del blocco | [5915da9](https://github.com/Pima95/ItaliansDelightRefabricated/commit/5915da90808979aaa81edefdfb0f9d43fdbcc399) |
| OPT-1109 | **Parziale** — workflow CI aggiuntivo che accende un server dedicato headless, senza multiplayer e senza Serene Seasons; avvio riuscito | [run server 43005250714](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250714) |
| OPT-1110/1111 | **Differite** — refactoring estesi di registri, torte e famiglie di alberi aumentano il rischio senza beneficio prestazionale misurato | Dopo la release |
| OPT-1112 | **Applicata** — artifact JAR di sviluppo etichettato SHA; workflow ripulito | [run build 43005250616](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250616) |
| OPT-1113 | **Non necessaria ora** — micro-ottimizzazione dei sette fluidi senza vantaggio misurabile | Aggiungere fluidi prima di riconsiderare |
| OPT-1114 | **Verifica pre-release ancora aperta** — notice, provenienza asset e compatibilità legacy; nessun asset eliminato | Revisione licenze prima del tag |

**Esiti di verifica:** [Development CI 43005250616](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250616) **SUCCESS**;
[dedicated server boot 43005250714](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250714) **SUCCESS**, server Fabric Minecraft 26.2 ha raggiunto `Done`.
Questo non misura FPS/TPS, non verifica le partite multiplayer e non prova la compatibilità
runtime con Serene Seasons + GlitchCore.

È disponibile il [JAR QA dello stesso commit del test server](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/43005250616/artifacts/13848360820).
Per i test dell'autore vedere la [checklist specifica](orchards-1.1.0/RELEASE_QA_AFTER_OPTIMIZATIONS.md).
**Il nome interno JAR resta 1.0.0** fino all'autorizzazione finale e
non distingue dalla pubblicazione precedente senza lo SHA.

## Sintesi esecutiva

**Risultato:** nessun errore bloccante di compilazione o JSON confermato dall'audit statico; esistono però diverse opportunità plausibili di riduzione del lavoro per tick e per frame. Non sono disponibili misure TPS/FPS o profili di allocazione: pertanto **nessun incremento prestazionale è dimostrato**. I rischi di compatibilità vanno distinti dalle ottimizzazioni.

La CI del commit di pulizia, [run 42927860323](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/42927860323), è **SUCCESS**. Il controllo trasversale riporta **163 file Java, 1.568 JSON e 216 JSON di ricette** analizzati sintatticamente; nessuna ricetta identica o incompatibilità strutturale rilevata dai controlli implementati. **68 script Python** (incluso il generatore e il validatore dei nomi) e la pipeline GitHub Actions sono stati censiti ed esaminati staticamente. Inventariate inoltre **531 texture PNG**, modelli, item model, blockstate, dati worldgen e configurazioni Fabric/Mixin/Gradle.

**Metodo e limiti:** esaminati tutti i file sorgenti Java e Python mediante scansione sistematica di cicli, allocazioni, accessi al mondo, sincronizzazione, lookup ricette, rendering, mixin, registrazione e API opzionali; approfondite a mano le classi critiche elencate sotto. Le risorse JSON sono state enumerate e validate dai controlli di CI, **non reinterpretate manualmente una per una**. Non sono stati eseguiti benchmark, test GameTest, server dedicato, profili di heap né Serene Seasons in runtime. La compilazione non prova il comportamento in multiplayer.

## Matrice delle proposte

| ID | Area | Priorità | Natura | Quando |
|---|---|---|---|---|
| OPT-1101 | Ricette Cheese Vat / ricerca globale | Alta, P1 | CPU per tick | Misurare prima del rilascio; intervenire solo se rilevante |
| OPT-1102 | Calderoni del sale / scansione ogni tick | Alta, P1 | CPU + marcatura salvataggi | Profilare con molti calderoni |
| OPT-1103 | Renderer pizze / ricostruzione frammenti | Alta, P1 | Allocazioni e FPS | Profilare con molte pizze visibili |
| OPT-1104 | Crushing Tub / scansioni entità | Media, P2 | CPU + allocazioni per tick | Dopo misurazione |
| OPT-1105 | Foglie / stagione / greenhouse | Media, P2 | Letture mondo e tick | Dopo test con molte chiome |
| OPT-1106 | Cheese Aging Rack e Cheese Hook | Media, P2 | Lookup ricette per tick | Profilare con produzione massiva |
| OPT-1107 | Render dei fluidi esterni sconosciuti | Media, P2 | Lookup sprite per frame | Solo in presenza di fluidi di altre mod |
| OPT-1108 | Stato persistente di spicchi ricollocati | Alta, P1 *robustezza* | Atomicità su dati anomali | Test regressione prima di un refactoring |
| OPT-1109 | Testing server e dipendenza opzionale | Alta, P1 *qualità* | Copertura mancante | Gate di release, non ottimizzazione runtime |
| OPT-1110 | Classi di registrazione e blocchi dolce | Bassa, P3 | Manutenibilità e duplicati | Dopo la 1.1.0 |
| OPT-1111 | Le tre famiglie di foglie e worldgen | Bassa, P3 | Manutenibilità e coerenza | Dopo la 1.1.0 |
| OPT-1112 | CI statica e packaging dei dati | Media, P2 | Affidabilità della build | Prima della release, senza cambiare meccaniche |
| OPT-1113 | Cataloghi di fluidi e conversioni | Bassa, P3 | Semplificazione | Solo se necessario |
| OPT-1114 | Asset legacy, attribuzioni e tag | Alta, P1 *rilascio* | Controllo legale e dimensioni | Gate di release, prima della pubblicazione |

### OPT-1101 — Cheese Vat: indicizzare ricette e rispettare la precedenza del siero

**Evidenza:** [CheeseVatBlockEntity.java](../src/main/java/dev/italiansdelight/common/block/entity/CheeseVatBlockEntity.java#L138-L174) esegue findRecipe dal serverTick; [findNormalRecipe](../src/main/java/dev/italiansdelight/common/block/entity/CheeseVatBlockEntity.java#L290-L368) e [findWheyRecipe](../src/main/java/dev/italiansdelight/common/block/entity/CheeseVatBlockEntity.java#L465-L520) possono scorrere RecipeManager.getRecipes(), cioè un elenco globale, quando non basta il suggerimento della ricetta corrente. **Esiste già una fast path per la ricetta in lavorazione**: non va ignorata né riscritta alla cieca.

**Proposta:** costruire per reload una vista delle sole ricette del tipo Cheese Vat, mantenere ordinamento deterministico e priorità siero > contenitore esatto > senza contenitore > contenitore pendente. Invalidare su datapack reload, cambio RecipeManager e cambi input/contenitore/serbatoio. Confrontare il risultato per un mix di 10, 50 e 200 vasche e un numero elevato di ricette installate da altri datapack.

**Rischi:** cambiare erroneamente la selezione Ricotta/Crema e i rimborsi delle ciotole o il progresso durante cambi di ricetta. Nessuna cache permanente di RecipeHolder fra reload senza invalidazione.

### OPT-1102 — Sale: percorso O(numero di calderoni) ogni tick

**Evidenza:** [SaltCauldronManager.java](../src/main/java/dev/italiansdelight/common/salt/SaltCauldronManager.java#L152-L251) percorre ogni posizione tracciata a ogni tick del mondo; per ciascuna valida verifica blocco e superficie con Heightmap, incrementa il contatore e marca dati persistenti dirty. La scansione dei chunk è già filtrata con LevelChunkSection.maybeHas ed è una protezione importante da mantenere.

**Proposta:** benchmark con 10, 100, 1.000 calderoni; valutare quote di verifica per tick, gestione di sole transizioni meteo/luce, e/o un contatore dei tick utili basato su eventi e tempi validati. Distinguere subito i calderoni rotti da quelli in chunk scaricati. Conservare la soglia effettiva di **12.000 tick utili**, la pausa per tempo sfavorevole e il fatto che un chunk scaricato non debba avanzare né essere caricato forzatamente.

**Rischio:** salti temporali, produzione di sale anticipata/posticipata, stati rimasti in memoria, perdita di progresso su reload. Evitare una semplice riduzione della frequenza senza compensazione dei tick.

### OPT-1103 — Rendering: ridurre costruzione di oggetti per pizza visibile

**Evidenza:** [CookedPizzaRenderer.java](../src/main/java/dev/italiansdelight/client/render/CookedPizzaRenderer.java#L29-L45) ricostruisce lo stato grafico durante extractRenderState. [CookedPizzaBlockEntity.renderComposition](../src/main/java/dev/italiansdelight/common/block/entity/CookedPizzaBlockEntity.java#L112-L143) alloca lista/applicazioni e ricostruisce il risultato di ogni quarto; [PizzaToppingVisuals.extract](../src/main/java/dev/italiansdelight/client/render/PizzaToppingVisuals.java#L62-L160) alloca più liste, frammenti, array di posti e identificatori.

**Proposta:** confrontare FPS, tempo renderer e allocazioni con 10/50/200 pizze visibili; poi valutare cache **solo client** della rappresentazione immutabile per BlockEntity, invalidata quando cambiano composizione, mask dei quarti, rotazione, risorse del client o texture pack. Evitare cache globali di ItemStack mutabili. Per pizze crude e cotte mantenere identiche l'orientazione e le regole di posizionamento degli sprite.

**Rischi:** texture dei condimenti stale dopo il cambio di spicchio, visualizzazioni errate dopo /reload o cambio resource pack. Prima della 1.1.0 non fare una cache speculativa.

### OPT-1104 — Crushing Tub: controlli entità negli impianti attivi

**Evidenza:** [CrushingTubBlockEntity.serverTick](../src/main/java/dev/italiansdelight/common/block/entity/CrushingTubBlockEntity.java#L88-L128) alloca una AABB e chiama getEntitiesOfClass per ogni tub con input, ogni tick. Usa una mappa transitoria UUID→tick per il salto, con pulizia ogni 20 tick; quest'ultimo comportamento è già difensivo.

**Proposta:** misurare 10/100 tub con input e nessun giocatore vicino; valutare il riuso della AABB costruita dalla posizione fissa, o una diversa strategia di prossimità che mantenga comunque il rilevamento del passaggio airborne→grounded. Non cambiare la finestra di 20 tick e non consentire stomp multipli con la stessa caduta.

**Rischi:** perdere salti rapidi oppure processare chi cammina, doppio drop / consumo non atomico. Va profilato prima di cambiarlo.

### OPT-1105 — Foglie e Serene Seasons: evitare controlli ridondanti

**Evidenza:** [AlmondLeavesBlock.java](../src/main/java/dev/italiansdelight/common/block/AlmondLeavesBlock.java#L75-L141) è sempre random-ticking per supportare anche le variazioni stagionali; lo stesso schema ricorre in OliveLeavesBlock e FruitCherryLeavesBlock. [OrchardSeasonPolicy](../src/main/java/dev/italiansdelight/common/orchard/OrchardSeasonPolicy.java#L19-L53) interroga FabricLoader per la mod opzionale. [SereneSeasonsOrchardBridge.hasGreenhouseGlass](../src/main/java/dev/italiansdelight/common/orchard/SereneSeasonsOrchardBridge.java#L20-L31) legge fino a 16 blocchi sopra la foglia per controllo.

**Proposta:** misurare costo in chunk ricchi di chiome, nelle modalità con/senza mod e dentro/fuori serre. La presenza di una mod nel processo è costante e potrebbe essere risolta una volta; il controllo del vetro non è costante e **non** va memorizzato senza un sistema completo di invalidazione per modifiche ai blocchi. Se si introduce un percorso leggero senza stagioni, preservare decay vanilla, rotazione del 65%, esposizione, crescita e bonemeal.

**Rischio alto di alterare visivamente la stagione** con una cache ingenua. L'assenza della mod opzionale non deve causare classloading error.

### OPT-1106 — Rack e Cheese Hook: durata ricette letta ripetutamente

**Evidenza:** [CheeseAgingRackBlockEntity.serverTick](../src/main/java/dev/italiansdelight/common/block/entity/CheeseAgingRackBlockEntity.java#L147-L230) usa RecipeManager.createCheck/getRecipeFor per ogni slot attivo a ogni tick. [CheeseHookBlockEntity.serverTick](../src/main/java/dev/italiansdelight/common/block/entity/CheeseHookBlockEntity.java#L170-L247) calcola la durata tramite una ricerca ricetta per ogni prodotto in elaborazione. La Cheese Vat e il Pizza Oven marcano inoltre il salvataggio dirty durante l'avanzamento, per preservare i progressi.

**Proposta:** valutare cache di sola durata per tipo e RecipeManager con reload invalidation, senza sacrificare la progressione esatta in tick. Profilare 50/200 hook e rack. Lo stato dei blocchi deve cambiare soltanto quando cambia la rappresentazione grafica, non per riflesso di una cache.

### OPT-1107 — Sprite dei fluidi di altre mod

**Evidenza:** [LiquidSurfaceVisuals.java](../src/main/java/dev/italiansdelight/client/render/LiquidSurfaceVisuals.java#L90-L144) tenta fino a cinque percorsi dell'atlante per ogni fluido sconosciuto e costruisce la lista dei candidati per ogni render; quelli conosciuti usano già la mappa SURFACES.

**Proposta:** memorizzare il percorso risolto per atlante/versione risorse e svuotare la cache al resource reload. Conservare il tint di FluidVariantRendering, incluso alpha, e il fallback acqua sui fluidi privi di still sprite. Priorità bassa se nessun fluido sconosciuto viene mostrato.

### OPT-1108 — Spicchi riposizionabili: transazione sul blocco e BE

**Evidenza:** [PizzaSlicePlacement.java](../src/main/java/dev/italiansdelight/common/pizza/PizzaSlicePlacement.java#L70-L90) porta avanti il BlockState REMAINING, poi inserisce la composizione nel CookedPizzaBlockEntity; [insertSlice](../src/main/java/dev/italiansdelight/common/block/entity/CookedPizzaBlockEntity.java#L92-L114) può rifiutare dati non validi o un quarto duplicato con IllegalStateException. Il percorso normale è stato testato dall'autore, ma gli stati corrotti / mod concorrenti non sono stati simulati.

**Proposta:** aggiungere test con salvataggio alterato, BE mancante, slot già occupato nei dati nonostante il mask e interazioni ravvicinate. In un futuro refactoring, prevalidare e mantenere la modifica atomica/rollback anche quando BE e BlockState sono incoerenti. **Non è un bug riprodotto** e non giustifica cambiare ora la meccanica approvata.

### OPT-1109 — Copertura runtime senza chiedere al giocatore server e Serene Seasons

**Evidenza:** [workflow GitHub Actions](../.github/workflows/pizza-build.yml) esegue molte verifiche statiche e Gradle; [check_orchard_serene_seasons.py](../.github/scripts/check_orchard_serene_seasons.py), [check_pizza_server_side_safety.py](../.github/scripts/check_pizza_server_side_safety.py) e [check_pizza_slice_replacement.py](../.github/scripts/check_pizza_slice_replacement.py) riconoscono esplicitamente che non sostituiscono un avvio server, due client o prove con la mod opzionale.

**Proposta:** prima della pubblicazione predisporre un workflow **separato, volontario e non pubblicante**, con avvio headless di un server Fabric 26.2, un caso senza Serene Seasons e uno con Serene Seasons+GlitchCore nelle versioni compatibili. Aggiungere GameTest/integration test per serializzazione spicchi, ricarica chunk, contenitori e import di un mondo backup 1.0.0. Non segnare verde un test che non è stato effettivamente eseguito.

**Vincolo dell'autore:** non può verificare personalmente Serene Seasons e multiplayer. Questo limita le evidenze disponibili; l'automazione può coprire parte della superficie, ma non dimostra da sola la correttezza con due giocatori reali.

### OPT-1110 — Classi grandi e due dolci quasi identici

**Evidenza:** ModBlocks.java ≈1.249 righe, ModItems.java ≈1.021, CheeseVatBlockEntity.java ≈1.518; [AlmondCakeBlock.java](../src/main/java/dev/italiansdelight/common/block/AlmondCakeBlock.java) e [CherryTartBlock.java](../src/main/java/dev/italiansdelight/common/block/CherryTartBlock.java) hanno strutture praticamente identiche (quattro fette, facing, loot e consumo).

**Proposta:** dopo il rilascio estrarre metodi di registrazione per le tre famiglie di legno e un componente comune per il comportamento dei dolci, conservando le classi concrete, i CODEC, le chiavi BlockState, gli ID nel registro e la forma dei salvataggi. Questa è **riduzione del debito tecnico**, non un guadagno FPS/TPS misurato. Refactoring ampio prima di 1.1.0 sconsigliato.

### OPT-1111 — Alberi: tre implementazioni molto simili

**Evidenza:** OliveLeavesBlock, AlmondLeavesBlock, FruitCherryLeavesBlock, i tre TrunkPlacer e i tre FoliagePlacer ripetono parti di selezione frutto, esposizione e placement; sono già presenti helper condivisi OrchardFruitingCycle e OrchardSeasonPolicy.

**Proposta:** consolidare solo funzioni *pure* comuni, senza unificare forme e calendario, che sono specifici e approvati. Confronto visivo e delle probabilità dopo ogni modifica. Il disegno della foresta non va modificato come sottoprodotto di un refactor.

### OPT-1112 — CI: test delle stringhe e controlli dei pacchetti

**Evidenza:** la suite contiene 68 script Python, moltissimi con assert su sottostringhe del sorgente. Anche la lunga [pipeline](../.github/workflows/pizza-build.yml) chiama verifiche di singole fasi, alcune con titoli non aggiornati (per esempio «70 percent» mentre il codice usa 65%). Alcuni controlli leggono davvero JSON, SHA e pixel; altri **non verificano la semantica**. Non ci sono test Java di integrazione nel sorgente esaminato.

**Proposta:** mantenere i controlli ad alto valore su struttura dati, PNG e modelli; accorpare progressivamente i check duplicati e costruire test comportamentali per transazioni e serializzazione. Correggere nomi obsoleti del workflow quando si prepara il packaging. Non confondere il verde CI con «tutti i casi runtime superati».

### OPT-1113 — Mapping di contenitori e cataloghi

**Evidenza:** BarrelFluidContainers usa una serie di condizioni per ID fluidi; FluidCatalog.canBePlaced usa stream su una lista costante di sette definizioni. I cataloghi di item fluidi e le definizioni per JEI sono intenzionalmente separati.

**Proposta:** se si aggiungono altri fluidi, valutare una tabella immutabile che raccolga compatibilità di contenitore e fluidi, con verifica di tutte le corrispondenze esistenti. Non sostituire per «performance» il piccolo stream di sette elementi senza una misura: costo trascurabile rispetto a mondo/render/tick.

### OPT-1114 — Asset inclusi nella distribuzione

**Evidenza:** censite 531 texture PNG; una coppia ha blob identico (cheese_vat_milk.png / cheese_vat_particle.png). [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md) rimanda ai [match Rustic](orchards-1.1.0/RUSTIC_ASSET_MATCHES.md), inclusi materiali legacy non necessariamente usati. La duplicazione di una singola texture è una micro-ottimizzazione, non un difetto.

**Proposta:** inventario per asset realmente referenziati da modelli, atlas, animazioni e ricette; escludere dal JAR solo gli asset dimostrabilmente inutilizzati e non necessari ai salvataggi o a mod compatibili. Verificare attribuzione e avvisi delle texture effettivamente distribuite. Non cancellare adesso nessun PNG dal runtime.

## Interventi che NON consiglio prima della release

- Cache globali di RecipeHolder, ItemStack o render state senza invalidazione su /reload, cambio mondo, risorse e quarti della pizza.
- Riduzione ingenua della frequenza dei tick di maturazione/fermentazione: rischia di cambiare tempi approvati.
- Modifica dei pesi worldgen, quota produttiva 65%, ricette, cibi o texture già approvate in nome delle prestazioni.
- Nuovi ID o rinomina di chiavi di registrazione; l'item di marmellata conserva volutamente l'ID storico cherry_jam_bottle benché l'icona sia un barattolo.
- Unificazione aggressiva di classi BlockEntity o dell'NBT alla vigilia del rilascio.
- Eliminazione di risorse Rustic/legacy senza verifica puntuale delle licenze e dei riferimenti.

## Protocollo di benchmark prima di ottimizzare

1. Fissare **lo stesso commit, seed, impostazioni JVM, mods, chunk e distanza di rendering** per scenario base e candidata; registrare versione Java e Fabric.
2. Lato server: campioni 10/50/200 Cheese Vat, 10/100 tub attivi, 10/100/1000 calderoni con acqua, 50/200 rack e hook, 1.000/10.000 foglie. Misurare TPS, MSPT mediana e p95, CPU e allocazioni per tick. Non usare simulazioni diverse tra prima/dopo.
3. Lato client: 10/50/200 pizze con topping misti in vista; misurare FPS mediano e p5, time per frame e allocazioni. Ripetere dopo cambio risorse e dopo posizionamento/rimozione spicchi.
4. Validare comportamento e dati: svuotamento, riempimento, pioggia, world reload, /reload, generazione new chunk, interazioni abortite, supporto rotto e salvataggi 1.0.0.
5. Registrare **evidenze numeriche prima e dopo** per ogni proposta; se il guadagno non supera il rumore dei test, non introdurre complessità di cache o API nuove.

## Decisioni e release gate

- **Ottimizzazioni autorizzate e implementate:** quelle elencate nella tabella di attuazione. Ulteriori refactoring richiedono nuove prove; `main` e la versione pubblica restano invariati.
- **Release candidate:** la build CI dello snapshot esaminato è riuscita, ma la release 1.1.0 **non è autorizzata automaticamente**.
- **Non verificato:** Serene Seasons con e senza dipendenza, server dedicato, multiplayer reale, backward compatibility in un mondo 1.0.0, profilo prestazionale quantitativo.
- **Da verificare prima di un tag pubblico:** avvio client pulito, upgrade su copia di mondo 1.0.0, licenze/notice e possibilmente almeno uno smoke test automatizzato di server dedicato/Serene Seasons. Se un test non è realizzabile, registrare il rischio residuo esplicitamente, senza contrassegnarlo come superato.
- **Implementazione consigliata:** misurare OPT-1101/1102/1103 per primi; non rifattorizzare soltanto perché una classe è lunga. OPT-1108/1109 sono controlli di sicurezza/qualità, non promesse di maggiore velocità.

**Conclusione aggiornata:** le ottimizzazioni conservative sono applicate e passano la CI e lo smoke server. Non sono stati eseguiti benchmark quantitativi e le verifiche manuali restano necessarie per validare comportamento, texture e salvataggi; niente release o merge automatici.
