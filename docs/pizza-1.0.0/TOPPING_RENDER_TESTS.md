# Pizza 1.0.0 — Fase 3F: rendering dei condimenti per quarto

> **Implementato su `feature/pizza-1.0.0` — build e test in Minecraft 26.2 DA ESEGUIRE.**
> L'autore ha già confermato che le linee di divisione (ora scure) e l'applicazione/rimozione dei condimenti funzionano. Questa fase cambia soltanto **la sincronizzazione della composizione verso il client e il rendering**, non il consumo, i rimborsi, la logica dei quarti o i drop.

## Novità tecniche

- [PizzaDoughBlockEntity.java](../../src/main/java/dev/italiansdelight/common/block/entity/PizzaDoughBlockEntity.java) implementa `getUpdateTag(HolderLookup.Provider)` e `getUpdatePacket()` usando il codec già esistente. Il server invia il pacchetto anche quando la composizione cambia senza mutare il `BlockState`. Il contenuto dell'item `raw_pizza` non è stato modificato.
- [PizzaToppingRenderer.java](../../src/main/java/dev/italiansdelight/client/render/PizzaToppingRenderer.java) è un `BlockEntityRenderer` solo client, registrato in [ItaliansDelightClient.java](../../src/main/java/dev/italiansdelight/ItaliansDelightClient.java). Estrae dati e stati dei modelli **prima** della fase di disegno, poi disegna le immagini degli **ItemStack originali** degli ingredienti sulla superficie della pizza.
- Il renderer conosce sempre **quattro quarti fisici**, anche se la pizza è ancora intera o le divisioni sono state riunite. L'orientamento usa il primo cardinale salvato. La disposizione è deterministica: fino a 6 rappresentazioni per quarto, sulla base delle maschere `remaining_mask`.
- Le rappresentazioni usano i modelli item **già presenti** in Italian's Delight, Farmer's Delight ed eventuali mod equivalenti: **nessuna texture nuova** e nessuna modifica delle texture preesistenti. La vecchia mozzarella intera del prototipo rimane nel modello del blocco e non viene duplicata dal nuovo renderer.
- L'overlay nero delle divisioni continua a funzionare **solo impugnando il mattarello o un topping solido valido**; al contrario, i condimenti applicati devono essere **sempre visibili**.

## 1. Build

In PowerShell nella cartella del progetto:

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Per una build Java 25 / Minecraft 26.2, inviare **il primo errore completo di compilazione** se presente. Questo commit non è stato compilato in locale dall'assistente.

## 2. Test visivi di base

1. Posizionare e stendere una pizza stadio 4. Applicare `italiansdelight:mozzarella_slice` su tutta la pizza ancora intera.
2. **Atteso:** quattro rappresentazioni distribuite nei quattro quarti fisici; il clic consuma sempre **un solo item**, non quattro.
3. Aggiungere basilico e speck su tutta la pizza: compariranno altre rappresentazioni per ciascun quarto, senza coprire il bordo.
4. Girare intorno alla pizza, uscire e rientrare nel mondo: le posizioni dei condimenti devono rimanere **identiche**, senza animazioni casuali.
5. Verificare che non ci siano oggetti verticali, scomparsi sotto l'impasto, ruotati in modo scorretto o troppo grandi/piccoli. In caso di errore visivo, inviare uno screenshot per regolare orientamento e scala.

## 3. Metà, quarti e riunioni

1. Dividere la base in due metà; mettere speck sulla sinistra, basilico sulla destra. Ogni condimento deve comparire solo nei **due quarti fisici** che appartengono alla metà mirata.
2. Dividere in quattro e aggiungere ingredienti diversi a quarti diversi: i modelli devono restare nelle rispettive posizioni.
3. Riunire i quarti con Shift + mattarello, poi riunire tutta la pizza: i condimenti **non devono spostarsi, estendersi o duplicarsi**; l'unica cosa che sparisce quando non impugni il mattarello sono le linee.
4. Il test delle maschere resta verificabile con `/data get block X Y Z`.

## 4. Rimozione LIFO e rimborso

1. Mettere un singolo basilico sulla pizza intera, poi dividerla in quattro.
2. Usare Shift + clic destro a mano vuota su un quarto: **solo il disegno in quel quarto** deve scomparire; il basilico non deve essere rimborsato finché non si rimuove anche l'ultimo quarto coperto.
3. Rimuovere i restanti tre quarti: verificare che il rimborso avvenga una sola volta e il modello sparisca ovunque.
4. Con più ingredienti, controllare l'ordine LIFO: deve sparire il condimento più recente nella zona puntata.

## 5. Salvataggio, inventario e multiplayer

1. Condire una pizza a quarti differenti, romperla (deve cadere un solo `raw_pizza`), raccoglierla, lanciarla con Q, raccoglierla e riposizionarla: le immagini e le maschere devono tornare senza variazioni.
2. Uscire dal mondo e rientrare: lo stato viene caricato dal codec e mandato ai client dal `getUpdateTag`.
3. In multiplayer, far applicare e rimuovere i condimenti dal giocatore A: il giocatore B vicino deve vedere gli aggiornamenti senza cambiare chunk, rompere blocchi o riconnettersi.
4. Il sistema deve rispettare l'item originale anche per bacon crudo/cotto e altri equivalenti: entrambi hanno la stessa identità canonica ai fini del gameplay, ma l'icona mostrata usa lo stack **realmente impiegato**.
5. I dati delle applicazioni e i rimborsi **non** devono cambiare quando un client è disconnesso o quando vengono ricaricate le risorse.

## 6. Regressioni e limiti dichiarati

- L'overlay nero dei tagli (fase 3E) rimane invariato.
- Il blocco pizza conserva lo stesso bordo 1px, hitbox, click, drop e comportamento vanilla degli item a terra.
- `tomato_sauce`, `cream_bowl`, `ricotta` **non sono ancora applicabili**: manca la gestione definitiva dei recipienti. Non ci sono nuovi item tagliati per carota e patata.
- La grafica 2.5D può essere perfezionata dopo il test con piccoli modelli dedicati e texture 16×16. Ogni nuova texture futura rispetterà: **RGBA con alpha 255 sui pixel dell'item, alpha 0 solo fuori dalla sagoma, niente pixel semitrasparenti**.
- Il rendering copia i modelli ItemStack per applicazione e per quarto, con al massimo 24 simboli su una pizza. Non crea render tickers o oggetti server.
