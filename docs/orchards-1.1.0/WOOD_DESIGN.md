# Orchard Update 1.1.0 — Legni, palette e inventario asset

**Stato:** progettazione + prima implementazione del legno d'ulivo verificata. **Branch:** `feature/orchards-1.1.0`.  
**Scopo:** tenere distinta ogni **decisione approvata**, ogni **proposta in attesa di risposta** e ogni **riscontro oggettivo sui file**. Documento creato il 23 ottobre 2026.

## 1. Decisioni già approvate dall'autore

- **WD-001 (approvata):** l'ulivo, il mandorlo e il ciliegio fruttifero hanno **tre famiglie di legno originali**. Il ciliegio fruttifero **non** riutilizza il legno del ciliegio ornamentale vanilla.
- **WD-002 (approvata):** l'ulivo secolare usa il legno dell'ulivo normale, è raro in natura, ha un tronco principale **2×2** ed è coltivabile con **quattro germogli di ulivo disposti in un quadrato 2×2**. Un germoglio isolato cresce in un ulivo normale.
- **WD-003 (approvata):** conservare gli asset di ulivo già presenti: nessuna texture preesistente viene sovrascritta o eliminata senza esplicita approvazione. Le coppie di texture alternative derivano da materiali delle mod storiche **Italian's Delight** e **Rustic**, non sono state concepite per distinguere l'ulivo comune da quello secolare. La provenienza esatta di ogni file non è ancora attribuita.
- **WD-004 (approvata):** tutte le specie hanno foglie e germogli riconoscibili, fruttificazione raccolta con clic destro e ricrescita senza distruggere le foglie. Il ciliegio fruttifero ha foglie verdi e ciliegie rosse, ed è diverso da quello rosa vanilla.
- **WD-005 (approvata):** le nuove ricette di pasta e i piatti secondari sono contenuti bonus graduali degli aggiornamenti, non il fulcro della 1.1.0.

### Regola specifica del mandorlo — sezioni di tronco, 26 ottobre 2026

**I due tagli devono mostrare lo stesso legno.** Per `almond_log_top` e `stripped_almond_log_top`, il nucleo **14×14** è identico e usa solo la palette approvata di `almond_planks.png`: lo scortecciamento cambia **soltanto il bordo esterno di 1 pixel**, non il colore dell'interno. Le texture laterali mantengono il bruno-nocciola approvato, ma devono presentare venature brevi, sfalsate e leggermente curve, non solchi dritti o un semplice recolor della quercia. Questa regola sostituisce ogni precedente descrizione di durame interno normale più scuro e scortecciato più chiaro.


**Revisione grafica eseguita il 26 ottobre 2026 — in attesa di verifica dell'autore in Minecraft.** Sono state aggiornate esclusivamente le quattro immagini `almond_log.png`, `almond_log_top.png`, `stripped_almond_log.png` e `stripped_almond_log_top.png`. Disegno originale, non copia o recolor pixel-per-pixel di un tronco vanilla: corteccia composta da placche irregolari, fessure brevi sfalsate e ombre con contrasto controllato; superficie scortecciata con fibre a leggere deviazioni e **soltanto i sei colori di `almond_planks.png`**. Il disegno degli anelli di crescita è stato inizialmente progettato su un'area 12×12 con bordo da 2 pixel; **questa versione è stata superata dalla revisione successiva (26/10/2026): area centrale 14×14 identica, bordo da 1 pixel**, corteccia bruna nel tronco normale e legno chiaro nello scortecciato. Le assi approvate restano binariamente identiche. Un nuovo controllo CI garantisce 16×16 RGBA, opacità totale, palette, coerenza delle sezioni e riferimenti dei modelli. Il collaudo in gioco resta da eseguire.

### Rifinitura estetica: anelli quadrati e fibre coerenti — 26 ottobre 2026

**Feedback in Minecraft:** la cornice da 1 pixel e i colori dei tagli erano approvati, ma le linee di accrescimento erano troppo tondeggianti, mentre i fianchi apparivano come una distribuzione casuale di pixel anziché vera corteccia.

**Revisione implementata, da validare visivamente:**
- Sezioni `almond_log_top` e `stripped_almond_log_top`: nucleo **14×14 identico pixel per pixel**, composto esclusivamente dai colori già approvati di `almond_planks.png`, con **tre anelli quadrati concentrici da 4×4, 8×8 e 12×12**, ciascuno formato da lati retti, angoli di 90° e spessore di un pixel. Lievi tocchi cromatici secondari non interrompono gli anelli.
- La **cornice esterna da 1 pixel** resta differente: bruno di corteccia per il normale, legno esposto chiaro per lo scortecciato. Nessuna variazione artificiale dei colori interni.
- `almond_log`: fessure longitudinali che si piegano di un pixel, con nervature illuminate accanto ai solchi, placche definite e un piccolo nodo coerente con la corteccia.
- `stripped_almond_log`: fibre più morbide ma tracciabili lungo l'altezza, piccole deviazioni a passo di pixel e un nodo discreto. Palette identica alle assi.
- **Test dedicati** per anelli interamente quadrati, bordi e centro invarianti, orientamento delle fibre (continuità verticale maggiore delle ripetizioni orizzontali), opacità 16×16 e SHA delle assi.
- `almond_planks.png`, asset d'ulivo, codice, ricette, configurazioni di alberi, `main` e generazione naturale restano invariati.

**Accettazione estetica delle nuove texture ancora aperta** finché non saranno verificate in gioco.

### Ulteriore direzione approvata dall'autore (26 ottobre 2026): riferimento reale per la corteccia, taglio oak vanilla

Su richiesta dell'autore e con riferimento alla **fotografia di corteccia di mandorlo allegata in chat**, le texture laterali `almond_log.png` devono mostrare **placche verticali riconoscibili, solchi profondi scuri, creste illuminate parallele ai solchi e fenditure con lievi deviazioni laterali**. Il disegno è originale alla scala 16×16: non è una copia fotografica, né pixel casuali, né una ricolorazione della corteccia di quercia.

`stripped_almond_log.png` corrisponde allo stesso legno privato della corteccia: niente fenditure profonde, ma **fibre verticali più chiare e continue**, con leggere curvature e nodi poco contrastati.

Per `almond_log_top.png` e `stripped_almond_log_top.png` l'autore ha chiesto **una vera ricolorazione della sezione `minecraft:oak_log_top` vanilla 26.2** (non più gli anelli geometrici inventati). Il nucleo condiviso **14×14** mantiene *esattamente* la disposizione dei pixel dell'originale oak, ricampionando i colori nei sei toni già approvati di `almond_planks.png`. Cambia solo la **cornice perimetrale di 1 pixel** (corteccia scura nel normale e legno chiaro nello scortecciato). Le teste superiore e inferiore sono gestite dal medesimo modello `cube_column`, dunque mostrano la stessa texture senza ulteriori asset.

Questa direttiva sostituisce la precedente richiesta di disegnare tre quadrati geometrici perfettamente uniformi 4×4, 8×8 e 12×12: il **riferimento esatto è il disegno oak originale**, con i suoi contorni quadrangolari e le sue naturali variazioni. Le assi approvate restano immutate; prima dell'accettazione definitiva l'autore verificherà le texture in Minecraft.

## 1.1. Selezione artistica: NO texture Rustic — DECISIONE APPROVATA

**Decisione dell'autore (23 ottobre 2026):** per i nuovi alberi e le nuove famiglie di legno della 1.1.0 **non utilizzare texture provenienti da Rustic**, perché i materiali originali appartengono a una mod della generazione **Minecraft 1.12.2** e sono considerati visivamente obsoleti per il nuovo progetto. La scelta riguarda i **nuovi blocchi e item** da implementare, non autorizza la cancellazione immediata delle risorse storiche presenti nel repository.

- **Esclusi esplicitamente:** `block/log_olive.png`, `block/leaves_olive.png`, `block/sapling_olive.png`, `item/olive_seed.png`, tutti verificati identici ai rispettivi asset di Rustic; allo stesso modo, eventuali altri file per cui viene verificata la provenienza Rustic.
- **Candidati per l'ulivo:** `block/olive_log.png`, `block/olive_log_top.png`, `block/stripped_olive_log.png`, `block/stripped_olive_log_top.png`, `block/olive_planks.png`, `block/olive_leaves.png`, `block/olive_sapling.png`, `block/olive_door_*.png`, `block/olive_trapdoor.png`; i loro blob **non** corrispondono ai file del repository Rustic consultato, ma occorre distinguere **assenza di corrispondenza** da **prova della provenienza Italian's Delight**.
- **Mandorlo e ciliegio da frutto:** nuove texture coerenti con lo stile attuale di Minecraft e con i propri legni originali; palette definitive ancora da confermare. **Nuova priorità dell'autore del 25 ottobre 2026:** sviluppare prima entrambe le famiglie legno e gli alberi coltivabili, poi una sola meccanica di raccolta condivisa dalle tre specie, e soltanto alla fine il worldgen naturale.
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

### Correzione dei cartelli Minecraft 26.2 e audit crafting (24 ottobre 2026)

**Segnalazioni osservate negli screenshot dell'autore:** cartelli in gioco con mappe UV deformate, editor del testo viola/nero per entrambe le varianti e icona del cartello appeso inadeguata.

**Diagnosi verificata confrontando le risorse vanilla 26.2** (`InventivetalentDev/minecraft-assets`, ref `26.2`):
- I modelli 3D `template_sign_rot_*` / `template_hanging_sign_rot_*` usano mappe **32×32** (non 16×16): `textures/block/olive_sign.png` e `textures/block/olive_hanging_sign.png` sono state rigenerate seguendo esattamente le regioni UV dei PNG vanilla, ricolorate con la palette approvata `olive_planks.png`. Le proporzioni dipendono dai template vanilla, non da un modello improvvisato.
- Gli editor del testo sono separati dai modelli 3D e richiedono `textures/gui/signs/olive.png` (**24×26**) e `textures/gui/hanging_signs/olive.png` (**16×16**). Aggiunte entrambe le risorse con gli stessi contorni della versione vanilla, eliminando la causa delle texture mancanti.
- Quattro modelli degli appesi fissati al soffitto usavano l'inesistente `minecraft:block/template_hanging_sign_attached_rot_*`. Corretto in `minecraft:block/template_attached_hanging_sign_rot_*`.
- **`textures/item/olive_hanging_sign.png`** ricreata **16×16** rispettando silhouette/catene vanilla. **`textures/item/olive_sign.png` non è stata modificata**, per esplicita richiesta dell'autore (SHA Git originale verificato in CI). I PNG storici Rustic ed entity pre-26.2 non sono stati riutilizzati.
- Rivista la costruzione dei crafting: `minecraft:iron_chain` anziché `minecraft:chain` per il cartello appeso; cabinet con **6 lastre d'ulivo e 2 botole d'ulivo** come il cabinet Farmer's Delight Refabricated 26.2, anziché 8 assi. Corretti anche gruppi e categorie delle ricette basate sulle equivalenti vanilla.
- Nuova validazione `.github/scripts/check_all_crafting_26_2.py`: controlla **tutti i crafting shaped/shapeless della mod** (struttura, chiavi, categorie, output) più gli input/output delle **17 ricette della famiglia d'ulivo** e le 2 ricette Cutting Board. **Verifiche statiche, non conferma di crafting eseguiti in gioco**.

**Stato:** modifiche inviate su `feature/orchards-1.1.0`; verifica Java 25 GitHub Actions e soprattutto test nel client Minecraft da registrare separatamente. La versione 1.0.0 pubblicata e `main` rimangono inalterate. [Checklist aggiornata](OLIVE_SIGNS_CABINET_CUTTING_TESTS.md).

### Ulivo: cartelli, cabinet e Cutting Board — decisione approvata 24 ottobre 2026

**Implementazione completata sul branch (24 ottobre 2026):** `olive_sign` con blocco da terra/a parete, `olive_hanging_sign` con blocco da soffitto/a parete, cabinet `olive_cabinet` compatibile con il block entity di Farmer's Delight, e le due ricette `cutting/olive_log`, `cutting/olive_wood` (ascia + corteccia). Le prime mappe dei cartelli erano state generate erroneamente 16×16; la correzione successiva impiega 32×32 per i blocchi, 16×16 per l'item e aggiunge i due sfondi GUI, tutti derivati dalla palette dell'ulivo, senza modificare le texture originali. **Compilazione Java 25 e controlli statici riusciti** (workflow [40042164597](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40042164597)). **Test manuali non ancora confermati:** [checklist](OLIVE_SIGNS_CABINET_CUTTING_TESTS.md). Il test della botola resta indipendente.



**Richiesta dell'autore:** implementare cartelli normali e appesi, cabinet d'ulivo e compatibilità per scortecciare legno d'ulivo sulla Cutting Board di Farmer's Delight Refabricated, mentre l'autore ritesta la botola. Solo branch `feature/orchards-1.1.0`, nessun merge su `main`.

- **Cartelli vanilla-equivalenti:** cartello d'ulivo piazzabile a terra o a parete, cartello appeso d'ulivo piazzabile a soffitto o a parete; testo e interazioni standard vanilla, ricette, item e drop. I vecchi `textures/entity/sign/olive.png` e `textures/entity/sign/hanging/olive.png` sono presenti, ma appartengono al formato di Minecraft anteriore alla **26.2**: la versione 26.2 usa modelli/texture di blocco. **Non sovrascriverli.**
- **Grafica dei cartelli:** la regola 16×16 si applica agli item; i blocchi 3D dei cartelli richiedono 32×32 e le schermate dell'editor 24×26/16×16; pixel art RGBA, colori campionati da `olive_planks.png` e dall'icona originale `olive_sign.png`: alfa di ciascun pixel esattamente 0 o 255, nessun antialiasing o sfumatura; sfondo esterno alle silhouette degli item trasparente; nessun riferimento alle texture Rustic obsolete.
- **Cabinet d'ulivo:** riutilizzare i quattro file già esistenti `olive_cabinet_front.png`, `olive_cabinet_front_open.png`, `olive_cabinet_side.png`, `olive_cabinet_top.png` e il **comportamento del Cabinet di Farmer's Delight** (27 slot, apertura, menu, redstone, salvataggio e compatibilità col BlockEntityType). Evitare di duplicare la logica inventario se l'API originale permette di aggiungere un altro blocco allo stesso tipo.
- **Scortecciatura sulla Cutting Board:** aggiungere **due ricette native `farmersdelight:cutting`** per `olive_log` e `olive_wood`, con ascia o equivalente `axe_strip`: restituiscono la rispettiva variante scortecciata **e** `farmersdelight:tree_bark`, un'unità ciascuno, suono vanilla `minecraft:item.axe.strip`. Il clic destro con ascia sui blocchi già funziona e non va modificato.
- **Accettazione:** build Java 25/CI, controlli sulle nuove texture e sulle ricette; **test manuali ancora richiesti** per posizionamento dei quattro cartelli, testo, persistenza, cabinato con item, tag e Cutting Board/JEI. Non riportare i test come superati senza riscontro in gioco dell'autore.

### Ritocco artistico richiesto dall'autore — cartelli d'ulivo (24 ottobre 2026)

L'autore ha **confermato che i modelli dei cartelli e il funzionamento del testo sono corretti** dopo la revisione vanilla 26.2, ma ha chiesto tre correzioni mirate:

- **Cartello normale:** lo stelo nella texture 3D 32×32 era arancio-marrone e non coerente con il ceppo storico. La vecchia texture `textures/entity/sign/olive.png` mostrava invece lo stelo con la corteccia verde-grigiastra dell'ulivo. Sostituiti **solo i pixel del supporto** nelle regioni UV del template `template_sign_rot_*`, usando colori e venature di `textures/block/olive_log.png` (asset più recente, **non Rustic**). Pannello e modello invariati.
- **Cartello appeso:** la vecchia texture `textures/entity/sign/hanging/olive.png` aveva una superficie chiara, coerente con `stripped_olive_log.png`. La nuova mappa 32×32 era troppo scura: ricolorate le parti in legno usando **esattamente le cinque tonalità** del tronco scortecciato, riprendendone direttamente le venature nelle facce frontali/posteriori; **catene metalliche e UV originali invariati**.
- **Item cartello normale:** applicata una lieve correzione (20 pixel di glifi sul pannello) per uniformare la scritta stilizzata a quella del cartello appeso, mantenendo intatti sagoma, stelo, bordo e sfondo trasparente. **Icona del cartello appeso invariata**.

**Nessun modello, blockstate, screen editor, script di gameplay, crafting o texture Rustic modificato.** Controlli CI ampliati: PNG 32×32/16×16 con alpha binario, colori dello stelo ricavati dal tronco, palette del cartello appeso limitata ai cinque toni scortecciati e colori delle catene, checksum del nuovo item normale. **Test in-game di resa estetica e accettazione artistica ancora da confermare.** La precedente conferma riguarda soltanto geometria e testo.

### Texture delle foglie d'ulivo — scelta dell'autore (24 ottobre 2026)

- **Usare** `textures/block/olive_leaves.png` già presente, che non corrisponde byte-per-byte al campione di Rustic. La provenienza artistica esatta continua a richiedere verifica, ma non va sostituita senza consenso.
- **Eliminare** `textures/block/leaves_olive.png` dal branch 1.1.0: corrispondenza binaria esatta con Rustic (`c90b2ece...`) già documentata, rimozione esplicitamente richiesta dall'autore.
- Implementazione limitata a un normale `LeavesBlock` (decay a distanza 7, persistenza, acqua, cesoie, Silk Touch, bastoni e comportamento del fuoco), senza frutta/germogli/worldgen fino alla fase successiva.
- Nel primo lotto foglie non sono stati rimossi gli altri asset Rustic; **nel successivo ritocco il file `sapling_olive.png` è stato eliminato su richiesta dell'autore**. Rimangono i file storici `log_olive.png` e `item/olive_seed.png`, che non vanno usati nei nuovi modelli.

Vedi [i controlli manuali](OLIVE_LEAVES_TESTS.md).

### Revisione cromatica foglie e pulizia germogli Rustic — 24 ottobre 2026

**Conferma dall'autore:** le foglie introdotte nel primo lotto **funzionano nel gioco**; tuttavia la texture esistente appariva troppo verde, priva del tipico colore argento degli ulivi mediterranei. Non è stata confermata singolarmente ogni voce della checklist (né un'approvazione cromatica).

**Nuova palette** per `assets/italiansdelight/textures/block/olive_leaves.png`: quattro colori opachi **#445B4E** (ombra verde-grigia), **#687F6E** (salvia), **#92A38F** (verde argento) e **#C6D1C2** (riflesso argenteo chiaro). I pixel occupati restano esattamente **168** e quelli trasparenti **88** su 16×16, senza sfumature, antialiasing, alterazioni di silhouette o modifiche al codice di decadimento. La colorazione fissa è conservata dalla classe `UntintedParticleLeavesBlock`, indipendentemente dal bioma. Palette ispirata ai veri ulivi con foglie grigio-verdi e pagina inferiore argentea.

**Seconda pulizia autorizzata:** rimossa esclusivamente `textures/block/sapling_olive.png` (Git blob storico `f03d6d4cdea69c6952e773c87d3ade4a5ecc9458`, corrispondente a Rustic). Resta intatta `textures/block/olive_sapling.png` (`be6c9b048a2e029fac39df202587a412961d7fbc`), candidata per il germoglio coltivabile della fase successiva. **Non** è ancora stato registrato un nuovo blocco germoglio: prima va implementata la generazione dell'ulivo normale e dell'ulivo secolare 2×2, così il germoglio potrà effettivamente crescere. Nessun altro asset Rustic è stato eliminato.

**Test ancora da effettuare:** valutazione del nuovo colore argentato in gioco, al sole e all'ombra, e controllo del nuovo PNG in Creativa. [Checklist aggiornata](OLIVE_LEAVES_TESTS.md).

### Germoglio d'ulivo e crescita normale / secolare 2×2 — implementazione (24 ottobre 2026)

**Problema segnalato dall'autore:** il germoglio non compariva in Minecraft perché prima era stata conservata **soltanto la texture**, senza registrazione di blocco o BlockItem. **Risolto**: `italiansdelight:olive_sapling` ora è un `SaplingBlock` posizionabile, visibile in Creativa, con nome IT/EN, modello a croce, drop e tag vanilla.

**Alberi coltivabili:** lo stesso `TreeGrower` seleziona `olive_tree` per un germoglio isolato e `ancient_olive_tree` se trova **quattro germogli d'ulivo nel quadrato 2×2**. Le configurazioni di feature 26.2 impiegano sempre `olive_log` e `olive_leaves`; il normale usa un tronco ramificato vanilla di altezza moderata e corona arrotondata, il secolare usa tronco massiccio 2×2 con generatore vanilla `dark_oak_trunk_placer` e chioma ampia. È una prima geometria vanilla affidabile, **da valutare visivamente** per un ulivo contorto e mediterraneo, senza dichiararla definitiva. Le foglie generate sono naturali (`persistent=false`) e decadono se manca il tronco.

**Generazione naturale provvisoria (annullata):** l'esperimento iniziale su pianure e savane è stato rimosso; non vengono generati nuovi ulivi naturalmente. La crescita manuale tramite germogli rimane disponibile.

**Riproduzione in Sopravvivenza:** le foglie d'ulivo ora possono rilasciare il germoglio con probabilità 5% base (Fortuna fino a 10%), imitando il meccanismo di altre foglie vanilla; cesoie e Silk Touch mantengono il drop del blocco, non dei germogli. Niente olive o frutti fino alla fase di fruttificazione.

**Stato:** la compilazione automatica e i test sul client devono essere eseguiti prima di considerare completata questa fase; checklist in [OLIVE_SAPLING_TREES_TESTS.md](OLIVE_SAPLING_TREES_TESTS.md). Non sono stati creati/modificati PNG: `olive_sapling.png` era già presente e il file Rustic `sapling_olive.png` resta rimosso.

### Decisione morfologica successiva al collaudo (24 ottobre 2026)

L'autore ha respinto le forme di **quercia / quercia scura** come soluzione definitiva e ha segnalato foglie **a terra** sull'ulivo normale. **Generazione naturale provvisoria DISATTIVATA**, tanto normale quanto secolare: nessuna feature `olive_*_wild` registrata o presente nel datapack. Restano il germoglio normale/2×2 e le `configured_feature` per gli esperimenti, con una correzione temporanea `straight_trunk_placer` per riportare la chioma sul fusto. Le forme definitive dovranno avere un tronco irregolare contorto, chioma ampia/grigio-argentea e secolare con vero tronco 2×2.

**Ritocco germoglio:** solo i 37 pixel di foglie di `olive_sapling.png` ricolorati nella palette silver-green di `olive_leaves.png`, restanti pixel, stelo, sagoma e trasparenze invariati. Nessuna texture Rustic ripristinata.

**Grafica item cartello normale:** ripristinata la forma esatta dei glifi originali (prima della riscrittura di ottobre); modificato esclusivamente il colore dell'inchiostro per accordarlo all'icona dell'hanging sign.

### Decisione approvata: generazione procedurale degli ulivi (24 ottobre 2026)

**Scelta dell'autore:** costruire gli ulivi mediante algoritmi procedurali anziché strutture NBT prefabbricate. Lo scopo è ottenere alberi diversi fra loro, ma riconoscibili per tronchi irregolari, rami biforcati e chiome argentate. Si seguono le convenzioni di registrazione e organizzazione già adottate dalla mod.

**Architettura preferita:**
1. `OliveTrunkPlacer` (estensione dei componenti vanilla `TrunkPlacer`) calcola base, fusto irregolare, pieghe, ramificazione e punti di ancoraggio del fogliame. Una variante secolare parte da **base 2×2** e rami più larghi; usa sempre blocchi `olive_log` / `olive_wood`.
2. `OliveFoliagePlacer` (estensione di `FoliagePlacer`) distribuisce molteplici gruppi di `olive_leaves` verde-argentee attorno ai rami, con piccole lacune; non una sfera regolare e non foglie al suolo.
3. Due `ConfiguredFeature` con parametri distinti per l'ulivo normale e quello secolare. Registrare i relativi `TrunkPlacer` e `FoliagePlacer` con i `Codec` previsti da Minecraft 26.2, tenendo i parametri leggibili e coerenti con le altre risorse del progetto.
4. Testare ripetibilità a seme fisso, collisioni con costruzioni, conservazione dei germogli se fallisce la crescita, decadimento foglie, limiti del mondo/chunk, FPS, e coerenza client/server.
5. **Nessuna generazione naturale** finché le forme non vengono approvate dall'autore. Mantenere temporaneamente i due `ConfiguredFeature` vanilla utilizzati dal germoglio come fallback fino a quando il prototipo procedurale non viene compilato e verificato.

**Parametri iniziali proposti, da tarare con screenshot in-game (NON definitivi):**

| Caratteristica | Ulivo normale (1 germoglio) | Ulivo secolare (4 germogli 2×2) |
|---|---|---|
| Altezza totale | 6–9 blocchi | 10–14 blocchi |
| Larghezza chioma | 7–11 blocchi | 13–19 blocchi |
| Base tronco | 1×1, progressivamente nodosa | 2×2, asimmetrica |
| Rami principali | 2–3 | 4–6 |
| Fogliame | 2–4 masse frammentate | molte masse ampie e irregolari |

**Criterio di implementazione:** concentrare le regole di ramificazione, distribuzione delle chiome e casualità nei componenti dedicati; riutilizzare il più possibile la logica comune a ulivo normale e secolare. Organizzare codice, registri, tag e JSON seguendo il progetto attuale, evitando classi o livelli di astrazione senza una necessità concreta.

**Stato:** **decisione di design registrata**; i nuovi placer procedurali non sono ancora implementati. Nessuna modifica a worldgen e release in questa fase.

### Ulivo normale procedurale — prima versione testabile (24 ottobre 2026)

**Implementazione:** `OliveTrunkPlacer`, `OliveFoliagePlacer` e `ModTreePlacers` aggiunti nel package `dev.italiansdelight.common.worldgen`. Le classi utilizzano l'API dei placer vanilla 26.2 e i relativi `MapCodec`. Il germoglio 1×1 richiama ora i due generatori originali tramite `data/italiansdelight/worldgen/configured_feature/olive_tree.json`.

- Tronco 1×1 con base dritta, lieve curvatura nella parte alta, 2–3 rami laterali rialzati e direzioni variabili. I rami usano orientamento orizzontale dell'asse del log. Le chiome sono ancorate alle estremità dei rami, non a livello del terreno.
- Ogni ramo produce un gruppo compatto di foglie verde-argentee, leggermente appiattito, con margini irregolari e spazi naturali. Non sono state modificate texture approvate né introdotti frutti.
- **Ulivo secolare:** conserva per ora il generatore 2×2 provvisorio della quercia scura; un tronco procedurale 2×2 dedicato sarà la prossima fase, riutilizzando i parametri e le regole morfologiche definite per l'ulivo normale.
- **Worldgen naturale degli ulivi ancora DISATTIVATO.** La nuova forma è testabile solo tramite il germoglio in Creativa/Sopravvivenza e tramite `/place feature italiansdelight:olive_tree`.
- **Collaudo ancora da confermare:** far crescere almeno 8–12 esemplari con farina d'ossa, verificare che i rami siano connessi al fusto, le chiome sopra il terreno, la raccolta dei tronchi e il decadimento delle foglie; evitare intersezioni eccessive con costruzioni. Non considerare la forma approvata finché l'autore non la verifica. [Checklist](OLIVE_SAPLING_TREES_TESTS.md).

### Revisione morfologica dell'ulivo normale — biforcazione bassa (24 ottobre 2026)

**Feedback dell'autore (screenshot di gioco + foto di ulivo reale):** il precedente prototipo aveva l'aspetto di un ciliegio vanilla, con fusto lungo, diramazioni troppo alte e orizzontali, chioma continua. È stata approvata una morfologia con **due grandi biforcazioni già al secondo o terzo blocco del tronco** e rami **obliqui a scalini**, simili nell'andamento alle ramificazioni delle mangrovie.

**Implementazione in `OliveTrunkPlacer`:**
- Fusto centrale **2–3 blocchi**; radice delle biforcazioni a **y=1/2** (secondo/terzo blocco dal suolo), senza alto pilastro centrale.
- **Esattamente due rami principali**, generalmente quasi opposti e talvolta ad angolo retto, con piccola asimmetria verticale. Lunghezza orizzontale 3–5 blocchi, salite intermedie ed eventuale deviazione laterale.
- Le salite sono costruite in due passaggi distinti (`relative(direction)` poi `above()`), con **ogni segmento attaccato per faccia al precedente**. Rami privi di spazio non generano chiome staccate.
- Due **chiome distinte** unicamente alle punte dei rami principali; solo se entrambe non possono svilupparsi, una piccola chioma di sicurezza sopra il fusto. `OliveFoliagePlacer` ora restringe anche lo strato inferiore oltre quello superiore, evitando il grande soffitto piatto.
- Rimangono invariati `olive_log`, `olive_leaves`, le altre texture, il germoglio 1×1 e la crescita provvisoria 2×2 dell'ulivo secolare. **Nessuna generazione naturale degli ulivi**.

**Stato:** modello procedurale modificato e controlli statici aggiornati; compilazione Java CI e valutazione visiva in-game vanno registrate separatamente. La struttura resta un prototipo, non una forma già approvata.

### Ritocco proporzioni — massimo due blocchi orizzontali per ramo (24 ottobre 2026)

**Feedback in gioco dell'autore:** il concetto di biforcazione bassa a Y funziona, ma gli estremi dei rami e i due gruppi di foglie erano **troppo distanti** (screenshot del prototipo precedente).

**Nuova regola:** entrambi i rami principali dell'ulivo normale mantengono la biforcazione al secondo/terzo blocco, ma possono effettuare **non più di 2 spostamenti orizzontali complessivi ciascuno**, **inclusa** l'eventuale deviazione laterale. La deviazione sostituisce il secondo passo in avanti e **non si somma** a esso. Dopo ogni spostamento orizzontale il ramo sale di un blocco; tutti i tronchi restano collegati per faccia. In caso di ostacolo il ramo si arresta e usa l'ultima punta effettivamente posizionata, senza generare chiome isolate. Le due chiome rimangono separate come punti di ancoraggio, ma più vicine al fusto.

**Invariati:** numero di rami principali (2), blocchi e texture, `OliveFoliagePlacer`, crescita 2×2 provvisoria, e worldgen naturale degli ulivi (**disattivato**).

**Da verificare in gioco:** crescere 8–12 ulivi normali e confrontare la larghezza con lo screenshot precedente; controllare le ramificazioni oblique e le chiome, inclusi esemplari con deviazioni laterali. Gli automatismi verificano il limite nel codice e la compilazione, non l'approvazione visiva.

## 4. Famiglie e nomenclatura — proposta tecnica

Per mandorlo e ciliegio da frutto perseguire progressivamente un set edilizio coerente con Minecraft; **la famiglia d'ulivo è già implementata**: `*_log`, `*_wood`, `stripped_*_log`, `stripped_*_wood`, `*_planks`, `*_stairs`, `*_slab`, `*_fence`, `*_fence_gate`, `*_door`, `*_trapdoor`, `*_button`, `*_pressure_plate`, `*_sign`, `*_hanging_sign` e germoglio/foglie. Barche e barche con baule d'ulivo sono già presenti; quelle delle altre specie restano da valutare.

**Prefissi ID proposti:** `italiansdelight:olive_*`, `italiansdelight:almond_*`, `italiansdelight:fruit_cherry_*`. Evitare di registrare ID `minecraft:cherry_*` o sovrascrivere altri blocchi. Riutilizzare i modelli vanilla quando sufficienti, con texture dedicate; aggiungere correttamente loot, crafting, bruciabilità, linguaggi IT/EN, Creative e tag legno/foglie/germogli.

**Rischi da verificare:** esatta gestione Minecraft 26.2 delle famiglie legno, collocamento e orientamento dei blocchi, porta in due metà, cartelli, boat entity e data fixers quando applicabili, crescita 2×2 col terreno ingombro e generazione ai confini di chunk. Le risorse non ancora esistenti devono essere create solo dopo l'approvazione della direzione artistica.

### Ordine delle prossime implementazioni — 25 ottobre 2026

1. **`almond_*`**: palette originale (direzione proposta: nocciola/beige caldo, **non ancora approvata**), set completo di legno secondo la famiglia vanilla 26.2, texture, crafting, tab Creative, tag, cartelli/barche/mensola ove pertinenti, foglie, germoglio e albero procedurale piantabile. Solo dopo il collaudo estetico dell'autore.
2. **`fruit_cherry_*`**: palette proposta castano ramato, **non ancora approvata**; legno completo originale, foglie verdi, germoglio e albero coltivabile distinto dal ciliegio rosa vanilla.
3. **Un solo sistema di fruttificazione** per le tre specie: maturazione, foglie produttive, raccolta con clic destro e ricrescita, con frutti, texture e tassi specifici.
4. **Generazione naturale dopo la fruttificazione**: tre specie nei rispettivi biomi; secolari molto più rari. Non anticiparla durante la creazione dei nuovi alberi.

La famiglia d'ulivo già approvata e le relative texture restano invariate; non usare asset di Rustic o modificare `main`.

### Mandorlo: rifinitura delle sfumature (26 ottobre 2026)

L'autore ha esplicitamente richiesto un nuovo intervento su **tutte e cinque** le texture del mandorlo. Le aree approvate da mantenere sono il **disegno oak vanilla degli anelli**, il **bordo di 1 pixel**, la disposizione delle fessure nei fianchi normali/scortecciati e la trama complessiva delle tavole. Ora sono permesse tonalità intermedie (anche nuove) per ombre e luci coerenti sulla corteccia e sul legno esposto; sulle assi il contrasto locale deve risultare più uniforme pur lasciando leggibili le fughe fra tavole. Il nucleo 14×14 dei due tagli resta identico pixel per pixel. La nuova resa in-game non è ancora stata validata.

### Direzione aggiornata — texture corteccia mandorlo e tagli, 26 ottobre 2026

Dopo ulteriore esame dell'autore, **conservare integralmente le assi di mandorlo e la texture laterale scortecciata**. Il nuovo `almond_log.png` deve ispirarsi alla foto reale allegata usando **poche fessure longitudinali continue**, non un mosaico di macchie scure. Prediligere placche larghe con bordi ombreggiati e creste illuminate; lasciare variazione a livello di singolo pixel solo dove contribuisce alla lettura della corteccia.

Per `almond_log_top.png` e `stripped_almond_log_top.png`, usare anelli quadrangolari già approvati e cornice da **1 pixel invariata**. Aggiungere ombreggiature organizzate lungo gli anelli, con riferimento anche ad abete, betulla, ciliegio, legno della giungla e quercia scura vanilla. **L'interno 14×14 deve continuare a essere identico** nelle due versioni; la geometria quadrata degli anelli non deve trasformarsi in puntini casuali. Il controllo automatico verifica la disposizione delle linee principali separatamente dalle sfumature. Il collaudo estetico in-game resta necessario.

### Allineamento della corteccia naturale e delle fibre scortecciate (26 ottobre 2026)

L'autore ha approvato la direzione delle striature ma chiede di avvicinarle di 1–2 pixel. La nuova pixel art di `almond_log.png` conserva le fessure longitudinali e i rilievi originali, comprimendo gli spazi fra di esse. `stripped_almond_log.png` viene ridisegnata **sulla stessa disposizione strutturale delle fibre**: le venature corrono lungo le medesime posizioni, ma sono più chiare e delicate per rappresentare la corteccia rimossa. Questa scelta supera il precedente vincolo di lasciare intatto il fianco scortecciato e non autorizza modifiche a `almond_planks.png` o alle due sezioni dei tronchi. Test in-game della nuova coppia ancora da confermare.

## 5. Prossimi passi di progettazione

- [x] Inventariare i file dell'ulivo e distinguere varianti grafiche presenti.
- [x] Registrare le decisioni approvate sulle tre famiglie di legno e sull'ulivo secolare 2×2.
- [ ] **Approvazione della palette:** ulivo = asset attuali scelti; mandorlo = nocciola/beige caldo; ciliegio fruttifero = castano ramato/rossastro.
- [x] Accertare **la licenza MIT indicata su CurseForge** per Italian's Delight e Rustic e confrontare gli asset con il repository Rustic; identificate **41 corrispondenze binarie**, inclusi quattro PNG relativi all'ulivo; redatti [rapporto](RUSTIC_ASSET_MATCHES.md) e [avvisi](../../THIRD_PARTY_NOTICES.md).
- [x] **Escludere tutti gli asset identificati come Rustic dai nuovi elementi della 1.1.0** (decisione dell'autore); conservare i PNG storici senza cancellarli.
- [ ] Completare la **provenienza file per file degli asset non corrispondenti**, incluse eventuali attribuzioni agli altri artisti; usare soltanto asset non-Rustic per il nuovo ulivo. **Nessuna assegnazione automatica di varianti all'ulivo secolare.**
- [x] **Ulivo:** cartelli normali/appesi, barche e barche con baule, cabinet e aging rack implementati; i collaudi specifici aperti restano nelle relative checklist.
- [x] **Prima implementazione verificata:** cinque blocchi di legno d'ulivo, build Java 25 CI riuscita e **6/6 test Minecraft confermati** ([report](OLIVE_WOOD_TESTS.md)).
- [x] **Set edilizio d'ulivo:** scale, lastre, recinzioni, cancelli, pulsanti, pedane, porte, botole, cartelli, barche e cabinet implementati.
- [ ] **Prossimo lotto:** creare `almond_*` e albero di mandorlo; a seguire `fruit_cherry_*` e ciliegio fruttifero; in seguito un'unica raccolta/ricrescita condivisa; per ultimo worldgen naturale delle tre specie.

**Stato attuale:** la famiglia edilizia d'ulivo e i suoi generatori procedurali sono già implementati. La priorità passa alle nuove famiglie `almond_*` e `fruit_cherry_*`; questa revisione della pianificazione non cambia codice, PNG né versione pubblicata. Le decisioni sono allineate alla [roadmap](ROADMAP.md).
