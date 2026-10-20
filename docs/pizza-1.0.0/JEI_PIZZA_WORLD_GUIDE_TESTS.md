# Pizza Update 1.0.0 — Guida JEI alla preparazione nel mondo

**Data:** 20 ottobre 2026  
**Ramo:** `feature/pizza-1.0.0`  
**Stato:** codice pubblicato, revisione della leggibilità in compilazione automatica; test grafici e in gioco da eseguire.

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

## Correzione leggibilità — 20 ottobre 2026

L'autore ha riscontrato testo sovrapposto/tagliato nella guida JEI. La prima implementazione utilizzava un pannello `170×100`, con soltanto **34 pixel** disponibili per la descrizione e una nota che iniziava a `y=78`, prima che le descrizioni lunghe potessero andare completamente a capo.

La categoria è stata modificata a **170×136 pixel**. Le tre aree sono ora separate:
- **Titolo:** `y=31`, altezza 18, grassetto e centrato.
- **Istruzioni:** `y=53`, altezza 51, testo normale con wrapping automatico di JEI.
- **Nota:** `y=110`, altezza 24, colore grigio scuro per differenziarla dal testo principale.

Le sette schede, i loro identificativi/segnalibri e gli item di esempio rimangono invariati. Non sono introdotti asset nuovi e il codice non altera le interazioni della pizza. Verificare in client IT/EN anche le descrizioni più lunghe (pagine 5 e 6), l'assenza di troncamenti/ellipsis e la navigazione di tutte le sette voci: JEI può usare pagine o scorrimento in base allo spazio disponibile e alle opzioni del client. Non assumere che il contatore di pagina debba mostrare sempre `1/7`.

## Correzione navigazione — 20 ottobre 2026

**Problema segnalato:** aprendo la categoria `Pizza Preparation (In World)` dalle ricette della pizza cruda, JEI mostrava **solo «7 · Collect the raw pizza» con contatore `1/1`**, non gli altri sei passaggi. Le sette schede erano già registrate, ma il filtro di ricerca JEI ammetteva soltanto la pagina 7 perché era l'unica ad avere la pizza cruda come ingrediente di **output** visibile.

**Risoluzione:** `PizzaWorldRecipeCategory#setRecipe` usa ora `IRecipeLayoutBuilder.addInvisibleIngredients` con:
- ruolo **OUTPUT**, la pizza cruda Margherita di esempio (navigazione della guida quando si preme **R** sulla pizza);
- ruolo **INPUT**, l'impasto di Farmer's Delight e il mattarello (navigazione quando si preme **U** su impasto o mattarello).

Questi riferimenti **sono solo per l'indice di ricerca JEI**: non disegnano slot extra, non dichiarano che tutti i passaggi producano una pizza e non registrano ricette gameplay aggiuntive. Ogni scheda mantiene i propri oggetti illustrativi, il titolo e le descrizioni.

### Collaudo specifico

- [ ] Aprire le ricette della pizza cruda con **R**, selezionare il tab del mattarello e confermare che **tutti e sette i passaggi** siano navigabili, non soltanto «Raccogli».
- [ ] Premere **U** sul mattarello: la guida permette di raggiungere tutti e sette i passaggi, inclusi posizionamento e raccolta.
- [ ] Premere **U** su `farmersdelight:wheat_dough`: stessa copertura di navigazione.
- [ ] Navigare dall'inizio fino a «Raccogli»: nessuna pagina mancante o duplicata, sia in italiano sia in inglese.
- [ ] Controllare che gli slot a schermo rimangano quelli corretti di ciascun passaggio: i riferimenti di ricerca **non devono comparire come input/output extra**.
- [ ] Controllare che la consultazione tramite ingredienti topping, pizza e mattarello non modifichi le ricette di crafting o gli item reali.

## Checklist build e client

- [ ] Build Java 25 GitHub Actions completa sul commit finale, senza errori di compilazione o caricamento.
- [ ] `runClient` con JEI: la categoria «Preparazione delle pizze (nel mondo)» compare con il **mattarello come icona**, e si possono raggiungere **tutte e 7 le schede** (pagine o scorrimento secondo il layout JEI).
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
