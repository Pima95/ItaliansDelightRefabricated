# Bug noti — Italian's Delight Refabricated

Questo documento raccoglie i problemi segnalati e ancora da risolvere nel progetto, indipendentemente dai singoli aggiornamenti.

## Problemi aperti

### BUG-001 — Scamorza e Provolone posizionabili a terra

- **Stato:** aperto — segnalato dall'autore; correzione non ancora implementata.
- **Elementi interessati:** Scamorza e Provolone.
- **Descrizione:** è possibile posizionare Scamorza e Provolone direttamente a terra. Il posizionamento sul terreno è segnalato come comportamento indesiderato.
- **Comportamento atteso:** impedire il posizionamento a terra non previsto, preservando le interazioni legittime già esistenti, incluse quelle con l'uncino per i formaggi.
- **Da verificare prima della correzione:** quali varianti dei due formaggi siano coinvolte, se il problema si presenti su ogni superficie, e che l'eventuale correzione non interferisca con l'aggancio e con le meccaniche di stagionatura/affumicatura.


### BUG-002 — Mozzarella intera applicabile alla pizza, ma invisibile

- **Stato:** da verificare in gioco — **correzione implementata il 20 ottobre 2026** nel branch `feature/pizza-1.0.0`.
- **Item interessato:** `italiansdelight:mozzarella` (mozzarella intera) vs `italiansdelight:mozzarella_slice` (fetta approvata).
- **Causa accertata:** `PizzaToppingCatalog` esclude correttamente l'item intero, ma `PizzaDoughPlacement` aveva ancora il vecchio ramo prototipale che lo consumava e impostava `PizzaDoughBlock.MOZZARELLA=true`. Il renderer della pizza cruda usava solamente `PizzaComposition.applications()`, quindi **ignorava quel vecchio flag**.
- **Correzione 1 (gameplay):** sulla base già stesa, il clic con mozzarella intera viene gestito senza modificare il blocco né consumare alcun item. Da questa versione **non si possono creare nuove applicazioni legacy**. Mozzarella a fette e altri topping approvati non vengono modificati.
- **Correzione 2 (compatibilità):** il renderer della **pizza cruda** visualizza le vecchie mozzarelle intere precedentemente applicate e salvate nel `BlockState`; usa lo stesso modello placeholder dei condimenti correnti, senza nuovi asset. Il renderer della pizza cotta già conservava questo supporto.
- **Salvataggi precedenti:** **non** eliminare né rinominare `MOZZARELLA` dal BlockState o `legacy_mozzarella` da `PizzaItemData`. La rimozione preesistente via **Shift + clic destro a mano vuota** restituisce un solo item intero, solo quando viene rimossa l'applicazione legacy. La rottura e la pala conservano l'informazione all'interno della pizza-item. Nessuna migrazione distruttiva.
- **Verifica:** seguire [LEGACY_MOZZARELLA_TESTS.md](pizza-1.0.0/LEGACY_MOZZARELLA_TESTS.md). Confermare test in client Survival, riposizionamento vecchie pizze, rendering crudo/cotto, salvataggio e rimborso prima di segnare «risolto».



## Convenzioni

- **Aperto:** problema segnalato, non ancora risolto.
- **In lavorazione:** è in corso una correzione.
- **Da verificare:** correzione apportata, ma non ancora confermata in gioco.
- **Risolto:** correzione confermata tramite test.

Aggiornare lo stato di ogni bug soltanto dopo le relative modifiche e verifiche.
