# Italian's Delight — Requisiti e Roadmap

Add-on per **Farmer's Delight Refabricated** (Fabric, Minecraft 26.2) a tema
cucina italiana. Documento di lavoro: da aggiornare man mano che il progetto
prende forma.

## 1. Target tecnico

| Componente | Versione | Note |
|---|---|---|
| Minecraft | 26.2 | verificare a ogni sessione su fabricmc.net, potrebbero uscire patch (26.2.x) |
| Fabric Loader | >= 0.16.9 | requisito minimo di FDR; usare l'ultima stabile |
| Fabric API | 0.158.0+26.2 (o successiva per 26.2) | |
| Farmer's Delight Refabricated | ultima versione per 26.2 (dependency **hard**, non opzionale) | via Cassian's Maven `https://maven.cassian.cc` |
| Java | 25 | richiesto da MC 26.2 |
| Fabric Loom | >= 1.16 (richiesto da FDR >= 3.3.0 per l'enum class tweaker) | |

**Da fare prima della prima build reale:** aprire il progetto in IntelliJ con il
plugin Fabric Loom, lasciare che generi/aggiorni `yarn_mappings` nel picker, e
allineare `fdrf_version` in `gradle.properties` alla release FDR corrente
(controllare Modrinth/CurseForge).

## 2. Decisioni di design (per differenziarsi dagli altri add-on italiani)

Punti su cui altri add-on simili tendono a fermarsi presto, e che vogliamo
invece curare:

1. **Integrazione vera col sistema FD, non item isolati.** Ogni piatto deve
   passare per Cutting Board (impasti, tagli) e/o Cooking Pot (cotture,
   bollitura pasta) come i piatti vanilla di Farmer's Delight, non essere un
   semplice "food item" craftato al banco.
2. **Catena produttiva regionale**, non solo il piatto finito:
   pomodoro → passata/conserva → sugo; grano → impasto → pasta secca/fresca;
   latte (già in FD) → formaggi stagionati (mozzarella, parmigiano, pecorino)
   come blocchi/item con "stagionatura" nel tempo, sul modello del Cheese
   già presente in alcuni add-on ma fatto con più cura (texture, nomi, lore).
3. **Un blocco funzionale originale**: forno a legna per pizza (diverso dallo
   Stove di FD), con la sua GUI o quantomeno una ricetta dedicata via tag.
4. **Struttura dati pulita**: namespace `italiansdelight` per tutto ciò che è
   nostro, tag aggiunti a `farmersdelight` (es. `knife`, `heat_sources`) solo
   dove serve interoperabilità, mai duplicazione di contenuti FD esistenti.
5. **Compatibilità dichiarata** con eventuali altri add-on FD via tag comuni
   (`c:foods`, `farmersdelight:snacks`, `farmersdelight:sweets`) invece di tag
   proprietari quando un tag condiviso esiste già.

## 3. Struttura del progetto

La logica di gioco condivisa è in `common`, la schermata è in `client` e
l'integrazione JEI è in `integration/jei`. Gli entrypoint restano nel
package principale. I percorsi delle risorse seguono le convenzioni di
Minecraft 26.2.

```text
src/main/
├── java/com/piergiuseppe/italiansdelight/
│   ├── ItaliansDelight.java
│   ├── ItaliansDelightClient.java
│   ├── common/
│   │   ├── block/CheeseVatBlock.java
│   │   ├── block/entity/CheeseVatBlockEntity.java
│   │   ├── block/entity/container/CheeseVatMenu.java
│   │   ├── crafting/{CheeseVatRecipe,CheeseVatRecipeInput}.java
│   │   ├── item/group/ModItemGroups.java
│   │   └── registry/Mod*.java
│   ├── client/gui/CheeseVatScreen.java
│   └── integration/jei/{ItaliansDelightJeiPlugin,CheeseVat*}.java
└── resources/
    ├── fabric.mod.json
    ├── assets/italiansdelight/{items,models,textures,lang,blockstates}/
    └── data/{italiansdelight,c}/
```

Le ricette della Cheese Vat restano in
`data/italiansdelight/recipe/cheese_vat/`. Le altre ricette sono nelle
cartelle `cooking/` e `cutting/`. I tag usano `tags/item/` (singolare).
La generazione dati verrà aggiunta quando servirà, senza cartelle vuote.

## 4. Roadmap proposta (prossimi step, uno alla volta)

- [ ] **Step 0 (fatto)**: scheletro repo, gradle, fabric.mod.json, entrypoint.
- [ ] **Step 1**: definire la lista completa di ingredienti e piatti (crop
      nuovi? o solo riuso di crop vanilla/FD?), con relative catene di
      lavorazione — questo va deciso insieme prima di scrivere codice.
- [ ] **Step 2**: implementare la catena Pomodoro → Passata → Sugo (item +
      ricette Cutting Board/Cooking Pot).
- [ ] **Step 3**: catena Pasta (impasto → pasta secca/fresca → piatti: cacio e
      pepe, carbonara, amatriciana, pesto...).
- [ ] **Step 4**: formaggi stagionati come blocchi (mozzarella, parmigiano,
      pecorino) con eventuale meccanica di stagionatura nel tempo.
- [ ] **Step 5**: blocco Forno a legna + pizza (impasto, condimenti, cottura).
- [ ] **Step 6**: texture e modelli (dipende da uno stile grafico scelto:
      coerente con lo stile FD, blocky/painterly).
- [ ] **Step 7**: advancement tree dedicato, datagen per recipe/loot/tag.
- [ ] **Step 8**: build, testing in-game, packaging per Modrinth/CurseForge.

## 5. Domande aperte da chiarire insieme prima dello Step 1

1. Vuoi crop italiani nuovi (es. basilico, melanzane) o preferisci basarti
   solo su ingredienti già presenti in vanilla/FD e concentrarti sui piatti?
2. Vuoi anche una parte "bevande" (vino, caffè/moka) o restiamo solo su cibo?
3. Stile grafico delle texture: fedele allo stile pittorico di FD o qualcosa
   di diverso?
