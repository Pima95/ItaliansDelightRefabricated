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
- elenco degli ingredienti reali (ID, quantità, eventuali proprietà necessarie);
- ordine di inserimento, se si consentirà di rimuovere l'ultimo topping;
- stato crudo/cotto o progresso pertinente;
- `render_seed` e parametri di disposizione che non cambino raccogliendo la pizza;
- informazioni per calcolare nutrizione, nome e tooltip, evitando stringhe ridondanti già tradotte.

**Invariante:** nessun item o condimento perso durante trasformazione mondo -> item -> forno -> item -> mondo. Il multiplayer deve sincronizzare lo stesso stato su client e server.

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

La definizione definitiva dovrà prevedere alternative via tag, gestione delle quantità, priorità e traduzioni. Non confondere **riconoscimento** dei gusti con limitazione al crafting.

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
