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

Ricotta, parmigiano, pecorino e stagionatura sono ancora da implementare.

## 2. Perimetro e principi

Il branch comprende ingredienti caseari, formaggi, caldaia, stagionatura,
porzionatura, risorse grafiche, traduzioni, tag, JEI e progressione casearia.
La selezione iniziale è mozzarella, ricotta, parmigiano e pecorino.

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

| Formaggio | Ruolo previsto | Lavorazione prevista | Stato |
|---|---|---|---|
| Mozzarella | Formaggio fresco di accesso alla filiera; ingrediente per futuri piatti e pizza | Caldaia → mozzarella → fette al tagliere | Base presente, da rifinire |
| Ricotta | Secondo prodotto fresco, con una ricetta distinta dalla mozzarella | Produzione in caldaia; ingredienti e contenitore da definire | Da progettare |
| Parmigiano | Formaggio stagionato destinato anche a porzioni per condire | Preparazione della forma → stagionatura → porzionatura | Da progettare |
| Pecorino | Formaggio stagionato con identità produttiva e culinaria propria | Preparazione distinta → stagionatura → porzionatura | Da progettare |

I nomi delle nuove registrazioni, le quantità, i tempi e i valori nutritivi
saranno fissati nella fase F2. Gli identificatori dei contenuti esistenti vanno
preservati, salvo una migrazione esplicitamente documentata.

## 4. Fasi di lavoro

Ordine previsto: **F1 → F2 → F3 → F4 → F5 → F6**. Texture, traduzioni e ricette
accompagnano ciascuna implementazione, così ogni fase produce contenuti provabili.

### F1 — Consolidare la filiera esistente

**Stato:** implementazione pronta per la prova in gioco. Build riuscita e quattro
verifiche automatiche della mod superate su server GameTest. La validazione
manuale finale è descritta nella [checklist F1](TEST_FORMAGGI_F1.md).

- [x] Restituire la bottiglia quando il caglio viene consumato come ingrediente.
- [x] Verificare restituzione di secchi di latte/acqua e ciotola della cagliata.
- [x] Bilanciare la mozzarella intera con le quattro fette: 4 punti fame e
      1,6 punti saturazione complessivi in entrambi i casi.
- [x] Verificare lato server contenitore errato, output bloccato,
      perdita del calore e cambio di ricetta durante la lavorazione.
- [ ] Verificare shift-click, persistenza dopo riavvio e comportamento alla
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

- [ ] Scrivere una tabella delle ricette con ingredienti, macchina, calore,
      contenitori, risultati, rese, durata e usi di ogni nuovo prodotto.
- [ ] Scegliere la filiera della ricotta e decidere se introdurre il siero.
      La caldaia attuale gestisce un solo prodotto per ricetta: un eventuale
      sottoprodotto richiede di progettare anche raccolta, GUI e JEI.
- [ ] Definire una differenza concreta tra parmigiano e pecorino nella
      produzione e negli impieghi. Decidere se il latte resta condiviso o se
      serve una provenienza distinta, valutandone il costo di implementazione.
- [ ] Evitare ricette ambigue: nella stessa macchina, combinazioni di
      ingredienti equivalenti non devono selezionare formaggi diversi per caso.
- [ ] Scegliere porzioni, eventuale formaggio grattugiato, valori nutritivi e
      possibili bonus della lavorazione, mantenendo sotto controllo le rese.
- [ ] Definire capacità, interazioni, tempi, condizioni e recupero delle forme
      del ripiano di stagionatura, risolvendo le decisioni della sezione 5.

**Completamento:** ogni formaggio ha una catena ottenibile con gli ingredienti
previsti, un ruolo distinto e regole sufficienti per implementare F3–F5.

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

Gorgonzola, provolone, scamorza, burrata, affumicatura, fermenti dedicati e nuove
fonti di latte potranno essere valutati dopo il completamento della selezione
iniziale. La struttura delle ricette e della stagionatura dovrà consentire
l'aggiunta di altre varietà senza duplicare la logica di base.
