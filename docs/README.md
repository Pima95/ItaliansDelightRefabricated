# Documentazione — Italian's Delight Refabricated

> **11 novembre 2026 — indice per la preparazione della 1.1.0.** La fonte di verità
> per il comportamento del gioco rimane il codice, non le vecchie roadmap.
> Alcuni documenti di sviluppo 1.0.0 descrivono fasi superate; consultare
> i documenti attuali prima di considerarli requisiti ancora da implementare.

## Da leggere per la release 1.1.0

1. [Stato e controlli prima del rilascio](orchards-1.1.0/RELEASE_READINESS.md) — checklist da non dichiarare superata senza prove
2. [Bozza note di versione](orchards-1.1.0/RELEASE_NOTES_DRAFT.md)
3. [Architettura e storia Orchard](orchards-1.1.0/ROADMAP.md) — note storiche, non tutte attuali
4. [Generazione naturale](orchards-1.1.0/NATURAL_WORLDGEN_26_2.md)
5. [Integrazione Serene Seasons](orchards-1.1.0/SERENE_SEASONS_INTEGRATION.md)
6. [Verifica licenze e attribuzione degli asset](orchards-1.1.0/LICENSE_AND_ASSET_RC_REVIEW.md) — credito upstream e attività ancora aperte
   - [Corrispondenze binarie Rustic](orchards-1.1.0/RUSTIC_ASSET_MATCHES.md)
7. [Inventario Creative e JEI](orchards-1.1.0/CREATIVE_JEI_ORDER.md)
8. [Audit e stato delle ottimizzazioni 1.1.0](CODE_OPTIMIZATION_AUDIT_2026-11-11.md) — interventi applicati, differiti e relativi rischi
9. [Checklist di collaudo dopo ottimizzazione](orchards-1.1.0/RELEASE_QA_AFTER_OPTIMIZATIONS.md) — azioni effettivamente eseguibili dall'autore

## Documentazione tecnica ancora utile

- [Requisiti](REQUISITI.md) / [Requirements](REQUIREMENTS.md)
- [Bug e regressioni registrati](KNOWN_BUGS.md)
- [Panoramica Pizza Update 1.0.0](pizza-1.0.0/CURRENT_STATUS.md)
- [Persistenza e compatibilità pizze](pizza-1.0.0/DATA_AND_COMPATIBILITY.md)
- [Composizione e quarti delle pizze](pizza-1.0.0/QUARTERS_AND_PEEL.md)
- [Pizze e condimenti](pizza-1.0.0/INGREDIENT_CATALOG.md)
- [Spicchi riposizionabili](pizza-1.0.0/REPLACEABLE_PIZZA_SLICES.md)
- [Catena latticini](cheese-update/ROADMAP_FORMAGGI.md)
- [Catena salumi](cured-meats/ROADMAP_SALUMI.md)
- [Catena piatti — archivio storico](https://github.com/Pima95/ItaliansDelightRefabricated/blob/e0af7199879043d6f9dc597cfbc20b25b7314ebe/docs/dishes/ROADMAP_PIATTI.md) (esclusa dal branch corrente tramite `.gitignore`)
- [Regole per le risorse grafiche](ASSET_GUIDELINES.md)

## Pulizia delle anteprime

Rimossi da `docs` **solo file generati di presentazione**: HTML interattivi,
JSON dei prompt di generazione e due immagini riassuntive (non texture di gioco).
Sono copie di lavoro e non servono per compilare o distribuire la mod.
Le specifiche Markdown, il codice, le texture del gioco, i dati salvati, i test
CI, le decisioni approvate e le note sulle licenze sono stati mantenuti.

I collegamenti rimasti in quei documenti Markdown portano alla
[istantanea GitHub precedente alla pulizia](https://github.com/Pima95/ItaliansDelightRefabricated/tree/8a0d32cac1807b7b6755c311722a5b158a04d7db/docs);
tutte le risorse eliminate rimangono recuperabili dalla cronologia Git.
La pulizia **non è una revisione o approvazione delle licenze**.

## Convenzioni di manutenzione

- Per 1.1.0 aggiungere scelte e rischi nel documento della release e nel
  report di audit, invece di moltiplicare file «FASE X TEST» per ogni microintervento.
- Non dichiarare verificati i test con Serene Seasons e server multiplayer
  finché non esiste una prova reale; CI statico ≠ test in-game.
- Non modificare `main`, versioni, tag o meccaniche senza approvazione.
