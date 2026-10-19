# Pizza Update 1.0.0 — Guida JEI per la cottura

**Data:** 19 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Stato:** implementazione pubblicata; verificare la build CI più recente e il comportamento in Minecraft.

## Obiettivo

La categoria JEI **«Cottura delle pizze (guida)» / «Pizza Baking (Guide)»** documenta i **tre metodi già esistenti** per cuocere la stessa pizza, senza registrare nuove ricette effettive:

| Stazione | Durata base | Fonte del gameplay |
|---|---:|---|
| Pizza Oven (`italiansdelight:pizza_oven`) | 400 tick = **20 secondi** | `PizzaOvenBlockEntity`, `PizzaBaking.Oven.PIZZA_OVEN` |
| Affumicatore (`minecraft:smoker`) | 600 tick = **30 secondi** | `PizzaSmokingRecipe` |
| Fornace (`minecraft:furnace`) | 1200 tick = **60 secondi** | `PizzaSmeltingRecipe` |

Tutte le anteprime riutilizzano l'esempio di **Margherita** già presente in `PizzaCraftingJeiRecipes`; il risultato è ottenuto mediante `PizzaBaking.finish`, conservando componenti, quarti, condimenti e nome traducibile. Non si assume che tutte le pizze debbano essere Margherita: essa è **solo un esempio visuale**.

**Carbone e blocco della stazione** sono inseriti come slot **solo illustrativi** (`RecipeIngredientRole.RENDER_ONLY`). Il carbone **non è obbligatorio**: qualsiasi combustibile vanilla valido è accettato. La pala non è necessaria e non viene consumata. Nessuna cottura può bruciare la pizza; il timer rimane nel forno e si azzera con l'estrazione dell'input crudo. Le ciotole vengono restituite **solo a cottura terminata**, una per applicazione originale di salsa/panna/ricotta, non per quarto.

La categoria usa come catalizzatore il Pizza Oven e contiene anche la comparazione con fornace e affumicatore; non sostituisce né altera le categorie vanilla di JEI. La freccia di progresso della GUI del Pizza Oven apre la guida tramite `addRecipeClickArea` (x=79, y=34, 24×16).

## File della modifica

- `PizzaBakingJeiRecipe.java` — dati immutabili/copied dei tre esempi
- `PizzaBakingJeiRecipeTypes.java` — tipo dedicato alla guida
- `PizzaBakingJeiRecipes.java` — creazione degli esempi tramite le meccaniche originali
- `PizzaBakingRecipeCategory.java` — slot standard per pizza cruda, forno, carbone illustrativo e pizza cotta
- `ItaliansDelightJeiPlugin.java` — registrazione categoria, ricette, catalizzatore e area cliccabile
- `lang/it_it.json`, `lang/en_us.json` — intestazioni, tempi, combustibili e note

## Checklist in gioco

- [ ] `gradlew clean build` su JDK 25: nessun errore.
- [ ] `runClient` con JEI: nessun crash e nessuna categoria precedente scomparsa.
- [ ] `R` su `italiansdelight:cooked_pizza`: aprire «Cottura delle pizze (guida)»; trovare esattamente **tre esempi**.
- [ ] Confrontare la stessa **Pizza Margherita** tra le tre anteprime: i nomi del prodotto crudo/cotto e i dati del tooltip rimangono uguali.
- [ ] Passare il mouse sul blocco stazione: **Pizza Oven 20 s**, **Affumicatore 30 s**, **Fornace 60 s**; traduzioni IT/EN corrette.
- [ ] Passare il mouse sul carbone: testo esplicito «solo un esempio; qualsiasi combustibile vanilla valido».
- [ ] Fare clic sull'**indicatore a freccia** della GUI del Pizza Oven e controllare che JEI apra la guida.
- [ ] La categoria deve essere disponibile anche tramite `U` su `raw_pizza` (item con condimenti) e tramite gli usi del Pizza Oven.
- [ ] Nessun carburante o macchina viene consumato come ingrediente della «ricetta» JEI: sono dimostrativi.
- [ ] Cuocere realmente una Margherita e una pizza assortita nei tre forni; verificarne composizione, nome, quarti e contenitori, senza duplicazioni.
- [ ] Verificare che le precedenti cinque anteprime di crafting, le ricette Cheese Vat, stagionatura, uncino e sale rimangano consultabili.
- [ ] Test multiplayer/server dedicato e assenza di codice client JEI lato server.

**Nota:** una build riuscita dimostra soltanto la compilazione del codice. I controlli JEI visivi e di gameplay richiedono il client Minecraft e rimangono aperti finché l'autore non li conferma.
