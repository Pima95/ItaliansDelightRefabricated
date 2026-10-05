# Cured Meats — Test Checklist

Checklist manuale per la prima filiera dei salumi nel branch
`feature/cured-meats`.

La verifica statica del repository è già stata completata:

- 25/25 item: definizione, modello e texture presenti;
- 14/14 stati appesi: modello presente;
- 27/27 ricette della filiera: presenti;
- nessun riferimento asset obbligatorio mancante rilevato.

Questa checklist riguarda quindi il comportamento reale in Minecraft.

---

## 1. Avvio e caricamento

- [x] `./gradlew clean build` completa senza errori.
- [x] `./gradlew runClient` avvia il gioco.
- [x] Il mondo esistente della 0.2.0 si apre senza errori.
- [x] Gli Hanging Hook già piazzati prima dell'update restano validi.
- [x] Provolone e Scamorza già supportati dall'uncino continuano a funzionare.

---

## 2. Creative tab e JEI

Controllare tutti gli item della filiera:

- [x] 9 intermedi presenti.
- [x] 8 salumi finiti presenti.
- [x] 8 fette presenti.
- [x] Nessuna missing texture.
- [x] Nessuna icona tagliata o con fondo indesiderato.

JEI:

- [x] Le ricette dei formaggi piazzabili compaiono in **Cheese Aging**.
- [x] Le ricette di Provolone, Scamorza e salumi appesi compaiono in
  **Hanging Aging & Drying**.
- [x] Cheese Aging mostra il Cheese Aging Rack come catalyst.
- [x] Hanging Aging & Drying mostra l'Hanging Hook come catalyst.
- [x] Le ricette Cutting Board mostrano 4 fette standard e 8 per i prodotti da Ham.
- [x] Mortadella mostra sia Furnace sia Smoker.
- [x] Speck preparato mostra sia Furnace sia Smoker per l'affumicatura.

---

## 3. Hanging Hook — inserimento e rimozione

Provare almeno una volta ogni coppia:

- [x] Salted Ham → Prosciutto Crudo.
- [x] Raw Salame → Salame.
- [x] Prepared Pancetta → Pancetta.
- [x] Prepared Guanciale → Guanciale.
- [x] Prepared Bresaola → Bresaola.
- [x] Prepared Coppa → Coppa.
- [x] Smoked Prepared Speck → Speck.

Per ciascuna:

- [x] L'intermedio entra nell'uncino.
- [x] Lo stack in mano diminuisce di 1.
- [x] Il blocco sottostante viene riservato.
- [x] Non è possibile piazzare un blocco nello spazio occupato.
- [x] Click destro rimuove il prodotto corretto.
- [x] Con inventario pieno il prodotto non viene perso.
- [x] Il blocco sottostante torna libero dopo la rimozione.

---

## 4. Modelli appesi

Controllare preparato e finito delle sette famiglie:

- [x] Prosciutto.
- [x] Salame.
- [x] Pancetta.
- [x] Guanciale.
- [x] Bresaola.
- [x] Coppa.
- [x] Speck.

Verifica visiva:

- [x] Modello centrato rispetto all'uncino.
- [x] Cappio collegato al metallo.
- [x] Nessuna faccia mancante.
- [x] Nessun flickering / z-fighting evidente.
- [x] Texture esterna e taglio assegnate correttamente.
- [x] Nessun pixel stirato.
- [x] Aspetto diverso e leggibile fra preparato e finito.
- [x] Hitbox/selezione coerente con il modello.
- [x] Testati tutti e quattro gli orientamenti dell'uncino.

---

## 5. Stagionatura e persistenza

Per un prodotto con tempo di lavorazione ridotto temporaneamente oppure
lasciando completare il timer normale:

- [x] Il progresso avanza solo mentre il chunk è caricato.
- [x] Il prodotto si trasforma nell'output corretto.
- [x] Il modello cambia allo stato finale.
- [x] Il prodotto finale può essere rimosso.
- [x] Il prodotto finale può essere riappeso come decorazione.

Persistenza:

- [x] Appendere un prodotto e attendere parte del tempo.
- [x] Salvare e uscire dal mondo.
- [x] Riaprire il mondo.
- [x] Il prodotto rimane sull'uncino.
- [x] Il progresso riprende dal valore precedente.
- [x] Scaricare e ricaricare il chunk.
- [x] Il progresso non si azzera.
- [x] Lo spazio tecnico sotto l'uncino viene ricostruito correttamente se necessario.

---

## 6. Rottura dell'uncino

Con prodotto preparato appeso:

- [x] Rompere l'uncino.
- [x] Viene droppato l'uncino.
- [x] Viene droppato anche il prodotto.
- [x] Il progresso parziale non viene trasferito all'item.
- [x] Lo spazio tecnico sottostante viene rimosso.

Ripetere con:

- [x] prodotto già stagionato;
- [x] Provolone;
- [x] Scamorza.

---

## 7. Mortadella

Catena:

```text
Raw Mortadella
      ↓
Furnace / Smoker
      ↓
Mortadella
      ↓ Cutting Board
4× Mortadella Slice
```

Test:

- [x] Furnace accetta Raw Mortadella.
- [x] Smoker accetta Raw Mortadella.
- [x] Furnace impiega circa il doppio dello Smoker.
- [x] Output identico nei due casi.
- [x] XP coerente.
- [x] Cutting Board produce 4 fette.

---

## 8. Speck

Catena:

```text
Prepared Speck
      ↓ Furnace / Smoker
Smoked Prepared Speck
      ↓ Hanging Hook
Speck
      ↓ Cutting Board
4× Speck Slice
```

Test:

- [x] Furnace accetta Prepared Speck.
- [x] Smoker accetta Prepared Speck.
- [x] Output: Smoked Prepared Speck.
- [x] Smoked Prepared Speck può essere appeso.
- [x] Completa la stagionatura in Speck.
- [x] Speck produce 8 fette sul Cutting Board.

---

## 9. Cutting Board

- [x] Prosciutto Crudo → 8 fette.
- [x] Salame → 4 fette.
- [x] Mortadella → 4 fette.
- [x] Pancetta → 4 fette.
- [x] Guanciale → 4 fette.
- [x] Bresaola → 4 fette.
- [x] Coppa → 8 fette.
- [x] Speck → 8 fette.
- [x] Il tag `#c:tools/knife` accetta i coltelli compatibili.

---

## 10. Regressione formaggi

Dopo le modifiche all'uncino:

- [x] Fresh Provolone → Provolone.
- [x] Fresh Scamorza → Scamorza.
- [x] Smoked Scamorza può essere appesa come decorazione.
- [x] Rimozione con mano vuota funziona.
- [x] Rimozione con item in mano funziona.
- [x] Rottura dell'uncino droppa correttamente il formaggio.
- [x] Nessuna regressione visuale dei modelli esistenti.

---

## 11. Validazione balance v1

- [x] tempi di stagionatura 15–35 minuti verificati;
- [x] costi degli ingredienti verificati in survival;
- [x] rese 4/8 fette verificate;
- [x] nutrition e saturation verificate;
- [x] stack size 16/64 verificati;
- [x] esperienza Furnace/Smoker verificata;
- [x] convenienza complessiva della filiera verificata.

**Esito:** balance v1 approvato.
