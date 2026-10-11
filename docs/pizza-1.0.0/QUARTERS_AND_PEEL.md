# Pizza 1.0.0 — Condimento per quarti e pala per pizza

> **Stato (11 ottobre 2026): specifica di design, non implementazione completata.**
> Questo documento registra le decisioni concordate. **CONFERMATO** indica un comportamento deciso; **DA DEFINIRE** indica una scelta ancora aperta. Le indicazioni tecniche sono requisiti o proposte, non attestazioni di codice gia' presente.

## Obiettivo e contesto

La pizza e' preparata direttamente nel mondo, senza GUI obbligatoria per condirla. Il giocatore aggiunge ingredienti con clic destro sulla base gia' stesa e puo' creare gusti liberi. Il rendering previsto resta 2.5D: salse piatte e condimenti leggermente rialzati.

Il prototipo attuale della mozzarella e' **provvisorio**: usa il booleano `mozzarella` del BlockState, consuma un item e visualizza cinque pezzi. **Non** implementa ancora i quattro quarti, le applicazioni multiple, la rimozione parziale o i dati sulla pala.

## 1. Divisione facoltativa con il mattarello — CONFERMATO

**Correzione importante:** la pizza NON nasce obbligatoriamente divisa in quattro parti. La suddivisione viene scelta dal giocatore **solo dopo aver steso completamente l'impasto**.

- **Pizza intera (1 zona):** nessuna ulteriore interazione col mattarello; un ingrediente applicato copre l'intera pizza.
- **Due meta' (2 zone):** con un clic destro del **mattarello sulla base gia' completamente stesa** la pizza si divide logicamente in due meta'. Ogni meta' puo' essere condita in modo autonomo.
- **Fino a quattro quarti (3 o 4 zone):** usando nuovamente il mattarello **sulla meta' mirata**, solo quella meta' viene divisa in due quarti; anche l'altra meta' puo' essere divisa successivamente. E' ammessa una pizza con una meta' ancora intera e l'altra divisa in due quarti.
- **Applicazione:** con clic destro tenendo un ingrediente valido, il condimento viene distribuito **sulla zona logica puntata** (pizza intera, una meta' o un quarto, in base alle divisioni esistenti). Non e' necessario cliccare sulle linee, ne' premere Shift per scegliere una superficie intera.
- **Divisioni solo come overlay temporaneo:** le linee di suddivisione si vedono soltanto quando il giocatore tiene **il mattarello o un ingrediente valido**. La suddivisione e' virtuale: **non cambiare le texture o i modelli fisici della base**.
- Le suddivisioni servono al condimento, **non** significano che la pizza e' stata tagliata fisicamente in fette.

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

**DA DEFINIRE:** direzione e hitbox della prima divisione e dell'eventuale divisione di una meta' e priorita' del clic al confine. **Durabilita' confermata:** zero per divisioni/riunioni, uno per completare la stesura. **Confermati:** riunione progressiva e conservazione della distribuzione, un solo item per applicazione riuscita e massimo 6 ingredienti diversi per quarto fisico (salsa inclusa).

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

**DA DEFINIRE:** se ingredienti equivalenti tramite tag debbano condividere il vincolo anti-duplicato. **L'eventuale limite non e' piu' aperto: sono confermati 6 ingredienti diversi per quarto fisico.** **Salse gia' confermate:** facoltative, al massimo una per sottoarea fisica, anche se di tipo diverso. Un clic con una salsa *diversa* su una zona che contiene anche solo parzialmente una salsa preesistente viene **rifiutato interamente**, senza consumare l'item.

### Distinzione dopo una riunione

Se nella zona riunita convivono piu' **applicazioni distinte dello stesso ingrediente**, il comando gia' confermato di rimozione (Shift + clic destro a mano vuota) agisce **prima sull'applicazione piu' recente**, e soltanto sulla copertura residua che le appartiene all'interno della zona selezionata. Le applicazioni precedenti restano al loro posto fino alle successive rimozioni. Non rimborsare due volte, ne' trattare una riunione come una nuova applicazione.

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

**DA DEFINIRE:** item concreti da utilizzare come salse e dettagli dei contenitori restituiti.

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

### Rottura della pizza — CONFERMATO

- Rompendo una **pizza cruda in preparazione** si recuperano l'impasto e gli ingredienti ancora associati alla preparazione.
- Un'applicazione ancora presente anche su un solo quarto puo' restituire **al massimo** la quantita' inizialmente consumata (es. **una mozzarella**, non una per quarto).
- Il recupero alla rottura e il recupero tramite rimozione devono essere reciprocamente esclusivi: **nessuna duplicazione**.
- La **pizza gia' cotta** non viene scomposta in ingredienti originari quando viene rotta: e' un prodotto finito.

**DA DEFINIRE:** dettagli della raccolta quando la pizza viene rotta da un evento senza giocatore, creativa, blocco di supporto distrutto, esplosioni e contenitori/remainder particolari. Queste varianti non devono violare la conservazione degli item.

## 3. Pala per pizza (Pizza Peel) — CONFERMATO

La **pala per pizza** e' lo strumento per trasferire una pizza **intera**. E' gia' disponibile un asset della vecchia Italian's Delight fornito dall'autore: **riutilizzarlo dopo averlo recuperato e verificato**, senza crearne o sostituirne la texture autonomamente.

- La pala trasporta **direttamente una pizza alla volta**, non la converte in un item pizza separato collocato in un altro slot dell'inventario.
- Con **clic destro** su una pizza posizionata, la pala libera la raccoglie solo se non ne trasporta gia' una.
- La pala occupata **non puo' raccogliere una seconda pizza**.
- Con **clic destro** su una superficie compatibile, la pala occupata deposita la pizza e ricostruisce lo stato precedente.
- L'uso della pala **non e' obbligatorio per applicare o rimuovere** i condimenti: queste interazioni avvengono direttamente sulla pizza nel mondo.
- La pala e' destinata anche a **inserimento ed estrazione** della pizza nel futuro Pizza Oven. L'integrazione del forno deve essere implementata e verificata quando il forno esistera'.

### Dati conservati durante il trasporto — CONFERMATO

L'intero stato della preparazione deve viaggiare **dentro i dati persistenti della pala occupata**, incluse almeno le seguenti informazioni:

- stato e progresso della pizza (impasto/base, cruda/condita/cotta secondo il caso), **inclusa la struttura di suddivisione scelta col mattarello**;
- ingredienti, quantita', applicazioni originali e quarti ancora coperti; **nessun ingrediente puo' occupare due volte lo stesso quarto fisico**;
- disposizione visiva, inclusi eventuali semi e parametri di rendering;
- proprieta' necessarie per determinare il gusto e preservare la cottura.

Il trasferimento **mondo -> pala -> mondo/forno -> pala** dev'essere atomico dal punto di vista logico: mai due pizze create dalla stessa raccolta e mai cancellazione della fonte prima che lo stato sia trasferito in sicurezza. Il salvataggio del mondo, il chunk unload e il multiplayer devono mantenere uno stato coerente.

**DA DEFINIRE:** ricetta della pala; se abbia durabilita' (orientamento: strumento riutilizzabile senza usura); tooltip, stackability e aspetto mentre e' occupata; comportamento se la pala occupata e' droppata, distrutta o spostata in contenitori; comandi/precedenze per il forno; supporto alle pizze gia' cotte e alle superfici specifiche. Nessuna di queste e' ancora una regola tecnica finalizzata.

## 4. Vincoli tecnici di progetto (da implementare)

Una rappresentazione possibile, **non API definitiva**:

```text
PizzaState
  format_version
  preparation_state / cooking_state
  division_state       // intera, 2 meta', 1 meta' divisa, 4 quarti
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
4. La rottura della pizza non rimborsa applicazioni gia' restituite.
5. Il trasporto sulla pala conserva dati e identita' visiva, non li ricostruisce da un gusto predefinito.
6. Tutte le mutazioni di inventario e mondo devono essere autorevoli lato server e sincronizzate ai client.
7. La rappresentazione deve consentire tag/definizioni per ingredienti di altre mod senza assumere automaticamente che tutti i cibi siano validi.

Questi nomi di campo sono *illustrativi*: codec, formato dei component, BlockEntity e protocolli saranno scelti durante l'implementazione.

## 5. Questioni ancora aperte

- Geometria delle suddivisioni create col mattarello e clic ai confini tra zone. Il **gesto di rimozione** e la **priorita' dell'ultima applicazione** sono ora confermati.
- **Riunione confermata** con Shift + clic destro col mattarello: quarti della stessa meta', poi due meta' intere. **Divisioni e riunioni non consumano durabilita'**; restano da precisare hitbox e casi limite.
- **Limite gia' confermato di 6 ingredienti diversi per quarto fisico**, salsa inclusa, con rifiuto dell'intera applicazione se anche un solo quarto supererebbe sei. Restano da definire equivalenze tra ingredienti e dettagli implementativi; per una salsa diversa su zona parzialmente occupata e' gia' previsto il rifiuto integrale. **Confermato:** le salse sono facoltative e non si sovrappongono; lo stesso ingrediente non puo' essere duplicato sulla stessa porzione.
- Priorita' e visualizzazione degli ingredienti **diversi** sovrapposti (non delle salse tra loro, che non possono sovrapporsi).
- Regole esatte per il recupero di contenitori e rimanenze (bottiglie, ciotole).
- Modellazione 2.5D per quarto, forme, seme deterministico, rendering da cruda e cotta.
- Interazione della pala con Pizza Oven e forni vanilla, inventari, hopper e pizza gia' cotta.
- Cosa fare con una pala occupata quando viene distrutta e come visualizzarla.
- Taglio in fette della pizza cotta, bilanciamento e valori alimentari.

## 6. Criteri di accettazione futuri

- [ ] Senza divisioni: il topping cliccato si applica all'intera pizza; dopo un passaggio col mattarello si applica alla meta' puntata; un ulteriore passaggio su quella meta' crea due quarti condibili singolarmente.
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
- [ ] Tentare pesto sull'intera pizza dopo aver riunito una meta' al pomodoro e una meta' bianca: **rifiutare tutto**, nessuna modifica e nessun consumo. Dopo nuova divisione, consentire di condire la sola meta' bianca.
- [ ] Dopo la riunione di zone parzialmente coperte, un clic dello stesso ingrediente riempie **solo le sottoaree libere**, consumando un solo item; se non restano sottoaree libere non accade nulla.
- [ ] La rottura di una pizza cruda recupera impasto e ingredienti residui senza duplicare; quella cotta non restituisce gli ingredienti separati.
- [ ] Recupero nel normale inventario; drop solo in mancanza di spazio.
- [ ] La pala trasporta esattamente una pizza con tutti i dati e rifiuta una seconda.
- [ ] Raccolta e riposizionamento mantengono quarti, stadio e resa visiva dopo salvataggio/ricarica.
- [ ] Test multiplayer, chunk unload, distruzione supporto, inventario pieno e trasferimento al forno quando disponibile.

## Documenti collegati

- [README.md](README.md) — panoramica della pizza 1.0.0
- [GAMEPLAY.md](GAMEPLAY.md) — flusso delle interazioni
- [TOPPINGS_AND_RENDERING.md](TOPPINGS_AND_RENDERING.md) — composizione e renderer
- [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md) — dati e compatibilita'
- [PIZZA_OVEN.md](PIZZA_OVEN.md) — futuro sistema di cottura
- [ROADMAP.md](ROADMAP.md) — stato e checklist
