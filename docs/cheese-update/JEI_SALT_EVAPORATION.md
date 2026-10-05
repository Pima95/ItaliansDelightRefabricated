# JEI — Evaporazione del Sale

La produzione del sale è esposta in JEI tramite una categoria dedicata:

- **Titolo EN:** Salt Evaporation
- **Titolo IT:** Evaporazione del Sale
- **Icona/catalyst:** Calderone vanilla
- **Input:** 1000 mB di acqua come fluido JEI
- **Output:** Sale
- **Tempo:** 12000 tick utili, circa 10 minuti di luce valida
- **Resa reale:** 3–7 unità di sale

La categoria riusa `textures/gui/jei/aging.png`, la stessa interfaccia usata
dalla stagionatura dei formaggi e dei salumi.

## Condizioni mostrate nel tooltip

Il processo avanza solo quando:

- è giorno;
- non piove e non c'è temporale;
- il cielo sopra il calderone è completamente libero;
- il chunk è caricato;
- il calderone resta pieno d'acqua.

Notte, maltempo o un blocco sopra il calderone mettono in pausa il processo.
Rimuovere l'acqua o rompere il calderone azzera il progresso.

## Checklist

- [ ] JEI mostra la categoria cercando `Salt`;
- [ ] l'icona della categoria è un calderone;
- [ ] il catalyst è il calderone vanilla;
- [ ] lo slot sinistro mostra acqua fluida e non un contenitore;
- [ ] lo slot destro mostra il sale;
- [ ] il tooltip indica 10 minuti;
- [ ] il tooltip indica resa 3–7;
- [ ] il tooltip riporta le condizioni ambientali;
- [ ] la categoria usa correttamente il background della stagionatura.
