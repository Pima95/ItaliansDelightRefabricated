# Gameplay: pizza preparata direttamente nel mondo

## 1. Posizionamento dell'impasto — CONFERMATO

La **palla d'impasto** va appoggiata con clic destro direttamente sopra un blocco. **Non** serve un Cutting Board, un piano dedicato o una GUI. Il giocatore deve essere libero di lavorare su pietra, legno, tavoli e altre superfici.

**Regola aggiornata e confermata:** l'impasto richiede una superficie superiore approvata. Sono consentiti i blocchi con collisione cubica piena, le slab superiori e doppie, le scale capovolte, le botole superiori chiuse e le incudini. Sono vietati gli altri stati delle slab/scale/botole e i casi esplicitamente esclusi dall'autore. Per il dettaglio e i casi non ancora decisi vedere [SURFACE_RULES.md](SURFACE_RULES.md). La sabbia delle anime non è consentita; non è più necessaria alcuna variante ribassata del modello. La stessa verifica governa posizionamento e sopravvivenza.

## 2. Stesura — CONFERMATO

- **A mano:** clic destro a mano vuota sulla palla per appiattirla progressivamente.
- **Con mattarello:** clic destro per velocizzare o completare la stesura.
- Entrambi i metodi arrivano alla **stessa base per pizza**. La pizza resta nel mondo, pronta per il condimento.
- La stesura con Cutting Board è **espressamente esclusa**.

**Stesura manuale testata prima dell'aggiunta del mattarello, da ritestare dopo la correzione del clic destro:** 4 clic destri a mano vuota fanno passare l'impasto da `stage=0` (palla) a `stage=4` (base stesa), con modelli e hitbox progressivamente più larghi e piatti. I progressi rimangono nel BlockState salvato nel mondo e l'impasto smette di stendersi al quarto clic. La stesura con Shift è esclusa per riservare l'interazione al futuro recupero. **I 4 clic restano un valore di bilanciamento provvisorio.** **Prototipo mattarello corretto, da testare:** l'oggetto `italiansdelight:rolling_pin` stende l'impasto direttamente allo stadio 4 con un singolo clic destro. Ha **64 punti di durabilità** (come il pennello vanilla): perde 1 punto soltanto quando completa realmente una stesura e si rompe al termine; non perde durabilità se la base è già stesa o in Creativa. La stesura manuale continua con 4 clic e non danneggia strumenti. Il gestore di interazione Fabric è unico per entrambi i modi, per correggere il problema del clic destro segnalato nel test precedente. Il mattarello è craftabile con **2 bastoni e 1 asse di legno di qualsiasi tipo** nella fila orizzontale `SPS` (`S` = bastone, `P` = tag vanilla `#minecraft:planks`). Sono ammessi anche legni aggiunti da altre mod, purché le relative assi appartengano al tag. Rimane disponibile nella scheda Creativa e tramite `/give`; la texture dedicata verrà decisa in seguito. I modelli usano una texture vanilla provvisoria, senza toccare gli asset dell'autore. Attualmente rompere la base rilascia il Wheat Dough di partenza e non un nuovo item Pizza Base; raccolta e conservazione di una base stesa verranno affrontate in un passo successivo.

**Test riuscito confermato dall'autore:** stesura manuale e con mattarello dopo la correzione dell'interazione. **Da provare ora:** crafting con assi di quercia, abete, bambù, cremisi e deformate; verificare il risultato singolo, il libro delle ricette e l'assenza di variazioni nelle meccaniche di stesura.

**Test di regressione richiesti:** clic destro con mano principale vuota (anche con oggetto nella mano secondaria), mattarello sulla palla e su impasto parzialmente steso, perdita di 1 durabilità, nessuna perdita sulla base completa, rottura dopo 64 stesure, Creative vs Survival e salvataggio progressi.

## 3. Condimento con clic destro — CONFERMATO

Il giocatore tiene l'ingrediente in mano e usa clic destro sulla pizza stesa. Se l'ingrediente è valido, viene consumata una porzione e aggiunta alla composizione. Non è necessario inserire prima pomodoro, formaggio o erbe in un ordine particolare. Sono ammesse pizze inventate dal giocatore, anche senza salsa rossa.

**Proposte:** massimo 8 porzioni complessive, includendo salse; più porzioni dello stesso topping; categorie ingredienti registrate e non tutti i cibi ammessi indiscriminatamente; controllo dei contenitori restituiti.

### Tabella interazioni ipotizzata

| Interazione | Comportamento progettato |
| --- | --- |
| Clic destro con palla d'impasto su blocco | Colloca impasto per la preparazione |
| Clic destro vuoto sull'impasto | Avanza la stesura manuale |
| Clic destro con mattarello | Stende rapidamente |
| Clic destro con topping valido sulla base | Aggiunge il topping |
| Shift + clic destro a mano vuota sulla pizza condita | **Proposta:** rimuove l'ultimo topping e lo restituisce |
| Shift + clic destro a mano vuota sulla pizza senza topping | **Proposta:** raccoglie la base |
| Clic destro con pala per pizza | **Proposta:** recupero/inserimento rapido; pala non obbligatoria |
| Rottura della pizza o del blocco di supporto | Recupero dell'oggetto con dati completi, senza duplicazioni |

Le priorità esatte di Shift, della mano secondaria e dei clic sono **ancora da confermare**.

## 4. Raccolta e conservazione

Quando si raccoglie la pizza, devono sopravvivere:
- topping effettivi, quantità e ordine di inserimento se serve per rimuovere l'ultimo;
- stadio impasto/cruda/cotta;
- identità visiva della pizza e seme di distribuzione;
- dati per nomi, nutrizione e cottura.

La pizza cruda va trasportata al Pizza Oven o a un forno vanilla. Se raccolta e riposizionata, deve tornare con la **stessa composizione**; la rottura del supporto non può creare più item.

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
