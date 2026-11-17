# Revisione delle texture dei crate — confronto con tutti i crate Farmer's Delight Refabricated (Minecraft 26.3)

## Riferimento verificato

Repository upstream: https://github.com/MehVahdJukaar/FarmersDelightRefabricated
Branch originale: `fabric/latest/26.3`, commit `1b85d66f5e2aba88592d7f4fb203d582f4d3cbe3`.
Directory originale: `src/main/resources/assets/farmersdelight/textures/block/`.

Sono state esaminate tutte le **sei varianti di contenuto alimentare** presenti in questa versione (sia `_crate_top.png` sia `_crate_side.png`), insieme alle relative caratteristiche dell'alimento:

| Crate upstream | Alimento | Principio illustrativo visto dall'alto |
|---|---|---|
| `beetroot_crate` | Barbabietola | 4/5 radici tonde, asimmetriche, con ombre rosso scuro, riflessi rosa e pochi dettagli di foglie |
| `cabbage_crate` | Cavolo | Grandi superfici tonde di foglie sovrapposte, più toni verdi e pochi riflessi chiari |
| `carrot_crate` | Carota | Radici allungate e sovrapposte in varie direzioni, dal colore arancio vivo con estremità verdi |
| `onion_crate` | Cipolla | Bulbi color crema disposti in 2x2 irregolare, piccoli ciuffi verdi e luce concentrata |
| `potato_crate` | Patata | Tubercoli giallo/marroni tondeggianti, senza foglie, ombre sui confini fra elementi |
| `tomato_crate` | Pomodoro | Frutti rossi tondeggianti, riflessi puntuali e piccoli sepali verdi, parzialmente sovrapposti |

**Regole comuni accertate:** tutti i top sono 16×16; stessi pixel della cornice in legno; apertura interna 12×12 completamente occupata; più alimenti parzialmente coperti, mai una semplice ripetizione in scala dell'icona item; 3–5 tinte per ciascun materiale più pochi pixel di peduncolo o fogliame; luci direzionate e ombre profonde tra i frutti; disposizione volutamente asimmetrica. Le texture laterali mantengono l'identica struttura di assi della cassa, distinguendosi per frutti nei vuoti fra le assi.

## Modifiche applicate

Le precedenti texture superiori del mod mostravano pochi grappoli/peperoncini grandi appoggiati su un fondo quasi nero, con troppo spazio vuoto o un effetto di mosaico.

1. **Uva rossa:** reinterpretazione della composizione di `beetroot_crate_top` per quattro grappoli di acini viola, con risalti e separazioni individuali, ombre più scure e pochi raspi.
2. **Uva bianca:** geometria parallela all'uva rossa, palette distinta giallo-dorata ricavata dall'item `white_grape.png`.
3. **Peperoncini:** disposizione e ombre del `carrot_crate_top` trasformate in frutti rossi con corpo curvo, punta, calice verde e sovrapposizioni.
4. **Peperoncini fantasma:** stessa impostazione ma distinta palette arancione più luminosa.

I quattro PNG sostituiti sono esclusivamente `*_crate_top.png`. Gli ID dei blocchi, i modelli, le ricette, i lati, le piante e gli item rimangono invariati. Ogni PNG è 16×16 RGB completamente opaco, senza pixel trasparenti, ed è diverso dalle versioni precedenti. I bordi esterni `x∈{0,1,14,15} or y∈{0,1,14,15}` coincidono esattamente con il `tomato_crate_top.png` upstream.

Verifica visiva necessaria nel client Minecraft a dimensione reale (non soltanto ingrandimento del PNG). Si possono provare con:

```mcfunction
/give @p italiansdelight:grape_crate
/give @p italiansdelight:white_grape_crate
/give @p italiansdelight:chili_pepper_crate
/give @p italiansdelight:ghost_pepper_crate
```

## Attribuzione degli asset di riferimento

I disegni sono adattamenti pixel-art delle texture `beetroot_crate_top.png` e `carrot_crate_top.png` e riprendono la cornice originale del `tomato_crate_top.png`. Copyright (c) 2020 vectorwing, Farmer's Delight Refabricated, con licenza MIT. Gli adattamenti originali della mod vengono distribuiti mantenendo questo riconoscimento della sorgente. Il testo MIT originale è conservato qui sotto per ottemperare alle condizioni della licenza.

```text
MIT License

Copyright (c) 2020 vectorwing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

=============================================================================
This software includes code from the NeoForge project
https://github.com/neoforged/NeoForge/
For specifically InvWrapper and ItemStackHandler logic.

NeoForge is licensed under the LGPL 2.1 license.
You may read the license here:
https://github.com/neoforged/NeoForge/blob/1.21.x/LICENSE.txt

Some files may include others' licenses. Please read the top of the file
for information on other licenses.
=============================================================================
```
