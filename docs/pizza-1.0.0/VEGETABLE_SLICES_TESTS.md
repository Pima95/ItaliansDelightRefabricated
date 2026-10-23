# Pizza 1.0.0 — Carote e patate a fette: collaudo

**Implementazione:** 20 ottobre 2026 — branch `feature/pizza-1.0.0`.  
**Ambito:** due nuovi item, due ricette Cutting Board di Farmer's Delight Refabricated, texture e integrazione con la pizza. Solo l'inserimento dei due item nel tab Creativa esistente fa parte di questa correzione; il riordino completo e le texture delle pizze nominate rimangono esclusi.

## Comportamento approvato

| Origine | Tagliere + strumento | Risultato |
| --- | --- | --- |
| `minecraft:carrot` | coltello appartenente a `#c:tools/knife` | `4× italiansdelight:carrot_slice` |
| `minecraft:potato` | coltello appartenente a `#c:tools/knife` | `4× italiansdelight:potato_slice` |

Gli utensili hanno normale usura gestita dal tagliere. Gli ortaggi **interi** non sono validi topping; le fette sì, come **Vegetali**, senza item cotto distinto. La singola fetta di pizza riceve +1 fame e +0,5 saturazione per la **categoria** Vegetali, non per ogni singolo topping vegetale.

## File aggiunti

- `src/main/resources/assets/italiansdelight/textures/item/carrot_slice.png` — 16×16 PNG RGBA con pixel alpha soltanto 0 o 255.
- `src/main/resources/assets/italiansdelight/textures/item/potato_slice.png` — 16×16 PNG RGBA con pixel alpha soltanto 0 o 255.
- File `items/*.json` e `models/item/*.json` dei due item per la versione Minecraft 26.2.
- Due `recipe/cutting/*.json` nel formato già adottato dalla mod.
- Due sprite `textures/block/pizza_topping_sprites/*.png` — 12×12 con quattro varianti, usando l'attuale renderer dei solidi.
- **Conservate senza sovrascrittura** le originali `textures/block/pizza_toppings/carrot_slice.png` e `potato_slice.png`.

## Test da eseguire in Minecraft

- [ ] Verificare che `/give @s italiansdelight:carrot_slice 4` e `/give @s italiansdelight:potato_slice 4` producano item con nomi tradotti e texture corrette in inventario, senza pixel viola/neri.
- [ ] Con la Cutting Board di Farmer's Delight Refabricated e un coltello valido, una carota produce **esattamente 4** carote a fette; ripetere con una patata.
- [ ] Verificare con coltello danneggiato che la **durabilità** diminuisca secondo la normale meccanica FDR, senza consumare l'intero strumento come ingrediente.
- [ ] Verificare che anche JEI mostri entrambe le ricette Cutting Board, strumenti e risultati.
- [ ] Sulla pizza, carota/patata intere **non** possono essere applicate, non provocano la falsa animazione di piazzamento, non consumano l'item; le rispettive fette invece sì.
- [ ] Testare lo stesso topping ripetuto nello stesso quarto: deve essere respinto senza consumo, anche con dati equivalenti.
- [ ] Con carote e patate su quattro quarti separati: ogni sprite appare soltanto nel suo quarto, non collide con gli altri frammenti e non modifica la salsa sottostante.
- [ ] Con 6 topping differenti nel quarto, verificare l'anti-sovrapposizione e i limiti del renderer.
- [ ] Shift + clic per la rimozione LIFO: un singolo item originale viene rimborsato **solo dopo aver rimosso tutte le zone** della stessa applicazione, senza drop duplicati.
- [ ] Cuocere la pizza e raccogliere le fette: sia carote sia patate sono riconosciute come Vegetali, senza conversione in un item "cotto". Nomi e tooltip IT/EN corretti.
- [ ] Creare una pizza **con quattro quarti identici** di mozzarella a fette + patate a fette + speck a fette: il nome diventa **Patate e Speck**. Se soltanto un quarto ha quella combinazione, la pizza rimane assortita e la singola fetta è riconosciuta come Patate e Speck.
- [ ] Su server dedicato e dopo reload/chunk unload, verificare che le composizioni persistano, che il nome e la grafica restino coerenti e che non compaiano duplicazioni.
- [ ] Aprire la scheda Creativa **Italian's Delight**: le carote a fette e le patate a fette devono comparire subito dopo il pomodoro a fette; verificare che siano selezionabili, che le icone 16×16 siano corrette e che i nomi siano tradotti. Il riordino generale delle categorie resta una fase futura.

**Prima della fase successiva:** conferma visiva delle due icone e della grafica sulla pizza. Successivamente sistemare il menù Creativa e realizzare le icone specifiche delle pizze riconosciute, senza sostituire asset già approvati senza autorizzazione.
