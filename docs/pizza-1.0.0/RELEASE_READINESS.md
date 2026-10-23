# Pizza Update 1.0.0 — checklist per il rilascio

**Stato:** sviluppo su `feature/pizza-1.0.0`; **non pubblicare ancora**.  
**Versione attualmente nei metadati del branch di sviluppo:** `mod_version=1.0.0` (aggiornamento autorizzato dall'autore, **non pubblicato**). L'ultima release pubblica resta 0.4.0; i test e l'approvazione per la pubblicazione rimangono necessari.  
**Target dichiarato:** Minecraft 26.2, Fabric Loader 0.20.1+, Fabric API 0.161.0+26.2, Java 25, Farmer's Delight Refabricated 26.2-3.6.29+refabricated.

## Prima del test

1. Usare **una copia di backup del mondo**, specialmente per provare un salvataggio creato con build precedenti della mod.
2. Sul branch `feature/pizza-1.0.0`, eseguire `git pull origin feature/pizza-1.0.0`, poi `.\\gradlew.bat clean build` e `.\\gradlew.bat runClient`. Su server dedicato usare la stessa build del JAR e le stesse dipendenze.
3. Registrare data, commit SHA, ambiente (singleplayer / dedicated server), log e se il risultato è stato verificato in **Survival**. Una CI verde controlla la compilazione e le invarianti statiche, **non** tutte le interazioni in Minecraft.

## Esiti recenti confermati dall'autore — 22 ottobre 2026

L'autore ha confermato in gioco il funzionamento di questi **tre** casi,
richiesti alla fine dell'ultima fase di sviluppo:

- [x] Gli spicchi dello stesso gusto si impilano fino a **16**.
- [x] Gli hopper **non estraggono la pizza cruda** dallo slot di ingresso del Pizza Oven.
- [x] Dopo il salvataggio e la riapertura del mondo, una **Pizza mista mantiene i quattro gusti**.

Il 22 ottobre 2026 l'autore ha inoltre confermato che **tutte le ultime modifiche al Cardoon, alla distribuzione delle piante, ai biomi, ai suoni dell'uncino e alla restituzione delle ciotole nel crafting funzionano**. I rispettivi test funzionali sono registrati in [CURRENT_STATUS.md](CURRENT_STATUS.md) e [PLANNED_IMPROVEMENTS.md](../PLANNED_IMPROVEMENTS.md); i test server e anti-duplicazione più approfonditi rimangono separati.

La conferma riguarda questi comportamenti specifici: **non** equivale a dichiarare
superate tutte le prove su hopper pieni, 17° spicchio, casse, multiplayer,
pale cariche, forno a metà cottura o server dedicato. Le checklist dettagliate
sottostanti restano aperte per le parti non confermate.

## Ultime verifiche JEI e caglio — 23 ottobre 2026

**Conferma dell'autore (23 ottobre 2026):** le ultime modifiche funzionano nel client. Sono quindi chiuse queste tre verifiche funzionali, ma **non** le prove non citate di JEI dopo reload, server dedicato, multiplayer o combinazioni estreme.

- [x] Le 18 pizze riconosciute dispongono delle nuove istruzioni personalizzate per stendere e condire l'impasto in JEI.
- [x] La categoria degli spicchi utilizza il layout originale Cutting Board di Farmer's Delight Refabricated.
- [x] Il siero nella ricetta JEI della Ricotta è allineato nel tank destro, dopo la correzione di 4 pixel.

- [ ] Ogni **pizza riconosciuta** (18) ha **sette pagine personalizzate** in JEI per posizionamento, stesura, divisione, topping, rimozione e raccolta. Le 126 pagine sono ricercabili partendo dal nome del gusto e mostrano gli item realmente necessari.
- [ ] La Cutting Board delle pizze usa lo stesso sfondo JEI vanilla di Farmer's Delight Refabricated, formato 117×57, ingredienti a sinistra e quattro fette in griglia 2×2 a destra.
- [ ] Nel JEI della Ricotta il whey del tank destro è **4 pixel più in basso**, senza alterare le proporzioni della barra o la GUI Cheese Vat.

- [ ] Le **18 pizze riconosciute** e i relativi **18 spicchi** compaiono come item distinti, cercabili nel pannello JEI, non soltanto dentro ricette.
- [ ] Comparsa di 18 varianti crude, 18 cotte e 18 fette, più due esempi di Pizza mista, con le texture già approvate.
- [ ] R/U su una pizza e uno spicchio **ottenuti in Survival** conduce alle giuste categorie; i quarti misti restano coerenti.
- [ ] **Unica ricetta caglio:** Cardoon selvatico/coltivato + acqua in secchio/bottiglia; pozioni non d'acqua escluse, resti restituiti esattamente una volta, 300 tick e recipiente originario conservati.

## Aggiornamento operativo del 23 ottobre 2026

L'autore ha dichiarato conclusi **il controllo Cheese Vat** (punto 1 del riepilogo) e **la revisione completa dei bug** (punto 3). Il collaudo **multiplayer non è attualmente eseguibile** e resta esplicitamente **non verificato**. Le prove dettagliate non confermate qui sotto conservano la casella vuota; non vanno scambiate con bug sicuramente presenti né con test già eseguiti.

La selezione delle ottimizzazioni pre-release è terminata: sono state conservate le implementazioni già approvate, applicata la rifinitura non invasiva OPT-011 e annotate le proposte da sottoporre a profiler nel [registro decisionale](../OPTIMIZATION_CLOSURE_2026-10-23.md). Le [note di rilascio](RELEASE_NOTES_DRAFT.md) sono pronte **come bozza**, non per la pubblicazione. Il numero di versione del JAR di sviluppo è già stato portato a 1.0.0 su richiesta dell'autore. Restano da ottenere i test obbligatori non eseguiti e l'approvazione esplicita per pubblicare.

## A. Interazioni e ricette base

- [ ] Impasto Farmer's Delight: posa su superficie valida, stesura con mano/mattarello, divisione metà/quarti e riunione; i topping rimangono sullo stesso quarto fisico.
- [ ] Quattro quarti con gusti diversi: l'intera pizza si chiama **Pizza mista**, usa l'icona Margherita e conserva un tooltip separato per quarto.
- [ ] Limite di **sei tipi canonici per quarto**; duplicati crudo/cotto o da equivalenze, applicazioni incompatibili di salsa/panna/ricotta e forme intere sono rifiutati senza consumare item.
- [ ] Rimozione Shift + mano vuota LIFO: rimborso di un item soltanto dopo l'ultima porzione della stessa applicazione originale; bowl non duplicati.
- [ ] Crafting shapeless: da un impasto + 1–6 topping diversi ottiene **una pizza uniforme**; provare due ricette con disposizione diversa della griglia e una combinazione rifiutata.
- [ ] Rottura e Pick Block di pizza cruda/condita, perdita del supporto, recupero tramite pala; verificare gli item e le componenti.

## B. Forni, fuel e hopper

- [ ] Pizza Oven: **20 s**, smoker **30 s**, furnace **60 s** a 20 TPS. Pizza completa senza bruciatura, stessi quattro quarti in output, nessuna pizza duplicata.
- [ ] Pizza Oven GUI senza pala; pala carica per inserimento rapido e pala vuota per estrazione. Forno occupato rifiuta inserimenti senza consumare la pizza caricata.
- [ ] Estrazione della pizza cruda a metà cottura: il tempo si azzera quando viene reinserita; spegnimento/riaccensione e chunk unload/save mantengono soltanto il progresso ancora presente nel forno.
- [ ] Automazione: hopper sopra = **solo pizza cruda**, sui lati = **solo combustibile**, sotto = **solo pizza cotta e secchio vuoto**; mai estrarre l'input crudo o una pizza incompleta.
- [ ] Salsa/panna/ricotta: **una ciotola per applicazione originaria**, anche quando copre più quarti. Due salse su due metà producono due ciotole, non quattro.
- [ ] Hopper sotto pieno e ciotole rinominate/con componenti personalizzati: nessuna fusione scorretta; residui espulsi una volta sola, nessuna perdita o duplicazione.
- [ ] Rompere il forno con pizza cruda, pizza cotta, fuel e contenitori: inventario recuperato una volta secondo le regole di drop vanilla.

## C. Servizio, stack e nutrizione

- [ ] Pizza cotta: clic a mano vuota preleva uno spicchio dal quarto selezionato, non consuma gli altri. Se l'inventario è pieno, nessuna sottrazione/droppaggio.
- [ ] Rottura di pizza cotta intera = pizza intera; dopo aver servito 1–3 quarti = **solo le fette rimaste**; perdita del supporto identica.
- [ ] Gli spicchi Margherita di più pizze e di quarti differenti vanno nello stesso stack **fino a 16**; il 17° apre un nuovo stack. Rossa, Bianca e Salame non si mescolano. Ripetere in cassa/hopper, con shift-click e dopo riavvio.
- [ ] Contrasto tra topping diversi, stesso topping aggiunto in ordine diverso, equivalenze crudo/cotto e mozzarella legacy; l'identità dello spicchio resta il gusto canonico.
- [ ] Bilanciamento per quarto: massimo **due ingredienti distinti per categoria**, massimo **6 fame e 6 saturazione**; Nourishment breve soltanto con fame almeno 3, nessuno per Bianca/Rossa.
- [ ] Caricare una vecchia fetta già salvata e una vecchia Pizza mista con icona custom: comportamento aggiornato nell'inventario senza perdere ingredienti; pala con pizza già caricata conserva le componenti.

## D. Compatibilità, UI e altre regressioni

- [ ] Creativa: **18 crude, 18 cotte, 18 spicchi, 2 Pizze miste**; nessuna voce base duplicata Pizza Bianca, icona scheda Margherita cotta, tutti gli altri item presenti.
- [ ] JEI: **18 gusti riconosciuti** nel Pizza Oven e nella nuova categoria Cutting Board (più una Pizza mista), crafting dinamico per i **17 gusti con topping**, guida impasto; provare lookup con R/U, bookmark, traduzioni **italiano e inglese** e texture approvate. La Bianca si prepara nel mondo, non con l'impasto da solo in crafting.
- [ ] Cutting Board Farmer's Delight: piazzare una **pizza cotta intera** sull'asse, clic con coltello idoneo, verificare **4 spicchi** con i condimenti di ciascun quarto, danno del coltello, nessun secondo output e salvataggio/load; testare anche una pizza mista. Vedi [PIZZA_JEI_CUTTING_BOARD.md](PIZZA_JEI_CUTTING_BOARD.md).
- [ ] Formaggi da uncino (Scamorza/Provolone) non piazzabili a terra; formaggi da superficie e scaffali continuano a funzionare; recupero uncino + salume/formaggio e pulizia spazio tecnico dopo rottura del supporto.
- [ ] Altro: la Cheese Vat, il sale, la coltivazione del basilico e le ricette dei salumi funzionano dopo l'aggiornamento.

## E. Server, persistenza e consegna

- [ ] Avviare un **server Fabric dedicato senza renderer client** e con le stesse versioni delle dipendenze; nessun crash/mixin client-side sul server. Eseguire [DEDICATED_SERVER_TESTS.md](DEDICATED_SERVER_TESTS.md) e conservare il log.
- [ ] Test multiplayer: due giocatori manipolano lo stesso quarto o la stessa pizza nello stesso tick; non vengono rilasciati due spicchi, recuperate due pizze con pala o rimborsati ingredienti due volte.
- [ ] Salvare e riaprire dopo una pizza a quattro quarti, pizza nel forno a metà, pala con cargo e formaggi/salumi in lavorazione; nessuna perdita, duplicazione o reset imprevisto.
- [ ] Testare vari chunk caricati/scaricati, hopper pieni e ripetuti trasferimenti; controllare log, RAM e TPS senza creare cicli di duplicazione.
- [ ] Raccogliere le verifiche e segnalazioni in `docs/KNOWN_BUGS.md`. Per ogni bug: riproduzione, versione, risultato atteso/ottenuto e riferimento al commit di fix.
- [ ] **Solo con approvazione finale:** verificare che `mod_version=1.0.0` e i metadati del JAR siano corretti (già impostati nel branch di sviluppo), rivedere `CHANGELOG.md`/README e la PR draft #9 verso `main`, eseguire la build finale, poi creare release e pubblicare CurseForge/Modrinth.

## Risultato e criteri di blocco

Una compilazione verde non è sufficiente per rilasciare la 1.0.0. Bloccano la pubblicazione: crash, perdita/duplicazione di item, incompatibilità su server dedicato, dati di pizza non persistenti, hopper che estraggono input crudo, stack di spicchi che si uniscono a gusti diversi e assenza di elementi essenziali in Creativa/JEI.

**Esito ultimo test:** le tre verifiche di base del 22 ottobre e le tre correzioni visive/JEI del 23 ottobre sono state confermate dall'autore. Le altre prove, in particolare la prova con due client su server dedicato, restano aperte. Non segnare nuove checkbox senza riscontro reale.

Test di dettaglio: [PIZZA_OVEN_IMPLEMENTATION_TESTS.md](PIZZA_OVEN_IMPLEMENTATION_TESTS.md), [VANILLA_FURNACES_TESTS.md](VANILLA_FURNACES_TESTS.md), [PIZZA_PEEL_TESTS.md](PIZZA_PEEL_TESTS.md), [CREATIVE_INVENTORY_ORDER.md](CREATIVE_INVENTORY_ORDER.md) e [RELEASE_BALANCE_AUDIT.md](RELEASE_BALANCE_AUDIT.md).
