# Pizza 1.0.0 — Fase 4C: pizza cotta piazzabile e quattro fette

> **Implementata sul branch `feature/pizza-1.0.0` (17 ottobre 2026), MA NON ANCORA COMPILATA O TESTATA IN MINECRAFT 26.2.** La cottura nel Pizza Oven, il reset del progresso vanilla, la raccolta di ciotole e la GUI senza ricettario sono fasi separate. Le texture dei topping e dell'item `raw_pizza` rimangono placeholder.

## Funzionamento

- L'item `italiansdelight:cooked_pizza` rimane **non commestibile intero**. Clic destro con l'item sulla **faccia superiore di un supporto accettato dal sistema impasto**: appare `italiansdelight:cooked_pizza` come **blocco interno senza BlockItem**, con quattro quarti fisici. L'item originale si consuma **una sola volta** in Sopravvivenza; in Creativa non si consuma.
- Il blocco conserva tutta la composizione originaria in `CookedPizzaBlockEntity`: `PizzaItemData`, cronologia applicazioni, ItemStack originari, maschere e orientamento. Non ritorna mai all'impasto crudo né rilascia ciotole aggiuntive: i recipienti sono già stati contabilizzati dal forno.
- La proprietà del blocco `remaining` è una maschera intera di quattro bit (inizialmente **15**, cioè tutti i quarti). **Clic destro a mano principale vuota** su un quarto fisico presente = consegna **una** `italiansdelight:pizza_slice`, quindi elimina quel quarto visivamente. Nessun coltello/ciotola, nessuna fase di taglio successiva e nessuna azione con la mano occupata.
- `PizzaQuarterSelection.physicalQuarter(orientation, x, z)` seleziona la stessa mappa **0=sinistra/dietro, 1=sinistra/davanti, 2=destra/dietro, 3=destra/davanti** che era già presente sulla pizza cruda, anche quando non è mai stata divisa dal mattarello. Orientamento salvato, nessuna rotazione causata dal giocatore che la serve.
- La maschera ridotta e il modello multipart dei quarti sono sincronizzati da Minecraft tramite BlockState. **L'ultimo clic rimuove il blocco senza drop aggiuntivi.** Se due giocatori cliccano lo stesso quarto, la mutazione è server-side prima della consegna dell'item.
- **Rompere la pizza ancora intera** = drop di **1 `cooked_pizza` completa** con lo stesso componente composizione. **Rompere una pizza parzialmente servita** = drop delle sole `pizza_slice` dei quarti ancora presenti (mai la pizza intera, mai le fette già servite e mai ingredienti/ciotole duplicati). Se l'inventario è pieno quando si prende una fetta, essa cade a terra.
- Ogni `pizza_slice` contiene il nuovo componente persistente `pizza_slice_contents`, che include **solo le applicazioni che coprono quel quarto** (identità canonica e ItemStack di origine), più il vecchio flag mozzarella se esistente. Gli altri quarti non vengono copiati nel suo elenco. Il componente è dati descrittivi: gli ingredienti della fetta **non possono essere riscattati come item grezzi**.
- Le fette sono **commestibili**, con valori per **categoria diversa presente nel quarto**: base crosta 1 fame/1 saturazione; pomodoro Salsa +1/+0,5; Latticini +1/+1,5; Proteine +2/+2,5; Verdure +1/+0,5; Erbe +0/+0; Oli/grassi +0/+1. Limiti **6 fame/6 punti di saturazione**, una sola contribuzione per categoria per fetta. In `FoodProperties` la saturazione è convertita a un modificatore vanilla.
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
3. Atteso: ciascuna fetta riceve `pizza_slice_contents` **solo degli ingredienti presenti in quel quarto**, non quelli vicini. Verificarlo tramite componenti/item data in debug o quando verrà implementato il tooltip.
4. Per ora i **nomi fissi dei gusti**, come «Fetta di Margherita», e le descrizioni dei condimenti sul tooltip restano una fase successiva: le fette usano il nome generico localizzato «Fetta di pizza». Non confondere questo limite con un'eventuale perdita dei dati.

## Test C — Rottura e recupero sicuro

1. Cuocere una pizza con condimenti e **romperla senza prendere fette**: drop di una `cooked_pizza` con composizione intatta, **zero ciotole aggiuntive**.
2. Piazzare di nuovo la pizza, prelevare due quarti e poi romperla: ottenere **solo le altre due fette**, non l'item pizza cotta intera.
3. Raccogliere, lanciare con Q, salvare e ricaricare gli item fette: valori nutrizionali e componente `pizza_slice_contents` restano invariati.
4. Riavviare il mondo con una pizza piazzata e **tre quarti ancora presenti**: al login devono risultarne ancora tre, senza ripristinare il quarto servito.

## Test D — Multiplayer e saturazione

1. Due giocatori vicini: fare clic contemporaneamente sullo stesso quarto. Il server deve consegnare **una sola fetta**, non due.
2. Riempire l'inventario: la fetta prelevata deve cadere nel mondo, non sparire.
3. Mangiare una fetta vuota, una margherita semplice e una con proteine, latticini, vegetali e salsa. Il valore nutritivo deve salire secondo le categorie, rispettando il limite di 6 fame e 6 saturazione.
4. Verificare che panna e ricotta siano ancora **Latticini** per la nutrizione, ma mutuamente esclusivi con pomodoro **come condimento prima della cottura**. Il modello non cancella le composizioni legacy sovrapposte già salvate.

## Da sviluppare in seguito

- **Nomi dinamici e fissi per pizza/fetta**, inclusa l'esatta «Fetta di Margherita» se il singolo quarto corrisponde ai suoi ingredienti; composizione visibile sul tooltip senza alterare il dato originale.
- Pixel art definitiva per `raw_pizza`, `cooked_pizza` e `pizza_slice` e topping 2.5D non più simili a item frame.
- Pala per trasporto, cottura in forno vanilla/affumicatore e ulteriori interazioni server.
