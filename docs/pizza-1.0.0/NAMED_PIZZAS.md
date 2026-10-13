# Pizze riconoscibili — nomi approvati 1.0.0 e proposte future

> **Decisione definitiva dell'autore del 13 ottobre 2026:** tra le 71 combinazioni per la release 1.0.0, **19 nomi fissi APPROVATI** (18 con item già registrati, 1 con patate a fette da creare) e **52 combinazioni a nome dinamico**. La whitelist rimane di 31 topping in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Le 36 proposte future sono ancora **NON APPROVATE**. **Nessun codice, texture, ricetta o datapack eseguibile è stato modificato**.

## Numeri e stati

- **Gruppo A — 56 combinazioni con item già registrati (inclusa Principessa):** **18 con nome fisso approvato e 38 con nome dinamico**. Il riconoscimento dei nomi non è ancora implementato.
- **Gruppo B — 15 combinazioni con carote/patate a fette da creare:** **1 con nome fisso approvato (Patate e Speck), 14 con nome dinamico**. Le rese delle ricette Cutting Board sono già approvate (1 vegetale -> 4 fette), ma gli item non sono ancora stati implementati.
- **Gruppo C — 36 proposte per aggiornamenti futuri:** i nuovi topping NON appartengono alla whitelist della 1.0.0; **tutti richiedono approvazione esplicita futura** e, quando pertinenti, item/texture/modelli/tag/ricette. Alcune hanno ingredienti extra anche in gastronomia reale, qui riportati come schema concettuale.

**Totale ricognizione:** **107 combinazioni**, di cui **71 per la release 1.0.0 con scelta SÌ/NO definita** e 36 semplici proposte per il futuro. Altre combinazioni libere mantengono il nome dinamico. I nomi ispirati ai piatti tradizionali non attestano conformità a disciplinari culinari.

## Elenco vincolante dei 19 nomi fissi APPROVATI — 13 ottobre 2026

| Nome riconosciuto automaticamente | Condimenti esatti | Disponibilità |
| --- | --- | --- |
| Bianca | Nessun condimento | **Ingredienti già registrati** |
| Rossa | Salsa di pomodoro | **Ingredienti già registrati** |
| Margherita (versione 1.0.0) | Salsa di pomodoro + Mozzarella a fette + Basilico | **Ingredienti già registrati** |
| Principessa | **Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Panna** | **Ingredienti già registrati** |
| Caprese rossa | Salsa di pomodoro + Mozzarella a fette + Pomodoro a fette + Basilico | **Ingredienti già registrati** |
| Caprese bianca | Mozzarella a fette + Pomodoro a fette + Basilico | **Ingredienti già registrati** |
| Quattro Formaggi bianca | Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | **Ingredienti già registrati** |
| Quattro Formaggi rossa | Salsa di pomodoro + Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | **Ingredienti già registrati** |
| Prosciutto crudo | Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette | **Ingredienti già registrati** |
| Salame | Salsa di pomodoro + Mozzarella a fette + Salame a fette | **Ingredienti già registrati** |
| Mortadella e Ricotta | Mozzarella a fette + Mortadella a fette + Ricotta | **Ingredienti già registrati** |
| Guanciale e Pecorino rossa (stile Amatriciana) | Salsa di pomodoro + Guanciale a fette + Pecorino Romano grattugiato | **Ingredienti già registrati** |
| Speck e Gorgonzola | Mozzarella a fette + Speck a fette + Gorgonzola a spicchi | **Ingredienti già registrati** |
| Salmone e Ricotta | Mozzarella a fette + Salmone a fette (crudo/cotto) + Ricotta + Basilico | **Ingredienti già registrati** |
| Salmone e Scamorza affumicata | Mozzarella a fette + Salmone a fette (crudo/cotto) + Scamorza affumicata a fette | **Ingredienti già registrati** |
| Carne macinata e Mozzarella | Salsa di pomodoro + Mozzarella a fette + Carne macinata | **Ingredienti già registrati** |
| Zucca e Speck | Mozzarella a fette + Zucca a fette + Speck a fette | **Ingredienti già registrati** |
| Orto disponibile | Mozzarella a fette + Pomodoro a fette + Foglia di cavolo + Zucca a fette + Basilico | **Ingredienti già registrati** |
| Patate e Speck | Mozzarella a fette + Patate a fette (NUOVO) + Speck a fette | **Attivare dopo registrazione delle patate a fette** |

I **18 gusti già realizzabili con gli item registrati** potranno essere riconosciuti quando sarà implementato il sistema delle pizze. **Patate e Speck** avrà un nome fisso soltanto dopo la creazione delle **patate a fette**: le patate intere `minecraft:potato` restano vietate come topping. **Principessa** usa precisamente salsa di pomodoro, mozzarella, prosciutto crudo a fette e panna: quest'ultima è un condimento cremoso, NON una salsa. **Margherita (versione 1.0.0)** è la variante del catalogo attuale senza olio come topping.

### Regola dei nomi fissi — 4/4 quarti APPROVATA

**MATCHING ESATTO SU QUATTRO QUARTI FISICI — CONFERMATO (13 ottobre 2026):** il nome fisso di una delle **19 ricette SÌ** si applica alla pizza integra **se e solo se ciascuno dei quattro quarti fisici contiene esattamente tutti e soli i tipi canonici di topping previsti dalla stessa ricetta**. Si controllano **quattro insiemi distinti, non la sola unione globale**. Basta **un ingrediente aggiuntivo o mancante in un solo quarto** perché l'intera pizza abbia **nome dinamico**, anche se gli altri tre quarti corrispondono. Il confronto vale anche senza divisioni visibili o dopo riunioni col mattarello; l'ordine di aggiunta è irrilevante, così come lo stato crudo/cotto per gli ingredienti equivalenti e le sostituzioni cross-mod esplicitamente mappate. **Bianca:** tutti e quattro i quarti senza condimenti. **Rossa:** sola salsa di pomodoro nei quattro quarti. **Margherita:** pomodoro, mozzarella a fette e basilico esatti in ciascun quarto. Un quarto con speck extra o senza basilico -> nome dinamico. Le pizze miste a quarti diversi **non** ricevono nomi fissi nella 1.0.0: eventuali Quattro Gusti / Quattro Stagioni riconosciute restano future proposte, **non approvate**. La regola disciplina la denominazione della **pizza integra**; i nomi durante il servizio di fette richiederanno una decisione separata.

## Nomi degli spicchi cotti — CONFERMATO (13 ottobre 2026)

**Il nome di ogni spicchio (item fetta cotta) si calcola INDIPENDENTEMENTE dal nome della pizza intera e dagli altri tre quarti.** Quando si preleva uno spicchio dalla pizza cotta con clic destro a mano vuota, oppure uno spicchio residuo viene ottenuto rompendo la pizza cotta già servita parzialmente, leggere **soltanto i condimenti del suo quarto fisico** e confrontarne l'insieme di **tipi canonici esatti** con ciascuna delle **19 ricette a nome fisso già approvate** in [NAMED_PIZZAS.md](NAMED_PIZZAS.md).

- **Match esatto di un singolo quarto con una delle 19 ricette SÌ:** l'item della fetta prende un nome riconosciuto, per esempio **`Spicchio di Margherita`** quando il suo quarto contiene esattamente `farmersdelight:tomato_sauce`, `italiansdelight:mozzarella_slice` e basilico. **Non occorre che la pizza intera fosse Margherita**, né che gli altri tre quarti fossero identici.
- **Nessun match (ricetta NO, extra o mancanza di un ingrediente, composizione libera):** il singolo spicchio usa un **nome dinamico basato sui soli suoi condimenti**, non un nome ereditato dalla pizza intera o da altri quarti. La grammatica/abbreviazione finale del nome dinamico è ancora da approvare.
- **Pizza mista:** il nome della **pizza intera** resta **dinamico**, in applicazione della regola 4/4 già confermata; **ogni spicchio** può comunque ricevere **il proprio nome fisso** se soddisfa da solo una ricetta approvata, oppure un nome dinamico se non la soddisfa. Quattro spicchi possono perciò avere quattro nomi distinti.
- **Identità canoniche:** l'ordine di applicazione è irrilevante; le versioni crudo/cotto e gli item equivalenti di altre mod, quando esplicitamente mappati, valgono come lo stesso tipo. Non usare la mera categoria nutrizionale per riconoscere un ingrediente e non ammettere item fuori catalogo.
- **Fetta di un quarto vuoto:** corrisponde alla ricetta SÌ della pizza Bianca e avrà un nome coerente con `Spicchio di pizza bianca` (testo/localizzazione da declinare senza rigidità). Il quarto con **sola salsa di pomodoro** corrisponde alla Rossa.
- **Compatibilità col servizio già approvato:** la fetta conserva unicamente i suoi topping, nutrienti, dati e stato, senza nuovi item registrati per ogni gusto; il nome è un **display name localizzato e derivato dai dati dello spicchio**. La pizza intera cotta **non è commestibile**. Le porzioni continuano a essere 4 e vengono prelevate dalla pizza cotta senza coltello. Inventario pieno o mano occupata non producono una fetta e non cambiano il blocco. I rimborsi/drops e la nutrizione restano invariati.
- **Limite del nuovo requisito:** il riconoscimento dei nomi **delle fette cotte è ora deciso**. Rimangono aperte le convenzioni di abbreviazione dei nomi dinamici molto lunghi e gli eventuali dettagli del nome del blocco pizza **rimasto nel mondo dopo il servizio parziale**, senza bloccare il naming del singolo item.

**Esempio:** pizza intera con quarto 1 Margherita, quarto 2 Principessa, quarto 3 Rossa, quarto 4 una combinazione personalizzata. La pizza intera ha nome dinamico. Dopo la cottura, le fette recuperate dai rispettivi quarti prendono **Spicchio di Margherita**, **Spicchio di Principessa**, **Spicchio di pizza rossa**, **Spicchio con ...** (per la quarta composizione libera). I nomi fissi si applicano **anche se i quarti adiacenti hanno altri condimenti**.

### Altre 52 combinazioni: SOLO NOME DINAMICO

Le **52 righe con NO** riportate nelle tabelle sottostanti sono gusti che il giocatore può preparare (se gli item esistono), ma **non devono mai ricevere un preset di denominazione fissa**. I loro nomi nella tabella servono a identificare il gusto nel documento, non a tradursi automaticamente in un nome specifico in gioco. Anche le altre combinazioni libere non incluse fra i 19 SÌ conservano il nome dinamico. Non ereditare automaticamente il nome da una ricetta presente come sottoinsieme.

## Vincoli e criteri comuni di design

1. **Ogni singolo ingrediente nel gruppo A usa soltanto gli item della whitelist già registrati** di [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Gruppo B usa soltanto gli stessi item e le due future fette di carota/patata già approvate. **Nessun item non autorizzato entra nella 1.0.0 attraverso un nome di pizza**.
2. Tutte le combinazioni uniformi elencate nei gruppi A/B contengono **da 0 a 6 ingredienti distinti per quarto** (la salsa è un ingrediente). La salsa è opzionale e massimo una per quarto fisico. **Panna e ricotta NON sono salse**, quindi possono coesistere con il pomodoro e tra loro. La loro categoria nutrizionale è Latticini e contribuisce una sola volta per fetta; analogamente gli altri gruppi nutrizionali.
3. Le coppie crude/cotte bacon, pollo a pezzi, merluzzo a fette e salmone a fette sono **la stessa identità canonica per coppia**. I sostituti di altre mod riconosciuti come equivalenti tramite tag `c:` **specifici/verificati** devono soddisfare esattamente gli stessi gusti; un tag generico non cambia identità o whitelist.
4. Una **combinazione esatta** di topping per pizza **uniformemente condita** può ottenere un nome fisso; con topping ulteriori, il nome diventa dinamico a meno di definizione più specifica approvata. L'ordine di posa è irrilevante. **La gestione delle pizze con quarti diversi è distinta** e richiede un possibile algoritmo di riconoscimento spaziale, non la sola somma globale dei topping. Una pizza con più gusti separati sui quarti non deve ricevere per errore il nome di una pizza uniforme.
5. Sulla **Margherita di gioco** si usano salsa di pomodoro, mozzarella e basilico; la variante napoletana disciplinata include anche olio e possibili dettagli di formaggio: **non rivendicare conformità AVPN** finché manca olio nella whitelist. **Marinara tradizionale non ottenibile oggi**: necessita almeno aglio, origano e olio EVO. **Diavola** significa **salame piccante**; salame ordinario non basta. **Capricciosa e Quattro Stagioni** necessitano per una proposta fedele funghi, carciofi, olive, prosciutto cotto porzionato, assenti dalla whitelist.
6. Alcuni gusti prendono **ispirazione da altri piatti** (Gricia, Amatriciana, Carbonara). Non sono la ricetta del piatto e non conferiscono effetti bonus oltre fame/saturazione già concordati. Per un'eventuale Carbonara autentica sulla pizza servirebbe una preparazione specifica dell'uovo, poiché la 1.0.0 ammette solo `farmersdelight:fried_egg`, **mai uovo intero**.
7. Per tutte le ricette B, **carote e patate intere non sono condimenti**: servono le fette degli appositi nuovi item, crude al momento della posa e cotte con la pizza, senza secondo item cotto.
8. **Le scelte SÌ/NO delle 71 combinazioni sono APPROVATE**: SÌ significa nome fisso solo per la combinazione esatta selezionata, NO significa **sempre nome dinamico**, anche se la ricetta è elencata per consultazione. I 36 gusti futuri restano proposte. Nessun preset è ancora implementato.

## Gruppo A — 56 combinazioni già componibili (18 SÌ / 38 NO)

### Basi e pizze rosse

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Bianca | Nessun condimento | Base senza condimenti; pizza bianca semplice | **SÌ — nome fisso** |
| Rossa | Salsa di pomodoro | Solo salsa di pomodoro | **SÌ — nome fisso** |
| Margherita (versione 1.0.0) | Salsa di pomodoro + Mozzarella a fette + Basilico | Versione giocabile senza olio come topping; NON equivale alla certificazione AVPN | **SÌ — nome fisso** |
| Margherita al Parmigiano | Salsa di pomodoro + Mozzarella a fette + Basilico + Parmigiano Reggiano grattugiato | Variante della Margherita con formaggio grattugiato | **NO — nome dinamico** |
| Margherita al Pecorino | Salsa di pomodoro + Mozzarella a fette + Basilico + Pecorino Romano grattugiato | Variante con Pecorino grattugiato | **NO — nome dinamico** |
| Margherita cremosa | Salsa di pomodoro + Mozzarella a fette + Basilico + Ricotta | Versione creativa con ricotta | **NO — nome dinamico** |
| **Principessa** | **Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Panna** | **PROPOSTA per 1.0.0**: interpretazione attestata da pizzerie di Bari; tutti e quattro gli item già approvati. Panna = condimento cremoso NON salsa, quindi può coesistere con il pomodoro. Il nome «Principessa» ha anche versioni differenti in altre pizzerie: questa precisa combinazione è stata **APPROVATA dall'autore come Principessa**, pur non essendo una ricetta universalmente standardizzata. | **SÌ — nome fisso** |
| Caprese rossa | Salsa di pomodoro + Mozzarella a fette + Pomodoro a fette + Basilico | Interpretazione della caprese con salsa e pomodoro a fette | **SÌ — nome fisso** |
| Caprese bianca | Mozzarella a fette + Pomodoro a fette + Basilico | Mozzarella, pomodoro fresco a fette e basilico | **SÌ — nome fisso** |
| Pomodoro e Bocconcino | Salsa di pomodoro + Bocconcino + Basilico | Con basilico | **NO — nome dinamico** |
| Pomodoro e Gorgonzola | Salsa di pomodoro + Mozzarella a fette + Gorgonzola a spicchi | Rossa al gorgonzola | **NO — nome dinamico** |

### Formaggi, creme e affumicati

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Quattro Formaggi bianca | Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | Quattro formaggi distinti esatti | **SÌ — nome fisso** |
| Quattro Formaggi rossa | Salsa di pomodoro + Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | Quattro formaggi + salsa, tutti entro 5 tipi | **SÌ — nome fisso** |
| Quattro Formaggi affumicata | Mozzarella a fette + Gorgonzola a spicchi + Scamorza affumicata a fette + Pecorino Romano grattugiato | Versione originale con scamorza affumicata | **NO — nome dinamico** |
| Tre Formaggi bianca | Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette | Tre formaggi distinti | **NO — nome dinamico** |
| Provolone e Pecorino | Mozzarella a fette + Provolone a fette + Pecorino Romano grattugiato | Bianca | **NO — nome dinamico** |
| Scamorza e Parmigiano | Mozzarella a fette + Scamorza a fette + Parmigiano Reggiano grattugiato | Bianca | **NO — nome dinamico** |
| Scamorza affumicata e Gorgonzola | Mozzarella a fette + Scamorza affumicata a fette + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |
| Ricotta e Basilico | Mozzarella a fette + Ricotta + Basilico | Bianca | **NO — nome dinamico** |
| Panna e Ricotta | Mozzarella a fette + Panna + Ricotta | Le creme sono topping, non salse | **NO — nome dinamico** |
| Gorgonzola e Panna | Mozzarella a fette + Gorgonzola a spicchi + Panna | Cremosa | **NO — nome dinamico** |
| Bocconcini e Ricotta | Bocconcino + Ricotta + Basilico | Bianca con basilico | **NO — nome dinamico** |

### Salumi e sapori italiani

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Prosciutto crudo | Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette | Versione rossa, senza prosciutto cotto intero | **SÌ — nome fisso** |
| Prosciutto crudo e Ricotta | Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Ricotta | Quattro topping | **NO — nome dinamico** |
| Salame | Salsa di pomodoro + Mozzarella a fette + Salame a fette | Salame normale, NON Diavola | **SÌ — nome fisso** |
| Salame e Provolone | Salsa di pomodoro + Mozzarella a fette + Salame a fette + Provolone a fette | Rossa | **NO — nome dinamico** |
| Mortadella bianca | Mozzarella a fette + Mortadella a fette | Semplice | **NO — nome dinamico** |
| Mortadella e Ricotta | Mozzarella a fette + Mortadella a fette + Ricotta | Bianca cremosa | **SÌ — nome fisso** |
| Mortadella e Provolone | Mozzarella a fette + Mortadella a fette + Provolone a fette | Bianca | **NO — nome dinamico** |
| Mortadella e Gorgonzola | Mozzarella a fette + Mortadella a fette + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |
| Pancetta e Provolone | Mozzarella a fette + Pancetta a fette + Provolone a fette | Bianca | **NO — nome dinamico** |
| Guanciale e Pecorino (stile Gricia) | Guanciale a fette + Pecorino Romano grattugiato + Mozzarella a fette | Ispirata al condimento romano; manca il pepe come topping | **NO — nome dinamico** |
| Guanciale e Pecorino rossa (stile Amatriciana) | Salsa di pomodoro + Guanciale a fette + Pecorino Romano grattugiato | Ispirata all'amatriciana, versione per item disponibili | **SÌ — nome fisso** |
| Carbonara ispirata | Mozzarella a fette + Guanciale a fette + Uovo fritto + Pecorino Romano grattugiato | Uovo fritto al posto della crema d'uovo: NON carbonara classica | **NO — nome dinamico** |
| Speck e Gorgonzola | Mozzarella a fette + Speck a fette + Gorgonzola a spicchi | Bianca | **SÌ — nome fisso** |
| Speck e Scamorza affumicata | Mozzarella a fette + Speck a fette + Scamorza affumicata a fette | Bianca, gusto affumicato | **NO — nome dinamico** |
| Bresaola e Ricotta | Mozzarella a fette + Bresaola a fette + Ricotta + Basilico | Bianca con basilico | **NO — nome dinamico** |
| Coppa e Provolone | Salsa di pomodoro + Mozzarella a fette + Coppa a fette + Provolone a fette | Rossa | **NO — nome dinamico** |
| Bresaola e Gorgonzola | Mozzarella a fette + Bresaola a fette + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |
| Bianca dei Salumi | Mozzarella a fette + Prosciutto crudo a fette + Salame a fette + Mortadella a fette + Speck a fette | Quattro salumi e mozzarella: 5 tipi | **NO — nome dinamico** |

### Carne, pesce e uova

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Bacon e Uovo | Salsa di pomodoro + Mozzarella a fette + Bacon (crudo/cotto) + Uovo fritto | Uovo solo fritto; bacon crudo/cotto stesso tipo | **NO — nome dinamico** |
| Pollo e Scamorza | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Scamorza a fette | Bianca | **NO — nome dinamico** |
| Pollo e Ricotta | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Ricotta | Bianca | **NO — nome dinamico** |
| Pollo affumicato (gusto) | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Scamorza affumicata a fette | Scamorza affumicata; NON implica pollo affumicato come item | **NO — nome dinamico** |
| Salmone e Ricotta | Mozzarella a fette + Salmone a fette (crudo/cotto) + Ricotta + Basilico | Bianca con basilico | **SÌ — nome fisso** |
| Salmone e Scamorza affumicata | Mozzarella a fette + Salmone a fette (crudo/cotto) + Scamorza affumicata a fette | Bianca | **SÌ — nome fisso** |
| Merluzzo e Pomodoro | Salsa di pomodoro + Mozzarella a fette + Merluzzo a fette (crudo/cotto) + Pomodoro a fette | Rossa | **NO — nome dinamico** |
| Merluzzo e Provolone | Mozzarella a fette + Merluzzo a fette (crudo/cotto) + Provolone a fette | Bianca | **NO — nome dinamico** |
| Carne macinata e Mozzarella | Salsa di pomodoro + Mozzarella a fette + Carne macinata | Carne macinata ufficialmente ammessa | **SÌ — nome fisso** |
| Carne macinata e Gorgonzola | Mozzarella a fette + Carne macinata + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |

### Verdure e stagionali

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Zucca e Gorgonzola | Mozzarella a fette + Zucca a fette + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |
| Zucca e Speck | Mozzarella a fette + Zucca a fette + Speck a fette | Bianca | **SÌ — nome fisso** |
| Zucca e Provolone | Mozzarella a fette + Zucca a fette + Provolone a fette | Bianca | **NO — nome dinamico** |
| Autunnale | Mozzarella a fette + Zucca a fette + Speck a fette + Gorgonzola a spicchi | Zucca, speck e gorgonzola | **NO — nome dinamico** |
| Cavolo e Ricotta | Mozzarella a fette + Foglia di cavolo + Ricotta | Bianca | **NO — nome dinamico** |
| Orto disponibile | Mozzarella a fette + Pomodoro a fette + Foglia di cavolo + Zucca a fette + Basilico | Versione con vegetali gia' disponibili, NON l'Ortolana tradizionale | **SÌ — nome fisso** |

## Gruppo B — 15 combinazioni dopo le nuove fette (1 SÌ / 14 NO)

| Pizza proposta | Condimenti (tipi approvati) | Nota | Riconoscimento 1.0.0 |
| --- | --- | --- | --- |
| Patate e Speck | Mozzarella a fette + Patate a fette (NUOVO) + Speck a fette | Bianca | **SÌ — nome fisso** *(dopo nuovo item)* |
| Patate e Pancetta | Mozzarella a fette + Patate a fette (NUOVO) + Pancetta a fette | Bianca | **NO — nome dinamico** |
| Patate e Guanciale | Mozzarella a fette + Patate a fette (NUOVO) + Guanciale a fette + Pecorino Romano grattugiato | Con Pecorino | **NO — nome dinamico** |
| Patate e Bacon | Mozzarella a fette + Patate a fette (NUOVO) + Bacon (crudo/cotto) | Bianca | **NO — nome dinamico** |
| Patate e Scamorza | Mozzarella a fette + Patate a fette (NUOVO) + Scamorza a fette | Bianca | **NO — nome dinamico** |
| Patate e Gorgonzola | Mozzarella a fette + Patate a fette (NUOVO) + Gorgonzola a spicchi | Bianca | **NO — nome dinamico** |
| Patate e Ricotta | Mozzarella a fette + Patate a fette (NUOVO) + Ricotta | Bianca | **NO — nome dinamico** |
| Patate e Salmone | Mozzarella a fette + Patate a fette (NUOVO) + Salmone a fette (crudo/cotto) + Ricotta | Bianca | **NO — nome dinamico** |
| Patate, Guanciale e Uovo | Mozzarella a fette + Patate a fette (NUOVO) + Guanciale a fette + Uovo fritto + Pecorino Romano grattugiato | Stile Carbonara con patate | **NO — nome dinamico** |
| Carote e Ricotta | Mozzarella a fette + Carote a fette (NUOVO) + Ricotta | Bianca | **NO — nome dinamico** |
| Carote e Zucca | Mozzarella a fette + Carote a fette (NUOVO) + Zucca a fette | Bianca | **NO — nome dinamico** |
| Carote e Pecorino | Mozzarella a fette + Carote a fette (NUOVO) + Pecorino Romano grattugiato + Basilico | Bianca con basilico | **NO — nome dinamico** |
| Carote e Pollo | Mozzarella a fette + Carote a fette (NUOVO) + Pollo a pezzi (crudo/cotto) + Scamorza a fette | Con scamorza | **NO — nome dinamico** |
| Carote e Patate | Mozzarella a fette + Carote a fette (NUOVO) + Patate a fette (NUOVO) + Provolone a fette | Con provolone | **NO — nome dinamico** |
| Orto di autunno | Mozzarella a fette + Carote a fette (NUOVO) + Patate a fette (NUOVO) + Zucca a fette + Foglia di cavolo + Pomodoro a fette | 6 topping per quarto: limite rispettato | **NO — nome dinamico** |

## Gruppo C — 36 pizze con ingredienti per eventuali update futuri (PROPOSTE)

**Questi ingredienti sono attualmente ESCLUSI**: nessun nome autorizza l'aggiunta di nuovi tipi alla versione 1.0.0. Gli ingredienti descritti nella colonna centrale sono **combinazioni future da valutare**, non stringhe registry verificate.

| Pizza proposta | Combinazione indicativa | Nuovi condimenti / lavorazioni necessarie | Nota |
| --- | --- | --- | --- |
| Marinara | Salsa di pomodoro, aglio tagliato, origano, olio EVO | Aglio in fette; origano; olio EVO | Classica; olio non attualmente ammesso come topping |
| Diavola | Salsa di pomodoro, mozzarella, salame piccante | Salame piccante affettato | NON equiparare al salame normale della whitelist |
| Capricciosa | Salsa di pomodoro, mozzarella, prosciutto cotto affettato, funghi, carciofi, olive | Prosciutto cotto a fette; funghi idonei; carciofi; olive | 6 tipi sul quarto, non oltre |
| Quattro Stagioni | Salsa di pomodoro, mozzarella, prosciutto cotto, funghi, carciofi, olive suddivisi sui quattro quarti | Prosciutto cotto a fette; funghi; carciofi; olive | Richiede matching strutturato per quarto, oltre agli item |
| Prosciutto e Funghi | Salsa di pomodoro, mozzarella, prosciutto cotto affettato, funghi | Prosciutto cotto a fette; funghi | Non sostituire automaticamente con prosciutto crudo |
| Napoli / Napoletana con acciughe | Salsa di pomodoro, mozzarella, acciughe, origano | Acciughe; origano | Nome ambiguo fra tradizioni locali: definire esattamente |
| Romana | Salsa di pomodoro, mozzarella, acciughe, capperi, origano | Acciughe; capperi; origano | Interpretazione comune, varianti regionali |
| Siciliana | Salsa di pomodoro, mozzarella, acciughe, olive, capperi | Acciughe; olive; capperi | Nome/ingredienti variabili per pizzeria |
| Ortolana tradizionale | Salsa di pomodoro, mozzarella, zucchine, melanzane, peperoni | Zucchine a fette; melanzane a fette; peperoni tagliati | Le verdure intere non entrano mai direttamente |
| Vegetariana mista | Salsa di pomodoro, mozzarella, zucchine, peperoni, funghi | Zucchine; peperoni; funghi | Da non confondere con l'attuale Orto disponibile |
| Parmigiana | Salsa di pomodoro, mozzarella, melanzane a fette, Parmigiano grattugiato, basilico | Melanzane a fette | Il Parmigiano grattugiato esiste gia' |
| Alla Norma | Salsa di pomodoro, melanzane, ricotta salata, basilico | Melanzane; ricotta salata grattugiata/porzionata | La ricotta attuale NON sostituisce la ricotta salata |
| Tonno e Cipolla | Salsa di pomodoro, mozzarella, tonno sminuzzato, cipolla affettata | Tonno lavorato; cipolla a fette | Cipolla intera FDR resta esclusa |
| Boscaiola | Mozzarella, funghi, salsiccia sbriciolata | Funghi; salsiccia sbriciolata | Bianca in questa variante |
| Salsiccia e Friarielli | Mozzarella, salsiccia sbriciolata, friarielli | Salsiccia sbriciolata; friarielli preparati | Bianca napoletana |
| Nerano ispirata | Mozzarella, zucchine a rondelle, provolone | Zucchine a rondelle | Varianti tradizionali con Provolone del Monaco |
| Scarola | Mozzarella, scarola preparata, olive, capperi, acciughe | Scarola; olive; capperi; acciughe | Pizza bianca, 5 ingredienti |
| Cacio e Pepe | Pecorino Romano grattugiato, pepe nero | Pepe nero come topping/condimento | Bianca; non richiede mozzarella |
| Fiori di Zucca e Alici | Mozzarella, fiori di zucca, acciughe | Fiori di zucca; acciughe | Bianca |
| Calabrese / 'Nduja | Salsa di pomodoro, mozzarella, 'nduja | 'Nduja | Piccante diverso da salame |
| Pesto e Pomodorini | Pesto di basilico, mozzarella, pomodoro a fette | Pesto come salsa alternativa | Pesto e salsa di pomodoro non si sovrappongono nello stesso quarto |
| Mortadella e Pistacchio | Mozzarella, mortadella a fette, pistacchio tritato o crema di pistacchio | Pistacchi lavorati / crema pistacchio | Se la crema e' salsa va definita; non sbloccare oggi |
| Bresaola, Rucola e Grana | Mozzarella, bresaola a fette, rucola, scaglie di formaggio stagionato | Rucola; scaglie di Grana/Parmigiano | Il grattugiato non e' la stessa forma di scaglie |
| Crudo, Rucola e Grana | Mozzarella, prosciutto crudo a fette, rucola, scaglie di Grana | Rucola; scaglie di formaggio | Bianca |
| Gorgonzola e Noci | Mozzarella, gorgonzola, noci tritate | Noci tritate | Bianca |
| Pere e Gorgonzola | Mozzarella, gorgonzola, pera a fettine | Pera a fettine | Bianca |
| Fichi e Prosciutto Crudo | Mozzarella, fichi a fette, prosciutto crudo a fette | Fichi a fette | Bianca |
| Radicchio, Speck e Gorgonzola | Mozzarella, radicchio tagliato, speck, gorgonzola | Radicchio tagliato | Bianca |
| Tartufata | Mozzarella, funghi, crema di tartufo | Funghi; crema di tartufo | Chiarire se la crema tartufo ha ruolo salsa |
| Gamberi e Zucchine | Mozzarella, gamberi puliti, zucchine tagliate | Gamberi puliti; zucchine tagliate | Bianca |
| Frutti di Mare | Salsa di pomodoro, cozze sgusciate, calamari tagliati, gamberi puliti, prezzemolo | Cozze; calamari; gamberi; prezzemolo | Niente pesci interi |
| Americana | Salsa di pomodoro, mozzarella, wurstel a fette, patatine fritte | Wurstel a fette; patatine fritte | Patate a fette crude NON equivalenti alle patatine |
| Hawaiana (internazionale) | Salsa di pomodoro, mozzarella, prosciutto cotto a fette, ananas a pezzi | Prosciutto cotto a fette; ananas a pezzi | Stile internazionale, non tradizionale italiano |
| BBQ Chicken (internazionale) | Salsa barbecue, mozzarella, pollo a pezzi, cipolla a fette | Salsa barbecue; cipolla a fette | BBQ e pomodoro sono due salse: vietata sovrapposizione |
| Norcina ispirata | Ricotta, prosciutto crudo a fette, tartufo nero | Tartufo nero preparato | Ricotta e prosciutto attuali disponibili |
| Caprese al Pesto | Pesto come salsa, mozzarella, pomodoro a fette, basilico | Pesto come salsa | Pesto non ammesso attualmente |

**Nota sulla Principessa:** è attestata una preparazione con pomodoro, mozzarella, prosciutto crudo e panna in [Pizzeria Da Zazzà, Bari](https://glovoapp.com/it/it/bari/stores/pizzeria-rosticceria-da-zazz); altre pizzerie usano invece stracciatella o altri condimenti. Il nome e la ricetta non sono universali. Per la Pizza Update 1.0.0 **l'autore ha APPROVATO** questa versione realizzabile con il catalogo corrente: Principessa è uno dei 19 nomi fissi, riconoscibile soltanto quando tutti i quarti corrispondono esattamente alla ricetta.

## Pizze a quarti distinti: proposta di naming spaziale

La divisione fisica/logica del mattarello permette di creare una pizza con **quattro quarti differenti**. I condimenti uniformi devono essere registrati su tutti i quarti secondo le regole correnti; condimenti speciali devono essere applicati solo sul quarto bersaglio. Proposte **non ancora approvate**:

- **Quattro Gusti Italian's Delight (realizzabile con ingredienti esistenti):** base uniforme di salsa di pomodoro + mozzarella; quarto A prosciutto crudo a fette, quarto B salame a fette, quarto C gorgonzola a spicchi, quarto D zucca a fette. Ogni quarto ha **3 ingredienti** (salsa, mozzarella, aggiunta). Nome riconosciuto solo se **tutti e quattro i quarti** mostrano esattamente quella distribuzione, indipendentemente dalla rotazione se il matching lo consentirà. 
- **Quattro Formaggi a quarti (realizzabile con ingredienti esistenti):** quattro quarti separati, ciascuno condito con **uno** dei quattro formaggi distinti gorgonzola, provolone, Parmigiano grattugiato e Pecorino grattugiato, senza mozzarella uniforme. Il nome **non** si confonde con Quattro Formaggi uniforme: gli stessi quattro tipi di formaggio sono distribuiti su quattro quarti distinti.
- **Quattro Stagioni tradizionale (futura):** salsa di pomodoro e mozzarella comuni, quattro quarti rispettivamente con prosciutto cotto affettato, funghi, carciofi e olive: servono prima quattro tipi nuovi di topping.
- **Nomi dinamici per pizze miste:** se la pizza non corrisponde a un pattern di quarti noto, usare un nome generico o dinamico coerente con i singoli quarti. **Il formato definitivo dei nomi e il matching delle rotazioni devono ancora essere approvati**, non forzare un nome tradizionale per la semplice presenza globale di ingredienti.

## Meccanismo di matching — regola su 4 quarti CONFERMATA, dettagli tecnici da implementare

- Definizione JSON/datapack di ogni gusto, con ID nome localizzati `it_it` / `en_us`, condizioni di ingredienti canonici e priorità esplicita; nessuna dipendenza dai nomi visualizzati degli item.
- **Pattern uniforme APPROVATO:** ciascuno dei quattro quarti fisici deve avere **esattamente l'insieme canonico della ricetta SÌ**, anche senza divisioni del mattarello; un solo quarto diverso produce il nome dinamico. Crudo/cotto ed equivalenze cross-mod mappate sono identità canoniche comuni.
- **Pattern a quarti differenti NON approvati per la 1.0.0:** eventuale riconoscimento con rotazioni o composizioni differenti richiede approvazione futura; nella 1.0.0 le pizze miste hanno sempre il nome dinamico.
- **Solo le 19 combinazioni SÌ** ricevono nomi fissi; nessuna delle 52 NO può ottenere il nome di una ricetta simile per matching parziale. Confrontare gli ingredienti canonici in modo esatto e deterministico, evitando collisioni fra Margherita e Margherita con formaggi extra. Le varianti non devono essere autorizzate implicitamente.
- **Più di 6 condimenti per quarto:** sempre vietato anche se una proposta futura li elencasse per errore. I condimenti delle quattro parti non vengono sommati ai fini della capienza dei singoli quarti.
- Non modificare tabelle nutrizionali: valori e massimali 6/6 sono fissati in [GAMEPLAY.md](GAMEPLAY.md). Il nome di una pizza **non** conferisce alcun effetto alimentare aggiuntivo.

## Fonti culinarie di riferimento (non autorizzano automaticamente ingredienti)

- [Associazione Verace Pizza Napoletana — disciplinare/condimenti](https://www.pizzanapoletana.org/it/ricetta_pizza_napoletana): differenza fra Margherita/Marinara, uso dell'olio.
- [GialloZafferano — pizza Capricciosa](https://ricette.giallozafferano.it/Pizza-capricciosa.html): funghi, carciofi, olive, prosciutto cotto, pomodoro, mozzarella.
- [La Cucina Italiana — Amatriciana](https://www.lacucinaitaliana.it/ricetta/primi/bucatini-all-amatriciana-ricetta-originale/): guanciale, pomodoro, Pecorino per la variante pizza ispirata.
- [Ooni — Pizza Carbonara](https://eu.ooni.com/it-it/blogs/recipes/la-pizza-carbonara-it-recipe): versioni gourmet prevedono uovo lavorato, a differenza dell'uovo fritto della whitelist attuale.
- [GialloZafferano — Patate e Speck](https://blog.giallozafferano.it/vickyart/ricetta-pizza-con-patate-e-speck-sofficissima/): abbinamento di patate, speck e mozzarella.
- [Raccolta di pizze e abbinamenti](https://blog.giallozafferano.it/ricettedilibellula/pizze-ricette/): spunti per future pizze con zucchine, frutti di mare, pistacchio, noci e altri ingredienti.

## Aspetti tecnici di naming ancora da definire

1. **DECISO (13 ottobre 2026):** delle **56 + 15 = 71 combinazioni per la 1.0.0**, **19 hanno il nome fisso** e **52 mantengono il nome dinamico**; vedi lista vincolante sopra. Non chiedere una nuova selezione: restano solo da implementare il comportamento, le definizioni e le localizzazioni.
2. Quali delle **36 future** diventano obiettivi concreti di nuove release? Ogni ingrediente nuovo richiede **separata approvazione del topping**, nuove risorse e possibile rebalancing, senza cambiare la whitelist della 1.0.0 retroattivamente.
3. Scelta tra **matching esatto, sottoinsiemi, varianti ammesse**, oltre alla distinzione per quarto e regole di priorità; conservare comunque le meccaniche già concordate per l'anti-duplicato, salse, sei topping/porzione e rimborso dell'item originale.
4. Localizzazioni dei nomi e comportamento delle pizze miste/ruotate.

**Ambito di questo aggiornamento:** solo documentazione `docs/pizza-1.0.0/NAMED_PIZZAS.md` e `docs/pizza-1.0.0/ROADMAP.md`. Meccaniche, item, texture, datapack e codice Java restano inalterati.
