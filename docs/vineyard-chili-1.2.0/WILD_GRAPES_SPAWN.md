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
- Heightmap `MOTION_BLOCKING_NO_LEAVES`: cerca la quota del terreno anche sotto la chioma degli alberi, invece del tetto delle foglie.
- Terreno valido per la comparsa: `grass_block`, `dirt`, `coarse_dirt`, `podzol`, `rooted_dirt`. Entrambi i blocchi di altezza della pianta devono essere vuoti.
- Il blocco `WildGrapeBlock` verifica anche il substrato FDR `GROWS_WILD_CROPS` prima del posizionamento; `WildGrapeFeature` piazza insieme metà superiore e inferiore (come `WildRiceFeature`).
- Nessuna generazione retroattiva: nuove patch **solo nei chunk generati dopo l'installazione** della modifica.

## Raccolta e compatibilità

Si raccolgono rompendo la pianta; con cesoie compatibili con la capacità FDR `shears_harvest` si recupera la pianta intera. Loot, bone meal, ID e texture approvate restano invariati.

Le quattro vecchie texture salvate in `docs/vineyard-chili-1.2.0/saved-textures/wild-grapevines/` sono state **eliminate dal branch su richiesta dell'utente**. Le texture nuove, dai grappoli irregolari approvati, restano in `src/main/resources/assets/italiansdelight/textures/block/`.

## Verifiche

I controlli CI verificano gli asset, i biomi, la parità fra varietà e il JSON della generazione. Il test del server dedicato utilizza `/place feature` su terreno di prova e verifica `half=lower` e `half=upper` di entrambe le viti. La densità effettiva nelle nuove zone va infine osservata nel client.
