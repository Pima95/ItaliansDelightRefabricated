# Pizza 1.0.0 — Texture dedicate 12×12, ritaglio dei quarti e contatto con la base

**Data:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** implementato su GitHub; build Java 25 e verifica PNG CI; test di resa grafica **ancora da confermare in Minecraft**.

## Motivazione

Dagli screenshot dell'autore, il renderer procedurale precedente mostrava **quattro chiazze separate**, piccole rappresentazioni degli item, e un evidente distacco dei topping dalla pasta. La nuova implementazione sostituisce completamente tale soluzione con texture pixel-art dedicate, secondo le proporzioni dell'impasto vanilla/Farmer's Delight.

## Geometria: nessun mezzo pixel sospeso

- Il modello `pizza_dough_stage4.json` occupa 14×14 pixel di Minecraft, coordinate **1..15**, con bordo rialzato di 1 pixel. La superficie del fondo si trova a **y=1/16**.
- Ogni texture dei topping misura **12×12 pixel**, con coordinate grafiche **2..14**, esattamente dentro il bordo e senza scalature o antialiasing.
- Il quad superiore di ogni ingrediente si trova a **y=1/16+0.00016** blocchi + `depth*0.00009` (profondità ottica inferiore a 1/1000 di pixel). L'offset invisibile evita `z-fighting` del buffer GPU e NON alza i condimenti di mezzo pixel.
- Le texture sono `PNG RGBA` 8-bit, con **alpha esclusivamente 0 o 255**. Non si sovrascrivono le texture originali degli item.
- Le quattro zone sono i **quadranti fisici 6×6** della **medesima texture 12×12**, non quattro immagini distinte. Ogni quadrante disegnato usa esclusivamente la propria porzione UV della texture: `u/v=0..0.5` oppure `0.5..1`.
- Le coordinate ruotano con l'orientamento salvato nella composizione e non con il movimento del giocatore.

## Come funzionano salsa e topping

- **Salse/creme:** `tomato_sauce.png`, `cream_bowl.png` e `ricotta.png` coprono con una grafica continua l'intera zona su cui sono state applicate. Una pizza intera con pomodoro occupa tutte le 12×12 celle; quando in un altro quarto è presente panna/ricotta, il pomodoro si interrompe al suo confine esatto e quel quarto usa la grafica dell'altra salsa.
- **Condimenti solidi:** ogni tipo canonico ha **la propria texture 12×12 con pixel trasparenti**: mozzarella, parmigiano, gorgonzola, salumi, verdure, basilico, uova e gli altri. Sono ritagliati per quarti nello stesso modo, ma i pixel trasparenti lasciano vedere la salsa o altri topping sottostanti. Nessun item `FIXED` o item frame viene più renderizzato sulla base.
- Anche applicando prima un solido e poi una salsa, il disegno della salsa resta **sotto** i solidi; la logica di interazione, cronologia, `remainingMask`, LIFO e recupero degli ingredienti rimane invariata.
- **Compatibilità mod esterne:** item differenti che corrispondono allo stesso topping canonico usano la stessa texture del topping (coerente con le regole di equivalenza del gameplay), pur conservando l'originalStack reale nei dati. Gli ID canonici sconosciuti di vecchi salvataggi utilizzano `unknown.png`, evitando una texture inesistente.
- **Mozzarella legacy:** `legacy_mozzarella.png` mostra i vecchi condimenti integrati nei salvataggi senza reintrodurre la possibilità di applicare una mozzarella intera.
- Sulle pizze cotte, il `CookedPizzaBlock.REMAINING` nasconde automaticamente tutte le texture dei quarti già raccolti.

## Inventario degli asset

**33 nuovi file RGBA 12×12** in `src/main/resources/assets/italiansdelight/textures/block/pizza_toppings/`: 29 tipi attivi (`PizzaToppingCatalog`), 2 tipi pianificati (carota/patata affettate), un fallback per la mozzarella legacy e uno per i topping di vecchi salvataggi non riconosciuti.

Gli asset sono tutti pixel art discreta con colori dedicati e silhouette progettate per la superficie della pizza; alcuni condimenti utilizzano pattern ripetuti deliberatamente in ciascun quarto in modo che una singola texture possa essere suddivisa senza bordi o texture esterne al ritaglio.

## File interessati

- `client/render/PizzaToppingVisuals.java`: nuova pipeline quad UV su una texture per applicazione, due passaggi (salsa poi solidi), clipping fisico delle 4 zone.
- `client/render/PizzaToppingRenderer.java` e `CookedPizzaRenderer.java`: estrazione dati e invio al renderer condiviso, senza `ItemModelResolver` per i topping.
- `textures/block/pizza_toppings/*.png`: 33 texture nuove (non sovrascrivono texture approvate).
- `.github/scripts/check_pizza_topping_textures.py`: validazione automatica PNG di tutte le texture.
- `.github/workflows/pizza-build.yml`: esegue verifica degli asset e `gradlew clean build` con Java 25.

## Checklist test in Minecraft

- [ ] Salsa pomodoro su tutta la base, vista dall'alto: **una sola superficie continua 12×12**, nessuna divisione grafica in 4 chiazze.
- [ ] Quarto 1 rosso, quarto 2 panna e quarto 3 ricotta, quarto 4 privo di salsa: i cambi avvengono **esattamente sul confine dei quarti 6×6**; l'area senza salsa mostra la pasta originale.
- [ ] Pizza intera con pomodoro + mozzarella a fette + basilico: la mozzarella e il basilico mostrano **texture dedicate**, non icone di item. Le aree trasparenti mostrano il pomodoro.
- [ ] Pizza con fino a 6 diversi solidi per quarto: tutte le texture sono visibili e tagliate entro il proprio quarto, senza uscire sul bordo. Controllare se la sovrapposizione richiede ulteriori rifiniture artistiche.
- [ ] Vista laterale ravvicinata: nessuna distanza di mezzo pixel dalla pasta, nessuna altezza in stile item frame.
- [ ] Ruotare la pizza nei 4 orientamenti: le texture mantengono la stessa corrispondenza con gli ingredienti del quarto.
- [ ] Cottura e raccolta di 1, 2, 3 e 4 fette: gli ingredienti spariscono unicamente nelle zone non più presenti.
- [ ] Rimozione parziale via Shift + clic: rimborso soltanto a cancellazione completa dell'applicazione originale, grafica dei quarti aggiornata immediatamente.
- [ ] Pizza con mozzarella legacy: grafica dedicata, rimborso singolo. Nessun modello viola/nero.
- [ ] Salvataggio/reload, pala pizza, hopper e multiplayer: stessa composizione e nessun ingrediente perso, duplicato o modificato.
- [ ] Controllo risorse: ciascuno dei 33 PNG deve misurare esattamente 12×12 e usare solo alpha 0/255 (verificato anche in CI).
- [ ] Assenza di artefatti di illuminazione, z-fighting, texture mancanti o quadranti ruotati nella GUI o nel mondo.

**Nota:** la pipeline CI garantisce formattazione e compilazione, ma **non dimostra** il risultato grafico in client. Conservare gli asset come prima iterazione finché l'autore non approva colori e sagome dopo la prova in Minecraft.
