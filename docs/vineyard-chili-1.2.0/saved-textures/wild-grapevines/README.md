# Viti selvatiche — copia di sicurezza delle texture (18 novembre 2026)

Questa cartella conserva **la versione attuale vista nel gioco** delle due viti selvatiche alte due blocchi, a uva rossa e a uva bianca, dopo il ritocco al fusto e alle foglie (commit sorgente: `355e9294cb3812ffa0d5b194c63a3799d2f9ef6e`).

I quattro file PNG sono copie **identiche byte per byte** degli sprite attivi al momento del salvataggio. Non sono risorse caricate da Minecraft: servono come riferimento e punto di ripristino quando si ridisegnano i grappoli.

## Motivazione del salvataggio

L'utente approva la nuova impostazione del fusto e delle foglie, ma ritiene che i grappoli abbiano sagome troppo uniformi, simili a cuori colorati. Questa coppia di texture deve restare intatta mentre si sperimentano grappoli meno simmetrici e più riconoscibili.

La prima proposta archiviata in precedenza (prima del cespuglio di rose) è stata sostituita **come riferimento preferito** da questa coppia. Le versioni più vecchie rimangono raggiungibili attraverso la normale cronologia Git.

## Files salvati

- `wild_grape_vine_bottom.png` / `wild_grape_vine_top.png` — uva rossa.
- `wild_white_grape_vine_bottom.png` / `wild_white_grape_vine_top.png` — uva bianca.

## Ripristino

Per ripristinare questa versione, copiare i quattro PNG sopra negli omonimi file di `src/main/resources/assets/italiansdelight/textures/block/`. Non cambiare modelli né ID: i modelli puntano già ai nomi canonici senza suffissi.

**Non modificare né eliminare questi backup nelle revisioni grafiche successive, salvo richiesta esplicita.**
