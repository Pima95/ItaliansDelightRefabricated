# Pizza 1.0.0 — Nutrizione dinamica con stacking massimo 2

**Decisione dell'autore:** 21 ottobre 2026.  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** stacking nutrizionale collaudato dall'autore il 21 ottobre 2026; nuovo effetto Nourishment da testare in client.

## Decisione e motivazione

L'autore ha osservato che una pizza **non deve risultare più nutritiva solo perché ha un nome riconosciuto**. Dopo aver ricontrollato l'accordo precedente sul calcolo per categorie, ha proposto il compromesso:

> Restiamo con i punti nutritivi che avevamo stabilito, ma permettiamo di sommarli al massimo due volte per ciascun tipo/categoria di ingrediente.

**Regola implementata:** per ogni spicchio, conteggiare **fino a due ingredienti canonici distinti della stessa categoria nutrizionale**. Ciascuno applica i **bonus di fame e saturazione già approvati**; dal terzo ingrediente di quella categoria in poi, non si aggiunge alcun bonus. Le categorie si sommano fra loro.

- La regola si applica **a tutte le pizze**, riconosciute o personalizzate, senza casi speciali `Margherita` / `Quattro Formaggi`, senza lookup del nome e senza riconoscimento della ricetta per assegnare statistiche.
- Il conteggio avviene **sui dati salvati del singolo quarto**, con ID canonici `PizzaSliceData.Topping.canonicalId()`. Tutti i quarti di una stessa pizza possono produrre spicchi con statistiche differenti.
- Applicazioni ripetute dello **stesso ID canonico** non aggiungono contributi multipli. Due item crudo/cotto equivalenti hanno lo stesso ID canonico, quindi sono nutrizionalmente identici e non si moltiplicano.
- La mozzarella legacy conta come **un latticino**, ma non si somma di nuovo se nello stesso quarto esiste già `mozzarella_slice` (lo stesso formaggio in altra forma). Il limite a due si applica anche a questi dati storici.
- **Fame massima 6, saturazione massima 6** per spicchio: i due limiti vengono applicati separatamente. Dal 21 ottobre 2026, gli spicchi con almeno 3 punti fame concedono **Nourishment di Farmer's Delight (durata breve)**; quelli più semplici non concedono effetti.
- La pizza intera resta non commestibile; le normali regole di consumo, taglio, crafting, posizionamento, JEI, creative e i PNG approvati non vengono modificate.

## Bonus nutritivi già approvati (invariati)

| Categoria | Fame per ingrediente | Saturazione per ingrediente | Contributi massimi |
| --- | ---: | ---: | ---: |
| Crosta | +1 | +1 | Sempre uno |
| Salse | +1 | +0,5 | 2 |
| Latticini e formaggi | +1 | +1,5 | 2 |
| Proteine animali | +2 | +2,5 | 2 |
| Verdure/funghi | +1 | +0,5 | 2 |
| Erbe e spezie | +0 | +0 | 2 (nessun bonus nutrizionale) |
| Oli e grassi | +0 | +1 | 2 |

`Saturazione` qui significa i **punti aggiunti**, non il `saturationModifier` dell'API vanilla; il codice imposta `saturationModifier = puntiSaturazione / (2 × fame)`.

## Alcuni esempi

| Ingredienti sul singolo quarto | Fame | Saturazione | Motivazione |
| --- | ---: | ---: | --- |
| Impasto senza condimenti (Bianca) | 1 | 1 | Crosta |
| Salsa + mozzarella + basilico (Margherita) | 3 | 3 | Crosta + salsa + latticini; erbe 0 |
| Mozzarella + gorgonzola + provolone + parmigiano | 3 | 4 | Solo 2 dei 4 latticini contribuiscono |
| Salsa + quattro formaggi | 4 | 4,5 | Come sopra + salsa |
| Mozzarella + ricotta + mortadella | 5 | 6 | Due latticini + proteina, saturazione limitata a 6 |
| Mozzarella + speck + gorgonzola | 5 | 6 | Due latticini + proteina |
| Mozzarella + pomodoro a fette + cavolo + zucca + basilico | 4 | 3,5 | Soltanto due dei tre ortaggi contribuiscono |
| Mozzarella + la stessa mozzarella nuovamente | 2 | 2,5 | ID canonico duplicato: nessun beneficio aggiuntivo |
| Mozzarella + salame + speck + salsa + pomodoro | 6 | 6 | Due proteine e altre categorie: si applicano entrambi i cap |

## Modifiche tecniche

- `src/main/java/dev/italiansdelight/common/pizza/PizzaSliceFactory.java`: `Set<Identifier> seenTypes` evita il doppio contributo dello stesso ID canonico; `EnumMap<NutritionCategory,Integer> counts` conteggia gli ID diversi per categoria; il ciclo assegna `min(2, count)` contributi senza variare i valori per categoria.
- `.github/scripts/check_pizza_release_balance.py`: snapshot delle **18 ricette effettive**, test di 1/2/3/4 formaggi, duplicati, due proteine, mozzarella legacy, pizza personalizzata con tetti a 6; restano i controlli di cottura.
- `.github/workflows/pizza-build.yml`: esegue già lo script precedente prima di Gradle, quindi non servono duplicazioni di job.

**Attenzione:** i test di GitHub Actions controllano il codice sorgente e le ricette e compilano la mod; la verifica in un mondo Survival, incluso il ripristino reale degli indicatori di fame/saturazione e lo stato di salvataggio, resta da effettuare.

**Compatibilità salvataggi e test:** la proprietà nutrizionale `DataComponents.FOOD` viene salvata sullo stack quando `PizzaSliceFactory.create` produce un nuovo spicchio. Gli spicchi **già presenti in un vecchio inventario** possono quindi conservare i valori precedenti, anche dopo l'aggiornamento della mod. Per verificare il nuovo bilanciamento occorre preparare o ottenere **nuovi spicchi** dopo il pull; non è stata aggiunta una migrazione automatica degli oggetti esistenti senza un requisito esplicito. Le pizze intere già in inventario possono invece generare nuove fette con i valori aggiornati al momento del taglio.

## Collaudo suggerito

- [x] Pizza Bianca e Margherita: fame e saturazione confermate dall'autore.
- [x] Quattro Formaggi bianca e rossa: nuovo stacking confermato dall'autore.
- [ ] Un quarto con un solo latticino, con due latticini distinti e con tre o quattro: il bonus si ferma al secondo.
- [ ] Due proteine animali diverse sullo stesso quarto: si sommano, fino al tetto 6.
- [ ] Crudo/cotto equivalente dello stesso ingrediente e doppia applicazione legacy: nessun bonus duplicato.
- [ ] Pizza assortita con quarti diversi: ogni spicchio usa **solo** la propria composizione.
- [ ] Persistenza dopo chiusura mondo, compatibilità con forni, pala e campioni Creativa; asset approvati invariati.

## Effetto Nourishment — 21 ottobre 2026

Al momento della creazione di una **nuova fetta cotta**, `PizzaSliceFactory.create` calcola i punti fame del solo quarto fisico. Quando sono **almeno 3**, assegna all'`ItemStack` il consumabile originale `FoodValues.ConsumableValues.NOURISHMENT_SHORT_DURATION` di Farmer's Delight Refabricated. Questo è lo stesso sistema dei piatti di pasta e risotto della mod. Non vengono modificati fame, saturazione, timer di cottura, PNG o nomi delle pizze. Il riconoscimento del gusto non influenza l'effetto.

- **Nessun effetto:** Bianca (1 fame), Rossa (2), sola mozzarella (2).
- **Nourishment breve:** Margherita (3 fame), Quattro Formaggi bianca (3), Salame (5) e qualsiasi fetta con almeno 3 punti fame; vale anche per pizze personalizzate e quarti indipendenti di una pizza assortita.
- **Compatibilità:** i vecchi `pizza_slice` già nell'inventario possono conservare il precedente `DataComponents.CONSUMABLE`; per il collaudo generare nuove fette dopo l'aggiornamento. `raw_pizza` e `cooked_pizza` rimangono non commestibili.

**Test nuovo, ancora da superare:** in Survival mangiare fette nuove di Bianca/Rossa/Margherita/Quattro Formaggi, controllare la presenza o assenza dell'effetto e verificare che la durata sia quella breve della dipendenza. Verificare anche una fetta dinamica da 2 e una da 3 fame e una pizza con quattro quarti differenti. La build CI non simula il consumo in Minecraft.
