# Italian's Delight — Stagionatura e asciugatura dei formaggi

Documento di design approvato per il branch `feature/cheeses`.

Questo documento definisce il comportamento della stagionatura di Parmigiano
Reggiano, Pecorino Romano, Gorgonzola e Provolone e dell'asciugatura della
Scamorza. Le texture e il layout grafico delle categorie JEI sono gestiti
separatamente e **non fanno parte di questo documento**.

## 1. Principi generali

La maturazione è un processo del mondo, non una proprietà persistente
dell'ItemStack.

Regole definitive:

- il progresso avanza **solo mentre il chunk che contiene il formaggio è
  caricato**;
- non vengono forzati chunk load;
- durante un chunk unload il progresso si mette semplicemente in pausa;
- dopo un riavvio del server/mondo il progresso del formaggio ancora piazzato
  viene ripristinato dal blocco o dalla block entity che lo contiene;
- **nessun dato di stagionatura/asciugatura viene scritto sull'ItemStack**;
- se una forma ancora incompleta viene rimossa dal suo supporto, torna a essere
  il normale item `fresh_*` con progresso pari a zero;
- se il rack, il formaggio piazzato, il gancio o il relativo supporto vengono
  rotti, l'eventuale progresso incompleto viene perso;
- una forma già completata è invece il normale item finale e viene recuperata
  come tale;
- non sono previste, nella prima versione, condizioni di temperatura, umidità,
  luce, bioma o strutture multiblocco.

Questa scelta è intenzionale: spostare una forma prima che sia pronta annulla
la lavorazione anche se non è realistico.

## 2. Stagionatura: `cheese_aging`

La stagionatura riguarda:

- `fresh_parmigiano_reggiano → parmigiano_reggiano`;
- `fresh_pecorino_romano → pecorino_romano`;
- `fresh_gorgonzola → gorgonzola`;
- `fresh_provolone → provolone`.

La Scamorza **non** usa questo processo.

### 2.1 Piazzamento su superfici

Le forme fresche stagionabili possono essere appoggiate direttamente nel mondo
su una superficie superiore piana e adatta a sostenerle.

Il rack è quindi utile per organizzare più forme, ma **non è obbligatorio** per
la stagionatura.

Comportamento previsto:

1. il giocatore usa una forma fresca su una superficie valida;
2. viene creata una forma visibile nel mondo; durante questa fase di sviluppo
   usa un modello tipo Cake con texture vanilla come placeholder;
3. il timer parte da zero;
4. il timer avanza lato server solo mentre il chunk è caricato;
5. al completamento la forma viene sostituita automaticamente dalla variante
   stagionata;
6. se la forma viene raccolta prima del completamento, viene restituito il
   relativo item fresco e il progresso viene perso;
7. se il blocco di supporto viene rimosso o la forma piazzata viene rotta prima
   della fine, viene droppato l'item fresco senza progresso.

Non viene salvato alcun dato aggiuntivo sull'item restituito.

### 2.2 Cheese Aging Rack / Ripiano di Stagionatura

Viene introdotto un unico blocco dedicato alla disposizione ordinata delle
forme.

Design concordato:

- aspetto generale ispirato al concetto di scaffale caseario della mod
  **Fromage**, ma con modello e asset originali di Italian's Delight;
- il rack occupa **esattamente un blocco 1×1×1**;
- sono presenti **due ripiani**, non quattro:
  - un ripiano inferiore a livello del pavimento;
  - un ripiano superiore circa a metà altezza del blocco;
- ogni ripiano possiede **un solo slot**, quindi il rack contiene al massimo
  **2 forme**;
- i due slot sono completamente indipendenti e possiedono timer separati;
- le forme sono visibili fisicamente sul rispettivo ripiano;
- nessuna GUI del blocco;
- nella prima implementazione i formaggi visualizzati nel rack e quelli
  appoggiati direttamente su superfici normali usano un **modello tipo torta
  con texture vanilla della Cake come placeholder**;
- i placeholder Cake usano UV laterali esplicite coerenti con il modello
  vanilla (`[1, 8, 15, 16]`) per evitare lati trasparenti o mancanti;
- il ripiano superiore e il relativo formaggio sono posizionati leggermente
  sotto la metà superiore del blocco per lasciare spazio visivo sopra la forma;
- il rack deve avere una variante per **ogni famiglia di legno vanilla**:
  Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Pale Oak,
  Bamboo, Crimson e Warped;
- ogni variante del rack è craftabile con **4 staccionate della stessa
  famiglia + 1 blocco di assi della stessa famiglia**, con schema:

  ```text
  S S
   B
  S S
  ```

  dove `S` è la staccionata relativa e `B` è il blocco di assi
  (`*_planks`) della stessa famiglia, compresi Bamboo, Crimson e Warped.

Interazione prevista:

- click destro con una forma fresca compatibile sul ripiano inferiore o
  superiore: inserisce la forma nello slot di quel ripiano;
- interazione a mano vuota con un ripiano occupato: rimuove la forma presente
  in quello specifico slot;
- un ripiano occupato può essere svuotato con click destro **indipendentemente
  dall'oggetto tenuto in mano**;
- con la mano vuota il formaggio viene restituito normalmente al giocatore;
- con la mano occupata, il formaggio rimosso cerca prima uno **stack dello
  stesso item con spazio disponibile**, poi il primo slot completamente libero;
  se non esiste spazio viene droppato a terra, senza consumare o sostituire
  l'oggetto tenuto in mano;
- rimuovere una forma incompleta restituisce l'item fresco e azzera il
  progresso;
- una forma pronta viene recuperata come item finale.

Rottura del rack:

- il blocco restituisce normalmente il proprio drop;
- ogni forma incompleta viene restituita come corrispondente item fresco;
- il progresso di tutte le forme incomplete viene perso;
- ogni forma già completata viene restituita come prodotto finale;
- non deve essere possibile duplicare né perdere forme a causa della rottura.

### 2.3 Stato nel mondo

Il progresso necessario al funzionamento è salvato **solo nel mondo**.

Concettualmente ogni posizione contiene:

```text
item/processo corrente
elapsed_ticks
```

Non esiste un campo `aging_progress` o equivalente sull'ItemStack.

Il server è autoritativo. Il client riceve soltanto lo stato necessario a
renderizzare correttamente le forme fresche/finali e gli eventuali cambi di
stato.

## 3. Asciugatura: `cheese_drying`

La Scamorza usa una meccanica separata:

```text
fresh_scamorza
    ↓ asciugatura appesa
scamorza
```

Non può essere stagionata appoggiandola su una superficie e non occupa uno slot
del Cheese Aging Rack.

### 3.1 Cheese Hook / Gancio per Formaggi

Il sistema di formaggi appesi usa un gancio metallico montato sotto una
superficie solida. Il riferimento concettuale è la meccanica dei ganci della
mod **Butchery**, ma modello, texture e implementazione sono originali di
Italian's Delight.

Il gancio è destinato a **Scamorza e Provolone**:

- `fresh_scamorza → scamorza` continua a usare la ricetta
  `cheese_drying`;
- `fresh_provolone → provolone` resta una ricetta `cheese_aging`, ma il
  Cheese Hook diventa un supporto fisico previsto per la sua maturazione.

Design previsto:

- il gancio viene piazzato esclusivamente sotto una superficie superiore valida;
- la curva dell'uncino viene orientata verso il giocatore al momento del
  piazzamento, con stato orizzontale `facing` N/E/S/W; il mapping del modello
  richiede direttamente `context.getHorizontalDirection()`, senza rotazione
  aggiuntiva di 180°;
- il modello dell'uncino non usa più una semplice piega a 90°: il profilo è
  spezzato/curvo e il **punto più basso è centrato sull'asse del blocco**, in
  modo che il formaggio penda sotto il centro del supporto;
- il punto superiore dei modelli appesi è stato abbassato fino a coincidere con
  il punto più basso dell'uncino; i formaggi possono quindi estendersi
  visivamente nel blocco sottostante;
- un gancio contiene al massimo un formaggio;
- sono agganciabili `fresh_scamorza`, `scamorza`,
  `smoked_scamorza`, `fresh_provolone` e `provolone`;
- le versioni mature e la **Scamorza Affumicata** possono quindi essere
  riappese come decorazione senza avviare un nuovo timer;
- click destro su un gancio occupato rimuove sempre il formaggio, anche con un
  altro oggetto in mano; con mano occupata il formaggio va nel primo slot
  completamente libero dell'inventario, oppure viene droppato se l'inventario
  è pieno;
- Scamorza, Scamorza Affumicata e Provolone vengono renderizzati verticalmente
  sotto il gancio; la Scamorza Affumicata riusa la geometria della Scamorza ma
  mantiene la propria texture scura;
- il modello della Scamorza è ispirato alla forma a doppio bulbo legata al
  collo; quello del Provolone alla forma a pera/goccia con legatura superiore;
- i modelli appesi usano texture 16×16 **dedicate**, non copie dirette degli
  item: mantengono il pattern adatto alle superfici 3D ma sono state ricolorate
  verso la palette dei rispettivi item, così fresh, mature e affumicata sono
  chiaramente distinguibili senza deformare la grafica dell'inventario;
- i diversi cuboidi che compongono Scamorza e Provolone usano una **mappatura
  UV continua**: ogni strato campiona soltanto la propria porzione della stessa
  texture, invece di ripetere l'intera PNG su ogni cuboide;
- mentre un formaggio è appeso, il blocco immediatamente sotto l'uncino viene
  riservato da un **blocco tecnico invisibile, senza collisione e non
  sostituibile**; in questo modo non è possibile piazzare blocchi dentro il
  volume visivo del formaggio, che supera l'altezza del blocco dell'uncino;
- il blocco tecnico viene rimosso automaticamente quando il formaggio viene
  staccato o l'uncino viene distrutto;
- il timer appartiene esclusivamente alla BlockEntity del gancio;
- al completamento la variante fresh viene sostituita visivamente e
  logicamente dalla rispettiva variante finale;
- la rimozione anticipata restituisce l'item fresco e azzera il timer;
- rompere il gancio o il suo supporto prima della fine restituisce il prodotto
  fresco senza progresso;
- rompere/rimuovere un prodotto già pronto restituisce la variante finale.

**Stato corrente:** blocco, orientamento, BlockEntity, interazioni,
persistenza, timer, trasformazioni e rendering di Scamorza/Provolone sono
implementati. Il Provolone continua a poter usare anche rack e superfici
normali.

Il `minecraft:lead` resta parte della forma durante l'asciugatura. Viene
recuperato successivamente tramite le ricette già definite al Cutting Board.

## 3.2 Ricette dei supporti

### Cheese Aging Rack

Schema per tutte le 12 varianti:

```text
S S
 B
S S
```

- `S`: staccionata della stessa famiglia del rack;
- `B`: blocco di assi (`*_planks`) della stessa famiglia;
- output: 1 Cheese Aging Rack della famiglia corrispondente.

### Cheese Hook

```text
C
P
```

- `C`: Iron Chain (`minecraft:iron_chain`);
- `P`: Iron Nugget;
- output: 1 Cheese Hook.

Le ricette sono normali `minecraft:crafting_shaped` e vengono quindi
mostrate automaticamente da JEI insieme alle altre ricette del crafting table.

## 4. Tempi approvati

I tempi seguenti sono quelli approvati per la prima implementazione.

| Processo | Input | Output | Tick | Tempo reale |
|---|---|---|---:|---:|
| Drying | `fresh_scamorza` | `scamorza` | 12.000 | 10 min |
| Aging | `fresh_gorgonzola` | `gorgonzola` | 48.000 | 40 min |
| Aging | `fresh_provolone` | `provolone` | 72.000 | 60 min |
| Aging | `fresh_pecorino_romano` | `pecorino_romano` | 96.000 | 80 min |
| Aging | `fresh_parmigiano_reggiano` | `parmigiano_reggiano` | 120.000 | 100 min |

Questi valori costituiscono la configurazione iniziale approvata. Possono
essere rivisti soltanto in seguito a prove di bilanciamento in-game.

## 5. Ricette data-driven

Si prevedono **due tipi di ricetta distinti**.

### 5.1 `italiansdelight:cheese_aging`

Usato da Parmigiano Reggiano, Pecorino Romano, Gorgonzola e Provolone.

Schema previsto:

```json
{
  "type": "italiansdelight:cheese_aging",
  "ingredient": "italiansdelight:fresh_parmigiano_reggiano",
  "result": {
    "id": "italiansdelight:parmigiano_reggiano"
  },
  "agingtime": 120000
}
```

Cartella prevista:

`src/main/resources/data/italiansdelight/recipe/aging/`

Il supporto fisico (superficie o rack) non è un ingrediente della ricetta: la
ricetta descrive la trasformazione del formaggio.

### 5.2 `italiansdelight:cheese_drying`

Usato dalla Scamorza.

Schema previsto:

```json
{
  "type": "italiansdelight:cheese_drying",
  "ingredient": "italiansdelight:fresh_scamorza",
  "result": {
    "id": "italiansdelight:scamorza"
  },
  "dryingtime": 12000
}
```

Cartella prevista:

`src/main/resources/data/italiansdelight/recipe/drying/`

Anche in questo caso il gancio è il supporto che esegue il processo, non un
ingrediente consumato.

## 6. Integrazione JEI

Per la fase corrente JEI usa **una sola categoria, Cheese Aging**, basata sulla
texture `assets/italiansdelight/textures/gui/jei/aging.png` fornita dal
developer.

La categoria aggrega entrambe le sorgenti dati:

- ricette `cheese_aging`: Parmigiano Reggiano, Pecorino Romano, Gorgonzola
  e **Provolone**;
- ricette `cheese_drying`: per ora anche la **Scamorza** viene mostrata nella
  stessa GUI.

I due tipi gameplay restano separati: questa unificazione riguarda soltanto la
presentazione JEI. Una categoria Cheese Drying separata potrà essere introdotta
più avanti quando il Cheese Hook avrà la propria presentazione definitiva.

Dati minimi da mostrare nella categoria:

- input;
- output;
- durata reale derivata dai tick della ricetta.

Per le ricette di aging l'interfaccia rappresenta la trasformazione
fresco → stagionato; il rack non è un ingrediente consumabile.

La Scamorza continua tecnicamente a essere una ricetta di drying anche se,
temporaneamente, viene visualizzata nello stesso pannello.

**Le texture, il background, le coordinate degli slot, le icone e le animazioni
JEI sono gestite manualmente dal developer e non devono essere generate o
sovrascritte automaticamente.**

## 7. Persistenza, chunk e reset

Comportamento definitivo:

| Evento | Risultato |
|---|---|
| Chunk caricato | Il timer avanza |
| Chunk scaricato | Il timer si ferma |
| Chunk ricaricato | Riprende dal valore salvato nel mondo |
| Riavvio server/mondo | Riprende dal valore salvato nel mondo |
| Forma incompleta rimossa | Drop fresco, progresso 0 |
| Rack rotto | Forme incomplete fresche, progresso 0 |
| Formaggio piazzato rotto | Item fresco, progresso 0 |
| Supporto del formaggio rotto | Item fresco, progresso 0 |
| Gancio rotto | Scamorza fresca, progresso 0 |
| Supporto del gancio rotto | Scamorza fresca, progresso 0 |
| Processo completato | L'item/stato diventa il prodotto finale |
| Prodotto finale rimosso | Drop del prodotto finale |

Non si effettua alcun calcolo del tempo trascorso mentre il chunk era scaricato.

## 8. Casi di test obbligatori

Prima di considerare completa la fase di stagionatura devono essere verificati:

- due forme diverse nello stesso rack con timer indipendenti;
- inserimento/rimozione indipendente dal ripiano inferiore e superiore;
- verifica delle dimensioni 1×1×1 e della posizione dei due ripiani;
- verifica di tutte le varianti di legno registrate;
- stagionatura di una forma su una normale superficie piana;
- pausa e ripresa dopo unload/reload del chunk;
- persistenza del timer dopo riavvio del mondo senza rimuovere la forma;
- reset completo dopo rimozione di una forma incompleta;
- reset completo dopo rottura del rack;
- reset completo dopo rottura della superficie di supporto;
- trasformazione automatica al raggiungimento del tempo richiesto;
- drop corretto di forme già mature;
- Scamorza visibile e appesa al gancio;
- reset della Scamorza dopo rimozione/rottura anticipata;
- completamento della Scamorza dopo 12.000 tick;
- assenza di duplicazioni o perdite anomale in multiplayer.

## 9. Fuori scope della prima versione

Non vengono introdotti ora:

- temperatura;
- umidità;
- controllo del bioma;
- controllo della luce;
- ventilazione;
- muffe ambientali;
- strutture multiblocco;
- qualità o livelli multipli di stagionatura;
- progresso persistente sugli ItemStack;
- avanzamento offline dei chunk.

Questi elementi potranno essere valutati in futuro senza cambiare la struttura
base delle ricette `cheese_aging` e `cheese_drying`.


## 10. Stato attuale dell'implementazione

### Completato

- registrati `cheese_aging` e `cheese_drying` con serializer sincronizzati;
- aggiunte le ricette data-driven e i tempi approvati per i cinque formaggi;
- Parmigiano Reggiano, Pecorino Romano, Gorgonzola e Provolone sono
  stagionabili direttamente su superfici solide;
- il progresso delle forme piazzate è salvato solo nel mondo e non sugli item;
- rimozione anticipata o rottura del supporto restituiscono il prodotto fresco
  con progresso azzerato;
- implementato il Cheese Aging Rack 1×1×1 con **2 ripiani / 2 slot totali**;
- ogni slot del rack ha tipo, timer e stato mature indipendenti;
- implementate 12 varianti di legno vanilla: Oak, Spruce, Birch, Jungle,
  Acacia, Dark Oak, Mangrove, Cherry, Pale Oak, Bamboo, Crimson e Warped;
- rimozione dal rack possibile con mano vuota o con qualsiasi oggetto in mano;
- con mano occupata il formaggio viene spostato nel primo slot libero
  dell'inventario;
- i formaggi nel rack e sulle superfici normali usano temporaneamente il
  modello/texture vanilla della Cake;
- corretto il mapping UV dei placeholder Cake per renderizzare anche i lati;
- categoria JEI Aging funzionante e unificata temporaneamente anche per la
  Scamorza;
- compatibilità opzionale con Fromage predisposta tramite common tag, senza
  dipendenza obbligatoria.

### Ancora da implementare o completare

- sistemare le **particelle emesse dal Cheese Hook al momento della rottura**;
  il problema è noto ma va corretto in una fase successiva, senza modifiche
  nell'implementazione corrente;
- verificare in-game proporzioni, hitbox e leggibilità dei modelli appesi di
  Scamorza e Provolone;
- il Cheese Hook è craftabile verticalmente con **1 Iron Chain + 1 Iron Nugget**:

  ```text
  C
  P
  ```

  dove `C` = `minecraft:iron_chain` e `P` = `minecraft:iron_nugget`;
- ricette di crafting dei rack per tutte le varianti di legno: **completate**;
- modelli definitivi dei rack e dei formaggi stagionati al posto dei placeholder
  Cake, se desiderati;
- verifica in-game definitiva del fix UV del formaggio sul ripiano superiore;
- test completi di persistenza dopo riavvio, chunk unload/reload, multiplayer,
  rottura del rack e inventario pieno;
- bilanciamento finale di tempi, rese, valori nutritivi e stack;
- build completa e test su server dedicato.


## 11. Ottimizzazioni tecniche

Revisione eseguita sull'intero codice Java del branch `feature/cheeses`.

Interventi applicati:

- il Cheese Hook non ricontrolla più a ogni tick il blocco tecnico che riserva
  lo spazio del formaggio: la verifica avviene all'inserimento e una sola volta
  dopo il caricamento del chunk/mondo;
- la Cheese Vat prova prima a riutilizzare in sicurezza la ricetta normale già
  in lavorazione, evitando nella maggior parte dei tick una scansione completa
  di tutte le ricette server;
- la ricerca whey conservava già un hint della ricetta corrente e non è stata
  duplicata;
- rack e formaggi piazzati usano già `RecipeManager.CachedCheck` e loop di
  dimensione fissa, quindi non sono stati complicati con cache aggiuntive che
  avrebbero rischiato di diventare stale dopo un datapack reload;
- il sistema del sale usa già il controllo della palette delle chunk section
  prima della scansione 16×16×16 e non forza il caricamento dei chunk: non sono
  state introdotte ulteriori cache globali inutili.

Principio adottato: ottimizzare le operazioni eseguite ogni tick senza ridurre
l'affidabilità del salvataggio del progresso o modificare il comportamento
data-driven delle ricette.
