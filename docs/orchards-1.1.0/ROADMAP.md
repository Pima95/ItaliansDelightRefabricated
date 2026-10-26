# Italian's Delight Refabricated — Orchard Update 1.1.0

**Stato:** il set edilizio d'ulivo (compresa la mensola vanilla), il germoglio e gli alberi procedurali **normale 1×1 e secolare 2×2** sono implementati e verificati esteticamente in gioco dall'autore. **Nuova priorità (25 ottobre 2026): completare mandorlo e ciliegio da frutto, con i rispettivi legni originali e alberi coltivabili, prima del worldgen naturale e del sistema condiviso di fruttificazione.** La generazione naturale resta disabilitata.  
**Branch:** `feature/orchards-1.1.0`, creato da `main` dopo la pubblicazione della 1.0.0.  
**Ambito:** tre alberi da frutto e relative coltivazioni. Le aggiunte di pasta, piatti e ricette saranno contenuti **bonus graduali** all'interno dei diversi aggiornamenti, non necessariamente una release dedicata.

> La famiglia edilizia, le foglie e il germoglio d'ulivo sono implementati. Entrambe le varianti crescono tramite generatori originali e l'autore ha verificato in gioco gli ultimi ritocchi. **Prima si realizzano le famiglie di legno e gli alberi di mandorlo e ciliegio da frutto; poi si implementa una sola meccanica di fruttificazione e raccolta riutilizzabile da tutte le specie; infine si attiva e bilancia la generazione naturale.** `main` e la release 1.0.0 restano invariati.

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

### Inventario storico degli asset dell'ulivo (23 ottobre 2026)

Questo elenco fotografa **i file disponibili il 23 ottobre 2026**, non lo stato attuale del branch: in seguito, le due texture identiche a Rustic `leaves_olive.png` e `sapling_olive.png` sono state eliminate, mentre gli asset alternativi `olive_leaves.png` e `olive_sapling.png` sono utilizzati nel gioco. La provenienza degli altri file resta soggetta a verifica:

- Tronco: `olive_log.png`, `olive_log_top.png`; anche `log_olive.png` (possibile variante/duplicato da confrontare).
- Tronco scortecciato: `stripped_olive_log.png` e `stripped_olive_log_top.png`.
- Tavole: `olive_planks.png`.
- Foglie: `olive_leaves.png` e `leaves_olive.png` (possibili varianti da confrontare).
- Germoglio: `olive_sapling.png` e `sapling_olive.png` (possibili varianti da confrontare).
- Porta e botola: `olive_door_bottom.png`, `olive_door_top.png`, `olive_trapdoor.png`.
- Elementi da arredamento: `olive_cabinet_front.png`, `olive_cabinet_front_open.png`, `olive_cabinet_side.png`, `olive_cabinet_top.png`.
- Texture item già presenti: `olive_door.png`, `olive_sign.png`, `olive_boat.png`, `olive_chest_boat.png`, `olive_seed.png`, `olives.png`.

**Stato corrente:** la famiglia edilizia d'ulivo, i cartelli, le barche, il cabinet, lo scaffale di stagionatura, le foglie e il germoglio sono implementati nel branch. I cinque blocchi di legno iniziali sono stati collaudati ([6/6 test](OLIVE_WOOD_TESTS.md)); le verifiche puntuali successive restano documentate nelle rispettive checklist. **Non sono definitive le forme degli alberi e la generazione naturale è disattivata**. Gli asset approvati non vanno modificati senza richiesta.

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

### Fase implementativa iniziale — primo lotto completato e verificato in gioco

- **Primo lotto tecnico:** `olive_log`, `olive_wood`, `stripped_olive_log`, `stripped_olive_wood` e `olive_planks`, con registrazioni, Creative, modelli, loot, crafting, tag, bruciabilità e scortecciatura da ascia.
- **Texture referenziate dai modelli:** solo `olive_log.png`, `olive_log_top.png`, `stripped_olive_log.png`, `stripped_olive_log_top.png` e `olive_planks.png`, **non** asset Rustic.
- Questa è la **prima porzione della famiglia di legno**, non un albero completo: non sono ancora implementati foglie, germogli, generazione nel mondo, ulivo secolare, frutti, porte, scale, lastre ecc. Gli altri due tipi di legno rimangono in fase di progettazione.
- **Verifica completata:** build Java 25 e controlli CI superati ([run GitHub Actions](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40028472641)); **sei su sei test manuali superati** e comunicati dall'autore il 24 ottobre 2026: Inventario Creativa, texture e rotazione, scortecciatura, ricette di crafting, rottura/recupero, combustione. Report dettagliato in [OLIVE_WOOD_TESTS.md](OLIVE_WOOD_TESTS.md). Le verifiche di intercompatibilità dei tag e server dedicato non fanno parte dei sei test, quindi restano aperte.

### Fase A — Blocchi edilizi derivati dalle assi (implementati, collaudo manuale aperto)

Il branch contiene sei nuovi blocchi: `olive_stairs`, `olive_slab`, `olive_fence`, `olive_fence_gate`, `olive_button`, `olive_pressure_plate`. Sono inclusi modelli orientabili con comportamenti vanilla, crafting, loot, tag, traduzioni, scheda Creative e registrazione. **Tutti i modelli usano esclusivamente `olive_planks.png`**, già presente e approvata durante il primo collaudo; non sono stati creati/sovrascritti PNG né recuperate le vecchie risorse di Rustic.

**Stato:** implementazione tecnica in attesa di verifiche Minecraft da parte dell'autore. Non confondere i sei test già superati del primo lotto con l'accettazione di questi nuovi blocchi. Registrare separatamente i nuovi risultati. Prossima sottofase prevista: porte e botole con gli asset `olive_door_*.png` e `olive_trapdoor.png` già presenti.

### Prossimo blocco proposto — completamento del set edilizio d'ulivo

**Fase A implementata; restano i test manuali:** aggiungere prima i derivati delle assi (`olive_stairs`, `olive_slab`, `olive_fence`, `olive_fence_gate`, `olive_button`, `olive_pressure_plate`), poi porte e botole (`olive_door`, `olive_trapdoor`). Preferire l'uso delle texture `olive_*` esistenti, mai quelle Rustic, senza alterare i PNG. In seguito affrontare cartelli, cartelli appesi e barche. La crescita dell'ulivo e quella dell'ulivo secolare 2×2 resteranno una fase successiva. Ogni lotto necessita di una build e di un collaudo distinti. Vedi [test e criteri](OLIVE_WOOD_TESTS.md).

### Feedback e correzione: orientamento della botola d'ulivo (24 ottobre 2026)

**Segnalazione dell'autore:** la texture della botola rimaneva orientata come `facing=north` in tutte le direzioni di posa; l'autore ha comunicato che **il resto va bene**, senza fornire una checklist completa punto per punto.

**Correzione:** in `assets/italiansdelight/blockstates/olive_trapdoor.json` applicate le rotazioni Y `north=0`, `east=90`, `south=180`, `west=270` **anche agli stati chiusi**, per entrambe le metà (`bottom`/`top`). Gli stati aperti avevano già la rotazione. La texture `olive_trapdoor.png` è immutata. Un controllo CI verifica **tutti i 16 stati**. **Da ricontrollare in gioco:** orientamento della texture della botola aperta e chiusa, metà alta e bassa; il fix non è ancora confermato dall'autore.

**Nota storica superata:** in questa fase cartelli e cabinet erano ancora mancanti; sono stati implementati nei lotti successivi. Il cabinet resta un arredo collegato a Farmer's Delight.

### Fase B e scaffale di stagionatura d'ulivo (implementazione tecnica)

- **Porta e botola d'ulivo**: blocchi vanilla-equivalenti, texture già esistenti `olive_door_bottom.png`, `olive_door_top.png`, `olive_door.png` e `olive_trapdoor.png`, senza modifiche PNG. Incluse ricette, proprietà del legno, redstone, loot (porta solo dal mezzo inferiore), modelli e traduzioni.
- **Aging rack d'ulivo**: nuova variante `olive_cheese_aging_rack`, costruita con recinzioni e assi d'ulivo. Condivide esattamente il comportamento, i due ripiani e l'entità persistente degli scaffali già implementati. Il generatore/check ricette registra **13** famiglie complessive.
- **Barca d'ulivo e barca con baule**: implementate come due entità veicolo distinte con item, crafting, modello e renderer vanilla, usando rispettivamente le texture `textures/entity/boat/olive.png` (128×64) e `textures/entity/chest_boat/olive.png` (128×128), oltre alle due icone 16×16 preesistenti; nessun PNG creato. **Da validare compilazione e funzionamento in gioco:** spawn su acqua, passeggeri, movimento, drop, salvataggio e inventario del baule.
- **Test manuali ancora da fare**: porte (cerniere, apertura, segnale, rottura) e botole (orientamenti e acqua); rack (inserimento, prelievo, maturazione, save/load, rottura e drop). La Fase A precedente resta da collaudare separatamente.

**Collaudo pendente:** [OLIVE_EXPANSION_TESTS.md](OLIVE_EXPANSION_TESTS.md) per porta, botola, scaffale, barca e barca con baule. Compilazione e controlli statici non equivalgono ai test manuali.

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

### 1.1.0 — Prima implementazione delle foglie d'ulivo (24 ottobre 2026)

**Collaudo dei cartelli:** l'autore ha confermato *«Funziona tutto»* dopo i ritocchi grafici e ha autorizzato la fase successiva. La conferma non certifica in modo distinto ogni ricetta non riportata.

**Foglie:** implementato `italiansdelight:olive_leaves` (classe concreta `UntintedParticleLeavesBlock` in Minecraft 26.2, senza particelle di foglie di altre specie) usando esclusivamente la texture preesistente **`textures/block/olive_leaves.png`**, alternativa senza corrispondenza binaria nota in Rustic. Blocco con `LeavesBlock` vanilla 26.2, `persistent`, `distance`, `waterlogged`, decay quando vengono rimossi i tronchi, tag blocco/item `minecraft:leaves`, scorrimento del fuoco (30/60), modelli 3D e item, loot da cesoie/*Silk Touch* e possibili bastoni (come vanilla). Nessun drop di olive, mele o germogli prima del relativo gameplay.

**Pulizia autorizzata dall'autore:** la texture **`textures/block/leaves_olive.png`** era un match binario esatto di Rustic (Git SHA `c90b2ece17d8452c07ba9d697dcbfb4d87f85d86`) e **viene eliminata da questo branch**. La relazione storica resta nel [confronto Rustic](RUSTIC_ASSET_MATCHES.md) per tracciabilità. Non rimuovere altri PNG Rustic non autorizzati.

**Prossimi passi:** collaudare foglie prima di iniziare germogli, struttura di crescita ulivo normale e secolare 2×2, worldgen e frutti. [Checklist foglie](OLIVE_LEAVES_TESTS.md).

### Revisione cromatica foglie e pulizia germogli Rustic — 24 ottobre 2026

**Conferma dall'autore:** le foglie introdotte nel primo lotto **funzionano nel gioco**; tuttavia la texture esistente appariva troppo verde, priva del tipico colore argento degli ulivi mediterranei. Non è stata confermata singolarmente ogni voce della checklist (né un'approvazione cromatica).

**Nuova palette** per `assets/italiansdelight/textures/block/olive_leaves.png`: quattro colori opachi **#445B4E** (ombra verde-grigia), **#687F6E** (salvia), **#92A38F** (verde argento) e **#C6D1C2** (riflesso argenteo chiaro). I pixel occupati restano esattamente **168** e quelli trasparenti **88** su 16×16, senza sfumature, antialiasing, alterazioni di silhouette o modifiche al codice di decadimento. La colorazione fissa è conservata dalla classe `UntintedParticleLeavesBlock`, indipendentemente dal bioma. Palette ispirata ai veri ulivi con foglie grigio-verdi e pagina inferiore argentea.

**Seconda pulizia autorizzata:** rimossa esclusivamente `textures/block/sapling_olive.png` (Git blob storico `f03d6d4cdea69c6952e773c87d3ade4a5ecc9458`, corrispondente a Rustic). Resta intatta `textures/block/olive_sapling.png` (`be6c9b048a2e029fac39df202587a412961d7fbc`), candidata per il germoglio coltivabile della fase successiva. **Non** è ancora stato registrato un nuovo blocco germoglio: prima va implementata la generazione dell'ulivo normale e dell'ulivo secolare 2×2, così il germoglio potrà effettivamente crescere. Nessun altro asset Rustic è stato eliminato.

**Test ancora da effettuare:** valutazione del nuovo colore argentato in gioco, al sole e all'ombra, e controllo del nuovo PNG in Creativa. [Checklist aggiornata](OLIVE_LEAVES_TESTS.md).

### Germoglio d'ulivo e crescita normale / secolare 2×2 — implementazione (24 ottobre 2026)

**Problema segnalato dall'autore:** il germoglio non compariva in Minecraft perché prima era stata conservata **soltanto la texture**, senza registrazione di blocco o BlockItem. **Risolto**: `italiansdelight:olive_sapling` ora è un `SaplingBlock` posizionabile, visibile in Creativa, con nome IT/EN, modello a croce, drop e tag vanilla.

**Alberi coltivabili:** lo stesso `TreeGrower` seleziona `olive_tree` per un germoglio isolato e `ancient_olive_tree` se trova **quattro germogli d'ulivo nel quadrato 2×2**. Le configurazioni di feature 26.2 impiegano sempre `olive_log` e `olive_leaves`; il normale usa un tronco ramificato vanilla di altezza moderata e corona arrotondata, il secolare usa tronco massiccio 2×2 con generatore vanilla `dark_oak_trunk_placer` e chioma ampia. È una prima geometria vanilla affidabile, **da valutare visivamente** per un ulivo contorto e mediterraneo, senza dichiararla definitiva. Le foglie generate sono naturali (`persistent=false`) e decadono se manca il tronco.

**Generazione naturale provvisoria (ANNULLATA):** l'esperimento iniziale nei biomi pianure/savana è **disattivato**. Non esistono più `placed_feature` d'ulivo attivi né inserimenti nei biomi. Gli alberi già esistenti nei vecchi chunk non sono eliminati e la crescita manuale dei germogli rimane possibile.

**Riproduzione in Sopravvivenza:** le foglie d'ulivo ora possono rilasciare il germoglio con probabilità 5% base (Fortuna fino a 10%), imitando il meccanismo di altre foglie vanilla; cesoie e Silk Touch mantengono il drop del blocco, non dei germogli. Niente olive o frutti fino alla fase di fruttificazione.

**Stato:** la compilazione automatica e i test sul client devono essere eseguiti prima di considerare completata questa fase; checklist in [OLIVE_SAPLING_TREES_TESTS.md](OLIVE_SAPLING_TREES_TESTS.md). Non sono stati creati/modificati PNG: `olive_sapling.png` era già presente e il file Rustic `sapling_olive.png` resta rimosso.

### Correzioni successive ai test dell'autore — 24 ottobre 2026

**Cartello normale:** l'autore intendeva **conservare la disposizione originale delle scritte** sull'icona `textures/item/olive_sign.png` e cambiare **soltanto il loro colore**, non ridisegnare i glifi. È stato ripreso il PNG originale (`0bf533baf5faf7e99a26aa58b3ebcabd3ca98aa9`) e ricolorati esattamente i 25 pixel delle due tonalità d'inchiostro nei toni già usati dall'icona del cartello appeso (**#563119** e **#7E4422**). Sagoma, stelo, assi, alfa e geometria del cartello invariati. La precedente alterazione delle lettere è dunque annullata.

**Forma ulivo normale:** l'autore ha segnalato una chioma generata **a livello del suolo**. La combinazione provvisoria `fancy_trunk_placer` + `blob_foliage_placer` non risultava coerente. Temporaneamente la feature di crescita del germoglio usa **`straight_trunk_placer` + `blob_foliage_placer`**, collocando normalmente la chioma in cima al fusto anziché produrla a terra. **Non è la forma definitiva:** è solo una riparazione intermedia per mantenere utilizzabile la crescita in attesa di modelli procedurali originali con fusti contorti e chiome realistiche. La crescita dell'ulivo secolare 2×2 continua a essere disponibile come prototipo con la geometria temporanea dark oak.

**Worldgen degli ulivi DISABILITATO:** eliminati i due `placed_feature` `olive_tree_wild.json` e `ancient_olive_tree_wild.json`; rimosse entrambe le chiamate `BiomeModifications.addFeature` dal registro. **Non vengono più generati nuovi ulivi naturalmente in pianure/savane**. Rimangono solo i `configured_feature` necessari per il germoglio singolo e i quattro germogli 2×2. Restano invariati cardoon, wild basil e worldgen vanilla. Gli ulivi già generati nei salvataggi esistenti non vengono cancellati.

**Germoglio:** `textures/block/olive_sapling.png` ritoccata senza alterarne la sagoma, il tronco, l'alpha né la risoluzione **16×16**. Solo 37 pixel di foglie sono stati ricolorati nella palette argentata **#445B4E**, **#687F6E**, **#92A38F**, **#C6D1C2** già usata da `olive_leaves.png`. Non è stata reintrodotta la risorsa Rustic `sapling_olive.png`.

**Accettazione:** occorrono test in gioco sul germoglio argentato, sulla chioma corretta e sulla completa assenza di worldgen negli **chunk nuovi**. L'originalità delle forme di entrambi gli ulivi è un blocco per riattivare il worldgen.

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

- Tronco 1×1 con base dritta, lieve curvatura nella parte alta, 2–3 rami laterali rialzati e direzioni variabili. I rami partono dal segmento corretto prima/dopo la piega e i loro tratti ascendenti sono **connessi per faccia** al resto del tronco, con orientamento orizzontale dell'asse dei segmenti. Le chiome sono ancorate alle estremità dei rami, non a livello del terreno.
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

### Ulivo secolare originale 2×2 — prototipo implementato (25 ottobre 2026)

**Via libera dell'autore:** la forma dell'ulivo normale 1×1 è già stata verificata in Minecraft e accettata, senza necessità di ripetere la richiesta di collaudo. Si passa quindi alla variante monumentale.

**Implementato su `feature/orchards-1.1.0`:**

- `AncientOliveTrunkPlacer` genera una base di legno di **2×2 blocchi**, persistente lungo il fusto principale; il segmento superiore cambia posizione di un blocco per suggerire la torsione di un albero secolare. Il tronco si dirama in **quattro rami** ascendenti, con curve opzionali e collegamento diretto per faccia. I raccordi sono in `olive_wood` con corteccia su tutti i lati; non vengono creati nuovi blocchi o PNG.
- `AncientOliveFoliagePlacer` produce **cinque masse di foglie** argentee sovrapposte, con strati ovali rastremati, bordi irregolari e nessuna isola di foglie priva di collegamento al legno. Usa esclusivamente `olive_leaves` già approvato.
- `ancient_olive_tree.json` non utilizza più `dark_oak_trunk_placer` e `dark_oak_foliage_placer`, bensì i due nuovi placer registrati nel `ModTreePlacers`. Il `TreeGrower` già presente continua a scegliere il secolare con **quattro germogli 2×2** e il normale con il germoglio singolo. Nessun cambiamento alla normale forma procedurale.
- La forma completa e gli ingombri previsti della chioma vengono controllati **prima** di modificare terreno e tronco. Se lo spazio non è sufficiente, nessun ramo viene piazzato e la crescita viene respinta, lasciando al `TreeGrower` vanilla il ripristino dei germogli. Per prudenza, anche ostacoli periferici possono impedire la crescita.
- `check_olive_procedural_placement.py` e `check_olive_sapling_trees.py` coprono entrambi i generatori e controllano che non siano introdotti `placed_feature` per ulivi selvatici.

**Verifica tecnica:** compilazione e test automatici riusciti ([GitHub Actions #40219167220](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40219167220)). **L'aspetto del nuovo secolare in Minecraft non è ancora stato approvato dall'autore.** Prima della generazione naturale occorrono i controlli in gioco su quattro germogli, sagoma, interferenze, collisioni, persistenza e multiplayer, oltre alla scelta della rarità.

**Importante:** la generazione naturale degli ulivi rimane **DISATTIVATA**. Non sono ancora implementati frutti o raccolta delle olive. La release 1.0.0 e `main` non vengono modificate.

### Revisione artistica ulivo secolare — tronco basso e contorto (25 ottobre 2026)

**Feedback dell'autore:** il primo secolare procedurale ha un tronco troppo alto e dritto, con silhouette simile a un baobab; la fotografia di riferimento mostra invece un grosso fusto antico e nodoso, corto, con più grandi rami che si dividono già in basso sotto una chioma larghissima.

**Correzione proposta e implementata:** `AncientOliveTrunkPlacer` conserva la base 2×2, ma limita il **fusto pieno a tre livelli** (y=0,1,2). Già il terzo livello del 2×2 è sfalsato di un blocco per creare una forte piega. Sopra non prosegue alcuna colonna 2×2: due nocche di legno formano una biforcazione aperta; quattro rami principali partono dal **secondo e terzo blocco** e si sviluppano con segmenti obliqui, curve occasionali e lunghezze lievemente variabili. Tre radici contrafforti irregolari allargano il piede del fusto e interrompono il profilo squadrato. I raccordi usano la corteccia `olive_wood`.

**Proporzioni:** la `configured_feature` del secolare ha ora `base_height=7`, `height_rand_a=1` e `height_rand_b=1`, invece di 9+2+2; si riduce così anche l'altezza minima richiesta dalla feature. Le cinque chiome arrotondate mantengono la texture argentea approvata e `AncientOliveFoliagePlacer` invariato. I rami restano connessi al tronco e il volume previsto viene verificato prima di piazzare legno.

**Collaudo da confermare:** aspetto del tronco da quattro lati, crescita tramite quattro germogli 2×2, collisioni, continuità dei rami, equilibrio della chioma. **Non considerare il ritocco approvato soltanto perché compila.** La generazione naturale resta disattivata; `main`, ulivo normale e texture restano invariati.

### Correzione altezza di partenza dei rami del secolare — 25 ottobre 2026

**Segnalazione dell'autore dopo prova in gioco:** alcuni elementi sporgenti sembrano rami che nascono dal **primo blocco** anziché dal secondo. Il problema non era nelle quattro biforcazioni principali (già a quota y=1/2), ma nei **contrafforti**: `BlockPos root = edge.relative(side)` creava espansioni orizzontali del fusto al livello y=0.

**Risoluzione nel generatore:** i contrafforti laterali iniziano ora esattamente a **y=1** usando `edge.relative(side).above()`, mentre a **y=0 rimangono soltanto i quattro tronchi del quadrato 2×2**. I contrafforti possono continuare al terzo livello e i quattro rami principali restano a y=1/2. Invariati: silhouette contorta, dimensioni, cinque chiome argentate, texture e crescita da quattro germogli. Aggiunto un controllo di regressione allo script CI per impedire di reintrodurre contrafforti a livello del suolo.

**Conferma visiva ancora aperta** sul nuovo ritocco. La generazione naturale è sempre disattivata e `main` resta invariato.

### Rifinitura ulivo secolare — rami dal terzo blocco e tronchi vanilla (25 ottobre 2026)

**Feedback in gioco:** il secondo blocco del tronco era ancora troppo affollato di rami. Inoltre l'uso di `olive_wood` (tutta corteccia) nei generatori non produceva l'aspetto vanilla desiderato.

**Implementato:**
- Ogni ramo principale dell'ulivo secolare parte dal **terzo blocco (y=2) nel 90% dei casi**, dal secondo (y=1) nel 10%. La probabilità per ramo supera l'85% richiesto, pur non garantendo la stessa percentuale sul singolo albero di quattro rami.
- Rimossi i tre contrafforti laterali indipendenti dal secondo blocco, lasciando una base 2×2 pulita e la torsione al terzo livello. Mantiene quattro rami e cinque chiome.
- **Entrambi i generatori** ora posano soltanto blocchi `olive_log`, mai `olive_wood`. Il generatore secolare orienta `axis=x/z` sui rami orizzontali e `axis=y` sui tratti verticali, come nei tronchi vanilla. Il blocco `olive_wood` rimane disponibile per i giocatori come materiale edilizio.
- Test CI statici aggiornati; nessuna texture, ricetta, foliage, worldgen naturale o `main` alterati.

**Collaudo visivo della forma aggiornata:** da confermare dall'autore; la generazione naturale resta disabilitata.

### Ritocco della chioma inferiore del secolare — 25 ottobre 2026

**Feedback dell'autore:** tronco approvato in Minecraft; forma superiore della chioma già convincente. Si prova una variazione ispirata ai ciliegi **solo nella parte sottostante**: zone che si allungano verso il basso e piccoli vuoti/rientranze fra esse.

**Sperimentazione:** in `AncientOliveFoliagePlacer`, la fascia inferiore `dy=-2` presenta ora rientranze localizzate soltanto sul bordo esterno, distribuite in settori angolari irregolari. Nei settori opposti, poche foglie laterali possono scendere fino a `dy=-3`, sempre direttamente sotto una foglia già prevista nella fascia `dy=-2`, con massimo **un blocco supplementare di discesa**. Le nuove foglie passano i controlli di terreno e di connessione al legno; i blocchi solidi non vengono sovrascritti.

**Invariati:** tronco dell'ulivo secolare approvato, generatore e chioma dell'ulivo normale, strati medi e superiori della chioma secolare, texture, frutti, ricette e worldgen naturale (ancora **disabilitato**). Il risultato visivo di questo esperimento resta da approvare in gioco.

### Mensola d'ulivo vanilla-equivalente (`olive_shelf`) — sostituita la variante decorativa

**Correzione dell'autore:** la precedente mensola da parete puramente decorativa NON era il blocco desiderato. Si intende la nuova **Shelf vanilla** presente in Minecraft 26.2, ultimo elemento mancante della famiglia di legni d'ulivo.

- [x] **Stesso ID** `italiansdelight:olive_shelf`: sostituito il blocco decorativo custom con `ShelfBlock` vanilla, senza cambiare il nome di registro nei mondi di prova.
- [x] **Gameplay vanilla**: tre slot visibili con oggetti, inserimento e scambio, salvataggio e renderer vanilla, alimentazione redstone e collegamento di più scaffali; aggiunto il nostro blocco tra quelli accettati da `BlockEntityType.SHELF` tramite Fabric `addValidBlock`.
- [x] **Modelli vanilla 26.2**: `template_shelf_body`, `template_shelf_unpowered`, `template_shelf_unconnected`, `template_shelf_left`, `template_shelf_center`, `template_shelf_right`, `template_shelf_inventory` e blockstate multipart per tutte le direzioni.
- [x] **Texture originale d'ulivo 32×32**: conservata l'impostazione UV della Shelf vanilla 26.2, rimappandone il disegno esclusivamente ai 7 colori della texture `olive_planks.png` già approvata; pixel RGBA opachi, senza sfumature né asset Rustic. Nessuna modifica a `olive_planks.png` stessa.
- [x] **Fregio superiore/inferiore distinto per l'ulivo (25 ottobre 2026):** su indicazione dell'autore è stato **annullato integralmente il precedente rametto inciso nei pannelli centrali** (posizione sbagliata). Dopo il confronto con gli atlanti vanilla di quercia, betulla, ciliegio e quercia pallida, sono state decorate **solo le modanature alte e basse**, **sia davanti sia dietro**, con un fregio geometrico a **coppie di foglioline d'ulivo e piccole olive alternate**. Il motivo si ripete ogni 8 pixel nelle due metà UV 16×16 dell'atlante 32×32. Sono modificate esclusivamente le righe y=1–3 e y=12–14; i tre alloggiamenti centrali, tutti gli altri UV, le dimensioni, i modelli e le meccaniche vanilla rimangono identici alla texture originaria. I colori appartengono rigorosamente alla palette già approvata di `olive_planks.png` e non provengono da Rustic. Controllo CI dedicato all'integrità dei pannelli centrali e alla ripetizione del fregio su entrambe le facce. Verifica estetica in Minecraft ancora aperta.
- [x] **Ricetta equivalente vanilla**: 6 `stripped_olive_log` in due righe di tre, con una riga vuota centrale, producono 6 `olive_shelf`. Conservate registrazione item, tag ascia, drop, traduzioni e scheda Creativa.
- [x] **CI**: `check_olive_shelf.py` controlla le nuove risorse e che il BE vanilla accetti `olive_shelf`; il vecchio codice `OliveShelfBlock.java` è rimosso.
- [ ] **Verifica in Minecraft**: 3 oggetti per mensola, redstone e collegamenti, rotazione, texture e icona, recupero con gli oggetti, mondo salvato e multiplayer.

**Non modificati:** `olive_cheese_aging_rack`, gli alberi e le loro texture approvate, `main`, worldgen naturale (disabilitato).

### Compattazione laterale della chioma secolare — 25 ottobre 2026

**Feedback dopo collaudo in Minecraft:** l'autore ha confermato che le rientranze inferiori e i ciuffi pendenti funzionano e hanno un buon aspetto, ma la chioma è troppo larga: lo spessore laterale percepito è di circa tre blocchi. Richiesto di passare a **due blocchi**.

**Ritocco implementato:** i cinque lobi del secolare conservano gli stessi strati verticali (`dy=-2..2`, più i ciuffi pendenti occasionali fino a `dy=-3`) ma sono più stretti. Le distanze radiali di base diventano **1,90 / 2,20 / 2,60 / 2,20 / 1,50** dal basso verso l'alto, rispetto a 2,15 / 3,15 / 3,75 / 3,10 / 2,00. Le coordinate orizzontali sono limitate a **±2 blocchi per asse rispetto a ciascun ancoraggio**, indipendentemente dalle ondulazioni della superficie; anche il raggio configurato passa da 3 a 2. Le rientranze e i ciuffi inferiori sono mantenuti, così come i controlli di connessione e distanza dal terreno.

**Invariati:** tronchi di entrambe le varianti, chioma dell'ulivo normale, asset grafici, scaffale `olive_shelf` ancora da implementare, generazione naturale disattivata e `main`. Il ritocco della larghezza resta da approvare visivamente.


### Decisione di sequenza approvata — completare i tre alberi prima dei frutti e del worldgen (25 ottobre 2026)

**Decisione dell'autore:** non riattivare ora gli ulivi naturali e non iniziare ancora le meccaniche di raccolta. Prima completare tutti gli alberi e i rispettivi set di legno, perché **olive, mandorle e ciliegie useranno lo stesso sistema di fruttificazione, raccolta con clic destro e ricrescita**.

**Ordine operativo aggiornato:**

1. **Mandorlo (`almond_*`) — prossimo lavoro:** definire/approvare una palette originale distinta dall'ulivo e creare progressivamente tutta la famiglia di legno vanilla (tronco, corteccia, scortecciati, assi, scale, lastre, recinzioni, cancelli, bottoni, pedane, porte, botole, cartelli, cartelli appesi, mensola, barche/varianti con baule ove supportate), più germoglio e foglie. Registrazioni, ricette, loot, Creative, tag, lingue IT/EN e test automatici. **Poi** generatore procedurale coltivabile dal germoglio, senza generazione naturale e senza frutti.
2. **Ciliegio da frutto (`fruit_cherry_*`) — lavoro successivo:** stessa completezza della famiglia di legno originale, rigorosamente distinta dal ciliegio ornamentale `minecraft:cherry_*`. Germoglio, foglie **verdi** e chioma del ciliegio fruttifero, modello procedurale coltivabile. Nessuna texture rosa del ciliegio vanilla riutilizzata automaticamente.
3. **Accettazione in gioco:** texture, forme, posa, ricette, foglie, decadimento e crescita di **tutte e tre le specie**; i controlli manuali dell'autore sono l'approvazione estetica, indipendentemente dalla CI.
4. **Una meccanica condivisa per i tre frutti:** progettare una base comune per foglie produttive, maturazione a stati/tick limitati, raccolta con clic destro **senza rompere la foglia**, drop del frutto corretto, reset e ricrescita. Le tre specie condividono il comportamento ma mantengono texture, frutti e parametri specifici.
5. **Worldgen naturale soltanto alla fine:** inserire ulivi, mandorli e ciliegi da frutto nei biomi previsti, mantenendo gli ulivi secolari assai più rari; bilanciare rarità, posizioni, ostacoli, intersezioni, confini chunk e multiplayer, senza alterare il mondo già generato.
6. In seguito ingredienti/lavorazioni, integrazioni Farmer's Delight, JEI, collaudi finali e rilascio.

**Stato dei nuovi asset:** la verifica dei file del branch al 25 ottobre 2026 non trova ancora risorse con nomi `almond` o `fruit_cherry`; le nuove texture vanno **create da zero**, senza riutilizzare Rustic. Le palette proposte nel documento [WOOD_DESIGN.md](WOOD_DESIGN.md) non sono ancora approvate come colori definitivi, quindi non presumere un'approvazione estetica prima della prova dell'autore.

**Vincoli in questa fase:** niente worldgen naturale, niente sistema di frutti prematuro, niente modifica delle texture d'ulivo già approvate; tutte le modifiche in `feature/orchards-1.1.0`, mai direttamente su `main`.

### Primo lotto mandorlo e profondità delle mensole (25 ottobre 2026)

L'autore richiede in questa fase **solo tre blocchi di mandorlo**: `almond_log`, `stripped_almond_log`, `almond_planks`. Tutte le altre varianti (compresi `almond_wood` e `stripped_almond_wood`), germogli, alberi e frutti sono rinviati fino all'approvazione delle texture.

- [x] **Cinque texture originali 16×16 RGBA**: corteccia e sezione superiore del tronco, lato e sezione del tronco scortecciato, assi. Direzione artistica provvisoria **bruno-nocciola / beige caldo**, diversa dal miele dell'ulivo. Nessun pixel trasparente o semitrasparente, nessuna risorsa Rustic.
- [x] **Tre blocchi vanilla-equivalenti** con orientamento assi X/Y/Z, scortecciatura da ascia, bruciabilità, crafting 1 tronco (anche scortecciato) → 4 assi, Creative, loot, modelli/definizioni item, traduzioni e tag legno.
- [x] **Mensola d'ulivo**: aggiunte ombre nelle cavità dei **tre slot** e nella cavità della variante **a slot unico**, con luci lungo le labbra inferiori e destre. Il fregio di foglioline alto/basso già approvato e tutti i pixel esterni alle cavità **rimangono invariati**, verificati dal test CI. Nessun cambiamento ai modelli e alla meccanica vanilla.
- [ ] Collaudo in Minecraft dei tre nuovi materiali e della profondità della mensola nelle due configurazioni. **Le texture del mandorlo non sono ancora approvate** e potranno essere rifinite dopo il giudizio dell'autore.
- [ ] Famiglia completa del mandorlo e il suo albero soltanto in una fase successiva.

### Correzione Shelf vanilla e texture mandorlo — 25 ottobre 2026

**Feedback dall'autore dopo il collaudo in gioco:**
- La precedente modifica degli slot aveva oscurato anche la **metà posteriore della mensola** (x=16–31, y=4–10) e aveva lavorato solo sul pannello da 3 slot. Mancavano le quattro varianti frontali alimentate.
- Non si collegava né alle altre mensole di ulivo né agli altri legni, nonostante fosse già una `ShelfBlock` vanilla.
- Il primo set di cinque texture del mandorlo era troppo distante dalla grammatica visuale vanilla; **i colori erano invece approvati**.

**Interventi:**

1. **Retro e fregio invariati**: ricostruito l'atlante a partire dalla versione approvata precedente all'errore. Hash SHA-256 invariato per tutti i pixel esterni ai cinque rettangoli UV frontali e per l'intera metà alta posteriore.
2. **Tutti e cinque i pannelli anteriori**: aggiunte vere ombre rientranti, bordi e venature usando i modelli e la distribuzione dei pixel della Shelf vanilla 26.2. L'atlante 32×32 include `unpowered` (tre slot, x=0–15/y=4–11), `unconnected` (un vano, x=16–31/y=24–31), `left` (x=0–15/y=16–23), `center` (x=0–15/y=24–31), `right` (x=16–31/y=16–23). **Non modificare il retro per cercare di migliorare uno slot**.
3. **Connessione redstone**: la mensola ora partecipa a `#minecraft:wooden_shelves` sia nei tag dei blocchi sia degli oggetti, senza riscrivere `ShelfBlock`. Le mensole si collegano *solo se alimentate* e orientate nella stessa direzione; fino a 3 possono collegarsi anche con legni diversi. Il comportamento è quello vanilla (3, 6 o 9 scambi in hotbar).
4. **Mandorlo più vanilla**: rigenerate `almond_log`, `almond_log_top`, `stripped_almond_log`, `stripped_almond_log_top`, `almond_planks` utilizzando come struttura di riferimento il pixel art dell'**oak wood vanilla 26.2** (venature verticali, anelli squadrati, sezioni di assi nette), mappato rigorosamente sugli **stessi colori della versione precedente**. Non è stata cambiata la palette approvata né aggiunto alcun blocco al primo lotto. Non si tratta più di distribuzione di pixel casuale.
5. **Test GitHub Actions**: controllare il tag di connessione, tutte le cinque varianti di cavità e il ripristino del retro; conservare i controlli RGBA e registrazione per le texture del mandorlo.

**Invariati:** ID dei blocchi, inventario e meccaniche vanilla della Shelf, texture d'ulivo approvate altrove, genera-zione naturale ancora disabilitata, altri alberi, versione 1.0.0 e branch `main`.

**Collaudo ancora necessario:** in Minecraft provare 2–3 mensole alimentate e rivolte nella stessa direzione (anche miste), provare i 3 slot normali e le quattro configurazioni alimentate, osservare il retro; verificare le 5 nuove texture del mandorlo in gioco.

### Ritocchi dopo il confronto in Minecraft — 25 ottobre 2026

**Mensola d'ulivo alimentata:**
- [x] **Configurazione center:** eliminate le evidenti venature a V; superficie del vano sostituita dalla trama poco contrastata della variante laterale sinistra, senza gli stipiti laterali (per connessioni visive continue).
- [x] **Configurazione unconnected (singola):** rimpiazzato il fondo piatto con la trama della variante sinistra, completando il bordo destro con quello della variante `right`. Adesso il vano ha fondo testurizzato e due cornici.
- [x] **Tutte le altre configurazioni, retro e fregio rimangono intatti**: cambiati soltanto gli UV `center` e `unconnected` (righe 24–31 del PNG 32×32). Nessuna modifica alla connessione redstone e ai tre slot vanilla.

**Mandorlo:** l'autore ha confermato i colori, ma chiede di distinguere il disegno dalla quercia. Rifatte quattro texture del tronco con uno stile vanilla originale: fessure verticali meno uniformi e un piccolo nodo/cicatrice ovale sulla corteccia; sullo scortecciato venature verticali più pulite e un nodo naturale. I tagli ora presentano anelli **ovalizzati e irregolari**, con **durame molto più scuro per `almond_log_top`** e **sezione chiara per `stripped_almond_log_top`**. Conservata esattamente la palette già accettata; **le assi `almond_planks.png` non sono state modificate**.

**Verifiche:** test CI aggiornati per controllare i bordi e la texture delle due varianti alimentate e la differenza di luminosità dei tagli del mandorlo. La resa estetica finale richiede ancora la verifica dell'autore in gioco; `main` e gli altri asset approvati restano invariati.

### Rifinitura mandorlo — stessa sezione lignea, corteccia diversa (26 ottobre 2026)

**Nuovo feedback dopo prova in gioco:** il tronco non deve sembrare la quercia ricolorata; inoltre la precedente versione presentava un taglio scuro sul tronco normale e uno chiaro sullo scortecciato, in contrasto con la coerenza delle sezioni dei tronchi vanilla. I lati avevano venature eccessivamente dritte.

**Correzioni:**
- **`almond_log_top.png` e `stripped_almond_log_top.png`** condividono ora **esattamente gli stessi 144 pixel centrali (12×12)**, composti unicamente dai sei colori originali di `almond_planks.png`. Gli anelli di crescita risultano naturali, leggermente irregolari, con durame e alburno della stessa tinta su entrambi i blocchi.
- Le sezioni differiscono **soltanto nella cornice esterna di 2 pixel**: bruno corteccia per il tronco normale, beige legno esposto per lo scortecciato. Nessuna differenza artificiale di colore nel centro.
- **`almond_log.png`**: piccoli solchi spezzati e leggermente ondulati, con brevi deviazioni e una piccola cicatrice naturale; non sono copie pixel per pixel dei solchi della quercia né colonne perfettamente dritte.
- **`stripped_almond_log.png`**: venature più morbide, che variano di 1 pixel di direzione lungo l'altezza, con una piccola imperfezione naturale. Mantiene la palette calda già concordata.
- **`almond_planks.png`** rimane *identica pixel per pixel* alla versione precedente; controllata con SHA-256 nel test automatico. Nessuna modifica ai blocchi, ricette, registrazioni, altri alberi o asset approvati.
- [ ] Verificare in Minecraft entrambe le sezioni affiancate e i lati dei tronchi in una colonna verticale, assicurando una buona ripetibilità del motivo.

**Controlli CI aggiornati:** verificare identità del centro delle due sezioni, uso esclusivo della palette delle assi, contrasto confinato alla cornice, originalità e opacità delle texture, conservazione di tutti gli altri asset.


### Ultima rifinitura del mandorlo: proporzioni vanilla a 16×16 — 26 ottobre 2026

Dopo ulteriore verifica dell'autore in Minecraft, sono state corrette le dimensioni apparenti del disegno e l'insufficiente separazione cromatica dei dettagli:

- [x] **Sezioni superiore/inferiore:** `almond_log_top.png` e `stripped_almond_log_top.png` condividono *esattamente* i 196 pixel del nucleo 14×14, con anelli squadrati e discontinuità da pixel art vanilla. Le sole differenze sono i **60 pixel della cornice esterna di 1 pixel**: corteccia per il tronco normale, alburno chiaro per il tronco scortecciato.
- [x] **Colori delle sezioni:** tutto il nucleo è ottenuto dai sei colori delle `almond_planks.png` approvate, senza modificare le assi. Gli anelli sono più articolati alla scala di un singolo pixel e non appaiono come grossi blocchi uniformi.
- [x] **Lati del tronco:** rifatte da zero le trame laterali a 16×16 con dettagli minuti, placche disallineate, solchi spezzati e leggere curvature. `almond_log.png` mantiene i sei toni bruno-nocciola di corteccia già in uso; `stripped_almond_log.png` usa i soli sei toni del legno esposto, uguali alle assi.
- [x] **Test di regressione:** 14×14/1 px obbligatori, nucleo identico, bordo completamente diverso, anelli con più sfumature locali, PNG 16×16 RGBA opachi, colori e SHA delle assi inalterati.
- [ ] **Accettazione estetica:** verificare direttamente in Minecraft se la nuova risoluzione apparente, i cerchi di crescita e la continuità dei solchi sui lati risultano ora vicini allo stile dei tronchi vanilla.

Nessuna modifica a `almond_planks.png`, modelli, registrazioni, mensole, worldgen o asset già approvati. `main` resta invariata.

### Mandorlo — seconda rifinitura texture vanilla (26 ottobre 2026)

Dopo il confronto dell'autore con le texture vanilla, le due sezioni del mandorlo mantengono bordo da **1 pixel** e stesso interno **14×14**, ma gli anelli prima arrotondati diventano **tre quadrati concentrici netti (4×4, 8×8, 12×12)**, come le sezioni dei tronchi Minecraft. Le nuove texture laterali sono progettate come **fessure e placche longitudinali con direzione e illuminazione riconoscibile**, non come pixel casuali; il tronco scortecciato presenta fibre regolari ma leggermente ondulate. Nessun cambiamento ai colori o alle assi approvate, agli altri blocchi o a `main`.

- [x] Rielaborazione delle quattro texture (fianco/taglio, normale/scortecciato).
- [x] Test per quadrati completi, coerenza delle fibre e palette.
- [ ] Collaudo visivo nel gioco in confronto a diversi tronchi vanilla.

### Mandorlo — corteccia fotografica e vero recolor vanilla del taglio (26 ottobre 2026)

**Feedback dell'autore:** rendere i lati del tronco simili alla foto di corteccia reale allegata e adeguare la versione scortecciata; per i due tagli utilizzare direttamente il disegno di `minecraft:oak_log_top` ricolorato.

- [x] **Corteccia normale** `almond_log.png`: quattro spaccature irregolari longitudinali con zone scure in profondità, placche allungate e bordi chiari in rilievo, reinterpretazione originale in 16×16 e con i sei colori già approvati.
- [x] **Lato scortecciato** `stripped_almond_log.png`: fibre verticali ordinate ma lievemente ondulate, qualche nodo e ombra discreta. Utilizzati solo i colori dell'attuale `almond_planks.png`.
- [x] **Sezioni** `almond_log_top.png` e `stripped_almond_log_top.png`: ricolorazione della disposizione di pixel del **vero oak_log_top vanilla 26.2**, non quadrati sintetici; nucleo centrale 14×14 identico, colore coerente con le assi, bordo esterno da 1 pixel diverso per i due stati.
- [x] **Protezione regressioni**: test aggiornati sulla struttura della sezione (hash della disposizione ricolorata), identità dell'interno, due bordi distinti, palette, fessure longitudinali e integrità dell'asset delle assi.
- [ ] **Verifica estetica in Minecraft**: appoggiare tronchi normali e scortecciati uno accanto all'altro, anche impilati; controllare continuità e leggibilità delle fessure e delle sezioni.

Non modificati: `almond_planks.png`, `olive_*`, asset approvati, registrazioni Java, gameplay, modelli, ricette, worldgen e `main`.

### Mandorlo — primo lotto di derivati approvati (26 ottobre 2026)

L'autore ha approvato i tronchi e le assi di mandorlo. Il primo lotto crea **otto nuovi blocchi vanilla**: `almond_wood`, `stripped_almond_wood`, `almond_stairs`, `almond_slab`, `almond_fence`, `almond_fence_gate`, `almond_button`, `almond_pressure_plate`.

- [x] Registrazione, scortecciatura di `almond_wood` con ascia, combustibilità, inventario Creativa, traduzioni italiane e inglesi.
- [x] Blocchi, item e varianti dei modelli vanilla clonati dagli equivalenti d'ulivo, ma con riferimenti **esclusivamente alle texture di mandorlo già approvate**, senza nuovi PNG.
- [x] Crafting, loot (compreso il recupero di due lastre per una doppia), tag Minecraft e due ricette Farmer's Delight per scortecciare tronco e legno tutto corteccia.
- [x] Test statici e workflow dedicati.
- [ ] Collaudo in Minecraft di orientamenti, recinzioni, cancelli, pedane, pulsanti, drop e ricette.
- [ ] **Prossima fase grafica:** porte, botole, cartelli, mensola e barche richiedono nuovi modelli o texture da sottoporre ad approvazione, prima di continuare con alberi, frutti e worldgen.

Confermata l'integrità delle cinque texture approvate del mandorlo. Nessuna modifica a `main` o alla generazione naturale.

### Mandorlo — sfumature più naturali e assi armonizzate (26 ottobre 2026)

**Modifiche richieste dall'autore dopo la valutazione dei blocchi a tutta corteccia:** i solchi originali erano leggibili ma eccessivamente monocromatici, le assi contenevano alcuni contrasti localmente troppo forti e grandi porzioni tra i cerchi del taglio risultavano piatte.

- [x] **`almond_log.png`:** introdotte sfumature a livello di singolo pixel accanto alle fessure scure e ai rilievi più chiari; i solchi verticali e le placche approvati mantengono la stessa disposizione.
- [x] **`stripped_almond_log.png`:** integrati toni intermedi lungo fibre e nodi già presenti, senza ricreare macchie casuali; i colori sono coerenti con quelli nuovi delle assi.
- [x] **`almond_planks.png`:** ridotto il contrasto fra pixel adiacenti intervenendo soprattutto sulle tonalità estreme. La separazione fra le quattro tavole, il disegno originale e le venature restano riconoscibili.
- [x] **`almond_log_top.png` e `stripped_almond_log_top.png`:** toni intermedi nelle bande larghe fra gli anelli, senza alterare la geometria del *recolor* della quercia vanilla. **Interno centrale 14×14 ancora identico**, cornice distinta da **1 pixel** confermata.
- [x] Aggiornati i test di regressione: controllo della silhouette degli anelli vanilla per classi tonali (non vecchio hash RGB rigido), contrasti locali delle assi, numero di sfumature, continuità direzionale delle fibre e struttura dei blocchi.
- [ ] Confrontare le cinque texture in Minecraft a distanza ravvicinata e normale distanza di gioco, in particolare i blocchi `almond_wood` e `stripped_almond_wood` ripetuti in parete.

**Ambito:** solo cinque PNG del mandorlo, test e documentazione; nessuna modifica a codice Java, modelli, ricette, ulivo, worldgen o `main`. Questo intervento grafico è autorizzato dall'autore e supera il precedente vincolo di conservare pixel-identiche le texture approvate.

### Ritocco mirato: corteccia meno rumorosa e sezioni più sfumate (26 ottobre 2026)

**Feedback dell'autore:** la nuova corteccia dava l'impressione di terra scavata da insetti; le sezioni `*_log_top` risultavano ancora troppo uniformi. Le **assi sono esplicitamente approvate e non devono essere toccate**. Il lato scortecciato è anch'esso lasciato inalterato perché il problema segnalato riguarda soltanto il fianco del tronco normale.

- [x] **`almond_log.png`:** ridisegnato con tre solchi longitudinali principali, placche più grandi, creste illuminate e brevi raccordi; eliminata la maggioranza dei pixel scuri isolati. Texture originale 16×16, ispirata alla foto della corteccia allegata, senza riprodurla letteralmente.
- [x] **`almond_log_top.png` e `stripped_almond_log_top.png`:** maggiore varietà di mezzitoni nelle fasce fra gli anelli, ispirata anche ai tagli di abete, betulla, ciliegio, legno della giungla e quercia scura vanilla. I **43 pixel di contorno degli anelli più scuri** sono rimasti nelle posizioni precedenti; i due nuclei centrali 14×14 sono identici. Cornici esterne da 1 pixel inalterate.
- [x] **`almond_planks.png` e `stripped_almond_log.png`:** nessun cambiamento, protette anche dai test SHA-256.
- [x] Aggiornati i controlli per verificare la silhouette degli anelli indipendentemente dalle sfumature, le cornici originali, il ridotto rumore della corteccia e i due PNG invariati.
- [ ] Verifica estetica in Minecraft, in particolare parete di più `almond_wood` adiacenti e visuale ravvicinata di `almond_log_top`.

**Ambito limitato ai tre PNG indicati, test e documentazione.** Non vengono modificati `main`, gameplay, altri alberi, modelli o ricette.

### Mandorlo — solchi ravvicinati e trama condivisa con lo scortecciato (26 ottobre 2026)

**Richiesta dell'autore:** avvicinare le strisce verticali del tronco di 1–2 pixel e ricostruire il lato scortecciato in continuità visiva con il nuovo disegno.

- [x] `almond_log.png`: spostati i gruppi di fessure verso l'interno di circa **1–2 pixel**, mantenendo le forme ondulate, le sfumature e la palette già selezionata. Tre direttrici delle spaccature principali risultano più vicine, con creste più strette e meno spazio fra le linee scure.
- [x] `stripped_almond_log.png`: ridisegnata la trama chiara riprendendo **le stesse posizioni delle venature verticali del nuovo tronco normale**, senza copiare il colore e le fenditure buie della corteccia. La versione scortecciata resta morbida e riconoscibile come legno esposto. Profilo cromatico orizzontale fortemente correlato (circa 0,99) a quello del normale.
- [x] Entrambi i PNG 16×16 RGBA opachi; nuovi controlli dedicati agli SHA, all'allineamento delle fibre e alle fessure spostate.
- [x] Assi `almond_planks.png`, sezioni `almond_log_top.png`/`stripped_almond_log_top.png`, modelli, blocchi derivati, ulivo e `main` **non modificati**.
- [ ] Verifica estetica in Minecraft di `almond_log` e `almond_wood` affiancati alle rispettive varianti scortecciate: osservare in particolare i solchi ravvicinati in pareti ripetute.

### Mandorlo — nuova porta e botola con intarsio coordinato (26 ottobre 2026)

- [x] **Arte originale 16×16:** la porta presenta due pannelli incassati con cornici smussate, un fiore geometrico di mandorlo nel pannello superiore e una losanga allungata in quello inferiore; la botola riprende il motivo floreale in una cornice quadrata più spessa. Disegni simili ma non uguali; tutti i pixel opachi utilizzano **esclusivamente la palette della versione approvata di `almond_planks.png`**.
- [x] Quattro nuovi PNG: `almond_door_top.png`, `almond_door_bottom.png`, `almond_trapdoor.png` e icona trasparente `textures/item/almond_door.png`.
- [x] `almond_door` e `almond_trapdoor`: comportamento vanilla, 32/16 blockstates, 11 modelli di blocco, 2 modelli oggetto e definizioni, tag wooden doors/trapdoors, ricette (3 porte / 2 botole), drop, ascia, combustibilità, scheda Creativa e traduzioni IT/EN.
- [x] Test statico dedicato alla palette e alla geometria delle texture, ai modelli e agli orientamenti (botola compresa); CI Java 25.
- [ ] **Verifica estetica dell'autore in Minecraft:** porta chiusa e aperta con cerniere sinistra/destra, botola su pavimento/soffitto e orientata nei quattro punti cardinali, icone e ricette. Motivi **non ancora definitivamente approvati**.
- [ ] Lotti successivi: cartelli, mensola, barca/chest boat e modelli di mobili; alberi e frutti dopo i legni.

**Nessuna modifica alle cinque texture base di mandorlo, all'ulivo, alle altre specie, alla generazione naturale o a `main`.**

### Mandorlo — seconda revisione estetica porta/botola (26 ottobre 2026)

**Feedback della prova in Minecraft:** la prima versione risultava troppo piatta, il fregio era spostato a destra e mancavano dettagli chiari delle cerniere e delle maniglie. Si è confrontato il disegno con porte e botole vanilla: preferire una cornice in rilievo, pannelli scavati con luce a sinistra/alto e ombra a destra/basso, minuteria leggibile con pochissimi pixel.

- [x] `almond_door_top.png` e `almond_door_bottom.png`: pannelli a doppio smusso, cornice più spessa, ombre di profondità, cerniere su montante sinistro, piastrina/maniglia al limite fra le due metà. Il fregio è ridisegnato su **asse x=7,5**, con petali simmetrici centrati.
- [x] `almond_trapdoor.png`: richiamo allo stesso fregio, centrato con precisione, incassato in un pannello quadrato profondo. Aggiunte **due cerniere simmetriche sul lato di attacco** e una **presa centrata sul bordo opposto**.
- [x] `textures/item/almond_door.png`: nuova icona coerente con il disegno reale dell'anta.
- [x] Tutte le nuove ombreggiature e i dettagli metallici stilizzati derivano **esclusivamente dai colori delle assi** di mandorlo già approvate. Nessun pixel delle assi, del tronco o di altri legni è stato modificato.
- [x] Aggiornato il test specifico per verificare centratura e simmetria del fregio, doppio smusso, dettagli di apertura, palette, PNG e blockstate.
- [ ] Verificare in gioco la leggibilità della ferramenta, l'effetto di profondità e la centratura da entrambi i lati; l'approvazione estetica finale resta aperta.

### Mandorlo — terza revisione porta/botola: profondità, vera ferramenta e item vanilla (26 ottobre 2026)

**Riscontro dell'autore:** nonostante le precedenti dichiarazioni di avanzamento, maniglie e perni non erano visivamente riconoscibili; diversi settori dei pannelli rimanevano quasi monocolore. I loghi/fiore, invece, erano già approvati e vanno conservati.

- [x] **Centratura e logo intatti:** i 36 pixel del nucleo 6×6 (`x=5..10`, `y=5..10`) sono preservati *identici* su porta alta, porta bassa e botola, protetti con controllo SHA del nucleo.
- [x] **Porta:** aggiunti veri perni e cerniere sulla montante sinistro in entrambi i blocchi (parte alta/bassa). Maniglia con impugnatura e piastrina sul montante destro, distribuita presso la giuntura tra i due blocchi; metallo brunito evidenziato con quattro colori neutri, anziché toni di legno indistinguibili.
- [x] **Botola:** due cerniere metalliche simmetriche su un lato e una vera presa ad anello centrata sul lato opposto. Collocazione testata ai pixel.
- [x] **Legno:** dettagli di venatura in brevi segmenti allineati e luci/ombre nelle cornici, soprattutto sulle travi precedentemente monocolore, senza sfumature casuali nell'intarsio.
- [x] **Item porta:** ricostruita come sprite 16×16 di **sagoma vanilla 10×14** (colonne 3–12, righe 2–15, due righe trasparenti sopra) ispirandosi alle reali icone oak, spruce e cherry, con due pannelli e ferramenta riconoscibile, non una copia miniaturizzata del blocco.
- [x] I quattro colori aggiuntivi sono **solo metallo**; tutti i pixel del legno continuano a usare la palette binariamente invariata di `almond_planks.png`. Sono aggiornati gli assert su hardware, identità del fregio, nuova sagoma dell'icona, alpha e blockstates.
- [ ] **Convalida estetica** in Minecraft: verifica leggibilità delle due cerniere della porta, maniglia alla giunzione fra blocchi, presa della botola e ingombro dell'item accanto alle porte vanilla.

Nessuna modifica alle texture base approvate, ai modelli, all'interazione/registrazione, alle ricette, alla generazione naturale né a `main`.

### Mandorlo — ferramenta ridisegnata secondo il linguaggio vanilla (26 ottobre 2026)

**Feedback in gioco:** la terza iterazione presentava ancora grandi placche grigie attaccate sopra la porta e la botola, visivamente estranee alla falegnameria vanilla. **L'icona della porta invece è stata esplicitamente approvata e resta intatta.**

Confronto con le texture `oak_door_top/bottom`, `oak_trapdoor`, `spruce_door_top`, `spruce_trapdoor`, `cherry_trapdoor` vanilla 26.2: le parti metalliche sono piccoli accenti integrati nelle cornici e nei listelli, non maniglie/grappoli grigi di 3×3 o 4×3 pixel.

- [x] `almond_door_top.png`: cerniera **di larghezza 1 pixel** sul bordo esterno sinistro e piccolo pomolo ombreggiato sul lato destro (3 pixel metallici), con resto del supporto ricavato da legno in ombra. Nessuna piastra che sovrapponga il pannello.
- [x] `almond_door_bottom.png`: perno inferiore sempre **di 1 pixel**, tre accenti metallici sul montante sinistro e nessun doppione della maniglia sul traverso.
- [x] `almond_trapdoor.png`: due piccoli perni incassati sul bordo superiore, uniti alla cornice con una leggera ombra nel legno; presa centrata `x=7,8` di **soli 2 pixel metallici** con una luce e un'ombra.
- [x] Restano immutati **i 36 pixel del fregio approvato di ogni texture** (area x=5..10, y=5..10), tutte le parti già accettate dell'icona `textures/item/almond_door.png`, i cinque PNG originali di mandorlo e tutti i modelli/ricette.
- [x] Test regressione aggiornati: posizioni esatte dei perni, maniglia entro 3 pixel metallici, nessun blocco di ferramenta 2×2, simboli centrati, risoluzione 16×16, comportamento dei blocchi e item vanilla approvato.
- [ ] Verifica in gioco della ferramenta sulle porte aperte/chiuse e sulle botole orientate, senza ulteriori modifiche automatiche all'icona.

### Mandorlo — corretta distribuzione della ferramenta nei modelli (26 ottobre 2026)

Le texture delle maniglie e delle cerniere sono state **approvate**, ma in gioco comparivano anche sui bordi sbagliati: nella porta su quello della maniglia, e nella botola lungo tutti i bordi. La causa è il modo in cui i modelli vanilla campionano la stessa fascia del PNG sui lati opposti.

- [x] **Porta:** 8 modelli JSON locali con geometria e UV di Minecraft vanilla invariati; il bordo realmente incernierato mantiene la sprite della porta, mentre quello della maniglia e i due bordi orizzontali usano `almond_planks` senza ferramenta. Copertura di sinistra/destra e aperta/chiusa.
- [x] **Botola:** 3 modelli locali mantengono i due pannelli decorati nelle facce principali e usano `almond_planks` per i quattro bordi stretti (spessore 3px), anche quando è aperta.
- [x] Controlli automatici su ciascuna faccia, sugli stati e su tutte le varianti; artwork e item invariati.
- [ ] Conferma della distribuzione delle cerniere in gioco da diverse angolazioni.

**Nessuna texture PNG, animazione, ricetta o registrazione Java modificata; `main` intatta.**

### Revisione grafica 26 ottobre — bordo porta, botole e corteccia mandorlo

Segnalazione dell'autore: il bordo della porta senza cerniera era stato erroneamente
sostituito con il motivo delle assi; le facce delle botole erano ancora troppo uniformi;
la corteccia laterale del tronco di mandorlo aveva ricevuto feedback estetici negativi.

- [x] **Bordo porta:** conservata la texture della cornice originale nelle varianti
  `almond_door_top_edge` e `almond_door_bottom_edge`. Eliminati ESATTAMENTE
  i tre pixel metallici della cerniera da ciascuna variante, sostituiti con tinte
  del legno già presenti: tutti gli altri pixel sono invariati. Il bordo
  incernierato conserva la texture originale e l'item non cambia.
- [x] **Botola:** venature e contrasti rafforzati in `almond_trapdoor.png`;
  meno zone 2×2 monocromatiche. Intarsio centrale e sei pixel di ferramenta
  invariati. Le due superfici principali condividono il nuovo disegno.
- [x] **Tronco:** nuovo `almond_log.png` con corteccia bruno-cenere, fessure
  discontinue e rilievi verticali più sottili. Il taglio `almond_log_top.png`,
  lo scortecciato e le assi non sono stati alterati.
- [x] **Regressioni:** controllo pixel-a-pixel sulle varianti senza cerniera,
  8 modelli di porta, numero di aree monocromatiche della botola,
  formato e varietà cromatica della corteccia.
- [ ] **Verifica visiva Minecraft:** bordi della porta aperta/chiusa e destra/sinistra;
  entrambe le facce della botola, differenti orientamenti e livelli;
  tronco in blocchi singoli e composizioni di più tronchi.

`main` e l'icona della porta approvata restano intatte.

## 4. Fasi di sviluppo — ordine aggiornato

- [x] **Fase 0 — Scelte iniziali e ulivo:** tre specie confermate, famiglia d'ulivo e alberi coltivabili 1×1 e secolare 2×2 sviluppati e verificati esteticamente; ulivo secolare raro in natura in una fase futura.
- [ ] **Fase 1 — Legno e albero di mandorlo:** palette originale da approvare, famiglia edilizia completa, texture originali, germoglio, foglie e albero procedurale coltivabile, prove in gioco.
- [ ] **Fase 2 — Legno e albero di ciliegio da frutto:** legno `fruit_cherry_*` autonomo, foglie verdi, germoglio, crescita procedurale e prove in gioco, senza modificare il ciliegio ornamentale vanilla.
- [ ] **Fase 3 — Fruttificazione condivisa:** una meccanica comune a olive, mandorle e ciliegie: maturazione, clic destro, raccolta, ricrescita e persistenza, senza distruggere le foglie.
- [ ] **Fase 4 — Generazione naturale e bilanciamento:** aggiungere le tre specie nei rispettivi biomi, con la variante secolare nettamente più rara, soltanto dopo la verifica di alberi e sistema frutti.
- [ ] **Fase 5 — Ingredienti, preparazioni e integrazione:** ricette, eventuali lavorazioni specifiche, Farmer's Delight, JEI, lingue e bonus.
- [ ] **Fase 6 — Test e pubblicazione:** build, client, server dedicato, vecchi mondi, worldgen, performance, verifica asset e rilascio.

## 5. Decisioni confermate e aspetti ancora aperti

1. **Legno — DECISO:** tre famiglie originali di legno. Quella dell'ulivo comprende già varianti edilizie, cartelli, barche, cabinet e aging rack; mandorlo e ciliegio da frutto restano da sviluppare.
2. **Ulivo secolare — DECISO (B):** crescita con quattro germogli in quadrato 2×2. La generazione naturale, inizialmente prevista come rara, resta **disabilitata fino all'approvazione delle nuove forme procedurali**.
3. **Raccolta:** maturazione e resa per foglia; interazione con la farina d'ossa.
4. **Grafica:** chiome (normali/fiorite/fruttifere) e livello di differenza visiva per i tre alberi.

**Vincoli:** non modificare la `main` stabile né le texture già approvate della 1.0.0 senza richiesta; nessun incremento della versione pubblicata in questa fase.
