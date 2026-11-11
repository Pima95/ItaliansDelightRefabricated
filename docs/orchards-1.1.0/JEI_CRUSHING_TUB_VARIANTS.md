# JEI — tutti i 15 Crushing Tub (Minecraft 26.2)

La schermata ricette del Crushing Tub è stata verificata in gioco dall'autore il 9 novembre 2026. Il layout `crushing_tub.png` (derivato da `aging.png` originale) e gli slot JEI **non devono essere modificati** senza una richiesta specifica.

## Questa integrazione

- Categoria: **Crushing Tub** / **Tino di pigiatura**, indipendente dal tipo di legno.
- Registrate come *crafting stations/catalysts* in JEI tutte e 15 le varianti realmente presenti, dalla quercia al legno di ulivo, mandorlo e ciliegio da frutto.
- Qualunque variante può aprire le stesse tre ricette di pigiatura in JEI con il comando **Usi**.
- Nessuna variazione a ricette, quantità in mB, collisioni, fisica dei liquidi, asset PNG o interazioni dei blocchi.

## Test

1. Cercare *Crushing Tub* in JEI e consultare gli Usi degli oggetti Oak, Spruce, Olive, Almond e Fruit Cherry. Devono mostrare la **stessa categoria generica** e le tre ricette.
2. Controllare che nome della categoria in italiano sia **Tino di pigiatura** e in inglese **Crushing Tub**.
3. Riaprire la ricetta della canna da zucchero: input a sinistra, **250 mB acqua** e **2 zuccheri** in slot distinti.
4. Verificare che le varianti interagiscano ancora con i medesimi ingredienti in gioco.

Test automatico: `python3 .github/scripts/check_crushing_tub_jei_variants_26_2.py`. La CI e il caricamento del client restano verifiche separate.
