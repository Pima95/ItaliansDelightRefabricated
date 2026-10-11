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
- [x] **Equivalenze di ingredienti tra mod**: due item riconosciuti esplicitamente come equivalenti sono lo stesso topping ai fini dell'anti-duplicato e del limite di 6 per quarto; il recupero restituisce sempre l'item originale.
- [x] Suddivisione **facoltativa con mattarello dopo la stesura**: intera -> due meta' -> divisione di una o entrambe le meta' in quarti, condibili indipendentemente. **Orientamento della prima divisione determinato dalla direzione del giocatore nel momento del clic**, anziche' fisso. Clic ingrediente applica alla zona puntata. Linee visibili temporaneamente solo tenendo mattarello o ingrediente valido; nessun cambio permanente ai modelli/texture.
- [x] Rimozione parziale senza rimborso; rimborso una sola volta dopo la rimozione di tutti i quarti coperti da una specifica applicazione.
- [x] **Rimozione con Shift + clic destro a mano vuota**: un condimento per clic, partendo dall'ultima applicazione ancora presente nella zona mirata; zona priva di topping = nessun effetto.
- [x] **Riunione con Shift + clic destro col mattarello**: due quarti della stessa meta' -> due meta' intere -> pizza intera, senza perdita, rimborso o redistribuzione dei topping preesistenti.
- [x] **Durabilita' del mattarello**: 1 punto per stesura completata, **0 per tutte le divisioni e riunioni**, 0 se il gesto non ha effetto e in Creativa. Il mattarello esistente ha 64 punti.
- [x] **Stesso ingrediente massimo una volta per sottoarea**: applicazioni su zone distinte consentite; se l'area puntata e' parzialmente coperta, il clic interessa solo la parte libera e consuma 1 item; se completamente coperta, nessun effetto/consumo.
- [x] **Salsa facoltativa**: pizze bianche valide, nessun ingrediente salsa richiesto in generale.
- [x] **Massimo una salsa per sottoarea fisica**, indipendentemente dal tipo; salse differenti ammesse in porzioni separate, mai sovrapposte.
- [x] **Rifiuto integrale delle salse differenti sulle zone parzialmente occupate**: se una zona mirata contiene gia' una salsa differente anche solo su una parte, il clic non applica nulla e non consuma item.
- [x] **Limite di 6 ingredienti diversi per quarto fisico**, salsa inclusa e facoltativa; invariato da divisioni e riunioni. Un tentativo che supererebbe sei in **anche un solo quarto interessato** e' rifiutato integralmente senza consumare item.
- [x] Recupero ingredienti direttamente nell'inventario, drop solo se pieno; rottura della pizza cruda recupera impasto e ingredienti residui senza duplicazioni, pizza cotta non restituisce ingredienti.
- [x] Pala per pizza: porta direttamente una sola pizza alla volta e conserva l'intero stato; non richiesta per condire.
- [x] **Pala senza durabilita'**, riutilizzabile indefinitamente per raccogliere, spostare e trasferire pizze.
- [x] **Pala come futura arma solo quando VUOTA**, ispirata alla padella di Farmer's Delight Refabricated. **Se OCCUPATA non attiva alcun bonus o effetto speciale da arma** e mantiene intatti i dati della pizza. Statistiche e dettagli non concordati; codice non implementato.

## Questioni da risolvere prima del codice (NON CONFERMATE)

- [ ] Bilanciamento definitivo della stesura (attualmente 4 clic a mano, 1 col mattarello).
- [x] Ricetta del mattarello: 2 bastoni + 1 asse di qualsiasi legno (`#minecraft:planks`), con sblocco nel libro delle ricette (**test in gioco necessario**).
- [x] Texture dedicata del mattarello: item handheld 16×16, sfondo trasparente e pixel opachi; durabilità confermata: **64 usi**, 1 per pizza stesa. **Aspetto da verificare in gioco.**
- [x] **Definito il limite:** massimo 6 ingredienti diversi per quarto fisico (salsa inclusa), sostituisce la vecchia proposta non approvata di 8 porzioni.
- [ ] Definire **formato tecnico e mapping iniziale** dei gruppi di equivalenza cross-mod, remainder e ulteriori bilanciamenti; **la regola di gameplay sulle equivalenze e' gia' confermata**; il comportamento delle **salse differenti sulle zone parzialmente condite e' gia' confermato** (rifiuto dell'intera applicazione). La coesistenza di salse diverse sulla **stessa sottoarea e' vietata**, su sottoaree distinte e' consentita. Per uno stesso ingrediente e' confermato **1 item per nuova applicazione effettiva a zone libere**, e nessun item per clic duplicato.
- [x] Definire il gesto per **rimuovere** topping: Shift + clic destro **a mano vuota** sulla zona puntata, dall'ultima applicazione alla piu' vecchia.
- [ ] Implementare cronologia persistente delle applicazioni e rimozione per zona senza duplicazioni.
- [x] Confermato che l'**orientamento della prima divisione dipende dalla direzione del giocatore**, non da un asse mondo fisso, e che la linea e' **parallela allo sguardo**, creando meta' sinistra/destra.
- [x] **Confermati linea parallela alla direzione cardinale piu' vicina allo sguardo e orientamento a scatti** N/S/E/O, per due meta' sinistra/destra. Non viene usata una rotazione libera per le diagonali.
- [x] **Selezione delle zone tramite posizione effettiva del clic**; sui confini e sulle intersezioni una sola zona in modo deterministico, senza selezioni doppie.
- [ ] Definire **quale zona prevale esattamente sul confine** (criterio di spareggio fisso), lo spareggio deterministico negli angoli a meta' tra due cardinali e i dettagli di hitbox/tolleranze (**snap cardinale, gesti e assenza di usura gia' confermati**).
- [ ] Pala: definire ricetta, tooltip, item occupato/distrutto, gestione inventari e dettagli di interazione col forno (**ruolo di trasporto e assenza di durabilita' gia' confermati**).
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

- [ ] Definizioni ingredienti via codice/datapack, con **identita' canonica** dei gruppi equivalenti cross-mod distinta dall'ItemStack originale consumato e da categorie generiche.
- [ ] Aggiunta topping con clic destro e gestione quantità.
- [ ] Implementare le **divisioni facoltative create col mattarello** (intera -> meta' -> quarti) e la **riunione inversa con Shift + clic destro** (due quarti stessa meta' -> due meta' -> intera), **senza consumare durabilita'**, salvandone la configurazione e preservando posizioni e storici dei topping; selezionare la zona dal punto reale di clic con **un solo risultato deterministico ai confini**; evidenziare le linee solo con mattarello o ingrediente valido.
- [ ] Implementare l'identita' delle singole applicazioni, le coperture su zone/quarti logici, il **vincolo dello stesso ingrediente massimo una volta per sottoarea**, le applicazioni parziali dei topping alle sole zone ancora libere, **il blocco totale delle salse differenti se una qualsiasi parte della zona mirata contiene gia' un'altra salsa**, la **validazione atomica del massimo di 6 tipi per quarto fisico (salsa inclusa)** e la rimozione parziale con rimborso senza duplicazioni (vedi [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md)).
- [ ] Recupero diretto nell'inventario (drop solo con spazio insufficiente); rottura pizza cruda/cotta secondo specifica.
- [ ] Pala: trasporto diretto di una pizza alla volta con dati persistenti e trasferimento sicuro al mondo/forno, **senza durabilita' ne' usura nei trasferimenti**.
- [ ] Gestire gli eventuali remainder dei contenitori senza duplicazioni.
- [ ] Nomi dinamici localizzati e ricette riconosciute.
- [ ] Rendering provvisorio per convalidare i dati.

### Estensione successiva: pala usabile anche come arma (pianificata)

- [ ] Studiare il comportamento effettivo della **padella di Farmer's Delight Refabricated** come riferimento per la pala.
- [ ] Concordare danni, velocita' d'attacco, animazioni ed effetti **della sola pala vuota**, e regole di priorita' con raccolta/posizionamento. **Pala occupata: funzionalita' speciali da arma disabilitate** (decisione confermata).
- [x] **Confermato** che la pala con pizza non funge da arma e non conferisce effetti/bonus speciali da combattimento.
- [ ] Verificare che lo stato vuota/occupata disattivi e riattivi correttamente gli attributi speciali da arma; gli attacchi base di Minecraft non devono alterare, perdere o duplicare la pizza trasportata.
- [ ] Aggiungere e testare la funzionalita' **solo dopo** averne definito le regole; non e' richiesta per considerare concluso il sistema base di trasporto.

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

**Priorita' esatta ai confini delle zone e hitbox tecniche** e spareggio deterministico al confine esatto fra direzioni cardinali; poi formato delle definizioni di equivalenza cross-mod. La **scelta di una sola zona dalla posizione effettiva del clic, anche sui confini, e' confermata**. **La linea e' gia' confermata parallela alla direzione cardinale piu' vicina allo sguardo (scatti N/S/E/O)** e divide sinistra/destra. L'orientamento dipendente dal giocatore e' confermato. **Il principio di equivalenza e' confermato:** stesso ingrediente logico, un solo tipo nel limite di 6 e nessuna doppia applicazione sullo stesso quarto. L'assenza di usura del mattarello per dividere/riunire e il limite di **6 ingredienti diversi per quarto** sono gia' confermati. Il rifiuto delle salse differenti su zone anche solo parzialmente occupate e' confermato. Il divieto di ripetere lo stesso ingrediente sulla stessa sottoarea e' gia' confermato. La riunione con Shift + clic destro col mattarello e la conservazione dei topping sono gia' **confermate**. Le decisioni gia' prese sono registrate in [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).
