# Test pizza cruda persistente (item ↔ blocco) — Pizza Update 1.0.0

> **Fase 3C confermata funzionante dall'autore (14 ottobre 2026); nuove modifiche da provare.** La rottura di una base stesa restituisce ora impasto normale quando la pizza non e' attualmente divisa e non contiene alcun condimento, anche se in precedenza era stata divisa e poi riunita. Una base ancora divisa o con almeno un condimento restituisce invece `raw_pizza` con tutti i suoi dati. Il modello dell'item usa ora una texture originale 16×16.
> La suddivisione logica dei quarti è stata verificata dall'autore. In questa fase una pizza **completamente stesa ma ancora divisa oppure con condimenti** diventa un unico `italiansdelight:raw_pizza` con dati conservati; una pizza **intera senza condimenti** restituisce invece l'impasto Farmer's Delight. Un impasto agli stadi 0–3 restituisce ancora impasto di Farmer's Delight. Non sono ancora stati attivati i clic per aggiungere topping generici.

## Implementazione

- [PizzaItemData.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaItemData.java): record codec che salva la `PizzaComposition` completa e un flag separato per la mozzarella del prototipo preesistente.
- [ModDataComponents.java](../../src/main/java/dev/italiansdelight/common/registry/ModDataComponents.java): componente item persistente `italiansdelight:pizza_contents`, registrato prima degli item.
- [ModItems.java](../../src/main/java/dev/italiansdelight/common/registry/ModItems.java): `raw_pizza` non impilabile (massimo 1) e non commestibile.
- [PizzaDoughBlock.java](../../src/main/java/dev/italiansdelight/common/block/PizzaDoughBlock.java): drop singolo e copia con Pick Block; il loot standard rimane per gli impasti incompleti. La pizza completa rispetta il fatto che la base sia sopravvissuta alle normali condizioni del loot table.
- [PizzaDoughPlacement.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaDoughPlacement.java): ricolloca sul terreno la base già completamente stesa e ripristina la BlockEntity. Non consuma l'item se il posizionamento fallisce.
- [Texture personalizzata 16×16](../../src/main/resources/assets/italiansdelight/textures/item/raw_pizza.png) e [modello item](../../src/main/resources/assets/italiansdelight/models/item/raw_pizza.json): nuova icona originale della pizza cruda in pixel art; nessuna texture preesistente alterata.

## 1. Build

In PowerShell:

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Testare in un **mondo di prova**, preferibilmente con backup, non su una partita importante.

## 2. Pizza completamente stesa: mondo → item → mondo

1. Posizionare un impasto FDR sul terreno e stenderlo **fino allo stadio 4**.
2. Col mattarello dividere la pizza in due metà e successivamente suddividere **solo la sinistra**: con `/data get block X Y Z` verificare `PizzaComposition.division_state = 3` e il corretto orientamento salvato.
3. In Sopravvivenza, rompere il blocco. **Atteso:** un solo item **Pizza cruda** (`italiansdelight:raw_pizza`), non impasto più ingredienti separati.
4. Posizionare l'item con **clic destro sulla faccia superiore** di un blocco idoneo. **Atteso:** una base di pizza già completamente stesa, non una pallina di impasto.
5. Ripetere `/data get block X Y Z` sulle nuove coordinate. **Atteso:** `division_state = 3` e orientamento identico. L'elenco delle applicazioni resta presente e invariato (attualmente vuoto finché i clic sui topping non vengono attivati).
6. Ripetere un secondo ciclo di rottura/posizionamento: nessun nuovo item, nessuna perdita o moltiplicazione dei dati.

## 3. Inventario, Q e ricaricamento

1. Raccogliere la pizza cruda; premere **Q** per lasciarla a terra, quindi raccoglierla di nuovo. Non deve perdere la sua composizione.
2. A pizza droppata, se servono dettagli, osservare l'entità più vicina con `/data get entity @e[type=minecraft:item,sort=nearest,limit=1]` (attenzione ad altri item vicini): la sezione dei componenti deve contenere `italiansdelight:pizza_contents`.
3. Uscire dal mondo con l'item in inventario, rientrare e riposizionarlo. Verificare che divisione e orientamento siano identici.
4. L'item **non è commestibile** e **non deve potersi impilare**. Si trova anche nel gruppo Creativa della mod per facilitare il test.
5. Il suo normale despawn e la distruzione ambientale come item seguono il comportamento vanilla; il contenuto non produce drop separati.

## 4. Compatibilità con il vecchio prototipo mozzarella

1. Stendere un impasto, aggiungere il vecchio item intero **`italiansdelight:mozzarella`** tramite il clic provvisorio già esistente.
2. Rompere la pizza completamente stesa. **Atteso:** soltanto `raw_pizza`, **non** un drop separato di mozzarella.
3. Rimettere l'item sul terreno: il topping del prototipo deve riapparire, perché il suo booleano `legacy_mozzarella` viene conservato nel componente.
4. Rompere nuovamente: sempre un solo item, senza duplicazioni.

**Attenzione:** il prototipo mozzarella a booleano rimane separato dagli ingredienti della `PizzaComposition`. Non è ancora il sistema di condimenti finale; la sua migrazione dovrà essere gestita quando verrà sostituito.

## 5. Regressioni e casi negativi

- Rompere un impasto **agli stadi 0, 1, 2 e 3**: deve continuare a restituire `farmersdelight:wheat_dough`, non `raw_pizza`.
- Rompere una base **stadio 4 mai divisa e senza topping**: deve restituire **un solo impasto FDR**, non pizza cruda.
- Dividere la base stadio 4 in metà (stato 1) oppure quarti (3, 5 o 7), **senza alcun condimento**, e romperla **mentre e' ancora divisa**: deve restituire **un solo `raw_pizza`** che conserva orientamento e suddivisione.
- Dividere, poi riunire con Shift + mattarello fino a `division_state = 0`, **senza avere applicato alcun topping**, infine rompere: deve tornare **un solo `farmersdelight:wheat_dough`**.
- Provare una pizza stesa con `division_state = 0` ma con la **mozzarella del prototipo**: deve restituire **`raw_pizza`**, preservare l'ingrediente e non dropparlo separatamente.
- In futuro, con le applicazioni generiche attive, una pizza **intera ma ancora con ingredienti** dovrà sempre dare `raw_pizza`; una pizza con tutte le applicazioni eliminate, riunita a intera, tornerà a dare il normale impasto.
- Usare **Pick Block** sugli stessi casi: deve restituire impasto negli stati vuoti/interi e l'item pizza quando ci sono divisioni o condimenti.
- Controllare l'inventario: `raw_pizza` deve avere l'icona pixel art **16×16** dedicata, non più la facciata del blocco.
- Tentare di posizionare la pizza in una cella occupata o senza supporto compatibile: non deve consumare l'item.
- Il modello della pizza completamente stesa deve mantenere il **bordo di 1 pixel**; divisioni e riunioni mantengono il comportamento già testato.
- Verificare le normali rotture del supporto e le esplosioni su un mondo di prova. In presenza di esplosione, il drop resta soggetto alla normale sopravvivenza della base; non è garantita la comparsa dell'item.
- In Creativa il comportamento della distruzione dei blocchi segue le regole vanilla e non costituisce un test del drop da Sopravvivenza.
- Non creare ancora pizza cotta, fetta, pala o ricette automatiche basandosi sul solo `raw_pizza`: queste meccaniche arriveranno dopo.

## Risultato atteso per questa fase

**Un ciclo mondo → inventario → Q → inventario → mondo → salvataggio/caricamento che preserva la composizione quando la pizza e' ancora divisa o condita. Se la pizza torna intera e senza condimenti, il drop si converte in normale impasto.** Una volta verificato, si potrà collegare `PizzaCompositionOperations` ai clic con ingredienti approvati e implementare le regole di prelievo/rimborso senza rischiare di perdere ingredienti alla rottura del blocco.

## Test supplementare — distruzione del supporto (fix pre-release)

- [ ] Sotto impasto non ancora steso o base vuota riunita, rimuovere il sostegno: drop di un solo Wheat Dough.
- [ ] Sotto pizza cruda con ingredienti/divisioni, rimuovere il sostegno: drop di un solo `raw_pizza` con componente `pizza_contents` completo, senza duplicazioni.
- [ ] Riprovare il ciclo mondo → item → mondo e verificare che i quattro quarti e le applicazioni siano preservati.

Questo intervento cambia solo il percorso di rottura per supporto diventato invalido: le esplosioni e i normali colpi sulla pizza mantengono le rispettive regole vanilla.
