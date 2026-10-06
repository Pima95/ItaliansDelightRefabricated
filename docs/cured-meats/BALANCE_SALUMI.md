# Cured Meats — Balance v1

Bilanciamento v1 della filiera dei salumi.

I test funzionali e il test survival complessivo della filiera sono stati
completati. I valori qui raccolti costituiscono il **balance v1 approvato**.

## Principi

- I salumi interi restano prodotti da lavorare e non sono direttamente
  consumabili.
- Il Cutting Board produce **4 fette** per i tagli standard e **8 fette** per
  i prodotti basati su Ham.
- Le fette devono essere utili come snack, ma il loro ruolo principale resta
  quello di ingrediente per ricette future.
- Tutte le fette restituiscono **2 punti fame** nella prima passata.
- La saturazione distingue i prodotti in base al contenuto di grasso.
- I tempi di stagionatura non vengono cambiati in questa passata, così il loro
  effetto sul bilanciamento può essere valutato separatamente.

## Valori alimentari — passata 1

| Fetta | Nutrition | Saturation modifier | Profilo |
|---|---:|---:|---|
| Prosciutto Crudo | 2 | 0.5 | equilibrato |
| Salame | 2 | 0.6 | grasso |
| Mortadella | 2 | 0.5 | medio |
| Pancetta | 2 | 0.7 | molto grassa |
| Guanciale | 2 | 0.8 | il più grasso |
| Bresaola | 2 | 0.4 | magra |
| Coppa | 2 | 0.6 | medio-grassa |
| Speck | 2 | 0.6 | medio-grasso |

Con la resa definitiva, i prodotti standard forniscono complessivamente
**8 punti fame**, mentre i prodotti basati su Ham arrivano a **16 punti fame**.
La differenza fra i prodotti dipende inoltre da saturazione, costo e tempo della
filiera.

## Rese

Prima passata:

| Salume | Output Cutting Board |
|---|---:|
| Prosciutto Crudo | 4 fette |
| Salame | 4 fette |
| Mortadella | 4 fette |
| Pancetta | 4 fette |
| Guanciale | 4 fette |
| Bresaola | 4 fette |
| Coppa | 4 fette |
| Speck | 4 fette |

La resa uniforme è intenzionale: semplifica la memoria delle ricette e rende
più facile usare i salumi come ingredienti intercambiabili nelle future
preparazioni.

## Costi delle ricette — passata 1

Per questa prima passata vengono mantenute le ricette già testate:

| Prodotto | Preparazione |
|---|---|
| Prosciutto Crudo | Farmer's Delight Ham + Salt |
| Salame | 2× Porkchop + Salt + String |
| Mortadella | 2× Porkchop + Salt + Egg |
| Pancetta | Farmer's Delight Bacon + Salt |
| Guanciale | Porkchop + Salt + Sugar |
| Bresaola | Beef + Salt |
| Coppa | Farmer's Delight Ham + Salt + Sugar |
| Speck | Farmer's Delight Ham + Salt + Sweet Berries |

La Sweet Berries dello Speck rappresenta per ora la componente aromatica della
preparazione. Potrà essere sostituita in futuro quando la filiera delle erbe
sarà implementata.

## Tempi di stagionatura — passata 2

Dopo il confronto con la scala già usata dai formaggi, i salumi vengono
mantenuti più rapidi dei formaggi a lunga stagionatura. Il motivo è che le
fette sono soprattutto ingredienti per piatti futuri e non devono richiedere
tempi paragonabili a Pecorino o Parmigiano.

| Prodotto | Tick | Tempo reale a 20 TPS |
|---|---:|---:|
| Pancetta | 18000 | 15 min |
| Salame | 24000 | 20 min |
| Guanciale | 24000 | 20 min |
| Bresaola | 24000 | 20 min |
| Coppa | 30000 | 25 min |
| Speck | 30000 | 25 min + affumicatura |
| Prosciutto Crudo | 42000 | 35 min |

Riferimento della filiera casearia:

| Formaggio | Tempo |
|---|---:|
| Scamorza | 10 min |
| Gorgonzola | 20 min |
| Provolone | 30 min |
| Pecorino Romano | 45 min |
| Parmigiano Reggiano | 60 min |

Il Prosciutto Crudo resta il salume più lento, ma non raggiunge il tempo del
Pecorino o del Parmigiano. Lo Speck mantiene un costo temporale aggiuntivo
attraverso la fase di affumicatura.

## Test survival finale

Prima della passata 2 verificare:

- [x] produrre almeno un salume partendo dalle materie prime;
- [x] valutare se 4 fette per i tagli standard e 8 per i prodotti da Ham risultano adeguate;
- [x] confrontare la convenienza con carne cotta vanilla/Farmer's Delight;
- [x] verificare Pancetta e Guanciale come snack;
- [x] verificare Bresaola con la saturazione minore;
- [x] valutare i 35 minuti caricati del Prosciutto Crudo;
- [ ] valutare Speck considerando sia affumicatura sia stagionatura.

La passata 2 ha ridotto e differenziato i **tempi di stagionatura**. Dopo il
test survival si valuteranno eventuali correzioni mirate a ricette, rese o
valori alimentari.


## Costi e rese — passata 3

La terza passata corregge il rapporto fra quantità di carne iniziale e resa al
Cutting Board.

Farmer's Delight considera il **Bacon** una mezza porzione di Porkchop, mentre
un **Ham** può essere tagliato in 2 Porkchop più 1 Bone. Per evitare che alcune
filiere siano nettamente migliori o peggiori della semplice cottura vanilla,
la resa viene differenziata in base alla materia prima.

### Preparazioni aggiornate

| Prodotto | Preparazione |
|---|---|
| Prosciutto Crudo | 1× Ham + Salt |
| Salame | 1× Porkchop + Salt + String |
| Mortadella | 1× Porkchop + Salt + Egg |
| Pancetta | 2× Bacon + Salt |
| Guanciale | 1× Porkchop + Salt + Sugar |
| Bresaola | 1× Beef + Salt |
| Coppa | 1× Ham + Salt + Sugar |
| Speck | 1× Ham + Salt + Sweet Berries |

### Rese aggiornate

| Salume | Output Cutting Board | Nutrition totale |
|---|---:|---:|
| Prosciutto Crudo | 8 fette | 16 |
| Salame | 4 fette | 8 |
| Mortadella | 4 fette | 8 |
| Pancetta | 4 fette | 8 |
| Guanciale | 4 fette | 8 |
| Bresaola | 4 fette | 8 |
| Coppa | 8 fette | 16 |
| Speck | 8 fette | 16 |

I prodotti basati su Ham producono quindi il doppio delle fette perché la
materia prima rappresenta un taglio più grande. Gli altri prodotti restano a
quattro fette.

Questa scelta mantiene la lettura semplice:

- 1 porzione standard di carne → 4 fette;
- 1 Ham → 8 fette;
- 2 Bacon equivalgono circa a 1 Porkchop → 4 fette.

La Mortadella mantiene l'Egg come costo aggiuntivo della preparazione e il
Salame mantiene String come rappresentazione della legatura/budello. Questi
ingredienti potranno essere raffinati in futuro senza cambiare la struttura
della filiera.


## Stack size e XP — passata 4

La quarta passata non modifica il gameplay: formalizza i valori già in uso
perché risultano coerenti con il ruolo degli item.

### Stack size

| Categoria | Stack massimo |
|---|---:|
| Intermedi crudi/preparati | 16 |
| Salumi interi finiti | 16 |
| Fette commestibili | 64 |

Motivazione:

- gli intermedi e i prodotti interi rappresentano pezzi voluminosi e vengono
  mantenuti a 16;
- le fette sono normali ingredienti alimentari e restano a 64;
- non vengono introdotte eccezioni fra famiglie diverse, così il comportamento
  dell'inventario rimane prevedibile.

### Esperienza di cottura

| Processo | Furnace | Smoker |
|---|---:|---:|
| Raw Mortadella → Mortadella | 0.35 XP | 0.35 XP |
| Prepared Speck → Smoked Prepared Speck | 0.35 XP | 0.35 XP |

Lo smoker rimane più rapido ma non produce più esperienza della fornace.
L'output e l'XP sono quindi identici; cambia soltanto il tempo di lavorazione.

### Stato balance v1

Valori confermati per la candidate v1:

- nutrition: **2** per tutte le fette;
- saturation modifier differenziato per prodotto;
- resa: **4 fette** per tagli standard, **8 fette** per prodotti basati su Ham;
- stack: **16** per intermedi/interi, **64** per fette;
- cottura: **400 tick Furnace / 200 tick Smoker**, **0.35 XP**;
- stagionatura: da **15 a 35 minuti** in base al prodotto.

Il test survival complessivo è stato completato con esito positivo. Il
**balance v1 è quindi considerato definitivo per questa release**.
