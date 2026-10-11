# Dati delle pizze, nomi dinamici, datapack e compatibilità

> Le seguenti sono proposte di architettura: **non sono ancora implementate né API definitive**.

## Obiettivo tecnico

Per supportare gusti combinabili senza migliaia di item separati, usare un sistema di pochi item/stati (palla, pizza cruda, pizza cotta, possibile fetta) con **data component** personalizzati. Le informazioni viaggiano tra blocco di preparazione, inventario, forni, pizza cotta e, se approvate, fette.

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

**Decisione aggiornata:** la pizza e' **inizialmente intera** e si divide **solo su richiesta col mattarello**, prima in due meta', poi eventualmente in quarti; il rendering mostra le linee solo se si tiene mattarello o topping valido e non modifica modelli/texture. I 4 quarti possono essere **un riferimento interno** per mappare ogni possibile copertura, ma la configurazione reale della divisione deve essere persistita. Ogni applicazione di topping conserva la provenienza (item/quantita' realmente consumata), i quarti originari e quelli ancora coperti. Il rimborso avviene **una sola volta**, solo quando tutti i quarti dell'applicazione sono stati liberati. Una pizza cruda rotta restituisce impasto e ingredienti ancora associati; una pizza cotta non viene scomposta nei vecchi ingredienti. Specifiche complete: [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

**Equivalenze tra mod — CONFERMATO:** se due ItemStack validi sono mappati esplicitamente allo stesso **ingrediente canonico**, sono equivalenti ai fini del vincolo anti-duplicato per quarto, della composizione e del massimo di 6 tipi distinti. Prima del controllo di disponibilita' e capienza, risolvere per ciascun item la **identita' logica dell'ingrediente** (per esempio un identificatore canonico per `mozzarella`); sottrarre dalla maschera obiettivo i quarti gia' occupati da *qualunque item con quell'identita'*. Il risultato puo' applicarsi solo alle sottoaree libere, senza doppi consumi. Ogni applicazione continua a conservare l'**ItemStack realmente consumato**, per il rimborso esatto dell'oggetto originale (non di un sostituto equivalente). Per le salse rimane inoltre il vincolo che una salsa *differente* presente anche solo in parte nella zona fa rifiutare l'intero clic. Una categoria generica (`formaggi`, `verdure`, ecc.) **non e' per se' un gruppo di equivalenza**.

**Proposta tecnica, NON approvata nel formato:** definizioni item/tag semanticamente specifiche -> una chiave `ingredient_identity` canonica condivisa. Priorita' deterministiche tra definizioni puntuali, tag specifici e fallback, con diagnostica per conflitti e prevenzione di equivalenze improprie. L'elenco delle mappature concrete e' ancora aperto.

**Limite di condimento CONFERMATO:** ogni quarto fisico puo' contenere **al massimo 6 ingredienti diversi**, **inclusa l'eventuale salsa**. Il limite non si applica al numero complessivo di ingredienti sull'intera pizza. Prima di consumare l'item, calcolare le sottoaree in cui l'applicazione aggiungerebbe effettivamente un nuovo ingrediente, e verificare il conteggio dei tipi di ingrediente per **tutti i quarti interessati**. Se almeno uno supererebbe sei, l'operazione e' **respinta interamente**, senza mutazioni anche nei quarti non pieni e senza consumo. Divisione e riunione non azzerano il conteggio; la rimozione libera capienza soltanto nei quarti realmente liberati. Conservare questa invariante in dati persistenti, sincronizzazione multiplayer, pala, forni e futura ricetta speciale di crafting.

**Selezione delle zone tramite punto di clic — CONFERMATO:** trasformare il punto di impatto sulla superficie del blocco pizza in coordinate locali coerenti con `division_orientation`, quindi individuare **esattamente una zona logica** della configurazione `division_state` corrente. Quando il punto cade esattamente su una linea o incrocio, usare un **tie-break deterministico fisso**, identico client/server: mai selezionare due zone per lo stesso clic. Non confondere una singola zona logica (che puo' coprire piu' quarti se non suddivisa) con i quattro quarti fisici usati per contare ingredienti, memorizzare le coperture e gestire i rimborsi. Il criterio preciso (ordine degli ID locali/tolleranze) e' un dettaglio tecnico ancora da fissare; non altera la regola di gameplay approvata.

**Invariante delle riunioni:** la modifica di `division_state` NON cancella ne' rimappa automaticamente le coperture degli ingredienti gia' presenti, le loro posizioni o l'ordine delle applicazioni; cambia soltanto le zone selezionabili per le interazioni successive. I nuovi topping coprono **solo le parti libere da quello stesso ingrediente della zona selezionabile**. Un clic interamente duplicato non consuma alcun item. Anche una riunione non genera alcun rimborso.

**Invariante:** nessun item o condimento perso nel trasferimento **mondo -> pala occupata -> mondo/Pizza Oven -> pala**; la pala conserva direttamente una sola pizza con tutti i dati, senza creare un item pizza separato. **La pala non ha durabilita'**: i trasferimenti ripetuti non provocano usura dello strumento. E' inoltre **previsto in futuro un uso come arma ispirata alla padella di Farmer's Delight Refabricated, ma esclusivamente quando la pala e' vuota**. **Se contiene una pizza, non attiva le caratteristiche o gli effetti speciali da arma** e non deve mettere a rischio la preparazione; i dati persistenti determinano senza ambiguita' lo stato vuoto/occupato. Danni, effetti e velocita' della versione vuota sono ancora da progettare. Restano da progettare i trasferimenti con crafting/forni vanilla, la gestione di inventari e distruzione della pala. Il multiplayer deve sincronizzare lo stesso stato su client e server.

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

## Cottura e crafting dinamici: punti a rischio

- Una ricetta speciale da crafting potrebbe costruire una pizza cruda con gli stessi componenti della preparazione manuale.
- Il Pizza Oven può salvare internamente l'intero `ItemStack` e produrne una copia cotta con componenti preservati.
- **Fornace/affumicatore vanilla**: una normale ricetta di smelting produce un output statico; trasferire i dati dell'input richiede verifica approfondita e una integrazione mirata che **non** cambi le ricette di altre mod.
- Per le fette, se confermate, usare gli stessi ingredienti/nome e nutrizione frazionata con gestione coerente di arrotondamenti.

## Compatibilità con altre mod

- Usare tag condivisi **solo se semanticamente appropriati**; per ingredienti canonici come la mozzarella, definizioni specifiche possono essere necessarie. **Equivalenza cross-mod confermata nel comportamento**, non nella selezione di tag esatti: un gruppo logico condiviso impedisce doppioni e vale un unico tipo di topping per quarto, ma il rimborso conserva l'item originario.
- Possibilità di datapack esterni che aggiungano ingredienti e gusti.
- Fallback di rendering per item senza modelli pizza dedicati.
- In caso di rimozione di una mod dal modpack, caricare i vecchi salvataggi senza crash o duplicazioni, per quanto possibile.
- Documentare il comportamento JEI per gusti canonici e ingredienti dinamici senza promettere la visualizzazione di tutte le combinazioni teoriche.

## Test obbligatori

Persistenza del seed; compatibilità dei componenti durante crafting/cottura/taglio; blocco-item-blocco; localizzazione dei nomi; input da hopper; ingredienti rimossi dai datapack; sovrapposizione di tag; due giocatori sullo stesso blocco; chunk unload e ripristino.
