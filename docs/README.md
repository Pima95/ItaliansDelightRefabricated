# Documentazione — Italian's Delight Refabricated

La documentazione delle release già pubblicate è ridotta a un changelog per versione;
i dettagli storici rimangono recuperabili dalla cronologia Git.

## Versioni precedenti

- [1.0.0 — Pizza Update (23 ottobre 2026)](pizza-1.0.0/CHANGELOG.md)
- [1.1.0 — Orchard Update (11 novembre 2026)](orchards-1.1.0/CHANGELOG.md)

## 1.2.0 — Vineyard & Chili (sviluppo, Minecraft 26.3)

- [Stato e roadmap](vineyard-chili-1.2.0/ROADMAP.md) · [Design storico](vineyard-chili-1.2.0/DESIGN.md)
- [Nuovi piatti, pizze e salse](vineyard-chili-1.2.0/NEW_FOODS_AND_SAUCE_LAYERS.md)
- [Brewing Barrel](vineyard-chili-1.2.0/BREWING_BARREL_GUI.md) · [palette e texture](vineyard-chili-1.2.0/BREWING_BARREL_TEXTURE_PALETTES.md)
- [Uva bianca](vineyard-chili-1.2.0/WHITE_GRAPES.md) · [viti selvatiche](vineyard-chili-1.2.0/WILD_GRAPES_SPAWN.md)
- [Bevande](vineyard-chili-1.2.0/BOTTLES_DRINKABILITY.md) · [test in gioco](vineyard-chili-1.2.0/TESTING.md)

Le note tecniche di sviluppo non sempre riportano lo stato più recente del codice.

## Documenti generali e attribuzioni

- [Requisiti in italiano](REQUISITI.md) · [Requirements in English](REQUIREMENTS.md)
- [Problemi registrati](KNOWN_BUGS.md) · [Linee guida asset](ASSET_GUIDELINES.md)
- [Licenza delle texture crate di Farmer's Delight](FDR_CRATES_LICENSE.md)
- [Licenza del progetto](../LICENSE) · [NOTICE](../NOTICE) · [Terze parti](../THIRD_PARTY_NOTICES.md) · [Crediti](../CREDIT.md)

## Controlli di rilascio 1.2.0

I workflow attivi sul branch 1.2.0 sono [build e audit](../.github/workflows/vineyard-chili-1.2.0.yml)
e [test del server](../.github/workflows/vineyard-chili-server.yml). Gli script
richiamati da questi workflow sono conservati. I controlli obsoleti delle versioni
precedenti sono recuperabili da Git.

**Compilazione e test automatici non sostituiscono la prova nel client**
(texture, JEI, interazioni, GUI, salvataggi e mod opzionali).
