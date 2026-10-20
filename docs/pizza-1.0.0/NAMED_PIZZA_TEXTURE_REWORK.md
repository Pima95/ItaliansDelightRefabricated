# Revisione vincolante delle texture per pizze riconosciute — 20 ottobre 2026

## Decisioni approvate dall'autore
1. **Prima ripristinare** il modello base `assets/italiansdelight/models/item/cooked_pizza.json` affinché punti nuovamente alla Margherita cotta originale `italiansdelight:item/pizza_margherita`. Eseguito in commit `e4ac32da2913befebe6aee9d0030095d31662919`.
2. **Poi realizzare i 18 gusti attivi in DUE versioni**, cruda e cotta. I PNG `textures/item/raw_pizza.png` (Margherita cruda di riferimento) e `textures/item/pizza_margherita.png` (vecchia Margherita cotta approvata) **non possono essere sovrascritti**.
3. Tutte le icone devono conservare **sagoma e prospettiva ellittica 16×16 dei due riferimenti**, bordo nella stessa posizione, scala, pixel art RGB coerente, e differire soltanto nella superficie centrale e nei condimenti del gusto. Non utilizzare la vista rotonda dall'alto introdotta nel primo tentativo.
4. PNG RGBA **16×16**, alpha 0 o 255 soltanto, nessun antialiasing/semotrasparenza/sfocature. Niente sovrascritture di altri asset approvati. Le prime vecchie icone `pizza_bianca.png`, `pizza_rossa.png`, `pizza_patate_speck.png` e `pizza_custom.png` possono essere conservate come archivio ma **non devono più essere collegate ai modelli in gioco**.
5. I gusti devono comparire in Creativa come **stack dimostrativi con componenti reali**, tra i salumi e i piatti pronti. **Non** registrare nuovi item o cambiare gli ID: si riutilizzano `italiansdelight:raw_pizza` e `italiansdelight:cooked_pizza`, ognuno con `pizza_contents` appropriato e modello dinamico sincronizzato.
6. I nomi dei gusti restano derivati da `PizzaDisplayNames` e dallo stesso insieme di ingredienti canonici su tutti e quattro i quarti. Nessuna differenza tra nome e texture, e fallback stilisticamente coerente per pizze assortite o libere.
7. La grafica delle fette resta invariata fino alla fase successiva; la pizza collocata nel mondo conserva il renderer dei condimenti approvato; nessuna modifica alle meccaniche di cottura, rimborsi, nutrition, crafting, pala o salvataggi.
8. **Ogni piccola scelta e conferma di sviluppo va registrata su GitHub**, non soltanto le modifiche al codice.

## Elenco dei gusti
18 gusti effettivamente attivi da `PizzaDisplayNames.buildPresets()`: Bianca, Rossa, Margherita, Caprese rossa, Caprese bianca, Quattro Formaggi bianca, Quattro Formaggi rossa, Prosciutto crudo, Salame, Mortadella e Ricotta, Guanciale e Pecorino rossa, Speck e Gorgonzola, Salmone e Ricotta, Salmone e Scamorza affumicata, Carne macinata e Mozzarella, Zucca e Speck, Patate e Speck, Orto disponibile. **Principessa è ancora sospesa.**

## Convenzione dei modelli
- `assets/italiansdelight/models/item/raw_pizza_<gusto>.json` e `cooked_pizza_<gusto>.json` con relativi `assets/italiansdelight/items/` e `textures/item/`.
- Eccezione Margherita: i due modelli puntano **alle texture originali** `raw_pizza.png` e `pizza_margherita.png`.
- `raw_pizza_custom` e `cooked_pizza_custom` per stack con dati senza match esatto; i modelli base `raw_pizza` e `cooked_pizza` sono **immutati** rispetto ai riferimenti.
- Ogni esempio in Creativa è uno stack con `PizzaItemData` coerente e tutti i quarti uniformi, non un nuovo ID.
- L'elenco dei preset in Creativa si ricava **dalla mappa centrale già esistente**, senza una seconda lista di ricette.

## Collaudo
- [ ] CI: integrazione Java 25, nessuna ricetta cancellata, PNG 16×16 alpha binario, stessa sagoma alpha dei due riferimenti, 18 modelli crudi e 18 cotti, fallback, assenza di asset mancanti.
- [ ] Aprire il tab Creativa tra Salumi e Piatti: vedere le varianti sia crude sia cotte con nomi e texture corretti, senza icone viola/nero.
- [ ] Verificare Bianca, Rossa, Margherita, Patate e Speck e almeno un altro gusto con ingredienti multipli. Stessa prospettiva del rispettivo riferimento, non vista circolare dall'alto.
- [ ] Posizionare, cuocere e raccogliere pizza normale e assortita, testare pala, recupero, riavvio mondo e multiplayer.
- [ ] Confermare che `raw_pizza.png` e `pizza_margherita.png` siano identiche a prima della revisione e che `models/item/cooked_pizza.json` usi il riferimento storico.
- [ ] Confermare spicchi, JEI e resa nel mondo non modificati.

## Stato dell'implementazione sul branch (20 ottobre 2026)

- **Fase 1: ripristino** del modello `cooked_pizza.json` alla texture Margherita cotta storica: commit `e4ac32da2913befebe6aee9d0030095d31662919`.
- **Fase 2: texture**: tutte le 18 pizze riconosciute hanno adesso un modello crudo e uno cotto, con **36 nuovi PNG** (17 gusti × 2 + fallback crudo e cotto). **Margherita cruda e cotta riutilizzano i due PNG originali** senza duplicazioni o modifiche. Le varianti usano la stessa maschera RGBA/sagoma delle rispettive Margherite approvate; soltanto la superficie interna è modificata.
- **Modelli:** 72 nuovi JSON (36 `models/item`, 36 `items`), con selezione `DataComponents.ITEM_MODEL` su stack sia crudi sia cotti in `PizzaDisplayNames.nameWholeStack`. Il modello base della pizza cotta resta quello originale.
- **Creativa:** la scheda già approvata mantiene le voci fra Salumi e Piatti pronti; le 3 voci generiche restano e vengono seguite da **18 coppie di stack autentici** (crudo/cotto) derivati dalla mappa centrale dei preset, ognuno con `PizzaItemData` valido. Non vengono registrati nuovi `Item`; non si alterano meccaniche né spicchi.
- **Archiviazione:** i vecchi PNG `pizza_bianca.png`, `pizza_rossa.png`, `pizza_patate_speck.png`, `pizza_custom.png` restano in GitHub per confronto, **ma non sono utilizzati** dai nuovi modelli selezionati dal codice.
- **CI:** `check_named_pizza_artwork.py` valida per tutte le texture nuove formato 16×16 RGBA, opacità binaria, stessa sagoma in pixel delle Margherite originali, modelli e collegamento Creativa; impedisce inoltre la sostituzione accidentale dei PNG Margherita approvati verificandone gli hash Git. Il vecchio test basato su quattro gusti è stato sostituito.
- **Build:** verificare l'esito dell'ultima GitHub Actions Java 25; la compilazione riuscita non sostituisce i test visivi dell'autore in Minecraft.

**Fase successiva:** collaudo in gioco delle 18 coppie nella scheda Creativa, della pala, della pizza cotta/raccolta, dei nomi, fallback e salvataggi. La grafica degli spicchi sarà affrontata solo dopo conferma.
