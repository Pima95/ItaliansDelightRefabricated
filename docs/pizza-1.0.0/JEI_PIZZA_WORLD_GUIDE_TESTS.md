# Pizza Update 1.0.0 — Guida JEI alla preparazione nel mondo

**Data:** 20 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Stato:** codice pubblicato, build GitHub Actions da confermare sul commit finale; test grafici e in gioco da eseguire.

## Obiettivo

Nuova categoria JEI **«Preparazione delle pizze (nel mondo)» / «Pizza Preparation (In World)»**, con **sette pagine informative** che riproducono le interazioni già implementate in `PizzaDoughPlacement`, `PizzaToppingInteractions` e `PizzaDoughBlock`. Non registra nuove ricette di crafting o di cottura, non aggiunge item o texture, e non modifica gameplay.

| Pagina | Oggetti illustrativi | Azione effettiva |
|---|---|---|
| 1. Posiziona | 1 `farmersdelight:wheat_dough` | Clic destro sulla faccia superiore di una superficie idonea; l'impasto è consumato una sola volta quando è posato |
| 2. Stendi a mano | Impasto illustrativo | 4 clic destri **a mano vuota** sull'impasto collocato nel mondo |
| 3. Stendi col mattarello | Impasto + `rolling_pin` riutilizzabile | 1 clic destro completa la stesura; **-1 durabilità solo quando funziona** |
| 4. Dividi / riunisci | Impasto illustrativo + mattarello | Clic destro divide prima in metà e poi in quarti; **Shift + clic destro riunisce** senza usurare il mattarello |
| 5. Aggiungi i condimenti | Salsa, mozzarella a fette, basilico **di esempio** | Clic destro con un topping valido sulla zona desiderata (intera, metà, quarto); **6 tipi max per quarto e al massimo una salsa** |
| 6. Rimuovi i condimenti | Mozzarella illustrativa come output eventuale | **Shift + clic destro a mano vuota** rimuove l'ultima applicazione; **rimborso solo quando tutte le sue parti vengono rimosse** |
| 7. Recupera la pizza | Pizza cruda Margherita con `pizza_contents` | Rompere la pizza preparata (condita o divisa), conservandone quarti, topping e nome nell'item |

**Importante:** gli oggetti nelle pagine di stesura e divisione sono una rappresentazione JEI dell'impasto **già posato nel mondo**. I simboli INPUT/OUTPUT servono a consultazione e segnalibri; non rappresentano ricette da eseguire nella crafting table, né richiedono di consumare più impasti. Solo l'ultima pagina mostra la pizza cruda come vero item ottenuto rompendo la preparazione. L'oggetto mozzarella della pagina di rimozione rappresenta un possibile **rimborso finale**, non un drop dopo una rimozione parziale.

La guida riutilizza gli stack della Margherita da `PizzaCraftingJeiRecipes`, inclusa la pizza finale con i suoi dati persistenti; non rende la Margherita obbligatoria o l'unica pizza realizzabile.

## Implementazione

- `PizzaWorldJeiRecipe`: dati immutabili con sette passi e identificatori stabili delle pagine.
- `PizzaWorldJeiRecipeTypes`: `italiansdelight:pizza_world_preparation`.
- `PizzaWorldJeiRecipes`: sette anteprime senza nuove ricette di gameplay.
- `PizzaWorldRecipeCategory`: icona mattarello, slot standard JEI, testo localizzato IT/EN e identificativi sintetici `italiansdelight:/pizza_world/<passo>` per i segnalibri.
- `ItaliansDelightJeiPlugin`: registrazione della categoria, sette pagine e mattarello come stazione/strumento di consultazione.
- `lang/it_it.json`, `lang/en_us.json`: titolo, azione e nota per ogni passaggio.

Non vengono toccate le meccaniche di Pizza Oven, altre fornaci, pala, divisioni, durabilità o whitelist dei topping. La ricetta shapeless uniforme e i suoi cinque esempi JEI restano separati.

## Checklist build e client

- [ ] Build Java 25 GitHub Actions completa sul commit finale, senza errori di compilazione o caricamento.
- [ ] `runClient` con JEI: la categoria «Preparazione delle pizze (nel mondo)» compare con il **mattarello come icona** e **7 pagine (1/7 ... 7/7)**.
- [ ] Pagine 1–7 ordinate correttamente, titoli e descrizioni leggibili e tradotti in **italiano e inglese**, senza sovrapposizioni o tagli.
- [ ] Ogni pagina mostra l'**icona del segnalibro JEI**; salvare e riaprire almeno la pagina «Stendi a mano», «Dividi» e «Raccogli».
- [ ] `U` su `wheat_dough` o sul mattarello mostra questa guida; le pagine dei topping sono reperibili anche dai relativi ingredienti.
- [ ] Nessuna pagina viene mostrata nel ricettario della crafting table come una **ricetta inventata** e non compare alcun pulsante di trasferimento fittizio.
- [ ] Pagina del mattarello: l'oggetto appare come **strumento non consumato**, non come output di crafting.
- [ ] Pagina dei condimenti: appare la Margherita come **solo esempio**, non vengono imposti ingredienti o salsa su tutti i gusti.
- [ ] Pagina della rimozione: la descrizione specifica il **rimborso dopo l'ultima porzione eliminata**.
- [ ] Pagina della raccolta: l'output mantiene il nome **Pizza Margherita**, i tooltip e il componente `pizza_contents`.
- [ ] Test regressione: JEI crafting libero (5 anteprime), Pizza Oven (20s e segnalibro), Smelting (60s), Smoking (30s), cheese/rack/hook/sale rimangono funzionanti.
- [ ] Test in gioco: una base stesa a mano, una con mattarello, divisioni/riunioni, condimenti per zona, restituzione di topping e raccolta della pizza.
- [ ] Test su server dedicato/multiplayer: nessun codice client JEI caricato lato server e nessuna alterazione degli item.

**Limiti:** una build passata verifica la compilazione, non il risultato grafico. La disponibilità in ricerca JEI, l'effettiva presenza dei segnalibri, il wrapping del testo e le interazioni vanno confermate dall'autore in Minecraft.
