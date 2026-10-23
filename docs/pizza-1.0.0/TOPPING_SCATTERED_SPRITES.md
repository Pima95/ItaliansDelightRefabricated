# Pizza 1.0.0 — Condimenti a frammenti con sprite dedicati

**Data:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** implementato e compilato su GitHub; da approvare visivamente nel client.

## Richiesta

Il sistema 12×12 a superficie intera è stato approvato **per salse e creme**, ma non per i condimenti solidi: i ripetuti quarti di mozzarella, basilico o salumi apparivano troppo simmetrici, ed erano frequenti le sovrapposizioni che rendevano difficile riconoscerli.

Questa iterazione usa **un modello grafico misto**:

- **Salse immutate:** il renderer usa ancora i PNG originali `textures/block/pizza_toppings/tomato_sauce.png`, `cream_bowl.png` e `ricotta.png`. Ogni salsa riveste in modo continuo il 12×12, tagliato esattamente nei quadranti 6×6 dove è stata applicata.
- **Condimenti solidi:** niente `ItemStackRenderState` o modelli degli item vanilla. Ogni ingrediente usa una **nuova texture atlas da 12×12**, sotto `textures/block/pizza_topping_sprites/`, che contiene **quattro varianti grafiche indipendenti 6×6** ricavate dai quattro quarti della precedente texture. Ogni variante ha una sagoma centrale disegnata a pixel di dimensioni max 3×3, con un ritaglio 2×2 sempre visibile.
- **Asset archiviati:** tutti i 33 PNG `textures/block/pizza_toppings/` preesistenti restano nel repository **senza modifiche o cancellazioni**. I loro corrispettivi solidi non vengono più letti in rendering; possono essere recuperati in futuro. Le tre texture salsa, come detto, **restano attive**.
- **30 nuovi atlanti:** 26 condimenti solidi attuali, due pianificati, la mozzarella legacy e `unknown` per tipi di topping non riconosciuti. Tutti i PNG sono 12×12 RGBA, alfa solo 0 o 255, niente antialiasing.
- Le quattro varianti sono scelte deterministicamente da `renderSeed`, `sequence`, indice del quarto e copia. I frammenti su una pizza già salvata non cambiano posizione tra i frame e non vengono risorteggiati dopo reload o sincronizzazione multiplayer.
- **Distribuzione anti-sovrapposizione:** una griglia logica per ogni quarto è mescolata in modo riproducibile; ciascuna porzione riceve una cella esclusiva. L'uso di posizioni diverse evita la disposizione fissa uguale su tutti e quattro i quarti.

## Densità dei condimenti in un quarto di 6×6

| Condimenti solidi nello stesso quarto | Frammenti per condimento | Dimensione per frammento |
|---|---:|---:|
| 1 | 2 | 3×3 pixel |
| 2 | 2 | 2×2 pixel |
| 3 | 1 | 3×3 pixel |
| 4–6 | 1 | 2×2 pixel |
| 7, esclusivamente nei salvataggi legacy con mozzarella prototipale | 1 | 2×2 pixel |

La distribuzione non fa sovrapporre i **rettangoli degli sprite** (anche quando le texture sono trasparenti e la singola sagoma è irregolare), limita ogni frammento al **proprio quarto fisico** e non altera la grafica della salsa presente sotto. Tutti i frammenti sono disegnati sulla stessa superficie della pasta, solo con un offset infinitesimale per evitare z-fighting e senza l'effetto item frame rialzato.

## File modificati

- `src/main/java/dev/italiansdelight/client/render/PizzaToppingVisuals.java`: separazione salse/solidi, gestione sprite 6×6 e UV 2×2 o 3×3, posizioni randomizzate ma persistenti grazie al seed.
- `src/main/resources/assets/italiansdelight/textures/block/pizza_topping_sprites/*.png`: 30 **nuovi** atlanti derivati dai vecchi quarti. Nessun asset precedente viene riscritto.
- `.github/scripts/check_pizza_topping_textures.py`: valida 33 PNG originali e 30 nuovi, 12×12 RGBA, alfa binario, presenza grafica dei quattro ritagli 3×3 e 2×2.
- I dati di gameplay, le ricette, i modelli di blocco, JEI, le texture originali e i renderer di base cruda/cotta restano invariati.

## Checklist in Minecraft

- [ ] Pizza intera con salsa di pomodoro: **identica** alla versione precedentemente approvata, anche quando si mischiano salse diverse sui quattro quarti.
- [ ] Margherita: mozzarella e basilico sono **frammenti distinti**, non texture quadranti ripetute e non icone degli item. Confrontare tutti e quattro i quarti.
- [ ] Pizza con **1 condimento solido**: due frammenti riconoscibili per quarto, non esattamente nella stessa posizione su tutte le pizze.
- [ ] Pizza con **2 e 3 condimenti per quarto**: nessuna sovrapposizione o posizione troppo simmetrica; verificare che ogni topping abbia colori e sagoma riconoscibili.
- [ ] Pizza con **6 solidi per quarto**: i frammenti più piccoli sono tutti presenti, separati e all'interno del bordo da 14×14.
- [ ] Pizza con quattro quarti diversi e pizza divisa in metà: ogni ingrediente rimane nel quarto cui appartiene, senza spostarsi o apparire su quelli vicini.
- [ ] Rimozione via Shift + clic: scompare soltanto il topping interessato; rimborsi e LIFO rimangono quelli salvati, senza applicazioni duplicate.
- [ ] Pizza cotta parzialmente consumata: gli sprite del quarto raccolto spariscono immediatamente; quelli delle fette restanti conservano la disposizione.
- [ ] Salvataggio, riavvio, pala, cottura, server multiplayer: layout deterministico e nessuna perdita dei dati.
- [ ] Vista da lato: nessuna distanza visibile tra condimenti e fondo della pizza, niente sfarfallio o texture viola/nera.
- [ ] Compilazione `gradlew clean build` e validatore degli asset completi in GitHub Actions.

**Limite:** il test CI assicura integrità degli asset e compilazione. L'aspetto di una vera pizza nel mondo è soggettivo e deve essere valutato in gioco dall'autore prima di segnare la rifinitura grafica come chiusa.
