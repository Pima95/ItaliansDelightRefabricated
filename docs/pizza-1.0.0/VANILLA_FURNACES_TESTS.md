# Pizza Update 1.0.0 — Fase 5: fornace e affumicatore vanilla

**Stato aggiornato 19 ottobre 2026 (conferma dell'autore):** **cottura nella fornace vanilla e nell'affumicatore confermata funzionante in gioco**. Codice pubblicato su `feature/pizza-1.0.0`. La conferma riguarda il funzionamento delle due postazioni; **non dimostra automaticamente** l'esito di tutti i casi limite sotto elencati (hopper pieni, ciotole, JEI, dati di pizze assortite, server dedicato, crash su altri modpack). Non marcare come superate quelle prove senza test specifici. Pizza Oven approvato in precedenza: **non modificato**.

## Cosa è stato aggiunto

- `src/main/resources/data/italiansdelight/recipe/pizza_smelting.json` — `raw_pizza` → `cooked_pizza`, `cookingtime=1200` (60 s).
- `src/main/resources/data/italiansdelight/recipe/pizza_smoking.json` — `raw_pizza` → `cooked_pizza`, `cookingtime=600` (30 s).
- `PizzaSmeltingRecipe` e `PizzaSmokingRecipe`: ricette con tipi vanilla SMELTING e SMOKING ma serializer dedicati; `assemble` genera il risultato con `PizzaBaking.finish()`, così non si perde `pizza_contents` né il nome derivato dai topping.
- `ModRecipes`: registra e sincronizza i due nuovi serializer.
- `PizzaVanillaFurnaceMixin`: intercetta la chiamata `AbstractFurnaceBlockEntity.burn` solo quando il timer vanilla è davvero completato. Non cambia combustibile, GUI, timer o automazione hopper; per le pizze restituisce le ciotole originali una sola volta.
- `PizzaFurnaceRemainders`: restituisce ciotole alla tramoggia direttamente sotto il forno, se presente, e droppa il residuo quando lo spazio manca. **Il Pizza Oven non è stato alterato.**

### Verifiche preliminari

- Ricette e JSON verificati in modo statico; sorgente dei metodi vanilla confrontato con Minecraft **26.2 snapshot 2**.
- La corretta applicazione del mixin nel gioco completo, la sincronizzazione JEI e la compilazione vanno ancora confermate. **Non considerare la funzionalità collaudata.**

## Compilazione

```powershell
git switch feature/pizza-1.0.0
git pull
.\gradlew.bat clean build
.\gradlew.bat runClient
```

Se il build fallisce, annotare il **primo errore Java o Mixin completo** con file e riga.

## Conferma funzionale ricevuta

- [x] L'autore conferma che le **due fornaci vanilla** (fornace e affumicatore) cuociono le pizze nel client in uso.
- [ ] Collaudi approfonditi aggiuntivi elencati qui sotto, da chiudere singolarmente.

## Test manuali

- [ ] **Fornace vanilla:** pizza cruda con quattro quarti **Margherita**, salsa/mozzarella/basilico in ognuno. Dopo ~60 s si ottiene la stessa **Pizza Margherita cotta**, conservando i topping e i quarti.
- [ ] **Affumicatore:** stesso impasto con quattro gusti diversi; dopo ~30 s il risultato è **Pizza assortita**, con tooltip per quarto e spicchi correttamente nominati dopo averla piazzata.
- [ ] **Dati integri:** cucinare una pizza mista con ingredienti originali e provenienti da altre mod già autorizzati; controllare maschere, orientamento, suddivisione e i quattro spicchi.
- [ ] **Nome IT/EN:** i risultati hanno il nome derivato dai dati, non `Pizza cotta` generica, e si traducono cambiando la lingua.
- [ ] **Rimborsi:** salsa applicata una sola volta su quattro quarti → **1 ciotola**, salsa su due metà in due applicazioni originali → **2 ciotole**. Anche panna/ricotta devono seguire la stessa regola.
- [ ] **Hopper sotto:** controllare che l'hopper riceva pizza cotta dall'output e le ciotole restituite. Con hopper pieno, solo le ciotole in eccesso vengono droppate.
- [ ] **Input rimosso a metà cottura:** riutilizzando la pizza cruda il progresso riparte da zero; nessuna ciotola prodotta prima del completamento.
- [ ] **Output già occupato:** con pizza diversa già presente, il forno non deve perdere componenti, sovrascrivere l'output o rimborsare ciotole.
- [ ] **Una pizza per slot:** `raw_pizza` e `cooked_pizza` restano non impilabili. La cottura non duplica.
- [ ] **Vanilla invariato:** cuocere bistecche/ferro nella fornace e carne nello smoker; nessun effetto della nuova logica.
- [ ] **JEI e ricettario:** premere R sulla `raw_pizza` o usare U per vedere entrambe le cotture; verificare le icone delle postazioni.
- [ ] **Multiplayer/server dedicato:** risultati persistenti dopo logout e chunk unload, senza desync o duplicazioni.
- [ ] **Pizza Oven originale:** cottura 20 s, restituzione ciotole, pala pizza, hopper e GUI rimangono funzionanti.

## Nota tecnica sulle ricette

Le ricette JSON ordinarie `minecraft:smelting`/`minecraft:smoking` non possono ricostruire i componenti `PizzaItemData` da un risultato statico. La fase usa quindi serializer dedicati ma **tipi vanilla**, mantenendo la scoperta da parte di fornace/affumicatore e delle integrazioni JEI. La consegna dei contenitori avviene solo al commit della cottura, non durante il lookup della ricetta.

**Non applicare modifiche alle texture o alla grafica approvata del Pizza Peel in questa fase.**

## Regressione ciotole personalizzate (pre-release)

- [ ] Con hopper sotto una fornace o affumicatore vanilla, lascia cinque stack di ciotole **rinominate** con spazio residuo ma nessuno slot vuoto. Cuoci una pizza con salsa: i contenitori restituiti devono cadere a terra, senza sommarsi agli stack personalizzati o modificarne i dati.
- [ ] Con uno slot libero, le ciotole normali entrano nell'hopper; con stack già esistente di ciotole **identiche** vengono unite.
- [ ] Ripetere con due applicazioni originarie di salsa e con hopper saturo: ottenere esattamente due ciotole complessive, senza duplicazioni, e una sola pizza cotta con tutti i componenti.
- [ ] Confermare che non è cambiata la cottura dei cibi vanilla non appartenenti alla mod.

Il percorso dell'affumicatore e della fornace vanilla utilizza la stessa correzione di identità completa degli ItemStack prevista per il Pizza Oven.
