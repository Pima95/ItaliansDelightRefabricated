# Roadmap — Pizza Update 1.0.0

## Stato

**Fase di progettazione documentata. Nessun codice di gameplay è stato ancora scritto in questo branch.**

## Checklist decisioni già CONFERMATE

- [x] Versione-obiettivo 1.0.0 con sistema di pizza personalizzabile.
- [x] Palla d'impasto posizionata direttamente sopra i blocchi, non sul Cutting Board.
- [x] Stesura sia a mano (clic destro) sia con mattarello.
- [x] Condimento diretto nel mondo con clic destro, senza GUI.
- [x] Preparazione tramite crafting table disponibile come alternativa.
- [x] Pizze riconosciute con nomi prestabiliti, altre con nomi dinamici.
- [x] Pizza Oven dedicato oltre a fornace e affumicatore vanilla.
- [x] Pizza Oven 1×1×1, **capienza una pizza alla volta**.
- [x] Pizza Oven accetta **tutti i combustibili vanilla**.
- [x] **Nessuna bruciatura** con qualsiasi metodo.
- [x] Rendering 2.5D: **salse piatte, altri topping poco rialzati**.
- [x] Ingredienti di altre mod supportabili tramite definizioni e tag.

## Questioni da risolvere prima del codice (NON CONFERMATE)

- [ ] Numero clic di stesura (proposta: 4 a mano, 1 col mattarello).
- [ ] Durabilità/ricetta del mattarello.
- [ ] Limite delle porzioni di topping (proposta: 8 incluse le salse).
- [ ] Consumi esatti per ingrediente, ripetizioni, più salse, remainder.
- [ ] Meccanica Shift + clic destro per annullamento e recupero.
- [ ] Pala per pizza facoltativa: ricetta e funzione precisa.
- [ ] Supporti parziali e comportamento se il blocco sottostante viene rotto.
- [ ] Tempi cottura (proposti: 20/30/60 secondi).
- [ ] Consumo fuel del Pizza Oven, preriscaldamento e burn time residuo.
- [ ] Hopper sopra/lati/sotto e fallback manuale.
- [ ] Ricetta crafting Pizza Oven.
- [ ] Griglia crafting della pizza libera e output crudo/cotto.
- [ ] Pizza cotta mangiabile intera o tagliata in 4 fette.
- [ ] Riconoscimento esatto di gusti e quantità, priorità tra ricette.
- [ ] Elenco ufficiale delle pizze riconosciute e dei condimenti iniziali.
- [ ] Valori fame, saturazione, eventuali effetti alimentari.
- [ ] Gestione delle collisioni, del posizionamento e rendering di dettaglio.
- [ ] API effettive per mantenere data component nelle fornaci vanilla.

## Piano di sviluppo proposto

### Fase 1: fondamenta interattive

- [ ] Registrare palla d'impasto, blocco di preparazione e dati serializzabili.
- [ ] Implementare appoggio della palla direttamente sui blocchi.
- [ ] Implementare stesura a mano e con mattarello.
- [ ] Salvare e recuperare correttamente stati e oggetti.
- [ ] Test chunk unload, multiplayer, rottura del supporto e superfici particolari.

### Fase 2: sistema condimenti

- [ ] Definizioni ingredienti via codice/datapack.
- [ ] Aggiunta topping con clic destro e gestione quantità.
- [ ] Eventuale rimozione ultimo topping e remainder.
- [ ] Nomi dinamici localizzati e ricette riconosciute.
- [ ] Rendering provvisorio per convalidare i dati.

### Fase 3: rendering 2.5D

- [ ] Impasto nei diversi stadi; pizza cruda/cotta.
- [ ] Salse piatte, formaggi/condimenti 2.5D, preset riutilizzabili.
- [ ] Seed deterministico e conservazione della distribuzione.
- [ ] Aspetto item inventario, eventuali fette e pizza nel forno.
- [ ] Fallback per topping modded e stress test prestazioni.

### Fase 4: Pizza Oven

- [ ] Forno compatto orientabile con una pizza alla volta.
- [ ] BlockEntity persistente, interazioni manuali e nessuna bruciatura.
- [ ] Tutti i combustibili vanilla, rimanenze dei contenitori e energia residua.
- [ ] Automazione con hopper, se confermata.
- [ ] Modelli, animazioni, traduzioni e ricetta.

### Fase 5: forno vanilla e crafting

- [ ] Prototipo conservazione componenti passando da **fornace e affumicatore vanilla**.
- [ ] Ricetta speciale di crafting per pizze libere.
- [ ] JEI: esempi di pizze riconosciute, condimenti, lavorazioni e forni.
- [ ] Taglio, cibo/nutrizione e ri-posizionamento pizza cotta, se confermati.

### Fase 6: test e pubblicazione

- [ ] Test approfonditi con ingredienti della mod e di altre mod.
- [ ] Test stress di tanti topping/pizze, multiplayer e server dedicato.
- [ ] Test no bruciatura, hopper, chunk unload, salvataggi e dupe.
- [ ] Rebalancing dei tempi, costi e valori alimentari.
- [ ] Documentazione EN/IT, README, CHANGELOG 1.0.0, JEI.
- [ ] Build finale, PR su `main`, release GitHub, CurseForge, Modrinth.

## Regole da rispettare

- Sviluppare sul branch `feature/pizza-1.0.0`; `main` è protetta e non va modificata direttamente.
- Prima di implementare meccaniche non ancora confermate, discuterle.
- Non fare modifiche radicali alle meccaniche cheese/cured meats già funzionanti.
- **Non spostare, eliminare, sovrascrivere o sostituire asset creati dall'autore** senza consenso esplicito.
- Tutti i JSON d'esempio nei documenti sono **bozze progettuali**, non file di datapack già utilizzabili.

## Prossimo tema da concordare

**Quantità e limiti dei condimenti** (unità massime, item ripetibili, quantità consumata, salse), poi interazioni di raccolta/annullamento, insieme dei gusti riconosciuti e dettagli del rendering.
