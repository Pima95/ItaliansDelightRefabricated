# Texture 1.0.0 — catalogo esatto delle pizze riconosciute e registro delle decisioni

**Data:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Fase:** inventario delle texture delle pizze intere cotte e piano di collegamento grafico.

## Decisione di processo dell'autore (20 ottobre 2026)

> **Ogni decisione, discussione, chiarimento o modifica sul progetto, anche minima, va registrata su GitHub.**

Questa istruzione si applica da ora a tutte le fasi di Italian's Delight Refabricated: documentare in `docs/` sul branch di lavoro le decisioni tecniche, gli eventuali compromessi, gli esiti dei test dell'autore, i nuovi requisiti, le scelte temporaneamente sospese, il piano del passo successivo e i cambi di direzione. Prima di dichiarare terminato un passaggio, verificare che il documento pertinente e la roadmap riflettano lo stato corrente. Non committare su `main` direttamente; PR per integrare le funzionalità.

**Conferma recente dell'autore:** la riorganizzazione della scheda Creativa funziona, incluse le tre voci pizza poste **dopo i salumi e prima dei piatti pronti**. La fase Creative è approvata. Passiamo alla grafica delle pizze riconosciute.

## Inventario ricavato dal codice

Fonte di verità: `src/main/java/dev/italiansdelight/common/pizza/PizzaDisplayNames.java`, funzione `buildPresets()`, `PRESETS`. Ci sono **18 preset attivi**, a prescindere dai **19 nomi storicamente approvati**. **Principessa è sospesa** (pomodoro + panna incompatibili nello stesso quarto) e non deve ricevere né modello né texture in questa release senza una nuova approvazione.

| Preset (ID canonico riconosciuto) | Nome | Ingredienti canonici (senza prefisso `pizza_toppings/`) | Texture dell'item pizza cotta | Direzione artistica |
|---|---|---|---|---|
| `bianca` | Bianca | — | `pizza_bianca.png` | Pasta dorata e irregolare, senza topping |
| `rossa` | Rossa | tomato_sauce | `pizza_rossa.png` | Salsa rossa continua con piccole irregolarità pixel |
| `margherita` | Margherita | tomato_sauce, mozzarella_slice, basil | `pizza_margherita.png (GIÀ PRESENTE)` | Riutilizzare la texture esistente; vietato sovrascrivere senza consenso |
| `caprese_rossa` | Caprese rossa | tomato_sauce, mozzarella_slice, tomato_slice, basil | `pizza_caprese_rossa.png` | Rosso con fette pomodoro e foglie verdi distinguibili |
| `caprese_bianca` | Caprese bianca | mozzarella_slice, tomato_slice, basil | `pizza_caprese_bianca.png` | Base chiara, pomodoro e basilico senza salsa rossa |
| `quattro_formaggi_bianca` | Quattro Formaggi bianca | mozzarella_slice, gorgonzola_wedge, provolone_slice, grated_parmigiano_reggiano | `pizza_quattro_formaggi_bianca.png` | Formaggi crema/giallo e piccole venature blu del gorgonzola |
| `quattro_formaggi_rossa` | Quattro Formaggi rossa | tomato_sauce, mozzarella_slice, gorgonzola_wedge, provolone_slice, grated_parmigiano_reggiano | `pizza_quattro_formaggi_rossa.png` | Salsa rossa sotto i quattro formaggi |
| `prosciutto_crudo` | Prosciutto crudo | tomato_sauce, mozzarella_slice, prosciutto_crudo_slice | `pizza_prosciutto_crudo.png` | Strisce rosa/rosse di prosciutto e mozzarella |
| `salame` | Salame | tomato_sauce, mozzarella_slice, salame_slice | `pizza_salame.png` | Dischi rossi scuri di salame, non salame piccante |
| `mortadella_ricotta` | Mortadella e Ricotta | mozzarella_slice, mortadella_slice, ricotta | `pizza_mortadella_ricotta.png` | Base crema con mortadella rosa, ricotta bianca |
| `guanciale_pecorino_rossa` | Guanciale e Pecorino rossa | tomato_sauce, guanciale_slice, grated_pecorino_romano | `pizza_guanciale_pecorino_rossa.png` | Salsa rossa, guanciale striato e scaglie chiare |
| `speck_gorgonzola` | Speck e Gorgonzola | mozzarella_slice, speck_slice, gorgonzola_wedge | `pizza_speck_gorgonzola.png` | Speck ramato e punte blu-verdi del gorgonzola |
| `salmone_ricotta` | Salmone e Ricotta | mozzarella_slice, salmon_slice, ricotta, basil | `pizza_salmone_ricotta.png` | Rosa salmone, ricotta chiara, basilico |
| `salmone_scamorza` | Salmone e Scamorza affumicata | mozzarella_slice, salmon_slice, smoked_scamorza_slice | `pizza_salmone_scamorza.png` | Salmone rosa e scamorza dorata affumicata |
| `carne_mozzarella` | Carne macinata e Mozzarella | tomato_sauce, mozzarella_slice, minced_beef | `pizza_carne_mozzarella.png` | Pezzetti marroni di carne su base rossa/bianca |
| `zucca_speck` | Zucca e Speck | mozzarella_slice, pumpkin_slice, speck_slice | `pizza_zucca_speck.png` | Arancio zucca e rosso scuro speck |
| `patate_speck` | Patate e Speck | mozzarella_slice, potato_slice, speck_slice | `pizza_patate_speck.png` | Patate crema-giallo, speck rosso-marrone |
| `orto_disponibile` | Orto disponibile | mozzarella_slice, tomato_slice, cabbage_leaf, pumpkin_slice, basil | `pizza_orto_disponibile.png` | Alternanza rosso, arancio e verdi vegetali |

**Conteggio:** 18 gusti attivi = **1 texture già presente (`pizza_margherita.png`) + 17 texture nuove** per pizze intere. Nessun asset per Principessa, per i 52 gusti storicamente senza nome fisso o per i 36 gusti futuri non approvati.

## Regole grafiche vincolanti

- Esclusivamente **16×16 PNG RGBA** per item Minecraft, pixel con alpha **0 o 255**, sfondo trasparente, senza antialiasing né sfumature che cancellino l'aspetto pixel art; coerenza con Vanilla e Farmer's Delight Refabricated.
- Disegni differenti, identificabili in hotbar, con base/crust sempre riconoscibile; evitare quattro quadranti identici o distribuzione troppo meccanica.
- Coerenza visiva con i topping di gioco già approvati: pomodoro, formaggi, salumi, basilico e verdure riconoscibili senza riprodurre gli item vanilla come miniature flottanti.
- Non sostituire `textures/item/pizza_margherita.png`, `textures/item/raw_pizza.png`, `textures/item/margherita_slice.png` o altri PNG precedenti senza un'autorizzazione esplicita. La Margherita cotta **ha già** una texture e deve essere riutilizzata.
- Il modello attuale `models/item/cooked_pizza.json` punta indiscriminatamente alla Margherita: **non è un fallback neutro**. Serve selezione dinamica e un modello/texture generici veri per pizze non riconosciute. La pizza cruda e gli spicchi rimangono invariati in questa fase.

## Regole di collegamento runtime

1. I gusti sono **solo modelli/texture**, non 18 nuovi `Item` e non nuove voci Creative. Restano `italiansdelight:cooked_pizza` e il componente originale `italiansdelight:pizza_contents`.
2. **Match esatto su quattro quarti fisici:** solo quattro quarti con insiemi canonici identici ed esattamente corrispondenti a un preset ricevono la sua icona. Un ingrediente extra o mancante annulla il match.
3. **Pizza assortita o dinamica:** mantenere nome, tooltip e contenuti originali; grafica **fallback generico**, mai Margherita per errore.
4. L'ID del gusto usato dal selettore grafico deve derivare dal **medesimo algoritmo centrale dei nomi**, per non far divergere etichetta e aspetto. Evitare elenchi hardcoded duplicati in client e JSON. La selezione del modello non deve cancellare il componente della composizione né alterare crafting, cottura, pala, servizio e salvataggi.
5. Il momento di assegnazione/selezione del modello deve coprire i percorsi normali di creazione, raccolta, ricottura, crafting e ripristino da vecchi salvataggi. Non cambiare automaticamente la grafica degli spicchi in questa fase; la scelta sugli spicchi è la **fase seguente** dopo verifica delle pizze intere.

## Ordine operativo

1. **Margherita** — recuperare e confermare dimensioni, palette e resa dell'asset già esistente; non sovrascrivere.
2. **Patate e Speck** — prima nuova texture per testare un gusto riconosciuto introdotto dopo il nuovo ingrediente `potato_slice`.
3. **Bianca e Rossa** — fondamentali per verificare le basi senza topping.
4. **Caprese bianca/rossa e Quattro Formaggi bianca/rossa** — verifica leggibilità fra basi chiare e rosse.
5. **Gli altri dieci gusti attivi** — usare gli stessi vincoli e naming, senza nuovi item.
6. **Integrazione modello** — mantenere selezione per composizione e fallback; validare 18 gusti, pizza assortita, pizza non riconosciuta, pala, salvataggi e texture mancanti. Inserire almeno un test automatico di copertura fra preset, modelli e PNG.
7. **Test in gioco** dell'autore prima di dichiarare approvato il risultato; soltanto dopo passare alle texture dedicate dei singoli spicchi riconosciuti, se confermate.

## Stato implementazione — primo lotto (20 ottobre 2026)

**Già pubblicato nel branch:** tre nuovi gusti con texture PNG originali 16×16: `pizza_bianca.png`, `pizza_rossa.png`, `pizza_patate_speck.png`. Creato inoltre `pizza_custom.png` (fallback neutro). La **Margherita** usa esattamente la vecchia `pizza_margherita.png`, senza modificarla. Registrati i relativi `assets/italiansdelight/models/item/pizza_*.json` e `assets/italiansdelight/items/pizza_*.json`. 

Il selettore in `PizzaDisplayNames.nameWholeStack` applica `DataComponents.ITEM_MODEL` **solo agli item pizza cotta**, scegliendo **Bianca, Rossa, Margherita, Patate e Speck** dal medesimo algoritmo usato per il nome canonico. **Gli altri 14 gusti ufficiali**, fino al completamento delle loro texture, usano `pizza_custom`. Anche pizze assortite, libere o con ingredienti extra usano `pizza_custom` e **non più il disegno Margherita**; le pizze crude e gli spicchi restano invariati.

**Verifica automatica:** lo script `.github/scripts/check_named_pizza_artwork.py` controlla le definizioni e i modelli, i PNG 16×16 RGBA, l'alpha binario dei nuovi asset, il fallback e le quattro mappature canoniche. È eseguito dalla workflow `pizza-build.yml` prima della compilazione Java 25.

**Ancora da collaudare nel client:** resa visiva delle tre nuove texture, contrasto/dimensione rispetto all'asset Margherita esistente, scelta effettiva degli item model, comportamento della pala e dei salvataggi precedenti. Uno stack cotto preesistente privo di `ITEM_MODEL` riceve il modello base neutro; verificarne il comportamento dopo cottura/raccolta e decidere eventuale migrazione automatica dei vecchi stack solo dopo un test. **Non dichiarare la fase grafica conclusa**.

### Checklist specifica del primo lotto

- [ ] Cuocere una pizza intera **Bianca**, una **Rossa**, una **Margherita** e una **Patate e Speck**: quattro nomi e quattro icone esatte nella hotbar e inventario, mai la stessa icona per tutti.
- [ ] Confrontare **Patate e Speck** in inventario e nel mondo: in inventario usare PNG 16×16 dedicato, nel mondo mantenere rendering con topping 2×2/3×3 già approvato.
- [ ] Pizza con **speck extra** su una Margherita, pizza assortita e gusto ufficiale non ancora disegnato (es. Caprese rossa): tutti devono mostrare l'icona neutra senza perdersi ingredienti, nome o tooltip.
- [ ] Verificare la pizza **cruda** con gli stessi topping: la sua icona originale non deve cambiare.
- [ ] Verificare il **trasporto sulla pala** con pizza cotta riconosciuta e fallback (nessuna regressione ai modelli nelle mani).
- [ ] Conservare, chiudere e riaprire un mondo con una pizza cotta nominata: modello e nome coerenti per i nuovi stack; provare anche vecchi stack senza componente modello.
- [ ] Verificare che JEI, i 97 item della scheda Creativa e gli spicchi restino invariati.
- [ ] Confermare qualità grafica e pixel nitidi degli asset prima di disegnare i restanti 14 gusti.

## Casi di accettazione

- [ ] 18 gusti con quattro quarti identici e match esatto → nome e icona corrispondenti.
- [ ] Principessa (sospesa), pizze libere e miste → **nessun** falso riconoscimento, fallback corretto.
- [ ] Cambio ingrediente, rimozione parziale o quarti diversi → il gusto non resta erroneamente fisso.
- [ ] Texture 16×16, RGBA e alpha binario, con contorni vanilla nitidi; nessun asset esistente sostituito.
- [ ] Pizza sulla pala, in inventario, a terra e dopo salvataggio → stessa icona corretta.
- [ ] Nessuna modifica a hunger, saturazione, ingredienti, recipe, JEI, blocchi pizza e sprite di superficie.
- [ ] Compilazione Java 25 + controlli automatici; approvazione finale dopo screenshot dell'autore.

**Prossimo commit previsto:** primi asset di prova e selettore dei modelli, senza modificare le pizze cotte nel mondo.
