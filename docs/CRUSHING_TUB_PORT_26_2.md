# Crushing Tub — modello Rustic adattato a Minecraft 26.2 / Fabric

Questo commit integra **solo il blocco, il suo item e il crafting**.
La texture `assets/italiansdelight/textures/block/crushing_tub.png` era già
presente nel branch e non è stata sostituita.

## Origine e adattamento

File di riferimento forniti dal progetto:
- JSON del blocco Rustic: cinque cuboidi (`Base`, `SideNorth`,
  `SideSouth`, `SideWest`, `SideEast`), con UV e trasformazioni
  dell'oggetto originali
- JSON item Rustic: `parent: rustic:block/crushing_tub`
- ricetta `forge:ore_shaped` con ore dictionary `plankWood`,
  `ingotIron`, `slabWood`

Adattamento Minecraft 26.2:
- ID: **`italiansdelight:crushing_tub`**
- Texture del legno: `minecraft:block/oak_planks` invece
  del vecchio `blocks/planks_oak`
- Texture del mastello: `italiansdelight:block/crushing_tub`
  al posto di `rustic:blocks/crushing_tub`
- Blockstate `blockstates/crushing_tub.json` con una variante
- Modello oggetto `models/item/crushing_tub.json` che eredita
  il modello del blocco (rotazioni e dimensioni originali)
- Definizione oggetto **obbligatoria nelle nuove versioni**
  `items/crushing_tub.json` che punta a
  `italiansdelight:item/crushing_tub`
- `CrushingTubBlock` usa un *VoxelShape* vuoto al centro che
  segue le cinque parti del modello (altezza massima 9/16):
  il cursore, le collisioni e la geometria restano coerenti
- Registrazione come BlockItem piazzabile, inserimento nella tab
  Creative di Italian's Delight e traduzioni EN/IT
- Rimozione con ascia e loot che restituisce il blocco

## Ricetta (invariata nella forma)

```
P P
I I
SSS
```

`P` = `#minecraft:planks`, qualsiasi tavola di legno;
`I` = `minecraft:iron_ingot`;
`S` = `#minecraft:wooden_slabs`, qualsiasi lastra di legno.

Entrambi i tag comprendono le varianti personalizzate di ulivo,
mandorlo e ciliegio da frutto già registrate da Italian's Delight.
La serializzazione è `minecraft:crafting_shaped` con
`result.id` e non i vecchi `forge:ore_dict` / `result.item`.

## Comportamento

**Non sono state aggiunte meccaniche di spremitura.** Il file Rustic
fornito non include ricette di produzione, lavorazione con i piedi,
liquidi, capacità, GUI o inventario. Il blocco è intenzionalmente
un recipiente piazzabile e craftabile, pronto per una futura
implementazione delle operazioni di crushing.

## Checklist Minecraft

- [ ] Il Tino di pigiatura appare nella scheda Creative di Italian's Delight
- [ ] L'oggetto 3D mantiene l'aspetto delle cinque parti e i display transform
- [ ] Il modello piazzato è aperto sopra e le collisioni seguono i bordi
- [ ] La texture `crushing_tub.png` si vede; il fondo usa quercia vanilla
- [ ] La ricetta 2 assi + 2 lingotti + 3 lastre funziona
- [ ] Sono accettate anche assi/lastre di ulivo, mandorlo e ciliegio
- [ ] L'ascia rompe correttamente il blocco e lo restituisce
- [ ] Nessun crash su mondo/server senza Rustic
