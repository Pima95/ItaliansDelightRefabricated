# Revisione completa delle opportunità di ottimizzazione

**Progetto:** Italian's Delight Refabricated — Minecraft 26.2 / Fabric / Java 25  
**Data:** 19 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Snapshot sorgenti revisionato:** `05ffed4c2ad92ecce8c5349711cfa6d076ae6edf`  
**Vincolo:** **SOLO RICOGNIZIONE. Non è stata eseguita alcuna ottimizzazione, correzione, sostituzione di asset, cambio di ricetta o refactoring.** Questo nuovo Markdown è l'unico file creato.

## 1. Perimetro e metodo

- Censiti e sottoposti a scansione statica **tutti i 92 file Java** presenti nel ramo. Sono state cercate iterazioni, allocazioni, ricerche di ricette, query al mondo, tick di entità/blocchi, preparazione dello stato di render e sincronizzazioni. I percorsi di costo maggiore sono stati **letti direttamente e confrontati tra i chiamanti**.
- Riesaminati `build.gradle`, `gradle.properties`, registrazioni, mixin, interazioni pizza, JEI, formaggi, salumi, Cheese Vat, sale, rendering, blocchi delle pizze, pala, worldgen e documentazione. I 94 JSON di ricette e i file di lingua erano stati già verificati nel precedente audit.
- Prima revisione generale e problemi funzionali: **[CODE_AUDIT_2026-10-19.md](CODE_AUDIT_2026-10-19.md)**. Questo rapporto evita di spacciare le sue voci per problemi nuovi: qui approfondisce il **costo delle operazioni** e propone esperimenti e soluzioni conservative.
- **Non eseguiti:** build, client, server dedicato, profilazione CPU/GPU, misure FPS/MSPT/GC, benchmark, test automatizzati, ispezione manuale di ogni singola texture/modello. I valori come «2.601 posizioni» sono calcoli derivati dal codice, **non misure di prestazione**.
- Le opportunità sono ordinate per *probabile importanza*, non per guadagno garantito. La prima regola è misurare e mantenere invariato il comportamento gameplay.

### Classificazione

- **O1 — Da profilare presto:** percorso caldo continuo con molte operazioni o allocazioni evidenti; rendimento non ancora quantificato.
- **O2 — Valutare successivamente:** ottimizzazione plausibile ma dipendente dalla scala del mondo/modpack, o che richiede invalidazione della cache.
- **O3 — Solo manutenzione/beneficio marginale:** non giustifica da sola una modifica prima della release.

Stati di tutte le voci: **PROPOSTA, NON IMPLEMENTATA**.

## 2. Indice delle opportunità

| ID | Priorità | Area | Evidenza essenziale |
|---|---|---|---|
| OPT-001 | O1 | Overlay tagli pizza | Fino a 2.601 letture di blocchi a estrazione |
| OPT-002 | O1 | Fornace/affumicatore vanilla | Ricostruzione e copie del risultato dinamico per tick |
| OPT-003 | O1 | Cheese Vat | Ricerca e validazione ripetuta di ricette |
| OPT-004 | O1 | Render topping crudi/cotti | Nuovi stati item per ogni topping/quarto |
| OPT-005 | O2 | Sale | Iterazione di ogni calderone tracciato ogni tick |
| OPT-006 | O2 | Stagionatura | Ricerca ricetta e ItemStack temporanei a ogni tick |
| OPT-007 | O2 | Pacchetti blocchi pizza | Notifiche `sendBlockUpdated` doppie |
| OPT-008 | O2 | Mixin globale del sale | Callback su ogni `Level.setBlock` |
| OPT-009 | O2 | Durata ricerca Cheese Vat | Match shapeless con array/backtracking ricreati |
| OPT-010 | O2 | Ritorno ciotole | Due procedure quasi identiche nei forni |
| OPT-011 | O3 | Nomi dinamici | Creazione ripetuta di set/liste e ordinamenti |
| OPT-012 | O3 | Item render pizza peel | Piccole allocazioni di array per bounding/extent |
| OPT-013 | O3 | JEI/formaggi | Categorie molto simili e codice di manutenzione |
| OPT-014 | O3 | Ricette rack | Molti file paralleli per varianti di legno |
| OPT-015 | O3 | Cheese Vat e output | Validazioni/stati usati solo durante azioni rare |

## 3. Analisi dettagliata

### OPT-001 — Ridurre il volume di scansione dell'overlay dei tagli

**Priorità O1 | Client / FPS | Evidenza diretta, impatto da misurare.**

- **File:** `src/main/java/dev/italiansdelight/client/render/PizzaDivisionOverlay.java`, righe ~49–53, 79–115, 117–136.
- **Situazione:** tenendo un mattarello o un topping nativo approvato, `extract()` percorre da `centerX-8` a `centerX+8`, da `centerY-4` a `centerY+4` e da `centerZ-8` a `centerZ+8`: **17 × 9 × 17 = 2.601 `getBlockState` per estrazione**, anche in assenza di pizze. A 60 estrazioni/s, il limite teorico è **156.060 consultazioni/s** prima di computare gli altri passaggi; non è una misura di FPS.
- **Possibile ottimizzazione:** (A) filtrare chunk e sezioni già note contenere pizza/BlockEntity; (B) usare un elenco locale di posizioni di impasti con tagli, aggiornato da eventi di modifica/caricamento/scaricamento e con invalidazione prudente; (C) eventualmente ridurre la ricerca al bersaglio del giocatore quando l'overlay non deve mostrare altre pizze.
- **Rischi:** overlay non aggiornato dopo split/join, cambio strumento, teletrasporto, chunk reload, modifiche da altri giocatori o datapack. Non mantenere riferimenti a Level/BlockEntity nella fase di render.
- **Test:** 0 / 20 / 100 impasti nel raggio, oggetto pertinente in mano vs mano vuota, movimento del giocatore, cambio di dimensione, taglio/rimozione multiplayer; misurare FPS, CPU frame time e numero consultazioni per estrazione.

### OPT-002 — Evitare il baking completo per ogni controllo di fornace vanilla

**Priorità O1 | Server / CPU+allocazioni | Evidenza diretta.**

- **File:** `common/crafting/PizzaSmeltingRecipe.java` (~40–45), `PizzaSmokingRecipe.java` (~36–41), `common/pizza/PizzaBaking.java` (~71–120), `mixin/PizzaVanillaFurnaceMixin.java` (~43–65).
- **Situazione:** `AbstractFurnaceBlockEntity.serverTick` vanilla interroga la ricetta e chiama `assemble(input)` per controllare se può bruciare l'input. I due `assemble` della mod invocano ogni volta **`PizzaBaking.finish`**, che crea una pizza cotta, genera il nome, **conta e costruisce ciotole** e usa copie difensive del record `BakingResult`; il mixin invoca `finish` **di nuovo al completamento**, per consegnare i rimborsi. Il numero di ricostruzioni dipende dai tick attivi o dai controlli del forno: 600/1.200 sono i tempi di cottura previsti, **non una quantità misurata di chiamate**.
- **Possibile ottimizzazione:** separare una funzione pura `previewCookedPizza` (solo confronto di item/componenti per il risultato) dalla raccolta delle ciotole, calcolata esclusivamente a cottura riuscita. Valutare cache **per singolo forno** dell'output di anteprima con invalidazione esatta su input, componenti e recipe reload; evitare cache globale di ItemStack mutabili.
- **Rischi:** output inesatto rispetto a `canBurn`, fusione di pizze diverse, perdita dei topping, piatti sbagliati, ciotole duplicate. È vitale mantenere **esattamente gli stessi componenti** tra anteprima e output finale.
- **Test:** fornace/smoker con pizze a quattro gusti, change input durante cottura, hopper, output bloccato, zero/poche/molte ciotole, confronto `ItemStack.isSameItemSameComponents`, mondo/server; misurare allocazioni per secondo e MSPT.

### OPT-003 — Indicizzare o memorizzare in modo controllato il matching della Cheese Vat

**Priorità O1 | Server / MSPT | Evidenza diretta, non significa che la macchina sia rotta.**

- **File:** `common/block/entity/CheeseVatBlockEntity.java`, `serverTick` ~146–210, `findRecipe` ~246–277, `findNormalRecipe` ~285–364, `findWheyRecipe` ~460–529.
- **Situazione:** quando i tre ingressi sono **tutti vuoti** esiste già un corretto *early return*: **non** sostenere che una vasca vuota scorra sempre tutte le ricette. Con ingredienti **non vuoti** e nessun hint valido, le ricerche normali e/o con siero possono esaminare `recipeManager.getRecipes()`, includendo ricette estranee. Questa ricerca può ripetersi ogni tick su input statico/non riconosciuto; il controllo del recipiente può modificare la priorità della ricetta.
- **Possibile ottimizzazione:** mantenere un indice delle sole `CHEESE_VAT_TYPE` ricostruito dopo reload delle ricette; cache per combinazione immutabile di input, contenitore, stato del siero e versione del recipe manager, includendo anche **risultati negativi** quando sicuro. Conservare precedenza di siero e selezione per contenitore.
- **Rischi:** ricetta sbagliata dopo modifica inventario, output, siero o datapack; rigenerazione di risultati inappropriati. Non affidarsi all'identità dell'oggetto `ItemStack` per invalidare la cache.
- **Test:** 0 / 16 / 64 vasche con ingredienti validi e invalidi in un modpack ricco, cambio contenitore a processo avviato, abilitazione/disabilitazione siero, reload datapack, comparazione risultati; misurare `getRecipes` per tick, allocazioni e MSPT.

### OPT-004 — Ridurre lo stato temporaneo dei rendering crudo e cotto

**Priorità O1 | Client / rendering+GC | Evidenza diretta.**

- **File:** `client/render/PizzaToppingRenderer.java` (~72–140), `client/render/CookedPizzaRenderer.java` (~58–140).
- **Situazione:** ogni `extractRenderState` costruisce una lista, un array di occupazione e un nuovo `ItemStackRenderState` per ciascuna combinazione ingrediente/quarto ancora visibile. Fino a **6 topping distinti × 4 quarti = 24 pezzi** di topping su una pizza, con variazioni a seconda delle applicazioni. `List.copyOf` crea anche uno snapshot finale e c'è codice geometrico duplicato fra le due versioni.
- **Possibile ottimizzazione:** isolare un modello matematico condiviso di coordinate/rotazione (nessun cambiamento visivo) e verificare con profiler se conviene riusare risultati per composizione+maschera **immutabili**. Una cache render va invalidata quando cambiano ingredienti, quadranti, resource pack, modelli e mondo.
- **Rischi:** cache di `ItemStackRenderState` che conservi un livello o un modello obsoleto; sovrapposizione grafica o render di fette già prelevate; problemi di thread extraction/render. Prima di memorizzare **stati renderer mutabili**, verificare le regole delle API 26.2.
- **Test:** 0 / 25 / 100 pizze visibili (bianche, assortite, complete e parziali), FPS, frame time, allocazioni, cambio resource pack e chunk; confrontare screenshot pixel a pixel o side-by-side.

### OPT-005 — Non scandire tutte le posizioni del sale quando i chunk sono scaricati

**Priorità O2 | Server / MSPT | Evidenza diretta, riprende AUD-010.**

- **File:** `common/salt/SaltCauldronManager.java`, `tickLevel` ~182–277, `SaltCauldronProgressData.java`.
- **Situazione:** ogni END_LEVEL_TICK itera la mappa persistente completa e crea `BlockPos.of` prima di controllare `level.hasChunkAt(pos)`. I chunk scaricati non avanzano, ma le loro posizioni hanno ancora costo di iterazione e conversione.
- **Possibile ottimizzazione:** indice volatile di calderoni nei chunk caricati, ricostruibile su chunk load/unload, lasciando la mappa persistente come fonte autorevole. In alternativa una verifica di costo limitata sui chunk attivi, senza introdurre tick *saltati* per calderoni validi.
- **Rischi:** salt progress persi/doppi, chunk non registrati, avanzamento a chunk scaricato, modifica del tempo di 12.000 tick e dei blocchi sopra il calderone.
- **Test:** 0 / 100 / 1.000 calderoni in 1, molti chunk caricati e molti scaricati; verificare tempo utile, pausa meteo/notte e riavvio. Profilare prima di realizzare due registri paralleli.

### OPT-006 — Evitare la creazione di ItemStack e input ricette nella stagionatura a ogni tick

**Priorità O2 | Server / GC e recipe lookup | Evidenza diretta.**

- **File:** `common/block/entity/CheeseHookBlockEntity.java`, `requiredTicks` ~311–358; `common/aging/HangingCheeseType.java` ~100–120; `CheeseAgingRackBlockEntity.java` ~145–228; `AgingCheeseBlockEntity.java` ~55–90.
- **Situazione:** con prodotti in lavorazione ogni tick può costruire `new SingleRecipeInput(cheeseType.stack())` (o `freshStack()`). I metodi `stack()` degli enum costruiscono nuovi `ItemStack`. Inoltre sono ripetute ricerche `quickCheck.getRecipeFor` (con una *cache* vanilla già in uso; non è una scansione completa garantita).
- **Possibile ottimizzazione:** calcolare la durata recipe per tipo prodotto/slot e tenerla valida finché quel prodotto, il mondo o le ricette datapack non cambiano. Oppure misurare prima il costo delle piccole allocazioni e non intervenire se trascurabile.
- **Rischi:** ricette/datapack aggiornati senza invalidazione, progressi maturazione cambiati, comportamento sbagliato dopo recupero/riaggancio; evitare riuso di ItemStack **mutabili** condivisi.
- **Test:** 0 / 100 / 500 rack/uncini/formaggi invecchiando, cambio ricetta durante reload, chunk unload, diversi processi (stagionatura/essiccazione), MSPT e allocazioni.

### OPT-007 — Eliminare invii di aggiornamento blocco già effettuati dal BlockEntity

**Priorità O2 | Rete / multiplayer | Doppia notifica visibile nel codice.**

- **File:** `common/pizza/PizzaToppingInteractions.java`, `add` ~80–100, `remove` ~119–137; `common/block/entity/PizzaDoughBlockEntity.java`, `setComposition` ~40–58.
- **Situazione:** i due handler chiamano `pizza.setComposition(...)`, che esegue già `syncDivisionBlockState()` e `level.sendBlockUpdated(..., UPDATE_CLIENTS)`; **poi chiamano una seconda volta** `server.sendBlockUpdated` con stesso stato. C'è pertanto lavoro e potenziale traffico di update duplicato, sebbene il protocollo possa coalescere pacchetti.
- **Possibile ottimizzazione:** dopo verifica multiplayer, affidare l'invio **a un solo proprietario** (`setComposition`), togliendo l'invio redundante dai chiamanti; mantenere sincronizzazione immediata e save.
- **Rischi:** pezzi di topping che non appaiono immediatamente ai client, blockstate split/join desincronizzati. Non confondere una notifica duplicata col `setBlock` necessario quando cambia il blockstate.
- **Test:** ascoltare/campionare `sendBlockUpdated` per un clic, 2 client, taglio/ricongiungimento, aggiunta/rimozione, chunk reload, FPS e traffico byte/s.

### OPT-008 — Ridurre l'impatto del mixin globale `Level.setBlock` per il sale

**Priorità O2 | Server / hook globale | Evidenza diretta, impatto da misurare.**

- **File:** `mixin/LevelMixin.java`, ~23–67.
- **Situazione:** un `@Inject(at = RETURN)` è applicato a **ogni** `Level.setBlock(...)`, non solo ai calderoni; il corpo esce subito se la sostituzione non è riuscita o non è un livello server e poi confronta i tipi di calderone. L'attuale guardia rapida è un pregio, ma l'intercettazione su una chiamata molto comune può pesare sotto alti volumi di block update.
- **Possibile ottimizzazione:** confrontare nel profiler il costo dell'hook con una registrazione più circoscritta tramite eventi o osservazione di stati di calderoni, verificando disponibilità e compatibilità con mod esterne. **Non** rimuoverlo senza un meccanismo equivalente per avviare/azzerare il tracking.
- **Rischi:** evaporazione non attivata su placement/change di calderoni vanilla o modded, avanzamento non azzerato dopo la rimozione.
- **Test:** carico con molti setBlock da costruzioni/redstone/worldgen, riempimento/svuotamento calderoni con bucket, dispenser e mod di automazione, reload chunks.

### OPT-009 — Ridurre i match shapeless e gli array intermedi nella Cheese Vat

**Priorità O2 | CPU/GC | Approfondimento distinto da OPT-003.**

- **File:** `common/crafting/CheeseVatRecipe.java`, `findMatchingIngredientSlots` ~130–212; `CheeseVatBlockEntity.java`, `canSupplyWhey`, `canCook` ~575–651 e `finishCooking` ~725–755.
- **Situazione:** per ogni controllo di una ricetta compatibile, `findMatchingIngredientSlots` alloca `int[] matchedSlots` e `boolean[] usedSlots` e tenta un piccolo backtracking fra **massimo tre** slot. Questi controlli possono ripetersi in selezione ricetta, controllo siero, `canCook` e `finishCooking`. Il backtracking in sé è limitato e corretto; il fattore principale è **la frequenza delle chiamate**, non la complessità delle tre permutazioni.
- **Possibile ottimizzazione:** calcolare una struttura temporanea per il singolo tick (ricetta scelta, slot abbinati, siero esplicito o dal tank, template output) e riutilizzarla nella validazione/commit **soltanto se** non sono stati modificati input, serbatoio, contenitori o output nel frattempo.
- **Rischi:** produzione con risorse insufficienti, perdita di ingredienti, duplicazione siero o packaging, bypass delle verifiche atomiche. Precedenza assoluta alla correttezza.
- **Test:** due ricette con stessi ingredienti ma contenitori diversi, bottle+bucket siero contemporanei, stack pieni, output incompatibile, cambio input all'ultimo tick, confronto con risultati attuali.

### OPT-010 — Consolidare la consegna dei contenitori dei diversi forni

**Priorità O2 | Manutenibilità (quasi nessun guadagno CPU atteso) | Riprende AUD-007.**

- **File:** `common/block/entity/PizzaOvenBlockEntity.java` ~175–215 e `common/pizza/PizzaFurnaceRemainders.java` ~20–56.
- **Situazione:** due implementazioni pressoché identiche eseguono lookup dell'hopper, ricerca slot, inserimento ciotole e drop del resto.
- **Possibile ottimizzazione:** una piccola utility comune se i test confermano che entrambi i forni devono avere **identica policy**; mantenere separati i momenti di commit per evitare doppio rimborso.
- **Test:** hopper vuoto, mezzo pieno, pieno, staccato durante la cottura; output pieno; quantità multi-ciotola. Prima di toccare una funzionalità già approvata, verificare che il refactoring valga il rischio.

### OPT-011 — Evitare ricalcoli non necessari dei nomi dinamici negli elenchi/tooltip

**Priorità O3 | Client / allocazioni piccole | Beneficio da misurare, non prioritario.**

- **File:** `common/item/PizzaNamedItem.java` ~26–50, `common/pizza/PizzaDisplayNames.java` ~80–225.
- **Situazione:** `getName` e tooltip ricalcolano insiemi di ingredienti per quarto; alcuni nomi costruiscono liste ordinate e componenti di testo. Gli item ricevono già `DataComponents.ITEM_NAME` quando vengono creati. Ogni pizza ha comunque soltanto **quattro quarti e al massimo sei tipi/quarto**: il costo del calcolo unitario è limitato.
- **Possibile ottimizzazione:** profilare inventari con molte pizze e tooltip aperti; usare eventualmente il nome già memorizzato solo se coerente con i dati e con il cambio lingua. Evitare cache locali obsolete quando cambiano i componenti, le ricette riconosciute o il client language.
- **Test:** bianca, rossa, Margherita, assortita, vecchi salvataggi, lingue IT/EN e nomi dinamici.

### OPT-012 — Piccole allocazioni nel bounding del modello pala

**Priorità O3 | Client / micro-ottimizzazione | Non cambiare la posa approvata.**

- **File:** `client/render/PizzaPeelItemModel.java`, `CargoRenderer` ~180–192.
- **Situazione:** un metodo di bounding usa tre piccoli array temporanei `new float[]` per calcolare otto angoli. Per un singolo item la differenza è presumibilmente minima; non è un hotspot misurato.
- **Possibile ottimizzazione:** eliminare queste allocazioni soltanto se il profiler le mostra significative, mantenendo identici i valori e le matrici.
- **Test:** prima/terza persona, ambedue le mani, pala carica/vuota, salvataggio e rendering della pizza. **Nessuna modifica al modello/texture già sistemati dall'autore.**

### OPT-013 — Unificare solo la logica grafica comune delle categorie JEI

**Priorità O3 | Manutenibilità, non performance in-game misurabile.**

- **File:** `integration/jei/CheeseAgingRecipeCategory.java`, `HangingAgingDryingRecipeCategory.java`; `ItaliansDelightJeiPlugin.java`.
- **Situazione:** le categorie stagionatura su rack e uncino condividono dimensioni, coordinate degli slot, orologio e parte della grafica, ma presentano semantiche e icone differenti. Non sono callback a ogni tick server.
- **Possibile ottimizzazione:** helper **solo per la presentazione comune** e costanti riutilizzabili, mantenendo categorie/sblocchi invariati. Non cambiare gli input/risultati dei recipe holder.
- **Test:** layout JEI e tooltip del rack e dell'uncino, ricette formaggi e salumi, lingue IT/EN, caricamento e reload ricette.

### OPT-014 — Generare le varianti ripetitive del crafting degli aging rack

**Priorità O3 | Facilità di manutenzione, nessun beneficio significativo per FPS/MSPT.**

- **File:** `data/italiansdelight/recipe/crafting/*_cheese_aging_rack.json` (12 varianti per legno).
- **Situazione:** ricette distinte per ciascun tipo di legno mantengono gli ID/output corretti ma ripetono un pattern simile.
- **Possibile ottimizzazione:** uno script di **generazione dei file durante lo sviluppo** con assert sugli ID; continuare a distribuire gli stessi JSON nel modpack. Non sostituirli con un output generico che cancelli le varianti del rack.
- **Test:** confronto JSON prodotti, file mancanti/duplicati e anteprime JEI prima/dopo.

### OPT-015 — Evitare «ottimizzazioni» per controlli già brevi o necessari

**Priorità O3 | Interventi sconsigliati senza profiling.**

- **File:** `common/pizza/PizzaCompositionOperations.java`, `PizzaQuarterSelection.java`, `PizzaSliceFactory.java`, `PizzaToppingMappings.java`, `common/block/WildBasilBlock.java`, `common/item/PizzaPeelItem.java`, `CheeseVatBlockEntity.java`.
- **Situazione:** le operazioni su composizioni pizza sono limitate a 4 quarti e 6 ingredienti/quarto; il mapping delle equivalenze è costruito **a reload datapack**, non a ogni clic; la bonemeal del basilico scansiona soltanto in risposta all'uso; `syncCombatAttributes` della pala effettua `set` **solo quando necessario**; i ticker marcano `setChanged` per consentire il salvataggio del progresso.
- **Indicazione:** non togliere validazioni, copie protettive, scritture dei progressi, controlli di inventario o proprietà di sicurezza soltanto per ridurre una manciata di istruzioni. Ogni eventuale cache di stato deve prevedere invalidazione, sincronizzazione e persistenza.

## 4. Aspetti già efficienti da mantenere

1. `PizzaToppingMappings` usa lookup runtime per item su una mappa immutabile, ricalcolata durante il reload datapack; non scansiona `c:` tag o file a ogni clic.
2. La `CheeseVat` evita già la scansione globale delle ricette con i **tre ingredient slot completamente vuoti** e usa un hint della ricetta corrente: non rimuovere queste scorciatoie.
3. `CheeseAgingRackBlock` ha precomputato il set di shape e aggiorna lo stato visivo in modo condizionale; non ricalcolare hitbox a ogni frame.
4. `PizzaComposition` è immutabile e preserva l'item originale e la cronologia delle applicazioni; i suoi vincoli impediscono errori di duplicazione più costosi di qualunque micro-ottimizzazione.
5. `PizzaDivisionSync` usa una coda one-shot anziché un ticker per ogni impasto dopo il caricamento. 
6. `PizzaOvenBlockEntity` cambia lo stato LIT del blocco soltanto alla transizione e non ad ogni tick.
7. `SaltCauldronManager` evita il force-load dei chunk e controlla il clima della dimensione una volta per tick, non per calderone.
8. Il client render già separa extraction/submit senza condividere nel submit riferimenti live a Level e BlockEntity.
9. Registi statici, item groups e documentazione vengono costruiti una volta: le loro dimensioni non sono, di per sé, un motivo per modificare il gameplay.

## 5. Piano proposto per un'eventuale fase di ottimizzazione (non avviata)

### Fase A — Misure e baseline, senza modificare gameplay
- Completare il build e i test funzionali previsti nel precedente audit. In particolare confermare le nuove fornaci 26.2 prima di qualunque intervento ottimizzativo sul mixin.
- Registrare **MSPT medio/P95, FPS, frame time, allocazioni/sec e pause GC**, con stesso seed, stessa distanza di rendering, impostazioni video, hardware e modlist. Specificare esplicitamente quando un caso non è riproducibile.
- Scenari ripetibili: 0/25/100 pizze visibili; 0/16/64 Cheese Vat; 0/100/500 stagionature in chunk caricati; 0/100/1.000 calderoni caricati e scaricati; 0/16/64 fornaci con pizze personalizzate.
- Considerare client e server dedicato separatamente; non dedurre un miglioramento FPS da una riduzione MSPT o viceversa.

### Fase B — Interventi con obiettivo misurabile
1. **OPT-001** (overlay): numero di consultazioni del mondo per estrazione e FPS con l'oggetto in mano.
2. **OPT-002** (assemble forni): invocazioni, `ItemStack` temporanei e allocazioni/secondo; preservazione delle componenti.
3. **OPT-003 / OPT-009** (Cheese Vat): costi dei matcher e ricerche ripetute con input non vuoti.
4. **OPT-004** (renderer): stati item creati per frame e frame time con molte pizze.
5. Valutare **OPT-005 / OPT-006 / OPT-008** soltanto se emergono hot spot server reali; **OPT-007** è un refactoring circoscritto da collaudare in multiplayer.

### Fase C — Refactoring opzionale
Consolidamento helper dei contenitori e categorie JEI, pulizia generatori delle ricette e documentazione, senza cambi ai dati pubblici o ID di registrazione.

## 6. Regressioni vietate

- Nessuna perdita/duplicazione di ingredienti o ciotole; stessi quarti, stessi nomi, stessa nutrizione.
- `raw_pizza` e `cooked_pizza` conservano i componenti definiti dal contratto del progetto; una pizza non può fondersi con un'altra avente dati diversi nello slot di output.
- Fornaci, smoker e Pizza Oven mantengono tempi, carburanti, gestione hopper e persistenza già approvati.
- La pizza peel mantiene **l'esatta posa corretta dall'autore**, il modello/UV/texture e gli attributi solo da vuota.
- Maturazione e stagionatura avanzano per i medesimi tick a chunk caricato, rispettando recipe reload, tempi e tipi di prodotto.
- Calderone: 12.000 tick validi, cielo completamente libero, notte/meteo in pausa, chunk scaricato in pausa, rimozione acqua reset.
- Compatibilità server dedicato e multiplayer, cambio lingua IT/EN, vecchi salvataggi e datapack equivalences.
- Non rinominare ID item/blocchi/ricette, non cambiare modelli/texture e non introdurre nuovo comportamento gameplay durante un intervento dichiarato «solo performance».

## 7. Decisione

**Nessuna ottimizzazione implementata.** L'ordine suggerito serve a identificare i guadagni reali tramite misure prima di intervenire. Il documento può essere usato come backlog: aggiornare la voce a **misurata**, **approvata**, **implementata** o **testata** soltanto dopo i passaggi effettivi.

_Questo audit integra, e non sostituisce, [CODE_AUDIT_2026-10-19.md](CODE_AUDIT_2026-10-19.md), [KNOWN_BUGS.md](KNOWN_BUGS.md) e [PLANNED_IMPROVEMENTS.md](PLANNED_IMPROVEMENTS.md)._

## Aggiornamento dopo la revisione (22 ottobre 2026)

- **OPT-002 — implementato, da misurare:** `PizzaSmeltingRecipe.assemble` e `PizzaSmokingRecipe.assemble` chiamano `previewCookedPizza` senza enumerare/restituire ciotole. I rimborsi per applicazione vengono conteggiati soltanto al commit di `PizzaBaking.finish`. Niente cache cross-tick di ItemStack mutabili; prima/dopo per tempo di cottura, output e bowl invariati.
- **OPT-007 — implementato, da verificare in multiplayer:** rimossi i due invii `server.sendBlockUpdated` ridondanti dagli handler di aggiunta/rimozione: `PizzaDoughBlockEntity.setComposition` resta il proprietario unico della sincronizzazione.
- Le altre OPT richiedono profiling o collaudi mirati; **non** compromettere conservazione degli item, interazioni già approvate o tempi di cottura per miglioramenti non misurati.

- **OPT-010 — implementato, da verificare in-game:** unificato il trasferimento dei contenitori in `PizzaFurnaceRemainders.deliver`, richiamato al commit dal Pizza Oven e dal mixin di fornace/smoker. Identici controlli `ItemStack.isSameItemSameComponents`, limiti stack, hopper sottostante e drop dell'overflow, senza cambiare i tre punti di commit.
- **OPT-012 — implementato, piccolo guadagno non misurato:** `CargoRenderer.getExtents` evita tre array `float[]` temporanei, iterando sugli stessi otto vertici, senza modificare i limiti spaziali o la posa già approvata della pala. Confrontare visivamente la pala vuota/piena.
