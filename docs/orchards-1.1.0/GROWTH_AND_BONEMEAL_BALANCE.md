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
- **Selezione del 70% di foglie produttive**: **NON IMPLEMENTATA**.
  Per ora ogni foglia naturale valida può fruttificare;
  l'eventuale selezione variabile per ciclo di raccolta
  sarà progettata e approvata separatamente.

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
