# Pizza 1.0.0 — Primo rendering 2.5D dei condimenti

**Data:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** renderer condiviso implementato; **build CI da registrare**; **risultato grafico non ancora approvato nel client**.

## Obiettivo e ambito

La precedente grafica ripeteva un modello ItemStack `FIXED` di dimensioni 0,17 per ogni applicazione e quarto. Ora `PizzaToppingVisuals.java` centralizza la grafica per **pizza cruda e pizza cotta**, lasciando invariati i componenti dell'item, le collisioni, la gestione della pala, cottura, JEI, rimborsi e nutrizione.

### Tre tipologie visive

| Ingredienti | Visualizzazione di questa iterazione | Risorse |
|---|---|---|
| Salsa di pomodoro (`SAUCE`) | Chiazze basse rosse con bordi pixelati; applicate esclusivamente ai quarti coperti | Geometria colorata, nessuna texture nuova |
| Panna e ricotta (`CREAMY`) | Chiazze basse avorio/bianco, distinte dai solidi; nessuna ciotola visualizzata sulla pizza | Geometria colorata, nessuna texture nuova |
| Formaggi a fette, affettati, verdure, erbe e altri topping `SOLID` | Frammenti più piccoli e appiattiti sulla superficie; per ciascuna applicazione la mod utilizza **l'ItemStack realmente consumato** (anche equivalente di altra mod). I frammenti variano dimensioni per categoria e posizione in modo deterministico | Modelli/texture già approvati |

Le chiazze sono quadrilateri poco spessi, costruiti con la stessa API di geometria utilizzata da `PizzaDivisionOverlay`. I frammenti solidi sono renderizzati dal sistema vanilla `ItemModelResolver` mediante `ItemStackRenderState`, con scala più piccola e spessore ridotto. Un singolo buffer geometrico raccoglie le chiazze per pizza, senza allocare oggetti server a ogni frame.

### Vincoli preservati

- **Quattro quarti fisici** e `remainingMask` originali. Un topping applicato sulla pizza intera copre quattro quarti ma consuma **un solo item**; rimozione parziale LIFO e rimborso restano immutati.
- Per pizza cotta, `CookedPizzaBlock.REMAINING` impedisce di mostrare condimenti sulle fette già raccolte.
- Posizionamento e rotazione riproducibili usando `renderSeed`, `sequence`, indice del quarto e orientamento salvati. Non viene generato un nuovo seed né modificato il codec.
- Vecchie pizze con `legacy_mozzarella=true` continuano a rendere la mozzarella tramite l'item originale, senza modello multipart mancante e senza consentire nuove applicazioni legacy.
- Salse e creme sono poste sotto gli ingredienti solidi. Nessuna visualizzazione di bowl come topping; i contenitori di cottura/rimborso restano gestiti esclusivamente dal server.
- L'overlay delle linee di divisione resta indipendente e visibile quando il giocatore impugna il mattarello o un topping previsto.
- La grafica dell'item pizza in inventario, pala e JEI rimane invariata: questa fase riguarda esclusivamente il rendering del **blocco collocato nel mondo**. La resa degli item potrà essere affrontata dopo approvazione della grafica in-world.
- Non è stata sovrascritta o creata alcuna texture PNG.

## Checklist in Minecraft

- [ ] `gradlew clean build` su JDK 25; avvio con `runClient` e assenza di errori nel log o crash di rendering.
- [ ] Mettere **salsa di pomodoro su pizza intera**: superficie rossa piatta, pixelata, non una ciotola sospesa. Guardare dall'alto, dal lato e da vicino: nessuno sfarfallio, linee nere non volute o compenetrazione del bordo.
- [ ] Ripetere con **panna** e **ricotta** su pizze separate: chiazze color crema / bianco, senza texture viola/nera. Applicare questi ingredienti soltanto secondo le regole di esclusività già previste.
- [ ] Mettere **mozzarella a fette, basilico, speck e pomodoro a fette** su quattro pizze: item spezzettati più piccoli e adagiati sulla superficie, non icone verticali o cornici.
- [ ] Pizza assortita con quattro quarti diversi, girata nei quattro orientamenti cardinali: un ingrediente deve comparire **solo sul quarto fisico in cui è stato realmente applicato**.
- [ ] Pizza intera con 1, 3 e 6 topping diversi per quarto: i frammenti non devono sovrapporsi in maniera illeggibile, uscire dai bordi o invadere gli altri quarti.
- [ ] Applicare poi togliere il topping più recente via Shift+clic, anche su un solo quarto: il solo disegno relativo deve sparire, senza dupliche o rimborsi anticipati.
- [ ] Raccogliere una pizza cruda e riposizionarla; salvare e ricaricare il mondo, muoverla tramite pala, cuocerla e piazzarla: stessa disposizione in tutte le trasformazioni e nessuna perdita degli originalStack.
- [ ] Servire gli spicchi di una pizza cotta: una fetta raccolta deve lasciare vuota anche la sua zona di condimenti, le altre zone devono restare uguali.
- [ ] Verificare una pizza **legacy** con `mozzarella=true`: mozzarella visibile e recuperabile una volta, nessuna texture mancante; la mozzarella intera non deve poter essere applicata sulle pizze nuove.
- [ ] Provare bacon crudo e cotto, e un item di terza mod tramite equivalenza JSON: geometria e maschere coerenti, **modello visualizzato uguale all'item usato**, anche se identità canonica condivisa.
- [ ] Controllare in multiplayer che due client vedano gli stessi quarti e la stessa disposizione durante applicazione/rimozione; stress con almeno 20 pizze, senza crolli evidenti di frame rate.
- [ ] Verificare gli altri render in JEI, blocchi e item: impasto e ricette/shapeless, Pizza Oven, affumicatore e fornace ancora funzionanti.

## Passi successivi

Questa è **una prima versione procedurale del 2.5D**, non l'asset grafico finale approvato. Dopo gli screenshot e le osservazioni dell'autore, perfezionare i colori, dimensioni, quantità, sagome e altezza delle chiazze, con eventuali **nuovi** asset dedicati solo se necessari. Non sostituire le texture approvate senza una decisione esplicita.

La fase conclusiva include l'eventuale aspetto 2.5D degli item inventario/spicchi, fallback per mod con modelli 3D insoliti, test multiplayer e prestazioni.
