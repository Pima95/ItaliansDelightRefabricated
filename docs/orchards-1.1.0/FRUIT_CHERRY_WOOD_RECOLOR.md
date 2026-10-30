# Ciliegio da frutto — revisione vanilla oak, 30 ottobre 2026

**Stato: proposta grafica implementata; attesa di approvazione in Minecraft.**
**Branch:** `feature/orchards-1.1.0`.

Su richiesta dell'autore sono state sostituite le cinque texture originali della
prima iterazione con un **recolor pixel-per-pixel della quercia di Minecraft 26.2**.
Il riferimento è il repository di asset Minecraft
[`InventivetalentDev/minecraft-assets`](https://github.com/InventivetalentDev/minecraft-assets/tree/26.2/assets/minecraft/textures/block),
ref `26.2`, per le seguenti texture:

| Texture della mod | Base vanilla |
| --- | --- |
| `fruit_cherry_planks.png` | `oak_planks.png` |
| `fruit_cherry_log.png` | `oak_log.png` |
| `fruit_cherry_log_top.png` | `oak_log_top.png` |
| `stripped_fruit_cherry_log.png` | `stripped_oak_log.png` |
| `stripped_fruit_cherry_log_top.png` | `stripped_oak_log_top.png` |

## Regole della revisione

- Dimensione 16×16, PNG RGBA senza semitrasparenza, senza filtri o antialiasing.
- **Nessuna geometria o pattern inventati:** ogni pixel conserva la propria
  coordinata e le stesse aree delle cinque immagini vanilla.
- Il recolor applica esclusivamente la gamma castano-ramata già scelta per il
  ciliegio da frutto, con scortecciato più chiaro della corteccia.
- Le due sezioni conservano identico il nucleo centrale 14×14 del disegno vanilla;
  soltanto il bordo esterno cambia nel passaggio alla versione scortecciata.
- Non modificati codice dei blocchi, crafting, tag, loot, Creative, generazione
  degli alberi, altri legni approvati o `main`.

## Controlli effettuati

I cinque PNG sono 16×16 e opachi; il confronto del nucleo delle due sezioni
è identico pixel per pixel, come nelle risorse oak vanilla.
La build GitHub Actions e soprattutto la verifica estetica in Minecraft
**sono verifiche distinte**: il colore non è ancora approvato dall'autore.

## Approvazione

- [ ] Verificare dal vivo i cinque blocchi isolati e accostati, compresi
  tronco orizzontale e verticale, parte superiore e scortecciatura con ascia
- [ ] Confermare se questa tonalità e le venature oak recolor sono definitive
- [ ] Solo allora avviare le texture e i blocchi derivati del nuovo legno
