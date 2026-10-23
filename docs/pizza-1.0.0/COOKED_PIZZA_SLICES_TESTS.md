# Pizza 1.0.0 — Fase 4C: pizza cotta piazzabile e quattro fette

> **Fase 4C confermata funzionante in Minecraft dall'autore il 17 ottobre 2026.** La cottura nel Pizza Oven, il reset del progresso vanilla, la raccolta di ciotole e la GUI senza ricettario sono fasi separate. Le texture dei topping e dell'item `raw_pizza` rimangono placeholder.

## Funzionamento

- L'item `italiansdelight:cooked_pizza` rimane **non commestibile intero**. Clic destro con l'item sulla **faccia superiore di un supporto accettato dal sistema impasto**: appare `italiansdelight:cooked_pizza` come **blocco interno senza BlockItem**, con quattro quarti fisici. L'item originale si consuma **una sola volta** in Sopravvivenza; in Creativa non si consuma.
- Il blocco conserva tutta la composizione originaria in `CookedPizzaBlockEntity`: `PizzaItemData`, cronologia applicazioni, ItemStack originari, maschere e orientamento. Non ritorna mai all'impasto crudo né rilascia ciotole aggiuntive: i recipienti sono già stati contabilizzati dal forno.
- La proprietà del blocco `remaining` è una maschera intera di quattro bit (inizialmente **15**, cioè tutti i quarti). **Clic destro a mano principale vuota** su un quarto fisico presente = consegna **una** `italiansdelight:pizza_slice`, quindi elimina quel quarto visivamente. Nessun coltello/ciotola, nessuna fase di taglio successiva e nessuna azione con la mano occupata.
- `PizzaQuarterSelection.physicalQuarter(orientation, x, z)` seleziona la stessa mappa **0=sinistra/dietro, 1=sinistra/davanti, 2=destra/dietro, 3=destra/davanti** che era già presente sulla pizza cruda, anche quando non è mai stata divisa dal mattarello. Orientamento salvato, nessuna rotazione causata dal giocatore che la serve.
- La maschera ridotta e il modello multipart dei quarti sono sincronizzati da Minecraft tramite BlockState. **L'ultimo clic rimuove il blocco senza drop aggiuntivi.** Se due giocatori cliccano lo stesso quarto, la mutazione è server-side prima della consegna dell'item.
- **Rompere la pizza ancora intera** = drop di **1 `cooked_pizza` completa** con lo stesso componente composizione. **Rompere una pizza parzialmente servita** = drop delle sole `pizza_slice` dei quarti ancora presenti (mai la pizza intera, mai le fette già servite e mai ingredienti/ciotole duplicati). Se l'inventario è pieno e non può accogliere una fetta, **nessuna fetta è consegnata o droppata e il quarto rimane sul blocco**. Una pila identica non ancora piena è comunque spazio disponibile.
- Ogni `pizza_slice` contiene il nuovo componente persistente `pizza_slice_contents`, che include **solo le applicazioni che coprono quel quarto** (identità canonica e ItemStack di origine), più il vecchio flag mozzarella se esistente. Gli altri quarti non vengono copiati nel suo elenco. Il componente è dati descrittivi: gli ingredienti della fetta **non possono essere riscattati come item grezzi**.
- Le fette sono **commestibili** e il loro valore dipende dal solo quarto fisico. Crosta +1 fame/+1 saturazione; per ogni ingrediente canonico diverso, fino a **due contributi per categoria**: Salse +1/+0,5, Latticini +1/+1,5, Proteine animali +2/+2,5, Vegetali +1/+0,5, Erbe +0/+0, Oli/grassi +0/+1. Limiti 6 fame/6 saturazione. Duplicati canonici (comprese varianti crude/cotte equivalenti) non producono bonus extra. In `FoodProperties`, la saturazione è convertita nel modificatore vanilla; il nome del gusto non modifica i valori.
- La grafica del blocco ricicla **temporaneamente** `pizza_dough_top` e `pizza_dough_side` mediante 4 modelli separati, e il renderer usa ancora gli item sospesi come topping (placeholder `item frame`). L'icona della fetta riusa `raw_pizza` come **placeholder**. Nessuna PNG è stata modificata.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

La build non è stata eseguita nell'ambiente GitHub: segnalare il primo errore con percorso e riga se compaiono problemi delle API Minecraft 26.2.

## Test A — Pizza intera e porzioni

1. Preparare e cuocere una pizza con **pomodoro su una metà e ricotta sull'altra**, una mozzarella a fette solo sul primo quarto e basilico solo sul terzo. Controllare l'uscita del Pizza Oven e le **2 ciotole** attese (i due topping sono applicazioni differenti).
2. Tenere la `cooked_pizza` e cliccare con il tasto destro sulla sommità di un blocco di pietra. **Atteso:** una pizza visibile con **4 quarti**; l'item completo scompare dall'inventario (Sopravvivenza).
3. Clic destro con **mano vuota** su un quarto. Atteso: 1 fetta nel proprio inventario e **una zona mancante** sulla pizza. Ripetere fino a quattro volte: il blocco sparisce all'ultima fetta.
4. Provare a cliccare su uno spazio già rimosso: **nessuna quinta fetta** e nessuna duplicazione.
5. Provare a cliccare con un item in mano: non deve consegnare fette (l'item usato può mantenere le proprie normali interazioni vanilla).
6. In Creativa verificare anche `/give @s italiansdelight:pizza_slice`: la fetta di debug è commestibile, ma il componente dei condimenti è vuoto per definizione; non usarla per validare il trasferimento dei topping.

## Test B — Ogni quarto conserva i suoi condimenti

1. Preparare una pizza con quattro quarti differenti: primo pomodoro + mozzarella; secondo panna + speck; terzo ricotta + basilico; quarto nessuna salsa + mozzarella.
2. Cotta e piazzata, puntare e prelevare le quattro fette in un ordine variabile.
3. Atteso: ciascuna fetta riceve `pizza_slice_contents` **solo degli ingredienti presenti in quel quarto**, non quelli vicini. Il nuovo tooltip della fase nomi (da testare separatamente) fornisce una verifica leggibile.
4. **Aggiornamento dopo il collaudo 4C:** la successiva fase nomi ha aggiunto i nomi riconosciuti/dinamici (per esempio «Spicchio di Margherita») e i tooltip: sono ancora da compilare e testare come indicato in [PIZZA_NAMING_TESTS.md](PIZZA_NAMING_TESTS.md).

## Test C — Rottura e recupero sicuro

1. Cuocere una pizza con condimenti e **romperla senza prendere fette**: drop di una `cooked_pizza` con composizione intatta, **zero ciotole aggiuntive**.
2. Piazzare di nuovo la pizza, prelevare due quarti e poi romperla: ottenere **solo le altre due fette**, non l'item pizza cotta intera.
3. Raccogliere, lanciare con Q, salvare e ricaricare gli item fette: valori nutrizionali e componente `pizza_slice_contents` restano invariati.
4. Riavviare il mondo con una pizza piazzata e **tre quarti ancora presenti**: al login devono risultarne ancora tre, senza ripristinare il quarto servito.

## Test D — Multiplayer e saturazione

1. Due giocatori vicini: fare clic contemporaneamente sullo stesso quarto. Il server deve consegnare **una sola fetta**, non due.
2. Riempire ogni slot dell'inventario con item non compatibili: il clic non deve dare alcuno spicchio o drop e la pizza deve conservare il suo quarto. Liberare un solo slot e ritentare: deve consegnare esattamente uno spicchio. Ripetere con inventario pieno ma uno stack di spicchi identici non pieno: lo spicchio deve aggiungersi alla pila.
3. Mangiare una fetta vuota, una margherita semplice e una con proteine, latticini, vegetali e salsa. Il valore nutritivo deve salire secondo le categorie, rispettando il limite di 6 fame e 6 saturazione.
4. Verificare che panna e ricotta siano ancora **Latticini** per la nutrizione, ma mutuamente esclusivi con pomodoro **come condimento prima della cottura**. Il modello non cancella le composizioni legacy sovrapposte già salvate.

## Da sviluppare in seguito

- **Nomi dinamici e fissi per pizza/fetta:** ora implementati nel branch; collaudo della nuova fase ancora da completare in [PIZZA_NAMING_TESTS.md](PIZZA_NAMING_TESTS.md).
- Pixel art definitiva per `raw_pizza`, `cooked_pizza` e `pizza_slice` e topping 2.5D non più simili a item frame.
- Pala per trasporto, cottura in forno vanilla/affumicatore e ulteriori interazioni server.


## Test E — stacking nutrizionale approvato (21 ottobre 2026)

I valori si riferiscono a **nuovi spicchi** generati dopo l'aggiornamento, non a fette gia' salvate con un vecchio componente `FOOD`.

1. **Bianca** (senza topping): **1 fame, 1 saturazione**. **Margherita** (salsa, mozzarella, basilico): **3 fame, 3 saturazione**.
2. **Un latticino** (solo mozzarella): **2 fame, 2,5 saturazione**; **due latticini distinti** (mozzarella + gorgonzola): **3 fame, 4 saturazione**; **quattro latticini** (aggiungere provolone e parmigiano): ancora **3 fame, 4 saturazione**.
3. **Quattro Formaggi** bianca: **3 fame, 4 saturazione**; rossa: **4 fame, 4,5 saturazione**. Nessun incremento determinato dal nome.
4. **Due proteine diverse** (salame + speck, senza altri topping): **5 fame, 6 saturazione**, con il tetto sulla saturazione. Il terzo ingrediente Proteine animali non aumenta quei valori.
5. **Due vegetali diversi** (carota + patata a fette, senza altri topping): **3 fame, 2 saturazione**. Il terzo vegetale non aggiunge punti.
6. **Fetta mista con due proteine, mozzarella, salsa e vegetali:** cap **6 fame, 6 saturazione**. Dopo l'aggiunta Nourishment, lo spicchio con almeno 3 fame dovrà ricevere Nourishment breve, senza cambiare il valore nutritivo.
7. Con `bacon` e `cooked_bacon` (o altra coppia canonica equivalente) riprovare la posa sul medesimo quarto: non deve duplicare lo stesso tipo; lo spicchio deve valere un singolo bonus proteine quando e' l'unico topping proteico.
8. Con quattro quarti di composizione diversa, controllare che i quattro item ottenuti abbiano dati/nome/nutrizione indipendenti. Dopo salvataggio e riavvio non devono cambiare. Il valore di saturazione **non e' visibile direttamente** nella barra fame vanilla: verificarlo con strumenti/debug adeguati o test controllati, non soltanto contando i cosciotti.

Questo e' un collaudo manuale in mondo reale, non un test gia' superato. La build automatica copre 18 preset e le combinazioni limite mediante lo script `.github/scripts/check_pizza_release_balance.py`.

## Test F — Nourishment (nuovo test dopo il bilanciamento approvato)

Usare **solo nuove fette cotte** create dopo il pull: gli ItemStack già salvati possono conservare il vecchio componente `CONSUMABLE`. Il precedente test dei punti fame/saturazione è superato; questo è un controllo aggiuntivo.

- [ ] **Bianca** (1 fame), **Rossa** (2 fame) e **sola mozzarella** (2 fame): nessun effetto Nourishment.
- [ ] **Margherita** (3 fame), **Quattro Formaggi bianca** (3 fame), **Salame** (5 fame): Nourishment di durata breve nella schermata effetti, nessun altro stato aggiunto.
- [ ] Pizza personalizzata con **mozzarella + gorgonzola** (3 fame): Nourishment breve; un terzo latticino non incrementa il livello o la durata dell'effetto configurato.
- [ ] Pizza assortita: ogni spicchio deve ricevere il suo risultato indipendentemente dagli altri quarti e dal nome.
- [ ] Verificare valori fame/saturazione invariati (cap 6/6) e persistenza dopo riavvio, raccolta e trasferimento fra giocatori.

## Test G — tooltip Nourishment (correzione del 21 ottobre 2026)

Questo fix cambia **solo la visualizzazione** nell'inventario, non il consumo o il bilanciamento.

- [ ] Inventario, `it_it`: fetta Margherita/Quattro Formaggi con effetto: compare **Nutrimento (1:00)** oltre agli ingredienti.
- [ ] Inventario, `en_us`: stessa fetta, nome localizzato nella lingua inglese e **1:00**; nessuna chiave mancante.
- [ ] Bianca/Rossa e altri spicchi sotto 3 punti fame non mostrano alcun effetto.
- [ ] Su `raw_pizza` e `cooked_pizza` non viene mostrato il tooltip del cibo.
- [ ] Disattivando il tooltip degli effetti in configurazione Farmer's Delight, la descrizione dell'effetto scompare ma restano ingredienti e nome della fetta.
- [ ] Vecchie fette con `CONSUMABLE` Nourishment già salvato: tooltip visibile senza ricreare lo stack. Verificare dopo riavvio/salvataggio.

## Test H — rimozione del supporto (fix pre-release)

- [ ] Pizza cotta intatta (4/4): rimuovere il blocco sottostante e raccogliere **una sola `cooked_pizza`** con i dati originali.
- [ ] Pizza cotta parzialmente servita: dopo aver prelevato 1, 2 o 3 spicchi, togliere il supporto. Recuperare **solo** i 3/2/1 spicchi rimasti, ciascuno col proprio nome, condimenti, Food component e Nourishment.
- [ ] Non deve cadere nessuna ciotola/ingrediente crudo in più, né ricomparire uno spicchio già raccolto.
- [ ] Ripetere su un supporto speciale divenuto invalido (botola superiore aperta), con salvataggio/rientro e in multiplayer. I drop seguono le normali gamerule.
