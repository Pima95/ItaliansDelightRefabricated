# Roadmap — Pizza Update 1.0.0

## Stato

**Posizionamento, stesura manuale e con mattarello confermati funzionanti dal giocatore.** Il mattarello ha **64 utilizzi** con usura di 1 per stesura riuscita. Aggiunta ricetta per **2 bastoni + qualsiasi asse di legno**, tramite tag `#minecraft:planks`; **crafting da testare**. Ridisegnati i cinque modelli dell'impasto con base schiacciata e bordo progressivamente rialzato, **in attesa di test visivo**. **Primo prototipo di condimento mozzarella in BlockState implementato, da testare in gioco.** Pomodoro, condimenti multipli, cottura e persistenza di combinazioni arbitrarie non ancora implementati.

## Checklist decisioni già CONFERMATE

- [x] Versione-obiettivo 1.0.0 con sistema di pizza personalizzabile.
- [x] Palla d'impasto posizionata direttamente sopra i blocchi, non sul Cutting Board.
- [x] Stesura sia a mano (clic destro) sia con mattarello.
- [x] Condimento diretto nel mondo con clic destro, senza GUI.
- [x] Preparazione tramite crafting table disponibile come alternativa.
- [x] Pizze riconosciute con nomi prestabiliti, altre con nomi dinamici.
- [x] Pizza Oven dedicato oltre a fornace e affumicatore vanilla.
- [x] Pizza Oven 1×1×1, **capienza una pizza alla volta**.
- [x] **Pizza Oven solo per pizze:** comportamento tipo fornace, ma accetta **esclusivamente pizze come input di cottura**; altri cibi e oggetti non vengono lavorati.
- [x] **Nessun preriscaldamento**: la cottura comincia subito con pizza ed energia disponibili.
- [x] **Combustibile consumato solo durante la cottura attiva**: energia residua conservata a forno vuoto o con pizza pronta e riutilizzabile con altre pizze.
- [x] **Cottura in pausa senza energia**, con avanzamento salvato e ripresa dopo rifornimento senza ripartire da zero.
- [x] Pizza Oven accetta **tutti i combustibili vanilla**.
- [x] **Pala FACOLTATIVA per il Pizza Oven**: inserimento ed estrazione devono essere possibili anche senza pala. Il suo impiego resta un'opzione di comodita', non un requisito.
- [x] **Pizza Oven con GUI VANILLA DELLA FORNACE**: stessa grafica/disposizione della fornace di Minecraft, **tre slot** (pizza in ingresso, combustibile, pizza cotta in uscita), fiamma e freccia di progressione. Nessuna GUI grafica personalizzata; la gestione della pala resta un'interazione diretta alternativa.
- [x] **Ricetta crafting Pizza Oven CONFERMATA:** shaped 3x3, **1 fornace vanilla al centro**, **4 blocchi di mattoni** negli angoli, **4 blocchi di mattoni di pietra** sui lati; risultato **1 Pizza Oven**, nessun altro materiale.
- [x] **Inserimento diretto con pala occupata**: clic destro sul Pizza Oven con pizza sulla pala trasferisce la pizza direttamente nel forno **senza aprire la GUI**; pala vuota dopo successo, nessun trasferimento se il forno e' occupato e dati conservati.
- [x] **Estrazione diretta con pala vuota**: clic destro sul Pizza Oven preleva la pizza **anche a cottura incompleta**, senza GUI e conservando il progresso, gli ingredienti e tutti i dati. La GUI resta disponibile come metodo alternativo.
- [x] **Ruolo della cottura parziale chiarito:** e' uno stato tecnico/progresso della pizza cruda, non un alimento o risultato separato previsto; serve a poter spostare la pizza senza perdere il lavoro svolto. Solo la pizza cotta al 100% entra nell'output.
- [x] **Pizza INTEGRA sul terreno recuperabile rompendo il blocco come ITEM PIZZA** (crudo o cotto), con tutti i dati di composizione, divisione, orientamento e cottura. **Nessuna raccolta con clic destro a mano vuota** sulla pizza stesa e nessun drop separato di impasto/topping dalla pizza formata. Stesura della palla e rimozione topping restano interazioni distinte.
- [x] **Nessuna bruciatura** con qualsiasi metodo.
- [x] Rendering 2.5D: **salse piatte, altri topping poco rialzati**.
- [x] Ingredienti di altre mod supportabili tramite definizioni e tag.
- [x] **Equivalenze di ingredienti tra mod**: due item riconosciuti esplicitamente come equivalenti sono lo stesso topping ai fini dell'anti-duplicato e del limite di 6 per quarto; il recupero restituisce sempre l'item originale.
- [x] Suddivisione **facoltativa con mattarello dopo la stesura**: intera -> due meta' -> divisione di una o entrambe le meta' in quarti, condibili indipendentemente. **Orientamento della prima divisione determinato dalla direzione del giocatore nel momento del clic**, anziche' fisso. Clic ingrediente applica alla zona puntata. Linee visibili temporaneamente solo tenendo mattarello o ingrediente valido; nessun cambio permanente ai modelli/texture.
- [x] Rimozione parziale senza rimborso; rimborso una sola volta dopo la rimozione di tutti i quarti coperti da una specifica applicazione.
- [x] **Rimozione con Shift + clic destro a mano vuota**: un condimento per clic, partendo dall'ultima applicazione ancora presente nella zona mirata; zona priva di topping = nessun effetto.
- [x] **Riunione con Shift + clic destro col mattarello**: due quarti della stessa meta' -> due meta' intere -> pizza intera, senza perdita, rimborso o redistribuzione dei topping preesistenti.
- [x] **Durabilita' del mattarello**: 1 punto per stesura completata, **0 per tutte le divisioni e riunioni**, 0 se il gesto non ha effetto e in Creativa. Il mattarello esistente ha 64 punti.
- [x] **Stesso ingrediente massimo una volta per sottoarea**: applicazioni su zone distinte consentite; se l'area puntata e' parzialmente coperta, il clic interessa solo la parte libera e consuma 1 item; se completamente coperta, nessun effetto/consumo.
- [x] **Salsa facoltativa**: pizze bianche valide, nessun ingrediente salsa richiesto in generale.
- [x] **Massimo una salsa per sottoarea fisica**, indipendentemente dal tipo; salse differenti ammesse in porzioni separate, mai sovrapposte.
- [x] **Rifiuto integrale delle salse differenti sulle zone parzialmente occupate**: se una zona mirata contiene gia' una salsa differente anche solo su una parte, il clic non applica nulla e non consuma item.
- [x] **Limite di 6 ingredienti diversi per quarto fisico**, salsa inclusa e facoltativa; invariato da divisioni e riunioni. Un tentativo che supererebbe sei in **anche un solo quarto interessato** e' rifiutato integralmente senza consumare item.
- [x] La **rimozione completa dei topping** rimborsa l'ingrediente originale nell'inventario (drop solo se pieno); la **rottura della pizza formata ancora INTEGRA** restituisce invece **la pizza-item completa** (se gia' servita in parte, solo fette residue), cruda o cotta, non impasto e ingredienti separati.
- [x] Pala per pizza: porta direttamente una sola pizza alla volta e conserva l'intero stato; non richiesta per condire.
- [x] **Pala senza durabilita'**, riutilizzabile indefinitamente per raccogliere, spostare e trasferire pizze.
- [x] **Pala non impilabile**, sia vuota sia occupata: **stack massimo 1**, una pala per slot di inventario.
- [x] **Pala droppata (Q)**: normale entita' item che mantiene la pizza al suo interno, anche dopo la raccolta da parte di un altro giocatore. Dati completi immutati, nessun item pizza separato.
- [x] **Despawn vanilla confermato**: una pala a terra con pizza puo' scomparire naturalmente nei tempi e condizioni vanilla; si **perdono pala e pizza**, senza ricompense o protezioni speciali.
- [x] **Danni ambientali vanilla confermati**: se lava, fuoco, cactus, esplosioni o altri danni vanilla distruggono una pala lasciata a terra, **si perdono pala e pizza**, senza generare ingredienti, impasto o rimborsi.
- [x] **Regola generale di progetto:** i comportamenti ordinari seguono **Minecraft vanilla per default**, salvo eccezioni personalizzate gia' confermate. Non discutere separatamente ogni dettaglio basilare: portare alla decisione dell'autore solo le vere meccaniche nuove o i trade-off di gameplay.
- [x] **Pala come futura arma solo quando VUOTA**, ispirata alla padella di Farmer's Delight Refabricated. **Se OCCUPATA non attiva alcun bonus o effetto speciale da arma** e mantiene intatti i dati della pizza. Statistiche e dettagli non concordati; codice non implementato.

## Questioni da risolvere prima del codice (NON CONFERMATE)

- [ ] Bilanciamento definitivo della stesura (attualmente 4 clic a mano, 1 col mattarello).
- [x] Ricetta del mattarello: 2 bastoni + 1 asse di qualsiasi legno (`#minecraft:planks`), con sblocco nel libro delle ricette (**test in gioco necessario**).
- [x] Texture dedicata del mattarello: item handheld 16×16, sfondo trasparente e pixel opachi; durabilità confermata: **64 usi**, 1 per pizza stesa. **Aspetto da verificare in gioco.**
- [x] **Definito il limite:** massimo 6 ingredienti diversi per quarto fisico (salsa inclusa), sostituisce la vecchia proposta non approvata di 8 porzioni.
- [x] **Classificazione speciale e compatibilita' approvate nel principio:** uova come proteine animali, olive come vegetali, panna come latticini, **olio d'oliva in nuova categoria Oli e grassi (+0 fame/+1 saturazione)**. Ruolo pizza distinto da categoria nutrizionale; una stessa categoria non moltiplica i bonus e non crea automaticamente equivalenze. **Prevedere ingredienti di Farmer's Delight Refabricated** oltre a Vanilla, Italian's Delight e altre mod.
- [ ] **Verificare nel JAR Farmer's Delight Refabricated 26.2/3.6.28** ID, tag, cibi idonei (salsa di pomodoro, pomodoro, cipolle, uova fritte, bacon, prosciutto, pesce), restituzioni di contenitori e casi limite; mappare item compatibili singolarmente, senza accettare ogni alimento.
- [ ] Definire **formato tecnico e mapping iniziale** dei gruppi di equivalenza cross-mod, remainder e ulteriori bilanciamenti; **la regola di gameplay sulle equivalenze e' gia' confermata**; il comportamento delle **salse differenti sulle zone parzialmente condite e' gia' confermato** (rifiuto dell'intera applicazione). La coesistenza di salse diverse sulla **stessa sottoarea e' vietata**, su sottoaree distinte e' consentita. Per uno stesso ingrediente e' confermato **1 item per nuova applicazione effettiva a zone libere**, e nessun item per clic duplicato.
- [x] Definire il gesto per **rimuovere** topping: Shift + clic destro **a mano vuota** sulla zona puntata, dall'ultima applicazione alla piu' vecchia.
- [ ] Implementare cronologia persistente delle applicazioni e rimozione per zona senza duplicazioni.
- [x] Confermato che l'**orientamento della prima divisione dipende dalla direzione del giocatore**, non da un asse mondo fisso, e che la linea e' **parallela allo sguardo**, creando meta' sinistra/destra.
- [x] **Confermati linea parallela alla direzione cardinale piu' vicina allo sguardo e orientamento a scatti** N/S/E/O, per due meta' sinistra/destra. Non viene usata una rotazione libera per le diagonali.
- [x] **Selezione delle zone tramite posizione effettiva del clic**; sui confini e sulle intersezioni una sola zona in modo deterministico, senza selezioni doppie.
- [ ] Definire **quale zona prevale esattamente sul confine** (criterio di spareggio fisso), lo spareggio deterministico negli angoli a meta' tra due cardinali e i dettagli di hitbox/tolleranze (**snap cardinale, gesti e assenza di usura gia' confermati**).
- [ ] Pala: definire ricetta, tooltip, resa grafica, e dettagli di integrazione **facoltativa** con il forno (**trasporto, drop/pickup, despawn e distruzione ambientale vanilla gia' confermati**, cosi' come assenza di durabilita' e stack massimo 1).
- [ ] Recuperare e verificare l'asset della pala dalla vecchia Italian's Delight fornito dall'autore, senza sostituirlo.
- [x] Casi approvati/esclusi per il posizionamento dell'impasto, documentati in [SURFACE_RULES.md](SURFACE_RULES.md). **Soul Sand ora vietata; rimossi modelli ribassati.**
- [ ] Verificare in gioco che la Soul Sand sia vietata e che slab superiori/doppie, scale capovolte, botole superiori chiuse e incudini funzionino ancora; verificare anche recupero se il supporto viene rotto.
- [ ] **Rebalancing finale dei tempi di cottura:** 20/30/60 secondi per Pizza Oven/affumicatore/fornace sono **placeholder** confermati solo come valori provvisori, non tempi definitivi.
- [x] **Meccanica consumo fuel / preriscaldamento / burn time residuo definita**: zero preriscaldamento, solo consumo durante cottura, energia residua conservata tra pizze, avanzamento in pausa se finisce il fuel.
- [ ] Implementare e testare la gestione del burn time residuo e il progresso di cottura persistente del Pizza Oven (inclusi salvataggio, chunk unload e cambio combustibile); il consumo durante inattivita' deve rimanere nullo.
- [x] **Automazione hopper approvata**: **sopra** inserimento pizze crude, **lati** inserimento combustibile, **sotto** estrazione esclusiva delle pizze completamente cotte dallo slot output, come nella fornace vanilla. La GUI e la pala restano metodi manuali.
- [ ] **Implementare e testare gli hopper** rispettando il vincolo di una pizza totale, l'assenza di estrazione automatica a cottura incompleta, le rimanenze del combustibile, i salvataggi e il multiplayer.
- [x] **Ricetta Pizza Oven concordata**: fornace centrale + 4 blocchi di mattoni + 4 blocchi di mattoni di pietra; schema **BSB / SFS / BSB** (B = bricks, S = stone bricks, F = furnace).
- [ ] **Implementare e testare** la ricetta shaped 3x3, il relativo sblocco nel ricettario e la visualizzazione in JEI.
- [ ] Implementare e testare la **pizza-item ottenuta rompendo la pizza formata nel mondo**, con stato di cottura, divisioni, applicazioni e rendering conservati; verificare nessun drop separato di dough/topping e trasferimento corretto al Pizza Oven.
- [x] **Percorso senza pala dal mondo al Pizza Oven — CONFERMATO:** rompere la pizza gia' formata a terra per ottenere la **pizza-item integra**, quindi inserirla nel forno direttamente. Nessun clic destro a mano vuota raccoglie la pizza da terra.
- [x] **Pizza-item nel forno tramite GUI**: la pizza ottenuta rompendo una pizza formata a terra (o da crafting) e' inseribile senza pala tramite la GUI; estrazione tramite GUI disponibile.
- [x] **Layout GUI definito:** usare la **fornace vanilla identica**, con input pizza, fuel, output pizza e indicatori vanilla. **Una sola pizza totale** tra ingresso e risultato: bloccare il nuovo input se l'output e' occupato; la pizza cotta passa nello slot risultato.
- [ ] Implementare e testare la GUI con layout vanilla e logica specializzata: estrazione manuale di pizza parzialmente cotta dallo slot ingresso con progresso salvato; input vietato se output occupato; test pali e hopper senza duplicazioni.
- [x] **Estrazione rapida con pala vuota — CONFERMATO:** clic destro sul Pizza Oven senza GUI preleva la pizza presente, **anche cruda o parzialmente cotta**. Dati e progresso di cottura restano nella pala. L'inserimento diretto con pala occupata era gia' confermato.
- [ ] Griglia crafting della pizza libera e output crudo/cotto.
- [x] **Servizio diretto della pizza cotta — CONFERMATO (12 ottobre 2026):** pizza cotta piazzabile, **4 porzioni prelevabili direttamente con clic destro a mano vuota** sulla zona fisica mirata, **senza tagliare prima**. I coltelli e il Cutting Board **NON sono richiesti** e la precedente meccanica di taglio obbligatorio e' SUPERATA. La pizza intera come item non si mangia.
- [x] **Fette con il gusto del proprio QUARTO FISICO — CONFERMATO:** ogni fetta conserva solo i topping del quarto corrispondente, mai la composizione globale. Vale indipendentemente da divisioni/riunioni logiche fatte con il mattarello.
- [ ] **Implementare e testare il prelievo diretto** dalla pizza cotta appena piazzata (nessuna fase di taglio): clic destro a mano vuota sul quarto ancora presente -> fetta con dati/topping di quel quarto in inventario e scomparsa del solo quarto prelevato. Mano occupata -> nessuna raccolta. Maschera quattro quarti persistente, multiplayer/anti-duplicazione.
- [ ] Riconoscimento esatto di gusti e quantità, priorità tra ricette.
- [ ] Elenco ufficiale delle pizze riconosciute e dei condimenti iniziali.
- [x] **Fame per fetta CONFERMATA:** **crosta/base +1**, **salsa +1**, **latticini/formaggi +1**, **proteine animali/salumi/carne/pesce/uova +2**, **vegetali/verdure/funghi/olive +1**, **oli e grassi +0**, **erbe/spezie +0**; ogni categoria conta **una sola volta per quarto** e la fetta ha **massimo 6 punti fame**. La scelta modifica la proposta iniziale (base 2 -> 1, salsa 0 -> 1).
- [x] **Principio della saturazione per categoria CONFERMATO:** ciascuna fetta usa i propri topping; formaggi e salumi/carne/pesce contribuiscono piu' di salse e verdure/funghi; erbe e spezie incidono poco o nulla. **Ogni categoria conta una sola volta**, senza moltiplicazione per numero di ingredienti. Fame e saturazione possono variare indipendentemente.
- [x] **Bonus saturazione CONFERMATI:** crosta **+1**, salsa **+0,5**, latticini/formaggi **+1,5**, proteine animali/salumi/carne/pesce/uova **+2,5**, vegetali/verdure/funghi/olive **+0,5**, **oli e grassi +1**, erbe/spezie **+0**. Un solo contributo per categoria presente in un quarto, **massimo 6 punti saturazione effettiva per fetta**.
- [x] **Effetti alimentari CONFERMATI per ora:** **nessun effetto speciale o status effect** al consumo delle fette nella Pizza Update 1.0.0; solo fame e saturazione. Possibile riesame in versioni future, non implementare effetti anticipatamente.
- [ ] Definire la mappatura iniziale cross-mod e verificare la conversione tecnica dei punti di saturazione nei campi di FoodComponent/FoodData per la versione target, senza scambiarli per un `saturationModifier` vanilla.
- [ ] **Testare** che le fette con condimenti diversi ripristinino soltanto fame e saturazione e non aggiungano effetti di stato al consumo.
- [ ] **Implementare/testare la saturazione dinamica con coefficienti APPROVATI**: quarti diversi, categorie ripetute, bianca (1), Margherita (3), salame+salsa+formaggio (5,5), cap a 6, due fette con fame uguale ma saturazione differente e compatibilita' di ingredienti esterni.
- [ ] **Test categoria nutrizionale indipendente dal ruolo:** panna con ruolo salsa non somma bonus salsa e latticini; olio non occupa lo slot salsa ma conta nel limite dei sei; uovo + salume non sommano due bonus proteine; olive + funghi non sommano due bonus vegetali; test cross-mod e remainder.
- [ ] **Implementare/testare la fame dinamica** delle singole fette (4 quarti con topping diversi, categoria ripetuta, pizza bianca, cap 6, ingredienti equivalenti di altre mod), senza dare proprieta' commestibili alla pizza intera.
- [x] **Raccolta fette SENZA taglio — CONFERMATA:** clic destro a mano vuota su un quarto ancora presente della pizza cotta piazzata -> una sola fetta corrispondente nell'inventario; il quarto scompare dalla pizza. Mano occupata non raccoglie nulla. La pizza integra come item non viene raccolta con clic destro.
- [x] **Drop alla rottura — CONFERMATO:** pizza cotta **ancora integra (4/4) -> item pizza intera**, mentre una pizza da cui e' stata gia' raccolta almeno una fetta (3/2/1 quarti restanti) -> **soltanto gli item delle fette rimanenti**, con topping del proprio quarto. Mai rimborsi extra o fette duplicate.
- [x] **Decisione sui coltelli SUPERATA:** nessun coltello, tag/API di coltelli o Cutting Board e' richiesto per ottenere una fetta. Prendere le porzioni direttamente dalla pizza cotta posizionata, ispirandosi ai cibi piazzabili di Farmer's Delight Refabricated.
- [x] **Inventario pieno — CONFERMATO:** clic destro a mano vuota con inventario senza spazio non rimuove la fetta dalla pizza e **non la droppa a terra**; nessuna perdita o duplicazione.
- [ ] **Implementare/testare** presa diretta senza coltello da pizza cotta integra, con mano vuota e spazio nell'inventario, mask dei quarti, scomparsa del blocco dopo l'ultima fetta, nessun effetto con mano occupata/inventario pieno, multiplayer, salvataggi e assenza di duplicazioni.
- [ ] **Test rottura pizza cotta parzialmente consumata e integra:** intatta (4/4) -> item pizza completo; 3/2/1 fette residue -> solo le fette corrispondenti. Provare anche rottura del supporto, multiplayer e salvataggi, senza dupe.
- [ ] Test visivo delle nuove forme e hitbox dell'impasto nei cinque stadi (vedi [DOUGH_GEOMETRY.md](DOUGH_GEOMETRY.md)).
- [ ] Gestione delle collisioni, del posizionamento e rendering di dettaglio.
- [ ] API effettive per mantenere data component nelle fornaci vanilla.

## Piano di sviluppo proposto

### Fase 1: fondamenta interattive

- [ ] Registrare palla d'impasto, blocco di preparazione e dati serializzabili.
- [x] Implementare e testare l'appoggio della palla su superfici approvate.
- [x] Implementare e testare la stesura a mano e con mattarello (**entrambi i metodi confermati funzionanti dall'autore**).
- [ ] Salvare e recuperare correttamente stati e oggetti.
- [ ] Test chunk unload, multiplayer, rottura del supporto e superfici particolari.

### Fase 2: sistema condimenti

- [ ] Test mozzarella sulla base completamente stesa: aggiunta via clic destro, un solo item consumato, blocco doppia aggiunta, modello 2.5D e recupero tramite loot.

- [x] Zona interna per condimenti definita nella nuova base finale (`x/z = 3..13`, centro alto 1 pixel), con bordo alto 2 pixel (**conca profonda 1 pixel**); implementazione dopo il test della geometria.
- [ ] Decidere se il pomodoro di Farmer's Delight va applicato direttamente oppure trasformato prima in salsa.

- [ ] Definizioni ingredienti via codice/datapack, con **identita' canonica** dei gruppi equivalenti cross-mod distinta dall'ItemStack originale consumato e da categorie generiche.
- [ ] Aggiunta topping con clic destro e gestione quantità.
- [ ] Implementare le **divisioni facoltative create col mattarello** (intera -> meta' -> quarti) e la **riunione inversa con Shift + clic destro** (due quarti stessa meta' -> due meta' -> intera), **senza consumare durabilita'**, salvandone la configurazione e preservando posizioni e storici dei topping; selezionare la zona dal punto reale di clic con **un solo risultato deterministico ai confini**; evidenziare le linee solo con mattarello o ingrediente valido.
- [ ] Implementare l'identita' delle singole applicazioni, le coperture su zone/quarti logici, il **vincolo dello stesso ingrediente massimo una volta per sottoarea**, le applicazioni parziali dei topping alle sole zone ancora libere, **il blocco totale delle salse differenti se una qualsiasi parte della zona mirata contiene gia' un'altra salsa**, la **validazione atomica del massimo di 6 tipi per quarto fisico (salsa inclusa)** e la rimozione parziale con rimborso senza duplicazioni (vedi [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md)).
- [ ] Recupero diretto nell'inventario dei topping **rimossi completamente** (drop solo se pieno); **rottura pizza formata INTEGRA -> pizza-item cruda/cotta con dati completi** (pizza cotta servita parzialmente -> sole fette residue), senza duplicare ingredienti.
- [ ] Pala: trasporto diretto di una pizza alla volta con dati persistenti e trasferimento sicuro al mondo/forno, **senza durabilita' ne' usura nei trasferimenti**, non impilabile in entrambi gli stati.
- [ ] Test: due pale vuote non si impilano; ogni pala occupata resta singola; spostamenti tra slot e contenitori conservano i dati senza duplicazioni.
- [ ] **Drop/pickup**: premere Q con pala occupata e raccoglierla, anche da altro giocatore; verificare che pizza, applicazioni, divisioni, orientamento, stadio e seed grafico restino identici. Ripetere in multiplayer e con piu' pale.
- [ ] **Despawn naturale vanilla**: lasciare a terra una pala con pizza fino al despawn; verificare che l'entita' scompaia normalmente, senza drop della pala o ingredienti e senza mantenere la pizza altrove; nessuna modifica alle regole standard del mondo/server.
- [ ] **Distruzione vanilla della pala occupata**: verificare danni ambientali da fuoco, lava, cactus ed esplosioni, controllando che se l'item viene distrutto sparisca anche la pizza, senza drop extra ne' duplicazioni.
- [ ] Gestire gli eventuali remainder dei contenitori senza duplicazioni.
- [ ] Nomi dinamici localizzati e ricette riconosciute.
- [ ] Rendering provvisorio per convalidare i dati.

### Estensione successiva: pala usabile anche come arma (pianificata)

- [ ] Studiare il comportamento effettivo della **padella di Farmer's Delight Refabricated** come riferimento per la pala.
- [ ] Concordare danni, velocita' d'attacco, animazioni ed effetti **della sola pala vuota**, e regole di priorita' con raccolta/posizionamento. **Pala occupata: funzionalita' speciali da arma disabilitate** (decisione confermata).
- [x] **Confermato** che la pala con pizza non funge da arma e non conferisce effetti/bonus speciali da combattimento.
- [ ] Verificare che lo stato vuota/occupata disattivi e riattivi correttamente gli attributi speciali da arma; gli attacchi base di Minecraft non devono alterare, perdere o duplicare la pizza trasportata.
- [ ] Aggiungere e testare la funzionalita' **solo dopo** averne definito le regole; non e' richiesta per considerare concluso il sistema base di trasporto.

### Fase 3: rendering 2.5D

- [x] Texture dell'impasto crudo nei cinque stadi di stesura: superficie, lati e particelle dedicate 16×16, completamente opache. **Aspetto da verificare in gioco**; [anteprima e test](TEXTURES.md).
- [ ] Pizza condita cruda/cotta.
- [ ] Salse piatte, formaggi/condimenti 2.5D, preset riutilizzabili.
- [ ] Seed deterministico e conservazione della distribuzione.
- [ ] Aspetto item inventario, eventuali fette e pizza nel forno.
- [ ] Fallback per topping modded e stress test prestazioni.

### Fase 4: Pizza Oven

- [ ] Forno compatto orientabile con una pizza alla volta, **utilizzabile con o senza pala** (la pala non e' obbligatoria).
- [ ] BlockEntity persistente, **GUI VANILLA della fornace** (input pizza, fuel, output pizza, fiamma/freccia standard) e inserimento/estrazione diretti con pala, senza bruciatura. Capienza massima una pizza totale in input/output.
- [ ] Test inserimento con pala occupata: un solo trasferimento, pala svuotata solo a successo, dati della pizza intatti; forno occupato -> nessun consumo/sostituzione. Test parallelo inserimento ed estrazione tramite GUI senza pala.
- [ ] Test estrazione rapida con **pala vuota** a qualsiasi stadio della pizza (cruda, parzialmente cotta, cotta), incluso forno vuoto, conservazione progressi e ripresa della cottura dopo reinserimento; verificare multiplayer e nessuna duplicazione.
- [ ] Tutti i combustibili vanilla, rimanenze dei contenitori e **energia residua conservata senza consumi a vuoto**; solo pizza come ricetta valida, inizio immediato senza preriscaldamento e progresso in pausa senza energia.
- [ ] **Implementare e testare l'automazione hopper gia' confermata** (sopra pizze crude, lati combustibile, sotto solo pizze cotte). Verificare che non estragga una pizza in ingresso con progresso parziale e che non si duplichi l'output.
- [ ] Modelli, animazioni, traduzioni e **implementazione della ricetta gia' approvata** (BSB / SFS / BSB).

### Fase 5: forno vanilla e crafting

- [ ] Prototipo conservazione componenti passando da **fornace e affumicatore vanilla**.
- [ ] Ricetta speciale di crafting per pizze libere.
- [ ] JEI: esempi di pizze riconosciute, condimenti, lavorazioni e forni.
- [ ] Implementare il sistema di **servizio diretto**: pizza cotta piazzata -> clic destro a mano vuota sul quarto -> un item fetta nell'inventario se c'e' spazio, altrimenti nessuna modifica. Nessun taglio preliminare/utensile. La pizza intera non e' commestibile; ogni fetta contiene i condimenti del quarto fisico. Quarto mancante visibile in-world; l'ultima raccolta elimina il blocco.

### Fase 6: test e pubblicazione

- [ ] Test approfonditi con ingredienti della mod e di altre mod.
- [ ] Test stress di tanti topping/pizze, multiplayer e server dedicato.
- [ ] Test no bruciatura, hopper, chunk unload, salvataggi e dupe.
- [ ] Test di bilanciamento in gioco dei tempi, costi, fame e saturazione (**bonus e tetti a 6 per fetta per entrambe le statistiche gia' approvati**; eventuali modifiche richiedono una nuova decisione dell'autore).
- [ ] Documentazione EN/IT, README, CHANGELOG 1.0.0, JEI.
- [ ] Build finale, PR su `main`, release GitHub, CurseForge, Modrinth.

## Regole da rispettare

- Sviluppare sul branch `feature/pizza-1.0.0`; `main` è protetta e non va modificata direttamente.
- Prima di implementare meccaniche non ancora confermate, discuterle.
- Non fare modifiche radicali alle meccaniche cheese/cured meats già funzionanti.
- **Non spostare, eliminare, sovrascrivere o sostituire asset creati dall'autore** senza consenso esplicito.
- Tutti i JSON d'esempio nei documenti sono **bozze progettuali**, non file di datapack già utilizzabili.

## Prossimo tema da concordare

**Priorita' esatta ai confini delle zone e hitbox tecniche** e spareggio deterministico al confine esatto fra direzioni cardinali; poi formato delle definizioni di equivalenza cross-mod. La **scelta di una sola zona dalla posizione effettiva del clic, anche sui confini, e' confermata**. **La linea e' gia' confermata parallela alla direzione cardinale piu' vicina allo sguardo (scatti N/S/E/O)** e divide sinistra/destra. L'orientamento dipendente dal giocatore e' confermato. **Il principio di equivalenza e' confermato:** stesso ingrediente logico, un solo tipo nel limite di 6 e nessuna doppia applicazione sullo stesso quarto. L'assenza di usura del mattarello per dividere/riunire e il limite di **6 ingredienti diversi per quarto** sono gia' confermati. Il rifiuto delle salse differenti su zone anche solo parzialmente occupate e' confermato. Il divieto di ripetere lo stesso ingrediente sulla stessa sottoarea e' gia' confermato. La riunione con Shift + clic destro col mattarello e la conservazione dei topping sono gia' **confermate**. Le decisioni gia' prese sono registrate in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).
