# Gameplay: pizza preparata direttamente nel mondo

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

**Test di regressione richiesti:** clic destro con mano principale vuota (anche con oggetto nella mano secondaria), mattarello sulla palla e su impasto parzialmente steso, perdita di 1 durabilità, nessuna perdita sulla base completa, rottura dopo 64 stesure, Creative vs Survival e salvataggio progressi.

## 3. Condimento con clic destro — CONFERMATO

Il giocatore tiene l'ingrediente in mano e usa clic destro sulla pizza stesa. Se l'ingrediente è valido, viene consumata una porzione e aggiunta alla composizione. Non è necessario inserire prima pomodoro, formaggio o erbe in un ordine particolare. Sono ammesse pizze inventate dal giocatore, anche senza salsa rossa.

**La specifica definitiva del condimento e della pala e' in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).** Dopo la stesura la pizza resta intera finche' il giocatore non usa il **mattarello per dividerla prima in due meta', poi eventualmente per suddividere ogni meta' in due quarti**. Il clic con ingrediente condice la zona puntata in base alla suddivisione effettiva (intera, meta' o quarto). Le linee sono visibili temporaneamente solo tenendo mattarello o ingrediente valido. Ogni applicazione mantiene l'associazione con la propria copertura. La rimozione parziale non rimborsa; il rimborso avviene soltanto quando tutte le parti dell'applicazione originale sono state rimosse. L'ingrediente torna nell'inventario, con drop soltanto se pieno.

**Primo prototipo funzionale, da testare:** su `stage=4`, un clic destro con **una mozzarella intera** della mod (`italiansdelight:mozzarella`) consuma 1 item in Sopravvivenza, imposta `mozzarella=true` sul BlockState e disegna cinque piccoli pezzi nella conca senza coprire il bordo. Non accetta una seconda mozzarella: è solo un test di interazione/rendering, non il futuro sistema di dosi e topping multipli. In Creativa non consuma l'item. Alla rottura del blocco il loot table restituisce anche la mozzarella, evitando di perderla. I modelli riutilizzano la texture mozzarella già presente, senza modificare PNG.

**Proposte:** massimo 8 porzioni complessive, includendo salse; più porzioni dello stesso topping; categorie ingredienti registrate e non tutti i cibi ammessi indiscriminatamente; controllo dei contenitori restituiti.

### Tabella interazioni ipotizzata

| Interazione | Comportamento progettato |
| --- | --- |
| Clic destro con palla d'impasto su blocco | Colloca impasto per la preparazione |
| Clic destro vuoto sull'impasto | Avanza la stesura manuale |
| Clic destro con mattarello sulla palla/base non ancora stesa | Stende rapidamente |
| Clic destro con mattarello su base completamente stesa e non divisa | Divide logicamente la pizza in **due meta'** |
| Clic destro con mattarello sulla meta' desiderata | Divide quella meta' in **due quarti** |
| Clic destro con ingrediente valido sulla zona desiderata | Applica il topping all'**intera pizza, alla meta' o al quarto** puntato, secondo la suddivisione corrente |
| Shift + clic destro col mattarello su una zona divisa | **Ipotesi da confermare:** riunisce le zone conservando i topping |
| Clic destro con topping valido sulla base | Aggiunge il topping solo alla zona logica puntata (intera, meta' o quarto) |
| Shift + clic destro **a mano vuota** sulla zona condita | **CONFERMATO:** rimuove un solo condimento, dall'ultima applicazione ancora presente in quella zona; il rimborso scatta soltanto quando l'intera applicazione originale e' stata rimossa da tutte le zone coperte |
| Shift + clic destro **a mano vuota** sulla zona senza condimenti | **CONFERMATO:** nessun effetto |
| Clic destro con pala per pizza libera | **Confermato:** raccoglie direttamente una pizza con tutti i dati; massimo una pizza per pala |
| Clic destro con pala occupata su superficie valida | **Confermato:** riposiziona la pizza, preservandone la composizione |
| Rottura della pizza o del blocco di supporto | Recupero dell'oggetto con dati completi, senza duplicazioni |

La rimozione con **Shift + clic destro a mano vuota**, una applicazione alla volta in ordine inverso a quello di aggiunta sulla zona, e' **CONFERMATA**. Le priorita' della mano secondaria e del gesto Shift col mattarello per riunire le zone sono ancora da confermare. Non prevedere clic sulle linee per condire due quarti, ne' Shift + clic con ingrediente per condire tutta la pizza: la superficie dipende dalla divisione col mattarello.

## 4. Raccolta e conservazione

Quando si raccoglie la pizza, devono sopravvivere:
- topping effettivi, quantita' e ordine di inserimento se serve per rimuovere l'ultimo;
- **livello e configurazione di divisione** della pizza, inclusa la possibilita' di avere una meta' divisa e l'altra intera;
- stadio impasto/cruda/cotta;
- identità visiva della pizza e seme di distribuzione;
- dati per nomi, nutrizione e cottura.

**Pala confermata:** trasporta direttamente una pizza per volta, con composizione, quarti, stato e dati visivi; non richiede un secondo item pizza nell'inventario. La pala non e' obbligatoria per aggiungere o rimuovere condimenti. L'inserimento/estrazione con pala nel Pizza Oven e' previsto ma ancora da implementare. Se raccolta e riposizionata, la pizza deve tornare con la **stessa composizione**; la rottura del supporto non puo' creare piu' item.

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
