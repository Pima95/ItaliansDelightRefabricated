# Orchard Update 1.1.0 — Legni, palette e inventario asset

**Stato:** progettazione + prima implementazione del legno d'ulivo verificata. **Branch:** `feature/orchards-1.1.0`.  
**Scopo:** tenere distinta ogni **decisione approvata**, ogni **proposta in attesa di risposta** e ogni **riscontro oggettivo sui file**. Documento creato il 23 ottobre 2026.

## 1. Decisioni già approvate dall'autore

- **WD-001 (approvata):** l'ulivo, il mandorlo e il ciliegio fruttifero hanno **tre famiglie di legno originali**. Il ciliegio fruttifero **non** riutilizza il legno del ciliegio ornamentale vanilla.
- **WD-002 (approvata):** l'ulivo secolare usa il legno dell'ulivo normale, è raro in natura, ha un tronco principale **2×2** ed è coltivabile con **quattro germogli di ulivo disposti in un quadrato 2×2**. Un germoglio isolato cresce in un ulivo normale.
- **WD-003 (approvata):** conservare gli asset di ulivo già presenti: nessuna texture preesistente viene sovrascritta o eliminata senza esplicita approvazione. Le coppie di texture alternative derivano da materiali delle mod storiche **Italian's Delight** e **Rustic**, non sono state concepite per distinguere l'ulivo comune da quello secolare. La provenienza esatta di ogni file non è ancora attribuita.
- **WD-004 (approvata):** tutte le specie hanno foglie e germogli riconoscibili, fruttificazione raccolta con clic destro e ricrescita senza distruggere le foglie. Il ciliegio fruttifero ha foglie verdi e ciliegie rosse, ed è diverso da quello rosa vanilla.
- **WD-005 (approvata):** le nuove ricette di pasta e i piatti secondari sono contenuti bonus graduali degli aggiornamenti, non il fulcro della 1.1.0.

## 1.1. Selezione artistica: NO texture Rustic — DECISIONE APPROVATA

**Decisione dell'autore (23 ottobre 2026):** per i nuovi alberi e le nuove famiglie di legno della 1.1.0 **non utilizzare texture provenienti da Rustic**, perché i materiali originali appartengono a una mod della generazione **Minecraft 1.12.2** e sono considerati visivamente obsoleti per il nuovo progetto. La scelta riguarda i **nuovi blocchi e item** da implementare, non autorizza la cancellazione immediata delle risorse storiche presenti nel repository.

- **Esclusi esplicitamente:** `block/log_olive.png`, `block/leaves_olive.png`, `block/sapling_olive.png`, `item/olive_seed.png`, tutti verificati identici ai rispettivi asset di Rustic; allo stesso modo, eventuali altri file per cui viene verificata la provenienza Rustic.
- **Candidati per l'ulivo:** `block/olive_log.png`, `block/olive_log_top.png`, `block/stripped_olive_log.png`, `block/stripped_olive_log_top.png`, `block/olive_planks.png`, `block/olive_leaves.png`, `block/olive_sapling.png`, `block/olive_door_*.png`, `block/olive_trapdoor.png`; i loro blob **non** corrispondono ai file del repository Rustic consultato, ma occorre distinguere **assenza di corrispondenza** da **prova della provenienza Italian's Delight**.
- **Mandorlo e ciliegio da frutto:** nuove texture coerenti con lo stile attuale di Minecraft e con i propri legni originali; palette definitive ancora da confermare.
- **Ulivo secolare:** condividere le texture dell'ulivo normale; la diversità visiva sarà la forma naturale (tronco 2×2, ramificazione/chioma), **non** il recupero dei vecchi file Rustic.
- **Conservazione storica:** non eliminare o modificare i PNG legacy in questa fase; non referenziarli nei nuovi modelli, blockstate, item o worldgen.
- **Verifica:** il nuovo set di registrazioni/modelli della 1.1.0 dovrà essere accompagnato da un controllo automatico che neghi riferimenti ai quattro file Rustic identificati, inclusi riferimenti indiretti.

Questa decisione prevale sulle precedenti proposte di usare i due gruppi di asset come varianti della chioma o della corteccia.

## 2. Inventario verificato degli asset ulivo su GitHub

Percorsi relativi a `src/main/resources/assets/italiansdelight/textures/`. I PNG elencati sono **16×16 pixel**. Una parte è ora effettivamente collegata a blocchi implementati: **tronco, legno intero, varianti scortecciate e assi**; per foglie, germogli, porte e altri oggetti la sola presenza della PNG non implica che siano stati implementati.

| Tipo | Texture trovate | Nota visiva / uso da valutare |
|---|---|---|
| Corteccia lato | `block/olive_log.png`, `block/log_olive.png` | Due file **diversi** con corteccia dall'aspetto differente; confrontare prima di scegliere |
| Corteccia superiore | `block/olive_log_top.png` | Presente |
| Scortecciato lato e superiore | `block/stripped_olive_log.png`, `block/stripped_olive_log_top.png` | Presenti |
| Tavole | `block/olive_planks.png` | Tavole di tonalità **calda, chiara e dorata** |
| Foglie | `block/olive_leaves.png`, `block/leaves_olive.png` | **Due disegni distinti**: il primo verde scuro, il secondo molto più chiaro / verde-grigiastro; non confondere con duplicati identici |
| Germoglio | `block/olive_sapling.png`, `block/sapling_olive.png` | **Due disegni distinti**, confrontare silhouette e coerenza con la chioma finale |
| Porta | `block/olive_door_bottom.png`, `block/olive_door_top.png`; `item/olive_door.png` | Presenti |
| Botola | `block/olive_trapdoor.png` | Presente |
| Cartello / barche | `item/olive_sign.png`, `item/olive_boat.png`, `item/olive_chest_boat.png` | Esistono gli **item**, non è garantita la presenza di modelli/texture entity/blocchi corrispondenti |
| Altri | `block/olive_cabinet_*.png`, `item/olive_seed.png`, `item/olives.png` | Asset recuperabili; mobile e semi/frutti non comportano approvazione di nuove meccaniche |

**Controllo struttura aggiornato:** le cartelle `blockstates/`, `models/block/` e `models/item/` contengono oggi i file per i primi cinque blocchi da costruzione dell'ulivo, **tutti verificati in gioco** dall'autore il 24 ottobre 2026 ([report](OLIVE_WOOD_TESTS.md)). Non sono ancora presenti gli alberi con foglie e frutti né l'intera famiglia di arredi. Il colore rimane basato sugli asset scelti, non su una palette arbitraria nuova.

**Chiarimento dell'autore (23 ottobre 2026):** la coesistenza di file alternativi di tronco, foglie e germoglio è dovuta al recupero di risorse dalle vecchie mod **Italian's Delight** e **Rustic**. Non è un progetto di differenziazione tra ulivo comune e ulivo secolare. **Non assegnare automaticamente le varianti a specie, stadi o alberi differenti.** Prima di riutilizzare o redistribuire singoli file, verificare **quale mod è la fonte di ciascun asset, la licenza e le condizioni d'uso**; i crediti nella descrizione non sostituiscono i permessi necessari. In assenza di una licenza compatibile o autorizzazione, prevedere una texture nuova e originale. Conservare i file attuali intatti durante la verifica.

**Risultato dell'audit GitHub (23 ottobre 2026):** le pagine CurseForge dell'[originale Italian's Delight](https://www.curseforge.com/minecraft/mc-mods/italians-delight/license) e di [Rustic](https://www.curseforge.com/minecraft/mc-mods/rustic/license) indicano entrambe **MIT**. Il confronto diretto con l'[albero sorgente di Rustic](https://github.com/cadaverous-eris/Rustic) ha trovato **41 corrispondenze binarie esatte** tra texture e file `.mcmeta` del nostro repository: inventario completo nel [rapporto di corrispondenze](RUSTIC_ASSET_MATCHES.md). Per il solo ulivo, i quattro asset identici sono `block/log_olive.png`, `block/leaves_olive.png`, `block/sapling_olive.png`, `item/olive_seed.png`. **Non è ancora provata** la provenienza degli altri file `olive_*`: MIT del progetto storico non permette di attribuirli automaticamente a un determinato autore. Aggiunto [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) per i crediti e gli avvisi di licenza; approfondire in futuro eventuali autori/avvisi specifici non presenti su CurseForge.

## 3. Proposta di direzione estetica — NON ancora approvata

| Famiglia | Tronco naturale suggerito | Legno lavorato suggerito | Foglie / frutto | Distinzione |
|---|---|---|---|---|
| **Olive Wood** (`olive_*`) | Corteccia grigio-bruna e irregolare; ulivo secolare dall'architettura più contorta | **Miele/dorato chiaro**, coerente con `olive_planks.png` già presente | Verde salvia / grigio-verde, olive visibili nella fase fruttifera | Conservare la texture legacy approvata o selezionata prima di inventarne una nuova |
| **Almond Wood** (`almond_*`) | Corteccia bruna calda, leggermente screpolata | **Nocciola caldo / beige rosato**, medio-chiaro, distinto dal miele dell'ulivo e dalla betulla vanilla | Verde naturale, con fioritura bianco-rosata come possibile variante; mandorle visibili | Identità neutra e calda, non copia di quercia/betulla |
| **Fruit Cherry Wood** (`fruit_cherry_*`) | Corteccia bruno-rossastra, con striature orizzontali | **Rame rosato / castano rossiccio**, più saturo dell'ulivo/mandorlo ma non rosa pastello come il legno del ciliegio vanilla | Foglie verdi, ciliegie rosse mature | Riconoscibile anche quando i frutti non sono presenti, distinto dal ciliegio ornamentale vanilla |

**Da approvare:** le direzioni cromatiche per mandorlo e ciliegio; per l'ulivo la selezione di asset utilizzabili **solo dopo** la verifica della loro provenienza e dei diritti. Le texture alternative storiche **non** sono candidate automatiche per l'ulivo secolare. La differenza visiva dell'albero secolare dovrà derivare innanzitutto da dimensioni, tronco 2×2 e forma della chioma, salvo future decisioni esplicite.

### Stato dei modelli dopo la Fase A (in attesa di collaudo)

Per le nuove **scale, lastre, recinzioni, cancelli, pulsanti e pedane a pressione** sono stati riutilizzati i modelli geometrici vanilla applicando unicamente `block/olive_planks.png`. **Non sono necessarie texture PNG dedicate** per questi elementi. Le texture preesistenti di porta `olive_door_bottom.png`, `olive_door_top.png` e botola `olive_trapdoor.png` verranno riutilizzate nella Fase B; `item/olive_door.png` è già presente.

Tutti i sei nuovi blocchi sono presenti nel codice e nelle risorse del branch 1.1.0, ma **non ancora dichiarati superati nei test in gioco**.

### Nuovi asset effettivamente riutilizzati (Fase B e barche)

Le due texture verticali della porta, la botola e le relative icone sono già presenti. Anche la barca normale e la barca con baule dispongono delle proprie texture **di entità 3D**. L'aging rack utilizza `olive_planks.png` e gli stessi modelli di tutte le altre famiglie; è la 13ª variante di scaffale, con registrazione nel tipo di block entity condiviso.

Tutto il codice, le ricette e le definizioni sono in fase di collaudo; vedi [OLIVE_EXPANSION_TESTS.md](OLIVE_EXPANSION_TESTS.md).

### Fix orientamento della botola e ultimo blocco edilizio da progettare (24 ottobre 2026)

Segnalata e corretta la texture della botola d'ulivo che da chiusa non seguiva la direzione di posa. Ogni variante nord/est/sud/ovest ora ruota il modello in modo coerente con la variante aperta, tanto in metà bassa quanto alta; **il PNG originale non è stato modificato**. CI: verifica dedicata sui 16 blockstate; collaudo finale in gioco ancora richiesto.

**Ancora mancanti nella famiglia completa:** cartello d'ulivo `olive_sign` (a terra e a parete) e cartello appeso `olive_hanging_sign` (soffitto e parete), sfruttando le due texture di entità già presenti. `olive_cabinet_*` indica un possibile mobile aggiuntivo, facoltativo e non equivalente a un blocco di legno vanilla. Non confondere foglie/germogli con blocchi decorativi: sono previsti nella fase alberi e frutti.

### Ulivo: cartelli, cabinet e Cutting Board — decisione approvata 24 ottobre 2026

**Implementazione completata sul branch (24 ottobre 2026):** `olive_sign` con blocco da terra/a parete, `olive_hanging_sign` con blocco da soffitto/a parete, cabinet `olive_cabinet` compatibile con il block entity di Farmer's Delight, e le due ricette `cutting/olive_log`, `cutting/olive_wood` (ascia + corteccia). I nuovi PNG 16×16 sono **soltanto** `block/olive_sign.png`, `block/olive_hanging_sign.png` e `item/olive_hanging_sign.png`, tutti derivati dalla palette dell'ulivo, senza modificare le texture originali. **Compilazione Java 25 e controlli statici riusciti** (workflow [40042164597](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40042164597)). **Test manuali non ancora confermati:** [checklist](OLIVE_SIGNS_CABINET_CUTTING_TESTS.md). Il test della botola resta indipendente.



**Richiesta dell'autore:** implementare cartelli normali e appesi, cabinet d'ulivo e compatibilità per scortecciare legno d'ulivo sulla Cutting Board di Farmer's Delight Refabricated, mentre l'autore ritesta la botola. Solo branch `feature/orchards-1.1.0`, nessun merge su `main`.

- **Cartelli vanilla-equivalenti:** cartello d'ulivo piazzabile a terra o a parete, cartello appeso d'ulivo piazzabile a soffitto o a parete; testo e interazioni standard vanilla, ricette, item e drop. I vecchi `textures/entity/sign/olive.png` e `textures/entity/sign/hanging/olive.png` sono presenti, ma appartengono al formato di Minecraft anteriore alla **26.2**: la versione 26.2 usa modelli/texture di blocco. **Non sovrascriverli.**
- **Grafica dei cartelli:** produrre solo i PNG nuovi indispensabili, in **pixel art Minecraft 16×16 RGBA**, colori campionati da `olive_planks.png` e dall'icona originale `olive_sign.png`: alfa di ciascun pixel esattamente 0 o 255, nessun antialiasing o sfumatura; sfondo esterno alle silhouette degli item trasparente; nessun riferimento alle texture Rustic obsolete.
- **Cabinet d'ulivo:** riutilizzare i quattro file già esistenti `olive_cabinet_front.png`, `olive_cabinet_front_open.png`, `olive_cabinet_side.png`, `olive_cabinet_top.png` e il **comportamento del Cabinet di Farmer's Delight** (27 slot, apertura, menu, redstone, salvataggio e compatibilità col BlockEntityType). Evitare di duplicare la logica inventario se l'API originale permette di aggiungere un altro blocco allo stesso tipo.
- **Scortecciatura sulla Cutting Board:** aggiungere **due ricette native `farmersdelight:cutting`** per `olive_log` e `olive_wood`, con ascia o equivalente `axe_strip`: restituiscono la rispettiva variante scortecciata **e** `farmersdelight:tree_bark`, un'unità ciascuno, suono vanilla `minecraft:item.axe.strip`. Il clic destro con ascia sui blocchi già funziona e non va modificato.
- **Accettazione:** build Java 25/CI, controlli sulle nuove texture e sulle ricette; **test manuali ancora richiesti** per posizionamento dei quattro cartelli, testo, persistenza, cabinato con item, tag e Cutting Board/JEI. Non riportare i test come superati senza riscontro in gioco dell'autore.

## 4. Famiglie e nomenclatura — proposta tecnica

Per ogni specie, perseguire progressivamente un set di costruzione coerente con Minecraft: `*_log`, `*_wood`, `stripped_*_log`, `stripped_*_wood`, `*_planks`, `*_stairs`, `*_slab`, `*_fence`, `*_fence_gate`, `*_door`, `*_trapdoor`, `*_button`, `*_pressure_plate`, `*_sign`, `*_hanging_sign` e germoglio/foglie. Valutare barche e barche con baule (comportamento completo, non soltanto item texture) in una fase successiva.

**Prefissi ID proposti:** `italiansdelight:olive_*`, `italiansdelight:almond_*`, `italiansdelight:fruit_cherry_*`. Evitare di registrare ID `minecraft:cherry_*` o sovrascrivere altri blocchi. Riutilizzare i modelli vanilla quando sufficienti, con texture dedicate; aggiungere correttamente loot, crafting, bruciabilità, linguaggi IT/EN, Creative e tag legno/foglie/germogli.

**Rischi da verificare:** esatta gestione Minecraft 26.2 delle famiglie legno, collocamento e orientamento dei blocchi, porta in due metà, cartelli, boat entity e data fixers quando applicabili, crescita 2×2 col terreno ingombro e generazione ai confini di chunk. Le risorse non ancora esistenti devono essere create solo dopo l'approvazione della direzione artistica.

## 5. Prossimi passi di progettazione

- [x] Inventariare i file dell'ulivo e distinguere varianti grafiche presenti.
- [x] Registrare le decisioni approvate sulle tre famiglie di legno e sull'ulivo secolare 2×2.
- [ ] **Approvazione della palette:** ulivo = asset attuali scelti; mandorlo = nocciola/beige caldo; ciliegio fruttifero = castano ramato/rossastro.
- [x] Accertare **la licenza MIT indicata su CurseForge** per Italian's Delight e Rustic e confrontare gli asset con il repository Rustic; identificate **41 corrispondenze binarie**, inclusi quattro PNG relativi all'ulivo; redatti [rapporto](RUSTIC_ASSET_MATCHES.md) e [avvisi](../../THIRD_PARTY_NOTICES.md).
- [x] **Escludere tutti gli asset identificati come Rustic dai nuovi elementi della 1.1.0** (decisione dell'autore); conservare i PNG storici senza cancellarli.
- [ ] Completare la **provenienza file per file degli asset non corrispondenti**, incluse eventuali attribuzioni agli altri artisti; usare soltanto asset non-Rustic per il nuovo ulivo. **Nessuna assegnazione automatica di varianti all'ulivo secolare.**
- [ ] Confermare dettaglio dell'insieme di blocchi extra (hanging signs, barche, mobili), senza confondere PNG esistenti con codice completato.
- [x] **Prima implementazione verificata:** cinque blocchi di legno d'ulivo, build Java 25 CI riuscita e **6/6 test Minecraft confermati** ([report](OLIVE_WOOD_TESTS.md)).
- [ ] **Prossimo lotto proposto:** scale, lastre, recinzioni, cancelli, pulsanti e pedane a pressione; successivamente porte/botole e altri blocchi. Poi vegetazione/worldgen, fruttificazione e raccolta.

**Primo lotto di codice già presente e verificato;** le presenti modifiche documentali **non** cambiano codice, PNG o versione pubblicata. Tutte le decisioni future saranno registrate qui e nella [roadmap principale](ROADMAP.md).
