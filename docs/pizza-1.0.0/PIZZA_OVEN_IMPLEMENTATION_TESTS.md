# Pizza 1.0.0 — Fase 4B: Pizza Oven funzionante

> **Codice pubblicato su `feature/pizza-1.0.0` (17 ottobre 2026). Build Java 25 / Minecraft 26.2 e prove in gioco ANCORA DA ESEGUIRE.** Non confondere una revisione statica dei file con il collaudo: il forno non è stato compilato dall'assistente.

## File principali

- `common/block/PizzaOvenBlock.java`: blocco orientabile da 1×1×1 con `facing`, `lit`, interazione in mano vuota o occupata per aprire la GUI; ticker soltanto lato server.
- `common/block/entity/PizzaOvenBlockEntity.java`: tre slot server (0 ingresso pizza, 1 combustibile vanilla, 2 risultato), contenuti/energia persistenti e sincronizzazione GUI tramite quattro valori vanilla; cottura solo quando l'ingresso è valido e il risultato vuoto. Hopper dall'alto, dai lati e dal basso con accesso differenziato. Rilascio di ciotole una volta sola al completamento.
- `common/block/entity/container/PizzaOvenMenu.java`: menu lato server con gli slot e le coordinate identiche alla fornace vanilla. Usa **`MenuType.FURNACE`**, così il client apre il normale `FurnaceMenu` e `FurnaceScreen` senza alcuna schermata custom.
- `common/pizza/PizzaBaking.java`: conversione pura di pizza cruda a cotta con composizione originale e `BakingResult` che conta una ciotola per applicazione originaria di pomodoro/panna/ricotta, non per quarto.
- JSON: `blockstates/pizza_oven.json`, item model, crafting shaped `BSB/SFS/BSB`, loot table e tag del piccone. **Modello/texture sono TEMPORANEAMENTE quelli della fornace vanilla**. Le texture degli item e dei condimenti esistenti non sono state modificate.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Al primo errore, fornire **il messaggio completo**, incluso file/riga: la compilazione non è ancora stata verificata in un runtime Minecraft 26.2.

## Test 1 — Registrazione, craft e GUI

1. `/give @s italiansdelight:pizza_oven`.
2. Posizionare il blocco guardando verso Nord, Est, Sud e Ovest: il lato frontale deve essere rivolto al giocatore. È una fornace vanilla come **placeholder**, e può apparire identica graficamente.
3. Verificare il craft 3×3 **BSB / SFS / BSB** con **B** = blocco mattoni, **S** = mattoni di pietra, **F** = fornace vanilla.
4. Aprire il forno a mano vuota o con un item qualunque. **Atteso:** schermata standard della fornace (3 slot, fiamma, freccia, inventario giocatore), titolo localizzato `Forno per pizza`.
5. Inserire solo `italiansdelight:raw_pizza` nello slot ingresso. **Non** devono entrare altri cibi nella macchina. Il risultato non ammette item inseriti dal giocatore.
6. Provare drag and drop, shift-click dal proprio inventario e dai tre slot; controllare se il menu vanilla client permette il clic manuale sulla pizza pur non avendo una ricetta vanilla di smelting: il server usa controlli dedicati e non registra ricette di smelting normali.

## Test 2 — Cottura e energia senza spreco

1. Con pizza cruda (preparata con topping e raccolta rompendo la base) e carbone, la cottura deve avviarsi subito **senza preriscaldamento**.
2. Dopo circa **20 secondi (400 tick di lavoro effettivo)**, lo slot ingresso si svuota e il risultato contiene **1 `cooked_pizza`**.
3. La composizione, il numero di applicazioni, gli stack originali e le maschere dei quattro quarti devono restare inalterati nel data component del risultato.
4. Non mettere una nuova pizza: il `lit` deve spegnersi e l'energia residua deve restare **congelata** anche per più minuti.
5. Mettere una nuova pizza: il progresso inizia da 0 e l'energia residua del primo combustibile viene riutilizzata. Quando termina, viene preso un solo altro combustibile se necessario.
6. Togliere il carburante prima che inizi: se la riserva è zero, il progresso resta fermo. Rifornire e controllare che ricominci dal punto precedente.
7. Mettere il secchio di lava: il forno deve usare 20.000 tick di energia e lasciare il **secchio vuoto** nello slot combustibile.
8. Provare legno, carbone, carbonella, blocchi combustibili e combustibili di altre mod che si registrano tramite la registry vanilla/Fabric: la fonte usata è `level.fuelValues()`, non una lista hardcoded.
9. Controllare effetti visivi `lit` durante la cottura e `unlit` a pizza pronta/senza input.

## Test 3 — Capienza singola, uscite, hopper

1. Con pizza già pronta nello slot risultato, provare ad aggiungere una nuova pizza sia manualmente sia con hopper sopra: **inserimento rifiutato**.
2. Hopper dall'alto: può inserire **solo pizze crude**, non cibi o pizze cotte, e soltanto se il risultato è libero.
3. Hopper sui lati: può inserire **solo combustibile valido**.
4. Hopper dal basso: può estrarre **solo `cooked_pizza`**, mai combustibile o pizza cruda/parzialmente cotta.
5. Verificare anche hopper concorrenti e operazioni quasi simultanee all'ultimo tick: **niente doppio risultato o doppie ciotole**.
6. Inventario pieno, guasti di rete e riavvio del server: la pizza e il fuel non devono sparire o essere replicati.

## Test 4 — Pizza parzialmente cotta, salvataggio

1. Cuocere una pizza solo **10 secondi**: avrà `bake_progress` intorno a **600 su 1200**.
2. Estrarla manualmente dall'ingresso: rimane `raw_pizza` e il suo progresso è nel componente.
3. Romperla/riposizionarla nel mondo, poi raccoglierla di nuovo: il progresso e gli ingredienti devono rimanere identici.
4. Reinserirla nel Pizza Oven con energia: deve terminare in circa **10 secondi**, non iniziare da zero.
5. Chiudere e riaprire il mondo con pizza parzialmente cotta nell'ingresso e con energia residua, poi completarla: nessun reset o perdita.
6. Una pizza con `bake_progress = 0` e senza condimenti/divisioni resta un impasto normale quando rotta nel mondo; una pizza con progresso maggiore di 0 deve tornare `raw_pizza`.

## Test 5 — Ciotole e applicazioni originali

1. Condire una pizza intera con **una** applicazione di pomodoro (maschera 15), romperla, cuocerla: in uscita deve comparire **una sola ciotola**, non 4.
2. Dividere una pizza in quattro e applicare pomodoro, panna e ricotta su **tre spicchi differenti**. Dopo cottura aspettarsi **esattamente 3 ciotole** come normali drop sopra il forno.
3. Applicare una salsa intera e rimuoverla completamente prima della cottura: l'item pieno viene già rimborsato. La cottura non deve generare alcuna ciotola per quell'applicazione cancellata.
4. Rimuovere soltanto una parte dell'applicazione (es. tre quarti): se l'ultimo quarto è ancora coperto, **una sola ciotola** al completamento.
5. Verificare che il menu non restituisca una seconda volta gli item originali dopo cottura: `cooked_pizza` conserva gli ItemStack solo come origine/ricetta, non per il rimborso di topping crudi.
6. Rompere il forno con pizza cruda, cotta o carburante nei rispettivi slot: recuperare i contenuti normalmente, senza creare ulteriori ciotole o risultati.

## Limiti di questa fase

- **Grafica placeholder:** modello e texture del forno sono la fornace vanilla, non un nuovo asset definitivo.
- **Pala non collegata:** l'interazione diretta con una pala occupata/vuota rimane futuro. Il forno usa la GUI con e senza utensile; non inventare una pala sostitutiva.
- **Pizza cotta servibile non ancora implementata:** l'item `cooked_pizza` non si mangia intero e non è ancora piazzabile per recuperare quattro fette.
- **Fornace e affumicatore vanilla** non trasformano ancora pizze crude preservandone i dati. Servirà l'implementazione delle ricette/componenti dinamici, non una smelting recipe statica che scarta gli ingredienti.
- Il rendering dei topping rimane l'attuale placeholder “item frame”. Rimangono in roadmap la nuova texture `raw_pizza` e quelle delle pizze cotte.
