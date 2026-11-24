# Italian's Delight Refabricated – 2.0.0: proposte alimentari

**Stato:** progettazione, 24 novembre 2026. Cinque ingredienti, dodici piatti di pasta, nove risotti e diciassette secondi approvati e implementati; le altre categorie restano da valutare.

## Obiettivo e vincoli

La versione 2.0.0 prevede nuovi cibi e, in fasi successive, focacce, un nuovo bioma e nuove strutture. Questa fase riguarda esclusivamente i cibi: le focacce saranno progettate separatamente.

Preferire ingredienti di Minecraft, Farmer's Delight Refabricated e Italian's Delight Refabricated già disponibili. Sono ammessi nuovi ingredienti semplici ottenibili mediante crafting, Cooking Pot, Cutting Board o altri sistemi esistenti. Evitare nuove coltivazioni (es. melanzane), blocchi funzionali, entità e nuove meccaniche.

### Difficoltà

- **Facile:** nuovo alimento, texture e ricetta con ingredienti esistenti.
- **Intermedio:** richiede uno o più nuovi ingredienti semplici; ad esempio il ragù, ottenibile mediante Cooking Pot. Non implica nuove meccaniche.
- **Complesso:** richiede nuove coltivazioni, blocchi funzionali, entità o meccaniche; da evitare per ora.

Le valutazioni sono preliminari: sono stati consultati gli item e le ricette del branch main, ma non tutte le combinazioni con Farmer's Delight sono state verificate. Quantità, ricette effettive, traduzioni, valori nutritivi, texture e compatibilità JEI verranno definiti solo dopo la selezione.

### Modalità di lavoro

Valutare una categoria alla volta. Tutte le proposte sono **da valutare**; segnare la decisione come approvata, scartata o rinviata. Nessun elemento deve essere implementato solo perché presente in questo elenco.

## 1. Primi piatti – Pasta

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Pasta all'arrabbiata | Pasta, pomodoro, peperoncino, olio | Facile | Implementato |
| Pasta alla sorrentina | Pasta, pomodoro, mozzarella, basilico | Facile | Implementato |
| Pasta ai quattro formaggi | Pasta, gorgonzola, parmigiano, provolone, scamorza | Facile | Implementato |
| Pasta alla boscaiola | Pasta, funghi, pancetta, panna | Facile | Implementato |
| Pasta alla crudaiola | Pasta, pomodoro, basilico, ricotta | Facile | Implementato |
| Pasta alla cenere | Pasta, gorgonzola, olive | Facile | Implementato |
| Pasta con mortadella e ricotta | Pasta, mortadella, ricotta | Facile | Implementato |
| Pasta con funghi e scamorza | Pasta, funghi, scamorza | Facile | Implementato |
| Pasta al ragù | Pasta, ragù, parmigiano | Facile | Implementato |
| Pasta alla puttanesca | Pasta, pomodoro, olive, peperoncino, acciughe sostituibili con merluzzo | Facile (adattamento) | Implementato |
| Pasta al salmone | Pasta, salmone, panna | Facile | Implementato |
| Pasta alla zozzona | Pasta, salsiccia, guanciale, uovo, pomodoro, pecorino | Facile | Implementato |

La puttanesca con merluzzo è un adattamento per Minecraft, non la ricetta tradizionale.

## 2. Risotti

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Risotto al vino rosso | Riso, vino rosso, parmigiano | Facile | Rinviato |
| Risotto alla zucca | Riso, zucca, parmigiano | Facile | Implementato |
| Risotto ai quattro formaggi | Riso, quattro formaggi | Facile | Implementato |
| Risotto alle mandorle | Riso, mandorle, panna | Facile | Implementato |
| Risotto alle ciliegie | Riso, ciliegie, parmigiano | Facile | Rinviato |
| Risotto al radicchio | Riso, cavolo rosso come sostituto del radicchio, vino | Intermedio (da rinviare) | Rinviato |
| Risotto alla pancetta | Riso, pancetta, parmigiano | Facile | Implementato |
| Risotto al ragù | Riso, ragù, parmigiano | Facile | Implementato |
| Risotto al salmone | Riso, salmone, panna | Facile | Implementato |
| Risotto alla salsiccia | Riso, salsiccia, parmigiano | Facile | Implementato |
| Risotto ai funghi e salsiccia | Riso, funghi, salsiccia, parmigiano | Facile | Implementato |
| Risotto al peperoncino | Riso, peperoncino, salsa di pomodoro, olio | Facile | Implementato |

Il cavolo rosso non è standard in Farmer's Delight: il risotto al radicchio resta in elenco per valutazione, ma va rinviato se richiede una nuova coltivazione. Verificare anche la distinzione dalle varianti di risotto al vino già esistenti.

## 3. Secondi piatti

| Piatto | Ingredienti effettivi | Formato e metodo | Decisione |
| --- | --- | --- | --- |
| Pollo alla cacciatora | `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `farmersdelight:tomato_sauce`, `italiansdelight:olive_pitted`, `italiansdelight:wine_bottle`, `minecraft:bowl` | Feast (4 porzioni), Crafting Table | Implementato |
| Pollo alla diavola | `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `#c:foods/cooked_chicken`, `italiansdelight:chili_pepper`, `italiansdelight:chili_pepper`, `italiansdelight:olive_oil_bottle`, `minecraft:bowl` | Feast (4 porzioni), Crafting Table | Implementato |
| Frittata di patate | `#minecraft:eggs`, `#minecraft:eggs`, `#minecraft:eggs`, `#minecraft:eggs`, `minecraft:potato`, `italiansdelight:olive_oil_bottle` | Piazzabile (4 fette), Cooking Pot | Implementato |
| Frittata al formaggio | `#minecraft:eggs`, `#minecraft:eggs`, `#minecraft:eggs`, `italiansdelight:grated_parmigiano_reggiano`, `italiansdelight:scamorza_slice`, `italiansdelight:olive_oil_bottle` | Piazzabile (4 fette), Cooking Pot | Implementato |
| Frittata con pancetta | `#minecraft:eggs`, `#minecraft:eggs`, `#minecraft:eggs`, `italiansdelight:pancetta_slice`, `italiansdelight:grated_parmigiano_reggiano`, `italiansdelight:olive_oil_bottle` | Piazzabile (4 fette), Cooking Pot | Implementato |
| Spezzatino con patate | `#c:foods/raw_beef`, `minecraft:potato`, `minecraft:carrot`, `#c:crops/onion` | Piatto singolo, Cooking Pot | Implementato |
| Salsiccia e patate | `italiansdelight:sausage`, `minecraft:potato`, `italiansdelight:olive_oil_bottle` | Piatto singolo, Cooking Pot | Implementato |
| Carne alla pizzaiola | `#c:foods/raw_beef`, `farmersdelight:tomato_sauce`, `italiansdelight:olive_oil_bottle`, `italiansdelight:basil` | Piatto singolo, Cooking Pot | Implementato |
| Merluzzo gratinato | `#c:foods/raw_cod`, `italiansdelight:breadcrumbs`, `italiansdelight:olive_oil_bottle`, `italiansdelight:basil` | Piatto singolo, Cooking Pot | Implementato |
| Scamorza alla pizzaiola | `italiansdelight:scamorza_slice`, `farmersdelight:tomato_sauce`, `italiansdelight:basil` | Piatto singolo, Cooking Pot | Implementato |
| Polpette al sugo | `farmersdelight:minced_beef`, `#minecraft:eggs`, `italiansdelight:breadcrumbs`, `farmersdelight:tomato_sauce` | Senza recipiente, Cooking Pot | Implementato |
| Involtini di carne | `#c:foods/raw_beef`, `italiansdelight:prosciutto_crudo_slice`, `italiansdelight:scamorza_slice` | Senza recipiente, Cooking Pot | Implementato |
| Polpette di ricotta | `italiansdelight:ricotta`, `#minecraft:eggs`, `italiansdelight:breadcrumbs`, `italiansdelight:grated_parmigiano_reggiano` | Senza recipiente, Cooking Pot | Implementato |
| Cotoletta alla milanese | `#c:foods/raw_beef`, `#minecraft:eggs`, `italiansdelight:breadcrumbs`, `italiansdelight:olive_oil_bottle` | Senza recipiente, Cooking Pot | Implementato |
| Cotoletta di pollo | `#c:foods/raw_chicken`, `#minecraft:eggs`, `italiansdelight:breadcrumbs`, `italiansdelight:olive_oil_bottle` | Senza recipiente, Cooking Pot | Implementato |
| Polpette fritte | `farmersdelight:minced_beef`, `#minecraft:eggs`, `italiansdelight:breadcrumbs`, `italiansdelight:olive_oil_bottle` | Senza recipiente, Cooking Pot | Implementato |
| Mozzarella in carrozza | `italiansdelight:mozzarella_slice`, `minecraft:bread`, `#minecraft:eggs`, `italiansdelight:olive_oil_bottle` | Senza recipiente, Cooking Pot | Implementato |
| Merluzzo alla livornese | — | — | Escluso |
| Frittata di pasta | — | — | Escluso |
| Scaloppine al vino bianco | — | — | Escluso |
| Scaloppine ai funghi | — | — | Escluso |
| Pollo alle mandorle | — | — | Escluso |
| Pollo al vino bianco | — | — | Escluso |
| Salmone in crosta di mandorle | — | — | Escluso |
| Salsiccia al vino bianco | — | — | Escluso |

## 4. Insalate e antipasti

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Insalata di patate | Patate, cipolla, olio | Facile | Da valutare |
| Insalata di riso | Riso, olive, carote, formaggio | Facile | Da valutare |
| Panzanella | Pane, pomodoro, cipolla, basilico, aceto | Facile | Da valutare |
| Insalata con burrata | Burrata, pomodoro, basilico, olio | Facile | Da valutare |
| Bresaola e parmigiano | Bresaola, parmigiano, olio | Facile | Da valutare |
| Tagliere di salumi | Salame, prosciutto, mortadella, speck | Facile | Da valutare |
| Tagliere di formaggi | Parmigiano, pecorino, gorgonzola, provolone | Facile | Da valutare |
| Tagliere misto | Salumi, formaggi, olive | Facile | Da valutare |
| Bruschetta alla burrata | Pane, burrata, pomodoro | Facile | Da valutare |
| Bruschetta ai funghi | Pane, funghi, olio | Facile | Da valutare |
| Bruschetta con prosciutto | Pane, prosciutto, mozzarella | Facile | Da valutare |

I taglieri possono essere semplici alimenti serviti in un piatto, senza blocchi piazzabili.

## 5. Street food

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Panzerotto | Impasto, mozzarella, pomodoro, olio | Facile | Da valutare |
| Calzone | Impasto, mozzarella, pomodoro, prosciutto | Facile | Da valutare |
| Arancino al ragù | Riso, ragù, mozzarella, pane | Intermedio | Da valutare |
| Arancino al prosciutto | Riso, prosciutto, mozzarella | Facile | Da valutare |
| Supplì | Riso, pomodoro, mozzarella, pane | Facile | Da valutare |
| Crocchette di patate | Patate, uovo, pane, olio | Facile | Da valutare |
| Olive all'ascolana | Olive denocciolate, carne macinata, pane, uovo | Facile | Da valutare |
| Taralli | Impasto, olio, vino bianco | Facile | Da valutare |
| Taralli piccanti | Impasto, olio, vino bianco, peperoncino | Facile | Da valutare |
| Grissini | Impasto, olio, sale | Facile | Da valutare |
| Panino alla mortadella | Pane, mortadella | Facile | Da valutare |
| Panino al prosciutto | Pane, prosciutto, mozzarella | Facile | Da valutare |
| Panino allo speck | Pane, speck, scamorza | Facile | Da valutare |
| Panino al salame | Pane, salame, provolone | Facile | Da valutare |

## 6. Dolci

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Cannoli siciliani | Impasto, ricotta, zucchero, cacao | Facile | Da valutare |
| Panna cotta | Panna, zucchero | Facile | Da valutare |
| Panna cotta alle ciliegie | Panna, zucchero, marmellata di ciliegie | Facile | Da valutare |
| Panna cotta al cioccolato | Panna, cacao, zucchero | Facile | Da valutare |
| Cantucci | Impasto, mandorle, uova, zucchero | Facile | Da valutare |
| Torrone alle mandorle | Mandorle, miele, zucchero, uova | Facile | Da valutare |
| Struffoli | Impasto, uova, miele | Facile | Da valutare |
| Salame di cioccolato | Biscotti, cacao, zucchero | Facile | Da valutare |
| Biscotti al vino | Impasto, vino, zucchero, olio | Facile | Da valutare |
| Biscotti alle ciliegie | Impasto, zucchero, marmellata | Facile | Da valutare |
| Biscotti ripieni di ricotta | Impasto, ricotta, zucchero | Facile | Da valutare |
| Torta di ricotta | Impasto, ricotta, zucchero, uova | Facile | Da valutare |
| Torta della nonna | Impasto, crema pasticcera, mandorle | Intermedio (adattamento) | Da valutare |
| Pasticciotto leccese | Impasto, crema pasticcera | Intermedio | Da valutare |
| Panettone | Impasto, uova, zucchero, uva passa | Intermedio | Da valutare |
| Pandoro | Impasto, uova, zucchero, burro | Intermedio | Da valutare |
| Cartellate pugliesi | Impasto, vino bianco, olio, mosto cotto | Intermedio | Da valutare |

Ricotta, mascarpone, panna, mandorle, ciliegie, uva e miele sono già disponibili. Per la torta della nonna, le mandorle sostituiscono i pinoli come adattamento.

## 7. Nuovi ingredienti semplici

| Ingrediente | Ingredienti principali | Metodo | Decisione |
| --- | --- | --- | --- |
| Ragù | Carne macinata (FDR Minced Beef), pomodoro, olio d'oliva | Cooking Pot | Implementato |
| Besciamella | Latte, farina, burro | Cooking Pot | Da valutare |
| Burro | Panna | Crafting | Da valutare |
| Farina | Grano | Crafting | Da valutare |
| Crema pasticciera | Latte vaccino o di pecora (bottiglia o secchio), uovo, zucchero, grano (in sostituzione della farina) | Cooking Pot | Implementato |
| Pangrattato | Pane, coltello | Cutting Board | Implementato |
| Salsiccia | Carne di maiale, sale | Crafting | Implementato |
| Sfoglia di pasta | Wheat Dough (FDR), mattarello | Cutting Board | Implementato |
| Gnocchi crudi | Patate, farina, uovo | Crafting | Da valutare |
| Ravioli crudi | Sfoglia di pasta, ricotta | Crafting | Da valutare |
| Uva passa | Uva | Essiccazione esistente o cottura | Da valutare |
| Mosto cotto | Succo d'uva | Cooking Pot | Da valutare |

Gli ingredienti sono proposte: riutilizzare i sistemi esistenti, senza creare nuove macchine o meccaniche. Verificare la disponibilità di ciascuna materia prima e la fattibilità dei contenitori e delle ricette.

## 8. Ulteriori piatti sbloccati dai nuovi ingredienti

| Piatto | Ingredienti principali | Difficoltà | Decisione |
| --- | --- | --- | --- |
| Lasagne alla bolognese | Sfoglia, ragù, besciamella, parmigiano | Intermedio | Da valutare |
| Gnocchi alla sorrentina | Gnocchi, pomodoro, mozzarella, basilico | Intermedio | Da valutare |
| Gnocchi al gorgonzola | Gnocchi, gorgonzola, panna | Intermedio | Da valutare |
| Ravioli al burro | Ravioli di ricotta, burro, parmigiano | Intermedio | Da valutare |
| Cannelloni al ragù | Sfoglia, ragù, besciamella, parmigiano | Intermedio | Da valutare |
| Pasta al forno | Pasta, ragù, mozzarella, parmigiano | Intermedio | Da valutare |

Le difficoltà sono stime riferite all'introduzione delle relative preparazioni intermedie.

## Priorità proposte (non approvate)

- Valutare ragù, farina, burro, besciamella e crema pasticcera: ingredienti semplici che sbloccano famiglie di ricette.
- Valorizzare maggiormente salumi e formaggi già implementati, soprattutto mortadella, bresaola, burrata e provolone.
- Verificare le ricette già presenti per evitare duplicazioni; confermare gli ingredienti realmente disponibili e gli adattamenti prima di implementare.

Per ora: nessuna focaccia, nessuna nuova coltivazione; le categorie pasta, risotti e secondi sono state valutate. Gli altri sette ingredienti non sono stati selezionati per questa fase.

## Riferimenti interni

- [Traduzioni degli item esistenti](../../src/main/resources/assets/italiansdelight/lang/en_us.json)
- [Risorse della mod](../../src/main/resources/)

## Aggiornamento implementazione – ingredienti

Sono stati aggiunti esclusivamente ragù, crema pasticciera, pangrattato, salsiccia e sfoglia di pasta. Nessuna farina, burro o altro ingrediente aggiuntivo. Rimossi i tre crafting alternativi dei risotti con vino: ai funghi con vino rosso/bianco e al parmigiano con vino bianco. Le ricette Cooking Pot originali restano disponibili. La sfoglia riutilizza il mattarello come utensile sul Cutting Board, senza consumarlo.

## 2.0.0 – Pasta: implementazione definitiva

Tutte le 12 ricette usano Cooking Pot, 1 ciotola per il risultato, nessun crafting alternativo. Gli ingredienti sono item già esistenti; tag pasta e funghi come nelle ricette precedenti. Le texture mantengono i pixel del recipiente di `pasta_alla_carbonara.png`, con pasta e condimenti ridisegnati in gruppi distinti. Valori nutrizionali e Nourishment sono da bilanciare durante i test.

| Item | Ingredienti effettivi | Fame | Saturation modifier | Nourishment |
| --- | --- | ---: | ---: | --- |
| `pasta_all_arrabbiata` | `#c:foods/pasta`, `farmersdelight:tomato_sauce`, `italiansdelight:chili_pepper`, `italiansdelight:olive_oil_bottle` | 10 | 0.7 | MEDIUM |
| `pasta_alla_sorrentina` | `#c:foods/pasta`, `farmersdelight:tomato_sauce`, `italiansdelight:mozzarella_slice`, `italiansdelight:basil` | 12 | 0.75 | MEDIUM |
| `pasta_ai_quattro_formaggi` | `#c:foods/pasta`, `italiansdelight:gorgonzola_wedge`, `italiansdelight:grated_parmigiano_reggiano`, `italiansdelight:provolone_slice`, `italiansdelight:scamorza_slice` | 14 | 0.8 | LONG |
| `pasta_alla_boscaiola` | `#c:foods/pasta`, `#c:mushrooms`, `italiansdelight:pancetta_slice`, `italiansdelight:cream_bowl` | 14 | 0.75 | LONG |
| `pasta_alla_crudaiola` | `#c:foods/pasta`, `italiansdelight:tomato_slice`, `italiansdelight:basil`, `italiansdelight:ricotta` | 10 | 0.7 | MEDIUM |
| `pasta_alla_cenere` | `#c:foods/pasta`, `italiansdelight:gorgonzola_wedge`, `italiansdelight:olive_pitted` | 12 | 0.75 | MEDIUM |
| `pasta_with_mortadella_and_ricotta` | `#c:foods/pasta`, `italiansdelight:mortadella_slice`, `italiansdelight:ricotta` | 12 | 0.75 | MEDIUM |
| `pasta_with_mushroom_and_scamorza` | `#c:foods/pasta`, `#c:mushrooms`, `italiansdelight:scamorza_slice` | 12 | 0.75 | MEDIUM |
| `pasta_with_ragu` | `#c:foods/pasta`, `italiansdelight:ragu`, `italiansdelight:grated_parmigiano_reggiano` | 14 | 0.75 | LONG |
| `pasta_alla_puttanesca` | `#c:foods/pasta`, `farmersdelight:tomato_sauce`, `italiansdelight:olive_pitted`, `italiansdelight:chili_pepper`, `#c:foods/raw_cod` | 14 | 0.75 | LONG |
| `pasta_with_salmon` | `#c:foods/pasta`, `#c:foods/raw_salmon`, `italiansdelight:cream_bowl` | 12 | 0.75 | MEDIUM |
| `pasta_alla_zozzona` | `#c:foods/pasta`, `italiansdelight:sausage`, `italiansdelight:guanciale_slice`, `minecraft:egg`, `farmersdelight:tomato_sauce`, `italiansdelight:grated_pecorino_romano` | 16 | 0.8 | LONG |

Verificare in gioco tutte le ricette, i contenitori restituiti, JEI e le texture.

## Compatibilità carne e pesce

Per le ricette che utilizzano carne o pesce vanilla, preferire i tag specifici di Farmer's Delight Refabricated che comprendono l'item intero e il suo taglio equivalente, mantenendo separati crudo e cotto e senza duplicare le ricette. Verificati sul branch `fabric/latest/26.3` (revisione `1b85d66f5e2aba88592d7f4fb203d582f4d3cbe3`): `#c:foods/raw_cod` (cod/cod_slice), `#c:foods/raw_salmon` (salmon/salmon_slice), `#c:foods/raw_beef` (beef/minced_beef), `#c:foods/raw_pork` (porkchop/bacon tramite raw_bacon). Per i futuri piatti verificare analogamente i tag di pollo e montone. Il ragù conserva `farmersdelight:minced_beef` come richiesto esplicitamente; le ricette dei salumi che richiedono specificamente ham o bacon restano invariate. Quantità, metodi e risultati invariati.

## 2.0.0 – Risotti: implementazione

Approvati solo i nove risotti sotto. Gli altri risotti proposti non sono stati implementati. Tutti utilizzano Cooking Pot, una ciotola per il risultato e nessun crafting alternativo. Per il salmone si usa il tag FDR `#c:foods/raw_salmon`, valido per salmone intero o affettato. La zucca usa `farmersdelight:pumpkin_slice`. Le texture 16×16 RGBA mantengono i pixel del recipiente dei risotti originali `risotto_with_tomato_sauce.png` e `risotto_with_mushroom.png`; il riso e i condimenti hanno profili e palette individuali.

| Item | Ingredienti effettivi | Fame | Saturation modifier | Nourishment |
| --- | --- | ---: | ---: | --- |
| `risotto_with_pumpkin` | `#c:crops/rice`, `farmersdelight:pumpkin_slice`, `italiansdelight:grated_parmigiano_reggiano` | 12 | 0.75 | MEDIUM |
| `risotto_ai_quattro_formaggi` | `#c:crops/rice`, `italiansdelight:gorgonzola_wedge`, `italiansdelight:grated_parmigiano_reggiano`, `italiansdelight:provolone_slice`, `italiansdelight:scamorza_slice` | 14 | 0.8 | LONG |
| `risotto_with_almonds` | `#c:crops/rice`, `italiansdelight:chopped_almonds`, `italiansdelight:cream_bowl` | 12 | 0.75 | MEDIUM |
| `risotto_with_pancetta` | `#c:crops/rice`, `italiansdelight:pancetta_slice`, `italiansdelight:grated_parmigiano_reggiano` | 12 | 0.8 | MEDIUM |
| `risotto_with_ragu` | `#c:crops/rice`, `italiansdelight:ragu`, `italiansdelight:grated_parmigiano_reggiano` | 14 | 0.75 | LONG |
| `risotto_with_salmon` | `#c:crops/rice`, `#c:foods/raw_salmon`, `italiansdelight:cream_bowl` | 12 | 0.75 | MEDIUM |
| `risotto_with_sausage` | `#c:crops/rice`, `italiansdelight:sausage`, `italiansdelight:grated_parmigiano_reggiano` | 12 | 0.8 | MEDIUM |
| `risotto_with_mushrooms_and_sausage` | `#c:crops/rice`, `#c:mushrooms`, `italiansdelight:sausage`, `italiansdelight:grated_parmigiano_reggiano` | 14 | 0.75 | LONG |
| `risotto_with_chili_pepper` | `#c:crops/rice`, `italiansdelight:chili_pepper`, `farmersdelight:tomato_sauce`, `italiansdelight:olive_oil_bottle` | 10 | 0.7 | MEDIUM |

Valori nutritivi proposti in continuità con i risotti precedenti. Test in gioco, JEI e compilazione da verificare.

## Uniformazione recipienti

Verificati tutti i 22 piatti di pasta e 14 risotti; aggiornati 21 piatti di pasta e 11 risotti. Carbonara, risotto al sugo, risotto ai funghi e risotto con scamorza e olive mantenuti invariati. Recipienti standard secondo `docs/PROMPTS.md`; sagome e condimenti distinti. Nessuna modifica alle ricette o al codice.

## 2.0.0 – Secondi: implementazione

Diciassette piatti approvati: 2 Feast di pollo, 3 frittate piazzabili, 5 piatti in ciotola e 7 alimenti senza recipiente. Il pollo usa 4 porzioni di pollo cotto (tag FDR, intero o tagliato) e condimenti in Crafting Table; le frittate e i secondi individuali usano esclusivamente Cooking Pot. Le polpette richiedono carne macinata, mentre gli altri piatti con carne/pesce vanilla usano i tag FDR specifici. Nessun ingrediente intermedio aggiunto. Le porzioni non hanno ricette alternative. Il vassoio pieno è recuperabile; se parzialmente consumato, restituisce solo una ciotola. Le frittate parziali restituiscono soltanto le fette rimaste. JEI mostra le ricette standard e pagine informative sulle porzioni. I valori nutrizionali sono proposti e da collaudare.
