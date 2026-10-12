# Pizza Oven: forno compatto e altri metodi di cottura

## Requisiti CONFERMATI

- **Pizza Oven dedicato** e possibilità di cuocere le pizze anche nei **forni vanilla esistenti** (fornace e affumicatore).
- Pizza Oven **compatto da un solo blocco (1×1×1)**, che cuoce **una pizza per volta**.
- **Specializzazione confermata:** e' un forno con logica simile alla fornace vanilla, ma **cuoce esclusivamente pizze**. Non accetta altri cibi o oggetti come input di cottura; il combustibile e' gestito separatamente con tutti i tipi vanilla validi.
- Accetta **tutti i combustibili vanilla validi**, non solo legna.
- **Le pizze non bruciano mai**, indipendentemente da tempo trascorso e tipo di forno.
- La trasformazione da cruda a cotta deve conservare ingredienti, quantità, composizione e dati della pizza.
- **Pala FACOLTATIVA — CONFERMATO:** il Pizza Oven deve funzionare sia **con** sia **senza** la pala per pizza. La pala e' una comodita' di trasporto, non un requisito generale per utilizzare il forno.
- **GUI VANILLA DELLA FORNACE — CONFERMATO (12 ottobre 2026):** riutilizzare **aspetto e disposizione dell'interfaccia standard della fornace di Minecraft**, senza disegnare una GUI personalizzata: **slot ingresso pizza in alto a sinistra, slot combustibile in basso a sinistra e slot risultato a destra**, con **fiamma** per il combustibile e **freccia** per il progresso di cottura. La GUI e' il normale percorso senza pala.
- **Inserimento diretto con pala OCCUPATA — CONFERMATO:** se una pizza e' caricata sulla pala, usare la pala sul Pizza Oven permette di **trasferire direttamente la pizza nel forno senza passare dalla GUI**, a condizione che lo spazio pizza sia disponibile. La pala ritorna vuota; ogni dato della pizza e' preservato. Se lo spazio e' occupato o il trasferimento fallisce, la pizza rimane sulla pala. La pala non e' consumata e non perde durabilita'.
- **Estrazione rapida con pala VUOTA — CONFERMATO (12 ottobre 2026):** clic destro sul Pizza Oven tenendo una pala vuota **preleva direttamente la pizza presente senza aprire la GUI**, sia quando e' **cotta** sia quando e' **cruda o parzialmente cotta**. La pala torna occupata e il forno resta senza pizza; l'energia residua si conserva. Lo stato completo, incluso **progresso di cottura parziale**, ingredienti, divisioni e dati visivi, deve essere trasferito intatto. L'estrazione funziona indipendentemente dalla disponibilita' di combustibile; non consuma durabilita'.
- **Nessuna perdita o duplicazione:** il trasferimento forno -> pala avviene soltanto se la pala e' vuota e la pizza e' presente; lato server lo stato viene trasferito in modo atomico, senza crearne una copia. Se il forno non contiene pizza non avviene alcun prelievo. La **GUI** rimane il metodo alternativo di estrazione senza pala.
- **Pizza preparata a terra — CONFERMATO:** non si raccoglie con clic destro a mano vuota. Per toglierla da terra **si rompe**, ottenendo **l'item pizza completo**, crudo o cotto secondo lo stato, con ingredienti, coperture, suddivisioni e ogni dato di preparazione/cottura. **Non** restituire separatamente impasto e ingredienti quando una pizza completa viene rotta. La pala resta un'alternativa facoltativa per trasportarla direttamente.
- **Percorso senza pala — CONFERMATO:** preparare la pizza sul terreno -> rompere la pizza per ottenere il relativo **item completo** -> inserire l'item nel Pizza Oven. Non serve alcun gesto di raccolta a mano vuota. L'item conserva la stessa ricetta personalizzata e gli stessi dati della pizza; inserimento, cottura ed estrazione non devono azzerarli.
- **Nessun preriscaldamento — CONFERMATO:** quando c'e' una pizza cruda e combustibile/energia sufficiente, la cottura inizia subito, senza attendere che il forno raggiunga una temperatura minima.
- **Nessuno spreco di combustibile — CONFERMATO:** il burn time/energia si consuma **solo mentre la cottura procede effettivamente**; rimane congelato quando il forno e' vuoto, in attesa di una pizza, o quando la pizza e' pronta.
- **Interruzione e ripresa — CONFERMATO:** se il combustibile termina durante la preparazione, la cottura si **mette in pausa** conservando il progresso, e riprende quando viene aggiunto combustibile valido. Non riparte da zero.
- **Energia residua — CONFERMATO:** il valore residuo di un combustibile gia' attivato puo' essere riutilizzato per cuocere altre pizze, senza consumarlo durante inattivita' e senza prelevare nuove unita' di combustibile quando non necessario.

## Struttura del Pizza Oven — PROPOSTA

- Forno orientabile verso il giocatore, apertura frontale, pizza visibile all'interno.
- Modello 3D con fuoco/fumo/particelle quando è attivo.
- **GUI vanilla confermata:** per l'uso ordinario, il giocatore apre l'interfaccia della fornace vanilla. **Tre slot nella posizione originale**: pizza da cuocere (input), combustibile (fuel), pizza cotta (output), con fiamma e freccia di progressione come nella fornace vanilla. Riutilizzare risorse grafiche/layout vanilla; non creare nuovi slot o texture di interfaccia personalizzate.
- **Eccezione all'apertura della GUI — CONFERMATO:** il clic destro sul Pizza Oven con **pala occupata** tenta **l'inserimento diretto**, mentre con **pala vuota** tenta **l'estrazione diretta**, senza aprire la GUI per le interazioni riuscite. Non introdurre il requisito della pala per chi usa la GUI.
- Una BlockEntity conserva `ItemStack` intero, combustibile, energia e avanzamento; salva i dati quando il chunk si scarica.

### Interfaccia vanilla della fornace — CONFERMATO

La GUI riprende **esattamente il layout standard della fornace vanilla**: tre slot, fiamma per il combustibile e freccia per la progressione. **Non** usare la proposta precedente di uno slot pizza unico, ne' disegnare un'interfaccia apposita; usare la grafica e i controlli normali della fornace con la logica di pizza specializzata del Pizza Oven.

- **Ingresso (slot superiore sinistro):** accetta soltanto pizze **non completamente cotte** (anche con progresso parziale salvato), ottenute dal mondo o via crafting. La pizza resta qui durante la cottura e puo' essere rimossa manualmente anche prima della fine, conservando la percentuale di avanzamento nei dati dell'item. Una pizza con progresso parziale resta **l'item crudo**, non un nuovo item alimentare.
- **Combustibile (slot inferiore sinistro):** accetta tutti i combustibili vanilla validi, con il burn time residuo conservato come gia' concordato.
- **Risultato (slot destro):** quando la cottura termina, la pizza cotta con dati integri passa dall'ingresso al risultato, che puo' essere ritirato con le normali interazioni dell'inventario.
- **Una pizza in totale:** poiche' e' gia' confermata la capienza massima di una sola pizza, non ammettere contemporaneamente pizza in ingresso e risultato. Quando il risultato e' occupato, impedire un nuovo inserimento (anche tramite pala) finche' la pizza cotta non viene estratta. Questa e' una limitazione del Pizza Oven, non un cambiamento alle fornaci vanilla.
- **Pala occupata:** inserisce la pizza nel percorso di ingresso soltanto se il Pizza Oven non contiene alcuna pizza; il trasferimento non apre la GUI. **Pala vuota:** preleva direttamente la pizza da ingresso o risultato (a seconda di dove si trova), anche incompleta, senza aprire la GUI. Gli stati sono trasferiti atomicamente e il progresso parziale non viene azzerato.
- **Regole speciali gia' concordate:** cuoce solo pizze, non le brucia, non ha preriscaldamento, conserva energia residua quando inattivo, e mette in pausa la cottura senza combustibile. Queste differenze riguardano la *logica del Pizza Oven*, non il suo aspetto vanilla.

### Interazioni confermate

| Interazione | Comportamento |
| --- | --- |
| Apertura ordinaria del Pizza Oven | **CONFERMATO:** apre la **GUI** per gestione pizza, combustibile, cottura e recupero |
| Inserimento di una **pizza-item** senza pala | **CONFERMATO:** avviene tramite la **GUI**, usando l'item ottenuto rompendo una pizza formata oppure tramite crafting |
| Inserimento del combustibile | **CONFERMATO:** possibile tramite la **GUI**; tutti i combustibili vanilla validi |
| Clic destro sul forno con **pala occupata** | **CONFERMATO:** trasferimento diretto di una pizza dalla pala al forno senza GUI; la pala torna vuota, senza perdita di dati e senza usura |
| Pala occupata su forno che contiene gia' una pizza | Nessun trasferimento: pala e pizza restano invariati, senza sostituzioni o duplicazioni |
| Estrazione della pizza dalla GUI | **CONFERMATO nel principio:** il forno e' utilizzabile senza pala e la GUI consente di recuperare la pizza mantenendo i dati |
| Clic destro sul forno con **pala vuota** | **CONFERMATO:** estrae subito la pizza, anche cruda o **parzialmente cotta**, senza aprire la GUI; pala occupata, forno libero e stato di cottura trasferito integralmente |
| Rottura del forno | Applicare le normali regole di recupero degli inventari del blocco, senza perdita o duplicazione di pizza e combustibile non consumato |

La GUI dovra' rendere chiaramente leggibili **pizza presente, stato di cottura e combustibile residuo**. **CONFERMATO:** la GUI e' quella della **fornace vanilla con tre slot, fiamma e freccia**, senza interfaccia dedicata; la pala occupata inserisce direttamente la pizza e la pala vuota estrae direttamente la pizza **a qualunque stadio di cottura**, sempre senza GUI. **Ancora da implementare/verificare:** automazione hopper, sincronizzazione del progresso e compatibilita' della schermata vanilla con i dati persistenti delle pizze. Evitare di inventare un secondo item pizza dentro l'inventario durante un trasferimento con pala.

**Conservazione dei dati:** ingresso da pizza-item tramite GUI e ingresso diretto da pala occupata devono convergere nello stesso stato persistente lato server. Il trasferimento deve essere atomico: verificare disponibilita' dello spazio pizza, copiare/salvare i dati nel forno, quindi svuotare la pala; mai svuotare la pala se il forno rifiuta l'item. Analogamente, per l'uscita diretta, copiare/trasferire alla pala vuota **tutti i dati compreso l'avanzamento di cottura**, quindi liberare lo spazio pizza nel forno, senza duplicazioni. Una pizza estratta prima del completamento resta cruda/parzialmente cotta e deve poter riprendere la cottura successivamente conservando l'avanzamento; non deve trasformarsi prematuramente in pizza cotta.

Rimane valida la distinzione fondamentale: **il clic destro a mano vuota sulla pizza nel mondo non la raccoglie**. Per ottenere una pizza-item la si **rompe**; la pizza-item puo' essere inserita nella GUI senza pala.

## Combustibili

**Confermato:** compatibilità con tutti i combustibili vanilla.

**Meccanica approvata:** consumo di energia **solo con cottura effettiva**; pausa del consumo a forno vuoto o pizza pronta; memorizzazione dell'energia residua anche tra pizze; stop della cottura quando l'energia finisce e ripresa dallo stesso avanzamento al rifornimento, senza preriscaldamento.

**Dettagli tecnici da implementare/verificare (non nuove decisioni di design):**
- usare i burn time dei combustibili validi di Minecraft/Fabric 26.2, evitando liste hardcoded;
- uno slot interno per combustibile, fino allo stack consentito;
- non prelevare unita' di combustibile se non serve per una cottura effettiva;
- salvare energia residua e progresso, sospendendo il progresso quando il chunk non e' caricato;
- gestire i contenitori dei combustibili, ad esempio **secchio vuoto dopo lava**;
- preservare fuel e stato tra caricamenti, multiplayer e automazioni;
- il comportamento del combustibile del Pizza Oven e' **intenzionalmente diverso dalla combustione continua vanilla** quando la fornace resta accesa senza cuocere.

Il forno non deve continuare a cuocere una pizza dopo il 100%, perché **non deve mai bruciarsi**.

## Test obbligatori per la pala — DA IMPLEMENTARE

- Pala occupata + forno vuoto -> un inserimento, pala vuota, pizza nel forno con tutti i dati.
- Pala occupata + forno occupato -> nessuna sovrascrittura, perdita o duplicazione.
- Pala vuota + pizza cruda, parzialmente cotta o pronta -> **un'estrazione** senza GUI, pala occupata; forno libero.
- Pala vuota + forno senza pizza -> nessuna pizza creata e nessun trasferimento.
- Estrazione a meta' cottura, salvataggio/ricarica, reinserimento -> cottura riprende dallo stato persistente, senza azzerare o completare artificialmente il progresso.
- **GUI vanilla identica alla fornace:** tre slot nelle posizioni standard, fiamma e freccia; verificare che non siano introdotti slot/grafiche extra. Testare pizza parzialmente cotta prelevata dallo slot ingresso con progressi invariati, pizza pronta nel risultato, e blocco di un secondo ingresso finche' il risultato e' occupato.
- GUI continua a funzionare per inserimento/estrazione senza pala; provare in multiplayer con piu' giocatori e nessuna duplicazione.

## Durate di cottura — valori PLACEHOLDER

| Sistema | Tempo proposto |
| --- | ---: |
| Pizza Oven (nessun preriscaldamento) | 20 secondi |
| Affumicatore vanilla | 30 secondi |
| Fornace vanilla | 60 secondi |

I **tempi 20/30/60 secondi sono placeholder**, non ancora bilanciati: i valori definitivi saranno decisi durante il rebalancing. **L'assenza di preriscaldamento e la gestione dell'energia del Pizza Oven sono invece confermate**, indipendentemente dai tempi.

## Automazione hopper — CONFERMATA (12 ottobre 2026)

Il Pizza Oven supporta gli **hopper con le direzioni della fornace vanilla**, rispettando la specializzazione pizza e la capienza massima di **una pizza totale** fra ingresso e risultato.

| Lato | Comportamento confermato |
| --- | --- |
| **Sopra** | Inserisce soltanto **pizze crude** nello slot di ingresso, se il forno non contiene gia' una pizza |
| **Laterali** | Inseriscono **combustibili vanilla validi** nello slot combustibile |
| **Sotto** | Estrae **soltanto pizze completamente cotte** dallo slot di risultato |
| **Fronte** | Il giocatore usa la **GUI vanilla** o i trasferimenti diretti con la pala |

- L'hopper inferiore **non puo' estrarre pizze crude o con cottura incompleta**, anche se sono visibili/recuperabili manualmente dallo slot di ingresso.
- Se lo slot di risultato contiene una pizza, l'inserimento di un'altra pizza e' bloccato anche tramite hopper, rispettando il limite di **una sola pizza** nel forno.
- Una pizza pronta passa nello slot risultato; solo da li' l'hopper inferiore puo' prelevarla. L'output deve mantenere composizione, gusto, componenti e dati visivi.
- Automazione e interazioni del giocatore devono essere **coerenti e sicure lato server**: niente prelievi simultanei duplicati, nessuna perdita di item, nessun aggiramento dei controlli con hopper multipli.
- **Test da effettuare:** hopper sopra/lati/sotto, hopper paralleli, forno pieno, combustibile esaurito, secchio vuoto dopo lava, salvataggio/ricarica, chunk unload e test multiplayer. Il comportamento dei remainder resta quello compatibile con vanilla da verificare nell'implementazione.

### Pizza con cottura incompleta — chiarimento di gameplay

La **cottura incompleta non e' un prodotto alimentare aggiuntivo previsto**: non e' stata introdotta una nuova pizza "mezza cotta" con ricetta, nutrizione o effetti particolari. E' semplicemente lo **stato temporaneo di una pizza ancora cruda** con una parte del progresso di cottura salvato.

Estrarla manualmente dalla GUI o con pala vuota e' utile per **spostare la pizza o liberare il forno senza perdere il progresso**, come gia' approvato. Se reinserita, la cottura riprende dal valore memorizzato. Solo al completamento si trasforma nell'item pizza cotta e passa nello slot risultato. **L'automazione con hopper non gestisce il prodotto incompleto in uscita**.

## Fornaci vanilla: problema tecnico da risolvere

La cottura di un item con data component personalizzati tramite **smelting ordinario** non garantisce il trasferimento automatico dei dati dall'input all'output. Per questo occorre progettare una soluzione **specifica per la pizza**, compatibile con Minecraft 26.2, Fabric, JEI, hopper e altre mod, che non alteri le ricette di oggetti estranei.

**La compatibilità è richiesta, ma non ancora implementata né verificata.** Prima della release va testata su pizza personalizzata con più condimenti: l'output deve conservare dati, nome dinamico e resa visiva.

## Ricetta per costruire il forno — CONFERMATA (12 ottobre 2026)

**Ricetta shaped 3x3 del banco da lavoro:** una **fornace vanilla** al centro, **4 blocchi di mattoni** (`minecraft:bricks`) e **4 blocchi di mattoni di pietra** (`minecraft:stone_bricks`) negli altri otto spazi. Per una disposizione regolare e simmetrica, collocare i mattoni agli **angoli** e i mattoni di pietra nelle **quattro posizioni cardinali** intorno alla fornace:

```text
B S B
S F S
B S B
```

- **B** = blocco di mattoni (`minecraft:bricks`), **4 totali**.
- **S** = blocco di mattoni di pietra (`minecraft:stone_bricks`), **4 totali**.
- **F** = fornace vanilla (`minecraft:furnace`), **1 al centro**.
- **Risultato:** 1 Pizza Oven. Ricetta *shaped*, non shapeless; nessun materiale aggiuntivo.
- **Confermato come gameplay; DA IMPLEMENTARE:** registrare il recipe JSON per Minecraft/Fabric 26.2, sblocco libro ricette, integrazione JEI e test crafting. Non modificare gli asset creati dall'autore.
