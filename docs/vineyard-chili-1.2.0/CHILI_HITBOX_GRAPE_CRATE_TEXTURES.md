# Peperoncini: hitbox FDR e crate uva — 1.2.0

## Hitbox
I peperoncini, anche quelli appesi, ereditano direttamente `TomatoBlock#getShape` di Farmer's Delight Refabricated 26.3. Sono state rimosse tutte le voxel shape personalizzate introdotte precedentemente. FDR utilizza un outline fisso 12×16×12; questo è ora lo stesso del peperoncino a tutti gli stadi. La collisione resta disattivata; gli stati AGE_7, i registri, la crescita e la raccolta con ricrescita da età 4 sono preservati.

## Crate
Le nuove texture superiori 16×16 sono disegnate partendo dal layout e dalla cornice del crate dei pomodori di FDR. I frutti ora sono piccoli acini raggruppati in grappoli triangolari riconoscibili, con palette distinte viola e giallo-verde; non sono recolor del crate delle ciliegie. Nessun pixel trasparente o antialias. Le texture laterali già presenti sono mantenute per una successiva revisione visiva.

Entrambi i crate sono registrati come blocchi piazzabili con BlockItem, modelli blocco/item, blockstates, traduzioni italiane/inglesi, gruppo Creative, combustibilità, loot table, tag storage/axe e ricette reversibili 9 grappoli ↔ 1 crate.

Comandi di prova:
```
/give @p italiansdelight:grape_crate
/give @p italiansdelight:white_grape_crate
```

La verifica in gioco e la build Java devono ancora essere eseguite localmente.
