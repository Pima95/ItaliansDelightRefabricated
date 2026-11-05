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

La regolazione dei colori del bioma, già presente solo per le foglie del
ciliegio da frutto, è mantenuta per lo **stato 0**. Negli stati 1–5
viene disattivata sul blocco per evitare che la tinta verde del bioma
trasformi i petali bianchi in fiori verdi o le ciliegie rosse in colori
diversi. Per rendere il bioma visibile anche durante la fioritura senza
alterare i petali sarà necessario, eventualmente, un sistema di overlay
con foglie e fiori su strati di tint separati.

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

Probabilità temporanea di **1/3 di avanzare di una fase per random tick**
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
