# Catalogo ufficiale dei condimenti — Pizza Update 1.0.0

**Decisione dell'autore: 13 ottobre 2026 — catalogo di 31 tipi approvato.** Il [registro canonico Java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingCatalog.java) dei 33 item ufficiali è già testato dall'autore. [Loader JSON/cache](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingMappings.java) aggiunto al branch ma **da testare**. L'applicazione dei topping sulle pizze e la persistenza dei quarti non sono ancora implementate.

**29 scelte AMMESSE**, corrispondenti a **33 ID di item gia' registrati** (quattro coppie crudo/cotto equivalenti contano come una scelta ciascuna), piu' **2 nuove tipologie approvate ma ancora da creare** (carote a fette e patate a fette). **4 scelte esplicitamente escluse** dal modulo. Per tutte le altre tipologie **non incluse nell'elenco ufficiale**, il default della release e' **NON ammesso**: le esclusioni esplicite non sono l'unica blacklist.

Sono approvati **tipi di condimento** di Italian's Delight Refabricated e Farmer's Delight Refabricated; altre mod forniscono **soltanto item effettivamente equivalenti a tali tipi**, tramite tag comuni specifici/definizioni verificate. Nessun nuovo tipo viene sbloccato da un tag alimentare generico.

 > **Stato tecnico aggiornato:** i nomi degli item qui elencati sono registrati nei rispettivi mod sorgenti e ora raccolti in un catalogo Java **read-only**; ciò NON implica che siano già utilizzabili come topping in gioco. Il prototipo mozzarella su BlockState resta indipendente. Nessun item, texture, ricetta, tag o datapack nuovo è stato creato in questa fase.

## 1. Italian's Delight — 20 condimenti AMMESSI

Namespace degli ID sotto: `italiansdelight:`.

| Gruppo | Nome / identita' del topping | Item ID confermato nel registro | Categoria nutrizionale | Decisione |
| --- | --- | --- | --- | --- |
| Formaggi | Mozzarella a fette | `mozzarella_slice` | Latticini | **SI'** |
| Formaggi | Bocconcino | `bocconcino` | Latticini | **SI'** — formato piccolo esplicitamente ammesso |
| Formaggi | Parmigiano Reggiano grattugiato | `grated_parmigiano_reggiano` | Latticini | **SI'** |
| Formaggi | Pecorino Romano grattugiato | `grated_pecorino_romano` | Latticini | **SI'** |
| Formaggi | Gorgonzola a spicchi | `gorgonzola_wedge` | Latticini | **SI'** — spicchio approvato |
| Formaggi | Provolone a fette | `provolone_slice` | Latticini | **SI'** |
| Formaggi | Scamorza a fette | `scamorza_slice` | Latticini | **SI'** |
| Formaggi | Scamorza affumicata a fette | `smoked_scamorza_slice` | Latticini | **SI'** |
| Salumi | Prosciutto crudo a fette | `prosciutto_crudo_slice` | Proteine animali | **SI'** |
| Salumi | Salame a fette | `salame_slice` | Proteine animali | **SI'** |
| Salumi | Mortadella a fette | `mortadella_slice` | Proteine animali | **SI'** |
| Salumi | Pancetta a fette | `pancetta_slice` | Proteine animali | **SI'** |
| Salumi | Guanciale a fette | `guanciale_slice` | Proteine animali | **SI'** |
| Salumi | Bresaola a fette | `bresaola_slice` | Proteine animali | **SI'** |
| Salumi | Coppa a fette | `coppa_slice` | Proteine animali | **SI'** |
| Salumi | Speck a fette | `speck_slice` | Proteine animali | **SI'** |
| Vegetali | Pomodoro a fette | `tomato_slice` | Vegetali | **SI'** |
| Erbe | Basilico | `basil` (item del blocco coltivabile) | Erbe e spezie | **SI'** |
| Creme | Panna | `cream_bowl` | Latticini | **SI'** — **condimento cremoso NON salsa**, sovrapponibile alla salsa di pomodoro, con gestione ciotola da testare |
| Creme | Ricotta | `ricotta` | Latticini | **SI'** — **condimento cremoso NON salsa**, utilizzabile sopra il pomodoro; contenitore da gestire |

**Conteggio: 20 righe della tabella Italian's Delight** (8 formaggi, 8 salumi, pomodoro, basilico, panna, ricotta). Ogni riga e' un tipo ammesso distinto. Nessun formaggio o salume intero entra automaticamente nel catalogo, anche se compare in `#c:foods/cheese`.

## 2. Farmer's Delight Refabricated — 9 condimenti AMMESSI

Namespace degli ID sotto: `farmersdelight:`. I registri del ramo `fabric/latest/26.2`, versione 3.6.28, confermano questi ID. Le coppie crudo/cotto sono **lo stesso ingrediente canonico** per anti-duplicato e limite di sei condimenti per quarto.

| Tipo canonico | ID ammessi (entrambi, se coppia) | Categoria nutrizionale | Decisione |
| --- | --- | --- | --- |
| Bacon | `bacon`, `cooked_bacon` | Proteine animali | **SI'**, stessa identita' e nutrizione |
| Pollo a pezzi | `chicken_cuts`, `cooked_chicken_cuts` | Proteine animali | **SI'**, stessa identita' e nutrizione |
| Merluzzo a fette | `cod_slice`, `cooked_cod_slice` | Proteine animali | **SI'**, stessa identita' e nutrizione |
| Salmone a fette | `salmon_slice`, `cooked_salmon_slice` | Proteine animali | **SI'**, stessa identita' e nutrizione |
| Uovo **fritto** | `fried_egg` | Proteine animali | **SI'** — solo fritto, **uovo intero escluso** |
| Carne di manzo macinata | `minced_beef` | Proteine animali | **SI'** — carne gia' macinata, senza richiedere un item cotto nuovo |
| Foglia di cavolo | `cabbage_leaf` | Vegetali | **SI'** — non il cavolo intero |
| Zucca a fette | `pumpkin_slice` | Vegetali | **SI'** — non la zucca intera |
| Salsa di pomodoro | `tomato_sauce` | Salse | **SI'** — ruolo salsa, ritorno ciotola |

**Conteggio: 9 tipologie, 13 ID** (4 coppie crudo/cotto aggiungono quattro ID). **Totale ufficiale attuale: 20 + 9 = 29 tipologie ammesse; 20 + 13 = 33 ID gia' esistenti**.

**Uova:** solo `farmersdelight:fried_egg`. Escludere sempre un uovo ancora intero, per esempio `minecraft:egg`, che non deve risultare ammesso ne' tramite tag `c:` generici ne' perche' classificato come proteina animale. Non e' richiesta la creazione di nuovi item per l'uovo fritto.

**Carne macinata:** `farmersdelight:minced_beef` e' stata **espressamente ammessa** dall'autore. E' una porzione preparata: l'esclusione di bistecche intere e `beef_patty` resta valida. Nessun ID inventato di carne cotta macinata.

## 3. Carote e patate a fette — AMMESSE come nuova funzionalita', DA CREARE

L'autore ha approvato anche **carote a fette** e **patate a fette** come due ulteriori **tipi di topping** della release 1.0.0. **NON esistono ancora come item dedicati nei registri esaminati**. Non sono incluse nel conteggio dei 33 ID gia' esistenti; **catalogo finale pianificato = 31 tipologie**.

| Nuovo condimento approvato | Ingrediente iniziale plausibile per la lavorazione | Output da creare | Categoria | Stato |
| --- | --- | --- | --- | --- |
| Carote a fette | `minecraft:carrot` | nuovo item Italian's Delight (ID finale **da scegliere**) | Vegetali | **AMMESSO; 1 carota + coltello/Cutting Board -> 4 fette; item + texture + ricetta da creare** |
| Patate a fette | `minecraft:potato` | nuovo item Italian's Delight (ID finale **da scegliere**) | Vegetali | **AMMESSO; 1 patata + coltello/Cutting Board -> 4 fette; item + texture + ricetta da creare** |

**Ricette e resa Cutting Board — CONFERMATE (13 ottobre 2026):**
- **1 `minecraft:carrot` + un coltello valido sulla Cutting Board di Farmer's Delight Refabricated -> 4 fette di carota**, ottenute come nuovo item Italian's Delight dedicato.
- **1 `minecraft:potato` + un coltello valido sulla Cutting Board di Farmer's Delight Refabricated -> 4 fette di patata**, ottenute come nuovo item Italian's Delight dedicato.
- Il **coltello usa il normale consumo di durabilita' previsto dall'interazione Cutting Board**; non imporre un nuovo sistema di usura e non consumare il coltello come ingrediente.
- Le fette si applicano **crude sulla pizza e cuociono insieme alla pizza**; **non occorre creare una variante cotta separata** per nessuna delle due. Nessuna conversione dell'ItemStack del topping in altro item durante la cottura e' stata richiesta.
- Entrambe le fette appartengono alla categoria nutrizionale **Vegetali**: **+1 fame e +0,5 saturazione per fetta di pizza con quella categoria**, conteggiati **una sola volta per categoria sul quarto fisico** (non +1 per ogni fetta di carota/patata). Rimangono i massimali generali 6/6.
- **`minecraft:carrot` e `minecraft:potato` interi NON sono topping validi** neppure se compaiono in un tag `c:` generico. Eventuali fette equivalenti di altre mod restano soggette alla whitelist canonica e ai tag specifici verificati.

**Ancora da implementare/verificare:** ID definitivi dei due nuovi item, texture, modelli, ricette JSON e matching esatto con l'API della Cutting Board MC 26.2/FDR 3.6.28, risultati in Creative/Survival, coltelli supportati e consumo normale della loro durabilita'. **Le rese 1 -> 4, la Cutting Board con coltello, la cottura sulla pizza senza secondo item e la categoria vegetali sono decisioni chiuse**, non punti da ridiscutere. Nessuna ricetta/item e' ancora stato creato.

**Compatibilita' cross-mod:** soltanto eventuali **fette di carota/patata equivalenti**, riconosciute tramite ID/tag semantici specifici e verificati, possono sostituire i nuovi item una volta creati. Una carota intera di un'altra mod resta vietata.

## 4. Scelte espressamente NON AMMESSE

| Item ID `italiansdelight:` | Decisione |
| --- | --- |
| `parmigiano_reggiano_wedge` | **NO** — consentito soltanto il Parmigiano grattugiato |
| `pecorino_romano_wedge` | **NO** — consentito soltanto il Pecorino grattugiato |
| `mascarpone` | **NO** — non usare come crema |
| `salt` | **NO** — non applicabile come topping |

Questi sono i quattro NO **espliciti** del modulo, **non una blacklist esaustiva**. Altri item non inclusi nella tabella SI' sono **NON ammessi per default**: uovo intero `minecraft:egg`, formaggi e salumi interi, scamorza intera, prodotti preparatori `fresh_*`, `prepared_*`, `raw_*`, `salted_ham`, `cabbage`, `tomato` intero, `onion` intera, `beef_patty`, bistecche `minecraft:beef`/`minecraft:cooked_beef`, `ham`/`smoked_ham` come pezzi interi, pasti finiti, bevande, dessert e altri cibi troppo grandi o non lavorati. **Burrata, mozzarella intera, latte e altri latticini non elencati rimangono esclusi**, salvo decisione successiva.

La categoria nutrizionale di olive e olio d'oliva e' gia' progettata, **ma nella 1.0.0 NON si aggiungono automaticamente come topping**, poiche' non compare un loro item/tipo approvato nel presente catalogo.

### Panna e ricotta come topping cremosi — CONFERMATO (13 ottobre 2026)

- **Item concreti:** `italiansdelight:cream_bowl` (**panna**) e `italiansdelight:ricotta` (**ricotta**), gia' approvati e registrati. **Entrambi hanno ruolo cremoso NON salsa** (ad esempio `creamy_topping`, nome tecnico non vincolante), diverso dal ruolo `sauce`.
- **Applicazione:** sia panna sia ricotta sono ammissibili **anche sopra** `farmersdelight:tomato_sauce` nella stessa zona/quarto, senza rimuoverla o sostituirla, ne' occupare lo slot salsa. La salsa esistente conserva il proprio ruolo. **Panna e ricotta sono due ingredienti canonici distinti fra loro**, ciascuno dei quali occupa **un posto nel limite dei sei tipi** per quarto (la salsa, se presente, occupa anch'essa un posto). Le due creme possono convivere anche tra loro rispettando limite sei e anti-duplicato del singolo tipo.
- **Nutrizione:** panna e ricotta appartengono ai **Latticini**: +1 fame e +1,5 saturazione **una sola volta per categoria**, anche se nello stesso quarto ci sono panna, ricotta, mozzarella e altri formaggi. **Nessuna delle due aggiunge il bonus Salsa**: quest'ultimo deriva soltanto da una salsa effettiva come quella di pomodoro.
- **Esempio verificabile:** crosta + pomodoro + ricotta (+ panna e mozzarella opzionali) = **3 fame / 3 saturazione**: crosta 1/1 + salsa 1/0,5 + latticini 1/1,5; il totale nutrizionale non cresce aggiungendo altre tipologie di latticini, pur aumentando il numero di topping distinti.
- **Persistenza/remainder:** consumare e salvare l'**ItemStack originario corretto** per panna e ricotta; gestire ciotola/recipienti e rimborsi **una sola volta** secondo le regole gia' approvate, senza perdita o duplicazioni. **Il ruolo della ricotta e' ora DEFINITO esattamente come quello cremoso della panna**.

## 5. Regole gia' confermate e da preservare

- **Idoneita' dimensionale:** solo item effettivamente pronti, in fette, pezzi plausibili, grattugiati, macinati, erbe o creme/salse pronte; le eccezioni **esplicitamente approvate** come il bocconcino non autorizzano altri grandi item interi.
- **Crudo e cotto dello stesso alimento porzionato:** stessa identita' canonica e **un solo tipo nel limite di sei** per quarto; non possono sovrapporsi. Stessi valori fame e saturazione ed effetti. Le specie e i salumi differenti restano ingredienti diversi. Preservare **esattamente l'ItemStack consumato** per l'eventuale rimborso dell'applicazione.
- **Compatibilita' altre mod:** tag `c:` verificati e abbastanza specifici o mapping puntuali per item equivalenti a tipi del catalogo; non promuovere qualsiasi item di un tag generico di cibi, formaggi, uova, verdure o carne a topping, e non accettare versioni intere non lavorate.
- **Valori per fetta:** crosta +1 fame/+1 saturazione; salsa +1/+0,5; latticini +1/+1,5; proteine animali +2/+2,5; vegetali +1/+0,5; erbe +0/+0; oli e grassi +0/+1 **solo qualora in futuro si ammetta un olio**. Ogni categoria nutrizionale presente conta una sola volta sul singolo quarto; **tetto 6 fame e 6 saturazione**; nessun effetto speciale.
- **Salse/creme — panna e ricotta definite il 13 ottobre 2026:** `farmersdelight:tomato_sauce` ha **ruolo salsa**. **`italiansdelight:cream_bowl` e `italiansdelight:ricotta` sono condimenti cremosi ma NON salse**: si possono aggiungere **sopra il pomodoro** nella stessa zona senza occupare l'unico slot salsa o provocare conflitti fra salse. **Panna e ricotta restano ingredienti distinti** e ciascuna conta come **uno dei massimo sei tipi** nel quarto. Condividono **solo la categoria nutrizionale Latticini** (+1 fame/+1,5 saturazione **una volta per categoria**), **mai il bonus salsa aggiuntivo**, anche in presenza di mozzarella o altri formaggi. Rimane vietato sovrapporre due salse effettive differenti nella stessa sottoarea.
- **Contenitori:** tomato_sauce, cream_bowl e ricotta hanno un recipiente/ritorno da verificare nell'applicazione sulla pizza: restituire correttamente la ciotola **una sola volta**, senza consumi doppi, exploit o perdita. **Né panna né ricotta devono consumare o sostituire la salsa preesistente** quando applicate; il rimborso conserva l'ItemStack originale e le rimanenze vengono gestite senza duplicazione.
- **Condimenti e quarti:** massimo **sei tipi** per quarto fisico (salsa opzionale inclusa), rimozione Shift+clic a mano vuota in ordine LIFO, rimborsi originali solo alla rimozione completa dell'applicazione; niente consumo per clic duplicato.

## 6. Tag comuni: livello di verifica

| Tag | Stato di verifica | Significato |
| --- | --- | --- |
| `#c:foods/cheese` | **VERIFICATO** nel file [`foods/cheese.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/foods/cheese.json) di Italian's Delight | Classificazione latticini, **non whitelist di tutti i formaggi e non equivalenza di formaggi diversi**; il tag contiene anche item qui esclusi |
| `#c:cheese` | **VERIFICATO** come alias [`cheese.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/cheese.json) | Categoria ampia, non ammissione diretta |
| `#c:crops/basil` | **VERIFICATO** in [`crops/basil.json`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/c/tags/item/crops/basil.json) | Tag di ricognizione: i nuovi membri esterni sono ammessi **solo con ID puntuale verificato**, non automaticamente |
| `#c:crops/tomato` | Nome **REFERENZIATO** nella [ricetta della mozzarella salad](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/resources/data/italiansdelight/recipe/mozzarella_salad.json); **contenuto runtime non verificato** | Non autorizzare pomodori interi come topping, neppure se presenti nel tag |
| Tag specifici per bacon, pollo a pezzi, pesci, uovo fritto, foglia di cavolo, panna, ricotta, carota/patata a fette | **DA VERIFICARE nel runtime MC 26.2/Fabric** | Solo gruppi puntuali verificati, non inventare tag `c:` non esistenti |

**Decisione APPROVATA (13 ottobre 2026):** prevale la whitelist dei 31 tipi; un sostituto di mod esterna è ammesso **solo con mapping esplicito del singolo ID verificato**. I tag `c:` si utilizzano per la ricognizione, non per espandere automaticamente gli item ammessi.

**Audit delle equivalenze (13 ottobre 2026):** i tag FDR `#c:foods/raw_chicken`/`cooked_chicken`, `raw_cod`/`cooked_cod`, `raw_salmon`/`cooked_salmon`, `raw_beef` e `cabbage` includono **anche item interi**; non vanno mappati in blocco. `#c:foods/raw_bacon`, `cooked_bacon` e `#c:crops/basil` aiutano a individuare candidati, ma ogni nuovo ID esterno richiede verifica specifica. [Mappatura](INGREDIENT_EQUIVALENCES.md).

## 7. Attivita' di sviluppo ancora aperte

1. Registrare i **due nuovi item** carota a fette e patata a fette; definire ID definitivi, texture e modelli e implementare le **due ricette Cutting Board gia' approvate: 1 carota -> 4 fette e 1 patata -> 4 fette, con coltello e sua usura normale**. Non aggiungere item cotti separati; entrambe le fette crude diventano condimento della pizza e cuociono con essa. **Nessuna risorsa e' stata creata in questa approvazione documentale.**
2. **Registro whitelist dati GIA' CREATO** (`PizzaToppingCatalog.java`: 29 tipi attuali / 33 ID), ma deve ancora essere **collegato alla validazione delle applicazioni** nel blocco pizza, ai nuovi due item da registrare e agli eventuali mapping esterni; verificare in gioco che tutti gli item non inclusi siano respinti.
3. Riconoscere `farmersdelight:fried_egg` ma non `minecraft:egg`; riconoscere `farmersdelight:minced_beef` ma non la bistecca intera o `beef_patty`.
4. **Quattro coppie crudo/cotto GIA' UNIFICATE NEL REGISTRO** (`bacon`, `chicken_cuts`, `cod_slice`, `salmon_slice`); manca ancora usare queste identità nei clic effettivi, per prevenire doppioni, applicazioni e rimborsi. Ogni sostituto di altre mod richiederà ID puntuale verificato, senza autorizzazione automatica dai tag.
5. Configurare rendering, ruolo interattivo e categoria alimentare dei 31 tipi: nuova resa per carne macinata e fette di carota/patata, erbe, foglie, creme.
6. Verificare tag e remainder dei contenitori; provare i test positivi e negativi con namespace di terze mod, item interi, salvataggi/multiplayer e assenza di duplicazioni.
7. Definire separatamente i **gusti di pizza riconosciuti**; il catalogo dei condimenti e' stato approvato, **non** ogni ricetta con nome del documento NAMED_PIZZAS.

### Fonti dei registri verificati

- Italian's Delight: [`ModItems.java`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModItems.java), [`ModBlocks.java`](https://github.com/Pima95/ItaliansDelightRefabricated/blob/feature/pizza-1.0.0/src/main/java/dev/italiansdelight/common/registry/ModBlocks.java).
- Farmer's Delight Refabricated 3.6.28/MC 26.2: [`ModItemIds.java`](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModItemIds.java), [`ModBlockItemIds.java`](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModBlockItemIds.java), [`ModItems.java`](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/registry/ModItems.java).
