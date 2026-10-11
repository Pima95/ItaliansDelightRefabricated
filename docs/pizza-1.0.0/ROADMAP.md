# Roadmap — Pizza Update 1.0.0

## Stato

**Posizionamento, stesura manuale e con mattarello confermati funzionanti dal giocatore.** Il mattarello ha **64 utilizzi** con usura di 1 per stesura riuscita. Aggiunta ricetta per **2 bastoni + qualsiasi asse di legno**, tramite tag `#minecraft:planks`; **crafting da testare**. Ridisegnati i cinque modelli dell'impasto con base schiacciata e bordo progressivamente rialzato, **in attesa di test visivo**. **Primo prototipo di condimento mozzarella in BlockState implementato, da testare in gioco.** Pomodoro, condimenti multipli, cottura e persistenza di combinazioni arbitrarie non ancora implementati.

## Checklist decisioni già CONFERMATE

- [x] Versione-obiettivo 1.0.0 con sistema di pizza personalizzabile.
- [x] Palla d'impasto posizionata direttamente sopra i blocchi, non sul Cutting Board.
- [x] Stesura sia a mano (clic destro) sia con mattarello.
- [x] Condimento diretto nel mondo con clic destro, senza GUI.
- [x] Preparazione tramite crafting table disponibile come alternativa.
- [x] Pizze riconosciute con nomi prestabiliti, altre con nomi dinamici.
- [x] Pizza Oven dedicato oltre a fornace e affumicatore vanilla.
- [x] Pizza Oven 1×1×1, **capienza una pizza alla volta**.
- [x] Pizza Oven accetta **tutti i combustibili vanilla**.
- [x] **Nessuna bruciatura** con qualsiasi metodo.
- [x] Rendering 2.5D: **salse piatte, altri topping poco rialzati**.
- [x] Ingredienti di altre mod supportabili tramite definizioni e tag.
- [x] Pizza divisa logicamente in quattro quarti condibili separatamente.
- [x] Rimozione parziale senza rimborso; rimborso una sola volta dopo la rimozione di tutti i quarti coperti da una specifica applicazione.
- [x] Recupero ingredienti direttamente nell'inventario, drop solo se pieno; rottura della pizza cruda recupera impasto e ingredienti residui senza duplicazioni, pizza cotta non restituisce ingredienti.
- [x] Pala per pizza: porta direttamente una sola pizza alla volta e conserva l'intero stato; non richiesta per condire.

## Questioni da risolvere prima del codice (NON CONFERMATE)

- [ ] Bilanciamento definitivo della stesura (attualmente 4 clic a mano, 1 col mattarello).
- [x] Ricetta del mattarello: 2 bastoni + 1 asse di qualsiasi legno (`#minecraft:planks`), con sblocco nel libro delle ricette (**test in gioco necessario**).
- [x] Texture dedicata del mattarello: item handheld 16×16, sfondo trasparente e pixel opachi; durabilità confermata: **64 usi**, 1 per pizza stesa. **Aspetto da verificare in gioco.**
- [ ] Limite delle porzioni di topping (proposta: 8 incluse le salse).
- [ ] Consumi esatti per ingrediente, ripetizioni, più salse, remainder.
- [ ] Definire i gesti per scegliere/rimuovere il condimento da un quarto; Shift + clic destro e' ancora una possibile interazione, non una decisione definitiva.
- [ ] Pala: definire ricetta, durabilita', tooltip, item occupato/distrutto, gestione inventari e dettagli di interazione col forno (il ruolo di trasporto di una pizza e' gia' confermato).
- [ ] Recuperare e verificare l'asset della pala dalla vecchia Italian's Delight fornito dall'autore, senza sostituirlo.
- [x] Casi approvati/esclusi per il posizionamento dell'impasto, documentati in [SURFACE_RULES.md](SURFACE_RULES.md). **Soul Sand ora vietata; rimossi modelli ribassati.**
- [ ] Verificare in gioco che la Soul Sand sia vietata e che slab superiori/doppie, scale capovolte, botole superiori chiuse e incudini funzionino ancora; verificare anche recupero se il supporto viene rotto.
- [ ] Tempi cottura (proposti: 20/30/60 secondi).
- [ ] Consumo fuel del Pizza Oven, preriscaldamento e burn time residuo.
- [ ] Hopper sopra/lati/sotto e fallback manuale.
- [ ] Ricetta crafting Pizza Oven.
- [ ] Griglia crafting della pizza libera e output crudo/cotto.
- [ ] Pizza cotta mangiabile intera o tagliata in 4 fette.
- [ ] Riconoscimento esatto di gusti e quantità, priorità tra ricette.
- [ ] Elenco ufficiale delle pizze riconosciute e dei condimenti iniziali.
- [ ] Valori fame, saturazione, eventuali effetti alimentari.
- [ ] Test visivo delle nuove forme e hitbox dell'impasto nei cinque stadi (vedi [DOUGH_GEOMETRY.md](DOUGH_GEOMETRY.md)).
- [ ] Gestione delle collisioni, del posizionamento e rendering di dettaglio.
- [ ] API effettive per mantenere data component nelle fornaci vanilla.

## Piano di sviluppo proposto

### Fase 1: fondamenta interattive

- [ ] Registrare palla d'impasto, blocco di preparazione e dati serializzabili.
- [x] Implementare e testare l'appoggio della palla su superfici approvate.
- [x] Implementare e testare la stesura a mano e con mattarello (**entrambi i metodi confermati funzionanti dall'autore**).
- [ ] Salvare e recuperare correttamente stati e oggetti.
- [ ] Test chunk unload, multiplayer, rottura del supporto e superfici particolari.

### Fase 2: sistema condimenti

- [ ] Test mozzarella sulla base completamente stesa: aggiunta via clic destro, un solo item consumato, blocco doppia aggiunta, modello 2.5D e recupero tramite loot.

- [x] Zona interna per condimenti definita nella nuova base finale (`x/z = 3..13`, centro alto 1 pixel), con bordo alto 2 pixel (**conca profonda 1 pixel**); implementazione dopo il test della geometria.
- [ ] Decidere se il pomodoro di Farmer's Delight va applicato direttamente oppure trasformato prima in salsa.

- [ ] Definizioni ingredienti via codice/datapack.
- [ ] Aggiunta topping con clic destro e gestione quantità.
- [ ] Implementare i **quattro quarti**, l'identita' delle singole applicazioni, i mask dei quarti ancora coperti e la rimozione parziale con rimborso senza duplicazioni (vedi [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md)).
- [ ] Recupero diretto nell'inventario (drop solo con spazio insufficiente); rottura pizza cruda/cotta secondo specifica.
- [ ] Pala: trasporto diretto di una pizza alla volta con dati persistenti e trasferimento sicuro al mondo/forno.
- [ ] Gestire gli eventuali remainder dei contenitori senza duplicazioni.
- [ ] Nomi dinamici localizzati e ricette riconosciute.
- [ ] Rendering provvisorio per convalidare i dati.

### Fase 3: rendering 2.5D

- [x] Texture dell'impasto crudo nei cinque stadi di stesura: superficie, lati e particelle dedicate 16×16, completamente opache. **Aspetto da verificare in gioco**; [anteprima e test](TEXTURES.md).
- [ ] Pizza condita cruda/cotta.
- [ ] Salse piatte, formaggi/condimenti 2.5D, preset riutilizzabili.
- [ ] Seed deterministico e conservazione della distribuzione.
- [ ] Aspetto item inventario, eventuali fette e pizza nel forno.
- [ ] Fallback per topping modded e stress test prestazioni.

### Fase 4: Pizza Oven

- [ ] Forno compatto orientabile con una pizza alla volta.
- [ ] BlockEntity persistente, interazioni manuali e nessuna bruciatura.
- [ ] Tutti i combustibili vanilla, rimanenze dei contenitori e energia residua.
- [ ] Automazione con hopper, se confermata.
- [ ] Modelli, animazioni, traduzioni e ricetta.

### Fase 5: forno vanilla e crafting

- [ ] Prototipo conservazione componenti passando da **fornace e affumicatore vanilla**.
- [ ] Ricetta speciale di crafting per pizze libere.
- [ ] JEI: esempi di pizze riconosciute, condimenti, lavorazioni e forni.
- [ ] Taglio, cibo/nutrizione e ri-posizionamento pizza cotta, se confermati.

### Fase 6: test e pubblicazione

- [ ] Test approfonditi con ingredienti della mod e di altre mod.
- [ ] Test stress di tanti topping/pizze, multiplayer e server dedicato.
- [ ] Test no bruciatura, hopper, chunk unload, salvataggi e dupe.
- [ ] Rebalancing dei tempi, costi e valori alimentari.
- [ ] Documentazione EN/IT, README, CHANGELOG 1.0.0, JEI.
- [ ] Build finale, PR su `main`, release GitHub, CurseForge, Modrinth.

## Regole da rispettare

- Sviluppare sul branch `feature/pizza-1.0.0`; `main` è protetta e non va modificata direttamente.
- Prima di implementare meccaniche non ancora confermate, discuterle.
- Non fare modifiche radicali alle meccaniche cheese/cured meats già funzionanti.
- **Non spostare, eliminare, sovrascrivere o sostituire asset creati dall'autore** senza consenso esplicito.
- Tutti i JSON d'esempio nei documenti sono **bozze progettuali**, non file di datapack già utilizzabili.

## Prossimo tema da concordare

**Quantita', copertura e limiti per applicazione/ciascun quarto** (unita' massime, item ripetibili, quantita' consumata, salse), poi gesto preciso per rimozione e selezione del quarto, insieme dei gusti riconosciuti e dettagli del rendering. Le decisioni gia' prese sono registrate in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).
