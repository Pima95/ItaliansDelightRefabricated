# Scheda Creativa — ordine definitivo proposto per Pizza 1.0.0

L'inventario Creativa di Italian's Delight usa **una sola scheda**, senza dividerla
in più tab o aggiungere elementi segnaposto. L'ordine è per utilizzo/produzione,
con sottogruppi continui (Minecraft non mostra separatori o titoli nelle celle).

| Ordine | Gruppo | Contenuto |
| --- | --- | --- |
| 1 | Postazioni | Cheese Vat, Hanging Hook, Pizza Oven |
| 2 | Stagionatura | Le 12 varianti di legno della Cheese Aging Rack |
| 3 | Strumenti | Grattugia, mattarello, pala per pizza |
| 4 | Coltivazioni e ortaggi | Cardoon, basilico, basilico selvatico, sale, pomodoro/carota/patata a fette |
| 5 | Lavorazione del latte | Latte di pecora, siero, caglio, cagliata, panna, coltura erborinata, aceto |
| 6 | Formaggi | Freschi e famiglie Parmigiano, Pecorino, Gorgonzola, Provolone, Scamorza |
| 7 | Salumi | Tutte le famiglie con preparazione, prodotto finale e relative fette vicini |
| 8 | Pizze crude | 18 gusti riconosciuti, **Pizza mista** |
| 9 | Pizze cotte | 18 gusti riconosciuti, **Pizza mista** |
| 10 | Spicchi | 18 gusti indipendenti, impilabili fino a **16 per gusto** |
| 11 | Piatti pronti | Pasta, risotti, insalata di mozzarella |

## Pizza mista in Creativa

Ci sono due nuovi **esempi** (non nuovi item registrati):
`PizzaCreativeSamples.createMixed(false/true)`. Ogni esempio contiene quattro
gusti diversi, precisamente **Margherita, Salame, Rossa e Quattro Formaggi
bianca**, nell'ordine dei quarti fisici 1–4. Gli originali dei topping sono
`ItemStack` reali della whitelist; copertura, numerazione delle applicazioni,
nome e dati di cottura/posizionamento seguono il meccanismo ordinario. Le icone
delle pizze intere sono quelle **originali della Margherita**; gli spicchi
mantengono invece le proprie texture e proprietà specifiche.

## Test in-game

- [ ] Aprire la scheda Italian's Delight e scorrere dall'alto: tutte le postazioni/rack/strumenti raggruppati, ingredienti → formaggi → salumi → pizze → piatti pronti.
- [ ] Verificare che non manchi alcun item della mod e che i blocchi solo tecnici siano assenti.
- [ ] Verificare esattamente **18 esempi crudi + Pizza mista**, **18 esempi cotti + Pizza mista**, **18 spicchi**: 56 stack totali, **nessuna voce generica base** e una sola Bianca per sezione.
- [ ] Confrontare Margherita e Pizza mista sia cruda sia cotta: le icone devono coincidere perfettamente, i **nomi devono essere diversi**.
- [ ] Piazzare la Pizza mista cotta e prelevare i quattro spicchi: Margherita, Salame, Rossa e Quattro Formaggi bianca, con sprite/nutrizione di ciascun quarto.
- [ ] Salvare, rientrare e verificare che i quattro quarti restino indipendenti. Provare anche la cottura della versione cruda con 20/30/60 secondi approvati.
- [ ] Provare la ricerca Creativa dei nomi italiani/inglesi e confermare assenza di duplicazioni dei tre Item ID reali.

Lo script `.github/scripts/check_creative_tab.py` verifica presenza/ordine
staticamente. Le texture 16×16 già approvate non sono state modificate.

## Compatibilità delle vecchie Pizze miste

Un item di Pizza mista salvato **prima** dell'aggiornamento poteva ancora conservare il valore `ITEM_MODEL=raw_pizza_custom`/`cooked_pizza_custom` nonostante la nuova regola di visualizzazione. Durante il normale tick dell'inventario il gioco corregge **solo** questi vecchi item misti portandoli al modello base Margherita. Vale anche per la pizza già caricata sulla pala: viene aggiornato il singolo `ItemStack` interno a `PEEL_CARGO`, senza cambiare attributi d'arma, ingredienti, stato crudo/cotto o consumare nulla. L'operazione non riscrive componenti ogni tick: dopo il primo aggiornamento diventa un controllo senza mutazioni.

- [ ] Prelevare da una cassa una vecchia Pizza mista (item precedente al fix): icona Margherita dopo che entra nell'inventario.
- [ ] Ripetere con una vecchia Pizza mista già caricata su una pala; trasporto e scarico preservano gli spicchi originali.
- [ ] Verificare che una pizza uniforme personalizzata conservi `*_custom` e non venga trasformata in Margherita.


## Icona della scheda (21 ottobre 2026)

L'icona dell'unica scheda Creativa è ora **la Pizza Margherita cotta**.
Il supplier di Fabric usa `new ItemStack(ModItems.COOKED_PIZZA)`, il cui
modello originale `cooked_pizza` punta già alla texture approvata
`italiansdelight:item/pizza_margherita` (16×16). Non sono stati introdotti
nuovi item, copiati PNG o modificati nomi/ordine della scheda.

Il controllo `.github/scripts/check_creative_tab.py` verifica sia il
supplier dell'icona sia il collegamento JSON alla texture originale.

- [ ] Verificare nel client che l'icona della scheda Italian's Delight sia
  la Margherita cotta e che tutta la scheda resti invariata.

## Pulizia duplicati Pizza Bianca e stack spicchi (21 ottobre 2026)

Il vecchio inventario Creativa aggiungeva sia gli `ItemStack` base `RAW_PIZZA`, `COOKED_PIZZA`, `PIZZA_SLICE` con `PIZZA_CONTENTS`/`PIZZA_SLICE_CONTENTS` vuoti, sia gli esempi già completi di `Pizza Bianca`. Si vedevano quindi copie con lo stesso nome (spesso con texture base non pertinente). La scheda ora espone **soltanto** gli esempi completi: 18 pizze crude, 18 cotte, 18 spicchi e due Pizze miste, cioè **56 stack**. Nessuna registrazione `Item` è stata rimossa e la scheda mantiene l'icona Margherita originale.

Gli spicchi avevano già `stacksTo(16)`, ma `PizzaSliceData` usava l'uguaglianza automatica del record, comprendendo indice del quarto e gli stack originali dei topping: anche due spicchi della stessa ricetta potevano rifiutare lo stack. Ora il confronto degli **item component** usa l'insieme degli ingredienti canonici e il flag di mozzarella legacy. L'origine e l'ordine di posa non cambiano gusto, nutrizione, nome o sprite. Tipi diversi restano separati e il tetto **16** rimane invariato; durante il merge Minecraft conserva un rappresentante del dato storico d'origine, che non è più rimborsabile dopo la cottura.

**Test manuali:** prendere quattro spicchi Margherita dai quattro quarti di una pizza; controllare che vadano nello stesso stack (4/16). Ripetere con una seconda pizza per 8/16, poi oltre 16 per verificare il secondo stack; unire spicchi ottenuti da ingredienti aggiunti in ordine diverso e da varianti crude/cotte con la stessa identità canonica. Verificare che Salame, Rossa, Bianca, diverse combinazioni personalizzate e il flag legacy non si mischino fra loro. Controllare hopper/casse, `Shift` + clic in inventario e riavvio mondo. Nei vecchi stack salvati la nuova regola di confronto vale senza cambiare il codec o cancellare i dati.
