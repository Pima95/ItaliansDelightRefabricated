# Pizza Update 1.0.0 — Fase 4D: pala per pizza

> **Codice pubblicato su `feature/pizza-1.0.0`; build Java 25 e verifica in gioco ancora da eseguire.**
> Test dei nomi e tooltip (fase precedente) confermati funzionanti dall'autore il 17 ottobre 2026.

## Implementazione e decisioni già rispettate

- Nuovo item `italiansdelight:pizza_peel` in `ModItems`, nello stesso creative tab dei tool, **stack massimo 1 e nessuna durabilità**.
- **Correzione grafica del 18 ottobre 2026 — DA VERIFICARE VISIVAMENTE:** il precedente modello piatto `minecraft:item/handheld` era inadatto al PNG 64×64, perché il file è una piccola texture atlas per un oggetto 3D (metallo a sinistra, legno e bordi in regioni separate). `models/item/pizza_peel.json` contiene ora **7 elementi tridimensionali** (piastra metallica con sagoma a gradini, raccordo, manico in legno), UV su zone opache dell'**identica texture originale**. Nessuna PNG sovrascritta. `peel_pizza_2.png` è conservata per un eventuale modello carico distinto, NON aggiunto automaticamente.
- Data component `italiansdelight:peel_cargo`, assente per pala vuota. Per pala occupata conserva **esattamente un ItemStack pizza cruda o cotta** con i suoi componenti originali (composizione, ID canonici, ingredienti, applicazioni, maschere, divisioni, orientamento, seed grafico, eventuale nome custom). Una pala occupata rimane un unico item nell'inventario.
- Clic destro con pala **vuota** su una pizza cruda *interamente stesa* prende la pizza, con gli ingredienti e senza drop aggiuntivi; il blocco sparisce. La palla o base non ancora stesa resta nel mondo.
- Clic destro con pala vuota su pizza cotta ancora **integra (4/4)** la raccoglie. Pizza con 1–3 spicchi già prelevati **non è caricabile** sulla pala: rimane servibile/rompibile secondo la fase 4C, senza ricreare i quarti consumati.
- Clic destro con pala **occupata** sulla faccia superiore di un supporto valido e libero posiziona la pizza con la sua BlockEntity e libera la pala soltanto dopo il successo. Funzionamento analogo al piazzamento di `raw_pizza` e `cooked_pizza`; non altera gli spicchi o le superfici ammesse.
- Clic destro sul **Pizza Oven** con pala occupata contenente pizza CRUDA trasferisce la pizza direttamente nello slot ingresso, senza aprire GUI. Rifiuta forno con input/output occupato e pizza già cotta. La pala rimane carica sui trasferimenti rifiutati.
- Clic destro sul **Pizza Oven** con pala VUOTA preleva la pizza cotta dallo slot uscita, altrimenti la pizza cruda dallo slot ingresso (anche se la cottura era già avviata). **Il progresso parziale si azzera all'estrazione**, perché appartiene al forno e non alla pizza: regola vanilla confermata nell'aggiornamento del 17 ottobre. Combustibile acceso continua a consumarsi.
- La GUI del forno resta usabile con mano vuota o altri item; **la pala è facoltativa**. Non sono state cambiate le automazioni con hopper, la cottura, i contenitori o la nutrizione.
- Nessuna ricetta di crafting aggiunta: **costo e schema della pala ancora da definire con l'autore**. Per ora ottenere la pala dal creative tab o con `/give @s italiansdelight:pizza_peel`. Nessun bonus da arma: è una funzionalità futura da definire.
- Drop con Q, raccolta da un altro giocatore, despawn, lava/fuoco/cactus/esplosioni restano **comportamenti vanilla**. Se la pala come item viene distrutta/despawna, anche la pizza al suo interno viene persa, senza rimborsi.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

Nessuna build è stata eseguita nell'ambiente GitHub. Se la compilazione fallisce, salvare il **primo errore completo**, con classe e numero di riga.

## Test A — Oggetto e texture

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
