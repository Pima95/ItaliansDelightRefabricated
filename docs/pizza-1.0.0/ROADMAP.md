# Roadmap — Pizza Update 1.0.0

## Stato

**Stato implementazione pizza (14 ottobre 2026):** stesura, quarti, recupero `raw_pizza`, condimenti SOLID e **overlay scuro dei tagli** verificati funzionanti dall'autore. **FASE 3F confermata funzionante dall'autore (15 ottobre 2026):** sincronizzazione della composizione completa e visualizzazione degli ItemStack originali sui quarti; corretta la patina rossa con `OverlayTexture.NO_OVERLAY`. **La resa attuale degli item appoggiati sulla pizza, simile a un item frame, e' esplicitamente PROVVISORIA: non e' la grafica finale.** Nessuna texture, modello del blocco o regola gameplay modificata. Rimangono da sviluppare il sistema anti-duplicazione dei recipienti, ricette forno e nomi dinamici. [Test](TOPPING_RENDER_TESTS.md).

## Checklist decisioni già CONFERMATE

- [x] Versione-obiettivo 1.0.0 con sistema di pizza personalizzabile.
- [x] Palla d'impasto posizionata direttamente sopra i blocchi, non sul Cutting Board.
- [x] Stesura sia a mano (clic destro) sia con mattarello.
- [x] Condimento diretto nel mondo con clic destro, senza GUI.
- [x] Preparazione tramite crafting table disponibile come alternativa.
- [x] Pizze riconosciute con nomi prestabiliti, altre con nomi dinamici.
- [x] **RICERCA GUSTI (13 ottobre 2026):** documentate **107 combinazioni** in [NAMED_PIZZAS.md](NAMED_PIZZAS.md): **56** con ingredienti già registrati (inclusa Principessa), **15** con le nuove fette carota/patata da creare e **36** proposte future ancora NON approvate. Tre esempi di pizze a quarti restano proposte tecniche, **non preset approvati**.
- [x] **NOMI FISSI DEFINITIVAMENTE APPROVATI (13 ottobre 2026):** selezionati **19 SÌ** (18 con ingredienti esistenti + **Patate e Speck** dopo la creazione dell'item) e **52 NO** a **nome dinamico**, dei 71 gusti 1.0.0. Principessa è approvata con **salsa di pomodoro, mozzarella, prosciutto crudo a fette e panna** (crema NON salsa). Tutte le 71 righe con status SÌ/NO e i 19 nomi esatti sono in [NAMED_PIZZAS.md](NAMED_PIZZAS.md). **Non implementato; nessun nuovo topping approvato dalle 36 ricette future.**
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
- [x] **Ricognizione dei registri sorgente 26.2/3.6.28:** verificati `ModItemIds.java`, `ModBlockItemIds.java` e `ModItems.java` di Farmer's Delight `fabric/latest/26.2`, incluso onion come BlockItem; verificati gli item e i tag presenti in Italian's Delight. Inventario: [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). **La ricognizione non costituisce approvazione o implementazione del supporto in gioco.**
- [ ] **Verificare JAR, datapack e gioco:** contenuti dei tag comuni oltre a quelli presenti nelle risorse Italian's Delight; remainder, combinazioni di topping, whitelist dei 29 item/tipi ufficiali piu' 2 nuovi da creare, filtri di esclusione, gruppi canonici e test di compatibilita' cross-mod.
- [x] **Architettura leggera APPROVATA:** mappa diretta in cache costruita al caricamento mondo/`/reload`, senza letture JSON e senza scansione di tag a ogni clic o per tick; base dei 31 tipi esplicita, **solo mappature per ID esterno verificato**, nessun selettore tag che abiliti automaticamente item. Prestazioni da misurare, non è dimostrato un primato assoluto sul Java diretto. [Dettagli](DATA_AND_COMPATIBILITY.md).
- [ ] **Misurare prestazioni** del mapping compilato vs mapping diretto (caricamento/reload, memoria, tempo per clic, pizze dense, multiplayer e modpack): se l'overhead è rilevante, mantenere statici i mapping di base e restringere le estensioni. Non introdurre gestione equivalenze per tick.
- [x] **Ricognizione 31 identità e tag DOCUMENTATA:** 20 tipi Italian's Delight, 9 Farmer's Delight (4 coppie crudo/cotto), 2 nuovi vegetali. Tag `c:` controllati, inclusi quelli troppo generici contenenti item interi. [Tabella](INGREDIENT_EQUIVALENCES.md). **Mapping esterno singolo verificato APPROVATO**, ma singoli ID di altre mod e formato JSON ancora da implementare.
- [x] **Schema delle equivalenze CONFERMATO (13 ottobre 2026):** un JSON per ID, esclusivamente proprietà `item` e `canonical`, nessun override dei mapping ufficiali; mapping esterni discordi sullo stesso ID rifiutati con diagnostica senza compromettere gli altri, nessun tag selettore automatico. [Specifica](INGREDIENT_EQUIVALENCES.md).
- [ ] **Test nuovo loader e mapping esterni:** build Java 25, log iniziale 33 ID, `/reload`, JSON validi e invalidi, override nativi, item esclusi, conflitti fra pack, mod opzionale assente. Scegliere i primi ID esterni realmente verificati. [Procedura](DATAPACK_RELOAD_TESTS.md).
- [x] Definire il gesto per **rimuovere** topping: Shift + clic destro **a mano vuota** sulla zona puntata, dall'ultima applicazione alla piu' vecchia.
- [ ] Implementare cronologia persistente delle applicazioni e rimozione per zona senza duplicazioni.
- [x] Confermato che l'**orientamento della prima divisione dipende dalla direzione del giocatore**, non da un asse mondo fisso, e che la linea e' **parallela allo sguardo**, creando meta' sinistra/destra.
- [x] **Confermati linea parallela alla direzione cardinale piu' vicina allo sguardo e orientamento a scatti** N/S/E/O, per due meta' sinistra/destra. Non viene usata una rotazione libera per le diagonali.
- [x] **Selezione delle zone tramite posizione effettiva del clic**; sui confini e sulle intersezioni una sola zona in modo deterministico, senza selezioni doppie.
- [x] **Spareggi confini e diagonali APPROVATI (13 ottobre 2026):** prevalgono destra sui confini delle meta', davanti sui confini dei quarti, davanti-destra al centro. In caso di visuale equidistante fra cardinali, prevale il successivo in senso orario (N/E -> E, E/S -> S, S/O -> O, O/N -> N). Nessuna hitbox separata per le linee. Tolleranza numerica iniziale `1/1024` di blocco, **da validare in test**. [Dettagli](QUARTERS_AND_PEEL.md).
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
- [x] **NOMI INDIVIDUALI DEGLI SPICCHI COTTI — CONFERMATO (13 ottobre 2026):** **anche quando la pizza intera ha un nome dinamico perché i quarti sono diversi, ogni singolo spicchio cotto** viene confrontato **solo con gli ingredienti canonici del proprio quarto** e può ottenere uno dei **19 nomi fissi**; ad esempio quarto con pomodoro + mozzarella + basilico -> **Spicchio di Margherita**. Altrimenti **nome dinamico dello spicchio** basato esclusivamente sui suoi condimenti. Valido sia per prelievo a mano vuota sia per drop di fette residue rompendo una pizza cotta già parzialmente servita. Il confronto non richiede che gli altri quarti coincidano, e non cambia il nome dinamico della pizza intera mista.
- [x] **NOME E TOOLTIP DELLA PIZZA INTERA — CONFERMATO (13 ottobre 2026):** 4 quarti identici con preset -> nome fisso; 4 quarti identici non riconosciuti -> **`Pizza con [ingredienti]`** (eventualmente abbreviata, tooltip completo); quarti differenti -> **`Pizza assortita`**, tooltip **Quarto 1–4** con gusti riconosciuti o ingredienti del quarto. Ogni spicchio cotto conserva il naming individuale indipendente. Specifica in [NAMED_PIZZAS.md](NAMED_PIZZAS.md#nomi-e-tooltip-delle-pizze--confermato-13-ottobre-2026).
- [ ] **Implementare/testare naming e tooltip pizza intera:** uniformità canonica, 19 preset, 52 NO, pizza assortita, lingua it_it/en_us, quarti numerati stabilmente, lista completa, abbreviazione del display name senza perdita dei dati e indipendenza del nome delle fette.
- [ ] **Implementare/testare nomi individuali delle fette cotte:** un quarto Margherita in pizza con altri tre gusti -> Spicchio di Margherita; un quarto Principessa -> Spicchio di Principessa; un quarto solo pomodoro -> Spicchio di pizza rossa; un quarto personalizzato -> nome dinamico basato sui propri ingredienti; copertura zero -> pizza bianca; test equivalenti crudo/cotto/cross-mod, quarti misti, inventario pieno, reload, tooltips e raccolta/drop senza creare nuovi item per singolo gusto.
- [x] **Fette con il gusto del proprio QUARTO FISICO — CONFERMATO:** ogni fetta conserva solo i topping del quarto corrispondente, mai la composizione globale. Vale indipendentemente da divisioni/riunioni logiche fatte con il mattarello.
- [ ] **Implementare e testare il prelievo diretto** dalla pizza cotta appena piazzata (nessuna fase di taglio): clic destro a mano vuota sul quarto ancora presente -> fetta con dati/topping di quel quarto in inventario e scomparsa del solo quarto prelevato. Mano occupata -> nessuna raccolta. Maschera quattro quarti persistente, multiplayer/anti-duplicazione.
- [x] **Scelta nomi riconosciuti CONFERMATA:** 19 combinazioni a nome fisso e 52 a nome dinamico per la release 1.0.0; 36 ricette future non approvate.
- [x] **MATCHING 4/4 QUARTI FISICI APPROVATO (13 ottobre 2026):** tutti e quattro i quarti devono contenere **esattamente la stessa combinazione canonica richiesta dal singolo preset SÌ**; anche un solo ingrediente extra/mancante in un quarto impone il **nome dinamico**. Valido senza divisioni, con divisioni e dopo riunioni. Identità crudo/cotto e cross-mod equivalenti, ordine di posa irrilevante. Nessun nome fisso per pizze miste nella 1.0.0.
- [ ] **Implementare 19 preset di riconoscimento e nome localizzato** secondo [NAMED_PIZZAS.md](NAMED_PIZZAS.md) e verificare che le 52 combinazioni NO e le altre combinazioni libere usino nomi dinamici. **Patate e Speck** va attivata solo dopo il nuovo item patate a fette; **Principessa** ha panna non salsa sopra il pomodoro. Usare identità canoniche (crudo/cotto equivalente) e mapping espliciti per ID esterno verificato; evitare falsi positivi su topping aggiuntivi, quarti misti e ordine di posa; nessun bonus nutrizionale speciale.
- [ ] **Implementare e testare la regola già approvata del match esatto su tutti e quattro i quarti fisici** dei 19 preset; Bianca vuota, Rossa solo pomodoro, Margherita uniforme; un quarto con speck extra o senza basilico -> dinamico; prova su pizza intera, divisa e riunita; tag equivalenti e ordine di posa irrilevante. Definire solo gli aspetti tecnici ancora aperti, inclusi stato/nome dopo aver servito fette.
- [x] **AMBITO TOPPING 1.0.0 — CONFERMATO:** soltanto i 31 tipi previsti da Italian's Delight e Farmer's Delight (incluse le 2 fette vegetali future); gli item di altre mod possono essere sostituti **solo con mappatura puntuale per ID verificato**, non per semplice appartenenza ai tag `c:`. Nessun nuovo tipo ammesso implicitamente.
- [x] **Filtro preparazione e dimensione CONFERMATO:** ammettere come topping soltanto **item gia' pronti in forma plausibile** (fette di salumi/formaggi, formaggi grattugiati/spicchi, carne/pesce in pezzi pronti, verdure tagliate, creme, salse, erbe). **Vietare forme intere di prosciutto e altri salumi anche se cotti, ruote di formaggio, bistecche intere crude/cotte, cibi da lavorare e ingredienti intermedi**, anche se ID/tag equivalenti. Un modello visivo ridotto non puo' sostituire un item lavorato. Cutting Board solo per ricette reali che producono una forma valida; **non** serve per condire o per prelevare le fette di pizza cotta. Non imporre cottura preventiva a vegetali gia' pronti; **carne e pesce gia' porzionati sono consentiti sia crudi sia cotti, con gli stessi effetti e valori alimentari**; le **quattro coppie sono ora approvate come tipi ufficiali**, e il mapping per equivalenti esterni resta da implementare.
- [x] **Stato crudo/cotto — CONFERMATO:** le porzioni di carne e pesce sono topping validi **in entrambe le varianti** quando il tipo e' nel catalogo ufficiale: **stessi bonus proteine animali (+2 fame/+2,5 saturazione), nessun effetto aggiuntivo e nessuna penalita' per il crudo**. Nessuna distinzione nutrizionale e nessun obbligo di cottura preventiva. Vietati i pezzi grandi in entrambi gli stati; non inventare conversioni automatiche di ItemStack.
- [x] **Un'unica identita' canonica per varianti crudo/cotto dello stesso ingrediente — CONFERMATO:** bacon/cooked_bacon, chicken_cuts/cooked_chicken_cuts, cod_slice/cooked_cod_slice e salmon_slice/cooked_salmon_slice condividono per ciascuna coppia anti-doppione e conteggio come **un solo tipo nel limite sei**; non unificare specie o alimenti diversi. Il rimborso restituisce l'**ItemStack crudo/cotto realmente utilizzato**, non una conversione.
- [ ] **Test varianti crude e cotte:** rifiutare la doppia applicazione sullo stesso quarto con ordine invertito; clic completamente duplicato senza consumo; area parzialmente coperta da una delle versioni -> applicare un solo item ai quarti liberi; controllare conteggio di uno nel limite sei, stato ItemStack originale e bonus fame/saturazione identici; test equivalenti di mod terze.
- [ ] **Test filtro topping e Cutting Board:** usare la whitelist dei 29 tipi approvati + due nuove fette dopo registrazione; respingere uova intere, salumi interi, formaggi interi, bistecche non tagliate, salumi/formaggi non approvati, quattro NO espliciti e item fuori catalogo anche se in tag generici. Verificare contenitori, tag `c:` e nessun consumo al rifiuto.
- [x] **Prima shortlist dei singoli ingredienti registrati** in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md): ID sorgente verificati, uso pizza proposto, esclusioni e tag con indicazione esplicita dello stato di verifica.
- [x] **Catalogo ufficiale topping 1.0.0 APPROVATO (13 ottobre 2026):** 29 tipologie con item reali delle due mod (33 ID fisici per le quattro coppie crudo/cotto), 4 NO espliciti e due nuovi tipi **carote/patate a fette** da creare. **Totale 31 tipi previsti**, tutti elencati in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md). Scelte dell'autore non ancora implementate.
- [x] **Uova:** ammettere solo `farmersdelight:fried_egg`, **mai** `minecraft:egg` intero (anche con tag ampi). **Carne macinata:** ammettere espressamente `farmersdelight:minced_beef`, non bistecche o hamburger/patty interi.
- [x] **Quattro NO espliciti:** `italiansdelight:parmigiano_reggiano_wedge`, `italiansdelight:pecorino_romano_wedge`, `italiansdelight:mascarpone`, `italiansdelight:salt` — nonostante eventuali tag comuni. **Altri item fuori catalogo** ugualmente esclusi.
- [x] **RICETTE CONFERMATE (13 ottobre 2026):** Cutting Board di Farmer's Delight Refabricated, **1 `minecraft:carrot` + coltello -> 4 fette di carota; 1 `minecraft:potato` + coltello -> 4 fette di patata**. Il coltello mantiene la normale usura Cutting Board e non viene consumato. Le fette crude si mettono sulla pizza e **cuociono con essa**, senza creare varianti cotte separate. Entrambe sono **Vegetali: +1 fame/+0,5 saturazione una volta per categoria nel quarto**; mai ammettere carota/patata intera come topping.
- [ ] **Implementare i due nuovi item Italian's Delight** e le ricette Cutting Board **gia' definite come principio e rese**, scegliendo ID effettivi, texture/modelli e registrazione JSON per MC 26.2/FDR 3.6.28. Testare ottenimento 4 fette per input, coltello valido/usura standard, assenza di item cotto separato, rendering e riconoscimento categoria Vegetali. **Non si tratta di ricette gia' implementate.**
- [ ] **Implementare whitelist e data mapping** dei 31 tipi approvati; testare ciascun item positivo/negativo, il gruppo nutrizionale, le quattro coppie crudo/cotto, gli ID esterni verificati (tag solo per ricognizione), il ruolo salsa e i remainder delle ciotole. **L'elenco di 19 nomi fissi e 52 ricette a nome dinamico è APPROVATO** in [NAMED_PIZZAS.md](NAMED_PIZZAS.md); rimane da implementarne il matching, non da riselezionarlo.
- [x] **Fame per fetta CONFERMATA:** **crosta/base +1**, **salsa +1**, **latticini/formaggi +1**, **proteine animali/salumi/carne/pesce/uova +2**, **vegetali/verdure/funghi/olive +1**, **oli e grassi +0**, **erbe/spezie +0**; ogni categoria conta **una sola volta per quarto** e la fetta ha **massimo 6 punti fame**. La scelta modifica la proposta iniziale (base 2 -> 1, salsa 0 -> 1).
- [x] **Principio della saturazione per categoria CONFERMATO:** ciascuna fetta usa i propri topping; formaggi e salumi/carne/pesce contribuiscono piu' di salse e verdure/funghi; erbe e spezie incidono poco o nulla. **Ogni categoria conta una sola volta**, senza moltiplicazione per numero di ingredienti. Fame e saturazione possono variare indipendentemente.
- [x] **Bonus saturazione CONFERMATI:** crosta **+1**, salsa **+0,5**, latticini/formaggi **+1,5**, proteine animali/salumi/carne/pesce/uova **+2,5**, vegetali/verdure/funghi/olive **+0,5**, **oli e grassi +1**, erbe/spezie **+0**. Un solo contributo per categoria presente in un quarto, **massimo 6 punti saturazione effettiva per fetta**.
- [x] **Effetti alimentari CONFERMATI per ora:** **nessun effetto speciale o status effect** al consumo delle fette nella Pizza Update 1.0.0; solo fame e saturazione. Possibile riesame in versioni future, non implementare effetti anticipatamente.
- [ ] Definire la mappatura iniziale cross-mod e verificare la conversione tecnica dei punti di saturazione nei campi di FoodComponent/FoodData per la versione target, senza scambiarli per un `saturationModifier` vanilla.
- [ ] **Testare** che le fette con condimenti diversi ripristinino soltanto fame e saturazione e non aggiungano effetti di stato al consumo.
- [ ] **Implementare/testare la saturazione dinamica con coefficienti APPROVATI**: quarti diversi, categorie ripetute, bianca (1), Margherita (3), salame+salsa+formaggio (5,5), cap a 6, due fette con fame uguale ma saturazione differente e compatibilita' di ingredienti esterni.
- [x] **Panna come condimento cremoso, NON salsa — CONFERMATO (13 ottobre 2026):** `italiansdelight:cream_bowl` puo' essere aggiunta **sopra `farmersdelight:tomato_sauce` nello stesso quarto** senza sostituirla, occupare lo slot salsa o bloccare altri condimenti consentiti. Conta **1 tipo nel limite di sei**, da' soltanto bonus nutrizionale **Latticini (+1 fame/+1,5 saturazione una volta per categoria)**, non bonus salsa extra; l'eventuale pomodoro conserva il proprio bonus. **La ricotta ha lo stesso ruolo cremoso NON salsa della panna**; ciascuna e' un tipo distinto nel limite sei, ma condividono il bonus nutrizionale Latticini.
- [x] **Ricotta come condimento cremoso NON salsa — CONFERMATO (13 ottobre 2026):** `italiansdelight:ricotta` puo' essere aggiunta sopra `farmersdelight:tomato_sauce` senza occupare lo slot salsa ne' impedire la panna. Panna e ricotta rimangono **due ingredienti distinti** entro il limite di sei, **una sola categoria Latticini** (+1 fame/+1,5 saturazione per categoria), senza bonus nutrizionale Salsa da questi item. La salsa pomodoro mantiene il proprio bonus e ruolo.
- [ ] **Test ricotta + pomodoro + panna + mozzarella:** i quattro ingredienti sono quattro tipi distinti; si mantiene un'unica salsa effettiva (pomodoro), con **3 fame/3 saturazione** per fetta (crosta 1/1 + salsa 1/0,5 + latticini 1/1,5). Rendering cremoso che non cancella la salsa sottostante, no duplicati di ricotta nella stessa zona, gestione dell'ItemStack originario e delle ciotole/remainder senza perdite o duplicazioni.
- [ ] **Test panna + pomodoro + mozzarella, anche tramite mod compatibili:** ruoli indipendenti, slot salsa occupato soltanto dal pomodoro, composizione e conteggio di 3 tipi distinti, **fetta 3 fame/3 saturazione** (crosta + salsa + un solo bonus latticini); visualizzazione crema sopra pomodoro, rimborso ItemStack e ciotola una sola volta senza dupe; rifiutare una seconda panna sullo stesso quarto. Test cross-mod, stessa identita' canonica e test normali sulle salse diverse.
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

- [x] **FASE 3E — overlay di divisione verificato dall'autore:** linee visibili negli stati 1/3/5/7, con colore nero/scuro applicato dall'autore e meccanica confermata. [Test](DIVISION_OVERLAY_TESTS.md).
- [x] **FASE 3E confermata dall'autore:** visualizzazione delle linee e comportamento della divisione funzionanti; eventuali controlli multiplayer restano consigliati.
- [x] **FASE 3F — rendering funzionale dei topping SOLID testato dall'autore:** `PizzaToppingRenderer` visualizza gli ItemStack originali secondo `remaining_mask`, la BlockEntity sincronizza la composizione. Confermata anche la correzione della patina rossa. **Aspetto "item frame" considerato PLACEHOLDER grafico, da sostituire**, senza modificare il gameplay. Il vecchio flag mozzarella resta separato. [Test](TOPPING_RENDER_TESTS.md).

- [x] **FASE 3C verificata dall'autore; aggiornamento dei drop e della texture da testare (14 ottobre 2026):** `raw_pizza` mantiene componente persistente completo, ma viene droppata solo se la pizza e' ancora suddivisa oppure contiene condimenti. Stadio 4 intero e pulito -> `farmersdelight:wheat_dough` anche dopo riunione. Nuova texture pixel art `textures/item/raw_pizza.png` (16×16 RGBA) e relativo modello; nessun asset preesistente sovrascritto. [Test](RAW_PIZZA_ITEM_TESTS.md).
- [ ] **Verificare nuove regole e texture:** build Java 25, `division_state = 0` pulito -> impasto; diviso -> raw_pizza; diviso poi riunito pulito -> impasto; mozzarella prototipo oppure qualsiasi topping residuo -> raw_pizza. Verificare Pick Block, explosion loot, icon 16×16 e Q/inventario.

- [x] **FASE 3A — modello persistente e BlockEntity nel branch:** codec versione 1, quattro quarti fisici, divisione e orientamento, identità delle applicazioni, maschere originaria/residua, `ItemStack` originale salvato con i suoi componenti, contatore cronologico e seed. Invarianti statici controllati; **compilazione e persistenza nel mondo non ancora provate**. [Guida](PERSISTENT_COMPOSITION_TESTS.md).
- [x] **FASE 3A testata dall'autore:** build, persistenza e comportamento del blocco confermati funzionanti; [guida](PERSISTENT_COMPOSITION_TESTS.md).

- [x] **SECONDO PASSO INFRASTRUTTURA completato (build da testare):** listener dei datapack e cache diretta degli ingredienti; nessun comportamento di clic modificato.

- [x] **PRIMO PASSO CODICE — REGISTRO CANONICO (13 ottobre 2026):** aggiunti `PizzaToppingType` (categoria nutrizionale distinta dal ruolo salsa/crema/solido) e `PizzaToppingCatalog` (mappa immutabile di 31 tipi / 33 item nativi). Quattro coppie FDR crudo/cotto raggruppate, panna/ricotta `CREAMY` ma non `SAUCE`; due identità vegetali future senza item. Avvio del registro in `ItaliansDelight.onInitialize` dopo gli item/blocchi; controllo duplicati e dimensioni. **Verifica statica dei 31/33 completata; build Java 25 e test in gioco ancora da eseguire.** Non collegato al prototipo mozzarella né a un loader JSON. [Identità e test](INGREDIENT_EQUIVALENCES.md).
- [ ] Test mozzarella sulla base completamente stesa: aggiunta via clic destro, un solo item consumato, blocco doppia aggiunta, modello 2.5D e recupero tramite loot.

- [x] Zona interna per condimenti definita nella nuova base finale (`x/z = 3..13`, centro alto 1 pixel), con bordo alto 2 pixel (**conca profonda 1 pixel**); implementazione dopo il test della geometria.
- [ ] Decidere se il pomodoro di Farmer's Delight va applicato direttamente oppure trasformato prima in salsa.

- [x] **JSON/reload/cache implementati:** caricamento iniziale 33 ID ufficiali confermato dal giocatore nel log; restano da provare mapping JSON esterni, conflitti ed esclusioni.
- [ ] Aggiunta topping con clic destro e gestione quantità.
- [x] **FASE 3B/3E provate dall'autore:** divisioni, riunioni, valori `division_state` e overlay scuro. Anteprima temporanea e hitbox invariata. [Guida](DIVISION_OVERLAY_TESTS.md).
- [x] **FASE 3D — condimenti SOLID testati dall'autore:** applicazione/rimozione sui quarti, rimborsi LIFO, persistenza item e no duplicazione. Restano i tre topping con recipiente, rendering fisico degli ingredienti e nomi/ricette. [Test archiviati](TOPPING_INTERACTIONS_TESTS.md).
- [ ] Recupero diretto nell'inventario dei topping **rimossi completamente** (drop solo se pieno); **rottura pizza formata INTEGRA -> pizza-item cruda/cotta con dati completi** (pizza cotta servita parzialmente -> sole fette residue), senza duplicare ingredienti.
- [ ] Pala: trasporto diretto di una pizza alla volta con dati persistenti e trasferimento sicuro al mondo/forno, **senza durabilita' ne' usura nei trasferimenti**, non impilabile in entrambi gli stati.
- [ ] Test: due pale vuote non si impilano; ogni pala occupata resta singola; spostamenti tra slot e contenitori conservano i dati senza duplicazioni.
- [ ] **Drop/pickup**: premere Q con pala occupata e raccoglierla, anche da altro giocatore; verificare che pizza, applicazioni, divisioni, orientamento, stadio e seed grafico restino identici. Ripetere in multiplayer e con piu' pale.
- [ ] **Despawn naturale vanilla**: lasciare a terra una pala con pizza fino al despawn; verificare che l'entita' scompaia normalmente, senza drop della pala o ingredienti e senza mantenere la pizza altrove; nessuna modifica alle regole standard del mondo/server.
- [ ] **Distruzione vanilla della pala occupata**: verificare danni ambientali da fuoco, lava, cactus ed esplosioni, controllando che se l'item viene distrutto sparisca anche la pizza, senza drop extra ne' duplicazioni.
- [ ] Gestire gli eventuali remainder dei contenitori senza duplicazioni.
- [ ] Nomi dinamici localizzati, tooltip completi per quarti, abbreviazione delle stringhe troppo lunghe e ricette riconosciute (regole di denominazione già approvate).
- [ ] Rendering provvisorio per convalidare i dati.

### Estensione successiva: pala usabile anche come arma (pianificata)

- [ ] Studiare il comportamento effettivo della **padella di Farmer's Delight Refabricated** come riferimento per la pala.
- [ ] Concordare danni, velocita' d'attacco, animazioni ed effetti **della sola pala vuota**, e regole di priorita' con raccolta/posizionamento. **Pala occupata: funzionalita' speciali da arma disabilitate** (decisione confermata).
- [x] **Confermato** che la pala con pizza non funge da arma e non conferisce effetti/bonus speciali da combattimento.
- [ ] Verificare che lo stato vuota/occupata disattivi e riattivi correttamente gli attributi speciali da arma; gli attacchi base di Minecraft non devono alterare, perdere o duplicare la pizza trasportata.
- [ ] Aggiungere e testare la funzionalita' **solo dopo** averne definito le regole; non e' richiesta per considerare concluso il sistema base di trasporto.

### Fase 3: rendering 2.5D

- [x] Texture dell'impasto crudo nei cinque stadi di stesura: superficie, lati e particelle dedicate 16×16, completamente opache. **Aspetto da verificare in gioco**; [anteprima e test](TEXTURES.md).
- [ ] **TODO GRAFICA — Ridisegnare la texture item `raw_pizza.png`:** sostituire/rifinire la pixel art attuale, che l'autore considera provvisoria, mantenendo il formato **16×16 PNG RGBA**, con **alpha=255 sui pixel visibili**, **alpha=0 soltanto fuori dalla sagoma** e nessun pixel semitrasparente (1–254). Preservare il riconoscimento dell'item, il modello JSON e i componenti/dati della pizza. Definire l'aspetto definitivo insieme all'autore prima di sostituire l'asset.
- [ ] **TODO GRAFICA — Sostituire la resa dei topping "item frame":** i modelli degli ItemStack oggi appoggiati sui quattro quarti sono **soltanto placeholder**; realizzare una rappresentazione credibile dei condimenti sulla superficie della pizza (salse piatte, formaggi/erbe/fette proporzionati, distribuzione naturale e leggibile). Conservare identità dell'ingrediente, maschere dei quarti, rotazione/orientamento, aggiornamenti multiplayer, ordine/rimborsi e prestazioni. Le future texture originali devono seguire la regola 16×16 RGBA/alpha definita sopra.
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

- [ ] Test con ingredienti ufficiali di **Italian's Delight e Farmer's Delight**, sostituti di altre mod riconosciuti tramite tag `c:` precisi, e casi **negativi** per nuovi tipi di topping provenienti da mod terze, tag troppo ampi e mancata presenza della mod opzionale.
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

**Prossimo passo:** implementare le altre meccaniche previste (inclusi i topping con recipiente, senza duplicazioni), mantenendo in roadmap **due attività grafiche da completare prima della release 1.0.0**: nuova texture item `raw_pizza` e sostituzione del renderer placeholder "item frame" con topping realistici sulla pizza. Le funzionalità attuali sono state confermate funzionanti dall'autore.
