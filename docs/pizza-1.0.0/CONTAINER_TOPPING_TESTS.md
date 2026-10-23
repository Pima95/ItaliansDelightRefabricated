# Pizza 1.0.0 — Fase 3H: una sola salsa/base cremosa per pizza o spicchio

> **Stato: codice aggiornato su `feature/pizza-1.0.0`, build e prove in gioco da eseguire.** Il 15 ottobre 2026 l'autore ha confermato che posa, rendering e rimborsi dei tre item funzionavano nella fase 3G, ma ha corretto la regola di esclusività: **non è consentito sovrapporre pomodoro, panna e ricotta**.

## Nuova regola di gameplay

| Item | Identità canonica | Categoria nutrizionale (invariata) | Occupa l'unico spazio salsa per quarto |
| --- | --- | --- | --- |
| `farmersdelight:tomato_sauce` | Salsa di pomodoro | Salse | **Sì** |
| `italiansdelight:cream_bowl` | Panna | Latticini | **Sì** |
| `italiansdelight:ricotta` | Ricotta | Latticini | **Sì** |

Le due creme restano di ruolo tecnico `CREAMY` e categoria `DAIRY` per non alterare le ricette nutrizionali. La nuova funzione `PizzaToppingType.occupiesSauceSlot()` gestisce **l'esclusività visiva e funzionale** indipendentemente dai bonus nutrizionali.

**Una sola salsa per ogni quarto fisico.** Quando la pizza è intera, il clic interessa tutti e quattro i quarti; se una zona è già occupata da una delle tre salse, il tentativo di applicarne una diversa sulla zona **viene rifiutato completamente, senza consumo**. Con la pizza suddivisa puoi dare a due metà o a due spicchi **salse diverse senza sovrapporle**.

Le normali applicazioni di altri ingredienti (mozzarella a fette, basilico, speck, ecc.) non vengono bloccate dalle salse. Restano il limite di **6 tipi per quarto**, le maschere di applicazione, l'ordine di rimozione **LIFO** e il rimborso dell'item originale una sola volta.

## Regole per le ciotole e la compatibilità

- Ogni applicazione riuscita consuma **un item pieno**; la ciotola non viene restituita alla posa, perché l'item originale deve restare interamente rimborsabile se si annulla la preparazione.
- Shift + clic destro a mano vuota rimuove soltanto la copertura della zona; l'ultima rimozione restituisce l'**item pieno originale**, senza ciotola separata. Mai un rimborso per quarto.
- Il futuro recupero della ciotola dopo il consumo irreversibile in cottura è **ancora da implementare**.
- Le pizze salvate in precedenza che contengono più salse in uno stesso quarto **rimangono caricabili e recuperabili**. I topping esistenti non vengono eliminati né trasformati automaticamente; la nuova regola blocca solo le **nuove** sovrapposizioni. Puoi rimuovere gli item preesistenti normalmente.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

## Test 1 — Pizza intera: una salsa

1. Stendere una pizza intera (`division_state=0`), applicare `farmersdelight:tomato_sauce`.
2. Tentare di aggiungere `italiansdelight:cream_bowl` e `italiansdelight:ricotta` senza dividerla: **entrambi i clic falliscono** senza consumi o cambiamenti al blocco.
3. Riutilizzare pomodoro: è un doppione, quindi nessun consumo.
4. Ripetere partendo prima da panna, poi provare ricotta e pomodoro: entrambi vietati. Ripetere partendo prima da ricotta.
5. Aggiungere mozzarella a fette e basilico: **consentito**, anche con una salsa applicata.
6. Verificare con `/data get block X Y Z` che l'unica salsa copra `remaining_mask=15`; non deve essere restituita una ciotola alla posa.

## Test 2 — Una salsa diversa su ogni spicchio

1. Stendere una nuova pizza, dividerla fino a `division_state=7` con mattarello.
2. Mettere pomodoro nel primo quarto, panna nel secondo e ricotta nel terzo; lasciare vuoto il quarto rimanente oppure aggiungere una delle tre.
3. Ognuna deve coprire **solo il proprio quarto**; i tre oggetti devono essere consumati una sola volta e renderizzati nei quarti rispettivi.
4. Provare su ogni quarto già condito le due salse alternative: **rifiuto senza consumo**.
5. Riunire tutti i quarti con Shift + mattarello: le salse restano in quarti distinti, senza moltiplicarsi o spostarsi. Tentare di applicare una salsa all'intera pizza riunita: **rifiuto integrale**, senza sostituire quelle preesistenti.
6. Dividere nuovamente e rimuovere una salsa da un quarto: il rimborso non interessa gli altri quarti.

## Test 3 — Metà e area parzialmente occupata

1. Dividere una pizza in due metà; applicare panna alla metà sinistra (`remaining_mask=3`).
2. Provare a piazzare pomodoro sulla stessa metà: rifiutato; aggiungerlo sulla metà destra (`remaining_mask=12`): accettato.
3. Riunire la pizza e tentare l'aggiunta di ricotta: **tutta l'applicazione fallisce**, senza consumare l'item e anche se ci fossero quarti ancora privi di salsa.
4. Su una pizza differente, dividere in quarti, mettere pomodoro su uno, riunire e provare a estendere panna al resto: il clic su una zona che include quel quarto già occupato deve essere rifiutato integralmente.
5. Verificare limite 6 per quarto indipendentemente dal tipo di salsa.

## Test 4 — Rimborsi, salvataggi e vecchie pizze

1. Applicare una salsa all'intera pizza, dividere in quattro e togliere progressivamente la salsa: nessun item alle prime tre rimozioni, **un solo item pieno** alla quarta.
2. Provare con inventario pieno: un solo item completo deve cadere nel mondo, nessuna ciotola extra.
3. Rompere una pizza con salsa e altri topping, raccogliere `raw_pizza`, lanciare con Q, raccogliere, salvare e riposizionare: nessuna perdita di `original_stack`, `original_mask`, `remaining_mask`.
4. Caricare un **backup** di un mondo con una vecchia pizza contenente contemporaneamente pomodoro, panna e ricotta: la pizza non deve sparire né perdere dati. La rimozione deve continuare a funzionare in ordine LIFO. **Nessun nuovo clic** deve aggravare la sovrapposizione.
5. In multiplayer due utenti non devono riuscire ad applicare due salse diverse sullo stesso quarto; l'item del clic rifiutato non viene consumato.

## Elementi futuri invariati

- I modelli degli ItemStack appoggiati sulla pizza, simili a un item frame, sono **placeholder**: sostituirli con una resa 2.5D credibile per pomodoro/panna/ricotta.
- Riprogettare `raw_pizza.png` (16×16 PNG RGBA: alpha 255 sull'item, alpha 0 solo fuori sagoma).
- Restituzione dei recipienti a consumo definitivo in cottura: non presente in questa fase.
- **Conflitto con il preset «Principessa»:** la ricetta precedentemente approvata contiene pomodoro **e** panna nello stesso quarto. Non è più realizzabile con la nuova regola. Mantenere la definizione storica nel documento [NAMED_PIZZAS.md](NAMED_PIZZAS.md) ma **sospendere quel preset** finché l'autore non avrà approvato una variante compatibile; non cambiare ingredienti o numero di nomi fissi senza approvazione.
