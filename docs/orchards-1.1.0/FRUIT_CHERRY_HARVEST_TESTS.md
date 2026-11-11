# Orchard 1.1.0 — Ciliegio da frutto: fioritura bianca e raccolta

**Branch:** `feature/orchards-1.1.0` · **Minecraft:** 26.2 / Fabric · **Java:** 25

## Tre fasi di fioritura, poi fruttificazione

Il **ciliegio da frutto** usa fiori **bianchi/avorio** con piccoli centri
giallo tenue. **Non usa i fiori rosa del ciliegio ornamentale giapponese.**
Le tre fasi di fioritura sono distinte dalla successiva fruttificazione.

| Stato | Nome della texture | Aspetto |
|---|---|---|
| 0 | `fruit_cherry_leaves.png` | Foglie normali, già approvate e **invariate** |
| 1 | `fruit_cherry_leaves_buds.png` | Boccioli crema e avorio distribuiti sui rami |
| 2 | `fruit_cherry_leaves_flowers_early.png` | Primi piccoli **fiori bianchi** con centro giallo |
| 3 | `fruit_cherry_leaves_flowers.png` | Fioritura piena: **molte corolle bianche** |
| 4 | `fruit_cherry_leaves_unripe.png` | Piccole **ciliegie verdi** con piccioli |
| 5 | `fruit_cherry_leaves_fruiting.png` | Piccole **ciliegie rosse**, sfalsate, raccoglibili |

Le nuove texture sono originali, 16×16, a pixel netti. Per tutte e cinque
le nuove PNG sono preservati esattamente i **157 pixel opachi e 99 pixel
trasparenti** della texture originale, alle stesse coordinate. Non è stato
modificato alcun pixel trasparente né introdotta alpha intermedia:
**solo 0 e 255**.

Le ciliegie mature sono rappresentate da piccoli gruppi da 1–2 pixel con
picciolo e riflessi, non come grandi pomodori. Non sono state modificate
la texture dell'item `cherries`, la texture delle foglie normali,
i blocchi del legno, la generazione dell'albero, le altre specie o i crate.

### Colorazione dei biomi

La regolazione del bioma (50%) ora si applica al **fogliame di tutte
le fasi 0–5**, anche mentre l'albero è in fiore o ha i frutti.
L'aspetto bianco/avorio dei petali e il colore verde/rosso delle
ciliegie sono protetti tramite un **overlay non colorato** collocato
su un secondo livello del modello. Si evitano così foglie di tonalità
diversa fra gli stadi senza trasformare fiori bianchi in verdi.

## Logica identica alle olive e alle mandorle

Si conserva l'ID del blocco `italiansdelight:fruit_cherry_leaves`, ora
con stato persistente `stage=0..5`. Le foglie da germoglio coltivato
possono fiorire/fruttificare solo se:

- `persistent=false`: foglie generate dall'albero, non posate a mano;
- `distance<7`: sono collegate a un tronco, non stanno decadendo;
- `waterlogged=false`: non sono immerse in acqua;
- **almeno una faccia su sei** è adiacente a un blocco d'aria (alto,
  basso, nord, sud, est, ovest). Le diagonali non valgono.

Una foglia completamente circondata perde immediatamente fiori/frutti
e ritorna allo stadio 0 quando cambiano i blocchi confinanti.
La stessa condizione viene controllata durante la crescita e la raccolta.

Probabilità aggiornata di **1/5 di avanzare di una fase per random tick**;
la farina d'ossa avanza di uno stadio solo sulle foglie naturali esposte
(vedi `GROWTH_AND_BONEMEAL_BALANCE.md`)
ricevuto dalla foglia, senza saltare fasi. Al raggiungimento dello
stadio 5, **clic destro** → **1–3 `italiansdelight:cherries`**,
la foglia resta sul posto e torna a `stage=0` per ricrescere.
Il drop e l'azzeramento sono gestiti lato server, per evitare doppia
raccolta della stessa foglia. Cesoie, Silk Touch, bastoni e possibilità
di germogli, decadimento e log-distance restano invariati. Non si
aggiungono ciliegie ai drop da rottura delle foglie.

### Bilanciamento e Serene Seasons

I **tempi definitivi di tutti e tre gli alberi** saranno impostati
solo dopo l'approvazione delle texture, come concordato.
Nessuna dipendenza o meccanica di **Serene Seasons** è stata aggiunta
in questa modifica: l'integrazione opzionale verrà affrontata
separatamente dopo la verifica delle tre raccolte.

## Checklist di collaudo — da eseguire in seguito

- [ ] Crescita del germoglio del ciliegio da frutto in Sopravvivenza;
  controllare tutte le sei fasi e la **fioritura bianca**, senza rosa.
- [ ] Verificare che ci siano **più fiori** nella fase 3 che nella fase 2.
- [ ] Distinguere i frutti verdi (fase 4) dalle ciliegie rosse (fase 5),
  senza un aspetto troppo simile ai pomodori.
- [ ] Verificare ogni singola faccia a contatto con aria e le foglie
  interne completamente circondate (che non devono fiorire).
- [ ] Chiudere l'ultima faccia d'aria di una foglia già matura:
  tornare allo stadio 0 senza drop.
- [ ] Clic destro su ciliegie mature: ottenere 1–3 item e mantenere
  il blocco; ripetere subito non deve duplicare il raccolto.
- [ ] Verificare ricrescita, salvataggio mondo, tick casuali,
  client e server dedicato.
- [ ] Foglie piazzate manualmente mai produttive; decadimento vanilla,
  cesoie e Silk Touch ancora regolari.
- [ ] Prova veloce con `/gamerule random_tick_speed 200`; ripristinare
  `/gamerule random_tick_speed 3` al termine.

**Nota:** la riuscita della build GitHub non equivale al collaudo in gioco.

## Ritocco delle ciliegie mature — 6 novembre 2026

Su richiesta dell'autore è stata modificata **soltanto**
`textures/block/fruit_cherry_leaves_fruiting.png` (stadio 5):

- I pixel decorativi modificati rispetto alle foglie originali passano da
  **57 a 18**: **13 pixel rossi** in **5 piccoli gruppi irregolari**
  e **5 pixel verdi di picciolo**.
- I frutti non sono specchiati e non hanno una disposizione a griglia:
  sono distribuiti ad altezze diverse, con sagome di 2–3 pixel.
- Tutti i colori dei frutti sono stati riportati verso il **rosso ciliegia,
  cremisi e bordeaux**, con riflessi rubino anziché arancione.
- Rimangono inalterati 16×16, i 157 pixel opachi e i 99 trasparenti nella
  maschera originale, alpha esclusivamente 0/255, la texture delle foglie
  normali e le altre quattro fasi di crescita.
- Le meccaniche di maturazione, esposizione all'aria, resa dei frutti e
  raccolta non sono state modificate.

Il controllo `check_fruit_cherry_harvest.py` ora verifica densità ridotta,
colore cremisi privo di arancione, cinque gruppi asimmetrici e maschera
alpha originale. La resa in Minecraft richiede ancora prova in gioco.

## Densità ciliegie mature — secondo ritocco

Su richiesta dell'autore, alla variante `fruit_cherry_leaves_fruiting.png`
sono stati aggiunti **quattro ulteriori piccoli gruppi di ciliegie**, conservando
**esattamente** i cinque precedenti. Il totale passa da cinque a **nove**
gruppi, disposti senza simmetrie: in alto al centro, verso il lato sinistro,
nella porzione inferiore centrale e sul lato destro in basso.

- **23 pixel rossi** (erano 13) e **9 pixel di picciolo verde** (erano 5):
  32 pixel decorativi complessivi sui 157 opachi originali.
- Aggiunti esattamente **14 pixel** al PNG precedente: 10 rossi e 4 verdi;
  nessuno dei pixel già approvati è stato alterato.
- Tutti i gruppi restano piccoli, da **2 a 3 pixel rossi**, con palette
  **rosso ciliegia/cremisi/bordeaux**, senza arancione.
- **Invariati**: maschera 16×16 con alpha esclusivamente 0/255, tutte le
  altre fasi, grafica delle foglie base, meccanica di raccolta e tempi.

Il test per lo stadio 5 richiede esattamente **9 gruppi asimmetrici**,
23 pixel rossi e 9 piccioli, impedendo regressioni alla vecchia disposizione.

## Allineamento completo delle sei fasi — 6 novembre 2026

**Problema risolto:** fiori e frutti acerbi comparivano in punti diversi
dalle **nove ciliegie rosse** finali, dando l'impressione di frutti che
si spostassero durante la maturazione.

La texture dell'ultima fase (`fruit_cherry_leaves_fruiting.png`) diventa
l'esatto **riferimento spaziale** ed è rimasta invariata, approvata
con nove piccoli gruppetti rosso cremisi asimmetrici.

- **Stadio 0 — foglie:** texture originale, invariata.
- **Stadio 1 — boccioli:** nove piccoli boccioli sui centri degli stessi
  nove gruppi che produrranno ciliegie, con i relativi piccioli.
- **Stadio 2 — primi fiori:** cinque dei nove boccioli si aprono in
  fiori bianchi/avorio a centro giallo; i quattro rimanenti restano
  in boccio, **nelle stesse posizioni**.
- **Stadio 3 — piena fioritura:** i nove gruppi si aprono; i petali
  bianchi si estendono solo attorno ai rispettivi centri, senza
  creare fiori isolati in posizioni che non produrranno frutti.
- **Stadio 4 — ciliegie verdi:** i 23 pixel di frutto sono collocati
  **alle stesse esatte coordinate** delle 23 celle rosse mature, con
  i **medesimi nove pixel di picciolo**. Cambia soltanto la palette.
- **Stadio 5 — ciliegie rosse:** asset precedentemente approvato,
  **invariato byte per byte**.

I quattro PNG intermedi sono stati rigenerati conservando sempre
la stessa maschera delle foglie (16×16, 157 pixel opachi e 99
trasparenti; alpha solo 0 o 255). Le tre fasi di fioritura conservano
colori naturali **crema e bianco**, nessun rosa ornamentale.
La meccanica Java, le probabilità di maturazione, la raccolta con
clic destro, il controllo delle sei facce e l'item ciliegie non sono
stati modificati.

La CI verifica ora **l'esatta posizione di tutte le nove
fruttificazioni durante le fasi 1–4**, i piccioli invariati, la
fioritura progressiva e l'identità completa dei pixel occupati
tra la ciliegia verde e quella rossa. Questo test impedisce che
fiori e frutti tornino a spostarsi nelle successive revisioni.

## Coerenza colore fogliame/bioma — 6 novembre 2026

Ogni fase con boccioli, fiori o ciliegie mantiene la **stessa
colorazione del bioma** delle foglie senza frutti. Il modello presenta
un cubo fogliare con `tintindex:0` e, appena all'esterno, un
secondo cubo cutout con soli pixel di fiori/frutti, **senza tint**.
Il vecchio caso nel client `stage > 0 → bianco` è stato eliminato.
Sono stati lasciati invariati **tutti e sei i PNG completi** delle
foglie e i nove gruppi delle ciliegie mature; i nuovi overlay sono
ottenuti estraendo solo le differenze rispetto alla foglia base.
Le due texture per ogni fase rispettano alpha 0/255; nessuna
meccanica Java, distribuzione, animazione o tempistica è stata toccata.
La resa cromatica va verificata in gioco in biomi diversi.
