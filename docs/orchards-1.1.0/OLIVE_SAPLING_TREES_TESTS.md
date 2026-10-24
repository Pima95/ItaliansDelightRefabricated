# Orchard 1.1.0 — Collaudo germoglio e alberi d'ulivo

**Branch:** `feature/orchards-1.1.0`. Non confondere CI verde con prove eseguite in Minecraft.

- [ ] **Inventario Creativa:** `italiansdelight:olive_sapling` presente una sola volta; icona 16×16, modello a croce e nome italiano.
- [ ] **Piazzamento e drop:** germoglio su terra, erba, podzol ecc., con luce appropriata; rompendolo deve restituire se stesso.
- [ ] **Nuovo prototipo procedurale 1×1:** coltivare almeno 8–12 ulivi da un singolo germoglio, con farina d'ossa e crescita naturale. Verificare tronco leggermente piegato, **2–3 rami connessi realmente al fusto**, foglie in più gruppi rialzati, nessuna chioma sul terreno e nessuna forma identica alla quercia. Valutare la forma prima di dichiararla definitiva.
- [ ] **Crescita 2×2:** quattro germogli a quadrato con spazio libero crescono **in un unico ulivo secolare**, con tronco 2×2, stessa texture dei tronchi e ampia chioma; controllare anche rotazione/altezza.
- [ ] **Fallimento sicuro:** se sopra i germogli ci sono ostacoli, la crescita non sostituisce costruzioni né perde i germogli.
- [ ] **Foglie naturali e drop:** dopo aver abbattuto un ulivo, le foglie decadono e possono lasciare germogli (5% base), anche con Fortuna, ma non olive né mele; cesoie e Silk Touch devono dare le foglie.
- [ ] **Worldgen DISATTIVATO:** entrare in **chunk nuovi** di pianure, savane e altopiani della savana: non devono più comparire ulivi naturali, né normali né secolari. Quelli eventualmente già generati nei vecchi chunk rimangono. La crescita dei germogli deve continuare indipendentemente dal bioma.
- [ ] **Persistenza:** salvare e riaprire mondo con germogli e alberi cresciuti; server dedicato e multiplayer.
- [ ] **Aspetto nuovo germoglio:** la chioma del germoglio è ora grigio-verde argentata, con la **stessa palette delle foglie 16×16**, stelo marrone originale invariato. Verificare contrasto al sole e all'ombra.
- [ ] **Aspetto generale:** il normale usa ora `OliveTrunkPlacer`/`OliveFoliagePlacer` da collaudare; il secolare 2×2 mantiene il prototipo quercia scura e **non è definitivo**. Nessuna riattivazione del worldgen senza approvazione delle forme.

**Nota per test rapido:** in Creativa usare il comando `/give @s italiansdelight:olive_sapling 4`, posizionare singolo o 2×2 e usare farina d'ossa. Per il test strutturale si può usare `/place feature italiansdelight:olive_tree` e `/place feature italiansdelight:ancient_olive_tree` in una zona libera, se i permessi lo consentono.
