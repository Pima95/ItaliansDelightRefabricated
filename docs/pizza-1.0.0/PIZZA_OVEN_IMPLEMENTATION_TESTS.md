# Pizza 1.0.0 — Fase 4B: Pizza Oven (regressioni aggiornate)

> **Conferma dell'autore del 17 ottobre 2026:** il Pizza Oven, inclusi i recenti effetti in stile fornace vanilla, è stato provato in gioco ed è risultato funzionante. La conferma è funzionale generale; la checklist di regressione dettagliata (tutti gli orientamenti, riavvio, multiplayer, ecc.) rimane disponibile e non va considerata implicitamente completata.

## Decisioni confermate

- **Avanzamento come fornace vanilla:** il `CookTime` è un contatore **dentro il Pizza Oven**. Una pizza cruda prelevata e reinserita ricomincia da zero: **nessun progresso nel data component `raw_pizza`**, né nel blocco pizza posizionato. Il forno può salvare il proprio contatore insieme al chunk, come fa la fornace vanilla, ma tale contatore non passa sull'item. Senza combustibile, il progresso nel forno diminuisce gradualmente, come nella fornace vanilla; al cambio input viene azzerato.
- **Fuoco come fornace vanilla:** una volta acceso un combustibile, la fiamma nella GUI e il `lit` decrescono fino a esaurirsi **anche se la pizza termina, viene tolta o il forno rimane inattivo**. Nessuna nuova unità di combustibile si accende a forno vuoto o bloccato.
- **Una ciotola per ogni applicazione originaria di salsa:** due salse differenti poste rispettivamente sulle due metà della stessa pizza producono **due ciotole**, non una e non quattro. Il risultato può essere **uno stack di due ciotole** (un solo oggetto visibile, quantità 2).
- **Hopper sottostante:** continua a estrarre la `cooked_pizza` tramite lo slot output. Le ciotole create al completamento entrano **direttamente nel medesimo hopper posto esattamente sotto il forno**, finché ha spazio. Se non è presente o è pieno, l'eventuale residuo viene espulso a terra come contenitore del Cooking Pot. Lo slot di output del forno non riceve mai ciotole e continua ad accettare una sola pizza cotta. Il secchio vuoto della lava può essere estratto dal basso come nelle fornaci vanilla.
- **Test degli ingredienti limitati:** la pizza cotta **non è ancora piazzabile** e i tooltip dei condimenti **non sono ancora implementati**. Non chiedere di verificare in gioco che gli ingredienti siano preservati visivamente o attraverso tooltip inesistenti. Il codice usa lo stesso `pizza_contents` in input e output; il collaudo visivo dei condimenti è rimandato alla fase di pizza cotta piazzabile/tooltip.

## Modifica del 17 ottobre — ricettario vanilla rimosso (da testare)

- **Perché:** il menu precedente si presentava come `MenuType.FURNACE`: Minecraft apriva il suo `FurnaceScreen` con il ricettario vanilla e relative categorie non pertinenti.
- **Implementazione:** nuovo `ModMenuTypes.PIZZA_OVEN`; `PizzaOvenMenu` ha un costruttore client con `SimpleContainer(3)` e `SimpleContainerData(4)`; `PizzaOvenScreen` estende `AbstractContainerScreen` e disegna direttamente **la texture vanilla della fornace e gli sprite GUI vanilla della fiamma/freccia**, **senza istanziare il pannello ricette**.
- **Regressione A:** aprire il Pizza Oven vuoto: **nessun pulsante ricettario e nessun pannello laterale**, anche con la scorciatoia del libro o dopo aver aperto una fornace vanilla.
- **Regressione B:** verificare **i tre slot nelle posizioni precedenti**, trasferimento con shift, inventario giocatore e GUI con titolo «Forno per pizza».
- **Regressione C:** durante 20 secondi di cottura controllare la freccia progressiva e la fiamma che cala dopo la fine, senza flicker né deformazioni della texture.
- **Regressione D:** controllare di nuovo hopper e ciotole: il cambio è **solo di menu/schermata**, non deve influire sul ticker o sui rimborsi.
- **Compatibilità:** la registrazione dei menu cambia da vanilla `furnace` a `italiansdelight:pizza_oven`; occorre aggiornare **client e server insieme**. Un menu già aperto durante un aggiornamento della mod va chiuso e riaperto.

## Animazione esterna del forno come vanilla — test funzionale confermato il 17 ottobre

La grafica del Pizza Oven acceso era già legata a `lit=true` e al modello con `wood_oven_face_on`. Mancavano le **particelle e il suono**, che una fornace di Minecraft emette tramite `animateTick`. Ora `PizzaOvenBlock.animateTick` usa esattamente le particelle vanilla `minecraft:flame` e `minecraft:smoke`, con il suono casuale `block.furnace.fire_crackle` (probabilità 10% per chiamata), **solo durante `lit=true`**.

- **Test 1 — accensione:** posizionare il Pizza Oven, inserire pizza cruda e carbone. Devono comparire fumo e fiammelle davanti all'apertura, oltre al modello acceso e al crepitio intermittente.
- **Test 2 — orientamenti:** ripetere rivolto a Nord, Est, Sud e Ovest; particelle soltanto davanti all'apertura, mai sui lati o dietro.
- **Test 3 — combustibile residuo:** al termine della cottura, mentre il carbone ha ancora burn time, particelle e crepitio devono **continuare** come nella fornace vanilla, anche con output occupato o pizza estratta.
- **Test 4 — spegnimento:** quando il combustibile arriva a zero e lo stato torna `lit=false`, nessuna nuova particella o suono deve essere generata.
- **Test 5 — regressione:** riaprire la GUI, estrarre una pizza a metà cottura, verificare il reset del progresso; controllare ancora hopper e ciotole. `animateTick` non modifica inventario, fuel, ingredienti né salvataggi.

**Non sono state modificate le PNG `wood_oven_*` già approvate** e non è stata introdotta alcuna sprite sheet `.png.mcmeta`: la fornace vanilla utilizza particelle/suoni per l'animazione esterna, non un cambio di texture a frame.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

## Test 1 — Cottura e progresso NON trasferibile

1. Preparare una pizza con qualche condimento e raccoglierla come `raw_pizza`.
2. Inserire pizza e carbone nel Pizza Oven. Dopo circa **10 secondi**, verificare una freccia intorno a metà.
3. Rimuovere la pizza prima di completarla: l'item rimane `raw_pizza`. Reinserendolo nello stesso forno, la freccia deve ripartire da zero, anche se c'è ancora fuoco.
4. Chiudere e riaprire solo la GUI senza togliere la pizza: il contatore del forno deve proseguire, non resettarsi.
5. Uscire dal mondo e rientrare mentre la pizza è ancora nel forno: il contatore nel forno deve persistere come una fornace vanilla (non in un ItemStack).
6. Lasciare esaurire il combustibile durante la cottura: la freccia diminuisce gradualmente. Con nuovo combustibile, la cottura riprende dal valore **attuale del forno**, senza ripristinare l'avanzamento precedente sull'item.
7. Completare i 20 secondi attivi: risultato una `cooked_pizza`; la pizza cotta non deve essere direttamente mangiabile.

## Test 2 — Fuoco che si esaurisce gradualmente

1. Mettere **una pizza + un carbone**, attendere la fine della pizza.
2. Estrarre la `cooked_pizza` senza aggiungere una seconda pizza: la **fiamma deve rimanere accesa** per il tempo residuo del carbone e la barra della fiamma nella GUI deve diminuire gradualmente.
3. A combustibile esaurito il forno passa a `lit=false`, senza accendere automaticamente altro carbone finché non arriva una pizza.
4. Provare a togliere l'input prima che finisca il carburante: la fiamma continua a calare, mentre la freccia del progresso torna a zero.
5. Provare il secchio di lava: il combustibile lascia il secchio vuoto e la fiamma consuma i suoi tick anche a forno inattivo.

## Test 3 — Due salse, due ciotole

1. Stendere una pizza, dividerla in **due metà**.
2. Applicare una salsa di pomodoro soltanto sulla metà sinistra e panna o ricotta soltanto sulla metà destra.
3. Raccogliere la pizza e cuocerla senza rimuovere i condimenti. **Risultato atteso: 2 ciotole in totale**, anche se cadono come **uno stack visibile da 2**.
4. Ripetere con una salsa applicata su tutti i quattro quarti in un'unica posa: attesa **1 ciotola**, non quattro.
5. Ripetere con una delle applicazioni completamente rimossa prima di cuocere: quella salsa non produce ciotola, perché è già stata rimborsata come item pieno.
6. Non confondere il numero di entità item a terra con il numero di **unità** nello stack.

## Test 4 — Hopper sotto il Pizza Oven

1. Posizionare un hopper con contenitore di destinazione direttamente **sotto** il forno.
2. Cuocere la pizza a due salse del Test 3: devono essere trasferiti al sistema sotto il forno **1 `cooked_pizza` e 2 `minecraft:bowl`**.
3. Ripetere con il hopper inizialmente vuoto ma senza cassa, per controllare le quantità nei suoi slot prima che vengano spostate altrove.
4. Ripetere con un hopper contenente già uno stack parziale di ciotole: devono accodarsi senza duplicazioni.
5. Ripetere con hopper pieno: la pizza rimane nello slot di output del forno finché non c'è spazio; le ciotole eccedenti devono cadere nel mondo, **mai sparire**.
6. Togliere l'hopper: le ciotole vengono espulse come prima (stile Cooking Pot); la pizza cotta resta prelevabile dalla GUI.
7. Con lava, verificare che l'hopper inferiore possa estrarre anche un secchio vuoto dal fuel slot.

## Regole non ancora implementate

- **Pizza cotta piazzabile, quattro spicchi e tooltip degli ingredienti:** attesi nelle prossime fasi.
- **Interazione diretta con pala:** rimane futura; la GUI funziona anche senza pala.
- **Cottura in fornace/affumicatore vanilla:** manca ancora la conversione dinamica dei componenti originali.
- **Grafica definitiva:** restano da disegnare modello Pizza Oven, `raw_pizza`, pizza cotta e rendering realistico dei topping. I modelli attuali sono segnaposto.
