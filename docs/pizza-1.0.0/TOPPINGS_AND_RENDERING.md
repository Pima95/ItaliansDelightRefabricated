# Condimenti e rendering 2.5D

## Direzione confermata

La pizza viene condita **cliccando sulla base posizionata nel mondo**. Il risultato deve mostrare ingredienti effettivamente scelti, non solo un'icona generica.

**Rendering 2.5D CONFERMATO:**
- salse **piatte** sul disco;
- formaggi, salumi, verdure, funghi ed erbe **leggermente in rilievo**;
- composizione leggibile sia da cruda sia da cotta.

Non è prevista una texture completa separata per ogni combinazione possibile.

## Divisione facoltativa, coperture e provenienza — CONFERMATO

Una pizza stesa e' inizialmente **intera**: si puo' condire completamente senza dividere nulla. Il mattarello permette di creare **2 meta'**, con orientamento scelto dalla **direzione del giocatore al momento della prima divisione**, e successivamente di suddividere una o entrambe le meta' in quarti, **senza cambiare i modelli/texture della base**. Il topping cliccato viene applicato alla **sola zona logica selezionata dalla posizione effettiva del clic**: intera, meta' o quarto. **Anche un clic esattamente su un confine o sull'incrocio delle linee produce un'unica zona, scelta deterministicamente**; non un'applicazione automatica a due zone. Le linee di divisione sono un **overlay temporaneo**, visibile solo mentre si tiene il mattarello o un ingrediente valido.

Internamente e' possibile mappare tutte le coperture sui quattro quarti logici, pur memorizzando la **configurazione effettiva** della divisione. Ogni applicazione conserva i quarti originari e quelli ancora coperti. **Rimuovere solo una parte dell'applicazione non restituisce l'item**, che viene recuperato una sola volta alla rimozione completa. Esempio: una mozzarella applicata all'intera pizza rappresenta una sola applicazione, anche se dopo la si divide in quarti; eliminarla da quattro quarti non rimborsa quattro mozzarelle.

**Orientamento confermato:** la linea della prima divisione non segue un asse globale fisso: dipende dalla direzione in cui guarda il giocatore nel momento del clic. Il rendering deve mantenere la suddivisione associata alla pizza mentre il giocatore si muove e dopo il trasporto, senza alterare il posizionamento dei topping. **CONFERMATO:** la linea centrale e' **parallela alla direzione cardinale piu' vicina allo sguardo orizzontale**, con **orientamento a scatti N/S/E/O**, producendo meta' a **sinistra e destra** del giocatore, non davanti/dietro. Non e' prevista rotazione libera delle linee in diagonale. Memorizzare il cardinale scelto per renderizzare i medesimi overlay e topping in modo stabile. Restano da precisare lo spareggio degli angoli esattamente intermedi e i clic sul confine.

**Riunione confermata:** Shift + clic destro col mattarello riunisce prima i due quarti della stessa meta' e infine le due meta' intere. Le coperture e le posizioni visive dei topping gia' applicati **non cambiano** quando l'overlay viene unificato, e non si restituiscono ingredienti. Dopo la riunione i topping aggiunti successivamente coprono la nuova zona logica; il renderer deve conservare le posizioni degli ingredienti preesistenti.

**Nessun topping duplicato nella stessa sottoarea — CONFERMATO:** piu' applicazioni dello stesso ingrediente, **incluse versioni di altre mod esplicitamente equivalenti**, sono ammesse **solo quando coprono aree fisiche differenti**. In una zona parzialmente gia' condita, un clic aggiunge l'ingrediente alle sole sottoaree libere, consumando un item; un clic interamente duplicato non consuma nulla. Dopo una riunione la copertura preesistente resta al suo posto. Le applicazioni originali mantengono identita' e cronologia per rimuoverle e rimborsarle senza duplicazioni.

Per il modello di stato, recupero e pala vedere [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

## Geometria già predisposta (modelli dell'impasto)

I cinque modelli ora realizzano una **base piatta, con scanalatura centrale e bordo rialzato**, senza un secondo strato appoggiato sopra come nel primo prototipo. Alla fine della stesura la superficie interna arriva a Y=1/16 e il bordo a Y=2/16, con **scanalatura profonda esattamente 1/16 di blocco (1 pixel)** e fascia esterna larga 2/16. Il condimento va collocato nella zona centrale (da X/Z=3 a 13) e deve **lasciare visibile il bordo**.

Per geometrie e hitbox dettagliate vedere [DOUGH_GEOMETRY.md](DOUGH_GEOMETRY.md).

**Prototipo successivo alla modifica dei modelli:** primo topping rappresentato da `italiansdelight:mozzarella`. Clic destro sulla base stesa: si consuma una mozzarella intera, il BlockState salva `mozzarella=true` e il modello multipart disegna cinque pezzi poco rialzati usando la texture mozzarella già esistente. Il loot table del **prototipo** restituisce ancora l'ingrediente alla rottura. **Per il sistema definitivo questa regola e' superata:** rompere una **pizza formata ancora integra** deve rilasciare **un item pizza integrale** (se la pizza cotta e' gia' stata servita parzialmente, solo le fette residue) con tutti i topping e le divisioni conservati, **senza** restituire ingredienti separati. **Questo non è ancora il sistema definitivo**: il booleano e il loot provvisorio non rappresentano quarti, identita' delle applicazioni, rimozione parziale, rimborso per applicazione e trasporto sulla pala. Tutto richiedera' dati persistenti generalizzati.

## Salsa facoltativa e unica per porzione — CONFERMATO

La pizza puo' essere **completamente bianca**, avere salsa soltanto su una parte o usare **tipi di salsa differenti su parti fisiche distinte**. **Non si possono sovrapporre due salse sulla stessa sottoarea**, neppure se sono diverse (per esempio pomodoro e pesto). Dividere o riunire la pizza non cambia le coperture delle salse preesistenti. Gli altri topping possono essere aggiunti anche senza salsa. Una salsa puo' essere necessaria per riconoscere una ricetta specifica, ma **non e' richiesta per preparare una pizza libera**.

**CONFERMATO:** se la zona selezionata contiene gia' **anche soltanto su una parte** una salsa *differente*, il clic con la nuova salsa viene **rifiutato interamente**: nessuna applicazione neanche sulle parti libere, nessuna sostituzione, nessun consumo di item. Dopo aver riunito due meta' (una al pomodoro, una bianca), un tentativo di applicare pesto alla pizza intera fallisce. Il giocatore puo' usare il mattarello per dividerla nuovamente e applicare il pesto **solo alla meta' bianca**.

Per la prima salsa è ancora da decidere se usare il pomodoro di Farmer's Delight direttamente o una salsa dedicata.

## Idoneita' fisica dei topping — CONFERMATO (12 ottobre 2026)

Il renderer 2.5D deve rappresentare **vere porzioni effettivamente utilizzabili**, non far sembrare valida una forma intera di prosciutto, una ruota di formaggio o una bistecca mediante una semplice riduzione del modello. **Il requisito di porzionatura/preparazione e' applicato prima di scegliere un preset grafico**: ammettere item affettati, grattugiati, tagliati in pezzi piccoli, salse/creme/erbe pronti; rifiutare item troppo grandi, interi o intermedi, **anche se trovati in un tag comune di categoria**. Il fatto che il formaggio sia `cheese_pieces` o che la carne sia `chunk_small` non rende utilizzabile qualunque item senza lavorazione. **Carne e pesce porzionati possono essere utilizzati crudi o cotti (CONFERMATO), con gli stessi bonus nutrizionali e senza effetti speciali**. La validazione e il rendering non devono respingere automaticamente la variante cruda di un tipo supportato. Le differenze visive eventuali tra crudo e cotto non influiscono su punteggi, categoria o limite di sei topping per quarto; non aggiungere trasformazioni automatiche dell'ItemStack senza una decisione separata.

La Cutting Board di Farmer's Delight Refabricated puo' produrre le porzioni prima del condimento **soltanto dove esistono ricette e output validi**; non serve per stendere o per servire le fette di pizza. Vedere [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md).

## Categorie ingredienti e preset (PROPOSTE)

| Categoria | Preset visuale | Esempi |
| --- | --- | --- |
| Salse | `flat_sauce`, superficie colorata | pomodoro, pesto futuro, creme |
| Formaggi | `cheese_pieces`, chiazze/pezzi bassi | mozzarella, gorgonzola, scamorza, provolone |
| Formaggi grattugiati | `grated_cheese`, scaglie sparse | parmigiano, pecorino |
| Salumi rotondi | `slice_round` | salame |
| Salumi a falde | `slice_folded` | prosciutto crudo, speck, pancetta |
| Pezzi piccoli | `chunk_small` | funghi, verdure, mortadella |
| Anelli | `ring_small` | cipolla, peperone |
| Foglie/erbe | `leaf_small` | basilico, altre erbe |
| Altri mod non specifici | `generic_topping` | fallback basato sulla categoria |

**Rendering a livelli proposto**: (1) impasto, (2) salsa, (3) formaggi, (4) salumi/verdure, (5) erbe e dettagli. L'ordine nel rendering non impone l'ordine di aggiunta degli ingredienti. **Le linee di divisione non hanno hitbox proprie**: il renderer deve usare lo stesso orientamento e le stesse zone logiche del selettore di hit, senza usare la vicinanza a una linea per condire automaticamente due parti.

## Ingredienti speciali e ruoli — CONFERMATO (12 ottobre 2026)

| Ingrediente | Ruolo visivo/interattivo | Categoria nutrizionale | Fame | Saturazione |
| --- | --- | --- | ---: | ---: |
| Uova | topping solido | proteine animali con salumi/carne/pesce | +2 | +2,5 |
| Olive | topping solido | vegetali con funghi e verdure | +1 | +0,5 |
| Olio d'oliva | olio / velatura (**grafica da definire**), non salsa | **oli e grassi (nuova)** | +0 | +1 |
| Panna | crema anche con ruolo salsa | latticini con formaggi | +1 | +1,5 |

**Il ruolo della pizza e' distinto dalla categoria nutrizionale**: se panna ha ruolo salsa, occupa l'unico slot salsa nel quarto ma nutrizionalmente conta solo come latticino; l'olio e' compatibile con la salsa senza occupare il suo ruolo, pur contando nel massimo di 6 topping. Ingrediente/categoria non significa equivalenza: uovo e prosciutto, panna e mozzarella restano item distinti per lo storico e i rimborsi. Ogni categoria contribuisce **una volta per quarto**; entrambe le statistiche mantengono tetto 6.

**Compatibilita' release 1.0.0 confermata:** mapping e resa/fallback per ingredienti selezionati da **Italian's Delight Refabricated e Farmer's Delight Refabricated**, come salsa di pomodoro, cipolla, uova fritte, bacon, prosciutto, pesce e altri ingredienti idonei; le **altre mod forniscono soltanto item sostitutivi equivalenti** riconosciuti tramite tag comuni `c:` specifici o mappature puntuali, **non ulteriori tipi di topping**; **verificare nomi e tag nella dipendenza 26.2 e approvare i topping singolarmente**. Non accettare automaticamente tutti i pasti/cibi di altre mod. Vedi [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md).

## Crudo e cotto

| Proprietà | Crudo | Cotto |
| --- | --- | --- |
| Impasto | più chiaro | bordo più dorato |
| Salsa | brillante | un poco più scura |
| Formaggi | pezzi distinti | aspetto parzialmente fuso |
| Condimenti | tono fresco | lieve variazione di tono |
| Bruciatura | assente | **assente, sempre** |

Queste variazioni cromatiche sono idee estetiche, non specifiche asset già approvate.

## Quantità e limiti

**CONFERMATO:** **massimo 6 ingredienti diversi per ciascun quarto fisico**, compresa l'eventuale salsa. Non e' un limite di 6 per pizza intera o per meta'; la griglia sottostante dei quattro quarti governa anche zone riunite e pizza indivisa.

- Se un ingrediente aggiunto porterebbe **almeno un quarto** a 7 tipi diversi, l'**intera applicazione** e' rifiutata senza consumo, anche se gli altri quarti hanno posti liberi.
- Il limite resta valido dopo divisioni e riunioni col mattarello, senza alterare le coperture di topping gia' presenti.
- Esempio di quarto pieno: pomodoro, mozzarella, gorgonzola, speck, funghi, basilico = 6 ingredienti; un altro tipo non puo' essere aggiunto.
- La duplicazione dello stesso ingrediente sulla stessa sottoarea rimane vietata **anche tra varianti cross-mod riconosciute come equivalenti**. Tutti gli item di un gruppo di equivalenza contano come **un solo tipo**; la salsa e' facoltativa e occupa uno dei sei posti, non uno slot extra.
- **Vietate le ripetizioni dello stesso ingrediente nella stessa sottoarea.** Lo stesso ingrediente puo' essere applicato su aree diverse non coperte; non sono previsti strati multipli dello stesso item nello stesso punto.
- Quantità **logica** (porzioni realmente consumate) separata dalla densità **visiva** (quanti elementi disegnare).
- **PROPOSTA GRAFICA:** distribuire pochi frammenti per ingrediente e limitare la densita' massima delle geometrie, cosi' che anche sei ingredienti distinti restino leggibili senza coprire il bordo.
- Non utilizzare un'unica enorme texture di salame/formaggio per rappresentare una porzione.

## Bonus alimentari delle categorie — CONFERMATO (12 ottobre 2026)

La classificazione per il **rendering** non coincide necessariamente con le **categorie alimentari**. Per ciascun quarto fisico cotto, il contributo nutrizionale confermato e' **base/crosta +1, salse +1, latticini (formaggi e panna) +1, proteine animali (anche uova, salumi, carne e pesce) +2, vegetali (anche olive, verdure e funghi) +1, oli e grassi +0, erbe/spezie +0**, **un bonus per ogni categoria presente**, non per ingrediente. **Massimo 6 punti fame per fetta.** Le quattro fette possono avere fame diversa. Il rapporto tra preset visivi e categorie di fame va gestito esplicitamente (es. formaggio grattugiato conta come formaggio, mortadella come salume, funghi come verdure/funghi); le definizioni di altre mod devono dichiarare o consentire di identificare la categoria in modo non ambiguo. **Saturazione per categoria CONFERMATA nel principio:** una sola contribuzione per categoria nel quarto, formaggi e salumi/carni/pesce piu' sazianti, salse e verdure/funghi meno sazianti, erbe/spezie contributo marginale o zero. **Bonus saturazione ORA CONFERMATI:** base/crosta **+1**, salse **+0,5**, latticini/formaggi **+1,5**, proteine animali (anche uova/salumi/carni/pesce) **+2,5**, vegetali (anche olive/verdure/funghi) **+0,5**, oli e grassi **+1**, erbe/spezie **+0**, con **massimo 6 punti saturazione effettiva per fetta**, una sola volta per categoria presente. Questi numeri sono punti di saturazione, non un generico parametro di moltiplicazione vanilla; classificazione nutrizionale distinta dai preset di rendering. **Effetti al consumo — CONFERMATO PER ORA:** i topping influenzano fame, saturazione, gusto e rendering delle fette, **non attribuiscono effetti speciali o status effect** nella Pizza Update 1.0.0. Questa scelta puo' cambiare solo per una futura decisione esplicita. Non cambiare il limite di massimo 6 tipi di topping per quarto.

## Posizioni stabili — PROPOSTA TECNICA

Ogni pizza conserva ingredienti, quantità e un `render_seed`. Il renderer distribuisce in maniera **deterministica** posizioni, rotazioni e leggere variazioni di scala dei topping. Lo stesso item deve sembrare identico:
- durante condimento e cottura;
- dopo raccolta/riposizionamento;
- dopo salvataggio e ricaricamento;
- per giocatori diversi in multiplayer.

Valutare caching di mesh e ricomposizione solo quando cambia la pizza, non a ogni frame. Verificare costi di rendering con molte pizze simultanee in un chunk.

## Blocco nel mondo, forno, inventario, fette

- **Nel mondo**: modello completo con strati 2.5D.
- **Nel Pizza Oven**: stessa pizza visibile dentro la bocca del forno.
- **Item in inventario**: rappresentazione semplificata ma coerente con il gusto e la condizione cruda/cotta.
- **SERVIZIO DIRETTO pizza cotta — CONFERMATO:** il blocco pizza cotta e' posizionabile nel mondo. **Nessun coltello o stato di taglio preliminare**: i suoi quattro quarti sono subito servibili con **clic destro a mano vuota sulla zona desiderata**. Il renderer mostra inizialmente la pizza intera; dopo il prelievo di una porzione, nasconde **soltanto quel quarto**, mantenendo le altre fette e i rispettivi condimenti visibili. Dopo l'ultima fetta il blocco scompare. Nessun drop durante un prelievo riuscito.
- **Pizza Slice — CONFERMATO:** l'item fetta rappresenta esclusivamente i condimenti del proprio quarto fisico, con gusto e nutrizione individuali. **La pizza intera come item (anche cotta) non e' commestibile**, mentre lo sono le fette cotte. Non e' richiesto taglio, coltello, ciotola o Cutting Board.

**Pizza cotta posizionabile e servibile nel mondo — CONFERMATO:** il giocatore puo' appoggiarla su una superficie compatibile e **prelevare subito un singolo quarto con clic destro a mano vuota**, senza taglio preliminare. Questa porzione scompare visivamente, le altre rimangono al loro posto; mano occupata -> nessuna raccolta. **Inventario pieno -> nessun prelievo, nessun drop e nessuna modifica al rendering**. **Rottura prima del primo prelievo -> pizza intera**; dopo almeno un prelievo -> cadono solo le fette ancora visibili/presenti, con rispettivi topping e senza duplicazioni. **Da implementare/testare:** collisioni/shape delle porzioni rimaste e sincronizzazione del rendering. La suddivisione logica di condimento con il mattarello non e' taglio fisico e non modifica la disponibilita' delle quattro porzioni.

## Compatibilità con altre mod

**Elenco dei candidati realmente registrati:** [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md), con ID 26.2, esclusioni, proposte e tag verificati separati dalle ipotesi. **AMBITO 1.0.0:** i tipi di topping ufficiali derivano solo da Italian's Delight e Farmer's Delight Refabricated; tag comuni `c:` e mapping specifici possono accettare item di altre mod **soltanto come varianti equivalenti di quei tipi**. Un tag alimentare ampio non deve introdurre nuovi tipi. Separare **validita' dell'ingrediente** (tag/definizione), **identita' canonica equivalente** (antiduplicazione e conteggio del limite di 6) e **preset grafico** (categoria, geometria, aspetto). Una mozzarella riconosciuta di un'altra mod deve condividere l'identita' logica della mozzarella gia' applicata, senza sostituire l'item reale da rimborsare. I tag troppo generici (es. tutti i formaggi) non determinano equivalenza. Un ingrediente compatibile senza modello dedicato usa un fallback visuale del suo gruppo, senza apparire invisibile. Non accettare automaticamente ogni alimento come topping.

## Texture già presenti da conservare

- `src/main/resources/assets/italiansdelight/textures/item/pizza_margherita.png`
- `src/main/resources/assets/italiansdelight/textures/block/top_mushroom_pizza.png`

**Non spostare, cancellare, sovrascrivere o generare sostituzioni per questi asset senza richiesta esplicita.** Andranno studiati quando realizzeremo il renderer.

## Da definire

Altezze e dimensioni in voxel, forma del bordo, hitbox, modalità di render per ogni categoria, resa dei formaggi fusi, implementazione del rifiuto completo dei clic con una salsa diversa su zone parzialmente occupate (nessuna modifica, nessun consumo), dettagli delle varianti per item in inventario e budget performance.
