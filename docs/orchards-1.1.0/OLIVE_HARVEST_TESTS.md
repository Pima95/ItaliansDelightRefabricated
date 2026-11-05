# Orchard 1.1.0 — Raccolta olive (prima delle tre specie)

**Branch:** `feature/orchards-1.1.0`. **Minecraft:** 26.2 / Fabric, Java 25.

## Regole di gioco implementate

- Lo stesso blocco già esistente `italiansdelight:olive_leaves` ora ha lo stato
  persistente `fruiting=false|true`; i vecchi mondi mantengono il blocco
  e acquisiscono lo stato predefinito `false` senza conversioni di ID.
- Le **foglie naturali** da germoglio (ulivo singolo 1×1 e secolare 2×2)
  possono maturare solo con `persistent=false`, `distance<7` e
  `waterlogged=false`; 1 probabilità su 12 **per random tick**
  delle foglie ancora prive di frutti. Niente nuovi alberi nei biomi.
- La maturazione è visibile: modello `olive_leaves_fruiting`, usando la
  sagoma e i quattro grigi/verdi esatti delle foglie originali con 16
  pixel di piccoli frutti oliva aggiunti sui pixel già opachi.
  La texture originale `olive_leaves.png` **non è stata modificata**.
- **Clic destro** su una foglia matura → lascia intatte le foglie,
  restituisce **1–3 `italiansdelight:olives`**, ripristina `fruiting=false`,
  riproduce un suono. Raccolta solo lato server e sincronizzazione automatica.
- Le foglie tornano a produrre frutti tramite tick casuali. La raccolta non
  cambia il loot da distruzione: Silk Touch, cesoie, germogli, bastoni e
  decadimento rimangono quelli precedenti.
- Le foglie posizionate a mano (`persistent=true`) **non fruttificano**:
  si evitano fattorie infinite di foglie artificiali. La crescita segue
  `randomTickSpeed` del mondo; con valore zero si ferma.
- Mandorle e ciliegie **non** sono ancora raccoglibili; l'integrazione
  opzionale con **Serene Seasons** verrà progettata quando tutte e tre
  le raccolte saranno approvate.

## Checklist di gioco (non sostituita dai test CI)

- [ ] Piantare un ulivo singolo, crescerlo con farina d'ossa e attendere:
      verificare le prime foglie con macchie oliva scure.
- [ ] Fare clic destro su foglie mature in Sopravvivenza: ricevere 1–3 olive,
      nessuna foglia distrutta, macchie visive scomparse e suono riprodotto.
- [ ] Ripetere subito: nessun doppio drop sulla stessa foglia immatura.
- [ ] Attendere la ricrescita; verificare foglie mature dopo ricarica chunk/mondo.
- [ ] Ripetere con un **ulivo secolare 2×2**.
- [ ] Piazzare a mano alcune foglie in Creativa: non fruttificano.
- [ ] Rimuovere il tronco: le foglie naturali devono ancora decadere.
- [ ] Cesoie/Silk Touch: raccogliere il blocco foglie, senza duplicare olive;
      senza cesoie mantenere il normale drop di germogli e bastoni.
- [ ] Testare client/server dedicato, assenza di crash, asset e suoni corretti.

**Nota:** CI con esito positivo non equivale a collaudo manuale in Minecraft.
