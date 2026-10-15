# Pizza 1.0.0 — Fase 4A: dati di cottura persistenti e pizza cotta

> **Stato (15 ottobre 2026): codice pubblicato, build Java 25/Minecraft 26.2 e test in gioco DA ESEGUIRE.**
> L'autore ha confermato funzionante la fase 3H (una sola salsa tra pomodoro, panna e ricotta per quarto). **In questa fase NON è ancora possibile cuocere una pizza in gioco**: non sono stati registrati né un Pizza Oven utilizzabile, né ricette di forno/affumicatore vanilla. È una base riutilizzabile per collegare le tre macchine senza perdere dati o ciotole.

## Implementazione

- `PizzaItemData`: la pizza cruda ora memorizza `bake_progress` (da **0 a 1200**) nel data component `pizza_contents`, oltre ai campi invariati `composition` e `legacy_mozzarella`. **Il nuovo campo nel codec è opzionale (default 0)**, così gli item dei vecchi salvataggi rimangono leggibili.
- `PizzaDoughBlockEntity`: salva/carica `BakeProgress` (default 0 nei vecchi mondi), non aumenta il progresso autonomamente e lo invia ai client con il normale sistema di aggiornamento. Raccogliendo e riposizionando la pizza, `PizzaDoughBlock` e `PizzaDoughPlacement` trasferiscono quel progresso senza alterare tagli e condimenti.
- `PizzaBaking`: logica pura e condivisa, senza modifiche agli inventari. Valori temporanei **normalizzati a 1200 unità**: Pizza Oven +3 unità/tick (400 tick attivi, 20 s), affumicatore +2 (600 tick, 30 s), fornace +1 (1200 tick, 60 s). Passare da un metodo all'altro conserva la stessa percentuale di completamento.
- Registrato `italiansdelight:cooked_pizza` come **singolo item NON commestibile** con lo stesso data component persistente dell'item crudo. Il modello JSON riusa temporaneamente la texture 16×16 `raw_pizza`: nessuna nuova PNG è stata creata. Le texture definitive restano TODO.
- `PizzaBaking.finish()` prepara `BakingResult`: 1 `cooked_pizza` con composizione/maschere/stack originali conservati, più una `minecraft:bowl` per **ogni applicazione originaria ancora presente** di `tomato_sauce`, `cream_bowl` o `ricotta`. La maschera 15 (quattro quarti) restituisce **una sola ciotola**, non quattro. Le applicazioni interamente rimosse in precedenza non vengono contate.
- **Sicurezza anti-duplicazione:** `finish()` è un helper puro, non crea item nel mondo. Il successivo codice del forno dovrà verificare capacità e completamento, **consumare un input** e produrre risultato e recipienti **una sola volta in una singola transazione server**. Nessun giocatore può ottenere una ciotola con la sola aggiunta/rimozione del topping, e nulla viene generato automaticamente dalla pura chiamata al helper.

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

Se fallisce, riportare **il primo errore di compilazione completo**, non solo l'ultima riga. Le API e il bytecode devono essere quelli di Minecraft 26.2/Java 25.

## Test immediati possibili in Minecraft

1. Avviare il mondo con le vecchie pizze (meglio una copia/backup); raccolta, divisioni, salse esclusive e rimborsi devono continuare come nella fase 3H.
2. Fare `/give @s italiansdelight:cooked_pizza`: l'item deve esistere, mostrarsi con l'icona **provvisoria** uguale a `raw_pizza`, e riportare il nome tradotto `Pizza cotta`. **Non deve essere mangiabile** intera.
3. Il nuovo item cotto **non è ancora piazzabile come pizza cotta servibile**: la futura fase di fette servirà a questo. Non utilizzare l'item di debug per testare la cottura.
4. Rompere un impasto stadio 4 integro e pulito, mai parzialmente cotto: deve ancora tornare il normale `farmersdelight:wheat_dough`. Rompere una pizza con condimenti o divisioni deve ancora restituire `raw_pizza` con tutti i dati.
5. Controllare che in questa fase **nessuna fornace vanilla** consumi `raw_pizza`: non è stata ancora aggiunta la ricetta dinamica per trasferire il data component.

## Test di regressione da eseguire quando saranno collegati i forni

| Scenario | Risultato atteso |
|---|---|
| Pizza cruda vecchia senza `bake_progress` | Progress 0, composizione invariata |
| Pizza Oven per 200 tick attivi | Progress 600 su 1200 (50%) |
| Affumicatore per 300 tick attivi | Progress 600 su 1200 (50%) |
| Fornace per 600 tick attivi | Progress 600 su 1200 (50%) |
| 100 tick Pizza Oven, poi 450 tick forno | 300 + 450 = 750 (62,5%) |
| Macchina senza combustibile o vuota | Progress invariato, non consumare combustibile |
| Pizza con progress 900 nel forno, estratta e rimessa nel mondo | Alla rottura conserva progress 900 e tutti i topping |
| Pizza pulita senza divisioni con progress 1 | Rompendola ritorna `raw_pizza`, **non** `wheat_dough` |
| Una applicazione di pomodoro con maschera 15, poi cottura completa | Una pizza cotta + **una** ciotola (non quattro) |
| Pomodoro su un quarto, panna su un altro, ricotta su un terzo | Una pizza cotta + **tre** ciotole totali |
| Applicazione di panna interamente rimossa prima del forno | Nessuna ciotola dalla panna: il giocatore ha già riavuto l'item pieno |
| Pizza non ancora al 100% | La conversione deve essere rifiutata, senza output/ciotole |
| Forno con output occupato o hopper concorrenti | Nessuna perdita, duplicazione o conversione parziale |
| Pizza completa dopo 1200 unità | Un solo `cooked_pizza`, niente bruciatura, stessi quarti |

## Prossimi incrementi

1. **Fase 4B — Pizza Oven funzionante:** blocco 1×1 orientabile, tre slot e **GUI vanilla fornace**. Combustibile vanilla, una pizza totale, avanzamento salvato, pausa senza combustibile e senza sprechi a forno inattivo. Collegare `PizzaBaking` tramite transazione atomica e collocare correttamente le ciotole rimanenti.
2. **Fase 4C — fornace/affumicatore vanilla:** soluzioni specifiche per preservare `pizza_contents` anche nei due metodi vanilla e recuperare ciotole senza duplicazione. Evitare ricette statiche che perdono NBT/components.
3. **Fase 4D — pizza cotta nel mondo, quattro fette e nomi/nutrizione dinamici:** la pizza cotta intera non si mangia; ciascuna fetta usa soltanto il contenuto del proprio quarto. Preservare la regola aggiornata di una salsa per quarto e il preset Principessa sospeso.
4. **TODO grafica** già approvati: nuova texture `raw_pizza`, texture cotta definitiva, sostituzione dell'attuale renderer item-frame per i condimenti; usare PNG RGBA 16×16, alpha 255 sui pixel visibili, alpha 0 solo fuori sagoma.
