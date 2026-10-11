# Italian's Delight Refabricated — Pizza Update 1.0.0

> **Stato:** progettazione, non ancora implementazione. Le voci **CONFERMATO** derivano dalle decisioni concordate; le **PROPOSTE** sono ancora modificabili.

## Visione

La 1.0.0 introduce una pizza davvero personalizzabile e preparabile **direttamente nel mondo**, senza obbligare il giocatore a scegliere tra pochi gusti o a usare la crafting table. Le ricette note (es. Margherita) vengono riconosciute dal contenuto, mentre una combinazione non registrata riceve un **nome dinamico** con gli ingredienti scelti.

Flusso previsto: **palla d'impasto -> posizionamento su blocco -> stesura a mano/mattarello -> condimento con clic destro -> raccolta -> cottura -> pizza cotta -> eventuali fette**. È prevista anche la preparazione via crafting table.

## Decisioni CONFERMATE

1. La **palla d'impasto si piazza sopra superfici approvate dei blocchi del mondo** (vedi [SURFACE_RULES.md](SURFACE_RULES.md)). La stesura **non usa il Cutting Board** né una GUI.
2. Il giocatore può stendere l'impasto **sia a mano, con clic destro ripetuto, sia con un mattarello**.
3. Il giocatore aggiunge i condimenti **direttamente alla pizza posizionata nel mondo**, con clic destro.
4. L'ordine di inserimento degli ingredienti non deve imporre una ricetta fissa: la pizza può essere inventata liberamente.
5. Le combinazioni registrate hanno **nomi ufficiali** (ad esempio Pizza Margherita); le altre hanno **nomi costruiti dagli ingredienti**.
6. Le pizze devono poter essere preparate anche tramite **crafting**.
7. Cottura possibile nel **Pizza Oven dedicato** e nei **forni vanilla esistenti** (fornace e affumicatore).
8. Il Pizza Oven è un blocco **compatto 1×1×1**, con capienza di **una pizza per volta**.
9. Il Pizza Oven accetta **tutti i combustibili vanilla** validi, non soltanto quelli a base di legno.
10. **Non esiste il rischio di bruciare la pizza**, in nessun forno.
11. Il rendering è **2.5D**: **salse piatte** e **condimenti leggermente in rilievo**.
12. Il sistema deve poter estendere i condimenti alle **altre mod**, tramite tag e definizioni compatibili.
12a. La **salsa e' facoltativa**: le pizze bianche sono valide. Ogni sottoarea puo' contenere **al massimo una salsa di qualsiasi tipo**; salse diverse possono condire porzioni diverse, senza sovrapposizioni.
13. La pizza parte **intera**; dopo la stesura il **mattarello** permette facoltativamente di dividerla in **due meta'**, poi di suddividere una o entrambe le meta' per ottenere fino a **quattro quarti**. Gli ingredienti si applicano alla zona puntata, secondo il livello di divisione. Le linee sono visibili solo mentre si tiene il mattarello o un ingrediente valido, senza modifiche ai modelli/texture.
14. La rimozione parziale non restituisce gli ingredienti: ogni applicazione originale e' rimborsata **una sola volta** solo dopo la rimozione da tutti i quarti da essa coperti. Recupero in inventario, drop soltanto se pieno; rompendo la pizza cruda si restituiscono impasto e ingredienti ancora associati, senza moltiplicazioni. La pizza cotta non si scompone nei vecchi ingredienti.
15. La **pala per pizza** trasporta direttamente **una sola pizza alla volta**, conservando ingredienti, quarti, stadio e dati visivi. Non serve per condire o rimuovere topping.

## PROPOSTE non ancora confermate

- Bilanciamento della stesura ancora provvisorio (attualmente 4 clic a mano, 1 col mattarello); il mattarello esistente ha 64 punti di durabilita' e perde 1 punto per stesura riuscita. Usura per le successive divisioni da decidere.
- Eventuale limite totale ai differenti condimenti: **8 era solo una proposta**. **Non sono ammesse ripetizioni dello stesso ingrediente sulla stessa parte**.
- Geometria precisa delle divisioni con mattarello e gesto di rimozione del condimento (Shift + clic col mattarello per riunire le zone e' una possibilita' discussa, non ancora finalizzata).
- Ricetta, durabilita' e comportamento della pala se occupata viene distrutta; asset della vecchia Italian's Delight fornito dall'autore, da recuperare e verificare.
- Pizza Oven senza GUI obbligatoria, con alimentazione/estrazione via hopper.
- Tempi indicativi: **20 s** Pizza Oven, **30 s** affumicatore, **60 s** fornace.
- Consumo di combustibile solo durante una cottura effettiva, conservazione dell'energia residua.
- Pizza intera mangiabile, riposizionabile nel mondo e tagliabile in **4 fette** mediante Cutting Board (usato **solo per il taglio**, non per stendere).
- Valori alimentari derivati dai condimenti, con un tetto per evitare abusi.
- Preparazione via crafting con ricetta speciale che conserva la composizione.
- Riconoscimento dei gusti tramite definizioni JSON/datapack e preset di rendering.

## Indice documentazione

- [QUARTERS_AND_PEEL.md](QUARTERS_AND_PEEL.md) — **riferimento aggiornato e confermato** per quarti, restituzioni e pala per pizza.
- [GAMEPLAY.md](GAMEPLAY.md) — fasi, comandi nel mondo, stesura, condimento, crafting, raccolta.
- [SURFACE_RULES.md](SURFACE_RULES.md) — superfici approvate/escluse, stati dei blocchi e test.
- [PIZZA_OVEN.md](PIZZA_OVEN.md) — forno 1×1, combustibili, tempi, automazione e forni vanilla.
- [TOPPINGS_AND_RENDERING.md](TOPPINGS_AND_RENDERING.md) — categorie, regole di composizione e resa 2.5D.
- [DATA_AND_COMPATIBILITY.md](DATA_AND_COMPATIBILITY.md) — dati persistenti, nomi dinamici, tag e datapack.
- [NAMED_PIZZAS.md](NAMED_PIZZAS.md) — possibili pizze riconosciute (non ancora approvate).
- [ROADMAP.md](ROADMAP.md) — checklist, questioni aperte e fasi di implementazione.

## Regole di progetto

- Mod per **Minecraft 26.2 / Fabric**, compatibile con Farmer's Delight Refabricated.
- Non sovrascrivere, spostare o cancellare texture e modelli già prodotti dall'autore.
- Nel repository esistono già `textures/item/pizza_margherita.png` e `textures/block/top_mushroom_pizza.png`: sono risorse da rispettare e integrare in seguito.
- Nessuna modifica al codice o alla versione della mod è implicita nella creazione di questi documenti.
- Il lavoro va sviluppato su un branch dedicato e portato su `main` mediante PR dopo test e revisione.
