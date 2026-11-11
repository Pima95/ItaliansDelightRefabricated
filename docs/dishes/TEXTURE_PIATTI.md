# Texture di pasta e risotti

Sei icone **16×16** in
`src/main/resources/assets/italiansdelight/textures/item/`.

[Anteprima con confronto Farmer's Delight](TEXTURE_PIATTI.html) ·
[Prompt completi e percorsi](TEXTURE_PIATTI_PROMPTS.json)

| File | Aspetto |
|---|---|
| `pasta_alla_carbonara.png` | Pasta giallo d'uovo, guanciale bruno e crema chiara |
| `pasta_alla_gricia.png` | Pasta avorio al pecorino, guanciale e ombre beige |
| `pasta_all_amatriciana.png` | Pasta al pomodoro rosso/arancio, guanciale e pecorino |
| `pasta_with_speck_and_gorgonzola.png` | Pasta cremosa, speck rosato e un accento verde-blu del Gorgonzola |
| `risotto_with_speck_and_gorgonzola.png` | Mucchietto compatto di riso chiaro, speck e Gorgonzola |
| `risotto_with_parmigiano.png` | Riso avorio dorato con scaglie chiare di Parmigiano |

## Coerenza con la mod madre

Riferimenti grafici estratti direttamente dal JAR di **Farmer's Delight
Refabricated 26.2-3.6.26+refabricated**: `pasta_with_meatballs`,
`pasta_with_mutton_chop`, `mushroom_rice` e `fried_rice`.
Sono stati usati come riferimenti visivi per imagegen integrato.

Le icone mantengono la ciotola di legno bassa, il bordo bruno scuro, la vista
a tre quarti, i riflessi ocra e i gruppi di pixel leggibili nello slot.
La base è allineata alla riga 13; le ultime due righe restano trasparenti,
come nei riferimenti. I condimenti distinguono i piatti senza introdurre
posate, vapore, piatti di ceramica o guarnizioni estranee alle ricette.

I riferimenti fotografici online sono serviti per colore e distribuzione dei
condimenti, senza essere incorporati nelle texture:

- [Piatti romani — La Cucina Italiana](https://www.lacucinaitaliana.it/gallery/amatriciana-carbonara-cacio-e-pepe-segreti-svelati/).
- [Pasta Gorgonzola e Speck — Dulcisss in forno](https://blog.giallozafferano.it/dulcisinforno/pasta-gorgonzola-e-speck/).
- [Risotto Speck e Gorgonzola — Luca Sessa](https://lucasessa.com/risotto-con-speck-e-gorgonzola/).
- [Risotto alla Parmigiana — The Guardian](https://www.theguardian.com/food/2021/mar/29/20-best-cheese-recipes-marcella-hazan-risotto-alla-parmigiana-risotto-with-parmesan).

## Esportazione e verifiche

Generazione tramite **imagegen integrato**, una sorgente per piatto.
Esportazione con nearest-neighbor, senza disegnare pixel tramite codice.
L'alpha viene convertito a **0 oppure 255**: sfondo completamente trasparente,
colori del piatto completamente opachi. Nessun pixel semitrasparente, alone
o interpolazione dei bordi nel PNG finale.

L'anteprima incorpora i sei PNG finali e le quattro icone originali di
confronto, mostrando sia l'ingrandimento sia la risoluzione nativa.

Controlli del **5 ottobre 2026**:

- [x] Sei PNG validi a 16×16, alpha esclusivamente 0/255, nessuna icona vuota.
- [x] Definizioni item, modelli, traduzioni e ricette collegate correttamente.
- [x] Build offline riuscita; i sei PNG nel JAR
  `build/libs/italiansdelight-0.2.0.jar` corrispondono alle sorgenti.
- [x] Confronto visivo con le icone originali nella pagina di anteprima.
- [ ] Verifica in Minecraft degli slot creativi, JEI e item tenuti in mano.
