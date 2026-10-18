# Pizza Update 1.0.0 — Fase 4D: pala per pizza

> **Codice pubblicato su `feature/pizza-1.0.0`; build Java 25 e verifica in gioco ancora da eseguire.**
> Test dei nomi e tooltip (fase precedente) confermati funzionanti dall'autore il 17 ottobre 2026.

## Implementazione e decisioni già rispettate

- Nuovo item `italiansdelight:pizza_peel` in `ModItems`, nello stesso creative tab dei tool, **stack massimo 1 e nessuna durabilità**.
- **Seconda correzione grafica del 18 ottobre 2026 — DA VERIFICARE VISIVAMENTE:** dopo il test dell'autore il modello a **7 elementi** è stato sostituito (era proporzionato e UV-mappato male e veniva impugnato fuori dalla mano). Il nuovo `models/item/pizza_peel.json` usa **3 cuboidi definiti dalle vere isole della texture atlas 64×64 originale**: una sola lama metallica (pixel 0–15, 0–25), manico tutto in legno (pixel 16–23, 4–27), raccordo metallico, bordi metalizzati dalla texture originale (in seguito rimappati per rispettare la densità uniforme dei pixel). Il manico non usa più una striscia grigia sui fianchi. Display in prima/terza persona ispirati al **tridente vanilla** (orientamenti delle mani), con offset e dimensioni riadattati al nostro modello a cuboidi: non si usa il renderer speciale tridente. **Nessuna texture PNG modificata**. L'asset `peel_pizza_2.png` resta conservato.
- **Terza correzione UV del 18 ottobre 2026 — pubblicata, da collaudare:** lo screenshot dell'autore mostra pixel allungati. L'analisi delle **dimensioni reali dei cuboidi rispetto ai texel campionati** ha individuato: (a) manico frontale 8×24 texel su una faccia di 2×13,5 unità (distorsione 2,25:1); (b) lato del raccordo 1×4 texel su 2×2 unità (4:1) e sommità 13×2 texel su 2,7×2 unità (4,81:1); (c) lato della lama 1×22 texel su 1×13 unità (1,69:1). Ora le UV sono basate sulla **densità di circa 2 texel per unità in entrambe le direzioni**: manico frontale (pixel 18–21, 4–27), lato del manico (18–20, 4–27), raccordo (pixel metallici nell'area 0–5, 0–3), lama principale invariata (0–15, 0–25), lato lama (14–15, 0–25), bordo lama (0–15, 0–1). Tutte le nuove aree della texture sono state verificate **100% opache** e la massima anisotropia residua sulle 18 facce è **1,13:1**, rispetto a **4,81:1** prima. **Nessun cambio a geometria, display transforms, PNG o logica di trasporto.** Test visivo ancora pendente.
- Data component `italiansdelight:peel_cargo`, assente per pala vuota. Per pala occupata conserva **esattamente un ItemStack pizza cruda o cotta** con i suoi componenti originali (composizione, ID canonici, ingredienti, applicazioni, maschere, divisioni, orientamento, seed grafico, eventuale nome custom). Una pala occupata rimane un unico item nell'inventario.
- Clic destro con pala **vuota** su una pizza cruda *interamente stesa* prende la pizza, con gli ingredienti e senza drop aggiuntivi; il blocco sparisce. La palla o base non ancora stesa resta nel mondo.
- Clic destro con pala vuota su pizza cotta ancora **integra (4/4)** la raccoglie. Pizza con 1–3 spicchi già prelevati **non è caricabile** sulla pala: rimane servibile/rompibile secondo la fase 4C, senza ricreare i quarti consumati.
- Clic destro con pala **occupata** sulla faccia superiore di un supporto valido e libero posiziona la pizza con la sua BlockEntity e libera la pala soltanto dopo il successo. Funzionamento analogo al piazzamento di `raw_pizza` e `cooked_pizza`; non altera gli spicchi o le superfici ammesse.
- Clic destro sul **Pizza Oven** con pala occupata contenente pizza CRUDA trasferisce la pizza direttamente nello slot ingresso, senza aprire GUI. Rifiuta forno con input/output occupato e pizza già cotta. La pala rimane carica sui trasferimenti rifiutati.
- Clic destro sul **Pizza Oven** con pala VUOTA preleva la pizza cotta dallo slot uscita, altrimenti la pizza cruda dallo slot ingresso (anche se la cottura era già avviata). **Il progresso parziale si azzera all'estrazione**, perché appartiene al forno e non alla pizza: regola vanilla confermata nell'aggiornamento del 17 ottobre. Combustibile acceso continua a consumarsi.
- La GUI del forno resta usabile con mano vuota o altri item; **la pala è facoltativa**. Non sono state cambiate le automazioni con hopper, la cottura, i contenitori o la nutrizione.
- Nessuna ricetta di crafting aggiunta: **costo e schema della pala ancora da definire con l'autore**. Per ora ottenere la pala dal creative tab o con `/give @s italiansdelight:pizza_peel`. Nessun bonus da arma: è una funzionalità futura da definire.
- Drop con Q, raccolta da un altro giocatore, despawn, lava/fuoco/cactus/esplosioni restano **comportamenti vanilla**. Se la pala come item viene distrutta/despawna, anche la pizza al suo interno viene persa, senza rimborsi.

## Fase 4D — Render dell'item trasportato (18 ottobre 2026, DA TESTARE)

La scelta dell'autore è di **mostrare l'item effettivo della pizza**, non un generico disco con texture fissa. Al momento la mod possiede soltanto gli item `raw_pizza` e `cooked_pizza`, ma il sistema deve accogliere in futuro altri item pizza, ognuno con il proprio modello.

- **Implementazione client:** `PizzaPeelItemModel.java` implementa `ItemModel` come involucro del **modello 3D già approvato** della pala. Ad ogni estrazione del render state, disegna prima la pala e, se il componente `peel_cargo` è presente, aggiunge i layer dell'**ItemStack autentico** tramite `ItemModelResolver.appendItemLayers`.
- **Registrazione:** `ItaliansDelightClient` usa `ModelLoadingPlugin.modifyItemModelAfterBake`, limitato all'ID `italiansdelight:pizza_peel`. La stessa registrazione continua a funzionare con resource pack e nuove varianti di item registrate.
- **Dati preservati:** non si crea una copia nella logica server, non si memorizza un secondo item e non si introducono texture nuove. L'overlay è soltanto grafico e usa l'item originale che la pala trasporta.
- **Compatibilità:** eventuali nuovi item pizza saranno visualizzati attraverso il loro componente `ITEM_MODEL`, senza aggiungere il loro ID al renderer. Un eventuale cargo malformato contenente un'altra pala è ignorato per prevenire ricorsione.
- **Nota grafica:** scala, profondità e allineamento dell'item sopra la pala richiedono **test visivo**; il modello del manico e le UV approvati **non sono stati modificati**.

**Correzione compilazione:** sostituita la chiamata rimossa `Player.displayClientMessage(Component, boolean)` con l'invio lato server di `ClientboundSetActionBarTextPacket(Component)`. Errore riportato dall'autore in `PizzaPeelInteractions.java:219` il 18 ottobre 2026; **fix pubblicato su GitHub, build da riprovare**.

**Nuovi test:**
- [ ] `.\\gradlew.bat clean build` completa con Java 25 senza l'errore `displayClientMessage`.
- [ ] Pala vuota: soltanto il modello 3D della pala (già approvato); niente immagine della pizza fantasma.
- [ ] Pala con `raw_pizza`: compare il modello dell'item trasportato in prima/terza persona e in inventario.
- [ ] Pala con `cooked_pizza`: compare il relativo item cotto, distinto da quello crudo, senza alterarne la composizione.
- [ ] Pala con pizza salvata, lasciata con Q e poi raccolta: item corretto sempre visibile.
- [ ] Testare orientamento, scala e contatto tra pizza visualizzata e superficie della pala: nessun clipping o sovrapposizione invadente.
- [ ] Confermare funzionamento del sistema anche con un futuro item pizza aggiuntivo o un modello sostituito da resource pack.

## Correzione render in mano — 18 ottobre 2026 (DA TESTARE)

**Problema osservato e documentato dall'autore:** l'item pizza appare anche nella hotbar/inventario e in terza persona viene mostrato vicino alla mano, mentre la testa metallica della pala rimane vuota. L'autore ha indicato in uno screenshot il centro desiderato della pizza **sulla piastra grigia**.

**Fix pubblicato:** `client/render/PizzaPeelItemModel.java`

- **Inventario, hotbar, drop a terra, item frame:** il renderer ora mostra **soltanto il modello 3D della pala**, anche quando `peel_cargo` contiene una pizza. Il tooltip continua a riportare il contenuto.
- **Prima e terza persona, entrambe le mani:** la pala mostra il modello dell'**ItemStack reale trasportato** risolto tramite `ItemModelResolver` in un layer `SpecialModelRenderer` separato. Non viene introdotta alcuna pizza generica né una whitelist di tipi di pizza.
- **Posizione derivata dalla geometria:** la testa metallica del modello approvato è compresa tra `[4,13.75,7.5]` e `[12,26.75,8.5]`; centro `[8,20.25,8]`. Con origine del modello `[8,8,8]` la quota del nuovo layer è `(20.25-8)/16 = 0.765625`, centrata lateralmente (`x=0`) e appena davanti alla superficie metallica (`z=0.070`). Scala item `0.34`, profondità `0.04`, regolabili dopo il test visivo.
- **Trasformazioni in mano:** la nuova layer ripete gli `ItemTransform` del JSON approvato per entrambe le mani e per entrambe le visuali, evitando che il centro dell'item finisca accanto all'impugnatura. La conversione delle traslazioni è 1/16 come per il modello Minecraft.
- **Invariati:** `models/item/pizza_peel.json`, texture, modello geometrico, meccaniche d'uso, ingredienti, salvataggi e tempi di cottura.

**Casi da provare in Minecraft:**
- [ ] Hotbar, inventario, item a terra e cornice: nessuna pizza aggiuntiva sul modello della pala.
- [ ] Terza persona, pala con pizza cruda: la pizza è centrata **sulla zona metallica segnata nello screenshot**, non vicino alla mano.
- [ ] Prima persona e seconda mano: stesso allineamento, scala coerente e nessuna compenetrazione evidente con la lamina.
- [ ] Pala vuota: nessuna sovrapposizione; esattamente il modello originale.
- [ ] Passaggio vuota → occupata → vuota e caricamento dopo riavvio: la pizza appare/scompare solo nel mondo e resta un unico ItemStack nella pala.
- [ ] Pizza cotta e futuri item pizza diversi da `raw_pizza`: si vede il loro vero modello senza modificare il renderer.
- [ ] Compilazione Java 25 (`gradlew.bat clean build`) e prova client (`gradlew.bat runClient`) completate.

## Pala orizzontale quando occupata — 18 ottobre 2026 (DA VERIFICARE IN GIOCO)

**Problema osservato:** nello screenshot dell'autore la pala con una pizza è ancora eretta/diagonale e la testa metallica non è parallela al terreno. Richiesta: ruotare la pala **solo quando contiene una pizza**, per appoggiare in piano l'item pizza sulla superficie metallica.

**Correzione pubblicata:**

- `assets/italiansdelight/models/item/pizza_peel_loaded.json`: nuovo modello figlio di `pizza_peel.json`, **senza duplicare elementi, texture o UV**. Cambia soltanto le trasformazioni **delle quattro visuali in mano**: rotazione X **-90°**, con piccoli angoli di impugnatura Z per prima persona. Il lato frontale originale (+Z) punta così verso l'alto (+Y) e il piano XY della lama diventa parallelo al terreno (XZ).
- `assets/italiansdelight/items/pizza_peel.json`: selettore `minecraft:display_context` limitato alle mani con condizione `minecraft:has_component` su `italiansdelight:peel_cargo`. **Pala vuota** → modello originale; **pala piena in mano** → modello orizzontale. Inventario, hotbar, item a terra, cornice e altri contesti usano sempre la posa originale, senza pizza extra.
- `client/render/PizzaPeelItemModel.java`: aggiornati gli `ItemTransform` del render dinamico perché il **vero ItemStack pizza** condivida la posa del modello carico. Si mantengono offset sulla lama e scala precedentemente definiti; nessuna modifica al trasporto, salvataggio, compatibilità futura con altri item pizza o modello 3D approvato.
- **Controlli statici:** selezione carico/contesto, riferimenti modelli, corrispondenza rotazioni Java/JSON, assenza modifiche alla texture e geometria superati. Non equivale a test in Minecraft.

**Test specifici:**

- [ ] Mano principale, terza persona: pala **vuota** identica a prima; caricandola la lamina passa in orizzontale, come un vassoio, senza abbandonare l'impugnatura.
- [ ] Mano principale, prima persona: stessa rotazione, con pizza sopra la lamina e non sotto né vicino alla testa del giocatore.
- [ ] Seconda mano, prima/terza persona: posa equivalente; verificare che la mano tenga il manico.
- [ ] Pala con pizza cruda e pala con pizza cotta: entrambi gli item reali rimangono centrati sulla superficie superiore; controllare assenza clipping.
- [ ] Pala caricata in inventario/hotbar, droppata o in cornice: soltanto pala originale, **nessuna pizza renderizzata fuori dalle mani**.
- [ ] Dopo deposito nel mondo o trasferimento nel Pizza Oven, la pala torna immediatamente all'orientamento originale; salvataggio e multiplayer invariati.
- [ ] `.\\gradlew.bat clean build` e `.\\gradlew.bat runClient` terminano senza errori.

## Revisione rendering condiviso — 18 ottobre 2026 (DA COLLAUDARE)

**Problemi rilevati dagli screenshot dell'autore:** vista in prima persona con pala in posizione corretta ma pizza invisibile; in terza persona lama e manico lontani dalla mano e pizza flottante davanti al ventre o sotto la pala.

**Causa tecnica eliminata:** prima la pala originale e l'item trasportato venivano aggiunti a `ItemStackRenderState` come **layer indipendenti**, con trasformazioni potenzialmente divergenti. La posa carica era selezionata anche da un file JSON dedicato, mentre la pizza aveva un secondo `ItemTransform`; questa duplicazione era difficile da sincronizzare.

**Nuova soluzione pubblicata:**

- `PizzaPeelItemModel.java` crea due render state neutri (pala base e vero `ItemStack` pizza) e li rende **con lo stesso `PoseStack` dentro un unico `SpecialModelRenderer`** e un solo `ItemTransform`. Il render della pala precede quello della pizza. La pizza è centrata sul piatto metallico (x=0; y=0.765625; z=0.0725, appena sopra la faccia positiva), scala 0.34; il piano del modello viene ruotato di -90° su X per far apparire la pizza verso l'alto.
- **Impugnatura terza persona:** la pala carica applica un compenso di -5 pixel su Y e -8 pixel su Z per mantenere il manico vicino alla mano dopo la rotazione di -90°; la mano destra e sinistra sono supportate.
- **Prima persona:** conservate le trasformazioni cariche già approvate (rotazione X -90°, prima persona Z ±25°, posizione 0,3,1), ma pala e pizza ora condividono la posa. Gli extents del renderer comprendono manico, lama e pizza: non rimangono vuoti e non dovrebbero escludere il livello dalla visibilità.
- `assets/italiansdelight/items/pizza_peel.json` torna a riferire solo il modello originale; il file `pizza_peel_loaded.json` non più utilizzato è stato eliminato. La **posa carica è definita soltanto nel renderer client Java**, non è duplicata nei modelli JSON.
- **Pala vuota / GUI / hotbar / terra / cornice:** passa direttamente all'item model originale, senza modifiche. Nessun cambiamento a texture, cuboidi, UV, gameplay, dati dei condimenti e forno.
- **Compatibilità futura:** la pizza visualizzata è sempre l'item vero recuperato da `peel_cargo`. Il renderer evita ID specifici e supporta nuovi tipi di item pizza.

**Controlli indispensabili da parte dell'autore:**

- [ ] Compilazione `.\\gradlew.bat clean build` senza errori Java 25.
- [ ] Prima persona, pala carica: pizza visibile sulla faccia superiore del metallo, non invisibile né sotto.
- [ ] Terza persona da davanti, retro e fianco: il fondo del manico è afferrato dalla mano; la testa non pende in basso, ma è orizzontale.
- [ ] Terza persona: la pizza appare esattamente sopra la testa metallica e non davanti al ventre; nessun clipping tra materiali.
- [ ] Mani destra e sinistra: stesso comportamento.
- [ ] Pala vuota identica al precedente modello visivamente approvato.
- [ ] Inventario, hotbar, oggetto droppato e cornice senza pizza aggiuntiva.
- [ ] Pizza cruda e cotta, Q/drop/ricarica dopo salvataggio, item personalizzati e trasferimento al forno non alterati.

**Stato:** codice pubblicato, build e prova grafica in Minecraft ancora pendenti. Le sezioni storiche sopra descrivono le precedenti iterazioni e non prevalgono su questa correzione.

## Revisione 18 ottobre 2026 — ripristino rendering vanilla (DA COMPILARE / TESTARE)

**Segnalazione dell'autore:** con la versione del renderer condiviso la pala carica è apparsa dietro il personaggio, vicina ai piedi, e non è stata più visibile in prima persona. Correggere **la posa dell'intero oggetto**, non spostare soltanto l'icona pizza.

**Nuova soluzione (sostituisce le iterazioni sopra):**

- Ripristinato **`peelModel.update(output, peel, resolver, context, ...)` in tutte le situazioni**, lasciando a Minecraft il rendering completo della geometria 3D del modello baked. Non viene più renderizzata l'intera pala tramite `SpecialModelRenderer`, che risultava invisibile in prima persona.
- `models/item/pizza_peel_loaded.json`: deriva via `parent` dal **modello 3D approvato**; modifica solo le trasformazioni delle quattro mani. Rotazione X=-90°, per pala orizzontale. In terza persona il punto di presa viene riportato verso la mano con translation `[0,2,-4]` (prima era erroneamente `[0,-5,-8]`, con la pala arretrata e abbassata). Posa prima persona `[-90,0,±25]`, traslazione `[0,3,1]`.
- `items/pizza_peel.json`: utilizza la condizione `minecraft:has_component` sul componente `italiansdelight:peel_cargo` e il selettore `minecraft:display_context` per usare il modello orizzontale **soltanto quando il peel è carico e tenuto in una delle quattro mani**. In ogni altro caso viene usato il modello vuoto originale.
- `PizzaPeelItemModel.java`: conserva il modello baked standard come strato primario, aggiungendo **soltanto la pizza** come layer speciale quando il peel è carico e in mano. Questo layer condivide la stessa trasformazione di mano definita nel modello carico; il suo centro è sul lato frontale della lamina (`y=(20.25-8)/16`, `z=0.075`). La pizza conserva l'item modello effettivo, senza ID hardcoded.
- Corretti gli `extents` del renderer pizza con gli **otto vertici** del box, anziché due soli vertici diagonali, per ridurre il rischio di culling erroneo.
- **Non modificati**: texture e cuboidi dell'item approvato, comportamenti server, utilizzo con forno, contenuto `peel_cargo`, dati degli ingredienti, item pizza futuri e inventario.

**Verifiche richieste in gioco, NON ancora confermate:**
- [ ] Client compila con `.\\gradlew.bat clean build`.
- [ ] Prima persona: pala carica e pala vuota **entrambe visibili** (niente scomparsa totale).
- [ ] Terza persona, visuale frontale/laterale/posteriore: **impugnatura nella mano**, non dietro al corpo o ai piedi.
- [ ] La pala occupata è orizzontale e la pizza appare sopra la lamina, non al fianco del busto.
- [ ] Mano sinistra e destra, stato vuoto e pieno, entrambi con pose corrette.
- [ ] Inventario, hotbar, cornice e item droppato: soltanto pala, senza icona pizza aggiunta.
- [ ] Trasporto, deposito sul terreno, forno e riavvio senza duplicazioni.

## Posizionamento finale pala carica — iterazione 18 ottobre 2026 (DA TESTARE)

**Nuovo riscontro dell'autore:** la pala è tornata visibile in prima persona, **ma la pizza rimane invisibile**; in terza persona l'**impugnatura non è centrata nella mano** e l'item pizza sembra sotto la testa metallica o davanti al ventre.

**Correzioni limitate pubblicate nel ramo:**

- `models/item/pizza_peel_loaded.json`: posa mano terza persona con `translation [0,3,-8]` al posto di `[0,2,-4]`; rotazione X=-90° e scala originale della posa carica invariate. Questo compensa la rotazione del manico portando il punto di presa più vicino alla mano, senza modificare il modello 3D.
- `PizzaPeelItemModel.java`: identico `ItemTransform` Java della terza persona (`pose(-90,0,0,0,3,-8,0.8)`), per non disallineare lo strato dell'item pizza. **Overlay pizza spostato sull'altro lato della lamina**: `BLADE_FACE_Z: +0.075 → -0.075`, perché l'item poteva risultare coperto dal metallo nella vista corrente. Scala visiva da `0.34` a `0.44` per facilitarne la lettura.
- Nessuna modifica al modello o alla posa della pala **vuota**, alle UV, alla texture, ai dati `peel_cargo` o alle interazioni col forno.

**Test da eseguire:**
- [ ] Prima persona, pizza cruda/carica: logo/item visibile e non nascosto dal metallo. Provare guardando da sopra e di lato.
- [ ] Terza persona, angolazioni frontale, posteriore e laterali: manico passa per il pugno; pala parallela al terreno.
- [ ] La pizza non rimane davanti al corpo, sotto la lamina o vicino alla mano; deve essere centrata nella testa metallica.
- [ ] Mano secondaria e pala vuota restano corrette.
- [ ] Inventario e hotbar continuano a mostrare soltanto la pala.
- [ ] Compilazione e gioco senza regressioni.

**Stato:** nuove UV di visualizzazione/offset nel renderer carico pubblicati, **NON verificati** con build Java 25 o screenshot aggiornati. Non indicare risolto fino alla conferma dell'autore.

## Correzione strutturale della pala carica — 18 ottobre 2026 (DA COMPILARE E VERIFICARE VISIVAMENTE)

**Segnalazione dell'autore:** dopo varie modifiche ai soli display transform, la pizza e la testa della pala restavano vicino ai piedi. L'autore ha richiesto esplicitamente di non procedere con altri tentativi di offset arbitrari.

### Diagnosi

Il modello approvato della pala vuota usa cuboidi **lungo Y** (`handle` y=0..13.5; `blade` y=13.75..26.75), ma le sue pose in mano funzionano già. Le versioni precedenti del modello carico applicavano una nuova rotazione X=-90° all'**intero item attorno all'origine Minecraft [8,8,8]**, poi tentavano di riportare il manico nella mano tramite traduzioni. Questo modifica contemporaneamente punto di presa, direzione della testa e posizione della pizza: correggere un offset ne spostava altri.

### Soluzione geometrica pubblicata (non solo offset)

1. **Punto di rotazione individuato nella geometria:** centro del manico `G=[8,6.75,8]` in pixel di modello. La pala **carica** usa tre cuboidi ruotati rigidamente di **-90° su X attorno a G**, tramite nuovi vertici nel file `models/item/pizza_peel_loaded.json`. Il punto G resta matematicamente invariato dopo la rotazione.
2. **Trasformazioni in mano identiche alla pala vuota approvata.** `pizza_peel_loaded.json` eredita `display`, `textures` e `gui_light` da `pizza_peel.json` senza override; il modello approvato della pala vuota non viene modificato. In tal modo l'impugnatura conserva l'ancoraggio già collaudato.
3. **Coordinate esatte della lama carica:** x=4..12, y=6.25..7.25, z=-12..1 (pixel di modello). Il lato superiore è y=7.25 e il centro della lama è `[8,7.25,-5.5]`. La pizza viene renderizzata con centro leggermente sopra la superficie a `[8,8,-5.5]`, equivalente a `(0,0,-0.84375)` in coordinate render item.
4. **Un solo allineamento condiviso:** `PizzaPeelItemModel.java` lascia il rendering della pala al normale modello Minecraft. Il livello della pizza usa **gli stessi display transform del modello vuoto**, perché ora sono gli stessi ereditati dalla pala carica. L'item pizza originale viene ruotato -90° su X per distenderlo sulla lamina orizzontale; scala 0.40, spessore visivo 0.055. Non applica nuove pose globali alla pala.
5. **Texture e identità preservate:** nessun nuovo asset PNG, il modello della pala vuota e le sue UV rimangono identici. Le sei facce di ogni cuboide sono riassegnate secondo la rotazione (sud → su, nord → giù, su → nord, giù → sud), con rotazione UV 90° sui due lati per preservare la densità dei texel. La pizza rimane l'**ItemStack completo** di `peel_cargo`, compatibile con futuri item pizza. GUI e hotbar mostrano sempre soltanto la pala.

**Verifiche geometriche eseguite senza Minecraft:** tutti e tre i cuboidi corrispondono esattamente alla trasformazione rigida; coordinate dentro l'intervallo ammesso [-16,32], normali delle facce riassegnate correttamente, selettore `has_component` valido, geometria della pala vuota invariata, centro dell'overlay calcolato dal modello carico.

### Checklist di accettazione

- [ ] `.\\gradlew.bat clean build` completa senza errori usando Java 25.
- [ ] Pala vuota: stessa posa, scala e impugnatura approvate, in prima e terza persona.
- [ ] Pala carica: manico attraversa la mano anche in terza persona, non è dietro il corpo né vicino ai piedi.
- [ ] Pala carica: testa metallica parallela al terreno, estesa in avanti e visibile sopra la cintura.
- [ ] Pizza **visibile in prima persona** e appoggiata sulla **faccia superiore** della testa metallica.
- [ ] Pizza **visibile in terza persona**, centrata sulla lama (non davanti al ventre, ai piedi o separata dalla pala).
- [ ] Verificare tutte e quattro le combinazioni prima/terza persona e mano destra/sinistra.
- [ ] Inventario, hotbar, item a terra e cornice mostrano solo la pala, senza pizza sovrapposta.
- [ ] Inserimento nel Pizza Oven, estrazione, recupero da terra, drop con Q e salvataggio mantengono il vero `ItemStack` pizza.
- [ ] Aspetto della pala con item `raw_pizza` e `cooked_pizza`, compresi futuri modelli alternativi.

**Stato:** codice e documentazione pubblicati; **build Java 25 e collaudo del modello in Minecraft non effettuabili nell'ambiente di lavoro attuale**. Non indicare come risolta la resa prima della conferma dell'autore. Questa sezione supera le note sulle vecchie pose X=-90 e sulle traduzioni arbitrarie precedenti.

## Correzione delle coordinate del rendering della pala carica — 18 ottobre 2026

**Difetto rilevato:** la pala veniva impugnata lateralmente («come una chitarra») e la pizza rimaneva invisibile o in una posizione distante. Le precedenti correzioni modificavano continuamente il pivot e gli offset, senza distinguere lo **spazio del modello** dallo **spazio centrato sull'item**.

### Analisi della causa

La mesh caricata (`pizza_peel_loaded.json`) ha il piatto metallico da `[4, 6.25, -12]` a `[12, 7.25, 1]`, in **pixel di modello Minecraft**. Il suo centro superiore, dopo un piccolo distacco di 0.5 pixel, è `[8, 7.75, -5.5]`. Il `SpecialModelRenderer` riceve una `PoseStack` da cui è già possibile disegnare nel sistema di coordinate **pixel/16** usato dai modelli baked; **non si deve sottrarre 8 a ciascuna coordinata**. Il codice precedente sottraeva 8 a Z e usava X=0 anziché X=8/16, spostando la pizza fuori dalla pala. Gli extents erano anch'essi espressi nel sistema errato.

Questa implementazione prende come riferimento il sistema di composizione di item reali di **CC: Tweaked per Minecraft 26.2**: `TrackingItemStackRenderState`, `ItemModelResolver.updateForTopItem(..., ItemDisplayContext.NONE)`, `LayerRenderState.setItemTransform`, `setupSpecialModel`, `ItemStackRenderState.submit`. Non introduce sprite fissi o copie dell'item.

### Modifica applicata su GitHub

- `PizzaPeelItemModel.java`: posizione reale dell'item pizza `(0.5, 0.484375, -0.34375)` invece di `(0, 0, -0.84375)`; scala `0.40`, orientamento locale X=-90° affinché la faccia della pizza guardi verso l'alto. I limiti del renderer speciale ora racchiudono il contenuto **nelle medesime coordinate di modello**.
- `pizza_peel_loaded.json`: la geometria 3D della pala carica rimane intatta e già ruotata attorno al manico. Viene modificato **soltanto il display in mano**: terza persona con yaw 0° anziché 60° e prima persona con yaw ±15° anziché ±90°, mantenendo identiche traslazione e scala alla pala vuota già approvata. La lamina carica punta verso l'avanti, non di traverso.
- La medesima rotazione di mano è rappresentata in `PizzaPeelItemModel.java` per il secondo livello renderizzato, così pala e vera pizza subiscono gli **stessi display transform**. Non viene cambiato alcun dato di salvataggio o meccanica.
- `pizza_peel.json`, le texture approvate e i modelli delle pizze **non vengono modificati**. Il selettore continua a usare la posa caricata soltanto se presente `peel_cargo` e soltanto nei quattro contesti in mano. L'hotbar e l'inventario mostrano sempre solo la pala.

### Test di accettazione (NUOVA VERSIONE, NON ANCORA COLLAUDATA)

- [ ] `.\\gradlew.bat clean build` completa con Java 25.
- [ ] In prima persona, pala **vuota** invariata e pala **carica** visibile con pizza appoggiata sul metallo.
- [ ] In terza persona frontale, posteriore e laterale, il **manico passa nella mano**, senza posa «a chitarra» o testa vicina ai piedi.
- [ ] In tutte e quattro le combinazioni delle mani, la pala carica punta in avanti ed è **parallela al terreno**.
- [ ] La pizza cruda **e** cotta resta centrata sopra la testa metallica, **non** sul personaggio o sotto la lamina.
- [ ] La pizza aggiuntiva **non** viene visualizzata in inventario, hotbar, oggetti droppati e cornici.
- [ ] Dopo Q/raccolta, salvataggio, trasferimento Pizza Oven e ritorno a pala vuota, non compaiono perdite di dati, duplicazioni o cambiamenti di modello involontari.
- [ ] Verificare anche il modello di un futuro item pizza aggiuntivo; nessuna whitelist di item.

**Stato:** pubblicato nel ramo di sviluppo, verifiche statiche da completare. Non segnare «risolto» prima dell'esito della build e del test in Minecraft.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

Nessuna build è stata eseguita nell'ambiente GitHub. Se la compilazione fallisce, salvare il **primo errore completo**, con classe e numero di riga.

## Raffinamento interazioni — 18 ottobre 2026 (DA TESTARE)

La resa grafica del modello 3D e delle UV è stata confermata soddisfacente dall'autore. Il codice di trasporto era già funzionante; questa fase aggiunge piccole rifiniture senza modificare i dati della pizza o il modello:

- **Seconda mano:** usare una pala anche in offhand. Con mano principale vuota, il clic su pizza o Pizza Oven è delegato alla pala in offhand prima del servizio a mano vuota. Se la mano principale contiene già un ingrediente o un utensile, le sue azioni continuano ad avere precedenza. Una pizza non ancora completamente stesa rimane estendibile a mano.
- **Feedback di rifiuto:** testo in action bar quando si clicca su una seconda pizza con pala carica, si tenta di inserire pizza cotta nel Pizza Oven, il forno non può accettare il carico, o si tenta di estrarre da forno vuoto. Messaggi disponibili in italiano e inglese, prodotti dal server.
- **Invariati:** modello UV e impugnatura approvati, un solo carico per pala, componenti di condimento, forno e tempi di cottura, nessun consumo dello strumento.

**Verifiche aggiuntive:**
- [ ] Pala in offhand + mano principale vuota: prelevare e depositare una pizza cruda e una pizza cotta intatta.
- [ ] Pala in offhand + mano principale vuota: inserire ed estrarre dal Pizza Oven senza aprire la GUI.
- [ ] Pala in offhand + mano principale vuota davanti a impasto incompleto: la stesura a mano continua come prima.
- [ ] Pala in offhand + ingrediente/mattarello in main hand: si applica la consueta priorità della mano principale.
- [ ] Messaggi IT/EN leggibili quando il forno è occupato, vuoto o contiene una pizza incompatibile; la pizza resta conservata sullo strumento dopo un rifiuto.
- [ ] Provare le stesse interazioni su server dedicato e con due giocatori; niente duplicazioni, cambi di orientamento o perdita di ingredienti.

## Test A — Oggetto e texture

- [ ] **Collaudo nuova UV:** in terza persona verificare che le venature del manico non siano stirate, il raccordo non mostri righe metalliche enormi e sui lati inferiore/superiore della pala non compaiano pixel allungati; verificare anche prima persona, inventario e icona a terra.

- [ ] La pala compare nella categoria creativa come **modello 3D** (non come rettangolo piatto o texture atlas intera): metallo grigio in testa, manico di legno, bordi senza zone trasparenti; tooltip `Vuota`.
- [ ] Due pale vuote **non** si impilano; con una pizza, resta una pala per slot; nessuna barra di durabilità.
- [ ] La pala occupata mostra nel tooltip il nome dinamico/fisso della pizza trasportata, senza perdere la traduzione in EN/IT.
- [ ] Il modello 3D è visibile in inventario, prima/terza persona, mani destra/sinistra, item a terra, cornice; nessun errore di texture/modelli mancanti. Controllare proporzioni e orientamento della pala, in particolare di profilo.
- [ ] La pala conserva **lo stesso modello tridimensionale anche da carica** (lo stato visuale separato della pala carica non è ancora stato progettato); la texture atlas rimane invariata.
- [ ] La pala non causa nuovi errori di ricettari o altri asset.

## Test B — Raccogliere e depositare a terra

1. Posizionare e stendere una pizza cruda, dividerla in quattro quarti, usare **condimenti diversi** e tenere la pala vuota.
2. Clic destro con la pala: il blocco sparisce, la pala si riempie e **non viene droppata alcuna pizza separata**, impasto, ciotola o ingrediente.
3. Clic destro su supporto valido (pietra piena, per esempio): la pizza ricompare con orientamento, quarti e condimenti identici; la pala è vuota.
4. Ripetere almeno tre volte e verificare assenza di duplicazioni e persistenza dei nomi dinamici.
5. Tentare di usare la pala su una base ancora allo stage 0/1/2/3: non deve sottrarla al mondo.
6. Provare supporto non valido, spazio di destinazione occupato e clic sul fianco di un blocco: nessuna perdita/duplicazione, pala carica e pizza non piazzata.
7. Ripetere con pizza **cotta integra**. Su pizza cotta servita parzialmente, la pala vuota **non** deve ripristinare o raccogliere i quarti mancanti.
8. Con pala già occupata, clic su una seconda pizza: la seconda resta nel mondo e la prima resta sulla pala.

## Test C — Trasferimenti diretti del Pizza Oven

1. Inserire nel forno **una pizza cruda** con pala occupata: senza GUI, la pala diventa vuota e la pizza appare nello slot input quando si apre l'interfaccia.
2. Provare la stessa interazione con input oppure output già occupati: nessun trasferimento, nessun consumo o sovrascrittura.
3. Inserire una pizza cruda nel forno, avviare la cottura, **estrarla a metà con pala vuota**, poi reinserirla: composizione e nome invariati, ma **freccia di progresso da zero**. Il fuoco residuo deve continuare a bruciare come prima.
4. Cuocere completamente una pizza, quindi estrarla con pala vuota dallo slot output. Posizionarla sul terreno e prelevare i suoi quattro spicchi: nutrizione e ingredienti come nella fase 4C.
5. Tentare di inserire una pizza **già cotta** dal lato della pala: trasferimento rifiutato, pala resta carica.
6. Con forno privo di input/output, pala vuota non riceve alcuna pizza fantasma; l'uso senza pala continua ad aprire la GUI.
7. Confermare che hopper, ciotole restituite, combustibili vanilla, reset cook time, luce/particelle e fiamma non sono cambiati.

## Test D — Salvataggi, multiplayer e comportamento vanilla

1. Trasportare una pizza mista, salvare/eseguire logout e rientrare: il componente `peel_cargo` mantiene **una pizza sola** con tutte le applicazioni originarie.
2. Lanciare con **Q** la pala carica, raccoglierla, farla raccogliere da un secondo giocatore e depositare la pizza: nessun nome, topping, orientamento o porzione perso.
3. Mettere pala carica in cassa e spostarla più volte tra inventari; testare anche server dedicato/client con lingue diverse.
4. Provare rimozione supporto della pizza, multiplayer con due giocatori che tentano di raccogliere lo stesso blocco, e tentativi di inserimento simultanei nel forno: nessun duping.
5. Confermare il normale **despawn** della pala lanciata e il suo comportamento a contatto con fuoco/lava/cactus/esplosioni: nessuna protezione o drop della pizza all'interno.

## Da definire successivamente

- [ ] Ricetta/materiali e sblocco nel recipe book, previa decisione dell'autore.
- [ ] Aspetto grafico **specifico della pala occupata**: mantenere per ora la texture già presente; non alterare gli asset senza autorizzazione.
- [ ] Possibile pala come arma **solo quando vuota**: danni e velocità non definiti, nessun codice da arma aggiunto.
- [ ] Prestazioni, resilienza delle modifiche in multiplayer e compatibilità dei modpack secondo la checklist generale.
