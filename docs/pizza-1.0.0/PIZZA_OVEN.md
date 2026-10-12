# Pizza Oven: forno compatto e altri metodi di cottura

## Requisiti CONFERMATI

- **Pizza Oven dedicato** e possibilità di cuocere le pizze anche nei **forni vanilla esistenti** (fornace e affumicatore).
- Pizza Oven **compatto da un solo blocco (1×1×1)**, che cuoce **una pizza per volta**.
- **Specializzazione confermata:** e' un forno con logica simile alla fornace vanilla, ma **cuoce esclusivamente pizze**. Non accetta altri cibi o oggetti come input di cottura; il combustibile e' gestito separatamente con tutti i tipi vanilla validi.
- Accetta **tutti i combustibili vanilla validi**, non solo legna.
- **Le pizze non bruciano mai**, indipendentemente da tempo trascorso e tipo di forno.
- La trasformazione da cruda a cotta deve conservare ingredienti, quantità, composizione e dati della pizza.
- **Pala FACOLTATIVA — CONFERMATO:** il Pizza Oven deve funzionare sia **con** sia **senza** la pala per pizza. La pala e' una comodita' di trasporto, non un requisito generale per utilizzare il forno.
- **GUI — CONFERMATO (12 ottobre 2026):** il forno ha una propria interfaccia di gestione per inserire pizza e combustibile, seguire la cottura e recuperare la pizza. La GUI e' il normale percorso senza pala.
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
- **GUI presente e confermata:** per l'uso ordinario, il giocatore apre la GUI con l'interazione sul blocco. L'interfaccia deve permettere di gestire pizza, combustibile, avanzamento ed estrazione; disposizione esatta degli slot ancora da progettare.
- **Eccezione all'apertura della GUI — CONFERMATO:** il clic destro sul Pizza Oven con **pala occupata** tenta **l'inserimento diretto**, mentre con **pala vuota** tenta **l'estrazione diretta**, senza aprire la GUI per le interazioni riuscite. Non introdurre il requisito della pala per chi usa la GUI.
- Una BlockEntity conserva `ItemStack` intero, combustibile, energia e avanzamento; salva i dati quando il chunk si scarica.

### Interfaccia e interazioni — decisioni confermate e dettagli aperti

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

La GUI dovra' rendere chiaramente leggibili **pizza presente, stato di cottura e combustibile residuo**. **CONFERMATO:** la pala occupata inserisce direttamente la pizza; la pala vuota estrae direttamente la pizza **a qualunque stadio di cottura**, sempre senza GUI. **DA DEFINIRE:** layout esatto, numero e ruolo degli slot di input/output (mantenendo la capienza di una pizza alla volta), resa grafica e automazione hopper. Evitare di inventare un secondo item pizza dentro l'inventario durante un trasferimento con pala.

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
- GUI continua a funzionare per inserimento/estrazione senza pala; provare in multiplayer con piu' giocatori e nessuna duplicazione.

## Durate di cottura — valori PLACEHOLDER

| Sistema | Tempo proposto |
| --- | ---: |
| Pizza Oven (nessun preriscaldamento) | 20 secondi |
| Affumicatore vanilla | 30 secondi |
| Fornace vanilla | 60 secondi |

I **tempi 20/30/60 secondi sono placeholder**, non ancora bilanciati: i valori definitivi saranno decisi durante il rebalancing. **L'assenza di preriscaldamento e la gestione dell'energia del Pizza Oven sono invece confermate**, indipendentemente dai tempi.

## Automazione hopper — PROPOSTA

| Lato | Inventario |
| --- | --- |
| Sopra | Inserimento pizza cruda |
| Laterali | Inserimento combustibile |
| Sotto | **Solo estrazione pizza cotta** |
| Fronte | Uso manuale |

Nessun hopper deve poter estrarre pizza ancora cruda o parzialmente cotta. Testare hopper paralleli, rifornimento e estrazione, inclusi i remainder dei combustibili.

## Fornaci vanilla: problema tecnico da risolvere

La cottura di un item con data component personalizzati tramite **smelting ordinario** non garantisce il trasferimento automatico dei dati dall'input all'output. Per questo occorre progettare una soluzione **specifica per la pizza**, compatibile con Minecraft 26.2, Fabric, JEI, hopper e altre mod, che non alteri le ricette di oggetti estranei.

**La compatibilità è richiesta, ma non ancora implementata né verificata.** Prima della release va testata su pizza personalizzata con più condimenti: l'output deve conservare dati, nome dinamico e resa visiva.

## Ricetta per costruire il forno — PROPOSTA

Mattoni, mattoni di pietra e fornace vanilla; forma e quantità da scegliere nel bilanciamento. Non introdurre nuovi minerali o materiali obbligatori solo per costruire il forno.
