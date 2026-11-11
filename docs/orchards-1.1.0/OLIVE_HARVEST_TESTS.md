# Orchard 1.1.0 — Raccolta olive, fioritura e vincolo aria

**Branch:** `feature/orchards-1.1.0` · **Minecraft:** 26.2 · **Java:** 25

## Ciclo visivo in cinque fasi

| `stage` | Aspetto | Raccolta |
|---|---|---|
| 0 | Foglie argentate originali, senza frutti | No |
| 1 | Boccioli piccoli color crema, distribuiti sulla chioma | No |
| 2 | Numerosi **fiori bianchi/avorio**, con centri giallo pallido | No |
| 3 | Piccole **olive acerbe** verdi | No |
| 4 | **Olive mature**, più dense e più contrastate con pixel verde oliva | **Clic destro: 1–3 olive** |

Il blocco mantiene l'ID `italiansdelight:olive_leaves`. Le cinque fasi sono
salvate nella blockstate `stage=0..4`, sincronizzata con il client.

La texture originale `olive_leaves.png` non è stata toccata. Le nuove
texture `olive_leaves_buds.png`, `olive_leaves_flowers.png`,
`olive_leaves_unripe.png` e la rielaborata `olive_leaves_fruiting.png`
conservano **esattamente la stessa sagoma 16×16 e gli 88 pixel trasparenti**:
si colorano solo pixel già opachi. Alpha sempre 0 o 255.
Le olive mature ora occupano **62 pixel color verde oliva** (prima 16),
mentre i fiori bianchi occupano gran parte del centro della chioma.

## Regola fondamentale: almeno una faccia esposta all'aria

Per sviluppare boccioli, fiori e olive, la foglia deve rispettare **tutte**
le condizioni:

1. Essere **naturale**: `persistent=false` (prodotta dalla crescita del
   germoglio, non piazzata in Creativa).
2. Essere collegata a un tronco: `distance<7`.
3. Non essere immersa in acqua: `waterlogged=false`.
4. Avere **almeno uno dei sei blocchi adiacenti** di tipo aria:
   **su, giù, nord, sud, est, ovest**. Contano le sei facce ortogonali,
   non le diagonali.

Il controllo aria viene applicato **alla crescita**, **alla raccolta** e
quando cambiano i blocchi vicini: se una foglia fiorita/fruttifera
viene completamente circondata, torna a `stage=0` e non produce olive.
Anche un tick casuale successivo corregge eventuali stati non più esposti.

Ogni fase cresce con probabilità **1 su 3 per random tick** della foglia,
senza salto degli stadi. I valori sono preliminari, da bilanciare dopo il
test in gioco. A `stage=4`, un clic destro a mano raccoglie **1–3
`italiansdelight:olives`** e riporta a `stage=0` senza distruggere
la foglia. Il server gestisce il drop dopo aver azzerato lo stato, per
impedire doppie raccolte simultanee.

Il normale decadimento vanilla, le foglie raccolte con cesoie o Silk Touch,
le probabilità di germogli/bastoni e la crescita degli alberi singoli 1×1
o secolari 2×2 restano inalterati. Non è stata attivata la generazione
naturale degli ulivi nei biomi. Nessuna oliva viene aggiunta al loot da
rottura della foglia: la raccolta avviene esclusivamente col clic destro.

**Compatibilità note:** il precedente stato sperimentale `fruiting=true/false`
è sostituito da `stage=0..4`. I blocchi provenienti dal vecchio build
possono ripartire da `stage=0` al caricamento; l'ID del blocco resta
invariato. Serene Seasons arriverà **solo dopo** il completamento delle
raccolte di olive, mandorle e ciliegie.

## Checklist test in Minecraft

- [ ] Far crescere un ulivo 1×1 e, in seguito, un secolare 2×2.
- [ ] Controllare le foglie di superficie: boccioli → fiori **bianchi** →
      olive acerbe → olive mature verde oliva.
- [ ] Controllare i blocchi di foglie completamente interni alla chioma,
      senza aria adiacente: **mai fiori o olive**.
- [ ] Verificare per tutte e sei le direzioni che **una sola** faccia
      a contatto con aria sia sufficiente per la crescita.
- [ ] Su una foglia matura, sostituire l'unico blocco d'aria vicino con
      un blocco pieno: lo stadio deve sparire, senza drop.
- [ ] Provare clic destro a mano su olive mature: 1–3 olive e ritorno
      allo stadio 0, senza distruzione delle foglie.
- [ ] Provare due clic rapidi e due giocatori in multiplayer: nessun doppio drop.
- [ ] Ripetere la prova con farina d'ossa usata soltanto per crescere
      il **germoglio**: non serve nuova meccanica di bonemeal sulle foglie.
- [ ] Verificare che le foglie piazzate manualmente non fioriscano.
- [ ] Verificare decadimento con il tronco rimosso, cesoie/Silk Touch,
      germogli, acqua e ricarica mondo/chunk.
- [ ] Confrontare i colori delle fasi con l'item oliva: il verde oliva deve
      essere distinto dalla palette salvia-argento delle foglie.

**CI non sostituisce il collaudo nel mondo di gioco.** Il bilanciamento
della maturazione e la resa visiva sono ancora da approvare.
