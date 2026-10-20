# Pizza 1.0.0 — Regressione mozzarella intera legacy (20 ottobre 2026)

**Branch:** `feature/pizza-1.0.0`  
**Stato:** correzione codice pubblicata, **build CI / verifica visiva in gioco da controllare separatamente**.

## Perché questa correzione

`italiansdelight:mozzarella` (intera) è **esclusa dalla whitelist** dei topping. La versione precedente, tuttavia, conteneva un ramo prototipale di `PizzaDoughPlacement` che permetteva di depositare questo item in una pizza stesa: consumava una mozzarella ma non creava un'applicazione in `PizzaComposition`. Invece, impostava il vecchio `PizzaDoughBlock.MOZZARELLA=true`. `PizzaToppingRenderer` leggeva solo la lista moderna e perciò non visualizzava la mozzarella consumata.

La nuova patch **impedisce ulteriori applicazioni legacy** e **mostra quelle già salvate** sulle pizze crude, usando solo gli asset esistenti. Il flag nel BlockState e nel componente `PizzaItemData` non viene rimosso o convertito: ciò impedisce perdite di ingredienti in vecchi mondi.

## Test essenziali

- [ ] Avviare Minecraft in **Sopravvivenza** con JEI su questo branch, posizionare l'impasto di Farmer's Delight e stenderlo fino allo stadio 4. Prendere 2 moz­zarelle **intere**. Clic destro con una mozzarella intera sulla pizza: **nessun item consumato**, nessun cambio dello stato `mozzarella`, nessuna applicazione di topping; riprovare per confermare che non duplichi nulla.
- [ ] Applicare invece `italiansdelight:mozzarella_slice` con clic destro: **funziona come prima**, consuma una sola fetta per applicazione riuscita e mostra il topping in tutte le zone interessate.
- [ ] Test su copia di un **mondo precedente** che conteneva una pizza con `mozzarella=true`. La mozzarella legacy deve comparire sulla base cruda, anche senza alcuna applicazione nel sistema nuovo.
- [ ] In alternativa, in un mondo test con comandi, ottenere un blocco legacy con `/setblock ~ ~ ~ italiansdelight:pizza_dough[stage=4,mozzarella=true]` (su un supporto compatibile e in posizione libera). Verificare il disegno della mozzarella intera. Questo genera un test di BlockState e non sostituisce del tutto una prova su salvataggio storico.
- [ ] Con una pizza legacy, **Shift + clic destro a mano principale vuota e senza pala nella secondaria**, dopo avere rimosso eventuali topping moderni: restituire **una sola mozzarella intera**, impostare `mozzarella=false` e cancellare il suo disegno da tutti e quattro i quarti. Nessun rimborso quadruplo.
- [ ] Con una pizza legacy contenente mozzarella intera e condimenti moderni, verificare che la rimozione LIFO dei topping moderni preceda la restituzione della mozzarella legacy e non modifichi le maschere dei quarti.
- [ ] Rompere una **pizza legacy cruda stesa**: deve cadere **una pizza-item con il dato preservato**, non impasto + mozzarella separata (né drop duplicati). Raccoglierla e riposizionarla: la mozzarella deve tornare visibile e recuperabile.
- [ ] Trasportare la stessa pizza con la pala, cuocerla e riposizionare la pizza cotta: la mozzarella legacy deve rimanere presente e visibile; le fette ottenute devono mantenere il bonus latticini previsto. Verificare che non compaiano ciotole per la mozzarella intera.
- [ ] Salvare il mondo, uscire e rientrare. In multiplayer due giocatori devono vedere lo stesso disegno della mozzarella legacy prima e dopo la rimozione.
- [ ] Regressione: salsa, mozzarella a fette, basilico, speck e altre applicazioni canoniche continuano a seguire limite 6 per quarto, ordine LIFO, rimborsi e rendering senza patina rossa; nessuna modifica a forni, hopper, JEI o nomi dinamici.

## Ambito tecnico

File codice modificati:
- `src/main/java/dev/italiansdelight/common/pizza/PizzaDoughPlacement.java`: elimina il ramo che creava il vecchio flag; click rifiutato senza consumo, mantenendo l'item intero non approvato.
- `src/main/java/dev/italiansdelight/client/render/PizzaToppingRenderer.java`: quando il vecchio flag è presente, rende una mozzarella per ogni quarto fisico tramite l'ItemStack originale. Le posizioni sono deterministiche, non c'è mutazione lato client, e il rendering delle applicazioni moderne usa lo stesso calcolo e asset di prima.
- `src/main/java/dev/italiansdelight/common/pizza/PizzaToppingInteractions.java`: **invariato**; mantiene il rimborso anti-perdita dei dati legacy.
- `src/main/java/dev/italiansdelight/common/pizza/PizzaItemData.java`: **invariato**; il campo `legacy_mozzarella` resta serializzabile per i mondi e gli item preesistenti.

La grafica definitiva **2.5D** di tutti i topping non fa parte di questa correzione: è una fase successiva che richiede test visivi separati e mantiene invariati gli asset approvati.

**Chiusura BUG-002:** passare da «da verificare» a «risolto» soltanto dopo il collaudo in gioco dell'autore.
