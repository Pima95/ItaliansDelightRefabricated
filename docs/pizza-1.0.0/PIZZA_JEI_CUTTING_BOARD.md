# Pizza JEI e Cutting Board — aggiornamento del 23 ottobre 2026

**Branch:** `feature/pizza-1.0.0` (nessun nuovo branch).
**Stato:** implementato e sottoposto alla CI, test runtime in Minecraft non ancora confermati.

## Catalogo degli oggetti nella lista laterale di JEI

**Correzione 23 ottobre 2026:** oltre alle ricette, JEI elenca singolarmente come ingredienti **54 varianti riconosciute** — 18 pizze crude, 18 pizze cotte e 18 spicchi — e **due esempi di Pizza mista** (cruda e cotta). Queste voci appaiono nella **barra degli item** di JEI e sono ricercabili con i nomi localizzati, come pasta e cibi tagliabili di Farmer's Delight; non sono soltanto esempi nascosti all'interno delle categorie.

Per rispettare i salvataggi già esistenti vengono usati veri `ItemStack` con i componenti originali `PIZZA_CONTENTS`/`PIZZA_SLICE_CONTENTS`, l'`ITEM_MODEL` e il nome approvato, **non 56 nuove registrazioni item**. `registerExtraIngredients` inserisce gli stack nel catalogo di JEI; `registerItemSubtypes` fa sì che gusto e quarti identifichino correttamente il singolo tipo anche quando cambiano ID di applicazione, orientamento, render seed o provenienza degli ingredienti. Fette provenienti da una pizza mista e da una uniforme dello stesso gusto corrispondono alla **stessa** voce JEI. Le pizze miste non riconosciute sono distinte in base ai quattro quarti effettivi.

**Test da fare:** cercare nel pannello di JEI 'Margherita', 'Rossa', 'Quattro Formaggi', 'Spicchio' e 'Pizza mista'; verificare i modelli, le varianti e la ricerca R/U sul prodotto vero ottenuto in gioco, anche dopo un salvataggio. Gli item generici senza condimenti devono continuare a essere riconosciuti senza corrompere i componenti.

## Cosa contiene davvero JEI

1. **Pizze personalizzabili / crafting:** i cinque esempi originari sono conservati e ora vengono aggiunti i gusti riconosciuti dal catalogo `PizzaDisplayNames.allPresets()` con almeno un topping. La vera ricetta in gioco è una sola ricetta shapeless dinamica: queste sono **anteprime JEI**, non ulteriori ricette o restrizioni.
2. **Pizza Oven:** una ricetta JEI con input raw e output cooked autentico per ciascuno dei **18 gusti riconosciuti**, con il solito tempo di 20 secondi. Ogni gusto ha un identificatore JEI distinto e bookmark. Le fornaci e gli affumicatori vanilla conservano le proprie categorie (60/30 secondi).
3. **Cutting Board / Spicchi di pizza:** nuova categoria con **18 pizze cotte riconosciute** più una **Pizza mista**. Per ognuna sono mostrati quattro spicchi ottenuti davvero dai propri quarti. La categoria è ricercabile partendo dalla pizza cotta, dallo spicchio o dal tagliere.
4. **Preparazione nel mondo:** **18 guide personalizzate da sette passaggi ciascuna (126 pagine JEI)**, ricavate direttamente dall'elenco di gusti riconosciuti e dai loro ingredienti originali. Ogni pagina usa il nome e la pizza effettiva; la pagina «Condimenti» elenca gli oggetti precisi che si devono applicare alla base stesa. La **Pizza Bianca** mostra esplicitamente che non richiede topping. Non sono state aggiunte finte ricette shapeless.

Nessuna nuova texture, nuovo item o nuova ricetta fissa duplicata è stata introdotta.

## GUI Cutting Board standard — Farmer's Delight Refabricated

Il precedente layout custom largo 165 pixel è stato sostituito dall'**interfaccia JEI standard di Farmer's Delight Refabricated 26.2**, utilizzando direttamente la sua texture `farmersdelight:textures/gui/jei/cutting_board.png`, il formato **117×57**, l'icona e il titolo della Cutting Board, il coltello a `(16,8)`, l'ingrediente a `(16,27)` e i **quattro risultati in griglia 2×2** a destra con gli slot standard. Non si copiano PNG e non si modifica la vera Cutting Board. Manteniamo solo un tipo ricetta JEI specifico perché quattro `ItemStack` con componenti per-quarto differenti non possono essere rappresentati da una ricetta JSON standard a risultato fisso.

**Cheese Vat / Ricotta:** sovrapposizione del siero nel tank destro abbassata di esattamente **4 pixel**, passando da `WHEY_TANK_Y=1` a `5`. Texture, quantità del siero, altezza del riempimento e hitbox del tooltip rimangono invariati.

## Come si usa la Cutting Board

- Posizionare sul tagliere Farmer's Delight Refabricated **una pizza cotta intera** (il singolo item con `PIZZA_CONTENTS`).
- Cliccare con **un coltello del tag `c:tools/knife`** utilizzato da Farmer's Delight. La Cutting Board consuma **una sola pizza** e produce **quattro fette**, una per quarto.
- **Pizza uniforme:** quattro spicchi dello stesso gusto, impilabili fino al limite già stabilito di 16.
- **Pizza mista:** le quattro fette mantengono separatamente ingredienti, modello, nome e nutrizione del rispettivo quarto (esempio: Margherita, Salame, Rossa e Quattro Formaggi).
- Il coltello perde **un punto di durabilità**; effetti audio/particelle e avanzamento della Cutting Board sono usati come nelle ricette native FD.
- Il taglio è server-autoritativo: rimuove la pizza prima di generare i quattro output. Il metodo di prelievo manuale dalla pizza collocata nel mondo continua a funzionare senza coltello.
- La ricetta vanilla FD non usa output dinamici per il contenuto dei quattro quarti; l'integrazione è quindi nel comportamento della BlockEntity. **La Cutting Board automatizzata tramite dispenser non riceve questa nuova capacità** fino a progettazione/test separati.

## Test da eseguire in Minecraft

- [ ] Aprire JEI, verificare **56 voci extra nel pannello item** (18 crude + 18 cotte + 18 spicchi + 2 miste), **18 ricette Pizza Oven**, **19 schede di taglio (18 + mista)** e **126 istruzioni nel mondo**, 7 per ciascuna pizza; cercare per nomi italiani/inglesi, usare R/U sugli stack ottenuti realmente in gioco e testare il bookmark.
- [ ] Confrontare la categoria JEI degli spicchi con quella delle altre ricette Cutting Board di FD: dimensione, sfondo, posizioni di coltello/pizza e quattro spicchi in griglia 2×2 devono combaciare.
- [ ] Confrontare Ricotta nella Cheese Vat JEI con la GUI della vasca: il livello di whey nel tank destro deve essere 4 pixel più basso e perfettamente allineato, senza sovrapporsi alla cornice.
- [ ] Verificare le ricette di crafting per Margherita, Salame, Quattro Formaggi e altre, inclusa la restituzione delle ciotole; **nessuna ricetta fittizia di Bianca con un solo impasto**.
- [ ] Tagliare sulla Cutting Board una Margherita e controllare quattro fette; tagliare una Pizza mista e verificare i quattro nomi/spicchi diversi.
- [ ] Verificare che la pizza intera scompaia **una sola volta**, e che il coltello perda un punto; provare coltello in ferro e altri utensili del tag knives.
- [ ] Tentare di tagliare una **pizza cruda** o con utensili non coltelli; non deve avvenire alcun taglio.
- [ ] Provare ingredienti/condimenti custom e una pizza mista appena caricata da un mondo salvato, dopo ripartenza; controllare fame, saturazione, Nourishment e nomi.
- [ ] Testare un inventario pieno, più clic ravvicinati e due giocatori, raccolta degli output, tasti di interazione e suoni.
- [ ] Provare server dedicato Fabric con Farmer's Delight Refabricated e JEI opzionale sul client; non devono esistere crash per riferimento a classi solo client sul server.

**Non segnare come eseguiti i test non dichiarati dall'autore.**
