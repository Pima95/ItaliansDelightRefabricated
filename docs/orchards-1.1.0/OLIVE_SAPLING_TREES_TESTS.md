# Orchard 1.1.0 — Collaudo germoglio e alberi d'ulivo

**Branch:** `feature/orchards-1.1.0`. Non confondere CI verde con prove eseguite in Minecraft.

- [ ] **Inventario Creativa:** `italiansdelight:olive_sapling` presente una sola volta; icona 16×16, modello a croce e nome italiano.
- [ ] **Piazzamento e drop:** germoglio su terra, erba, podzol ecc., con luce appropriata; rompendolo deve restituire se stesso.
- [ ] **Correzione chioma 1×1:** un germoglio con farina d'ossa cresce in ulivo provvisorio con tronco `olive_log` e foglie `olive_leaves`, **chioma IN ALTO e non sul suolo**, senza foglie a livello dei piedi; crescita naturale dopo un certo tempo. La forma finale contorta e mediterranea è da progettare.
- [ ] **Crescita 2×2:** quattro germogli a quadrato con spazio libero crescono **in un unico ulivo secolare**, con tronco 2×2, stessa texture dei tronchi e ampia chioma; controllare anche rotazione/altezza.
- [ ] **Fallimento sicuro:** se sopra i germogli ci sono ostacoli, la crescita non sostituisce costruzioni né perde i germogli.
- [ ] **Foglie naturali e drop:** dopo aver abbattuto un ulivo, le foglie decadono e possono lasciare germogli (5% base), anche con Fortuna, ma non olive né mele; cesoie e Silk Touch devono dare le foglie.
- [ ] **Worldgen DISATTIVATO:** entrare in **chunk nuovi** di pianure, savane e altopiani della savana: non devono più comparire ulivi naturali, né normali né secolari. Quelli eventualmente già generati nei vecchi chunk rimangono. La crescita dei germogli deve continuare indipendentemente dal bioma.
- [ ] **Persistenza:** salvare e riaprire mondo con germogli e alberi cresciuti; server dedicato e multiplayer.
- [ ] **Aspetto nuovo germoglio:** la chioma del germoglio è ora grigio-verde argentata, con la **stessa palette delle foglie 16×16**, stelo marrone originale invariato. Verificare contrasto al sole e all'ombra.
- [ ] **Aspetto forme NON definitive:** la forma di quercia e quercia scura è soltanto un prototipo tecnico. Non approvare il worldgen né trattare l'architettura degli ulivi come definitiva finché non saranno progettati generatori originali.

**Nota per test rapido:** in Creativa usare il comando `/give @s italiansdelight:olive_sapling 4`, posizionare singolo o 2×2 e usare farina d'ossa. Per il test strutturale si può usare `/place feature italiansdelight:olive_tree` e `/place feature italiansdelight:ancient_olive_tree` in una zona libera, se i permessi lo consentono.
