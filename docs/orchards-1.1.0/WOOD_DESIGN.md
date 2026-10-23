# Orchard Update 1.1.0 — Legni, palette e inventario asset

**Stato:** progettazione. **Branch:** `feature/orchards-1.1.0`.  
**Scopo:** tenere distinta ogni **decisione approvata**, ogni **proposta in attesa di risposta** e ogni **riscontro oggettivo sui file**. Documento creato il 23 ottobre 2026.

## 1. Decisioni già approvate dall'autore

- **WD-001 (approvata):** l'ulivo, il mandorlo e il ciliegio fruttifero hanno **tre famiglie di legno originali**. Il ciliegio fruttifero **non** riutilizza il legno del ciliegio ornamentale vanilla.
- **WD-002 (approvata):** l'ulivo secolare usa il legno dell'ulivo normale, è raro in natura, ha un tronco principale **2×2** ed è coltivabile con **quattro germogli di ulivo disposti in un quadrato 2×2**. Un germoglio isolato cresce in un ulivo normale.
- **WD-003 (approvata):** conservare gli asset di ulivo già presenti: nessuna texture preesistente viene sovrascritta o eliminata senza esplicita approvazione. Le coppie di texture alternative derivano da materiali delle mod storiche **Italian's Delight** e **Rustic**, non sono state concepite per distinguere l'ulivo comune da quello secolare. La provenienza esatta di ogni file non è ancora attribuita.
- **WD-004 (approvata):** tutte le specie hanno foglie e germogli riconoscibili, fruttificazione raccolta con clic destro e ricrescita senza distruggere le foglie. Il ciliegio fruttifero ha foglie verdi e ciliegie rosse, ed è diverso da quello rosa vanilla.
- **WD-005 (approvata):** le nuove ricette di pasta e i piatti secondari sono contenuti bonus graduali degli aggiornamenti, non il fulcro della 1.1.0.

## 2. Inventario verificato degli asset ulivo su GitHub

Percorsi relativi a `src/main/resources/assets/italiansdelight/textures/`. **I PNG qui sotto sono presenti, ma non costituiscono blocchi implementati.** Lettura della testata PNG: tutti i file elencati sono **16×16 pixel**.

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

**Controllo struttura:** non risultano, nelle cartelle `blockstates/`, `models/block/` e `models/item/`, definizioni `olive_*` corrispondenti a una famiglia di blocchi giocabile. La registrazione e i modelli saranno da introdurre. Il colore dell'ulivo è ancorato agli asset presenti, **non** a una palette arbitraria nuova.

**Chiarimento dell'autore (23 ottobre 2026):** la coesistenza di file alternativi di tronco, foglie e germoglio è dovuta al recupero di risorse dalle vecchie mod **Italian's Delight** e **Rustic**. Non è un progetto di differenziazione tra ulivo comune e ulivo secolare. **Non assegnare automaticamente le varianti a specie, stadi o alberi differenti.** Prima di riutilizzare o redistribuire singoli file, verificare **quale mod è la fonte di ciascun asset, la licenza e le condizioni d'uso**; i crediti nella descrizione non sostituiscono i permessi necessari. In assenza di una licenza compatibile o autorizzazione, prevedere una texture nuova e originale. Conservare i file attuali intatti durante la verifica.

## 3. Proposta di direzione estetica — NON ancora approvata

| Famiglia | Tronco naturale suggerito | Legno lavorato suggerito | Foglie / frutto | Distinzione |
|---|---|---|---|---|
| **Olive Wood** (`olive_*`) | Corteccia grigio-bruna e irregolare; ulivo secolare dall'architettura più contorta | **Miele/dorato chiaro**, coerente con `olive_planks.png` già presente | Verde salvia / grigio-verde, olive visibili nella fase fruttifera | Conservare la texture legacy approvata o selezionata prima di inventarne una nuova |
| **Almond Wood** (`almond_*`) | Corteccia bruna calda, leggermente screpolata | **Nocciola caldo / beige rosato**, medio-chiaro, distinto dal miele dell'ulivo e dalla betulla vanilla | Verde naturale, con fioritura bianco-rosata come possibile variante; mandorle visibili | Identità neutra e calda, non copia di quercia/betulla |
| **Fruit Cherry Wood** (`fruit_cherry_*`) | Corteccia bruno-rossastra, con striature orizzontali | **Rame rosato / castano rossiccio**, più saturo dell'ulivo/mandorlo ma non rosa pastello come il legno del ciliegio vanilla | Foglie verdi, ciliegie rosse mature | Riconoscibile anche quando i frutti non sono presenti, distinto dal ciliegio ornamentale vanilla |

**Da approvare:** le direzioni cromatiche per mandorlo e ciliegio; per l'ulivo la selezione di asset utilizzabili **solo dopo** la verifica della loro provenienza e dei diritti. Le texture alternative storiche **non** sono candidate automatiche per l'ulivo secolare. La differenza visiva dell'albero secolare dovrà derivare innanzitutto da dimensioni, tronco 2×2 e forma della chioma, salvo future decisioni esplicite.

## 4. Famiglie e nomenclatura — proposta tecnica

Per ogni specie, perseguire progressivamente un set di costruzione coerente con Minecraft: `*_log`, `*_wood`, `stripped_*_log`, `stripped_*_wood`, `*_planks`, `*_stairs`, `*_slab`, `*_fence`, `*_fence_gate`, `*_door`, `*_trapdoor`, `*_button`, `*_pressure_plate`, `*_sign`, `*_hanging_sign` e germoglio/foglie. Valutare barche e barche con baule (comportamento completo, non soltanto item texture) in una fase successiva.

**Prefissi ID proposti:** `italiansdelight:olive_*`, `italiansdelight:almond_*`, `italiansdelight:fruit_cherry_*`. Evitare di registrare ID `minecraft:cherry_*` o sovrascrivere altri blocchi. Riutilizzare i modelli vanilla quando sufficienti, con texture dedicate; aggiungere correttamente loot, crafting, bruciabilità, linguaggi IT/EN, Creative e tag legno/foglie/germogli.

**Rischi da verificare:** esatta gestione Minecraft 26.2 delle famiglie legno, collocamento e orientamento dei blocchi, porta in due metà, cartelli, boat entity e data fixers quando applicabili, crescita 2×2 col terreno ingombro e generazione ai confini di chunk. Le risorse non ancora esistenti devono essere create solo dopo l'approvazione della direzione artistica.

## 5. Prossimi passi di progettazione

- [x] Inventariare i file dell'ulivo e distinguere varianti grafiche presenti.
- [x] Registrare le decisioni approvate sulle tre famiglie di legno e sull'ulivo secolare 2×2.
- [ ] **Approvazione della palette:** ulivo = asset attuali scelti; mandorlo = nocciola/beige caldo; ciliegio fruttifero = castano ramato/rossastro.
- [ ] Ricostruire la provenienza **file per file** delle texture ereditate da Italian's Delight e Rustic; verificare licenze/permessi e attribuzioni, quindi scegliere quale asset usare per l'ulivo comune. **Nessuna assegnazione automatica di varianti all'ulivo secolare.**
- [ ] Confermare dettaglio dell'insieme di blocchi extra (hanging signs, barche, mobili), senza confondere PNG esistenti con codice completato.
- [ ] Passare alla prima implementazione soltanto dopo la decisione di design, iniziando da registrazione e blocchi semplici; poi vegetazione/worldgen, fruttificazione e raccolta.

**Nessuna modifica a codice, PNG o versione durante questa fase documentale.** Tutte le future decisioni vanno registrate qui e nella [roadmap principale](ROADMAP.md).
