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

## Valori alimentari iniziali

| Piatto | Nutrition | Saturation modifier |
|---|---:|---:|
| Pasta alla Carbonara | 10 | 0.9 |
| Pasta alla Gricia | 9 | 0.8 |
| Pasta all'Amatriciana | 10 | 0.8 |
| Pasta Speck e Gorgonzola | 10 | 0.9 |
| Risotto Speck e Gorgonzola | 10 | 0.9 |
| Risotto al Parmigiano | 9 | 0.8 |
| Insalata di Mozzarella | 7 | 0.6 |
| Risotto ai Funghi | 8 | 0.7 |

Tutti i piatti completi sono bowl foods con stack massimo **16** e restituiscono
una bowl dopo il consumo.

## Texture

Le texture di questi due piatti erano già presenti e vengono ora collegate alla
logica applicativa:

- `textures/item/mozzarella_salad.png`
- `textures/item/risotto_with_mushroom.png`

Le texture dei sei nuovi piatti **non vengono create automaticamente**. Devono
essere aggiunte manualmente con questi nomi esatti:

- `pasta_alla_carbonara.png`
- `pasta_alla_gricia.png`
- `pasta_all_amatriciana.png`
- `pasta_with_speck_and_gorgonzola.png`
- `risotto_with_speck_and_gorgonzola.png`
- `risotto_with_parmigiano.png`

Cartella:

`src/main/resources/assets/italiansdelight/textures/item/`

Fino all'aggiunta delle sei PNG, Minecraft mostrerà la missing texture per
quegli item. Modelli, registrazione, traduzioni e ricette sono già predisposti.

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

- [ ] aggiungere le 6 texture mancanti;
- [ ] eseguire `./gradlew.bat clean build`;
- [ ] controllare gli 8 nuovi item nel creative tab;
- [ ] controllare tutte le ricette in JEI;
- [ ] verificare il ritorno della bowl dopo il consumo;
- [ ] verificare nutrition e saturation in survival;
- [ ] eseguire un controllo finale delle ricette prima della PR.
