# Miglioramenti — Italian's Delight Refabricated

**Aggiornamento 22 ottobre 2026:** le quattro migliorie IMP-001–IMP-004 richieste dall'autore sono **implementate e confermate funzionanti in gioco**. Sono state integrate con fast-forward dal branch `feature/cardoon-farming-worldgen-sounds` in `feature/pizza-1.0.0` (commit integrato `24b450b`), senza creare nuovi branch e senza modificare `main`.

| ID | Miglioramento consegnato | Stato |
|---|---|---|
| **IMP-001** | Cardoon selvatico meno frequente: `rarity_filter 1/120` con 64 tentativi per patch, come carote/cipolle selvatiche di Farmer's Delight Refabricated 26.2. Stessa revisione per il basilico selvatico, richiesta successivamente. | **Completato e confermato** |
| **IMP-002** | `cultivated_cardoon` coltivabile come il basilico, 8 età, 4 stadi visivi con fiore adulto unico, item nuovo con PNG 16×16, loot/ripiantagione e tag del caglio compatibile col vecchio `cardoon`. | **Completato e confermato** |
| **IMP-003** | Ampliati i biomi naturali del Cardoon (anche Savanna, Savanna Plateau, Windswept Savanna) e Basilico (Forest, Flower Forest, Birch Forest, Cherry Grove, Sparse Jungle); l'autore ha approvato l'elenco. | **Completato e confermato** |
| **IMP-004** | Hanging Hook: `SoundType.LANTERN` e `SoundEvents.LANTERN_PLACE/LANTERN_HIT` per i prodotti appesi. | **Completato e confermato** |

La conferma generica dell'autore riguarda il funzionamento delle modifiche. Un **benchmark numerico dei tassi di generazione nei nuovi chunk**, la verifica su server dedicato e le prove combinatorie estreme non risultano registrati come eseguiti: sono test facoltativi o di release separati, non nuove funzionalità mancanti.

Per i dettagli tecnici e il collaudo: [CARDOON_FARMING_WORLDGEN.md](CARDOON_FARMING_WORLDGEN.md).

## Convenzioni

- **Completato:** implementato, compilato, testato dall'autore.
- **Da verificare:** implementato ma privo di conferma in-game.
- **Da progettare:** serve ancora la decisione sui requisiti.

Per i problemi ancora aperti vedere [KNOWN_BUGS.md](KNOWN_BUGS.md). Per la preparazione della 1.0.0 vedere [RELEASE_READINESS.md](pizza-1.0.0/RELEASE_READINESS.md).
