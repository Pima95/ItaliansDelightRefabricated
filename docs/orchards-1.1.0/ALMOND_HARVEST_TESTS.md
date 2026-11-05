# Orchard Update 1.1.0 — Raccolta mandorle (fase 2/3)

**Branch:** `feature/orchards-1.1.0` · **Minecraft:** 26.2 · **Java:** 25

## Progressione delle mandorle

Le foglie del mandorlo conservano l'ID originale `italiansdelight:almond_leaves`.
I blocchi generati da `almond_sapling` possono avanzare nelle cinque fasi
persistenti `stage=0..4`, senza sostituire i tronchi o cambiare la generazione
della chioma.

| Stage | Texture | Aspetto |
|------:|---------|---------|
| 0 | `almond_leaves.png` | Foglie normali: asset già approvato, **invariato** |
| 1 | `almond_leaves_buds.png` | Boccioli rosati e crema, ancora radi |
| 2 | `almond_leaves_flowers.png` | Fioritura con **petali rosa** e lumeggiature **bianco-rosate**, centri gialli |
| 3 | `almond_leaves_unripe.png` | Mandorle immature con **mallo verde**, frutti allungati |
| 4 | `almond_leaves_fruiting.png` | Mallo **secco e aperto** nei toni ocra, mandorle e guscio marroni, raccoglibili |

Le quattro nuove texture sono originali, **16×16**, con soli pixel alpha **0/255**:
tutte conservano gli stessi **197 pixel opachi e 59 pixel trasparenti**
della texture `almond_leaves.png`, esattamente alle stesse coordinate.
Non sono stati modificati foglie base, sapling, tronchi, asset delle olive,
ciliegie o crate. L'attenuazione del colore del bioma specifica del mandorlo
(30%) resta applicata dal client anche alle foglie fiorite/fruttifere.

## Stessa meccanica delle olive

Una foglia avanza di fase se e solo se:
- `persistent=false`: proviene dalla crescita naturale del germoglio;
- `distance<7`: ha supporto dal tronco, non è destinata a decadere;
- `waterlogged=false`: non è immersa in acqua;
- **almeno una delle sei facce** (su, giù, nord, sud, est, ovest) tocca
  un blocco d'aria. Le diagonali non sono considerate.

La progressione avviene un passo per volta con i random tick (parametro
provvisorio identico all'ulivo: probabilità 1/3 di avanzare per random tick).
Se la foglia viene circondata completamente, fiori e frutti scompaiono
immediatamente e `stage` torna a 0; un tick successivo corregge anche
eventuali blocchi diventati non idonei dopo il caricamento.

Clic destro su `stage=4` → raccolta server-side di **1–3**
`italiansdelight:almonds`, foglia intatta, ritorno a `stage=0`
e successiva ricrescita. Lo stato viene aggiornato **prima** del drop
per impedire doppie raccolte. Il loot dalla rottura della foglia rimane
invariato (cesoie, Silk Touch, bastoni e germoglio).

**Bilanciamento dei tempi/rendimenti:** provvisorio. I valori definitivi
per tutti gli alberi verranno decisi **dopo** aver completato le texture
di tutte e tre le specie, secondo le indicazioni dell'autore.

La raccolta delle **ciliegie** resta da implementare. Compatibilità opzionale
con **Serene Seasons** soltanto dopo il completamento delle tre raccolte.

## Checklist del collaudo in Minecraft

- [ ] Piantare un germoglio di mandorlo e portarlo a maturazione con farina d'ossa.
- [ ] Osservare l'ordine: foglie → boccioli → fiori rosa/bianco → mallo verde
  → mallo secco e mandorle marroni.
- [ ] In gioco distinguere chiaramente i fiori rosa dalle foglie verdi.
- [ ] Verificare che il fiore e il frutto siano visibili anche nella chioma,
  in ogni bioma, con la lieve colorazione stagionale vanilla attualmente attiva.
- [ ] Ogni blocco di foglie completamente circondato: **nessuna fase di fioritura**.
- [ ] Controllare che **una sola faccia** esposta all'aria basti: tutte e sei
  le direzioni, non le diagonali.
- [ ] Occludere l'ultima faccia esposta di una foglia già matura: `stage=0`
  senza caduta di mandorle.
- [ ] Clic destro a mano in Sopravvivenza su mandorle mature: **1–3** frutti,
  foglia non distrutta, suono e ritorno alle foglie semplici.
- [ ] Doppio clic/2 giocatori: nessuna duplicazione dei frutti.
- [ ] Verificare la ricrescita dal primo stadio dopo la raccolta.
- [ ] Verificare che le foglie piazzate a mano non fruttifichino.
- [ ] Togliere il tronco e attendere il decadimento vanilla delle foglie naturali.
- [ ] Cesoie/Silk Touch: la foglia cade regolarmente e non duplica mandorle.
- [ ] Verificare salvataggio e riapertura mondo, client/server dedicato.
- [ ] Usare `/gamerule random_tick_speed 200` durante il collaudo e
  ripristinare `3` alla fine; ricordare che accelera anche altre piante.

**I test automatici e la compilazione non sostituiscono il test in Minecraft.**
