# Orchard 1.1.0 — Collaudo germoglio e alberi d'ulivo

**Branch:** `feature/orchards-1.1.0`. Non confondere CI verde con prove eseguite in Minecraft.

- [ ] **Inventario Creativa:** `italiansdelight:olive_sapling` presente una sola volta; icona 16×16, modello a croce e nome italiano.
- [ ] **Piazzamento e drop:** germoglio su terra, erba, podzol ecc., con luce appropriata; rompendolo deve restituire se stesso.
- [ ] **Crescita 1×1:** un solo germoglio con farina d'ossa cresce in ulivo normale, con tronco `olive_log`, foglie `olive_leaves` e chioma non troppo geometrica; crescita naturale dopo un certo tempo.
- [ ] **Crescita 2×2:** quattro germogli a quadrato con spazio libero crescono **in un unico ulivo secolare**, con tronco 2×2, stessa texture dei tronchi e ampia chioma; controllare anche rotazione/altezza.
- [ ] **Fallimento sicuro:** se sopra i germogli ci sono ostacoli, la crescita non sostituisce costruzioni né perde i germogli.
- [ ] **Foglie naturali e drop:** dopo aver abbattuto un ulivo, le foglie decadono e possono lasciare germogli (5% base), anche con Fortuna, ma non olive né mele; cesoie e Silk Touch devono dare le foglie.
- [ ] **Worldgen:** in **chunk nuovi** visitare pianure, savane e altopiani della savana; verificare alberi rari, specialmente i secolari, senza eccessiva frequenza; nessuna generazione su costruzioni o altri biomi.
- [ ] **Persistenza:** salvare e riaprire mondo con germogli e alberi cresciuti; server dedicato e multiplayer.
- [ ] **Aspetto:** valutare la palette argentata delle foglie e la forma di entrambe le chiome. Si potranno migliorare le forme senza cambiare i blocchi esistenti.

**Nota per test rapido:** in Creativa usare il comando `/give @s italiansdelight:olive_sapling 4`, posizionare singolo o 2×2 e usare farina d'ossa. Per il test strutturale si può usare `/place feature italiansdelight:olive_tree` e `/place feature italiansdelight:ancient_olive_tree` in una zona libera, se i permessi lo consentono.
