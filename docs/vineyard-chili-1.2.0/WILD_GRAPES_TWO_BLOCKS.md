# Viti selvatiche: due blocchi

Riferimento: **Farmer's Delight Refabricated 26.3**, `WildRiceBlock` e `WildRiceFeature`.

- Due sezioni grafiche 16×16 trasparenti, `bottom` e `top`, raccordate verticalmente: fusto, rami, foglie e grappoli.
- Due metà dello stesso blocco `half=lower|upper` (`DoublePlantBlock`), con ID invariati. Entrambe vengono piazzate insieme dal nuovo `WildGrapeFeature` come nel riso selvatico.
- Rottura per raccolta, mai tasto destro; cesoie danno la pianta intera, altri strumenti semi e 20% probabilità di uva. Loot differenziato per metà, secondo lo schema del riso FDR, evitando duplicazioni. Farina d'ossa come riso selvatico: 30% possibilità di una pianta intera.
- La generazione naturale è stata rivista: gruppi più piccoli in biomi temperati, terreno libero e controllo dei due blocchi disponibili. Vedi `WILD_GRAPES_SPAWN.md`. Coltivazioni normali, tempi di crescita e crate invariati.
- I vecchi esemplari selvatici a un solo blocco salvati nel mondo possono essere rimossi al prossimo aggiornamento del blocco, se privi della metà superiore.

Verificare nel client il posizionamento, la rottura della parte alta/bassa, le cesoie e nuovi chunk.

## Correzione dei drop (18 novembre 2026)

- Vite selvatica rossa e bianca: rottura normale = 1–4 semi e 0–2 grappoli, due quantità casuali indipendenti. Il bonus Fortune è disattivato qui affinché non superi i limiti. L'esplosione può ridurre gli oggetti.
- Con le cesoie compatibili FDR rimane il recupero della pianta selvatica intera, senza doppi drop di frutti o semi.
- Vite coltivata: il fusto, a qualunque età, restituisce esattamente un seme della sua varietà; nessun grappolo automatico a `age=7`.
- I grappoli maturi provengono dalle foglie laterali `grapes=true`: drop tramite loot table quando si rompono direttamente, o drop prima che scompaiano quando perdono il supporto del fusto. Le foglie con frutti immaturi non rilasciano grappoli.
- Nessuna modifica a sprite, varietà, crescita, corde e configurazione dello spawn.
