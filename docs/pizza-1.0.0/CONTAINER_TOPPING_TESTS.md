# Pizza 1.0.0 — Fase 3G: salsa di pomodoro, panna e ricotta

**Stato: codice pubblicato sul branch `feature/pizza-1.0.0`; build e test Minecraft 26.2 ANCORA DA ESEGUIRE.** Le funzioni testate precedentemente per i topping solidi rimangono attive e invariate.

## Novità

Sono ora utilizzabili sulla pizza completamente stesa tutti e tre gli item del catalogo già approvati e in precedenza bloccati:

| Item usato con clic destro | Identità canonica | Ruolo sulla pizza | Categoria nutrizionale |
| --- | --- | --- | --- |
| `farmersdelight:tomato_sauce` | Pomodoro | **SAUCE**, massimo una salsa per quarto | Salse |
| `italiansdelight:cream_bowl` | Panna | **CREAMY**, non occupa la posizione salsa | Latticini |
| `italiansdelight:ricotta` | Ricotta | **CREAMY**, non occupa la posizione salsa | Latticini |

Una **sola unità** viene consumata per ogni applicazione riuscita, anche se copre più quarti. Le interazioni utilizzano la composizione persistente e il rendering degli `ItemStack` esistenti, senza nuovi item o texture.

### Regola temporanea anti-duplicazione per le ciotole

**Il contenitore rimane depositato insieme all'item originario applicato.** Un clic che applica `tomato_sauce`, `cream_bowl` o `ricotta` consuma 1 item completo e **non** produce una ciotola vuota nello stesso momento.

Perché: quando si rimuove il topping prima della cottura (Shift + clic destro a mano vuota), il sistema restituisce **esattamente l'originale** `ItemStack`, con il suo recipiente, dopo che viene rimossa anche l'ultima parte dell'applicazione. Restituire la ciotola al primo clic E poi l'item pieno al rimborso produrrebbe una ciotola duplicata. Nessuna ciotola va quindi rimborsata separatamente alla rimozione.

**Da completare nella fase di cottura:** quando la salsa/crema viene consumata definitivamente e l'item originale cessa di essere rimborsabile, deve essere emessa **una ciotola per applicazione originale** (e mai per quarto, mai per ogni item renderizzato). La meccanica di cottura non è ancora stata implementata, quindi il recupero del recipiente a fine cottura **NON è ancora disponibile**. Questa non è una modifica alle regole vanilla per consumo normale degli item in altri contesti.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

## Test A — Salsa sulla pizza intera

1. Piazzare/stendere l'impasto e impugnare `farmersdelight:tomato_sauce`.
2. Fare clic destro una volta sulla pizza intera. **Atteso:** `tomato_sauce` diminuisce di 1, `applications` aumenta di 1, copertura `original_mask=15`, `remaining_mask=15`; NON viene prodotta una ciotola vuota.
3. Ripetere il clic con la stessa salsa: **nessun consumo**, nessuna applicazione ulteriore.
4. Controllare che il vecchio flag `mozzarella` non venga impostato.
5. Con il recipiente in mano le linee di divisione devono comparire quando si utilizza il mattarello per tagliare una pizza già divisa.

## Test B — Panna e ricotta sopra pomodoro

1. Aggiungere, sulla stessa pizza non divisa, **salsa di pomodoro + panna + ricotta + mozzarella a fette**.
2. **Atteso:** quattro applicazioni canoniche distinte su ognuno dei quattro quarti; panna e ricotta **non** cancellano il pomodoro e non sono trattate come salse.
3. Lo stesso item non può essere duplicato nella medesima zona. Se la pizza è parzialmente coperta, un'applicazione del medesimo tipo copre solo i quarti ancora liberi.
4. Provare con `italiansdelight:cream_bowl` e `italiansdelight:ricotta` impugnate: l'overlay temporaneo dei tagli deve essere visibile; la grafica degli item per ora resta **placeholder** (effetto 'item frame').
5. Provare altri due solidi distinti nello stesso quarto: si raggiunge **6 topping per quarto**. Il settimo è respinto integralmente, senza consumi, ciotole o mutazioni.

## Test C — Rimborsi esatti e parziali

1. Applicare **una** salsa a tutta la pizza, poi dividerla fino a quattro quarti.
2. Rimuovere la salsa da tre quarti separati: **nessun item né ciotola** restituito; `remaining_mask` perde un bit per quarto.
3. Rimuovere l'ultimo quarto: ricevere **esattamente 1 salsa di pomodoro piena**, senza ciotola extra. L'applicazione sparisce completamente.
4. Ripetere con panna e ricotta, sia individualmente sia nello stesso impasto. Le applicazioni devono essere rimosse in ordine **LIFO** all'interno della zona selezionata.
5. Riunire le zone e ridividerle: il rimborso dell'item originale non deve cambiare e non si moltiplica per il numero di quarti.

## Test D — Inventario pieno, salvataggio e multiplayer

1. Riempire l'inventario e rimuovere l'ultima parte di un'applicazione: deve cadere **un solo** item originario a terra, non la ciotola separata.
2. Preparare una pizza con pomodoro, panna e ricotta, romperla e raccogliere `raw_pizza`: deve cadere solo la pizza completa, conservando le applicazioni originali.
3. Lanciarla con Q, riprenderla, salvarla, uscire dal mondo e riposizionarla: `original_stack`, `original_mask` e `remaining_mask` rimangono invariati.
4. In multiplayer, un secondo giocatore deve vedere gli stessi condimenti; due clic contemporanei con la stessa salsa non possono provocare doppi consumi o duplicazioni.
5. In Creativa controllare che le interazioni continuino a essere non consumanti come per i solidi; i test di economia dei recipienti si effettuano in **Sopravvivenza**.
6. Testare `farmersdelight:tomato_sauce` con una ricetta esistente/Farmer's Delight fuori dalla pizza: il comportamento vanilla/FDR non deve essere alterato.

## Da implementare successivamente

- Recupero delle ciotole **al momento del consumo irreversibile in cottura**: una per applicazione originaria, non per quarto. Richiede la cottura e la gestione dell'output, e non deve introdurre nuovi exploit dopo i trasferimenti con pala o forno.
- Rendering definitivo delle salse come strato sottile, della panna/ricotta come crema e degli altri topping come modelli naturali: la resa attuale con item appoggiati è **provvisoria**.
- Texture item `raw_pizza` definitiva in PNG RGBA 16×16, con alpha 255 sulla sagoma, alpha 0 solo fuori e nessuna semitrasparenza.
- Gli ingredienti esterni rimangono autorizzati dal server soltanto con mapping per item esatto; la preview degli equivalenti non ancora sincronizzati al client richiede un collaudo separato.
