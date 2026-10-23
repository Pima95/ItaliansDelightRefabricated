# Pizza JEI e Cutting Board — aggiornamento del 23 ottobre 2026

**Branch:** `feature/pizza-1.0.0` (nessun nuovo branch).
**Stato:** implementato e sottoposto alla CI, test runtime in Minecraft non ancora confermati.

## Cosa contiene davvero JEI

1. **Pizze personalizzabili / crafting:** i cinque esempi originari sono conservati e ora vengono aggiunti i gusti riconosciuti dal catalogo `PizzaDisplayNames.allPresets()` con almeno un topping. La vera ricetta in gioco è una sola ricetta shapeless dinamica: queste sono **anteprime JEI**, non ulteriori ricette o restrizioni.
2. **Pizza Oven:** una ricetta JEI con input raw e output cooked autentico per ciascuno dei **18 gusti riconosciuti**, con il solito tempo di 20 secondi. Ogni gusto ha un identificatore JEI distinto e bookmark. Le fornaci e gli affumicatori vanilla conservano le proprie categorie (60/30 secondi).
3. **Cutting Board / Spicchi di pizza:** nuova categoria con **18 pizze cotte riconosciute** più una **Pizza mista**. Per ognuna sono mostrati quattro spicchi ottenuti davvero dai propri quarti. La categoria è ricercabile partendo dalla pizza cotta, dallo spicchio o dal tagliere.
4. **Preparazione nel mondo:** la guida preesistente per posa/stesura/divisione/condimento è invariata. La **Pizza Bianca** nasce dall'impasto piazzato nel mondo; l'impasto da solo **non** costituisce una ricetta shapeless nella crafting table.

Nessuna nuova texture, nuovo item o nuova ricetta fissa duplicata è stata introdotta.

## Come si usa la Cutting Board

- Posizionare sul tagliere Farmer's Delight Refabricated **una pizza cotta intera** (il singolo item con `PIZZA_CONTENTS`).
- Cliccare con **un coltello del tag `c:tools/knife`** utilizzato da Farmer's Delight. La Cutting Board consuma **una sola pizza** e produce **quattro fette**, una per quarto.
- **Pizza uniforme:** quattro spicchi dello stesso gusto, impilabili fino al limite già stabilito di 16.
- **Pizza mista:** le quattro fette mantengono separatamente ingredienti, modello, nome e nutrizione del rispettivo quarto (esempio: Margherita, Salame, Rossa e Quattro Formaggi).
- Il coltello perde **un punto di durabilità**; effetti audio/particelle e avanzamento della Cutting Board sono usati come nelle ricette native FD.
- Il taglio è server-autoritativo: rimuove la pizza prima di generare i quattro output. Il metodo di prelievo manuale dalla pizza collocata nel mondo continua a funzionare senza coltello.
- La ricetta vanilla FD non usa output dinamici per il contenuto dei quattro quarti; l'integrazione è quindi nel comportamento della BlockEntity. **La Cutting Board automatizzata tramite dispenser non riceve questa nuova capacità** fino a progettazione/test separati.

## Test da eseguire in Minecraft

- [ ] Aprire JEI, verificare **18 ricette Pizza Oven**, **19 schede di taglio (18 + mista)**, gusti e icone corrette; usare R/U su varie pizze e fette, testare il bookmark.
- [ ] Verificare le ricette di crafting per Margherita, Salame, Quattro Formaggi e altre, inclusa la restituzione delle ciotole; **nessuna ricetta fittizia di Bianca con un solo impasto**.
- [ ] Tagliare sulla Cutting Board una Margherita e controllare quattro fette; tagliare una Pizza mista e verificare i quattro nomi/spicchi diversi.
- [ ] Verificare che la pizza intera scompaia **una sola volta**, e che il coltello perda un punto; provare coltello in ferro e altri utensili del tag knives.
- [ ] Tentare di tagliare una **pizza cruda** o con utensili non coltelli; non deve avvenire alcun taglio.
- [ ] Provare ingredienti/condimenti custom e una pizza mista appena caricata da un mondo salvato, dopo ripartenza; controllare fame, saturazione, Nourishment e nomi.
- [ ] Testare un inventario pieno, più clic ravvicinati e due giocatori, raccolta degli output, tasti di interazione e suoni.
- [ ] Provare server dedicato Fabric con Farmer's Delight Refabricated e JEI opzionale sul client; non devono esistere crash per riferimento a classi solo client sul server.

**Non segnare come eseguiti i test non dichiarati dall'autore.**
