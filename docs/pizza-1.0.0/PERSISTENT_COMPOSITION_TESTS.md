# Test infrastruttura dati persistenti — Pizza Update 1.0.0

> **Fase 3, primo intervento nel branch `feature/pizza-1.0.0`.** Questo intervento aggiunge la BlockEntity e il formato serializzabile dei quattro quarti, **NON** ancora il gesto di inserimento di condimenti sui quarti. Nessun cambiamento previsto ai modelli o alla stesura. Il nuovo codice necessita di **build e collaudo in Minecraft 26.2**.

## File principali

- [PizzaComposition.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaComposition.java): stato immutabile, quattro quarti fisici, configurazione divisione, orientamento, sequenza, seed e codec
- [PizzaToppingApplication.java](../../src/main/java/dev/italiansdelight/common/pizza/PizzaToppingApplication.java): un'applicazione, ID cronologico, identità canonica, stack originale e maschere originaria/residua
- [PizzaDoughBlockEntity.java](../../src/main/java/dev/italiansdelight/common/block/entity/PizzaDoughBlockEntity.java): persistenza `ValueInput`/`ValueOutput`, dati iniziali vuoti
- [PizzaDoughBlock.java](../../src/main/java/dev/italiansdelight/common/block/PizzaDoughBlock.java), [ModBlockEntities.java](../../src/main/java/dev/italiansdelight/common/registry/ModBlockEntities.java): collegamento e registrazione della BlockEntity

## Test A — Build

Aprire PowerShell nella cartella del progetto:

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
```

**Atteso:** nessun errore di compilazione Java 25. Se qualcosa fallisce, salvare **l'errore completo** (classe, file e numero di riga): non tentare correzioni casuali alle API Minecraft 26.2.

## Test B — Blocco pizza e salvataggio del mondo

1. Entrare in **un mondo di prova** in modalità creativa con i permessi per `/data`.
2. Piazzare l'impasto di Farmer's Delight su un blocco approvato: come prima deve comparire il blocco pizza. Stenderlo a mano e poi provare con il mattarello.
3. Prendere le coordinate del **blocco impasto**, non del blocco di supporto, e usare:

```mcfunction
/data get block X Y Z
```

4. Dovrebbe comparire la BlockEntity `italiansdelight:pizza_dough` con un valore `PizzaComposition` che contiene i campi `version`, `division_state`, `orientation`, `next_application_id`, `render_seed` e la lista `applications` vuota. Il formato dovrebbe avere:
   - versione = 1
   - division_state = 0
   - orientation = north (valore tecnico iniziale, non ancora orientamento scelto dal giocatore)
   - next_application_id = 1
   - render_seed = 0 (placeholder non ancora attivato dal rendering)
   - applications = lista vuota
5. Uscire dal mondo, rientrare e ripetere il comando. Verificare che il blocco e lo stato di stesura siano ancora presenti e che la BlockEntity non abbia perso i dati.
6. Rompere la pizza di prova e riposizionare altro impasto: la nuova BlockEntity deve essere inizializzata vuota. **Il drop di una pizza con topping persistenti non è ancora implementato e non è ancora un test valido.**

## Test C — Regressioni del prototipo

- Verificare che gli stadi 0–4 si stendano esattamente come prima.
- Verificare che il bordo della pizza completamente stesa sia ancora **1 pixel**.
- Verificare che il prototipo della mozzarella rimanga quello già esistente, senza far comparire un'applicazione fittizia nello stato persistente.
- Verificare che avviare/chiudere il mondo e usare `/reload` non causi crash.
- Provare anche un mondo creato prima dell'aggiunta della BlockEntity, preferibilmente una **copia di backup**, con un impasto già piazzato.

## Invarianti preparati nel codice (da provare quando saranno attive le interazioni)

- Quattro quarti fisici sempre presenti, anche per una pizza visivamente intera.
- Massimo **sei identità canoniche differenti per quarto**.
- Un ingrediente canonico non compare due volte sullo stesso quarto; al massimo una salsa effettiva per quarto.
- Ogni applicazione usa **un solo item** e conserva l'`ItemStack` originale con i suoi data component.
- L'applicazione conserva maschere `original_mask` e `remaining_mask`, con `remaining_mask` sottoinsieme della prima.
- ID delle applicazioni positivi, unici, crescenti; nessun rimborso durante la semplice persistenza.
- Il formato salva gli ID canonici, **non** i nomi già tradotti o le definizioni JSON.
- Nessun tick aggiuntivo per il contenuto della pizza.

**Non ancora implementati:** divisione con mattarello, ricerca della zona cliccata, transazioni applicazione/rimozione/rimborso, trasferimento a item/pala/forno, sincronizzazione client e rendering finale. La nuova BlockEntity è un contenitore di dati, non una meccanica completa.
