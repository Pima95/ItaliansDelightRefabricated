# Pizze riconoscibili — ampio catalogo di proposte 1.0.0 e aggiornamenti futuri

> **Inventario proposto il 13 ottobre 2026, NON ancora approvato dal creatore come lista di gusti con nomi riconosciuti.** Sono **approvati** l'insieme dei **31 tipi di topping** (29 già registrati + due nuove fette di carota/patata da creare) e il principio dei nomi riconosciuti; **non** i singoli nomi/abbinamenti di questa pagina. **Nessun codice, ricetta o datapack di riconoscimento è implementato in questa fase.**

## Numeri e stati

- **Gruppo A — 55 proposte con tipi già registrati:** gli item condimento esistono in Italian's Delight o Farmer's Delight Refabricated nella versione target; **non** significa che il sistema pizza completo sia già implementato o che il gusto sia già approvato.
- **Gruppo B — 15 proposte per la release 1.0.0 dopo la creazione delle due nuove fette:** usano carote e/o patate a fette già approvate come concetti, **ma non ancora registrate**; ricette Cutting Board di rendimento **1 vegetale -> 4 fette** già decise.
- **Gruppo C — 36 proposte per aggiornamenti futuri:** i nuovi topping NON appartengono alla whitelist della 1.0.0; **tutti richiedono approvazione esplicita futura** e, quando pertinenti, item/texture/modelli/tag/ricette. Alcune hanno ingredienti extra anche in gastronomia reale, qui riportati come schema concettuale.

**Totale:** **106 pizze proposte**, più le combinazioni libere con nome dinamico. Le ricette classiche riportate come *ispirate/adattate* NON vanno spacciate per ricette tradizionali autentiche o conformi a disciplinari.

## Vincoli e criteri comuni di design

1. **Ogni singolo ingrediente nel gruppo A usa soltanto gli item della whitelist già registrati** di [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Gruppo B usa soltanto gli stessi item e le due future fette di carota/patata già approvate. **Nessun item non autorizzato entra nella 1.0.0 attraverso un nome di pizza**.
2. Tutte le combinazioni uniformi elencate nei gruppi A/B contengono **da 0 a 6 ingredienti distinti per quarto** (la salsa è un ingrediente). La salsa è opzionale e massimo una per quarto fisico. **Panna e ricotta NON sono salse**, quindi possono coesistere con il pomodoro e tra loro. La loro categoria nutrizionale è Latticini e contribuisce una sola volta per fetta; analogamente gli altri gruppi nutrizionali.
3. Le coppie crude/cotte bacon, pollo a pezzi, merluzzo a fette e salmone a fette sono **la stessa identità canonica per coppia**. I sostituti di altre mod riconosciuti come equivalenti tramite tag `c:` **specifici/verificati** devono soddisfare esattamente gli stessi gusti; un tag generico non cambia identità o whitelist.
4. Una **combinazione esatta** di topping per pizza **uniformemente condita** può ottenere un nome fisso; con topping ulteriori, il nome diventa dinamico a meno di definizione più specifica approvata. L'ordine di posa è irrilevante. **La gestione delle pizze con quarti diversi è distinta** e richiede un possibile algoritmo di riconoscimento spaziale, non la sola somma globale dei topping. Una pizza con più gusti separati sui quarti non deve ricevere per errore il nome di una pizza uniforme.
5. Sulla **Margherita di gioco** si usano salsa di pomodoro, mozzarella e basilico; la variante napoletana disciplinata include anche olio e possibili dettagli di formaggio: **non rivendicare conformità AVPN** finché manca olio nella whitelist. **Marinara tradizionale non ottenibile oggi**: necessita almeno aglio, origano e olio EVO. **Diavola** significa **salame piccante**; salame ordinario non basta. **Capricciosa e Quattro Stagioni** necessitano per una proposta fedele funghi, carciofi, olive, prosciutto cotto porzionato, assenti dalla whitelist.
6. Alcuni gusti prendono **ispirazione da altri piatti** (Gricia, Amatriciana, Carbonara). Non sono la ricetta del piatto e non conferiscono effetti bonus oltre fame/saturazione già concordati. Per un'eventuale Carbonara autentica sulla pizza servirebbe una preparazione specifica dell'uovo, poiché la 1.0.0 ammette solo `farmersdelight:fried_egg`, **mai uovo intero**.
7. Per tutte le ricette B, **carote e patate intere non sono condimenti**: servono le fette degli appositi nuovi item, crude al momento della posa e cotte con la pizza, senza secondo item cotto.
8. **Ogni proposta è modificabile/scartabile prima dell'approvazione definitiva.** Nessuna tabella crea automaticamente nuovi gusti in gioco.

## Gruppo A — 55 pizze componibili con item già registrati (PROPOSTE)

### Basi e pizze rosse

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Bianca | Nessun condimento | Base senza condimenti; pizza bianca semplice |
| Rossa | Salsa di pomodoro | Solo salsa di pomodoro |
| Margherita (adattata alla 1.0.0) | Salsa di pomodoro + Mozzarella a fette + Basilico | Versione giocabile senza olio come topping; NON equivale alla certificazione AVPN |
| Margherita al Parmigiano | Salsa di pomodoro + Mozzarella a fette + Basilico + Parmigiano Reggiano grattugiato | Variante della Margherita con formaggio grattugiato |
| Margherita al Pecorino | Salsa di pomodoro + Mozzarella a fette + Basilico + Pecorino Romano grattugiato | Variante con Pecorino grattugiato |
| Margherita cremosa | Salsa di pomodoro + Mozzarella a fette + Basilico + Ricotta | Versione creativa con ricotta |
| Caprese rossa | Salsa di pomodoro + Mozzarella a fette + Pomodoro a fette + Basilico | Interpretazione della caprese con salsa e pomodoro a fette |
| Caprese bianca | Mozzarella a fette + Pomodoro a fette + Basilico | Mozzarella, pomodoro fresco a fette e basilico |
| Pomodoro e Bocconcino | Salsa di pomodoro + Bocconcino + Basilico | Con basilico |
| Pomodoro e Gorgonzola | Salsa di pomodoro + Mozzarella a fette + Gorgonzola a spicchi | Rossa al gorgonzola |

### Formaggi, creme e affumicati

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Quattro Formaggi bianca | Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | Quattro formaggi distinti esatti |
| Quattro Formaggi rossa | Salsa di pomodoro + Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette + Parmigiano Reggiano grattugiato | Quattro formaggi + salsa, tutti entro 5 tipi |
| Quattro Formaggi affumicata | Mozzarella a fette + Gorgonzola a spicchi + Scamorza affumicata a fette + Pecorino Romano grattugiato | Versione originale con scamorza affumicata |
| Tre Formaggi bianca | Mozzarella a fette + Gorgonzola a spicchi + Provolone a fette | Tre formaggi distinti |
| Provolone e Pecorino | Mozzarella a fette + Provolone a fette + Pecorino Romano grattugiato | Bianca |
| Scamorza e Parmigiano | Mozzarella a fette + Scamorza a fette + Parmigiano Reggiano grattugiato | Bianca |
| Scamorza affumicata e Gorgonzola | Mozzarella a fette + Scamorza affumicata a fette + Gorgonzola a spicchi | Bianca |
| Ricotta e Basilico | Mozzarella a fette + Ricotta + Basilico | Bianca |
| Panna e Ricotta | Mozzarella a fette + Panna + Ricotta | Le creme sono topping, non salse |
| Gorgonzola e Panna | Mozzarella a fette + Gorgonzola a spicchi + Panna | Cremosa |
| Bocconcini e Ricotta | Bocconcino + Ricotta + Basilico | Bianca con basilico |

### Salumi e sapori italiani

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Prosciutto crudo | Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette | Versione rossa, senza prosciutto cotto intero |
| Prosciutto crudo e Ricotta | Salsa di pomodoro + Mozzarella a fette + Prosciutto crudo a fette + Ricotta | Quattro topping |
| Salame | Salsa di pomodoro + Mozzarella a fette + Salame a fette | Salame normale, NON Diavola |
| Salame e Provolone | Salsa di pomodoro + Mozzarella a fette + Salame a fette + Provolone a fette | Rossa |
| Mortadella bianca | Mozzarella a fette + Mortadella a fette | Semplice |
| Mortadella e Ricotta | Mozzarella a fette + Mortadella a fette + Ricotta | Bianca cremosa |
| Mortadella e Provolone | Mozzarella a fette + Mortadella a fette + Provolone a fette | Bianca |
| Mortadella e Gorgonzola | Mozzarella a fette + Mortadella a fette + Gorgonzola a spicchi | Bianca |
| Pancetta e Provolone | Mozzarella a fette + Pancetta a fette + Provolone a fette | Bianca |
| Guanciale e Pecorino (stile Gricia) | Guanciale a fette + Pecorino Romano grattugiato + Mozzarella a fette | Ispirata al condimento romano; manca il pepe come topping |
| Guanciale e Pecorino rossa (stile Amatriciana) | Salsa di pomodoro + Guanciale a fette + Pecorino Romano grattugiato | Ispirata all'amatriciana, versione per item disponibili |
| Carbonara ispirata | Mozzarella a fette + Guanciale a fette + Uovo fritto + Pecorino Romano grattugiato | Uovo fritto al posto della crema d'uovo: NON carbonara classica |
| Speck e Gorgonzola | Mozzarella a fette + Speck a fette + Gorgonzola a spicchi | Bianca |
| Speck e Scamorza affumicata | Mozzarella a fette + Speck a fette + Scamorza affumicata a fette | Bianca, gusto affumicato |
| Bresaola e Ricotta | Mozzarella a fette + Bresaola a fette + Ricotta + Basilico | Bianca con basilico |
| Coppa e Provolone | Salsa di pomodoro + Mozzarella a fette + Coppa a fette + Provolone a fette | Rossa |
| Bresaola e Gorgonzola | Mozzarella a fette + Bresaola a fette + Gorgonzola a spicchi | Bianca |
| Bianca dei Salumi | Mozzarella a fette + Prosciutto crudo a fette + Salame a fette + Mortadella a fette + Speck a fette | Quattro salumi e mozzarella: 5 tipi |

### Carne, pesce e uova

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Bacon e Uovo | Salsa di pomodoro + Mozzarella a fette + Bacon (crudo/cotto) + Uovo fritto | Uovo solo fritto; bacon crudo/cotto stesso tipo |
| Pollo e Scamorza | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Scamorza a fette | Bianca |
| Pollo e Ricotta | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Ricotta | Bianca |
| Pollo affumicato (gusto) | Mozzarella a fette + Pollo a pezzi (crudo/cotto) + Scamorza affumicata a fette | Scamorza affumicata; NON implica pollo affumicato come item |
| Salmone e Ricotta | Mozzarella a fette + Salmone a fette (crudo/cotto) + Ricotta + Basilico | Bianca con basilico |
| Salmone e Scamorza affumicata | Mozzarella a fette + Salmone a fette (crudo/cotto) + Scamorza affumicata a fette | Bianca |
| Merluzzo e Pomodoro | Salsa di pomodoro + Mozzarella a fette + Merluzzo a fette (crudo/cotto) + Pomodoro a fette | Rossa |
| Merluzzo e Provolone | Mozzarella a fette + Merluzzo a fette (crudo/cotto) + Provolone a fette | Bianca |
| Carne macinata e Mozzarella | Salsa di pomodoro + Mozzarella a fette + Carne macinata | Carne macinata ufficialmente ammessa |
| Carne macinata e Gorgonzola | Mozzarella a fette + Carne macinata + Gorgonzola a spicchi | Bianca |

### Verdure e stagionali

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Zucca e Gorgonzola | Mozzarella a fette + Zucca a fette + Gorgonzola a spicchi | Bianca |
| Zucca e Speck | Mozzarella a fette + Zucca a fette + Speck a fette | Bianca |
| Zucca e Provolone | Mozzarella a fette + Zucca a fette + Provolone a fette | Bianca |
| Autunnale | Mozzarella a fette + Zucca a fette + Speck a fette + Gorgonzola a spicchi | Zucca, speck e gorgonzola |
| Cavolo e Ricotta | Mozzarella a fette + Foglia di cavolo + Ricotta | Bianca |
| Orto disponibile | Mozzarella a fette + Pomodoro a fette + Foglia di cavolo + Zucca a fette + Basilico | Versione con vegetali gia' disponibili, NON l'Ortolana tradizionale |

## Gruppo B — 15 pizze 1.0.0 dopo i nuovi item carota/patata a fette (PROPOSTE)

| Pizza proposta | Condimenti (tipi approvati) | Nota |
| --- | --- | --- |
| Patate e Speck | Mozzarella a fette + Patate a fette (NUOVO) + Speck a fette | Bianca |
| Patate e Pancetta | Mozzarella a fette + Patate a fette (NUOVO) + Pancetta a fette | Bianca |
| Patate e Guanciale | Mozzarella a fette + Patate a fette (NUOVO) + Guanciale a fette + Pecorino Romano grattugiato | Con Pecorino |
| Patate e Bacon | Mozzarella a fette + Patate a fette (NUOVO) + Bacon (crudo/cotto) | Bianca |
| Patate e Scamorza | Mozzarella a fette + Patate a fette (NUOVO) + Scamorza a fette | Bianca |
| Patate e Gorgonzola | Mozzarella a fette + Patate a fette (NUOVO) + Gorgonzola a spicchi | Bianca |
| Patate e Ricotta | Mozzarella a fette + Patate a fette (NUOVO) + Ricotta | Bianca |
| Patate e Salmone | Mozzarella a fette + Patate a fette (NUOVO) + Salmone a fette (crudo/cotto) + Ricotta | Bianca |
| Patate, Guanciale e Uovo | Mozzarella a fette + Patate a fette (NUOVO) + Guanciale a fette + Uovo fritto + Pecorino Romano grattugiato | Stile Carbonara con patate |
| Carote e Ricotta | Mozzarella a fette + Carote a fette (NUOVO) + Ricotta | Bianca |
| Carote e Zucca | Mozzarella a fette + Carote a fette (NUOVO) + Zucca a fette | Bianca |
| Carote e Pecorino | Mozzarella a fette + Carote a fette (NUOVO) + Pecorino Romano grattugiato + Basilico | Bianca con basilico |
| Carote e Pollo | Mozzarella a fette + Carote a fette (NUOVO) + Pollo a pezzi (crudo/cotto) + Scamorza a fette | Con scamorza |
| Carote e Patate | Mozzarella a fette + Carote a fette (NUOVO) + Patate a fette (NUOVO) + Provolone a fette | Con provolone |
| Orto di autunno | Mozzarella a fette + Carote a fette (NUOVO) + Patate a fette (NUOVO) + Zucca a fette + Foglia di cavolo + Pomodoro a fette | 6 topping per quarto: limite rispettato |

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

## Pizze a quarti distinti: proposta di naming spaziale

La divisione fisica/logica del mattarello permette di creare una pizza con **quattro quarti differenti**. I condimenti uniformi devono essere registrati su tutti i quarti secondo le regole correnti; condimenti speciali devono essere applicati solo sul quarto bersaglio. Proposte **non ancora approvate**:

- **Quattro Gusti Italian's Delight (realizzabile con ingredienti esistenti):** base uniforme di salsa di pomodoro + mozzarella; quarto A prosciutto crudo a fette, quarto B salame a fette, quarto C gorgonzola a spicchi, quarto D zucca a fette. Ogni quarto ha **3 ingredienti** (salsa, mozzarella, aggiunta). Nome riconosciuto solo se **tutti e quattro i quarti** mostrano esattamente quella distribuzione, indipendentemente dalla rotazione se il matching lo consentirà. 
- **Quattro Formaggi a quarti (realizzabile con ingredienti esistenti):** mozzarella uniforme, quattro quarti con aggiunte ciascuna di un formaggio diverso fra gorgonzola, provolone, Parmigiano grattugiato e Pecorino grattugiato. Il nome **non** si confonde con Quattro Formaggi uniforme: qui i quattro formaggi aggiuntivi sono spazialmente distinti (con mozzarella i tipi globali sarebbero 5).
- **Quattro Stagioni tradizionale (futura):** salsa di pomodoro e mozzarella comuni, quattro quarti rispettivamente con prosciutto cotto affettato, funghi, carciofi e olive: servono prima quattro tipi nuovi di topping.
- **Nomi dinamici per pizze miste:** se la pizza non corrisponde a un pattern di quarti noto, usare un nome generico o dinamico coerente con i singoli quarti. **Il formato definitivo dei nomi e il matching delle rotazioni devono ancora essere approvati**, non forzare un nome tradizionale per la semplice presenza globale di ingredienti.

## Meccanismo di matching consigliato (NON approvato come algoritmo finale)

- Definizione JSON/datapack di ogni gusto, con ID nome localizzati `it_it` / `en_us`, condizioni di ingredienti canonici e priorità esplicita; nessuna dipendenza dai nomi visualizzati degli item.
- **Pattern uniforme:** confronto insiemistico esatto di ingredienti canonici presenti su ciascuno dei quattro quarti (ignorare ordine di applicazione, usare l'identità condivisa per versioni crude/cotte e mod compatibili). Evita falsi positivi su pizze miste.
- **Pattern per quarto:** confrontare i quattro insiemi di ingredienti per posizione fisica, con una policy di rotazione/reflection futura da decidere. Mai riconoscere come Quattro Stagioni una pizza con tutti i condimenti distribuiti uniformemente.
- **Definizioni più specifiche > più generiche**, con tie-break deterministico; evitare collisioni fra per esempio Margherita, Margherita al Parmigiano e Margherita cremosa. Le definizioni possono consentire varianti solo se esplicitamente configurate.
- **Più di 6 condimenti per quarto:** sempre vietato anche se una proposta futura li elencasse per errore. I condimenti delle quattro parti non vengono sommati ai fini della capienza dei singoli quarti.
- Non modificare tabelle nutrizionali: valori e massimali 6/6 sono fissati in [GAMEPLAY.md](GAMEPLAY.md). Il nome di una pizza **non** conferisce alcun effetto alimentare aggiuntivo.

## Fonti culinarie di riferimento (non autorizzano automaticamente ingredienti)

- [Associazione Verace Pizza Napoletana — disciplinare/condimenti](https://www.pizzanapoletana.org/it/ricetta_pizza_napoletana): differenza fra Margherita/Marinara, uso dell'olio.
- [GialloZafferano — pizza Capricciosa](https://ricette.giallozafferano.it/Pizza-capricciosa.html): funghi, carciofi, olive, prosciutto cotto, pomodoro, mozzarella.
- [La Cucina Italiana — Amatriciana](https://www.lacucinaitaliana.it/ricetta/primi/bucatini-all-amatriciana-ricetta-originale/): guanciale, pomodoro, Pecorino per la variante pizza ispirata.
- [Ooni — Pizza Carbonara](https://eu.ooni.com/it-it/blogs/recipes/la-pizza-carbonara-it-recipe): versioni gourmet prevedono uovo lavorato, a differenza dell'uovo fritto della whitelist attuale.
- [GialloZafferano — Patate e Speck](https://blog.giallozafferano.it/vickyart/ricetta-pizza-con-patate-e-speck-sofficissima/): abbinamento di patate, speck e mozzarella.
- [Raccolta di pizze e abbinamenti](https://blog.giallozafferano.it/ricettedilibellula/pizze-ricette/): spunti per future pizze con zucchine, frutti di mare, pistacchio, noci e altri ingredienti.

## Decisioni necessarie prima di implementare nomi fissi

1. Quali delle **55 + 15 proposte della release 1.0.0** devono essere riconosciute con nome dedicato? Non servono tutte: altre combinazioni restano valide come pizza personalizzata con nome dinamico.
2. Quali delle **36 future** diventano obiettivi concreti di nuove release? Ogni ingrediente nuovo richiede **separata approvazione del topping**, nuove risorse e possibile rebalancing, senza cambiare la whitelist della 1.0.0 retroattivamente.
3. Scelta tra **matching esatto, sottoinsiemi, varianti ammesse**, oltre alla distinzione per quarto e regole di priorità; conservare comunque le meccaniche già concordate per l'anti-duplicato, salse, sei topping/porzione e rimborso dell'item originale.
4. Localizzazioni dei nomi e comportamento delle pizze miste/ruotate.

**Ambito di questo aggiornamento:** solo documentazione `docs/pizza-1.0.0/NAMED_PIZZAS.md`. Le meccaniche, gli item, le texture, i datapack e il codice Java restano inalterati.
