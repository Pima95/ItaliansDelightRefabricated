# Pizza Oven: forno compatto e altri metodi di cottura

> **Aggiornamento del 17 ottobre 2026:** la fase 4A era un prototipo con progresso della pizza salvato nell'item. **Quella parte è stata superata e rimossa su richiesta dell'autore**. Il progresso è ora solo nella BlockEntity del forno, che lo resetta se la pizza viene rimossa. `PizzaItemData` conserva esclusivamente composizione e flag mozzarella legacy, come nei salvataggi prima del prototipo. [Test attuali](PIZZA_OVEN_IMPLEMENTATION_TESTS.md).


## Implementazione FASE 4B — 17 ottobre 2026

> **Revisione post-test PUBBLICATA, DA COMPILARE E RITESTARE MC 26.2.** L'autore ha già confermato avvio e cottura del forno; il nuovo codice corregge fiamma, reset progresso, numero di ciotole e raccolta con hopper. [Test mirati](PIZZA_OVEN_IMPLEMENTATION_TESTS.md). Grafica del blocco provvisoria, fornace vanilla.

- `PizzaOvenBlock` (1×1, direzione salvata, `lit` gestito dal ticker), `PizzaOvenBlockEntity` (3 slot, energia residua, data GUI a 4 valori, caricamento/salvataggio, sided hopper, cottura delle sole pizze), `PizzaOvenMenu` (menu server associato al **tipo vanilla FURNACE**, per usare il normale `FurnaceScreen` client).
- Combustibili dalla registry `level.fuelValues()`. **AGGIORNATO:** una volta acceso, il combustibile continua a consumarsi anche senza lavoro, con la barra della fiamma in calo come vanilla. Quando il forno è vuoto non accende un'altra unità. `CookTime` appartiene al forno, non alla pizza.
- Al completamento `PizzaBaking.finish(input, cookTime)` produce una sola `cooked_pizza` conservandone tutti gli ingredienti. Conta le ciotole **per applicazione originale**: due salse separate sulle due metà danno **2 ciotole**, eventualmente un unico stack da 2. Con hopper direttamente sotto il forno, le ciotole vengono inserite nel suo inventario, mentre la pizza cotta è estratta dall'output con il normale comportamento hopper; senza spazio, i contenitori rimanenti sono espulsi come il Cooking Pot.
- Caricamento da hopper sopra `raw_pizza`; hopper laterali solo combustibili; hopper sotto soltanto `cooked_pizza` dallo slot risultato. Inserimento e output contemporanei non ammessi.
- **La pala non è ancora realizzata come item di trasporto**: le interazioni dirette con pala vuota/occupata rimangono specifiche approvate da implementare in una fase successiva. La GUI funziona senza pala.
- **Non sono ancora disponibili**: cottura della pizza personalizzata in fornace/affumicatore vanilla, pizza cotta piazzabile/servita in quattro spicchi, nomi dinamici in gioco e texture dedicate.

Le sezioni successive descrivono le **specifiche finali** (alcune già realizzate, altre ancora da testare/implementare). Le affermazioni storiche «ancora da implementare» vanno interpretate alla luce di questo stato 4B.

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
- **Estrazione rapida con pala VUOTA — SPECIFICA AGGIORNATA 17 ottobre:** l'interazione futura deve prelevare la pizza cruda/parzialmente cotta o il prodotto finito senza GUI; **il progresso di cottura NON deve essere trasferito sulla pala**, ma azzerarsi quando la pizza lascia l'input, come una fornace vanilla. La pala resta riutilizzabile e non obbligatoria; non è ancora implementata.
- **Nessuna perdita o duplicazione:** il trasferimento forno -> pala avviene soltanto se la pala e' vuota e la pizza e' presente; lato server lo stato viene trasferito in modo atomico, senza crearne una copia. Se il forno non contiene pizza non avviene alcun prelievo. La **GUI** rimane il metodo alternativo di estrazione senza pala.
- **Pizza preparata a terra — CONFERMATO:** non si raccoglie con clic destro a mano vuota. Per toglierla da terra **si rompe**, ottenendo **l'item pizza completo**, crudo o cotto secondo lo stato, con ingredienti, coperture, suddivisioni e ogni dato di preparazione/cottura. **Non** restituire separatamente impasto e ingredienti quando una pizza completa viene rotta. La pala resta un'alternativa facoltativa per trasportarla direttamente.
- **Percorso senza pala — CONFERMATO:** preparare la pizza sul terreno -> rompere la pizza per ottenere il relativo **item completo** -> inserire l'item nel Pizza Oven. Non serve alcun gesto di raccolta a mano vuota. L'item conserva la stessa ricetta personalizzata e gli stessi dati della pizza; inserimento, cottura ed estrazione non devono azzerarli.
- **Nessun preriscaldamento — CONFERMATO:** quando c'e' una pizza cruda e combustibile/energia sufficiente, la cottura inizia subito, senza attendere che il forno raggiunga una temperatura minima.
- **Fuoco come fornace vanilla — CONFERMATO (17 ottobre):** una volta acceso un combustibile, la fiamma continua a consumarlo anche a forno vuoto/pizza pronta. La barra GUI diminuisce fino a 0 e il blocco `lit` si spegne al termine. Non si consuma una nuova unità di combustibile finché non serve avviare altra cottura.
- **Progresso come fornace vanilla — CONFERMATO (17 ottobre):** `CookTime` è conservato soltanto dal forno mentre la pizza rimane nello slot; se l'input è tolto o sostituito, il progresso riparte da zero. Se il fuoco manca, il contatore interno decresce gradualmente. Chiudere la GUI non azzera il forno.
- **Energia del combustibile acceso — AGGIORNATA:** dopo l'accensione il burn time residuo **non è congelato**, neppure quando la pizza è pronta. Il forno non avvia nuovo fuel da vuoto; il comportamento è quello della fornace vanilla.

## Struttura del Pizza Oven — PROPOSTA

- Forno orientabile verso il giocatore, apertura frontale, pizza visibile all'interno.
- Modello 3D con fuoco/fumo/particelle quando è attivo.
- **GUI vanilla confermata:** per l'uso ordinario, il giocatore apre l'interfaccia della fornace vanilla. **Tre slot nella posizione originale**: pizza da cuocere (input), combustibile (fuel), pizza cotta (output), con fiamma e freccia di progressione come nella fornace vanilla. Riutilizzare risorse grafiche/layout vanilla; non creare nuovi slot o texture di interfaccia personalizzate.
- **Eccezione all'apertura della GUI — CONFERMATO:** il clic destro sul Pizza Oven con **pala occupata** tenta **l'inserimento diretto**, mentre con **pala vuota** tenta **l'estrazione diretta**, senza aprire la GUI per le interazioni riuscite. Non introdurre il requisito della pala per chi usa la GUI.
- Una BlockEntity conserva `ItemStack` intero, combustibile, energia e avanzamento; salva i dati quando il chunk si scarica.

### Interfaccia vanilla della fornace — CONFERMATO

La GUI riprende **esattamente il layout standard della fornace vanilla**: tre slot, fiamma per il combustibile e freccia per la progressione. **Non** usare la proposta precedente di uno slot pizza unico, ne' disegnare un'interfaccia apposita; usare la grafica e i controlli normali della fornace con la logica di pizza specializzata del Pizza Oven.

- **Ingresso (slot superiore sinistro):** accetta solo `raw_pizza`, ottenuta dal mondo o tramite crafting futuro. Il progresso appartiene esclusivamente al forno e non viene salvato nell'item: se tolta dall'input prima della fine, la pizza rimane cruda e **ricomincia da 0 quando reinserita**. Nessuna pizza parzialmente cotta è un item diverso.
- **Combustibile (slot inferiore sinistro):** accetta tutti i combustibili vanilla validi, con il burn time residuo conservato come gia' concordato.
- **Risultato (slot destro):** quando la cottura termina, la pizza cotta con dati integri passa dall'ingresso al risultato, che puo' essere ritirato con le normali interazioni dell'inventario.
- **Una pizza in totale:** poiche' e' gia' confermata la capienza massima di una sola pizza, non ammettere contemporaneamente pizza in ingresso e risultato. Quando il risultato e' occupato, impedire un nuovo inserimento (anche tramite pala) finche' la pizza cotta non viene estratta. Questa e' una limitazione del Pizza Oven, non un cambiamento alle fornaci vanilla.
- **Pala occupata:** inserisce la pizza nel percorso di ingresso soltanto se il Pizza Oven non contiene alcuna pizza; il trasferimento non apre la GUI. **Pala vuota:** preleva direttamente la pizza da ingresso o risultato (a seconda di dove si trova), anche incompleta, senza aprire la GUI. Gli stati sono trasferiti atomicamente e il progresso parziale non viene azzerato.
- **Regole confermate/aggiornate:** cuoce solo pizze, non brucia le pizze, non necessita preriscaldamento. Fiamma e reset dell'avanzamento seguono **Minecraft vanilla**; solo la conversione dati pizza e l'output delle ciotole sono specializzati.

### Interazioni confermate

| Interazione | Comportamento |
| --- | --- |
| Apertura ordinaria del Pizza Oven | **CONFERMATO:** apre la **GUI** per gestione pizza, combustibile, cottura e recupero |
| Inserimento di una **pizza-item** senza pala | **CONFERMATO:** avviene tramite la **GUI**, usando l'item ottenuto rompendo una pizza formata oppure tramite crafting |
| Inserimento del combustibile | **CONFERMATO:** possibile tramite la **GUI**; tutti i combustibili vanilla validi |
| Clic destro sul forno con **pala occupata** | **CONFERMATO:** trasferimento diretto di una pizza dalla pala al forno senza GUI; la pala torna vuota, senza perdita di dati e senza usura |
| Pala occupata su forno che contiene gia' una pizza | Nessun trasferimento: pala e pizza restano invariati, senza sostituzioni o duplicazioni |
| Estrazione della pizza dalla GUI | **CONFERMATO nel principio:** il forno e' utilizzabile senza pala e la GUI consente di recuperare la pizza mantenendo i dati |
| Clic destro sul forno con **pala vuota** (futuro) | Preleva cruda/parzialmente cotta/cotta, preserva ingredienti; **NON il progresso**: reset all'estrazione come vanilla |
| Rottura del forno | Applicare le normali regole di recupero degli inventari del blocco, senza perdita o duplicazione di pizza e combustibile non consumato |

La GUI vanilla mostra pizza, fiamma e freccia del contatore **interno del forno**. I normali hopper (sopra input, lati fuel, sotto output) e il trasferimento diretto delle ciotole all'hopper sotto sono implementati, da ritestare con la nuova build. **La pala non è ancora implementata**, dunque i clic diretti con pala restano specifiche future e il `CookTime` non potrà mai essere trasferito all'item.

**Conservazione dei dati:** trasferimenti GUI e futuri trasferimenti con pala devono preservare composizione, quarti e ItemStack originali dei topping, ma **non** il `CookTime`: il progresso resta nel forno e si resetta quando la pizza è estratta. La pala deve essere svuotata soltanto a trasferimento riuscito e non duplicare il contenuto.

Rimane valida la distinzione fondamentale: **il clic destro a mano vuota sulla pizza nel mondo non la raccoglie**. Per ottenere una pizza-item la si **rompe**; la pizza-item puo' essere inserita nella GUI senza pala.

## Combustibili

**Confermato:** compatibilità con tutti i combustibili vanilla.

**Meccanica approvata/aggiornata 17 ottobre:** una volta avviato, il combustibile brucia continuamente come vanilla, non è congelato nel forno vuoto e la fiamma GUI cala; il forno non attiva nuovo fuel a vuoto. Il contatore `CookTime` è mantenuto dalla macchina (non dall'item), diminuisce senza fuoco e si resetta quando cambia l'input.

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
- Pala vuota + pizza cruda, parzialmente cotta o pronta -> un'estrazione senza GUI, pala occupata; forno libero e **progresso non trasferito** sull'item.
- Pala vuota + forno senza pizza -> nessuna pizza creata e nessun trasferimento.
- Estrazione a metà cottura con pala (futura): la pizza conserva condimenti, ma perde il progresso. Reinserimento dopo salvataggio/ricarica -> cottura da zero, come nella fornace vanilla.
- **GUI vanilla identica alla fornace:** tre slot, fiamma e freccia; quando la pizza parzialmente cotta è estratta la freccia si azzera, l'item resta crudo. Il risultato è una pizza cotta non ancora piazzabile, da testare visivamente quando esisterà il relativo blocco/tooltip.
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
- **Test da effettuare:** hopper sopra/lati/sotto, due applicazioni distinte di salsa in due metà -> 2 ciotole (non una), inserimento delle ciotole nell'hopper sotto insieme a `cooked_pizza`, overflow come drop; secchio vuoto lava, gestione input/output concorrente, chunk unload, GUI e multiplayer.

### Pizza con cottura incompleta — chiarimento di gameplay

**Cottura incompleta:** non esiste un prodotto alimentare diverso dalla pizza cruda. Finché resta nell'ingresso del Pizza Oven, la pizza ha una percentuale di cottura **solo nel contatore del forno**. Se la si preleva, l'avanzamento viene azzerato e la `raw_pizza` non contiene alcun dato di progresso.

Estrarla manualmente dalla GUI (o con pala vuota quando sarà implementata) serve a liberare il forno o recuperare una pizza cruda. **Il progresso di cottura viene perso all'estrazione.** La nuova cottura parte da zero all'inserimento, come in una fornace vanilla. L'hopper sotto non può prelevare pizza incompleta.

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
