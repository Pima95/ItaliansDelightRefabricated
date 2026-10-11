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
- elenco degli ingredienti reali (ID, quantità, eventuali proprietà necessarie);
- identificativo distinto di ogni applicazione e maschere dei quarti originari/ancora coperti (requisito confermato); **ordine cronologico persistente di inserimento**, necessario per rimuovere con Shift + clic destro a mano vuota **l'ultima applicazione ancora presente nella zona selezionata**;
- **vincolo univoco per ingrediente e quarto fisico**: prima di applicare, sottrarre la copertura gia' occupata dallo stesso ingrediente; salvare solo la maschera residua libera, consumare **1 item per applicazione non vuota**, nessun consumo e nessuna nuova applicazione per maschera residua vuota;
- **vincolo salse**: nessuna salsa richiesta per una pizza valida; al massimo una salsa **di qualunque tipo** per singola sottoarea fisica. Le aree senza salsa sono valide, mentre salse di tipo diverso non possono occupare insieme lo stesso quarto. Non confondere il requisito opzionale di una salsa per il **matching di un gusto specifico** con un requisito di preparazione generale;
- stato crudo/cotto o progresso pertinente;
- `render_seed` e parametri di disposizione che non cambino raccogliendo la pizza;
- informazioni per calcolare nutrizione, nome e tooltip, evitando stringhe ridondanti già tradotte.

**Decisione aggiornata:** la pizza e' **inizialmente intera** e si divide **solo su richiesta col mattarello**, prima in due meta', poi eventualmente in quarti; il rendering mostra le linee solo se si tiene mattarello o topping valido e non modifica modelli/texture. I 4 quarti possono essere **un riferimento interno** per mappare ogni possibile copertura, ma la configurazione reale della divisione deve essere persistita. Ogni applicazione di topping conserva la provenienza (item/quantita' realmente consumata), i quarti originari e quelli ancora coperti. Il rimborso avviene **una sola volta**, solo quando tutti i quarti dell'applicazione sono stati liberati. Una pizza cruda rotta restituisce impasto e ingredienti ancora associati; una pizza cotta non viene scomposta nei vecchi ingredienti. Specifiche complete: [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

**Invariante delle riunioni:** la modifica di `division_state` NON cancella ne' rimappa automaticamente le coperture degli ingredienti gia' presenti, le loro posizioni o l'ordine delle applicazioni; cambia soltanto le zone selezionabili per le interazioni successive. I nuovi topping coprono **solo le parti libere da quello stesso ingrediente della zona selezionabile**. Un clic interamente duplicato non consuma alcun item. Anche una riunione non genera alcun rimborso.

**Invariante:** nessun item o condimento perso nel trasferimento **mondo -> pala occupata -> mondo/Pizza Oven -> pala**; la pala conserva direttamente una sola pizza con tutti i dati, senza creare un item pizza separato. Restano da progettare i trasferimenti con crafting/forni vanilla, la gestione di inventari e distruzione della pala. Il multiplayer deve sincronizzare lo stesso stato su client e server.

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

- Usare tag condivisi **solo se semanticamente appropriati**; per ingredienti canonici come la mozzarella, definizioni specifiche possono essere necessarie.
- Possibilità di datapack esterni che aggiungano ingredienti e gusti.
- Fallback di rendering per item senza modelli pizza dedicati.
- In caso di rimozione di una mod dal modpack, caricare i vecchi salvataggi senza crash o duplicazioni, per quanto possibile.
- Documentare il comportamento JEI per gusti canonici e ingredienti dinamici senza promettere la visualizzazione di tutte le combinazioni teoriche.

## Test obbligatori

Persistenza del seed; compatibilità dei componenti durante crafting/cottura/taglio; blocco-item-blocco; localizzazione dei nomi; input da hopper; ingredienti rimossi dai datapack; sovrapposizione di tag; due giocatori sullo stesso blocco; chunk unload e ripristino.
