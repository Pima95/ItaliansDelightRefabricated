# Revisione del codice e delle ricette — 23 ottobre 2026

**Repository:** `Pima95/ItaliansDelightRefabricated`  
**Branch:** `feature/pizza-1.0.0` (nessun branch aggiuntivo, `main` invariata).  
**Ambito:** ricerca sistematica di doppie ricette, tag incoerenti, differenze fra varianti della stessa produzione, restituzione dei contenitori e problemi di manutenzione/ottimizzazione. Il codice precedente di Pizza 1.0.0 viene conservato salvo correzioni motivate.

## Copertura reale e metodo

- **114 file Java**: scansione automatica di sorgenti per TODO/FIXME, API client nel codice comune, eccezioni esplicite e rimborsi di ciotole.
- **590 file JSON**: parsing integrale per controllare sintassi, incluse ricette, modelli, lingua e configurazioni.
- **94 ricette JSON** (dopo il consolidamento): ricerca di contenuti esattamente identici, collisioni fra ingredienti/risultati, doppie ricette con sola differenza fra secchio e bottiglia d'acqua; controllo di tutti i simboli nelle ricette `crafting_shaped`.
- Controlli mirati su `ModItems`, `CheeseVatRecipe`, `CheeseVatBlockEntity`, tag Cardoon, JEI Cheese Vat e sottotipi della pizza; confronto con i problemi già censiti negli audit [19 ottobre](CODE_AUDIT_2026-10-19.md) e [ottimizzazioni](OPTIMIZATION_REVIEW_2026-10-19.md).
- I controlli vivono in [`audit_project_consistency.py`](../.github/scripts/audit_project_consistency.py) ed entrano nel workflow GitHub Actions. **Non è una suite di test multiplayer o un profiler; non dimostra l'assenza assoluta di difetti.**

## Correzioni di incongruenze confermate

| Caso | Prima | Dopo | Variazione gameplay |
|---|---|---|---|
| Caglio | Due ricette distinte per acqua in secchio/bottiglia | Un solo `rennet.json` con `fabric:any` | **Nessuna intenzionale**: 300 tick, contenitore, quantità e Cardoon selvatico/coltivato invariati |
| Bocconcino | `bocconcino_from_water_bucket` e `bocconcino_from_water_bottle` | Un solo `bocconcino.json`, ingrediente acqua alternativo | **Nessuna intenzionale**: 2 bocconcini, 360 tick, `#c:foods/curd` invariati |
| Provolone fresco | `fresh_provolone_from_water_bucket` e `fresh_provolone_from_water_bottle` | Un solo `fresh_provolone.json` | **Nessuna intenzionale**: 440 tick, sale e `#c:foods/curd` invariati |
| Commento obsoleto `ModItems` | Citava come TODO la grafica delle pizze già approvata | Commento aggiornato al sistema `ITEM_MODEL` dinamico | Nessuna: sola manutenzione |

La Cheese Vat elabora gli ingredienti senza dipendere dalla loro posizione (algoritmo shapeless) e usa le proprietà di `ItemStack.getCraftingRemainder()` per espellere secchio vuoto e bottiglia vuota a fine lavorazione. Il tag comune `#c:crops/cardoon` include entrambi gli item, senza dover copiare le ricette.

**Compatibilità dei salvataggi:** i vecchi identificatori delle ricette Bocconcino/Provolone con suffissi `_from_water_*` sono stati sostituiti da ID più brevi. Gli item già prodotti restano invariati; **una Cheese Vat che stava cuocendo una vecchia ricetta potrebbe azzerare il progresso parziale**, perché `cookingRecipe` salva la chiave della ricetta. È un aggiornamento del solo progresso, non una conversione degli oggetti. Verificarlo su una copia di un vecchio mondo.

## Differenze simili ma intenzionali: non cambiare

- **Ricotta e panna:** condividono latte e ciotola, ma differiscono per la richiesta di siero e per la selezione della ricetta. Unificarle distruggerebbe questa distinzione.
- **Mozzarella e Scamorza fresca:** ingredienti simili, ma la Scamorza richiede il `lead` come contenitore e ha un diverso processo di asciugatura. Non sono doppioni.
- **Smelting e Smoking:** gli oggetti affumicati (Scamorza, Mortadella, Speck) hanno ricette distinte perché vanilla usa forni differenti e tempi differenti (400/200 tick); anche la pizza ha serializzatori diversi per conservarne i componenti.
- **Latte di pecora:** l'item `sheep_milk_bucket` dichiara già `.craftRemainder(Items.BUCKET)`; la ricetta delle quattro bottiglie usa questo stesso item, e non ha bisogno di un rimborso manuale come il sistema pizza.
- **Ciotole delle pizze:** `PizzaCraftingRecipe` restituisce il contenitore nella griglia e salva `bowlReturnedOnCrafting` per impedire doppio rimborso dalla cottura o dalla rimozione del topping. È un'eccezione voluta rispetto ai normali residui vanilla.

## Segnalazioni del controllo automatico

- **Nessun duplicato JSON esatto**, nessuna coppia acqua secchio/bottiglia ancora separata nella Cheese Vat e nessun errore sintattico JSON.
- La coppia **Ricotta / panna** appare come possibile sovrapposizione di input nell'audit, ma **non è un bug** per il criterio di scelta basato sul siero.
- Il punto sulle ciotole del crafting è un promemoria deliberato: lo script dedicato `check_pizza_crafting_bowls.py` ne controlla già il flag persistente e il rimborso unico.
- Trovato un commento `TODO` sulla grafica vecchia già superata: eliminata l'informazione obsoleta senza alterare texture o modelli.

## Rischi e test ancora necessari

1. **Priorità alta — runtime dei tre prodotti con acqua:** provare ciascuno con secchio e bottiglia d'acqua, scambiando la posizione degli ingredienti e includendo Cardoon coltivato nel caglio. Verificare tempo, quantità, resti in casi di inventario pieno, blocchi della macchina, JEI e `/reload`. Le ricette `fabric:any` sono verificate staticamente e compilano, **ma la CI non avvia un mondo Minecraft e non prova i cicli completi**.
2. **Priorità alta — salvataggi prima della modifica:** test su una Cheese Vat con Bocconcino/Provolone in lavorazione, per osservare il possibile reset del progresso quando cambia l'ID ricetta; verificare anche la ripresa quando il chunk viene ricaricato.
3. **Priorità media — multiplayer e carichi:** transazioni contemporanee di Cheese Vat, tagli di Pizza mista su Cutting Board, due giocatori e inventari/hopper saturi. I controlli statici non equivalgono a test di concorrenza.
4. **Priorità media — compatibilità con datapack:** il registro `PizzaToppingMappings` lato client e i tag personalizzati degli ingredienti richiedono una verifica insieme a mod/datapack esterni (AUD-005).
5. **Ottimizzazioni da misurare, non applicate alla cieca:** costo delle scansioni Cheese Vat, gestione dei chunk del sale e rendering dei topping (AUD-009/010, OPT-001/003/005/008). Le precedenti ottimizzazioni già provate non sono state alterate.

## Esito

Le duplicazioni equivalenti individuate sono state consolidate. Non sono state registrate texture nuove, modificati i valori di gameplay, né effettuati merge su `main`. Le difformità che restano richiedono **prove runtime o misurazioni**, anziché semplificazioni automatiche.
