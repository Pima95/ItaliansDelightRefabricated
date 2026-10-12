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
- categoria logica (`sauce`, `cheese`, `meat`, `vegetable`, `herb`...);
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
- **Fette CONFERMATE nel principio:** la pizza cotta e' consumabile intera oppure tagliabile in **4 fette sul Cutting Board di Farmer's Delight Refabricated**. Ogni fetta deve mantenere metadati di gusto/composizione e contribuire indicativamente per **1/4 ai valori nutrizionali totali**, con arrotondamenti e bilanciamento ancora aperti. **Non e' ancora stabilito** se i dati degli ingredienti di una fetta provengano esclusivamente dal **quarto fisico corrispondente** oppure rappresentino il gusto complessivo della pizza; evitare una scelta implicita nell'implementazione.

## Compatibilità con altre mod

- Usare tag condivisi **solo se semanticamente appropriati**; per ingredienti canonici come la mozzarella, definizioni specifiche possono essere necessarie. **Equivalenza cross-mod confermata nel comportamento**, non nella selezione di tag esatti: un gruppo logico condiviso impedisce doppioni e vale un unico tipo di topping per quarto, ma il rimborso conserva l'item originario.
- Possibilità di datapack esterni che aggiungano ingredienti e gusti.
- Fallback di rendering per item senza modelli pizza dedicati.
- In caso di rimozione di una mod dal modpack, caricare i vecchi salvataggi senza crash o duplicazioni, per quanto possibile.
- Documentare il comportamento JEI per gusti canonici e ingredienti dinamici senza promettere la visualizzazione di tutte le combinazioni teoriche.

## Test obbligatori

Persistenza del seed; compatibilità dei componenti durante crafting/cottura/taglio; blocco-item-blocco; localizzazione dei nomi; input da hopper; ingredienti rimossi dai datapack; sovrapposizione di tag; due giocatori sullo stesso blocco; chunk unload e ripristino.
