# Cardoon coltivabile, spawn selvatico e suoni dell'uncino

Branch: `feature/cardoon-farming-worldgen-sounds` (basato su Pizza 1.0.0).

## Riferimento Farmer's Delight Refabricated 26.2

Confrontati i sorgenti upstream `MehVahdJukaar/FarmersDelightRefabricated`, ramo `fabric/latest/26.2`, commit `1778f671ffabbf99ea20e0ae88908e4478bb6f3c`:
- **Cipolle selvatiche:** `rarity_filter.chance=120`, `count=64`.
- **Carote selvatiche:** `chance=120`, `count=64`.
- **Patate selvatiche:** `chance=100`, `count=64`.

Il **Cardoon** e il **Basilico selvatico** usano entrambi `120/64`, con offset e filtro aria/terreno. Questo allinea la rarità dei *tentativi*, non garantisce una densità identica: upstream usa anche una feature `in_order`, noi `simple_block`.

## Biomi selezionati

- Cardoon selvatico: **Plains, Sunflower Plains, Meadow, Savanna, Savanna Plateau, Windswept Savanna**.
- Basilico selvatico: **Plains, Sunflower Plains, Meadow, Forest, Flower Forest, Birch Forest, Cherry Grove, Sparse Jungle**.

Gli originari Plains/Sunflower Plains/Meadow restano. I nuovi chunk usano la nuova generazione; quelli già esplorati conservano le piante presenti.

## Compatibilità e nuovi oggetti

- Il registro **`italiansdelight:cardoon` resta invariato**: rappresenta la pianta selvatica, preserva le piante e i vasi dei vecchi mondi. Il vecchio item, se posseduto, resta utilizzabile.
- Aggiunto **`italiansdelight:cultivated_cardoon`**, nuovo BlockItem piantabile su farmland, 8 età, bonemeal, 4 stadi visivi, 1 item se raccolto prematuramente e almeno 2 se maturo (bonus Fortune). Nessun seme separato, come per il basilico.
- La versione selvatica rilascia **1–2 cultivated_cardoon**. La bonemeal sul selvatico fa diffondere al massimo una pianta per uso riuscito, entro il limite locale di 10 piante; non produce direttamente item a ogni clic.
- Il caglio della Cheese Vat usa ora **`#c:crops/cardoon`**, un tag con **entrambi** gli item vecchio e nuovo.
- Aggiunte **cinque** nuove texture PNG RGBA **16×16**, alpha rigorosamente 0 o 255, senza antialiasing, senza modificare i PNG approvati.
- Hanging Hook: `SoundType.CHAIN` per posa e rottura e `SoundEvents.CHAIN_PLACE`/`CHAIN_HIT` per aggancio/rimozione.

## Test Survival in attesa

- [ ] Controllare distribuzione in **nuovi chunk** contro la densità di cipolle/carote FDR, incluso il Basilico.
- [ ] In mondo vecchio, verificare che il Cardoon e il vaso non scompaiano e che il vecchio Cardoon continui a produrre caglio.
- [ ] Rompere pianta selvatica: **1–2 nuovi item coltivabili**, non direttamente piante selvatiche; bonemeal produce un vicino vegetale, non oggetti.
- [ ] Piantare su farmland, osservare tutti gli stadi (0–7), usare bone meal, raccogliere presto e a maturazione e confrontare rese/Fortune.
- [ ] Verificare 5 sprite 16×16, tutte le texture ben distinguibili e niente pixel viola/nero.
- [ ] Verificare incrocio delle due versioni dell'item nella ricetta del caglio.
- [ ] Controllare suoni dell'uncino quando è vuoto/occupato, con formaggi e salumi, senza doppie riproduzioni.
- [ ] Verificare tutte le voci nella scheda Creativa, le traduzioni e l'avvio di un server dedicato.

## Decisione confermata il 22 ottobre 2026

L'autore ha **approvato tutti i nuovi biomi** per Cardoon e Basilico selvatico elencati sopra. Non effettuare ulteriori modifiche alla selezione senza una nuova richiesta.
