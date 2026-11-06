# Orchard 1.1.0 — Compatibilità opzionale con Serene Seasons (Minecraft 26.2 / Fabric)

## Dipendenze e attivazione

- **Non obbligatoria**: Italian's Delight continua a funzionare **senza Serene Seasons**.
- Quando Serene Seasons è installata, i tre blocchi foglie
  `italiansdelight:olive_leaves`, `almond_leaves` e
  `fruit_cherry_leaves` leggono il calendario del server.
- Versione API verificata: **Serene Seasons Fabric 26.2 — 26.1.2.0.6**,
  file CurseForge ID **8938283**; dipendenza **solo compilazione**,
  mai inserita nel JAR della nostra mod.
- Il giocatore deve installare separatamente **Serene Seasons + GlitchCore**
  e Fabric API. `fabric.mod.json` include `suggests.sereneseasons`,
  ma NON un `depends`.
- Riferimento sorgenti upstream: `Glitchfiend/SereneSeasons`, branch `26.2`,
  `api/season/SeasonHelper.java` e `ISeasonState.java`.

## Calendario iniziale (non definitivo)

**I periodi seguenti sono impostazioni provvisorie da collaudare e
bilanciare in seguito.** Non cambiano la probabilità per random tick
(ancora 1/3), il numero di frutti per raccolta (1–3), la dimensione
degli alberi o le texture.

| Specie | Inizio boccioli | Fioritura | Frutto acerbo | Raccolta |
|---|---|---|---|---|
| Mandorlo | Fine inverno | Inizio primavera (rosa/bianco) | Metà primavera – metà estate (mallo verde) | Fine estate – metà autunno (mallo secco) |
| Ciliegio da frutto | Inizio primavera | Metà–fine primavera (fiori **bianchi**) | Inizio estate (verde) | Metà–fine estate (rosso) |
| Ulivo | Fine primavera | Inizio estate (fiori bianchi) | Metà–fine estate (verde) | Inizio–fine autunno |

Per ogni specie, Serene Seasons fissa lo **stadio massimo raggiungibile**
in ciascuna delle sue 12 sottostagioni. Non ci sono salti:
foglie → boccioli → fiore → frutto verde → maturo come già implementato.
Un blocco già avanti rispetto alla sottostagione attuale torna a
`stage=0` al successivo random tick, per evitare frutti maturi
sopravvissuti durante la dormienza invernale o un ritorno indietro
del calendario con `/season set`.

**La raccolta è controllata anche lato server**: non basta un blocco
`stage=4/5` residuo; è necessaria una finestra stagionale di raccolta.
Il controllo delle **6 facce con aria**, il decadimento, Silk Touch,
cesoie, la ricrescita e i drop non cambiano.

## Comportamento senza Serene Seasons e biomi tropicali

Il codice comune controlla `FabricLoader.isModLoaded("sereneseasons")`
**prima** di caricare la classe che importa le API opzionali.
Se non è installata, non cambia assolutamente nulla rispetto alla
raccolta dell'Orchard precedente.

Quando Serene Seasons è presente, ma la dimensione non applica
il normale ciclo a 4 stagioni o il bioma utilizza stagioni
**tropicali secca/umida**, resta attivo il sistema precedente di
maturazione libera. Questo evita di bloccare gli ulivi e i ciliegi
tropicali in un inverno inesistente. Il futuro calendario tropicale
può essere aggiunto separatamente.

La colorazione delle foglie del mandorlo/ciliegio continua a usare
la tinta del bioma in **ogni fase**; petali e frutti restano
sul livello grafico non colorato. Serene Seasons può inoltre
modificare la colorazione del fogliame secondo la sua configurazione.

## Checklist per il collaudo

**Modalità A: senza Serene Seasons**
- [ ] Avviare il modpack senza Serene Seasons e GlitchCore:
      nessun `NoClassDefFoundError`/crash, tre raccolte vanilla funzionanti.
- [ ] Ripetere coltivazione di ulivo, mandorlo e ciliegio, con clic destro.
- [ ] Verificare che i PNG e gli overlay per bioma rimangano invariati.

**Modalità B: con Serene Seasons + GlitchCore**
- [ ] Avviare anche su un server dedicato, controllare i log e la
      disponibilità del comando `/season`.
- [ ] Usare `/season get` per conoscere il periodo attuale.
- [ ] Con permessi OP, provare `/season set early_spring`,
      `mid_spring`, `late_spring`, `early_summer`,
      `mid_summer`, `late_summer`, `early_autumn`,
      `mid_autumn`, `late_autumn`, `early_winter`,
      `mid_winter`, `late_winter`.
      I comandi vanno verificati contro la versione 26.2 installata.
- [ ] Controllare che ogni specie si fermi allo stadio
      massimo previsto; `random_tick_speed 200` accelera i test.
- [ ] Verificare che le mandorle fioriscano rosa/bianco all'inizio
      della primavera, i ciliegi **bianchi** in primavera e gli ulivi
      **bianchi** a inizio estate.
- [ ] Provare la raccolta nella finestra stagionale e fuori stagione;
      la seconda non deve far cadere frutti.
- [ ] Cambiare immediatamente dalla raccolta all'inverno e
      verificare il ritorno allo stadio 0 al successivo random tick.
- [ ] Controllare biomi temperati, tropicali e dimensioni senza stagioni.
- [ ] Ripristinare `/gamerule random_tick_speed 3` al termine.

## Implementazione

- `common/orchard/OrchardSeasonPolicy.java`: politica generale, controllo
  della presenza della mod e calendario puro (nessuna API esterna).
- `common/orchard/SereneSeasonsOrchardBridge.java`: unico adattatore
  che importa l'API ufficiale `SeasonHelper`.
- `OliveLeavesBlock.java`, `AlmondLeavesBlock.java`,
  `FruitCherryLeavesBlock.java`: controllo stagionale nel random tick
  e nell'interazione di raccolta lato server.
- `.github/scripts/check_orchard_serene_seasons.py`: verifica
  tutti i 12 periodi, il caricamento opzionale e le tre specie.
- **Il JAR NON contiene Serene Seasons o GlitchCore**: entrambe
  rimangono mod esterne e facoltative.

**Stato:** implementazione e verifiche statiche; collaudo in gioco
ancora necessario. Le finestre e i valori definitivi si stabiliscono
dopo il collaudo, senza toccare l'artwork approvato.
