# Schede JEI «Information» — Piante Italian's Delight

## Riferimento video

Nel filmato allegato, Farmer's Delight mostra la scheda JEI
**Information** per il pomodoro: icona dell'ingrediente, testo descrittivo,
barra di scorrimento e, passando sull'icona, nome **Tomato Shrub**.
Questo è il **visualizzatore di informazioni nativo JEI**, non una ricetta
aggiuntiva né una categoria personalizzata.

Per ottenere **lo stesso comportamento**, l'integrazione usa
`IRecipeRegistration.addItemStackInfo(List<ItemStack>, Component)`
di JEI per Minecraft **26.2**. I singoli oggetti raggruppati condividono
la stessa descrizione localizzata e possono essere raggiunti dalla loro
pagina di usi/ricette JEI; JEI controlla icone, scorrimento e impaginazione.

## Cinque schede (13 oggetti collegati)

| Scheda | Oggetti collegati | Contenuto |
|---|---|---|
| Ulivo | `olives`, `olive_sapling`, `olive_leaves` | Ulivo normale/secolare, raccolta, selezione produttiva, serra |
| Mandorlo | `almonds`, `almond_sapling`, `almond_leaves` | Crescita tramite germoglio, raccolta, calendario stagionale |
| Ciliegio da frutto | `cherries`, `fruit_cherry_sapling`, `fruit_cherry_leaves` | Fioritura bianca, frutti, stagione, serre |
| Basilico | `basil`, `wild_basil` | Biomi dove nasce selvatico, coltivazione su terreno agricolo |
| Cardoon | `cultivated_cardoon`, `cardoon` | Variante spontanea, coltivazione, crescita/Serene Seasons |

Le descrizioni sono incluse **in italiano e inglese** e descrivono
esclusivamente meccaniche già implementate nel gioco:

- Alberi piantati con germogli; **nessuna generazione naturale**
  degli ulivi, mandorli e ciliegi ancora abilitata.
- Le foglie naturali esposte possono fruttificare: selezione del
  **65%** che ruota ogni **4 giorni Minecraft** per singola foglia.
  Raccolta a clic destro (1–3 frutti), farina d'ossa per avanzare
  uno stadio e serre per fruttificare fuori stagione.
- Basilico e cardoon spontanei generati nei biomi realmente
  dichiarati in `ModWorldGeneration.java`; colture su farmland
  con otto età e farina d'ossa.
- Con Serene Seasons: tag delle **quattro stagioni** per colture
  e possibilità di maturazione fuori stagione degli alberi sotto vetro.
  Senza Serene Seasons: crescita vanilla/bilanciamento già concordato.

## Scelte tecniche

- Non è stata creata una categoria JEI fittizia: la scheda
  **Information** nativa corrisponde esattamente al video.
- Non sono stati aggiunti nuovi item, texture, GUI o ricette.
- Non è stato introdotto alcun liquido di olio o ricetta di
  spremitura: il `Crushing Tub` resta nella fase precedente,
  in attesa dei file del liquido che l'autore sta cercando.
- La nuova classe `OrchardPlantJeiInfo` è invocata
  esclusivamente da `registerRecipes` nel plugin JEI già presente.
  Le traduzioni sono sotto `jei.italiansdelight.plants.*`.
- Il test `.github/scripts/check_jei_plant_information.py`
  valida copertura degli oggetti, localizzazioni e assenza di
  ricette/GUI aggiunte.

## Checklist per la verifica in gioco

- [ ] Aprire JEI (usi) su un'oliva, una mandorla, una ciliegia
  e sui rispettivi germogli e foglie: deve esserci Information.
- [ ] Verificare Information su basilico selvatico/coltivato
  e su cardoon selvatico/coltivato.
- [ ] Confrontare icona, scorrimento e tab con la scheda Tomato
  di Farmer's Delight mostrata nel video.
- [ ] Cambiare lingua italiana/inglese e verificare il testo.
- [ ] Verificare che l'assenza di Serene Seasons non interrompa JEI.
- [ ] Verificare che il Crushing Tub non mostri ricette di
  olio non ancora implementate.
