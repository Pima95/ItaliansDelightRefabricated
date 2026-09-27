# Italian's Delight — Checklist texture formaggi

Documento di riferimento per creare le texture degli item registrati nel branch
`feature/cheeses`.

## Regole comuni

Tutte le texture elencate qui sono sprite item Minecraft **16×16 px**.

Percorso base:

`src/main/resources/assets/italiansdelight/textures/item/`

Il nome del PNG deve coincidere esattamente con l'ID indicato nella tabella.
I file `assets/italiansdelight/items/*.json` e
`assets/italiansdelight/models/item/*.json` sono già presenti e puntano alle
texture indicate. Non sono stati creati PNG vuoti: se un file manca verrà
mostrata la normale missing texture di Minecraft.

Convenzioni dei nomi:

- `fresh_` = forma fresca/intermedia, prima di stagionatura o asciugatura;
- nome senza `fresh_` = prodotto finale/stagionato;
- `_wedge` = **spicchio** di una forma intera;
- `_slice` = fetta;
- `grated_` = formaggio grattugiato;
- `smoked_` = variante affumicata.

## Texture da creare

| ID item | Nome visualizzato IT | Significato grafico | File PNG |
|---|---|---|---|
| `bocconcini` | Bocconcini | Piccole mozzarelle/formaggi freschi singoli | `bocconcini.png` |
| `fresh_parmigiano_reggiano` | Forma Fresca di Parmigiano Reggiano | Forma appena prodotta, prima della stagionatura | `fresh_parmigiano_reggiano.png` |
| `parmigiano_reggiano` | Forma di Parmigiano Reggiano | Forma intera stagionata | `parmigiano_reggiano.png` |
| `parmigiano_reggiano_wedge` | Spicchio di Parmigiano Reggiano | Spicchio/cuneo ricavato dalla forma | `parmigiano_reggiano_wedge.png` |
| `grated_parmigiano_reggiano` | Parmigiano Reggiano Grattugiato | Porzione di formaggio grattugiato | `grated_parmigiano_reggiano.png` |
| `fresh_pecorino_romano` | Forma Fresca di Pecorino Romano | Forma appena prodotta, prima della stagionatura | `fresh_pecorino_romano.png` |
| `pecorino_romano` | Forma di Pecorino Romano | Forma intera stagionata | `pecorino_romano.png` |
| `pecorino_romano_wedge` | Spicchio di Pecorino Romano | Spicchio/cuneo ricavato dalla forma | `pecorino_romano_wedge.png` |
| `grated_pecorino_romano` | Pecorino Romano Grattugiato | Porzione di formaggio grattugiato | `grated_pecorino_romano.png` |
| `fresh_gorgonzola` | Forma Fresca di Gorgonzola | Forma prima della maturazione/erborinatura finale | `fresh_gorgonzola.png` |
| `gorgonzola` | Forma di Gorgonzola | Forma intera maturata | `gorgonzola.png` |
| `gorgonzola_wedge` | Spicchio di Gorgonzola | Spicchio/cuneo ricavato dalla forma | `gorgonzola_wedge.png` |
| `fresh_provolone` | Provolone Fresco | Forma prima della stagionatura | `fresh_provolone.png` |
| `provolone` | Provolone | Forma intera stagionata | `provolone.png` |
| `provolone_slice` | Fetta di Provolone | Fetta sottile ricavata dal Provolone | `provolone_slice.png` |
| `fresh_scamorza` | Scamorza Fresca | Scamorza appena formata, prima dell'asciugatura | `fresh_scamorza.png` |
| `scamorza` | Scamorza | Scamorza asciugata/pronta | `scamorza.png` |
| `smoked_scamorza` | Scamorza Affumicata | Variante affumicata della Scamorza | `smoked_scamorza.png` |
| `burrata` | Burrata | Burrata pronta al consumo | `burrata.png` |
| `mascarpone` | Mascarpone | Mascarpone servito nel suo contenitore/ciotola | `mascarpone.png` |

## Nota su `wedge`

`wedge` significa **spicchio**: una porzione a cuneo/triangolare ricavata da
una forma intera. È il suffisso definitivo per Parmigiano Reggiano, Pecorino
Romano e Gorgonzola.

Per il Provolone si usa invece `slice`, perché la porzione prevista è una
fetta e non uno spicchio.

## Stato

Registrazione item, traduzioni, creative tab e file item/model: **completati**.

Texture PNG elencate in questo documento: **da creare**.

La progettazione della stagionatura verrà affrontata nella fase successiva e
non richiede di rinominare questi item.
