# Fruit Cherry Wood — primo lotto (30 ottobre 2026)

Stato: implementato nel branch `feature/orchards-1.1.0`, **texture in attesa di approvazione in Minecraft**. Nessuna modifica a `main`.

Cinque blocchi: `fruit_cherry_log`, `fruit_cherry_wood`, `stripped_fruit_cherry_log`, `stripped_fruit_cherry_wood`, `fruit_cherry_planks`. Cinque texture 16×16 originali, palette castano-ramata: corteccia con incisioni e lievi lenticelle, tavole orizzontali, fibre più chiare nello stripped. Le sezioni dei tronchi hanno l'interno 14×14 identico; differisce solo il bordo di 1 pixel.

Inclusi: modelli orientabili, BlockItem, Creative, traduzioni italiane e inglesi, crafting (un tronco = 4 assi; 4 tronchi = 3 wood), loot, tag del legno e minerabilità con ascia, propagazione incendio, scortecciatura. Test statico in GitHub Actions.

## Checklist in Minecraft

- [ ] Provare i cinque blocchi in Creative e Survival senza duplicati.
- [ ] Verificare leggibilità e palette su pareti e tronchi ruotati nei tre assi.
- [ ] Verificare scortecciatura con ascia, output ricette, drop e combustione.
- [ ] Confrontare con `minecraft:cherry_*`: deve essere distinto, non rosa.
- [ ] Verificare salvataggio e caricamento di un mondo di prova.

Da realizzare dopo approvazione: derivati edilizi, foglie verdi, germoglio, albero procedurale, frutti e generazione naturale. Asset di mandorlo e ulivo approvati non modificati.
