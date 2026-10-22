# Miglioramenti pianificati — Italian's Delight Refabricated

Questo documento raccoglie **miglioramenti e nuove funzionalita' richiesti dall'autore**, separati dai bug riportati in [KNOWN_BUGS.md](KNOWN_BUGS.md).

Le voci sono impegni da pianificare: **non indicano codice gia' implementato**. I valori numerici di bilanciamento e i nuovi biomi non sono ancora stati concordati. Aggiornare gli stati solo dopo una modifica effettiva e, dove previsto, un test in gioco.

## Miglioramenti aperti

### IMP-001 — Ridurre la frequenza del Cardoon selvatico

- **Stato:** implementato su `feature/cardoon-farming-worldgen-sounds`; **da testare in Minecraft** (22 ottobre 2026).
- **Problema:** il Cardoon (cardo selvatico) appare troppo frequentemente nella generazione naturale.
- **Obiettivo:** diminuire la sua frequenza e/o la densita' dei gruppi, mantenendo una presenza riconoscibile ma meno invasiva.
- **Codice e dati da esaminare:** `src/main/resources/data/italiansdelight/worldgen/placed_feature/cardoon_patch.json`, `configured_feature/cardoon.json` e `ModWorldGeneration.java`.
- **Stato attuale:** `cardoon_patch.json` utilizza una probabilita' iniziale 0/1 e una seconda distribuzione da 25–26 tentativi; analizzare l'effetto combinato dei placement modifier prima di scegliere il nuovo bilanciamento.
- **Da definire:** tasso di generazione desiderato e dimensione/densita' dei gruppi. Nessun valore definitivo e' ancora approvato.
- **Verifica:** esplorare soltanto nuovi chunk, confrontare distribuzione e quantita' di Cardoon e controllare che la pianta continui a comparire.

### IMP-002 — Aggiungere una variante coltivabile del Cardoon

- **Stato:** implementato su `feature/cardoon-farming-worldgen-sounds`; **da testare in Minecraft** (22 ottobre 2026).
- **Obiettivo:** affiancare al Cardoon selvatico una **coltura coltivabile**, seguendo dove opportuno il modello gia' funzionante del basilico: pianta selvatica come fonte iniziale, coltura in farmland con crescita e raccolta.
- **Stato attuale:** `CardoonBlock.java` e' una `VegetationBlock` selvatico/ripiantabile, non una `CropBlock` con stadi di crescita; la bone meal produce un ulteriore item Cardoon. Il basilico dispone gia' di `WildBasilBlock` e `BasilCropBlock`.
- **Da definire prima dell'implementazione:** item usato per piantare (eventuale assenza di semi separati), numero di stadi, velocita' di crescita, resa, loot, impiego della bone meal, texture per gli stadi e comportamento della versione selvatica dopo l'introduzione della coltura.
- **Vincoli:** non modificare o sovrascrivere le texture originali senza approvazione; preservare l'ottenimento del Cardoon nei mondi esistenti e le ricette gia' funzionanti.
- **Verifica:** piantagione su farmland, crescita naturale, bone meal, raccolta/ripiantagione, Fortune e persistenza dopo il riavvio.

### IMP-003 — Ampliare i biomi naturali di Cardoon e Basilico selvatico

- **Stato:** implementato su `feature/cardoon-farming-worldgen-sounds`; **da testare in Minecraft** (22 ottobre 2026).
- **Piante interessate:** `italiansdelight:cardoon` e `italiansdelight:wild_basil`. **Non e' richiesta alcuna variazione dello spawn rate del basilico**.
- **Stato attuale:** in `ModWorldGeneration.java` entrambe le piante usano `BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW)`.
- **Obiettivo:** aggiungere biomi adatti a ciascuna pianta oltre ai tre attuali, valutando separatamente compatibilita' climatica, terreno e distribuzione.
- **Da definire:** elenco dei nuovi biomi per il Cardoon ed elenco dei nuovi biomi per il Basilico; eventuale supporto ai biomi delle altre mod. Non considerare un elenco proposto come approvato.
- **Vincoli:** espansione dei biomi del basilico **senza modificare** la sua coltivazione gia' implementata e **senza alterare intenzionalmente la sua frequenza per biome**; applicare il riequilibrio di IMP-001 esclusivamente al Cardoon.
- **Verifica:** generazione in nuovi chunk in ciascun bioma aggiunto; confermare assenza di sostituzioni indesiderate e preservazione dei biomi originari.

### IMP-004 — Rivedere i suoni dell'Hanging Hook

- **Stato:** implementato su `feature/cardoon-farming-worldgen-sounds`; **da testare in Minecraft** (22 ottobre 2026).
- **Elemento interessato:** Hanging Hook (uncino utilizzato per appendere i prodotti).
- **Obiettivo:** modificare gli effetti sonori associati al blocco e alle sue interazioni.
- **Da verificare prima dell'implementazione:** quali suoni vengono attualmente riprodotti durante posizionamento, aggancio/rimozione dei prodotti, interazione e rottura; individuare quelli da sostituire o regolare.
- **Da definire:** suoni desiderati, eventuale riutilizzo dei suoni vanilla o audio personalizzati, volume e tonalita'. Nessun suono specifico e' stato ancora approvato.
- **Vincoli:** intervenire soltanto sull'audio, senza alterare funzionamento dell'Hanging Hook, contenuti appesi, hitbox, orientamento o compatibilita' con i mondi esistenti.
- **Verifica:** ascoltare i suoni nelle diverse interazioni, anche in multiplayer, e assicurarsi che non ci siano riproduzioni duplicate o rumori non previsti.

## Convenzioni

- **Da progettare:** obiettivo confermato ma dettagli o valori ancora da definire.
- **Da implementare:** requisiti definiti, codice non ancora modificato.
- **In lavorazione:** implementazione avviata.
- **Da testare:** codice pubblicato, verifica in Minecraft non ancora confermata.
- **Completato:** implementazione e test in gioco confermati dall'autore.

## Ambito e sviluppo

Queste modifiche **non fanno parte dei lavori in corso sulla Pizza Update 1.0.0**: sono registrate come migliorie future per non interrompere le attivita' attuali. L'eventuale sviluppo dovra' avvenire su un branch dedicato, con test prima della PR verso `main`.

Per i bug effettivi usare [KNOWN_BUGS.md](KNOWN_BUGS.md). Per la coltivazione del basilico gia' presente vedere [ROADMAP_BASIL.md](herbs/ROADMAP_BASIL.md).
