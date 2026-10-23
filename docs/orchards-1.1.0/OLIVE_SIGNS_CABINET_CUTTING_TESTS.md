# Orchard Update 1.1.0 — Cartelli, cabinet e Cutting Board

**Branch:** `feature/orchards-1.1.0` · **Stato:** implementati nel branch; **build Java 25 e controlli statici superati** ([CI 40042164597](https://github.com/Pima95/ItaliansDelightRefabricated/actions/runs/40042164597)); **collaudo in Minecraft ancora da eseguire**. La verifica della botola d'ulivo è indipendente.

## Cosa è stato implementato

- `italiansdelight:olive_sign` — **un item** che piazza il cartello in piedi e il cartello a parete, con editing del testo vanilla, grafica nuova **Minecraft 26.2**, ricetta e drop.
- `italiansdelight:olive_hanging_sign` — **un item** che piazza cartello appeso al soffitto o a parete, grafica Minecraft 26.2, ricetta e drop.
- `italiansdelight:olive_cabinet` — cabinet Farmer's Delight di legno d'ulivo con le quattro texture originali già presenti nel repository e blocco entity condiviso con i cabinet FD. **Stessi 27 slot** previsti dal sistema originale, apertura/chiusura, menu, redstone, persistenza; nessuna logica inventario duplicata.
- `farmersdelight:cutting` — due ricette di scortecciatura: `olive_log → stripped_olive_log + farmersdelight:tree_bark`; `olive_wood → stripped_olive_wood + farmersdelight:tree_bark`. Usano `axe_strip` e suono vanilla.

## Regole sulle texture

- **Nuovi file generati:** `textures/block/olive_sign.png`, `textures/block/olive_hanging_sign.png`, `textures/item/olive_hanging_sign.png`. Hanno esattamente **16×16 RGBA**, pixel con alfa soltanto 0/255, senza antialiasing, palette derivata da `olive_planks.png` e `olive_sign.png`.
- **File non modificati:** `textures/entity/sign/olive.png` e `textures/entity/sign/hanging/olive.png` (vecchio formato pre-26.2); `textures/item/olive_sign.png`; i quattro `olive_cabinet_*.png`.
- **Escluse:** tutte le vecchie texture Rustic identificate. I vecchi PNG legacy restano disponibili nel repository a fini storici.

## Checklist di accettazione manuale

- [ ] **Inventario Creativa:** compaiono Cartello d'Ulivo, Cartello Appeso d'Ulivo e Armadietto d'Ulivo, ciascuno una sola volta; icone distinguibili.
- [ ] **Cartello normale:** piazzamento a terra nelle 16 orientazioni e a parete sui quattro lati; testo editabile avanti/retro, colori, luminescenza, persistenza dopo save/load e drop corretto.
- [ ] **Cartello appeso:** posa al soffitto con catene e appoggio laterale, orientamento e collisioni come vanilla; testo avanti/retro, apertura dell'editor, salvataggio e drop.
- [ ] **Grafica cartelli:** nessun viola/nero; palette coerente con le assi d'ulivo, assenza di sbavature, nessuna texture vecchia Rustic; attenzione anche all'icona del cartello appeso.
- [ ] **Cabinet — aspetto e interazione:** texture frontale chiusa/aperta, tutte le direzioni, suoni e comportamento di apertura/chiusura come il cabinet FD.
- [ ] **Cabinet — inventario:** 27 slot, inserimento ed estrazione, nome/indicatore redstone, recupero di cabinet e contenuto senza perdita/duplicazione in Sopravvivenza.
- [ ] **Cabinet — persistenza e automatismi:** ripristino oggetti dopo riavvio del mondo; hopper laterali/sotto e, se previsti dal comportamento FD, trasporto di oggetti tramite Fabric Transfer API.
- [ ] **Cutting Board — tronco:** con `olive_log` posato sulla tavola e un'ascia si ottengono `stripped_olive_log` + un `farmersdelight:tree_bark`. Suono e consumo durabilità come FD.
- [ ] **Cutting Board — legno:** con `olive_wood` + ascia si ottengono `stripped_olive_wood` + una corteccia, senza duplicazioni.
- [ ] **JEI:** ricette della Cutting Board raggiungibili con R/U dal tronco, dal legno scortecciato e dalla corteccia; registrazioni normali di crafting per cartelli e armadietto.
- [ ] **Regressioni:** controllo botola e legni già presenti, 13 scaffali stagionatura e vecchi cabinet Farmer's Delight invariati.
- [ ] **Server dedicato:** log senza errori, cartelli con testo e cabinet persistenti e sincronizzati nel multiplayer.

**IMPORTANTE:** la build GitHub valida sintassi, risorse e registrazioni; non equivale all'esecuzione dei test di cui sopra.
