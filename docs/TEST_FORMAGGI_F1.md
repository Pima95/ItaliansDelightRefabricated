# Prova in gioco — F1 formaggi

## Stato delle verifiche

- Build Gradle completata con successo su Java 25 / Minecraft 26.2.
- Quattro verifiche della mod superate su server Fabric GameTest: filiera e
  contenitori; salvataggio/ripristino del prodotto nel drop; cambio ricetta,
  output bloccato e perdita del calore; risorse e drop del cardo.
- Il runner ha completato cinque test in totale, incluso il controllo di
  avvio del runner. Rapporto locale: `build/f1-smoke/results.xml`.
- I test temporanei e il loro mondo sono sotto `build/f1-smoke/`; nessun mondo
  di gioco esistente è stato usato. Il client grafico non è stato avviato.

Le verifiche qui sotto restano da eseguire manualmente prima di chiudere F1.

## Preparazione

Avviare il client di sviluppo con `./gradlew runClient`, oppure usare il JAR
`build/libs/italiansdelight-0.1.0.jar` in un'istanza compatibile.

Servono una caldaia sopra una fonte di calore accesa, cardo, acqua, latte,
bottiglie, ciotole, sale, un piccone, farina d'ossa e un tagliere con coltello.
Il cardo è disponibile anche nel gruppo creativo della mod. La generazione
naturale riguarda soltanto nuovi chunk di `plains`, `sunflower_plains` e
`meadow`, senza modificare le zone già esplorate.

## Checklist

- [X] **Cardo:** verificare aspetto e nome in inventario e nel mondo. Raccoglierlo
      a mano, ripiantarlo su terra o erba e applicare farina d'ossa: deve produrre
      un cardo aggiuntivo, lasciando intatta la pianta. Rimuovere il terreno
      sottostante deve far cadere la pianta come item. FUNZIONA
- [X] **Caglio:** mettere cardo e secchio d'acqua nei primi tre slot e una
      bottiglia nello slot contenitore. Dopo 100 tick (circa 5 secondi a 20 TPS)
      deve uscire un caglio; il secchio vuoto viene espulso lateralmente.
      L'allium non deve più attivare questa ricetta. FUNZIONA
- [X] **Cagliata:** latte + caglio e una ciotola producono una cagliata dopo
      200 tick. Recuperare un secchio e una bottiglia vuoti espulsi dalla caldaia. FUNZIONA
- [X] **Mozzarella:** cagliata + sale producono una mozzarella dopo 160 tick;
      la ciotola viene restituita. Tagliare la mozzarella restituisce quattro
      fette. L'intero vale 4 punti fame, ogni fetta 1; la saturazione complessiva
      è 1,6 in entrambi i casi. FUNZIONA
- [X] **Contenitore assente o errato:** cuocere cagliata senza ciotola o con
      una bottiglia. Il prodotto resta nella preview non prelevabile. Inserire
      la ciotola corretta deve trasferirlo una sola volta nell'output, anche
      dopo aver spento il fuoco, consumando una sola ciotola. FUNZIONA
- [X] **Output pieno:** riempire lo slot con il prodotto fino al limite dello
      stack e avviare un altro ciclo. Gli ingredienti non devono essere
      consumati finché non si libera spazio. Provare anche con un prodotto
      diverso rimasto nell'output. DA RITESTARE DOPO LA CORREZIONE DEL BUFFER.
- [X] **Cambio ricetta e calore:** avviare una mozzarella, poi sostituire gli
      ingredienti con cardo e acqua. La nuova lavorazione deve partire da zero.
      Ingredienti non validi azzerano il progresso; spegnere il calore o
      bloccare l'output lo fa diminuire di 2 tick per tick.
- [X] **Shift-click:** bottiglie e ciotole vanno nello slot contenitore;
      gli ingredienti nei primi tre slot. Il prodotto confezionato torna
      nell'inventario del giocatore e la preview non può essere prelevata.
      Uno slot contenitore occupato da un oggetto incompatibile non deve
      deviare bottiglie o ciotole negli ingredienti.
- [X] **Salvataggio:** uscire e rientrare durante una lavorazione e con un
      prodotto in attesa. Verificare inventario, progresso e preview; il
      contenitore necessario deve essere consumato una sola volta.
- [X] **Rottura in survival:** rompere con il piccone una caldaia con ingredienti,
      contenitori e output. Questi cadono separatamente insieme al blocco.
      Ripetere con una cagliata in attesa: il prodotto rimane nella caldaia
      caduta; ripiazzandola e inserendo una ciotola si ottiene una sola cagliata.
      La preview e il contenitore richiesto non devono cadere come item gratuiti.
- [X] **JEI e mondo:** controllare la nuova ricetta del caglio, le altre due
      ricette e le traduzioni; cercare cardo nei biomi previsti in chunk nuovi.

## Correzioni implementate da verificare

Le richieste seguenti sono ora presenti nel codice del branch, ma restano aperte
finché non vengono provate con una nuova build e in gioco.

- [X] Il nome del caglio è **Bottle of Rennet** / **Bottiglia di Caglio**.
- [X] Il cardo può essere inserito in un vaso da fiori e viene recuperato
      correttamente insieme al vaso quando viene rotto.
- [X] Le ricette della caldaia accumulano esperienza e la rilasciano alla
      raccolta del prodotto o alla rimozione della caldaia.
- [X] Con un prodotto già cotto in preview, usare direttamente il contenitore
      corretto sulla caldaia deve consumare il contenitore e consegnare una
      singola porzione senza aprire obbligatoriamente la GUI.
- [X] La Bottle of Rennet può essere prodotta sia con un secchio d'acqua sia
      con una pozione d'acqua; l'ampolla vuota viene restituita.
- [X] La caldaia richiede più tempo per essere rotta rispetto alla versione
      precedente.
- [X] **Particelle:** da ritestare. Le particelle di rottura mantengono il
      normale colore metallico del calderone ma vengono generate in quantità
      ridotta. Quando la caldaia è riscaldata, dalla superficie interna salgono
      invece leggere particelle color crema simili al contenuto caldo.
- [X] La Bottle of Rennet ha stack massimo 16.
- [X] I prodotti che richiedono un contenitore continuano ad accumularsi nella
      preview fino al limite massimo dello stack; solo a quel punto la produzione
      si interrompe. L'output occupato non deve impedire questo accumulo.
- [X] Sopra le fonti incluse in `farmersdelight:tray_heat_sources`, incluso il
      campfire, la caldaia mostra il supporto/vassoio come il Cooking Pot.
- [X] Senza calore la superficie interna usa l'aspetto scuro del fondo del
      calderone; con calore torna alla superficie chiara della caldaia.
- [X] Lo spawn del cardo è ridotto rispetto alla prima implementazione e le
      generazioni valide formano gruppi più consistenti.
- [X] **Scarti di produzione:** da ritestare. Bottiglie, secchi e ciotole
      vengono ora generati oltre il bordo laterale della caldaia con una spinta
      orizzontale maggiore, così non dovrebbero ricadere al suo interno.

## Regole di recupero

Il contenitore finale viene consumato soltanto al confezionamento. I contenitori
degli ingredienti sono invece restituiti al termine della cottura. La rottura
con piccone conserva il prodotto già cotto dentro l'item caldaia, senza copiarvi
l'inventario reale. Una lavorazione ancora incompleta perde il progresso alla
rottura, ma restituisce gli ingredienti ancora presenti. I vecchi salvataggi
senza identificativo della ricetta ripartono da zero al primo caricamento;
inventario e prodotti già cotti restano conservati.

## Scelta del caglio vegetale

La ricetta cardo + acqua è una semplificazione di gameplay ispirata agli estratti
dei fiori di *Cynara cardunculus*, studiati come coagulanti caseari. La caldaia
rappresenta l'estrazione senza simulare essiccazione, temperature o macerazione:
[Tripaldi et al., Italian Journal of Food Science](https://doi.org/10.15586/ijfs.v33i4.2026).

## Texture del cardo

File: `src/main/resources/assets/italiansdelight/textures/block/cardoon.png`.
Generata con il tool integrato `imagegen`, poi adattata a PNG RGBA 16×16 con
ridimensionamento nearest-neighbor e alpha binario per il rendering cutout. Lo stesso asset
è usato dal modello del blocco e dall'item.

Prompt di generazione:

```text
Use case: stylized-concept. Asset type: Minecraft vanilla-style 16x16 pixel-art plant texture for a crossed-plane block and inventory item in a cooking mod. Primary request: a single wild cardoon (Cynara cardunculus), an upright pale green stem with jagged spiny gray-green leaves and one violet-purple tufted thistle flower in a green rounded prickly cup at the top. Composition: one isolated full plant centered, stem reaches the bottom edge, flower near the top; recognizable broad spiky leaves, entire silhouette fits inside the square. Style: strictly low resolution pixel art on an EXACT 16 by 16 logical pixel grid, every pixel square and uniform, at most 12 flat colors, no antialiasing, no fine detail. Transparent alpha background, no ground, no shadow, no border, no grid lines, no text, no checkerboard painted into the image. Output a square PNG, native 16x16 if possible; otherwise nearest-neighbor enlarged 16x16 artwork with perfect aligned pixel squares.
```
