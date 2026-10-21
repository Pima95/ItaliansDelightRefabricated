# Scheda Creativa — ordine definitivo proposto per Pizza 1.0.0

L'inventario Creativa di Italian's Delight usa **una sola scheda**, senza dividerla
in più tab o aggiungere elementi segnaposto. L'ordine è per utilizzo/produzione,
con sottogruppi continui (Minecraft non mostra separatori o titoli nelle celle).

| Ordine | Gruppo | Contenuto |
| --- | --- | --- |
| 1 | Postazioni | Cheese Vat, Hanging Hook, Pizza Oven |
| 2 | Stagionatura | Le 12 varianti di legno della Cheese Aging Rack |
| 3 | Strumenti | Grattugia, mattarello, pala per pizza |
| 4 | Coltivazioni e ortaggi | Cardoon, basilico, basilico selvatico, sale, pomodoro/carota/patata a fette |
| 5 | Lavorazione del latte | Latte di pecora, siero, caglio, cagliata, panna, coltura erborinata, aceto |
| 6 | Formaggi | Freschi e famiglie Parmigiano, Pecorino, Gorgonzola, Provolone, Scamorza |
| 7 | Salumi | Tutte le famiglie con preparazione, prodotto finale e relative fette vicini |
| 8 | Pizze crude | Item base, 18 gusti riconosciuti, **Pizza mista** |
| 9 | Pizze cotte | Item base, 18 gusti riconosciuti, **Pizza mista** |
| 10 | Spicchi | Item base e 18 gusti indipendenti |
| 11 | Piatti pronti | Pasta, risotti, insalata di mozzarella |

## Pizza mista in Creativa

Ci sono due nuovi **esempi** (non nuovi item registrati):
`PizzaCreativeSamples.createMixed(false/true)`. Ogni esempio contiene quattro
gusti diversi, precisamente **Margherita, Salame, Rossa e Quattro Formaggi
bianca**, nell'ordine dei quarti fisici 1–4. Gli originali dei topping sono
`ItemStack` reali della whitelist; copertura, numerazione delle applicazioni,
nome e dati di cottura/posizionamento seguono il meccanismo ordinario. Le icone
delle pizze intere sono quelle **originali della Margherita**; gli spicchi
mantengono invece le proprie texture e proprietà specifiche.

## Test in-game

- [ ] Aprire la scheda Italian's Delight e scorrere dall'alto: tutte le postazioni/rack/strumenti raggruppati, ingredienti → formaggi → salumi → pizze → piatti pronti.
- [ ] Verificare che non manchi alcun item della mod e che i blocchi solo tecnici siano assenti.
- [ ] Verificare esattamente **18 esempi crudi + Pizza mista**, **18 esempi cotti + Pizza mista**, **18 spicchi**, oltre ai tre item base.
- [ ] Confrontare Margherita e Pizza mista sia cruda sia cotta: le icone devono coincidere perfettamente, i **nomi devono essere diversi**.
- [ ] Piazzare la Pizza mista cotta e prelevare i quattro spicchi: Margherita, Salame, Rossa e Quattro Formaggi bianca, con sprite/nutrizione di ciascun quarto.
- [ ] Salvare, rientrare e verificare che i quattro quarti restino indipendenti. Provare anche la cottura della versione cruda con 20/30/60 secondi approvati.
- [ ] Provare la ricerca Creativa dei nomi italiani/inglesi e confermare assenza di duplicazioni dei tre Item ID reali.

Lo script `.github/scripts/check_creative_tab.py` verifica presenza/ordine
staticamente. Le texture 16×16 già approvate non sono state modificate.
