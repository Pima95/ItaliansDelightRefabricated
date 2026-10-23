# Bug noti — Italian's Delight Refabricated

Questo documento raccoglie i problemi segnalati e ancora da risolvere nel progetto, indipendentemente dai singoli aggiornamenti.

## Problemi aperti

### BUG-001 — Scamorza e Provolone posizionabili a terra

- **Stato:** **RISOLTO — confermato dall'autore il 23 ottobre 2026** su `feature/pizza-1.0.0`.
- **Elementi interessati:** Scamorza fresca, Scamorza, Scamorza affumicata, Provolone fresco e Provolone stagionato.
- **Causa verificata nel codice:** `AgingCheeseItem.useOn` creava il blocco di stagionatura su superfici solide per qualunque `AgingCheeseType`, senza controllare `agesOnSurface()`. Anche Provolone era incluso fra i tipi permessi da quel metodo.
- **Correzione:** `agesOnSurface()` ora esclude esplicitamente Provolone e le due varianti di Scamorza. `AgingCheeseItem.useOn` rifiuta per questi tipi il posizionamento a terra con `InteractionResult.PASS`, **senza consumare l'item**. Tutte le interazioni dell'uncino sono lasciate intatte, perché sono gestite dal `CheeseHookBlock` prima dell'uso dell'item. Parmigiano Reggiano, Pecorino Romano e Gorgonzola mantengono la meccanica preesistente di posizionamento/stagionatura su superficie.
- **Compatibilità:** i blocchi `aging_cheese` già presenti nei vecchi mondi non vengono cancellati o convertiti; cambiano solo i nuovi tentativi di posizionamento tramite item.
- **Test Survival richiesti:**
  1. Provare ciascuna delle cinque varianti di formaggio citate su un blocco pieno e su una slab superiore; non deve crearsi un blocco di formaggio né diminuire lo stack.
  2. Provare tutti e cinque gli item su un uncino libero; devono agganciarsi normalmente. Rimuoverli con mano vuota e verificare il rimborso dell'item corretto.
  3. Verificare che Scamorza fresca e Provolone fresco continuino a progredire sull'uncino, senza alterazioni dei tempi. La variante affumicata non deve acquisire un nuovo processo.
  4. Come regressione, posizionare Parmigiano Reggiano, Pecorino Romano e Gorgonzola nelle varianti già consentite e verificare i normali blocchi di stagionatura.
  5. Ripetere per quanto possibile su mondo riavviato e server; il test statico in CI non simula interazioni Minecraft.
- **Rafforzamento del 22 ottobre 2026:** oltre al blocco già esistente per tutti e cinque gli item da appendere, `CheeseHookBlock.getStateForPlacement` controlla il supporto superiore **prima** di piazzare e consumare l'uncino. `CheeseHookBlockEntity.insert/remove` verificano il successo delle modifiche di BlockState prima di consumare o rimborsare il prodotto: nessun prodotto regalato se l'operazione fallisce. L'ammissibilità sulla superficie di Scamorza/Provolone non è stata allargata.
- **Chiusura:** correzione nel codice rafforzata e controlli statici aggiunti; segnare «risolto in gioco» solo dopo conferma delle prove Survival indicate.


### BUG-002 — Mozzarella intera applicabile alla pizza, ma invisibile

- **Stato:** **risolto e confermato funzionante dall'autore il 20 ottobre 2026** nel branch `feature/pizza-1.0.0`: mozzarella intera rifiutata senza falsa animazione, nessuna texture legacy viola/nera e rimborso singolo dello stato storico.
- **Item interessato:** `italiansdelight:mozzarella` (mozzarella intera) vs `italiansdelight:mozzarella_slice` (fetta approvata).
- **Causa accertata:** `PizzaToppingCatalog` esclude correttamente l'item intero, ma `PizzaDoughPlacement` aveva ancora il vecchio ramo prototipale che lo consumava e impostava `PizzaDoughBlock.MOZZARELLA=true`. Il renderer della pizza cruda usava solamente `PizzaComposition.applications()`, quindi **ignorava quel vecchio flag**.
- **Correzione 1 (gameplay):** sulla base già stesa, il clic con mozzarella intera viene gestito senza modificare il blocco né consumare alcun item. Da questa versione **non si possono creare nuove applicazioni legacy**. Mozzarella a fette e altri topping approvati non vengono modificati.
- **Correzione 2 (compatibilità):** il renderer della **pizza cruda** visualizza le vecchie mozzarelle intere precedentemente applicate e salvate nel `BlockState`; usa lo stesso modello placeholder dei condimenti correnti, senza nuovi asset. Il renderer della pizza cotta già conservava questo supporto.
- **Salvataggi precedenti:** **non** eliminare né rinominare `MOZZARELLA` dal BlockState o `legacy_mozzarella` da `PizzaItemData`. La rimozione preesistente via **Shift + clic destro a mano vuota** restituisce un solo item intero, solo quando viene rimossa l'applicazione legacy. La rottura e la pala conservano l'informazione all'interno della pizza-item. Nessuna migrazione distruttiva.
- **Seconda correzione, 20 ottobre 2026 (da verificare nel client):** il clic con mozzarella intera seguiva ancora un ramo `InteractionResult.SUCCESS`, provocando l'animazione della mano pur non applicando nulla. Rimosso il ramo: gli item esclusi usano `PASS`, come gli altri. Inoltre `blockstates/pizza_dough.json` includeva ancora il vecchio modello multipart `pizza_topping_mozzarella` quando `mozzarella=true`; il modello visualizzava la texture mancante viola/nera. Eliminato **solo il riferimento multipart**: la mozzarella storica è ancora renderizzata dal `PizzaToppingRenderer` già implementato, con l'item esistente. Nessun file PNG sovrascritto.
- **Rimborso legacy:** con `mozzarella=true`, **Shift + clic destro a mano vuota** deve restituire una mozzarella una sola volta; è un recupero intenzionale dell'ingrediente salvato, anche se il blocco è stato creato artificialmente con `/setblock`.
- **Verifica:** seguire [LEGACY_MOZZARELLA_TESTS.md](pizza-1.0.0/LEGACY_MOZZARELLA_TESTS.md). Confermare assenza di animazione da falso piazzamento, nessun modello viola/nero e rimborso singolo, oltre a salvataggio/cottura, prima di segnare «risolto».



### BUG-003 — Perdita degli oggetti se si rompe il supporto di un uncino o formaggio

- **Stato:** **RISOLTO — confermato dall'autore il 23 ottobre 2026** su `feature/pizza-1.0.0`.
- **Causa:** `AgingCheeseBlock.updateShape` e `CheeseHookBlock.updateShape` restituivano direttamente `Blocks.AIR` quando il blocco di supporto scompariva. Ciò poteva eludere il percorso `getDrops` e perdere il formaggio (o il prodotto appeso insieme all'uncino).
- **Correzione:** i blocchi restano intatti per un tick programmato; se il supporto è ancora invalido, `level.destroyBlock(pos, true)` usa i drop vanilla e i rispettivi metodi `getDrops`. L'uncino elimina anche lo spazio tecnico riservato sotto. Progressi di stagionatura incompleti non vengono trasferiti all'item, come già previsto.
- **Test Survival:** (1) rompere il soffitto sopra un uncino vuoto e sopra un uncino con Scamorza/Provolone o salume in lavorazione, controllando **un solo uncino e un solo prodotto**, nessun residuo di blocco invisibile; (2) rompere il supporto sotto Gorgonzola/Parmigiano/Pecorino fresco o stagionato: **un solo formaggio corretto**; (3) ripetere con il supporto reso invalido per cambio stato, `doTileDrops` disabilitato, salvataggio/ricarica e in multiplayer; (4) verificare che le normali interazioni di rimozione a mano e i tempi di lavorazione siano invariati.
- **Rafforzamento del 22 ottobre 2026:** `CheeseHookBlock.getDrops` legge l'oggetto appeso direttamente da `state.getValue(CHEESE).stack()`, fonte autorevole anche quando il contesto loot **non contiene la BlockEntity**; aggiunge una sola copia. `CheeseHookBlockEntity.serverTick` e `AgingCheeseBlockEntity.serverTick` distruggono con drop vanilla il blocco senza supporto al primo tick dopo il caricamento, senza attendere un aggiornamento dei vicini. Anche le forme già mature e gli uncini vuoti sono coperti. La rimozione manuale di un formaggio da superficie restituisce l'item **solo dopo** che `level.removeBlock(pos,false)` ha avuto successo: evita doppi prelievi.
- **Chiusura:** il codice gestisce ora anche i casi di BlockEntity assente / blocco orfano dopo caricamento. Verifiche statiche CI estese; **non dichiarare testato in gioco** finché non arrivano i risultati Survival/server. Gli asset dei formaggi e dei salumi non sono stati modificati.

**Conferma 23 ottobre 2026 (BUG-001 e BUG-003):** l'autore riferisce che le ultime correzioni sono funzionanti in gioco. Entrambi i bug sono considerati **risolti**; le checklist di regressione approfondite (per esempio multiplayer e stati artificialmente corrotti) restano disponibili senza bloccare la chiusura dei bug.

## Convenzioni

- **Aperto:** problema segnalato, non ancora risolto.
- **In lavorazione:** è in corso una correzione.
- **Da verificare:** correzione apportata, ma non ancora confermata in gioco.
- **Risolto:** correzione confermata tramite test.

Aggiornare lo stato di ogni bug soltanto dopo le relative modifiche e verifiche.
