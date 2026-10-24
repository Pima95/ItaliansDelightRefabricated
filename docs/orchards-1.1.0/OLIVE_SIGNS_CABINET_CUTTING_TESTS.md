# Orchard Update 1.1.0 — Cartelli, cabinet e Cutting Board

**Branch:** `feature/orchards-1.1.0` · **Stato:** implementati nel branch; **build Java 25 e controlli statici superati** ([CI 40042164597](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40042164597)); **collaudo in Minecraft ancora da eseguire**. La verifica della botola d'ulivo è indipendente.

## Cosa è stato implementato

- `italiansdelight:olive_sign` — **un item** che piazza il cartello in piedi e il cartello a parete, con editing del testo vanilla, grafica nuova **Minecraft 26.2**, ricetta e drop.
- `italiansdelight:olive_hanging_sign` — **un item** che piazza cartello appeso al soffitto o a parete, grafica Minecraft 26.2, ricetta e drop.
- `italiansdelight:olive_cabinet` — cabinet Farmer's Delight di legno d'ulivo con le quattro texture originali già presenti nel repository e blocco entity condiviso con i cabinet FD. **Stessi 27 slot** previsti dal sistema originale, apertura/chiusura, menu, redstone, persistenza; nessuna logica inventario duplicata.
- `farmersdelight:cutting` — due ricette di scortecciatura: `olive_log → stripped_olive_log + farmersdelight:tree_bark`; `olive_wood → stripped_olive_wood + farmersdelight:tree_bark`. Usano `axe_strip` e suono vanilla.

## Regole sulle texture

- **Nuovi file generati:** `textures/block/olive_sign.png`, `textures/block/olive_hanging_sign.png`, `textures/item/olive_hanging_sign.png`. Le texture dei **blocchi sono ora 32×32**, l'item dell'appeso 16×16 e gli sfondi GUI 24×26/16×16; tutti i pixel hanno alfa soltanto 0/255, senza antialiasing, palette derivata da `olive_planks.png` e `olive_sign.png`.
- **File storici rimasti invariati:** `textures/entity/sign/olive.png` e `textures/entity/sign/hanging/olive.png` (formato precedente a 26.2), i quattro `olive_cabinet_*.png`. **Aggiornamento mirato successivo:** su richiesta dell'autore, `textures/item/olive_sign.png` è stata modificata soltanto nei 20 pixel della scritta stilizzata, per uniformarla alla scritta dell'item appeso; geometria, catene, editor e altre icone invariati.
- **Escluse:** tutte le vecchie texture Rustic identificate. I vecchi PNG legacy restano disponibili nel repository a fini storici.

## Checklist di accettazione manuale

- [ ] **Inventario Creativa:** compaiono Cartello d'Ulivo, Cartello Appeso d'Ulivo e Armadietto d'Ulivo, ciascuno una sola volta; icone distinguibili.
- [ ] **Cartello normale:** piazzamento a terra nelle 16 orientazioni e a parete sui quattro lati; testo editabile avanti/retro, colori, luminescenza, persistenza dopo save/load e drop corretto.
- [ ] **Cartello appeso:** posa al soffitto con catene e appoggio laterale, orientamento e collisioni come vanilla; testo avanti/retro, apertura dell'editor, salvataggio e drop.
- [ ] **REGRESSIONE GUI:** aprire l'editor del cartello normale e quello del cartello appeso e verificare che lo sfondo di entrambi non contenga più quadrati viola/neri; provare testo e persistenza.
- [x] **Geometria e testo dei cartelli:** dichiarati funzionanti dall'autore il 24 ottobre 2026; non confondere con approvazione delle palette e degli item.
- [ ] **RITOCCO TEXTURE DA VERIFICARE:** il palo del cartello normale deve richiamare la corteccia verde-grigia del tronco d'ulivo, come nel vecchio modello; il pannello del cartello appeso deve richiamare le venature chiare del tronco d'ulivo scortecciato, senza oscuramenti eccessivi, mantenendo catene e geometria. Verificare in gioco al sole e all'ombra.
- [ ] **Icona del cartello normale:** confermare che i segni neri stilizzati ricordino quelli dell'icona del cartello appeso, senza cambiamenti alla sagoma e al supporto; l'icona appesa resta invariata.
- [ ] **Cabinet — aspetto e interazione:** texture frontale chiusa/aperta, tutte le direzioni, suoni e comportamento di apertura/chiusura come il cabinet FD.
- [ ] **Cabinet — inventario:** 27 slot, inserimento ed estrazione, nome/indicatore redstone, recupero di cabinet e contenuto senza perdita/duplicazione in Sopravvivenza.
- [ ] **Cabinet — persistenza e automatismi:** ripristino oggetti dopo riavvio del mondo; hopper laterali/sotto e, se previsti dal comportamento FD, trasporto di oggetti tramite Fabric Transfer API.
- [ ] **Cutting Board — tronco:** con `olive_log` posato sulla tavola e un'ascia si ottengono `stripped_olive_log` + un `farmersdelight:tree_bark`. Suono e consumo durabilità come FD.
- [ ] **Cutting Board — legno:** con `olive_wood` + ascia si ottengono `stripped_olive_wood` + una corteccia, senza duplicazioni.
- [ ] **Crafting dei cartelli:** 6 assi + 1 bastone → 3 cartelli normali; 6 tronchi scortecciati + 2 catene di ferro → 6 cartelli appesi. Verificare sia nel banco 3×3 sia in JEI.
- [ ] **Crafting cabinet:** 6 lastre d'ulivo + 2 botole d'ulivo nel pattern Farmer's Delight → 1 armadietto. Controllare gruppo, output e compatibilità JEI.
- [ ] **Tutti i crafting:** provare le 17 ricette di legno d'ulivo elencate nella roadmap, non soltanto validarle con la CI. Registrare eventuali ricette invisibili o non riconosciute.
- [ ] **JEI:** ricette della Cutting Board raggiungibili con R/U dal tronco, dal legno scortecciato e dalla corteccia; registrazioni normali di crafting per cartelli e armadietto.
- [ ] **Regressioni:** controllo botola e legni già presenti, 13 scaffali stagionatura e vecchi cabinet Farmer's Delight invariati.
- [ ] **Server dedicato:** log senza errori, cartelli con testo e cabinet persistenti e sincronizzati nel multiplayer.

**IMPORTANTE:** la build GitHub valida sintassi, risorse e registrazioni; non equivale all'esecuzione dei test di cui sopra.
