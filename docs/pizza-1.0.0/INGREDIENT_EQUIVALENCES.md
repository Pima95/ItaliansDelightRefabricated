# Mappatura delle identita' canoniche — Pizza Update 1.0.0

> **Bozza tecnica proposta il 13 ottobre 2026, NON ancora implementata né definitivamente approvata come formato JSON.** Il catalogo dei **31 tipi di condimento** e la regola delle quattro coppie crudo/cotto condivise sono invece decisioni **gia' confermate**. Questo documento traduce quelle decisioni in una mappatura concreta, da verificare in gioco. Non crea item, tag o compatibilita' eseguibili.

## Obiettivi e definizioni

- **Tipo canonico** = un ingrediente distinto per il limite di sei per quarto, antiduplicazione, nomi, matching dei 19 gusti e nutrizione. **Non** coincide necessariamente con un singolo item: bacon crudo e cotto hanno lo stesso tipo.
- **Item originario** = l'ItemStack esatto consumato per ogni applicazione, necessario a restituire la variante originale e gestire gli eventuali contenitori. Non sostituirlo con l'ID canonico per i rimborsi.
- **Categoria nutrizionale** e **ruolo fisico** (salsa, crema, topping solido) sono attributi separati: due tipi possono condividere un bonus alimentare senza essere equivalenti.
- I nomi di identita' canonica `italiansdelight:pizza_toppings/<slug>` qui elencati sono **ID tecnici proposti**, **non ID di item gia' registrati**. Possono essere rinominati in implementazione senza alterare l'insieme dei 31 tipi approvati.
- Le equivalenze esterne **possono soltanto scegliere una delle 31 identita' esistenti**. Nessun nuovo tipo di topping viene approvato attraverso JSON o un tag generico.

## 1. Italian's Delight — 20 tipi distinti (ID ufficiali gia' registrati)

Tutti gli ID nella colonna di destra hanno namespace `italiansdelight:`. Ogni riga corrisponde a **una identita' distinta**, senza accorpamenti automatici.

| Chiave canonica proposta (suffisso) | Item ufficiale (suffisso) | Categoria / ruolo |
| --- | --- | --- |
| `mozzarella_slice` | `mozzarella_slice` | Latticini / solido |
| `bocconcino` | `bocconcino` | Latticini / solido |
| `grated_parmigiano_reggiano` | `grated_parmigiano_reggiano` | Latticini / grattugiato |
| `grated_pecorino_romano` | `grated_pecorino_romano` | Latticini / grattugiato |
| `gorgonzola_wedge` | `gorgonzola_wedge` | Latticini / solido |
| `provolone_slice` | `provolone_slice` | Latticini / solido |
| `scamorza_slice` | `scamorza_slice` | Latticini / solido |
| `smoked_scamorza_slice` | `smoked_scamorza_slice` | Latticini / solido |
| `prosciutto_crudo_slice` | `prosciutto_crudo_slice` | Proteine animali / solido |
| `salame_slice` | `salame_slice` | Proteine animali / solido |
| `mortadella_slice` | `mortadella_slice` | Proteine animali / solido |
| `pancetta_slice` | `pancetta_slice` | Proteine animali / solido |
| `guanciale_slice` | `guanciale_slice` | Proteine animali / solido |
| `bresaola_slice` | `bresaola_slice` | Proteine animali / solido |
| `coppa_slice` | `coppa_slice` | Proteine animali / solido |
| `speck_slice` | `speck_slice` | Proteine animali / solido |
| `tomato_slice` | `tomato_slice` | Vegetali / solido |
| `basil` | `basil` | Erbe / solido |
| `cream_bowl` | `cream_bowl` | Latticini / **cremoso NON salsa** |
| `ricotta` | `ricotta` | Latticini / **cremoso NON salsa** |

**Distinzioni rilevanti:** panna e ricotta sono identita' diverse; grattugiato e spicchio intero di Parmigiano non sono equivalenti (lo spicchio e' escluso); scamorza normale e affumicata restano due tipi distinti; salumi differenti rimangono diversi anche se sono riuniti in `#italiansdelight:cured_meat_slices`. `italiansdelight:mozzarella` **intera** non e' sostituto di `mozzarella_slice`.

## 2. Farmer's Delight Refabricated — 9 tipi / 13 item ID ufficiali

Tutti gli ID item nella colonna centrale hanno namespace `farmersdelight:`. Le **quattro coppie crude/cotte** condividono ciascuna **una sola chiave canonica**. Ogni coppia e' diversa dalle altre.

| Chiave canonica proposta (suffisso) | Item ammessi (suffisso) | Categoria / ruolo |
| --- | --- | --- |
| `bacon` | `bacon`, `cooked_bacon` | Proteine animali / solido |
| `chicken_cuts` | `chicken_cuts`, `cooked_chicken_cuts` | Proteine animali / solido |
| `cod_slice` | `cod_slice`, `cooked_cod_slice` | Proteine animali / solido |
| `salmon_slice` | `salmon_slice`, `cooked_salmon_slice` | Proteine animali / solido |
| `fried_egg` | `fried_egg` | Proteine animali / solido |
| `minced_beef` | `minced_beef` | Proteine animali / solido |
| `cabbage_leaf` | `cabbage_leaf` | Vegetali / solido |
| `pumpkin_slice` | `pumpkin_slice` | Vegetali / solido |
| `tomato_sauce` | `tomato_sauce` | Salse / **salsa** |

**Scelta conservativa di questa bozza:** `farmersdelight:bacon` **non** e' automaticamente la stessa identita' di `italiansdelight:pancetta_slice`; `cabbage_leaf` non e' `cabbage`; `minced_beef` non e' `minecraft:beef`. Un eventuale accorpamento tra **tipi gia' approvati ma diversi** richiede una decisione esplicita, altrimenti potrebbe cambiare i gusti riconosciuti, i limiti e i rimborsi.

## 3. Italian's Delight — 2 nuovi tipi APPROVATI, item da creare

| Chiave canonica proposta | Item previsto, **non registrato** | Attivazione |
| --- | --- | --- |
| `italiansdelight:pizza_toppings/carrot_slice` | fette di carota (ID definitivo da scegliere) | solo dopo nuova registrazione e ricetta Cutting Board 1 carota -> 4 fette |
| `italiansdelight:pizza_toppings/potato_slice` | fette di patata (ID definitivo da scegliere) | solo dopo nuova registrazione e ricetta Cutting Board 1 patata -> 4 fette |

Categoria Vegetali, ruolo solido. `minecraft:carrot` e `minecraft:potato` interi sono **sempre esclusi**. Entrambe le fette possono essere aggiunte crude e cuocere con la pizza; nessun item cotto distinto richiesto.

**Controllo conteggi:** 20 identita' Italian's Delight + 9 Farmer's Delight + 2 nuove = **31 identita' canoniche**; 20 + 13 = **33 ID attuali**, piu' 2 futuri.

## 4. Tag effettivamente verificati — non confondere categoria ed equivalenza

Risorse controllate sui rami `feature/pizza-1.0.0` e `fabric/latest/26.2`:

| Tag presente | Contenuto verificato nel repository | Valutazione per equivalenze automatiche |
| --- | --- | --- |
| `#c:crops/basil` (Italian's Delight) | `italiansdelight:basil` | **Candidato** specifico per il basilico; membri futuri esterni da verificare |
| `#c:foods/raw_bacon` (FDR) | `farmersdelight:bacon` | **Candidato** per variante bacon cruda, non approvazione indiscriminata di nuovi membri |
| `#c:foods/cooked_bacon` (FDR) | `farmersdelight:cooked_bacon` | **Candidato** per variante bacon cotta, stessa identita' canonica del crudo |
| `#c:foods/raw_chicken` (FDR) | `minecraft:chicken`, `farmersdelight:chicken_cuts` | **NO** mapping diretto: contiene pollo intero |
| `#c:foods/cooked_chicken` (FDR) | `minecraft:cooked_chicken`, `farmersdelight:cooked_chicken_cuts` | **NO** mapping diretto: contiene pollo intero |
| `#c:foods/raw_cod` / `#c:foods/cooked_cod` (FDR) | merluzzo vanilla intero + fette FDR | **NO** mapping diretto |
| `#c:foods/raw_salmon` / `#c:foods/cooked_salmon` (FDR) | salmone vanilla intero + fette FDR | **NO** mapping diretto |
| `#c:foods/raw_beef` (FDR) | `minecraft:beef` + `farmersdelight:minced_beef` | **NO** mapping diretto |
| `#c:foods/cabbage` (FDR) | cavolo intero + foglia di cavolo | **NO** mapping diretto |
| `#c:foods/cooked_egg` (FDR) | `farmersdelight:fried_egg` nella sorgente controllata | Non abilitare automaticamente uova cotte di altre mod senza verificarne forma/tipo |
| `#c:foods/cheese` (Italian's Delight) | include mozzarella intera, fette, forme/esclusi e tipi diversi | **NO** mapping diretto: categoria troppo ampia |
| `#italiansdelight:cured_meat_slices` | otto salumi a fette differenti | **NO** come identita' unica: salumi diversi non sono lo stesso topping |

**Riferimenti:** [basil](../../src/main/resources/data/c/tags/item/crops/basil.json), [formaggi](../../src/main/resources/data/c/tags/item/foods/cheese.json), [salumi affettati](../../src/main/resources/data/italiansdelight/tags/item/cured_meat_slices.json), [tag FDR generati](https://github.com/MehVahdJukaar/FarmersDelightRefabricated/tree/fabric/latest/26.2/src/generated/resources/data/c/tags/item/foods).

**Regola prudente proposta:** gli ID dei 33 item ufficiali sono **sempre mappati esplicitamente**; i tag possono suggerire equivalenze per altri item **solo** se il tag e il singolo item concreto sono stati verificati come stesso tipo e dimensione plausibile. L'appartenenza a un tag, da sola, non e' una prova automatica di preparazione adeguata. Gli item non verificati rimangono non applicabili, senza perdere la possibilita' di aggiungere in futuro equivalenze tramite datapack.

## 5. Ordine di risoluzione — formato tecnico PROPOSTO

1. **Esclusioni assolute** (item interi, non porzionati, non appartenenti al catalogo o un suo sostituto verificato) e verifica della forma reale. Non permettere ai tag di aggirare il controllo.
2. **Corrispondenza puntuale per ID** ai tipi canonici approvati; hanno precedenza sui selettori a tag.
3. **Tag specifici e verificati** come sorgente di candidati cross-mod; accettare soltanto membri esplicitamente verificati in forma idonea. Un tag generico non aggiunge nuovi tipi.
4. **Conflitti non risolti** fra identita' distinte: rifiutare l'item ambiguo e loggare un avviso di configurazione, invece di scegliere arbitrariamente per ordine di caricamento.
5. **Lookup ottimizzato**: dopo avvio del mondo/`/reload`, compilare una `Map<Item, CanonicalToppingDefinition>` immutabile e usarla ai clic. Nessun parsing di JSON, scansione tag, allocazione periodica o controllo per tick.
6. **Persistenza:** mantenere un ID canonico per l'antiduplicato e l'ItemStack originale per rimborsi e remainder. Non mutare la composizione o i dati di una pizza gia' esistente in risposta a un semplice reload senza una migrazione esplicita.

### Esempio di schema di mapping — SOLO ILLUSTRATIVO, NON ESEGUIBILE

```json
{
  "canonical": "italiansdelight:pizza_toppings/bacon",
  "items": [
    "farmersdelight:bacon",
    "farmersdelight:cooked_bacon"
  ],
  "category": "animal_protein",
  "role": "solid_topping"
}
```

Un eventuale file che aggiunge un **sostituto di un'altra mod** dovra' referenziare una delle identita' canoniche sopra e identificare un item verificato, senza poter creare per errore un nuovo topping ufficiale. Nome e posizione del file nel datapack, serializzazione, validazione di tag, priorita' fra datapack e gestione degli aggiornamenti rimangono **dettagli di implementazione da approvare**.

## 6. Test di accettazione futuri

- Ciascuno dei 33 ID esistenti viene associato a uno e un solo tipo canonico; 2 tipi nuovi restano disattivati prima della registrazione dei nuovi item.
- Bacon crudo/cotto, pollo a pezzi crudo/cotto, merluzzo a fette crudo/cotto, salmone a fette crudo/cotto: secondo clic con la variante equivalente sullo stesso quarto **non** consuma item; su quarti liberi si applica una sola volta e il recupero restituisce **la variante realmente usata**.
- Mozzarella a fette ≠ mozzarella intera; Parmigiano grattugiato ≠ spicchio; `farmersdelight:cabbage_leaf` ≠ cavolo intero; carne macinata ≠ bistecca. Un item non ammesso non si aggiunge e non consuma nulla.
- `#c:foods/raw_chicken` e tag analoghi non rendono legali item vanilla interi; `#c:foods/cheese` non fa equivalere formaggi differenti.
- Item di altre mod **mappato e verificato** come una delle 31 identita' -> valido e antiduplicato con la variante ufficiale. Un item ambiguo tra due identita' -> rifiutato senza consumo.
- Il nome della pizza Margherita e dei singoli spicchi usa la composizione **canonica**: la sostituzione equivalente approvata conserva il riconoscimento, ma un formaggio diverso non ottiene il nome per errore.
- Valori nutrizionali invariati: categorie distinte una sola volta per quarto; max sei tipi, salsa inclusa; nessun doppio rimborso.
- Verificare reload, salvataggio, trasferimento con pala/forno, inventario pieno, multiplayer e server dedicato; confrontare il costo della cache con un mapping Java diretto, senza millantare risultati prima dei benchmark.

## Da confermare prima del codice

- **Compatibilita' esterna:** abilitazione automatica per i membri di tag specifici anche quando un'altra mod aggiunge un nuovo item, oppure richiesta di una **mappatura puntuale verificata** per ogni nuovo item esterno. La seconda e' la scelta prudente qui raccomandata perché un tag non prova la dimensione fisica.
- **Eventuali identita' incrociate fra le due mod:** mantenere separati per default bacon FDR e pancetta Italian's Delight (cosi' come tutti gli altri topping), a meno di approvazione esplicita di un accorpamento.
- Formato definitivo dei JSON, gestione dei conflitti di due datapack, scelta dei nomi degli ID dei nuovi item vegetali e test di benchmark.

**Fonti ufficiali del catalogo e delle regole confermate:** [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md), [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md), [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md), [NAMED_PIZZAS.md](NAMED_PIZZAS.md).
