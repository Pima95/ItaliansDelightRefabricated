# Stato corrente di Italian's Delight — Pizza 1.0.0

**Branch:** `feature/pizza-1.0.0` (la `main` è protetta).  
**Data verifica:** 22 ottobre 2026. **Versione del JAR:** ancora `0.4.0`; il numero della 1.0.0 non è stato incrementato né pubblicato.  
**Riferimenti:** [roadmap](ROADMAP.md), [checklist di release](RELEASE_READINESS.md), [bug](../KNOWN_BUGS.md), [migliorie](../PLANNED_IMPROVEMENTS.md), [audit](../CODE_AUDIT_2026-10-19.md) e [ottimizzazioni](../OPTIMIZATION_REVIEW_2026-10-19.md).

## Funzionalità implementate

- Preparazione pizze nel mondo (impasto, stesura, quarti, posa/rimozione ingredienti) e crafting shapeless di pizze uniformi.
- Pala riutilizzabile con cargo, Pizza Oven, fornace vanilla e smoker con cottura e rimborsi di ciotole.
- 18 ricette di pizza riconosciute, Pizza mista con grafica Margherita, nomi e sprite propri degli spicchi; massimo **16 spicchi uguali per stack**.
- Valori alimentari dinamici approvati (due ingredienti differenti per categoria, massimo 6 fame/6 saturazione) e Nourishment breve quando dovuto.
- JEI e scheda Creativa per l'aggiornamento, senza la doppia Pizza Bianca.
- Preservazione delle componenti strutturate, nome personalizzato, lore e `CUSTOM_DATA` durante cottura; i metadati specifici di altri sistemi devono essere valutati caso per caso.

## Test *effettivamente confermati dall'autore*

- [x] Il sistema di stacking degli spicchi funziona fino a 16.
- [x] Il Pizza Oven non lascia estrarre pizze crude dall'ingresso tramite hopper.
- [x] I quattro gusti di una Pizza mista restano distinti dopo salvataggio e riavvio.
- [x] Icona Margherita della Creativa e riordino della scheda funzionano.
- [x] Nome «Pizza mista» e rimozione della mozzarella intera come topping sono stati confermati in game.
- [ ] Tutti gli altri casi combinatori elencati nella [checklist completa](RELEASE_READINESS.md), in particolare server dedicato, due giocatori, hopper pieni/rinominati e aggiornamenti da versioni vecchie.

## Bug registrati

| ID | Stato effettivo |
|---|---|
| BUG-001 — Scamorza/Provolone a terra | Fix pubblicato, manca prova su superfici e uncino |
| BUG-002 — Mozzarella intera | Risolto e testato |
| BUG-003 — Drop con supporti rimossi | Fix pubblicato, mancano test reali di drop/assenza di duplicazioni |

## Audit tecnico del 19 ottobre

| ID | Stato aggiornato |
|---|---|
| AUD-001 | Fix introdotto per `CUSTOM_NAME`, `LORE`, `CUSTOM_DATA`; test cottura con componenti custom pendente |
| AUD-002 | Risolto precedentemente (BUG-002) |
| AUD-003 | Compilazione e test base già disponibili, **server dedicato e carichi speciali ancora da verificare** |
| AUD-004 | Output/preview JEI dinamici: comportamento in-game da verificare |
| AUD-005 | Mappings da datapack server vs anteprima client: protocollo da progettare, non allargare la whitelist |
| AUD-006 | Modificatori esterni della pala: regola di compatibilità non approvata; nessun cambio |
| AUD-007 | Duplicazione *del codice* di invio ciotole eliminata, non equivale a bug di dupe in game |
| AUD-008 | Grafica e prestazioni dei topping 2.5D: necessarie prove e approvazione per nuovi asset |
| AUD-009 | Cache Cheese Vat: richiede benchmark e invalidazione sicura |
| AUD-010 | Sale su chunk scaricati: richiede benchmark senza rompere i 12.000 tick |
| AUD-011 | Migrazione del codec pizza: mantenute le regole strette, futura versione richiede piano dedicato |
| AUD-012 | Pipeline Java 25 + verifiche statiche presenti; **manca suite di test Java/runtime** |
| AUD-013 | Risolta ambiguità operativa con questo stato attuale; cronologia in ROADMAP preservata |
| AUD-014 | Lettura JEI ora tollera level nullo e reload, test client di leave/rejoin ancora necessario |

## Ottimizzazioni

- **Applicate:** OPT-002 (preview senza preparare ciotole), OPT-007 (un unico update dei topping), OPT-010 (rimborsi condivisi), OPT-012 (meno array temporanei nella pala), OPT-014 (generatore/test delle ricette degli scaffali).
- **Ancora da misurare o richiedono cautela:** OPT-001, OPT-003–006, OPT-008–009. Profilare prima con più pizze, vasche, calderoni e stagionature; non introdurre cache senza invalidazione rigorosa.
- **Rifiniture opzionali:** OPT-004 renderer, OPT-011 nomi, OPT-013 grafica JEI. **OPT-015** è una linea guida: non rimuovere validazioni e copie difensive soltanto per risparmiare qualche operazione.

## Migliorie IMP-001–IMP-004 integrate — 22 ottobre 2026

- [x] Cardoon e basilico selvatici allineati alla rarità delle piante Farmer's Delight (1/120, 64 tentativi).
- [x] Cardoon coltivabile con texture 16×16 e un unico fiore centrato nel modello adulto, vecchi item/ricette conservati.
- [x] Nuovi biomi per entrambe le piante approvati dall'autore.
- [x] Suoni di lanterna vanilla per Hanging Hook.
- [x] Ciotole restituite nella crafting table delle pizze senza doppio rimborso al forno; test in-game confermato dall'autore.

L'autore ha confermato il funzionamento delle modifiche il 22 ottobre 2026. Il branch `feature/cardoon-farming-worldgen-sounds` è stato unito con **fast-forward** in questo branch, senza conflitti né nuovi branch. Vedi [registro delle migliorie](../PLANNED_IMPROVEMENTS.md) e [guida Cardoon](../CARDOON_FARMING_WORLDGEN.md).

Il completamento di queste funzioni non costituisce conferma automatica dei test non ancora eseguiti su server dedicato, concorrenza, modpack esterni e vecchi salvataggi.

## Test di regressione per gli ultimi cambiamenti

1. Incudine: assegnare un nome a una Pizza mista cruda; cuocerla in tutti e tre i forni, verificare nome, componenti della pizza e quattro nomi degli spicchi.
2. Componenti extra `LORE` e `CUSTOM_DATA`: verificare che sopravvivano al passaggio cruda → cotta, ma che `ITEM_MODEL` continui a dipendere dal gusto.
3. Ciotole: una applicazione di salsa su quattro quarti = una ciotola, due applicazioni distinte = due; hopper sotto parziale/pieno/rinominato.
4. Due client: applicare/rimuovere topping e verificare refresh istantaneo. Su 20+ pizze verificare assenza di stale render.
5. JEI: uscita/ingresso mondo, `/reload`, formaggi su rack/uncino, Cheese Vat; nessun crash e categorie corrette dopo join.
6. Pala: mantenere lo stesso modello e posizionamento 3D in entrambe le mani, vuota e carica. Non sono state alterate le texture.

**Non sono state modificate texture approvate né i valori nutrizionali, i tempi di cottura, gli ID registrati o la versione del JAR.**

## Correzioni BUG-001 e BUG-003 rafforzate — 22 ottobre 2026

**Commit:** il ramo `feature/pizza-1.0.0` include salvaguardie contro drops persi e duplicazioni causate da stati incoerenti (supporto rimosso, BE assente, setBlock/removeBlock falliti). Il normale blocco di posizionamento a terra dei formaggi da uncino resta invariato.

- `CheeseHookBlock`: rifiuta di piazzarsi senza soffitto; getDrops aggiunge il prodotto leggendo direttamente il suo BlockState, senza dipendere dalla BE opzionale.
- `CheeseHookBlockEntity`: insert e remove effettuano il commit dello stato prima della transazione inventario; serverTick intercetta anche uncini senza supporto ricaricati in un chunk.
- `AgingCheeseBlockEntity`: al caricamento e durante il tick recupera ruote senza pavimento attraverso il normale percorso di loot.
- `AgingCheeseBlock`: prelievo manuale prima rimuove il blocco senza drop, poi consegna il singolo formaggio.

**Stato reale:** implementato, soggetto a CI; l'autore deve ancora confermare le prove del [registro bug](../KNOWN_BUGS.md), soprattutto uncini con salumi/formaggi appesi, rottura del supporto, vecchi mondi, casi `doTileDrops=false` e multiplayer. Non indicare BUG-001/003 come confermati in-game prima delle prove.
