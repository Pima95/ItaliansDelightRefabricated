# Pizza Update 1.0.0 — Test fase 3D: condimenti solidi e rimozione per quarti

**Stato: codice pubblicato sul branch `feature/pizza-1.0.0`, compilazione e test in Minecraft 26.2 DA ESEGUIRE.** Non confondere questo stato con una verifica positiva in gioco.

Questa fase collega [PizzaToppingInteractions.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingInteractions.java), [PizzaCompositionOperations.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaCompositionOperations.java) e [PizzaDoughPlacement.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaDoughPlacement.java). Usa la BlockEntity e il `raw_pizza` con componente persistente delle fasi precedenti. **Aggiornamento (FASE 3E, da testare):** l'overlay visivo dei tagli e la sincronizzazione delle sole proprietà `division`/`facing` sul client sono ora implementati nel branch, ma non ancora verificati in gioco. Il rendering dei condimenti resta assente: per vedere gli ingredienti memorizzati si usa ancora `/data get block`. [Test overlay](DIVISION_OVERLAY_TESTS.md).

## Build e preparazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Eseguire in un mondo di prova e, prima delle prove di trasferimento, preparare un backup. La cache del server deve aver caricato i **33 mapping nativi** dopo l'avvio o `/reload`.

**Interazioni attivate:**

- **Clic destro** sulla pizza stesa (stadio 4) con un ingrediente **SOLID** approvato: applicazione solo alla zona logica puntata, 1 item consumato per una nuova applicazione in Survival, nessun consumo nei tentativi senza effetto.
- **Shift + clic destro a MANO PRINCIPALE VUOTA** sulla pizza stesa: toglie **l'ultima applicazione ancora presente** nella zona puntata, solo una per clic.
- **Mattarello**: clic destro divide; Shift + clic riunisce (senza usura per le divisioni), come nella fase 3B.
- **Prototipo mozzarella intera** preesistente: il suo clic resta disponibile e usa ancora il vecchio BlockState `mozzarella=true`. Il suo rimborso resta distinto dalle nuove applicazioni canoniche.

**Volutamente NON ancora attive:**

- `farmersdelight:tomato_sauce`, `italiansdelight:cream_bowl`, `italiansdelight:ricotta` come topping: sono le tre varianti in ciotola/recipiente. Verranno abilitate solo quando recupero del recipiente e rimborsi potranno convivere senza duplicazioni.
- I 2 nuovi item `carrot_slice` e `potato_slice`: non ancora registrati.
- Overlay, modelli/texture dei topping applicati, sincronizzazione BE al client, nomi dinamici, forno e pala. Le informazioni sui topping sono **salvate**, ma non disegnate sopra la base in questa fase.
- Le equivalenze da datapack esterni hanno già una cache lato server, ma le loro anteprime lato client devono ancora essere sincronizzate; verificare separatamente quando si aggiungono mod opzionali.

## Test 1 — Pizza intera: applicazione, rifiuto duplicato, rimborso

1. Posizionare impasto FDR e stenderlo completamente senza tagliarlo. Accertarsi che `division_state = 0`.
2. Tenendo `italiansdelight:mozzarella_slice`, clic destro sulla pizza **intera**.
3. **Atteso:** l'inventario perde una fetta di mozzarella in Survival, `applications` nella BlockEntity ha un'unica applicazione `canonical = italiansdelight:pizza_toppings/mozzarella_slice`, `original_mask = 15`, `remaining_mask = 15`; `next_application_id` avanza da 1 a 2.
4. Ripetere il clic con la stessa fetta: **nessun consumo**, nessuna nuova applicazione.
5. Usare **Shift + clic destro con mano vuota**: l'ultima applicazione scompare e si ottiene **esattamente un** `mozzarella_slice`. `next_application_id` rimane incrementato (non deve tornare a 1).
6. Rompere la pizza ormai intera, non divisa e senza topping: torna il normale impasto FDR, come già approvato.

## Test 2 — Pizza divisa in quarti: rimozione parziale senza rimborsi doppi

1. Stendere la pizza, dividerla con il mattarello fino a `division_state = 7`.
2. Riunirla fino a 0, aggiungere `italiansdelight:speck_slice` alla pizza intera, poi ridividerla in quattro.
3. Verificare `original_mask = 15` e `remaining_mask = 15` (l'applicazione appartiene a tutti i quarti, pur avendo consumato un solo speck).
4. Shift + clic vuoto su un quarto: `remaining_mask` perde soltanto un bit; **nessun rimborso**.
5. Ripetere su altri due quarti: ancora nessun rimborso.
6. Rimuovere l'ultimo quarto: `applications` diventa vuota e ricevi **una sola** `speck_slice`.

## Test 3 — Metà, riunione e area parzialmente libera

1. Dividere la pizza in due metà (`division_state = 1`).
2. Mettere una `mozzarella_slice` sulla metà sinistra: maschera di copertura `3`.
3. Riunire entrambe le metà (Shift + mattarello) e applicare un'altra `mozzarella_slice` all'intera pizza.
4. **Atteso:** la seconda applicazione copre **solo la metà destra** (maschera `12`) e costa **un solo item**; il lato sinistro non riceve un doppione.
5. Un ulteriore clic con mozzarella non deve consumare nulla.
6. Shift + clic vuoto sulla pizza riunita: viene rimossa **per prima** la seconda applicazione (ID più alto) e restituito il suo item, poi con un altro clic viene rimossa la prima e restituito l'altro.

## Test 4 — Distinzioni e blocchi

- **Ammessi:** `italiansdelight:basil`, `italiansdelight:mozzarella_slice`, `italiansdelight:speck_slice`, `farmersdelight:bacon`, `farmersdelight:cooked_bacon` e altri solidi presenti nel catalogo.
- **Anti-duplicato:** `farmersdelight:bacon` e `farmersdelight:cooked_bacon` non possono stare sullo stesso quarto. A seconda del caso, solo la parte libera riceve una nuova applicazione.
- **Negati:** `minecraft:egg`, `minecraft:carrot`, `minecraft:potato`, `minecraft:beef`, forme intere di salumi/formaggi e qualsiasi item non incluso negli ID canonici/espliciti.
- **Panna, ricotta, pomodoro in ciotola:** non devono essere consumati in questa fase (restano intenzionalmente in sospeso).
- **Sei per quarto:** aggiungere sei tipi differenti su un quarto; il settimo deve essere rifiutato senza consumare nulla. Le applicazioni su altri quarti liberi restano possibili.
- Se il clic è parzialmente libero ma una delle nuove parti supererebbe il limite di sei, **l'intera applicazione fallisce** senza modificare stato/inventario.
- Una crema non è automaticamente salsa e le salse sono facoltative; **il test sulle salse** seguirà l'attivazione dei recipienti.

## Test 5 — Recupero da mondo, inventario, Q, riavvio

1. Preparare una pizza con due o più solidi e divisioni differenti. Confermare le applicazioni con `/data get block X Y Z`.
2. Rompere in **Sopravvivenza**: deve cadere **una sola** `italiansdelight:raw_pizza`, **non** gli ingredienti individuali.
3. Raccogliere, lanciare con **Q**, raccogliere, salvare/uscire e rientrare nel mondo.
4. Piazzare la pizza: stessa `PizzaComposition` (ID canonici, `original_stack`, `original_mask`, `remaining_mask`, `division_state`, `orientation`).
5. Rimuovere i topping uno alla volta fino al rimborso finale; nessuna perdita e nessuna duplicazione.
6. Provare con inventario **completamente pieno**: il rimborso di un'applicazione completa deve cadere come item nel mondo, **non** cancellarsi o duplicarsi.

## Test 6 — Multiplayer e regressioni

- Due giocatori che cliccano lo stesso quarto quasi contemporaneamente: sul server la stessa identità canonica non può essere consumata due volte nella medesima area.
- Shift + clic mano vuota su quarto privo di topping: nessun nuovo item e nessun mutamento.
- Applicare un solido, poi dividere e riunire con mattarello, salvare/rientrare: gli ingredienti restano nelle zone fisiche originali.
- Il vecchio prototipo di mozzarella intera deve continuare a essere leggibile, droppabile nella pizza e rimuovibile **una volta sola** col gesto Shift + mano vuota quando non ci sono applicazioni canoniche più recenti nella zona.
- La texture item `raw_pizza.png` rimane **16×16 RGBA**: alpha 255 sui pixel visibili, alpha 0 soltanto **all'esterno dell'ovale**, mai alpha fra 1 e 254. Non è stata toccata da questa fase.

**Nota di sicurezza:** utilizzare i dati di `/data` finché i topping non hanno una rappresentazione grafica definitiva. Non è previsto alcun rimborso automatico quando si limita a dividere o riunire la pizza.
