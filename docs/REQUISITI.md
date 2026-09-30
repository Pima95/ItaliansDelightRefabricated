# Italian's Delight — Requisiti e Roadmap

Add-on per **Farmer's Delight Refabricated** su Fabric, dedicato alla cucina
italiana. Questo documento descrive sia i requisiti tecnici correnti sia lo
stato reale delle funzionalità già implementate e della roadmap futura.

## 1. Target tecnico

| Componente | Versione attuale | Note |
|---|---|---|
| Minecraft | 26.2 | versione target del progetto |
| Fabric Loader | 0.19.5 | configurato in `gradle.properties` |
| Fabric API | 0.161.0+26.2 | configurato in `gradle.properties` |
| Farmer's Delight Refabricated | 26.2-3.6.26+refabricated | dipendenza hard via Cassian's Maven |
| Java | 25 | release target usato da Gradle |
| Fabric Loom | 1.17-SNAPSHOT | plugin configurato in `build.gradle` |
| JEI | Curse Maven file 8937443 | integrazione presente per la Cheese Vat |

Prima di aggiornare una dipendenza è necessario verificare che la nuova release
sia compatibile con Minecraft 26.2 e con le altre dipendenze della mod.

## 2. Principi di design

1. **Integrazione reale con Farmer's Delight.** Quando possibile gli alimenti
   devono usare sistemi già esistenti come Cutting Board, Cooking Pot, Stove,
   tag comuni e contenitori, invece di essere semplici recipe da crafting table.

2. **Catene produttive coerenti.** Gli ingredienti intermedi devono avere uno
   scopo reale nella preparazione dei prodotti finali. La Cheese Vat è il primo
   esempio concreto di questa filosofia.

3. **Blocchi funzionali propri.** La mod può introdurre macchine originali
   quando Farmer's Delight non offre già una meccanica equivalente. La Cheese
   Vat è attualmente il primo blocco funzionale personalizzato; il forno a legna
   resta pianificato per una fase successiva.

4. **Namespace e struttura dati puliti.** Tutto il contenuto proprietario usa
   il namespace `italiansdelight`. Per interoperabilità vengono preferiti tag
   condivisi come `c:foods`, `c:crops`, `c:dusts` e `c:tools`.

5. **Compatibilità tramite tag.** Ingredienti compatibili con altri mod devono
   preferire tag comuni rispetto a riferimenti diretti quando questo non cambia
   il significato della ricetta.

## 3. Funzionalità attualmente implementate

### 3.1 Cheese Vat

La **Cheese Vat** è un blocco funzionale completo con:

- BlockEntity e inventario persistente;
- tre slot ingrediente;
- slot contenitore;
- output reale separato dalla preview del prodotto cotto;
- supporto a ricette shapeless personalizzate;
- requisito di fonte di calore compatibile con Farmer's Delight;
- avanzamento della lavorazione sincronizzato client/server;
- GUI dedicata;
- supporto allo shift-click;
- automazione con hopper direzionali: ingredienti dall'alto, recipienti
  (`bowl`, `glass_bottle`, `lead`) dai lati e output dal basso;
- gestione dei crafting remainder;
- serializzazione delle ricette tramite codec;
- integrazione JEI.

Ricette Cheese Vat attualmente presenti:

- latte + caglio → **cagliata**, richiede una ciotola;
- cardo + secchio d'acqua → **caglio**, richiede una bottiglia di vetro;
- cagliata + sale → **mozzarella**.

Il cardo compare nei nuovi chunk di pianure, pianure con girasoli e prati di
montagna. È raccoglibile, ripiantabile sul terreno e moltiplicabile con farina
d'ossa. Secchi, bottiglie del caglio e ciotole della cagliata vengono restituiti
quando il rispettivo ingrediente è consumato dalla caldaia.

Cambiare ricetta o invalidare gli ingredienti azzera il progresso. Senza calore
o con output bloccato il progresso diminuisce di 2 tick per tick, senza consumo
di ingredienti. Un prodotto già cotto può essere raccolto senza calore.

Rompendo la caldaia con un piccone si recuperano blocco e inventario reale.
Il prodotto in attesa del contenitore rimane nel blocco caduto: dopo averlo
ripiazzato occorre ancora fornire la ciotola o bottiglia richiesta.
La mozzarella intera e le quattro fette restituiscono entrambe 4 punti fame
e 1,6 punti saturazione complessivi.

Build e verifiche automatiche lato server superate; prove manuali finali nella
[checklist F1](TEST_FORMAGGI_F1.md).

### 3.2 Item alimentari e lavorazioni Farmer's Delight

Item registrati attualmente:

- pasta al sugo;
- risotto al sugo;
- mozzarella;
- fetta di mozzarella;
- fetta di pomodoro;
- caglio;
- cagliata;
- sale.

Ricette Farmer's Delight già presenti:

- pasta + tomato sauce → pasta al sugo tramite Cooking Pot;
- riso + tomato sauce → risotto al sugo tramite Cooking Pot;
- mozzarella → 4 fette tramite Cutting Board;
- pomodoro → 4 fette tramite Cutting Board.

### 3.3 Produzione del sale

Un normale calderone vanilla pieno d'acqua può produrre sale tramite
evaporazione naturale.

Regole attuali:

- servono **12.000 tick utili**;
- il progresso avanza soltanto di giorno;
- pioggia e temporale mettono il processo in pausa;
- ogni blocco non-aria sopra il calderone blocca temporaneamente
  l'evaporazione, inclusi vetro, foglie e botole aperte;
- i chunk non caricati non avanzano e non vengono caricati forzatamente;
- rimuovere o ridurre l'acqua, rompere il calderone o sostituirlo azzera il
  progresso;
- il progresso viene salvato per dimensione tramite `SavedData`;
- al completamento l'acqua sparisce e viene visualizzato uno strato di sale
  alto 2 pixel sul fondo;
- il residuo usa una texture blocco dedicata;
- tasto destro sul residuo restituisce casualmente **da 3 a 7 sale** e lascia
  un calderone vuoto;
- il Pick Block in creativa permette di copiare e ripiazzare direttamente il
  calderone contenente il residuo;
- rompere il blocco finale restituisce il normale calderone e non il sale.

## 4. Struttura del progetto

La logica condivisa è contenuta in `common`, il codice esclusivamente client
in `client`, le integrazioni esterne in `integration` e i mixin nel package
`mixin`.

```text
src/main/
├── java/dev/italiansdelight/
│   ├── ItaliansDelight.java
│   ├── ItaliansDelightClient.java
│   ├── client/
│   │   └── gui/CheeseVatScreen.java
│   ├── common/
│   │   ├── block/
│   │   │   ├── CheeseVatBlock.java
│   │   │   ├── SaltCauldronBlock.java
│   │   │   └── entity/
│   │   │       ├── CheeseVatBlockEntity.java
│   │   │       └── container/CheeseVatMenu.java
│   │   ├── crafting/
│   │   │   ├── CheeseVatRecipe.java
│   │   │   └── CheeseVatRecipeInput.java
│   │   ├── item/group/ModItemGroups.java
│   │   ├── registry/Mod*.java
│   │   └── salt/
│   │       ├── SaltCauldronManager.java
│   │       └── SaltCauldronProgressData.java
│   ├── integration/
│   │   └── jei/
│   │       ├── ItaliansDelightJeiPlugin.java
│   │       └── CheeseVat*.java
│   └── mixin/
│       └── LevelMixin.java
└── resources/
    ├── fabric.mod.json
    ├── italiansdelight.mixins.json
    ├── assets/italiansdelight/
    │   ├── blockstates/
    │   ├── items/
    │   ├── lang/
    │   ├── models/
    │   └── textures/
    └── data/
        ├── c/tags/item/
        └── italiansdelight/
            ├── advancement/
            ├── loot_table/
            └── recipe/
```

Le ricette della Cheese Vat sono in
`data/italiansdelight/recipe/cheese_vat/`.

Le ricette che usano sistemi Farmer's Delight sono organizzate nelle cartelle
`recipe/cooking/` e `recipe/cutting/`.

## 5. Stato della roadmap

Il lavoro del branch `feature/cheeses` è dettagliato nella
[roadmap formaggi](ROADMAP_FORMAGGI.md), con fasi, decisioni aperte e criteri di
completamento.

- [x] **Step 0 — Fondamenta progetto:** repository, Gradle, Fabric, entrypoint,
      registri principali e struttura risorse.
- [x] **Step 1 — Prima macchina funzionale:** Cheese Vat completa di GUI,
      ricette personalizzate, persistenza e JEI.
- [x] **Step 2 — Prima filiera casearia:** caglio → cagliata → mozzarella,
      inclusa lavorazione della mozzarella al Cutting Board.
- [x] **Step 3 — Sistema di produzione del sale:** evaporazione nel calderone,
      persistenza del progresso, residuo visivo e raccolta.
- [ ] **Step 4 — Filiera del pomodoro completa:** attualmente esistono fetta di
      pomodoro e ricette che riusano la tomato sauce di Farmer's Delight; manca
      ancora una catena proprietaria completa passata/conserva/sugo se verrà
      mantenuta questa scelta di design.
- [ ] **Step 5 — Filiera pasta completa:** definire produzione di impasto,
      pasta fresca/secca e nuovi piatti come cacio e pepe, carbonara,
      amatriciana e pesto.
- [ ] **Step 6 — Filiera casearia completa:** consolidare la mozzarella,
      aggiungere ricotta, parmigiano e pecorino e implementare la stagionatura;
      seguire le fasi della [roadmap formaggi](ROADMAP_FORMAGGI.md).
- [ ] **Step 7 — Forno a legna e pizza:** blocco funzionale dedicato,
      condimenti e sistema di cottura.
- [ ] **Step 8 — Colture e ingredienti aggiuntivi:** decidere quali texture già
      presenti diventeranno contenuti reali e quali colture dovranno essere
      implementate.
- [ ] **Step 9 — Progressione:** advancement tree dedicato, ricette aggiuntive,
      loot/tag mancanti e possibile datagen.
- [ ] **Step 10 — Release:** testing completo, bilanciamento, packaging e
      pubblicazione per Modrinth/CurseForge.

## 6. Regole per le prossime implementazioni

Prima di aggiungere un nuovo contenuto:

1. verificare se Farmer's Delight offre già la meccanica necessaria;
2. preferire tag condivisi a dipendenze rigide da singoli item;
3. aggiungere registrazione, modello, texture, traduzioni e recipe necessari;
4. verificare il comportamento sia lato server sia lato client;
5. aggiungere commenti alle parti di codice non immediatamente intuitive;
6. aggiornare questo documento quando una feature cambia stato nella roadmap;
7. eseguire almeno `gradlew build` e un test in-game prima del merge in
   `main`.

## 7. Decisioni ancora aperte

Restano da definire durante lo sviluppo:

1. quali nuove colture italiane implementare realmente rispetto alle texture
   già preparate;
2. se introdurre una vera filiera del pomodoro distinta dalla tomato sauce di
   Farmer's Delight;
3. quali tipi di pasta e piatti avranno una catena produttiva completa;
4. funzionamento preciso della stagionatura dei formaggi;
5. design e gameplay del forno a legna;
6. eventuale sistema di bevande come vino e caffè.
