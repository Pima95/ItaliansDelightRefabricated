# Italian's Delight — Roadmap formaggi

Roadmap di lavoro per il branch `feature/cheeses`.

**Obiettivo:** completare una filiera casearia giocabile, dalla produzione degli
ingredienti alla lavorazione dei formaggi freschi e stagionati, integrata con
Farmer's Delight e consultabile attraverso JEI.

Questo documento dettaglia lo Step 6 della [roadmap generale](REQUISITI.md),
includendo il consolidamento della filiera già presente. Le caselle delle nuove
fasi restano aperte finché il lavoro e le relative verifiche non sono completati.

## 1. Punto di partenza

Funzionalità già presenti nel codice, da sottoporre alle verifiche della fase F1:

- [x] Caldaia casearia con tre slot ingrediente, contenitore e output.
- [x] Calore, avanzamento, salvataggio e sincronizzazione della lavorazione.
- [x] GUI, shift-click e integrazione JEI della caldaia.
- [x] Cardo + acqua → caglio, raccolto in una bottiglia.
- [x] Latte + caglio → cagliata, raccolta in una ciotola.
- [x] Cagliata + sale → mozzarella.
- [x] Mozzarella → quattro fette tramite Cutting Board.
- [x] Produzione del sale tramite evaporazione nel calderone.

Gli altri prodotti concordati e i sistemi di stagionatura/asciugatura sono ancora
da implementare.

## 2. Perimetro e principi

Il branch comprende ingredienti caseari, formaggi, caldaia, stagionatura,
porzionatura, risorse grafiche, traduzioni, tag, JEI e progressione casearia.
La selezione concordata comprende mozzarella, bocconcini, ricotta, Parmigiano
Reggiano, Pecorino Romano, Gorgonzola, Provolone, Scamorza, Burrata e
Mascarpone.

La caldaia rimane il punto di partenza della produzione. Il Cutting Board serve
per le porzioni; un ripiano con forme visibili è la direzione prevista per la
stagionatura. Si riusano le meccaniche di Farmer's Delight quando adatte.

Ogni formaggio deve avere un ruolo riconoscibile. Nuovi ingredienti intermedi
vanno introdotti quando hanno un impiego concreto e un costo di gestione
proporzionato al beneficio per il giocatore.

Le ricette dei piatti completi si coordinano con `feature/dishes`; forno e pizza
rimangono nella roadmap generale. La produzione e la porzionatura dei formaggi
devono poter essere completate e provate senza dipendere da quei lavori.
Ulteriori varietà, allevamenti dedicati e simulazioni complesse della maturazione
restano estensioni da valutare successivamente.

## 3. Ruolo dei formaggi

| Formaggio | Processo produttivo concordato | Stato |
|---|---|---|
| Mozzarella | Latte + caglio → cagliata; cagliata + sale → mozzarella; porzionatura al Cutting Board | Base presente, da rifinire |
| Bocconcini | Cagliata + acqua normale → 2 bocconcini nella Cheese Vat; nessun contenitore finale e nessun item "acqua calda" | Da implementare |
| Ricotta | La produzione della cagliata recupera anche il siero; siero + una piccola quantità di latte → ricotta nella Cheese Vat | Da implementare |
| Parmigiano Reggiano | Latte vaccino + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato | Da implementare |
| Pecorino Romano | Latte di pecora + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato | Da implementare |
| Gorgonzola | Cagliata + sale + coltura erborinata → forma fresca → stagionatura | Da implementare |
| Provolone | Cagliata + sale + acqua → forma fresca → stagionatura → fette | Da implementare |
| Scamorza | Cagliata + sale → scamorza fresca usando `minecraft:lead` nello slot contenitore → breve asciugatura; possibile variante affumicata | Da implementare |
| Burrata | Mozzarella + panna → burrata; prodotto fresco senza stagionatura | Da implementare |
| Mascarpone | Panna + aceto di mele → mascarpone nella Cheese Vat; ciotola separata nello slot contenitore | Da implementare |

I nomi delle nuove registrazioni, le quantità, i tempi e i valori nutritivi
saranno fissati nella fase F2. Gli identificatori dei contenuti esistenti vanno
preservati, salvo una migrazione esplicitamente documentata.

## 4. Fasi di lavoro

Ordine previsto: **F1 → F2 → F3 → F4 → F5 → F6**. Texture, traduzioni e ricette
accompagnano ciascuna implementazione, così ogni fase produce contenuti provabili.

### F1 — Consolidare la filiera esistente

**Stato:** completo. La validazione manuale finale è descritta nella [checklist F1](TEST_FORMAGGI_F1.md).

- [x] Restituire la bottiglia quando il caglio viene consumato come ingrediente.
- [x] Verificare restituzione di secchi di latte/acqua e ciotola della cagliata.
- [x] Bilanciare la mozzarella intera con le quattro fette: 4 punti fame e
      1,6 punti saturazione complessivi in entrambi i casi.
- [x] Verificare lato server contenitore errato, output bloccato,
      perdita del calore e cambio di ricetta durante la lavorazione.
- [x] Verificare shift-click, persistenza dopo riavvio e comportamento alla
      rottura in gioco. Lo shift-click è stato corretto; il salvataggio e
      ripristino del prodotto nel drop sono già verificati automaticamente.
- [x] Sostituire la ricetta placeholder con allium: cardo selvatico
      raccoglibile, ripiantabile e moltiplicabile con farina d'ossa;
      cardo + secchio d'acqua → caglio nella caldaia.
- [x] Correggere i problemi individuati e documentare le regole di
      recupero dei prodotti e dei contenitori nella checklist F1.

**Completamento:** la filiera cardo/acqua/latte/sale → mozzarella → fette è provata in
survival, i contenitori sono recuperabili secondo le regole definite e le prove
di interruzione e rottura non producono duplicazioni o perdite impreviste.

### F2 — Definire ricette e progressione

- [x] Scegliere il catalogo di prodotti: mozzarella, bocconcini, ricotta,
      Parmigiano Reggiano, Pecorino Romano, Gorgonzola, Provolone, Scamorza,
      Burrata e Mascarpone.
- [x] Definire la struttura generale dei processi produttivi riportata nella
      sezione 3.
- [x] Stabilire che i bocconcini usano acqua normale: non viene introdotto un
      item separato per l'acqua calda.
- [x] Introdurre il siero come recupero della lavorazione della cagliata e
      riutilizzarlo nella filiera della ricotta.
- [x] Prevedere panna come intermedio comune per Burrata e Mascarpone.
- [x] Distinguere Parmigiano e Pecorino anche per origine del latte, prevedendo
      latte di pecora per il Pecorino.
- [x] Definire gli ingredienti intermedi principali: la panna usa una sola
      ricetta con `#c:drinks/milk` e ciotola, senza differenze tra secchio e
      bottiglia; la coltura erborinata è un item craftabile shapeless da
      `minecraft:bread` + `#c:mushrooms`. Per la Scamorza si usa
      `minecraft:lead` nello slot contenitore; per il Mascarpone si usa aceto
      di mele, la cui produzione resta nel branch dedicato al vino/barile.
- [x] Definire il controllo del siero nella Cheese Vat: la freccia della GUI
      abilita/disabilita soltanto il prelievo automatico dal tank, senza
      bloccare Whey Bottle/Whey Bucket inseriti come ingredienti. Lo stato è
      persistente finché la macchina resta piazzata; JEI mantiene una singola
      categoria e usa due background per distinguere ricette normali e ricette
      che richiedono siero dal serbatoio.
- [x] Integrare il serbatoio base della Cheese Vat: capacità 4000 mB,
      sincronizzazione e salvataggio, livello visibile nella GUI e produzione
      data-driven tramite `whey_output`. La cagliata produce 250 mB e resta in
      attesa al 100% se il tank non ha spazio sufficiente.
- [x] Integrare lo slot di estrazione sotto il tank: un solo contenitore vuoto
      alla volta, riempimento automatico di Whey Bottle (250 mB) o Whey Bucket
      (1000 mB), con persistenza e migrazione dei salvataggi precedenti.
- [ ] Definire rese, tempi di lavorazione, tempi di stagionatura/asciugatura,
      valori nutritivi e quantità ottenute dalle porzionature. Per la ricotta,
      il siero è già definito: 500 mB dal tank oppure una bottiglia/secchio di
      siero consumati come unità intera, con priorità dell'item sul tank.
- [ ] Definire capacità, interazioni, condizioni e recupero delle forme del
      sistema di stagionatura/asciugatura.

**Completamento:** ogni prodotto ha una catena definita, senza ambiguità, con
ingredienti, rese, tempi, porzioni e regole di stagionatura sufficienti per
l'implementazione.

### F3 — Completare i formaggi freschi

- [ ] Applicare il bilanciamento e le rifiniture concordate per la mozzarella.
- [ ] Implementare la ricotta e gli eventuali intermedi definiti in F2.
- [ ] Aggiungere registrazioni, modelli, texture, traduzioni italiane e inglesi,
      ricette, tag e presenza nel gruppo creativo.
- [ ] Integrare le nuove lavorazioni in JEI e verificare i contenitori.

**Completamento:** mozzarella e ricotta sono ottenibili e utilizzabili in
survival, con ricette consultabili e valori coerenti con la tabella di F2.

### F4 — Implementare la stagionatura

- [ ] Creare il ripiano con forme visibili, ricetta di costruzione, interazioni
      per inserire e prelevare i prodotti, modello, texture e drop.
- [ ] Rappresentare in modo leggibile lo stato di maturazione, distinguendo
      almeno prodotto in lavorazione e prodotto pronto.
- [ ] Implementare avanzamento lato server, salvataggio e sincronizzazione
      visiva, secondo le regole definite in F2.
- [ ] Gestire riavvii, caricamento e scaricamento dei chunk, rimozione delle
      forme e rottura del ripiano senza duplicazioni o perdite impreviste.
- [ ] Rendere ingredienti, risultati, tempi e condizioni consultabili in JEI.
- [ ] Implementare una prima forma tra quelle progettate in F2 e usarla per
      provare il ciclo completo, anticipando il minimo necessario di F5.

**Completamento:** una forma passa dalla preparazione al prodotto maturo;
progresso, interazioni e recupero rispettano le regole documentate anche dopo
un riavvio e in multiplayer.

### F5 — Aggiungere parmigiano e pecorino

- [ ] Completare le preparazioni delle due forme e le rispettive ricette di
      stagionatura, partendo dalla prima forma di F4 e dalle differenze di F2.
- [ ] Implementare porzioni e relative ricette al Cutting Board; aggiungere
      il grattugiato soltanto se previsto dal design.
- [ ] Completare registrazioni, modelli, texture, traduzioni, tag e JEI.
- [ ] Documentare gli ingredienti disponibili per `feature/dishes` e gli usi
      previsti, distinguendoli dalle ricette di piatti già implementate.
- [ ] Verificare rese e valori nutritivi lungo l'intera catena e impedire
      eventuali cicli di conversione che moltiplichino i prodotti.

**Completamento:** entrambi i formaggi sono producibili, stagionabili e
porzionabili in survival, con differenze riconoscibili per il giocatore.

### F6 — Completare integrazione e verifiche

- [ ] Controllare coerenza di nomi, texture, traduzioni e visualizzazione JEI.
- [ ] Verificare tag condivisi, loot e compatibilità delle fonti di calore;
      definire e provare il comportamento delle tramogge dove supportato.
- [ ] Aggiungere advancement essenziali per guidare la progressione casearia.
- [ ] Provare la filiera completa in survival, bilanciando anche disponibilità
      del sale, contenitori, quantità prodotte e tempi di attesa.
- [ ] Eseguire `gradlew build` e prove in-game su client e server dedicato,
      includendo riavvio, chunk scaricati, output bloccato e rottura dei blocchi.
- [ ] Aggiornare README, requisiti in entrambe le lingue e questa roadmap con
      lo stato effettivo e le verifiche eseguite prima del merge in `main`.

**Completamento:** i quattro formaggi sono ottenibili senza comandi, tutte le
lavorazioni sono documentate, la build passa e le prove di gioco sono registrate.

## 5. Decisioni da risolvere durante F2

Queste sono proposte iniziali di gameplay, ancora da fissare nei dettagli.

| Tema | Direzione proposta | Decisione necessaria |
|---|---|---|
| Ricotta e siero | Introdurre il siero se sostiene una filiera utile | Ricetta, raccolta del siero ed eventuale estensione della caldaia |
| Parmigiano e pecorino | Differenziare produzione e impieghi | Ingredienti, provenienza del latte e porzioni |
| Ripiano | Forme visibili e interazione diretta | Capacità, disposizione, inserimento e prelievo |
| Maturazione | Tempo come requisito iniziale, stato visibile | Durate ed eventuali condizioni ambientali utili al gameplay |
| Chunk scaricati | Mettere in pausa senza caricare forzatamente i chunk | Confermare la regola e il comportamento alla ripresa |
| Rimozione delle forme | Conservare il progresso già acquisito | Come rappresentarlo nell'item e ripristinarlo sul ripiano |
| Rottura del ripiano | Restituire blocco e forme con il loro stato | Regole dei drop e verifica del recupero |
| Porzioni | Tagliere come strumento principale | Numero di porzioni, grattugiato ed eventuali bonus nutritivi |

Le scelte definitive vanno riportate qui e nelle tabelle delle ricette prima
dell'implementazione della fase interessata.

## 6. Estensioni successive

Altri formaggi italiani oltre ai dieci prodotti già concordati potranno essere
valutati dopo il completamento della filiera principale. La struttura delle
ricette e della stagionatura dovrà consentire nuove varietà senza duplicare la
logica di base.
