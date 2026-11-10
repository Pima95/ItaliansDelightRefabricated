# Inventario Creative e JEI — ordine 1.1.0 (novembre 2026)

Il tab **Italian's Delight** rimane unico. Non vengono aggiunti elementi segnaposto,
nuovi item ID o duplicati. I tre item Pizza (cruda, cotta, fetta) continuano
a essere rappresentati dai rispettivi preset reali, inclusa la Pizza mista.

## Ordine delle sezioni

1. **Postazioni e stoccaggio**: Pizza Oven (prima voce assoluta), Cheese Vat,
   tutte le Crushing Tub, tutte le Barrel, Cheese Hook, tutte le Cheese Aging Rack,
   **cinque crate** (olive, mandorle, ciliegie, basilico, cardo).
2. **Tre famiglie di legno**, in ordine ulivo, mandorlo, ciliegio da frutto.
3. **Utensili**: grattugia, mattarello, pala da pizza.
4. **Colture, piante selvatiche e frutti**: cardo, basilico, olive, semi,
   mandorle, mandorle schiacciate, ciliegie, semi di mela.
   I crate non interrompono più questo elenco di ingredienti.
5. **Condimenti e verdure preparate**.
6. **Liquidi**: olio d'oliva, succo di mela, succo di mela dorata, miele;
   latte, siero, caglio, aceto, e ingredienti caseari.
7. **Formaggi**, **salumi**, **pizze crude**, **pizze cotte**, **fette di pizza**,
   **piatti pronti**, **bruschette** e **dolci**.

## Regola unica per i legni

Ogni famiglia utilizza lo stesso ordine:

1. Tronco (log), legno intero (wood), tronco scortecciato, legno scortecciato
2. Tavole, scale, lastre
3. Staccionata, cancello, porta, botola
4. Pedana a pressione, pulsante
5. Cartello, cartello appeso
6. Armadietto (cabinet), mensola (shelf)
7. Barca, barca con baule
8. **Foglie e sapling, in fondo alla famiglia**

## Contenitori

Per ogni liquido che ha entrambi i formati, visualizzare sempre
**secchio seguito immediatamente da bottiglia**. La regola vale per olio
d'oliva, succo di mela, succo di mela dorata, latte di pecora, siero, caglio
e aceto di mele. Le voci già presenti mantengono i loro ID.

## JEI

La categoria e il catalyst della **Pizza Oven** sono registrati per primi.
Gli extra stack delle pizze sono inseriti con la stessa progressione della
Creative: pizze crude → Pizza mista cruda → pizze cotte → Pizza mista cotta →
fette.

La sidebar generale JEI è tuttavia riordinata dal **client JEI**, non dalla
nostra `ModItemGroups` in modo incondizionato: per vederla con l'ordine
Creative, la configurazione di JEI deve usare lo stage `CREATIVE_MENU`
(ad esempio `MOD_NAME, INGREDIENT_TYPE, CREATIVE_MENU`). Modifiche volontarie
all'ordinamento in JEI, filtri e segnalibri possono cambiare la presentazione.

## Pixel art

- `textures/item/chopped_almonds.png`: mandorle rotte con profilo più
  appuntito, buccia scura sottile e polpa chiara; meno vaste aree uniformi.
- `textures/item/tomato_slice.png`: fetta rossa con bordo, camere interne
  di polpa e semi distinguibili.
- 16×16 per entrambe; canale alpha solo 0 o 255.
- La texture della torta è **invariata**.

Il controllo `.github/scripts/check_creative_tab.py` blocca regressioni
di ordine, crate intercalati negli ingredienti, coppie di contenitori
invertite o famiglie di legno incomplete.
