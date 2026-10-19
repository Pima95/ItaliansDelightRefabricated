# Pizza Update 1.0.0 — JEI per il crafting libero

**Data:** 19 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Stato:** codice pubblicato, **BUILD JAVA 25 SUPERATA SU GITHUB ACTIONS; TEST NEL CLIENT ANCORA PENDENTI**.

## Scopo

Il crafting shapeless (già confermato funzionante dall'autore) accetta **esattamente un impasto di Farmer's Delight e da uno a sei topping diversi approvati**, disposti liberamente. Questa singola ricetta speciale (`italiansdelight:pizza_crafting`) costruisce output dinamici con nome e dati completi, ma `CustomRecipe` non fornisce una ricetta vanilla classica enumerabile dal ricettario.

Per rendere JEI utile senza restringere la meccanica sono state aggiunte **cinque anteprime didattiche**, non cinque nuove ricette di gameplay. La categoria si chiama «Pizze personalizzabili (esempi)» / «Customizable Pizzas (Examples)», utilizza lo stesso banco da lavoro vanilla ed è ricercabile tramite gli ingredienti e l'output.

## Esempi disponibili

| Condimenti esposti | Esempio |
|---|---|
| `tomato_sauce` | Rossa, 1 condimento |
| `tomato_sauce`, `mozzarella_slice`, `basil` | Margherita, 3 condimenti |
| `mozzarella_slice`, `gorgonzola_wedge`, `provolone_slice`, `grated_parmigiano_reggiano` | Quattro Formaggi Bianca, 4 condimenti |
| `mozzarella_slice`, `tomato_slice`, `cabbage_leaf`, `pumpkin_slice`, `basil` | Orto Disponibile, 5 condimenti |
| `tomato_sauce`, `mozzarella_slice`, `basil`, `tomato_slice`, `cooked_bacon`, `speck_slice` | Esempio dinamico, 6 condimenti |

Tutti gli item sono già presenti in Italian's Delight / Farmer's Delight Refabricated. Nessuno è un topping soltanto pianificato (carota/patata a fette restano da introdurre). Ogni esempio:
- Usa un impasto `farmersdelight:wheat_dough`.
- Usa la stessa **identità canonica** del catalogo approvato e un'applicazione originale per ogni ingrediente.
- Copre tutti e quattro i quarti e impiega lo stesso codice `PizzaDisplayNames.nameWholeStack` della ricetta effettiva.
- Ha al massimo una salsa (compresi gli spalmabili cremosi), non ripete identità canoniche e rispetta il limite di sei.
- Produce una preview di `italiansdelight:raw_pizza` completa di `pizza_contents`, evitando un output vuoto/generico. I nomi sono componenti traducibili e rispettano la lingua client.

**Nota importante:** il client JEI usa **soltanto item nativi già presenti** per generare le preview, senza leggere le equivalenze server provenienti dai datapack. Questo garantisce che le anteprime non dipendano da sincronizzazioni aggiuntive. Le equivalenze opzionali approvate **continuano a essere accettate dal crafting effettivo** secondo le regole del server, ma non sono enumerate da JEI. Eventuali item opzionali mancanti portano a ignorare un esempio, non a creare una ricetta inesistente.

## File introdotti

- `src/main/java/dev/italiansdelight/integration/jei/PizzaCraftingJeiRecipe.java` — wrapper immutabile con copie difensive degli ItemStack.
- `src/main/java/dev/italiansdelight/integration/jei/PizzaCraftingJeiRecipeTypes.java` — tipo JEI statico per gli esempi.
- `src/main/java/dev/italiansdelight/integration/jei/PizzaCraftingJeiRecipes.java` — cinque preview valide costruite dai nomi canonici.
- `src/main/java/dev/italiansdelight/integration/jei/PizzaCraftingRecipeCategory.java` — layout shapeless 3×3 con slot vanilla forniti da JEI, output e descrizione al passaggio del mouse fra griglia e risultato.

## File aggiornati

- `integration/jei/ItaliansDelightJeiPlugin.java`: nuova categoria, registrazione cinque esempi, stazione di crafting `Blocks.CRAFTING_TABLE`.
- `assets/italiansdelight/lang/{it_it,en_us}.json`: tre chiavi aggiuntive per titolo e spiegazione. Nessuna texture aggiunta o sovrascritta.
- `docs/pizza-1.0.0/ROADMAP.md` e `PIZZA_CRAFTING_TESTS.md`: stato e collaudi.

## Checklist di collaudo

- [x] Build Java 25 senza errori di compilazione JEI, registri e classe `GuiGraphicsExtractor` (`gradlew clean build`). **Superata in GitHub Actions il 19 ottobre 2026** sul commit `394a42abeded0adf34416041c86a486f88cbe904`: [log della build](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/39321402134).
- [ ] `runClient`: nessun crash durante caricamento/modlist/JEI o ingresso in mondo.
- [ ] Nel JEI aprire la categoria «Pizze personalizzabili (esempi)», con icona del banco da lavoro: **cinque esempi**, ciascuno con gli ingredienti reali in griglia 3×3, output di pizza cruda con nome corretto.
- [ ] JEI `R` su `raw_pizza` o `U` su `wheat_dough`, tomato sauce, mozzarella: controllare che la categoria compaia e non venga nascosta come ricetta speciale vanilla.
- [ ] L'output della Margherita mantiene `pizza_contents` e il suo nome **Pizza Margherita**; l'esempio da sei ingredienti mostra il suo nome dinamico, non un nome fisso inesistente.
- [ ] Le descrizioni sono traducibili IT/EN e indicano che **sono esempi**, non il set completo; nessun tooltip ingannevole sull'obbligatorietà della salsa.
- [ ] Preparare una combinazione **non presente** fra i cinque esempi: deve funzionare ugualmente nel banco da lavoro, con la stessa quantità consumata e gli stessi dati.
- [ ] Verificare rimborsi delle ciotole: nessun resto nel banco da lavoro; una ciotola per applicazione a cottura terminata.
- [ ] Due salse, due tipi canonici duplicati o oltre sei topping sono ancora rifiutati (JEI non deve modificare il comportamento).
- [ ] JEI esistente: categorie Cheese Vat, rack, uncino/essiccazione e sale tutte presenti e senza sovrapposizioni.
- [ ] Test su server dedicato con e senza equivalenze datapack: nessun crash del client, gli esempi funzionano e le regole gameplay restano gestite dal server.
- [ ] JEI non modifica modelli, texture o la posa della pizza peel già approvati.

## Build automatizzata

La compilazione viene verificata automaticamente per ogni push sul ramo `feature/pizza-1.0.0` dal workflow [Pizza 1.0.0 - Build](../../.github/workflows/pizza-build.yml). Esegue `bash ./gradlew clean build --no-daemon --stacktrace` con JDK 25. La build verifica la compilazione e i controlli Gradle configurati, **non** prova avvio client, caricamento JEI, GUI o gameplay: le checklist runtime restano aperte.

## Limite di verifica

La revisione statica confronta le firme con l'API JEI 26.2 in uso nelle altre categorie del repository; **la compilazione automatica su GitHub Actions è riuscita, ma non è stata eseguita una prova GUI reale nel client Minecraft**. La nuova categoria è pronta per il collaudo dell'autore. Nella fase 6 rimane da completare la documentazione generale di lavorazioni, forni e condimenti (anche oltre i cinque esempi illustrativi).
