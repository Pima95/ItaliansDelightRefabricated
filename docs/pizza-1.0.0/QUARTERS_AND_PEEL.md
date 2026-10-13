# Pizza 1.0.0 — Condimento per quarti e pala per pizza

> **Stato (11 ottobre 2026): specifica di design, non implementazione completata.**
> Questo documento registra le decisioni concordate. **CONFERMATO** indica un comportamento deciso; **DA DEFINIRE** indica una scelta ancora aperta. Le indicazioni tecniche sono requisiti o proposte, non attestazioni di codice gia' presente.
>
> **Principio generale CONFERMATO:** per le interazioni di base si segue sempre il **comportamento vanilla di Minecraft** (caduta, raccolta, despawn, distruzione tramite danni ambientali, ecc.) salvo diversa regola personalizzata gia' approvata. Non richiedere nuove decisioni per ogni caso standard; discutere soltanto eccezioni e meccaniche che cambiano il gameplay.

## Obiettivo e contesto

La pizza e' preparata direttamente nel mondo, senza GUI obbligatoria per condirla. Il giocatore aggiunge ingredienti con clic destro sulla base gia' stesa e puo' creare gusti liberi. Il rendering previsto resta 2.5D: salse piatte e condimenti leggermente rialzati.

Il prototipo attuale della mozzarella e' **provvisorio**: usa il booleano `mozzarella` del BlockState, consuma un item e visualizza cinque pezzi. **Non** implementa ancora i quattro quarti, le applicazioni multiple, la rimozione parziale o i dati sulla pala.

## 1. Divisione facoltativa con il mattarello — CONFERMATO

**Correzione importante:** la pizza NON nasce obbligatoriamente divisa in quattro parti. La suddivisione viene scelta dal giocatore **solo dopo aver steso completamente l'impasto**.

- **Pizza intera (1 zona):** nessuna ulteriore interazione col mattarello; un ingrediente applicato copre l'intera pizza.
- **Due meta' (2 zone):** con un clic destro del **mattarello sulla base gia' completamente stesa** la pizza si divide logicamente in due meta'. **L'orientamento della prima linea dipende dalla direzione verso cui guarda il giocatore quando esegue il clic**, non da un asse fisso del mondo. Ogni meta' puo' essere condita in modo autonomo.
- **Fino a quattro quarti (3 o 4 zone):** usando nuovamente il mattarello **sulla meta' mirata**, solo quella meta' viene divisa in due quarti; anche l'altra meta' puo' essere divisa successivamente. E' ammessa una pizza con una meta' ancora intera e l'altra divisa in due quarti.
- **Applicazione:** con clic destro tenendo un ingrediente valido, il condimento viene distribuito **sulla zona logica puntata** (pizza intera, una meta' o un quarto, in base alle divisioni esistenti). Non e' necessario cliccare sulle linee, ne' premere Shift per scegliere una superficie intera.
- **Divisioni solo come overlay temporaneo:** le linee di suddivisione si vedono soltanto quando il giocatore tiene **il mattarello o un ingrediente valido**. La suddivisione e' virtuale: **non cambiare le texture o i modelli fisici della base**.
- Le suddivisioni col mattarello servono **solo a condire**; non sono un taglio fisico. **DECISIONE AGGIORNATA:** la pizza cotta intera come item non e' commestibile, ma puo' essere piazzata nel mondo e servita direttamente in quattro porzioni, **senza coltello, Cutting Board o ciotola**. Clic destro **a mano vuota** sul quarto della pizza cotta ancora presente produce una fetta con i topping di quel quarto (se c'e' spazio nell'inventario); la mano occupata non prende nulla. Il primo prelievo lascia un settore mancante e gli altri rimangono disponibili. Un giocatore non puo' raccogliere la **pizza-item intera** tramite clic destro a mano vuota: per quella occorre rompere una pizza ancora integra. **Inventario pieno:** nessuna consegna, nessun drop e pizza invariata. La precedente decisione su coltelli compatibili e taglio preliminare e' superata.
- **Orientamento della prima divisione — CONFERMATO:** usare la direzione del giocatore al momento del primo clic con mattarello sulla base stesa. La configurazione spaziale risultante resta associata alla pizza, anche dopo che il giocatore cambia direzione, e va conservata attraverso salvataggio/ricarica e trasporto con pala: i topping non devono ruotare seguendo la visuale del giocatore.
- **Linea parallela allo sguardo — CONFERMATO:** osservando la pizza dalla posizione del giocatore, la prima linea centrale corre nella direzione in cui guarda (proiezione orizzontale), producendo una **meta' a sinistra** e una **a destra**. **Non** separa la pizza in una meta' vicina e una lontana. Le divisioni successive di ciascuna meta' rispettano l'orientamento iniziale.
- **Orientamento a scatti — CONFERMATO:** leggere la direzione orizzontale del giocatore al momento della prima divisione, scegliere la **direzione cardinale piu' vicina** (Nord, Sud, Est o Ovest) e tracciare la linea centrale **parallela a quella direzione cardinale**, non ruotata liberamente in base a un angolo diagonale. La divisione resta in meta' sinistra/destra rispetto alla direzione selezionata. Memorizzare il verso cardinale scelto con la pizza, in modo che zone, topping e interazioni siano coerenti dopo movimenti, riunioni, salvataggio e trasporto con pala.
- **Spareggi APPROVATI (13 ottobre 2026):** se lo sguardo e' equidistante da due cardinali, scegliere quello in **senso orario** (N/E -> E, E/S -> S, S/O -> O, O/N -> N). Sui confini prevalgono **destra** per le meta', **davanti** per i quarti e **davanti-destra** al centro. La selezione usa l'orientamento della pizza salvato, non la visuale corrente. Dettagli sotto.

### Selezione della zona e clic sui confini — CONFERMATO (11 ottobre 2026)

- Usare la **posizione effettiva del punto di interazione** sulla superficie della pizza (coordinate locali ricavate dal punto del clic nel mondo) per identificare una zona **della suddivisione effettivamente presente**: pizza intera, una delle due meta', un quarto oppure una meta' accanto a due quarti.
- Le coordinate locali devono rispettare l'**orientamento cardinale memorizzato** nella prima divisione; non dipendono dal verso in cui il giocatore guarda ai clic successivi.
- **Un clic seleziona esattamente una zona logica.** Non deve selezionare due meta' o due quarti solo perche' il punto coincide con la linea di separazione. La zona selezionata puo' comunque comprendere piu' quarti *fisici* se il giocatore non ha suddiviso la pizza, come gia' previsto dal gameplay.
- Per un punto **esattamente su una linea di confine** (inclusa un'eventuale intersezione centrale di quattro quarti), applicare una **priorita' stabile e deterministica**: scegliere sempre una sola zona secondo un criterio fisso, uguale lato server e client, mai casuale o legato all'ordine temporaneo degli oggetti grafici.
- Lo stesso metodo di selezione viene usato per **aggiunta ingredienti**, **rimozione con Shift + clic destro a mano vuota**, **divisione di una meta'** e **riunione col mattarello**. Non cambia i gesti confermati, la logica di rimborso, il limite dei sei ingredienti o i divieti sulle salse.
- Le linee dell'overlay rimangono **soltanto visive** quando si tiene mattarello/ingrediente valido: non diventano elementi con hitbox proprie e non richiedono un clic esatto su di esse.

**CONFERMATO (13 ottobre 2026):** prevalgono destra/davanti e davanti-destra al centro, in coordinate locali coerenti con l'orientamento salvato. Nessuna hitbox separata per le linee; tolleranza numerica iniziale `1/1024` di blocco, da testare in gioco e in multiplayer.

### Spareggi ai confini e diagonali — CONFERMATO (13 ottobre 2026)

**Criterio APPROVATO:** selezionare una sola zona, anche su confini e diagonali perfette. La specifica seguente è il riferimento per implementazione e test client/server.

1. **Orientamento iniziale della divisione:** arrotondare la visuale orizzontale al cardinale più vicino. Quando la visuale cade **esattamente** a metà fra due cardinali (45°, 135°, 225°, 315°), scegliere quello **in senso orario**: Nord/Est -> Est, Est/Sud -> Sud, Sud/Ovest -> Ovest, Ovest/Nord -> Nord. Applicare il criterio una sola volta alla prima divisione e salvarne l'orientamento con la pizza.
2. **Coordinate del clic:** usare il punto d'impatto effettivo nel sistema di coordinate locale della pizza, trasformato secondo l'orientamento salvato. Definire `u` positivo **verso destra** rispetto al primo orientamento del giocatore e `v` positivo **in avanti**. Le due linee centrali sono `u=0` (sinistra/destra) e `v=0` (davanti/dietro).
3. **Priorità sui confini:** un clic con `u=0` sceglie la **destra**, un clic con `v=0` sceglie la **parte davanti**. Nel centro perfetto (`u=0, v=0`) vince quindi **davanti-destra**. Non selezionare mai più di una zona. Applicare la seconda linea soltanto se la meta' puntata è già divisa in quarti; altrimenti il clic individua tutta quella metà. La pizza non divisa resta una zona unica per aggiunta e rimozione, come già approvato.
4. **Precisione e multiplayer:** nessuna hitbox separata per le linee; server autorevole per le mutazioni, stesso algoritmo condiviso col client per l'anteprima. Tolleranza numerica iniziale approvata: **`1/1024` di blocco**. Per `abs(u) <= epsilon` applicare priorita' a destra; per `abs(v) <= epsilon` applicare priorita' a davanti; fuori dalla tolleranza scegliere secondo il segno della coordinata. **Valore iniziale da verificare con test** su linee, orientamenti cardinali, divisioni miste, salvataggio e multiplayer; non creare fasce di selezione visibili.

**Esempio:** pizza già divisa in quattro; il clic esattamente al centro seleziona sempre il quarto **davanti a destra dell'orientamento salvato**, indipendentemente da dove sia arrivato il giocatore. Se la meta' destra non è suddivisa, lo stesso clic seleziona tutta la meta' destra, non un quarto immaginario. Niente consumo di item o usura aggiuntiva per tentativi senza effetto.

### Riunione progressiva delle zone con il mattarello — CONFERMATO (11 ottobre 2026)

- **Gesto:** Shift + clic destro **tenendo in mano il mattarello** sulla zona da riunire.
- Se si punta un **quarto**, esso viene riunito al quarto adiacente **della stessa meta'**, ritornando a una singola meta' logica. Le due meta' della pizza non vengono riunite finche' una delle due e' ancora suddivisa.
- Quando **entrambe le meta' sono intere** (nessun quarto ulteriormente diviso), Shift + clic destro col mattarello permette di riunirle in **una sola zona: la pizza intera**.
- Se la zona selezionata **non puo' essere riunita allo stato attuale**, l'operazione non deve eliminare ingredienti o alterare la composizione. Se la pizza e' gia' intera, il gesto non produce una ulteriore riunione.
- **Gli ingredienti rimangono esattamente nelle zone fisiche in cui erano stati applicati:** riunire zone logiche **non redistribuisce**, non elimina e non duplica i topping. Non c'e' **alcun rimborso** di ingredienti dovuto alla sola riunione.
- Ogni applicazione conserva identita', quantita' consumata, maschere dei quarti originari e ancora coperti e ordine cronologico. Le successive operazioni di rimozione rispettano la zona **attualmente selezionabile**, senza perdere lo storico dei singoli ingredienti.
- **I nuovi ingredienti** aggiunti *dopo* una riunione si distribuiscono sulla zona logica risultante (per esempio, sulla pizza intera), mentre quelli precedenti restano nella posizione originaria.
- Le divisioni restano **virtuali**: l'overlay appare soltanto tenendo il mattarello o un ingrediente valido; la riunione non modifica fisicamente modelli o texture dell'impasto.

**Esempio:** si mette mozzarella su un solo quarto, poi con Shift + clic col mattarello si riuniscono prima i quarti della stessa meta', poi entrambe le meta'. La mozzarella rimane visibile **soltanto nel quarto originario**, anche se la pizza ora e' logicamente intera; un condimento aggiunto dopo si distribuisce sull'intera pizza.

**Durabilita' — CONFERMATO:** dividere o riunire le zone di una pizza **non consuma alcun punto di durabilita' del mattarello**. Rimane invariato il consumo di **1 punto soltanto quando si completa effettivamente la stesura** di un impasto (attualmente 64 punti totali); nessun consumo per tentativi senza effetto e in Creativa. **DA DEFINIRE:** dettagli di hitbox e precedenza delle interazioni ai confini.

**CONFERMATO (13 ottobre 2026):** spareggio orario tra cardinali equidistanti, destra sui confini delle meta', davanti sui quarti e davanti-destra al centro. Tolleranza iniziale `1/1024` di blocco da validare. Restano confermate la selezione di una sola zona dal clic reale, la linea parallela al cardinale piu' vicino e l'orientamento salvato con la pizza, riunione progressiva, un solo item per applicazione, max sei topping per quarto e durabilita' del mattarello pari a 0 per divisione/riunione (1 per stesura).

**Nota tecnica:** una griglia interna di quattro quarti puo' essere usata per rappresentare le coperture anche quando la pizza e' intera o divisa solo in due, ma il **livello di divisione effettivo** va salvato separatamente. NON mostrare o attivare automaticamente tutti e quattro i quarti.

## 2. Un ingrediente una sola volta per area — CONFERMATO

La stessa tipologia di ingrediente **non puo' essere aggiunta due volte sulla stessa porzione fisica di pizza** (regola valida anche dopo successive divisioni e riunioni). Questo non impedisce di usare quel medesimo ingrediente su **zone differenti non gia' coperte**.

- Un clic destro con un ingrediente valido prova a condire la **zona selezionata** (intera, meta' o quarto) secondo le divisioni create dal mattarello.
- Per l'ingrediente selezionato, il sistema determina quali sottoaree sono **gia' occupate** e quali sono **ancora prive** di quell'ingrediente.
- L'applicazione copre **soltanto la parte ancora priva** dell'ingrediente; le parti gia' condite non ricevono uno strato o una porzione supplementare.
- **Una sola unita' di ingrediente** viene consumata per un'applicazione effettiva riuscita, anche quando la nuova copertura comprende piu' quarti logici liberi. Se nessuna sottoarea della zona cliccata e' disponibile, il clic **non consuma nulla e non modifica la pizza**.
- Applicazioni dello stesso ingrediente in zone differenti restano **identificabili separatamente** per ordine cronologico e rimborso; e' vietato usare questa separazione per sovrapporre lo stesso ingrediente.
- La riunione col mattarello **non modifica** la copertura esistente. Un clic successivo con lo stesso ingrediente su una zona riunita applica l'ingrediente **solo alla parte ancora libera**, preservando le applicazioni precedenti.

**Esempio A:** pizza divisa in due meta'. Si applica mozzarella prima a sinistra e poi a destra: si consumano due mozzarelle, una per ogni applicazione non sovrapposta. Un'ulteriore applicazione a sinistra e' respinta senza consumi.

**Esempio B:** si applica mozzarella solo a sinistra, poi si riuniscono le due meta'. Un clic con mozzarella sulla pizza intera **consuma una mozzarella solo per coprire la meta' destra**. Un altro clic, dopo che entrambe le meta' sono coperte, non produce alcun effetto.

**Esempio C:** mozzarella applicata sull'intera pizza prima di dividerla: divide la pizza in quarti ma **non diventa applicabile altra mozzarella su nessuno dei quarti** finche' quella copertura non e' stata rimossa.

**Equivalenze confermate:** due item, anche provenienti da mod diverse, riconosciuti come lo stesso ingrediente condividono il vincolo anti-duplicato e valgono come **un solo tipo** nel limite di 6 ingredienti diversi per quarto fisico. Il limite e' gia' confermato. **Salse gia' confermate:** facoltative, al massimo una per sottoarea fisica, anche se di tipo diverso. Un clic con una salsa *diversa* su una zona che contiene anche solo parzialmente una salsa preesistente viene **rifiutato interamente**, senza consumare l'item.

### Distinzione dopo una riunione

Se nella zona riunita convivono piu' **applicazioni distinte dello stesso ingrediente**, il comando gia' confermato di rimozione (Shift + clic destro a mano vuota) agisce **prima sull'applicazione piu' recente**, e soltanto sulla copertura residua che le appartiene all'interno della zona selezionata. Le applicazioni precedenti restano al loro posto fino alle successive rimozioni. Non rimborsare due volte, ne' trattare una riunione come una nuova applicazione.

## Denominazione degli spicchi cotti — CONFERMATO (13 ottobre 2026)

**Il nome di ogni spicchio (item fetta cotta) si calcola INDIPENDENTEMENTE dal nome della pizza intera e dagli altri tre quarti.** Quando si preleva uno spicchio dalla pizza cotta con clic destro a mano vuota, oppure uno spicchio residuo viene ottenuto rompendo la pizza cotta già servita parzialmente, leggere **soltanto i condimenti del suo quarto fisico** e confrontarne l'insieme di **tipi canonici esatti** con ciascuna delle **19 ricette a nome fisso già approvate** in [NAMED_PIZZAS.md](NAMED_PIZZAS.md).

- **Match esatto di un singolo quarto con una delle 19 ricette SÌ:** l'item della fetta prende un nome riconosciuto, per esempio **`Spicchio di Margherita`** quando il suo quarto contiene esattamente `farmersdelight:tomato_sauce`, `italiansdelight:mozzarella_slice` e basilico. **Non occorre che la pizza intera fosse Margherita**, né che gli altri tre quarti fossero identici.
- **Nessun match (ricetta NO, extra o mancanza di un ingrediente, composizione libera):** il singolo spicchio usa un **nome dinamico basato sui soli suoi condimenti**, non un nome ereditato dalla pizza intera o da altri quarti. La grammatica `Spicchio con [ingredienti]` è confermata; rimane solo da definire la politica tecnica di abbreviazione.
- **Pizza mista:** il nome della **pizza intera** è **Pizza assortita**, in applicazione della regola 4/4 già confermata; **ogni spicchio** può comunque ricevere **il proprio nome fisso** se soddisfa da solo una ricetta approvata, oppure un nome dinamico se non la soddisfa. Quattro spicchi possono perciò avere quattro nomi distinti.
- **Identità canoniche:** l'ordine di applicazione è irrilevante; le versioni crudo/cotto e gli item equivalenti di altre mod, quando esplicitamente mappati, valgono come lo stesso tipo. Non usare la mera categoria nutrizionale per riconoscere un ingrediente e non ammettere item fuori catalogo.
- **Fetta di un quarto vuoto:** corrisponde alla ricetta SÌ della pizza Bianca e avrà un nome coerente con `Spicchio di pizza bianca` (testo/localizzazione da declinare senza rigidità). Il quarto con **sola salsa di pomodoro** corrisponde alla Rossa.
- **Compatibilità col servizio già approvato:** la fetta conserva unicamente i suoi topping, nutrienti, dati e stato, senza nuovi item registrati per ogni gusto; il nome è un **display name localizzato e derivato dai dati dello spicchio**. La pizza intera cotta **non è commestibile**. Le porzioni continuano a essere 4 e vengono prelevate dalla pizza cotta senza coltello. Inventario pieno o mano occupata non producono una fetta e non cambiano il blocco. I rimborsi/drops e la nutrizione restano invariati.
- **Limite del nuovo requisito:** il riconoscimento dei nomi **delle fette cotte è ora deciso**. Rimangono aperte le convenzioni di abbreviazione dei nomi dinamici molto lunghi e gli eventuali dettagli del nome del blocco pizza **rimasto nel mondo dopo il servizio parziale**, senza bloccare il naming del singolo item.

**Esempio:** pizza intera con quarto 1 Margherita, quarto 2 Principessa, quarto 3 Rossa, quarto 4 una combinazione personalizzata. La pizza intera si chiama **Pizza assortita**. Dopo la cottura, le fette recuperate dai rispettivi quarti prendono **Spicchio di Margherita**, **Spicchio di Principessa**, **Spicchio di pizza rossa**, **Spicchio con ...** (per la quarta composizione libera). I nomi fissi si applicano **anche se i quarti adiacenti hanno altri condimenti**.

## Naming della pizza intera e tooltip — CONFERMATO (13 ottobre 2026)

Il nome della pizza completa si ricava dalla composizione dei **quattro quarti fisici**, indipendentemente dalla presenza delle linee del mattarello. Se i quattro insiemi canonici sono uguali e corrispondono a uno dei 19 preset, vale il **nome fisso**; se sono uguali ma non registrati, usare **`Pizza con [ingredienti]`** con eventuale abbreviazione nell'inventario e tooltip integrale; se almeno un quarto è differente, usare **`Pizza assortita`** e tooltip che elenca **Quarto 1–4** con il nome del gusto riconosciuto per quel quarto o con i suoi ingredienti. Gli **spicchi cotti** continuano a essere riconosciuti in autonomia sulla composizione del solo quarto d'origine. Le stringhe tradotte non vanno persistite. Specifica unica in [NAMED_PIZZAS.md](NAMED_PIZZAS.md#nomi-e-tooltip-delle-pizze--confermato-13-ottobre-2026). Resta da definire l'indicizzazione stabile dei quarti per il tooltip; lo spareggio dei clic sui confini è già **APPROVATO** nella sezione tecnica precedente.

## Nomi fissi e quarti fisici — CONFERMATO (13 ottobre 2026)

**MATCHING ESATTO SU QUATTRO QUARTI FISICI — CONFERMATO (13 ottobre 2026):** il nome fisso di una delle **19 ricette SÌ** si applica alla pizza integra **se e solo se ciascuno dei quattro quarti fisici contiene esattamente tutti e soli i tipi canonici di topping previsti dalla stessa ricetta**. Si controllano **quattro insiemi distinti, non la sola unione globale**. Basta **un ingrediente aggiuntivo o mancante in un solo quarto** perché l'intera pizza abbia **nome dinamico**, anche se gli altri tre quarti corrispondono. Il confronto vale anche senza divisioni visibili o dopo riunioni col mattarello; l'ordine di aggiunta è irrilevante, così come lo stato crudo/cotto per gli ingredienti equivalenti e le sostituzioni cross-mod esplicitamente mappate. **Bianca:** tutti e quattro i quarti senza condimenti. **Rossa:** sola salsa di pomodoro nei quattro quarti. **Margherita:** pomodoro, mozzarella a fette e basilico esatti in ciascun quarto. Un quarto con speck extra o senza basilico -> nome dinamico. Le pizze miste a quarti diversi **non** ricevono nomi fissi nella 1.0.0: eventuali Quattro Gusti / Quattro Stagioni riconosciute restano future proposte, **non approvate**. La regola disciplina la denominazione della **pizza integra**; il riconoscimento indipendente delle singole fette cotte è confermato nella sezione precedente.

## Ingredienti equivalenti tra mod — CONFERMATO (11 ottobre 2026)

**Whitelist 1.0.0 APPROVATA (13 ottobre 2026):** [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md) definisce 29 tipi ufficiali gia' presenti (33 ID registrati per effetto delle quattro coppie crudo/cotto), piu' carota e patata a fette da creare. **Ogni altro ingrediente e' escluso**, anche se appare nei tag `c:` generici: NO espliciti a spicchi di Parmigiano/Pecorino, mascarpone e sale; uova soltanto `farmersdelight:fried_egg`, **non `minecraft:egg`**; `farmersdelight:minced_beef` e' approvato, le bistecche intere restano vietate. Carote e patate intere non si applicano: servono le varianti a fette con nuovi item/ricette Cutting Board. La whitelist verra' implementata successivamente: questa e' una regola documentata, non codice attivo.



- Due item provenienti da mod differenti **riconosciuti esplicitamente come equivalenti** sono considerati **lo stesso ingrediente** ai fini della pizza. Esempio: due mozzarelle equivalenti, una di Italian's Delight e una di una mod compatibile.
- **Antiduplicazione:** se nel quarto fisico e' gia' presente un item equivalente, non si puo' aggiungerne una seconda versione come ingrediente distinto; su una zona che ne e' solo parzialmente coperta l'applicazione interessa soltanto le sottoaree ancora libere da *tutto il gruppo equivalente*.
- **Limite di 6:** tutti gli item appartenenti al medesimo gruppo di equivalenza contano come **un solo tipo di ingrediente** per quarto fisico. Non si puo' aggirare il limite usando mod diverse.
- **Salse:** varianti riconosciute come equivalenti rappresentano la medesima salsa, ma resta valido anche il vincolo generale di **una sola salsa di qualsiasi tipo per quarto** e il rifiuto integrale se si prova una salsa *differente* su una zona gia' parzialmente occupata.
- **Provenienza conservata:** l'applicazione registra sempre l'**ItemStack realmente consumato**, l'origine, l'ordine e la copertura. Quando e solo quando si completa la rimozione di quell'applicazione, il giocatore recupera **l'item originale**, non un suo equivalente scelto arbitrariamente.
- **Non confondere categorie ed equivalenza:** appartenere entrambi al gruppo generale dei formaggi **non** rende automaticamente equivalenti mozzarella e gorgonzola. I gruppi di equivalenza devono essere abbastanza specifici da non confondere ingredienti diversi.
- **Compatibilità configurabile — DECISA (13 ottobre 2026):** le mod esterne forniscono soltanto sostituti dei 31 tipi previsti, **identificati con singoli ID verificati e dichiarati esplicitamente**. I tag `c:` sono fonti per la ricerca di candidati, mai whitelist automatica; nessun item intero, non porzionato o nuovo tipo entra attraverso un tag.

**Preparazione del condimento — CONFERMATO (12 ottobre 2026):** oltre a essere riconosciuto come tipo ufficiale o sua variante cross-mod, **l'item concreto deve essere gia' lavorato/porzionato per una pizza**. Non ammettere forme intere di prosciutto/salumi, ruote di formaggio, bistecche intere (anche cotte), verdure troppo grandi o fasi grezze/intermedie tramite tag `c:` generici; accettare soltanto forme in fette, grattugiate, piccoli pezzi, erbe e salse/creme pronte. Un tag di categoria nutrizionale non prova la prontezza. **Carne e pesce gia' tagliati in porzioni idonee possono essere applicati sia crudi sia cotti**, se il tipo di ingrediente e' supportato; **stessa fame (+2) e saturazione (+2,5) come proteine animali, nessun effetto speciale o penalita' da crudo**. La bistecca intera resta vietata in entrambi gli stati; salvare esattamente l'ItemStack originale senza presumere conversioni automatiche durante la cottura della pizza. L'eventuale equivalenza canonica crudo/cotto ai fini dei doppioni e' distinta dalla regola nutrizionale. Se occorre lavorazione tramite Cutting Board, deve esistere una vera ricetta e un output idoneo; nessuna trasformazione implicita al clic. Ingredienti pronti non necessitano della Cutting Board. Non cambiare le meccaniche confermate per applicazioni e rimborsi; un item non pronto fallisce senza consumo. Vedi [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md).

**Esempio:** se un quarto contiene gia' mozzarella di Italian's Delight, un'altra mozzarella esplicitamente riconosciuta come equivalente non puo' essere aggiunta nella stessa posizione e non consuma nulla. Su un quarto libero, quella seconda mozzarella e' valida e, quando rimossa completamente, restituisce esattamente l'item di origine.

**Ambito release CONFERMATO:** solo Italian's Delight e Farmer's Delight Refabricated definiscono i **tipi ufficiali** di topping; item di mod esterne possono sostituire gli stessi ingredienti solo dopo **mapping esplicito e verificato del singolo ID** a un'identita' canonica esistente, ma non si ammettono nuovi tipi dalla sola presenza di una mod o di un tag alimentare generale. **Classificazione estesa CONFERMATA:** uova condividono **la categoria nutrizionale** delle proteine animali con salumi/carne/pesce; olive quella dei vegetali; panna quella dei latticini con formaggi; olio d'oliva appartiene alla nuova categoria Oli e grassi. **Categoria non implica equivalenza:** uovo e prosciutto, panna e mozzarella, olive e funghi restano ingredienti distinti per il limite dei sei e i rimborsi, salvo equivalenza puntuale. Il ruolo e' indipendente: **la panna NON conta come salsa**, e' un condimento cremoso **utilizzabile insieme alla salsa di pomodoro nello stesso quarto** senza sostituirla ne' occupare lo slot salsa; resta latticino per nutrizione, un tipo ai fini del massimo sei, senza bonus salsa extra. **La ricotta ha anch'essa ruolo di condimento cremoso NON salsa**, applicabile sopra il pomodoro; panna e ricotta sono due topping distinti per il limite sei, ma condividono l'unica categoria Latticini (+1 fame/+1,5 saturazione, una volta per categoria), senza bonus Salsa.  l'olio non occupa lo slot salsa, ma non e' un topping attivo nella whitelist 1.0.0. Mappare anche Farmer's Delight Refabricated e mod compatibili tramite ID/tag verificati, preservando l'item di origine.

**Equivalenza crudo/cotto — CONFERMATO (12 ottobre 2026):** le varianti gia' affettate/tagliate **crude e cotte dello stesso ingrediente** hanno **un'identita' canonica condivisa** per la regola anti-doppione e il limite massimo di sei tipi per quarto. Esempio: bacon e cooked_bacon non possono essere applicati entrambi nello stesso quarto; uguale per le coppie chicken_cuts/cooked_chicken_cuts, cod_slice/cooked_cod_slice, salmon_slice/cooked_salmon_slice, quando abilitate nel catalogo. **Non significa equivalenza fra proteine animali differenti.** La selezione di zona gia' definita sottrae automaticamente i quarti occupati da qualsiasi versione del topping: clic solo duplicato -> nessun consumo; clic su zona parzialmente libera -> una applicazione con un solo item nei quarti liberi. Il rimborso restituisce **il vero ItemStack crudo/cotto** dell'applicazione originale. Niente trasformazioni automatiche durante la cottura, ne' bonus differenti. Mapping esterni per ID puntuale ancora da selezionare e schema JSON da definire, **regola funzionale gia' approvata**.

**Criterio prestazionale (13 ottobre 2026):** cache diretta per item, ricostruita all'apertura mondo/`/reload` con soli **ID mappati esplicitamente**, senza espandere tag durante il gameplay e senza controlli per tick. Antiduplicato basato su identità canonica e maschere di quattro quarti; tempi e memoria da confrontare con una tabella Java diretta. [Dettagli](DATA_AND_COMPATIBILITY.md).

**DECISO (13 ottobre 2026):** solo mapping per singolo ID di item esterno verificato, senza abilitazione automatica dei membri dei tag; **un file JSON per equivalenza, esattamente campi `item` e `canonical`**, mapping nativi non sovrascrivibili. ID esterno ambiguo fra due identità diverse rifiutato con diagnostica senza disattivare gli altri mapping. **DA REALIZZARE:** reload listener, cache e test; percorso tecnico dei file da finalizzare. Nessuna equivalenza esterna ancora implementata.

## Salsa facoltativa, al massimo una per porzione — CONFERMATO (11 ottobre 2026)

- **Nessuna salsa e' obbligatoria**: una pizza puo' essere preparata interamente bianca oppure con salsa solo su alcune zone.
- Ogni parte fisica della pizza puo' contenere **al massimo una salsa**: non e' possibile sovrapporre pomodoro e pesto (o altre salse) sulla stessa parte.
- Le zone differenti possono avere **salse diverse** (esempio: una meta' al pomodoro, una meta' al pesto) oppure restare senza salsa.
- La stessa salsa non si applica due volte sulla medesima parte, in accordo con il vincolo generale sugli ingredienti.
- Le divisioni e riunioni col mattarello **non cancellano, sostituiscono o redistribuiscono** le salse gia' applicate. L'obbligo di una sola salsa e' verificato sulla copertura fisica, non soltanto sulle zone logiche correnti.
- I condimenti diversi dalle salse (formaggi, salumi, verdure, erbe) possono essere applicati anche **senza salsa**; non esiste un ordine obbligatorio di preparazione.
- La salsa puo' essere un ingrediente necessario **per riconoscere un gusto specifico** (per esempio Margherita), senza diventare un requisito per tutte le pizze, specialmente quelle personalizzate o bianche.

**Applicazione di una salsa differente su zona parzialmente occupata — CONFERMATO:** se una parte qualsiasi della zona selezionata contiene gia' una salsa differente, il tentativo deve essere **rifiutato per intero**. Non applicare la nuova salsa nemmeno alle parti libere della zona, non sovrascrivere la salsa esistente, **non consumare alcun item** e non modificare la pizza. Il controllo va fatto sull'intera zona mirata, anche quando questa deriva da una riunione col mattarello.

**Esempio:** la meta' sinistra contiene pomodoro, la destra e' senza salsa; dopo aver riunito la pizza, tentare di applicare il pesto sull'intera superficie **non fa nulla**. Per mettere pesto nella meta' destra occorre prima **dividerla nuovamente col mattarello** e fare clic soltanto sulla parte senza salsa.

**Distinzione:** questa regola riguarda il tentativo di aggiungere una **salsa differente da una gia' presente nell'area mirata**. Restano valide, senza modifiche, le regole gia' confermate contro la doppia applicazione dello **stesso ingrediente**, e quelle di copertura delle sole parti libere per i normali topping.

**Catalogo APPROVATO; panna DECISA (13 ottobre 2026):** `farmersdelight:tomato_sauce` e' la salsa di pomodoro ammessa. **`italiansdelight:cream_bowl` e' un condimento cremoso NON salsa**, utilizzabile **sopra la salsa di pomodoro nel medesimo quarto**: il pomodoro conserva l'unico ruolo salsa, la panna conta come topping distinto per il limite di sei ma nutrizionalmente solo come latticino (un bonus latticini per categoria, nessun bonus salsa dalla panna). `italiansdelight:ricotta` e' un topping ammesso con **lo stesso ruolo cremoso NON salsa della panna**: anche la ricotta si applica sopra il pomodoro senza occupare lo slot salsa. **Panna e ricotta sono due ingredienti distinti**, contano ciascuno nel limite sei ma apportano un **solo bonus Latticini per categoria**, senza bonus Salsa ulteriore. Recipienti/rimborsi da gestire una sola volta senza dupe; nessun altro item fuori [catalogo](INGREDIENT_CATALOG.md) e' autorizzato automaticamente.

**Stato:** decisioni documentate, **non ancora implementate** nel prototipo attuale.

## Limite di 6 ingredienti diversi per quarto fisico — CONFERMATO (11 ottobre 2026)

- Il limite e' **6 ingredienti diversi per ciascuno dei quattro quarti fisici/logici sottostanti** alla pizza, **non** 6 per l'intera pizza e **non** 6 per ciascuna zona selezionabile con il mattarello.
- Se e' presente una salsa, **conta come uno dei sei**; senza salsa e' possibile usare sei ingredienti di altre categorie. La salsa resta facoltativa e ne puo' esistere **al massimo una per quarto**.
- Contano i **tipi di ingredienti effettivamente presenti su ogni quarto**, non il numero globale di applicazioni, le ricette nominate o il numero di frammenti disegnati dal renderer. Lo stesso ingrediente non puo' comunque essere duplicato sulla stessa area.
- Le divisioni (intera / meta' / quarto) e le successive riunioni col mattarello **non modificano il numero di ingredienti su alcun quarto**, ne' azzerano il limite.
- **Validazione atomica prima del consumo:** determinare prima la nuova copertura effettiva della singola applicazione (al netto delle parti gia' occupate dallo stesso ingrediente); controllare che **ogni quarto in cui verrebbe aggiunto un ingrediente** resti a massimo sei tipi differenti.
- Se anche **un solo quarto** della copertura richiesta arriverebbe a **sette**, **rifiutare integralmente** il clic: non condire nemmeno gli altri quarti disponibili, non decrementare lo stack e non creare una nuova applicazione.
- Se la nuova copertura e' vuota perche' l'ingrediente e' gia' presente ovunque nella zona selezionata, il clic non modifica la pizza e non consuma nulla.
- Rimuovere un condimento libera un posto **solo nei quarti da cui viene realmente rimosso**. Non si rimborsa l'ingrediente finche' l'intera applicazione originale non e' stata rimossa, come gia' concordato.
- Il limite vale anche dopo trasporto sulla pala, salvataggio/ricarica, spostamento al forno e preparazione alternativa via crafting: il mezzo di preparazione non deve aggirarlo.

**Esempio A:** un quarto con salsa di pomodoro + mozzarella + speck + funghi + gorgonzola + basilico ha **6 ingredienti**, ed e' pieno. Un tentativo di aggiungere prosciutto su quel quarto viene respinto, senza consumare nulla.

**Esempio B:** due meta' sono state riunite. Un quarto ha gia' 6 ingredienti e un quarto vicino ne ha 4. Un clic che aggiungerebbe un nuovo ingrediente a entrambi viene **respinto integralmente**: non aggiunge il settimo al primo e non aggiunge il quinto al secondo.

**Esempio C:** una salsa su un quarto e' **1 ingrediente**, non un requisito obbligatorio e non un settimo slot separato.

**Nota implementativa:** i quattro quarti sono la griglia di riferimento per il conteggio anche quando l'overlay di divisione e' nascosto o la pizza e' logicamente intera. La validazione lato server deve essere deterministica e impedire duplicazioni nel multiplayer.

## 2. Applicazione originale e recupero — CONFERMATO

Una singola applicazione che consuma un ingrediente puo' interessare uno o piu' quarti. L'applicazione va considerata una **unita' indivisibile ai fini del rimborso**, anche se la sua rappresentazione viene rimossa un quarto alla volta.

- Ogni applicazione tiene traccia dell'**oggetto realmente consumato**, della **quantita'** consumata e dell'**insieme dei quarti originariamente coperti**.
- Rimuovere il condimento **solo da alcuni** dei quarti coperti non restituisce nessun item.
- Soltanto quando sono stati rimossi **tutti i quarti** di quella specifica applicazione, si restituisce **esattamente** la quantita' consumata da quell'applicazione.
- Una seconda applicazione dello stesso ingrediente e' distinta dalla prima: non devono essere unite in modo da perdere la provenienza o moltiplicare i rimborsi.
- Il sistema deve impedire sia i rimborsi multipli sia la perdita degli oggetti in seguito a interazioni ripetute o simultanee.

**Esempio A:** 1 mozzarella applicata su quattro quarti = **una** applicazione e **una** mozzarella consumata. Rimuovere 1, 2 o 3 quarti non restituisce nulla; rimuovere il quarto rimanente restituisce **1 mozzarella**, non 4.

**Esempio B:** 4 mozzarelle applicate singolarmente, una per ogni quarto = **quattro** applicazioni distinte. Ognuna puo' essere restituita separatamente quando viene rimossa interamente.

### Gesto e ordine di rimozione — CONFERMATO (11 ottobre 2026)

- Il giocatore usa **Shift + clic destro con la mano vuota** sulla zona della pizza che vuole ripulire.
- Viene rimossa **una singola applicazione alla volta**, scegliendo tra quelle ancora presenti sulla zona cliccata quella **aggiunta più recentemente**.
- Se sulla zona sono sovrapposti, in ordine, pomodoro, mozzarella e basilico, i tre Shift + clic successivi rimuovono **basilico, mozzarella, pomodoro**, purché ciascuno sia ancora presente in quella zona.
- L'operazione agisce sulla **zona logica corrente** determinata dalle divisioni col mattarello: pizza intera, metà oppure quarto. Quando un'applicazione copre anche altre zone, il clic la elimina solo dalla zona selezionata.
- Se in quella zona non esistono topping da rimuovere, **non avviene nulla**. Non si rimuovono ingredienti di zone adiacenti e non si raccoglie l'impasto come effetto secondario.
- L'**ordine cronologico delle applicazioni** deve essere persistente, distinto dall'ordine dei livelli grafici e conservato raccogliendo la pizza con la pala, ricaricando il mondo e dividendo le zone successivamente.
- Restano valide le regole sui rimborsi: la rimozione parziale **non restituisce nulla**, quella completa rimborsa **una sola volta** la quantità consumata, direttamente nell'inventario o a terra solo se pieno.
- **Shift + clic destro col mattarello riunisce progressivamente le zone** senza alterare i topping; Shift + clic destro **a mano vuota** resta riservato alla rimozione, quindi i due gesti non confliggono.

**Esempio di applicazione globale:** mozzarella aggiunta alla pizza intera, poi base divisa in quattro quarti. Shift + clic vuoto su un quarto toglie solo la porzione di mozzarella visibile lì, senza restituire l'item; il quarto e ultimo recupero della stessa applicazione rende una sola mozzarella.

**Non ancora implementato:** questa è una regola di design approvata, non il comportamento del prototipo attuale con booleano `mozzarella` nel BlockState.

### Destinazione degli oggetti recuperati — CONFERMATO

- La rimozione normale restituisce l'ingrediente **direttamente all'inventario del giocatore**.
- Un item puo' cadere a terra **soltanto se manca spazio nell'inventario** (o in casi di recupero senza inventario destinatario, da disciplinare).
- Nessun drop avviene durante una rimozione parziale che non ha ancora completato l'applicazione originale.

### Rimozione della pizza dal terreno — CONFERMATO (gia' deciso nelle conversazioni precedenti)

- **Per raccogliere una pizza intera ancora integra posizionata nel mondo bisogna rompere il blocco**, ottenendo la pizza completa come item. Il clic destro a mano vuota non raccoglie la pizza completa, ma **sulla pizza COTTA permette di prelevare una singola fetta dal quarto selezionato**, anche prima di qualsiasi taglio o altra interazione. Per impasto e pizza cruda restano valide le altre interazioni di stesura e condimento.
- **Clic destro normale a mano vuota:** su pizza cruda gia' stesa non raccoglie la pizza e non crea alcuna pizza-item; su pizza cotta posizionata, se la mano e' vuota e l'inventario ha spazio, **estrae una sola fetta del quarto mirato**. La raccolta di una fetta cotta e' distinta dalla stesura dell'impasto crudo e dal gesto **Shift + clic destro** per rimuovere topping dalla pizza condibile.
- La **rottura di una pizza cruda completamente formata** restituisce **una pizza cruda come item integro** che conserva condimenti, quantita' effettivamente impiegate, maschere delle zone, orientamento, ordine cronologico delle applicazioni, stato e seed visivo. Non restituire contemporaneamente impasto e ingredienti separati: sarebbero duplicati dei materiali gia' incorporati nell'item pizza.
- La **rottura di una pizza cotta ancora INTEGRA**, dalla quale **non e' stata prelevata nessuna porzione**, restituisce la pizza cotta come item completo, senza ingredienti separati. Se **almeno una fetta e' gia' stata prelevata**, la rottura del blocco (anche per rimozione del supporto) fa cadere **solo una fetta per ogni quarto ancora presente**, con i topping locali. Nessuna fetta gia' prelevata, pizza integra aggiuntiva, impasto o topping separati; nessuna duplicazione.

### Rottura della pizza — CONFERMATO

- Rompendo una **pizza completa ancora integra** nel mondo, cruda o cotta, si recupera **un item pizza completo con tutti i dati**, senza rimborsare ingredienti. La pizza cotta da cui e' **gia' stata prelevata almeno una fetta** non e' piu' integra: alla rottura produce **soltanto le singole fette dei quarti rimasti**. Non esiste una fase preliminare di taglio.
- La rottura non produce **impasto o topping separati**, per evitare il doppio recupero dei materiali gia' incorporati nell'item pizza.
- La **rimozione intenzionale di topping con Shift + clic destro a mano vuota**, prima della rottura, resta distinta: un'applicazione restituisce l'item originale soltanto dopo essere stata rimossa per intero, come gia' concordato.
- **Palla e impasto non ancora completamente steso** mantengono per ora il comportamento di recupero del Wheat Dough del prototipo; il nuovo item riguarda la **pizza gia' formata** e i suoi dati.
- Una **pizza cotta appena piazzata e ancora integra** puo' essere raccolta come item completo rompendola, oppure **servita a porzioni con clic destro a mano vuota**. Dopo il primo prelievo, resta nel mondo con i quarti residui; ulteriori clic vuoti sui quarti rimasti producono le rispettive fette se l'inventario ha spazio. Se viene rotta dopo il primo prelievo, droppa **solo le fette residue**, senza rigenerare quelle gia' raccolte.

**Dettagli implementativi da testare:** rottura effettuata da un giocatore, rimozione del supporto, multiplayer, modalita' Creativa e altri eventi vanilla. La regola per una pizza formata **ancora INTEGRA** e' un solo item pizza con dati completi; dopo che e' stata servita una fetta da una pizza cotta, la rottura rilascia **soltanto le fette residue**. In entrambi i casi nessun drop separato di topping; non introdurre restituzioni multiple o eccezioni non richieste.

## 3. Pala per pizza (Pizza Peel) — CONFERMATO

La **pala per pizza** e' lo strumento per trasferire una pizza **intera**. E' gia' disponibile un asset della vecchia Italian's Delight fornito dall'autore: **riutilizzarlo dopo averlo recuperato e verificato**, senza crearne o sostituirne la texture autonomamente.

- La pala trasporta **direttamente una pizza alla volta**, non la converte in un item pizza separato collocato in un altro slot dell'inventario.
- Con **clic destro** su una pizza posizionata, la pala libera la raccoglie solo se non ne trasporta gia' una.
- La pala occupata **non puo' raccogliere una seconda pizza**.
- Con **clic destro** su una superficie compatibile, la pala occupata deposita la pizza e ricostruisce lo stato precedente.
- L'uso della pala **non e' obbligatorio per applicare o rimuovere** i condimenti: queste interazioni avvengono direttamente sulla pizza nel mondo.
- La pala e' **FACOLTATIVA** per il Pizza Oven, che ha una **GUI confermata** per le pizze-item, il combustibile e il recupero. **CONFERMATO:** con una pizza sulla pala, **clic destro sul forno per inserimento diretto senza GUI**: dopo successo la pala torna vuota e i dati della pizza sono trasferiti integralmente. Se il forno non puo' accettare la pizza, la pala resta occupata e nulla viene sovrascritto. **CONFERMATO:** una **pala vuota** sul Pizza Oven estrae direttamente con clic destro la pizza presente, **anche se non completamente cotta**, senza aprire la GUI. La pala diventa occupata e il forno resta vuoto; ingredienti, orientamento, divisioni, aspetto e progresso di cottura devono restare intatti. Se non c'e' una pizza nel forno non e' possibile estrarre nulla. **Non aggiungere raccolta a mano vuota della pizza dal terreno:** la pizza completa si rimuove **rompendola e raccogliendola come item**, con dati preservati. Questo e' anche il **percorso confermato senza pala** dal mondo al forno. La pala rimane una via alternativa facoltativa per il trasporto diretto. L'integrazione del forno deve essere implementata e verificata quando il forno esistera'.
- **Durabilita' — CONFERMATO:** la pala e' uno **strumento riutilizzabile senza durabilita'**. Raccogliere, posizionare o trasferire una pizza, anche ripetutamente, non consuma la pala ne' ne modifica la capacita' di trasportare **una pizza alla volta**.
- **Non impilabile — CONFERMATO:** la pala ha **dimensione massima dello stack pari a 1**, sia **VUOTA** sia **OCCUPATA**. Ogni pala richiede un proprio slot di inventario; due pale identiche, anche vuote, non si uniscono mai in uno stack. Lo stato vuoto/occupato non cambia questa regola.

### Dati conservati durante il trasporto — CONFERMATO

L'intero stato della preparazione deve viaggiare **dentro i dati persistenti della pala occupata**, incluse almeno le seguenti informazioni:

- stato e progresso della pizza (impasto/base, cruda/condita/cotta secondo il caso), **inclusa la struttura di suddivisione scelta col mattarello**;
- ingredienti, quantita', applicazioni originali e quarti ancora coperti; **nessun ingrediente puo' occupare due volte lo stesso quarto fisico**;
- disposizione visiva, inclusi eventuali semi e parametri di rendering;
- proprieta' necessarie per determinare il gusto e preservare la cottura.

Il trasferimento **mondo -> pala -> mondo/forno -> pala** dev'essere atomico dal punto di vista logico: mai due pizze create dalla stessa raccolta e mai cancellazione della fonte prima che lo stato sia trasferito in sicurezza. Il salvataggio del mondo, il chunk unload e il multiplayer devono mantenere uno stato coerente. **La pala non e' mai impilabile**: le informazioni di contenuto restano associate a singoli ItemStack di dimensione 1, anche quando entrambi gli oggetti sono vuoti.

### Pala lasciata cadere e recuperata — CONFERMATO (12 ottobre 2026)

- Il tasto **Q** (o il normale comando di lancio/drop dell'inventario) lascia cadere la pala, **vuota o occupata**, come un normale oggetto di Minecraft: una sola entita' item che trasporta il relativo `ItemStack`.
- Se la pala contiene una pizza, **la pizza resta nei dati della pala**; non viene piazzata automaticamente nel mondo e non diventa un item separato. La pala rimane **non impilabile**.
- **Raccogliendo l'oggetto lasciato a terra**, lo stesso `ItemStack` ritorna nell'inventario, con la pizza intatta: ingredienti originali e loro applicazioni/coperure, divisioni, orientamento, stato di cottura/preparazione, cronologia e dati visivi invariati.
- Anche altri giocatori che raccolgono la pala devono ottenere il medesimo stato persistente; il passaggio inventario -> entita' item -> inventario non crea copie, non cancella contenuti e non modifica le regole per salse e limite di sei ingredienti per quarto.
- La pala resta riutilizzabile senza durabilita' e **le capacita' future da arma restano disabilitate mentre contiene una pizza**.
- **Despawn naturale — CONFERMATO:** la pala lasciata a terra, anche se contiene una pizza, segue il **normale despawn degli item vanilla** (tempo e condizioni del mondo/server); **non e' immune al despawn** e non ha un timer personalizzato. Quando l'entita' item scompare per despawn naturale, **vengono perse sia la pala sia la pizza contenuta**, senza drop aggiuntivi, recupero automatico o estrazione degli ingredienti.
- **Distruzione ambientale — CONFERMATO (regola vanilla):** lava, fuoco, cactus, esplosioni e altri danni alle entita' item seguono esattamente le normali regole vanilla. **Se** questi eventi distruggono la pala lasciata a terra, si perdono **sia la pala sia la pizza contenuta**, senza item pizza separato, recupero automatico, ingredienti, impasto o drop aggiuntivi. Se l'oggetto non viene distrutto dalle regole vanilla, il contenuto resta intatto. Nessuna immunita', resistenza, eccezione o drop personalizzato.

**Test di accettazione:** pala con pizza personalizzata -> Q -> raccolta dello stesso oggetto -> riposizionamento della pizza: composizione e stato identici; ripetere con due pale separate e in multiplayer, senza perdita o duplicazioni.

### Uso futuro anche come arma — SOLO A PALA VUOTA (CONFERMATO)

L'autore vuole rendere **anche la pala un'arma**, prendendo come riferimento il comportamento della **padella di Farmer's Delight Refabricated**. Questa e' una **funzionalita' da progettare e implementare successivamente**; non e' ancora una meccanica approvata nei dettagli e non risulta implementata dal prototipo pizza.

- La funzione principale gia' confermata rimane **raccogliere, trasportare e depositare una sola pizza alla volta**; l'uso offensivo futuro dovra' coesistere con gli usi di clic destro sul mondo e sul Pizza Oven.
- **Stato VUOTO — CONFERMATO:** la pala potra' attivare le **funzionalita' speciali da arma** soltanto quando non contiene alcuna pizza.
- **Stato OCCUPATO — CONFERMATO:** la pala contenente una pizza **non funziona come arma**: nessun bonus/statistica o effetto offensivo della futura arma mentre trasporta la preparazione. Rimane uno strumento di trasporto e collocazione. La pizza e i suoi dati non devono essere alterati o persi per un tentativo di attacco.
- Nel progetto delle future proprieta' da combattimento, **abilitare/disabilitare le proprieta' speciali da arma in base alla presenza della pizza**; il normale funzionamento degli attacchi base di Minecraft, indipendente da queste proprieta', non e' specificato qui.
- La pala resta **senza durabilita'** come oggetto; non introdurre ora usura o rottura dello strumento per il trasporto.
- **DA DEFINIRE in fase di combattimento:** danni, velocita' d'attacco, eventuali effetti/animazioni/suoni **solo quando la pala e' vuota**; dettagli di interazione con i normali attacchi base quando e' occupata. **Gia' definito:** con una pizza caricata non si attivano mai le capacita' speciali da arma. Non copiare parametri della padella senza prima verificarne la versione effettiva e concordare il bilanciamento.
- Non modificare ne' sostituire l'**asset gia' disponibile** della pala dalla vecchia Italian's Delight: recuperarlo e valutarne il riutilizzo prima di creare ulteriori risorse.

**CONFERMATO:** la pala e' **riutilizzabile senza durabilita'** e in futuro dovra' poter fungere anche da **arma ispirata alla padella di Farmer's Delight Refabricated, ma esclusivamente quando e' vuota**. Se contiene una pizza, le capacita' speciali da arma sono disattivate e i dati della pizza devono rimanere intatti. **CONFERMATO:** stack massimo 1, sia a pala vuota sia occupata. **CONFERMATO:** una pala droppata come item (Q) conserva la pizza internamente e il recupero ripristina tutti i dati; **se l'item despawna naturalmente, pala e pizza vengono perse secondo il comportamento vanilla**. **CONFERMATO:** distruzione e danni ambientali seguono Minecraft vanilla; se l'item viene distrutto, pala e pizza scompaiono senza rimborsi. **CONFERMATO:** inserimento diretto con pala occupata nel Pizza Oven, dotato di GUI; con forno occupato niente trasferimento o perdita di dati. **CONFERMATO:** estrazione rapida dal forno con pala vuota, anche per pizze parzialmente cotte, conservando il progresso e senza GUI. **DA DEFINIRE:** ricetta, tooltip, resa grafica quando occupata, dettagli di resa delle pizze cotte e superfici compatibili. **CONFERMATO:** la pala non e' obbligatoria per utilizzare il Pizza Oven, ma non e' consentita la raccolta a mano vuota di una pizza collocata nel mondo. **CONFERMATO:** rompendo una pizza formata a terra si ottiene la pizza **integra come item** (senza drop separati di impasto e ingredienti); l'item puo' essere portato al Pizza Oven senza pala. Le statistiche e interazioni da arma sono ancora aperte e non vanno implementate senza definizione.

## 4. Vincoli tecnici di progetto (da implementare)

Una rappresentazione possibile, **non API definitiva**:

```text
PizzaState
  format_version
  preparation_state / cooking_state
  division_state       // intera, 2 meta', 1 meta' divisa, 4 quarti
  division_orientation // N/S/E/O scelto dalla direzione cardinale piu' vicina al primo clic; stabile dopo
  applications[]
    application_id
    ingredient_id + consumed_stack/count
    original_quarter_mask   // 4 bit
    remaining_quarter_mask  // 4 bit
    application_order   // cronologia persistente della rimozione
    render data / seed
  additional_visual_and_recipe_data

PizzaPeel
  contained_pizza_state?    // zero oppure una pizza
```

Invarianti da mantenere:

1. `remaining_quarter_mask` e' un sottoinsieme dei quarti originariamente coperti.
2. Ogni applicazione puo' rimborsare la propria quantita' **una volta sola**.
3. La rimozione completa cancella l'applicazione soltanto dopo aver gestito il rimborso.
4. La rottura di una pizza formata **ancora INTEGRA** produce **soltanto la pizza-item** con le applicazioni ancora presenti, senza rimborsarle di nuovo come ingredienti separati. Le applicazioni gia' rimosse e rimborsate non ricompaiono nell'item.
5. Il trasporto sulla pala conserva dati e identita' visiva, non li ricostruisce da un gusto predefinito.
6. Tutte le mutazioni di inventario e mondo devono essere autorevoli lato server e sincronizzate ai client.
7. La rappresentazione deve consentire **mapping espliciti per singoli ID verificati** di item esterni senza espansione automatica dei tag né ammissione generica di qualsiasi cibo.

**Il primo codec di composizione e la BlockEntity sono ora aggiunti al branch (FASE 3A, build/test ancora necessari):** maschere di copertura, ordine applicazioni, item originario, divisione e orientamento sono serializzabili. **Non** sono ancora state collegate la posa/rimozione dei condimenti, il taglio, il servizio fette, i progressi di cottura, la sincronizzazione o il trasporto item/pala/forno. Gli altri nomi tecnici nella bozza concettuale restano illustrativi. [Test](PERSISTENT_COMPOSITION_TESTS.md).

## 5. Questioni ancora aperte

- Geometria delle suddivisioni create col mattarello: **linea parallela alla direzione cardinale piu' vicina allo sguardo e orientamento a scatti confermati**. **Clic su zone/confini confermato:** posizione effettiva del punto colpito, sempre una sola zona con tie-break deterministico; priorita' sui confini, spareggio orario e tolleranza iniziale `1/1024` sono **APPROVATI**, ma restano da verificare con test di precisione client/server. Il **gesto di rimozione** e la **priorita' dell'ultima applicazione** sono ora confermati.
- **Riunione confermata** con Shift + clic destro col mattarello: quarti della stessa meta', poi due meta' intere. **Divisioni e riunioni non consumano durabilita'**; restano da precisare hitbox e casi limite.
- **Limite confermato di 6 ingredienti diversi per quarto fisico**, salsa inclusa, con rifiuto dell'intera applicazione se anche un solo quarto supererebbe sei. **Equivalenze tra mod confermate nel principio**: item esplicitamente equivalenti condividono il vincolo anti-duplicato e occupano un unico tipo; da definire formato tecnico e mapping iniziale. Per una salsa diversa su zona parzialmente occupata e' previsto il rifiuto integrale. **Confermato:** le salse sono facoltative e non si sovrappongono; lo stesso ingrediente non puo' essere duplicato sulla stessa porzione.
- Priorita' e visualizzazione degli ingredienti **diversi** sovrapposti (non delle salse tra loro, che non possono sovrapporsi).
- Regole esatte per il recupero di contenitori e rimanenze (bottiglie, ciotole).
- Modellazione 2.5D per quarto, forme, seme deterministico, rendering da cruda e cotta.
- Interazione della pala con Pizza Oven e forni vanilla, inventari, hopper e pizza gia' cotta. **Riutilizzo senza durabilita' confermato. Futuro utilizzo come arma in stile padella di Farmer's Delight Refabricated da progettare, senza cambiare le regole di trasporto gia' confermate.**
- **Drop, pickup, despawn e distruzione ambientale della pala gia' confermati secondo vanilla:** Q produce il normale item con la pizza persistente all'interno; al pickup ritorna integra. Se l'item despawna o viene effettivamente distrutto da lava, fuoco, cactus, esplosioni ecc., **si perdono pala e pizza senza rimborsi**. Resta da progettare la resa grafica e l'uso col forno.
- Servizio diretto in quattro porzioni della pizza cotta piazzata, senza taglio preliminare; bilanciamento nutrizionale per singolo quarto.

## 6. Criteri di accettazione futuri

- [ ] Senza divisioni: il topping cliccato si applica all'intera pizza; dopo un passaggio col mattarello si applica alla meta' puntata; un ulteriore passaggio su quella meta' crea due quarti condibili singolarmente.
- [ ] La prima divisione **aggancia** la direzione orizzontale del giocatore al **cardinale piu' vicino** (N/S/E/O), tracciando una linea parallela a quel cardinale e creando meta' **sinistra/destra**. Non esistono linee a rotazione libera in diagonale; la direzione scelta e' memorizzata e non cambia quando il giocatore si sposta, ne' dopo salvataggio o trasporto con la pala.
- [ ] I clic dentro una zona, esattamente sul confine tra due zone o sul centro tra quattro quarti selezionano **una e una sola zona** in modo deterministico, usando la posizione locale reale e l'orientamento persistente; nessuna doppia applicazione per un solo clic.
- [ ] Le divisioni sono mostrate solo mentre e' in mano il mattarello o un ingrediente valido, senza variazioni permanenti ai modelli/texture.
- [ ] Shift + clic destro col mattarello riunisce prima due quarti della stessa meta' e poi due meta' intere, senza cancellare o redistribuire topping, senza restituzioni automatiche.
- [ ] Una stesura completata con mattarello consuma **1 durabilita'** (in Sopravvivenza); divisioni e riunioni riuscite consumano **0**. Nessuna usura per interazioni prive di effetto o in Creativa.
- [ ] Shift + clic destro a mano vuota sulla zona selezionata rimuove una applicazione alla volta partendo dalla piu' recente presente su quella zona; se vuota non produce effetti.
- [ ] Una mozzarella originariamente applicata a tutti i 4 quarti logici costa 1 item; toglierla da 3 quarti non rimborsa; togliere l'ultimo rimborsa 1.
- [ ] Quattro applicazioni distinte non si fondono in una e non producono rimborsi errati.
- [ ] Due clic dello stesso ingrediente sulla stessa zona: il secondo non consuma item e non aggiunge condimento.
- [ ] Pizza bianca senza alcuna salsa, pizza con salsa su una sola meta' e pizza con salse diverse su meta' differenti sono tutte preparazioni valide.
- [ ] Impedire che due salse, anche diverse, occupino la stessa parte fisica, anche dopo divisioni e riunioni col mattarello.
- [ ] Applicazione con massimo sei tipi di ingredienti diversi per quarto (salsa inclusa); un nuovo ingrediente che farebbe arrivare qualsiasi quarto interessato a sette viene respinto integralmente **senza consumo**.
- [ ] Conteggio e rifiuto invariati dopo divisioni, riunioni, salvataggio, pala e eventuale crafting. Rimozione libera il posto soltanto nella parte effettivamente ripulita.
- [ ] Due mozzarelle di mod diverse esplicitamente equivalenti sono trattate come lo stesso ingrediente: niente doppia applicazione sul medesimo quarto, un solo tipo nel limite di 6; recupero dell'item *effettivamente usato* e non di un item equivalente.
- [ ] Tentare pesto sull'intera pizza dopo aver riunito una meta' al pomodoro e una meta' bianca: **rifiutare tutto**, nessuna modifica e nessun consumo. Dopo nuova divisione, consentire di condire la sola meta' bianca.
- [ ] Dopo la riunione di zone parzialmente coperte, un clic dello stesso ingrediente riempie **solo le sottoaree libere**, consumando un solo item; se non restano sottoaree libere non accade nulla.
- [ ] Rompere una **pizza formata INTEGRA cruda o cotta** restituisce **un solo item pizza completo**; dopo aver servito almeno una fetta dalla pizza cotta, romperla restituisce invece esclusivamente i quarti rimasti, con dati di ricetta e cottura invariati, senza drop separati di impasto o topping. Le pizze-item possono essere utilizzate come input del forno senza pala.
- [ ] Recupero nel normale inventario; drop solo in mancanza di spazio.
- [ ] La pala trasporta esattamente una pizza con tutti i dati e rifiuta una seconda.
- [ ] La pala **non ha durabilita'**: raccolte, depositi e trasferimenti ripetuti non causano usura o rottura dell'oggetto.
- [ ] **Pizza Oven:** pala occupata inserisce; pala vuota estrae con clic destro **anche una pizza cruda o parzialmente cotta** senza GUI. Nessuna duplicazione o usura; prova con forno vuoto, forno pieno, energia residua, salvataggio e reinserimento della pizza a meta' cottura.
- [ ] **Stack massimo 1** in entrambi gli stati; pale vuote uguali non si impilano, pale occupate non si uniscono tra loro ne' a pale vuote, nemmeno trasferendole tra inventari e contenitori. Le pizze devono mantenere dati separati senza duplicazioni.
- [ ] **Q/drop** di pala occupata: un normale item a terra, con pizza ancora nei suoi dati; **pickup** restituisce pala occupata e pizza con ingredienti, divisioni, orientamento, applicazioni, cottura e visuale identici, anche tra giocatori diversi.
- [ ] Test drop/pickup ripetuti con una o piu' pale e in multiplayer, senza dupe o corruzione.
- [ ] Test **despawn naturale vanilla** della pala occupata: al normale termine della vita dell'item a terra scompaiono **sia pala sia pizza**, senza item/ingredienti droppati e senza protezioni o timer aggiuntivi. Verificare anche comportamento del mondo quando il chunk non e' caricato.
- [ ] Test **danni ambientali vanilla** sulla pala occupata lasciata a terra (lava, fuoco, cactus, esplosioni): quando l'item viene distrutto, pala e pizza scompaiono senza drop o recuperi, come per gli item normali. Nessuna protezione aggiuntiva.
- [ ] In una fase successiva, progettare la pala come **arma ispirata alla padella di Farmer's Delight Refabricated solo quando e' VUOTA**; la versione OCCUPATA non ha bonus ne' effetti speciali da arma e deve conservare integralmente la pizza. Danni, animazioni e dettagli di combattimento restano da definire.
- [ ] Test: pala vuota con caratteristiche da arma attive; caricare una pizza e verificare che le caratteristiche speciali siano disattivate; depositare la pizza e verificare che tornino attive, senza perdita o duplicazione dei dati.
- [ ] Raccolta e riposizionamento mantengono quarti, stadio e resa visiva dopo salvataggio/ricarica.
- [ ] Test multiplayer, chunk unload, distruzione supporto, inventario pieno e trasferimento al forno quando disponibile.

## Documenti collegati

- [README.md](README.md) — panoramica della pizza 1.0.0
- [GAMEPLAY.md](GAMEPLAY.md) — flusso delle interazioni
- [TOPPINGS_AND_RENDERING.md](TOPPINGS_AND_RENDERING.md) — composizione e renderer
- [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md) — dati e compatibilita'
- [PIZZA_OVEN.md](PIZZA_OVEN.md) — futuro sistema di cottura
- [ROADMAP.md](ROADMAP.md) — stato e checklist
