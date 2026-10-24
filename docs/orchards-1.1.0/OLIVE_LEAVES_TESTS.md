# Orchard Update 1.1.0 — Collaudo Foglie d'Ulivo

**Branch:** `feature/orchards-1.1.0`. **Stato:** l'autore ha confermato che **le foglie funzionano**, senza fornire conferma singola di tutte le casistiche della checklist. Il colore verde precedente è stato giudicato insoddisfacente e sostituito con una nuova palette argentata **da verificare in gioco**. Assicurarsi che non sia stato installato un vecchio JAR della mod insieme alla build corrente.

## Modifiche cromatiche e pulizia asset (24 ottobre 2026)

- [x] Risultato generale: **«Le foglie funzionano»**, confermato dall'autore; non equivale a superamento certificato di ogni caso sotto elencato.
- [x] Rimossa la risorsa Rustic `textures/block/sapling_olive.png`, mantenuta la risorsa candidata `textures/block/olive_sapling.png`.
- [ ] **Nuovo test visivo:** foglie color verde salvia/argento, con riflessi grigio-argentei distribuiti sul blocco e texture leggibile anche con luce solare/ombra; nessun colore di bioma sovrapposto e silhouette invariata.
- [ ] **Consistenza futura:** quando si aggiungerà il germoglio, verificare che l'asset abbia una tonalità coerente con la chioma, senza riutilizzare la versione Rustic.

## Checklist Minecraft

- [ ] Foglie d'Ulivo presenti nella scheda Creativa con nome corretto e texture `olive_leaves.png`, senza quadrati viola/neri; blocco e item visivamente coerenti.
- [ ] Posizionare foglie a mano in aria: devono avere `persistent=true` e **non decadere**, anche senza tronchi.
- [ ] Attaccare foglie a `olive_log` e `olive_wood` naturali (`persistent=false` tramite `/setblock`); controllare `distance=1` e aumento a distanza maggiore. Devono funzionare anche con gli altri tronchi vanilla.
- [ ] Rimuovere i tronchi di supporto da foglie naturali: devono **decadere nel tempo** per tick casuali; foglie piazzate a mano restano. Testare fino a distanza 7.
- [ ] Foglie restano waterlogged come vanilla se collocate nell'acqua; verificare il salvataggio e ricaricamento del mondo.
- [ ] In Sopravvivenza: cesoie e utensile con *Silk Touch* restituiscono il blocco foglie; a mano/attrezzi normali non lo restituiscono, ma possono ottenere bastoni. **Niente drop di olive, germogli o mele in questa fase**.
- [ ] Fuoco si propaga fra foglie e può consumarle (lontano dagli edifici). Zappa/cesoie le rompono con comportamento vanilla.
- [ ] Verificare compatibilità con `#minecraft:leaves`, `#minecraft:logs`, altre mod/JEI e il server dedicato quando disponibile.
- [ ] Verificare assenza di riferimenti residui alla texture rimossa `block/leaves_olive.png`; i riferimenti nei documenti storici non sono una risorsa di gioco.

**CI ≠ collaudo manuale.** Le risorse/registrazioni sono verificate automaticamente; solo l'autore può confermare il comportamento in game. Prossima fase: germogli e crescita dell'ulivo normale e secolare 2×2, poi frutti e ricrescita.
