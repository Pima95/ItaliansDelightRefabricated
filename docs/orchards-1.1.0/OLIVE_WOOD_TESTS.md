# Orchard Update 1.1.0 — Collaudo del primo lotto di legno d'ulivo

**Branch:** `feature/orchards-1.1.0`  
**Data della conferma dell'autore:** 24 ottobre 2026  
**Stato:** **6/6 test manuali dichiarati superati dall'autore**.  
**Ambito:** soltanto i primi cinque blocchi da costruzione dell'ulivo; non si estende automaticamente alle parti future dell'aggiornamento.

## Blocco implementato e verificato

- `italiansdelight:olive_log` — Tronco d'ulivo
- `italiansdelight:olive_wood` — Legno d'ulivo
- `italiansdelight:stripped_olive_log` — Tronco d'ulivo scortecciato
- `italiansdelight:stripped_olive_wood` — Legno d'ulivo scortecciato
- `italiansdelight:olive_planks` — Assi d'ulivo

## Risultati dei test in Minecraft

L'autore ha eseguito la checklist concordata e ha confermato **tutti e sei i controlli** come superati.

| N. | Test | Risultato dichiarato |
|---|---|---|
| 1 | **Inventario Creativa:** presenza di tutti e cinque i blocchi nella scheda Italian's Delight | ✅ Superato |
| 2 | **Texture e rotazione:** modelli visibili correttamente, tronchi posizionati sui diversi assi, nessuna texture mancante rilevata | ✅ Superato |
| 3 | **Scortecciatura:** ascia con clic destro converte `olive_log` e `olive_wood` nei blocchi scortecciati corrispondenti | ✅ Superato |
| 4 | **Ricette:** tronco → 4 assi; quattro tronchi 2×2 → 3 `olive_wood`; variante scortecciata corrispondente | ✅ Superato |
| 5 | **Rottura e recupero:** ciascuno dei cinque blocchi restituisce l'oggetto corretto in Sopravvivenza | ✅ Superato |
| 6 | **Combustione:** blocchi infiammabili come i normali legni vanilla | ✅ Superato |

**Interpretazione:** questi risultati sono una conferma di test manuali fornita dall'autore, non un'asserzione che un agente automatico abbia riprodotto il collaudo. Nessuna anomalia segnalata in questa checklist.

## Controlli automatici

- GitHub Actions: [run 40028472641](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40028472641) — **successo**, Java 25/Gradle, dopo la correzione del controllo dei metadati di Pizza 1.0.0.
- Il controllo `.github/scripts/check_orchard_wood_assets.py` esclude riferimenti ai vecchi asset Rustic dai **nuovi modelli** del primo lotto.
- Le texture di Rustic preesistenti restano nel repository come archivio storico; non vengono usate per questi nuovi blocchi.

## Limiti del collaudo: NON ancora confermati

- Proprietà avanzate dei tag del legno in presenza di altre mod; crafting attraverso tag esterni; utilizzo come combustibile nelle fornaci.
- Blocchi futuri: scale, lastre, recinzioni, cancelli, bottoni, pedane a pressione, porte, botole, cartelli, cartelli appesi, barche e varianti con baule.
- Crescita degli alberi, generazione nei biomi, ulivo secolare 2×2, raccolta e ricrescita dei frutti, performance di worldgen, dedicated server e multiplayer.
- Le palette di mandorlo e ciliegio da frutto restano **proposte**, non approvate.

## Prossimo lotto proposto — NON ancora implementato

Completare gradualmente il **set edilizio dell'ulivo**, iniziando dai blocchi derivati dalle assi che riusano `olive_planks.png`: scale, lastre, recinzioni, cancelli, pulsanti e pedane a pressione. Seguire con porte e botole riutilizzando le texture `olive_door_*.png` e `olive_trapdoor.png`, **senza sovrascrivere alcun PNG**. In una fase distinta, valutare cartelli, cartelli appesi, barche e barche con baule (con registrazioni e modelli funzionanti).

**Criteri di accettazione del prossimo lotto:** comportamento vanilla equivalente; strumenti e loot corretti; drop; crafting; nessun riferimento a texture Rustic; Creative, traduzioni IT/EN e tag; Java 25 CI verde e test manuali dell'autore.

La versione pubblicata **1.0.0** e la branch protetta `main` rimangono invariate.
