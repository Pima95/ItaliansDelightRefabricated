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
