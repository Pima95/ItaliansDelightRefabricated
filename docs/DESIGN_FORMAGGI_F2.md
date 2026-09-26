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
Farmer's Delight Refabricated. Il latte di pecora è la materia prima
caratterizzante del Pecorino Romano.

### Panna

La panna è un intermedio comune per almeno:

- Burrata;
- Mascarpone.

La forma esatta dell'item/contenitore e la sua ricetta di produzione sono ancora
da definire.

### Coltura erborinata

È un nuovo ingrediente necessario per la produzione del Gorgonzola.

La modalità di ottenimento è ancora da definire.

### Laccio per formaggi

Per la Scamorza non viene usata la stringa vanilla.

È previsto un item dedicato:

- id di riferimento: `italiansdelight:cheese_tie`
- italiano: **Laccio per Formaggi**
- inglese: **Cheese Tie**

Il laccio viene inserito nello **slot contenitore** della Cheese Vat, non negli
slot ingrediente.

La Cheese Vat dovrà quindi poter considerare il contenitore richiesto anche nel
matching delle ricette quando necessario, così ricette con ingredienti simili
possono essere distinte correttamente.

### Aceto di mele

L'ingrediente acido scelto per il Mascarpone è l'**Aceto di Mele**.

Filiera prevista:

```text
Mele
  ↓
Sidro di mele
  ↓ fermentazione nel Brewing Barrel
Aceto di mele
```

L'aceto sarà poi usato nella lavorazione:

```text
Panna + Aceto di mele
→ Mascarpone
```

Nel branch sono già presenti texture dedicate al sidro:

- `textures/block/fluids/booze/cider_still.png`
- `textures/block/fluids/booze/cider_flow.png`

con relativi file `.mcmeta` animati.

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

Il siero deve poter essere contenuto nei **calderoni vanilla**.

Requisiti già fissati:

- un calderone può contenere siero;
- il siero deve poter essere trasferito tra calderone e contenitori compatibili;
- questa possibilità non consente comunque di riversare siero nel tank interno
  della Cheese Vat.

Dettagli ancora aperti:

- corrispondenza tra i tre livelli del calderone e i mB;
- interazioni precise con bottiglia e secchio;
- block state / implementazione tecnica del calderone con siero;
- texture/colore del contenuto;
- comportamento dei drop e della sostituzione del blocco.

Questi punti devono essere definiti prima dell'implementazione del calderone con
siero.

## 7. Decisioni ancora aperte

Non sono ancora stati fissati:

- ricetta e forma definitiva della panna;
- modalità di ottenimento della coltura erborinata;
- ricetta del Laccio per Formaggi;
- produzione esatta del sidro e dell'aceto nel Brewing Barrel;
- quantità, tempi e valori nutritivi dei nuovi formaggi;
- tempi e regole della stagionatura/asciugatura;
- quantità/livelli del siero nei calderoni;
- dettagli finali della ricetta della ricotta;
- nomi definitivi di registrazione per gli item non ancora implementati.

Quando una di queste decisioni viene approvata, va aggiunta a questo documento
prima o insieme alla relativa implementazione.
