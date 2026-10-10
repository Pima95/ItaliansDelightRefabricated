# Superfici di appoggio dell'impasto — decisioni 1.0.0

> **Confermato dall'autore** il 10 ottobre 2026. Non ampliare i casi particolari senza una nuova decisione.

## Criterio di base

La palla di Wheat Dough di Farmer's Delight si può piazzare solo **sopra una superficie approvata e orizzontale**. Il gioco controlla lo **stato attuale del blocco**, non solo il tipo: una scala normale non è equivalente a una scala capovolta.

Regola prudente per i blocchi non elencati: **consentire soltanto blocchi con collisione di cubo pieno**. Una superficie non piena/atipica non è automaticamente approvata. Le eccezioni esplicitamente confermate sono riportate sotto.

## Confermati CONSENTITI

| Superficie | Dettagli implementativi |
| --- | --- |
| Blocco pieno (pietra, terra, assi, ecc.) | Collisione di cubo pieno |
| Slab superiore | `SlabBlock.TYPE=TOP` |
| Slab doppia | `SlabBlock.TYPE=DOUBLE` |
| Scala capovolta | `StairBlock.HALF=TOP`; tutti gli orientamenti |
| Botola superiore chiusa | `TrapDoorBlock.HALF=TOP` e `OPEN=false` |
| Sabbia delle anime | `Blocks.SOUL_SAND`: consentita; la collisione è ribassata di 2 pixel, quindi la palla usa un modello ribassato |
| Incudini | Tutte le condizioni di usura (`AnvilBlock`) |

## Confermati VIETATI

| Superficie | Dettagli implementativi |
| --- | --- |
| Slab inferiore | `SlabBlock.TYPE=BOTTOM` |
| Scale normali | `StairBlock.HALF=BOTTOM` |
| Botola inferiore chiusa | `TrapDoorBlock.HALF=BOTTOM`, `OPEN=false` |
| Botole aperte | Nessuna faccia superiore orizzontale utile, `OPEN=true` |
| Sentiero di terra | `Blocks.DIRT_PATH` |
| Terra arata | `Blocks.FARMLAND` |
| Strati di neve | `Blocks.SNOW`; non confondere con il blocco pieno di neve |
| Tappeti | `CarpetBlock` e varianti |
| Bauli | Normali, trappola ed Ender |
| Tavolo degli incantesimi | `Blocks.ENCHANTING_TABLE` |
| Letti | `BedBlock` e varianti colore |
| Calderoni | `AbstractCauldronBlock`, anche quelli riempiti |
| Compostiere | `Blocks.COMPOSTER` |

## Casi ancora APERTI

Le altre superfici particolari, incluse superfici parziali di mod esterne, non sono state automaticamente approvate. La categoria generica dei **blocchi con collisione cubica piena** continua a essere consentita; tutti i nuovi tipi di supporto speciale richiedono una nuova valutazione.

## Comportamento del prototipo

- Il clic destro su una superficie approvata inserisce il `pizza_dough` nel blocco d'aria soprastante.
- Il blocco impasto usa **lo stesso controllo** in `canSurvive`, quindi non può continuare a rimanere su un supporto che non è più consentito.
- Quando la sabbia delle anime viene sostituita con un supporto normale o viceversa, il modello aggiorna l'altezza senza dover ricollocare la palla.
- Il recupero con la rottura del supporto deve rilasciare l'impasto una volta soltanto; verificare in gioco.
- Il modello 3D è sempre **provvisorio**; nessuna texture dell'autore viene sostituita.

## Checklist test manuale

- [ ] Slab BOTTOM vietata / TOP consentita / DOUBLE consentita.
- [ ] Stair BOTTOM vietata / TOP consentita (tutti gli orientamenti).
- [ ] Trapdoor TOP chiusa consentita; TOP aperta, BOTTOM chiusa o aperta vietate.
- [ ] Soul Sand consentita con modello non sospeso.
- [ ] Incudini normali, scheggiate e danneggiate consentite.
- [ ] DIRT_PATH, FARMLAND, SNOW, CARPET, CHEST, ENDER_CHEST, ENCHANTING_TABLE, BED, CAULDRON, COMPOSTER vietati.
- [x] Blocchi cubici normali ancora consentiti (test confermato dall'autore).
- [ ] Cambio di stato del supporto: la palla non rimane su supporto vietato.
- [ ] La rottura del supporto non duplica né perde il Wheat Dough.

I test qui sopra sono **ancora da svolgere dall'autore** sul client Minecraft 26.2 con la mod caricata.
