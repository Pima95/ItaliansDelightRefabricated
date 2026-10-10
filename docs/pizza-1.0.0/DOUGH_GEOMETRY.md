# Geometria dell'impasto per pizza — nuovo prototipo (da testare)

## Perché la modifica

La schermata di test mostrava una forma simile a un **cappello**: un elemento più piccolo sovrapposto a una base più larga. La richiesta dell'autore è un **parallelepipedo schiacciato** iniziale; allargando la pasta, il **centro si abbassa** mentre si forma una scanalatura e una **cornice rialzata** utile a contenere il condimento.

## Geometrie (coordinate in pixel / 16)

| Stadio | Ingombro X/Z | Altezza centro Y | Altezza bordo Y | Larghezza bordo | Forma |
| --- | --- | --- | --- | --- | --- |
| 0 | 4–12 | 4 | 4 | — | Cubo basso singolo, **senza strati sovrapposti** |
| 1 | 3–13 | 3 | 4 | 1 | Prima scanalatura |
| 2 | 2–14 | 2 | 4 | 1 | Scanalatura più profonda |
| 3 | 2–14 | 2 | 3 | 1 | Penultimo stadio: disco leggermente più piccolo, centro spesso 2 pixel |
| 4 | 1–15 | 1 | 3 | 2 | **Base stesa, con bordo alto e spesso** |


**Distinzione visiva dopo il test del 10 ottobre 2026:** lo stadio 3 rimane largo 12 pixel, con centro spesso 2 pixel e bordo alto 3; lo stadio 4 è largo 14 pixel, con **centro spesso soltanto 1 pixel** e bordo alto 3, largo 2. Si evita che penultimo e ultimo sembrino identici. Il bordino finale rimane rialzato come da richiesta.

Gli stadi 1–4 sono composti da **una base** e da **quattro listelli del bordo**: non c'è più un blocco centrale rialzato. Il bordo è continuo e non invade la zona del condimento. La hitbox di selezione usa gli stessi cinque volumi tramite una union di voxel shape, lasciando raggiungibile il centro incavato.

**Zona topping della pizza stesa:** X/Z da **3 a 13**, subito sopra Y=1; i condimenti piatti devono restare più bassi del bordo (Y=3), quelli in rilievo possono sporgere leggermente secondo il futuro renderer 2.5D.

## Asset e compatibilità

- Si riutilizzano senza modificarle le texture **`pizza_dough_top.png` e `pizza_dough_side.png`**, inclusi i riferimenti texture già presenti nei modelli originali dell'autore.
- Restano invariati gli ID dei cinque modelli, gli stati `stage=0..4`, la stesura manuale in 4 clic, la stesura col mattarello e la durabilità.
- Tutte le regole di supporto rimangono quelle definite in [SURFACE_RULES.md](SURFACE_RULES.md).
- Un successivo prototipo **in attesa di test** aggiunge cinque pezzi di mozzarella via modello multipart solo sulla base stesa; le salse e il sistema generale di topping rimangono da implementare.

## Checklist da testare in Minecraft

- [ ] All'atto del piazzamento l'impasto è un unico cubo schiacciato, non sembra un cappello.
- [ ] Durante i quattro clic si allarga e si crea progressivamente una cavità al centro.
- [ ] Stadio 4: centro basso e piano, bordo continuo più alto su tutti e quattro i lati.
- [ ] Clic destro a mano vuota sul centro della conca continua ad avanzare gli stadi.
- [ ] Mattarello: stende immediatamente senza cambiare la regola di usura (1 punto).
- [ ] Nessuna texture mancante/viola, nessun UV stirato vistosamente.
- [ ] Su slab superiore/doppia, scala capovolta, botola superiore chiusa e incudine la forma rimane corretta.
