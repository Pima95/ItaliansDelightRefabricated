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
La selezione concordata comprende mozzarella, Bocconcino, ricotta, Parmigiano
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
| Bocconcino | Cagliata + acqua normale → 2 unità di Bocconcino nella Cheese Vat; nessun contenitore finale e nessun item "acqua calda" | Da implementare |
| Ricotta | La produzione della cagliata recupera anche il siero; latte + requisito siero → ricotta nella Cheese Vat | Implementata con valori placeholder da ribilanciare |
| Parmigiano Reggiano | Latte vaccino + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato | Da implementare |
| Pecorino Romano | Latte di pecora + caglio + sale → forma fresca → stagionatura → porzionatura/grattugiato | Da implementare |
| Gorgonzola | Cagliata + sale + coltura erborinata → forma fresca → stagionatura | Da implementare |
| Provolone | Cagliata + sale + acqua → forma fresca → stagionatura → fette | Da implementare |
| Scamorza | Cagliata + sale → scamorza fresca usando `minecraft:lead` nello slot contenitore → breve asciugatura; possibile variante affumicata | Da implementare |
| Burrata | Mozzarella + panna → burrata; prodotto fresco senza stagionatura | Da implementare |
| Mascarpone | Panna + aceto di mele → mascarpone nella Cheese Vat; ciotola separata nello slot contenitore | Da implementare |

Gli identificatori degli item della filiera casearia sono stati fissati e
registrati. La checklist grafica completa è documentata in
[`TEXTURE_FORMAGGI.md`](TEXTURE_FORMAGGI.md). Quantità, tempi e valori
nutritivi restano invece placeholder fino al rebalancing finale. Gli
identificatori esistenti vanno preservati, salvo una migrazione esplicitamente
documentata.

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

- [x] Scegliere il catalogo di prodotti: mozzarella, Bocconcino, ricotta,
      Parmigiano Reggiano, Pecorino Romano, Gorgonzola, Provolone, Scamorza,
      Burrata e Mascarpone.
- [x] Definire la struttura generale dei processi produttivi riportata nella
      sezione 3.
- [x] Stabilire che il Bocconcino usa acqua normale: non viene introdotto un
      item separato per l'acqua calda.
- [x] Introdurre il siero come recupero della lavorazione della cagliata e
      riutilizzarlo nella filiera della ricotta.
- [x] Prevedere panna come intermedio comune per Burrata e Mascarpone.
- [x] Distinguere Parmigiano e Pecorino anche per origine del latte, prevedendo
      latte di pecora per il Pecorino.
- [x] Implementare Sheep Milk Bottle/Bucket, mungitura delle pecore adulte,
      comportamento di consumo coerente con FD/vanilla e compatibilità
      `#c:drinks/milk`.
- [x] Definire gli ingredienti intermedi principali: la panna usa una sola
      ricetta con `#c:drinks/milk` e ciotola, senza differenze tra secchio e
      bottiglia; la coltura erborinata è un item craftabile shapeless da
      `#c:foods/bread` + `#c:mushrooms`. Per la Scamorza si usa
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
- [x] Collegare il campo `whey` alle ricette: priorità Whey Bottle/Whey Bucket
      sul tank, consumo esatto dal tank solo a fine lavorazione e rispetto del
      toggle del flow durante recipe matching.
- [ ] Definire definitivamente rese, tempi di lavorazione, tempi di
      stagionatura/asciugatura, valori nutritivi e quantità ottenute dalle
      porzionature. Durante lo sviluppo possono essere usati valori placeholder
      da rivedere nel rebalancing finale. La Ricotta usa attualmente 1 output,
      200 tick, 4 fame e 0.3 saturation modifier come placeholder; il requisito
      siero resta definito: 500 mB dal tank oppure una bottiglia/secchio di
      siero consumati come unità intera, con priorità dell'item sul tank.
      La Panna usa temporaneamente 1 output, 160 tick, 2 fame e 0.2 saturation
      modifier come placeholder.
- [x] Definire capacità, interazioni, condizioni e recupero delle forme del
      sistema di stagionatura/asciugatura. Specifiche complete in
      [`STAGIONATURA_FORMAGGI.md`](STAGIONATURA_FORMAGGI.md): rack 1×1×1 con
      2 ripiani e 2 slot totali, stagionatura anche su superfici piane,
      Scamorza appesa al gancio, pausa
      nei chunk scaricati e reset del progresso quando il prodotto viene
      rimosso o il supporto viene rotto.

**Completamento:** ogni prodotto ha una catena definita, senza ambiguità, con
ingredienti, rese, tempi, porzioni e regole di stagionatura sufficienti per
l'implementazione.

### F3 — Completare i formaggi freschi

- [ ] Applicare il bilanciamento e le rifiniture concordate per la mozzarella.
- [x] Implementare la ricotta e collegarla al requisito siero della Cheese Vat,
      usando valori di bilanciamento placeholder.
- [x] Implementare la Panna come `cream_bowl` con valori placeholder e
      verificare la selezione Ricotta/Panna tramite il flow del tank.
- [x] Implementare la Coltura Erborinata craftabile da `#c:foods/bread` + `#c:mushrooms`.
- [x] Registrare gli item concordati, creare i relativi file item/model,
      aggiungere traduzioni italiane/inglesi e inserirli nel gruppo creativo.
- [x] Creare le texture mancanti seguendo la checklist
      [`TEXTURE_FORMAGGI.md`](TEXTURE_FORMAGGI.md).
- [x] Aggiungere le ricette Cheese Vat già definite per Bocconcino, forme
      fresche di Parmigiano/Pecorino/Gorgonzola/Provolone/Scamorza e Burrata,
      più i tag latte vaccino/pecora.
- [x] Aggiungere le porzionature già definite al Cutting Board e la ricetta
      Smoker della Scamorza affumicata, mantenendo rese placeholder.
- [ ] Collegare le forme fresche alle forme mature tramite il sistema di
      stagionatura/asciugatura della fase F4.
- [x] Registrare l'Aceto di Mele in bottiglia come ingrediente tecnico, senza
      texture né ricetta di produzione, e aggiungere il Mascarpone alla Cheese
      Vat con Cream Bowl + Apple Cider Vinegar + Bowl nello slot contenitore.
- [x] Aggiungere fette di Scamorza normali/affumicate, rese 4 + lazzo al
      Cutting Board e conversione della fetta normale nello Smoker.
- [x] Portare da 2 a 4 l'output del grattugiato di Parmigiano e Pecorino.
- [ ] Integrare le nuove lavorazioni in JEI e verificare i contenitori.

**Completamento:** mozzarella e ricotta sono ottenibili e utilizzabili in
survival, con ricette consultabili e valori coerenti con la tabella di F2.

### F4 — Implementare stagionatura e asciugatura

Design approvato: [`STAGIONATURA_FORMAGGI.md`](STAGIONATURA_FORMAGGI.md).

- [x] Definire e registrare i due processi data-driven:
      `italiansdelight:cheese_aging` e `italiansdelight:cheese_drying`,
      con serializer sincronizzati e ricette JSON per i cinque formaggi.
- [x] Fissare i tempi iniziali: Scamorza 12.000 tick, Gorgonzola 48.000,
      Provolone 72.000, Pecorino Romano 96.000 e Parmigiano Reggiano 120.000.
- [x] Implementare il primo ciclo di stagionatura su superfici piane:
      `fresh_parmigiano_reggiano`, `fresh_pecorino_romano`,
      `fresh_gorgonzola` e `fresh_provolone` sono piazzabili su una faccia
      superiore robusta, usano una forma visibile nel mondo e avanzano con
      timer lato server.
- [x] Implementare il Cheese Aging Rack 1×1×1 ispirato concettualmente agli
      scaffali di Fromage: **due ripiani e due slot totali**, uno per ripiano.
      Sono presenti BlockEntity, timer indipendenti, reset del progresso,
      12 varianti di legno vanilla e Cake vanilla come placeholder visivo dei
      formaggi sia nel rack sia sulle superfici normali. Un ripiano occupato può
      essere svuotato con click destro anche tenendo qualsiasi oggetto; con mano
      occupata il formaggio va nel primo slot completamente libero. Corrette
      anche le UV laterali della Cake placeholder.
- [ ] Definire e aggiungere le ricette di crafting dei rack per tutte le
      varianti di legno.
- [ ] Sostituire, se necessario, i placeholder Cake con modelli definitivi dei
      formaggi e rifinire il modello estetico finale del rack.
- [x] Implementare il Cheese Hook per **Scamorza e Provolone**: blocco,
      orientamento verso il giocatore, BlockEntity, inserimento/rimozione,
      reset del progresso, timer lato server, trasformazione automatica,
      drop alla rottura e modelli appesi originali. Sono appendibili anche
      `scamorza`, `smoked_scamorza` e `provolone` già pronti come
      decorazione. La Scamorza usa
      `cheese_drying`, il Provolone mantiene `cheese_aging`. Il modello
      dell'uncino ha il punto inferiore centrato e i formaggi appesi sono
      abbassati fino al punto di presa. Il facing è stato corretto rispetto al
      modello, le texture appese sono dedicate ma ricolorate sulla palette degli
      item e uno spazio tecnico invisibile impedisce di piazzare blocchi nel
      volume occupato dal formaggio sotto l'uncino.
- [x] Implementare per forme piazzate e rack il salvataggio del progresso
      **nel mondo**, senza component/NBT di progresso sugli ItemStack. Il gancio
      della Scamorza userà la stessa regola.
- [x] Applicare la regola di reset a formaggi piazzati, superfici di
      supporto, rack e Cheese Hook: nessun progresso viene salvato sull'item.
- [x] Mettere in pausa il progresso di forme piazzate, rack e Cheese Hook nei
      chunk scaricati, senza chunk loading forzato e senza recupero del tempo
      offline.
- [x] Trasformare automaticamente forme piazzate e contenuti del rack nel
      prodotto finale al 100%, usando risultato e durata della ricetta
      data-driven.
- [x] Integrare la texture JEI `aging.png` fornita dal developer in una
      categoria **Cheese Aging** unica che aggrega temporaneamente sia
      `cheese_aging` sia `cheese_drying`: comprende quindi anche Provolone e
      Scamorza. I due tipi gameplay restano separati.
- [ ] Eseguire i casi di test definiti nel documento di design, compresi
      riavvio, multiplayer, reset e assenza di duplicazioni.

**Completamento:** Parmigiano, Pecorino, Gorgonzola e Provolone possono
stagionare su rack o superficie; la Scamorza può asciugare appesa al gancio;
chunk unload e riavvii rispettano le regole concordate e qualsiasi rimozione
anticipata azzera il progresso senza salvarlo sull'item.

### F5 — Completare Parmigiano e Pecorino

- [x] Implementare preparazioni e ricette di stagionatura di Parmigiano
      Reggiano e Pecorino Romano.
- [x] Implementare wedge, grattugiato e relative ricette al Cutting Board;
      l'output del grattugiato è attualmente 4 ed è ancora soggetto al
      rebalancing finale.
- [x] Completare registrazioni item, modelli item, texture, traduzioni, common
      tag e visualizzazione JEI necessaria alla filiera attuale.
- [ ] Documentare gli ingredienti disponibili per `feature/dishes` e gli usi
      previsti, distinguendoli dalle ricette di piatti già implementate.
- [ ] Verificare rese e valori nutritivi lungo l'intera catena e impedire
      eventuali cicli di conversione che moltiplichino i prodotti.

**Completamento:** entrambi i formaggi sono producibili, stagionabili e
porzionabili in survival, con differenze riconoscibili per il giocatore.

### F6 — Completare integrazione e verifiche

- [x] Preparare la compatibilità opzionale con Fromage tramite common tag
      `c:foods/cheese` / `c:cheese`, senza dipendenza runtime. Strategia e
      limiti sono documentati in [`COMPAT_FROMAGE.md`](COMPAT_FROMAGE.md).
- [~] Controllare coerenza di nomi, texture, traduzioni e visualizzazione JEI:
      gran parte del lavoro è completata; restano verifica finale dei modelli
      di stagionatura e controlli globali prima del merge.
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
| Ripiano | 2 posizioni indipendenti, forme visibili, nessuna GUI | **Definito**: blocco 1×1×1, due ripiani, una forma per ripiano |
| Maturazione | Tempo come requisito, nessuna condizione ambientale nella prima versione | **Definito**: tempi in `STAGIONATURA_FORMAGGI.md` |
| Chunk scaricati | Pausa senza caricamento forzato | **Definito**: nessun avanzamento offline |
| Rimozione delle forme | L'item non conserva progresso | **Definito**: ritorno al prodotto fresco con timer 0 |
| Rottura del ripiano/supporto | Restituisce le forme ma azzera quelle incomplete | **Definito**: nessun dato di progresso sull'ItemStack |
| Formaggi appesi | Supporto sospeso per Scamorza e Provolone | **Definito**: Cheese Hook; Scamorza usa `cheese_drying`, Provolone `cheese_aging` |
| Porzioni | Tagliere come strumento principale | Numero di porzioni, grattugiato ed eventuali bonus nutritivi |

Le scelte definitive vanno riportate qui e nelle tabelle delle ricette prima
dell'implementazione della fase interessata.

## 6. Estensioni successive

Altri formaggi italiani oltre ai dieci prodotti già concordati potranno essere
valutati dopo il completamento della filiera principale. La struttura delle
ricette e della stagionatura dovrà consentire nuove varietà senza duplicare la
logica di base.
