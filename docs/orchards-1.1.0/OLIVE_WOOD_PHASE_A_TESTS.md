# Orchard Update 1.1.0 — Verifica manuale fase A: blocchi da costruzione

**Branch:** `feature/orchards-1.1.0`  
**Stato:** **IN ATTESA DI COLLAUDO IN-GAME**. Nessuno dei controlli qui sotto è ancora confermato dall'autore.  
**Modifiche da verificare:** `olive_stairs`, `olive_slab`, `olive_fence`, `olive_fence_gate`, `olive_button`, `olive_pressure_plate`.

**Asset approvato riutilizzato:** `textures/block/olive_planks.png`. Nessun PNG nuovo o sovrascritto. Texture `log_olive.png`, `leaves_olive.png` e `sapling_olive.png` di Rustic escluse dai nuovi modelli.

## Checklist da eseguire in un mondo di prova

- [ ] **Creative e traduzioni.** Tutti i sei blocchi compaiono una volta nella scheda Italian's Delight con nomi corretti IT/EN; nessuna icona viola/nera.
- [ ] **Scale.** Posizionamento nelle quattro direzioni, metà inferiore/superiore, angoli interni/esterni e waterlogging; ogni forma mantiene la texture e l'orientamento attesi.
- [ ] **Lastre.** Posizionamento inferiore, superiore e doppio; unione di due lastre identiche; waterlogging per lastre singole; rottura della doppia lastra rilascia la quantità corretta (due lastre).
- [ ] **Recinzioni.** Connessione reciproca e con recinzioni vanilla/pareti previste; palo e lati ruotati correttamente; hitbox e collisione normali; drop corretto.
- [ ] **Cancelletti.** Apertura/chiusura a mano, orientamento nelle quattro direzioni, comportamento accanto ai muri, passaggio e collisione; redstone apre/chiude senza inconsistenze.
- [ ] **Pulsanti.** Attivazione a mano su pavimento, parete e soffitto; orientamento, clic e impulso redstone. Verificare anche il comportamento con le frecce previsto per i pulsanti di legno.
- [ ] **Pedane.** Attivazione con giocatori/mob e segnale redstone; ritorno allo stato sollevato, texture e drop corretti.
- [ ] **Ricette, sopravvivenza e fuoco.** Crafting di tutti i sei oggetti, recupero dei blocchi con gli attrezzi opportuni, combustione dei blocchi lignei. Confrontare le quantità con le controparti vanilla.
- [ ] **Compatibilità e stabilità.** Chiudere/riaprire il mondo; testare ricette e tag vanilla/JEI se disponibili; assenza di errori nel log. Server dedicato/multiplayer da testare separatamente.

### Quantità previste nelle ricette

| Input | Output |
|---|---|
| 6 assi (a scaletta) | 4 scale d'ulivo |
| 3 assi (riga) | 6 lastre d'ulivo |
| 4 assi + 2 bastoni | 3 recinzioni d'ulivo |
| 2 assi + 4 bastoni | 1 cancelletto d'ulivo |
| 1 asse | 1 pulsante d'ulivo |
| 2 assi | 1 pedana a pressione d'ulivo |

**Nota:** i sei test già superati del primo lotto (tronchi, legno e assi) sono registrati separatamente in [OLIVE_WOOD_TESTS.md](OLIVE_WOOD_TESTS.md). Non ereditare la conferma per questi sei nuovi blocchi. Se tutti i test avranno esito positivo, potremo registrarlo qui e passare alla **Fase B: porte e botole**.
