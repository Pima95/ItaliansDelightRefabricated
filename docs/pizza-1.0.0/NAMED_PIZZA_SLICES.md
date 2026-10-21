# Pizza 1.0.0 — Texture e nomi degli spicchi riconosciuti

**Data decisione:** 21 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** texture approvate dall'autore il 21 ottobre 2026; test funzionali estesi ancora da completare.

## Approvazione finale delle texture — 21 ottobre 2026

**Conferma dell'autore:** «Questi spicchi vanno bene, procediamo». La seconda serie delle **18 texture varianti degli spicchi** (17 gusti + fallback) è quindi **approvata in gioco per l'aspetto visivo e la distinguibilità**. La build funzionava già nei test precedenti. Anche le texture delle pizze intere erano già state approvate.

**Vincolo permanente:** non sovrascrivere, ritoccare o rigenerare `margherita_slice.png`, gli altri `pizza_slice_*.png`, né le texture delle pizze intere crude/cotte approvate senza una nuova richiesta dell'autore. Le regole 16×16 RGBA, alpha binario, pixel di bordo conservati e modelli già assegnati restano operative.

Questa conferma è *estetica*: non implica che siano già stati eseguiti tutti i test multiplayer, inventario pieno, persistenza, bilanciamento nutrizionale o compatibilità cross-mod della checklist sottostante. Si passa a **audit integrato e valutazione del bilanciamento per la Pizza 1.0.0** prima di modificare valori di gameplay. La regola di documentare su GitHub anche brevi decisioni rimane in vigore.

## Revisione richiesta dall'autore: maggiore distinguibilità (21 ottobre 2026)

**Feedback dopo test client:** la build e gli spicchi funzionano, ma molte icone della prima iterazione risultavano pressoché identiche, in particolare a dimensione inventario. Il confronto byte-per-byte già presente in CI dimostrava soltanto che i PNG fossero *diversi*, non che fossero **riconoscibili a 16×16 pixel**.

**Modifica su GitHub:** sostituiti **tutti i 18 PNG variabili** (17 gusti riconosciuti + fallback), lavorando direttamente pixel per pixel senza ridimensionare alcuna immagine. Lo spicchio Margherita originale `margherita_slice.png` resta immutato e continua ad avere il modello originario. Per tutte le 18 varianti sono invariati, in RGBA, tutti i **218 pixel di bordo, silhouette e trasparenza**; solo 38 pixel interni vengono disegnati nuovamente.

**Nuovo approccio grafico:** anziché distribuire 2–6 puntini sopra lo stesso impasto, ogni pizza presenta una grande silhouette di topping e una palette dominante caratteristica. Distinzioni deliberate:

| Pizza | Motivo distintivo leggibile a 16×16 |
| --- | --- |
| Bianca / Rossa | Superficie crema-dorata senza topping / salsa rossa continua |
| Caprese bianca / rossa | Mozzarella bianca con gruppi di pomodoro e grandi foglie verdi / stesso condimento su salsa rossa |
| Quattro Formaggi bianca / rossa | Grandi zone giallo formaggio, crema e *venature blu-verdi* ben contrastanti / su salsa rossa |
| Prosciutto crudo | Strisce e falde di prosciutto rosato, non rondelle |
| Salame | Rondelle color bordeaux scuro e grasso bianco su mozzarella chiara |
| Mortadella e Ricotta | Zona rosa predominante con piccoli accumuli bianchi di ricotta |
| Guanciale e Pecorino | Fasce scure alternate a grasso chiaro, su salsa rossa |
| Speck e Gorgonzola | Striature di speck scuro con ricche venature blu-verdi |
| Salmone e Ricotta | Nastro di salmone arancio-rosato su crema chiara, basilico verde |
| Salmone e Scamorza | Salmone arancione su scamorza affumicata ambrata |
| Carne e Mozzarella | Grandi bocconi bruno scuro di carne su pomodoro con mozzarella chiara |
| Zucca e Speck | Arancione di zucca predominante con striature scure e venature chiare |
| Patate e Speck | Fondo patate giallo pallido con macchie di buccia brunita e strisce di speck |
| Orto disponibile | Verde foglia predominante, tocchi rossi di pomodoro e arancioni di zucca |
| Personalizzata | Motivo multicolore asimmetrico per un gusto non riconosciuto |

**Nuova regressione automatica:** `.github/scripts/check_named_pizza_slices.py` ora esegue confronti su **ogni coppia delle 19 icone complessive**, includendo la Margherita originale e il fallback. Per ogni coppia sono richiesti **almeno 10 pixel di farcitura con differenza di colore RGB complessiva maggiore di 105**, su una superficie modificabile di 38 pixel. Questa verifica si aggiunge ai test di CRC, formato PNG 16×16, alpha binario, 218 pixel della sagoma intatti e nessuna texture identica.

**Stato:** revisione pubblicata nel branch `feature/pizza-1.0.0`, compilazione Java 25 e nuova approvazione estetica dell'autore da verificare. Le meccaniche del taglio, le ricette, i dati salvati e le texture delle pizze intere già approvate non sono stati modificati.

## Conferme ricevute dall'autore

L'autore ha confermato che **tutte le texture delle pizze intere sono approvate** e che **le 18 pizze riconosciute sono correttamente visibili nella scheda Creativa** dopo le revisioni dell'artwork 16×16. Non rielaborare ulteriormente texture crude e cotte approvate, bordi, salsa o condimenti senza una nuova richiesta.

L'autore ha chiesto di procedere alla fase successiva: gli spicchi di pizza, come previsto nella roadmap. Vale sempre la regola di registrare in GitHub **ogni decisione, anche piccola**.

## Requisito funzionale centrale

**Uno spicchio riconosce solo gli ingredienti del proprio quarto fisico** (`PizzaSliceData`). Uno spicchio di Margherita prelevato da una pizza assortita deve chiamarsi **Spicchio di Margherita** e mostrare l'icona Margherita, **anche quando gli altri tre spicchi sono completamente diversi**. Non rinominare lo spicchio in base al nome della pizza intera o al riepilogo dei quattro quarti.

La lista di 18 gusti attivi rimane quella di `PizzaDisplayNames.buildPresets()`. Principessa è sospesa e non ha una texture dedicata.

## Grafica approvata e nuova serie

- `textures/item/margherita_slice.png` è **la texture storica approvata**. Non viene sovrascritta; per Margherita si usa il modello `pizza_slice` esistente.
- Per gli altri **17 gusti riconosciuti** è stato creato `textures/item/pizza_slice_<id>.png`.
- In aggiunta è stato creato `textures/item/pizza_slice_custom.png` per quarti non riconosciuti, ingredienti extra, legacy mozzarella o dati di mod opzionali.
- **18 nuovi PNG** totali (17 gusti + fallback), e 36 JSON di definizione/modello, senza creare alcun nuovo tipo di item o duplicare ricette.
- Le texture sono disegnate *direttamente in 16×16*, PNG RGBA, con pixel alpha 0 o 255 (mai semitrasparenti), senza antialiasing/downscale. Sono derivate dalla texture originale dello spicchio e mantengono **la sagoma triangolare, i bordi e le ombre originali identici**, modificando esclusivamente la piccola zona centrale di 38 pixel; i restanti **218 pixel sono immutati, inclusi i trasparenti**.
- I condimenti sono piccoli gruppi di pixel significativi, non puntini casuali: salame a dischi, speck in strisce, patate crema, verdure verdi, gorgonzola venato, pomodoro rosso, mozzarella e altri formaggi chiari, salmone rosa.
- La versione cotta degli spicchi è l'unica prevista: **nessuna texture di spicchio crudo**, perché il sistema divide la pizza per consumo solo dopo la cottura.

## Modello e dati: un solo Item per tutti

`italiansdelight:pizza_slice` resta **l'unico item**, senza alterare `ModDataComponents.PIZZA_SLICE_CONTENTS` né `FoodProperties`.

In `PizzaDisplayNames`:
- `slicePresetId(PizzaSliceData)` usa **`PRESETS.get(quarter(slice))`**, lo stesso riconoscimento dei nomi.
- `nameSliceStack` assegna `DataComponents.ITEM_NAME` e `DataComponents.ITEM_MODEL` **allo stack creato**, con modello specifico per il preset del singolo quarto.
- Se lo spicchio è Margherita → `italiansdelight:pizza_slice` (riferimento originale).
- Se lo spicchio è di un altro gusto attivo → `italiansdelight:pizza_slice_<id>`.
- Se non è riconosciuto → `italiansdelight:pizza_slice_custom`.

`PizzaSliceFactory.create` continua a salvare gli ingredienti originali del singolo quarto e a calcolare fame/saturazione dalle categorie approvate. Nessuna modifica alla cottura, alla rimozione degli ingredienti, alla pala, ai salvataggi o al renderer del blocco pizza.

**Creativa:** dopo gli esempi delle pizze crude/cotte, fra salumi e piatti, sono presenti **18 veri stack `pizza_slice` commestibili**, generati dalla mappa centrale `allPresets()` usando `PizzaCreativeSamples.createSlice` e `PizzaSliceFactory.create`. Nessun nuovo ID nel registro.

## Verifiche automatizzate

Lo script `.github/scripts/check_named_pizza_slices.py`:
- controlla 18 preset attivi, modelli/definizioni per tutti i 17 gusti nuovi e fallback;
- valida i PNG 16×16, CRC, RGBA/alpha binario, e impedisce la modifica della Margherita approvata (hash Git);
- confronta **tutti i 218 pixel intoccabili di ogni variante RGBA** con la Margherita originale, escludendo soltanto i 38 pixel della farcitura;
- rileva texture visivamente identiche tra gusti, inclusa la Margherita e il fallback;
- verifica il riconoscimento per singolo quarto e gli stack di esempio in Creativa.

## Checklist manuale Minecraft 26.2

- [ ] Aprire la scheda Creativa: dopo le pizze intere compaiono gli spicchi dei **18 gusti**, con icone e nomi corretti.
- [ ] Confrontare in hotbar le icone Margherita originale, Bianca, Rossa, Salame, Patate e Speck, Caprese e Quattro Formaggi.
- [ ] In Sopravvivenza preparare/cuocere una Margherita uniforme: uno spicchio deve usare esattamente la texture Margherita precedente.
- [ ] Preparare una pizza **con quattro quarti diversi**; includere Margherita su uno solo. Raccogliendo i quattro spicchi, i nomi, le icone e le informazioni devono essere indipendenti per ogni quarto.
- [ ] Aggiungere un ingrediente extra a uno spicchio altrimenti Margherita: nome e texture devono passare al fallback, senza falso riconoscimento.
- [ ] Verificare anche uno spicchio con `legacyMozzarella=true`: non deve essere riconosciuto erroneamente come Bianca o Margherita.
- [ ] Consumare gli spicchi: i valori fame/saturazione approvati e gli effetti invariati devono essere rispettati.
- [ ] Chiudere e riaprire il mondo e, se disponibile, provare multiplayer: componente `pizza_slice_contents` e `item_model` persistono.
- [ ] Il rendering delle pizze piazzate, la pala, JEI e la scheda Creativa già approvati devono rimanere invariati.

**Limite da controllare:** eventuali `pizza_slice` creati in salvataggi precedenti, già presenti in un inventario senza `DataComponents.ITEM_MODEL`, continueranno temporaneamente a utilizzare il modello base predefinito finché non vengono ricreati tramite `PizzaSliceFactory.create`. Non introdurre una migrazione automatica senza una decisione separata dell'autore.

**Fase seguente:** collaudare l'insieme in Minecraft prima di qualsiasi rebalancing o nuovo contenuto pizza; non apportare revisioni a immagini già approvate senza autorizzazione.
