# Cured Meats — Balance

Prima passata di bilanciamento della filiera dei salumi.

I valori di questa pagina sono intenzionalmente separati dai test funzionali:
la meccanica è già stata verificata, mentre questi numeri devono essere provati
in survival prima di considerarli definitivi.

## Principi

- I salumi interi restano prodotti da lavorare e non sono direttamente
  consumabili.
- Il Cutting Board continua a produrre **4 fette** per ogni salume.
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

Con una resa di quattro fette, un salume intero fornisce complessivamente
**8 punti fame** se consumato interamente a fette. La differenza fra i prodotti
è quindi affidata soprattutto alla saturazione e al costo/tempo della filiera.

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

## Tempi attuali da testare

| Prodotto | Tick | Tempo reale a 20 TPS |
|---|---:|---:|
| Pancetta | 24000 | 20 min |
| Salame | 36000 | 30 min |
| Guanciale | 36000 | 30 min |
| Bresaola | 36000 | 30 min |
| Coppa | 48000 | 40 min |
| Speck | 48000 | 40 min + affumicatura |
| Prosciutto Crudo | 72000 | 60 min |

Questi tempi restano invariati nella passata 1.

## Test survival richiesto

Prima della passata 2 verificare:

- [ ] produrre almeno un salume partendo dalle materie prime;
- [ ] valutare se 4 fette sembrano una resa adeguata;
- [ ] confrontare la convenienza con carne cotta vanilla/Farmer's Delight;
- [ ] verificare se Pancetta e Guanciale risultano troppo efficienti come snack;
- [ ] verificare se Bresaola risulta abbastanza utile nonostante la saturazione minore;
- [ ] valutare se 60 minuti caricati per il Prosciutto Crudo sono eccessivi;
- [ ] valutare Speck considerando sia affumicatura sia stagionatura.

La passata 2 riguarderà soprattutto i **tempi di stagionatura** ed eventuali
correzioni mirate a rese o valori alimentari.
