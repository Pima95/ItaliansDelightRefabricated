# Texture: mattarello e impasto della pizza

Aggiornamento del 10 ottobre 2026, branch `feature/pizza-1.0.0`.

## Asset e applicazione

| PNG in `src/main/resources/assets/italiansdelight/textures/` | Uso | Dimensioni | Alpha |
| --- | --- | --- | --- |
| `item/rolling_pin.png` | Icona del mattarello, modello `minecraft:item/handheld` | 16×16 | 0 sullo sfondo, 255 sull'utensile |
| `block/pizza_dough_top.png` | Facce superiori e inferiori dell'impasto | 16×16 | 255 ovunque |
| `block/pizza_dough_side.png` | Quattro lati e particelle di rottura dell'impasto | 16×16 | 255 ovunque |

Nessun pixel semitrasparente. Il mattarello segue il chiarimento dell'autore: normale texture item come una spada, con sfondo trasparente. Legno caldo, due manici e corpo centrale più largo; la texture entra nel modello handheld esistente.

L'impasto crudo usa toni crema e avorio, con piccoli dettagli di farina e impastatura. La superficie rimane abbastanza neutra per i futuri condimenti. Tutti e cinque gli stati di `pizza_dough` condividono gli stessi materiali: la progressione è data dall'allargamento e dall'appiattimento della geometria.

| Stato | Modello in `models/block/` | Forma |
| --- | --- | --- |
| 0 | `pizza_dough.json` | Palla iniziale |
| 1 | `pizza_dough_stage1.json` | Prima stesura |
| 2 | `pizza_dough_stage2.json` | Seconda stesura |
| 3 | `pizza_dough_stage3.json` | Terza stesura |
| 4 | `pizza_dough_stage4.json` | Base completamente stesa |

I modelli hanno UV esplicite con un texel per unità di modello; la superficie è ritagliata in modo continuo nei due livelli delle forme intermedie. Le facce laterali seguono l'orientamento UV standard di Minecraft. Il riferimento `particle` usa il materiale dei lati.

## Anteprima e generazione

Aprire [TEXTURE_PREVIEW.html](TEXTURE_PREVIEW.html) nel browser: contiene i PNG ingranditi senza interpolazione e un'anteprima ruotabile dei cinque modelli reali. È autonoma e fotografa gli asset di questo aggiornamento; l'illuminazione è semplificata.

Generazione con il tool integrato `image_gen`, una chiamata per ogni texture finale. Riferimenti: Wheat Dough e Iron Knife di Farmer's Delight Refabricated **26.2-3.6.28+refabricated**, oltre alla grattugia della mod. I prompt finali e i riferimenti sono in [TEXTURE_PROMPTS.json](TEXTURE_PROMPTS.json).

I sorgenti generati rimangono nella cartella `.codex/generated_images` del profilo Codex. Esportazione tecnica a 16×16 con campionamento nearest neighbour al centro dei texel; alpha dell'item ridotto a 0/255. Nessuna interpolazione o sfumatura trasparente nell'asset finale.

## Verifica

Controllati dimensioni e alpha di tutti i PNG, riferimenti dei cinque blockstate, assegnazione delle sei facce, limiti e scala delle UV. Geometrie dell'impasto confrontate con quelle precedenti. Verificata anche la presenza degli asset nel JAR, con confronto byte per byte. Build offline riuscita per Minecraft 26.2.

La prova in gioco resta da svolgere:

- [ ] Mattarello riconoscibile nell'inventario, in mano destra e sinistra; nessun alone.
- [ ] Palla e quattro stati successivi con texture corrette su superficie e lati, senza nero/viola o sandstone.
- [ ] Quattro clic manuali e un clic con mattarello mostrano la stessa base finale.
- [ ] Particelle di rottura color crema coerenti con l'impasto.
- [ ] Impasto correttamente appoggiato anche su slab superiore, scala capovolta, botola superiore chiusa e incudine.

JAR da provare: `build/libs/italiansdelight-0.4.0.jar`.
