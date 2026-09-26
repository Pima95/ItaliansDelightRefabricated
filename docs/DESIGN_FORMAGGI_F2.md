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
| Bocconcini | Cagliata + acqua normale → bocconcini nella Cheese Vat |
| Ricotta | Siero + latte → ricotta nella Cheese Vat, con ciotola come contenitore finale |
| Parmigiano Reggiano | Latte vaccino + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato |
| Pecorino Romano | Latte di pecora + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato |
| Gorgonzola | Cagliata + sale + coltura erborinata → forma fresca → stagionatura |
| Provolone | Cagliata + sale + acqua → forma fresca → stagionatura → fette |
| Scamorza | Cagliata + sale → scamorza fresca usando un laccio nello slot contenitore; breve asciugatura e possibile variante affumicata |
| Burrata | Mozzarella + panna → burrata; nessuna stagionatura |
| Mascarpone | Panna + aceto di mele → mascarpone nella Cheese Vat |

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

Identificatori definitivi degli item ancora da fissare, ma come riferimento il
design usa i concetti `whey_bottle` e `whey_bucket`.

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

Quantità già concordate:

- bottiglia di siero: **250 mB**;
- secchio di siero: **1000 mB**.

L'interazione precisa con la macchina verrà definita in fase di implementazione.

### Rottura della Cheese Vat

Il siero nel serbatoio **non viene conservato** nella Cheese Vat droppata.

Rompendo la macchina:

- inventario/prodotti continuano a seguire le regole già esistenti;
- tutto il siero presente nel tank viene perso.

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

La ricotta userà **500 mB di siero** quando il siero viene prelevato dal tank.

Schema:

```text
500 mB siero dal tank
+ latte
+ ciotola
↓ Cheese Vat
Ricotta
```

Se invece la lavorazione utilizza una bottiglia o un secchio di siero previsto
dalla ricetta, il tank non deve perdere quei 500 mB.

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

## 7. Decisioni ancora aperte

Non sono ancora stati fissati:

- tempo/resa finale della lavorazione della panna;

- produzione/crafting dell'aceto nel branch dedicato al sistema vino/barile;
- quantità, tempi e valori nutritivi dei nuovi formaggi;
- tempi e regole della stagionatura/asciugatura;
- texture e implementazione tecnica del siero nei calderoni;
- dettagli finali della ricetta della ricotta;
- nomi definitivi di registrazione per gli item non ancora implementati.

Quando una di queste decisioni viene approvata, va aggiunta a questo documento
prima o insieme alla relativa implementazione.
