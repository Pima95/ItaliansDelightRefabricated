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

Dati da conservare:
- versione del formato, per eventuali migrazioni;
- **division_state**: configurazione scelta col mattarello (intera, due meta', solo una meta' ulteriormente divisa, entrambe divise in quattro quarti); puo' regredire con **Shift + clic destro col mattarello** senza eliminare o modificare le applicazioni gia' effettuate;
- **division_orientation** (campo concettuale): orientamento della prima divisione **determinato dalla direzione del giocatore al momento del clic col mattarello**, non da un asse fisso del mondo. **La linea e' parallela alla direzione cardinale piu' vicina allo sguardo orizzontale** (orientamento a scatti Nord/Sud/Est/Ovest, senza rotazione libera), dividendo la pizza in meta' sinistra e destra dal punto di vista del giocatore al momento della divisione. Deve restare stabile quando il giocatore cambia direzione e deve essere persistito con la pizza e i suoi topping (mondo/pala/forno). **Da definire solo** il caso esattamente equidistante tra due cardinali (spareggio deterministico) e i clic ai confini; la scelta di **snap al cardinale piu' vicino** e' confermata;
- elenco degli ingredienti reali (ID, quantità, eventuali proprietà necessarie);
- identificativo distinto di ogni applicazione e maschere dei quarti originari/ancora coperti (requisito confermato); **ordine cronologico persistente di inserimento**, necessario per rimuovere con Shift + clic destro a mano vuota **l'ultima applicazione ancora presente nella zona selezionata**;
- **vincolo univoco per ingrediente canonico e quarto fisico**: prima di applicare, sottrarre la copertura gia' occupata da qualunque item esplicitamente equivalente (anche di altre mod); salvare solo la maschera residua libera, consumare **1 item per applicazione non vuota**, nessun consumo e nessuna nuova applicazione per maschera residua vuota;
- **vincolo salse**: nessuna salsa richiesta per una pizza valida; al massimo una salsa **di qualunque tipo** per singola sottoarea fisica. Le aree senza salsa sono valide, mentre salse di tipo diverso non possono occupare insieme lo stesso quarto. **Prima di consumare l'item**, se anche un solo quarto della zona mirata contiene una salsa *differente*, il tentativo di applicazione **fallisce per intero**: nessuna mutazione della pizza, nessuna applicazione alle sottoaree libere, nessun decremento dello stack. Selezionando con il mattarello una zona priva di altre salse e' invece possibile condirla autonomamente. Non confondere il requisito opzionale di una salsa per il **matching di un gusto specifico** con un requisito di preparazione generale;
- stato crudo/cotto o progresso pertinente;
- `render_seed` e parametri di disposizione che non cambino raccogliendo la pizza;
- informazioni per calcolare nutrizione, nome e tooltip, evitando stringhe ridondanti già tradotte.

**Decisione aggiornata:** la pizza e' **inizialmente intera** e si divide **solo su richiesta col mattarello**, prima in due meta', poi eventualmente in quarti; il rendering mostra le linee solo se si tiene mattarello o topping valido e non modifica modelli/texture. I 4 quarti possono essere **un riferimento interno** per mappare ogni possibile copertura, ma la configurazione reale della divisione deve essere persistita. Ogni applicazione di topping conserva la provenienza (item/quantita' realmente consumata), i quarti originari e quelli ancora coperti. Il rimborso avviene **una sola volta**, solo quando tutti i quarti dell'applicazione sono stati liberati. Una **pizza gia' formata**, se rotta da cruda o cotta, restituisce **un item pizza con tutti i dati**, senza drop separati di impasto o ingredienti; per la rimozione singola dei topping continuano a valere i rimborsi gia' approvati. Specifiche complete: [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

**Equivalenze tra mod — CONFERMATO:** se due ItemStack validi sono mappati esplicitamente allo stesso **ingrediente canonico**, sono equivalenti ai fini del vincolo anti-duplicato per quarto, della composizione e del massimo di 6 tipi distinti. Prima del controllo di disponibilita' e capienza, risolvere per ciascun item la **identita' logica dell'ingrediente** (per esempio un identificatore canonico per `mozzarella`); sottrarre dalla maschera obiettivo i quarti gia' occupati da *qualunque item con quell'identita'*. Il risultato puo' applicarsi solo alle sottoaree libere, senza doppi consumi. Ogni applicazione continua a conservare l'**ItemStack realmente consumato**, per il rimborso esatto dell'oggetto originale (non di un sostituto equivalente). Per le salse rimane inoltre il vincolo che una salsa *differente* presente anche solo in parte nella zona fa rifiutare l'intero clic. Una categoria generica (`formaggi`, `verdure`, ecc.) **non e' per se' un gruppo di equivalenza**.

**Proposta tecnica, NON approvata nel formato:** definizioni item/tag semanticamente specifiche -> una chiave `ingredient_identity` canonica condivisa. Priorita' deterministiche tra definizioni puntuali, tag specifici e fallback, con diagnostica per conflitti e prevenzione di equivalenze improprie. L'elenco delle mappature concrete e' ancora aperto.

**Limite di condimento CONFERMATO:** ogni quarto fisico puo' contenere **al massimo 6 ingredienti diversi**, **inclusa l'eventuale salsa**. Il limite non si applica al numero complessivo di ingredienti sull'intera pizza. Prima di consumare l'item, calcolare le sottoaree in cui l'applicazione aggiungerebbe effettivamente un nuovo ingrediente, e verificare il conteggio dei tipi di ingrediente per **tutti i quarti interessati**. Se almeno uno supererebbe sei, l'operazione e' **respinta interamente**, senza mutazioni anche nei quarti non pieni e senza consumo. Divisione e riunione non azzerano il conteggio; la rimozione libera capienza soltanto nei quarti realmente liberati. Conservare questa invariante in dati persistenti, sincronizzazione multiplayer, pala, forni e futura ricetta speciale di crafting.

**Selezione delle zone tramite punto di clic — CONFERMATO:** trasformare il punto di impatto sulla superficie del blocco pizza in coordinate locali coerenti con `division_orientation`, quindi individuare **esattamente una zona logica** della configurazione `division_state` corrente. Quando il punto cade esattamente su una linea o incrocio, usare un **tie-break deterministico fisso**, identico client/server: mai selezionare due zone per lo stesso clic. Non confondere una singola zona logica (che puo' coprire piu' quarti se non suddivisa) con i quattro quarti fisici usati per contare ingredienti, memorizzare le coperture e gestire i rimborsi. Il criterio preciso (ordine degli ID locali/tolleranze) e' un dettaglio tecnico ancora da fissare; non altera la regola di gameplay approvata.

**Invariante delle riunioni:** la modifica di `division_state` NON cancella ne' rimappa automaticamente le coperture degli ingredienti gia' presenti, le loro posizioni o l'ordine delle applicazioni; cambia soltanto le zone selezionabili per le interazioni successive. I nuovi topping coprono **solo le parti libere da quello stesso ingrediente della zona selezionabile**. Un clic interamente duplicato non consuma alcun item. Anche una riunione non genera alcun rimborso.

**Invariante:** nessun item o condimento perso nel trasferimento **mondo -> pala occupata -> mondo/Pizza Oven -> pala** o nel ciclo **inventario -> item droppato con Q -> raccolta in inventario**; la pala conserva direttamente una sola pizza con tutti i dati, senza creare un item pizza separato. **La pala non e' impilabile in alcuno stato (stack massimo 1)**, anche quando vuota: ogni ItemStack rappresenta una pala autonoma e non deve poter essere unito ad altre pale ne' duplicare/sovrascrivere dati della pizza. **La pala non ha durabilita'**: i trasferimenti ripetuti non provocano usura dello strumento. E' inoltre **previsto in futuro un uso come arma ispirata alla padella di Farmer's Delight Refabricated, ma esclusivamente quando la pala e' vuota**. **Se contiene una pizza, non attiva le caratteristiche o gli effetti speciali da arma** e non deve mettere a rischio la preparazione; i dati persistenti determinano senza ambiguita' lo stato vuoto/occupato. Danni, effetti e velocita' della versione vuota sono ancora da progettare. **Drop/pickup confermato:** il normale `ItemEntity` conserva l'`ItemStack` pala con gli stessi componenti persistenti della pizza (incluse maschere, identita' delle applicazioni, orientamento, stato e seed visuale); al pickup, anche da altro giocatore, si ripristina il medesimo stato senza creare una pizza-item separata ne' duplicare la preparazione. **Despawn naturale — CONFERMATO:** non alterare la scadenza ordinaria dell'`ItemEntity` prevista dal comportamento vanilla del mondo/server. Quando l'item pala scade naturalmente, **si perde l'`ItemStack` e con esso l'intero stato della pizza contenuta**, senza drop o conversioni di recupero. Nessuna immunita' o timer speciale. **Distruzione ambientale — CONFERMATO:** l'`ItemEntity` della pala occupata segue danni e distruzione degli item vanilla (fuoco, lava, cactus, esplosioni e analoghi), senza immunita' o loot personalizzato. Se l'entita' viene distrutta, scompare il suo `ItemStack` e con esso **la pizza completa**, senza creare drop di impasto, topping o un item pizza autonomo. Se non viene distrutta, conserva i dati immutati. Restano da progettare i trasferimenti con crafting/forni vanilla, la gestione di inventari e distruzione della pala. Il multiplayer deve sincronizzare lo stesso stato su client e server.

## Riconoscimento dei gusti — PRINCIPIO CONFERMATO

- **Pizza Margherita** se combacia con una ricetta canonica definita.
- Altre combinazioni prestabilite ricevono nomi specifici secondo definizioni.
- Combinazioni non registrate ricevono un nome dinamico come **Pizza con Mozzarella, Speck, Funghi e Gorgonzola**.
- **L'ordine di inserimento non cambia il gusto riconosciuto.**

### Matching consigliato (ancora da approvare)

1. Normalizzare categorie, quantità e identificativi equivalenti.
2. Preferire combinazioni **esatte** per evitare che qualsiasi topping extra resti una Margherita.
3. Usare quantità richieste e tag semantici specifici dove opportuno.
4. Se si riconoscono più ricette, applicare una priorità esplicita e deterministica.
5. Le pizze personalizzate molto lunghe mostrano un nome abbreviato e un tooltip con tutti i topping.
6. Conservare chiavi di traduzione EN/IT, non nomi finali dipendenti dalla lingua di chi cucina.

## Definizioni ingredienti tramite datapack — PROPOSTA

I datapack possono stabilire:
- ID item o tag ammesso;
- **ruolo nella pizza** (`sauce`, `solid_topping`, `oil`...) **distinto dalla categoria nutrizionale** (`sauce`, `dairy`, `animal_protein`, `vegetable`, `oils_fats`, `herb`...); i nomi dei campi sono indicativi, non formato tecnico gia' implementato;
- preset grafico;
- quantità consumata, eventuale remainder e limiti;
- token per il nome e proprietà nutrizionali;
- priorità tra una definizione per item specifico e un fallback per tag.

**Esempio puramente illustrativo** — questo formato NON è già supportato:

```json
{
  "selector": { "item": "italiansdelight:mozzarella" },
  "category": "cheese",
  "render_preset": "cheese_pieces",
  "name_token": "mozzarella",
  "priority": 100
}
```

Compatibilità generica proposta, sempre come bozza:

```json
{
  "selector": { "tag": "c:foods/cheese" },
  "category": "cheese",
  "render_preset": "generic_topping",
  "priority": 10
}
```

Tag ampi possono includere alimenti non adatti: occorrono eccezioni e priorità. **Non rendere automaticamente valido qualsiasi item commestibile.**

## Ambito dei condimenti nella Pizza Update 1.0.0 — CONFERMATO (12 ottobre 2026)

**Catalogo di tipi di ingrediente CHIUSO per la release 1.0.0:** i tipi di condimento che la mod supporta ufficialmente sono selezionati **esclusivamente dai contenuti di Italian's Delight Refabricated e Farmer's Delight Refabricated**. La lista concreta verra' composta dai loro item idonei e verificati nelle rispettive versioni target; non si amplia il catalogo soltanto perche' un'altra mod e' installata. La classificazione nutrizionale gia' approvata (incluse uova, olive, panna, oli e grassi) **non implica** che nuovi item o tipi assenti dalle due mod siano automaticamente disponibili.

**Eccezione deliberata per equivalenti cross-mod:** un item di **qualsiasi altra mod, e anche vanilla quando pertinente**, puo' essere utilizzato **come variante di un tipo GIA' incluso nel catalogo 1.0.0** se rientra in un **tag comune/convenzionale specifico e semanticamente corretto** oppure in una equivalenza puntuale verificata. Esempio: un formaggio di un'altra mod riconosciuto **esplicitamente come mozzarella** puo' sostituire la mozzarella presente nel catalogo; un formaggio generico o un tipo nuovo di formaggio **non** entra solo per il tag ampio dei formaggi. I tag condivisi facilitano la sostituzione, **non aggiungono nuovi concetti di topping**. Non introdurre un blocco indiscriminato sul namespace di origine: contano **tipo di ingrediente supportato e corrispondenza confermata**.

**Uso previsto dei tag comuni:**
- preferire **tag convenzionali `c:`** realmente esistenti per la versione Fabric/Minecraft target e semanticamente specifici per item equivalenti; eventuali tag locali di Italian's Delight possono includere questi tag per offrire una sola regola di matching;
- un tag generico (es. `c:foods`, `c:foods/cheese` o gruppi ampi equivalenti) puo' aiutare a classificare, **ma non autorizza automaticamente qualsiasi elemento del gruppo come topping, ne' rende equivalenti mozzarella, gorgonzola e parmigiano**;
- adottare ID item esatti e definizioni di compatibilita' mirate quando non esista un tag comune specifico; verificare **ID, presenza, nome e contenuto effettivo** di ogni tag nel JAR e nei datapack della versione target **prima dell'implementazione**. I nomi di esempio non sono una whitelist tecnica pronta;
- precedenza progettuale: **identita' supportata del catalogo -> corrispondenza tramite item/tag specifico -> validazione dei vincoli per quarto -> applicazione**. Un item non riconosciuto si rifiuta **senza consumarlo** e senza cambiare la pizza; nessun fallback che consenta di usare qualunque cibo.
- ingredienti identici di mod diverse condividono **identita' canonica**, occupazione, limite sei tipi e bonus nutrizionali; l'**ItemStack esatto usato** resta salvato per il rimborso e per i contenitori restituiti, senza duplicazioni.

**Estensioni future:** mantenere l'architettura configurabile tramite datapack per nuovi cataloghi in altre versioni, ma **la configurazione e il catalogo di default di questa release non introducono ingredienti aggiuntivi di altre mod** oltre agli equivalenti dei tipi supportati. Eventuali datapack esterni che modifichino intenzionalmente il catalogo costituiscono personalizzazioni del modpack, non ingredienti ufficiali della 1.0.0.

**Test di accettazione:** un ingrediente nativo di Italian's Delight e uno di Farmer's Delight ammessi esplicitamente; sostituzione con item equivalente di un'altra mod attraverso tag `c:` verificato; equivalenza anti-doppione nello stesso quarto; rifiuto di un ingrediente nuovo della terza mod pur essendo alimentare; tag generico che **non** ammette un item non previsto; assenza della mod esterna, contenitori restituiti e salvataggio/ricarica senza dupe. Regole di salsa, limite sei, fame/saturazione e rimborsi restano invariati.

## Ingredienti utilizzabili: porzioni pronte, non cibi interi — CONFERMATO (12 ottobre 2026)

Oltre a provenire da un **tipo incluso nel catalogo 1.0.0**, l'item applicato deve essere **gia' lavorato/preparato e di dimensione adatta** a una guarnizione. **Vietati direttamente** salumi e prosciutti interi anche se cotti, grandi ruote di formaggio, scamorze intere quando troppo grandi, bistecche intere crude o cotte, grandi pezzi di carne/pesce non suddivisi, verdure intere non realistiche e ingredienti intermedi di lavorazione. Ne' il fatto di essere cibo commestibile ne' i tag `c:` (anche comuni) sostituiscono questo controllo.

**Varianti pronte idonee in principio:** formaggi affettati, grattugiati o in spicchi/porzioni plausibili; salumi a fette; pezzi di carne/pesce gia' preparati nella dimensione corretta; verdure affettate/a foglie/pezzetti; erbe, salse, creme. **Non tutta la verdura deve essere cotta**: pomodori gia' affettati e basilico sono ingredienti pronti per condire prima della cottura della pizza. **Carne e pesce gia' affettati/tagliati sono ammessi sia crudi sia cotti**, quando appartengono a un tipo di ingrediente ufficialmente supportato. **Entrambi hanno gli stessi bonus nutrizionali e nessun effetto speciale**; la scelta di quali tipi entrare nel catalogo e' distinta da questo principio approvato.

**Cutting Board di Farmer's Delight Refabricated:** puo' essere la postazione per ottenere porzioni valide da pezzi grandi **quando una ricetta e un item di output effettivamente appropriati esistono**. Non e' obbligatoria per gli item gia' pronti e non si usa per stendere la pizza, per affettare la pizza cotta o per prenderne le porzioni. **Non trasformare automaticamente la bistecca intera in pezzetti al clic sulla pizza**, ne' inventare ID di carne tagliata o ricette assenti.

**Validazione operativa:** riconoscere il **tipo canonico ammesso** -> verificare l'**ItemStack specifico** in una forma/stato pronto e plausibile -> controllare salsa/capienza/anti-duplicato -> consumare un solo item in caso di applicazione effettiva. Un item non pronto (anche di una mod terza equivalente a livello generale) e' rifiutato senza consumo, nessuna modifica a blocco/dati e nessun rimborso. Il `render_preset` da solo non e' una lavorazione. Si veda [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md) per candidati e esclusioni.

### Varianti crude e cotte di carne/pesce — CONFERMATO (12 ottobre 2026)

**Lo stato di cottura del topping non modifica l'idoneita' o i suoi effetti quando la porzione e' gia' della dimensione corretta**. Per un tipo di carne/pesce approvato, accettare **sia variante cruda sia cotta** (es. bacon, chicken_cuts, cod_slice, salmon_slice e rispettive varianti `cooked_*` di Farmer's Delight Refabricated), con **identici bonus categoria proteine animali: +2 fame, +2,5 saturazione per fetta**, contabilizzati **una sola volta per categoria**, sempre entro il tetto di 6/6. **Nessun effetto speciale, penalita' per la variante cruda o vantaggio per la cotta**, ne' necessita' di cuocere separatamente prima di condire la pizza. Vale anche per item di mod terze riconosciuti equivalenti e gia' porzionati.

**La preparazione fisica resta obbligatoria:** una bistecca intera `minecraft:beef` o `minecraft:cooked_beef` non e' un topping valido indipendentemente dallo stato. Preservare l'ItemStack d'origine, anche per rimborsi. **Non definita** al momento un'autotrasformazione degli ingredienti crudi in versioni cotte dentro la pizza; **non introdurla implicitamente**. Il mapping delle **identita' canoniche per il controllo anti-doppione** tra le due versioni dell'ingrediente verra' verificato insieme al resto del catalogo, separato dal bonus nutrizionale gia' deciso.

## Ruolo e categoria nutrizionale — CONFERMATO (12 ottobre 2026)

Un ingrediente valido deve distinguere **quattro caratteristiche indipendenti**: ruolo di preparazione/interazione, categoria nutrizionale, identita' canonica (equivalenze anti-duplicato) e preset grafico. La condivisione della categoria nutrizionale **non** rende automaticamente equivalenti due item e **non** li abilita automaticamente come topping.

| Ingrediente | Ruolo nella pizza | Categoria nutrizionale | Bonus fame | Bonus saturazione |
| --- | --- | --- | ---: | ---: |
| Uova | topping solido | **Proteine animali** (insieme a salumi/carne/pesce) | +2 | +2,5 |
| Olive | topping solido | **Vegetali** (insieme a verdure/funghi) | +1 | +0,5 |
| Olio d'oliva | olio da distribuire, **non salsa** | **Oli e grassi**, nuova categoria | +0 | +1 |
| Panna | condimento cremoso, eventualmente **ruolo salsa** | **Latticini** (insieme a formaggi) | +1 | +1,5 |

**Una sola contribuzione per categoria nel quarto:** uovo e prosciutto condividono il bonus proteine animali; panna e mozzarella quello latticini; olive e funghi quello vegetali. **Restano ingredienti diversi** per occupazione, limite di sei tipi per quarto, provenienza e rimborsi a meno di equivalenze **esplicitamente dichiarate**.

**Ruolo e categoria non coincidono:** se panna e' usata con ruolo **salsa**, rispetta il limite di una salsa per quarto e il rifiuto integrale dei conflitti gia' concordato, ma applica **soltanto** il bonus latticini (+1 fame/+1,5 saturazione), **non anche** il bonus nutrizionale salsa. L'olio non occupa il ruolo salsa, quindi puo' coesistere con pomodoro; conta comunque come **tipo di ingrediente** nel limite di sei. Non generare un nuovo item olio se non previsto: questa categoria serve anche per item di altre mod.

Le **formule gia' confermate** diventano `hunger = min(6, 1 + sum(bonus_fame_categorie_nutrizionali_distinte))` e `saturation_points = min(6, 1 + sum(bonus_saturazione_categorie_nutrizionali_distinte))`, per ogni quarto fisico. Valori invariati: crosta (+1/+1), salsa (+1/+0,5), latticini (+1/+1,5), proteine animali (+2/+2,5), vegetali (+1/+0,5), **oli e grassi (+0/+1)**, erbe/spezie (+0/+0). Limite 6 per ciascuna statistica.

### Compatibilita' Farmer's Delight Refabricated e altre mod — REQUISITO CONFERMATO

**Priorita' richiesta dall'autore:** catalogo ufficiale di topping ricavato da **Italian's Delight Refabricated e Farmer's Delight Refabricated**, con **varianti di altre mod soltanto quando equivalenti a tipi gia' supportati** tramite tag comuni mirati o matching specifico. Nel progetto, la dipendenza ha namespace `farmersdelight`, Minecraft `26.2`, e la versione prevista in `gradle.properties` e' `26.2-3.6.28+refabricated`. Gli esempi seguenti sono **candidati concreti da testare e verificare nel JAR 26.2**, non un elenco gia' implementato ne' una promessa che tutti gli item siano condimenti accettati:

| Identificativo da verificare nella dipendenza target | Categorie/ruolo di partenza | Nota |
| --- | --- | --- |
| `farmersdelight:tomato_sauce` | salsa, nutrizione salse | priorita' alta; verificare il contenitore di ritorno |
| `farmersdelight:tomato` | vegetali se topping solido; eventuale uso come salsa da decidere | **non** trattare automaticamente un pomodoro intero come salsa |
| `farmersdelight:onion`, `farmersdelight:cabbage` | vegetali | candidati topping da approvare/testare singolarmente |
| `farmersdelight:fried_egg` | proteine animali | candidato uova |
| `farmersdelight:bacon`, `farmersdelight:cooked_bacon`, `farmersdelight:ham`, `farmersdelight:smoked_ham` | proteine animali | candidati salumi/carne |
| `farmersdelight:salmon_slice`, `farmersdelight:cod_slice` | proteine animali | candidati pesce, non fette della pizza |
| `farmersdelight:wheat_dough` | base d'impasto; **non** topping | riutilizzare il percorso impasto gia' definito |
| Olive, olio d'oliva, panna da Italian's Delight/altre mod | classificazione approvata sopra | **non inventare ID o tag di mod esterne** |

**Verifica delle fonti aggiornata:** ho controllato gli ID citati nel codice sorgente della **versione corretta 3.6.28 per Minecraft 26.2**, ramo `fabric/latest/26.2`, inclusa la cipolla registrata come BlockItem. Gli **ID sorgente** sono verificati; **tag effettivi, rimanenze e funzionamento nel JAR caricato** restano da verificare prima di implementare. La lista completa e aggiornata, con categorie, esclusioni e livello di conferma dei tag, e' [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md).
- https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModItemIds.java
- https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/references/ModBlockItemIds.java
- https://github.com/MehVahdJukaar/FarmersDelightRefabricated/blob/fabric/latest/26.2/src/main/java/vectorwing/farmersdelight/common/registry/ModItems.java
- https://www.curseforge.com/minecraft/mc-mods/farmers-delight-refabricated/files/all

**Integrazione per la 1.0.0:** mappature puntuali o tag semantici **verificati**, limitati ai tipi del catalogo chiuso; precedenza proposta item specifico > tag specifico > fallback prudente; definizioni estendibili via datapack (schema tecnico ancora da progettare). **Non** ammettere automaticamente ogni alimento, cibo composto, piatto pronto, bevanda o preparazione di Farmer's Delight e mod terze. La categoria nutrizionale e' separata dall'identita' canonica per le equivalenze, dalla validita' come condimento e dal rendering. Salvare sempre l'**ItemStack originario** per restituzioni corrette e gestire le eventuali ciotole/bottiglie remainder **una volta sola**, senza perdite o dupe. Verificare mod mancanti, fallback grafici, conflitti di tag, salvataggi e multiplayer. Non modificare ancora il codice.

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

La definizione definitiva dovrà prevedere alternative via tag, gestione delle quantità, priorità e traduzioni. Non confondere **riconoscimento** dei gusti con limitazione al crafting. **La salsa e' facoltativa per le pizze personalizzate e bianche**, anche se una singola definizione di gusto puo' richiedere il pomodoro o un'altra salsa.

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
