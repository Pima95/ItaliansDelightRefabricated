# Condimenti e rendering 2.5D

## Direzione confermata

La pizza viene condita **cliccando sulla base posizionata nel mondo**. Il risultato deve mostrare ingredienti effettivamente scelti, non solo un'icona generica.

**Rendering 2.5D CONFERMATO:**
- salse **piatte** sul disco;
- formaggi, salumi, verdure, funghi ed erbe **leggermente in rilievo**;
- composizione leggibile sia da cruda sia da cotta.

Non è prevista una texture completa separata per ogni combinazione possibile.

## Divisione facoltativa, coperture e provenienza — CONFERMATO

Una pizza stesa e' inizialmente **intera**: si puo' condire completamente senza dividere nulla. Il mattarello permette di creare **2 meta'**, con orientamento scelto dalla **direzione del giocatore al momento della prima divisione**, e successivamente di suddividere una o entrambe le meta' in quarti, **senza cambiare i modelli/texture della base**. Il topping cliccato viene applicato alla **zona puntata**: intera, meta' o quarto. Le linee di divisione sono un **overlay temporaneo**, visibile solo mentre si tiene il mattarello o un ingrediente valido.

Internamente e' possibile mappare tutte le coperture sui quattro quarti logici, pur memorizzando la **configurazione effettiva** della divisione. Ogni applicazione conserva i quarti originari e quelli ancora coperti. **Rimuovere solo una parte dell'applicazione non restituisce l'item**, che viene recuperato una sola volta alla rimozione completa. Esempio: una mozzarella applicata all'intera pizza rappresenta una sola applicazione, anche se dopo la si divide in quarti; eliminarla da quattro quarti non rimborsa quattro mozzarelle.

**Orientamento confermato:** la linea della prima divisione non segue un asse globale fisso: dipende dalla direzione in cui guarda il giocatore nel momento del clic. Il rendering deve mantenere la suddivisione associata alla pizza mentre il giocatore si muove e dopo il trasporto, senza alterare il posizionamento dei topping. **CONFERMATO:** la linea centrale e' **parallela alla direzione orizzontale dello sguardo**, producendo meta' a **sinistra e destra** del giocatore, non davanti/dietro. Rimane aperto il trattamento tecnico degli angoli diagonali e dei clic sul confine.

**Riunione confermata:** Shift + clic destro col mattarello riunisce prima i due quarti della stessa meta' e infine le due meta' intere. Le coperture e le posizioni visive dei topping gia' applicati **non cambiano** quando l'overlay viene unificato, e non si restituiscono ingredienti. Dopo la riunione i topping aggiunti successivamente coprono la nuova zona logica; il renderer deve conservare le posizioni degli ingredienti preesistenti.

**Nessun topping duplicato nella stessa sottoarea — CONFERMATO:** piu' applicazioni dello stesso ingrediente, **incluse versioni di altre mod esplicitamente equivalenti**, sono ammesse **solo quando coprono aree fisiche differenti**. In una zona parzialmente gia' condita, un clic aggiunge l'ingrediente alle sole sottoaree libere, consumando un item; un clic interamente duplicato non consuma nulla. Dopo una riunione la copertura preesistente resta al suo posto. Le applicazioni originali mantengono identita' e cronologia per rimuoverle e rimborsarle senza duplicazioni.

Per il modello di stato, recupero e pala vedere [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md).

## Geometria già predisposta (modelli dell'impasto)

I cinque modelli ora realizzano una **base piatta, con scanalatura centrale e bordo rialzato**, senza un secondo strato appoggiato sopra come nel primo prototipo. Alla fine della stesura la superficie interna arriva a Y=1/16 e il bordo a Y=2/16, con **scanalatura profonda esattamente 1/16 di blocco (1 pixel)** e fascia esterna larga 2/16. Il condimento va collocato nella zona centrale (da X/Z=3 a 13) e deve **lasciare visibile il bordo**.

Per geometrie e hitbox dettagliate vedere [DOUGH_GEOMETRY.md](DOUGH_GEOMETRY.md).

**Prototipo successivo alla modifica dei modelli:** primo topping rappresentato da `italiansdelight:mozzarella`. Clic destro sulla base stesa: si consuma una mozzarella intera, il BlockState salva `mozzarella=true` e il modello multipart disegna cinque pezzi poco rialzati usando la texture mozzarella già esistente. Il loot table restituisce l'ingrediente alla rottura. **Questo non è ancora il sistema definitivo**: il booleano e il loot provvisorio non rappresentano quarti, identita' delle applicazioni, rimozione parziale, rimborso per applicazione e trasporto sulla pala. Tutto richiedera' dati persistenti generalizzati.

## Salsa facoltativa e unica per porzione — CONFERMATO

La pizza puo' essere **completamente bianca**, avere salsa soltanto su una parte o usare **tipi di salsa differenti su parti fisiche distinte**. **Non si possono sovrapporre due salse sulla stessa sottoarea**, neppure se sono diverse (per esempio pomodoro e pesto). Dividere o riunire la pizza non cambia le coperture delle salse preesistenti. Gli altri topping possono essere aggiunti anche senza salsa. Una salsa puo' essere necessaria per riconoscere una ricetta specifica, ma **non e' richiesta per preparare una pizza libera**.

**CONFERMATO:** se la zona selezionata contiene gia' **anche soltanto su una parte** una salsa *differente*, il clic con la nuova salsa viene **rifiutato interamente**: nessuna applicazione neanche sulle parti libere, nessuna sostituzione, nessun consumo di item. Dopo aver riunito due meta' (una al pomodoro, una bianca), un tentativo di applicare pesto alla pizza intera fallisce. Il giocatore puo' usare il mattarello per dividerla nuovamente e applicare il pesto **solo alla meta' bianca**.

Per la prima salsa è ancora da decidere se usare il pomodoro di Farmer's Delight direttamente o una salsa dedicata.

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

**Rendering a livelli proposto**: (1) impasto, (2) salsa, (3) formaggi, (4) salumi/verdure, (5) erbe e dettagli. L'ordine nel rendering non impone l'ordine di aggiunta degli ingredienti.

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
- **Pizza Slice**: piccola resa che mostri i condimenti principali della pizza originale (se il sistema fette viene confermato).

Le pizze cotte riposizionabili sono una proposta da validare.

## Compatibilità con altre mod

Separare **validita' dell'ingrediente** (tag/definizione), **identita' canonica equivalente** (antiduplicazione e conteggio del limite di 6) e **preset grafico** (categoria, geometria, aspetto). Una mozzarella riconosciuta di un'altra mod deve condividere l'identita' logica della mozzarella gia' applicata, senza sostituire l'item reale da rimborsare. I tag troppo generici (es. tutti i formaggi) non determinano equivalenza. Un ingrediente compatibile senza modello dedicato usa un fallback visuale del suo gruppo, senza apparire invisibile. Non accettare automaticamente ogni alimento come topping.

## Texture già presenti da conservare

- `src/main/resources/assets/italiansdelight/textures/item/pizza_margherita.png`
- `src/main/resources/assets/italiansdelight/textures/block/top_mushroom_pizza.png`

**Non spostare, cancellare, sovrascrivere o generare sostituzioni per questi asset senza richiesta esplicita.** Andranno studiati quando realizzeremo il renderer.

## Da definire

Altezze e dimensioni in voxel, forma del bordo, hitbox, modalità di render per ogni categoria, resa dei formaggi fusi, implementazione del rifiuto completo dei clic con una salsa diversa su zone parzialmente occupate (nessuna modifica, nessun consumo), dettagli delle varianti per item in inventario e budget performance.
