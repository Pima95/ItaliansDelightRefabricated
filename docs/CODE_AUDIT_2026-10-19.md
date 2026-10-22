# Revisione statica del codice — Italian's Delight Refabricated

**Data:** 19 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Snapshot GitHub esaminato:** `39d5239edf7a90413ee6a2413013fcbf09ea4eea`  
**Stato:** **solo analisi, nessuna correzione eseguita**. Questo file è l'unico nuovo artefatto della revisione.

## 1. Scopo, copertura e limiti

Revisione in sola lettura orientata a **correttezza, perdite/duplicazioni, sicurezza delle transazioni sugli ItemStack, compatibilità Minecraft 26.2/Fabric/Farmer's Delight Refabricated, prestazioni, integrazione JEI, manutenzione e verificabilità**.

- Albero GitHub rilevato: **862 file tracciati**, **92 sorgenti Java**, **94 JSON di ricette** di Italian's Delight, 24 blockstate e 182 modelli (questi ultimi **censiti**, non tutti revisionati singolarmente), 2 file di localizzazione.
- Esaminati i sorgenti Java di registri/inizializzazione, blocchi ed entità, sistemi pizza/pala/fornaci, Cheese Vat, stagionatura/formaggi/salumi, evaporazione del sale, colture, renderer, mixin e integrazione JEI. Approfondite le funzioni con più rischi di perdita o duplicazione.
- Tutti i **94 JSON di ricette** sono stati letti/analizzati come JSON; non sono emersi file malformati nel controllo sintattico. Non sono stati verificati semanticamente tutti gli ID di item delle mod opzionali o tutte le regole di recipe matching di Minecraft.
- `en_us.json` e `it_it.json`: **182 chiavi ciascuno**, nessuna chiave presente in una lingua e assente nell'altra. Mixin dichiarati: `LevelMixin`, `SheepMixin`, `PizzaVanillaFurnaceMixin`, più `client.CheeseHookParticlesMixin`.
- Controllo incrociato di alcune API tramite sorgenti decompilati **Minecraft 26.2 snapshot 2** (non equivalgono a una compilazione verificata della release installata dall'autore). La firma di `Item.inventoryTick`, `ItemAttributeModifiers.EMPTY` e la chiamata `AbstractFurnaceBlockEntity.burn` risultano compatibili a livello di fonte; questo non dimostra che il mixin verrà applicato.
- **NON eseguiti:** `gradlew build`, `runClient`, JEI nel gioco, server dedicato, profiler, test di runtime o validazione visiva totale degli asset. Quindi i possibili difetti runtime sono classificati come **rischi da verificare**, non dichiarati guasti confermati.
- Questa revisione non modifica il codice, i JSON, i modelli, le texture, i dati del mondo, le ricette né i documenti precedenti.

### Come interpretare le priorità

- **P1 — alta:** possibile perdita di dati, regressione sostanziale o incompatibilità che blocca l'avvio; da verificare prima di una release.
- **P2 — media:** difetto funzionale circoscritto, rischio di compatibilità, ottimizzazione utile o debito tecnico concreto.
- **P3 — bassa:** pulizia, documentazione, attività già pianificata o controllo preventivo.

La voce **`OSSERVATO NEL CODICE`** indica un comportamento concretamente presente nei sorgenti. **`DA CONFERMARE IN GIOCO`** segnala che il danno effettivo è ipotetico.

## 2. Problemi e rischi prioritari

### AUD-001 — La cottura può eliminare componenti personalizzati della pizza cruda

**Priorità:** P1 · **Tipo:** comportamento osservato nel codice, impatto da provare con item personalizzati.  
**File:** `src/main/java/dev/italiansdelight/common/pizza/PizzaBaking.java` (metodo `finish`, circa righe 71–84); chiamato da `PizzaOvenBlockEntity`, `PizzaSmeltingRecipe`, `PizzaSmokingRecipe`.

**Evidenza:** viene costruito `new ItemStack(ModItems.COOKED_PIZZA)`, viene copiato soltanto `PIZZA_CONTENTS` e viene impostato un nome tramite `PizzaDisplayNames.nameWholeStack`. Non c'è trasferimento deliberato di altre componenti dal precedente `rawPizza`, come `CUSTOM_NAME`, `CUSTOM_DATA`, lore o componenti aggiunte da mod/datapack. **I topping strutturati risultano invece esplicitamente copiati**: non confondere i due casi.

**Rischio:** una pizza rinominata con un'incudine o con dati aggiuntivi potrebbe perdere quelle personalizzazioni durante la cottura in qualsiasi forno.

**Verifica:** attribuire un nome personalizzato e, se possibile, una componente custom a `raw_pizza`, cuocerla nei tre forni e confrontare le componenti prima/dopo. **Proposta, non applicata:** definire quali componenti debbano sopravvivere; copiare solo quelle consentite, escludendo gli attributi dipendenti dal vecchio tipo di item.

### AUD-002 — Mozzarella intera ancora gestita dal percorso legacy della pizza

**Priorità:** P1 · **Tipo:** bug preesistente già segnalato; nessuna nuova diagnosi runtime.  
**File:** `src/main/java/dev/italiansdelight/common/pizza/PizzaDoughPlacement.java` (circa righe 139–165); `PizzaToppingInteractions.java` (recupero flag legacy); `PizzaDoughBlock.java`.

**Evidenza:** dopo il nuovo percorso degli ingredienti canonici esiste ancora il ramo `held.is(ModItems.MOZZARELLA)` che imposta `PizzaDoughBlock.MOZZARELLA`. Contrasta con la whitelist prevista per la pizza che accetta la **mozzarella a fette**, non l'intera. È già descritto in [KNOWN_BUGS.md](KNOWN_BUGS.md), **BUG-002**, con l'effetto grafico invisibile confermato dall'autore.

**Verifica:** applicazione di mozzarella intera, corretta restituzione e caricamento di pizze legacy già salvate. **Proposta, non applicata:** disabilitare i nuovi inserimenti senza perdere la possibilità di leggere/rimuovere e recuperare le vecchie applicazioni.

### AUD-003 — Nuovo mixin sulle fornaci vanilla non ancora provato al caricamento

**Priorità:** P1 · **Tipo:** rischio di compatibilità, NON un errore accertato.  
**File:** `src/main/java/dev/italiansdelight/mixin/PizzaVanillaFurnaceMixin.java` (~29–66); `src/main/resources/italiansdelight.mixins.json`; `ModRecipes.java`.

**Evidenza:** un `@WrapOperation` intercetta una chiamata privata `AbstractFurnaceBlockEntity.burn(...)` e cattura `ServerLevel` / `BlockPos` con `@Local(argsOnly = true)`. Il punto di chiamata appare compatibile con le sorgenti vanilla 26.2 snapshot 2, ma una corrispondenza testuale **non equivale** a compilazione/trasformazione Mixin verificata.

**Rischio:** firma, remapping o cattura dei parametri non compatibili con il runtime reale possono impedire l'avvio o il funzionamento della cottura. In particolare occorre verificare che il callback si attivi solo al **commit** e che non sia chiamato due volte.

**Verifica:** `clean build`, avvio client **e server dedicato**, lettura dei log Mixin, 1 cottura normale e 1 pizza per ciascuna fornace, blocco dell'output e controllo dei rimborsi. **Proposta:** soltanto dopo una prova negativa, correggere la firma/strategia d'iniezione; non sostituire l'intero sistema senza necessità.

### AUD-004 — Ricette dinamiche: la visualizzazione JEI/recipe book può mostrare il risultato generico

**Priorità:** P2 · **Tipo:** limitazione osservata a livello di API, da verificare nel client.  
**File:** `PizzaSmeltingRecipe.java`, `PizzaSmokingRecipe.java`; `data/italiansdelight/recipe/pizza_smelting.json`, `pizza_smoking.json`.

**Evidenza:** `assemble(SingleRecipeInput)` costruisce la pizza con i dati reali, mentre il `display()` ereditato da `AbstractCookingRecipe` utilizza il `result` statico del JSON. Quindi l'anteprima delle ricette potrebbe apparire come una pizza cotta generica, anche se l'output effettivo preserva le informazioni.

**Verifica:** JEI e ricettario con pizze Margherita e assortita; confrontare **anteprima** ed **item realmente estratto**. **Proposta:** documentare una rappresentazione generica o un'anteprima dedicata, senza creare centinaia di ricette per gusti dinamici.

### AUD-005 — Equivalenze da datapack potenzialmente non riconosciute dall'interazione client

**Priorità:** P2 · **Tipo:** rischio da testare in multiplayer, non bug dimostrato.  
**File:** `PizzaDoughPlacement.java` (~116–131); `PizzaToppingMappings.java` (~35–65); `PizzaToppingInteractions.java`.

**Evidenza:** il controllo preliminare **client** accetta soltanto `PizzaToppingCatalog.findNativeItem(...)`; il controllo **server** usa anche `PizzaToppingMappings.find(...)` che può includere equivalenze specifiche provenienti dai datapack. Gli insiemi possono differire per un ingrediente di altra mod.

**Rischio:** la risposta al clic, l'anteprima e/o l'intercettazione del client potrebbero non corrispondere alla decisione del server.

**Verifica:** installare un item sostituto approvato con mapping JSON, provarlo sul server dedicato e nel mondo singleplayer, confrontando con item non ammessi. **Proposta:** definire un percorso client coerente/sincronizzato senza autorizzare tag generici né fidarsi del client per il consumo.

### AUD-006 — La pala reimposta tutti i modificatori d'attributo personalizzati a ogni controllo

**Priorità:** P2 · **Tipo:** comportamento osservato; rischio compatibilità.  
**File:** `src/main/java/dev/italiansdelight/common/item/PizzaPeelItem.java` (~54–70).

**Evidenza:** `syncCombatAttributes` sostituisce l'intero `DataComponents.ATTRIBUTE_MODIFIERS` con gli attributi fissi della padella a pala vuota o con `ItemAttributeModifiers.EMPTY` a pala carica; `inventoryTick` lo verifica periodicamente. La sincronizzazione è corretta per il requisito **«arma solo da vuota»**, ma non distingue eventuali modificatori aggiunti intenzionalmente da altri sistemi.

**Verifica:** pala con attributi speciali assegnati con comandi/mod, passaggio vuota/carica, inventario e riavvio. **Proposta:** mantenere invariata la regola attuale finché il requisito di compatibilità con attributi esterni non è definito; valutare una gestione dei soli modificatori del progetto se necessario.

### AUD-007 — Restituzione ciotole duplicata fra Pizza Oven e forni vanilla

**Priorità:** P2 · **Tipo:** ottimizzazione/manutenibilità, non bug di duplicazione osservato.  
**File:** `PizzaOvenBlockEntity.java` (`deliverRemainder` ~175–215); `PizzaFurnaceRemainders.java` (~20–56).

**Evidenza:** due implementazioni quasi uguali inseriscono gli item residui nel primo hopper sottostante e fanno cadere l'eccesso nel mondo. Regole simili gestite in due punti possono divergere in futuro.

**Verifica:** hopper vuoto/pieno/parziale, multipli di ciotole, server dedicato. **Proposta:** estrarre un'utility condivisa **solo dopo** la conferma che i due contratti di output siano identici; non modificare prematuramente la Cheese Vat.

### AUD-008 — Render dei topping su blocco: modello provvisorio e costi per ogni pezzo

**Priorità:** P2 · **Tipo:** miglioramento grafico e prestazionale già previsto, non crash.  
**File:** `client/render/PizzaToppingRenderer.java`, `client/render/CookedPizzaRenderer.java`, `client/render/PizzaDivisionOverlay.java`.

**Evidenza:** vengono risolti e renderizzati diversi ItemStack per ogni applicazione e quarto. Le texture dei topping in 2.5D, con salse piatte e distribuzione naturale, risultano ancora una fase pianificata della [roadmap pizza](pizza-1.0.0/ROADMAP.md).

**Verifica:** scena con molte pizze e più topping in un singolo chunk, confronto FPS/tempi di rendering prima e dopo una futura ottimizzazione. **Proposta:** introdurre eventuali cache degli stati grafici **per dati invariati**, mantenendo aggiornamenti di chunk, orientamento, rimozione e cambi dei resource pack. Non toccare le texture già approvate senza consenso.

### AUD-009 — Ricerca ricette della Cheese Vat: scansioni complete di fallback

**Priorità:** P2 · **Tipo:** costo osservato nel codice, impatto non profilato.  
**File:** `common/block/entity/CheeseVatBlockEntity.java` (~300–530), `serverTick` (~145–210).

**Evidenza:** durante la ricerca normale e delle ricette con siero vengono percorsi `recipeManager.getRecipes()` quando non è riutilizzabile una ricetta esistente. Anche vasche ferme o con combinazioni non valide possono ripetere tentativi a ogni tick.

**Verifica:** misurare i tick server con molte Cheese Vat, con/senza ingredienti e con modpack con grandi quantità di ricette. **Proposta:** indicizzazione per tipo di ricetta, `RecipeManager` dedicato ove possibile o cache del risultato negativo invalidata al mutare di input/tank/datapack. Preservare la priorità attuale fra ricette con/senza recipiente.

### AUD-010 — Evaporazione sale: costo lineare nel totale dei calderoni tracciati

**Priorità:** P2/P3 · **Tipo:** possibile ottimizzazione sotto carico.  
**File:** `common/salt/SaltCauldronManager.java` (`tickLevel` ~175–277); `SaltCauldronProgressData.java`.

**Evidenza:** a ogni tick viene iterata tutta la mappa dei calderoni conosciuti, anche se per i chunk non caricati l'avanzamento viene semplicemente saltato. Per ogni calderone attivo vengono controllati stato e `WORLD_SURFACE`.

**Verifica:** confrontare tick time con molti calderoni in chunk caricati, molti in chunk scaricati e dopo riavvii. **Proposta:** distinguere set dei chunk caricati dal registro persistente solo se il profiling rileva un problema; non alterare la regola dei **12.000 tick di luce utile**.

### AUD-011 — Persistenza delle pizze: validazioni strette e piano di migrazione

**Priorità:** P2 · **Tipo:** robustezza e compatibilità futura.  
**File:** `PizzaComposition.java` (~44–108), `PizzaToppingApplication.java` (~28–53), `PizzaItemData.java`.

**Evidenza:** i costruttori rifiutano versioni non pari a `FORMAT_VERSION=1`, maschere/sequence invalide, ID canonici duplicati. È una protezione positiva contro corruzioni ma va abbinata a migrazioni esplicite quando cambierà il formato. Esistono già note per `bake_progress` legacy e ricette storiche.

**Verifica:** copia di un mondo vecchio, pizze con vecchi componenti, rimozione temporanea di mod facoltative, serializzazione e reload dei dati. **Proposta:** test di migrazione per versioni successive, senza indebolire l'attuale validazione.

### AUD-012 — Nessuna suite Java di regressione o CI visibile nel ramo

**Priorità:** P2 · **Tipo:** lacuna verificata nella struttura del repository.  
**File/area:** `src/test/` **assente**; `.github/workflows/` **assente** nel commit esaminato.

**Evidenza:** non risultano test JUnit/Gradle versionati né una workflow GitHub Actions che compili Java 25; diversi comportamenti critici sono verificati tramite checklist manuali. Per i sistemi transazionali è una protezione limitata contro future regressioni.

**Proposta:** introdurre gradualmente test puri per `PizzaCompositionOperations`, `PizzaQuarterSelection`, `PizzaBaking`, codec dei componenti, inventari e ricette; poi una CI minima con `gradlew build`. **Non aggiunti in questa revisione**.

### AUD-013 — Documentazione storica della Pizza Update contiene stati ormai superati

**Priorità:** P3 · **Tipo:** documentazione/manutenibilità.  
**File:** `docs/pizza-1.0.0/ROADMAP.md`, `docs/pizza-1.0.0/README.md`, precedenti checklist di test.

**Evidenza:** la roadmap conserva cronologie di sviluppo che indicano in punti diversi pala/arma/ricetta o nomi ancora «da decidere», pur riportando successivamente l'implementazione. Sono annotazioni storiche utili ma possono essere confuse con lo stato attuale.

**Proposta:** aggiungere in futuro una pagina `CURRENT_STATUS.md` breve (implementato / testato / pendente) e conservare le cronologie come archivio, senza cancellarle.

### AUD-014 — Gestione JEI usa il livello client presente al momento della registrazione

**Priorità:** P3 · **Tipo:** controllo preventivo, non crash verificato.  
**File:** `integration/jei/CheeseAgingJeiRecipes.java` (~31–47); `CheeseVatJeiRecipes.java` (~21–28).

**Evidenza:** il costruttore recupera `Minecraft.getInstance().level` e lancia `NullPointerException` se è nullo. Nelle normali fasi di sincronizzazione JEI la registrazione può avvenire con mondo presente; verificare però uscita dal mondo, rejoin, caricamento ricette e transizioni.

**Proposta:** inizializzazione differita o gestione esplicita di «nessun mondo» qualora si riproduca un problema. Non alterare categorie JEI già funzionanti.

## 3. Bug e miglioramenti già registrati (NON duplicare come nuove scoperte)

- **[KNOWN_BUGS.md — BUG-001](KNOWN_BUGS.md):** Scamorza e Provolone posizionabili a terra in situazioni non desiderate. Resta aperto; verificare le interazioni con l'uncino prima di intervenire.
- **[KNOWN_BUGS.md — BUG-002](KNOWN_BUGS.md):** mozzarella intera applicabile ma invisibile sulla pizza; richiamato come **AUD-002**.
- **[PLANNED_IMPROVEMENTS.md](PLANNED_IMPROVEMENTS.md):** Cardoon selvatico troppo frequente, versione coltivabile, ampliamento biomi di Cardoon/basilico e revisione audio Hanging Hook. Sono richieste future, non nuove anomalie emerse da questo audit.
- **[Roadmap pizza](pizza-1.0.0/ROADMAP.md):** fette di carota/patata, grafica definitiva topping, crafting libero, nuove ricette JEI e release 1.0.0. Sono parti incomplete o pianificate, **non bug da correggere alla cieca**.

## 4. Aspetti positivi da preservare

1. Le operazioni sui topping separano le validazioni dal consumo effettivo dell'ItemStack e conservano un originale per applicazione, con rimborso soltanto a rimozione totale; attenzione a non introdurre drop o duplicazioni durante le future modifiche.
2. `PizzaComposition` è immutabile e valida maschere, sequenze, numero di tipi per quarto e identità canoniche; le equivalenze esterne usano mapping per ID esatto anziché tag troppo generici.
3. Il Pizza Oven conserva l'avanzamento nel BlockEntity, non sull'item, e i suoi output sono transazioni a successo; mantenere questa semantica nei forni vanilla.
4. Lo sblocco della ricetta dell'uncino usa ora `minecraft:iron_chain`, e l'autore ha confermato il funzionamento; **non reintrodurre `minecraft:chain`**.
5. L'integrazione del Pizza Peel separa il render dei modelli dai dati del cargo. **Non toccare** la posa regolata manualmente dall'autore durante le ottimizzazioni.
6. Nessun disallineamento di chiavi fra le 182 traduzioni inglesi e italiane.

## 5. Ordine di intervento consigliato (nessun intervento eseguito)

1. **Prima di sviluppare altro:** compilare e testare `PizzaVanillaFurnaceMixin` in client e server dedicato (**AUD-003**), poi testare la conservazione di dati extra con le tre cotture (**AUD-001**).
2. Correggere il bug già aperto della mozzarella intera **soltanto con una strategia di migrazione legacy** (**AUD-002**).
3. Verificare in multiplayer i topping sostitutivi e l'anteprima JEI (**AUD-004**, **AUD-005**) prima di estendere la lista di ingredienti.
4. Solo dopo, profilare `CheeseVatBlockEntity`, renderer e sale (**AUD-008–010**); evitare ottimizzazioni speculative che cambino il gameplay.
5. Programmare test automatici/CI e riordinare lo stato documentale (**AUD-012**, **AUD-013**).

### Checklist minima prima della PR su main

- [ ] Build Java 25 su Minecraft 26.2 senza errori Mixin o serializer.
- [ ] Avvio client **e** server dedicato; nessun crash al caricamento JEI.
- [ ] Pizza assortita: ingredienti, quarti, nome e componenti extra conservati in Pizza Oven, fornace e smoker.
- [ ] Nessuna duplicazione/perdita di ciotole, topping o pizza con input/output/hopper pieni, clic simultanei, reload chunk e logout.
- [ ] Pala vuota con attributi da padella, pala carica senza bonus; pickup/drop/forno in entrambe le mani; nessuna regressione della posa approvata.
- [ ] Cheese Vat, stagionatura, uncino, siero, salumi, latte di pecora, sale, Cardoon e basilico verificati almeno con smoke test.
- [ ] Le traduzioni IT/EN, i crafting e JEI funzionano con le rispettive dipendenze.
- [ ] Nessuna modifica involontaria agli asset e texture approvati; PR solo dopo i test.

---

**Nota finale:** questo è un audit **statico** svolto a partire dallo snapshot di GitHub indicato. Le priorità sono valutazioni di rischio e non attestano che il gioco attuale sia difettoso. **Non è stata eseguita alcuna correzione, refactoring, ottimizzazione o modifica di ricette/texture in conseguenza di questo documento.**

## Aggiornamento applicato dopo l'audit (22 ottobre 2026)

- **AUD-001 — implementato, test in-game pendente:** `PizzaBaking.previewCookedPizza` copia i componenti **CUSTOM_NAME, LORE e CUSTOM_DATA** dall'item crudo al nuovo item cotto; ricrea invece `ITEM_NAME` e `ITEM_MODEL` dal gusto corretto, senza copiare componenti di gameplay del tipo crudo. Condiviso da Pizza Oven, Smoker e Furnace. Per eventuali componenti custom di altre mod diversi da questi tre occorre una policy specifica, non copiarli indiscriminatamente.
- **AUD-002 — precedentemente corretto:** la mozzarella intera non è più un topping applicabile; il flag legacy sopravvive per i vecchi mondi.
- **AUD-012 — parzialmente superato:** esiste già una workflow CI Java 25 con numerosi audit statici; manca ancora una vera suite di test Java runtime / server dedicato. Non considerare chiuso l'intero AUD-012.
- **AUD-003, 004, 005, 006, 008–011, 013, 014:** restano da completare / verificare secondo priorità. I test di runtime non possono essere marcati superati dalla compilazione.

- **AUD-014 — gestione difensiva implementata, test JEI pendente:** `CheeseVatJeiRecipes` e `CheeseAgingJeiRecipes` interrogano il `ClientLevel` nel metodo di registrazione e restituiscono una lista vuota quando il livello client è assente, al posto di lanciare `NullPointerException` o trattenere un vecchio `SynchronizedRecipes`. La registrazione dei piatti/categorie resta invariata; verificare uscita, cambio mondo, rejoin, `/reload` e apertura JEI.
