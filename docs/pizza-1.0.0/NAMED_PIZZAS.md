# Pizze prestabilite — proposte per la 1.0.0

> **Stato: gusti/nomi di pizza ancora PROPOSTE; catalogo topping invece APPROVATO il 13 ottobre 2026.** Il riconoscimento delle combinazioni e' confermato, ma gli ingredienti consentiti sono esclusivamente i **29 tipi con item gia' registrati**, piu' due nuovi tipi approvati ma ancora da creare (carote e patate a fette), elencati in [INGREDIENT_CATALOG.md](INGREDIENT_CATALOG.md).

## Comportamento richiesto

Le ricette riconosciute non devono impedire la creatività:
- combinazione che corrisponde a una definizione -> **nome ufficiale**;
- combinazione non registrata -> **nome dinamico con i condimenti**;
- ordine di inserimento degli ingredienti **irrilevante** per il nome;
- **nessuna salsa obbligatoria** per preparare una pizza (anche bianca); la salsa puo' invece essere **richiesta da una specifica ricetta riconosciuta**;
- **una sola salsa per sottoarea fisica**, pur ammettendo salse diverse in parti distinte;
- **non piu' di 6 ingredienti diversi per quarto fisico**, contando l'eventuale salsa; nessuna divisione/riunione col mattarello permette di superare il limite;
- ingrediente aggiunto oltre alla ricetta riconosciuta -> di norma **personalizzata**, salvo una definizione più specifica.

Esempi illustrativi:
- Pomodoro + Mozzarella + Basilico -> **Pizza Margherita**.
- Pomodoro + Mozzarella + Basilico + Speck -> **Pizza con Pomodoro, Mozzarella, Basilico e Speck**, se non esiste un gusto definito che corrisponda.
- Mozzarella + Gorgonzola + Speck -> **Pizza con Mozzarella, Gorgonzola e Speck**, se non esiste ricetta riconosciuta. **Funghi non inclusi** nella whitelist 1.0.0 approvata.

## Lista iniziale da discutere

| Nome suggerito | Ingredienti indicativi | Questione aperta |
| --- | --- | --- |
| Margherita | salsa di pomodoro, mozzarella, basilico | tag/quantità precisi |
| Marinara | salsa di pomodoro, aglio, origano | **NON realizzabile nella whitelist 1.0.0** finche' aglio/origano non sono approvati; non sbloccarli automaticamente |
| Quattro Formaggi | quattro formaggi specifici o equivalenti | quali formaggi, base bianca/rossa |
| Funghi | funghi, salsa/formaggio da definire | **NON realizzabile nella whitelist 1.0.0**: funghi non approvati come topping |
| Prosciutto | **prosciutto crudo a fette Italian's Delight** e altri ingredienti ammessi | prosciutto cotto intero NON supportato; gusto e combinazione da approvare |
| Salame / Diavola | salsa, mozzarella, salame | salame piccante vs normale |
| Capricciosa | combinazione tradizionale con funghi ecc. | **NON realizzabile nella whitelist attuale**, da riprogettare solo con topping approvati |
| Vegetariana | salsa/formaggio e verdure | insieme minimo e varianti |

Non introdurre nuovi ingredienti (origano, aglio, funghi, olive, pesto ecc.) automaticamente solo perche' compaiono come esempi culinari. **Il fatto che una categoria alimentare sia definita non rende il relativo item un topping.** Carote e patate sono ammesse **solo nelle future versioni a fette, non come item interi**.

## Matching proposto

- Match esatto di default sulle quantità/categorie quando necessario.
- **Equivalenze cross-mod confermate nel comportamento**: item di mod diverse esplicitamente identificati come lo stesso ingrediente condividono anti-duplicazione, conteggio di un solo tipo nel limite di 6 e riconoscimento semantico del gusto. Si conserva l'ItemStack consumato per rimborsare l'originale. Tag soltanto quando la corrispondenza preserva il gusto: **non ogni formaggio generico diventa mozzarella** per una Margherita.
- Priorità deterministiche fra ricette sovrapposte.
- **Numero massimo gia' confermato: 6 ingredienti diversi per quarto fisico (salsa inclusa)**; valori nutrizionali e bilanciamento ancora da definire.
- Nomi tradotti EN/IT con ingredienti completi nel tooltip.

## Bilanciamento da affrontare

- Costi effettivi degli ingredienti già presenti (formaggi e salumi richiedono lavorazioni).
- **Fame/saturazione gia' confermate**, calcolate per categoria nutrizionale presente in ogni quarto, con tetto 6/6 (vedere [GAMEPLAY.md](GAMEPLAY.md)).
- **Nessun effetto speciale nella release 1.0.0**, incluso Nourishment, salvo futura decisione esplicita.
- **Servizio in quattro fette gia' confermato:** pizza cotta piazzabile nel mondo, clic destro a mano vuota per prelevare una fetta del quarto senza taglio preliminare.
- Resta aperta soltanto la **lista dei gusti riconosciuti per nome e il matching specifico delle ricette**, non la whitelist dei topping.
