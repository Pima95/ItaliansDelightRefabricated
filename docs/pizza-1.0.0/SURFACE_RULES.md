# Superfici di appoggio dell'impasto — decisioni 1.0.0

> **Confermato dall'autore e aggiornato** il 10 ottobre 2026. La Soul Sand è stata esclusa su richiesta successiva. Non ampliare i casi particolari senza una nuova decisione.

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
| Incudini | Tutte le condizioni di usura (`AnvilBlock`) |

## Confermati VIETATI

| Superficie | Dettagli implementativi |
| --- | --- |
| Sabbia delle anime | `Blocks.SOUL_SAND`: **vietata** (decisione aggiornata) |
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
- Non esistono più le varianti `lowered`: tutte le superfici permesse usano i modelli normali, senza spostamenti verticali.
- **Recupero alla perdita del supporto — FIX PUBBLICATO, DA COLLAUDARE:** la pizza non viene più sostituita subito con aria. Il blocco programma un tick server che ricontrolla il sostegno e, se resta invalido, esegue la distruzione vanilla con loot della BlockEntity. La palla/base vuota restituisce Wheat Dough, la pizza cruda preparata restituisce un solo item completo; la pizza cotta intera restituisce la pizza, quella già servita soltanto gli spicchi rimasti. Nessun topping o recipiente extra. Verificare in Survival.
- I cinque modelli 3D mantengono la geometria del prototipo e usano texture dedicate all'impasto crudo, documentate in [TEXTURES.md](TEXTURES.md).

## Checklist test manuale

- [ ] Slab BOTTOM vietata / TOP consentita / DOUBLE consentita.
- [ ] Stair BOTTOM vietata / TOP consentita (tutti gli orientamenti).
- [ ] Trapdoor TOP chiusa consentita; TOP aperta, BOTTOM chiusa o aperta vietate.
- [ ] Soul Sand vietata (anche dopo cambio del blocco di supporto).
- [ ] Incudini normali, scheggiate e danneggiate consentite.
- [ ] DIRT_PATH, FARMLAND, SNOW, CARPET, CHEST, ENDER_CHEST, ENCHANTING_TABLE, BED, CAULDRON, COMPOSTER vietati.
- [x] Blocchi cubici normali ancora consentiti (test confermato dall'autore).
- [ ] Cambio di stato del supporto: la palla non rimane su supporto vietato.
- [ ] La rottura del supporto non duplica ne' perde il Wheat Dough per l'impasto non ancora completamente steso e restituisce invece **un solo item pizza** completo quando la pizza e' gia' formata.

I test qui sopra sono **ancora da svolgere dall'autore** sul client Minecraft 26.2 con la mod caricata.

## Test mirato: rimozione del supporto (fix pre-release)

Da eseguire in **Survival**, in un mondo di prova, con i drop vanilla abilitati.

- [ ] Rompere il blocco sotto un impasto allo stadio 0–3 o una base stesa non divisa e senza condimenti: **esattamente un Wheat Dough**.
- [ ] Rompere il supporto sotto una pizza cruda divisa o condita: **una raw_pizza** che mantiene composizione, maschere e orientamento, senza ingredienti separati.
- [ ] Rompere il supporto sotto una pizza cotta ancora intera: **una cooked_pizza**, zero ciotole aggiuntive.
- [ ] Dopo aver raccolto uno, due o tre spicchi, rompere il supporto: **solo** i tre, due o uno spicchi non ancora raccolti; nessun duplicato.
- [ ] Ripetere su una botola superiore che passa da chiusa ad aperta e dopo un salvataggio/riavvio. Supporti nuovamente validi prima del tick programmato non devono provocare la rottura.
- [ ] Verificare gli stessi dati dopo aver riposizionato la pizza; confrontare con la normale rottura del blocco pizza.

Il test automatico `.github/scripts/check_pizza_support_drops.py` controlla gli invarianti nel codice, **non** simula eventi di Minecraft.
