# Secondi — checklist di collaudo

- Compilare il branch e controllare i log di caricamento ricette, tag, loot table, item models e blockstates.
- Verificare 17 ricette: 2 Feast nella Crafting Table, 3 frittate e 12 secondi individuali nella Cooking Pot. Nessuna ricetta duplicata delle porzioni.
- Provare carne e pesce interi e affettati con i tag FDR raw_beef, raw_chicken e raw_cod; le polpette richiedono minced_beef. I Feast richiedono un solo pollo cotto intero come il Roast Chicken FDR.
- Ogni pollo intero: piazzamento in quattro direzioni; prelevare esattamente 4 porzioni con 4 ciotole; senza ciotola non prelevare; recipiente vuoto; rottura a 4/3/2/1/0 porzioni. Il blocco intero si recupera solo a 4; dopo aver servito si recupera solo la ciotola.
- Ogni frittata: 4 fette senza ciotola; rimozione automatica all'ultima fetta; a 4 si recupera la frittata intera, a 3/2/1 soltanto il numero corrispondente di fette. Controllare salvataggio/riavvio e multiplayer.
- Verificare le 4 direzioni, hitbox e modelli in ogni stato; controllo visivo delle texture e dei condimenti.
- Verificare la restituzione delle ciotole quando si consumano porzioni di pollo e secondi in ciotola; nessun recipiente per gli alimenti singoli.
- JEI: ricette Cooking Pot e Crafting Table e informazioni localizzate di ciascun blocco e della rispettiva porzione.
- Test di compilazione e gameplay non ancora eseguiti.

## Revisione grafica dei secondi

- Piatti singoli e porzioni di pollo: recipiente canonico della carbonara; controllare i dettagli a risoluzione nativa.
- Frittate: icona tonda, blocco quadrato 12×12×3; texture top, side e inner separate. Verificare che la sezione interna sia visibile solo sulle facce esposte e che le hitbox ruotino correttamente.
- Feast di pollo: parent dei cinque modelli originali FDR, stessa geometria e BlockShapes.ROAST_CHICKEN_SHAPES/TRAY_SHAPE, texture ricolorate; licenza MIT e attribuzione incluse negli asset. Verificare tutti gli stadi in gioco.
- Ricette, drop, porzioni e valori nutritivi invariati.

- Verificare i due nuovi crafting da nove ingredienti e il tooltip Placeable/Piazzabile in corsivo grigio scuro solo sui due polli interi. Verificare che le ricette delle due crostate con idromele siano assenti in JEI e che quelle base e con sidro rimangano disponibili.

- Provare carote/fette in spezzatino e polli; patate/fette crude nei tre secondi Cooking Pot, cotte nei due polli. Verificare le due sequenze per ottenere quattro fette di patata cotta e il condimento pizza con entrambe le fette (un'unica identità canonica, nessuna doppia applicazione).
