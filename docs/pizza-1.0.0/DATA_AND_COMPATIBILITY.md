# Dati delle pizze, nomi dinamici, datapack e compatibilità

> **Principio generale CONFERMATO:** mantenere i comportamenti ordinari di Minecraft **vanilla come default** per gli item e le entita' (drop, pickup, despawn, danni ambientali, ecc.), salvo le meccaniche personalizzate gia' concordate. Non aggiungere eccezioni senza una reale motivazione di gameplay.


> Le seguenti sono proposte di architettura: **non sono ancora implementate né API definitive**.

## Obiettivo tecnico

Per supportare gusti combinabili senza migliaia di item separati, usare un sistema di pochi item/stati (palla, pizza cruda, pizza cotta, fetta) con **data component** personalizzati. Le informazioni viaggiano tra blocco di preparazione, inventario, forni, pizza cotta e, se approvate, fette.

Identificatori iniziali possibili, **non definitivi**:
- `italiansdelight:pizza_dough_ball`;
- `italiansdelight:raw_pizza`;
- `italiansdelight:baked_pizza`;
- `italiansdelight:pizza_slice`.

## Composizione e persistenza

**Prima infrastruttura di persistenza aggiunta al codice (FASE 3A, da compilare/testare):** [PizzaComposition.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaComposition.java) descrive quattro quarti, orientamento/divisioni, seed e applicazioni con `Codec` formato 1. [PizzaToppingApplication.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingApplication.java) conserva ID cronologico, ID canonico, `ItemStack` originale difensivamente copiato e maschere originali/residue. [PizzaDoughBlockEntity.java](../../src/main/java/dev/italiansdelight/common/block/entity/PizzaDoughBlockEntity.java) salva e ripristina con `ValueInput.read`/`ValueOutput.store`. **Non** sono ancora implementati la sincronizzazione client, le mutazioni sui quarti, i progressi di cottura, il servizio delle fette e i trasferimenti item/pala/forno. [Test](PERSISTENT_COMPOSITION_TESTS.md).

Dati da conservare:
- versione del formato, per eventuali migrazioni;
- **division_state**: configurazione scelta col mattarello (intera, due meta', solo una meta' ulteriormente divisa, entrambe divise in quattro quarti); puo' regredire con **Shift + clic destro col mattarello** senza eliminare o modificare le applicazioni gia' effettuate;
- **division_orientation** (campo concettuale): orientamento della prima divisione **determinato dalla direzione del giocatore al momento del clic col mattarello**, non da un asse fisso del mondo. **La linea e' parallela alla direzione cardinale piu' vicina allo sguardo orizzontale** (orientamento a scatti Nord/Sud/Est/Ovest, senza rotazione libera), dividendo la pizza in meta' sinistra e destra dal punto di vista del giocatore al momento della divisione. Deve restare stabile quando il giocatore cambia direzione e deve essere persistito con la pizza e i suoi topping (mondo/pala/forno). **Spareggio APPROVATO (13 ottobre 2026):** fra cardinali equidistanti prevale quello successivo in senso orario (N/E -> E, E/S -> S, S/O -> O, O/N -> N); per i clic sui confini prevalgono destra/davanti e davanti-destra all'intersezione, come specificato in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md);
- elenco degli ingredienti reali (ID, quantità, eventuali proprietà necessarie);
- identificativo distinto di ogni applicazione e maschere dei quarti originari/ancora coperti (requisito confermato); **ordine cronologico persistente di inserimento**, necessario per rimuovere con Shift + clic destro a mano vuota **l'ultima applicazione ancora presente nella zona selezionata**;
- **vincolo univoco per ingrediente canonico e quarto fisico**: prima di applicare, sottrarre la copertura gia' occupata da qualunque item esplicitamente equivalente (anche di altre mod); salvare solo la maschera residua libera, consumare **1 item per applicazione non vuota**, nessun consumo e nessuna nuova applicazione per maschera residua vuota;
- **vincolo salse**: nessuna salsa richiesta per una pizza valida; al massimo una salsa **di qualunque tipo** per singola sottoarea fisica. Le aree senza salsa sono valide, mentre salse di tipo diverso non possono occupare insieme lo stesso quarto. **Prima di consumare l'item**, se anche un solo quarto della zona mirata contiene una salsa *differente*, il tentativo di applicazione **fallisce per intero**: nessuna mutazione della pizza, nessuna applicazione alle sottoaree libere, nessun decremento dello stack. Selezionando con il mattarello una zona priva di altre salse e' invece possibile condirla autonomamente. Non confondere il requisito opzionale di una salsa per il **matching di un gusto specifico** con un requisito di preparazione generale;
- stato crudo/cotto o progresso pertinente;
- `render_seed` e parametri di disposizione che non cambino raccogliendo la pizza;
- informazioni per calcolare nutrizione, nome e tooltip, evitando stringhe ridondanti già tradotte.

**Decisione aggiornata:** la pizza e' **inizialmente intera** e si divide **solo su richiesta col mattarello**, prima in due meta', poi eventualmente in quarti; il rendering mostra le linee solo se si tiene mattarello o topping valido e non modifica modelli/texture. I 4 quarti possono essere **un riferimento interno** per mappare ogni possibile copertura, ma la configurazione reale della divisione deve essere persistita. Ogni applicazione di topping conserva la provenienza (item/quantita' realmente consumata), i quarti originari e quelli ancora coperti. Il rimborso avviene **una sola volta**, solo quando tutti i quarti dell'applicazione sono stati liberati. Una **pizza gia' formata**, se rotta da cruda o cotta, restituisce **un item pizza con tutti i dati**, senza drop separati di impasto o ingredienti; per la rimozione singola dei topping continuano a valere i rimborsi gia' approvati. Specifiche complete: [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

**Equivalenze tra mod — CONFERMATO:** se due ItemStack validi sono mappati esplicitamente allo stesso **ingrediente canonico**, sono equivalenti ai fini del vincolo anti-duplicato per quarto, della composizione e del massimo di 6 tipi distinti. Prima del controllo di disponibilita' e capienza, risolvere per ciascun item la **identita' logica dell'ingrediente** (per esempio un identificatore canonico per `mozzarella`); sottrarre dalla maschera obiettivo i quarti gia' occupati da *qualunque item con quell'identita'*. Il risultato puo' applicarsi solo alle sottoaree libere, senza doppi consumi. Ogni applicazione continua a conservare l'**ItemStack realmente consumato**, per il rimborso esatto dell'oggetto originale (non di un sostituto equivalente). Per le salse rimane inoltre il vincolo che una salsa *differente* presente anche solo in parte nella zona fa rifiutare l'intero clic. Una categoria generica (`formaggi`, `verdure`, ecc.) **non e' per se' un gruppo di equivalenza**.

**Proposta tecnica di serializzazione ancora aperta:** identita' canonica condivisa per item diversi, ma **compatibilita' di mod esterne soltanto per ID puntuale verificato** (decisione confermata). Nessuna espansione automatica da tag; i conflitti fra mapping di ID singoli vanno gestiti con un criterio deterministico e diagnostica.

**Limite di condimento CONFERMATO:** ogni quarto fisico puo' contenere **al massimo 6 ingredienti diversi**, **inclusa l'eventuale salsa**. Il limite non si applica al numero complessivo di ingredienti sull'intera pizza. Prima di consumare l'item, calcolare le sottoaree in cui l'applicazione aggiungerebbe effettivamente un nuovo ingrediente, e verificare il conteggio dei tipi di ingrediente per **tutti i quarti interessati**. Se almeno uno supererebbe sei, l'operazione e' **respinta interamente**, senza mutazioni anche nei quarti non pieni e senza consumo. Divisione e riunione non azzerano il conteggio; la rimozione libera capienza soltanto nei quarti realmente liberati. Conservare questa invariante in dati persistenti, sincronizzazione multiplayer, pala, forni e futura ricetta speciale di crafting.

**Selezione delle zone tramite punto di clic — CONFERMATO:** trasformare il punto di impatto sulla superficie del blocco pizza in coordinate locali coerenti con `division_orientation`, quindi individuare **esattamente una zona logica** della configurazione `division_state` corrente. Quando il punto cade esattamente su una linea o incrocio, usare un **tie-break deterministico fisso**, identico client/server: mai selezionare due zone per lo stesso clic. Non confondere una singola zona logica (che puo' coprire piu' quarti se non suddivisa) con i quattro quarti fisici usati per contare ingredienti, memorizzare le coperture e gestire i rimborsi. **Criterio tecnico APPROVATO (13 ottobre 2026):** con coordinate locali `u` a destra e `v` in avanti rispetto all'orientamento salvato, sui confini centrali prevalgono destra (`u >= 0`) e avanti (`v >= 0`); al centro prevale il quarto **davanti-destra**. Tolleranza numerica iniziale `1/1024` di blocco, da validare nei test. La seconda linea divide solo le meta' ulteriormente suddivise; una meta' non divisa resta zona unica. Nel tie-break tra cardinali equidistanti prevale quello in senso orario. [Specifica](QUARTERS_AND_PEEL.md).

**Invariante delle riunioni:** la modifica di `division_state` NON cancella ne' rimappa automaticamente le coperture degli ingredienti gia' presenti, le loro posizioni o l'ordine delle applicazioni; cambia soltanto le zone selezionabili per le interazioni successive. I nuovi topping coprono **solo le parti libere da quello stesso ingrediente della zona selezionabile**. Un clic interamente duplicato non consuma alcun item. Anche una riunione non genera alcun rimborso.

**Invariante:** nessun item o condimento perso nel trasferimento **mondo -> pala occupata -> mondo/Pizza Oven -> pala** o nel ciclo **inventario -> item droppato con Q -> raccolta in inventario**; la pala conserva direttamente una sola pizza con tutti i dati, senza creare un item pizza separato. **La pala non e' impilabile in alcuno stato (stack massimo 1)**, anche quando vuota: ogni ItemStack rappresenta una pala autonoma e non deve poter essere unito ad altre pale ne' duplicare/sovrascrivere dati della pizza. **La pala non ha durabilita'**: i trasferimenti ripetuti non provocano usura dello strumento. E' inoltre **previsto in futuro un uso come arma ispirata alla padella di Farmer's Delight Refabricated, ma esclusivamente quando la pala e' vuota**. **Se contiene una pizza, non attiva le caratteristiche o gli effetti speciali da arma** e non deve mettere a rischio la preparazione; i dati persistenti determinano senza ambiguita' lo stato vuoto/occupato. Danni, effetti e velocita' della versione vuota sono ancora da progettare. **Drop/pickup confermato:** il normale `ItemEntity` conserva l'`ItemStack` pala con gli stessi componenti persistenti della pizza (incluse maschere, identita' delle applicazioni, orientamento, stato e seed visuale); al pickup, anche da altro giocatore, si ripristina il medesimo stato senza creare una pizza-item separata ne' duplicare la preparazione. **Despawn naturale — CONFERMATO:** non alterare la scadenza ordinaria dell'`ItemEntity` prevista dal comportamento vanilla del mondo/server. Quando l'item pala scade naturalmente, **si perde l'`ItemStack` e con esso l'intero stato della pizza contenuta**, senza drop o conversioni di recupero. Nessuna immunita' o timer speciale. **Distruzione ambientale — CONFERMATO:** l'`ItemEntity` della pala occupata segue danni e distruzione degli item vanilla (fuoco, lava, cactus, esplosioni e analoghi), senza immunita' o loot personalizzato. Se l'entita' viene distrutta, scompare il suo `ItemStack` e con esso **la pizza completa**, senza creare drop di impasto, topping o un item pizza autonomo. Se non viene distrutta, conserva i dati immutati. Restano da progettare i trasferimenti con crafting/forni vanilla, la gestione di inventari e distruzione della pala. Il multiplayer deve sincronizzare lo stesso stato su client e server.

## Riconoscimento dei gusti — REGOLA ESATTA 4/4 CONFERMATA

- **Pizza Margherita** se combacia con una ricetta canonica definita.
- Altre combinazioni prestabilite ricevono nomi specifici secondo definizioni.
- Combinazioni non registrate ricevono un nome dinamico, ad esempio **Pizza con Mozzarella, Speck e Gorgonzola** (topping ammessi).
- **L'ordine di inserimento non cambia il gusto riconosciuto.**

**Regola di matching dei 19 preset confermata (13 ottobre 2026):** **MATCHING ESATTO SU QUATTRO QUARTI FISICI — CONFERMATO (13 ottobre 2026):** il nome fisso di una delle **19 ricette SÌ** si applica alla pizza integra **se e solo se ciascuno dei quattro quarti fisici contiene esattamente tutti e soli i tipi canonici di topping previsti dalla stessa ricetta**. Si controllano **quattro insiemi distinti, non la sola unione globale**. Basta **un ingrediente aggiuntivo o mancante in un solo quarto** perché l'intera pizza abbia **nome dinamico**, anche se gli altri tre quarti corrispondono. Il confronto vale anche senza divisioni visibili o dopo riunioni col mattarello; l'ordine di aggiunta è irrilevante, così come lo stato crudo/cotto per gli ingredienti equivalenti e le sostituzioni cross-mod esplicitamente mappate. **Bianca:** tutti e quattro i quarti senza condimenti. **Rossa:** sola salsa di pomodoro nei quattro quarti. **Margherita:** pomodoro, mozzarella a fette e basilico esatti in ciascun quarto. Un quarto con speck extra o senza basilico -> nome dinamico. Le pizze miste a quarti diversi **non** ricevono nomi fissi nella 1.0.0: eventuali Quattro Gusti / Quattro Stagioni riconosciute restano future proposte, **non approvate**. La regola disciplina la denominazione della **pizza integra**; i nomi durante il servizio di fette richiederanno una decisione separata.

### Matching: regola confermata, dettagli tecnici ancora da implementare

1. Normalizzare categorie, quantità e identificativi equivalenti.
2. **OBBLIGATORIO:** per ciascuno dei 4 quarti fisici, `insiemeCanonico(quarto) == insiemeCanonico(preset)`; **4 confronti esatti su 4** per il nome fisso. Nessun match di soli sottoinsiemi o sull'insieme globale.
3. Usare quantità richieste e tag semantici specifici dove opportuno.
4. Se si riconoscono più ricette, applicare una priorità esplicita e deterministica.
5. Le pizze personalizzate molto lunghe mostrano un nome abbreviato e un tooltip con tutti i topping.
6. Conservare chiavi di traduzione EN/IT, non nomi finali dipendenti dalla lingua di chi cucina.

### Nome della pizza intera e tooltip — CONFERMATO (13 ottobre 2026)

Il formato del display name e del tooltip è **approvato** e descritto in [NAMED_PIZZAS.md](NAMED_PIZZAS.md#nomi-e-tooltip-delle-pizze--confermato-13-ottobre-2026): 
- 4 quarti canonici uguali che corrispondono a uno dei 19 preset SÌ -> nome fisso.
- 4 quarti canonici uguali senza preset -> **`Pizza con [ingredienti]`**, breve nell'inventario se necessario, tooltip con ingredienti completi.
- 2 o più quarti con composizioni canoniche differenti -> **`Pizza assortita`**, tooltip **Quarto 1: ...** fino a **Quarto 4: ...**, con nome riconosciuto individuale o ingredienti completi per ciascun quarto.
- Ogni fetta cotta conserva e nomina **solo la composizione del quarto d'origine**, anche se la pizza era assortita; se il quarto coincide con un preset SÌ, es. **Spicchio di Margherita**, altrimenti **Spicchio con [ingredienti]**.

L'item e i data component conservano identità canoniche, maschere e orientamento, **non** nomi già tradotti. Tooltip e nome sono renderizzati nella lingua del giocatore. Non cambiare le tabelle nutrizionali né la logica degli item. Restano da precisare solo ordine, soglia di abbreviazione, numero stabile dei quarti rispetto alla rotazione e denominazione residua dopo servizio parziale.

### Architettura delle equivalenze ottimizzata — scelta progettuale (13 ottobre 2026)

**Obiettivo prioritario: minimizzare il costo nel gameplay**, mantenendo configurabilità tramite datapack e compatibilità con mod di terze parti. **Non dichiarare un primato prestazionale assoluto rispetto al Java hardcoded senza benchmark**: il codice puro puo' avere un caricamento più economico, ma i lookup a runtime possono essere equivalenti quando le definizioni vengono compilate una volta in una cache.

- **Base incorporata leggera:** i 29 tipi/33 ID ufficiali esistenti e i 2 nuovi tipi pianificati sono validati tramite un catalogo esplicito di identita' canoniche; **nessuna scansione di tutte le ricette o di tutti i tag per ogni clic**.
- **Estensioni datapack opzionali, scelta APPROVATA:** soltanto **ID item esatti e verificati** possono essere associati alle identità canoniche già supportate. I tag `c:` servono alla ricognizione, non ad abilitare automaticamente nuovi membri.
- **Caricamento/reload:** validare solo ID puntuali dichiarati nei JSON al caricamento del mondo o a `/reload`, quindi costruire una mappa compatta `Item -> definizione canonica` da sostituire atomicamente; nessuna espansione automatica di tag o scansione nel gameplay. Senza estensioni rimangono gli ID base.
- **Lookup per interazione:** un singolo accesso alla cache per risolvere il tipo canonico (costo atteso medio **O(1)**), poi controllo delle maschere dei massimi quattro quarti e delle normali regole già approvate (duplicati, salse, limite di sei). **Nessun parsing JSON, espansione di tag, ricerca di tutte le definizioni, reflection o accesso a file nei clic**.
- **Nessun lavoro periodico:** non aggiungere tick handler, polling, allocazioni periodiche o sincronizzazione di tutta la tabella a ogni tick/frame per gestire le equivalenze. Il server resta autorevole sulle mutazioni e la UI traduce gli ID già salvati.
- **Persistenza minima:** salvare le identita' e i componenti necessari a ricostruire la pizza, e l'**ItemStack originale** delle applicazioni per il rimborso; non salvare copie di definizioni JSON o nomi tradotti per ogni pizza.
- **Priorità e conflitti — CONFERMATO:** whitelist base e forme idonee prevalgono; **gli ID ufficiali non sono sovrascrivibili**. Estensioni con un file JSON minimo per ID. Se due pack attribuiscono identità differenti allo stesso item esterno, escludere **solo quell'ID ambiguo** con diagnostica, senza invalidare mapping esterni non coinvolti. Duplicati identici non devono produrre collisioni.
- **Prestazioni da misurare, non ipotizzare:** confrontare in test la cache con un mapping Java diretto su startup/reload, tempo di interazione su pizze dense, uso memoria, server dedicato e grandi modpack. Requisito di accettazione: **nessun overhead rilevante a tick inattivi** e nessuna rilettura/ricerca lineare di definizioni a ogni clic; se i benchmark mostrano costi significativi, preferire tabelle statiche per i tipi di base e limitare ulteriormente le estensioni configurabili.

**Loader ora aggiunto al codice (DA TESTARE):** [PizzaToppingMappings.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingMappings.java) gestisce JSON e cache sul server al caricamento mondo e con `/reload` usando l'API Fabric 26.2. Nessun lavoro per tick. **Non** è ancora collegato al gameplay dei topping e la build Minecraft 26.2 va verificata.

**Mappatura concreta:** [INGREDIENT_EQUIVALENCES.md](INGREDIENT_EQUIVALENCES.md) contiene i 31 tipi, le quattro coppie crudo/cotto e l'audit dei tag. **DECISO:** ID esterni singoli verificati, nessuna ammissione automatica dai tag; **JSON minimale `item` + `canonical`, un file per mapping**, senza sovrascrivere le equivalenze native. Elenco specifico di item esterni e codice restano da realizzare.

## Definizioni di equivalenze tramite datapack — ID puntuali e JSON minimo CONFERMATI (13 ottobre 2026)

I datapack opzionali possono proporre **associazioni puntuali** tra un ID di item esistente e **una identità canonica già approvata**. Per la 1.0.0 non è consentito introdurre nuovi tipi di topping attraverso un tag o ridefinire valori nutrizionali, ruolo, rendering e preset con un file di sola equivalenza. **I tag `c:` non sono selettori automatici**, anche quando semanticamente specifici.

**Formato approvato (non ancora implementato):** esattamente un item per file e le due sole proprietà obbligatorie `item` e `canonical`:

```json
{
  "item": "farmersdelight:cooked_bacon",
  "canonical": "italiansdelight:pizza_toppings/bacon"
}
```

**Percorso usato nel codice:** `data/<namespace>/italiansdelight/pizza_equivalences/<file>.json` (un file per item). L'esempio sopra usa un item ufficiale **solo per mostrare la sintassi**: un tale file di estensione verrebbe ignorato perché non si possono sovrascrivere i 33 mapping nativi. Conflitti fra estensioni diverse per lo stesso item invalidano solo quell'ID; tutti gli altri rimangono disponibili. Il codice del loader è presente ma va testato; la posa dei topping è ancora separata. Vedi [INGREDIENT_EQUIVALENCES.md](INGREDIENT_EQUIVALENCES.md).

## Catalogo item ufficialmente APPROVATO — 13 ottobre 2026

L'elenco **vincolante** e' [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md): **29 tipologie di topping con ID reali**, formate da **20 Italian's Delight + 9 Farmer's Delight Refabricated**, e **2 tipologie approvate ma non ancora registrate (carote e patate a fette Italian's Delight)**. Le **quattro coppie** `bacon/cooked_bacon`, `chicken_cuts/cooked_chicken_cuts`, `cod_slice/cooked_cod_slice`, `salmon_slice/cooked_salmon_slice` contano **una tipologia canonica ciascuna**: in totale **33 ID esistenti**, **31 tipologie finali pianificate**. Tutte le voci positive sono ora approvate dall'autore, **non** restano semplici candidati.

**Esclusi per decisione esplicita:** `italiansdelight:parmigiano_reggiano_wedge`, `italiansdelight:pecorino_romano_wedge`, `italiansdelight:mascarpone` e `italiansdelight:salt`. Altri ID non elencati sono anch'essi esclusi per default (compresi `minecraft:egg`, forme intere, verdure intere, bistecche e preparazioni pronte troppo grandi). **Uova soltanto `farmersdelight:fried_egg`; carne macinata `farmersdelight:minced_beef` e' ammessa.**

**Nuovi item richiesti, non ancora creati:** carote a fette e patate a fette. **RICETTE APPROVATE (13 ottobre 2026):** `minecraft:carrot` + **coltello** sulla **Cutting Board Farmer's Delight Refabricated** -> **4 fette di carota**; `minecraft:potato` + **coltello** sulla stessa Cutting Board -> **4 fette di patata**. Il coltello perde solo la **normale durabilita' della Cutting Board**, non viene consumato come ingrediente. Entrambi sono nuovi item Italian's Delight ancora da registrare (ID, texture, modelli/JSON ancora da definire). Le fette sono topping **crudi che cuociono assieme alla pizza senza un secondo item cotto**; entrambe appartengono alla categoria **Vegetali (+1 fame/+0,5 saturazione per categoria distinta presente nel quarto, una sola volta)**. Carote e patate intere restano ingredienti **di lavorazione, non topping**. Non sono previste ricette eseguibili gia' create; non inventare nomi di registro o tag `c:` non verificati.

**Semantica invariata:** ruolo interattivo, identita' canonica, categoria nutrizionale, aspetto/render e provenienza ItemStack sono dimensioni indipendenti. Salse e creme in ciotola da gestire con remainder una sola volta; **ruolo della panna DEFINITO come condimento cremoso non salsa**, sovrapponibile al pomodoro. **Anche il ruolo della ricotta e' ora DEFINITO come condimento cremoso non salsa**, indipendente dal pomodoro. Nessun doppio bonus nutrizionale. **La nuova approvazione non implica implementazione gia' avvenuta.**

## Ambito dei condimenti nella Pizza Update 1.0.0 — CONFERMATO (12 ottobre 2026)

**Catalogo di tipi di ingrediente CHIUSO per la release 1.0.0:** i tipi di condimento che la mod supporta ufficialmente sono selezionati **esclusivamente dai contenuti di Italian's Delight Refabricated e Farmer's Delight Refabricated**. La lista concreta verra' composta dai loro item idonei e verificati nelle rispettive versioni target; non si amplia il catalogo soltanto perche' un'altra mod e' installata. La classificazione nutrizionale gia' approvata (incluse uova, olive, panna, oli e grassi) **non implica** che nuovi item o tipi assenti dalle due mod siano automaticamente disponibili.

**Equivalenti cross-mod — DECISIONE del 13 ottobre 2026:** un item di qualunque mod esterna (o vanilla, quando idoneo) può essere sostituto di uno dei 31 tipi **solo con una mappatura puntuale del suo ID esplicitamente verificata**. Essere incluso in un tag `c:` specifico è un indizio utile alla verifica, **non** un'ammissione automatica. Non introdurre blocchi indiscriminati per namespace, né nuovi tipi via tag.

**Uso previsto dei tag comuni:**
- usare tag `c:` realmente esistenti solo come **riferimento per audit dei potenziali sostituti**, mai come fonte automatica di nuovi item ammessi;
- un tag generico (es. `c:foods`, `c:foods/cheese` o gruppi ampi equivalenti) puo' aiutare a classificare, **ma non autorizza automaticamente qualsiasi elemento del gruppo come topping, ne' rende equivalenti mozzarella, gorgonzola e parmigiano**;
- adottare ID item esatti e definizioni di compatibilita' mirate quando non esista un tag comune specifico; verificare **ID, presenza, nome e contenuto effettivo** di ogni tag nel JAR e nei datapack della versione target **prima dell'implementazione**. I nomi di esempio non sono una whitelist tecnica pronta;
- precedenza progettuale: **identità canonica prevista -> ID esatto esplicitamente mappato -> forma idonea -> vincoli per quarto -> applicazione**. Un ID non registrato come topping o sostituto viene rifiutato senza consumi.
- ingredienti identici di mod diverse condividono **identita' canonica**, occupazione, limite sei tipi e bonus nutrizionali; l'**ItemStack esatto usato** resta salvato per il rimborso e per i contenitori restituiti, senza duplicazioni.

**Estensioni future:** mantenere l'architettura configurabile tramite datapack per nuovi cataloghi in altre versioni, ma **la configurazione e il catalogo di default di questa release non introducono ingredienti aggiuntivi di altre mod** oltre agli equivalenti dei tipi supportati. Eventuali datapack esterni che modifichino intenzionalmente il catalogo costituiscono personalizzazioni del modpack, non ingredienti ufficiali della 1.0.0.

**Test di accettazione:** item base di Italian's Delight e Farmer's Delight; sostituto di mod esterna con **ID puntuale verificato**; rifiuto di nuovi membri inseriti in tag `c:` senza mapping; antiduplicato, limite sei, salsa, categoria nutrizionale e rimborso corretto; assenza della mod opzionale, ricarica e multiplayer senza duplicazioni.

## Ingredienti utilizzabili: porzioni pronte, non cibi interi — CONFERMATO (12 ottobre 2026)

Oltre a provenire da un **tipo incluso nel catalogo 1.0.0**, l'item applicato deve essere **gia' lavorato/preparato e di dimensione adatta** a una guarnizione. **Vietati direttamente** salumi e prosciutti interi anche se cotti, grandi ruote di formaggio, scamorze intere quando troppo grandi, bistecche intere crude o cotte, grandi pezzi di carne/pesce non suddivisi, verdure intere non realistiche e ingredienti intermedi di lavorazione. Ne' il fatto di essere cibo commestibile ne' i tag `c:` (anche comuni) sostituiscono questo controllo.

**Varianti pronte idonee in principio:** formaggi affettati, grattugiati o in spicchi/porzioni plausibili; salumi a fette; pezzi di carne/pesce gia' preparati nella dimensione corretta; verdure affettate/a foglie/pezzetti; erbe, salse, creme. **Non tutta la verdura deve essere cotta**: pomodori gia' affettati e basilico sono ingredienti pronti per condire prima della cottura della pizza. **Carne e pesce gia' affettati/tagliati sono ammessi sia crudi sia cotti**, quando appartengono a un tipo di ingrediente ufficialmente supportato. **Entrambi hanno gli stessi bonus nutrizionali e nessun effetto speciale**; la scelta di quali tipi entrare nel catalogo e' distinta da questo principio approvato.

**Cutting Board di Farmer's Delight Refabricated:** puo' essere la postazione per ottenere porzioni valide da pezzi grandi **quando una ricetta e un item di output effettivamente appropriati esistono**. Non e' obbligatoria per gli item gia' pronti e non si usa per stendere la pizza, per affettare la pizza cotta o per prenderne le porzioni. **Non trasformare automaticamente la bistecca intera in pezzetti al clic sulla pizza**, ne' inventare ID di carne tagliata o ricette assenti.

**Validazione operativa:** riconoscere il **tipo canonico ammesso** -> verificare l'**ItemStack specifico** in una forma/stato pronto e plausibile -> controllare salsa/capienza/anti-duplicato -> consumare un solo item in caso di applicazione effettiva. Un item non pronto (anche di una mod terza equivalente a livello generale) e' rifiutato senza consumo, nessuna modifica a blocco/dati e nessun rimborso. Il `render_preset` da solo non e' una lavorazione. Si veda [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md) per candidati e esclusioni.

### Varianti crude e cotte di carne/pesce — CONFERMATO (12 ottobre 2026)

**Anti-duplicato — CONFERMATO:** le quattro coppie porzionate crudo/cotto `bacon/cooked_bacon`, `chicken_cuts/cooked_chicken_cuts`, `cod_slice/cooked_cod_slice` e `salmon_slice/cooked_salmon_slice` hanno ciascuna una sola identita' canonica. Un quarto che contiene una variante non accetta l'altra; una zona più ampia permette solo le sottoaree ancora libere, consumando un item soltanto se aggiunge copertura. Un sostituto esterno deve avere **mapping verificato del proprio ID**, non basta un tag `c:`; il limite di sei e il bonus per categoria restano invariati. Salvare e restituire l'**ItemStack effettivamente utilizzato**, senza trasformazioni automatiche né duplicazioni.



**Lo stato di cottura del topping non modifica l'idoneita' o i suoi effetti quando la porzione e' gia' della dimensione corretta**. Per un tipo di carne/pesce approvato, accettare **sia variante cruda sia cotta** (es. bacon, chicken_cuts, cod_slice, salmon_slice e rispettive varianti `cooked_*` di Farmer's Delight Refabricated), con **identici bonus categoria proteine animali: +2 fame, +2,5 saturazione per fetta**, contabilizzati **una sola volta per categoria**, sempre entro il tetto di 6/6. **Nessun effetto speciale, penalita' per la variante cruda o vantaggio per la cotta**, ne' necessita' di cuocere separatamente prima di condire la pizza. Vale anche per item di mod terze riconosciuti equivalenti e gia' porzionati.

**La preparazione fisica resta obbligatoria:** una bistecca intera `minecraft:beef` o `minecraft:cooked_beef` non e' un topping valido indipendentemente dallo stato. Preservare l'ItemStack d'origine, anche per rimborsi. **Non definita** al momento un'autotrasformazione degli ingredienti crudi in versioni cotte dentro la pizza; **non introdurla implicitamente**. **Identita' canonica crudo/cotto — CONFERMATA:** le due varianti porzionate dello stesso ingrediente hanno **la stessa identita' logica** per anti-doppione, copertura dei quarti e limite sei; per esempio `farmersdelight:bacon` e `farmersdelight:cooked_bacon` sono un tipo, `chicken_cuts` e `cooked_chicken_cuts` un altro, analogamente `cod_slice` e `salmon_slice` con le rispettive `cooked_*`. **Non** rendere equivalenti carni/pesci diversi solo perche' appartengono alle proteine animali. La definizione tecnica delle chiavi resta da implementare, ma l'equivalenza funzionale e' gia' approvata.

## Ruolo e categoria nutrizionale — CONFERMATO (12 ottobre 2026)

Un ingrediente valido deve distinguere **quattro caratteristiche indipendenti**: ruolo di preparazione/interazione, categoria nutrizionale, identita' canonica (equivalenze anti-duplicato) e preset grafico. La condivisione della categoria nutrizionale **non** rende automaticamente equivalenti due item e **non** li abilita automaticamente come topping.

| Ingrediente | Ruolo nella pizza | Categoria nutrizionale | Bonus fame | Bonus saturazione |
| --- | --- | --- | ---: | ---: |
| Uova | topping solido | **Proteine animali** (insieme a salumi/carne/pesce) | +2 | +2,5 |
| Olive | topping solido | **Vegetali** (insieme a verdure/funghi) | +1 | +0,5 |
| Olio d'oliva | olio da distribuire, **non salsa** | **Oli e grassi**, nuova categoria | +0 | +1 |
| Panna (`italiansdelight:cream_bowl`) | **condimento cremoso, NON salsa**; ammissibile sopra il pomodoro | **Latticini** | +1 | +1,5 |
| Ricotta (`italiansdelight:ricotta`) | **condimento cremoso, NON salsa**; ammissibile sopra il pomodoro e insieme alla panna | **Latticini** | +1 | +1,5 |

**Una sola contribuzione per categoria nel quarto:** uovo e prosciutto condividono il bonus proteine animali; panna e mozzarella quello latticini; olive e funghi quello vegetali. **Restano ingredienti diversi** per occupazione, limite di sei tipi per quarto, provenienza e rimborsi a meno di equivalenze **esplicitamente dichiarate**.

**Ruolo e categoria non coincidono — panna DEFINITA il 13 ottobre 2026:** `italiansdelight:cream_bowl` usa il ruolo **condimento cremoso NON salsa**; **non occupa lo spazio della salsa** e puo' essere applicata sullo stesso quarto gia' condito con `farmersdelight:tomato_sauce`, senza rimuovere, sostituire o confliggere con il pomodoro. **Conta comunque come tipo di ingrediente nel massimo di sei**, indipendentemente dalla sua categoria latticini. Aggiunge **solo il bonus nutrizionale latticini (+1 fame/+1,5 saturazione una volta per categoria)**, **mai anche il bonus salsa**. Se mozzarella o altri latticini sono gia' presenti, la panna non aggiunge un secondo bonus latticini. Il bonus Salsa (+1 fame/+0,5 saturazione) si applica invece al pomodoro presente, separatamente. **Esempio:** crosta + pomodoro + panna (+ mozzarella) = 3 fame e 3 saturazione: 1/1 + 1/0,5 + 1/1,5. **La ricotta e' ora definitivamente condimento cremoso NON salsa, come la panna.** L'olio non occupa il ruolo salsa; la sua categoria rimane progettata senza introdurre item olio non approvati.

**Ricotta come panna — CONFERMATO (13 ottobre 2026):** `italiansdelight:ricotta` e' una **seconda identita' canonica di topping** (non la stessa identita' di `italiansdelight:cream_bowl`), con ruolo cremoso NON salsa; **si applica sul pomodoro senza consumare o bloccare lo slot salsa** e puo' convivere anche con la panna e altri latticini sul medesimo quarto. **Entrambi i topping contano singolarmente entro i sei tipi**, mentre la **categoria Latticini paga una volta sola**: +1 fame/+1,5 saturazione, senza bonus salsa aggiuntivo. Esempio crosta + pomodoro + ricotta (+ panna e mozzarella opzionali) = **3 fame e 3 saturazione**. Salvare l'ItemStack originario di ciascuna applicazione e gestire eventuali ciotole/remainder separatamente senza doppio rimborso. **Non modificare il trattamento delle vere salse** e la regola del rifiuto delle salse differenti.

Le **formule gia' confermate** diventano `hunger = min(6, 1 + sum(bonus_fame_categorie_nutrizionali_distinte))` e `saturation_points = min(6, 1 + sum(bonus_saturazione_categorie_nutrizionali_distinte))`, per ogni quarto fisico. Valori invariati: crosta (+1/+1), salsa (+1/+0,5), latticini (+1/+1,5), proteine animali (+2/+2,5), vegetali (+1/+0,5), **oli e grassi (+0/+1)**, erbe/spezie (+0/+0). Limite 6 per ciascuna statistica.

### Compatibilita' Farmer's Delight Refabricated e altre mod — REQUISITO CONFERMATO

Farmer's Delight Refabricated `3.6.28` / Minecraft `26.2` fornisce esclusivamente i **9 tipi di topping approvati** elencati con i loro veri ID in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). **Gli altri item reali di Farmer's Delight non sono topping per default** (es. `tomato` intero, `onion` intera, `ham`/`smoked_ham` interi, `cabbage` intero, `beef_patty`, zuppe e piatti completi). Tra i 9 tipi sono inclusi **uovo fritto** e **carne macinata**; le quattro coppie crudo/cotto devono condividere ciascuna identita' canonica.

Origine degli ID e delle proprieta' da verificare in implementazione:
- [Registro item 26.2](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModItemIds.java)
- [Registro ModItems 26.2](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/registry/ModItems.java)
- [Elenco ufficiale approvato e tag con stato di verifica](INGREDIENT_CATALOG.md)

**Interoperabilità:** un item esterno può sostituire uno dei tipi whitelist soltanto con **mapping di ID esatto verificato** e forma porzionata idonea. I tag `c:` possono aiutare a ricercare equivalenze, ma non ne autorizzano automaticamente l'uso. Conservare ItemStack, rimborsi e contenitori originali; lo schema JSON è ancora da implementare.

## Ricette riconosciute via JSON — PROPOSTA

La Margherita richiede concettualmente **pomodoro + mozzarella + basilico**. Un possibile schema, **non ancora definito o supportato**, è:

```json
{
  "id": "italiansdelight:margherita",
  "name_key": "pizza.italiansdelight.margherita",
  "required": [
    { "item": "farmersdelight:tomato_sauce", "count": 1 },
    { "item": "italiansdelight:mozzarella", "count": 1 },
    { "tag": "c:crops/basil", "count": 1 }
  ],
  "allow_extra_toppings": false
}
```

Questo JSON e' soltanto illustrativo: non include ancora la struttura dei quattro quarti, mentre **il matching canonico esatto su ogni quarto e' obbligatorio**. La definizione definitiva dovrà prevedere alternative via tag, gestione delle quantità, priorità e traduzioni. Non confondere **riconoscimento** dei gusti con limitazione al crafting. **La salsa e' facoltativa per le pizze personalizzate e bianche**, anche se una singola definizione di gusto puo' richiedere il pomodoro o un'altra salsa.

## Blocco pizza -> item pizza -> forno — CONFERMATO

Una pizza gia' formata nel mondo, cruda o cotta, quando viene rotta produce **un solo ItemStack pizza** contenente stato, ingredienti originali ancora associati, identita' delle applicazioni e maschere di copertura, suddivisioni e orientamento, ordine applicazioni, `render_seed` e progressi di cottura. Il relativo blocco e il suo stato vengono eliminati **atomicamente**, senza drop paralleli di impasto o topping. L'item puo' essere usato per inserimento diretto nel Pizza Oven, senza pala. Le applicazioni gia' eliminate e rimborsate in precedenza **non** devono ricomparire. Il comportamento dei blocchi di impasto prima della pizza completamente stesa e' separato e rimane quello previsto dal prototipo.

## Pizza Oven: GUI e trasporto tramite pala — CONFERMATO

Il Pizza Oven **ha una GUI** che gestisce l'inserimento della pizza-item, combustibili, visualizzazione del progresso e recupero della pizza. Questa e' la modalita' ordinaria senza pala. **Inserimento diretto alternativo con pala occupata:** l'interazione sul blocco col `PizzaPeel` contenente una pizza deve trasferire **atomicamente** lo stato persistente della preparazione nello spazio pizza del forno, senza aprire la GUI, e svuotare la pala **solo dopo il successo**. Se il forno e' occupato, l'operazione fallisce senza mutare ne' pala ne' forno. Il percorso con pala e il percorso con item nella GUI devono condividere la stessa validazione e non perdere campi dei data component o generare duplicazioni. **Estrazione rapida con pala vuota — CONFERMATO:** clic destro sul Pizza Oven con pala vuota trasferisce direttamente la pizza presente, anche se **ancora cruda o parzialmente cotta**, dal forno alla pala senza aprire la GUI. L'operazione deve essere atomica e autorevole lato server: leggere e trasferire lo stato completo della pizza, incluso l'avanzamento esatto della cottura, nella pala e solo in seguito liberare lo slot del forno. La pala resta senza usura; il combustibile residuo resta nel forno. Se il forno non contiene pizza, non generare output. **La pizza non ancora cotta non va completata artificialmente**; reinserendola deve poter riprendere dal progresso salvato. L'estrazione via GUI senza pala continua a essere possibile. **GUI VANILLA della fornace — CONFERMATO:** stessa schermata/texture e identica disposizione di input pizza, fuel e output pizza, con fiamma e freccia standard. Lo stato di cottura resta nel data component dell'item in ingresso; al completamento, l'item pizza cotta con i medesimi dati (eccetto stato di cottura aggiornato) viene trasferito all'output. **Capienza una pizza totale:** non consentire di inserire una nuova pizza se l'output e' occupato, e non lasciare contemporaneamente una pizza nell'input e una nell'output. La pala vuota preleva in modo atomico la pizza dallo slot occupato, ingresso anche a progresso parziale oppure risultato. L'estrazione manuale dallo slot di input salva progresso parziale; in entrambi i casi niente dupe, perdita di dati o completamento automatico. Il layout non va personalizzato; l'uso generale del forno non deve richiedere la pala. La preparazione/condimento della pizza nel mondo continua senza GUI.

## Pizza Oven: automazione hopper — CONFERMATO

Gli hopper seguono le interazioni laterali standard della fornace: **dall'alto** possono inserire soltanto pizze crude nell'input quando non e' gia' presente una pizza; **dai lati** possono inserire combustibili vanilla validi; **dal basso** possono estrarre soltanto pizze **completamente cotte presenti nell'output**. Mai estrarre dal basso la pizza cruda o con progresso incompleto che si trova nell'input. Il vincolo di **una pizza totale** vale anche per l'automazione, e il dato della pizza deve essere preservato attraverso slot, cottura e hopper; niente duplicazioni con accessi simultanei da GUI/pala/hopper. Dettagli di implementazione e remainder dei contenitori combustibile devono essere verificati contro le convenzioni vanilla.

La **pizza parzialmente cotta non rappresenta un nuovo tipo alimentare previsto**: resta pizza cruda con avanzamento persistente, recuperabile **manualmente** tramite GUI o pala per cambiare forno o interrompere e riprendere la preparazione. Il prodotto finale viene generato solo al completamento e passa nell'output, dove l'hopper inferiore puo' estrarlo.

## Pizza Oven: regole di cottura e combustibile — CONFERMATO

Il Pizza Oven e' un forno specializzato **esclusivamente per le pizze**: accetta come input di cottura solo item pizza validi (crudi e con dati personalizzati), mai altri alimenti o materiali; nello slot combustibile restano validi tutti i fuel vanilla supportati. **Nessun preriscaldamento:** il progresso comincia subito con pizza ed energia disponibili. **Consumo durante cottura attiva soltanto:** conservare il burn time residuo quando non c'e' pizza da cuocere o la pizza e' pronta e riutilizzarlo per pizze successive. **Fuel terminato:** sospendere e memorizzare il progresso senza azzerarlo, riprendendo quando torna disponibile energia. Salvare e sincronizzare lato server energia residua, avanzamento, input/output e stato del forno, senza duplicazioni e senza progressi a chunk scaricato. **20/30/60 secondi** per Pizza Oven/affumicatore/fornace restano placeholder di bilanciamento. Questa conservazione del fuel e' una **meccanica intenzionale del Pizza Oven**, non una garanzia che le normali fornaci vanilla modifichino il loro comportamento.

## Cottura e crafting dinamici: punti a rischio

- Una ricetta speciale da crafting potrebbe costruire una pizza cruda con gli stessi componenti della preparazione manuale.
- Il Pizza Oven può salvare internamente l'intero `ItemStack` e produrne una copia cotta con componenti preservati. **CONFERMATO:** nessun requisito generale di possedere o impugnare una pala per usare il forno; l'inserimento/estrazione diretti senza pala sono previsti per una pizza ottenuta come item tramite un percorso valido (il crafting e' un'alternativa di preparazione prevista). **VINCOLO CONFERMATO:** una pizza preparata nel mondo **non** viene raccolta con clic destro a mano vuota; per rimuoverla si rompe, ottenendo **l'item pizza completo con data component persistenti**, anche quando e' cruda. Il percorso dal terreno al forno **senza pala** e' quindi la raccolta della pizza-item tramite rottura e il successivo inserimento diretto nel forno.
- **Fornace/affumicatore vanilla**: una normale ricetta di smelting produce un output statico; trasferire i dati dell'input richiede verifica approfondita e una integrazione mirata che **non** cambi le ricette di altre mod.
- **SERVIZIO DIRETTO — DECISIONE CONFERMATA (12 ottobre 2026):** la pizza cotta intera non ha `FoodComponent` e non e' mangiabile come item. Una pizza cotta piazzata sul blocco di appoggio supportato e' subito pronta a servire **quattro porzioni**, senza coltello, taglio preliminare, Cutting Board, ciotola o altri utensili. Il giocatore seleziona un quarto fisico presente e usa **clic destro a mano vuota** per ottenere la fetta corrispondente nell'inventario. A mano occupata nessuna raccolta. La ciotola di alcuni piatti di Farmer's Delight Refabricated e' soltanto l'analogia per il serving; per la pizza serve la **mano vuota**, non una ciotola.
- **STATO TECNICO DEL SERVIZIO — CONFERMATO:** il blocco pizza cotta posizionato conserva fin dall'inizio una **maschera a quattro bit `remaining_quarters`** (nome concettuale) con tutti i quarti presenti, senza booleano di taglio `sliced`. La pizza puo' apparire intera fino alla prima porzione prelevata. La selezione tramite punto di clic individua un solo quarto fisico e ricava un ItemStack fetta con **i soli condimenti di quel quarto**, indipendentemente dalle divisioni logiche create col mattarello. Se la mano e' vuota, c'e' spazio nell'inventario e il quarto e' ancora presente, il server trasferisce **esattamente una fetta** e solo allora azzera il suo bit, salvando e sincronizzando il rendering delle porzioni residue; quando la maschera arriva a zero, rimuove il blocco. **Inventario pieno** o mano occupata -> **nessun item consegnato, nessuna modifica del blocco e nessun drop**. Ogni transazione deve essere atomica anche in multiplayer e dopo i salvataggi. **Rottura dell'intera pizza cotta ancora intatta (`remaining_quarters=1111`) -> unico item pizza cotta completo**; dopo uno o piu' prelievi, la rottura produce **solo le fette relative ai bit rimasti a 1**, ciascuna con propri ingredienti. Mai duplicare fette, pizza intera o topping. La cottura e la divisione logica dei condimenti sono sistemi indipendenti; bilanciamento nutrizionale ancora aperto.

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

## Nutrizione dinamica delle fette — CONFERMATO (12 ottobre 2026)

Ogni fetta servita direttamente da una pizza cotta piazzata deve usare **soltanto i topping del proprio quarto fisico** per calcolare la fame, senza ricavare un valore medio dalla pizza completa.

| Categoria nutrizionale | Punti fame per fetta |
| --- | ---: |
| Base/crosta della fetta | **+1** |
| Salse (opzionali) | **+1** |
| Latticini: formaggi (anche grattugiati), panna | **+1** |
| Proteine animali: salumi, carni, pesce, uova | **+2** |
| Vegetali: verdure, funghi, olive | **+1** |
| Oli e grassi: olio d'oliva | **+0** |
| Erbe e spezie | **+0** |

**Algoritmo confermato:** dare il valore base **1** alla porzione e aggiungere **una sola volta** il bonus di ogni categoria presente nel relativo quarto (non un bonus per ogni item). Quindi **`hunger = min(6, 1 + sum(bonus_per_categoria_presente))`**, con cap di **6 punti fame (3 cosciotti) per fetta**. La salsa non e' obbligatoria ma, se presente, vale **+1**; mozzarella e gorgonzola insieme danno un solo **+1 formaggi**, salame e prosciutto insieme un solo **+2 salumi/carne/pesce**. I valori per fetta di una pizza mista possono differire. Esempi: margherita **3**, salame+salsa+formaggio **5**, vegetariana+salsa+formaggio **4**, bianca vuota **1**.

**Compatibilita' e dati:** separare le **categorie nutrizionali** dalle identita' canoniche usate per i doppioni e dal preset grafico. Per item di altre mod determinare la categoria da definizioni/registrazioni appropriate (formato e mappatura iniziale da implementare), evitando di assegnare arbitrariamente bonus a un ingrediente non classificato. Non modificare il limite distinto di **6 ingredienti differenti per quarto** e non distribuire automaticamente un ingrediente globale su una fetta dove non e' presente. **SATURAZIONE CONFERMATA (12 ottobre 2026):** calcolata dinamicamente su ogni quarto, con **un solo bonus per categoria**; **base/crosta +1**, **salsa +0,5**, **formaggi +1,5**, **salumi/carne/pesce +2,5**, **verdure/funghi +0,5**, **erbe/spezie +0**. **Tetto 6 punti effettivi per fetta**, distinto dal tetto 6 della fame. Una fetta Margherita ha fame **3** e saturazione **3**; una fetta con salsa+formaggio+salame fame **5** e saturazione **5,5**. I valori sono punti di saturazione effettiva, **non un moltiplicatore `saturationModifier` pronto da impostare**. Verificare le API FoodComponent/FoodData di Minecraft 26.2 e trasformare i punti desiderati nei campi effettivamente esposti, senza cambiare il comportamento approvato. **Effetti al consumo — CONFERMATO PER ORA:** nella Pizza Update 1.0.0 gli item fetta **non devono attribuire status effect, potion effect o benefici speciali aggiuntivi** al consumo (ad esempio Rigenerazione, Velocita', Forza), ne' per ricetta riconosciuta ne' per singola categoria di topping. La fetta modifica soltanto fame e saturazione secondo le tabelle gia' approvate. **Non codificare un divieto permanente:** un futuro aggiornamento potra' prevedere effetti su nuova approvazione. La pizza intera non acquisisce FoodComponent.

### Valori di saturazione per categoria — CONFERMATO

| Categoria | Punti saturazione per fetta |
| --- | ---: |
| Base/crosta | **+1** |
| Salsa facoltativa | **+0,5** |
| Latticini: formaggi (anche grattugiati), panna | **+1,5** |
| Proteine animali: salumi, carne, pesce, uova | **+2,5** |
| Vegetali: verdure, funghi, olive | **+0,5** |
| Oli e grassi: olio d'oliva | **+1** |
| Erbe aromatiche e spezie | **+0** |

**Formula:** `saturation_points = min(6, 1 + sum(category_saturation_bonus))`, ogni categoria distinta presente sul quarto fisico conta **una volta**. Saturazione distinta dalla fame, anche per fette con la stessa fame. Il conteggio e' effettuato sul quarto dopo il trasferimento dei dati dei topping, e non modifica il limite di sei ingredienti diversi per quarto.

## Compatibilità con altre mod — sole varianti di ingredienti gia' supportati in 1.0.0

- Usare tag condivisi **solo se semanticamente appropriati**; per ingredienti canonici come la mozzarella, definizioni specifiche possono essere necessarie. **Equivalenza cross-mod confermata nel comportamento**, non nella selezione di tag esatti: un gruppo logico condiviso impedisce doppioni e vale un unico tipo di topping per quarto, ma il rimborso conserva l'item originario.
- **Catalogo ufficiale 1.0.0 non estendibile automaticamente a nuovi tipi da terze mod**. Datapack esterni possono essere previsti per personalizzazioni volontarie dei modpack, o per estensioni future; non fanno parte della lista di condimenti ufficiali di questa release.
- Fallback di rendering per item senza modelli pizza dedicati.
- In caso di rimozione di una mod dal modpack, caricare i vecchi salvataggi senza crash o duplicazioni, per quanto possibile.
- Documentare il comportamento JEI per gusti canonici e ingredienti dinamici senza promettere la visualizzazione di tutte le combinazioni teoriche.

## Test obbligatori

Persistenza del seed; compatibilità dei componenti durante crafting/cottura/servizio a fette; blocco-item-blocco; localizzazione dei nomi; input da hopper; ingredienti rimossi dai datapack; sovrapposizione di tag; due giocatori sullo stesso blocco; chunk unload e ripristino.
