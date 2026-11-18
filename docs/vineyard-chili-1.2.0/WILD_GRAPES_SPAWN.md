# Generazione naturale delle viti selvatiche (1.2.0)

Riferimento tecnico: Farmer's Delight Refabricated 26.3, colture selvatiche e `WildRiceFeature` per le piante alte due blocchi.

## Biomi di generazione

Le due varietà hanno **identica distribuzione**, senza una rarità artificiale per l'uva bianca:

- Pianure (`plains`), pianure dei girasoli (`sunflower_plains`)
- Prati (`meadow`)
- Foreste (`forest`), foreste fiorite (`flower_forest`) e di betulle (`birch_forest`)
- Boschetti di ciliegi (`cherry_grove`)

Non si generano in deserti, savane, giungle, paludi, biomi innevati o nelle caverne. La preferenza per radure e margini boschivi deriva da terreno e spazio liberi; **non c'è un algoritmo che identifichi esattamente i confini tra biomi**.

## Frequenza e aspetto del gruppo

- Un tentativo di patch ogni **100 chunk idonei** per varietà, come la rarità delle colture selvatiche di FDR.
- **24 tentativi di collocazione** per patch, su posizioni casuali in un'area orizzontale di ±4 blocchi e verticale ±2, con preferenza per un piccolo gruppo sparso. L'effettivo numero di piante varia con il terreno, gli ostacoli e l'aleatorietà; non esiste un numero garantito.
- La patch ora richiama un configured feature `farmersdelight:in_order`, costruito come le colture selvatiche FDR: prima la vite a due blocchi, poi la **terra brulla (coarse dirt)** e infine gli **arboscelli secchi (dead bush)** attorno. Sono decorazioni occasionali, non obbligatorie a ogni singolo tentativo.
- La terra brulla ha filtro di rarità `1/3` per tentativo decorativo e compare solo al posto di `grass_block` o `dirt`, se il blocco superiore è vuoto. Il dead bush ha filtro `1/4` e viene posto solo in aria su terreno naturale valido. Offsets decorativi: circa ±4 blocchi per coarse dirt e ±5 per dead bush. Il numero effettivo dipende dalla casualità e dagli ostacoli.
- L'ordine e i controlli impediscono di sostituire piante, tronchi o altre strutture con la terra brulla. Le decorazioni possono comparire anche quando un singolo tentativo di vite non va a buon fine, come nel sistema `in_order` di FDR.
- Heightmap `MOTION_BLOCKING_NO_LEAVES`: cerca la quota del terreno anche sotto la chioma degli alberi, invece del tetto delle foglie.
- Terreno valido per la comparsa: `grass_block`, `dirt`, `coarse_dirt`, `podzol`, `rooted_dirt`. Entrambi i blocchi di altezza della pianta devono essere vuoti.
- Il blocco `WildGrapeBlock` verifica anche il substrato FDR `GROWS_WILD_CROPS` prima del posizionamento; `WildGrapeFeature` piazza insieme metà superiore e inferiore (come `WildRiceFeature`).
- Nessuna generazione retroattiva: nuove patch **solo nei chunk generati dopo l'installazione** della modifica.

## Raccolta e compatibilità

Si raccolgono rompendo la pianta; con cesoie compatibili con la capacità FDR `shears_harvest` si recupera la pianta intera. Loot, bone meal, ID e texture approvate restano invariati.

Le quattro vecchie texture salvate in `docs/vineyard-chili-1.2.0/saved-textures/wild-grapevines/` sono state **eliminate dal branch su richiesta dell'utente**. Le texture nuove, dai grappoli irregolari approvati, restano in `src/main/resources/assets/italiansdelight/textures/block/`.

## Verifiche

I controlli CI verificano gli asset, i biomi, la parità fra varietà, il collegamento ai due nuovi feature `in_order` e i filtri di sicurezza della decorazione. Il test del server dedicato usa `/place feature` sia sui feature delle singole viti sia sui gruppi completi `_cluster`: verifica che i datapack vengano caricati e che siano presenti le due metà. La densità effettiva di terra brulla e dead bush nelle nuove zone va infine osservata nel client.

## Riferimenti tecnici upstream

- `FarmersDelightRefabricated`, branch `fabric/latest/26.3`: `src/main/resources/data/farmersdelight/worldgen/feature/wild_carrots.json` (coarse dirt) e `wild_tomatoes.json` (dead bush).
- `src/main/java/vectorwing/farmersdelight/common/world/feature/InOrderFeature.java` definisce la composizione dei placed features. Il sistema non aggiunge artificialmente yield o comportamento alla vite.
- Si conservano i feature `italiansdelight:wild_grape_vine` e `italiansdelight:wild_white_grape_vine` come generatori delle singole piante a due blocchi; i feature `*_cluster` sono quelli referenziati dalle patch naturali.
