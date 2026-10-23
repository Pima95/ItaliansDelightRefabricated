# Italian's Delight Refabricated — Orchard Update 1.1.0

**Stato:** progettazione iniziale, decisioni approvate dall'autore il 23 ottobre 2026.  
**Branch:** `feature/orchards-1.1.0`, creato da `main` dopo la pubblicazione della 1.0.0.  
**Ambito:** tre alberi da frutto e relative coltivazioni. Le aggiunte di pasta, piatti e ricette saranno contenuti **bonus graduali** all'interno dei diversi aggiornamenti, non necessariamente una release dedicata.

> Questo documento specifica il gameplay concordato; non indica che nuovi blocchi, alberi o ricette siano già implementati. La versione e il contenuto pubblicati della 1.0.0 rimangono invariati.

## 1. Alberi confermati

| Specie | Aspetto | Frutto | Biomi proposti e accettati come direzione |
|---|---|---|---|
| **Ulivo / Olive Tree** | Basso, chioma larga, foglie verde-grigiastre, tronco irregolare | **Olive** | Savanna, Savanna Plateau, Plains |
| **Mandorlo / Almond Tree** | Chioma arrotondata, fogliame e possibile fioritura chiara | **Mandorle** | Plains, Meadow, Savanna |
| **Ciliegio da frutto / Fruit Cherry Tree** | Albero con foglie verdi, ciliegie rosse riconoscibili | **Ciliegie** | Forest, Flower Forest, Cherry Grove |

### Ciliegio: decisione A approvata

- Introdurre un **albero da frutto distinto** dal ciliegio ornamentale vanilla a foglie rosa.
- Il nuovo ciliegio produce ciliegie commestibili tramite il sistema di raccolta comune.
- È possibile **riutilizzare il legno di ciliegio vanilla** per evitare un ulteriore set di legni; la decisione sull'eventuale nuovo legno resta aperta.
- Non alterare il comportamento o la generazione dei ciliegi ornamentali vanilla.

### Variante rara: Ulivo secolare / Ancient Olive Tree

**Richiesta approvata:** oltre all'ulivo normale, generare raramente una variante monumentale di **ulivo secolare**.

- **Tronco 2×2** blocchi alla base e lungo la parte principale del fusto: elemento distintivo obbligatorio.
- Aspetto più antico e imponente dell'ulivo comune: tronco largo e contorto, rami irregolari, chioma ampia non necessariamente simmetrica.
- Utilizza lo **stesso frutto (olive)** e appartiene alla stessa filiera dell'ulivo comune; non è una quarta specie.
- La sua generazione naturale deve essere **sensibilmente più rara** degli ulivi normali, preferibilmente negli stessi biomi adatti all'ulivo.
- Non introdurre nuove ricette, raccolte o bonus di resa esclusivi senza una decisione dedicata.
- **Scelta B approvata:** l'ulivo secolare può essere ottenuto sia tramite rara generazione naturale, sia piantando **quattro germogli di ulivo in un quadrato 2×2**, in modo analogo agli alberi giganti vanilla. Il germoglio singolo continua a produrre l'ulivo normale.
- La crescita del 2×2 deve verificare i quattro germogli e lo spazio necessario, consumandoli **soltanto a generazione riuscita**, senza lasciare tronchi o foglie parziali quando il terreno è ostruito. La farina d'ossa deve poter innescare anche questa crescita, rispettando le regole di gioco appropriate.
- **Da definire prima dell'implementazione:** probabilità numerica di spawn, altezza/dimensioni, forma esatta di tronco e foglie, bilanciamento della probabilità di crescita del 2×2.
- Verificare spazio libero, intersezioni tra alberi, generazione al bordo del chunk, luce/ombre, compatibilità con alberi vanilla e tempi di worldgen.
- Non sostituire ulivi normali già generati nei vecchi chunk; gli alberi nuovi possono comparire solo dove la generazione di mondo viene eseguita.

## 2. Meccaniche condivise approvate

1. **Germogli coltivabili:** ciascuna specie può essere piantata e crescere come un albero, con supporto alla farina d'ossa.
2. **Fruttificazione sulle foglie:** alcune foglie dedicate sviluppano frutti in modo visibile, senza richiedere la distruzione dell'intera chioma.
3. **Raccolta con clic destro:** quando il frutto è maturo, viene raccolto; la foglia rimane al suo posto.
4. **Ricrescita:** le foglie produttive ritornano gradualmente allo stato fruttifero dopo la raccolta.
5. **Frequenza generale:** i tre tipi di alberi sono poco comuni in natura; gli ulivi secolari sono una sottovariante nettamente più rara.
6. **Compatibilità e performance:** evitare tick continui sull'intera chioma; privilegiare stati e tick limitati alle foglie fruttifere.

**Da progettare in dettaglio:** probabilità/tempo di maturazione, drop per raccolta, numero e posizione delle foglie produttive, eventuale bone meal sui frutti, decadimento delle foglie, comportamenti con Silk Touch/Shears e regole di raccolta da dispenser/automazioni.

## 3. Filiera di ingredienti e contenuti bonus

- **Olive:** alimento/ingrediente; future lavorazioni come olive conservate e olio d'oliva. Le fasi precise sono ancora da definire.
- **Mandorle:** ingrediente; possibili mandorle sgusciate, latte di mandorla e dolci. Definire eventuale sgusciatura.
- **Ciliegie:** frutto consumabile; possibili confetture e dolci.
- **Bonus degli aggiornamenti:** alcune ricette di pasta o altri piatti italiani possono arrivare in ciascuna release, purché coerenti con gli ingredienti implementati.
- Preferire integrazioni con **Farmer's Delight Refabricated** (Cutting Board, Cooking Pot, strumenti, tag condivisi) quando adatte, invece di introdurre nuovi macchinari senza necessità.

Le preparazioni elencate qui sono **idee**, non funzionalità già approvate per l'intera 1.1.0.

## 4. Fasi di sviluppo (non iniziate)

- [x] **Fase 0 — Scelte iniziali:** tre specie, ciliegio fruttifero separato, ulivo secolare raro con tronco 2×2 e crescita coltivabile tramite quattro germogli disposti 2×2 (scelta B).
- [ ] **Fase 1 — Asset e modelli:** definire riuso o creazione di legno, foglie, frutti e germogli; requisiti di pixel art.
- [ ] **Fase 2 — Alberi e generazione:** crescita, chiome, biomi, rarità e ulivo secolare 2×2.
- [ ] **Fase 3 — Raccolta e ricrescita:** foglie fruttifere, raccolta, progressione e salvataggio.
- [ ] **Fase 4 — Ingredienti e ricette:** decidere prima le lavorazioni principali e poi implementarle.
- [ ] **Fase 5 — JEI, localizzazione e bonus:** ricette e integrazione completa.
- [ ] **Fase 6 — Test e pubblicazione:** build, client, server dedicato, vecchi mondi, worldgen e bilanciamento.

## 5. Decisioni da prendere prima di iniziare la fase 1

1. **Legno:** gli alberi useranno legni vanilla oppure nuove famiglie di tronchi, tavole, scale e lastre?
2. **Ulivo secolare — DECISO (B):** rara generazione naturale **e** crescita da quattro germogli di ulivo in un quadrato 2×2. Rimangono da bilanciare probabilità naturali e crescita del 2×2.
3. **Raccolta:** maturazione e resa per foglia; interazione con la farina d'ossa.
4. **Grafica:** chiome (normali/fiorite/fruttifere) e livello di differenza visiva per i tre alberi.

**Vincoli:** non modificare la `main` stabile né le texture già approvate della 1.0.0 senza richiesta; nessun incremento della versione pubblicata in questa fase.
