# Italian's Delight Refabricated

<p align="center">
  <img src="src/main/resources/assets/italiansdelight/textures/italians_delight_refabricated.png" alt="Italian's Delight Refabricated" width="50%">
</p>

**Italian's Delight Refabricated** è un add-on per [Farmer's Delight Refabricated](https://github.com/MehVahdJukaar/FarmersDelightRefabricated) su **Fabric**. Aggiunge lavorazioni e piatti della tradizione italiana, integrati con Cooking Pot, Cutting Board, JEI e le altre meccaniche di Farmer's Delight.

## Guida rapida

- **[Documentazione e indice per argomenti](docs/README.md)** — installazione, funzionalità, guide tecniche e test.
- **[Crediti degli autori](CREDIT.md)** — chi ha contribuito e provenienza degli asset.
- **[Prompt approvati e regole grafiche](docs/PROMPTS.md)** — istruzioni originali da rispettare nello sviluppo.
- **[Licenza e attribuzioni](LICENSE)** · [NOTICE](NOTICE) · [Materiali di terzi](THIRD_PARTY_NOTICES.md) · [Licenza crate FDR](docs/FDR_CRATES_LICENSE.md).
- **[Changelog](CHANGELOG.md)** · [Release e download](https://github.com/Pima95/ItaliansDelightRefabricated/releases) · [Segnalazioni](https://github.com/Pima95/ItaliansDelightRefabricated/issues).

## Versioni e installazione

| Versione | Stato | Minecraft | Principali novità |
| --- | --- | --- | --- |
| [1.0.0 — Pizza Update](docs/pizza-1.0.0/CHANGELOG.md) | Pubblicata il 23/10/2026 | 26.2 | Pizze interattive, forno, pala, spicchi e JEI |
| [1.1.0 — Orchard Update](docs/orchards-1.1.0/CHANGELOG.md) | **Ultima release pubblica** (11/11/2026) | 26.2 | Ulivi, mandorli, ciliegi da frutto, legni, frutta e Crushing Tub |
| 1.2.0 — Vineyard & Chili | In sviluppo su `feature/vineyard-chili-1.2.0` | **26.3** | Uva, peperoncini, fermentazione, bevande, nuovi piatti e pizze |

### Installare una versione pubblicata

1. Installa **Minecraft** nella versione richiesta dalla release scelta e **Fabric Loader** compatibile.
2. Aggiungi alla cartella `mods` la **Fabric API**, **Farmer's Delight Refabricated** e il JAR di **Italian's Delight Refabricated** corretti per quella versione. JEI e le integrazioni opzionali possono essere aggiunti separatamente.
3. Usa **Java 25**, avvia Minecraft e controlla l'elenco delle mod. Non confondere i JAR `sources` o `dev` con il JAR utilizzabile nel gioco.

Le versioni delle dipendenze per la **1.0.0** e la **1.1.0** sono specificate nei rispettivi [changelog](docs/README.md) e nella [pagina delle release pubblicate](https://github.com/Pima95/ItaliansDelightRefabricated/releases).

**La 1.2.0 non è ancora pubblicata.** Il numero di versione nel branch di sviluppo è `1.2.0-mc26.3-dev`. Consultare la [documentazione 1.2.0](docs/vineyard-chili-1.2.0/README.md) e i [test manuali](docs/vineyard-chili-1.2.0/TESTING.md); una build CI riuscita non sostituisce una prova in gioco.

## Funzionalità

- Produzione e stagionatura dei formaggi (mozzarella, ricotta, burrata, mascarpone, Parmigiano, Pecorino, Gorgonzola, Provolone e Scamorza), Cheese Vat, grattugia, rastrelliere e uncini.
- Preparazione e stagionatura dei salumi: prosciutto crudo, salame, mortadella, pancetta, guanciale, bresaola, coppa e speck.
- Coltivazione di cardo e basilico, sale da evaporazione, cucina italiana con pasta e risotti.
- Pizze personalizzabili con condimenti su quarti indipendenti, forno, pala, spicchi e guide JEI.
- Nella 1.1.0: tre specie di alberi da frutto con legni e derivati, estrazione con Crushing Tub, bottiglie e secchi per i liquidi.
- Nel branch 1.2.0: vitigni, peperoncini, Brewing Barrel, bevande fermentate, ricette e gusti aggiuntivi.

## Sviluppo

Per il branch **1.2.0**: **Minecraft 26.3**, **Java 25**, **Fabric Loader 0.20.1**, **Fabric API 0.166.0+26.3** e **Farmer's Delight Refabricated 26.3-3.6.30+refabricated**. Consultare `gradle.properties` per la configurazione effettiva.

```bash
./gradlew build
```

Su Windows: `./gradlew.bat build`. I JAR vengono generati in `build/libs/`.

Per i requisiti delle versioni pubblicate 1.0.0 e 1.1.0, consultare le rispettive [release GitHub](https://github.com/Pima95/ItaliansDelightRefabricated/releases) e i changelog sopra indicati.

## Documentazione e contributi

- [Indice della documentazione](docs/README.md)
- [Changelog generale](CHANGELOG.md)
- [Segnalazione problemi e regressioni](https://github.com/Pima95/ItaliansDelightRefabricated/issues)

Segnalazioni di problemi e pull request sono benvenute.

## Licenze e crediti

Il progetto contiene contributi originali distribuiti sotto [Apache License 2.0](LICENSE) a partire dalla 1.1.0. La 1.0.0 precedentemente distribuita conserva i suoi termini MIT. I materiali di terzi conservano le condizioni e attribuzioni proprie.

Consultare [NOTICE](NOTICE), [CREDIT](CREDIT.md), [THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES.md) e la [licenza MIT delle texture crate derivate da Farmer's Delight](docs/FDR_CRATES_LICENSE.md). La documentazione di provenienza degli asset delle versioni precedenti è recuperabile nella cronologia Git; le attribuzioni individuali di alcuni sprite legacy restano da verificare.
