# Italian's Delight — Design formaggi F2

Documento di lavoro del branch `feature/cheeses`.

Questo file raccoglie soltanto le decisioni già concordate per la fase F2.
I dettagli ancora da scegliere restano esplicitamente aperti, così il design
può evolvere senza trasformare proposte non approvate in requisiti.

## 1. Prodotti caseari concordati

La selezione comprende:

1. Mozzarella
2. Bocconcini
3. Ricotta
4. Parmigiano Reggiano
5. Pecorino Romano
6. Gorgonzola
7. Provolone
8. Scamorza
9. Burrata
10. Mascarpone

La mozzarella è già presente nel branch; gli altri prodotti verranno introdotti
nelle fasi successive.

## 2. Processi produttivi concordati

| Prodotto | Processo generale |
|---|---|
| Mozzarella | Latte + caglio → cagliata; cagliata + sale → mozzarella; porzionatura al Cutting Board |
| Bocconcini | Cagliata + acqua normale → **2 bocconcini** nella Cheese Vat; nessun contenitore finale |
| Ricotta | Siero + latte → ricotta nella Cheese Vat, con ciotola come contenitore finale |
| Parmigiano Reggiano | Latte vaccino + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato |
| Pecorino Romano | Latte di pecora + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato |
| Gorgonzola | Cagliata + sale + coltura erborinata → forma fresca → stagionatura |
| Provolone | Cagliata + sale + acqua → forma fresca → stagionatura → fette |
| Scamorza | Cagliata + sale → scamorza fresca usando un laccio nello slot contenitore; breve asciugatura e possibile variante affumicata |
| Burrata | Mozzarella + panna → burrata; nessuna stagionatura |
| Mascarpone | Panna + aceto di mele → mascarpone nella Cheese Vat, confezionato con una ciotola separata nello slot contenitore |

Per i bocconcini si usa acqua normale. Non viene introdotto alcun item
"acqua calda".

## 3. Nuovi ingredienti/intermedi concordati

### Siero di latte

Il siero è una risorsa liquida prodotta dalla Cheese Vat durante le lavorazioni
che lo prevedono.

Formati previsti:

- serbatoio interno della Cheese Vat;
- bottiglia di siero;
- secchio di siero;
- siero contenuto in un calderone.

Gli identificatori implementati sono:

- `italiansdelight:whey_bottle`;
- `italiansdelight:whey_bucket`.

La bottiglia rappresenta **250 mB** e il secchio **1000 mB**.

### Latte di pecora

Sono previsti entrambi:

- `italiansdelight:sheep_milk_bucket`
- `italiansdelight:sheep_milk_bottle`

La bottiglia di latte di pecora deve essere l'equivalente della Milk Bottle di
Farmer's Delight Refabricated. Sia il secchio sia la bottiglia devono essere
compatibili con il tag del latte usato dalla Cheese Vat
(`#c:drinks/milk`), così possono essere impiegati nelle lavorazioni generiche
come cagliata e panna.

Il latte di pecora resta comunque la materia prima caratterizzante del Pecorino
Romano, la cui ricetta dovrà richiedere specificamente latte di pecora.

### Panna

La panna è un intermedio comune per almeno:

- Burrata;
- Mascarpone.

La panna viene rappresentata come **ciotola di panna**.

La Cheese Vat usa una sola ricetta generica per il latte:

```text
#c:drinks/milk
+ ciotola nello slot contenitore
↓ Cheese Vat
Ciotola di panna
```

Non esistono ricette o rese differenti tra secchio e bottiglia di latte: il
giocatore può usare qualunque item compatibile con il tag
`#c:drinks/milk`, compresi i futuri contenitori di latte di pecora.

Il contenitore del latte usato come ingrediente viene restituito secondo le sue
normali regole di remainder. La ciotola nello slot contenitore viene invece
consumata per confezionare la panna.

Quando la ciotola di panna viene successivamente usata come ingrediente, la
ciotola vuota deve essere restituita.

La produzione della panna **non produce siero** e non viene bloccata da un
serbatoio del siero pieno.

### Coltura erborinata

È un nuovo ingrediente necessario per la produzione del Gorgonzola.

La coltura erborinata è un **item normale e craftabile**:

- non è contenuta in una bottiglia;
- non richiede uno slot contenitore;
- viene consumata direttamente come ingrediente della Cheese Vat.

Ricetta shapeless concordata:

```text
1x minecraft:bread
+ 1x #c:mushrooms
→ 1x Coltura Erborinata
```

Si usa il tag comune `#c:mushrooms`, così sono validi i funghi vanilla
(`minecraft:red_mushroom` e `minecraft:brown_mushroom`) e gli eventuali
funghi aggiunti da altre mod compatibili con lo stesso tag.

### Laccio per la Scamorza

Non viene creato un nuovo item dedicato.

Per legare la Scamorza si usa direttamente il **lazzo vanilla**
(`minecraft:lead`).

Il lazzo viene inserito nello **slot contenitore** della Cheese Vat, non negli
slot ingrediente.

La Cheese Vat dovrà quindi poter considerare il contenitore richiesto anche nel
matching delle ricette quando necessario, così ricette con ingredienti simili
possono essere distinte correttamente.

### Aceto di mele

L'ingrediente acido scelto per il Mascarpone è l'**Aceto di Mele**.

Nel branch formaggi interessa soltanto il suo utilizzo:

```text
Panna + Aceto di mele
→ Mascarpone
```

La produzione, il crafting e l'eventuale fermentazione dell'aceto vengono
progettati e implementati nel branch dedicato al sistema vino/barile, non in
`feature/cheeses`.

Nel repository sono già presenti asset per il sidro che potranno essere usati
da quel lavoro separato.

### Bocconcini — regola ricetta

Ricetta concordata:

```text
Curd
+ Water
↓ Cheese Vat
2x Bocconcini
```

Regole:

- ogni lavorazione produce **2 bocconcini**;
- non è richiesto alcun contenitore finale;
- l'acqua è un ingrediente e il relativo contenitore viene restituito secondo
  le normali regole di remainder.

### Mascarpone — gestione della ciotola

La ciotola della panna **non viene riutilizzata direttamente come contenitore
del Mascarpone**.

La lavorazione deve seguire il modello già usato dalla Cheese Vat:

```text
Cream Bowl
+ Apple Cider Vinegar
+ Bowl nello slot contenitore
↓ Cheese Vat
Mascarpone
```

La `Cream Bowl` è un ingrediente e restituisce la propria ciotola vuota come
remainder. La ciotola nello slot contenitore è invece quella consumata per
confezionare il Mascarpone.

### Ricotta e selezione ricetta

Il conflitto tra Ricotta e Panna viene risolto con un **toggle del flusso del
siero** nella GUI della Cheese Vat.

La freccia tra serbatoio e caldaia è cliccabile:

- **flow attivo**: le ricette possono usare automaticamente il siero presente
  nel serbatoio interno;
- **flow bloccato**: il siero del serbatoio viene ignorato dal recipe matching.

Il toggle riguarda soltanto il **prelievo automatico dal tank**. Una
`Whey Bottle` o un `Whey Bucket` inseriti esplicitamente negli slot
ingrediente restano validi anche quando il flow è bloccato.

Questo permette al giocatore di scegliere la Panna anche quando la Cheese Vat
contiene abbastanza siero per una Ricotta. Il pulsante non svuota né modifica
la quantità di siero.

Lo stato predefinito è **flow attivo**. Lo stato viene salvato nella BlockEntity
e sincronizzato con il menu; rompendo e ripiazzando la Cheese Vat non viene
conservato nel blocco droppato e torna quindi al valore predefinito.

#### GUI e JEI del flow

La GUI usa la freccia già disegnata nella texture principale:

- area cliccabile: `x=133, y=29, 9x9`;
- flow attivo: freccia normale della GUI;
- flow bloccato: overlay con la X rossa dello spritesheet, UV
  `176,55`, dimensione `9x9`.

JEI mantiene **una sola categoria Cheese Vat**, ma dispone di due background:

- `textures/gui/jei/cheese_vat.png`: ricette che non prelevano siero dal tank;
- `textures/gui/jei/cheese_vat_whey.png`: ricette che richiedono siero dal
  serbatoio interno.

Il formato delle ricette Cheese Vat prevede il campo opzionale `whey`,
espresso in mB e pari a `0` per default. In questa fase il campo descrive la
dipendenza dal tank e permette a JEI di scegliere il layout corretto. Il
controllo della quantità e il consumo effettivo del liquido verranno collegati
insieme alla logica del serbatoio.

## 4. Serbatoio del siero della Cheese Vat

La nuova GUI della Cheese Vat contiene un indicatore dedicato al serbatoio del
siero. La logica deve rispettare le regole seguenti.

### Capacità

- capacità massima: **4000 mB**;
- ogni lavorazione che produce siero aggiunge **250 mB**.

### Blocco della lavorazione

Se il serbatoio non ha spazio sufficiente:

- viene bloccata soltanto una lavorazione che **deve produrre siero**;
- le ricette che non producono siero continuano a funzionare normalmente.

Il controllo deve quindi essere proprietà della singola ricetta/lavorazione e
non un blocco generale della Cheese Vat.

### Inserimento

Il serbatoio **non può essere riempito manualmente**.

Non è consentito versare nel tank:

- bottiglie di siero;
- secchi di siero;
- siero proveniente da un calderone;
- altre sorgenti esterne.

Il serbatoio aumenta soltanto come conseguenza delle lavorazioni della Cheese
Vat che producono siero.

### Estrazione

Il siero può essere prelevato dal serbatoio usando contenitori.

Comportamento implementato:

- `minecraft:glass_bottle` richiede almeno **250 mB**, consuma 250 mB dal tank
  e produce `italiansdelight:whey_bottle`;
- `minecraft:bucket` richiede almeno **1000 mB**, consuma 1000 mB dal tank
  e produce `italiansdelight:whey_bucket`;
- se il tank non contiene abbastanza siero, il contenitore non viene consumato;
- l'estrazione usa suoni/eventi vanilla di raccolta del fluido;
- una Whey Bottle o un Whey Bucket pieni non possono essere riversati nel tank;
- se la Cheese Vat ha già un prodotto pronto che richiede il contenitore in
  mano, la raccolta del prodotto ha priorità rispetto all'estrazione del siero.

I due item restituiscono rispettivamente bottiglia di vetro e secchio vuoto
come crafting remainder quando verranno usati nelle ricette.

### Rottura della Cheese Vat

Il siero nel serbatoio **non viene conservato** nella Cheese Vat droppata.

Rompendo la macchina:

- inventario/prodotti continuano a seguire le regole già esistenti;
- tutto il siero presente nel tank viene perso.

### Stato implementazione del tank

Il serbatoio base è implementato nella Cheese Vat:

- capacità effettiva di **4000 mB** salvata nella BlockEntity;
- quantità sincronizzata con il menu/client;
- il cilindro della GUI mostra il livello dal basso verso l'alto;
- passando il mouse sul cilindro viene mostrato un tooltip vanilla con il nome
  **Siero di Latte / Whey** e la quantità corrente nel formato
  `500 mB / 4000 mB`;
- l'area interna del cilindro è 16x32 px: **250 mB = 2 px**;
- le ricette Cheese Vat possono dichiarare `"whey_output"`;
- la ricetta della cagliata dichiara `"whey_output": 250`;
- se una ricetta deve produrre più siero dello spazio libero disponibile,
  raggiunge il completamento ma resta in attesa senza consumare ingredienti
  finché non torna disponibile spazio sufficiente;
- le ricette con `whey_output = 0` non vengono influenzate dal tank pieno;
- il siero resta escluso dai dati salvati nell'item droppato della Cheese Vat,
  quindi viene perso alla rottura come concordato.

Non sono ancora implementati in questa sottofase:

- consumo del tank da parte delle ricette con campo `whey`;
- priorità item di siero -> tank;
- calderone con siero.

## 5. Consumo del siero nelle ricette

Le ricette che richiedono siero possono soddisfare il requisito in due modi:

1. usando un item di siero, come bottiglia o secchio;
2. usando direttamente il siero presente nel tank della Cheese Vat.

La priorità è:

```text
bottiglia/secchio di siero
        ↓
serbatoio interno
```

Quindi, se la ricetta può usare un item di siero e un contenitore di siero
valido è presente, viene consumato l'item e il tank resta invariato.

Solo quando non è disponibile un item di siero appropriato la ricetta può
prelevare dal serbatoio interno.

### Ricotta

La ricotta può soddisfare il requisito del siero in due modi.

**Uso del serbatoio interno**

Quando non viene usato un item di siero, la lavorazione consuma **500 mB** dal
tank:

```text
500 mB siero dal tank
+ latte
+ ciotola
↓ Cheese Vat
Ricotta
```

**Uso di un contenitore di siero**

Se è presente un item di siero compatibile, questo ha priorità sul tank:

```text
1x Whey Bottle
oppure
1x Whey Bucket
+ latte
+ ciotola
↓ Cheese Vat
Ricotta
```

Bottiglia e secchio vengono trattati entrambi come **una singola unità valida
di siero per la ricetta**, indipendentemente dalla quantità nominale che
rappresentano.

Il contenuto del contenitore viene consumato interamente: non esiste consumo
parziale e l'eventuale siero in eccesso viene perso. Quindi un secchio può
essere usato anche se contiene più siero dei 500 mB che sarebbero richiesti dal
tank.

I **500 mB sono una regola esclusiva del prelievo dal serbatoio interno** e non
vengono usati per calcolare quanto consumare da bottiglie o secchi.

Il tank resta invariato quando viene usato un item di siero.

Il formato esatto del latte e le quantità finali della ricetta restano da
definire.

## 6. Siero nei calderoni

Il siero deve poter essere contenuto nei **calderoni vanilla** mantenendo un
comportamento il più possibile coerente con i calderoni di Minecraft.

Regole concordate:

- il calderone usa i normali **tre livelli**;
- i livelli del calderone non vengono espressi né salvati in mB;
- una bottiglia di siero aumenta di un livello un calderone non pieno;
- una bottiglia di vetro preleva un livello da un calderone con siero;
- un secchio di siero riempie direttamente un calderone vuoto;
- un secchio vuoto può prelevare il siero soltanto da un calderone pieno;
- non si possono usare secchio o bottiglia del calderone per riversare siero nel
  tank interno della Cheese Vat.

I **mB restano l'unità interna del serbatoio della Cheese Vat**. Il calderone è
invece una meccanica discreta a livelli, come i calderoni vanilla.

Restano da definire soltanto:

- implementazione tecnica/block state;
- texture e colore del siero nel calderone;
- comportamento preciso in caso di sostituzione/rottura del blocco.

## 7. Consumabilità degli item

In questa fase si stabilisce soltanto **quali item possono essere consumati**.
I valori di fame e saturazione verranno bilanciati successivamente.

| Item | Consumabile | Regola |
|---|---:|---|
| Mozzarella | Sì | Formaggio fresco consumabile anche intero |
| Mozzarella Slice | Sì | Porzione diretta |
| Bocconcini | Sì | Piccole porzioni già pronte |
| Ricotta | Sì | Servita in ciotola |
| Burrata | Sì | Formaggio fresco consumabile direttamente |
| Mascarpone | Sì | Servito in ciotola |
| Cream Bowl | Sì | Panna in ciotola, anche se usata soprattutto come ingrediente |
| Curd | Sì | La cagliata resta consumabile |
| Parmigiano Reggiano — forma intera | No | Deve essere porzionata |
| Parmigiano Reggiano — porzione/spicchio | Sì | Porzione mangiabile |
| Parmigiano Reggiano grattugiato | No | Ingrediente culinario |
| Pecorino Romano — forma intera | No | Deve essere porzionata |
| Pecorino Romano — porzione/spicchio | Sì | Porzione mangiabile |
| Pecorino Romano grattugiato | No | Ingrediente culinario |
| Gorgonzola — forma intera | No | Forma da stagionatura |
| Gorgonzola — porzione/spicchio | Sì | Porzione mangiabile |
| Provolone — forma intera | No | Forma da stagionatura |
| Provolone Slice | Sì | Porzione mangiabile |
| Scamorza intera | Sì | La forma è considerata una porzione consumabile |
| Scamorza affumicata intera | Sì | Come la Scamorza normale |
| Fresh Scamorza | No | Prodotto intermedio prima dell'asciugatura |
| Sheep Milk Bottle | Sì | Bevibile come equivalente della Milk Bottle |
| Sheep Milk Bucket | Sì | Bevibile come il secchio di latte vanilla e rimuove gli effetti attivi |
| Whey Bottle | No | Risorsa di lavorazione |
| Whey Bucket | No | Risorsa di lavorazione |
| Blue Mold Culture | No | Ingrediente tecnico |
| Rennet | No | Ingrediente tecnico |
| Salt | No | Ingrediente |
| Apple Cider Vinegar | No | Ingrediente |

Regola generale: le **forme grandi destinate a stagionatura o porzionatura non
sono cibo diretto**; le porzioni, i formaggi freschi e i prodotti serviti in
ciotola possono invece avere proprietà alimentari.

## 8. Decisioni ancora aperte

Non sono ancora stati fissati:

- tempo/resa finale della lavorazione della panna;

- produzione/crafting dell'aceto nel branch dedicato al sistema vino/barile;
- quantità, tempi e valori di fame/saturazione degli item già definiti come consumabili;
- tempi e regole della stagionatura/asciugatura;
- texture e implementazione tecnica del siero nei calderoni;
- nomi definitivi di registrazione per gli item non ancora implementati.

Quando una di queste decisioni viene approvata, va aggiunta a questo documento
prima o insieme alla relativa implementazione.
