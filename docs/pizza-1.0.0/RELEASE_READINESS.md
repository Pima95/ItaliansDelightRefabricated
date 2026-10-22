# Pizza Update 1.0.0 — checklist per il rilascio

**Stato:** sviluppo su `feature/pizza-1.0.0`; **non pubblicare ancora**.  
**Versione attualmente nei metadati:** `mod_version=0.4.0`; non incrementarla finché la checklist non è confermata dall'autore.  
**Target dichiarato:** Minecraft 26.2, Fabric Loader 0.19.5+, Fabric API 0.161.0+26.2, Java 25, Farmer's Delight Refabricated 26.2-3.6.28+refabricated.

## Prima del test

1. Usare **una copia di backup del mondo**, specialmente per provare un salvataggio creato con build precedenti della mod.
2. Sul branch `feature/pizza-1.0.0`, eseguire `git pull origin feature/pizza-1.0.0`, poi `.\\gradlew.bat clean build` e `.\\gradlew.bat runClient`. Su server dedicato usare la stessa build del JAR e le stesse dipendenze.
3. Registrare data, commit SHA, ambiente (singleplayer / dedicated server), log e se il risultato è stato verificato in **Survival**. Una CI verde controlla la compilazione e le invarianti statiche, **non** tutte le interazioni in Minecraft.

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
- [ ] JEI: categorie, guide, 5 esempi shapeless, Pizza Oven, progressi e nomi; traduzioni **italiano e inglese** senza chiavi mancanti. Conservare tutte le texture approvate.
- [ ] Formaggi da uncino (Scamorza/Provolone) non piazzabili a terra; formaggi da superficie e scaffali continuano a funzionare; recupero uncino + salume/formaggio e pulizia spazio tecnico dopo rottura del supporto.
- [ ] Altro: la Cheese Vat, il sale, la coltivazione del basilico e le ricette dei salumi funzionano dopo l'aggiornamento.

## E. Server, persistenza e consegna

- [ ] Avviare un **server Fabric dedicato senza renderer client** e con le stesse versioni delle dipendenze; nessun crash/mixin client-side sul server.
- [ ] Due giocatori manipolano lo stesso quarto o la stessa pizza nello stesso tick: non vengono rilasciati due spicchi, recuperate due pizze con pala o rimborsati ingredienti due volte.
- [ ] Salvare e riaprire dopo una pizza a quattro quarti, pizza nel forno a metà, pala con cargo e formaggi/salumi in lavorazione; nessuna perdita, duplicazione o reset imprevisto.
- [ ] Testare vari chunk caricati/scaricati, hopper pieni e ripetuti trasferimenti; controllare log, RAM e TPS senza creare cicli di duplicazione.
- [ ] Raccogliere le verifiche e segnalazioni in `docs/KNOWN_BUGS.md`. Per ogni bug: riproduzione, versione, risultato atteso/ottenuto e riferimento al commit di fix.
- [ ] **Solo con approvazione finale:** aggiornare `mod_version=1.0.0`, rivedere `CHANGELOG.md`/README, aprire PR verso la `main` protetta, creare release e poi pubblicare CurseForge/Modrinth.

## Risultato e criteri di blocco

Una compilazione verde non è sufficiente per rilasciare la 1.0.0. Bloccano la pubblicazione: crash, perdita/duplicazione di item, incompatibilità su server dedicato, dati di pizza non persistenti, hopper che estraggono input crudo, stack di spicchi che si uniscono a gusti diversi e assenza di elementi essenziali in Creativa/JEI.

**Esito ultimo test:** da compilare dall'autore. Non segnare checkbox eseguite senza una conferma reale.

Test di dettaglio: [PIZZA_OVEN_IMPLEMENTATION_TESTS.md](PIZZA_OVEN_IMPLEMENTATION_TESTS.md), [VANILLA_FURNACES_TESTS.md](VANILLA_FURNACES_TESTS.md), [PIZZA_PEEL_TESTS.md](PIZZA_PEEL_TESTS.md), [CREATIVE_INVENTORY_ORDER.md](CREATIVE_INVENTORY_ORDER.md) e [RELEASE_BALANCE_AUDIT.md](RELEASE_BALANCE_AUDIT.md).
