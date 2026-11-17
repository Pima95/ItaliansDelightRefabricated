# Viti selvatiche: due blocchi

Riferimento: **Farmer's Delight Refabricated 26.3**, `WildRiceBlock` e `WildRiceFeature`.

- Due sezioni grafiche 16×16 trasparenti, `bottom` e `top`, raccordate verticalmente: fusto, rami, foglie e grappoli.
- Due metà dello stesso blocco `half=lower|upper` (`DoublePlantBlock`), con ID invariati. Entrambe vengono piazzate insieme dal nuovo `WildGrapeFeature` come nel riso selvatico.
- Rottura per raccolta, mai tasto destro; cesoie danno la pianta intera, altri strumenti semi e 20% probabilità di uva. Loot differenziato per metà, secondo lo schema del riso FDR, evitando duplicazioni. Farina d'ossa come riso selvatico: 30% possibilità di una pianta intera.
- Distribuzione dei patch e biomi invariati. Coltivazioni normali, tempi di crescita, altri sprite e crate invariati.
- I vecchi esemplari selvatici a un solo blocco salvati nel mondo possono essere rimossi al prossimo aggiornamento del blocco, se privi della metà superiore.

Verificare nel client il posizionamento, la rottura della parte alta/bassa, le cesoie e nuovi chunk.
