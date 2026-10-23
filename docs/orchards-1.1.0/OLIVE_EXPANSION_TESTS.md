# Orchard 1.1.0 — Porte, botole, aging rack e barche d'ulivo

**Branch:** `feature/orchards-1.1.0`. **Stato:** implementati tecnicamente; **tutti i seguenti collaudi manuali sono ancora da confermare**. Registrare solo risultati dichiarati dall'autore.

## Checklist per il test in Minecraft

- [ ] **Creativa e aspetto**: porte, botole, scaffale, barca e barca con baule sono presenti una volta; nomi IT/EN e texture senza viola/nero.
- [ ] **Porte**: entrambe le metà appaiono correttamente; prova tutte le direzioni, cerniera destra/sinistra, apertura a mano, leve/redstone, collisioni e suoni; rompendo la porta si recupera una sola porta.
- [ ] **Botole**: orientamenti, posizioni alte e basse, apertura/chiusura a mano e con redstone; con acqua e dopo il riavvio del mondo; rottura restituisce un oggetto.
- [ ] **Ricette**: sei assi in due colonne → tre porte; sei assi su due righe → due botole. Verificare l'aging rack con **4 olive fence + 1 olive planks** nella disposizione degli altri rack.
- [ ] **Aging rack, livello inferiore**: inserire un formaggio idoneo, verificare la resa grafica, farlo maturare e recuperarlo; nessuna duplicazione/perdita.
- [ ] **Aging rack, livello superiore**: inserire un formaggio diverso; i due ripiani funzionano indipendentemente e non accelerano/alterano la stagionatura.
- [ ] **Persistenza aging rack**: salvare/riavviare un mondo con due formaggi in maturazione, verificare la persistenza e il risultato; rompere lo scaffale e controllare che rack e contenuti vengano recuperati correttamente.
- [ ] **Barca normale**: creare l'oggetto con 5 assi, posizionarla sull'acqua e sulla terraferma, salire, remare, scendere, rompere e recuperare la barca; non deve perdere la texture 3D né essere resa come barca vanilla di quercia.
- [ ] **Barca con baule**: crafting da barca d'ulivo + baule, piazzamento, movimento e interazione con l'inventario; inserire oggetti, salvare/riavviare, rompere e verificare la gestione dei contenuti.
- [ ] **Multiplayer/server**: verificare funzionamento su server dedicato, desincronizzazioni, spawn delle barche e persistenza dei due rack dopo riavvio (non coperto dalla CI).
- [ ] **Regressioni**: i precedenti 12 aging rack e i cinque blocchi di legno già testati continuano a funzionare. I sei blocchi della Fase A (scale, lastre, ecc.) hanno una checklist separata e non vanno marcati superati implicitamente.

## Asset riutilizzati

Le PNG originali sono rimaste immutate: `olive_door_bottom.png`, `olive_door_top.png`, `olive_door.png`, `olive_trapdoor.png`, `olive_planks.png`, `item/olive_boat.png`, `item/olive_chest_boat.png`, `entity/boat/olive.png` ed `entity/chest_boat/olive.png`. **Non usare materiali delle vecchie texture di Rustic** nei nuovi modelli.

Se un test fallisce, riportare il blocco, la situazione e il comportamento osservato; non correggere alla cieca, aggiornare la roadmap.
