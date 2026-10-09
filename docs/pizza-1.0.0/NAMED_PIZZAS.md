# Pizze prestabilite — proposte per la 1.0.0

> **Stato: proposte, non ricette approvate.** Il meccanismo di riconoscimento delle combinazioni è CONFERMATO; l'elenco preciso di pizze e ingredienti è ancora da definire.

## Comportamento richiesto

Le ricette riconosciute non devono impedire la creatività:
- combinazione che corrisponde a una definizione -> **nome ufficiale**;
- combinazione non registrata -> **nome dinamico con i condimenti**;
- ordine di inserimento degli ingredienti **irrilevante** per il nome;
- ingrediente aggiunto oltre alla ricetta riconosciuta -> di norma **personalizzata**, salvo una definizione più specifica.

Esempi illustrativi:
- Pomodoro + Mozzarella + Basilico -> **Pizza Margherita**.
- Pomodoro + Mozzarella + Basilico + Speck -> **Pizza con Pomodoro, Mozzarella, Basilico e Speck**, se non esiste un gusto definito che corrisponda.
- Mozzarella + Gorgonzola + Funghi -> **Pizza con Mozzarella, Gorgonzola e Funghi**, se non esiste ricetta riconosciuta.

## Lista iniziale da discutere

| Nome suggerito | Ingredienti indicativi | Questione aperta |
| --- | --- | --- |
| Margherita | salsa di pomodoro, mozzarella, basilico | tag/quantità precisi |
| Marinara | salsa di pomodoro, aglio, origano | origano non necessariamente disponibile |
| Quattro Formaggi | quattro formaggi specifici o equivalenti | quali formaggi, base bianca/rossa |
| Funghi | funghi, salsa/formaggio da definire | quale variante canonica |
| Prosciutto | prosciutto e altri ingredienti | prosciutto cotto o crudo |
| Salame / Diavola | salsa, mozzarella, salame | salame piccante vs normale |
| Capricciosa | combinazione di funghi, prosciutto, verdure ecc. | ingredienti disponibili |
| Vegetariana | salsa/formaggio e verdure | insieme minimo e varianti |

Non introdurre nuovi ingredienti (origano, olive, pesto ecc.) automaticamente solo perché compaiono come esempi culinari.

## Matching proposto

- Match esatto di default sulle quantità/categorie quando necessario.
- Tag solo se la corrispondenza preserva il gusto: non ogni formaggio generico deve diventare mozzarella per una Margherita.
- Priorità deterministiche fra ricette sovrapposte.
- Numero di condimenti massimo e valori nutrizionali da approvare.
- Nomi tradotti EN/IT con ingredienti completi nel tooltip.

## Bilanciamento da affrontare

- Costi effettivi degli ingredienti già presenti (formaggi e salumi richiedono lavorazioni).
- Fame/saturazione e tetto massimo.
- Eventuali effetti come Nourishment soltanto se coerenti con Farmer's Delight.
- Consumo come pizza intera o fette: funzionalità proposta, non ancora confermata.
