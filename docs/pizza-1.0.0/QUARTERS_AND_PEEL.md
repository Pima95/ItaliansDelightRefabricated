# Pizza 1.0.0 — Condimento per quarti e pala per pizza

> **Stato (11 ottobre 2026): specifica di design, non implementazione completata.**
> Questo documento registra le decisioni concordate. **CONFERMATO** indica un comportamento deciso; **DA DEFINIRE** indica una scelta ancora aperta. Le indicazioni tecniche sono requisiti o proposte, non attestazioni di codice gia' presente.

## Obiettivo e contesto

La pizza e' preparata direttamente nel mondo, senza GUI obbligatoria per condirla. Il giocatore aggiunge ingredienti con clic destro sulla base gia' stesa e puo' creare gusti liberi. Il rendering previsto resta 2.5D: salse piatte e condimenti leggermente rialzati.

Il prototipo attuale della mozzarella e' **provvisorio**: usa il booleano `mozzarella` del BlockState, consuma un item e visualizza cinque pezzi. **Non** implementa ancora i quattro quarti, le applicazioni multiple, la rimozione parziale o i dati sulla pala.

## 1. Quattro quarti — CONFERMATO

- Ogni pizza ha **quattro quarti logici distinti**, condibili in modo indipendente.
- Il giocatore puo' applicare condimenti diversi su quarti diversi, oppure distribuire un condimento su piu' quarti.
- I quarti sono una suddivisione **per il condimento**: non e' automaticamente una pizza tagliata in quattro fette.
- Gli ingredienti e la loro distribuzione devono rimanere identificabili durante le interazioni, la cottura e il trasporto.

**DA DEFINIRE:** selezione del quarto tramite hitbox/clic, applicazione simultanea a piu' quarti, costo in item per tipo, limite complessivo, ripetizioni di topping e gestione dei livelli sovrapposti.

## 2. Applicazione originale e recupero — CONFERMATO

Una singola applicazione che consuma un ingrediente puo' interessare uno o piu' quarti. L'applicazione va considerata una **unita' indivisibile ai fini del rimborso**, anche se la sua rappresentazione viene rimossa un quarto alla volta.

- Ogni applicazione tiene traccia dell'**oggetto realmente consumato**, della **quantita'** consumata e dell'**insieme dei quarti originariamente coperti**.
- Rimuovere il condimento **solo da alcuni** dei quarti coperti non restituisce nessun item.
- Soltanto quando sono stati rimossi **tutti i quarti** di quella specifica applicazione, si restituisce **esattamente** la quantita' consumata da quell'applicazione.
- Una seconda applicazione dello stesso ingrediente e' distinta dalla prima: non devono essere unite in modo da perdere la provenienza o moltiplicare i rimborsi.
- Il sistema deve impedire sia i rimborsi multipli sia la perdita degli oggetti in seguito a interazioni ripetute o simultanee.

**Esempio A:** 1 mozzarella applicata su quattro quarti = **una** applicazione e **una** mozzarella consumata. Rimuovere 1, 2 o 3 quarti non restituisce nulla; rimuovere il quarto rimanente restituisce **1 mozzarella**, non 4.

**Esempio B:** 4 mozzarelle applicate singolarmente, una per ogni quarto = **quattro** applicazioni distinte. Ognuna puo' essere restituita separatamente quando viene rimossa interamente.

### Destinazione degli oggetti recuperati — CONFERMATO

- La rimozione normale restituisce l'ingrediente **direttamente all'inventario del giocatore**.
- Un item puo' cadere a terra **soltanto se manca spazio nell'inventario** (o in casi di recupero senza inventario destinatario, da disciplinare).
- Nessun drop avviene durante una rimozione parziale che non ha ancora completato l'applicazione originale.

### Rottura della pizza — CONFERMATO

- Rompendo una **pizza cruda in preparazione** si recuperano l'impasto e gli ingredienti ancora associati alla preparazione.
- Un'applicazione ancora presente anche su un solo quarto puo' restituire **al massimo** la quantita' inizialmente consumata (es. **una mozzarella**, non una per quarto).
- Il recupero alla rottura e il recupero tramite rimozione devono essere reciprocamente esclusivi: **nessuna duplicazione**.
- La **pizza gia' cotta** non viene scomposta in ingredienti originari quando viene rotta: e' un prodotto finito.

**DA DEFINIRE:** dettagli della raccolta quando la pizza viene rotta da un evento senza giocatore, creativa, blocco di supporto distrutto, esplosioni e contenitori/remainder particolari. Queste varianti non devono violare la conservazione degli item.

## 3. Pala per pizza (Pizza Peel) — CONFERMATO

La **pala per pizza** e' lo strumento per trasferire una pizza **intera**. E' gia' disponibile un asset della vecchia Italian's Delight fornito dall'autore: **riutilizzarlo dopo averlo recuperato e verificato**, senza crearne o sostituirne la texture autonomamente.

- La pala trasporta **direttamente una pizza alla volta**, non la converte in un item pizza separato collocato in un altro slot dell'inventario.
- Con **clic destro** su una pizza posizionata, la pala libera la raccoglie solo se non ne trasporta gia' una.
- La pala occupata **non puo' raccogliere una seconda pizza**.
- Con **clic destro** su una superficie compatibile, la pala occupata deposita la pizza e ricostruisce lo stato precedente.
- L'uso della pala **non e' obbligatorio per applicare o rimuovere** i condimenti: queste interazioni avvengono direttamente sulla pizza nel mondo.
- La pala e' destinata anche a **inserimento ed estrazione** della pizza nel futuro Pizza Oven. L'integrazione del forno deve essere implementata e verificata quando il forno esistera'.

### Dati conservati durante il trasporto — CONFERMATO

L'intero stato della preparazione deve viaggiare **dentro i dati persistenti della pala occupata**, incluse almeno le seguenti informazioni:

- stato e progresso della pizza (impasto/base, cruda/condita/cotta secondo il caso);
- ingredienti, quantita', applicazioni originali e quarti ancora coperti;
- disposizione visiva, inclusi eventuali semi e parametri di rendering;
- proprieta' necessarie per determinare il gusto e preservare la cottura.

Il trasferimento **mondo -> pala -> mondo/forno -> pala** dev'essere atomico dal punto di vista logico: mai due pizze create dalla stessa raccolta e mai cancellazione della fonte prima che lo stato sia trasferito in sicurezza. Il salvataggio del mondo, il chunk unload e il multiplayer devono mantenere uno stato coerente.

**DA DEFINIRE:** ricetta della pala; se abbia durabilita' (orientamento: strumento riutilizzabile senza usura); tooltip, stackability e aspetto mentre e' occupata; comportamento se la pala occupata e' droppata, distrutta o spostata in contenitori; comandi/precedenze per il forno; supporto alle pizze gia' cotte e alle superfici specifiche. Nessuna di queste e' ancora una regola tecnica finalizzata.

## 4. Vincoli tecnici di progetto (da implementare)

Una rappresentazione possibile, **non API definitiva**:

```text
PizzaState
  format_version
  preparation_state / cooking_state
  applications[]
    application_id
    ingredient_id + consumed_stack/count
    original_quarter_mask   // 4 bit
    remaining_quarter_mask  // 4 bit
    render data / seed
  additional_visual_and_recipe_data

PizzaPeel
  contained_pizza_state?    // zero oppure una pizza
```

Invarianti da mantenere:

1. `remaining_quarter_mask` e' un sottoinsieme dei quarti originariamente coperti.
2. Ogni applicazione puo' rimborsare la propria quantita' **una volta sola**.
3. La rimozione completa cancella l'applicazione soltanto dopo aver gestito il rimborso.
4. La rottura della pizza non rimborsa applicazioni gia' restituite.
5. Il trasporto sulla pala conserva dati e identita' visiva, non li ricostruisce da un gusto predefinito.
6. Tutte le mutazioni di inventario e mondo devono essere autorevoli lato server e sincronizzate ai client.
7. La rappresentazione deve consentire tag/definizioni per ingredienti di altre mod senza assumere automaticamente che tutti i cibi siano validi.

Questi nomi di campo sono *illustrativi*: codec, formato dei component, BlockEntity e protocolli saranno scelti durante l'implementazione.

## 5. Questioni ancora aperte

- Interazione precisa per **scegliere il quarto** e per **rimuovere** un condimento senza interferire con stesura, aggiunta e pala.
- Se una singola applicazione copra di default un quarto, piu' quarti o tutta la pizza; costi, ripetizioni e limiti.
- Priorita' e visualizzazione degli ingredienti sovrapposti, specialmente delle salse.
- Regole esatte per il recupero di contenitori e rimanenze (bottiglie, ciotole).
- Modellazione 2.5D per quarto, forme, seme deterministico, rendering da cruda e cotta.
- Interazione della pala con Pizza Oven e forni vanilla, inventari, hopper e pizza gia' cotta.
- Cosa fare con una pala occupata quando viene distrutta e come visualizzarla.
- Taglio in fette della pizza cotta, bilanciamento e valori alimentari.

## 6. Criteri di accettazione futuri

- [ ] Una mozzarella applicata sui 4 quarti costa 1 item; togliere 3 quarti non rimborsa; togliere l'ultimo rimborsa 1.
- [ ] Quattro applicazioni distinte non si fondono in una e non producono rimborsi errati.
- [ ] La rottura di una pizza cruda recupera impasto e ingredienti residui senza duplicare; quella cotta non restituisce gli ingredienti separati.
- [ ] Recupero nel normale inventario; drop solo in mancanza di spazio.
- [ ] La pala trasporta esattamente una pizza con tutti i dati e rifiuta una seconda.
- [ ] Raccolta e riposizionamento mantengono quarti, stadio e resa visiva dopo salvataggio/ricarica.
- [ ] Test multiplayer, chunk unload, distruzione supporto, inventario pieno e trasferimento al forno quando disponibile.

## Documenti collegati

- [README.md](README.md) — panoramica della pizza 1.0.0
- [GAMEPLAY.md](GAMEPLAY.md) — flusso delle interazioni
- [TOPPINGS_AND_RENDERING.md](TOPPINGS_AND_RENDERING.md) — composizione e renderer
- [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md) — dati e compatibilita'
- [PIZZA_OVEN.md](PIZZA_OVEN.md) — futuro sistema di cottura
- [ROADMAP.md](ROADMAP.md) — stato e checklist
