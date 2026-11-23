# Documentazione — Italian's Delight Refabricated

Questa cartella contiene la **documentazione tecnica utile**. La cronologia Git
conserva i vecchi diari di sviluppo, gli audit intermedi e i test di fase
rimossi per mantenere il repository più leggibile.

## Versione 1.2.0 — Vineyard & Chili (branch di sviluppo)

- [Roadmap e vincoli di compatibilità](vineyard-chili-1.2.0/ROADMAP.md) — alcune note iniziali descrivono uno stato precedente all'implementazione.
- [Design originale](vineyard-chili-1.2.0/DESIGN.md) — documento storico, non elenco aggiornato delle funzioni ancora mancanti.
- [Nuovi piatti, pizze e regole dei condimenti](vineyard-chili-1.2.0/NEW_FOODS_AND_SAUCE_LAYERS.md).
- [Brewing Barrel e trasformazioni](vineyard-chili-1.2.0/BREWING_BARREL_GUI.md).
- [Texture del fermentatore e palette](vineyard-chili-1.2.0/BREWING_BARREL_TEXTURE_PALETTES.md).
- [Uva bianca](vineyard-chili-1.2.0/WHITE_GRAPES.md) e [generazione delle viti selvatiche](vineyard-chili-1.2.0/WILD_GRAPES_SPAWN.md).
- [Bevande e contenitori](vineyard-chili-1.2.0/BOTTLES_DRINKABILITY.md).
- [Collaudo manuale in gioco](vineyard-chili-1.2.0/TESTING.md).

## Funzioni esistenti e compatibilità

- [Requisiti IT](REQUISITI.md) / [Requirements EN](REQUIREMENTS.md).
- [Bug e regressioni registrati](KNOWN_BUGS.md).
- [Pizza: composizione e dati dei salvataggi](pizza-1.0.0/DATA_AND_COMPATIBILITY.md),
  [quarti e pala](pizza-1.0.0/QUARTERS_AND_PEEL.md) e
  [catalogo ingredienti](pizza-1.0.0/INGREDIENT_CATALOG.md).
- [Orchard: roadmap](orchards-1.1.0/ROADMAP.md),
  [world generation](orchards-1.1.0/NATURAL_WORLDGEN_26_2.md) e
  [Serene Seasons](orchards-1.1.0/SERENE_SEASONS_INTEGRATION.md).
- [Compatibilità e licenze asset Orchard](orchards-1.1.0/LICENSE_AND_ASSET_RC_REVIEW.md)
  e [attribuzioni Rustic](orchards-1.1.0/RUSTIC_ASSET_MATCHES.md).
- [Catena formaggi](cheese-update/ROADMAP_FORMAGGI.md),
  [salumi](cured-meats/ROADMAP_SALUMI.md) e [linee guida grafiche](ASSET_GUIDELINES.md).

## Test e rilascio

Per il branch `feature/vineyard-chili-1.2.0` sono mantenuti i workflow
[`vineyard-chili-1.2.0.yml`](../.github/workflows/vineyard-chili-1.2.0.yml)
(build, audit essenziali, JAR) e
[`vineyard-chili-server.yml`](../.github/workflows/vineyard-chili-server.yml)
(boot/reload server e test RCON). I workflow e i controlli statici specifici
delle precedenti versioni **rimangono nelle rispettive branch Git**;
non sono duplicati qui.

**CI verde non sostituisce il collaudo nel client**: texture, GUI, interazioni,
JEI, salvataggi reali e compatibilità opzionali richiedono verifiche di gioco.
Non pubblicare la 1.2.0 solo sulla base dei test automatici.

## Manutenzione

Aggiornare i documenti consolidati sopra indicati anziché creare un nuovo
`.md` per ogni microcorrezione. Conservare sempre README, CHANGELOG,
LICENSE, NOTICE, THIRD_PARTY_NOTICES e la documentazione di attribuzione.
La rimozione di vecchi appunti non modifica codice di gioco, asset, ricette
né diritti di terze parti.
