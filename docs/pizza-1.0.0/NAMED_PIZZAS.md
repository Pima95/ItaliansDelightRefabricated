# Pizze riconoscibili — nomi approvati 1.0.0 e proposte future

> **AGGIORNAMENTO 20 ottobre 2026:** `potato_slice` è ora registrata, con ricetta Cutting Board e resa 1→4. **Patate e Speck è diventata la diciottesima pizza riconosciuta attiva**, con mozzarella a fette + patate a fette + speck a fette. «Principessa» resta sospesa per la regola della salsa esclusiva; non ne è stata inventata una sostituzione. Il menù Creativa e le nuove texture delle pizze riconosciute sono attività successive.

> **Decisione definitiva dell'autore del 13 ottobre 2026:** tra le 71 combinazioni per la release 1.0.0, **19 nomi fissi APPROVATI** (18 con item già registrati, 1 con patate a fette da creare) e **52 combinazioni a nome dinamico**. La whitelist rimane di 31 topping in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Le 36 proposte future sono ancora **NON APPROVATE**. **Nessun codice, texture, ricetta o datapack eseguibile è stato modificato**.

> **AGGIORNAMENTO DI GAMEPLAY — 15 ottobre 2026, prioritario sulle decisioni precedenti:** ogni quarto può contenere **una sola scelta fra salsa di pomodoro, panna e ricotta**. La ricetta «Principessa» approvata il 13 ottobre (pomodoro + panna) è ora **INCOMPATIBILE/SOSPESA**: non è più componibile come nuova pizza. La lista dei **19 nomi storicamente approvati** e i conteggi SÌ/NO rimangono documentati, ma «Principessa» **non va implementata come preset attivo** finché l'autore non decide gli ingredienti della variante compatibile. Non inventare automaticamente una ricetta sostitutiva e non eliminare i dati delle pizze già salvate. Le altre combinazioni prive di sovrapposizioni continuano a seguire le regole previste.


**Stato storico codice 17 ottobre 2026 (superato dalla nota del 20 ottobre):** il riconoscimento è stato aggiunto su `feature/pizza-1.0.0` per i **17 gusti attualmente componibili**, insieme a nomi dinamici, Pizza mista, singoli spicchi e tooltip IT/EN. **Da compilare/testare:** [PIZZA_NAMING_TESTS.md](PIZZA_NAMING_TESTS.md). La Principessa resta sospesa; il preset Patate e Speck verrà abilitato soltanto dopo l'item approvato. Le note sottostanti sulla mancanza di codice descrivono lo stato storico della selezione del 13 ottobre.

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
| Principessa | **RICETTA STORICA SOSPESA:** Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Panna | **Ingredienti registrati, ma combinazione non più realizzabile (pomodoro + panna); in attesa di nuova scelta** |
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

Dei 18 gusti con item già registrati e un nome fisso storicamente approvato, **17 sono attualmente preparabili secondo la nuova regola di una salsa per quarto**, mentre **Principessa** (pomodoro + panna) è sospesa in attesa di decisione. **Patate e Speck** rimane il diciannovesimo nome fisso, attivabile soltanto dopo la creazione delle patate a fette. Le decisioni precedenti sulla Principessa sono conservate sotto come documentazione storica, non come istruzione di implementazione. La Margherita 1.0.0 resta quella senza olio.

### Regola dei nomi fissi — 4/4 quarti APPROVATA

**MATCHING ESATTO SU QUATTRO QUARTI FISICI — CONFERMATO (13 ottobre 2026):** il nome fisso di una delle **19 ricette SÌ** si applica alla pizza integra **se e solo se ciascuno dei quattro quarti fisici contiene esattamente tutti e soli i tipi canonici di topping previsti dalla stessa ricetta**. Si controllano **quattro insiemi distinti, non la sola unione globale**. Basta **un ingrediente aggiuntivo o mancante in un solo quarto** perché l'intera pizza abbia **nome dinamico**, anche se gli altri tre quarti corrispondono. Il confronto vale anche senza divisioni visibili o dopo riunioni col mattarello; l'ordine di aggiunta è irrilevante, così come lo stato crudo/cotto per gli ingredienti equivalenti e le sostituzioni cross-mod esplicitamente mappate. **Bianca:** tutti e quattro i quarti senza condimenti. **Rossa:** sola salsa di pomodoro nei quattro quarti. **Margherita:** pomodoro, mozzarella a fette e basilico esatti in ciascun quarto. Un quarto con speck extra o senza basilico -> nome dinamico. Le pizze miste a quarti diversi **non** ricevono nomi fissi nella 1.0.0: eventuali Quattro Gusti / Quattro Stagioni riconosciute restano future proposte, **non approvate**. La regola disciplina la denominazione della **pizza integra**; il riconoscimento indipendente dei singoli spicchi cotti è già confermato nella sezione seguente.

## Nomi degli spicchi cotti — CONFERMATO (13 ottobre 2026)

**Il nome di ogni spicchio (item fetta cotta) si calcola INDIPENDENTEMENTE dal nome della pizza intera e dagli altri tre quarti.** Quando si preleva uno spicchio dalla pizza cotta con clic destro a mano vuota, oppure uno spicchio residuo viene ottenuto rompendo la pizza cotta già servita parzialmente, leggere **soltanto i condimenti del suo quarto fisico** e confrontarne l'insieme di **tipi canonici esatti** con ciascuna delle **19 ricette a nome fisso già approvate** in [NAMED_PIZZAS.md](NAMED_PIZZAS.md).

- **Match esatto di un singolo quarto con una delle 19 ricette SÌ:** l'item della fetta prende un nome riconosciuto, per esempio **`Spicchio di Margherita`** quando il suo quarto contiene esattamente `farmersdelight:tomato_sauce`, `italiansdelight:mozzarella_slice` e basilico. **Non occorre che la pizza intera fosse Margherita**, né che gli altri tre quarti fossero identici.
- **Nessun match (ricetta NO, extra o mancanza di un ingrediente, composizione libera):** il singolo spicchio usa un **nome dinamico basato sui soli suoi condimenti**, non un nome ereditato dalla pizza intera o da altri quarti. La grammatica `Spicchio con [ingredienti]` è approvata; rimangono solo da implementare l'abbreviazione automatica e le localizzazioni.
- **Pizza mista:** il nome della **pizza intera** è **Pizza mista**, in applicazione della regola 4/4 già confermata; **ogni spicchio** può comunque ricevere **il proprio nome fisso** se soddisfa da solo una ricetta approvata, oppure un nome dinamico se non la soddisfa. Quattro spicchi possono perciò avere quattro nomi distinti.
- **Identità canoniche:** l'ordine di applicazione è irrilevante; le versioni crudo/cotto e gli item equivalenti di altre mod, quando esplicitamente mappati, valgono come lo stesso tipo. Non usare la mera categoria nutrizionale per riconoscere un ingrediente e non ammettere item fuori catalogo.
- **Fetta di un quarto vuoto:** corrisponde alla ricetta SÌ della pizza Bianca e avrà un nome coerente con `Spicchio di pizza bianca` (testo/localizzazione da declinare senza rigidità). Il quarto con **sola salsa di pomodoro** corrisponde alla Rossa.
- **Compatibilità col servizio già approvato:** la fetta conserva unicamente i suoi topping, nutrienti, dati e stato, senza nuovi item registrati per ogni gusto; il nome è un **display name localizzato e derivato dai dati dello spicchio**. La pizza intera cotta **non è commestibile**. Le porzioni continuano a essere 4 e vengono prelevate dalla pizza cotta senza coltello. Inventario pieno o mano occupata non producono una fetta e non cambiano il blocco. I rimborsi/drops e la nutrizione restano invariati.
- **Limite del nuovo requisito:** il riconoscimento dei nomi **delle fette cotte è ora deciso**. Rimangono aperte le convenzioni di abbreviazione dei nomi dinamici molto lunghi e gli eventuali dettagli del nome del blocco pizza **rimasto nel mondo dopo il servizio parziale**, senza bloccare il naming del singolo item.

**Esempio aggiornato 15 ottobre:** pizza intera con quarto 1 Margherita, quarto 2 una combinazione panna + speck (senza pomodoro), quarto 3 Rossa, quarto 4 una combinazione personalizzata. La pizza intera è **Pizza mista**; i quarti riconosciuti ricevono nomi fissi, gli altri dinamici. **L'esempio precedente che includeva Principessa non è più realizzabile con i nuovi clic** e rimane soltanto come nota storica.

## Nomi e tooltip delle pizze — CONFERMATO (13 ottobre 2026)

La denominazione deriva **sempre dai quattro insiemi canonici dei quarti fisici**, non dalle divisioni visive fatte col mattarello, dall'ordine di posa dei condimenti o dalla lingua del creatore.

1. **Quattro quarti identici e match esatto con un preset SÌ:** nome ufficiale riconosciuto, ad esempio `Pizza Margherita` (come da lista dei 19 gusti; nessun riconoscimento per sottoinsieme).
2. **Quattro quarti identici ma senza match fra i 19 preset:** nome dinamico **`Pizza con [ingredienti]`**, ad esempio `Pizza con Mozzarella, Speck e Ricotta`. Se il testo sarebbe troppo lungo, **abbreviare soltanto il nome mostrato nell'inventario**, senza perdere la lista completa nel tooltip. Il criterio tecnico di abbreviazione e l'ordine di visualizzazione sono da definire nell'implementazione.
3. **Almeno un quarto diverso dagli altri:** nome **`Pizza mista`**, anche se uno o più quarti coincidono esattamente con ricette SÌ; nessun preset fisso per pizze miste nella 1.0.0. Tooltip suddiviso in **Quarto 1–4**, con il nome del gusto approvato quando il quarto corrisponde esattamente a un preset SÌ e, altrimenti, con l'elenco completo degli ingredienti di *quel singolo quarto*.
4. **Spicchi cotti:** il nome resta **indipendente dalla pizza intera**. Si applica il preset SÌ del singolo quarto (es. `Spicchio di Margherita`) oppure `Spicchio con [ingredienti]`, eventualmente abbreviato con tooltip completo. Il quarto senza condimenti usa la denominazione Bianca già approvata; quello con solo pomodoro la Rossa.

**Esempio di tooltip per `Pizza mista`:**

```text
Pizza mista
Quarto 1: Margherita
Quarto 2: combinazione con panna e speck (nome dinamico, nessun pomodoro)
Quarto 3: Rossa
Quarto 4: Mozzarella, Speck e Ricotta
```

Le righe indicano il contenuto di ciascun **quarto fisico**, anche se la pizza non è stata divisa col mattarello oppure è stata riunita. I nomi e le liste sono **localizzati** (`it_it`, `en_us`) a partire dagli ID canonici degli ingredienti, senza salvare stringhe tradotte nei data component. Un tooltip non altera la composizione, la nutrizione, la cottura o la restituzione degli ingredienti.

**Ancora aperti, solo tecnicamente:** soglia e resa dell'abbreviazione, ordinamento stabile delle descrizioni, assegnazione deterministica dei numeri Quarto 1–4 secondo l'orientamento della pizza, e nome visualizzato del blocco rimasto dopo prelievo parziale. Nessuno di questi punti cambia i nomi e la struttura del tooltip qui approvati.

### Altre 52 combinazioni: SOLO NOME DINAMICO

Le **52 righe con NO** riportate nelle tabelle sottostanti sono gusti che il giocatore può preparare (se gli item esistono), ma **non devono mai ricevere un preset di denominazione fissa**. I loro nomi nella tabella servono a identificare il gusto nel documento, non a tradursi automaticamente in un nome specifico in gioco. Anche le altre combinazioni libere non incluse fra i 19 SÌ conservano il nome dinamico. Non ereditare automaticamente il nome da una ricetta presente come sottoinsieme.

## Vincoli e criteri comuni di design

1. **Ogni singolo ingrediente nel gruppo A usa soltanto gli item della whitelist già registrati** di [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Gruppo B usa soltanto gli stessi item e le due future fette di carota/patata già approvate. **Nessun item non autorizzato entra nella 1.0.0 attraverso un nome di pizza**.
2. I record originali dei 71 gusti rappresentano una lista **storicamente approvata**. Con la decisione del 15 ottobre, le combinazioni che sovrappongono pomodoro/panna/ricotta **non sono più realizzabili come nuove pizze**, indipendentemente dall'etichetta SÌ/NO. Una sola delle tre scelte è ammessa per quarto fisico; combinazioni differenti su quarti distinti rimangono valide. Panna/ricotta mantengono categoria nutrizionale Latticini, non Salsa. Non si modificano in automatico le ricette originali.
3. Le coppie crude/cotte bacon, pollo a pezzi, merluzzo a fette e salmone a fette sono **la stessa identità canonica per coppia**. I sostituti di altre mod riconosciuti come equivalenti tramite tag `c:` **specifici/verificati** devono soddisfare esattamente gli stessi gusti; un tag generico non cambia identità o whitelist.
4. Una **combinazione esatta** di topping distribuita uniformemente sui quattro quarti può ottenere uno dei 19 nomi fissi, altrimenti il nome è **Pizza con [ingredienti]**. Per quarti non uniformi il nome approvato è **Pizza mista** con tooltip dettagliato per quarto; nessun preset spaziale misto è approvato nella 1.0.0. L'ordine di posa non influisce sul riconoscimento.
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
| Margherita cremosa | Salsa di pomodoro + Mozzarella a fette + Basilico + Ricotta | Versione creativa con ricotta | **NO storico — combinazione non più applicabile (pomodoro + ricotta)** |
| **Principessa** | **Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Panna** | **PROPOSTA per 1.0.0**: interpretazione attestata da pizzerie di Bari; tutti e quattro gli item già approvati. Panna = condimento cremoso NON salsa, quindi può coesistere con il pomodoro. Il nome «Principessa» ha anche versioni differenti in altre pizzerie: questa precisa combinazione è stata **APPROVATA dall'autore come Principessa**, pur non essendo una ricetta universalmente standardizzata. | **SÌ storico — PRESET SOSPESO, ricetta incompatibile con la regola 15/10** |
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

**Nota storica sulla Principessa:** era stata approvata una variante documentata con pomodoro, mozzarella, prosciutto crudo e panna. **La nuova regola del 15 ottobre ne impedisce la preparazione**, perché pomodoro e panna occupano lo stesso slot funzionale. Conservare questo record, ma sospendere il riconoscimento e la produzione di quella variante finché l'autore non ne approvi una sostituzione.

## Pizze a quarti distinti: proposta di naming spaziale

La divisione fisica/logica del mattarello permette di creare una pizza con **quattro quarti differenti**. I condimenti uniformi devono essere registrati su tutti i quarti secondo le regole correnti; condimenti speciali devono essere applicati solo sul quarto bersaglio. Proposte **non ancora approvate**:

- **Quattro Gusti Italian's Delight (realizzabile con ingredienti esistenti):** base uniforme di salsa di pomodoro + mozzarella; quarto A prosciutto crudo a fette, quarto B salame a fette, quarto C gorgonzola a spicchi, quarto D zucca a fette. Ogni quarto ha **3 ingredienti** (salsa, mozzarella, aggiunta). Nome riconosciuto solo se **tutti e quattro i quarti** mostrano esattamente quella distribuzione, indipendentemente dalla rotazione se il matching lo consentirà. 
- **Quattro Formaggi a quarti (realizzabile con ingredienti esistenti):** quattro quarti separati, ciascuno condito con **uno** dei quattro formaggi distinti gorgonzola, provolone, Parmigiano grattugiato e Pecorino grattugiato, senza mozzarella uniforme. Il nome **non** si confonde con Quattro Formaggi uniforme: gli stessi quattro tipi di formaggio sono distribuiti su quattro quarti distinti.
- **Quattro Stagioni tradizionale (futura):** salsa di pomodoro e mozzarella comuni, quattro quarti rispettivamente con prosciutto cotto affettato, funghi, carciofi e olive: servono prima quattro tipi nuovi di topping.
- **Nomi per pizze miste — CONFERMATO:** usare **Pizza mista** e il tooltip con Quarto 1–4, secondo la sezione *Nomi e tooltip delle pizze*. Queste proposte di gusti a quarti **non diventano preset fissi** nella 1.0.0; non forzare nomi tradizionali dal solo insieme globale di ingredienti.

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
3. **Matching esatto 4/4 già DECISO** per pizze intere, confronto esatto su un quarto per gli spicchi; da implementare con anti-duplicato, salse, sei topping/quarto e rimborso dell'item originale.
4. **Pizza mista e tooltip per quarto DECISI**, così come **Pizza con [ingredienti]** per quarti uguali non riconosciuti. Da implementare traduzioni, ordinamento, abbreviazione e indicizzazione stabile dei quarti quando la pizza è ruotata.

**Stato:** specifiche documentali; meccaniche, item, texture, datapack e codice Java del naming ancora da implementare.
