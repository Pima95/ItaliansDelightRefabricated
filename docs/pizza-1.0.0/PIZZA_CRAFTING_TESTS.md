# Pizza Update 1.0.0 — Crafting shapeless delle pizze uniformi

**Data:** 19 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Stato aggiornato 19 ottobre 2026:** l'autore **ha confermato funzionante il crafting shapeless in gioco**. Restano da eseguire/annotare separatamente i casi limite della checklist, il multiplayer, JEI e la verifica su server dedicato.

## Decisione approvata dall'autore

La preparazione nel mondo resta il sistema completo, che permette quattro quarti differenti; il crafting è una **alternativa più semplice** per produrre pizze **uniformi**.

- **Base obbligatoria:** esattamente **1 `farmersdelight:wheat_dough`** (Farmer's Delight Refabricated), consumata.
- **Condimenti:** da **1 a 6 item**, ciascuno in uno slot, presi dall'allowlist attiva di Italian's Delight / Farmer's Delight o dalle equivalenze esplicite già autorizzate attraverso `PizzaToppingMappings`.
- **Griglia:** disposizione **shapeless**, ordine e posizioni irrilevanti, incluse caselle separate e input non adiacenti. Minecraft può usare anche la piccola griglia personale per combinazioni abbastanza piccole: è il comportamento naturale di una ricetta shapeless. Le ricette con 4–6 condimenti richiedono necessariamente il tavolo 3×3.
- **Unicità:** un solo ingrediente per ogni **identità canonica**; `bacon` e `cooked_bacon` contano come un duplicato, come le eventuali equivalenze confermate di altre mod.
- **Salse:** **al massimo una** fra salsa di pomodoro, panna e ricotta, con salsa completamente facoltativa.
- **Esclusioni:** ingredienti grandi/interi, ID non riconosciuti, ingredienti soltanto pianificati (carota/patata affettate ancora non implementate) o sette o più condimenti sono rifiutati.
- **Output:** **1 `italiansdelight:raw_pizza`**, già formata/stesa, con `pizza_contents` che contiene **quattro quarti identici**, `divisionState=0`, orientamento predefinito `north`, `legacyMozzarella=false` e un'applicazione originale per topping. Nome dinamico o riconosciuto via `PizzaDisplayNames`.
- **Consumo:** **1 dough + 1 unità per slot-condimento**; nessuna restituzione immediata di ciotole/bottiglie dal banco da lavoro. Per pomodoro, panna e ricotta la ciotola originale rimane rappresentata nell'applicazione e viene restituita **una sola volta** alla cottura completa della pizza, come nella preparazione manuale. Non creare due rimborsi con `getCraftingRemainder`.
- **Compatibilità:** la pizza ricavata dal crafting può essere trasportata con la peel, posizionata nel mondo e cotta nel Pizza Oven, fornace vanilla o smoker, e poi servita in quattro fette con i rispettivi condimenti.

## Implementazione

| File | Funzione |
|---|---|
| `src/main/java/dev/italiansdelight/common/crafting/PizzaCraftingRecipe.java` | Ricetta shapeless **dinamica** (estende `CustomRecipe`), validazione e creazione degli ItemStack personalizzati; `getRemainingItems` disabilita i rimborsi prematuri |
| `src/main/java/dev/italiansdelight/common/registry/ModRecipes.java` | Registra `italiansdelight:pizza_crafting` e sincronizza il relativo serializer |
| `src/main/resources/data/italiansdelight/recipe/pizza_crafting.json` | Carica una sola ricetta speciale con `type: italiansdelight:pizza_crafting` senza creare centinaia di varianti fisse |

**Perché non una normale `minecraft:crafting_shapeless`?** In JSON una ricetta standard contiene un elenco **fisso** di ingredienti e un output senza i dati provenienti dagli slot. Qui la quantità e la combinazione sono variabili e l'output deve mantenere ID canonici, applicazioni originali (es. equivalenze crude/cotte) e nome.

**Nota JEI/ricettario aggiornata:** `CustomRecipe.isSpecial()` è `true` in Minecraft 26.2. Per rendere consultabile la meccanica sono stati aggiunti **cinque esempi validi** nella categoria JEI «Pizze personalizzabili (esempi)», senza sostituire la ricetta dinamica, limitare combinazioni o creare altre ricette di gameplay. **Implementazione pubblicata ma visualizzazione JEI ancora da testare**. [Checklist JEI](JEI_PIZZA_CRAFTING_TESTS.md). Il libro ricette vanilla non mostra automaticamente tutte le combinazioni libere.

**Nota multiplayer/equivalenze:** la validazione server utilizza le equivalenze lette dai datapack. Verificare separatamente la presenza della preview sul client multiplayer in combinazioni che usano mapping esterni: le mappe non sono ancora necessariamente sincronizzate al client. I topping **nativi** funzionano senza tale estensione.

## Come provare

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

### Test funzionali e anti-duplicazione

- [ ] **Margherita:** 1 FDR wheat dough + 1 tomato sauce + 1 `mozzarella_slice` + 1 basil, disposti senza forma fissa → 1 pizza cruda con nome Margherita e stesso trio su tutti e quattro i quarti.
- [ ] **Bianca:** 1 dough + 1 mozzarella_slice senza salsa → pizza Bianca o nome dinamico corrispondente alla composizione esatta, senza salsa obbligatoria.
- [ ] **Rossa:** 1 dough + 1 tomato sauce → 1 pizza Rossa (senza altre aggiunte).
- [ ] **Massimo 6:** 1 dough + 6 condimenti diversi validi → 1 pizza, tutti e 6 su ciascuno dei 4 quarti. Provare ingredienti in slot discontinui della 3×3.
- [ ] **0 condimenti:** solo dough → nessun output pizza.
- [ ] **7 condimenti o più:** nessun output pizza.
- [ ] **Due dough:** nessun output pizza.
- [ ] **Duplicati:** due `mozzarella_slice` in slot diversi → nessun output; bacon + cooked_bacon → nessun output (stessa identità canonica).
- [ ] **Più salse:** pomodoro + ricotta o panna → nessun output, indipendentemente dalla disposizione.
- [ ] **Vietati:** `mozzarella` intera, ruote formaggio, carne non tagliata, item casuali → nessun output, ingredienti non consumati.
- [ ] **Consumo:** dopo aver preso l'output, ogni ingrediente occupato diminuisce di **uno**, mai di quattro; le quantità residue rimangono nella griglia e una seconda craft segue la normale semantica vanilla.
- [ ] **Rimborsi:** craft con tomato sauce / cream bowl / ricotta → **nessuna ciotola anticipata**. Cottura completata restituisce esattamente una ciotola per ciascuna applicazione che la richiede.
- [ ] **Dati:** rompere la pizza posizionata e riprenderla conserva nomi, ingredienti, item originali e quattro quarti; senza separare i quarti prima, produrre 4 fette con uguali condimenti.
- [ ] **Cottura:** test in Pizza Oven, fornace vanilla e smoker, con output realmente composto e nomi IT/EN, senza componenti persi.
- [ ] **Regressioni:** crafting normale della mod (pala, uncino, mattarello, cheese vat), altre ricette vanilla e Farmer's Delight restano funzionanti.
- [ ] **Reload/server:** avvio mondo, `/reload`, server dedicato con mod e datapack, chiusura e riapertura: la ricetta resta disponibile; salvataggi invariati.
- [ ] **Equivalent datapack:** mappatura esterna esplicita riconosciuta dal server, no falsi positivi su item non mappati; verificare il comportamento della preview in client.

**Criterio di completamento:** il successo di un solo crafting non basta; la ricetta deve compilare, consumare i materiali esatti e conservare componenti e nomi durante l'intero ciclo fino alla cottura e alle fette.

**Stato dei test:** nessuna compilazione o prova Minecraft eseguita dall'assistente; checklist in attesa della conferma dell'autore.
