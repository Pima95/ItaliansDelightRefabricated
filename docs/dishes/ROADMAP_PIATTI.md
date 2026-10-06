# Italian's Delight — Piatti

Documento di riferimento per i piatti completi introdotti o pianificati.

Questa prima integrazione riutilizza gli ingredienti delle filiere dei
formaggi e dei salumi senza introdurre nuovi blocchi funzionali.

## Implementati

| Piatto | ID | Preparazione | Ingredienti principali |
|---|---|---|---|
| Pasta alla Carbonara | `pasta_alla_carbonara` | Cooking Pot | Pasta, Guanciale, Egg, Pecorino Romano |
| Pasta alla Gricia | `pasta_alla_gricia` | Cooking Pot | Pasta, Guanciale, Pecorino Romano |
| Pasta all'Amatriciana | `pasta_all_amatriciana` | Cooking Pot | Pasta, Guanciale, Tomato Sauce, Pecorino Romano |
| Pasta Speck e Gorgonzola | `pasta_with_speck_and_gorgonzola` | Cooking Pot | Pasta, Speck, Gorgonzola, Cream |
| Risotto Speck e Gorgonzola | `risotto_with_speck_and_gorgonzola` | Cooking Pot | Rice, Speck, Gorgonzola, Cream |
| Risotto al Parmigiano | `risotto_with_parmigiano` | Cooking Pot | Rice, Parmigiano Reggiano, Cream |
| Insalata di Mozzarella | `mozzarella_salad` | Crafting shapeless | Bowl, Mozzarella, Tomato |
| Risotto ai Funghi | `risotto_with_mushroom` | Cooking Pot | Rice, Mushrooms, Cream |
| Pasta al Sugo | `pasta_with_tomato_sauce` | Cooking Pot | Pasta, Tomato Sauce |
| Risotto al Sugo | `risotto_with_tomato_sauce` | Cooking Pot | Rice, Tomato Sauce |

### Scelte di ingredienti

- Carbonara, Gricia e Amatriciana usano **Guanciale Slice**.
- Carbonara non usa Cream.
- Pecorino e Parmigiano vengono usati nella forma grattugiata.
- I piatti con Gorgonzola usano il **Gorgonzola Wedge**.
- Il Risotto ai Funghi usa `#c:mushrooms`, quindi accetta funghi compatibili
  aggiunti da altre mod.
- L'Insalata di Mozzarella usa `#c:crops/tomato` per compatibilità con
  Farmer's Delight e altre mod.

## Balance v1 dei piatti

| Piatto | Nutrition | Saturation modifier | Nourishment |
|---|---:|---:|---:|
| Pasta al Sugo | 8 | 0.7 | 1 min |
| Risotto al Sugo | 8 | 0.7 | 1 min |
| Insalata di Mozzarella | 8 | 0.6 | 1 min |
| Risotto al Parmigiano | 10 | 0.7 | 1 min |
| Pasta alla Gricia | 12 | 0.8 | 3 min |
| Risotto ai Funghi | 12 | 0.8 | 3 min |
| Pasta alla Carbonara | 14 | 0.75 | 5 min |
| Pasta all'Amatriciana | 14 | 0.75 | 5 min |
| Pasta Speck e Gorgonzola | 14 | 0.75 | 5 min |
| Risotto Speck e Gorgonzola | 14 | 0.75 | 5 min |

Tutti i piatti completi sono bowl foods con stack massimo **16**, restituiscono
una bowl dopo il consumo e applicano **Nourishment** con probabilità 100%.

### Criterio di bilanciamento

La scala segue direttamente Farmer's Delight Refabricated 26.2:

- **Short (1 min)** per piatti semplici da 8–10 nutrition;
- **Medium (3 min)** per piatti completi da 12 nutrition;
- **Long (5 min)** per piatti ricchi da 14 nutrition.

Riferimenti principali della mod madre:

| Farmer's Delight | Nutrition | Saturation | Nourishment |
|---|---:|---:|---:|
| Bone Broth | 8 | 0.7 | 1 min |
| Ratatouille | 10 | 0.6 | 1 min |
| Pasta with Meatballs | 12 | 0.8 | 3 min |
| Mushroom Rice | 12 | 0.8 | 3 min |
| Fried Rice | 14 | 0.75 | 5 min |

Carbonara, Amatriciana e i piatti Speck/Gorgonzola entrano nel tier più alto
perché richiedono più ingredienti e, soprattutto, prodotti provenienti da
filiere di stagionatura. Gricia resta nel tier Medium, mentre Risotto ai Funghi
riprende intenzionalmente il profilo del Mushroom Rice di Farmer's Delight.

## Texture

Tutte le texture degli **8 nuovi piatti** sono ora presenti e collegate ai
rispettivi item/model JSON.

Texture già esistenti prima di questa fase:

- `mozzarella_salad.png`
- `risotto_with_mushroom.png`

Texture aggiunte il 5 ottobre 2026:

- `pasta_alla_carbonara.png`
- `pasta_alla_gricia.png`
- `pasta_all_amatriciana.png`
- `pasta_with_speck_and_gorgonzola.png`
- `risotto_with_speck_and_gorgonzola.png`
- `risotto_with_parmigiano.png`

Percorso comune:

`src/main/resources/assets/italiansdelight/textures/item/`

Documentazione grafica:

- [Texture piatti](TEXTURE_PIATTI.md)
- [Anteprima texture](TEXTURE_PIATTI.html)
- [Prompt e riferimenti](TEXTURE_PIATTI_PROMPTS.json)

## Da implementare in futuro

Piatti candidati per una fase successiva:

- Panino Salame e Provolone;
- Panino Prosciutto Crudo e Mozzarella;
- Bruschetta con Burrata e Prosciutto Crudo;
- Bresaola con Parmigiano;
- Tagliere Italiano;
- Pizza Speck e Gorgonzola;
- Pasta al Pesto;
- piatti basati su Basilico quando la filiera delle erbe sarà implementata;
- piatti con olive quando la filiera dell'olivo sarà implementata;
- ricette a base di vino quando la filiera dell'uva sarà implementata.

## Checklist

- [x] aggiungere le 6 texture mancanti;
- [x] eseguire `./gradlew.bat clean build`;
- [x] controllare gli 8 nuovi item nel creative tab;
- [x] controllare tutte le ricette in JEI;
- [x] verificare il ritorno della bowl dopo il consumo;
- [x] verificare la durata di Nourishment in survival;
- [x] verificare nutrition e saturation in survival;
- [ ] eseguire un controllo finale delle ricette prima della PR.
