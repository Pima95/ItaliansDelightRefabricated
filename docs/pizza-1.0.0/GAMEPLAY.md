# Gameplay: pizza preparata direttamente nel mondo

**Principio trasversale CONFERMATO:** ogni comportamento ordinario di Minecraft segue le regole **vanilla per impostazione predefinita** (drop, pickup, despawn, danni ambientali, inventari e simili), fatta eccezione per meccaniche personalizzate gia' concordate esplicitamente. Non e' necessario discutere una ad una le casistiche basilari.

## 1. Posizionamento dell'impasto — CONFERMATO

La **palla d'impasto** va appoggiata con clic destro direttamente sopra un blocco. **Non** serve un Cutting Board, un piano dedicato o una GUI. Il giocatore deve essere libero di lavorare su pietra, legno, tavoli e altre superfici.

**Regola aggiornata e confermata:** l'impasto richiede una superficie superiore approvata. Sono consentiti i blocchi con collisione cubica piena, le slab superiori e doppie, le scale capovolte, le botole superiori chiuse e le incudini. Sono vietati gli altri stati delle slab/scale/botole e i casi esplicitamente esclusi dall'autore. Per il dettaglio e i casi non ancora decisi vedere [SURFACE_RULES.md](SURFACE_RULES.md). La sabbia delle anime non è consentita; non è più necessaria alcuna variante ribassata del modello. La stessa verifica governa posizionamento e sopravvivenza.

## 2. Stesura — CONFERMATO

- **A mano:** clic destro a mano vuota sulla palla per appiattirla progressivamente.
- **Con mattarello:** clic destro per velocizzare o completare la stesura.
- Entrambi i metodi arrivano alla **stessa base per pizza**. La pizza resta nel mondo, pronta per il condimento.
- La stesura con Cutting Board è **espressamente esclusa**.

**Stesura manuale testata prima dell'aggiunta del mattarello, da ritestare dopo la correzione del clic destro:** 4 clic destri a mano vuota fanno passare l'impasto da `stage=0` (palla) a `stage=4` (base stesa), con modelli e hitbox progressivamente più larghi e piatti. I progressi rimangono nel BlockState salvato nel mondo e l'impasto smette di stendersi al quarto clic. La stesura con Shift è esclusa per riservare l'interazione al futuro recupero. **I 4 clic restano un valore di bilanciamento provvisorio.** **Prototipo mattarello corretto, da testare:** l'oggetto `italiansdelight:rolling_pin` stende l'impasto direttamente allo stadio 4 con un singolo clic destro. Ha **64 punti di durabilità** (come il pennello vanilla): perde 1 punto soltanto quando completa realmente una stesura e si rompe al termine; non perde durabilità se la base è già stesa o in Creativa. La stesura manuale continua con 4 clic e non danneggia strumenti. Il gestore di interazione Fabric è unico per entrambi i modi, per correggere il problema del clic destro segnalato nel test precedente. Il mattarello è craftabile con **2 bastoni e 1 asse di legno di qualsiasi tipo** nella fila orizzontale `SPS` (`S` = bastone, `P` = tag vanilla `#minecraft:planks`). Sono ammessi anche legni aggiunti da altre mod, purché le relative assi appartengano al tag. Rimane disponibile nella scheda Creativa e tramite `/give`; usa una texture item dedicata 16×16, con sfondo trasparente e colori completamente opachi sull'utensile. I cinque modelli dell'impasto usano texture opache 16×16 per superficie e lati, con particelle coerenti; **le geometrie e le hitbox sono state aggiornate dopo il test visivo**: all'inizio l'impasto è un parallelepipedo basso senza strati sovrapposti; durante la stesura si forma una scanalatura con un bordo rialzato continuo. I dettagli sono in [DOUGH_GEOMETRY.md](DOUGH_GEOMETRY.md). Dettagli e anteprima in [TEXTURES.md](TEXTURES.md). Attualmente rompere la base rilascia il Wheat Dough di partenza e non un nuovo item Pizza Base; raccolta e conservazione di una base stesa verranno affrontate in un passo successivo.

**Test riuscito confermato dall'autore:** stesura manuale e con mattarello dopo la correzione dell'interazione. **Da provare ora:** crafting con assi di quercia, abete, bambù, cremisi e deformate; verificare il risultato singolo, il libro delle ricette e l'assenza di variazioni nelle meccaniche di stesura.

**Test di regressione richiesti:** clic destro con mano principale vuota (anche con oggetto nella mano secondaria), mattarello sulla palla e su impasto parzialmente steso, perdita di 1 durabilita' **solo quando completa una stesura**, nessuna perdita per divisioni o riunioni sulla base stesa, nessuna perdita per tentativi senza effetto, rottura dopo 64 stesure riuscite, Creative vs Survival e salvataggio progressi.

## 3. Condimento con clic destro — CONFERMATO

Il giocatore tiene l'ingrediente in mano e usa clic destro sulla pizza stesa. Se l'ingrediente e' valido **e rimangono sottoaree prive di quell'ingrediente nella zona mirata**, viene consumato **un solo item** e aggiunta la sua applicazione **solo sulle sottoaree libere**. Un tentativo interamente duplicato non consuma nulla. Non è necessario inserire prima pomodoro, formaggio o erbe in un ordine particolare. Sono ammesse pizze inventate dal giocatore, **anche senza alcuna salsa** (pizza bianca): la salsa e' **facoltativa**. Se presente, una porzione fisica puo' avere **una sola salsa**; salse diverse possono essere distribuite su zone distinte ma mai sovrapposte.

**La specifica definitiva del condimento e della pala e' in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).** Dopo la stesura la pizza resta intera finche' il giocatore non usa il **mattarello per dividerla prima in due meta', poi eventualmente per suddividere ogni meta' in due quarti**. Il clic con ingrediente condice la zona puntata in base alla suddivisione effettiva (intera, meta' o quarto). Le linee sono visibili temporaneamente solo tenendo mattarello o ingrediente valido. Ogni applicazione mantiene l'associazione con la propria copertura. La rimozione parziale non rimborsa; il rimborso avviene soltanto quando tutte le parti dell'applicazione originale sono state rimosse. L'ingrediente torna nell'inventario, con drop soltanto se pieno.

**Primo prototipo funzionale, da testare:** su `stage=4`, un clic destro con **una mozzarella intera** della mod (`italiansdelight:mozzarella`) consuma 1 item in Sopravvivenza, imposta `mozzarella=true` sul BlockState e disegna cinque piccoli pezzi nella conca senza coprire il bordo. Non accetta una seconda mozzarella: è solo un test di interazione/rendering, non il futuro sistema di dosi e topping multipli. In Creativa non consuma l'item. Alla rottura del blocco il loot table restituisce anche la mozzarella, evitando di perderla. I modelli riutilizzano la texture mozzarella già presente, senza modificare PNG.

**Ingredienti equivalenti CONFERMATO:** due item di mod differenti mappati esplicitamente allo stesso ingrediente valgono come uno solo: non si puo' applicare due volte sullo stesso quarto nemmeno usando versioni cross-mod; ogni gruppo equivalente conta come un solo ingrediente nel limite, mentre l'item restituito e' sempre quello originariamente consumato. **Limite CONFERMATO: massimo 6 ingredienti diversi per quarto fisico, contando anche la salsa facoltativa.** Se un'applicazione porterebbe anche un solo quarto interessato sopra sei, va **rifiutata tutta**, senza consumi, anche negli altri quarti eventualmente liberi. Le suddivisioni e riunioni col mattarello non modificano il conteggio. **Questioni aperte:** categorie ingredienti registrate e gestione dei contenitori restituiti. **Confermato:** se la zona cliccata contiene anche parzialmente una salsa differente, l'intera applicazione viene rifiutata senza consumi, anche nelle parti libere; dividere la pizza con il mattarello permette di mirare alla sola zona senza salsa. **Confermato:** stessa tipologia di ingrediente mai ripetuta sulla stessa porzione; salsa non obbligatoria e massimo una salsa per sottoarea.

### Tabella interazioni ipotizzata

| Interazione | Comportamento progettato |
| --- | --- |
| Clic destro con palla d'impasto su blocco | Colloca impasto per la preparazione |
| Clic destro vuoto sull'impasto | Avanza la stesura manuale |
| Clic destro con mattarello sulla palla/base non ancora stesa | Stende rapidamente |
| Clic destro con mattarello su base completamente stesa e non divisa | Aggancia lo sguardo orizzontale al **cardinale piu' vicino** (N/S/E/O) e traccia una linea **parallela a quel cardinale**, creando **meta' sinistra/destra**, senza usura |
| Clic destro con mattarello sulla meta' desiderata | Divide quella meta' in **due quarti**, senza perdere durabilita' |
| Clic destro con ingrediente valido sulla zona desiderata | Individua **una sola zona logica dalla posizione effettiva del clic**, con priorita' deterministica su eventuali confini, poi applica il topping all'**intera pizza, alla meta' o al quarto** individuato secondo la suddivisione corrente |
| Shift + clic destro col mattarello su un quarto | **CONFERMATO:** riunisce i due quarti della stessa meta' senza spostare o rimborsare ingredienti e senza consumare durabilita' |
| Shift + clic destro col mattarello quando le due meta' sono intere | **CONFERMATO:** riunisce le meta' in una pizza intera, conservando il posizionamento dei topping e senza consumare durabilita' |
| Clic destro con topping valido sulla base | Aggiunge il topping **solo alle parti non gia' coperte dallo stesso ingrediente** nella zona logica puntata (intera, meta' o quarto); consuma 1 item per clic riuscito |
| Clic con ingrediente gia' presente su tutta la zona | **CONFERMATO:** non modifica nulla e non consuma item |
| Clic con una variante di un'altra mod esplicitamente equivalente a un ingrediente gia' presente | **CONFERMATO:** vale come lo **stesso ingrediente**; non sovrappone, non aggira il limite di 6 e non consuma nulla se la zona e' gia' coperta tutta |
| Clic con salsa diversa su una zona che contiene gia' un'altra salsa anche soltanto in parte | **CONFERMATO:** rifiuta **l'intera applicazione**, anche sulle parti vuote; nessun item consumato o salsa sostituita |
| Clic che aggiungerebbe un settimo ingrediente diverso in almeno un quarto coinvolto | **CONFERMATO:** rifiuta **l'intera applicazione** senza modificare nessun quarto e senza consumare item; salsa inclusa nel conteggio |
| Shift + clic destro **a mano vuota** sulla zona condita | **CONFERMATO:** rimuove un solo condimento, dall'ultima applicazione ancora presente in quella zona; il rimborso scatta soltanto quando l'intera applicazione originale e' stata rimossa da tutte le zone coperte |
| Shift + clic destro **a mano vuota** sulla zona senza condimenti | **CONFERMATO:** nessun effetto |
| Clic destro con pala per pizza libera | **Confermato:** raccoglie direttamente una pizza con tutti i dati; massimo una pizza per pala |
| Clic destro con pala occupata su superficie valida | **Confermato:** riposiziona la pizza, preservandone la composizione e senza consumare durabilita' |
| Spostare una pala nell'inventario o tra contenitori | **CONFERMATO:** pala non impilabile, stack massimo **1** vuota oppure occupata; nessuna fusione o duplicazione dei dati della pizza |
| Premere **Q** con pala occupata | **CONFERMATO:** cade un normale item pala **con la pizza conservata al suo interno**, senza separarla o alterarla |
| Raccogliere una pala occupata lasciata a terra | **CONFERMATO:** la pala ritorna in inventario con **tutti i dati originali della pizza** (condimenti, quarti, orientamento, cottura e rendering) |
| Despawn naturale della pala occupata lasciata a terra | **CONFERMATO:** normale despawn vanilla; scompaiono **sia pala sia pizza contenuta**, senza rimborsi o drop aggiuntivi |
| Pala occupata a terra distrutta da lava, fuoco, cactus o esplosione | **CONFERMATO:** valgono i normali danni alle entita' item vanilla; se la pala viene distrutta, si perdono **anche pizza e ingredienti**, senza drop aggiuntivi |
| Rottura della pizza o del blocco di supporto | Recupero dell'oggetto con dati completi, senza duplicazioni |

**Orientamento della prima divisione confermato:** dipende dalla direzione in cui guarda il giocatore nell'istante del clic col mattarello. Una volta creata, la divisione e' memorizzata e non ruota con i movimenti del giocatore, ne' sposta i condimenti. **CONFERMATO:** la linea e' **parallela alla direzione cardinale piu' vicina allo sguardo orizzontale**, separa sinistra/destra anziche' vicino/lontano, e usa un **orientamento a scatti N/S/E/O**, senza rotazione libera in diagonale. **CONFERMATO:** la posizione effettiva del clic seleziona **una sola zona logica**, anche se il punto e' esattamente su una linea o all'incrocio; in questi casi il risultato deve essere deterministico. Rimangono da precisare l'ordine esatto della priorita' tecnica sui confini e lo spareggio dei cardinali per angoli perfettamente intermedi.

La rimozione con **Shift + clic destro a mano vuota**, una applicazione alla volta in ordine inverso a quello di aggiunta sulla zona, e' **CONFERMATA**. Anche dopo una riunione le applicazioni precedenti restano separate: una nuova applicazione dello stesso ingrediente puo' coprire **solo le parti ancora libere** della zona riunita. Il gesto **Shift + clic destro col mattarello per riunire le zone e' confermato**. La mano secondaria e i dettagli tecnici della priorita' sui confini rimangono da definire; la regola approvata e' che il clic sul confine risolva **sempre in una sola zona**. Non prevedere clic sulle linee per condire due quarti, ne' Shift + clic con ingrediente per condire tutta la pizza: la superficie dipende dalla divisione col mattarello.

## 4. Raccolta e conservazione

Quando si raccoglie la pizza, devono sopravvivere:
- topping effettivi, quantita' e ordine di inserimento se serve per rimuovere l'ultimo;
- **livello e configurazione di divisione** della pizza, inclusa la possibilita' di avere una meta' divisa e l'altra intera; una successiva riunione deve preservare i dettagli spaziali dei vecchi condimenti, anche se le linee temporanee non sono piu' visibili;
- stadio impasto/cruda/cotta;
- identità visiva della pizza e seme di distribuzione;
- dati per nomi, nutrizione e cottura.

**Pala confermata:** trasporta direttamente una pizza per volta, con composizione, quarti, stato e dati visivi; non richiede un secondo item pizza nell'inventario. **Q/drop e pickup confermati:** come un normale oggetto a terra, la pala occupata conserva la pizza dentro i suoi dati e viene raccolta senza alcuna alterazione; **il despawn naturale segue il comportamento vanilla, facendo perdere sia pala sia pizza**; anche la distruzione tramite lava, fuoco, cactus ed esplosioni segue le regole vanilla: se la pala viene distrutta, scompare anche la pizza senza rimborsi. E' uno **strumento riutilizzabile senza durabilita'**, **non impilabile (stack massimo 1)** sia quando e' vuota sia quando trasporta una pizza: **ogni pala occupa un proprio slot**. Raccogliere e riposizionare pizze non la usura. La pala non e' obbligatoria per aggiungere o rimuovere condimenti. **Il Pizza Oven deve permettere l'inserimento e l'estrazione anche senza pala (CONFERMATO)**. La pala rimane un'opzione comoda, ma non obbligatoria, e la sua integrazione deve ancora essere implementata. Le interazioni proposte sono pizza-item per inserire e mano vuota per estrarre; il gesto che consente di raccogliere senza pala una pizza costruita nel mondo va ancora definito. Se raccolta e riposizionata, la pizza deve tornare con la **stessa composizione**; la rottura del supporto non puo' creare piu' item.

**Uso futuro come arma — solo a pala vuota (CONFERMATO):** l'autore vuole usare anche la pala in combattimento, prendendo come riferimento la padella di Farmer's Delight Refabricated, **ma soltanto quando non trasporta alcuna pizza**. **Se occupata, nessun bonus, statistica o effetto speciale da arma**: rimane uno strumento di trasporto e collocazione, e la pizza con tutti i suoi dati resta integra. Danni, velocita' ed eventuali animazioni per la versione vuota restano da definire; il normale attacco base di Minecraft non e' oggetto di questa regola. L'assenza di durabilita' resta confermata.

**Rottura della pizza:** se cruda, recuperare impasto e applicazioni di ingredienti ancora associate senza duplicazioni; se cotta, non restituire gli ingredienti separati. Dettagli e test in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

## 5. Crafting table — CONFERMATO come alternativa

La pizza deve essere ottenibile **anche con crafting**, senza costringere tutti a usare il procedimento fisico. Una proposta tecnica è una ricetta di crafting speciale, capace di registrare l'elenco dei topping in una pizza **cruda**, in modo da non richiedere centinaia di ricette statiche.

**Da decidere:** griglia/forma della ricetta, condimenti ripetuti e limitazioni di capienza, presenza di ricette predefinite per JEI, risultato crudo o cotto (proposta: crudo). La logica delle porzioni e dei nomi deve essere condivisa tra crafting e preparazione nel mondo.

## 6. Servizio e taglio — PROPOSTA

- Pizza cotta consumabile intera e potenzialmente riposizionabile nel mondo.
- Taglio in **4 fette** sul Cutting Board di Farmer's Delight.
- Ogni fetta conserva gusto, informazioni e rappresentazione coerente.
- Il Cutting Board sarebbe utilizzato **per tagliare la pizza cotta**, non per preparare/stendere l'impasto.
- Nutrizione/saturazione dipendenti dai condimenti con valore massimo bilanciato.

## 7. Stati da gestire e testare

`DOUGH_BALL -> STRETCHING -> RAW_BASE -> RAW_TOPPED -> COOKING -> BAKED -> SLICES`

Test: salvataggio durante stesura, chunk unload, rottura del supporto, due giocatori che cliccano la stessa pizza, mano secondaria, riconoscimento degli ingredienti, recupero dei remainder (es. ciotole/bottiglie), limite topping raggiunto, trasferimento tra blocco e inventario senza perdite.
