# Orchard 1.1.0 — Collaudo germoglio e alberi d'ulivo

**Branch:** `feature/orchards-1.1.0`. Non confondere CI verde con prove eseguite in Minecraft.

- [ ] **Inventario Creativa:** `italiansdelight:olive_sapling` presente una sola volta; icona 16×16, modello a croce e nome italiano.
- [ ] **Piazzamento e drop:** germoglio su terra, erba, podzol ecc., con luce appropriata; rompendolo deve restituire se stesso.
- [ ] **Ulivo normale 1×1 — nuova forma storta:** generare almeno 20–30 alberi singoli con farina d'ossa in un'area libera. Il tronco deve avere una piega bassa obbligatoria (nessun pilastro verticale perfetto) e il ramo principale deve curvare e salire, con **massimo due spostamenti orizzontali**, senza tronchi sospesi o lunghi rami orizzontali da ciliegio. La chioma deve rimanere rialzata, non appoggiarsi al terreno, ed essere irregolare tra semi diversi.
- [ ] **Variante biforcuta rara (circa 1/10):** generare un campione più ampio (indicativamente 50–100) senza aspettarsi una frequenza esatta nei piccoli campioni. Oltre al tronco storto comune, il secondo ramo parte dal basso, è più corto e si sviluppa lateralmente senza specchiarsi con il primo. Controllare connessione tra i log **faccia a faccia**, assenza di ammassamenti all'attacco dei rami e chiome non geometriche.
- [ ] **Crescita 2×2:** quattro germogli a quadrato con spazio libero crescono **in un unico ulivo secolare**, con tronco 2×2, stessa texture dei tronchi e ampia chioma; controllare anche rotazione/altezza.
- [ ] **Fallimento sicuro:** se sopra i germogli ci sono ostacoli, la crescita non sostituisce costruzioni né perde i germogli.
- [ ] **Foglie naturali e drop:** dopo aver abbattuto un ulivo, le foglie decadono e possono lasciare germogli (5% base), anche con Fortuna, ma non olive né mele; cesoie e Silk Touch devono dare le foglie.
- [ ] **Worldgen DISATTIVATO:** entrare in **chunk nuovi** di pianure, savane e altopiani della savana: non devono più comparire ulivi naturali, né normali né secolari. Quelli eventualmente già generati nei vecchi chunk rimangono. La crescita dei germogli deve continuare indipendentemente dal bioma.
- [ ] **Persistenza:** salvare e riaprire mondo con germogli e alberi cresciuti; server dedicato e multiplayer.
- [ ] **Aspetto nuovo germoglio:** la chioma del germoglio è ora grigio-verde argentata, con la **stessa palette delle foglie 16×16**, stelo marrone originale invariato. Verificare contrasto al sole e all'ombra.
- [ ] **Aspetto generale:** il normale usa ora `OliveTrunkPlacer`/`OliveFoliagePlacer` da collaudare; il secolare 2×2 mantiene il prototipo quercia scura e **non è definitivo**. Nessuna riattivazione del worldgen senza approvazione delle forme.

**Nota per test rapido:** in Creativa usare il comando `/give @s italiansdelight:olive_sapling 4`, posizionare singolo o 2×2 e usare farina d'ossa. Per il test strutturale si può usare `/place feature italiansdelight:olive_tree` e `/place feature italiansdelight:ancient_olive_tree` in una zona libera, se i permessi lo consentono.
