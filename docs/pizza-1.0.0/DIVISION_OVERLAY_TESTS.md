# Pizza 1.0.0 — Fase 3E: overlay temporaneo dei tagli

**Codice pubblicato nel branch `feature/pizza-1.0.0` il 14 ottobre 2026. Compilazione e test visuali IN GIOCO ancora da eseguire.**

La visualizzazione dei tagli non cambia la logica già verificata dei quattro quarti o delle applicazioni dei topping. L'overlay è un effetto **solo client**, sopra la parte interna della base piatta, senza cambiare texture, block model, hitbox o bounding box.

## Implementazione

- [PizzaDivisionOverlay.java](../../src/main/java/dev/italiansdelight/client/render/PizzaDivisionOverlay.java): renderizzazione Fabric API 26.2 tramite estrazione `LevelExtractionEvents.END_EXTRACTION` e disegno `LevelRenderEvents.COLLECT_SUBMITS`. Render state immutabile, nessun accesso a ClientLevel nella draw phase. Quad sottili in posizione `1/16 + 0.004` sopra la base, entro il bordo rialzato. Colore crema opaco/leggermente traslucido.
- [PizzaDoughBlock.java](../../src/main/java/dev/italiansdelight/common/block/PizzaDoughBlock.java): proprietà di rete `division=0..7` e `facing=north/east/south/west` (invariata la geometria e il multipart dei modelli).
- [PizzaDoughBlockEntity.java](../../src/main/java/dev/italiansdelight/common/block/entity/PizzaDoughBlockEntity.java): conserva il vero stato persistente e sincronizza soltanto le due proprietà visive quando un taglio, una riunione o un riposizionamento modifica il record.
- [PizzaDivisionSync.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaDivisionSync.java): migrazione una tantum, dopo il caricamento del chunk, per pizze salvate prima di queste nuove proprietà; non modifica ingredienti, divisioni, stesura o durata del mattarello.
- [ItaliansDelightClient.java](../../src/main/java/dev/italiansdelight/ItaliansDelightClient.java) registra l'overlay soltanto nell'entrypoint client.

**Prestazioni:** la ricerca render esamina al massimo un cubo vicino alla camera (17×9×17 blocchi), e **solo** se il giocatore impugna il mattarello o un topping solido *nativo e già attivato*. Non carica chunk, non produce effetti server e non aggiunge BlockEntity tickers. L'eventuale supporto visuale ai topping equivalenti da datapack esterni si farà insieme alla sincronizzazione dei mapping lato client.

## Test 1 — Build e compatibilità

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Avviare un mondo di prova con almeno una pizza completamente stesa (stadio 4), mattarello e due ingredienti solidi `italiansdelight:mozzarella_slice` e `italiansdelight:basil`.

Se Gradle non compila, inviare l'errore completo della prima classe coinvolta. Per MC 26.2 sono stati verificati i nomi e la struttura delle API Fabric negli esempi ufficiali `LevelRenderEventsTests` del branch `26.2`.

## Test 2 — Linee nei cinque stati

Tenere **il mattarello in mano principale** e guardare una pizza stesa.

| `PizzaComposition.division_state` | Proprietà BlockState `division` | Linee visive |
|---|---|---|
| `0` | `0` | Nessuna |
| `1` | `1` | Una linea centrale completa |
| `3` | `3` | Una linea centrale + metà sinistra tagliata in due |
| `5` | `5` | Una linea centrale + metà destra tagliata in due |
| `7` | `7` | Croce completa |

La prima linea deve essere **parallela alla direzione salvata del giocatore**. Le linee secondarie sono solo nelle zone effettivamente suddivise. Non devono mai arrivare sopra il bordo rialzato né risultare interrate nell'impasto.

## Test 3 — Visibilità condizionata

1. Con il **mattarello in mano** le linee devono essere visibili sulle pizze vicine.
2. Con una **mozzarella a fette o basilico** in mano le linee devono essere visibili.
3. Con mano vuota **senza premere Shift**, oppure con pietra, attrezzo non pertinente o ingredienti esclusi (uovo intero, bistecca), **nessuna linea**.
4. Per questa fase i topping con recipiente (`tomato_sauce`, `cream_bowl`, `ricotta`) **non** attivano le linee: il loro clic non è ancora abilitato.
5. Allontanarsi dalla pizza o cambiare oggetto: le linee devono sparire immediatamente.
6. Dividere una pizza con mattarello, riunirla con Shift + mattarello e ripetere: le linee si aggiornano senza blocchi da rompere/riposizionare.

## Test 4 — Direzioni e clic

Ripetere il primo taglio guardando alternativamente **Nord, Est, Sud e Ovest** e verificare che la linea segua `facing`.

- Se la linea principale è Nord/Sud, deve essere disegnata sull'asse Z del mondo.
- Se la linea principale è Est/Ovest, deve essere disegnata sull'asse X del mondo.
- Su un taglio della metà sinistra/destra, controllare che la linea secondaria si trovi nel lato fisico corrispondente anche per direzioni opposte.
- Fare clic sui quattro quarti mentre si vedono le linee, aggiungendo per esempio basilico e speck: i valori di `original_mask`/`remaining_mask` letti con `/data get block X Y Z` devono corrispondere a **quel** quarto.
- Provare la divisione mista `3` e `5` e la riunione progressiva senza perdere topping.

## Test 5 — Mondo salvato prima dell'overlay

1. Aprire una **copia** di un vecchio mondo di test con pizza già divisa (per es. `division_state = 7` nella BlockEntity).
2. Dopo qualche tick controllare con i comandi `/data get block X Y Z` che il **BlockState** contenga `division=7` e il `facing` corrispondente.
3. Le linee devono apparire col mattarello, senza bisogno di rompere o riseparare la pizza.
4. Uscire e rientrare: stato, orientamento e linee devono mantenersi corretti.
5. Rompere e riposizionare `raw_pizza`: la divisione e l'orientamento visuali devono essere conservati esattamente come quelli dell'item.

## Test 6 — Regressioni e multiplayer

- Il **bordo stadio 4 rimane di 1 pixel**. Non sono state introdotte texture nuove.
- Stendere a mano o mattarello come prima; dividere/riunire non usa durabilità aggiuntiva.
- Applicazione/rimozione dei topping invariata: nessun ingrediente viene consumato per il solo overlay.
- Con due giocatori, **entrambi** vedono la stessa divisione quando impugnano un oggetto valido (client-side, stato BlockState sincronizzato dal server); se uno cambia divisione, l'altro vede la nuova forma.
- Verificare gioco in terza persona, distanza, due pizze vicine e un mondo con altri mod di rendering. Cercare eventuali linee attraverso i blocchi, artefatti di depth, z-fighting o glitch visivi.
- In caso di calo di FPS vicino a molte pizze, annotare distanza, numero di pizze e condizioni: il raggio iniziale di ricerca può essere ridotto o sostituito da una cache render.

**Limite esplicito della fase:** le linee **non** sono un topping né una divisione fisica. Sono un overlay render di soli dati del BlockState; la pizza continua a essere un unico blocco e le texture degli oggetti devono rispettare i vincoli 16×16/alpha 0–255 stabiliti dall'autore (nessuna texture modificata qui).
