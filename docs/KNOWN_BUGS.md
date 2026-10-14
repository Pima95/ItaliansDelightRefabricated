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

- **Stato:** aperto — segnalato dall'autore il 14 ottobre 2026; nessuna correzione implementata.
- **Ambito:** Pizza Update 1.0.0, impasto completamente steso (`stage=4`).
- **Item interessato:** `italiansdelight:mozzarella` (mozzarella **intera**, da non confondere con `italiansdelight:mozzarella_slice`).
- **Riproduzione:** stendere completamente l'impasto, impugnare una mozzarella intera e usare il clic destro sulla pizza.
- **Comportamento osservato:** il gioco permette di applicare la mozzarella intera alla pizza, ma non ne mostra il condimento sul blocco.
- **Comportamento atteso:** rispettare la whitelist della Pizza Update: come topping deve essere utilizzabile **solo la mozzarella a fette** (`mozzarella_slice`), non la mozzarella intera. La pizza non deve consumare l'item intero, né applicare il flag legacy `mozzarella=true` con grafica invisibile.
- **Probabile area da esaminare (non ancora diagnosticata):** gestione legacy del clic in `PizzaDoughPlacement.java` (`ModItems.MOZZARELLA`/`PizzaDoughBlock.MOZZARELLA`); modello multipart `pizza_topping_mozzarella`; nuova visualizzazione degli ingredienti persistenti in `PizzaToppingRenderer.java` (che usa invece le applicazioni canoniche). Verificare prima di rimuovere il prototipo che le pizze già salvate mantengano un recupero sicuro dell'item, senza perdite o duplicazioni.
- **Regressioni da evitare:** non alterare `mozzarella_slice`, la logica dei quattro quarti, le applicazioni di topping solidi approvate o il recupero di pizza cruda/impasto.


## Convenzioni

- **Aperto:** problema segnalato, non ancora risolto.
- **In lavorazione:** è in corso una correzione.
- **Da verificare:** correzione apportata, ma non ancora confermata in gioco.
- **Risolto:** correzione confermata tramite test.

Aggiornare lo stato di ogni bug soltanto dopo le relative modifiche e verifiche.
