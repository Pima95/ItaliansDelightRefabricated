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
- **Decisione successiva approvata:** anche il ciliegio da frutto avrà una **famiglia di legno originale**, distinta dal legno `minecraft:cherry_*`. Non riutilizzare più il legno vanilla per la sua filiera; usare identificatori non ambigui (p.es. prefisso `fruit_cherry_`).
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

## 1.1. Nuova decisione: tre famiglie di legno uniche

**Decisione approvata dall'autore:** ulivo, mandorlo e ciliegio da frutto avranno **ognuno un proprio tipo di legno**, non semplice riutilizzo dei blocchi vanilla. La decisione annulla esplicitamente la precedente ipotesi di usare il legno di ciliegio vanilla.

| Albero | Famiglia proposta | Distinzione importante |
|---|---|---|
| Ulivo | **Olive Wood** (`olive_*`) | La stessa famiglia è usata anche dall'ulivo secolare 2×2; nessuna famiglia secolare separata |
| Mandorlo | **Almond Wood** (`almond_*`) | Tavole e corteccia proprie |
| Ciliegio da frutto | **Fruit Cherry Wood** (`fruit_cherry_*`) | Distinta dalla famiglia di legno `minecraft:cherry_*` e dal ciliegio ornamentale rosa |

**Obiettivo dei tre set di legno:** tronchi, tronchi scortecciati, blocchi di legno completi, tavole, scale, lastre, recinzioni e cancelli, porte, botole, pulsanti, pedane a pressione, cartelli e versioni appese; valutare anche barche e barche con baule per coerenza con i set vanilla. Gli asset possono essere aggiunti progressivamente, ma il criterio è una famiglia edilizia completa, non soltanto tronco e foglie. Compatibilità dei tag `minecraft:logs`, `minecraft:planks` e tag specifici da includere nella fase implementativa. Per tutti gli asset vale la regola delle texture Minecraft senza antialiasing o alfa semitrasparente.

### Asset dell'ulivo già presenti e verificati nel repository (23 ottobre 2026)

Le seguenti **texture PNG esistono** in `src/main/resources/assets/italiansdelight/textures/block/`, recuperate dai materiali delle vecchie mod Italian's Delight e Rustic. La fonte precisa di ciascun file e la sua licenza sono **da verificare prima del riutilizzo**, non sono blocchi o modelli già implementati:

- Tronco: `olive_log.png`, `olive_log_top.png`; anche `log_olive.png` (possibile variante/duplicato da confrontare).
- Tronco scortecciato: `stripped_olive_log.png` e `stripped_olive_log_top.png`.
- Tavole: `olive_planks.png`.
- Foglie: `olive_leaves.png` e `leaves_olive.png` (possibili varianti da confrontare).
- Germoglio: `olive_sapling.png` e `sapling_olive.png` (possibili varianti da confrontare).
- Porta e botola: `olive_door_bottom.png`, `olive_door_top.png`, `olive_trapdoor.png`.
- Elementi da arredamento: `olive_cabinet_front.png`, `olive_cabinet_front_open.png`, `olive_cabinet_side.png`, `olive_cabinet_top.png`.
- Texture item già presenti: `olive_door.png`, `olive_sign.png`, `olive_boat.png`, `olive_chest_boat.png`, `olive_seed.png`, `olives.png`.

**Verifica di implementazione:** nella cartella dei `models/block`, `models/item` e `blockstates` non risultano ancora modelli o stati registrati per `olive_*`: non confondere la semplice presenza delle PNG con una funzionalità già presente in gioco. Prima di creare nuovi PNG controllare le coppie con nomi invertiti e riusare le risorse esistenti ove idonee. **Non modificare, sovrascrivere o eliminare texture preesistenti senza approvazione.** Per mandorlo e ciliegio da frutto serviranno asset coerenti ma visivamente distinti.

### Regola artistica approvata per gli asset Rustic (23 ottobre 2026)

**Decisione vincolante:** non usare texture provenienti da **Rustic** per nuovi contenuti della 1.1.0, essendo materiali della generazione Minecraft 1.12.2 che l'autore non intende riutilizzare. Per ulivi normali e secolari utilizzare un unico set coerente di asset non-Rustic; distinguere l'ulivo secolare dalla struttura 2×2 e dalla chioma, non tramite la texture antica. I file Rustic già nel repository restano intatti a fini storici, senza riferimenti nei nuovi modelli. Inventario, specifiche e possibili alternative in [WOOD_DESIGN.md](WOOD_DESIGN.md).

### Revisione artistica delle famiglie di legno (23 ottobre 2026)

**Inventario completato:** tutte le texture legacy di ulivo elencate sono presenti e le relative dimensioni PNG sono state verificate (16×16). Le coppie `olive_log/log_olive`, `olive_leaves/leaves_olive` e `olive_sapling/sapling_olive` sono file grafici distinti perché provenienti dai materiali delle vecchie mod **Italian's Delight** e **Rustic** (chiarimento dell'autore). **Non costituiscono varianti concepite per l'ulivo secolare** e non vanno assegnate automaticamente al nuovo gameplay. L'attribuzione esatta e le licenze vanno verificate file per file. Nessun asset è stato modificato.

**Proposte cromatiche in attesa di approvazione:** mantenere le tavole chiare dorate dell'ulivo, proporre **nocciola/beige caldo** per il mandorlo e **castano ramato/rossiccio** per il ciliegio da frutto (ben diverso dal rosa del ciliegio vanilla). Non considerare questa palette approvata finché l'autore non la conferma.

**Audit di provenienza avviato:** Italian's Delight e Rustic dichiarano entrambe una licenza MIT sulle pagine CurseForge; un controllo dei blob Git ha identificato **41 file identici** al repository originale Rustic, inclusi `log_olive.png`, `leaves_olive.png`, `sapling_olive.png` e `olive_seed.png`. Per gli altri PNG, una mancata corrispondenza **non** prova che siano di Italian's Delight. Vedere [RUSTIC_ASSET_MATCHES.md](RUSTIC_ASSET_MATCHES.md), [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) e [WOOD_DESIGN.md](WOOD_DESIGN.md). **Ogni decisione definitiva sarà registrata nel repository prima dell'implementazione.**

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
- [ ] **Fase 1 — Asset e modelli:** inventario legacy completato, **esclusione delle texture Rustic approvata**, 41 match Rustic documentati ([confronto](RUSTIC_ASSET_MATCHES.md)); rimangono l'attribuzione degli altri file, l'approvazione delle palette mandorlo/ciliegio, la registrazione delle famiglie legno e la progettazione di foglie/frutti/germogli.
- [ ] **Fase 2 — Alberi e generazione:** crescita, chiome, biomi, rarità e ulivo secolare 2×2.
- [ ] **Fase 3 — Raccolta e ricrescita:** foglie fruttifere, raccolta, progressione e salvataggio.
- [ ] **Fase 4 — Ingredienti e ricette:** decidere prima le lavorazioni principali e poi implementarle.
- [ ] **Fase 5 — JEI, localizzazione e bonus:** ricette e integrazione completa.
- [ ] **Fase 6 — Test e pubblicazione:** build, client, server dedicato, vecchi mondi, worldgen e bilanciamento.

## 5. Decisioni da prendere prima di iniziare la fase 1

1. **Legno — DECISO:** **tre famiglie originali** di legno, una per ulivo (anche secolare), mandorlo e ciliegio da frutto. Rimangono da definire palette e lista finale dei blocchi secondari; per l'ulivo partire dalle texture già presenti.
2. **Ulivo secolare — DECISO (B):** rara generazione naturale **e** crescita da quattro germogli di ulivo in un quadrato 2×2. Rimangono da bilanciare probabilità naturali e crescita del 2×2.
3. **Raccolta:** maturazione e resa per foglia; interazione con la farina d'ossa.
4. **Grafica:** chiome (normali/fiorite/fruttifere) e livello di differenza visiva per i tre alberi.

**Vincoli:** non modificare la `main` stabile né le texture già approvate della 1.0.0 senza richiesta; nessun incremento della versione pubblicata in questa fase.
