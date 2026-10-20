# Riordino scheda Creativa — Italian's Delight / Pizza 1.0.0

**Data:** 20 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** implementato; verifica CI e controllo visivo in Minecraft ancora da confermare.

## Decisioni di compatibilità

Si mantiene **una sola scheda Creativa**, la stessa `italiansdelight:italiansdelight_group` con il titolo e l'icona originali. L'intervento riordina l'elenco in `ModItemGroups.java` senza introdurre ulteriori tab, item fittizi, stack pre-conditi o modifiche ai dati. Tutti i **97 elementi** precedentemente presenti rimangono accessibili una sola volta: **79 item** registrati e **18 blocchi** con forma utilizzabile nella scheda. I seguenti sei blocchi tecnici rimangono giustamente esclusi: `potted_cardoon`, `pizza_dough`, `cooked_pizza` (blocchi interni), `cheese_hook_occupied_space`, `aging_cheese`, `salt_cauldron`. Gli item `raw_pizza`, `cooked_pizza` e `pizza_slice` restano nella scheda, come nella versione precedente.

## Nuovo ordine

1. **Attrezzature pizza:** Pizza Oven, mattarello e pala.
2. **Produzione formaggi:** Cheese Vat, uncino, grattugia.
3. **Stagionatura:** tutte le 12 varianti in legno delle cheese aging rack.
4. **Colture e vegetali:** cardo, basilico, basilico selvatico, sale, pomodoro a fette, carote a fette, patate a fette.
5. **Materie prime lattiero-casearie:** latte di pecora, siero, caglio, cagliata, panna, coltura erborinata, aceto di mele.
6. **Latticini freschi:** mozzarella, fette, bocconcino, burrata, ricotta, mascarpone.
7. **Formaggi stagionati:** Parmigiano, Pecorino, Gorgonzola, Provolone e Scamorza, con i prodotti della stessa filiera adiacenti.
8. **Salumi:** prosciutto, salame, mortadella, pancetta, guanciale, bresaola, coppa, speck, con preparazione, prodotto intero e fette vicini.
9. **Pizze:** pizza cruda, pizza cotta e spicchio generico, subito dopo i salumi.
10. **Piatti pronti:** paste, risotti, insalata di mozzarella.

Le categorie sono **gruppi logici consecutivi**, non intestazioni visibili o pulsanti. Non si inseriscono spazi vuoti, stack fittizi né modifiche all'inventario vanilla.

## Controlli automatizzati

Lo script `.github/scripts/check_creative_tab.py` viene eseguito nella workflow `pizza-build.yml` prima di Gradle. Controlla l'elenco degli item e blocchi registrati contro l'elenco Creativa; segnala item mancanti, blocchi tecnici esposti, duplicati o la creazione accidentale di un'altra callback Creativa.

## Test in client

- [ ] Aprire il tab Italian's Delight in Creativa; la prima sequenza mostra **Pizza Oven → mattarello → pala**.
- [ ] Confermare che grattugia, uncino, Cheese Vat e tutte le 12 rack seguano nel nuovo ordine.
- [ ] Trovare basilico, cardo, sale, pomodoro, carota e patata a fette raggruppati.
- [ ] Scorrere le filiere dei formaggi senza oggetti mancanti o duplicati.
- [ ] Controllare tutti gli otto salumi con gli intermedi immediatamente vicini ai prodotti finali e alle rispettive fette.
- [ ] Dopo **Speck a fette**, verificare **pizza cruda → pizza cotta → spicchio**, seguiti dai piatti pronti (paste, risotti, insalata).
- [ ] Usare la ricerca Creativa: i nuovi item `carrot_slice` e `potato_slice` devono essere ancora trovabili, e JEI deve funzionare come prima.
- [ ] Nessuno dei blocchi tecnici o delle strutture immateriali deve diventare ottenibile dall'inventario Creativa.
- [ ] Sopravvivenza e server dedicato: ricette, valori nutrizionali, item, salvataggi e meccaniche pizza invariati.

**Prossima fase approvata:** texture dedicate dei gusti di pizza riconosciuti dal sistema. La riorganizzazione Creativa non ha introdotto ancora nuovi modelli/texture di pizze con nomi fissi: saranno una fase separata da testare in gioco.
