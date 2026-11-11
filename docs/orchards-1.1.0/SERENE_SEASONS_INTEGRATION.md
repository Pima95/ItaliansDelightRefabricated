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
bilanciare in seguito.** La crescita naturale è stata ribilanciata nella revisione successiva; vedi
`GROWTH_AND_BONEMEAL_BALANCE.md`. La presente integrazione stagionale
non cambia ulteriormente la probabilità per random tick
(ora 1/5 ulivo, 2/11 mandorlo, 1/5 ciliegio), il numero di frutti per raccolta (1–3), la dimensione
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

## Fenologia delle foglie — mandorlo, ciliegio, ulivo (correzione approvata)

Le foglie del mandorlo e del ciliegio **restano visibili anche in inverno**
e **utilizzano esattamente lo stesso modello e la stessa texture dell'autunno**.
I precedenti modelli di rami spogli e i relativi PNG sono stati eliminati.

| Specie | Primavera/estate | Autunno | Inverno |
|---|---|---|---|
| Ulivo | Sempreverde e fruttificazione | Sempreverde | Sempreverde |
| Mandorlo | Foglie verdi e frutti | Colori ocra dalla fine dell'autunno | **Stesse foglie ocra autunnali** |
| Ciliegio | Foglie verdi, fiori bianchi e ciliegie rosse | Foglie giallo-arancio dall'inizio dell'autunno | **Stesse foglie giallo-arancio autunnali** |

`seasonal=0` usa l'aspetto normale verde, `seasonal=1` e
`seasonal=2` **puntano allo stesso modello autunnale**. Le proprietà
di stato sono conservate per compatibilità dei mondi salvati, ma non ci
sono più differenze grafiche tra fase autunnale e invernale.

I blocchi della chioma non vengono distrutti. Il cambiamento visivo
avviene al successivo random tick; i blocchi tornano verdi quando
Serene Seasons raggiunge il periodo favorevole. Non è stata aggiunta
alcuna altra mod stagionale.

## Fertilità delle colture — basilico e cardoon (approvato)

Le colture **non muoiono e non si rompono per la sola stagione
sfavorevole**. Si utilizzano esclusivamente i tag ufficiali di
Serene Seasons 26.2, senza un secondo sistema di random tick:

| Coltura | Stagioni fertili | Fuori stagione |
|---|---|---|
| Basilico coltivato `italiansdelight:basil` | Primavera ed estate | Autunno e inverno: crescita più lenta |
| Cardoon coltivato `italiansdelight:cultivated_cardoon` | Primavera, estate e autunno | Inverno: crescita più lenta |

Serene Seasons gestisce **quattro stagioni**, non singole
sottostagioni, nei suoi tag nativi di fertilità. Di conseguenza
non si possono rappresentare con questi soli tag l'inizio
della crescita alla *tarda* primavera, la raccolta a *inizio*
autunno o a *inizio* inverno. I frutti/crop maturi possono
comunque essere raccolti anche fuori stagione.

Il comportamento predefinito `general.out_of_season_crop_behavior=0`
è crescita a **1/6 della velocità normale** quando il raccolto
non è fertile. Se il giocatore cambia la configurazione globale in
`1`, la mod può fermare completamente la crescita; se la imposta
a `2`, distrugge normalmente i raccolti non protetti.
Per **basilico e cardoon coltivati** il tag
`sereneseasons:unbreakable_infertile_crops` impedisce la
distruzione in modalità `2`; la modalità `0` resta
necessaria per garantire il solo rallentamento.

I tag per **blocchi** regolano la fertilità e quelli per **item**
permettono a Serene Seasons di indicare le stagioni fertili
nei tooltip. Il sistema conserva serre (vetro entro 16 blocchi
sopra), coltivazione sotterranea e bone meal di Serene Seasons,
non modifica le età 0–7, gli item, i drop o i modelli.

**Piante selvatiche:** `italiansdelight:wild_basil` e
`italiansdelight:cardoon` non hanno età di crescita;
la loro diffusione tramite bone meal rimane invariata. Non
vengono inserite nei tag di fertilità delle colture.

Senza Serene Seasons non si applica alcun rallentamento
e i tag di compatibilità sono innocui.

## Serre per gli alberi e crescita dei sapling (scelte approvate)

**Serra (anche per olive, mandorle, ciliegie):** si usa esattamente
la regola presente in Serene Seasons 26.2 in
`SeasonalCropGrowthHandler.isGlassAboveBlock`: un blocco appartenente
al tag `sereneseasons:greenhouse_glass` in una delle **16 posizioni
verticali soprastanti** il blocco da coltivare. Il tag upstream comprende
`#c:glass_blocks` e `#forge:glass`: non creiamo un tag proprietario.

Per le foglie degli alberi, questa regola agisce sullo stesso
`OrchardSeasonPolicy.maxStage` già usato sia per avanzare gli stadi
sia per la raccolta con clic destro:
- **All'aperto:** mantiene i limiti stagionali originari; non si
  producono ciliegie, mandorle o olive mature fuori stagione.
- **In serra:** abilita gli stadi fino a maturazione per le tre specie
  anche fuori stagione, come eccezione deliberata.
- **Aspetto della serra:** la chioma sotto vetro mantiene il modello
  normale `seasonal=0`, così fiori/frutti non risultano nascosti
  dalla texture autunnale/invernale. All'aperto rimangono immutate
  le texture e le regole approvate.

**Germogli (`SaplingBlock` vanilla, nessuna logica custom):**

| Albero | Tag Serene Seasons | Riferimento vanilla |
|---|---|---|
| `olive_sapling` | `year_round_crops` | `oak_sapling`: cresce normalmente tutto l'anno |
| `almond_sapling` | `spring_crops`, `autumn_crops` | `birch_sapling`: primavera/autunno |
| `fruit_cherry_sapling` | `spring_crops` | `cherry_sapling`: primavera |

**Le stagioni più favorevoli non aumentano artificialmente la
velocità vanilla:** consentono crescita normale, mentre fuori stagione
Serene Seasons riduce la velocità (configurazione predefinita:
1 successo ogni 6 tentativi) o agisce secondo la configurazione
del modpack. Il bonus *relativo* è quindi la fertilità stagionale,
senza nuovi timer. Il vetro della serra elimina il rallentamento anche
per i sapling grazie alla meccanica nativa di Serene Seasons.

I tre sapling sono aggiunti a
`sereneseasons:unbreakable_infertile_crops`: nessun germoglio
viene distrutto quando il giocatore cambia la modalità fuori stagione.
I tag **block e item** rimangono sincronizzati; l'ulivo è inserito
nei tag `year_round_crops` aggiuntivi, senza sovrascrivere quelli
di Serene Seasons.

**Confronto Farmer's Delight Refabricated 26.2:** la mod upstream
dichiara le colture fertili tramite gli stessi tag Serene Seasons
(`tomatoes`, `rice`, `onions`, `cabbages` e relativa forma item);
le erbe selvatiche usano `WildCropBlock` e la normale propagazione
con bonemeal, senza un calendario aggiuntivo. Seguendo quella struttura,
**basilico coltivato e cardoon coltivato restano tag-based** e le
piante selvatiche non ricevono un nuovo controllo stagionale.

Nessuna nuova logica di sottostagioni è stata introdotta per le
colture o i sapling: sono impiegate esclusivamente le **quattro
stagioni native** di Serene Seasons. Il calendario arboreo delle
fasi di fioritura già presente nel progetto è lasciato invariato.

### Verifiche gameplay ancora necessarie

- [ ] Vetro del tag `greenhouse_glass` entro 16 blocchi sopra la
  chioma: ulivo, mandorlo e ciliegio fruttificano fuori stagione,
  con foglie verdi e raccolta con clic destro funzionante.
- [ ] Allontanare/togliere il vetro: fuori dalla finestra di
  raccolta lo stadio maturo non è più raccoglibile e ritorna a 0
  al successivo random tick; nessun item viene generato da solo.
- [ ] Vetro posto a 17 blocchi, oppure vetro non appartenente
  al tag: nessun effetto serra.
- [ ] Sapling: nel periodo fertile crescono normalmente;
  fuori stagione rallentano in base alla configurazione;
  in serra tornano alla velocità normale.
- [ ] Con `out_of_season_crop_behavior=2`, basilico,
  cardoon e i tre sapling non si rompono.
- [ ] Senza Serene Seasons e GlitchCore il mod avvia ancora
  normalmente e le piante crescono come prima.
- [ ] Verificare i test su mondo singleplayer e server dedicato.

## Punti ancora aperti

L'unica eccezione deliberata al comportamento standard delle
colture riguarda il calendario dei **frutti degli alberi**:
fuori periodo sono bloccati all'aperto ma fruttificano sotto
serra, come richiesto. La fertilità nel sottosuolo delle chiome
non è stata equiparata al comportamento delle colture su farmland:
non aggiungiamo logiche non richieste. La propagazione delle piante
selvatiche continua a seguire Farmer's Delight Refabricated.

Vedi `SERENE_SEASONS_COMPATIBILITY_AUDIT.md` per i dettagli.
