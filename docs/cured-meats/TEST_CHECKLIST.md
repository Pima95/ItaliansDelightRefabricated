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

- [ ] `./gradlew clean build` completa senza errori.
- [ ] `./gradlew runClient` avvia il gioco.
- [ ] Il mondo esistente della 0.2.0 si apre senza errori.
- [ ] Gli Hanging Hook già piazzati prima dell'update restano validi.
- [ ] Provolone e Scamorza già supportati dall'uncino continuano a funzionare.

---

## 2. Creative tab e JEI

Controllare tutti gli item della filiera:

- [ ] 9 intermedi presenti.
- [ ] 8 salumi finiti presenti.
- [ ] 8 fette presenti.
- [ ] Nessuna missing texture.
- [ ] Nessuna icona tagliata o con fondo indesiderato.

JEI:

- [ ] Le ricette dei formaggi piazzabili compaiono in **Cheese Aging**.
- [ ] Le ricette di Provolone, Scamorza e salumi appesi compaiono in
  **Hanging Aging & Drying**.
- [ ] Cheese Aging mostra il Cheese Aging Rack come catalyst.
- [ ] Hanging Aging & Drying mostra l'Hanging Hook come catalyst.
- [ ] Le ricette Cutting Board mostrano 4 fette come output.
- [ ] Mortadella mostra sia Furnace sia Smoker.
- [ ] Speck preparato mostra sia Furnace sia Smoker per l'affumicatura.

---

## 3. Hanging Hook — inserimento e rimozione

Provare almeno una volta ogni coppia:

- [ ] Salted Ham → Prosciutto Crudo.
- [ ] Raw Salame → Salame.
- [ ] Prepared Pancetta → Pancetta.
- [ ] Prepared Guanciale → Guanciale.
- [ ] Prepared Bresaola → Bresaola.
- [ ] Prepared Coppa → Coppa.
- [ ] Smoked Prepared Speck → Speck.

Per ciascuna:

- [ ] L'intermedio entra nell'uncino.
- [ ] Lo stack in mano diminuisce di 1.
- [ ] Il blocco sottostante viene riservato.
- [ ] Non è possibile piazzare un blocco nello spazio occupato.
- [ ] Click destro rimuove il prodotto corretto.
- [ ] Con inventario pieno il prodotto non viene perso.
- [ ] Il blocco sottostante torna libero dopo la rimozione.

---

## 4. Modelli appesi

Controllare preparato e finito delle sette famiglie:

- [ ] Prosciutto.
- [ ] Salame.
- [ ] Pancetta.
- [ ] Guanciale.
- [ ] Bresaola.
- [ ] Coppa.
- [ ] Speck.

Verifica visiva:

- [ ] Modello centrato rispetto all'uncino.
- [ ] Cappio collegato al metallo.
- [ ] Nessuna faccia mancante.
- [ ] Nessun flickering / z-fighting evidente.
- [ ] Texture esterna e taglio assegnate correttamente.
- [ ] Nessun pixel stirato.
- [ ] Aspetto diverso e leggibile fra preparato e finito.
- [ ] Hitbox/selezione coerente con il modello.
- [ ] Testati tutti e quattro gli orientamenti dell'uncino.

---

## 5. Stagionatura e persistenza

Per un prodotto con tempo di lavorazione ridotto temporaneamente oppure
lasciando completare il timer normale:

- [ ] Il progresso avanza solo mentre il chunk è caricato.
- [ ] Il prodotto si trasforma nell'output corretto.
- [ ] Il modello cambia allo stato finale.
- [ ] Il prodotto finale può essere rimosso.
- [ ] Il prodotto finale può essere riappeso come decorazione.

Persistenza:

- [ ] Appendere un prodotto e attendere parte del tempo.
- [ ] Salvare e uscire dal mondo.
- [ ] Riaprire il mondo.
- [ ] Il prodotto rimane sull'uncino.
- [ ] Il progresso riprende dal valore precedente.
- [ ] Scaricare e ricaricare il chunk.
- [ ] Il progresso non si azzera.
- [ ] Lo spazio tecnico sotto l'uncino viene ricostruito correttamente se necessario.

---

## 6. Rottura dell'uncino

Con prodotto preparato appeso:

- [ ] Rompere l'uncino.
- [ ] Viene droppato l'uncino.
- [ ] Viene droppato anche il prodotto.
- [ ] Il progresso parziale non viene trasferito all'item.
- [ ] Lo spazio tecnico sottostante viene rimosso.

Ripetere con:

- [ ] prodotto già stagionato;
- [ ] Provolone;
- [ ] Scamorza.

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

- [ ] Furnace accetta Raw Mortadella.
- [ ] Smoker accetta Raw Mortadella.
- [ ] Furnace impiega circa il doppio dello Smoker.
- [ ] Output identico nei due casi.
- [ ] XP coerente.
- [ ] Cutting Board produce 4 fette.

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

- [ ] Furnace accetta Prepared Speck.
- [ ] Smoker accetta Prepared Speck.
- [ ] Output: Smoked Prepared Speck.
- [ ] Smoked Prepared Speck può essere appeso.
- [ ] Completa la stagionatura in Speck.
- [ ] Speck produce 4 fette sul Cutting Board.

---

## 9. Cutting Board

- [ ] Prosciutto Crudo → 4 fette.
- [ ] Salame → 4 fette.
- [ ] Mortadella → 4 fette.
- [ ] Pancetta → 4 fette.
- [ ] Guanciale → 4 fette.
- [ ] Bresaola → 4 fette.
- [ ] Coppa → 4 fette.
- [ ] Speck → 4 fette.
- [ ] Il tag `#c:tools/knife` accetta i coltelli compatibili.

---

## 10. Regressione formaggi

Dopo le modifiche all'uncino:

- [ ] Fresh Provolone → Provolone.
- [ ] Fresh Scamorza → Scamorza.
- [ ] Smoked Scamorza può essere appesa come decorazione.
- [ ] Rimozione con mano vuota funziona.
- [ ] Rimozione con item in mano funziona.
- [ ] Rottura dell'uncino droppa correttamente il formaggio.
- [ ] Nessuna regressione visuale dei modelli esistenti.

---

## 11. Prima del rebalancing

Il rebalancing va iniziato solo dopo che i test funzionali sopra sono
soddisfatti.

Valori da rivedere:

- tempi di stagionatura;
- costo degli ingredienti;
- resa del Cutting Board;
- nutrition;
- saturation modifier;
- stack size degli intermedi e prodotti interi;
- esperienza di cottura;
- rapporto fra valore del salume intero e delle sue fette.
