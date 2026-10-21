# Pizza 1.0.0 — Audit prima del rebalancing e collaudo finale

**Data:** 21 ottobre 2026  
**Branch:** `feature/pizza-1.0.0`  
**Stato:** raccolta dei valori dal codice completata; **non sono stati modificati valori di gameplay**. Build Java 25 e prove in gioco sono passaggi indipendenti.

## Decisioni e conferme dell'autore

- L'autore ha confermato **approvate le texture crude e cotte delle 18 pizze riconosciute**, già visibili correttamente nel menu Creativa.
- L'autore ha ora confermato: **«Questi spicchi vanno bene, procediamo»**. Sono quindi approvate anche tutte le icone degli spicchi dopo la revisione di distinguibilità.
- **Non modificare né sovrascrivere le texture approvate** (pizze crude, cotte, Margherita storica, spicchi, asset esistenti) senza una nuova richiesta.
- Prossima fase: audit dei sistemi e confronto dei valori provvisori **prima** di autorizzare modifiche al bilanciamento. L'autore richiede di registrare su GitHub anche le discussioni e le piccole scelte.

## Chiarimento richiesto dall'autore — 21 ottobre 2026

L'autore ha chiesto di controllare un accordo precedente: **«Voglio un sistema che vari in base agli ingredienti con cui è cucinata la pizza, se non sbaglio lo avevamo già deciso»**. È stato verificato il contenuto della roadmap originale e il metodo `PizzaSliceFactory.foodFor()`.

**Accordo precedente verificato (12 ottobre 2026):** il sistema è già *dinamico per quarto e per composizione*, ma il contributo nutrizionale è determinato dalle **categorie distinte** presenti nel quarto (crosta + categorie di topping, una volta per categoria), **non dalla quantità o dal numero di ingredienti singoli**. Bonus: crosta +1 fame/+1 saturazione; salse +1/+0,5; latticini +1/+1,5; proteine animali +2/+2,5; verdure +1/+0,5; erbe +0/+0; oli e grassi +0/+1. Limiti 6 punti fame e 6 punti saturazione per spicchio. Variante cruda/cotta equivalente non si duplica; nessun effetto speciale.

**Conseguenza confermata:** una Quattro Formaggi bianca con quattro latticini conta `DAIRY` una volta e riceve 2 fame / 2,5 saturazione, mentre una Margherita riceve 3 fame / 3 saturazione perché include `SAUCES` e `DAIRY`. **Il bonus suggerito precedentemente per tre o più formaggi NON faceva parte dell'accordo e non è stato implementato.**

**Decisione ancora da prendere con l'autore:** la nuova richiesta «vari in base agli ingredienti» intende confermare la logica dinamica *per categorie diverse* già approvata, oppure desidera esplicitamente una *revisione* per cui latticini/ingredienti diversi della stessa categoria contribuiscano individualmente? Non anticipare questa scelta: **nessuna modifica del sistema nutrizionale o delle texture autorizzata** in questo chiarimento.

## 1. Valori di cottura ATTUALI (ancora placeholder)

| Metodo | Durata a 20 TPS | Tick attivi | Origine |
| --- | ---: | ---: | --- |
| Pizza Oven | **20 s** | **400** | `PizzaBaking.MAX_PROGRESS = 1200`; `Oven.PIZZA_OVEN(3)` |
| Affumicatore | **30 s** | **600** | `data/.../pizza_smoking.json`, `PizzaSmokingRecipe` |
| Fornace | **60 s** | **1200** | `data/.../pizza_smelting.json`, `PizzaSmeltingRecipe` |

Il progresso è nella BlockEntity della macchina e **non nel singolo item pizza**. Estrarre una pizza cruda in corso di cottura azzera il progresso all'atto del successivo inserimento. La pizza cotta conserva il contenuto originale e i dati dei quarti; ciotole restitute per applicazione di topping cremoso/salsa originaria (se la relativa maschera è ancora attiva), **non per numero di quarti**. Il combustibile continua a consumarsi quando acceso, come vanilla.

Questi tempi sono **provvisori** dalla specifica originale, non sono già stati accettati come tempi definitivi.

## 2. Nutrizione ATTUALE per uno spicchio cotto

**Unità:** punti fame Minecraft (2 punti = 1 icona cosciotto completa), punti saturazione. I valori sono **per spicchio**; una pizza completa contiene quattro spicchi, ma il cibo si consuma una fetta alla volta.

| Gusto | Fame | Saturazione |
| --- | ---: | ---: |
| Bianca | 1 | 1 |
| Rossa | 2 | 1,5 |
| Margherita | 3 | 3 |
| Caprese rossa | 4 | 3,5 |
| Caprese bianca | 3 | 3 |
| Quattro Formaggi bianca | 2 | 2,5 |
| Quattro Formaggi rossa | 3 | 3 |
| Prosciutto crudo | 5 | 5,5 |
| Salame | 5 | 5,5 |
| Mortadella e Ricotta | 4 | 5 |
| Guanciale e Pecorino rossa | 5 | 5,5 |
| Speck e Gorgonzola | 4 | 5 |
| Salmone e Ricotta | 4 | 5 |
| Salmone e Scamorza affumicata | 4 | 5 |
| Carne macinata e Mozzarella | 5 | 5,5 |
| Zucca e Speck | 5 | 5,5 |
| Patate e Speck | 5 | 5,5 |
| Orto disponibile | 3 | 3 |

Fonte del calcolo: `PizzaDisplayNames.buildPresets()` (ingredienti reali), `PizzaToppingCatalog` (categoria di ogni ingrediente), `PizzaSliceFactory.foodFor()` (bonus, caps e conversione a `FoodProperties`). **Non sono valori consigliati o stimati a occhio**: descrivono la formula implementata. I dati per combinazioni personalizzate dipendono dalle categorie effettivamente presenti nel quarto.

Regole correnti, **una sola volta per categoria distinta**:

| Categoria | Fame bonus | Saturazione bonus |
| --- | ---: | ---: |
| Crosta (base) | +1 | +1 |
| Latticini | +1 | +1,5 |
| Proteine animali | +2 | +2,5 |
| Verdure | +1 | +0,5 |
| Salse | +1 | +0,5 |
| Erbe | +0 | +0 |
| Oli/grassi | +0 | +1 |

**Limiti:** massimo **6 fame** e **6 punti saturazione** per singolo spicchio. Il modificatore interno della saturazione vanilla è `saturazione / (2 × fame)`; non confondere modificatore e punti di saturazione. La vecchia mozzarella legacy conta come categoria Latticini una volta.

## 3. Osservazioni per il futuro bilanciamento — NON applicate

1. **Quattro Formaggi bianca: 2 fame / 2,5 saturazione**, perché quattro formaggi sono comunque **una sola categoria Latticini**. Un singolo spicchio Margherita vale più fame (3) pur usando meno formaggi. Questa è la principale anomalia di progressione nutrizionale da sottoporre all'autore, tenendo conto di ingredienti e costi di produzione.
2. Le pizze **Salame, Prosciutto, Guanciale, Carne, Zucca e Speck, Patate e Speck** arrivano spesso a **5 fame / 5,5 saturazione**, vicine al cap 6 anche con soli 3 ingredienti diversi: verificare se il costo degli ingredienti giustifica questa resa.
3. Bianca (1) e Rossa (2) sono molto economiche; **la Rossa richiede salsa** e il costo reale deve tener conto dei recipienti recuperati in cottura.
4. L'assenza di contributo nutrizionale aggiuntivo da erbe, o da latticini ripetuti, è coerente con la regola *categorie distinte*. Per cambiare tale regola occorre una nuova decisione dell'autore; **non aggiungere effetti di stato** senza approvazione.
5. Il Pizza Oven attualmente completa una pizza in **un terzo** del tempo della fornace vanilla. Decidere se mantenere questo vantaggio, valutandone costo del blocco, combustibile e facilità di realizzazione.

## 4. Verifiche automatiche introdotte

`.github/scripts/check_pizza_release_balance.py` è eseguito da `.github/workflows/pizza-build.yml` **prima di** `./gradlew clean build`.

- Ricava tutti i 18 preset dal catalogo `PizzaDisplayNames` e le categorie dal `PizzaToppingCatalog`: fallisce per ID sconosciuti, duplicati o nuovi gusti non aggiornati.
- Verifica espressioni della formula nutrizionale attuale (categoria distinta, bonus, caps, conversione a modificatore vanilla, supporto alla mozzarella legacy), e confronta i 18 output con questo snapshot.
- Controlla durata forno + ricette JSON e codec affumicatore/fornace, ingrediente e output; testa staticamente che il baking usi `PizzaBaking.finish` e che le ciotole siano contate per applicazione.
- **Limite:** queste sono verifiche strutturali e di dati, non test di un mondo Minecraft; i percorsi Java devono essere collaudati anche in Survival, su server e attraverso riavvio/reload. Se il rebalancing viene approvato, aggiornare deliberatamente lo snapshot.

## 5. Checklist Survival/multiplayer prima della release

- [ ] **Pizze assortite:** quattro quarti con gusti differenti, compresa una Margherita. Cuocere con Pizza Oven e prelevare in ordine sparso: quattro spicchi diversi con contenuto, nome, modello e tooltip rispettivi.
- [ ] **Nutrizione:** confrontare in Survival Bianca, Margherita, Quattro Formaggi, Salame e una pizza a quattro categorie; valori entro i cap e nessun effetto speciale.
- [ ] **Tre forni:** input e risultato, tempi attivi reali, interruzione combustibile, reinserimento della pizza, ciotole restituite una volta, niente duplicazioni o alterazione della cottura vanilla di altri cibi.
- [ ] **Pala per pizza:** piena/vuota, inserimento/estrazione rapida, `peel_cargo` e nomi/modelli conservati, nessuna perdita o duplicazione, posizionamento in prima e terza persona e attributi d'arma soltanto quando vuota.
- [ ] **Mattarello:** verifica danno/velocità rispetto alla pala di legno, durabilità e stesura, e assenza del precedente crash `Components not bound yet`.
- [ ] **Rimozioni e rimborsi:** LIFO dopo applicazione su metà/quarti, topping equivalenti, un solo rimborso per applicazione completa e nessuna creazione artificiale di ingredienti.
- [ ] **Inventory pieno e rottura:** niente quinta fetta, conservazione delle quattro maschere, drop corretti se pizza cotta servita parzialmente.
- [ ] **Persistenza/multiplayer:** salvataggio/rientro, chunk unload/reload, trasferimento item e pala tra giocatori, clic contemporanei sul medesimo quarto, gestione dei datapack esterni e ingredienti opzionali.
- [ ] **Risorse e UX:** menù Creativa, JEI, le 18 icone intere e le 18 icone spicchi già approvate non devono cambiare.
- [ ] **Bilanciamento:** discutere con l'autore valori definitivi di cottura, nutrizione e costo prima di toccare il gameplay.

## 6. Prossima decisione

Presentare all'autore una **proposta concreta di rebalancing**, partendo dalle anomalie nutrizionali e dai tempi placeholder. L'autore deve prima scegliere se mantenere la regola categorie distinte o prevedere un bonus moderato per ingredienti della stessa categoria (es. Quattro Formaggi). **Nessuna modifica al gameplay è stata autorizzata in questa fase.**
