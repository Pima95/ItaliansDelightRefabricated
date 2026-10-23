# Pizza 1.0.0 — Base cottura (fase 4A storica, aggiornata dopo 4B)

> **AGGIORNAMENTO DEL 17 OTTOBRE 2026, PRIORITARIO rispetto alle vecchie specifiche:** l'autore ha testato il Pizza Oven e richiesto il comportamento di una **fornace vanilla**. Di conseguenza **non esiste più progresso di cottura sull'item `raw_pizza` né sulla pizza piazzata**. La prima implementazione 4A (che salvava `bake_progress` nel componente) è stata ritirata. Invece il Pizza Oven possiede un contatore interno `CookTime`, normalmente persistente con il chunk del forno, ma che si **azzera alla rimozione o sostituzione dell'input**.

## Componenti ancora validi

- `PizzaItemData`: conserva la `PizzaComposition` completa (quattro quarti, maschere, ingredienti e ItemStack originari) e il flag legacy mozzarella. Il campo sperimentale `bake_progress`, presente negli oggetti delle prime build 4A, **non è più letto né scritto dalla nuova versione**. Il codec resta in grado di leggere gli altri campi già salvati.
- `PizzaBaking`: `MAX_PROGRESS = 1200` unità; `advance(int current, Oven oven, int activeTicks)` calcola il progresso numerico della **macchina**, non modifica item. Le velocità provvisorie restano Pizza Oven 3 unità/tick (~20 secondi), affumicatore 2 unità/tick (~30 s) e fornace 1 unità/tick (~60 s). Per ora soltanto il Pizza Oven è collegato alla logica.
- `PizzaBaking.finish(rawPizza, ovenProgress)`: prepara la pizza cotta con la stessa composizione soltanto se **il forno** ha raggiunto 1200 unità. I contenitori sono contati **per applicazione originale**, non per parte/quarto della pizza: pomodoro sulla metà sinistra e panna sulla metà destra = **2 ciotole**.
- Il nuovo item `cooked_pizza` è presente, **non commestibile** direttamente e non ancora piazzabile. Il modello riusa temporaneamente la grafica `raw_pizza`; tooltip dei condimenti e porzioni non sono ancora disponibili.
- Nel Pizza Oven il fuoco **consuma sempre il combustibile già acceso**, anche quando inattivo, come vanilla. Le ciotole vanno all'hopper direttamente sotto se ha spazio; quelle in eccesso vengono espulse come drop.

## Verifiche

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

1. Prendere una pizza dall'input dopo metà cottura e reinserirla: **freccia azzerata**. I topping non si perdono.
2. Chiudere la GUI lasciando la pizza nel forno: il progresso continua. Un riavvio con pizza ancora nel forno deve preservare il contatore del forno, non sull'item.
3. Il fuoco prosegue e la sua barra diminuisce anche dopo che il risultato è pronto.
4. Mettere due salse su **due metà distinte** della pizza e terminare la cottura: aspettarsi **2 ciotole** (possibilmente un unico stack di quantità 2).
5. Posizionare un hopper sotto il forno: deve ricevere le ciotole e, tramite lo slot output, la pizza cotta.
6. **Non richiedere per ora verifica visiva degli ingredienti nella pizza cotta:** la sua collocazione nel mondo e i tooltip non sono ancora implementati.

Il protocollo dettagliato è in [PIZZA_OVEN_IMPLEMENTATION_TESTS.md](PIZZA_OVEN_IMPLEMENTATION_TESTS.md). Tutte le altre note di questa cartella che menzionano `bake_progress` nell'item sono **storiche e superate** dal comportamento vanilla richiesto successivamente.
