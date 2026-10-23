# Mappatura delle identita' canoniche — Pizza Update 1.0.0

> **DECISIONE CONFERMATA:** mapping esterni verificati per ID singolo, un file JSON minimo con soli `item` e `canonical`, senza override degli ID ufficiali. **CODICE AGGIUNTO:** registro delle 31 identità/33 ID e loader JSON/cache `PizzaToppingMappings` nel branch `feature/pizza-1.0.0`. **Il loader è ancora da compilare/testare con Minecraft 26.2** e non modifica i clic dei topping.

## Obiettivi e definizioni

- **Tipo canonico** = un ingrediente distinto per il limite di sei per quarto, antiduplicazione, nomi, matching dei 19 gusti e nutrizione. **Non** coincide necessariamente con un singolo item: bacon crudo e cotto hanno lo stesso tipo.
- **Item originario** = l'ItemStack esatto consumato per ogni applicazione, necessario a restituire la variante originale e gestire gli eventuali contenitori. Non sostituirlo con l'ID canonico per i rimborsi.
- **Categoria nutrizionale** e **ruolo fisico** (salsa, crema, topping solido) sono attributi separati: due tipi possono condividere un bonus alimentare senza essere equivalenti.
- I nomi di identita' canonica `italiansdelight:pizza_toppings/<slug>` sono **ora adottati nel registro Java** come identificatori logici (NON item registrati), e devono rimanere stabili per salvataggi e mapping futuri.
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

| Tag presente | Contenuto verificato nel repository | Valutazione per ricognizione (mai ammissione automatica) |
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

**Regola di ammissione CONFERMATA:** i 33 ID ufficiali sono mappati esplicitamente. Gli item di mod esterne vengono ammessi soltanto con **ID puntuale verificato** che indica una delle identità canoniche già approvate; un tag anche specifico non abilita nuovi membri. Il tag è una fonte per la ricognizione, non un selettore del sistema. Un datapack personalizzato può richiedere esplicitamente mapping di nuovi ID, ma la sua attendibilità dipende dal creatore del pack: non è automaticamente una compatibilità verificata dalla mod.

## 5. Risoluzione delle equivalenze — mapping puntuale e JSON minimo CONFERMATI (13 ottobre 2026)

**Regola confermata e presente nel catalogo Java:** i 33 item registrati di Italian's Delight e Farmer's Delight sono assegnati esplicitamente alle 29 identità già disponibili; i due nuovi item vegetali entreranno dopo la loro registrazione. **Un item di una mod esterna è ammesso solo attraverso una corrispondenza verificata del suo ID esatto** a una delle 31 identità. I tag sono utili per individuare possibili equivalenze, ma non sono selettori di compatibilità a runtime.

**Ordine di validazione previsto:**

1. Whitelist base autorevole e protezione delle esclusioni (item interi, non porzionati, non ammessi); nessun datapack di equivalenze modifica i tipi approvati o riassegna un ID nativo.
2. Verifica delle mappature opzionali **per singolo ID item**, con identità canonica di destinazione già esistente e porzione idonea. Nessuna espansione automatica dei tag `c:`.
3. **Conflitti ed override — CONFERMATO:** nessuna definizione di datapack può cambiare le associazioni ufficiali degli ID nativi o riassegnare un tipo canonico. Un file che tenta l'override viene ignorato e segnalato. Per un ID esterno assegnato da due file a identità differenti, **rifiutare le definizioni in conflitto di quell'ID**, lasciando valide tutte le altre; produrre diagnostica. Mapping multipli identici non producono doppioni. Se la mod esterna opzionale non è installata, saltare l'ID senza rendere obbligatoria la dipendenza. I datapack personalizzati dichiarano intenzionalmente un ID, ma non sono verifiche ufficiali del suo contenuto.
4. Cache immutabile `Map<Item, CanonicalToppingDefinition>` ricostruita dopo il caricamento del mondo e a `/reload`; lookup diretto ai clic, **nessuna scansione tag/lettura JSON a ogni interazione e nessun tick dedicato**.
5. Persistenza di ID canonico, applicazioni e **ItemStack originale** per i rimborsi; niente mutazione automatica delle pizze già esistenti durante il reload.

### Schema minimale di datapack — APPROVATO (13 ottobre 2026), NON IMPLEMENTATO

**Un file JSON per ciascuna equivalenza**, con **esattamente due proprietà obbligatorie**: `item` (ID registrato dell'oggetto concreto) e `canonical` (identità canonica già ammessa). Nessun campo aggiuntivo per categoria nutrizionale, ruolo, render o nome: le informazioni sono definite una sola volta dal tipo canonico, e il singolo datapack non può alterarle. Invalidità di campi, ID o identità -> scartare quel file e riportare l'errore di caricamento. Esempio con un item di Farmer's Delight **reale e già approvato**, riportato unicamente per illustrare la sintassi:

```json
{
  "item": "farmersdelight:cooked_bacon",
  "canonical": "italiansdelight:pizza_toppings/bacon"
}
```

L'ID `italiansdelight:pizza_toppings/bacon` è una **chiave canonica proposta**, non un item del registro. Esempio **interamente fittizio** di futuro sostituto esterno:

```json
{
  "item": "examplemod:mozzarella_slices",
  "canonical": "italiansdelight:pizza_toppings/mozzarella_slice"
}
```

**Percorso effettivamente utilizzato dal nuovo reload listener:** `data/<namespace>/italiansdelight/pizza_equivalences/<file>.json`. I datapack possono mettere file in qualsiasi namespace; ogni file contiene una sola corrispondenza. Il listener si registra in `DataResourceLoader`, legge al caricamento mondo o `/reload`, non ai tick.

**Implementazione nel branch (DA TESTARE):** scansione degli stack di datapack, anche se sovrappongono lo stesso file; JSON rigoroso, protezione delle associazioni native e degli item notoriamente esclusi, item di mod opzionali non presenti ignorati, conflitti esterni isolati e loggati, cache `Map<Item, PizzaToppingType>` aggiornata atomicamente. **Nessuna compatibilità non verificata installata per default**; resta da collegare la cache ai clic sulle pizze. [Test manuali](DATAPACK_RELOAD_TESTS.md).


## 6. Test di accettazione futuri

- **Registro statico: verifica di struttura già eseguita** (31 identità, 33 ID, 4 coppie condivise, 2 pianificate senza mapping item, nessun duplicato, esclusioni non registrate). **Da testare in una build Java 25/MC 26.2 e in gioco:** registrazione effettiva degli item e riconoscimento runtime nel nuovo sistema.
- Bacon crudo/cotto, pollo a pezzi crudo/cotto, merluzzo a fette crudo/cotto, salmone a fette crudo/cotto: secondo clic con la variante equivalente sullo stesso quarto **non** consuma item; su quarti liberi si applica una sola volta e il recupero restituisce **la variante realmente usata**.
- Mozzarella a fette ≠ mozzarella intera; Parmigiano grattugiato ≠ spicchio; `farmersdelight:cabbage_leaf` ≠ cavolo intero; carne macinata ≠ bistecca. Un item non ammesso non si aggiunge e non consuma nulla.
- `#c:foods/raw_chicken` e tag analoghi non rendono legali item vanilla interi; `#c:foods/cheese` non fa equivalere formaggi differenti.
- Item di altre mod **mappato e verificato** come una delle 31 identita' -> valido e antiduplicato con la variante ufficiale. Un item ambiguo tra due identita' -> rifiutato senza consumo.
- Il nome della pizza Margherita e dei singoli spicchi usa la composizione **canonica**: la sostituzione equivalente approvata conserva il riconoscimento, ma un formaggio diverso non ottiene il nome per errore.
- Valori nutrizionali invariati: categorie distinte una sola volta per quarto; max sei tipi, salsa inclusa; nessun doppio rimborso.
- Verificare il JSON minimo (due proprietà soltanto), l'ignorare mapping che sovrascrivono ID ufficiali, la collisione tra due mapping discordi su un solo item esterno senza disattivare gli altri, una mod opzionale assente, e il rifiuto di identità inesistenti.
- Verificare reload, salvataggio, trasferimento con pala/forno, inventario pieno, multiplayer e server dedicato; confrontare il costo della cache con un mapping Java diretto, senza millantare risultati prima dei benchmark.

## Da confermare prima del codice

- **DECISO (13 ottobre 2026):** ogni item esterno necessita di **mapping puntuale verificato per ID**; i tag `c:` non abilitano mai automaticamente nuovi membri. Non riproporre la scelta fra questa regola e l'ammissione automatica.
- **Eventuali identita' incrociate fra le due mod:** mantenere separati per default bacon FDR e pancetta Italian's Delight (cosi' come tutti gli altri topping), a meno di approvazione esplicita di un accorpamento.
- **DECISO:** schema JSON di equivalenza con esattamente `item` e `canonical`, un file per mapping; **associazioni ufficiali non sovrascrivibili**, collisioni fra identità esterne diverse rifiutate solo per l'ID ambiguo e segnalate.
- **GIÀ IMPLEMENTATO:** registro Java read-only dei 31 tipi e mapping statico dei 33 item esistenti, senza tick né cambi di comportamento. [PizzaToppingCatalog.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingCatalog.java), [PizzaToppingType.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingType.java).
- **GIA' NEL CODICE (DA TESTARE):** percorso JSON, parser, listener reload, cache di lookup. [PizzaToppingMappings.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingMappings.java).
- **DA IMPLEMENTARE/VALIDARE:** clic sui topping, data component e BlockEntity, item nuovi per le verdure, test Java 25 e benchmark.

**Fonti ufficiali del catalogo e delle regole confermate:** [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md), [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md), [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md), [NAMED_PIZZAS.md](NAMED_PIZZAS.md).
