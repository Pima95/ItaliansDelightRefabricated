# Orchard 1.1.0 — Crescita senza Serene Seasons e farina d'ossa

## Bilanciamento approvato (intermedio tra Rapido e Bilanciato)

`/gamerule randomTickSpeed 3`, 20 TPS, chunk e chioma attivi,
foglia *naturale* collegata a un tronco, non immersa in acqua, con
almeno una delle sei facce adiacente all'aria.

| Albero | Stadi fino al frutto | Probabilità avanzamento per random tick | Tempo medio per singola foglia |
|---|---:|---:|---:|
| Ulivo | 4 | 1/5 | **22,8 min** |
| Mandorlo | 4 | 2/11 | **25,0 min** |
| Ciliegio da frutto | 5 | 1/5 | **28,4 min** |

Ogni posizione in una sezione di chunk riceve mediamente un random tick
ogni 4096/(3*20)=**68,27 secondi**. Il tempo per foglia è dunque
`stadi * 68,27 / probabilità_avanzamento` in secondi. Sono
**valori medi statistici**, non timer: la variazione è ampia.
Ogni foglia matura indipendentemente dalle altre, perciò i **primi
frutti sull'albero** possono comparire molto prima del tempo medio
di maturazione di una singola foglia.

La raccolta con clic destro continua a generare **1–3 frutti** e
riporta la singola foglia a `stage=0`. La crescita naturale non è
modificata dall'uso della farina d'ossa su un'altra foglia.

## Farina d'ossa: solo foglie naturalmente cresciute

La classe di ciascuna delle tre foglie fruttifere implementa
`net.minecraft.world.level.block.BonemealableBlock` per riutilizzare
il normale intervento della farina d'ossa in Minecraft, come
`CropBlock` e le colture di Farmer's Delight Refabricated.

La farina d'ossa può essere utilizzata **solo se**:

1. Il blocco è del tipo di foglia fruttifera corretta ed è
   *naturale*: `PERSISTENT=false`. Le foglie piazzate a mano
   hanno `PERSISTENT=true`, quindi **non sono fertilizzabili**.
2. Le foglie sono ancora connesse al legno:
   `DISTANCE < 7`; non sono `WATERLOGGED`.
3. **Almeno una delle sei facce** è adiacente a un blocco d'aria.
   Le foglie completamente racchiuse nella chioma non vengono
   fertilizzate e non producono frutti.
4. Il frutto **non è già maturo**.
5. Con Serene Seasons installata, lo stadio corrente è inferiore
   al massimo consentito dalla stagione; in serra si usa lo stesso
   controllo del vetro già esistente, consentendo maturazione
   anche fuori stagione.

Ogni applicazione valida fa avanzare **esattamente di uno stadio**:
foglie → boccioli → fiori → frutto acerbo → maturo
(per il ciliegio ci sono le due fasi della fioritura).
La verifica delle condizioni e l'avanzamento si ripetono sul
**server** al momento dell'applicazione. Non viene creato
automaticamente alcun drop né saltata la raccolta.

Senza Serene Seasons non c'è alcun vincolo stagionale;
si applicano solo i normali requisiti delle foglie e della
crescita.

## Meccaniche non modificate

- **Basilico e cardoon coltivati**: classi `CropBlock`,
  velocità, rese, interazione con Serene Seasons e fertilizzante
  rimangono esattamente come in precedenza.
- **Sapling**: crescita vanilla invariata.
- **Texture**, colore del bioma, foglie autunnali/invernali,
  sistema serre, frutti/drop e ricrescita dopo la raccolta:
  invariati.
- **Selezione del 65% di foglie produttive**: **IMPLEMENTATA**.
  Circa il 65% delle foglie naturali esposte può portare fiori e
  frutti in una finestra di crescita; la selezione cambia nel tempo.
  La percentuale è statistica, non una quota esatta per ogni albero.

## Verifiche da fare in gioco

- [ ] Provare farina d'ossa su foglie naturali esposte:
      una fase per applicazione, nessun raccolto istantaneo.
- [ ] Provare su foglie piazzate a mano, sommerse,
      staccate dal tronco o senza facce ad aria: non applicabile.
- [ ] Provare su frutti maturi: non consumare farina d'ossa.
- [ ] Con Serene Seasons, testare fuori periodo:
      all'aperto crescita non oltre lo stadio consentito;
      **in serra** tutte le fasi fino alla maturazione.
- [ ] Provare senza Serene Seasons, anche su server dedicato.
- [ ] A `randomTickSpeed 3`, misurare più blocchi di foglie;
      i tempi reali sono statistici e dipendono dal tick rate.

## Rotazione del 65% delle foglie produttive — implementazione approvata

**Scopo:** rendere i raccolti equilibrati e l'aspetto naturale, evitando
che tutte le foglie della superficie dell'albero maturino insieme.

### Come vengono scelte le foglie

Un blocco è inizialmente *idoneo* solo se è naturale
(`PERSISTENT=false`), collegato al legno (`DISTANCE<7`),
non è sommerso ed è esposto ad almeno una faccia d'aria. Queste
condizioni continuano a escludere sia le foglie interne
sia quelle posizionate manualmente dal giocatore.

Fra le sole foglie così idonee, una selezione pseudocasuale
deterministica sceglie **circa il 65%** come *produttive*
e lascia temporaneamente il rimanente **35% inattivo**.

- La selezione è stabile per ogni singola foglia durante una
  finestra di **quattro giorni Minecraft** (`96.000 game tick`:
  circa **80 minuti** a 20 TPS), più lunga del tempo medio di
  maturazione di una foglia (23–28 minuti).
- Ogni posizione ha un **offset temporale diverso**, quindi
  i cambiamenti sono distribuiti nel tempo e non avvengono
  contemporaneamente in tutta la chioma.
- Ad ogni finestra ciascuna foglia riceve un nuovo risultato.
  Un albero non conserverà quindi sempre le stesse foglie fruttifere:
  per ogni passaggio di finestra circa il 45,5% delle posizioni
  cambia stato in media.
- La probabilità è **indipendente per ogni foglia**, non viene
  calcolata una lista precisa di blocchi per singolo albero.
  Un albero piccolo può quindi avere, in un dato momento, una
  percentuale superiore o inferiore al 65%.
- L'algoritmo usa la **posizione**, la specie e il **tempo di gioco
  persistente**, non un numero random ad ogni tick.
  La selezione è stabile tra salvataggi e ricariche e coerente
  fra client e server. Nessun blockstate o dato salvato aggiuntivo.

### Effetti sulla crescita e sulla raccolta

Se una foglia viene deselezionata dopo il passaggio di ciclo,
al **successivo random tick** torna alla texture normale
(`stage=0`) senza lasciare oggetti. Non viene spezzato
il blocco e non vengono eliminati i normali colori di bioma.
Un eventuale frutto maturo diventato inattivo **non può essere
raccolto** durante il breve periodo prima del prossimo tick.

La farina d'ossa può far avanzare di un singolo stadio
**solo le foglie naturali esposte che risultano selezionate**
nell'attuale finestra. Le altre foglie non sono bersagli validi
per la fertilizzazione. Non viene mai saltato il controllo
dello stadio massimo di Serene Seasons: all'aperto si
rispettano le finestre stagionali; in serra funziona la
maturazione fuori stagione secondo la regola già approvata.

La resa rimane **1–3 frutti per clic destro** e la foglia
raccolta riparte da zero. Il limite **non introduce
cooldown obbligatori tra raccolte**, quindi una stessa
foglia può maturare nuovamente più volte nella sua
finestra produttiva di quattro giorni. Durante le
finestre successive la selezione varierà.

### Come verificare in Minecraft

- [ ] Controllare un albero con molte foglie naturali esposte:
      alcune restano verdi mentre altre fioriscono e maturano.
- [ ] Fertilizzare una foglia selezionata: uno stadio per
      applicazione. Provare poi una foglia naturale inattiva:
      nessuna fertilizzazione fino alla prossima rotazione.
- [ ] Provare le foglie piazzate dal giocatore e quelle interne:
      continuano a non fruttificare né accettare farina d'ossa.
- [ ] Usare `/time add 96000` **non** come prova della rotazione:
      il gioco può modificare il tempo del giorno senza cambiare
      il contatore `gameTime`. Per verificare la rotazione,
      lasciare passare il tempo di gioco (o usare tick
      accelerati in un ambiente di test).
- [ ] Nel passaggio di ciclo, le foglie perdono o acquisiscono
      produttività gradualmente, non tutte nello stesso istante.
- [ ] Controllare che Serene Seasons blocchi la maturazione
      all'aperto fuori stagione e che la consenta in serra.
- [ ] Verificare che rompere una foglia non lasci cadere frutti
      aggiuntivi e che il clic destro dia ancora 1–3 frutti.

**Automazione:** lo script
`.github/scripts/check_orchard_fruiting_rotation.py`
verifica la distribuzione pseudocasuale, le transizioni e i tre
controlli di eleggibilità (random tick, farina d'ossa e raccolta).
