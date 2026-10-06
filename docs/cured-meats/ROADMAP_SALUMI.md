# Italian's Delight Refabricated — Roadmap Salumi

Documento di progettazione per lo sviluppo della filiera dei salumi nel branch
`feature/cured-meats`.

L'obiettivo è aggiungere salumi italiani con una progressione coerente con
Farmer's Delight Refabricated, evitando di introdurre nuove macchine o blocchi
funzionali quando esistono già sistemi adatti in Minecraft, Farmer's Delight o
Italian's Delight.

---

## 1. Decisioni confermate

### Salumi previsti

La prima versione della filiera comprenderà:

- **Prosciutto Crudo**
- **Salame**
- **Mortadella**
- **Pancetta**
- **Guanciale**
- **Bresaola**
- **Coppa**
- **Speck**

La lista potrà essere estesa in futuro, ma questi otto prodotti costituiscono
lo scope iniziale.

### Nessun nuovo blocco funzionale

La filiera non introdurrà nuovi macchinari dedicati.

Si riutilizzeranno:

- blocchi vanilla;
- sistemi di Farmer's Delight Refabricated;
- blocchi e meccaniche già presenti in Italian's Delight.

Questo mantiene la mod più compatta e rende la filiera coerente con il principio
generale del progetto: aggiungere nuovi blocchi solo quando manca davvero una
meccanica equivalente.

### Riutilizzo dell'uncino

L'attuale **Cheese Hook** verrà reso un blocco generico per prodotti appesi.

Nome di lavoro:

- **Hanging Hook** in inglese;
- **Uncino da stagionatura** in italiano.

L'uncino dovrà poter continuare a gestire i formaggi già supportati e, in
aggiunta, i salumi che richiedono asciugatura o stagionatura.

Per evitare incompatibilità con mondi già esistenti, la prima scelta tecnica è
**mantenere l'ID registrato attuale `italiansdelight:cheese_hook`** e cambiare
il nome mostrato al giocatore. Un eventuale cambio dell'ID interno verrà
considerato solo con una migrazione esplicita.

Anche le classi oggi specifiche per i formaggi potranno essere generalizzate
gradualmente quando necessario, senza riscrivere da zero il sistema già
funzionante.

---

## 2. Filosofia della filiera

I salumi non devono essere semplici ricette da crafting table.

La produzione deve combinare, quando appropriato:

1. preparazione o taglio della carne;
2. salatura / preparazione;
3. eventuale cottura o affumicatura;
4. eventuale stagionatura sull'uncino;
5. taglio del prodotto finito tramite Cutting Board.

Non tutti i salumi devono attraversare tutte le fasi.

Ad esempio:

- **Prosciutto Crudo, Salame, Guanciale, Bresaola e Coppa** sono candidati
  naturali alla stagionatura;
- **Speck** richiede anche una fase di affumicatura;
- **Mortadella** deve avere una lavorazione principalmente cotta e non deve
  essere trattata come un normale prodotto stagionato.

---

## 3. Sistemi da riutilizzare

| Sistema | Utilizzo previsto |
|---|---|
| Hanging Hook | stagionatura/asciugatura dei prodotti appesi |
| Farmer's Delight Cutting Board | preparazione delle carni e taglio dei salumi finiti |
| Farmer's Delight Cooking Pot | preparazioni composte quando adatto |
| Vanilla Smoker | affumicatura, in particolare per lo Speck |
| Crafting Table | assemblaggi semplici che non richiedono una lavorazione speciale |
| Salt di Italian's Delight | ingrediente principale per la salatura |
| Farmer's Delight / vanilla meats | materie prime della filiera |

Non verrà creato un blocco separato per la stagionatura dei salumi.

---

## 4. Materie prime

La priorità è riutilizzare carni già esistenti in Minecraft e Farmer's Delight
Refabricated.

Materie prime candidate:

- Porkchop;
- Farmer's Delight Ham;
- Farmer's Delight Bacon;
- Beef;
- Farmer's Delight Minced Beef, quando coerente con la preparazione;
- Salt di Italian's Delight.

Se per distinguere correttamente alcune lavorazioni sarà necessario introdurre
tagli intermedi, questi saranno **item** e non nuovi blocchi.

Possibili intermedi, da confermare solo se realmente necessari:

- carne salata;
- impasto crudo per salame;
- impasto crudo per mortadella;
- taglio di maiale preparato per pancetta/guanciale/coppa.

L'obiettivo è comunque mantenere il numero di intermedi il più basso possibile.

---

## 5. Filiere preliminari

Le seguenti catene definiscono il comportamento generale. Ingredienti esatti,
quantità, tempi e rese verranno bilanciati durante lo sviluppo.

### 5.1 Prosciutto Crudo

Proposta:

```text
Ham
 + Salt
   ↓
Prepared / Salted Ham
   ↓ Hanging Hook
Prosciutto Crudo
   ↓ Cutting Board
Prosciutto Crudo slices
```

Il Prosciutto Crudo deve essere uno dei prodotti con stagionatura più lunga
della filiera.

### 5.2 Salame

Proposta:

```text
Pork-based ingredients
 + Salt
 + eventuali ingredienti aromatici
   ↓
Raw Salame
   ↓ Hanging Hook
Salame
   ↓ Cutting Board
Salame slices
```

La ricetta dell'impasto verrà definita dopo aver verificato quali ingredienti
Farmer's Delight possono essere riutilizzati senza introdurre componenti
superflui.

### 5.3 Mortadella

La Mortadella non seguirà la normale stagionatura sull'uncino.

Proposta generale:

```text
Pork-based ingredients
 + Salt
 + eventuali ingredienti aggiuntivi
   ↓
Raw Mortadella
   ↓ cooking process
Mortadella
   ↓ Cutting Board
Mortadella slices
```

La fase di cottura dovrà riutilizzare un sistema già presente, preferibilmente
Farmer's Delight o vanilla.

### 5.4 Pancetta

Proposta:

```text
Pork / Bacon
 + Salt
   ↓
Prepared Pancetta
   ↓ curing / drying
Pancetta
   ↓ Cutting Board
Pancetta slices
```

Va deciso durante l'implementazione se la fase di asciugatura debba avvenire
sull'uncino o possa essere rappresentata da una lavorazione più semplice.

### 5.5 Guanciale

Proposta:

```text
Pork
 + Salt
   ↓
Prepared Guanciale
   ↓ Hanging Hook
Guanciale
   ↓ Cutting Board
Guanciale slices
```

Guanciale e Pancetta devono restare prodotti distinti anche se condividono parte
delle materie prime.

### 5.6 Bresaola

Proposta:

```text
Beef
 + Salt
   ↓
Prepared Bresaola
   ↓ Hanging Hook
Bresaola
   ↓ Cutting Board
Bresaola slices
```

È il principale salume della prima fase basato su carne bovina.

### 5.7 Coppa

Proposta:

```text
Pork
 + Salt
   ↓
Prepared Coppa
   ↓ Hanging Hook
Coppa
   ↓ Cutting Board
Coppa slices
```

La durata dovrà essere intermedia tra i prodotti più rapidi e il Prosciutto
Crudo.

### 5.8 Speck

Proposta:

```text
Ham / prepared pork
 + Salt
   ↓
Prepared Speck
   ↓ smoking + curing
Speck
   ↓ Cutting Board
Speck slices
```

Lo Speck deve distinguersi dal Prosciutto Crudo attraverso l'affumicatura.

La sequenza esatta tra affumicatura e stagionatura verrà definita durante la
fase di implementazione, scegliendo quella più chiara da comunicare al
giocatore.

---

## 6. Sistema di stagionatura

La stagionatura dei salumi deve riutilizzare il sistema già realizzato per i
formaggi appesi.

Il sistema deve poter distinguere almeno:

- item iniziale;
- item finale;
- durata;
- modello/texture del prodotto appeso;
- eventuali condizioni speciali future.

### Obiettivo tecnico

Generalizzare il codice oggi legato ai soli formaggi appesi senza rompere la
funzionalità esistente.

Possibili refactor:

- `HangingCheeseType` → struttura generica per prodotti appesi;
- logica del Cheese Hook → logica condivisa per formaggi e salumi;
- rendering/model mapping esteso ai nuovi prodotti.

Il refactor deve essere **incrementale**: prima si preserva il comportamento
esistente dei formaggi, poi si aggiunge il supporto ai salumi.

---

## 7. Cutting Board

Ogni salume finito che ha senso tagliare dovrà avere una ricetta Cutting Board.

Prodotti previsti in forma affettata:

- Prosciutto Crudo slices;
- Salame slices;
- Mortadella slices;
- Pancetta slices;
- Guanciale slices;
- Bresaola slices;
- Coppa slices;
- Speck slices.

Le rese verranno bilanciate in una fase successiva, mantenendo proporzioni
coerenti tra prodotto intero e fette.

Le fette saranno poi utilizzabili nelle future ricette del branch
`feature/dishes`, ad esempio panini, antipasti, pasta e altre preparazioni.

---

## 8. Compatibilità e tag

Quando possibile verranno utilizzati tag comuni invece di item hard-coded.

Obiettivi:

- supportare ingredienti equivalenti aggiunti da altre mod;
- permettere alle future ricette di riconoscere i salumi tramite tag;
- mantenere compatibilità con l'ecosistema Farmer's Delight.

Tag interni/comuni da valutare durante l'implementazione:

```text
#c:foods/meat
#c:foods/raw_meat
#c:foods/cooked_meat
#c:foods
```

Verrà verificata la struttura effettiva dei tag disponibili in Minecraft 26.2
e Farmer's Delight Refabricated prima di definire i JSON definitivi.

Potrà inoltre essere aggiunto un tag dedicato, ad esempio:

```text
#italiansdelight:cured_meats
```

oppure un tag comune equivalente se già standardizzato.

---

## 9. Fasi di sviluppo

### Fase S0 — Progettazione

- [x] definire la lista iniziale dei salumi;
- [x] decidere di non aggiungere nuovi blocchi funzionali;
- [x] decidere di riutilizzare l'uncino esistente;
- [x] definire le catene produttive preliminari;
- [x] definire una prima versione degli ingredienti di ogni prodotto;
- [x] definire nomi e ID della prima versione degli item intermedi.

### Fase S1 — Generalizzazione dell'uncino

- [x] rinominare il blocco mostrato al giocatore;
- [x] mantenere la compatibilità con i formaggi esistenti;
- [x] estendere la logica dei prodotti appesi a formaggi e salumi;
- [x] aggiungere supporto ai modelli dei salumi;
- [x] verificare salvataggio, rottura e riposizionamento;
- [x] verificare comportamento client/server.

### Fase S2 — Item e intermedi

- [x] registrare gli otto salumi;
- [x] registrare le relative fette;
- [x] aggiungere gli intermedi necessari alla prima filiera;
- [x] aggiungere traduzioni EN/IT;
- [x] aggiungere i modelli JSON;
- [x] creare le 25 texture degli item: 8 interi, 8 fette e 9 intermedi;
- [x] collegare ai modelli appesi 14 texture opache dedicate, distinte per stadio.

### Fase S3 — Preparazione

- [x] implementare ricette di salatura/preparazione;
- [x] usare il Cutting Board per l'affettatura dei prodotti finiti;
- [x] valutare il Cooking Pot: non necessario nella prima filiera;
- [x] implementare preparazione e cottura della Mortadella;
- [x] implementare il percorso preparazione → affumicatura → stagionatura dello Speck.

### Fase S4 — Stagionatura e affumicatura

- [x] implementare le ricette sull'Hanging Hook;
- [x] impostare tempi iniziali placeholder;
- [x] implementare affumicatura dello Speck;
- [x] verificare persistenza del progresso;
- [x] verificare rendering, hitbox e orientamenti;

### Fase S5 — Cutting Board e utilizzo finale

- [x] aggiungere tutte le ricette di affettatura;
- [x] definire una resa placeholder di 4 fette per prodotto;
- [x] aggiungere tag interni per salumi e fette;
- [x] integrare i salumi nei primi piatti completi; vedere `docs/dishes/ROADMAP_PIATTI.md`.

### Fase S6 — JEI e documentazione

- [x] separare in JEI Cheese Aging e Hanging Aging & Drying;
- [x] mostrare il Cheese Aging Rack per i formaggi da ripiano;
- [x] mostrare l'Hanging Hook per Provolone, Scamorza e salumi;
- [x] aggiornare la documentazione tecnica;
- [x] aggiungere una checklist di test;
- [x] documentare il balance v1 candidate.

### Fase S7 — Bilanciamento

- [x] definire una prima passata dei valori nutrizionali;
- [x] ribilanciare le rese: 4 fette standard, 8 per i prodotti basati su Ham;
- [x] ribilanciare i costi delle ricette in base alla materia prima;
- [x] definire la seconda passata dei tempi di stagionatura;
- [x] formalizzare stack size e XP di cottura;
- [x] confrontare in survival costi e valori con Vanilla / Farmer's Delight;
- [x] eseguire test survival finale del balance v1.

Dettagli e valori correnti: [BALANCE_SALUMI.md](BALANCE_SALUMI.md).

---

## 10. Tempi di stagionatura — balance v1

| Prodotto | Tick | Tempo |
|---|---:|---:|
| Pancetta | 18000 | 15 min |
| Salame | 24000 | 20 min |
| Guanciale | 24000 | 20 min |
| Bresaola | 24000 | 20 min |
| Coppa | 30000 | 25 min |
| Speck | 30000 | 25 min + affumicatura |
| Prosciutto Crudo | 42000 | 35 min |

La progressione mantiene il Prosciutto Crudo come prodotto più lento e lo
Speck come filiera a più passaggi, senza raggiungere i tempi dei formaggi
italiani a lunga stagionatura.

---

## 11. Cose da evitare

- nessun nuovo blocco funzionale dedicato esclusivamente ai salumi;
- nessun duplicato di sistemi già offerti da Farmer's Delight;
- nessuna ricetta complessa solo per aumentare artificialmente il numero di
  passaggi;
- evitare troppi ingredienti intermedi usa-e-getta;
- non modificare radicalmente il sistema dei formaggi già funzionante;
- non fissare valori di bilanciamento definitivi prima dei test.

---

## 12. Decisioni implementative attuali

La prima implementazione ha ormai fissato le decisioni principali:

1. gli otto salumi e i relativi intermedi sono registrati;
2. Mortadella usa cottura vanilla tramite Furnace o Smoker;
3. Speck segue preparazione → affumicatura → stagionatura;
4. l'uncino mantiene l'ID storico `italiansdelight:cheese_hook`, ma viene
   mostrato come Hanging Hook / Uncino da Stagionatura;
5. i tagli standard producono 4 fette, mentre Prosciutto Crudo, Coppa e Speck
   producono 8 fette perché basati su Ham;
6. tempi, rese e valori alimentari hanno completato il rebalancing v1.

I test manuali e survival sono completati; il branch è pronto per la pulizia
finale e la preparazione della pull request.


---

## 13. Stato implementazione attuale

La prima implementazione del branch usa i seguenti intermedi:

- `salted_ham` → Prosciutto Crudo;
- `raw_salame` → Salame;
- `raw_mortadella` → Mortadella tramite furnace;
- `prepared_pancetta` → Pancetta;
- `prepared_guanciale` → Guanciale;
- `prepared_bresaola` → Bresaola;
- `prepared_coppa` → Coppa;
- `prepared_speck` → affumicatura → `smoked_prepared_speck` → Speck.

Tempi balance v1 sull'uncino:

| Prodotto | Tick |
|---|---:|
| Prosciutto Crudo | 42000 |
| Salame | 24000 |
| Pancetta | 18000 |
| Guanciale | 24000 |
| Bresaola | 24000 |
| Coppa | 30000 |
| Speck | 30000 |

Prosciutto Crudo, Coppa e Speck producono **8 fette** sul Cutting Board; gli
altri salumi ne producono **4**.

Valori nutrizionali, rese e tempi hanno superato il test survival finale e
costituiscono il balance v1 della feature.

### Texture

Le texture della prima filiera sono disponibili in formato PNG 16×16:
25 icone con sfondo trasparente, 14 atlanti opachi per i prodotti appesi e
un materiale per l'osso del Prosciutto.
Preparati, prodotti finiti e fette mantengono colori e caratteristiche della
stessa famiglia; lo Speck distingue anche lo stadio affumicato intermedio.

I modelli appesi usano sette geometrie dedicate, ispirate alle sagome degli
item: coscia con osso, insaccato sottile, lastra di Pancetta, Guanciale
rastremato, Bresaola allungata, Coppa panciuta e trancio di Speck. Le legature
seguono il corpo e le UV distinguono superficie esterna e taglio. Le sagome
di selezione seguono i nuovi modelli. La resa in Minecraft è stata verificata durante i test runtime; dettagli e
checklist sono in [MODELLI_SALUMI.md](MODELLI_SALUMI.md).

Criteri, catalogo e checklist visiva sono in [TEXTURE_SALUMI.md](TEXTURE_SALUMI.md).
L'[anteprima interattiva](TEXTURE_SALUMI.html) mostra le icone alla dimensione
originale e ingrandite e permette di ruotare i modelli appesi.


### Compatibilità Vanilla per la cottura

Tutti gli alimenti della mod che usano una normale lavorazione tramite
`minecraft:smelting` o `minecraft:smoking` devono essere compatibili con
**sia la fornace sia lo smoker**, seguendo il comportamento vanilla.

Regola iniziale:

- Furnace: **400 tick**
- Smoker: **200 tick**
- stessa esperienza;
- stesso output.

Questa regola si applica attualmente a:

- Mortadella;
- Speck preparato;
- Scamorza affumicata;
- Fetta di Scamorza affumicata.

Lo smoker rimane quindi l'opzione più rapida dedicata agli alimenti, mentre la
fornace standard resta sempre una alternativa valida.


### Verifica statica repository

Controllo eseguito sul branch `feature/cured-meats`:

- **25/25** item della filiera con definizione, modello e texture;
- **14/14** stati appesi con modello;
- **27/27** ricette previste presenti;
- nessun riferimento obbligatorio mancante rilevato.

La verifica statica non sostituisce i test runtime. La procedura completa è in
[TEST_CHECKLIST.md](TEST_CHECKLIST.md).


### Bilanciamento — Passata 3

Aggiornato il rapporto fra materia prima e resa:

- Salame e Mortadella richiedono 1 Porkchop;
- Pancetta richiede 2 Bacon;
- Prosciutto Crudo, Coppa e Speck producono 8 fette;
- gli altri salumi continuano a produrre 4 fette.

La distinzione segue la scala delle carni di Farmer's Delight: Bacon è una
porzione più piccola, mentre Ham rappresenta un taglio più grande.
