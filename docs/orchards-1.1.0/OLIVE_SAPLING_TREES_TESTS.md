# Orchard 1.1.0 — Collaudo germoglio e alberi d'ulivo

**Branch:** `feature/orchards-1.1.0`. Non confondere CI verde con prove eseguite in Minecraft.

- [ ] **Inventario Creativa:** `italiansdelight:olive_sapling` presente una sola volta; icona 16×16, modello a croce e nome italiano.
- [ ] **Piazzamento e drop:** germoglio su terra, erba, podzol ecc., con luce appropriata; rompendolo deve restituire se stesso.
- [ ] **Ulivo normale compatto 1×1:** far crescere almeno 20–30 germogli in un'area libera. Il legno del tronco arriva al massimo alla quota +2 dal germoglio (3 livelli di blocchi); la chioma di 3 strati arriva indicativamente alla quota +4 (altezza totale circa 5 blocchi). Nessun palo verticale alto e nessuna lunga scalinata di tronchi. Tronco basso con almeno una piega; il prolungamento della chioma è corto e piega lateralmente, senza trave diritta.
- [ ] **Corteccia sui raccordi:** verificare da ogni lato che nelle pieghe e nei rami non siano visibili facce arancioni con gli anelli di taglio. I raccordi usano il blocco `italiansdelight:olive_wood` (corteccia su tutte le facce); la base usa `olive_log`. Verificare che entrambi i blocchi siano abbattibili e che le foglie rimangano collegate.
- [ ] **Ulivo biforcuto raro (circa 10%):** generare 50–100 esemplari, senza aspettarsi frequenza esatta in piccoli campioni. Un ramo segue la piega principale e può allargarsi sul lato, il secondo parte dalla base della piega su un asse perpendicolare, più corto. I due rami devono essere collegati tramite facce, non simmetrici e senza monconi orizzontali scoperti. La biforcazione non deve aumentare l'altezza totale rispetto all'ulivo singolo.
- [ ] **Terreno/ostacoli:** testare crescita con erba alta e vicino a ostacoli, verificando che il generatore non sostituisca blocchi solidi e non lasci rametti orizzontali tronchi.
- [ ] **Crescita 2×2:** quattro germogli a quadrato con spazio libero crescono **in un unico ulivo secolare**, con tronco 2×2, stessa texture dei tronchi e ampia chioma; controllare anche rotazione/altezza.
- [ ] **Fallimento sicuro:** se sopra i germogli ci sono ostacoli, la crescita non sostituisce costruzioni né perde i germogli.
- [ ] **Foglie naturali e drop:** dopo aver abbattuto un ulivo, le foglie decadono e possono lasciare germogli (5% base), anche con Fortuna, ma non olive né mele; cesoie e Silk Touch devono dare le foglie.
- [ ] **Worldgen DISATTIVATO:** entrare in **chunk nuovi** di pianure, savane e altopiani della savana: non devono più comparire ulivi naturali, né normali né secolari. Quelli eventualmente già generati nei vecchi chunk rimangono. La crescita dei germogli deve continuare indipendentemente dal bioma.
- [ ] **Persistenza:** salvare e riaprire mondo con germogli e alberi cresciuti; server dedicato e multiplayer.
- [ ] **Aspetto nuovo germoglio:** la chioma del germoglio è ora grigio-verde argentata, con la **stessa palette delle foglie 16×16**, stelo marrone originale invariato. Verificare contrasto al sole e all'ombra.
- [ ] **Aspetto generale:** il normale usa ora `OliveTrunkPlacer`/`OliveFoliagePlacer` da collaudare; il secolare 2×2 mantiene il prototipo quercia scura e **non è definitivo**. Nessuna riattivazione del worldgen senza approvazione delle forme.

**Nota per test rapido:** in Creativa usare il comando `/give @s italiansdelight:olive_sapling 4`, posizionare singolo o 2×2 e usare farina d'ossa. Per il test strutturale si può usare `/place feature italiansdelight:olive_tree` e `/place feature italiansdelight:ancient_olive_tree` in una zona libera, se i permessi lo consentono.
